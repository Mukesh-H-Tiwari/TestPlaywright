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

    // Predefined list of future date offsets (days from today) spread across ~6 months
    private static final List<Integer> FUTURE_DATE_DAYS = Arrays.asList(
            30, 33, 36, 39, 42, 45, 48, 51, 54, 57,
            60, 63, 66, 69, 72, 75, 78, 81, 84, 87,
            90, 93, 96, 99, 102, 105, 108, 111, 114, 117,
            120, 123, 126, 129, 132, 135, 138, 141, 144, 147,
            150, 153, 156, 159, 162, 165, 168, 171, 174, 177,
            180
    );

    /**
     * Returns a random future LocalDate between 30 and 120 days from today.
     */
    public static LocalDate getRandomFutureDate() {
        int randomDays = FUTURE_DATE_MIN_DAYS
                + new Random().nextInt(FUTURE_DATE_MAX_DAYS - FUTURE_DATE_MIN_DAYS + 1);
        return LocalDate.now().plusDays(randomDays);
    }

    public static List<LocalDate> getShuffledFutureDates() {
        List<Integer> shuffled = new ArrayList<>(FUTURE_DATE_DAYS);
        Collections.shuffle(shuffled);
        List<LocalDate> dates = new ArrayList<>();
        for (int days : shuffled) {
            dates.add(LocalDate.now().plusDays(days));
        }
        return dates;
    }
}
