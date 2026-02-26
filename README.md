# Playwright Automation – Shady Meadows B&B

Automation suite for [https://automationintesting.online/](https://automationintesting.online/) using Playwright (Java) + TestNG.

---

## What's covered

I explored the app manually first and picked the flows that matter most from a real user's perspective.

### TC_Book_Room_Success — end-to-end booking journey

-   Lands on the home page, verifies the header
-   Enters check-in / check-out dates and confirms the inputs reflect what was typed
-   Checks the list of available rooms matches expectations
-   Books a Double room and lands on the reservation page
-   Confirms the room title, date selection UI, and URL query parameters
-   Validates the total price (`£150/night × 4 nights + fees`) against the UI
-   Fills in guest details and submits
-   Asserts the confirmation message and the dates on the confirmation card
-   Clicks "Return Home" and confirms we're back on the home page

### TC_Book_Room_Form_Validation — progressive field validation (negative)

-   Submits the reservation form completely empty, checks all alert messages are present
-   Fills fields one at a time (first name → last name → email → phone), re-submits after each step
-   Confirms the right alerts disappear as each field is filled
-   Checks phone number length boundary (too long)

### Bugs documented in test comments

| Bug | Test | Description |
|---|---|---|
| Past date booking | `TC_Book_Room_Success` | App accepts past check-in dates — no frontend guard |
| Random alert order | `TC_Book_Room_Form_Validation` | Alert messages appear in non-deterministic order |
| Generic alert text | `TC_Book_Room_Form_Validation` | `"must not be empty"` doesn't say which field it refers to |

---

## Project structure

```
src/
  main/java/com/example/automation/
    pages/
      HomePage.java          ← search, date entry, room listing
      ReservationPage.java   ← booking form, confirmation card, alert messages
    utils/
      BasePage.java          ← shared base (holds Page reference, getCurrentUrl)
      urlHelper.java         ← base URL constant

  test/java/com/example/automation/
    tests/Home/
      VerifyHomePageTest.java  ← both test cases + setup/teardown + screenshot on failure

testNg.xml    ← suite config
pom.xml
```

Page Object Model throughout. Every page method returns `this` so steps chain naturally.

---

## Prerequisites

-   **Java 8** — tested with Amazon Corretto 1.8.0_472
-   **Maven 3.6+**
-   **Google Chrome** installed at the default path: `C:\Program Files\Google\Chrome\Application\chrome.exe`

Playwright downloads its own browser driver on first run — nothing else to install.

---

## Running the tests

**From IntelliJ:**

Right-click `testNg.xml` → Run. Or right-click any test method directly.

**Full suite from terminal:**

```bash
mvn clean test
```

**Single test method:**

```bash
mvn test -Dtest=VerifyHomePageTest#verifyUserIsAbleToBookTheRoomSuccessfully
mvn test -Dtest=VerifyHomePageTest#VerifyFormValidationForBooking
```

The browser runs in **headed mode** — Chrome opens visibly, maximized, using the real Chrome binary (`setChannel("chrome")`).

---

## Test execution report / screenshots

On test failure, a full-page screenshot is automatically captured and saved to:

```
target/screenshots/<yyyyMMdd_HHmmss>_<testName>.png
```

Example:

```
target/screenshots/20260226_143022_verifyUserIsAbleToBookTheRoomSuccessfully.png
```

The `target/screenshots/` folder is created automatically if it doesn't exist. Screenshots are timestamped so multiple failures in the same run don't overwrite each other.

---

## Key decisions

**Full Playwright stack created per test (`@BeforeMethod`)** `Playwright → Browser → BrowserContext → Page` is created fresh for every test and disposed in `@AfterMethod`. Sharing a browser or context across tests caused cross-thread object errors (`TargetClosedError`, `Cannot find object to call __adopt__`).

**`playwright.close()` only in teardown — never `browser.close()` separately** Calling `browser.close()` before `playwright.close()` double-closes the browser and crashes the IPC pipe for the next test. `playwright.close()` disposes everything it owns in the right order.

**`DOMCONTENTLOADED` instead of `LOAD` on navigation** The app fires background XHR calls after `load`. Waiting for full `load` caused `net::ERR_ABORTED` race conditions when the context closed mid-flight. `domcontentloaded` + an explicit `waitFor()` on the header is more stable.

**Screenshot on failure wired into `@AfterMethod`** `ITestResult.getStatus()` is checked in `tearDown(ITestResult result)`. If the test failed, a full-page screenshot is taken before `playwright.close()` is called, so the page is still alive when the screenshot runs.

**Fluent page methods** Page methods return `this` so test steps read as a sequence:

```java
reservationPage.clickReserveButton()
               .enterFirstName("John")
               .enterLastName("Doe")
               .clickReserveNow();
```

---

## Test cases

Detailed test case steps and expected results are documented in [`TEST_CASES.md`](TEST_CASES.md).

---

## Time spent

| Activity | Time |
|---|---|
| Exploratory testing of the app | ~30 min |
| Framework setup, pom, folder structure | ~20 min |
| Writing page objects (HomePage, ReservationPage) | ~45 min |
| Writing test cases | ~60 min |
| Debugging browser lifecycle / teardown issues | ~40 min |
| README | ~15 min |
| **Total** | **~3h 30min** |

---

## AI assistance

GitHub Copilot (via JetBrains plugin) was used during development.

**Where it helped:**

-   Diagnosed and suggested fixes for Playwright-specific teardown errors (`TargetClosedError`, `ERR_ABORTED`, `Cannot find object to call __adopt__`). Root causes were understood and verified manually before applying fixes.
-   Suggested `DOMCONTENTLOADED` as the navigation wait strategy after the `ERR_ABORTED` pattern was explained.
-   Wrote the first draft of this README.

**What was done without AI:**

-   All exploratory testing and flow identification
-   Locator selection and page object design
-   All assertion logic and test data decisions
-   Bug identification and documentation
-   Final review and editing of all code
