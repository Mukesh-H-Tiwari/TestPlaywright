package com.example.automation.utils;

import com.microsoft.playwright.Page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BasePage {

    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public String getCurrentUrl() {
        return page.url();
    }

    public BasePage waitForElementToBeVisible(String selectorStr, int waitTimeInSeconds) {
        this.waitForElementState(this.page.locator(selectorStr), "visible", waitTimeInSeconds);
        return this;
    }

    public BasePage waitForElementToBeVisible(Locator locator, int waitTimeInSeconds) {
        this.waitForElementState(locator, "visible", waitTimeInSeconds);
        return this;
    }

    public BasePage waitForElementState(Locator locator, String state, int waitTimeInSeconds) {
        locator.waitFor((new Locator.WaitForOptions()).setState(this.getSelectorState(state)).setTimeout((double)(waitTimeInSeconds * 1000)));
        return this;
    }

    private WaitForSelectorState getSelectorState(String state) {
        WaitForSelectorState elementState = null;
        switch (state.toLowerCase()) {
            case "attached":
                elementState = WaitForSelectorState.ATTACHED;
                break;
            case "detached":
                elementState = WaitForSelectorState.DETACHED;
                break;
            case "visible":
                elementState = WaitForSelectorState.VISIBLE;
                break;
            case "hidden":
                elementState = WaitForSelectorState.HIDDEN;
                break;
            default:
                elementState = WaitForSelectorState.VISIBLE;
        }

        return elementState;
    }

}
