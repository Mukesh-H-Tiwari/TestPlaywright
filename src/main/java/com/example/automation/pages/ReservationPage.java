package com.example.automation.pages;

import com.example.automation.utils.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.List;
import java.util.stream.Collectors;

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
        this.cssTagAReturnToHome = page.locator("A:text-is('Return Home')");
        this.cssTagAlertMessages = page.locator("div.alert li");
    }

    public ReservationPage waitForReservationPageToLoad() {
        cssTagH1RoomTitle.waitFor();
        return this;
    }

    public boolean verifySelectionIsReflected() {
        return cssTagDivSelectedDate.isVisible();
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
}
