package com.example.automation.utils;

import com.microsoft.playwright.Page;

public class BasePage {

    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }


    // Get Current Page Url
    public String getCurrentUrl() {
        return page.url();
    }

}
