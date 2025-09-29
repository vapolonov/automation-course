package tests;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;

public class OptimizedLoginTest {
  static Playwright playwright;
  static Browser browser;
  static List<Cookie> allCookies;

  @BeforeAll
  static void globalSetup() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch();
    // Выполняем вход один раз перед всеми тестами
    Page page = browser.newPage();
    page.navigate("https://the-internet.herokuapp.com/login");
    page.locator("#username").fill("tomsmith");
    page.locator("#password").fill("SuperSecretPassword!");
    page.locator("button[type='submit']").click();
    // Сохраняем все cookies
    allCookies = page.context().cookies();
    page.close();
  }

  @Test
  void testDashboard() {
    BrowserContext context = browser.newContext();
    context.addCookies(allCookies);
    Page page = context.newPage();
    page.navigate("https://the-internet.herokuapp.com/secure");
    Assertions.assertTrue(page.url().contains("/secure"));
    Assertions.assertTrue(page.locator("h2").isVisible());
    Assertions.assertTrue(page.locator("h2").textContent().equals(" Secure Area"));
    Assertions.assertTrue(page.locator("text=Welcome to the Secure Area").isVisible());
    context.close();
  }

  @AfterAll
  static void tearDown() {
    browser.close();
    playwright.close();
  }
}