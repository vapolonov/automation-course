package tests;
import base.AbsBaseTest;
import com.google.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pages.LoginPage;

import static config.Env.env;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParametrizedLoginTest extends AbsBaseTest {

  @Inject
  private LoginPage loginPage;

  @Test
  public void testSuccessfulLogin() {
    page().navigate(env.getBaseUrl() + "/login");
    page().locator("#username").fill("tomsmith");
    page().locator("#password").fill("SuperSecretPassword!");
    page().locator("button[type='submit']").click();

    assertAll("Assert successful login",
        () -> assertTrue(page().url().contains("/secure")),
        () -> assertTrue(page().locator("#flash").isVisible()),
        () -> assertTrue(page().locator("#flash").textContent().contains("You logged into a secure area!"))
    );
  }

  @Test
  public void testLoginWithWrongUsername() {
    page().navigate(env.getBaseUrl() + "/login");
    page().locator("#username").fill("test");
    page().locator("#password").fill("SuperSecretPassword");
    page().locator("button[type='submit']").click();

    assertAll("Assert unsuccessful login",
        () -> assertTrue(page().url().contains("/login")),
        () -> assertTrue(page().locator("#flash").isVisible()),
        () -> assertTrue(page().locator("#flash").textContent().contains("Your username is invalid!"))
    );
  }

  @Test
  public void testLoginWithWrongPassword() {
    page().navigate(env.getBaseUrl() + "/login");
    page().locator("#username").fill("tomsmith");
    page().locator("#password").fill("test");
    page().locator("button[type='submit']").click();

    assertAll("Assert unsuccessful login",
        () -> assertTrue(page().url().contains("/login")),
        () -> assertTrue(page().locator("#flash").isVisible()),
        () -> assertTrue(page().locator("#flash").textContent().contains("Your password is invalid!"))
    );
  }

  @ParameterizedTest
  @CsvSource({
      "tomsmith, SuperSecretPassword!, Assert successful login, /secure, You logged into a secure area!",
      "wrong_login, SuperSecretPassword!, Assert unsuccessful login, /login, Your username is invalid! ",
      "tomsmith, wrong_password, Assert unsuccessful login, /login, Your password is invalid!"
  })
  public void testLogin(String username, String password, String description, String path, String message) {
    loginPage.navigateTo(env.getBaseUrl() + "/login");
    loginPage.fillLoginForm(username, password);
    loginPage.assertAuthorization(description, path, message);
  }
}
