package tests;

import io.qameta.allure.Story;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Story("UI POM tests")
@Tag("ui")
class UiPomTests extends BaseTest {

    @Test
    void loginPomTest() {
        LoginPage loginPage = new LoginPage(driver, longWait);
        loginPage.login();

        // Ждём, пока произойдёт редирект после логина
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlMatches(".*login-su(c|)cess.*"));

        String url = driver.getCurrentUrl();
        System.out.println("Actual URL: " + url);

        assertThat(url).matches(".*login-su(c|)cess.*");
    }
}
