# Playwright Automation – Shady Meadows B&B

Automation suite for [https://automationintesting.online/](https://automationintesting.online/) using Playwright (Java) + TestNG.

---

## What's covered

I explored the app manually first and picked the flows that matter most from a user's perspective:

**TC_Book_Room_Success** — the full booking journey
- Opens the home page, verifies the header
- Enters check-in / check-out dates and confirms what was entered is reflected back
- Checks available rooms match expectations
- Books a Double room, lands on the reservation page
- Confirms the room title, date selection UI, and URL query parameters
- Validates the total price against `£150/night × 4 nights`
- Fills in guest details and submits
- Asserts the confirmation message and the dates shown on the confirmation card
- Clicks "Return Home" and confirms we're back on the home page

**TC_Book_Room_Form_Validation** — form validation, field by field
- Submits the reservation form completely empty and checks all alert messages are present
- Then fills fields one at a time (first name → last name → email → phone) and re-submits after each step to confirm the right alerts disappear progressively
- Also checks the phone number length boundary (too long)

### Bugs spotted (documented in test comments)
- `verifyUserIsAbleToBookTheRoomSuccessfully` — the app accepts past dates for booking (no frontend guard)
- `VerifyFormValidationForBooking` — alert messages appear in a random order on the UI, and the messages themselves are too generic (e.g. `"must not be empty"` instead of `"Email must not be empty"`)

---

## Project structure

```
src/
  main/java/com/example/automation/
    pages/
      HomePage.java          ← locators + actions for the home/search page
      ReservationPage.java   ← locators + actions for the booking/confirmation page
    utils/
      BasePage.java          ← shared page base (holds Page reference, common helpers)
      urlHelper.java         ← base URL constant
  test/java/com/example/automation/
    tests/Home/
      VerifyHomePageTest.java ← both test cases live here
testNg.xml                    ← suite config, points to the test class
pom.xml
```

Page Object Model is used throughout. Every page method returns `this` so steps can be chained where it reads naturally.

---

## Prerequisites

- **Java 8** (project is compiled against 1.8 — tested with Amazon Corretto 1.8.0_472)
- **Maven 3.6+**
- **Google Chrome** installed at the default path (`C:\Program Files\Google\Chrome\Application\chrome.exe` on Windows)

Playwright downloads its own browser driver on the first run — you don't need to install anything extra for that.

---

## Running the tests

**From IntelliJ:**

Right-click `testNg.xml` → Run, or right-click any test method and hit Run.

**From the terminal (if Maven is on PATH):**

```bash
mvn clean test
```

The Surefire plugin picks up `testNg.xml` automatically, so no extra flags are needed.

**Run a single test:**

```bash
mvn test -Dtest=VerifyHomePageTest#verifyUserIsAbleToBookTheRoomSuccessfully
```

The browser launches in **headed mode** (you'll see Chrome open). It uses the real Chrome binary via `setChannel("chrome")`, not the bundled Chromium, so the experience looks exactly like a normal browser session.

---

## Key decisions

**Why Playwright over Selenium?**
Playwright's auto-waiting is much more reliable for a React/SPA app like this one. No manual `Thread.sleep` or `FluentWait` gymnastics needed.

**`DOMCONTENTLOADED` instead of `LOAD`**
Navigation waits for `domcontentloaded` rather than the default `load` event. The app fires a number of background XHR calls after load, and waiting for full `load` was causing `ERR_ABORTED` race conditions when the browser context closed mid-flight.

**`@BeforeMethod` / `@AfterMethod` for full stack setup**
Each test creates its own `Playwright → Browser → BrowserContext → Page` and tears it all down via `playwright.close()` at the end. This avoids the cross-thread object adoption errors that happen when browser instances are shared across TestNG methods.

**`playwright.close()` only in teardown — never `browser.close()` separately**
Calling `browser.close()` before `playwright.close()` causes a double-close crash because `playwright.close()` already disposes everything it owns.

**Fluent / builder-style page methods**
Methods like `enterCheckInDate()`, `enterCheckOutDate()` return `this` so tests read as a sequence of steps rather than a wall of individual statements.

---

## Time spent

| Activity | Time |
|---|---|
| Exploratory testing of the app | ~30 min |
| Framework setup, pom, folder structure | ~20 min |
| Writing page objects (HomePage, ReservationPage) | ~45 min |
| Writing and debugging test cases | ~60 min |
| Fixing browser launch / teardown issues | ~40 min |
| README | ~15 min |
| **Total** | **~3.5 hours** |

---

## AI assistance

GitHub Copilot (via JetBrains plugin) was used during development. Specifically:

- Helped diagnose and fix a series of Playwright-specific teardown errors (`TargetClosedError`, `ERR_ABORTED`, `Cannot find object to call __adopt__`) — the root causes were understood and verified manually before applying fixes
- Suggested the `DOMCONTENTLOADED` navigation strategy after the `ERR_ABORTED` errors were explained
- Assisted with writing this README

All test logic, locator choices, assertion strategy, and bug identification were done manually by exploring the application.

