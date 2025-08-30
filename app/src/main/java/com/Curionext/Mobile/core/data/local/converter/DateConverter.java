package com.curionext.mobile.core.data.local.converter;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Room TypeConverter for Date objects
 * Converts Date to Long (timestamp) for database storage
 * and Long back to Date for application use
 */
public class DateConverter {

    /**
     * Convert Date to Long timestamp for database storage
     * @param date The Date object to convert
     * @return Long timestamp or null if date is null
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    /**
     * Convert Long timestamp to Date object
     * @param timestamp The timestamp to convert
     * @return Date object or null if timestamp is null
     */
    @TypeConverter
    public static Date timestampToDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    /**
     * Convert String array to JSON string for database storage
     * @param array The String array to convert
     * @return JSON string representation
     */
    @TypeConverter
    public static String stringArrayToJson(String[] array) {
        if (array == null || array.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append("\"").append(array[i].replace("\"", "\\\"")).append("\"");
            if (i < array.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Convert JSON string back to String array
     * @param json The JSON string to convert
     * @return String array or null if json is null/empty
     */
    @TypeConverter
    public static String[] jsonToStringArray(String json) {
        if (json == null || json.isEmpty() || json.equals("null")) {
            return new String[0];
        }

        try {
            // Simple JSON array parser (for basic string arrays)
            json = json.trim();
            if (!json.startsWith("[") || !json.endsWith("]")) {
                return new String[0];
            }

            json = json.substring(1, json.length() - 1); // Remove brackets
            if (json.trim().isEmpty()) {
                return new String[0];
            }

            String[] parts = json.split(",");
            String[] result = new String[parts.length];

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i].trim();
                if (part.startsWith("\"") && part.endsWith("\"")) {
                    part = part.substring(1, part.length() - 1);
                }
                result[i] = part.replace("\\\"", "\"");
            }

            return result;
        } catch (Exception e) {
            return new String[0];
        }
    }
}