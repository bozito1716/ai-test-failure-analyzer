package com.aianalyzer.example;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;

/**
 * Example Cucumber step definitions demonstrating how the AI Failure Analyzer
 * works alongside a real Selenium test.
 *
 * <p>To trigger the AI analysis, intentionally break one of these steps
 * (e.g., use a wrong locator) and run with the AIFailureListener registered.
 */
public class ExampleSteps {

    private WebDriver driver;
    private ExamplePageObject page;
    private String errorText;

    @Given("I open the example page {string}")
    public void iOpenThePage(String url) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu");
        driver = new ChromeDriver(options);
        page   = new ExamplePageObject(driver);
        page.navigateTo(url);
    }

    @When("I enter an invalid email {string}")
    public void iEnterAnInvalidEmail(String email) {
        page.enterEmail(email);
    }

    @When("I click submit")
    public void iClickSubmit() {
        page.clickSubmit();
    }

    @Then("I should see an email validation error")
    public void iShouldSeeAnEmailValidationError() {
        errorText = page.getErrorMessage();
        Assert.assertFalse(errorText.isEmpty(), "Expected an error message but none was displayed");
        System.out.println("✅ Error message displayed: " + errorText);

        if (driver != null) {
            driver.quit();
        }
    }
}
