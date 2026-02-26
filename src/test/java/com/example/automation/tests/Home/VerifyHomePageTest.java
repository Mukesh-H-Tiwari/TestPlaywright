package com.example.automation.tests.Home;

import com.example.automation.pages.HomePage;
import com.example.automation.pages.ReservationPage;
import com.example.automation.tests.BaseTest;
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

        // ---------- Test Steps ----------
        homePage.navigate(urlHelper.homePageUrl);

        Assert.assertEquals(homePage.getHeaderText(), TestConstants.HOME_PAGE_HEADER, "Header text mismatch.");

        homePage.enterCheckInDate(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)))
                .enterCheckOutDate(LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)));

        Assert.assertEquals(homePage.getCheckInDate(),
                LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)), "Check-in date mismatch.");
        Assert.assertEquals(homePage.getCheckOutDate(),
                LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)), "Check-out date mismatch.");

        homePage.clickCheckAvailability();

        Assert.assertEquals(homePage.getAvailableRooms(),
                Arrays.asList(TestConstants.ROOM_SINGLE, TestConstants.ROOM_DOUBLE, TestConstants.ROOM_SUITE), "Available rooms mismatch.");

        homePage.clickBookNowForRoom(TestConstants.ROOM_DOUBLE);
        reservationPage.waitForReservationPageToLoad();

        Assert.assertEquals(reservationPage.getReservationRoomTitle(),
                TestConstants.DOUBLE_ROOM_TITLE, "Reservation page title mismatch.");
        Assert.assertTrue(reservationPage.verifySelectionIsReflected(),
                "Selected room is not reflected in the UI.");

        Assert.assertTrue(reservationPage.getCurrentUrl().contains(LocalDate.now().plusDays(1).toString()),
                "URL does not contain correct check-in date.");
        Assert.assertTrue(reservationPage.getCurrentUrl().contains(LocalDate.now().plusDays(5).toString()),
                "URL does not contain correct check-out date.");

        Assert.assertTrue(reservationPage.getTotalPrice().contains(
                Integer.toString((TestConstants.DOUBLE_ROOM_PRICE_PER_NIGHT * 4) + TestConstants.CLEANING_FEE + TestConstants.SERVICE_FEE)),
                "Total price calculation is incorrect.");

        reservationPage.clickReserveButton()
                .enterFirstName(TestConstants.GUEST_FIRST_NAME)
                .enterLastName(TestConstants.GUEST_LAST_NAME)
                .enterEmail(TestConstants.GUEST_EMAIL)
                .enterPhoneNumber(TestConstants.GUEST_PHONE)
                .clickReserveNow();

        Assert.assertEquals(reservationPage.getBookingConfirmationMessage(),
                TestConstants.BOOKING_CONFIRMED_MESSAGE, "Booking confirmation message mismatch.");
        Assert.assertEquals(reservationPage.getCheckInAndCheckOutDatesFromConfirmation(),
                LocalDate.now().plusDays(1) + " - " + LocalDate.now().plusDays(5),
                "Check-in and Check-out dates in confirmation message mismatch.");

        reservationPage.clickReturnToHome();

        Assert.assertEquals(homePage.getHeaderText(), TestConstants.HOME_PAGE_HEADER, "Header text mismatch.");
    }

    // Defect1: The Alert messages are not in correct order and randomly displayed on the UI
    // Defect2: The Alert messages are not correct for the respective empty fields and validation rules.
    //          e.g. "must not be empty" should say "Phone number must not be empty"
    @Test(description = "Verify form validation for booking a room",
            groups = {"Regression", "Booking"},
            testName = "TC_Book_Room_Form_Validation")
    public void VerifyFormValidationForBooking() {

        // ---------- Test Steps ----------
        homePage.navigate(urlHelper.homePageUrl);

        homePage.enterCheckInDate(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)))
                .enterCheckOutDate(LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern(TestConstants.DATE_FORMAT)));

        homePage.clickCheckAvailability();

        homePage.clickBookNowForRoom(TestConstants.ROOM_SINGLE);
        reservationPage.waitForReservationPageToLoad();

        // All fields empty
        reservationPage.clickReserveButton().clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                TestConstants.ALERT_FIRSTNAME_BLANK,
                TestConstants.ALERT_PHONE_SIZE,
                TestConstants.ALERT_LASTNAME_BLANK,
                TestConstants.ALERT_LASTNAME_SIZE,
                TestConstants.ALERT_EMAIL_BLANK,
                TestConstants.ALERT_SUBJECT_SIZE,
                TestConstants.ALERT_PHONE_BLANK)), "Alert messages mismatch for empty fields.");

        // Enter first name only
        reservationPage.enterFirstName(TestConstants.GUEST_FIRST_NAME).clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                TestConstants.ALERT_PHONE_SIZE,
                TestConstants.ALERT_LASTNAME_BLANK,
                TestConstants.ALERT_LASTNAME_SIZE,
                TestConstants.ALERT_EMAIL_BLANK,
                TestConstants.ALERT_SUBJECT_SIZE,
                TestConstants.ALERT_PHONE_BLANK)), "Alert messages mismatch after entering first name.");

        // Enter last name
        reservationPage.enterLastName(TestConstants.GUEST_LAST_NAME).clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                TestConstants.ALERT_PHONE_SIZE,
                TestConstants.ALERT_LASTNAME_SIZE,
                TestConstants.ALERT_EMAIL_BLANK,
                TestConstants.ALERT_SUBJECT_SIZE,
                TestConstants.ALERT_PHONE_BLANK)), "Alert messages mismatch after entering last name.");

        // Enter email
        reservationPage.enterEmail(TestConstants.GUEST_EMAIL).clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                TestConstants.ALERT_PHONE_SIZE,
                TestConstants.ALERT_EMAIL_BLANK,
                TestConstants.ALERT_PHONE_BLANK)), "Alert messages mismatch after entering email.");

        // Enter phone number that exceeds max length
        reservationPage.enterPhoneNumber(TestConstants.INVALID_PHONE_TOO_LONG).clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                TestConstants.ALERT_PHONE_SIZE)), "Alert message mismatch for oversized phone number.");
    }
}
