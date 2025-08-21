package practice;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD) // новый экземпляр на каждый тест
@Execution(ExecutionMode.CONCURRENT)
public class ParallelTests {

  private Playwright playwright;
  private Browser browser;
  private BrowserContext context;
  private Page page;

  @BeforeEach
  void setup() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(
        new BrowserType.LaunchOptions().setHeadless(false)
    );
    context = browser.newContext();
    page = context.newPage();
  }

  @AfterEach
  void teardown() {
    if (context != null) context.close();
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @Test
  void testLoginPage() {
    page.navigate("https://the-internet.herokuapp.com/login");
    page.waitForLoadState();
    assertEquals("The Internet", page.title());
  }

  @Test
  void testAddRemoveElements() {
    page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");
    page.waitForLoadState();

    page.click("button:text('Add Element')");
    page.locator("button.added-manually").waitFor();
    assertTrue(page.locator("button.added-manually").isVisible());
  }
}
