package com.itqa.steps;

import com.itqa.utils.TestDataStore;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Step definitions for Sales Management API test cases.
 * Tester: Sharhaan M.F.M.
 * Test cases: TC_SAL_API_01 – TC_SAL_API_06, TC_SAL_API_09, TC_SAL_API_10
 *
 * HTTP call steps (POST/GET/PUT/DELETE) are shared via CategoryAPISteps.
 */
public class SalesAPISteps {

    // TC_SAL_API_04 sort assertion
    @Then("the sales records should be sorted by quantity in descending order")
    public void salesSortedByQuantityDesc() {
        Response resp = TestDataStore.get("lastResponse");
        List<Map<String, Object>> records = null;

        try {
            records = resp.jsonPath().getList("content");
        } catch (Exception ignored) {}

        if (records == null) {
            try {
                records = resp.jsonPath().getList("$");
            } catch (Exception ignored) {}
        }

        if (records == null || records.size() < 2) {
            System.out.println("Not enough records to verify sort order");
            return;
        }

        for (int i = 0; i < records.size() - 1; i++) {
            Object q1 = records.get(i).get("quantity");
            Object q2 = records.get(i + 1).get("quantity");
            int qty1 = q1 instanceof Number ? ((Number) q1).intValue() : 0;
            int qty2 = q2 instanceof Number ? ((Number) q2).intValue() : 0;
            assertTrue("Expected descending sort at index " + i + ": " + qty1 + " >= " + qty2, qty1 >= qty2);
        }
    }
}
