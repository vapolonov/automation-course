package extension;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

public class AttachExtension implements
    AfterEachCallback,
    TestExecutionExceptionHandler,
    LifecycleMethodExecutionExceptionHandler {

  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create(AttachExtension.class);

  private Page getPageFromTestInstance(ExtensionContext context) {
    Object testInstance = context.getRequiredTestInstance();
    try {
      Field field = testInstance.getClass().getDeclaredField("page");
      field.setAccessible(true);
      return (Page) field.get(testInstance);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось получить поле 'page' из теста", e);
    }
  }

  private void doScreenshot(Page page) {
    if (page != null) {
      byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
      Allure.addAttachment("Screenshot on fail", new ByteArrayInputStream(screenshot));
    }
  }

  private void attachVideo(Page page) {
    try {
      if (page.video() != null) {
        Path videoPath = page.video().path();
        Allure.addAttachment("Video", "video/mp4",
            Files.newInputStream(videoPath), ".mp4");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void attachArtifactsOnFail(Page page, ExtensionContext context) {
    doScreenshot(page);
    context.getStore(NAMESPACE).put("testFailed", true);
  }

  @Override
  public void afterEach(ExtensionContext context) {
    Boolean failed = context.getStore(NAMESPACE).get("testFailed", Boolean.class);
    if (failed != null && failed) {
      Page page = getPageFromTestInstance(context);
      attachVideo(page);
    }
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    Page page = getPageFromTestInstance(context);
    attachArtifactsOnFail(page, context);
    throw throwable;
  }

  @Override
  public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    Page page = getPageFromTestInstance(context);
    attachArtifactsOnFail(page, context);
    throw throwable;
  }

  @Override
  public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    Page page = getPageFromTestInstance(context);
    attachArtifactsOnFail(page, context);
    throw throwable;
  }
}
