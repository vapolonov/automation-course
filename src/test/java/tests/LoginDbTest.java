package tests;

import base.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static config.DataBase.db;
import static config.Env.env;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class LoginDbTest extends BaseTest {
  private Connection connection;

    @BeforeEach
    void setup() throws SQLException {
        // Создание пользователя в БД
        connection = DriverManager.getConnection(
                db.getDbUrl(),
                db.getDbUser(),
                db.getDbPass()
        );

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "INSERT INTO users (username, password) VALUES ('tomsmith', 'SuperSecretPassword!')"
            );
        }

    }

    @Test
    void testLoginWithDbUser() throws SQLException {
        // Получение данных из БД
        String username = null;
        String password = null;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT username, password FROM users WHERE username = 'tomsmith'")) {

            if (rs.next()) {
                username = rs.getString("username");
                password = rs.getString("password");
            }
        }

        assertNotNull(username, "Username not found in DB");
        assertNotNull(password, "Password not found in DB");

        // Выполнение логина
        page.navigate(env.getBaseUrl() + "/login");
        page.locator("#username").fill(username);
        page.locator("#password").fill(password);
        page.locator("button[type='submit']").click();

        // Проверка успешной авторизации
        assertTrue(page.locator("#flash").isVisible());
        assertTrue(page.url().endsWith("/secure"));
    }

    @AfterEach
    void teardown() throws SQLException {
        // Удаление тестового пользователя
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "DELETE FROM users WHERE username = 'tomsmith'"
            );
        }
        // Закрытие ресурсов
        if (connection != null) connection.close();
    }
}
