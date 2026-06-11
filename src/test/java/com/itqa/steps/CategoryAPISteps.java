package com.itqa.steps;

import com.itqa.utils.ApiUtils;
import com.itqa.utils.TestDataStore;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static org.junit.Assert.*;

/**
 * Step definitions for Category Management API test cases (TC_CAT_API_01 – TC_CAT_API_10).
 * Tester: Thenujan U.
 */
public class CategoryAPISteps {

    private Response lastResponse;

    // ----------------------------------------------------------------
    // Shared step – POST with body (also used in Auth/Sales API steps)
    // ----------------------------------------------------------------

    @When("I send a POST request to {string} with body:")
    public void iSendPostRequest(String path, String body) {
        String token = TestDataStore.getString("adminToken");
        String resolvedPath = resolvePlaceholders(path);
        String timestamp = String.valueOf(System.currentTimeMillis());

        body = body.replace("{timestamp}",
            timestamp.substring(timestamp.length() - 4)); // last 4 digits

        if (token != null) {
            lastResponse = ApiUtils.givenWithToken(token)
                    .body(body)
                    .post(resolvedPath);
        } else {
            lastResponse = ApiUtils.given()
                    .body(body)
                    .post(resolvedPath);
        }
        TestDataStore.put("lastResponse", lastResponse);
        System.out.println("POST " + resolvedPath + " → " + lastResponse.statusCode());
    }

    @When("I send a GET request to {string}")
    public void iSendGetRequest(String path) {
        // Use userToken if set (user-role test), otherwise adminToken
        String userToken  = TestDataStore.getString("userToken");
        String adminToken = TestDataStore.getString("adminToken");
        String token = userToken != null ? userToken : adminToken;
        String resolvedPath = resolvePlaceholders(path);

        if (token != null) {
            lastResponse = ApiUtils.givenWithToken(token).get(resolvedPath);
        } else {
            lastResponse = ApiUtils.given().get(resolvedPath);
        }
        TestDataStore.put("lastResponse", lastResponse);
        System.out.println("GET " + resolvedPath + " → " + lastResponse.statusCode()
                + " (token role: " + (userToken != null ? "USER" : "ADMIN") + ")");
    }

    @When("I send a GET request to {string} without authentication")
    public void iSendGetRequestWithoutAuth(String path) {
        String resolvedPath = resolvePlaceholders(path);
        lastResponse = ApiUtils.given().get(resolvedPath);
        TestDataStore.put("lastResponse", lastResponse);
        System.out.println("GET (no auth) " + resolvedPath + " → " + lastResponse.statusCode());
    }

    @When("I send a PUT request to {string} with body:")
    public void iSendPutRequest(String path, String body) {
        // Use userToken if set (user-role test), otherwise adminToken
        String userToken  = TestDataStore.getString("userToken");
        String adminToken = TestDataStore.getString("adminToken");
        String token = userToken != null ? userToken : adminToken;
        String resolvedPath = resolvePlaceholders(path);

        if (token != null) {
            lastResponse = ApiUtils.givenWithToken(token).body(body).put(resolvedPath);
        } else {
            lastResponse = ApiUtils.given().body(body).put(resolvedPath);
        }
        TestDataStore.put("lastResponse", lastResponse);
        System.out.println("PUT " + resolvedPath + " → " + lastResponse.statusCode()
                + " (token role: " + (userToken != null ? "USER" : "ADMIN") + ")");
        System.out.println("Response Body:");
        System.out.println(lastResponse.getBody().asPrettyString());
    }

    @When("I send a DELETE request to {string}")
    public void iSendDeleteRequest(String path) {
        // Use userToken if set (user-role test), otherwise adminToken
        String userToken  = TestDataStore.getString("userToken");
        String adminToken = TestDataStore.getString("adminToken");
        String token = userToken != null ? userToken : adminToken;
        String resolvedPath = resolvePlaceholders(path);

        if (token != null) {
            lastResponse = ApiUtils.givenWithToken(token).delete(resolvedPath);
        } else {
            lastResponse = ApiUtils.given().delete(resolvedPath);
        }
        TestDataStore.put("lastResponse", lastResponse);
        System.out.println("DELETE " + resolvedPath + " → " + lastResponse.statusCode()
                + " (token role: " + (userToken != null ? "USER" : "ADMIN") + ")");
    }

    // ----------------------------------------------------------------
    // Response assertions
    // ----------------------------------------------------------------

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        lastResponse = TestDataStore.get("lastResponse");
        assertEquals("Expected HTTP " + expectedStatus,
                expectedStatus, lastResponse.statusCode());
    }

    @Then("the response body should contain {string}")
    public void responseBodyShouldContain(String expectedText) {
        lastResponse = TestDataStore.get("lastResponse");
        assertTrue("Expected response body to contain: " + expectedText,
                lastResponse.body().asString().contains(expectedText));
    }

    @Then("the response is a JSON array")
    public void responseIsJsonArray() {
        lastResponse = TestDataStore.get("lastResponse");
        String body = lastResponse.body().asString().trim();
        assertTrue("Expected JSON array response", body.startsWith("["));
    }

    @Then("the response is a JSON array or paginated response")
    public void responseIsJsonArrayOrPaginated() {
        lastResponse = TestDataStore.get("lastResponse");
        String body = lastResponse.body().asString().trim();
        assertTrue("Expected array or paginated response",
                body.startsWith("[") || body.contains("content") || body.contains("data"));
    }

    // ----------------------------------------------------------------
    // ID saving helpers
    // ----------------------------------------------------------------

    @Then("I save the created category ID as {string}")
    public void saveCreatedCategoryId(String storeKey) {
        lastResponse = TestDataStore.get("lastResponse");
        int id = lastResponse.jsonPath().getInt("id");
        TestDataStore.put(storeKey, id);
        System.out.println("Saved " + storeKey + " = " + id);
    }

    @Then("I save the created plant ID as {string}")
    public void saveCreatedPlantId(String storeKey) {
        lastResponse = TestDataStore.get("lastResponse");
        int id = lastResponse.jsonPath().getInt("id");
        TestDataStore.put(storeKey, id);
    }

    @Then("I save the created sales ID as {string}")
    public void saveCreatedSalesId(String storeKey) {
        lastResponse = TestDataStore.get("lastResponse");
        int id = lastResponse.jsonPath().getInt("id");
        TestDataStore.put(storeKey, id);
    }

    // ----------------------------------------------------------------
    // Pre-conditions – create data via API
    // ----------------------------------------------------------------

    @Given("a category exists with name {string}")
    public void aCategoryExistsWithName(String catName) {
        // Always use ADMIN token to create the category (setup step)
        // but PRESERVE the scenario token (user/admin) for the actual test action
        String scenarioAdminToken = TestDataStore.getString("adminToken");
        String scenarioUserToken  = TestDataStore.getString("userToken");

        String adminToken = ApiUtils.getAdminToken();

        Response resp = ApiUtils.givenWithToken(adminToken)
                .body("{\"name\":\"" + catName + "\"}")
                .post("/api/categories");

        if (resp.statusCode() == 201) {
            TestDataStore.put("createdCategoryId", resp.jsonPath().getInt("id"));
        } else {
            // Category may already exist; try to find it
            Response list = ApiUtils.givenWithToken(adminToken).get("/api/categories");
            list.jsonPath().getList("$").stream()
                    .filter(c -> catName.equals(((java.util.Map<?, ?>) c).get("name")))
                    .findFirst()
                    .ifPresent(c -> TestDataStore.put("createdCategoryId",
                            ((java.util.Map<?, ?>) c).get("id")));
        }
        System.out.println("Category '" + catName + "' ID: " + TestDataStore.getString("createdCategoryId"));

        // Restore the scenario's token context (do NOT pollute user-token tests with admin token)
        if (scenarioUserToken != null) {
            TestDataStore.put("userToken", scenarioUserToken);
            TestDataStore.put("adminToken", null);  // clear admin so PUT/DELETE use user token
        } else {
            TestDataStore.put("adminToken", scenarioAdminToken != null ? scenarioAdminToken : adminToken);
        }
    }

    @Given("a plant exists in the system")
    public void aPlantExistsInSystem() {
        // Reuse existing createdPlantId if already set
        if (TestDataStore.getString("createdPlantId") != null) return;

        String adminToken = ApiUtils.getAdminToken();
        // Ensure at least one category exists
        Response catResp = ApiUtils.givenWithToken(adminToken)
                .body("{\"name\":\"AutoCat\"}")
                .post("/api/categories");
        int catId = catResp.statusCode() == 201
                ? catResp.jsonPath().getInt("id")
                : ApiUtils.givenWithToken(adminToken).get("/api/categories")
                    .jsonPath().getInt("[0].id");

        Response plantResp = ApiUtils.givenWithToken(adminToken)
                .body("{\"name\":\"AutoPlant\",\"description\":\"Auto-created\",\"price\":9.99,\"quantity\":50}")
                .post("/api/plants/category/" + catId);
        if (plantResp.statusCode() == 201) {
            TestDataStore.put("createdPlantId", plantResp.jsonPath().getInt("id"));
        }
    }

    @Given("a deletable plant exists in the system")
    public void aDeletablePlantExistsInSystem() {
        String adminToken = ApiUtils.getAdminToken();
        Response catResp = ApiUtils.givenWithToken(adminToken)
                .body("{\"name\":\"DeleteCat\"}")
                .post("/api/categories");
        int catId = catResp.statusCode() == 201
                ? catResp.jsonPath().getInt("id")
                : ApiUtils.givenWithToken(adminToken).get("/api/categories")
                    .jsonPath().getInt("[0].id");

        Response plantResp = ApiUtils.givenWithToken(adminToken)
                .body("{\"name\":\"DeletePlant\",\"description\":\"To be deleted\",\"price\":1.0,\"quantity\":5}")
                .post("/api/plants/category/" + catId);
        if (plantResp.statusCode() == 201) {
            TestDataStore.put("deletablePlantId", plantResp.jsonPath().getInt("id"));
        }
    }

    @Given("a sales record exists in the system")
    public void aSalesRecordExistsInSystem() {
        if (TestDataStore.getString("createdSalesId") != null) return;
        String adminToken = ApiUtils.getAdminToken();
        aPlantExistsInSystem();
        int plantId = TestDataStore.getInt("createdPlantId");
        Response resp = ApiUtils.givenWithToken(adminToken)
                .post("/api/sales/plant/" + plantId + "?quantity=1");
        if (resp.statusCode() == 201) {
            TestDataStore.put("createdSalesId", resp.jsonPath().getInt("id"));
        }
    }

    // ----------------------------------------------------------------
    // Helper – replace {placeholder} with stored values
    // ----------------------------------------------------------------

    private String resolvePlaceholders(String path) {
        return path
                .replace("{createdCategoryId}", String.valueOf(TestDataStore.getString("createdCategoryId") != null ? TestDataStore.getString("createdCategoryId") : "1"))
                .replace("{createdPlantId}",    String.valueOf(TestDataStore.getString("createdPlantId")    != null ? TestDataStore.getString("createdPlantId")    : "1"))
                .replace("{createdSalesId}",    String.valueOf(TestDataStore.getString("createdSalesId")    != null ? TestDataStore.getString("createdSalesId")    : "1"))
                .replace("{deletablePlantId}",  String.valueOf(TestDataStore.getString("deletablePlantId")  != null ? TestDataStore.getString("deletablePlantId")  : "1"));
    }
}
