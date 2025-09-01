package practice;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HoverTest {
  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  Page page;

  @BeforeAll
  static void setupClass() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(1000));
  }

  @AfterAll
  static void teardownClass() {
    browser.close();
    playwright.close();
  }

  @BeforeEach
  void setup() {
    context = browser.newContext();
    page = context.newPage();
  }

  @AfterEach
  void cleanup() {
    context.close();
  }

  @Test
  void testHoverProfiles() {
    page.navigate("https://the-internet.herokuapp.com/hovers",
        new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

    Locator figures = page.locator(".figure");
    int count = figures.count();

    for (int i = 0; i < count; i++) {
      Locator figure = figures.nth(i);
      figure.hover();

      // Проверяем, что появилась ссылка "View profile"
      Locator profileLink = figure.locator("text=View profile");
      assertTrue(profileLink.isVisible(), "Ссылка 'View profile' должна быть видна");

      // Кликаем
      profileLink.click();

      // Проверяем, что URL соответствует /users/{id}
      String expectedUrlPart = "/users/" + (i + 1);
      assertTrue(page.url().contains(expectedUrlPart),
          "Ожидалось, что URL содержит " + expectedUrlPart + ", но был " + page.url());

      // Возвращаемся назад
      page.goBack();
    }
  }

}
