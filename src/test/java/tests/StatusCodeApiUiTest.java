package tests;

import base.BaseTest;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeApiUiTest extends BaseTest {

  @BeforeEach
  void setup() {
    // Навигация на страницу статус кодов один раз
    page.navigate("https://the-internet.herokuapp.com/status_codes");
    page.waitForSelector("div.example");
  }

  @Test
  void testStatus200CodesCombined() {
    assertEquals(getApiStatusCode(200), getUiStatusCode(200));
  }

  @Test
  void testStatus404CodesCombined() {
    assertEquals(getApiStatusCode(404), getUiStatusCode(404));
  }

  private int getApiStatusCode(int code) {
    APIResponse response = apiRequest.get("/status_codes/" + code);
    System.out.println("Status Code: " + response.status());
    return response.status();
  }

  private int getUiStatusCode(int code) {
    Locator link = page.locator("text=" + code).first();

    Response response = page.waitForResponse(
        res -> res.url().endsWith("/status_codes/" + code),
        () -> link.click(new Locator.ClickOptions().setTimeout(15000)));

    System.out.println("Status Code: " + response.status());
    return response.status();
  }
}
