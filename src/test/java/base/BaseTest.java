package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

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
        new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(500)
    );
  }

  @BeforeEach
  void setUp() {
    this.context = browser.newContext(new Browser.NewContextOptions()
        .setViewportSize(1280, 720)
        .setRecordVideoDir(Paths.get("videos/"))
        .setRecordVideoSize(1280, 720));
    String baseUrl = getBaseUrl();
    apiRequest = playwright.request().newContext(
            new APIRequest.NewContextOptions()
                    .setBaseURL(baseUrl)
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

  protected String getBaseUrl() {
    return "https://the-internet.herokuapp.com";
  }
}
