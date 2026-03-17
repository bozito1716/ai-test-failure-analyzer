package com.aianalyzer.runner;

import com.aianalyzer.listeners.AIFailureListener;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

/**
 * Example TestNG + Cucumber runner with the AIFailureListener registered.
 *
 * <p>Run with: {@code mvn test}
 * <p>The listener is registered via the @Listeners annotation here, but
 * you can also register it globally in testng.xml (see project root).
 */
@Listeners(AIFailureListener.class)
@CucumberOptions(
        features = {"classpath:features"},
        glue     = {"com.aianalyzer.example"},
        plugin   = {
                "pretty",
                "html:target/cucumber.html",
                "json:target/cucumber.json"
        },
        monochrome = true
)
public class ExampleRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
