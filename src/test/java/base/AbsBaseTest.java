package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbsBaseTest {

  private Playwright playwright;
  private Browser browser;

  // ThreadLocal гарантирует изоляцию page/context для каждого потока
  private final ThreadLocal<BrowserContext> threadLocalContext = new ThreadLocal<>();
  private final ThreadLocal<Page> threadLocalPage = new ThreadLocal<>();

  @BeforeAll
  void globalSetup() {
    playwright = Playwright.create();
    browser = playwright.chromium()
        .launch(new BrowserType.LaunchOptions().setHeadless(false));
    System.out.println(">>> Browser started");
  }

  @BeforeEach
  void createContextAndPage() {
    BrowserContext context = browser.newContext(
        new Browser.NewContextOptions().setViewportSize(1920, 1080)
    );
    Page page = context.newPage();
    threadLocalContext.set(context);
    threadLocalPage.set(page);
    System.out.println(">>> New context + page created");
  }

  @AfterEach
  void closeContext() {
    threadLocalPage.get().close();
    threadLocalContext.get().close();
    System.out.println(">>> Context + page closed");
    threadLocalPage.remove();
    threadLocalContext.remove();
  }

  @AfterAll
  void globalTeardown() {
    browser.close();
    playwright.close();
    System.out.println(">>> Browser closed");
  }

  protected Page page() {
    return threadLocalPage.get();
  }

  protected BrowserContext context() {
    return threadLocalContext.get();
  }
}

