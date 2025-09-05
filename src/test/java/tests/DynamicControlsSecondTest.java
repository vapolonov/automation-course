package tests;

import base.AbsBaseTest;
import com.google.inject.Inject;
import org.junit.jupiter.api.Test;
import pages.DynamicControlsPage;

public class DynamicControlsSecondTest extends AbsBaseTest {

  @Inject
  private DynamicControlsPage controlsPage;

  @Test
  public void testCheckboxRemoval() {
    controlsPage.navigateTo("https://the-internet.herokuapp.com/dynamic_controls");
    controlsPage.checkCheckboxA();
    controlsPage.clickRemoveButton();
    controlsPage.isCheckboxVisible();
  }
}
