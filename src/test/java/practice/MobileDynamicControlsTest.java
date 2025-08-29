package practice;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MobileDynamicControlsTest {
  Playwright playwright;
  Browser browser;
  BrowserContext context;
  Page page;

  @BeforeEach
  void setUp() {
    playwright = Playwright.create();

    // Настройка параметров iPad Pro 11
    Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
        .setUserAgent("Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)")
        .setViewportSize(834, 1194)
        .setDeviceScaleFactor(2)
        .setIsMobile(true)
        .setHasTouch(true);

    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    context = browser.newContext(deviceOptions);
    page = context.newPage();
  }

  @Test
  void testInputEnabling() {
    Locator form = page.locator("#input-example");

    Allure.step("Открыть страницу Dynamic Controls", () -> {
      page.navigate("https://the-internet.herokuapp.com/dynamic_controls",
        new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
      assertThat(page.locator("h4:has-text('Dynamic Controls')")).isVisible();
    });

    Allure.step("Проверить, что поле ввода Disabled", () -> {
      assertThat(form.locator("input")).isDisabled();
    });

    Allure.step("Нажать на кнопку Enable", () -> {
      form.locator("button[onclick='swapInput()']").click();
    });

    Allure.step("Проверить, что поле ввода стало Enabled", () -> {
      form.locator("input").waitFor(new Locator.WaitForOptions()
        .setState(WaitForSelectorState.VISIBLE));
    assertThat(form.locator("input")).isEnabled();
    assertThat(form.locator("#message")).containsText("It's enabled!");
    });
  }

  @AfterEach
  void tearDown() {
    context.close();
    browser.close();
    playwright.close();
  }
}
