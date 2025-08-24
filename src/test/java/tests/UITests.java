package tests;

import io.qameta.allure.Story;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static constants.CommonConstants.UI_BASE_URL;
import static org.assertj.core.api.Assertions.assertThat;

@Story("UI tests")
@Tag("ui")
class UiTests extends BaseTest {

    @Test
    void submitWebFormTest() {
        driver.get(UI_BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Кликаем по ссылке "Web form"
        WebElement webFormLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Web form")));
        webFormLink.click();

        // Вводим текст в поле
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("my-text-id")));
        input.sendKeys("Text");

        // Кликаем по кнопке сабмита (с прокруткой в центр экрана)
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);
        submitBtn.click();

        // Ждём появления текста и проверяем
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.className("display-6"), "Form submitted"));

        Assertions.assertEquals("Form submitted",
                driver.findElement(By.className("display-6")).getText());
    }

    @Test
    void loadingImagesImplicitWaitTest() {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement calendar = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("calendar")));
        WebElement compass  = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("compass")));
        WebElement award    = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("award")));
        WebElement landscape= wait.until(ExpectedConditions.presenceOfElementLocated(By.id("landscape")));

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(compass.getAttribute("src")).containsIgnoringCase("compass");
        softly.assertThat(calendar.getAttribute("src")).containsIgnoringCase("calendar");
        softly.assertThat(award.getAttribute("src")).containsIgnoringCase("award");
        softly.assertThat(landscape.getAttribute("src")).containsIgnoringCase("landscape");
        softly.assertAll();
    }

    @Test
    void loadingImagesExplicitWaitTest() {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");

        WebElement landscape = longWait.until(ExpectedConditions.presenceOfElementLocated(By.id("landscape")));
        assertThat(landscape.getAttribute("src")).containsIgnoringCase("landscape");
    }
}
