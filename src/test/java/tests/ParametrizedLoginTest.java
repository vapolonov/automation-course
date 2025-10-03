package tests;
import base.BaseTest;
import org.junit.jupiter.api.Test;
import static config.Env.env;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParametrizedLoginTest extends BaseTest {

  @Test
  public void testSuccessfulLogin() {
    page.navigate(env.getBaseUrl() + "/login");
    page.locator("#username").fill("tomsmith");
    page.locator("#password").fill("SuperSecretPassword!");
    page.locator("button[type='submit']").click();

    assertAll("Assert successful login",
        () -> assertTrue(page.url().contains("/secure")),
        () -> assertTrue(page.locator("#flash").isVisible()),
        () -> assertTrue(page.locator("text=Welcome to the Secure Area").isVisible())
    );
  }

  @Test
  public void testLoginWithWrongUsername() {
    page.navigate(env.getBaseUrl() + "/login");
    page.locator("#username").fill("test");
    page.locator("#password").fill("SuperSecretPassword");
    page.locator("button[type='submit']").click();

    assertAll("Assert unsuccessful login",
        () -> assertTrue(page.url().contains("/login")),
        () -> assertTrue(page.locator("#flash").isVisible()),
        () -> assertTrue(page.locator("#flash").textContent().contains("Your username is invalid!"))
    );
  }

  @Test
  public void testLoginWithWrongPassword() {
    page.navigate(env.getBaseUrl() + "/login");
    page.locator("#username").fill("tomsmith");
    page.locator("#password").fill("test");
    page.locator("button[type='submit']").click();

    assertAll("Assert unsuccessful login",
        () -> assertTrue(page.url().contains("/login")),
        () -> assertTrue(page.locator("#flash").isVisible()),
        () -> assertTrue(page.locator("#flash").textContent().contains("Your password is invalid!"))
    );
  }
}
