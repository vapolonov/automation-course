package practice;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeInterceptionTest {
  Playwright playwright;
  Browser browser;
  BrowserContext context;
  Page page;

  @BeforeEach
  void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    context = browser.newContext();
    page = context.newPage();

    // Перехват запроса к /status_codes/404
    context.route("**/status_codes/404", route -> {
      route.fulfill(new Route.FulfillOptions()
          .setStatus(200)
          .setHeaders(Collections.singletonMap("Content-Type", "text/html"))
          .setBody("<h3>Mocked Success Response</h3>")
      );
    });
  }

  @Test
  void testMockedStatusCode() {
    page.navigate("https://the-internet.herokuapp.com/status_codes",
        new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

    // Клик по ссылке "404"
    page.getByText("404").click();
//    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("404")).click();
//    page.locator("a:has-text('404')").click();
//    page.locator("//a[text()='404']").click();

    // Проверка мок-текста
    Locator text = page.locator("h3");
    text.waitFor(new Locator.WaitForOptions()
        .setState(WaitForSelectorState.VISIBLE)
        .setTimeout(3000)
    );
    assertEquals("Mocked Success Response", text.textContent());
  }

  @AfterEach
  void tearDown() {
    browser.close();
    playwright.close();
  }
}
