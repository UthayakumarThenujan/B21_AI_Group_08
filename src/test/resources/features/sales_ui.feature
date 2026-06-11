# ====================================================================
# Feature: Sales Management – UI Tests
# Tester: Sharhaan M.F.M.
# Module: Sales Management
# ====================================================================
@UI @SalesUI @Sharhaan
Feature: Sales Management UI Tests

  Background:
    Given the application is running

  # ------------------------------------------------------------------
  # TC_SAL_UI_01
  # ------------------------------------------------------------------
  @TC_SAL_UI_01
  Scenario: TC_SAL_UI_01 – Admin can create a new sales record successfully
    Given I am logged in as Admin
    And at least one plant exists in the system
    When I navigate to "/ui/sales"
    And I click the "Sell Plant" button
    And I select the first available plant
    And I enter quantity "2"
    And I click the Sell button
    Then the sales record should be created successfully
    And the new sale should appear in the Sales list

  # ------------------------------------------------------------------
  # TC_SAL_UI_02
  # ------------------------------------------------------------------
  @TC_SAL_UI_02
  Scenario: TC_SAL_UI_02 – Admin can sort sales records by Total Price column
    Given I am logged in as Admin
    And multiple sales records exist in the system
    When I navigate to "/ui/sales"
    And I click the "Total Price" column header
    Then the sales records should be sorted by total price
    And the sorting order should change correctly when the column is clicked again

  # ------------------------------------------------------------------
  # TC_SAL_UI_03
  # ------------------------------------------------------------------
  @TC_SAL_UI_03
  Scenario: TC_SAL_UI_03 – Admin can delete an existing sales record
    Given I am logged in as Admin
    And at least one sales record exists in the system
    When I navigate to "/ui/sales"
    And I click the Delete button for the first sales record
    And I confirm the delete action
    Then the sales record should be deleted successfully
    And the deleted record should not appear in the Sales list

  # ------------------------------------------------------------------
  # TC_SAL_UI_04
  # ------------------------------------------------------------------
  @TC_SAL_UI_04
  Scenario: TC_SAL_UI_04 – Validation error when Admin submits sale without selecting a plant
    Given I am logged in as Admin
    When I navigate to "/ui/sales"
    And I click the "Sell Plant" button
    And I leave the plant selection empty
    And I enter quantity "2"
    And I click the Sell button
    Then a validation message "Plant is required" should be displayed
    And no sales record should be created

  # ------------------------------------------------------------------
  # TC_SAL_UI_05
  # ------------------------------------------------------------------
  @TC_SAL_UI_05
  Scenario: TC_SAL_UI_05 – Validation error when Admin enters quantity less than 1
    Given I am logged in as Admin
    And at least one plant exists in the system
    When I navigate to "/ui/sales"
    And I click the "Sell Plant" button
    And I select the first available plant
    And I enter quantity "0"
    And I click the Sell button
    Then a validation message "Value must be greater than or equal to 1" should be displayed
    And no sales record should be created

  # ------------------------------------------------------------------
  # TC_SAL_UI_06 – User can sort by ALL sortable columns
  # SRS: Sorting supported on Plant, Quantity, Total Price, Sold At
  # Default sorting: Sold At (descending)
  # Single Scenario Outline covers all 4 columns in one common block
  # ------------------------------------------------------------------
  @TC_SAL_UI_06
  Scenario Outline: TC_SAL_UI_06 – User can sort sales records by <column> column
    Given I am logged in as User
    And multiple sales records exist in the system
    When I navigate to "/ui/sales"
    And I click the "<column>" column header to sort
    Then the sales list should be sorted by "<column>" in ascending or descending order
    When I click the "<column>" column header to sort again
    Then the sort order for "<column>" should be reversed

    Examples:
      | column      |
      | Plant       |
      | Quantity    |
      | Total Price |
      | Sold At     |

  # ------------------------------------------------------------------
  # TC_SAL_UI_07
  # ------------------------------------------------------------------
  @TC_SAL_UI_07
  Scenario: TC_SAL_UI_07 – Delete functionality is hidden for User role on Sales page
    Given I am logged in as User
    And at least one sales record exists in the system
    When I navigate to "/ui/sales"
    Then the Delete button should not be visible to the User
    And the User should not have access to delete sales records

  # ------------------------------------------------------------------
  # TC_SAL_UI_08
  # ------------------------------------------------------------------
  @TC_SAL_UI_08
  Scenario: TC_SAL_UI_08 – User can navigate through sales records using pagination
    Given I am logged in as User
    And multiple sales records exist in the system
    When I navigate to "/ui/sales"
    And I click the Next page button
    Then the next page of sales records should load successfully
    And the active page number should be highlighted correctly

  # ------------------------------------------------------------------
  # TC_SAL_UI_09
  # ------------------------------------------------------------------
  @TC_SAL_UI_09
  Scenario: TC_SAL_UI_09 – Sales navigation menu is highlighted on the Sales page
    Given I am logged in as User
    When I navigate to "/ui/sales"
    Then the "Sales" navigation menu item should be highlighted as active

  # ------------------------------------------------------------------
  # TC_SAL_UI_10
  # ------------------------------------------------------------------
  @TC_SAL_UI_10
  Scenario: TC_SAL_UI_10 – No sales found message displayed when no sales records exist
    Given I am logged in as Admin
    And no sales records exist in the system
    When I navigate to "/ui/sales"
    Then the message "No sales found" should be displayed
    And the sales table should not contain any records
