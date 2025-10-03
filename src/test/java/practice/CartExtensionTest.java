package practice;

import base.BaseTest;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CartExtensionTest extends BaseTest {

  @Test
  void testCartActions() {
    Allure.step("Открываем страницу", () -> {
      page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");
    });

    Allure.step("Нажимаем на кнопку 'Добавить товар'", () -> {
      page.click("button:has-text('Add Element')");
    });

    Allure.step("Нажимаем на кнопку 'Удалить товар'", () -> {
      page.click("button:has-text('Delete')");
    });
  }

  @RepeatedTest(100)
  void failTextExample() {
    page.navigate("https://playwright.dev/java");
    page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    assertTrue(page.locator("h1")
        .textContent()
        .contains("Playwright enables reliable end-to-end testing for modern web apps."));


  }
}
