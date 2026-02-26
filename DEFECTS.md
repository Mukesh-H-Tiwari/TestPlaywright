# Defects – Shady Meadows B&B

Application Under Test: https://automationintesting.online/
Discovered via: Automated test execution (Playwright Java + TestNG)
Date: 2026-02-26

---

## DEF-01 – App Accepts Past Check-In Dates (No Frontend Validation)

| Field | Detail |
|---|---|
| **ID** | DEF-01 |
| **Severity** | High |
| **Priority** | High |
| **Status** | Open |
| **Affected Test** | `TC_Book_Room_Success` → `verifyUserIsAbleToBookTheRoomSuccessfully` |
| **Discovered** | Automated test execution |

**Description:**
The home page date picker allows users to enter and submit a past date as the check-in date. There is no frontend guard that prevents a booking with a check-in date in the past. A user should not be able to book a room with a check-in date before today.

**Steps to Reproduce:**
1. Navigate to https://automationintesting.online/
2. Enter a past date (e.g. `01/01/2020`) in the **Check-in** date field
3. Enter any future date in the **Check-out** field
4. Click **Check Availability**

**Actual Result:**
Available rooms are displayed and the user is able to proceed with booking using a past check-in date.

**Expected Result:**
The app should validate that the check-in date is not in the past and display an appropriate error message (e.g. *"Check-in date must be today or in the future"*), preventing the user from proceeding.

**Evidence:**
Observed during automated test execution. The test uses `DateUtils.getRandomFutureDate()` as a workaround to avoid booking failures caused by this defect.

---

## DEF-02 – Validation Alert Messages Displayed in Non-Deterministic Order

| Field | Detail |
|---|---|
| **ID** | DEF-02 |
| **Severity** | Low |
| **Priority** | Medium |
| **Status** | Open |
| **Affected Test** | `TC_Book_Room_Form_Validation` → `VerifyFormValidationForBooking` |
| **Discovered** | Automated test execution |

**Description:**
When the reservation form is submitted with missing or invalid fields, the validation alert messages are displayed in a random, non-deterministic order on each page load. The order changes between submissions and between test runs.

**Steps to Reproduce:**
1. Navigate to https://automationintesting.online/
2. Search for available rooms and click **Book now** on any room
3. Click **Reserve** then **Reserve Now** without filling in any fields
4. Observe the order of the alert messages displayed
5. Repeat steps 3–4 multiple times

**Actual Result:**
Alert messages appear in a different order on each submission (e.g. phone alert may appear first on one run, last name alert on another).

**Expected Result:**
Validation alert messages should appear in a consistent, deterministic order — ideally top-to-bottom matching the field order on the form: First Name → Last Name → Email → Phone.

**Impact on Automation:**
The automated test uses `containsAll()` instead of `assertEquals()` on the alert list to work around this defect and avoid order-dependent failures.

---

## DEF-03 – Validation Alert Messages Are Too Generic (Field Not Identified)

| Field | Detail |
|---|---|
| **ID** | DEF-03 |
| **Severity** | Medium |
| **Priority** | Medium |
| **Status** | Open |
| **Affected Test** | `TC_Book_Room_Form_Validation` → `VerifyFormValidationForBooking` |
| **Discovered** | Automated test execution |

**Description:**
Several validation alert messages do not identify the field they relate to. For example, both the Email and Phone fields produce the message `"must not be empty"` — the same generic string. Similarly, `"size must be between 11 and 21"` does not mention that it applies to the phone number field, and `"size must be between 3 and 30"` does not mention the subject field.

**Current Alert Messages vs Expected:**

| Field | Current Alert | Expected Alert |
|---|---|---|
| Email | `"must not be empty"` | `"Email must not be empty"` |
| Phone | `"must not be empty"` | `"Phone number must not be empty"` |
| Phone | `"size must be between 11 and 21"` | `"Phone number must be between 11 and 21 characters"` |
| Last Name | `"size must be between 3 and 18"` | `"Last name must be between 3 and 18 characters"` |
| Subject | `"size must be between 3 and 30"` | `"Subject must be between 3 and 30 characters"` |

**Steps to Reproduce:**
1. Navigate to https://automationintesting.online/
2. Search for available rooms and click **Book now** on any room
3. Click **Reserve** then **Reserve Now** without filling in any fields
4. Observe the alert messages shown for Email and Phone fields

**Actual Result:**
Both Email and Phone show identical `"must not be empty"` messages. Size constraint messages do not reference the field name.

**Expected Result:**
Each alert message should clearly identify the field it refers to so the user knows exactly which input needs to be corrected.

**Impact on Automation:**
Because `"must not be empty"` is shared by both Email and Phone, the test asserts `"must not be empty"` appears at least twice (`×2`) rather than asserting distinct messages per field. This reduces assertion specificity.

---

## Summary

| ID | Title | Severity | Priority | Affected TC |
|---|---|---|---|---|
| DEF-01 | App accepts past check-in dates | High | High | TC01 |
| DEF-02 | Alert messages in non-deterministic order | Low | Medium | TC02 |
| DEF-03 | Generic alert messages don't identify the field | Medium | Medium | TC02 |

