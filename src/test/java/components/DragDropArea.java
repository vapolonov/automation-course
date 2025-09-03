package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class DragDropArea {
  private final Page page;

  public DragDropArea(Page page) {
    this.page = page;
  }

  @Step("Перетащить элемент 'A' в зону 'B'")
  public void dragAToB() {
    Locator elementA = page.locator("#column-a");
    Locator elementB = page.locator("#column-b");
    elementA.dragTo(elementB);
  }

  @Step("Получить текст в зоне 'В'")
  public String getTextB() {
    return page.locator("#column-b").textContent();
  }
}
