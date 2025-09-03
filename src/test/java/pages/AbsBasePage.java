package pages;

import com.microsoft.playwright.Page;

public abstract class AbsBasePage {
  protected Page page;

  public AbsBasePage(Page page) {
    this.page = page;
  }
}
