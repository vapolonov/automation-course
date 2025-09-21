package tests;

import base.BaseTest;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.file.Paths;
import static config.Env.env;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeCombinedTest extends BaseTest {

  @ParameterizedTest
  @ValueSource(ints = {200, 404})
  void testStatusCodeCombined(int statusCode) {
    // API проверка
    int apiStatusCode = getApiStatusCode(statusCode);
    assertEquals(statusCode, apiStatusCode,
                "API вернул неверный статус-код для " + statusCode);

    // UI проверка
    int uiStatusCode = getUiStatusCode(statusCode);
    assertEquals(statusCode, uiStatusCode,
                "UI вернул неверный статус-код для " + statusCode);

    // Сравнение результатов
    assertEquals(apiStatusCode, uiStatusCode,
                String.format("Статус-коды не совпадают для кода %d. API: %d, UI: %d",
                        statusCode, apiStatusCode, uiStatusCode));
  }

  private int getApiStatusCode(int code) {
    APIResponse response = apiRequest.get("/status_codes/" + code);
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
      takeScreenshot("error_code_" + code);
      throw new RuntimeException("UI проверка упала для кода " + code, e);
    }
  }

  private void takeScreenshot(String prefix) {
        try {
            String screenshotName = String.format("screenshots/%s.png", prefix);
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(screenshotName))
                    .setFullPage(true));
            System.out.println("Скриншот сохранен: " + screenshotName);
        } catch (Exception e) {
            System.err.println("Не удалось сохранить скриншот: " + e.getMessage());
        }
    }

}
