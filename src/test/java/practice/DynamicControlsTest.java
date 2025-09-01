package practice;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamicControlsTest {
  Playwright playwright;
  Browser browser;
  Page page;

  @BeforeEach
  void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    page = browser.newPage();
  }

  @Test
  void testDynamicCheckbox() {

    Locator checkbox = page.locator("input[type='checkbox']");

    Allure.step("Открыть страницу 'Dynamic Controls'", () -> {
      page.navigate("https://the-internet.herokuapp.com/dynamic_controls",
          new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    });

    Allure.step("Проверить, что чекбокс виден на странице", () -> {
      assertTrue(checkbox.isVisible());
    });

    Allure.step("Кликнуть на кнопку 'Remove'", () -> {
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove")).click();
    });

    Allure.step("Проверить, что чекбокс не виден на странице", () -> {
      checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
      assertTrue(checkbox.isHidden());
      assertEquals("It's gone!", page.locator("#message").textContent());
    });

    Allure.step("Кликнуть на кнопку 'Add'", () -> {
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add")).click();
    });

    Allure.step("Проверить, что чекбокс снова виден на странице", () -> {
      checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
      assertTrue(checkbox.isVisible());
      assertEquals("It's back!", page.locator("#message").textContent());
    });
  }

  @AfterEach
  void tearDown() {
    page.close();
    browser.close();
    playwright.close();
  }
}
