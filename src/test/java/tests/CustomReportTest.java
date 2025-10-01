package tests;

import com.microsoft.playwright.Page;
import extension.CustomReportExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static config.Env.env;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(CustomReportExtension.class)
public class CustomReportTest {

  @Test
  public void testCustomReportLogin(Page page) {
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
}
