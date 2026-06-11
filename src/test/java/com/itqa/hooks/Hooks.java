package com.itqa.hooks;

import com.itqa.config.DriverManager;
import com.itqa.utils.TestDataStore;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

/**
 * Cucumber hooks – initialise/teardown WebDriver for UI tests;
 * attach screenshots on failure.
 */
public class Hooks {

    @Before(order = 1)
    public void setUp(Scenario scenario) {
        // Only spin up browser for UI scenarios
        if (isUiScenario(scenario)) {
            DriverManager.initDriver();
        }
    }

    @After(order = 1)
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            if (scenario.isFailed()) {
                // Attach screenshot to Allure report
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot on Failure",
                        "image/png", new ByteArrayInputStream(screenshot), "png");
                scenario.attach(screenshot, "image/png", "Screenshot on Failure");
            }
            DriverManager.quitDriver();
        }
    }

    @AfterAll
    public static void afterAll() {
        TestDataStore.clear();
    }

    private boolean isUiScenario(Scenario scenario) {
        return scenario.getSourceTagNames().stream()
                .anyMatch(tag -> tag.equalsIgnoreCase("@UI")
                        || tag.equalsIgnoreCase("@ui_test"));
    }
}
