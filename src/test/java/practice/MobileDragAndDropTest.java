package practice;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MobileDragAndDropTest {
  Playwright playwright;
  Browser browser;
  BrowserContext context;
  Page page;

  @BeforeEach
  void setup() {
    playwright = Playwright.create();

    // Ручная настройка параметров Samsung Galaxy S22 Ultra
    Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
        .setUserAgent("Mozilla/5.0 (Linux; Android 12; SM-S908B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Mobile Safari/537.36")
        .setViewportSize(384, 873)  // Разрешение экрана
        .setDeviceScaleFactor(3.5)
        .setIsMobile(true)
        .setHasTouch(true);

    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    context = browser.newContext(deviceOptions);
    page = context.newPage();
  }

  @Test
  void testDragAndDropMobile() {

    Locator columnA = page.locator("#column-a");
    Locator columnB = page.locator("#column-b");

    Allure.step("Открыть страницу 'Drag and Drop'", () -> {
      page.navigate("https://the-internet.herokuapp.com/drag_and_drop",
          new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
      assertThat(page.locator("h3")).hasText("Drag and Drop");
    });

    Allure.step("Проверить начальное состояние элементов", () -> {
      assertThat(columnA.locator("header")).hasText("A");
      assertThat(columnB.locator("header")).hasText("B");
    });

    //    page.locator("#column-a").dragTo(page.locator("#column-b"));

    // Перетаскивание через JS (т.к. HTML5 DnD не работает в мобильном режиме)
    Allure.step("Перетащить (через JS) элемент 'A' в зону 'B'", () -> {
      page.evaluate("() => {\n" +
          "  const dataTransfer = new DataTransfer();\n" +
          "  const event = new DragEvent('drop', { dataTransfer });\n" +
          "  document.querySelector('#column-a').dispatchEvent(new DragEvent('dragstart', { dataTransfer }));\n" +
          "  document.querySelector('#column-b').dispatchEvent(event);\n" +
          "}");
    });

    Allure.step("Проверить, что текст А переместился в зону В", () -> {
      assertEquals("A", columnB.textContent().trim(), "Текст в зоне B должен быть 'A'");
    });
  }

  @AfterEach
  void tearDown() {
    if (context != null) context.close();
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }
}
