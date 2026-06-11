package com.itqa.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner for Sharhaan M.F.M. – Sales Management Module (UI + API)
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "src/test/resources/features/sales_ui.feature",
                "src/test/resources/features/sales_api.feature"
        },
        glue = {
                "com.itqa.steps",
                "com.itqa.hooks"
        },
        plugin = {
                "pretty",
                "json:target/cucumber-reports/sales-report.json",
                "html:target/cucumber-reports/sales-report.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@Sharhaan",
        monochrome = true,
        publish = false
)
public class SalesTestRunner {
}
