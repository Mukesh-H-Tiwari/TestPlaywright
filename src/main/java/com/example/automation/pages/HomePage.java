package com.example.automation.pages;

import com.example.automation.utils.BasePage;
import com.example.automation.utils.TestConstants;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.List;
import static com.microsoft.playwright.options.AriaRole.BUTTON;

public class HomePage extends BasePage {

    private final Locator header;
    private final Locator checkInInput;
    private final Locator checkOutInput;
    private final Locator checkAvailabilityButton;
    private final Locator roomCards;
    private final Locator roomTitles;

    public HomePage(Page page) {
        super(page);
        this.header = page.locator("a.navbar-brand");
        this.checkInInput = page.locator("div.col-md-6:has(label[for='checkin']) input");
        this.checkOutInput = page.locator("div.col-md-6:has(label[for='checkout']) input");
        this.checkAvailabilityButton = page.getByRole(BUTTON, new Page.GetByRoleOptions().setName("Check Availability"));
        this.roomCards = page.locator("div.room-card");
        this.roomTitles = page.locator("h5.card-title");
    }

    public HomePage navigate(String url) {
        page.navigate(url, new Page.NavigateOptions().setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
        header.waitFor();
        return this;
    }

    public String getHeaderText() {
        return header.textContent().trim();
    }

    public HomePage enterCheckInDate(String date) {
        checkInInput.fill(date);
        return this;
    }

    public HomePage enterCheckOutDate(String date) {
        checkOutInput.fill(date);
        return this;
    }

    public String getCheckInDate() {
        return checkInInput.inputValue();
    }

    public String getCheckOutDate() {
        return checkOutInput.inputValue();
    }

    public HomePage clickCheckAvailability() {
        checkAvailabilityButton.click();
        roomCards.first().waitFor();
        return this;
    }

    public List<String> getAvailableRooms() {
        roomTitles.first().waitFor();
        return roomTitles.allInnerTexts();
    }

    public HomePage clickBookNowForRoom(String roomName) {
        Locator targetRoomCard = roomCards.filter(new Locator.FilterOptions().setHasText(roomName));
        targetRoomCard.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName(TestConstants.BOOK_NOW_BUTTON_TEXT)).click();
        return this;
    }

    public boolean isPageLoaded() {
        return header.isVisible();
    }

}