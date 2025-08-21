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
    Path actualPath = Paths.get("actual.jpg");
    page.screenshot(new Page.ScreenshotOptions().setPath(actualPath).setFullPage(true));

     //! Путь к эталонному скриншоту
        Path expectedPath = Paths.get("expected.jpg");

    //! Если эталонного файла нет, создаем его из текущего скриншота
        if (!Files.exists(expectedPath)) {
            Files.copy(actualPath, expectedPath);
            System.out.println("Эталонный скриншот создан. Проверьте его корректность и перезапустите тест.");
            return; // Прерываем тест при первом запуске
        }

    long mismatch = Files.mismatch(actualPath, Paths.get("expected.jpg"));

    //! Если найдены различия, сохраняем diff-файл
    if (mismatch != -1) {
      Path diffPath = Paths.get("diff.png");
      Files.copy(actualPath, diffPath);
      System.out.println("Обнаружены визуальные различия!");
      System.out.println("Diff-файл сохранен: " + diffPath.toAbsolutePath());
    }

    assertThat(mismatch).isEqualTo(-1);
  }
}
