package practice;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CartTest {
  private static Playwright playwright;
  private static Browser browser;
  private BrowserContext context;
  private Page page;
  private Video video;
  private Path screenshotDir;

  @BeforeAll
  static void launchBrowser() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(true)
        .setSlowMo(500));
  }

  @BeforeEach
  void initContex() {
    screenshotDir = Paths.get("screenshots/");
    try {
      Files.createDirectories(screenshotDir);
    } catch (IOException e) {
      throw new RuntimeException("Cannot create screenshots directory", e);
    }
    context = browser.newContext(new Browser.NewContextOptions()
        .setRecordVideoDir(Paths.get("videos/")));
    page = context.newPage();
    video = page.video();
  }

  @Test
  void testCartActions() {
    Allure.step("Открываем страницу", () -> {
      page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");
    });

    Allure.step("Нажимаем на кнопку 'Добавить товар'", () -> {
      page.click("button:has-text('Add Element')");
      page.locator("#content").screenshot(new Locator.ScreenshotOptions()
          .setPath(getTimestampPath("cart_after_add.png")));
    });

    Allure.step("Нажимаем на кнопку 'Удалить товар'", () -> {
      page.click("button:has-text('Delete')");
      page.locator("#content").screenshot(new Locator.ScreenshotOptions()
          .setPath(getTimestampPath("cart_after_remove.png")));
    });
  }

  private Path getTimestampPath(String filename) {
    String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    Path dir = Paths.get("screenshots", timestamp);
    return dir.resolve(filename);
  }

  @RegisterExtension
  TestWatcher watcher = new TestWatcher() {
    @Override
    public void testFailed(ExtensionContext extensionContext, Throwable cause) {
      try {
        if (page != null && !page.isClosed()) {
          // Генерируем имя файла
          String testName = extensionContext.getDisplayName();
          Path screenshotPath = screenshotDir.resolve(testName + ".png");

          // Делаем и сохраняем скриншот
          byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
              .setPath(screenshotPath)
              .setFullPage(true));

          // прикрепляем в Allure
          saveScreenshotToAllure(screenshot, testName);
          System.out.println("Screenshot saved to: " + screenshotPath);
        }
      } catch (Exception e) {
        System.out.println("Screenshot failed: " + e.getMessage());
      }
    }

    @Attachment(value = "Скриншот при падении: {name}", type = "image/png")
    private byte[] saveScreenshotToAllure(byte[] screenshot, String name) {
      return screenshot;
    }
  };

  @AfterEach
  void attachScreenshotOnFailure(TestInfo testInfo) throws IOException {
    if (context != null) {
      if (video != null) {
        String videoName = testInfo.getDisplayName();
        Path videoPath = Paths.get("videos/" + videoName + ".webm");
        context.close();
        video.saveAs(videoPath);
        attachVideo(videoName);
      }
    }
  }

  @AfterAll
  static void teardown() {
    browser.close();
    if(playwright != null) {
      playwright.close();
    }
  }

  @Attachment(value = "Видео теста {name}", type = "video/webm")
  private byte[] attachVideo(String name) throws IOException {
    return Files.readAllBytes(
        Paths.get("videos/" + name + ".webm")
    );
  }
}
