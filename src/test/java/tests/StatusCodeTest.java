package tests;

import base.BaseTest;
import com.microsoft.playwright.Response;
import config.StatusConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeTest extends BaseTest {

  private final StatusConfig config = ConfigFactory.create(StatusConfig.class, System.getProperties());

  @Test
  public void test200() {
    Response response = page.navigate(config.getBaseUrl() + "/status_codes/200");
    assertEquals(200, response.status());
  }
}
