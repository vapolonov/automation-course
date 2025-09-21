package tests;

import base.BaseTest;
import com.microsoft.playwright.Response;
import org.junit.jupiter.api.Test;

import static config.Env.env;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeTest extends BaseTest {

  @Test
  public void test200() {
    Response response = page.navigate(env.getBaseUrl() + "/status_codes/200");
    assertEquals(200, response.status());
  }
}
