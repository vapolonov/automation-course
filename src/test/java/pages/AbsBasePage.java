package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Step;

public abstract class AbsBasePage {
  protected Page page;

  public AbsBasePage(Page page) {
    this.page = page;
  }

  @Step("Открытие страницы {url}")
    public void navigateTo(String url) {
      page.navigate(url,
          new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }
}
