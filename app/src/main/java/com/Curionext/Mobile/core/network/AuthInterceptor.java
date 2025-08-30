package com.curionext.mobile.core.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private static final String TAG = "AuthInterceptor";
    private static final String PREF_AUTH_TOKEN = "auth_token";
    private static final String PREF_DEVICE_ID = "device_id";
    private static final String PREF_CHILD_ID = "child_id";

    private final SharedPreferences sharedPreferences;

    public AuthInterceptor(Context context) {
        this.sharedPreferences = context.getSharedPreferences("curionext_prefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();

        // Add common headers
        requestBuilder
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "CurioNext-Mobile/1.0.0");

        // Add authentication token if available
        String authToken = getAuthToken();
        if (authToken != null && !authToken.isEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);
            Log.d(TAG, "Added auth token to request");
        } else {
            Log.w(TAG, "No auth token available");
        }

        // Add device ID header for device identification
        String deviceId = getDeviceId();
        if (deviceId != null && !deviceId.isEmpty()) {
            requestBuilder.addHeader("X-Device-ID", deviceId);
        }

        // Add child ID header for child-specific requests
        String childId = getChildId();
        if (childId != null && !childId.isEmpty()) {
            requestBuilder.addHeader("X-Child-ID", childId);
        }

        Request authenticatedRequest = requestBuilder.build();

        try {
            Response response = chain.proceed(authenticatedRequest);

            // Handle authentication errors
            if (response.code() == 401) {
                Log.w(TAG, "Authentication failed - clearing stored token");
                clearAuthToken();
            }

            // Log successful requests in debug mode
            if (response.isSuccessful()) {
                Log.d(TAG, "Request successful: " + originalRequest.url());
            } else {
                Log.w(TAG, "Request failed: " + response.code() + " - " + originalRequest.url());
            }

            return response;

        } catch (IOException e) {
            Log.e(TAG, "Network request failed: " + e.getMessage());
            throw e;
        }
    }

    private String getAuthToken() {
        return sharedPreferences.getString(PREF_AUTH_TOKEN, null);
    }

    private String getDeviceId() {
        String deviceId = sharedPreferences.getString(PREF_DEVICE_ID, null);
        if (deviceId == null) {
            // Generate a device ID if not present
            deviceId = generateDeviceId();
            saveDeviceId(deviceId);
        }
        return deviceId;
    }

    private String getChildId() {
        return sharedPreferences.getString(PREF_CHILD_ID, null);
    }

    private void clearAuthToken() {
        sharedPreferences.edit()
                .remove(PREF_AUTH_TOKEN)
                .apply();
    }

    private String generateDeviceId() {
        // Generate a unique device identifier
        return "device_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 10000);
    }

    private void saveDeviceId(String deviceId) {
        sharedPreferences.edit()
                .putString(PREF_DEVICE_ID, deviceId)
                .apply();
    }

    // Public methods for token management (called from login/logout)
    public void setAuthToken(String token) {
        sharedPreferences.edit()
                .putString(PREF_AUTH_TOKEN, token)
                .apply();
        Log.d(TAG, "Auth token saved");
    }

    public void setChildId(String childId) {
        sharedPreferences.edit()
                .putString(PREF_CHILD_ID, childId)
                .apply();
        Log.d(TAG, "Child ID saved: " + childId);
    }

    public void clearAllCredentials() {
        sharedPreferences.edit()
                .remove(PREF_AUTH_TOKEN)
                .remove(PREF_CHILD_ID)
                .apply();
        Log.d(TAG, "All credentials cleared");
    }

    public boolean hasValidToken() {
        String token = getAuthToken();
        return token != null && !token.isEmpty();
    }

    public String getCurrentChildId() {
        return getChildId();
    }

    public String getCurrentDeviceId() {
        return getDeviceId();
    }
}