package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

public class OptimizedLoginTest {
  static Playwright playwright;
  static Browser browser;
  static String authToken;

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
    authToken = page.evaluate("localStorage.getItem('authToken')").toString();
    page.close();
  }

  @Test
  void testDashboard() {
    BrowserContext context = browser.newContext();
    // Устанавливаем токен в новый контекст
    context.addInitScript("localStorage.setItem('authToken', '" + authToken + "')");
    Page page = context.newPage();
    page.navigate("https://the-internet.herokuapp.com/dashboard");
    Assertions.assertTrue(page.locator(".welcome-message").isVisible());
    context.close();
  }

  @AfterAll
  static void tearDown() {
    browser.close();
    playwright.close();
  }
}