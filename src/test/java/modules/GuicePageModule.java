package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.microsoft.playwright.Page;
import pages.DynamicControlsPage;
import pages.LoginPage;

public class GuicePageModule extends AbstractModule {

  private final Page page;

  public GuicePageModule(Page page) {
      this.page = page;
  }

  @Override
  protected void configure() {
      bind(Page.class).toInstance(page);
  }

  @Provides
  public DynamicControlsPage controlsPage(Page page) {
      return new DynamicControlsPage(page);
  }

  @Provides
  public LoginPage loginPage(Page page) {
    return new LoginPage(page);
  }
}
