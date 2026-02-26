package com.example.automation.utils;

import java.time.LocalDate;
import java.util.*;

/**
 * Utility methods for date generation used across test cases.
 */
public class DateUtils {

    private DateUtils() { /* utility class — no instances */ }

    private static final int FUTURE_DATE_MIN_DAYS = 30;
    private static final int FUTURE_DATE_MAX_DAYS = 120;


    /**
     * Returns a random future LocalDate between 30 and 120 days from today.
     */
    public static LocalDate getRandomFutureDate() {
        int randomDays = FUTURE_DATE_MIN_DAYS
                + new Random().nextInt(FUTURE_DATE_MAX_DAYS - FUTURE_DATE_MIN_DAYS + 1);
        return LocalDate.now().plusDays(randomDays);
    }

}
