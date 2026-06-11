package com.itqa.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Master runner that executes ALL test cases from all 4 members.
 * Run this for the complete report: mvn test -Dtest=AllTestsRunner
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.itqa.steps",
                "com.itqa.hooks"
        },
        plugin = {
                "pretty",
                "json:target/cucumber-reports/all-report.json",
                "html:target/cucumber-reports/all-report.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true,
        publish = false
)
public class AllTestsRunner {
}
