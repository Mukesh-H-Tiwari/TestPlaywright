package com.example.automation.tests.Home;

import com.example.automation.pages.HomePage;
import com.example.automation.pages.ReservationPage;
import com.example.automation.tests.BaseTest;
import com.example.automation.utils.DateUtils;
import com.example.automation.utils.TestConstants;
import com.example.automation.utils.urlHelper;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class VerifyHomePageTest extends BaseTest {

    private HomePage homePage;
    private ReservationPage reservationPage;

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void setUpPages() {
        homePage = new HomePage(page);
        reservationPage = new ReservationPage(page);
    }

    //Defect1: User is able to book with past dates.
    @Test(description = "Verify user is able to book the room successfully",
            groups = {"Regression", "Booking"},
            testName = "TC_Book_Room_Success")
    public void verifyUserIsAbleToBookTheRoomSuccessfully() {

        // Generate a random future date for booking
        LocalDate randomFutureDate = DateUtils.getRandomFutureDate();

        // Navigate to home page and verify header
        homePage.navigate(urlHelper.homePageUrl);
        Assert.assertEquals(homePage.getHeaderText(), TestConstants.HOME_PAGE_HEADER, "Header text mismatch.");

        // Enter check-in and check-out dates
        homePage.enterCheckInDate(randomFutureDate.format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)))
                .enterCheckOutDate(randomFutureDate.plusDays(4).format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)));

        // Verify check-in and check-out dates are correctly populated
        Assert.assertEquals(homePage.getCheckInDate(),
                LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)), "Check-in date mismatch.");
        Assert.assertEquals(homePage.getCheckOutDate(),
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)), "Check-out date mismatch.");

        // Search for available rooms and verify all room types are listed
        homePage.clickCheckAvailability();
        Assert.assertEquals(homePage.getAvailableRooms(),
                Arrays.asList(TestConstants.ROOM_SINGLE, TestConstants.ROOM_DOUBLE, TestConstants.ROOM_SUITE), "Available rooms mismatch.");

        // Select Double room and navigate calendar to the target month
        homePage.clickBookNowForRoom(TestConstants.ROOM_DOUBLE);
        reservationPage.waitForReservationPageToLoad();
        reservationPage.navigateCalendarToMonth(randomFutureDate);

        // Verify reservation page title and selected room is reflected in UI
        Assert.assertEquals(reservationPage.getReservationRoomTitle(),
                TestConstants.DOUBLE_ROOM_TITLE, "Reservation page title mismatch.");
        Assert.assertTrue(reservationPage.verifySelectionIsReflected(),
                "Selected room is not reflected in the UI.");

        // Verify URL contains correct check-in and check-out dates
        Assert.assertTrue(reservationPage.getCurrentUrl().contains(randomFutureDate.toString()),
                "URL does not contain correct check-in date.");
        Assert.assertTrue(reservationPage.getCurrentUrl().contains(randomFutureDate.plusDays(4).toString()),
                "URL does not contain correct check-out date.");

        // Verify total price includes room rate, cleaning fee, and service fee
        Assert.assertTrue(reservationPage.getTotalPrice().contains(
                        Integer.toString((TestConstants.DOUBLE_ROOM_PRICE_PER_NIGHT * 4) + TestConstants.CLEANING_FEE + TestConstants.SERVICE_FEE)),
                "Total price calculation is incorrect.");

        // Fill in guest details and submit the reservation form
        reservationPage.clickReserveButton()
                .enterFirstName(TestConstants.GUEST_FIRST_NAME)
                .enterLastName(TestConstants.GUEST_LAST_NAME)
                .enterEmail(TestConstants.GUEST_EMAIL)
                .enterPhoneNumber(TestConstants.GUEST_PHONE)
                .clickReserveNow();

        // Verify booking confirmation message and dates on the confirmation screen
        Assert.assertEquals(reservationPage.getBookingConfirmationMessage(),
                TestConstants.BOOKING_CONFIRMED_MESSAGE, "Booking confirmation message mismatch.");
        Assert.assertEquals(reservationPage.getCheckInAndCheckOutDatesFromConfirmation(),
                randomFutureDate + " - " + randomFutureDate.plusDays(4),
                "Check-in and Check-out dates in confirmation message mismatch.");

    }

    // Defect1: The Alert messages are not in correct order and randomly displayed on the UI
    // Defect2: The Alert messages are not correct for the respective empty fields and validation rules.
    //          e.g. "must not be empty" should say "Phone number must not be empty"
    @Test(description = "Verify form validation for booking a room",
            groups = {"Regression", "Booking"},
            testName = "TC_Book_Room_Form_Validation")
    public void VerifyFormValidationForBooking() {

        // Generate a random future date for booking
        LocalDate randomFutureDate = DateUtils.getRandomFutureDate();

        // Navigate to home page, enter dates, and search for available rooms
        homePage.navigate(urlHelper.homePageUrl);
        homePage.enterCheckInDate(randomFutureDate.format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)))
                .enterCheckOutDate(randomFutureDate.plusDays(4).format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)));
        homePage.clickCheckAvailability();

        // Select Single room and wait for reservation page to load
        homePage.clickBookNowForRoom(TestConstants.ROOM_SINGLE);
        reservationPage.waitForReservationPageToLoad();

        // Submit empty form and verify all required field validation alerts are shown
        reservationPage.clickReserveButton().clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                TestConstants.ALERT_FIRSTNAME_BLANK,
                TestConstants.ALERT_PHONE_SIZE,
                TestConstants.ALERT_LASTNAME_BLANK,
                TestConstants.ALERT_LASTNAME_SIZE,
                TestConstants.ALERT_EMAIL_BLANK,
                TestConstants.ALERT_SUBJECT_SIZE,
                TestConstants.ALERT_PHONE_BLANK)), "Alert messages mismatch for empty fields.");

        // Enter first name and verify remaining field alerts still appear
        reservationPage.enterFirstName(TestConstants.GUEST_FIRST_NAME).clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                TestConstants.ALERT_PHONE_SIZE,
                TestConstants.ALERT_LASTNAME_BLANK,
                TestConstants.ALERT_LASTNAME_SIZE,
                TestConstants.ALERT_EMAIL_BLANK,
                TestConstants.ALERT_SUBJECT_SIZE,
                TestConstants.ALERT_PHONE_BLANK)), "Alert messages mismatch after entering first name.");

        // Enter last name and verify first name alert is dismissed
        reservationPage.enterLastName(TestConstants.GUEST_LAST_NAME).clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                TestConstants.ALERT_PHONE_SIZE,
                TestConstants.ALERT_LASTNAME_SIZE,
                TestConstants.ALERT_EMAIL_BLANK,
                TestConstants.ALERT_SUBJECT_SIZE,
                TestConstants.ALERT_PHONE_BLANK)), "Alert messages mismatch after entering last name.");

        // Enter email and verify only phone-related alerts remain
        reservationPage.enterEmail(TestConstants.GUEST_EMAIL).clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                TestConstants.ALERT_PHONE_SIZE,
                TestConstants.ALERT_EMAIL_BLANK,
                TestConstants.ALERT_PHONE_BLANK)), "Alert messages mismatch after entering email.");

        // Enter an oversized phone number and verify phone size validation alert is triggered
        reservationPage.enterPhoneNumber(TestConstants.INVALID_PHONE_TOO_LONG).clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                TestConstants.ALERT_PHONE_SIZE)), "Alert message mismatch for oversized phone number.");
    }
}
