package com.example.truedone.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        if (diff < TimeUnit.MINUTES.toMillis(1)) return "Just now";
        if (diff < TimeUnit.HOURS.toMillis(1)) return TimeUnit.MILLISECONDS.toMinutes(diff) + " mins ago";
        if (diff < TimeUnit.DAYS.toMillis(1)) return TimeUnit.MILLISECONDS.toHours(diff) + " hours ago";
        return TimeUnit.MILLISECONDS.toDays(diff) + " days ago";
    }

    public static String formatTimer(long millis) {
        long seconds = millis / 1000;
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    // --- UPDATED METHOD ---
    public static String formatDuration(long millis) {
        long totalSeconds = millis / 1000;

        // If it took less than a minute to complete the task, show seconds
        if (totalSeconds < 60) {
            return totalSeconds + " secs";
        }

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        // If it took an hour or more to complete the task, show "X hrs Y mins"
        if (hours > 0) {
            // Optional: hide minutes if it's exactly on the hour (e.g., "1 hrs 0 mins", then "1 hrs")
            if (minutes == 0) return hours + " hrs";
            return hours + " hrs " + minutes + " mins";
        }

        // Otherwise, just show minutes
        return minutes + " mins";
    }
}