package practice;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.options.WaitForSelectorState.VISIBLE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicLoadingTest {
  Playwright playwright;
  Browser browser;
  BrowserContext context;
  Page page;

  @BeforeEach
  void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    context = browser.newContext();

    // Запускаем запись трассировки
    context.tracing().start(new Tracing.StartOptions()
        .setScreenshots(true)
        .setSnapshots(true)
        .setSources(true));

    page = context.newPage();
  }

  @Test
  void testDynamicLoading() {

    page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1",
        new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    assertEquals("Dynamically Loaded Page Elements", page.locator("h3").textContent(),
        "Заголовок страницы должен быть 'Dynamically Loaded Page Elementss'");

    // Перехват ответов
    page.onResponse(response -> {
      if (response.url().contains("/dynamic_loading")) {
        assertThat(response.status())
            .as("Запрос к /dynamic_loading завершился успешно (200)")
            .isEqualTo(200);
      }
    });

    page.click("button");
    Locator finishText = page.locator("#finish");
    finishText.waitFor(new Locator.WaitForOptions().setState(VISIBLE));

    assertEquals("Hello World!", finishText.textContent().trim(),
        "Ожидался текст 'Hello World!'");

    context.tracing().stop(new Tracing.StopOptions()
        .setPath(Paths.get("trace/trace-success.zip")));
  }

  @AfterEach
  void tearDown() {
     if (page != null) page.close();
     if (browser != null) browser.close();
     if (playwright != null) playwright.close();
  }
}
