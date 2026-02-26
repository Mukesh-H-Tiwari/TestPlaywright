package com.example.automation.utils;

/**
 * Central place for all test data constants.
 * If the app changes pricing or copy, only this file needs updating.
 */
public class TestConstants {

    private TestConstants() { /* utility class — no instances */ }

    // ---------- URLs ----------
    public static final String HOME_PAGE_URL = "https://automationintesting.online/";

    // ---------- Room pricing ----------
    public static final int SINGLE_ROOM_PRICE_PER_NIGHT = 100;
    public static final int DOUBLE_ROOM_PRICE_PER_NIGHT = 150;
    public static final int SUITE_ROOM_PRICE_PER_NIGHT  = 200;
    public static final int CLEANING_FEE                = 25;
    public static final int SERVICE_FEE                 = 15;

    // ---------- Guest details ----------
    public static final String GUEST_FIRST_NAME = "John";
    public static final String GUEST_LAST_NAME  = "Doe";
    public static final String GUEST_EMAIL      = "JohnDoe@cba.com";
    public static final String GUEST_PHONE      = "56345678910";

    // ---------- Invalid data ----------
    public static final String INVALID_PHONE_TOO_LONG = "56345678910102910291029102910";

    // ---------- Expected UI text ----------
    public static final String HOME_PAGE_HEADER          = "Shady Meadows B&B";
    public static final String BOOKING_CONFIRMED_MESSAGE = "Booking Confirmed";
    public static final String DOUBLE_ROOM_TITLE         = "Double Room";

    // ---------- Room types ----------
    public static final String ROOM_SINGLE = "Single";
    public static final String ROOM_DOUBLE = "Double";
    public static final String ROOM_SUITE  = "Suite";

    // ---------- Date format ----------
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    // ---------- Validation alert messages ----------
    public static final String ALERT_FIRSTNAME_BLANK        = "Firstname should not be blank";
    public static final String ALERT_LASTNAME_BLANK         = "Lastname should not be blank";
    public static final String ALERT_PHONE_SIZE             = "size must be between 11 and 21";
    public static final String ALERT_LASTNAME_SIZE          = "size must be between 3 and 18";
    public static final String ALERT_EMAIL_BLANK            = "must not be empty";
    public static final String ALERT_SUBJECT_SIZE           = "size must be between 3 and 30";
    public static final String ALERT_PHONE_BLANK            = "must not be empty";
}

