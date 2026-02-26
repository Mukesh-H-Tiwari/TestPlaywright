# Test Cases – QA Automation Assignment (Playwright Java)

Application Under Test: https://automationintesting.online/

---

## Automated Test Cases

### TC01 – Successful Room Booking (End-to-End)

**Priority:** High
**Type:** E2E
**Test Method:** `verifyUserIsAbleToBookTheRoomSuccessfully`
**Groups:** `Regression`, `Booking`

**Known Defect:**
> **DEF-01** — App accepts past check-in dates with no frontend guard preventing booking with a past date. See [`DEFECTS.md`](DEFECTS.md#def-01--app-accepts-past-check-in-dates-no-frontend-validation).

**Pre-condition:** A random future date is generated to be used as the check-in date throughout the test.

**Steps & Assertions:**

| # | Action | Assertion |
|---|---|---|
| 1 | Navigate to the home page | Header text equals `"Shady Meadows B&B"` |
| 2 | Enter check-in date (`randomFutureDate`) and check-out date (`randomFutureDate + 4 days`) | Check-in input reflects `today + 1`; check-out input reflects `today + 5` |
| 3 | Click **Check Availability** | Available rooms list equals `["Single", "Double", "Suite"]` |
| 4 | Click **Book now** on the **Double** room; wait for reservation page to load; navigate calendar to target month | Reservation page title equals `"Double Room"`; selected dates are reflected in the calendar UI |
| 5 | — | URL contains the correct check-in date string and check-out date string |
| 6 | — | Total price contains `£150 × 4 nights + £25 cleaning fee + £15 service fee = £640` |
| 7 | Click **Reserve**, fill in guest details (`John`, `Doe`, `JohnDoe@cba.com`, `56345678910`), click **Reserve Now** | Confirmation message equals `"Booking Confirmed"`; confirmation card shows `randomFutureDate – randomFutureDate + 4 days` |

**Expected Result:**
- Full booking journey completes without errors
- Confirmation message and dates on the confirmation card are correct

---

### TC02 – Booking Form Field Validation (Progressive Negative)

**Priority:** High
**Type:** Negative / Validation
**Test Method:** `VerifyFormValidationForBooking`
**Groups:** `Regression`, `Booking`

**Known Defects:**
> **DEF-02** — Alert messages are displayed in a non-deterministic order on the UI. See [`DEFECTS.md`](DEFECTS.md#def-02--validation-alert-messages-displayed-in-non-deterministic-order).
> **DEF-03** — Alert messages are too generic (e.g. `"must not be empty"` instead of `"Phone number must not be empty"`). See [`DEFECTS.md`](DEFECTS.md#def-03--validation-alert-messages-are-too-generic-field-not-identified).

**Pre-condition:** Navigate to the home page, enter dates (`randomFutureDate` / `randomFutureDate + 4 days`), click **Check Availability**, click **Book now** on the **Single** room, and wait for the reservation page to load.

**Steps & Assertions:**

| # | Action | Alerts expected to be present |
|---|---|---|
| 1 | Click **Reserve** then **Reserve Now** (all fields empty) | `"Firstname should not be blank"`, `"Lastname should not be blank"`, `"size must be between 3 and 18"`, `"must not be empty"` ×2, `"size must be between 11 and 21"`, `"size must be between 3 and 30"` |
| 2 | Enter First Name (`"John"`), click **Reserve Now** | `"Lastname should not be blank"`, `"size must be between 3 and 18"`, `"must not be empty"` ×2, `"size must be between 11 and 21"`, `"size must be between 3 and 30"` |
| 3 | Enter Last Name (`"Doe"`), click **Reserve Now** | `"size must be between 3 and 18"`, `"must not be empty"` ×2, `"size must be between 11 and 21"`, `"size must be between 3 and 30"` |
| 4 | Enter Email (`"JohnDoe@cba.com"`), click **Reserve Now** | `"must not be empty"` ×2, `"size must be between 11 and 21"` |
| 5 | Enter oversized phone number (`"56345678910102910291029102910"` — exceeds 21 chars), click **Reserve Now** | `"size must be between 11 and 21"` |

**Expected Result:**
- All 7 required-field alerts appear on empty form submit
- Alerts clear progressively as each field is correctly filled
- Phone number length boundary (max 21 chars) is enforced

---

## Not Automated (Out of Scope for This Assignment)

| TC   | Description                              | Reason                                                                                     |
|------|------------------------------------------|--------------------------------------------------------------------------------------------|
| TC03 | Invalid email format validation          | App does not validate email format client-side; no distinct error message shown            |
| TC04 | Homepage smoke – page loads and rooms visible | Covered as pre-condition steps in TC01                                                |

---

## Notes

- Automation uses Playwright Java with TestNG
- Page Object Model used throughout; all page methods return `this` for fluent chaining
- Each test gets a fresh `Playwright → Browser → BrowserContext → Page` stack — no shared state between tests
- Screenshots are captured automatically on failure to `target/screenshots/<yyyyMMdd_HHmmss>_<testName>.png`
- Test data (prices, alert messages, guest details) are centralised in `TestConstants.java`
