package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamicControlsPage extends AbsBasePage {

  Locator checkbox = page.locator("input[type='checkbox']");

  public DynamicControlsPage(Page page) {
    super(page);
  }

  @Step("Проверить, что чекбокс виден на странице")
  public void checkCheckboxA() {
    assertTrue(checkbox.isVisible());
  }

  @Step("Кликнуть на кнопку 'Remove'")
  public void clickRemoveButton() {
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove")).click();
  }

  @Step("Проверить, что чекбокс не виден на странице")
  public void isCheckboxVisible() {
    checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
    assertTrue(checkbox.isHidden());
    assertEquals("It's gone!", page.locator("#message").textContent());
  }
}
