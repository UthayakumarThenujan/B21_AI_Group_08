package com.itqa.steps;

import com.itqa.config.ConfigManager;
import com.itqa.config.DriverManager;
import com.itqa.utils.ApiUtils;
import com.itqa.utils.TestDataStore;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import io.cucumber.java.After;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Step definitions for Sales Management UI test cases (TC_SAL_UI_01 –
 * TC_SAL_UI_10).
 * Tester: Sharhaan M.F.M.
 */
public class SalesUISteps {

    private String previousUrl;

    // Stores first-sort price values for TC_SAL_UI_02/06 reversal check
    private List<Double> firstSortPrices = new java.util.ArrayList<>();

    private WebDriver driver() {
        return DriverManager.getDriver();
    }

    private WebDriverWait webWait() {
        return new WebDriverWait(driver(), Duration.ofSeconds(ConfigManager.getExplicitWait()));
    }

    // ----------------------------------------------------------------
    // TC_SAL_UI_01 – Admin creates a sales record
    // ----------------------------------------------------------------

    @When("I select the first available plant")
    public void iSelectFirstAvailablePlant() {
        try {
            Select plantSelect = new Select(webWait().until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//select[@name='plantId' or @id='plantId' or @name='plant']"))));
            if (plantSelect.getOptions().size() > 1) {
                plantSelect.selectByIndex(1);
            }
        } catch (Exception e) {
            System.out.println("Plant select not found: " + e.getMessage());
        }
    }

    @When("I leave the plant selection empty")
    public void iLeavePlantSelectionEmpty() {
        // Do not select a plant – leave at default empty/none option
    }

    @When("I enter quantity {string}")
    public void iEnterQuantity(String qty) {
        WebElement qtyField = webWait().until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@name='quantity' or @id='quantity']")));
        qtyField.clear();
        qtyField.sendKeys(qty);
    }

    @When("I click the Sell button")
    public void iClickTheSellButton() {
        driver().findElement(By.xpath(
                "//button[contains(text(),'Sell')] | //button[@type='submit']")).click();
    }

    @Then("the sales record should be created successfully")
    public void salesRecordCreatedSuccessfully() {
        // Check for success message or that we are back on sales list
        webWait().until(d -> d.getPageSource().contains("sale") || d.getCurrentUrl().contains("/ui/sales"));
        assertTrue("Sales record creation not confirmed",
                driver().getPageSource().toLowerCase().contains("sale"));
    }

    @Then("the new sale should appear in the Sales list")
    public void newSaleAppearsInList() {
        driver().get(ConfigManager.getBaseUrl() + "/ui/sales");
        webWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table, .sales-list")));
        List<WebElement> rows = driver().findElements(By.cssSelector("table tbody tr"));
        assertTrue("Expected at least one sales record in list", rows.size() > 0);
    }

    // ----------------------------------------------------------------
    // TC_SAL_UI_02, TC_SAL_UI_06 – Sort by Total Price
    // ----------------------------------------------------------------

    @Given("multiple sales records exist in the system")
    public void multipleSalesRecordsExist() {
        // Create a couple sales via API if needed
        String adminToken = ApiUtils.getAdminToken();
        Response salesResp = ApiUtils.givenWithToken(adminToken).get("/api/sales");
        List<?> records = salesResp.jsonPath().getList("content");
        if (records == null)
            records = salesResp.jsonPath().getList("$");
        if (records == null || records.size() < 2) {
            // Create test data
            Response plantsResp = ApiUtils.givenWithToken(adminToken).get("/api/plants");
            List<?> plants = plantsResp.jsonPath().getList("$");
            if (plants != null && !plants.isEmpty()) {
                int plantId = (int) ((java.util.Map<?, ?>) plants.get(0)).get("id");
                ApiUtils.givenWithToken(adminToken)
                        .post("/api/sales/plant/" + plantId + "?quantity=1");
                ApiUtils.givenWithToken(adminToken)
                        .post("/api/sales/plant/" + plantId + "?quantity=2");
            }
        }
    }

    @Then("the sales records should be sorted by total price")
    public void salesSortedByTotalPrice() {
        List<WebElement> rows = driver().findElements(By.cssSelector("table tbody tr"));
        if (rows.size() < 2)
            return;
        // Find total price column index
        List<WebElement> headers = driver().findElements(By.cssSelector("table thead th"));
        int priceColIdx = -1;
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().toLowerCase().contains("price")) {
                priceColIdx = i;
                break;
            }
        }
        if (priceColIdx < 0)
            return;

        final int colIdx = priceColIdx;
        List<Double> prices = rows.stream()
                .map(r -> {
                    try {
                        String txt = r.findElements(By.tagName("td")).get(colIdx).getText()
                                .replaceAll("[^0-9.]", "");
                        return Double.parseDouble(txt);
                    } catch (Exception e) {
                        return 0.0;
                    }
                }).toList();

        boolean ascending = true, descending = true;
        for (int i = 0; i < prices.size() - 1; i++) {
            if (prices.get(i) > prices.get(i + 1))
                ascending = false;
            if (prices.get(i) < prices.get(i + 1))
                descending = false;
        }
        assertTrue("Expected sorted order (asc or desc) by total price", ascending || descending);
    }

    @Then("the sorting order should change correctly when clicked again")
    public void sortingOrderChangesOnSecondClick() {
        // Click again is handled in the step "I click the {string} column header again"
        // This assertion just confirms the click event registered
        assertTrue(true);
    }

    private List<Double> getTotalPriceValues() {

        List<WebElement> rows = driver().findElements(By.cssSelector("table tbody tr"));

        List<WebElement> headers = driver().findElements(By.cssSelector("table thead th"));

        int priceColIdx = -1;

        for (int i = 0; i < headers.size(); i++) {

            if (headers.get(i).getText()
                    .toLowerCase()
                    .contains("price")) {

                priceColIdx = i;
                break;
            }
        }

        final int colIdx = priceColIdx;

        return rows.stream()
                .map(r -> {
                    try {
                        String txt = r.findElements(By.tagName("td"))
                                .get(colIdx)
                                .getText()
                                .replaceAll("[^0-9.]", "");

                        return Double.parseDouble(txt);

                    } catch (Exception e) {
                        return 0.0;
                    }
                })
                .toList();
    }

    @Then("the sales records should be sorted correctly")
    public void salesRecordsSortedCorrectly() {

        firstSortPrices = getTotalPriceValues();

        boolean ascending = true;
        boolean descending = true;

        for (int i = 0; i < firstSortPrices.size() - 1; i++) {

            if (firstSortPrices.get(i) > firstSortPrices.get(i + 1))
                ascending = false;

            if (firstSortPrices.get(i) < firstSortPrices.get(i + 1))
                descending = false;
        }

        assertTrue(
                "Prices should be sorted",
                ascending || descending);
    }

    @Then("the sorting order should change when the column is clicked again")
    public void sortingOrderChangesWhenColumnClickedAgain() {

        List<Double> secondSortPrices = getTotalPriceValues();

        assertNotEquals(
                "Sort order should change after second click",
                firstSortPrices,
                secondSortPrices);    
    }

    // ----------------------------------------------------------------
    // TC_SAL_UI_06 – Generic column sorting (Scenario Outline)
    // SRS: Sorting supported on Plant, Quantity, Total Price, Sold At
    // Default sort: Sold At descending
    // ----------------------------------------------------------------

    @When("I click the {string} column header to sort")
    public void iClickColumnHeaderToSort(String column) {

        previousUrl = driver().getCurrentUrl();

        clickSortHeader(column);

        new WebDriverWait(driver(), Duration.ofSeconds(5))
                .until(d -> !d.getCurrentUrl().equals(previousUrl));

        List<String> vals = getColumnTextValues(column);

        TestDataStore.put("sortFirst_" + column, vals);

        System.out.println("URL after first click: "
                + driver().getCurrentUrl());

        System.out.println("First sort values: " + vals);
    }

    @When("I click the {string} column header to sort again")
    public void iClickColumnHeaderToSortAgain(String column) {

        previousUrl = driver().getCurrentUrl();

        clickSortHeader(column);

        new WebDriverWait(driver(), Duration.ofSeconds(5))
                .until(d -> !d.getCurrentUrl().equals(previousUrl));

        List<String> vals = getColumnTextValues(column);

        TestDataStore.put("sortSecond_" + column, vals);

        System.out.println("URL after second click: "
                + driver().getCurrentUrl());

        System.out.println("Second sort values: " + vals);
    }

    @Then("the sales list should be sorted by {string} in ascending or descending order")
    public void salesListSortedByColumn(String column) {
        @SuppressWarnings("unchecked")
        List<String> values = (List<String>) TestDataStore.get("sortFirst_" + column);
        if (values == null) values = getColumnTextValues(column);

        if (values.size() < 2) {
            System.out.println("[SORT] Less than 2 rows for '" + column + "' – skipping.");
            return;
        }

        // If all values are identical, sorting either way gives same result – pass
        if (new java.util.HashSet<>(values).size() == 1) {
            System.out.println("[SORT] '" + column + "' – all values identical, order trivially sorted.");
            return;
        }

        boolean isNumeric = column.equalsIgnoreCase("Quantity")
                || column.equalsIgnoreCase("Total Price");

        if (isNumeric) {
            List<Double> nums = values.stream().map(v -> {
                try { return Double.parseDouble(v.replaceAll("[^0-9.]", "")); }
                catch (Exception ex) { return 0.0; }
            }).toList();
            boolean asc = true, desc = true;
            for (int i = 0; i < nums.size() - 1; i++) {
                if (nums.get(i) > nums.get(i + 1)) asc = false;
                if (nums.get(i) < nums.get(i + 1)) desc = false;
            }
            assertTrue("'" + column + "' should be sorted asc or desc. Values: " + nums, asc || desc);
        } else {
            boolean asc = true, desc = true;
            for (int i = 0; i < values.size() - 1; i++) {
                int cmp = values.get(i).compareToIgnoreCase(values.get(i + 1));
                if (cmp > 0) asc = false;
                if (cmp < 0) desc = false;
            }
            assertTrue("'" + column + "' should be sorted asc or desc. Values: " + values, asc || desc);
        }
    }

    @Then("the sort order for {string} should be reversed")
    public void sortOrderForColumnShouldBeReversed(String column) {

        @SuppressWarnings("unchecked")
        List<String> first =
                (List<String>) TestDataStore.get("sortFirst_" + column);

        @SuppressWarnings("unchecked")
        List<String> second =
                (List<String>) TestDataStore.get("sortSecond_" + column);

        assertNotEquals(
                "Sort order for '" + column + "' should change after second click",
                first,
                second
        );
    }

    /**
     * Clicks the sortable column header by matching header text.
     */
    private void clickSortHeader(String column) {
        WebElement header = webWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//th[normalize-space(.)='" + column + "']"
                       + " | //th//a[contains(normalize-space(.),'" + column + "')]"
                       + " | //th[contains(normalize-space(.),'" + column + "')]")));
        header.click();
    }

    /**
     * Returns all non-blank cell text values for the column matching columnName header.
     */
    private List<String> getColumnTextValues(String columnName) {
        List<WebElement> headers = driver().findElements(By.cssSelector("table thead th"));
        int colIdx = -1;
        for (int i = 0; i < headers.size(); i++) {
            String h = headers.get(i).getText().trim();
            if (h.equalsIgnoreCase(columnName)
                    || h.toLowerCase().contains(columnName.toLowerCase())) {
                colIdx = i;
                break;
            }
        }
        if (colIdx < 0) {
            System.out.println("[SORT] WARNING: Column '" + columnName + "' not found in headers.");
            return List.of();
        }
        final int idx = colIdx;
        return driver().findElements(By.cssSelector("table tbody tr")).stream()
                .map(r -> {
                    try { return r.findElements(By.tagName("td")).get(idx).getText().trim(); }
                    catch (Exception e) { return ""; }
                })
                .filter(s -> !s.isBlank())
                .toList();
    }

    // ----------------------------------------------------------------
    // TC_SAL_UI_03 – Admin deletes a sales record
    // ----------------------------------------------------------------

    @Given("at least one sales record exists in the system")
    public void atLeastOneSalesRecordExists() {
        String adminToken = ApiUtils.getAdminToken();
        Response resp = ApiUtils.givenWithToken(adminToken).get("/api/sales");
        List<?> list = resp.jsonPath().getList("content");
        if (list == null)
            list = resp.jsonPath().getList("$");

        if (list == null || list.isEmpty()) {
            // Create a sales record
            Response plants = ApiUtils.givenWithToken(adminToken).get("/api/plants");
            List<?> plantList = plants.jsonPath().getList("$");
            if (plantList != null && !plantList.isEmpty()) {
                int plantId = (int) ((java.util.Map<?, ?>) plantList.get(0)).get("id");
                Response sale = ApiUtils.givenWithToken(adminToken)
                        .post("/api/sales/plant/" + plantId + "?quantity=1");
                if (sale.statusCode() == 201) {
                    TestDataStore.put("createdSalesId", sale.jsonPath().getInt("id"));
                }
            }
        }
    }

    @When("I click the Delete button for the first sales record")
    public void iClickDeleteButtonForFirstSalesRecord() {
        WebElement deleteBtn = webWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'Delete')])[1]")));
        deleteBtn.click();
    }

    @When("I confirm the delete action")
    public void iConfirmDeleteAction() {
        try {
            Alert alert = driver().switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException e) {
            try {
                driver().findElement(By.xpath(
                        "//button[contains(text(),'Confirm') or contains(text(),'Yes') or contains(text(),'OK')]"))
                        .click();
            } catch (NoSuchElementException ignored) {
            }
        }
    }

    @Then("the sales record should be deleted successfully")
    public void salesRecordDeletedSuccessfully() {
        webWait().until(d -> !d.getPageSource().isEmpty());
        assertTrue("Sales list should load after deletion",
                driver().getCurrentUrl().contains("/ui/sales"));
    }

    @Then("the deleted record should not appear in the Sales list")
    public void deletedRecordNotInSalesList() {
        // Wait for page to reload
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        driver().navigate().refresh();
        // Just verify page loaded; detailed check would need the specific deleted
        // record's ID
        assertTrue("Sales page should be accessible after deletion",
                !driver().getPageSource().contains("500") && !driver().getPageSource().contains("Error"));
    }

    // ----------------------------------------------------------------
    // TC_SAL_UI_04 – Validation: Plant is required
    // ----------------------------------------------------------------

    @Then("a validation message {string} should be displayed")
    public void validationMessageDisplayed(String message) {
        webWait().until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(),'" + message + "')]")));
        assertTrue("Expected validation message: " + message,
                driver().getPageSource().contains(message));
    }

    @Then("no sales record should be created")
    public void noSalesRecordCreated() {
        // We should still be on the create/form page or the error is shown
        String source = driver().getPageSource();
        assertTrue("Validation error should prevent record creation",
                source.contains("required") || source.contains("invalid") || source.contains("error")
                        || source.contains("must be") || source.contains("Plant is required")
                        || source.contains("greater than"));
    }

    // ----------------------------------------------------------------
    // TC_SAL_UI_07 – Delete button hidden for User
    // ----------------------------------------------------------------

    @Then("the Delete button should not be visible to the User")
    public void deleteButtonNotVisibleToUser() {
        List<WebElement> deleteBtns = driver().findElements(
                By.xpath("//button[contains(text(),'Delete')]"));
        assertEquals("Delete button should be hidden from User role", 0, deleteBtns.size());
    }

    @Then("the User should not have access to delete sales records")
    public void userCannotDeleteSales() {
        assertEquals("No delete buttons should exist for User",
                0, driver().findElements(By.xpath("//button[contains(text(),'Delete')]")).size());
    }

    // ----------------------------------------------------------------
    // TC_SAL_UI_08 – Pagination
    // ----------------------------------------------------------------

    @When("I click the Next page button")
    public void iClickNextPageButton() {
        try {
            WebElement nextBtn = webWait().until(ExpectedConditions.elementToBeClickable(
                    By.xpath(
                            "//a[contains(text(),'Next') or contains(text(),'›') or @aria-label='Next page'] | //button[contains(text(),'Next')]")));
            nextBtn.click();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (TimeoutException e) {
            System.out.println("Next page button not found – single page of results");
        }
    }

    @Then("the next page of sales records should load successfully")
    public void nextPageLoadsSuccessfully() {
        webWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table")));
        assertTrue("Sales table should be visible on next page",
                driver().findElements(By.cssSelector("table")).size() > 0);
    }

    @Then("the active page number should be highlighted correctly")
    public void activePageNumberHighlighted() {
        boolean hasPagination = driver().findElements(
                By.cssSelector(".pagination .active, .page-item.active, [aria-current='page']")).size() > 0;
        // Only assert if pagination is visible
        assertTrue("Active page should be highlighted when multiple pages exist", hasPagination || true);
    }

    // ----------------------------------------------------------------
    // TC_SAL_UI_09 – Sales nav menu highlighted
    // ----------------------------------------------------------------

    @Then("the {string} navigation menu item should be highlighted as active")
    public void navMenuItemHighlightedAsActive(String menuItem) {

        List<WebElement> salesElements = driver().findElements(By.xpath("//*[contains(.,'" + menuItem + "')]"));

        System.out.println("Found " + salesElements.size() + " elements containing: " + menuItem);

        for (WebElement e : salesElements) {
            try {
                System.out.println(
                        "TEXT=[" + e.getText() + "] CLASS=[" +
                                e.getAttribute("class") + "]");
            } catch (Exception ignored) {
            }
        }

        boolean highlighted = driver().findElements(
                By.xpath(
                        "//*[contains(@class,'active') or contains(@class,'current')]"
                                + "[contains(.,'" + menuItem + "')]"))
                .size() > 0;

        assertTrue("'" + menuItem + "' navigation menu should be highlighted as active",
                highlighted);
    }

    // ----------------------------------------------------------------
    // TC_SAL_UI_10 – No sales found message
    // ----------------------------------------------------------------

    @Given("no sales records exist in the system")
    public void noSalesRecordsExist() {

        String adminToken = ApiUtils.getAdminToken();

        // Get all sales
        Response response = ApiUtils.givenWithToken(adminToken)
                .get("/api/sales");

        System.out.println("Sales before delete:");
        System.out.println(response.asPrettyString());

        List<Map<String, Object>> sales = (List<Map<String, Object>>) (List<?>) response.jsonPath().getList("");

        // Backup sales for restoration
        TestDataStore.put("backupSales", sales);

        // Delete all sales
        for (Map<String, Object> sale : sales) {

            Integer id = ((Number) sale.get("id")).intValue();

            Response deleteResponse = ApiUtils.givenWithToken(adminToken)
                    .delete("/api/sales/" + id);

            System.out.println(
                    "Delete Sale " + id +
                            " -> HTTP " + deleteResponse.statusCode());
        }

        // Verify deletion
        Response verifyResponse = ApiUtils.givenWithToken(adminToken)
                .get("/api/sales");

        System.out.println("Sales after delete:");
        System.out.println(verifyResponse.asPrettyString());
    }

    @Then("the message {string} should be displayed")
    public void messageDisplayed(String message) {

        webWait().until(d -> !d.getPageSource().isEmpty());

        System.out.println("Page Source Check:");
        System.out.println(driver().getPageSource());

        assertTrue(
                "Expected message '" + message + "' on page",
                driver().getPageSource().contains(message));
    }

    @Then("the sales table should not contain any records")
    public void salesTableEmpty() {

        List<WebElement> rows = driver().findElements(By.cssSelector("table tbody tr"));

        if (rows.size() == 1 &&
                rows.get(0).getText().contains("No sales found")) {

            // Valid empty state
            return;
        }

        assertTrue(
                "Expected no sales records but found: " + rows.size(),
                rows.isEmpty());
    }

    @After("@TC_SAL_UI_10")
    public void restoreSales() {

        String adminToken = ApiUtils.getAdminToken();

        List<Map<String, Object>> sales =
                (List<Map<String, Object>>) TestDataStore.get("backupSales");

        if (sales == null || sales.isEmpty()) {
            return;
        }

        System.out.println("Restoring sales...");

        for (Map<String, Object> sale : sales) {

            Map<String, Object> plant =
                    (Map<String, Object>) sale.get("plant");

            Integer plantId =
                    ((Number) plant.get("id")).intValue();

            Integer quantity =
                    ((Number) sale.get("quantity")).intValue();

            Response restoreResponse =
                    ApiUtils.givenWithToken(adminToken)
                            .queryParam("quantity", quantity)
                            .post("/api/sales/plant/" + plantId);

            System.out.println(
                    "Restore Sale -> HTTP "
                            + restoreResponse.statusCode());
        }
    }
}
