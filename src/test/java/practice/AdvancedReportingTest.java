package practice;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import extension.WebTest;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Тесты для the-internet.herokuapp.com")
@Feature("Работа с JavaScript-алертами")
@WebTest
public class AdvancedReportingTest {
  private static ExtentReports extent;
  private Browser browser;
  private Playwright playwright;
  private Page page;

  SoftAssertions softly = new SoftAssertions();

  @BeforeAll
  static void setupExtent() {
    ExtentSparkReporter reporter = new ExtentSparkReporter("allure-results/extent-report.html");
    reporter.config().setDocumentTitle("Playwright Report");
    extent = new ExtentReports();
    extent.attachReporter(reporter);
  }

  @BeforeEach
  void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(1000));
  }

  @AfterEach
  void tearDownEach() {
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @Test
  @Story("Проверка алертов")
  @Description("Тест взаимодействия с JS-алертами")
  @Severity(SeverityLevel.NORMAL)
  void testJavaScriptAlerts() {

    ExtentTest test = extent.createTest("Тест страницы логина");

    try {
      test.log(Status.INFO, "Запущен браузер Chromium");

      BrowserContext context = browser.newContext();
      page = context.newPage();

      Allure.step("Открыть страницу с алертами", () -> {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts", new Page.NavigateOptions()
            .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        assertEquals("JavaScript Alerts", page.locator("h3").textContent(),
            "Страница должна содержать заголовок 'JavaScript Alerts'");
        test.pass("Страница загружена");
      });

      Allure.step("Кликнуть на кнопку 'Click for JS Alert'", () -> {
        page.click("button[onclick='jsAlert()']");
        test.info("Открыт Alert");
      });

      page.onDialog(dialog -> {
        String alertText = dialog.message();

        if (alertText.equals("I am a JS Alert")) {
          test.pass("Текст алерта корректен");
        } else {
          test.fail("Неверный текст алерта: " + alertText);
        }

        Allure.step("Проверить текст алерта", () -> {
          assertEquals("I am a JS Alert", alertText);
        });

        Allure.step("Закрыть окно алерта", () -> {
          dialog.accept();
        });
      });

    // Так не работает т.к. Allure.step внутри себя ловит исключение и AttachExtension уже не видит исключение
    // (оно не "долетает" до JUnit lifecycle)
    //      Allure.step("Проверить текст после закрытия алерта", () -> {
    //        assertEquals("You successfully clicked an aler", page.locator("#result").textContent());
    //      });
      Allure.step("Проверить текст после закрытия алерта");
      softly.assertThat(page.locator("#result").textContent())
          .isEqualTo("You successfully clicked an alert");

      String screenshotFile = "alert-success.png";
      Path screenshotPath = Paths.get("allure-results", screenshotFile);
      page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath));

      test.pass("Скриншот после теста",
          MediaEntityBuilder.createScreenCaptureFromPath(screenshotFile).build());

      softly.assertAll();

    } catch (Exception e) {
      test.fail("Тест упал: " + e.getMessage());
    }
  }

  @AfterAll
  static void tearDown() {
    extent.flush();
  }
}
