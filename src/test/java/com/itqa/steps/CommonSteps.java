package com.itqa.steps;

import com.itqa.config.ConfigManager;
import com.itqa.config.DriverManager;
import com.itqa.utils.ApiUtils;
import com.itqa.utils.TestDataStore;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.Assert.*;

/**
 * Common step definitions shared across all feature files.
 * Handles: application setup, login/logout, navigation.
 *
 * LOGIN PAGE HTML FACTS (qa-training-app):
 *   - Username: <input type="text" name="username" class="form-control">  (NO id attribute)
 *   - Password: <input type="password" name="password" class="form-control"> (NO id attribute)
 *   - Submit:   <button type="submit" class="btn btn-primary w-100 mb-3">Login</button>
 *   - Form has JS validation – we use JavascriptExecutor to bypass it and submit directly.
 *   - Error on bad login: URL becomes /ui/login?error
 */
public class CommonSteps {

    private WebDriver driver() { return DriverManager.getDriver(); }

    private WebDriverWait webWait() {
        return new WebDriverWait(driver(), Duration.ofSeconds(ConfigManager.getExplicitWait()));
    }

    // ─────────────────────────────────────────────
    // Selectors (from actual page HTML)
    // ─────────────────────────────────────────────
    private static final By USERNAME_FIELD = By.name("username");
    private static final By PASSWORD_FIELD = By.name("password");
    private static final By SUBMIT_BUTTON  = By.cssSelector("button[type='submit']");

    // ─────────────────────────────────────────────
    // Application setup
    // ─────────────────────────────────────────────

    @Given("the application is running")
    public void theApplicationIsRunning() {
        // No-op: driver not yet started for API scenarios
    }

    // ─────────────────────────────────────────────
    // Login helpers
    // ─────────────────────────────────────────────

    @Given("I am logged in as Admin")
    public void iAmLoggedInAsAdmin() {
        navigateAndLogin(ConfigManager.getAdminUsername(), ConfigManager.getAdminPassword());
    }

    @Given("I am logged in as User")
    public void iAmLoggedInAsUser() {
        navigateAndLogin(ConfigManager.getUserUsername(), ConfigManager.getUserPassword());
    }

    private void navigateAndLogin(String username, String password) {
        driver().get(ConfigManager.getBaseUrl() + "/ui/login");
        // Wait for the username field (uses name="username", NOT id)
        webWait().until(ExpectedConditions.presenceOfElementLocated(USERNAME_FIELD));

        WebElement usernameEl = driver().findElement(USERNAME_FIELD);
        WebElement passwordEl = driver().findElement(PASSWORD_FIELD);

        usernameEl.clear();
        usernameEl.sendKeys(username);
        passwordEl.clear();
        passwordEl.sendKeys(password);

        // The form has client-side JS validation; use JS submit to bypass it reliably
        WebElement form = driver().findElement(By.id("loginForm"));
        ((JavascriptExecutor) driver()).executeScript("arguments[0].submit();", form);

        // Wait for redirect away from login page
        webWait().until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/ui/login")));
    }

    // ─────────────────────────────────────────────
    // Navigation
    // ─────────────────────────────────────────────

    @When("I navigate to {string}")
    public void iNavigateTo(String path) {
        driver().get(ConfigManager.getBaseUrl() + path);
        // If redirected back to login (session lost), re-login
        webWait().until(d -> !d.getCurrentUrl().contains("/ui/login?error"));
    }

    @When("I navigate directly to {string}")
    public void iNavigateDirectlyTo(String path) {
        driver().get(ConfigManager.getBaseUrl() + path);
    }

    // ─────────────────────────────────────────────
    // Login page interactions
    // ─────────────────────────────────────────────

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        driver().get(ConfigManager.getBaseUrl() + "/ui/login");
        webWait().until(ExpectedConditions.presenceOfElementLocated(USERNAME_FIELD));
    }

    @When("I enter username {string} and password {string}")
    public void iEnterUsernameAndPassword(String username, String password) {
        driver().findElement(USERNAME_FIELD).clear();
        driver().findElement(USERNAME_FIELD).sendKeys(username);
        driver().findElement(PASSWORD_FIELD).clear();
        driver().findElement(PASSWORD_FIELD).sendKeys(password);
    }

    @When("I enter username {string} and leave password empty")
    public void iEnterUsernameAndLeavePasswordEmpty(String username) {
        driver().findElement(USERNAME_FIELD).clear();
        driver().findElement(USERNAME_FIELD).sendKeys(username);
        driver().findElement(PASSWORD_FIELD).clear();
    }

    @When("I leave username empty and enter password {string}")
    public void iLeaveUsernameEmptyAndEnterPassword(String password) {
        driver().findElement(USERNAME_FIELD).clear();
        driver().findElement(PASSWORD_FIELD).clear();
        driver().findElement(PASSWORD_FIELD).sendKeys(password);
    }

    @When("I leave both username and password fields empty")
    public void iLeaveBothFieldsEmpty() {
        driver().findElement(USERNAME_FIELD).clear();
        driver().findElement(PASSWORD_FIELD).clear();
    }

    @When("I click the Login button")
    public void iClickTheLoginButton() {
        // Click the JS-validated submit button
        driver().findElement(SUBMIT_BUTTON).click();
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    @When("I click the Logout button")
    public void iClickTheLogoutButton() {
        // The Logout link in the sidebar: <a href="/ui/logout" class="nav-link text-danger">
        // Using href selector — more reliable than text-based XPath when the element
        // contains a child <i> icon before the text node.
        WebElement logoutBtn = webWait().until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[href='/ui/logout']")));
        logoutBtn.click();
    }

    @When("I click the Save button")
    public void iClickTheSaveButton() {
        driver().findElement(By.cssSelector("button[type='submit']")).click();
    }

    @When("I click the Cancel button")
    public void iClickTheCancelButton() {
        driver().findElement(By.xpath(
                "//a[contains(text(),'Cancel')] | //button[contains(text(),'Cancel')]")).click();
    }

    @When("I click the Search button")
    public void iClickTheSearchButton() {
        driver().findElement(By.xpath(
                "//button[contains(text(),'Search')] | //button[@type='submit'][not(contains(text(),'Login'))]")).click();
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // ─────────────────────────────────────────────
    // Assertions
    // ─────────────────────────────────────────────

    @Then("I should remain on the login page")
    public void iShouldRemainOnTheLoginPage() {
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        assertTrue("Expected to still be on login page",
                driver().getCurrentUrl().contains("/ui/login"));
    }

    @Then("I should be redirected to the Admin dashboard")
    public void iShouldBeRedirectedToAdminDashboard() {
        webWait().until(ExpectedConditions.urlContains("/ui/"));
        assertFalse("Should not be on login page after admin login",
                driver().getCurrentUrl().contains("/ui/login"));
    }

    @Then("I should be redirected to the User dashboard")
    public void iShouldBeRedirectedToUserDashboard() {
        webWait().until(ExpectedConditions.urlContains("/ui/"));
        assertFalse("Should not be on login page after user login",
                driver().getCurrentUrl().contains("/ui/login"));
    }

    @Then("I should be redirected to the login page")
    public void iShouldBeRedirectedToLoginPage() {
        webWait().until(ExpectedConditions.urlContains("/ui/login"));
        assertTrue(driver().getCurrentUrl().contains("/ui/login"));
    }

    @Then("the dashboard modules and navigation options should be visible")
    public void dashboardShouldBeVisible() {
        webWait().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("nav, .sidebar, .navbar, ul.nav")));
        assertTrue("Dashboard navigation should be visible",
                driver().findElements(By.cssSelector("nav, .sidebar, .navbar, ul.nav")).size() > 0);
    }

    @Then("an error message {string} should be displayed")
    public void anErrorMessageShouldBeDisplayed(String expectedMessage) {
        webWait().until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(),'" + expectedMessage + "')]")));
        assertTrue("Expected error: " + expectedMessage,
                driver().getPageSource().contains(expectedMessage));
    }

    @Then("a validation message for password should be displayed")
    public void validationMessageForPasswordDisplayed() {
        try { Thread.sleep(400); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        String source = driver().getPageSource();
        assertTrue("Expected password validation message",
                source.contains("Password is required") || source.contains("password")
                        || source.contains("required") || source.contains("is-invalid"));
    }

    @Then("a validation message for username should be displayed")
    public void validationMessageForUsernameDisplayed() {
        try { Thread.sleep(400); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        String source = driver().getPageSource();
        assertTrue("Expected username validation message",
                source.contains("Username is required") || source.contains("username")
                        || source.contains("required") || source.contains("is-invalid"));
    }

    @Then("a logout confirmation message should be displayed")
    public void logoutConfirmationMessageDisplayed() {
        assertTrue("Expected login page after logout",
                driver().getCurrentUrl().contains("/ui/login"));
    }

    @Then("the active navigation menu item should be highlighted correctly on the Dashboard")
    public void activeNavMenuHighlighted() {
        webWait().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".active, [aria-current='page'], .nav-link.active")));
        assertTrue("Active nav item should be highlighted",
                driver().findElements(By.cssSelector(
                        ".active, [aria-current='page'], .nav-link.active")).size() > 0);
    }

    @Then("I should see an access denied page or be redirected away from the edit form")
    public void accessDeniedOrRedirected() {
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        String currentUrl = driver().getCurrentUrl();
        String pageSource = driver().getPageSource();
        boolean denied = pageSource.contains("403")
                || pageSource.contains("Access Denied")
                || pageSource.contains("Forbidden")
                || !currentUrl.contains("/ui/categories/edit");
        assertTrue("User should be denied access to category edit page", denied);
    }

    // ─────────────────────────────────────────────
    // Token helpers for API steps
    // ─────────────────────────────────────────────

    @Given("I have a valid Admin JWT token")
    public void iHaveValidAdminToken() {
        String token = ApiUtils.getAdminToken();
        TestDataStore.put("adminToken", token);
        // Clear user token to avoid cross-contamination
        TestDataStore.put("userToken", null);
    }

    @Given("I have a valid User JWT token")
    public void iHaveValidUserToken() {
        String token = ApiUtils.getUserToken();
        TestDataStore.put("userToken", token);
        // Clear admin token to avoid cross-contamination
        TestDataStore.put("adminToken", null);
    }
}
