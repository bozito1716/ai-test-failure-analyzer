package com.aianalyzer.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Example Page Object showing the recommended pattern for use alongside
 * the AI Failure Analyzer.
 *
 * <p>When tests using this pattern fail, the listener captures the failure
 * and the AI analyzer provides targeted advice based on the specific
 * error and stack trace.
 */
public class ExamplePageObject {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By emailField   = By.id("email");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By errorMessage = By.id("error-msg-email");

    public ExamplePageObject(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateTo(String url) {
        driver.navigate().to(url);
    }

    public void enterEmail(String email) {
        wait.until(ExpectedConditions.elementToBeClickable(emailField)).sendKeys(email);
    }

    public void clickSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }
}
