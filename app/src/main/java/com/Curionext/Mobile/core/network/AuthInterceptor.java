package com.curionext.mobile.core.network;

import android.content.SharedPreferences;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    private SharedPreferences sharedPreferences;

    public AuthInterceptor() {
        // Initialize with dependency injection
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Add authentication token if available
        String token = getAuthToken();
        if (token != null && !token.isEmpty()) {
            Request authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .build();
            return chain.proceed(authenticatedRequest);
        }

        return chain.proceed(originalRequest);
    }

    private String getAuthToken() {
        // Retrieve token from secure storage
        return ""; // Implement token retrieval
    }
}
