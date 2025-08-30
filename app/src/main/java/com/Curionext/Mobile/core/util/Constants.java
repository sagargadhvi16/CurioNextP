package com.curionext.mobile.core.util;

public class Constants {

    // Shared Preferences Keys
    public static final String PREF_NAME = "curionext_prefs";
    public static final String PREF_AUTH_TOKEN = "auth_token";
    public static final String PREF_DEVICE_ID = "device_id";
    public static final String PREF_CHILD_ID = "child_id";
    public static final String PREF_PARENT_ID = "parent_id";
    public static final String PREF_IS_FIRST_LAUNCH = "is_first_launch";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";
    public static final String PREF_TRACKING_ENABLED = "tracking_enabled";
    public static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String PREF_LAST_SYNC_TIME = "last_sync_time";

    // API Endpoints
    public static final String ENDPOINT_AUTH = "auth/";
    public static final String ENDPOINT_CHILDREN = "children/";
    public static final String ENDPOINT_CONVERSATIONS = "conversations/";
    public static final String ENDPOINT_ANALYTICS = "analytics/";
    public static final String ENDPOINT_LOCATIONS = "locations/";
    public static final String ENDPOINT_SAFE_ZONES = "safezones/";
    public static final String ENDPOINT_NOTIFICATIONS = "notifications/";
    public static final String ENDPOINT_PREFERENCES = "preferences/";

    // WebSocket Topics
    public static final String WS_TOPIC_CHILD_UPDATES = "/topic/child/{childId}/updates";
    public static final String WS_TOPIC_DASHBOARD = "/topic/parent/{childId}/dashboard";
    public static final String WS_TOPIC_EMERGENCY = "/topic/emergency/{childId}";
    public static final String WS_TOPIC_INTERESTS = "/topic/child/{childId}/interests";
    public static final String WS_TOPIC_ENGAGEMENT = "/topic/child/{childId}/engagement";

    // Database Constants
    public static final String DB_NAME = "curionext_database";
    public static final int DB_VERSION = 1;

    // Location Constants
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    public static final int LOCATION_UPDATE_INTERVAL = 30000; // 30 seconds
    public static final int LOCATION_FASTEST_INTERVAL = 10000; // 10 seconds
    public static final float LOCATION_DISPLACEMENT = 10.0f; // 10 meters
    public static final int DEFAULT_SAFE_ZONE_RADIUS = 100; // 100 meters
    public static final int MAX_SAFE_ZONE_RADIUS = 1000; // 1 km
    public static final int MIN_SAFE_ZONE_RADIUS = 25; // 25 meters

    // Audio Constants
    public static final int AUDIO_PERMISSION_REQUEST_CODE = 1002;
    public static final int AUDIO_SAMPLE_RATE = 16000;
    public static final int AUDIO_RECORDING_DURATION = 30000; // 30 seconds max

    // Notification Constants
    public static final String NOTIFICATION_CHANNEL_GENERAL = "general";
    public static final String NOTIFICATION_CHANNEL_SAFETY = "safety";
    public static final String NOTIFICATION_CHANNEL_LEARNING = "learning";
    public static final String NOTIFICATION_CHANNEL_EMERGENCY = "emergency";

    // Interest Analysis Constants
    public static final double MIN_INTEREST_LEVEL = 0.0;
    public static final double MAX_INTEREST_LEVEL = 10.0;
    public static final double HIGH_INTEREST_THRESHOLD = 7.0;
    public static final double LOW_INTEREST_THRESHOLD = 3.0;
    public static final int MAX_INTERESTS_PER_CATEGORY = 50;

    // Preference Analysis Constants
    public static final double POSITIVE_SENTIMENT_THRESHOLD = 0.1;
    public static final double NEGATIVE_SENTIMENT_THRESHOLD = -0.1;
    public static final double HIGH_CONFIDENCE_THRESHOLD = 0.7;
    public static final double MIN_CONFIDENCE_THRESHOLD = 0.3;

    // Time Constants
    public static final int RECENT_DAYS_DEFAULT = 7;
    public static final int ANALYTICS_PERIOD_DEFAULT = 30;
    public static final long SYNC_INTERVAL = 300000; // 5 minutes
    public static final long CACHE_EXPIRY_TIME = 3600000; // 1 hour

    // UI Constants
    public static final int DASHBOARD_ITEMS_LIMIT = 5;
    public static final int INTEREST_GRID_COLUMNS = 2;
    public static final int NOTIFICATION_PAGE_SIZE = 20;
    public static final int LOCATION_HISTORY_PAGE_SIZE = 50;
    public static final int ANIMATION_DURATION_SHORT = 200;
    public static final int ANIMATION_DURATION_MEDIUM = 400;
    public static final int ANIMATION_DURATION_LONG = 600;

    // Error Messages
    public static final String ERROR_NETWORK = "Network connection error";
    public static final String ERROR_AUTH = "Authentication failed";
    public static final String ERROR_PERMISSION_LOCATION = "Location permission required";
    public static final String ERROR_PERMISSION_AUDIO = "Audio permission required";
    public static final String ERROR_NO_DATA = "No data available";
    public static final String ERROR_SYNC_FAILED = "Data sync failed";

    // Success Messages
    public static final String SUCCESS_DATA_LOADED = "Data loaded successfully";
    public static final String SUCCESS_LOCATION_UPDATED = "Location updated";
    public static final String SUCCESS_SAFE_ZONE_CREATED = "Safe zone created";
    public static final String SUCCESS_NOTIFICATION_READ = "Notification marked as read";

    // Intent Extra Keys
    public static final String EXTRA_CHILD_ID = "extra_child_id";
    public static final String EXTRA_INTEREST_ID = "extra_interest_id";
    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";
    public static final String EXTRA_SAFE_ZONE_ID = "extra_safe_zone_id";
    public static final String EXTRA_LOCATION_DATA = "extra_location_data";

    // File Paths
    public static final String AUDIO_CACHE_DIR = "audio_cache";
    public static final String IMAGE_CACHE_DIR = "image_cache";
    public static final String EXPORT_DIR = "exports";

    // Emotion Categories (matching your backend)
    public static final String EMOTION_EXCITEMENT = "excitement";
    public static final String EMOTION_CURIOSITY = "curiosity";
    public static final String EMOTION_FRUSTRATION = "frustration";
    public static final String EMOTION_CONFUSION = "confusion";
    public static final String EMOTION_SATISFACTION = "satisfaction";
    public static final String EMOTION_BOREDOM = "boredom";

    // Learning Indicators
    public static final String TOPIC_DEPTH_SHALLOW = "shallow";
    public static final String TOPIC_DEPTH_MODERATE = "moderate";
    public static final String TOPIC_DEPTH_DEEP = "deep";

    public static final String VOCABULARY_LEVEL_BASIC = "basic";
    public static final String VOCABULARY_LEVEL_INTERMEDIATE = "intermediate";
    public static final String VOCABULARY_LEVEL_ADVANCED = "advanced";

    // Engagement Levels
    public static final String ENGAGEMENT_LOW = "low";
    public static final String ENGAGEMENT_MEDIUM = "medium";
    public static final String ENGAGEMENT_HIGH = "high";

    // Trend Directions
    public static final String TREND_INCREASING = "increasing";
    public static final String TREND_DECREASING = "decreasing";
    public static final String TREND_STABLE = "stable";

    // Priority Levels
    public static final String PRIORITY_LOW = "low";
    public static final String PRIORITY_MEDIUM = "medium";
    public static final String PRIORITY_HIGH = "high";
    public static final String PRIORITY_URGENT = "urgent";

    // Notification Types
    public static final String NOTIFICATION_TYPE_INTEREST = "interest";
    public static final String NOTIFICATION_TYPE_MILESTONE = "milestone";
    public static final String NOTIFICATION_TYPE_ALERT = "alert";
    public static final String NOTIFICATION_TYPE_SUMMARY = "summary";
    public static final String NOTIFICATION_TYPE_ACTIVITY = "activity";
    public static final String NOTIFICATION_TYPE_SYSTEM = "system";

    // Notification Categories
    public static final String NOTIFICATION_CATEGORY_LEARNING = "learning";
    public static final String NOTIFICATION_CATEGORY_SAFETY = "safety";
    public static final String NOTIFICATION_CATEGORY_REPORT = "report";
    public static final String NOTIFICATION_CATEGORY_SYSTEM = "system";

    // Default Values
    public static final String DEFAULT_CHILD_NAME = "Child";
    public static final int DEFAULT_CHILD_AGE = 8;
    public static final String DEFAULT_SAFE_ZONE_COLOR = "#4CAF50";
    public static final String DEFAULT_SAFE_ZONE_ICON = "home";
    public static final double DEFAULT_INTEREST_LEVEL = 5.0;
    public static final double DEFAULT_CONFIDENCE_LEVEL = 0.5;

    // Validation Constants
    public static final int MIN_CHILD_NAME_LENGTH = 2;
    public static final int MAX_CHILD_NAME_LENGTH = 50;
    public static final int MIN_CHILD_AGE = 3;
    public static final int MAX_CHILD_AGE = 18;
    public static final int MIN_SAFE_ZONE_NAME_LENGTH = 3;
    public static final int MAX_SAFE_ZONE_NAME_LENGTH = 30;

    // Private constructor to prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}