package practice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;


public class TodoApiTest {
  Playwright playwright;
  APIRequestContext requestContext;
  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    playwright = Playwright.create();
    requestContext = playwright.request().newContext(
        new APIRequest.NewContextOptions()
            .setBaseURL("https://jsonplaceholder.typicode.com")
    );
  }

  @Test
  void testUserApi() throws JsonProcessingException {
    APIResponse response = requestContext.get("/users/1");

    assertEquals(200, response.status(), "Статус ответа должен быть 200");
    assertThat(response).isOK();

    String body = response.text();
    JsonNode user = objectMapper.readTree(body);

    assertEquals(1, user.get("id").asInt());
    assertEquals("Leanne Graham", user.get("name").asText());
    assertEquals("Bret", user.get("username").asText());
    assertEquals("Sincere@april.biz", user.get("email").asText());
  }

  @Test
  void userSchemaTest() throws IOException {
    // 1. GET-запрос
    APIResponse response = requestContext.get("/users");

    // 2. Проверка статуса
    assertEquals(200, response.status(), "Статус ответа должен быть 200");

    // 3. Получаем JSON строкой
    String body = response.text();

    // 4. Валидируем через Rest-Assured JsonSchemaValidator
    try (InputStream schemaStream = getClass().getClassLoader()
        .getResourceAsStream("schemas/user-schema.json")) {

      if (schemaStream == null) {
        throw new RuntimeException("Файл схемы users-schema.json не найден в resources/schemas");
      }
      assertThat(body, matchesJsonSchema(schemaStream));
    }
  }

  @AfterEach
  void tearDown() {
    requestContext.dispose();
    playwright.close();
  }
}
