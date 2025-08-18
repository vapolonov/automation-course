package practice;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CartTest {
  private Playwright playwright;
  private BrowserContext context;
  private Page page;
  private Browser browser;

  @BeforeEach
  void setup() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(false)
        .setSlowMo(500));
    context = browser.newContext(new Browser.NewContextOptions()
        .setRecordVideoDir(Paths.get("videos/")));
    page = context.newPage();
  }

  @Test
  void testCartActions() {
    page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");

    // Добавление товара
    page.click("button:has-text('Add Element')");
    page.locator("#content").screenshot(new Locator.ScreenshotOptions()
        .setPath(getTimestampPath("cart_after_add.png")));

    // Удаление товара
    page.click("button:has-text('Delete')");
    page.locator("#content").screenshot(new Locator.ScreenshotOptions()
        .setPath(getTimestampPath("cart_after_remove.png")));
  }

  private Path getTimestampPath(String filename) {
    String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    Path dir = Paths.get("screenshots", timestamp);
    return dir.resolve(filename);
  }

  @AfterEach
  void teardown() {
    context.close();
    browser.close();
    playwright.close();
  }
}
