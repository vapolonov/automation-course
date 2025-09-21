package tests;

import base.BaseTest;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static config.Env.env;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeCombinedTest extends BaseTest {

  @ParameterizedTest
  @ValueSource(ints = {200, 404})
  void testStatusCodeCombined(int statusCode) {
    // API проверка
    int apiCode = getApiStatusCode(statusCode);
    assertThat(apiCode).isEqualTo(statusCode);

    // UI проверка
    int uiCode = getUiStatusCode(statusCode);
    assertThat(uiCode).isEqualTo(statusCode);

    // Сравнение результатов
    assertEquals(apiCode, uiCode, "API и UI статус коды не равны");

  }

  private int getApiStatusCode(int code) {
    APIResponse response = apiRequest.get("/status_codes/" + code);
    assertEquals(code, response.status(),
        "API: Неверный статус код для " + code);
    return response.status();
  }

  private int getUiStatusCode(int code) {
    try {
      // Навигация на страницу статус кодов
      page.navigate(env.getBaseUrl() + "/status_codes");
      page.waitForSelector("div.example");

      // Локатор
      Locator link = page.locator(
          String.format("a[href*='status_codes/%d']", code)
      ).first();

      // Перехват ответа перед кликом
      Response response = page.waitForResponse(
          res -> res.url().endsWith("/status_codes/" + code),
          () -> link.click(new Locator.ClickOptions().setTimeout(10000))
      );

      return response.status();

    } catch (Exception e) {
      // Скриншот с именем, включающим код ошибки
      //Ваш код...
      throw new RuntimeException("UI проверка упала для кода " + code, e);
    }
  }

}
