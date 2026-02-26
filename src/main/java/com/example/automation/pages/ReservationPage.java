package com.example.automation.pages;

import com.example.automation.utils.BasePage;
import com.example.automation.utils.DateUtils;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReservationPage extends BasePage {

    private final Locator cssTagH1RoomTitle;
    private final Locator cssTagDivSelectedDate;
    private final Locator cssTagCardBody;
    private final Locator cssTagDivTotal;
    private final Locator cssTagButtonReserve;
    private final Locator cssTagInputFirstName;
    private final Locator cssTagInputLastName;
    private final Locator cssTagInputEmail;
    private final Locator cssTagInputPhoneNumber;
    private final Locator cssTagButtonReserveNow;
    private final Locator cssTagH2BookingConfirmation;
    private final Locator cssTagPCheckInAndCheckOutDates;
    private final Locator cssTagAReturnToHome;
    private final Locator cssTagAlertMessages;

    // ---------- Calendar locators ----------
    private final Locator calendarNextButton;
    private final Locator calendarBackButton;
    private final Locator calendarMonthLabel;
    private final Locator calendarDateCells;


    public ReservationPage(Page page) {
        super(page);
        this.cssTagH1RoomTitle = page.locator("h1.fw-bold");
        this.cssTagDivSelectedDate = page.locator("div.rbc-event-content[title='Selected']");
        this.cssTagCardBody = page.locator("div.card-body");
        this.cssTagDivTotal = page.locator("div.fw-bold span");
        this.cssTagButtonReserve = page.locator("button#doReservation");
        this.cssTagInputFirstName = page.getByLabel("FirstName");
        this.cssTagInputLastName = page.getByLabel("LastName");
        this.cssTagInputEmail = page.getByLabel("Email");
        this.cssTagInputPhoneNumber = page.getByLabel("Phone");
        this.cssTagButtonReserveNow = page.locator("button:text-is('Reserve Now')");
        this.cssTagH2BookingConfirmation = page.locator("h2.card-title.mb-3");
        this.cssTagPCheckInAndCheckOutDates = page.locator("p.pt-2");
        this.cssTagAlertMessages = page.locator("div.alert li");
        this.calendarNextButton  = page.locator(".rbc-toolbar button:last-child");
        this.calendarBackButton  = page.locator(".rbc-toolbar .rbc-btn-group:first-child button:first-child");
        this.calendarMonthLabel  = page.locator(".rbc-toolbar-label");
        this.calendarDateCells   = page.locator(".rbc-date-cell");
        this.cssTagAReturnToHome   = page.locator("a.btn:text-is('Return home')");
    }

    public ReservationPage waitForReservationPageToLoad() {
        cssTagH1RoomTitle.waitFor();
        return this;
    }

    public boolean verifySelectionIsReflected() {
        return cssTagDivSelectedDate.first().isVisible();
    }

    public String getReservationRoomTitle() {
        return cssTagH1RoomTitle.textContent().trim();
    }

    public String getTotalPrice() {
        Locator total = cssTagCardBody.filter(new Locator.FilterOptions().setHasText("Total"));
        return total.locator(cssTagDivTotal).last().innerText();
    }

    public ReservationPage clickReserveButton() {
        cssTagButtonReserve.click();
        return this;
    }

    public ReservationPage enterFirstName(String firstName) {
        cssTagInputFirstName.fill(firstName);
        return this;
    }

    public ReservationPage enterLastName(String lastName) {
        cssTagInputLastName.fill(lastName);
        return this;
    }

    public ReservationPage enterEmail(String email) {
        cssTagInputEmail.fill(email);
        return this;
    }

    public ReservationPage enterPhoneNumber(String phoneNumber) {
        cssTagInputPhoneNumber.fill(phoneNumber);
        return this;
    }

    public ReservationPage clickReserveNow() {
        cssTagButtonReserveNow.click();
        return this;
    }

    public String getBookingConfirmationMessage() {
        cssTagAReturnToHome.waitFor();
        return cssTagH2BookingConfirmation.textContent().trim();
    }

    public String getCheckInAndCheckOutDatesFromConfirmation() {
        return cssTagPCheckInAndCheckOutDates.textContent();
    }

    public ReservationPage clickReturnToHome() {
        cssTagAReturnToHome.click();
        return this;
    }

    public List<String> getAllAlertMessages() {
        cssTagAlertMessages.first().waitFor();
        return cssTagAlertMessages.allInnerTexts();
    }

    public ReservationPage waitForAlertMessages() {
        cssTagAlertMessages.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
        return this;
    }

    /**
     * Reads the currently displayed month from rbc-toolbar-label,
     * then clicks Next or Back until it matches the target date's month/year.
     *
     * e.g. if label shows "February 2026" and target is June 2026 → clicks Next 4 times
     *      if label shows "February 2026" and target is November 2025 → clicks Back 3 times
     */
    public ReservationPage navigateCalendarToMonth(LocalDate targetDate) {
        DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        String targetLabel = targetDate.format(labelFmt);

        calendarMonthLabel.waitFor();

        for (int i = 0; i < 30; i++) {
            String currentLabel = calendarMonthLabel.innerText().trim();
            System.out.println("[Calendar] showing=" + currentLabel + "  need=" + targetLabel);

            if (currentLabel.equals(targetLabel)) break;

            // Parse the month currently visible on screen
            LocalDate currentMonth = LocalDate.parse(
                    "01 " + currentLabel,
                    DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH));

            // If target is after the last day of current month → go Next, else go Back
            if (targetDate.isAfter(currentMonth.withDayOfMonth(currentMonth.lengthOfMonth()))) {
                calendarNextButton.click();
            } else {
                calendarBackButton.click();
            }

            // Wait for the label to update before reading it again
            page.waitForFunction(
                    "prev => document.querySelector('.rbc-toolbar-label').innerText.trim() !== prev",
                    currentLabel);
        }
        return this;
    }

    /**
     * Returns true if the given day on the currently displayed calendar month
     * has no booked events (i.e. the day is available).
     */
    public boolean isDayAvailable(LocalDate date) {
        String dayText = String.valueOf(date.getDayOfMonth());
        Locator dayCell = calendarDateCells
                .filter(new Locator.FilterOptions().setHasText(dayText)).first();
        String cellClass = dayCell.getAttribute("class");
        boolean isOffRange   = cellClass != null && cellClass.contains("rbc-off-range");
        long    bookedEvents = dayCell.locator(".rbc-event")
                .filter(new Locator.FilterOptions().setHasNotText("Selected"))
                .count();
        return !isOffRange && bookedEvents == 0;
    }

    /**
     * Clicks a specific day cell on the currently displayed calendar month.
     */
    public ReservationPage selectCalendarDate(LocalDate date) {
        String dayText = String.valueOf(date.getDayOfMonth());
        calendarDateCells
                .filter(new Locator.FilterOptions().setHasText(dayText))
                .first().click();
        return this;
    }

    /**
     * Iterates the shuffled list of future dates from DateUtils.
     * For each candidate, reads rbc-toolbar-label, clicks Next or Back to reach
     * that month, then checks isDayAvailable(). Returns the first available date.
     * Throws if no available date is found across the entire list.
     */
    public LocalDate findFirstAvailableDate() {
        for (LocalDate candidate : DateUtils.getShuffledFutureDates()) {
            navigateCalendarToMonth(candidate);
            if (isDayAvailable(candidate)) {
                return candidate;
            }
        }
        throw new RuntimeException("No available date found across all predefined future dates.");
    }
}