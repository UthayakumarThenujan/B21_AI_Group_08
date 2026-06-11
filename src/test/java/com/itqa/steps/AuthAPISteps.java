package com.itqa.steps;

import com.itqa.utils.TestDataStore;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static org.junit.Assert.*;

/**
 * Step definitions for Authentication API test cases.
 * Tester: Asarak A.
 * Test cases: TC_AUTH_API_01 – TC_AUTH_API_10, TC_SAL_API_07, TC_SAL_API_08
 *
 * HTTP call steps are reused from CategoryAPISteps (shared step glue).
 */
public class AuthAPISteps {

    // All HTTP steps (POST/GET/DELETE) are already defined in CategoryAPISteps.
    // Auth-specific assertions below.

    @Then("the response body should contain {string} field")
    public void responseBodyContainsField(String fieldName) {
        Response resp = TestDataStore.get("lastResponse");
        assertTrue("Expected field '" + fieldName + "' in response",
                resp.body().asString().contains("\"" + fieldName + "\""));
    }
}
