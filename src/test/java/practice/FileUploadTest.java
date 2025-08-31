package practice;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class FileUploadTest {
  static Playwright playwright;
  static APIRequestContext request;
  static Path tempFile;

  @BeforeAll
  public static void setup() {
    playwright = Playwright.create();
    request = playwright.request().newContext();
  }

  @Test
  void testFileUploadAndDownload() throws IOException {
    // Создание пустого изображения (1x1 пиксель)
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    image.setRGB(0, 0, 0xFF0000);
    tempFile = Files.createTempFile("test-image", ".png");
    ImageIO.write(image, "png", tempFile.toFile());

    // Загрузка файла
    APIResponse uploadResponse = request.post(
        "https://httpbin.org/post",
        RequestOptions.create().setMultipart(
            FormData.create().set("file", tempFile)
        )
    );

    assertEquals(200, uploadResponse.status(), "Upload should return 200");

    // Проверка получения файла
    String responseBody = uploadResponse.text();
    assertTrue(responseBody.contains("data:image/png;base64"));

    // Верификация содержимого
    String base64Data = responseBody.split("\"file\": \"")[1].split("\"")[0];
    byte[] receivedBytes = Base64.getDecoder().decode(base64Data.split(",")[1]);
    assertArrayEquals(Files.readAllBytes(tempFile), receivedBytes);

    // Скачивание и проверка эталона
    APIResponse downloadResponse = request.get("https://httpbin.org/image/png");
    assertEquals("image/png", downloadResponse.headers().get("content-type"));

    // Проверка сигнатуры PNG
    byte[] content = downloadResponse.body();
    assertEquals(0x89, content[0] & 0xFF);
    assertEquals(0x50, content[1] & 0xFF);
    assertEquals(0x4E, content[2] & 0xFF);
    assertEquals(0x47, content[3] & 0xFF);
  }

  @AfterAll
  public static void tearDown() {
    if (request != null) request.dispose();
    if (playwright != null) playwright.close();
    if (tempFile != null) {
    try {
      Files.deleteIfExists(tempFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  }
}
