# ====================================================================
# Feature: Category Management – UI Tests
# Tester: Thenujan U.
# Module: Category Management
# NOTE: Button is called "Add A Category" (not "Add Category") in the app.
# ====================================================================
@UI @CategoryUI @Thenujan
Feature: Category Management UI Tests

  Background:
    Given the application is running

  # ------------------------------------------------------------------
  # TC_CAT_UI_01 – Admin can add a category with a valid name
  # Fix: Button text is "Add A Category" in the app UI
  # ------------------------------------------------------------------
  @TC_CAT_UI_01
  Scenario: TC_CAT_UI_01 – Admin can add a category with a valid name
    Given I am logged in as Admin
    When I navigate to "/ui/categories"
    And I click the "Add A Category" button
    And I enter category name "PlantTest"
    And I click the Save button
    Then the category "PlantTest" should be visible in the categories list

  # ------------------------------------------------------------------
  # TC_CAT_UI_02 – Category list is sortable by ID column
  # ------------------------------------------------------------------
  @TC_CAT_UI_02
  Scenario: TC_CAT_UI_02 – Category list is sortable by ID column
    Given I am logged in as Admin
    When I navigate to "/ui/categories"
    And I click the "ID" column header
    Then the categories should be sorted in ascending order by ID
    When I click the "ID" column header again
    Then the categories should be sorted in descending order by ID

  # ------------------------------------------------------------------
  # TC_CAT_UI_03 – Category list is sortable by Name column
  # Manual check: clicking "Name" header sorts correctly – test passes
  # ------------------------------------------------------------------
  @TC_CAT_UI_03
  Scenario: TC_CAT_UI_03 – Category list is sortable by Name column
    Given I am logged in as Admin
    When I navigate to "/ui/categories"
    And I click the "Name" column header
    Then the categories should be sorted alphabetically A to Z

  # ------------------------------------------------------------------
  # TC_CAT_UI_04 – Category list is sortable by Parent Category column
  # ------------------------------------------------------------------
  @TC_CAT_UI_04
  Scenario: TC_CAT_UI_04 – Category list is sortable by Parent Category column
    Given I am logged in as Admin
    When I navigate to "/ui/categories"
    And I click the "Parent Category" column header
    Then the categories should be grouped and sorted by parent category

  # ------------------------------------------------------------------
  # TC_CAT_UI_05 – Admin can add a main category without selecting a parent
  # SRS: parent is OPTIONAL – no validation error should occur
  # Fix: removed parent field assertion; test should PASS when parent is empty
  # ------------------------------------------------------------------
  @TC_CAT_UI_05
  Scenario: TC_CAT_UI_05 – Admin can add a main category without selecting a parent
    Given I am logged in as Admin
    When I navigate to "/ui/categories"
    And I click the "Add A Category" button
    And I enter category name "MainCat"
    And I leave the parent category field empty
    And I click the Save button
    Then the category "MainCat" should be visible in the categories list

  # ------------------------------------------------------------------
  # TC_CAT_UI_06 – Categories list page loads with pagination
  # ------------------------------------------------------------------
  @TC_CAT_UI_06
  Scenario: TC_CAT_UI_06 – Categories list page loads with pagination
    Given I am logged in as User
    When I navigate to "/ui/categories"
    Then the categories table should be visible
    And pagination controls should be visible if multiple pages exist

  # ------------------------------------------------------------------
  # TC_CAT_UI_07 – Normal user cannot see Edit buttons
  # BUG-CAT-UI-001: Edit buttons ARE visible (confirmed app bug)
  # Test correctly FAILS to confirm the bug exists
  # ------------------------------------------------------------------
  @TC_CAT_UI_07 @BUG-CAT-UI-001
  Scenario: TC_CAT_UI_07 – Normal user cannot see Edit buttons on category list
    Given I am logged in as User
    When I navigate to "/ui/categories"
    Then no Edit buttons should be visible in the category list

  # ------------------------------------------------------------------
  # TC_CAT_UI_08 – Filtering by parent category shows only child categories
  # Manual check: filter works correctly
  # Fix: use flexible filter selector + soft assertion for no matching records
  # ------------------------------------------------------------------
  @TC_CAT_UI_08
  Scenario: TC_CAT_UI_08 – Filtering by parent category shows only child categories
    Given I am logged in as User
    When I navigate to "/ui/categories"
    And I filter categories by parent category
    Then only matching category records should be displayed

  # ------------------------------------------------------------------
  # TC_CAT_UI_09 – Normal user cannot see Delete buttons
  # BUG-CAT-UI-002: Delete buttons ARE visible (confirmed app bug)
  # Test correctly FAILS to confirm the bug exists
  # ------------------------------------------------------------------
  @TC_CAT_UI_09 @BUG-CAT-UI-002
  Scenario: TC_CAT_UI_09 – Normal user cannot see Delete buttons on category list
    Given I am logged in as User
    When I navigate to "/ui/categories"
    Then no Delete buttons should be visible in the category list

  # ------------------------------------------------------------------
  # TC_CAT_UI_10 – Cancel button on Add Category page navigates back
  # ------------------------------------------------------------------
  @TC_CAT_UI_10
  Scenario: TC_CAT_UI_10 – Cancel button on Add Category page navigates back to list
    Given I am logged in as Admin
    When I navigate to "/ui/categories/add"
    And I click the Cancel button
    Then I should be redirected to the categories list page
    And no new category should be saved

  # ------------------------------------------------------------------
  # TC_CAT_UI_11 – Normal user cannot access category edit page via direct URL
  # BUG-CAT-UI-003: No 403 returned (confirmed security gap)
  # Test correctly FAILS to confirm the bug exists
  # ------------------------------------------------------------------
  @TC_CAT_UI_11 @BUG-CAT-UI-003
  Scenario: TC_CAT_UI_11 – Normal user cannot access category edit page via direct URL
    Given I am logged in as User
    When I navigate directly to "/ui/categories/edit/1"
    Then I should see an access denied page or be redirected away from the edit form
