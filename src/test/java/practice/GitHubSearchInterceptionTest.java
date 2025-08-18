package practice;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GitHubSearchInterceptionTest {
  Playwright playwright;
  Browser browser;
  BrowserContext context;
  Page page;

  @BeforeEach
  void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    context = browser.newContext();
    page = browser.newPage();

    // Перехват запроса поиска
    context.route("**/search**", route -> {
      // Получаем оригинальный URL
      String originalUrl = route.request().url();

      // Декодируем и модифицируем параметры
      String modifiedUrl = originalUrl.contains("q=")
          ? originalUrl.replaceAll("q=[^&]+", "q=stars%3A%3E10000")
          : originalUrl + (originalUrl.contains("?") ? "&" : "?") + "q=stars%3A%3E10000";

      // Продолжаем запрос с модифицированным URL
      route.resume(new Route.ResumeOptions().setUrl(modifiedUrl));
    });
  }

  @Test
  void testSearchModification() {
    page.navigate("https://github.com/search?q=java");

    // Ожидаем появления результатов
    page.locator("[data-testid='results-list']").first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

    // Проверяем модифицированный запрос в UI
    // String searchValue = page.locator("input[name='q'][type='text']").inputValue();
    // assertEquals("stars:>10000", searchValue);
    // содержит stars
    Locator link = page.locator("[aria-label*='stars']").first();
    // заканчивается на stars
    // Locator link = page.locator("[aria-label$='stars']").first();
    String text = link.getAttribute("aria-label");
    String numberStr = text.split(" ")[0];
    int stars = Integer.parseInt(numberStr);
    assertTrue(stars > 10000);
  }

  @AfterEach
  void tearDown() {
    context.close();
    browser.close();
    playwright.close();
  }
}
