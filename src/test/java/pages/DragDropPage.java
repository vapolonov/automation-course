package pages;

import com.microsoft.playwright.Page;
import components.DragDropArea;
import io.qameta.allure.Step;

public class DragDropPage extends AbsBasePage {

  private DragDropArea dragDropArea;

    public DragDropPage(Page page) {
        super(page);
    }

    public DragDropArea dragDropArea() {
        if (dragDropArea == null) {
            dragDropArea = new DragDropArea(page);
        }
        return dragDropArea;
    }

    @Step("Открытие страницы {url}")
    public DragDropPage navigateTo(String url) {
      page.navigate(url);
      return this;
    }
}
