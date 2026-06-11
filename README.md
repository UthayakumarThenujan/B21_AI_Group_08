# ITQA Group 08 – Test Automation Framework

**Selenium + RestAssured + Cucumber BDD + Allure Report**

## Group Members & Test Modules

| Member | Module | Test Cases |
|--------|--------|-----------|
| Thenujan U. | Category Management | TC_CAT_UI_01–11, TC_CAT_API_01–10 |
| Asarak A. | Authentication | TC_AUTH_UI_01–09, TC_DASH_UI_10, TC_AUTH_API_01–10, TC_SAL_API_07–08 |
| Pirapanchan R. | Plant Management | TC_PLA_UI_01–11, TC_PLA_API_01–10 |
| Sharhaan M.F.M. | Sales Management | TC_SAL_UI_01–10, TC_SAL_API_01–06, TC_SAL_API_09–10 |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Chrome browser (latest)
- MySQL running with `qa_training` database
- QA Training App running on `http://localhost:8080`

---

## Configuration

Edit `src/test/resources/config.properties`:

```properties
base.url=http://localhost:8080
admin.username=admin
admin.password=admin123
user.username=testuser
user.password=test123
browser=chrome
headless=false
```

---

## Running Tests

### Start the Application First
```bash
java -jar "QA Training App/qa-training-app.jar" --spring.config.location="QA Training App/application.properties"
```

### Run All Tests
```bash
cd itqa-automation
mvn clean test
```

### Run by Member
```bash
# Thenujan – Category Management
mvn clean test -Dcucumber.filter.tags="@Thenujan"

# Asarak – Authentication
mvn clean test -Dcucumber.filter.tags="@Asarak"

# Pirapanchan – Plant Management
mvn clean test -Dcucumber.filter.tags="@Pirapanchan"

# Sharhaan – Sales Management
mvn clean test -Dcucumber.filter.tags="@Sharhaan"
```

### Run API Tests Only
```bash
mvn clean test -Dcucumber.filter.tags="@API"
```

### Run UI Tests Only
```bash
mvn clean test -Dcucumber.filter.tags="@UI"
```

### Run Headless (CI/CD)
```bash
mvn clean test -Dheadless=true
```

---

## Allure Report

```bash
# Generate report after tests
mvn allure:report

# Open interactive report in browser
mvn allure:serve
```

The Allure report will be generated at: `target/allure-report/index.html`

---

## Project Structure

```
itqa-automation/
├── pom.xml
├── src/
│   ├── main/java/com/itqa/
│   │   ├── config/
│   │   │   ├── ConfigManager.java       # App configuration
│   │   │   └── DriverManager.java       # WebDriver lifecycle
│   │   └── utils/
│   │       ├── ApiUtils.java            # REST Assured helpers
│   │       └── TestDataStore.java       # Shared test data
│   └── test/
│       ├── java/com/itqa/
│       │   ├── hooks/
│       │   │   └── Hooks.java           # Browser init/teardown + screenshots
│       │   ├── runners/
│       │   │   ├── AllTestsRunner.java  # Master runner
│       │   │   ├── CategoryTestRunner.java
│       │   │   ├── AuthTestRunner.java
│       │   │   ├── PlantTestRunner.java
│       │   │   └── SalesTestRunner.java
│       │   └── steps/
│       │       ├── CommonSteps.java     # Shared login/nav steps
│       │       ├── CategoryUISteps.java
│       │       ├── CategoryAPISteps.java # + shared HTTP verb steps
│       │       ├── AuthUISteps.java
│       │       ├── AuthAPISteps.java
│       │       ├── PlantUISteps.java
│       │       ├── PlantAPISteps.java
│       │       ├── SalesUISteps.java
│       │       └── SalesAPISteps.java
│       └── resources/
│           ├── features/
│           │   ├── category_ui.feature
│           │   ├── category_api.feature
│           │   ├── auth_ui.feature
│           │   ├── auth_api.feature
│           │   ├── plant_ui.feature
│           │   ├── plant_api.feature
│           │   ├── sales_ui.feature
│           │   └── sales_api.feature
│           ├── config.properties
│           └── allure.properties
```

---

## Cucumber Tags Reference

| Tag | Description |
|-----|-------------|
| `@UI` | All UI tests |
| `@API` | All API tests |
| `@Thenujan` | Thenujan's test cases |
| `@Asarak` | Asarak's test cases |
| `@Pirapanchan` | Pirapanchan's test cases |
| `@Sharhaan` | Sharhaan's test cases |
| `@CategoryUI` | Category UI tests |
| `@CategoryAPI` | Category API tests |
| `@AuthUI` | Auth UI tests |
| `@AuthAPI` | Auth API tests |
| `@PlantUI` | Plant UI tests |
| `@PlantAPI` | Plant API tests |
| `@SalesUI` | Sales UI tests |
| `@SalesAPI` | Sales API tests |
| `@TC_CAT_UI_01` | Specific test case |
