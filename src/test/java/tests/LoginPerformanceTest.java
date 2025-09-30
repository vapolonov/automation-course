package tests;

import base.BaseTest;
import com.microsoft.playwright.Tracing;
import org.apache.commons.lang3.time.StopWatch;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

import static config.Env.env;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginPerformanceTest extends BaseTest {

  SoftAssertions softly = new SoftAssertions();

  @Test
  public void testLoginPerformance() {
    StopWatch watch = StopWatch.createStarted();

    boolean enableTracing = ThreadLocalRandom.current().nextInt(100) < 10; // 10%

    if (enableTracing) {
      context.tracing().start(new Tracing.StartOptions()
          .setScreenshots(true)
          .setSnapshots(true));
    }

    page.navigate(env.getBaseUrl() + "/login");
    page.locator("#username").fill("tomsmith");
    page.locator("#password").fill("SuperSecretPassword!");
    page.locator("button[type='submit']").click();

    assertAll("Assert successful login",
        () -> assertTrue(page.url().contains("/secure")),
        () -> assertTrue(page.locator("#flash").isVisible()),
        () -> assertTrue(page.locator("text=Welcome to the Secure Area").isVisible())
    );

    watch.stop();
    long duration = watch.getTime();

    softly.assertThat(duration)
        .as("Login took " + duration + "ms (exceeds 3000ms limit)")
        .isLessThan(3000);

    // Сохраняем трассировку при превышении времени
    if (enableTracing && duration > 3000) {
      context.tracing().stop(new Tracing.StopOptions()
          .setPath(Paths.get("trace/slow-login-trace.zip")));
    }

    softly.assertAll();
  }
}

