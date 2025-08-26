package practice;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import extension.WebTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;

import static org.junit.jupiter.api.Assertions.*;

@WebTest
@Epic("Изучение Playwright Java")
public class CheckboxesTest {
  private Playwright playwright;
  private Browser browser;
  private BrowserContext context;
  private Page page;

  private final String
      CHECKBOXES = "input[type='checkbox']";

  @BeforeEach
  @Step("Инициализация браузера и контекста")
  void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    context = browser.newContext();
    page = context.newPage();
  }

  @Test
  @Story("Проверка работы чекбоксов")
  @DisplayName("Тестирование выбора/снятия чекбоксов")
  @Severity(SeverityLevel.CRITICAL)
  void testCheckboxes() {
    navigateToCheckboxesPage();
    verifyInitialState();
    toggleCheckboxes();
    verifyToggledState();
  }

  @Step("Переход на страницу /checkboxes")
  private void navigateToCheckboxesPage() {
    page.navigate("https://the-internet.herokuapp.com/checkboxes", new Page.NavigateOptions()
        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    assertTrue(page.locator("h3").textContent().contains("Checkboxes"),
        "Страница должна содержать заголовок 'Checkboxes'");
  }

  @Step("Проверка начального состояния чекбоксов")
  private void verifyInitialState() {
    assertFalse(page.locator(CHECKBOXES).nth(0).isChecked());
    assertTrue(page.locator(CHECKBOXES).nth(1).isChecked());
    assertEquals(2, page.locator(CHECKBOXES).count(), "На странице должно быть 2 чекбокса");
  }

  @Step("Проверка состояния чекбоксов")
  private void verifyToggledState() {
    assertTrue(page.locator(CHECKBOXES).nth(1).isChecked());
    assertFalse(page.locator(CHECKBOXES).nth(1).isChecked());
  }

  @Step("Изменение состояния чекбоксов")
  private void toggleCheckboxes() {
    page.locator(CHECKBOXES).nth(0).check();
    page.locator(CHECKBOXES).nth(1).uncheck();
  }

  @AfterEach
  @Step("Закрытие ресурсов")
  void tearDown() {
    if (context != null) context.close();
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @RegisterExtension
  TestWatcher watcher = new TestWatcher() {
    @Override
    public void testFailed(ExtensionContext extensionContext, Throwable cause) {
      try {
        if (page != null && !page.isClosed()) {
          // Делаем и сохраняем скриншот
          byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
              .setFullPage(true));
          // прикрепляем в Allure
          saveScreenshotToAllure(screenshot);
        }
      } catch (Exception e) {
        System.out.println("Screenshot failed: " + e.getMessage());
      }
    }

    @Attachment(value = "Скриншот при падении: {name}", type = "image/png")
    private byte[] saveScreenshotToAllure(byte[] screenshot) {
      return screenshot;
    }
  };
}
