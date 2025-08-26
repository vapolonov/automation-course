package practice;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class testDynamicLoadingWithTrace {
  Playwright playwright;
  Browser browser;
  BrowserContext context;
  Page page;

  @Test
  void testDynamicLoadingWithTrace() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    context = browser.newContext();

    // Настройка трассировки
    context.tracing().start(new Tracing.StartOptions()
        .setScreenshots(true)
        .setSnapshots(true));

    page = context.newPage();

    Allure.step("Открыть страницу с алертами", () -> {
      page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1", new Page.NavigateOptions()
          .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    });

    Allure.step("Нажать на кнопку 'Start'", () ->{
      page.click("button");
    });


    Allure.step("Проверить текст после загрузки элемента", () -> {
      page.locator("#finish").waitFor();
      assertThat(page.locator("#finish").textContent().trim()).isEqualTo("Hello World!");
    });

    // Сохранение трассировки
    context.tracing().stop(new Tracing.StopOptions()
        .setPath(Paths.get("trace-dynamic-loading.zip")));
  }

  @AfterEach
  void tearDown() {
    context.close();
    browser.close();
    playwright.close();
  }
}
