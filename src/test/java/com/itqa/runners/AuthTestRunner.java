package com.itqa.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner for Asarak A. – Authentication Module (UI + API)
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "src/test/resources/features/auth_ui.feature",
                "src/test/resources/features/auth_api.feature"
        },
        glue = {
                "com.itqa.steps",
                "com.itqa.hooks"
        },
        plugin = {
                "pretty",
                "json:target/cucumber-reports/auth-report.json",
                "html:target/cucumber-reports/auth-report.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@Asarak",
        monochrome = true,
        publish = false
)
public class AuthTestRunner {
}
