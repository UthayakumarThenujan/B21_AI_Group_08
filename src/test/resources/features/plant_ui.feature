# ====================================================================
# Feature: Plant Management – UI Tests
# Tester: Pirapanchan R.
# Module: Plant Management
# ====================================================================
@UI @PlantUI @Pirapanchan
Feature: Plant Management UI Tests

  Background:
    Given the application is running

  # ------------------------------------------------------------------
  # TC_PLA_UI_01
  # ------------------------------------------------------------------
  @TC_PLA_UI_01
  Scenario: TC_PLA_UI_01 – Admin can access and view the plant list page
    Given I am logged in as Admin
    When I navigate to "/ui/plants"
    Then the plant list page should load correctly
    And plant details should be displayed in table format

  # ------------------------------------------------------------------
  # TC_PLA_UI_02
  # ------------------------------------------------------------------
  @TC_PLA_UI_02
  Scenario: TC_PLA_UI_02 – Active navigation menu highlighted for Categories and Plants
    Given I am logged in as Admin
    When I navigate to "/ui/categories"
    Then the "Categories" navigation menu item should be highlighted
    When I navigate to "/ui/plants"
    Then the "Plants" navigation menu item should be highlighted

  # ------------------------------------------------------------------
  # TC_PLA_UI_03
  # ------------------------------------------------------------------
  @TC_PLA_UI_03
  Scenario: TC_PLA_UI_03 – Admin can update existing plant details
    Given I am logged in as Admin
    And at least one plant exists in the system
    When I navigate to "/ui/plants"
    And I click the Edit button for the first plant
    And I update the plant name to "UpdatedPlant"
    And I click the Save button
    Then the plant list should show "UpdatedPlant"

  # ------------------------------------------------------------------
  # TC_PLA_UI_04
  # ------------------------------------------------------------------
  @TC_PLA_UI_04
  Scenario: TC_PLA_UI_04 – Validation error when Admin enters invalid plant price
    Given I am logged in as Admin
    When I navigate to "/ui/plants"
    And I click the "Add Plant" button
    And I enter valid plant details with name "TestPlant"
    And I enter a negative price value "-1"
    And I click the Save button
    Then a price validation error message should be displayed
    And the plant should not be saved

  # ------------------------------------------------------------------
  # TC_PLA_UI_05
  # ------------------------------------------------------------------
  @TC_PLA_UI_05
  Scenario: TC_PLA_UI_05 – Deletion of plant with linked records shows friendly error
    Given I am logged in as Admin
    And a plant with linked sales or inventory records exists
    When I navigate to "/ui/plants"
    And I click the Delete button for the linked plant
    And I confirm the deletion
    Then a friendly error message about existing records should be displayed
    And the plant should not be deleted

  # ------------------------------------------------------------------
  # TC_PLA_UI_06
  # ------------------------------------------------------------------
  @TC_PLA_UI_06
  Scenario: TC_PLA_UI_06 – User can view plant list page
    Given I am logged in as User
    When I navigate to "/ui/plants"
    Then the plant list page should load successfully
    And Admin action buttons should not be visible to the User

  # ------------------------------------------------------------------
  # TC_PLA_UI_07
  # ------------------------------------------------------------------
  @TC_PLA_UI_07
  Scenario: TC_PLA_UI_07 – Add Plant button is hidden for User role
    Given I am logged in as User
    When I navigate to "/ui/plants"
    Then the "Add Plant" button should not be visible

  # ------------------------------------------------------------------
  # TC_PLA_UI_08
  # ------------------------------------------------------------------
  @TC_PLA_UI_08
  Scenario: TC_PLA_UI_08 – User can search plants by name
    Given I am logged in as User
    And at least one plant exists in the system
    When I navigate to "/ui/plants"
    And I enter a plant name in the search box
    And I click the Search button
    Then matching plant records should be displayed
    And non-matching plants should not be shown

  # ------------------------------------------------------------------
  # TC_PLA_UI_09
  # ------------------------------------------------------------------
  @TC_PLA_UI_09
  Scenario: TC_PLA_UI_09 – User can filter plants by category
    Given I am logged in as User
    And plants exist under different categories
    When I navigate to "/ui/plants"
    And I select a category from the category filter
    And I click the Search button
    Then only plants belonging to the selected category should be displayed

  # ------------------------------------------------------------------
  # TC_PLA_UI_10
  # ------------------------------------------------------------------
  @TC_PLA_UI_10
  Scenario: TC_PLA_UI_10 – Deleted plants are hidden from User plant list
    Given I am logged in as User
    When I navigate to "/ui/plants"
    Then deleted plants should not be visible in the plant list

  # ------------------------------------------------------------------
  # TC_PLA_UI_11
  # ------------------------------------------------------------------
  @TC_PLA_UI_11
  Scenario: TC_PLA_UI_11 – Low badge displayed for plants with quantity below 5
    Given I am logged in as User
    And a plant with quantity below 5 exists in the system
    When I navigate to "/ui/plants"
    Then a "Low" stock badge should be displayed for plants with quantity below 5
