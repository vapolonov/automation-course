package tests;

import base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MockedApiTest extends BaseTest {
  // Мок-сервис для имитации API
  @Mock
  private ApiService apiService;

  @BeforeEach
  void setUp() {
    // Настраиваем поведение мока — возвращаем тестовые данные
    when(apiService.fetchUserData())
        .thenReturn("{\"name\": \"Test User\", \"email\": \"test@example.com\"}");
  }

  @Test
  void testUserProfileWithMockedApi() {
    // Используем мок вместо реального API
    String userData = apiService.fetchUserData();

    // Передаем данные в браузер
    page.navigate("https://the-internet.herokuapp.com/dynamic_content");
    page.evaluate("(data) => { window.userData = data; }", userData);

    // Проверяем, что данные корректно обрабатываются
    Object result = page.evaluate("() => window.userData");
    assertNotNull(result);
    assertTrue(result.toString().contains("Test User"));
  }

  // Тестовый класс-заглушка для API сервиса
  static class ApiService {
    public String fetchUserData() {
      // Имитация медленного API-запроса
      try {
        Thread.sleep(3000); // 3 секунды задержки
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return "{\"name\": \"Real User\", \"email\": \"real@example.com\"}";
    }
  }
}
