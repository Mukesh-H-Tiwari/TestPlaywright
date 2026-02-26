# Test Cases – QA Automation Assignment (Playwright Java)

Application Under Test: https://automationintesting.online/

These test cases cover the main user workflows of the application.  
Focus is mainly on the booking flow, since it represents the primary user journey.

Due to limited assignment time, only high-priority scenarios were automated.

---

## Automated Test Cases

### TC01 – Successful Room Booking (End-to-End Flow)

Priority: High  
Type: E2E

Steps:
1. Open application homepage
2. Select Check-in and Check-out dates and click Check availability
3. Select the available room (Double) and click Book now
4. Fill booking form with valid data:
    - First Name
    - Last Name
    - Email
    - Phone
    - Check-in date
    - Check-out date
5. Submit the booking

Expected Result:
- Home page Header Title is visible (Shady Meadows B&B)
- Available rooms are displayed after checking availability (Single, Double, Suite)
- Booking confirmation message is displayed
- No validation errors are shown
- Booking is completed successfully

Purpose:
Validates the complete booking journey from room selection to confirmation.

---

### TC02 – Booking with Invalid Email

Priority: High  
Type: Negative / Validation

Steps:
1. Navigate to booking page
2. Enter invalid email (example: test@)
3. Fill remaining fields with valid values
4. Submit booking

Expected Result:
- Email validation message is displayed
- Booking is not submitted
- Success message is not shown

Purpose:
Verifies email validation and prevents incorrect user input.

---

### TC03 – Submit Booking with Empty Required Fields

Priority: Medium  
Type: Negative

Steps:
1. Open booking form
2. Leave mandatory fields empty
3. Click submit

Expected Result:
- Required field validation messages appear
- Booking is blocked

---

### TC04 – Homepage Smoke Test

Priority: Medium  
Type: Smoke

Steps:
1. Open homepage

Expected Result:
- Page loads correctly
- Room listings are visible

---

## Notes

- Automation is implemented using Playwright Java.
- Page Object Model is used for cleaner structure and maintainability.
- Assertions focus on visible UI behavior and validation messages.
- Only critical scenarios were automated as part of this assignment.

End of document.