package tests;
import base.AbsBaseTest;
import com.google.inject.Inject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pages.LoginPage;

import static config.Env.env;

public class ParametrizedLoginTest extends AbsBaseTest {

  @Inject
  private LoginPage loginPage;

  @ParameterizedTest
  @CsvSource({
      "tomsmith, SuperSecretPassword!, Assert successful login, /secure, You logged into a secure area!",
      "wrong_login, SuperSecretPassword!, Assert unsuccessful login, /login, Your username is invalid! ",
      "tomsmith, wrong_password, Assert unsuccessful login, /login, Your password is invalid!"
  })
  public void testLogin(String username, String password, String description, String path, String message) {
    loginPage.navigateTo(env.getBaseUrl() + "/login");
    loginPage.fillLoginForm(username, password);
    loginPage.assertAuthorization(description, path, message);
  }
}
