package com.itqa.steps;

import com.itqa.utils.TestDataStore;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;
import io.cucumber.java.After;
import static org.junit.Assert.*;
import com.itqa.utils.ApiUtils;

/**
 * Step definitions for Sales Management API test cases.
 * Tester: Sharhaan M.F.M.
 * Test cases: TC_SAL_API_01 – TC_SAL_API_06, TC_SAL_API_09, TC_SAL_API_10
 *
 * HTTP call steps (POST/GET/PUT/DELETE) are shared via CategoryAPISteps.
 */
public class SalesAPISteps {

    // TC_SAL_API_04 sort assertion
    @Then("the sales records should be sorted by {string} in {string} order")
    public void salesRecordsShouldBeSorted(String field, String direction) {

        Response response = TestDataStore.get("lastResponse");

        List<Map<String, Object>> sales =
                response.jsonPath().getList("$");

        assertNotNull("Response should contain sales records", sales);

        boolean descending =
                direction.equalsIgnoreCase("desc");

        for (int i = 0; i < sales.size() - 1; i++) {

            Comparable current;
            Comparable next;

            switch (field) {

                case "quantity":
                    current = ((Number) sales.get(i)
                            .get("quantity")).doubleValue();

                    next = ((Number) sales.get(i + 1)
                            .get("quantity")).doubleValue();
                    break;

                case "totalPrice":
                    current = ((Number) sales.get(i)
                            .get("totalPrice")).doubleValue();

                    next = ((Number) sales.get(i + 1)
                            .get("totalPrice")).doubleValue();
                    break;

                case "soldAt":
                    current = sales.get(i)
                            .get("soldAt")
                            .toString();

                    next = sales.get(i + 1)
                            .get("soldAt")
                            .toString();
                    break;

                case "plant.name":

                    Map<String, Object> plant1 =
                            (Map<String, Object>) sales.get(i)
                                    .get("plant");

                    Map<String, Object> plant2 =
                            (Map<String, Object>) sales.get(i + 1)
                                    .get("plant");

                    current = plant1.get("name")
                            .toString()
                            .toLowerCase();

                    next = plant2.get("name")
                            .toString()
                            .toLowerCase();
                    break;

                default:
                    fail("Unsupported sort field: " + field);
                    return;
            }

            int comparison = current.compareTo(next);

            if (descending) {
                assertTrue(
                        "Expected descending order but found "
                                + current + " before " + next,
                        comparison >= 0);
            } else {
                assertTrue(
                        "Expected ascending order but found "
                                + current + " before " + next,
                        comparison <= 0);
            }
        }
    }

    @When("I record the current stock of the plant")
    public void recordCurrentStockOfPlant() {

        Integer plantId =
                TestDataStore.get("createdPlantId");

        String adminToken =
                ApiUtils.getAdminToken();

        Response response =
                ApiUtils.givenWithToken(adminToken)
                        .get("/api/plants/" + plantId);

        Integer stock =
                response.jsonPath().getInt("quantity");

        TestDataStore.put("stockBeforeSale", stock);

        System.out.println(
                "Plant ID: " + plantId +
                ", Stock Before Sale: " + stock);
    }

    @Then("the plant stock should be reduced by {int}")
    public void plantStockShouldBeReducedBy(int soldQty) {

        Integer plantId =
                TestDataStore.get("createdPlantId");

        Integer stockBefore =
                TestDataStore.get("stockBeforeSale");

        String adminToken =
                ApiUtils.getAdminToken();

        Response response =
                ApiUtils.givenWithToken(adminToken)
                        .get("/api/plants/" + plantId);

        Integer stockAfter =
                response.jsonPath().getInt("quantity");

        System.out.println(
                "Stock Before: " + stockBefore +
                ", Stock After: " + stockAfter);

        assertEquals(
                "Plant stock was not reduced correctly",
                stockBefore - soldQty,
                stockAfter.intValue()
        );
    }

    @Given("a plant exists with at least {int} units in stock")
    public void plantExistsWithStock(int requiredStock) {

        String adminToken = ApiUtils.getAdminToken();

        Response response =
                ApiUtils.givenWithToken(adminToken)
                        .get("/api/plants");

        List<Map<String, Object>> plants =
                response.jsonPath().getList("$");

        for (Map<String, Object> plant : plants) {

            Integer quantity =
                    ((Number) plant.get("quantity")).intValue();

            if (quantity >= requiredStock) {

                Integer plantId =
                        ((Number) plant.get("id")).intValue();

                TestDataStore.put("createdPlantId", plantId);

                System.out.println(
                        "Using plant id=" + plantId +
                        " stock=" + quantity);

                return;
            }
        }

        fail("No plant found with stock >= " + requiredStock);
    }

    @Given("a sales record exists in the system sales")
    public void aSalesRecordExistsInSystem() {

        String adminToken = ApiUtils.getAdminToken();

        Response response =
                ApiUtils.givenWithToken(adminToken)
                        .get("/api/sales");

        List<Map<String, Object>> sales =
                response.jsonPath().getList("$");

        assertNotNull(sales);
        assertFalse("No sales records found", sales.isEmpty());

        Map<String, Object> sale = sales.get(0);

        TestDataStore.put("deletedSaleBackup", sale);

        Integer saleId =
                ((Number) sale.get("id")).intValue();

        TestDataStore.put("createdSalesId", saleId);

        System.out.println(
                "Using existing sale ID = " + saleId);
    }


    @After("@TC_SAL_API_03")
    public void restoreDeletedSale() {

        String adminToken = ApiUtils.getAdminToken();

        Map<String, Object> sale =
                (Map<String, Object>) TestDataStore.get("deletedSaleBackup");

        if (sale == null) {
            return;
        }

        Map<String, Object> plant =
                (Map<String, Object>) sale.get("plant");

        Integer plantId =
                ((Number) plant.get("id")).intValue();

        Integer quantity =
                ((Number) sale.get("quantity")).intValue();

        Response restoreResponse =
                ApiUtils.givenWithToken(adminToken)
                        .post("/api/sales/plant/"
                                + plantId
                                + "?quantity="
                                + quantity);

        System.out.println(
                "Restore Sale -> HTTP "
                        + restoreResponse.statusCode());
    }

    @After("@TC_SAL_API_07")
    public void restoreSaleIfDeleted() {

    Integer saleId =
            TestDataStore.get("createdSalesId");

    if (saleId == null) {
        return;
    }

    String adminToken =
            ApiUtils.getAdminToken();

    Response verify =
            ApiUtils.givenWithToken(adminToken)
                    .get("/api/sales/" + saleId);

    if (verify.statusCode() == 200) {

        System.out.println(
                "Sale still exists. No restore needed.");
        return;
    }

    System.out.println(
            "Sale missing. Restoring backup...");

    Map<String, Object> sale =
            (Map<String, Object>)
                    TestDataStore.get("deletedSaleBackup");

    if (sale == null) {
        return;
    }

    Map<String, Object> plant =
            (Map<String, Object>) sale.get("plant");

    Integer plantId =
            ((Number) plant.get("id")).intValue();

    Integer quantity =
            ((Number) sale.get("quantity")).intValue();

    Response restoreResponse =
            ApiUtils.givenWithToken(adminToken)
                    .post("/api/sales/plant/"
                            + plantId
                            + "?quantity="
                            + quantity);

    System.out.println(
            "Restore Sale -> HTTP "
                    + restoreResponse.statusCode());
    }

    @After("@TC_SAL_API_09")
    public void restoreUpdatedSale() {

        Response verify =
                ApiUtils.givenWithToken(ApiUtils.getAdminToken())
                        .get("/api/sales/" +
                                TestDataStore.get("createdSalesId"));

        System.out.println(
                "Post-test verification status: "
                        + verify.statusCode());
    }
}
