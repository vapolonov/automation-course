package practice;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
    }

    @AfterEach
    void teardown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    static Stream<Arguments> testPageLoad() {
        return Stream.of(
                // Chromium тесты
                Arguments.of("chromium", "/"),
                Arguments.of("chromium", "/abtest"),
                Arguments.of("chromium", "/checkboxes"),

                // Firefox тесты
                Arguments.of("firefox", "/"),
                Arguments.of("firefox", "/abtest"),
                Arguments.of("firefox", "/checkboxes")
        );
    }

    @ParameterizedTest
    @MethodSource()
    void testPageLoad(String browserType, String path) {
        browser = selectBrowser(browserType).launch();
        context = browser.newContext();
        page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com" + path);
        assertThat(page.title()).isNotEmpty();
        context.clearCookies();
    }

    private BrowserType selectBrowser(String browser) {
        return switch (browser.toLowerCase()) {
            case "chromium" -> playwright.chromium();
            case "firefox" -> playwright.firefox();
            default -> throw new IllegalArgumentException("Unsupported browser type: " + browser);
        };
    }
}
