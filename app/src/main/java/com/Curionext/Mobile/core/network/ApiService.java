package com.curionext.mobile.core.network;

import com.curionext.mobile.core.data.model.Child;
import com.curionext.mobile.core.data.model.Interest;
import com.curionext.mobile.core.data.model.LocationData;
import com.curionext.mobile.core.data.model.NotificationItem;
import com.curionext.mobile.core.data.model.Preference;
import com.curionext.mobile.core.data.model.SafeZone;
import com.curionext.mobile.core.data.model.WeeklySummary;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Child Profile
    @GET("children/{childId}")
    Single<Child> getChildProfile(@Path("childId") String childId);

    @PUT("children/{childId}")
    Single<Child> updateChildProfile(@Path("childId") String childId, @Body Child child);

    // Interests
    @GET("children/{childId}/interests")
    Single<List<Interest>> getChildInterests(@Path("childId") String childId);

    @GET("children/{childId}/interests/trends")
    Single<List<Interest>> getInterestTrends(@Path("childId") String childId, @Query("period") String period);

    // Location Tracking
    @GET("children/{childId}/location/current")
    Single<LocationData> getCurrentLocation(@Path("childId") String childId);

    @GET("children/{childId}/location/history")
    Single<List<LocationData>> getLocationHistory(@Path("childId") String childId, @Query("date") String date);

    @POST("children/{childId}/location")
    Single<LocationData> updateLocation(@Path("childId") String childId, @Body LocationData location);

    // Safe Zones
    @GET("children/{childId}/safezones")
    Single<List<SafeZone>> getSafeZones(@Path("childId") String childId);

    @POST("children/{childId}/safezones")
    Single<SafeZone> createSafeZone(@Path("childId") String childId, @Body SafeZone safeZone);

    @PUT("children/{childId}/safezones/{zoneId}")
    Single<SafeZone> updateSafeZone(@Path("childId") String childId, @Path("zoneId") String zoneId, @Body SafeZone safeZone);

    @DELETE("children/{childId}/safezones/{zoneId}")
    Single<Void> deleteSafeZone(@Path("childId") String childId, @Path("zoneId") String zoneId);

    // Notifications
    @GET("children/{childId}/notifications")
    Single<List<NotificationItem>> getNotifications(@Path("childId") String childId);

    @PUT("notifications/{notificationId}/read")
    Single<Void> markNotificationAsRead(@Path("notificationId") String notificationId);

    // Preferences
    @GET("children/{childId}/preferences")
    Single<List<Preference>> getPreferences(@Path("childId") String childId);

    @GET("children/{childId}/preferences/analysis")
    Single<List<Preference>> getPreferenceAnalysis(@Path("childId") String childId);

    // Weekly Summary
    @GET("children/{childId}/summary/weekly")
    Single<WeeklySummary> getWeeklySummary(@Path("childId") String childId);

    // Real-time updates
    @GET("children/{childId}/updates")
    Observable<String> getRealTimeUpdates(@Path("childId") String childId);
}
