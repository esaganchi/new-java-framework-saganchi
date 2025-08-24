package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.AfterTestExtension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.sql.Driver;
import java.net.URL;
import java.time.Duration;

@ExtendWith(AfterTestExtension.class)
public class BaseTest {
    static WebDriver driver;
    WebDriverWait longWait;

    public static WebDriver getDriver() {
        return driver;
    }

//    @BeforeEach
//    void setup() {
//        driver = new ChromeDriver();
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
//        longWait = new WebDriverWait(driver, Duration.ofSeconds(10));
//    }

    @BeforeEach
    void setup() throws Exception {
        String remoteUrl = System.getenv("SELENIUM_REMOTE_URL");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");

        if (remoteUrl != null && !remoteUrl.isEmpty()) {
            options.addArguments("--headless=new");
            driver = new RemoteWebDriver(new URL(remoteUrl), options);
            System.out.println("[BaseTest] Using RemoteWebDriver: " + remoteUrl);
        } else {
            driver = new ChromeDriver(options);
            System.out.println("[BaseTest] Using local ChromeDriver");
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        longWait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
