package tests;

import base.AbsBaseTest;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import pages.DragDropPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.CONCURRENT)
public class DragDropTest extends AbsBaseTest {

  @Test
  public void testDragAndDrop() {
    DragDropPage dragDropPage = new DragDropPage(page());

    dragDropPage.navigateTo("https://the-internet.herokuapp.com/drag_and_drop");

    dragDropPage.dragDropArea().dragAToB();

    Allure.step("Проверить, что текст в зоне 'B' изменился на 'A'. ", () -> {
      assertEquals("A", dragDropPage.dragDropArea().getTextB());
    });

  }
}
