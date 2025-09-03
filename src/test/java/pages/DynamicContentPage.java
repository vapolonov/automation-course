package pages;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class DynamicContentPage extends AbsBasePage {

  public DynamicContentPage(Page page) {
    super(page);
  }

  @Step("Получить текст со страницы")
  public String getPageText() {
    return page.locator(".large-10.columns").first().textContent();
  }
}
