# Playwright Automation – Shady Meadows B&B

Automation suite for [https://automationintesting.online/](https://automationintesting.online/) using **Playwright (Java) + TestNG**.

---

## Table of Contents

- [What's covered](#whats-covered)
- [Project structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Running the tests](#running-the-tests)
- [Screenshots on failure](#screenshots-on-failure)
- [HTML Test Report (ExtentReports)](#html-test-report-extentreports)
- [Key design decisions](#key-design-decisions)
- [Test cases & defects](#test-cases--defects)
- [Time spent](#time-spent)
- [AI assistance](#ai-assistance)

---

## What's covered

Explored the app manually first and picked the two flows that matter most from a real user's perspective.

### TC01 · TC_Book_Room_Success — end-to-end booking journey

| Step | What's verified |
|---|---|
| Generate a random future date | Random date used as check-in across the test |
| Navigate to home page | Header text matches `"Shady Meadows B&B"` |
| Enter check-in / check-out dates | Both date inputs accept the formatted values |
| Verify dates are populated | Actual input values match the entered dates |
| Search for available rooms | Room list returns `["Single", "Double", "Suite"]` |
| Select Double room, navigate calendar | Reservation page loads; calendar scrolls to the target month |
| Verify reservation page title & selection | Room title and date selection are reflected in the UI |
| Verify URL query parameters | URL contains correct check-in and check-out date strings |
| Verify total price | `£150 × 4 nights + £25 cleaning fee + £15 service fee = £640` matches displayed total |
| Fill guest details and submit | Form accepts First Name, Last Name, Email, Phone and submits |
| Verify confirmation | Confirmation message and date range match expected values |

### TC02 · TC_Book_Room_Form_Validation — progressive field validation (negative)

| Step | What's verified |
|---|---|
| Navigate, enter dates, search | Home page loads and room search completes |
| Select Single room | Reservation page loads |
| Submit completely empty form | All 7 required-field alerts are shown simultaneously |
| Enter first name, re-submit | First-name alert clears; all other alerts remain |
| Enter last name, re-submit | Last-name alerts clear; email and phone alerts remain |
| Enter email, re-submit | Email alert clears; only phone-related alerts remain |
| Enter oversized phone number, re-submit | Phone-size validation alert is triggered |

### Bugs found

| ID | Affected TC | Description |
|---|---|---|
| [DEF-01](DEFECTS.md#def-01--app-accepts-past-check-in-dates-no-frontend-validation) | TC01 | App accepts past check-in dates — no frontend guard prevents booking with a past date |
| [DEF-02](DEFECTS.md#def-02--validation-alert-messages-displayed-in-non-deterministic-order) | TC02 | Validation alert messages are displayed in a non-deterministic order on the UI |
| [DEF-03](DEFECTS.md#def-03--validation-alert-messages-are-too-generic-field-not-identified) | TC02 | Alert text is too generic — `"must not be empty"` does not identify which field it refers to |

---

## Project structure

```
src/
  main/java/com/example/automation/
    pages/
      HomePage.java            ← search, date entry, room listing
      ReservationPage.java     ← booking form, confirmation card, alert messages
    utils/
      BasePage.java            ← shared base (holds Page reference, getCurrentUrl)
      DateUtils.java           ← random future date generation
      TestConstants.java       ← all test data constants (prices, alerts, guest details)
      urlHelper.java           ← base URL constant

  test/java/com/example/automation/
    tests/
      BaseTest.java            ← Playwright lifecycle setup / teardown + screenshot on failure
      Home/
        VerifyHomePageTest.java ← TC01 and TC02

testNg.xml     ← suite config
pom.xml
README.md
TEST_CASES.md  ← full step-by-step test case specs
DEFECTS.md     ← all bugs with reproduction steps and expected behaviour
```

Page Object Model throughout. Every page method returns `this` so steps chain naturally.

---

## Prerequisites

| Requirement | Version tested |
|---|---|
| Java | 8 (Amazon Corretto 1.8.0_472) |
| Maven | 3.6+ |
| Google Chrome | Installed at default path: `C:\Program Files\Google\Chrome\Application\chrome.exe` |

Playwright downloads its own browser driver on first run — nothing else to install.

---

## Running the tests

**From IntelliJ:**

Right-click `testNg.xml` → **Run**. Or right-click any test method directly.

**Full suite from terminal:**

```bash
mvn clean test
```

**Single test by method name:**

```bash
mvn test -Dtest=VerifyHomePageTest#verifyUserIsAbleToBookTheRoomSuccessfully
mvn test -Dtest=VerifyHomePageTest#VerifyFormValidationForBooking
```

The browser runs in **headed mode** — Chrome opens visibly, maximized, using the real Chrome binary (`setChannel("chrome")`).

---

## Screenshots on failure

On any test failure a full-page screenshot is automatically captured and saved to:

```
target/screenshots/<yyyyMMdd_HHmmss>_<testName>.png
```

Example:

```
target/screenshots/20260226_143022_verifyUserIsAbleToBookTheRoomSuccessfully.png
```

The folder is created automatically if it doesn't exist. Screenshots are timestamped so multiple failures in the same run never overwrite each other.

---

## HTML Test Report (ExtentReports)

After every run an interactive HTML report is generated at:

```
target/extent-reports/ExtentReport_<yyyyMMdd_HHmmss>.html
```

Open it in any browser — no server needed. The report includes:

- Pass / Fail / Skip status per test with full stack trace on failure
- Failure screenshots embedded directly in the report
- System info (browser, framework, app URL)
- Test groups / categories
- Execution timestamp and duration

---

## Key design decisions

**Fresh Playwright stack per test (`@BeforeMethod`)**
`Playwright → Browser → BrowserContext → Page` is created fresh for every test and fully disposed in `@AfterMethod`. Sharing a browser or context across tests caused cross-thread object errors (`TargetClosedError`, `Cannot find object to call __adopt__`).

**`playwright.close()` only — never `browser.close()` separately**
Calling `browser.close()` before `playwright.close()` double-closes the browser and crashes the IPC pipe for the next test. `playwright.close()` disposes everything it owns in the correct order.

**`DOMCONTENTLOADED` instead of `LOAD` for navigation**
The app fires background XHR calls after `load`. Waiting for full `load` caused `net::ERR_ABORTED` race conditions when the context closed mid-flight. `domcontentloaded` + an explicit `waitFor()` on the header element is more stable.

**Screenshot taken before `playwright.close()`**
`ITestResult.getStatus()` is checked in `tearDown(ITestResult result)`. The screenshot runs before `playwright.close()` so the page is still alive when the capture executes.

**Fluent page methods**
All page methods return `this`, making test steps read as a natural sequence:

```java
reservationPage.clickReserveButton()
               .enterFirstName("John")
               .enterLastName("Doe")
               .enterEmail("JohnDoe@cba.com")
               .enterPhoneNumber("56345678910")
               .clickReserveNow();
```

**Centralised test data (`TestConstants.java`)**
All prices, alert message strings, guest details, room names, and date formats live in one file. If the app changes any copy or pricing, only `TestConstants.java` needs updating.

---

## Test cases & defects

- Full step-by-step test case specs → [`TEST_CASES.md`](TEST_CASES.md)
- All bugs with reproduction steps and expected behaviour → [`DEFECTS.md`](DEFECTS.md)

---

## Time spent

| Activity | Time |
|---|---|
| Exploratory testing of the app | ~30 min |
| Framework setup, pom, folder structure | ~20 min |
| Writing page objects (`HomePage`, `ReservationPage`) | ~45 min |
| Writing test cases | ~60 min |
| Debugging browser lifecycle / teardown issues | ~40 min |
| Writing README, TEST_CASES.md, DEFECTS.md | ~25 min |
| **Total** | **~3h 40min** |

---

## AI assistance

GitHub Copilot (via JetBrains plugin) was used during development.

**Where it helped:**
- Diagnosed and suggested fixes for Playwright-specific teardown errors (`TargetClosedError`, `ERR_ABORTED`, `Cannot find object to call __adopt__`). Root causes were understood and verified manually before applying fixes.
- Suggested `DOMCONTENTLOADED` as the navigation wait strategy after the `ERR_ABORTED` pattern was explained.
- Provided guidance on integrating ExtentReports as a TestNG listener — including wiring `ExtentSparkReporter`, attaching screenshots to failed test nodes, and registering the listener in both `testNg.xml` and `maven-surefire-plugin`.
- Provided guidance on the screenshot-on-failure mechanism — identifying that `page.screenshot()` throws `PlaywrightException` (not `IOException`), and that the capture must happen before `playwright.close()` is called in `tearDown`.
- Wrote first drafts of README, TEST_CASES.md, and DEFECTS.md.
- Assisted with adding grouped comments to test methods.
- Created date-related utility methods in `DateUtils.java` — specifically the logic to generate a random future date and the calendar navigation method (`navigateCalendarToMonth`) that computes how many times to click **Next** to reach the target month from the current displayed month.

**What was done without AI:**
- All exploratory testing and flow identification
- Locator selection and page object design
- All assertion logic and test data decisions
- Bug identification and documentation
- Final review and editing of all code

