package tests;

import base.AbsBaseTest;
import com.github.javafaker.Faker;
import com.microsoft.playwright.Route;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Test;
import pages.DynamicContentPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FakerGenerTest extends AbsBaseTest {

  Faker faker = new Faker();
  private final String mockName = faker.name().fullName();

  @Test
  public void withFakerTest() {
    DynamicContentPage dynamicContentPage = new DynamicContentPage(page());

    page().route("**/dynamic_content", route -> {
      route.fulfill(new Route.FulfillOptions()
          .setBody("<div class='large-10 columns'>" + mockName + "</div>"));
    });

    Allure.step("Открываем страницу", () -> {
      dynamicContentPage.navigateTo("https://the-internet.herokuapp.com/dynamic_content");
    });


    Allure.step("Проверяем, что имя отображается", () -> {
      String actualText = dynamicContentPage.getPageText();
      assertTrue(actualText.contains(mockName),
          "Ожидали, что на странице будет имя: " + mockName);
    });

  }
}
