package extension;

import com.microsoft.playwright.*;
import helpers.HtmlReportGenerator;
import org.junit.jupiter.api.extension.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CustomReportExtension implements TestWatcher, BeforeAllCallback, BeforeEachCallback,
    AfterEachCallback, AfterAllCallback, ParameterResolver {

  protected Playwright playwright;
  protected Browser browser;
  protected BrowserContext context;
  protected Page page;

  private long startTime;
  private static final List<TestResult> results = new ArrayList<>();
  private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.GLOBAL;

  static {
    if (System.getProperty("env") == null) {
      System.setProperty("env", "dev");
    }
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(
        new BrowserType.LaunchOptions().setHeadless(true)
    );
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    startTime = System.currentTimeMillis();
    this.context = browser.newContext(new Browser.NewContextOptions()
        .setViewportSize(1280, 720));
    page = this.context.newPage();
  }

  @Override
  public void afterEach(ExtensionContext context) {
    long duration = System.currentTimeMillis() - startTime;
    Throwable ex = context.getExecutionException().orElse(null);
    String screenshotPath = null;

    if (ex != null && page != null) {
      try {
        Files.createDirectories(Paths.get("screenshots"));
        screenshotPath = "screenshots/" + context.getDisplayName() + ".png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    results.add(new TestResult(
        context.getDisplayName(),
        ex == null ? "Passed" : "Failed",
        duration,
        ex != null ? ex.getMessage() : null,
        screenshotPath
    ));

    this.context.close();
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
    // Генерация HTML-отчета
    HtmlReportGenerator.generateReport(results, "test-report.html");
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
    return parameterContext.getParameter().getType() == Page.class;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
    return page;
  }
}