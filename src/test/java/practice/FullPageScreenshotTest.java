package practice;

import base.BaseTest;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FullPageScreenshotTest extends BaseTest {

  @Test
  public void testHomePageVisual() throws IOException {
    page.navigate("https://the-internet.herokuapp.com");
    Path actual = Paths.get("actual.jpg");
    page.screenshot(new Page.ScreenshotOptions().setPath(actual));

    long mismatch = Files.mismatch(actual, Paths.get("expected.jpg"));
    assertThat(mismatch).isEqualTo(-1);
  }
}
