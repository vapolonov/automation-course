package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Paths;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

  protected Playwright playwright;
  protected Browser browser;
  protected BrowserContext context;
  public Page page;

  @BeforeAll
  void setUpAll() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(
        new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500)
    );
  }

  @BeforeEach
  void setUp() {
    this.context = browser.newContext(new Browser.NewContextOptions()
        .setRecordVideoDir(Paths.get("videos/"))
        .setRecordVideoSize(1280, 720));
    page = this.context.newPage();
  }

  @AfterEach
  void tearDown() {
    context.close();
  }

  @AfterAll
  void tearDownAll() {
    browser.close();
    playwright.close();
  }
}