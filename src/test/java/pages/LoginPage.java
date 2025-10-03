package pages;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginPage extends AbsBasePage {

  public LoginPage(Page page) {
    super(page);
  }

  @Step("Заполнить форму авторизации")
  public void fillLoginForm(String username, String password) {
    page.locator("#username").fill(username);
    page.locator("#password").fill(password);
    page.locator("button[type='submit']").click();
  }

  @Step("Заполнить форму авторизации")
  public void assertAuthorization(String description, String path, String message) {
    assertAll(description,
        () -> assertTrue(page.url().contains(path)),
        () -> assertTrue(page.locator("#flash").isVisible()),
        () -> assertTrue(page.locator("#flash").textContent().contains(message))
    );
  }
}
