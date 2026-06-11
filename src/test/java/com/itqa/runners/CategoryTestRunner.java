package com.itqa.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner for Thenujan U. – Category Management (UI + API)
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "src/test/resources/features/category_ui.feature",
                "src/test/resources/features/category_api.feature"
        },
        glue = {
                "com.itqa.steps",
                "com.itqa.hooks"
        },
        plugin = {
                "pretty",
                "json:target/cucumber-reports/category-report.json",
                "html:target/cucumber-reports/category-report.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@Thenujan",
        monochrome = true,
        publish = false
)
public class CategoryTestRunner {
}
