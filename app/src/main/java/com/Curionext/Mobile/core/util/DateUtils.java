package com.curionext.mobile.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    // Date format patterns
    public static final String FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String FORMAT_DISPLAY_DATE = "MMM dd, yyyy";
    public static final String FORMAT_DISPLAY_TIME = "HH:mm";
    public static final String FORMAT_DISPLAY_DATETIME = "MMM dd, HH:mm";
    public static final String FORMAT_DISPLAY_FULL = "EEEE, MMM dd, yyyy 'at' HH:mm";
    public static final String FORMAT_FILE_TIMESTAMP = "yyyyMMdd_HHmmss";
    public static final String FORMAT_WEEK_RANGE = "MMM dd - MMM dd";

    private static final SimpleDateFormat apiFormat = new SimpleDateFormat(FORMAT_API, Locale.getDefault());
    private static final SimpleDateFormat displayDateFormat = new SimpleDateFormat(FORMAT_DISPLAY_DATE, Locale.getDefault());
    private static final SimpleDateFormat displayTimeFormat = new SimpleDateFormat(FORMAT_DISPLAY_TIME, Locale.getDefault());
    private static final SimpleDateFormat displayDateTimeFormat = new SimpleDateFormat(FORMAT_DISPLAY_DATETIME, Locale.getDefault());
    private static final SimpleDateFormat displayFullFormat = new SimpleDateFormat(FORMAT_DISPLAY_FULL, Locale.getDefault());
    private static final SimpleDateFormat fileTimestampFormat = new SimpleDateFormat(FORMAT_FILE_TIMESTAMP, Locale.getDefault());

    /**
     * Get current date and time
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * Format date for API communication
     */
    public static String formatForApi(Date date) {
        return apiFormat.format(date);
    }

    /**
     * Format date for display (e.g., "Dec 25, 2024")
     */
    public static String formatDisplayDate(Date date) {
        return displayDateFormat.format(date);
    }

    /**
     * Format time for display (e.g., "14:30")
     */
    public static String formatDisplayTime(Date date) {
        return displayTimeFormat.format(date);
    }

    /**
     * Format date and time for display (e.g., "Dec 25, 14:30")
     */
    public static String formatDisplayDateTime(Date date) {
        return displayDateTimeFormat.format(date);
    }

    /**
     * Format full date and time for display (e.g., "Monday, Dec 25, 2024 at 14:30")
     */
    public static String formatDisplayFull(Date date) {
        return displayFullFormat.format(date);
    }

    /**
     * Format date for file naming (e.g., "20241225_143000")
     */
    public static String formatForFileName(Date date) {
        return fileTimestampFormat.format(date);
    }

    /**
     * Get relative time string (e.g., "2 hours ago", "Yesterday", "3 days ago")
     */
    public static String getRelativeTimeString(Date date) {
        Date now = new Date();
        long diffMillis = now.getTime() - date.getTime();

        if (diffMillis < 0) {
            return "In the future";
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffMillis);
        long days = TimeUnit.MILLISECONDS.toDays(diffMillis);

        if (seconds < 60) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (days == 1) {
            return "Yesterday";
        } else if (days < 7) {
            return days + " days ago";
        } else if (days < 30) {
            long weeks = days / 7;
            return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";
        } else if (days < 365) {
            long months = days / 30;
            return months + " month" + (months > 1 ? "s" : "") + " ago";
        } else {
            long years = days / 365;
            return years + " year" + (years > 1 ? "s" : "") + " ago";
        }
    }

    /**
     * Get greeting based on time of day
     */
    public static String getTimeBasedGreeting(String name) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            return "Good morning, " + name;
        } else if (hour < 17) {
            return "Good afternoon, " + name;
        } else {
            return "Good evening, " + name;
        }
    }

    /**
     * Get start of day for a given date
     */
    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Get end of day for a given date
     */
    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * Get date N days ago
     */
    public static Date getDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        return calendar.getTime();
    }

    /**
     * Get start of current week
     */
    public static Date getStartOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return getStartOfDay(calendar.getTime());
    }

    /**
     * Get end of current week
     */
    public static Date getEndOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return getEndOfDay(calendar.getTime());
    }

    /**
     * Get start of current month
     */
    public static Date getStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(calendar.getTime());
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);

        return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Check if date is yesterday
     */
    public static boolean isYesterday(Date date) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        Calendar target = Calendar.getInstance();
        target.setTime(date);

        return yesterday.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Check if date is this week
     */
    public static boolean isThisWeek(Date date) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);

        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.WEEK_OF_YEAR) == target.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Get day of week name
     */
    public static String getDayOfWeek(Date date) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        return dayFormat.format(date);
    }

    /**
     * Get month name
     */
    public static String getMonthName(Date date) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        return monthFormat.format(date);
    }

    /**
     * Format duration in milliseconds to readable string
     */
    public static String formatDuration(long durationMillis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long days = TimeUnit.MILLISECONDS.toDays(durationMillis);

        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "");
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "");
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        } else {
            return seconds + " second" + (seconds != 1 ? "s" : "");
        }
    }

    /**
     * Format session duration for display (e.g., "12 minutes")
     */
    public static String formatSessionDuration(String sessionLength) {
        if (sessionLength == null || sessionLength.isEmpty()) {
            return "Unknown duration";
        }

        try {
            // Parse session length like "12_minutes" or "1_hour_30_minutes"
            String[] parts = sessionLength.split("_");
            if (parts.length >= 2) {
                int value = Integer.parseInt(parts[0]);
                String unit = parts[1];
                return value + " " + unit;
            }
        } catch (Exception e) {
            // Fall back to original string
        }

        return sessionLength.replace("_", " ");
    }

    /**
     * Get week range string (e.g., "Dec 18 - Dec 24")
     */
    public static String getWeekRangeString(Date weekStart, Date weekEnd) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd", Locale.getDefault());
        return format.format(weekStart) + " - " + format.format(weekEnd);
    }

    /**
     * Check if two dates are on the same day
     */
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Get age-appropriate time format for children
     */
    public static String getChildFriendlyTime(Date date) {
        if (isToday(date)) {
            return "Today at " + formatDisplayTime(date);
        } else if (isYesterday(date)) {
            return "Yesterday at " + formatDisplayTime(date);
        } else if (isThisWeek(date)) {
            return getDayOfWeek(date) + " at " + formatDisplayTime(date);
        } else {
            return formatDisplayDateTime(date);
        }
    }

    /**
     * Calculate age from birth date
     */
    public static int calculateAge(Date birthDate) {
        Calendar birth = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        birth.setTime(birthDate);

        int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        // Adjust if birthday hasn't occurred this year yet
        if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return Math.max(0, age);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private DateUtils() {
        throw new UnsupportedOperationException("DateUtils class cannot be instantiated");
    }
}