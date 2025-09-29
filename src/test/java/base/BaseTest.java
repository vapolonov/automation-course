package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static config.Env.env;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

  protected Playwright playwright;
  protected Browser browser;
  protected BrowserContext context;
  protected Page page;
  protected APIRequestContext apiRequest;

  static {
    if (System.getProperty("env") == null) {
      System.setProperty("env", "dev");
    }
  }

  @BeforeAll
  void setUpAll() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(
        new BrowserType.LaunchOptions().setHeadless(true)
    );
  }

  @BeforeEach
  void setUp() {
    this.context = browser.newContext(new Browser.NewContextOptions()
        .setViewportSize(1280, 720)
        .setRecordVideoDir(Paths.get("videos/"))
        .setRecordVideoSize(1280, 720));
    apiRequest = playwright.request().newContext(
            new APIRequest.NewContextOptions()
                    .setBaseURL(env.getBaseUrl())
    );
    page = this.context.newPage();
  }

  @AfterEach
  void tearDown() {
    context.close();
    apiRequest.dispose();
  }

  @AfterAll
  void tearDownAll() {
    browser.close();
    playwright.close();
  }
}
