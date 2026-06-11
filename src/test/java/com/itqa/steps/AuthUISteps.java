package com.itqa.steps;

import com.itqa.config.ConfigManager;
import com.itqa.config.DriverManager;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Step definitions for Authentication UI test cases.
 * Tester: Asarak A.
 * Test cases: TC_AUTH_UI_01 – TC_AUTH_UI_09, TC_DASH_UI_10
 */
public class AuthUISteps {

    private WebDriver driver() { return DriverManager.getDriver(); }

    private WebDriverWait webWait() {
        return new WebDriverWait(driver(), Duration.ofSeconds(ConfigManager.getExplicitWait()));
    }

    // All steps are already defined in CommonSteps (login/logout/navigation).
    // AuthUISteps provides any auth-specific additional steps.

    // TC_AUTH_UI_09 is handled by CommonSteps "I click the Logout button"

    // No additional unique steps required here; the full Auth UI test scenarios
    // use only CommonSteps definitions.
}

