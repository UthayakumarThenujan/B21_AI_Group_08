package com.itqa.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner for Pirapanchan R. – Plant Management Module (UI + API)
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "src/test/resources/features/plant_ui.feature",
                "src/test/resources/features/plant_api.feature"
        },
        glue = {
                "com.itqa.steps",
                "com.itqa.hooks"
        },
        plugin = {
                "pretty",
                "json:target/cucumber-reports/plant-report.json",
                "html:target/cucumber-reports/plant-report.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@Pirapanchan",
        monochrome = true,
        publish = false
)
public class PlantTestRunner {
}
