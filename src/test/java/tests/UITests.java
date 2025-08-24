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

        // Переход на страницу формы
        WebElement webFormLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Web form")));
        webFormLink.click();

        // Ввод текста
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("my-text-id")));
        input.sendKeys("Text");

        // Кнопка сабмита + защита от перекрытий
        WebElement submitBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@type='submit']")));
        waitOverlaysGone(); // если на странице появляются модалки/лоадеры
        scrollCenter(submitBtn);
        safeClick(submitBtn);

        // Ожидание результата
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.className("display-6"), "Form submitted"));

        Assertions.assertEquals("Form submitted",
                driver.findElement(By.className("display-6")).getText());
    }

    @Test
    void loadingImagesImplicitWaitTest() {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/loading-images.html");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement calendar  = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("calendar")));
        WebElement compass   = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("compass")));
        WebElement award     = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("award")));
        WebElement landscape = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("landscape")));

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

    // ----------------- helpers -----------------

    private void scrollCenter(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    /** Ждём исчезновения возможных перекрытий; при необходимости подставь точные селекторы своего проекта */
    private void waitOverlaysGone() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".modal-backdrop, .overlay, .spinner, .loader, [aria-busy='true']")));
    }

    /** Пытаемся кликнуть нормально; если что-то перехватило — делаем JS-клик */
    private void safeClick(WebElement el) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(el)).click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }
}
