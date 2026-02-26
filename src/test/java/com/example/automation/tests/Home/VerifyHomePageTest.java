package com.example.automation.tests.Home;

import com.example.automation.pages.HomePage;
import com.example.automation.pages.ReservationPage;
import com.example.automation.utils.urlHelper;
import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class VerifyHomePageTest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private HomePage homePage;
    private ReservationPage reservationPage;

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();

        // Launch real Chrome (not Chromium) in maximized, non-incognito mode
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setChannel("chrome")
                        .setArgs(Arrays.asList(
                                "--start-maximized",
                                "--disable-gpu",
                                "--no-sandbox",
                                "--disable-dev-shm-usage"
                        )));

        // Create a fresh context and page for each test
        context = browser.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(null));

        page = context.newPage();
        homePage = new HomePage(page);
        reservationPage = new ReservationPage(page);
    }

    //Defect1: User is able to book with past dates.
    @Test(description = "Verify user is able to book the room successfully",
            groups = {"Regression", "Booking"},
            testName = "TC_Book_Room_Success")
    public void verifyUserIsAbleToBookTheRoomSuccessfully() {

        // ---------- Test Data ----------
        String expectedHeader = "Shady Meadows B&B";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
        String checkInDate = LocalDate.now()
                .plusDays(1)
                .format(dateFormatter);
        String checkOutDate = LocalDate.now()
                .plusDays(5)
                .format(dateFormatter);
        String checkInDateForUrl = LocalDate.now().plusDays(1).toString();
        String checkOutDateForUrl = LocalDate.now().plusDays(5).toString();
        String roomToBook = "Double";
        String reservationPageTitle = "Double Room";
        List<String> expectedRooms = Arrays.asList("Single", "Double", "Suite");
        int pricePerNight = 150;
        int numberOfNights = 4;
        int cleaningFee = 25;
        int serviceFee = 15;
        String expectedTotalPrice = Integer.toString((pricePerNight * numberOfNights)+(cleaningFee+serviceFee));
        String firstName = "John";
        String lastName = "Doe";
        String email = "JohnDoe@cba.com";
        String phoneNumber = "56345678910";
        String expectedConfirmationMessage = "Booking Confirmed";
        String expectedCheckInAndCheckOutDates = checkInDateForUrl +" - "+ checkOutDateForUrl;

        // ---------- Test Steps ----------
        homePage.navigate(urlHelper.homePageUrl);

        // Verify Home Page header
        Assert.assertEquals(homePage.getHeaderText(), expectedHeader, "Header text mismatch.");

        // Enter dates
        homePage.enterCheckInDate(checkInDate)
                .enterCheckOutDate(checkOutDate);

        // Validate entered dates
        Assert.assertEquals(homePage.getCheckInDate(), checkInDate, "Check-in date mismatch.");
        Assert.assertEquals(homePage.getCheckOutDate(), checkOutDate, "Check-out date mismatch.");

        // Check availability
        homePage.clickCheckAvailability();

        // Validate available rooms
        List<String> actualRooms = homePage.getAvailableRooms();
        Assert.assertEquals(actualRooms, expectedRooms, "Available rooms mismatch.");

        // Click Book Now
        homePage.clickBookNowForRoom(roomToBook);
        reservationPage.waitForReservationPageToLoad();

        // Validate Double room is selected in the reservation page
        Assert.assertEquals(reservationPage.getReservationRoomTitle(), reservationPageTitle, "Reservation page title mismatch.");

        // Validate booking selection is reflected in the UI
        Assert.assertTrue(reservationPage.verifySelectionIsReflected(), "Selected room is not reflected in the UI.");

        // get current URL and validate it contains the correct query parameters
        String url = reservationPage.getCurrentUrl();
        Assert.assertTrue(url.contains(checkInDateForUrl), "URL does not contain correct check-in date.");
        Assert.assertTrue(url.contains(checkOutDateForUrl), "URL does not contain correct check-out date.");

        // Verify total price is calculated correctly based on the number of nights and room price
        Assert.assertTrue(reservationPage.getTotalPrice().contains(expectedTotalPrice), "Total price calculation is incorrect.");

        reservationPage.clickReserveButton()
                .enterFirstName(firstName)
                .enterLastName(lastName)
                .enterEmail(email)
                .enterPhoneNumber(phoneNumber)
                .clickReserveNow();

        // Validate booking confirmation message
        Assert.assertEquals(reservationPage.getBookingConfirmationMessage(), expectedConfirmationMessage,
                "Booking confirmation message mismatch.");

        // Validate booking Dates in the confirmation message
        Assert.assertEquals(reservationPage.getCheckInAndCheckOutDatesFromConfirmation(), expectedCheckInAndCheckOutDates,
                "Check-in and Check-out dates in confirmation message mismatch.");

        //click on return to home
        reservationPage.clickReturnToHome();

        // Verify Home Page header
        Assert.assertEquals(homePage.getHeaderText(), expectedHeader, "Header text mismatch.");
    }

    // Defect1: The Alert messages are not in correct order and randomly displayed on the UI
    // Defect2: The Alert messages are not correct for the respective empty fields and validation rules. For ex: For Must not be empty, the alert message should be "Phone number must not be empty" instead of just "must not be empty"
    @Test(description = "Verify form validation for booking a room",
            groups = {"Regression", "Booking"},
            testName = "TC_Book_Room_Form_Validation")
    public void VerifyFormValidationForBooking() {
        // ---------- Test Data ----------
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
        String checkInDate = LocalDate.now()
                .plusDays(1)
                .format(dateFormatter);
        String checkOutDate = LocalDate.now()
                .plusDays(5)
                .format(dateFormatter);
        String roomToBook = "Single";
        String firstName = "John";
        String lastName = "Doe";
        String email = "JohnDoe@cba.com";
        String invalidEmail = "JohnDoe";
        String phoneNumber = "56345678910";
        String invalidNumber = "56345678910102910291029102910";

        // ---------- Test Steps ----------
        homePage.navigate(urlHelper.homePageUrl);

        // Enter dates
        homePage.enterCheckInDate(checkInDate)
                .enterCheckOutDate(checkOutDate);

        // Check availability
        homePage.clickCheckAvailability();

        // Click Book Now
        homePage.clickBookNowForRoom(roomToBook);
        reservationPage.waitForReservationPageToLoad();

        // Make all the fields empty and try to reserve
        reservationPage.clickReserveButton().clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList("Firstname should not be blank",
                        "size must be between 11 and 21",
                        "Lastname should not be blank",
                        "size must be between 3 and 18",
                        "must not be empty",
                        "size must be between 3 and 30",
                        "must not be empty")), "Alert messages mismatch for empty fields.");

        reservationPage.enterFirstName(firstName)
                .clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                "size must be between 11 and 21",
                "Lastname should not be blank",
                "size must be between 3 and 18",
                "must not be empty",
                "size must be between 3 and 30",
                "must not be empty")), "Alert messages mismatch for empty fields.");

        reservationPage.enterLastName(lastName)
                .clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                "size must be between 11 and 21",
                "size must be between 3 and 18",
                "must not be empty",
                "size must be between 3 and 30",
                "must not be empty")), "Alert messages mismatch for empty fields.");

        reservationPage.enterEmail(email)
                .clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                "size must be between 11 and 21",
                "must not be empty",
                "must not be empty")), "Alert messages mismatch for empty fields.");

        reservationPage.enterPhoneNumber(invalidNumber)
                .clickReserveNow();
        Assert.assertTrue(reservationPage.getAllAlertMessages().containsAll(Arrays.asList(
                "size must be between 11 and 21")), "Alert messages mismatch for empty fields.");

    }


    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // playwright.close() disposes browser + context + pages gracefully in one shot.
        // Closing context separately first causes ERR_ABORTED race when navigation is still in flight.
        try { if (playwright != null) { playwright.close(); } } catch (Exception ignored) {}
        playwright = null;
        browser    = null;
        context    = null;
        page       = null;
    }
}