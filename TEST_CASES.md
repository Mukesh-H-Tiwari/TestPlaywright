# Test Cases – QA Automation Assignment (Playwright Java)

Application Under Test: https://automationintesting.online/

---

## Automated Test Cases

### TC01 – Successful Room Booking (End-to-End)

**Priority:** High  
**Type:** E2E  
**Test Method:** `verifyUserIsAbleToBookTheRoomSuccessfully`

**Steps:**
1. Open homepage and verify header (Shady Meadows B&B)
2. Enter check-in date (today + 1) and check-out date (today + 5)
3. Verify entered dates are reflected in the inputs
4. Click "Check Availability"
5. Verify available rooms list matches expected (Single, Double, Suite)
6. Click "Book now" on the Double room
7. Verify reservation page title is "Double Room"
8. Verify selected dates are shown in the calendar UI
9. Verify URL contains the correct check-in and check-out dates
10. Verify total price = £150 × 4 nights + £25 cleaning + £15 service = £640
11. Fill in guest details (First Name, Last Name, Email, Phone) and submit
12. Verify confirmation message = "Booking Confirmed"
13. Verify confirmation card shows correct check-in and check-out dates
14. Click "Return Home" and verify the header is visible again

**Expected Result:**
- Full booking journey completes without errors
- Confirmation message and dates are correct
- User is returned to the home page after booking

---

### TC02 – Booking Form Field Validation (Progressive Negative)

**Priority:** High  
**Type:** Negative / Validation  
**Test Method:** `VerifyFormValidationForBooking`

**Steps:**
1. Open homepage, enter dates, click Check Availability
2. Click "Book now" on the Single room
3. Click Reserve without filling any fields → verify all 7 alert messages appear
4. Enter First Name only → click Reserve Now → verify first name alert disappears
5. Enter Last Name → click Reserve Now → verify last name alert disappears
6. Enter Email → click Reserve Now → verify email alert disappears
7. Enter phone number that exceeds max length (> 21 chars) → verify size alert remains

**Expected Result:**
- All required field alerts appear on empty submit
- Alerts disappear progressively as each field is filled
- Phone number length boundary is enforced

**Known Defects:**
- Alert messages appear in a random/non-deterministic order on the UI
- Alert messages are too generic (e.g. `"must not be empty"` instead of `"Phone must not be empty"`)

---

## Notes

- Automation uses Playwright Java with TestNG
- Page Object Model used throughout
- Each test gets a fresh browser instance — no shared state between tests
- Screenshots are captured automatically on failure to `target/screenshots/`
