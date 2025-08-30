package com.curionext.mobile.features.location.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.curionext.mobile.R;
import com.curionext.mobile.core.data.model.LocationData;
import com.curionext.mobile.core.util.DateUtils;
import com.curionext.mobile.core.util.LocationUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class LocationHistoryAdapter extends RecyclerView.Adapter<LocationHistoryAdapter.LocationHistoryViewHolder> {

    private final Context context;
    private final List<LocationData> locationHistory = new ArrayList<>();

    private OnLocationClickListener onLocationClickListener;

    public LocationHistoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public LocationHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_location_history, parent, false);
        return new LocationHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHistoryViewHolder holder, int position) {
        LocationData location = locationHistory.get(position);

        // Set address
        if (location.getAddress() != null && !location.getAddress().isEmpty()) {
            holder.addressText.setText(location.getAddress());
        } else {
            holder.addressText.setText("Getting address...");
            // Try to get address from coordinates
            String address = LocationUtils.getAddressFromLocation(context,
                    location.getLatitude(), location.getLongitude());
            holder.addressText.setText(address);
        }

        // Set time
        holder.timeText.setText(DateUtils.getChildFriendlyTime(location.getTimestamp()));

        // Set coordinates (for debugging/detailed view)
        String coordinates = String.format("%.6f, %.6f", location.getLatitude(), location.getLongitude());
        holder.coordinatesText.setText(coordinates);

        // Set accuracy
        String accuracyText = LocationUtils.formatDistance(location.getAccuracy()) +
                " (" + LocationUtils.getAccuracyDescription(location.getAccuracy()) + ")";
        holder.accuracyText.setText(accuracyText);

        // Set safe zone status
        if (location.isInSafeZone()) {
            holder.safeZoneIcon.setImageResource(R.drawable.ic_safe_zone_active);
            holder.safeZoneIcon.setColorFilter(ContextCompat.getColor(context, R.color.interest_positive));
            holder.safeZoneText.setText("In " + location.getSafeZoneName());
            holder.safeZoneText.setTextColor(ContextCompat.getColor(context, R.color.interest_positive));

            // Set card background for safe zone
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.safe_zone_background)
            );
        } else {
            holder.safeZoneIcon.setImageResource(R.drawable.ic_safe_zone_inactive);
            holder.safeZoneIcon.setColorFilter(ContextCompat.getColor(context, R.color.interest_negative));
            holder.safeZoneText.setText("Outside safe zone");
            holder.safeZoneText.setTextColor(ContextCompat.getColor(context, R.color.interest_negative));

            // Set card background for outside safe zone
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.outside_safe_zone_background)
            );
        }

        // Set speed and movement status
        if (location.getSpeed() > 0) {
            String speedText = LocationUtils.formatSpeed(location.getSpeed());
            String movementStatus = LocationUtils.getMovementStatus(location);
            holder.speedText.setText(speedText + " (" + movementStatus + ")");
            holder.speedText.setVisibility(View.VISIBLE);
        } else {
            holder.speedText.setVisibility(View.GONE);
        }

        // Set battery level if available
        if (location.getBatteryLevel() > 0) {
            holder.batteryText.setText(location.getBatteryLevel() + "%");
            holder.batteryText.setVisibility(View.VISIBLE);

            // Set battery icon based on level
            if (location.getBatteryLevel() > 75) {
                holder.batteryIcon.setImageResource(R.drawable.ic_battery_full);
            } else if (location.getBatteryLevel() > 50) {
                holder.batteryIcon.setImageResource(R.drawable.ic_battery_75);
            } else if (location.getBatteryLevel() > 25) {
                holder.batteryIcon.setImageResource(R.drawable.ic_battery_50);
            } else {
                holder.batteryIcon.setImageResource(R.drawable.ic_battery_25);
            }
            holder.batteryIcon.setVisibility(View.VISIBLE);
        } else {
            holder.batteryText.setVisibility(View.GONE);
            holder.batteryIcon.setVisibility(View.GONE);
        }

        // Set time-based styling
        if (DateUtils.isToday(location.getTimestamp())) {
            holder.timeText.setTextColor(ContextCompat.getColor(context, R.color.primary));
        } else {
            holder.timeText.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        }

        // Set click listener
        holder.cardView.setOnClickListener(v -> {
            if (onLocationClickListener != null) {
                onLocationClickListener.onLocationClick(location);
            }
        });

        holder.cardView.setOnLongClickListener(v -> {
            if (onLocationClickListener != null) {
                onLocationClickListener.onLocationLongClick(location);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return locationHistory.size();
    }

    public void updateLocationHistory(List<LocationData> newHistory) {
        locationHistory.clear();
        if (newHistory != null) {
            locationHistory.addAll(newHistory);
        }
        notifyDataSetChanged();
    }

    public void addLocation(LocationData location) {
        locationHistory.add(0, location); // Add at beginning
        notifyItemInserted(0);
    }

    public void clearHistory() {
        locationHistory.clear();
        notifyDataSetChanged();
    }

    static class LocationHistoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView addressText;
        TextView timeText;
        TextView coordinatesText;
        TextView accuracyText;
        TextView safeZoneText;
        TextView speedText;
        TextView batteryText;
        ImageView safeZoneIcon;
        ImageView batteryIcon;
        ImageView locationIcon;

        LocationHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.location_card);
            addressText = itemView.findViewById(R.id.address_text);
            timeText = itemView.findViewById(R.id.time_text);
            coordinatesText = itemView.findViewById(R.id.coordinates_text);
            accuracyText = itemView.findViewById(R.id.accuracy_text);
            safeZoneText = itemView.findViewById(R.id.safe_zone_text);
            speedText = itemView.findViewById(R.id.speed_text);
            batteryText = itemView.findViewById(R.id.battery_text);
            safeZoneIcon = itemView.findViewById(R.id.safe_zone_icon);
            batteryIcon = itemView.findViewById(R.id.battery_icon);
            locationIcon = itemView.findViewById(R.id.location_icon);
        }
    }

    // Click listener interface
    public interface OnLocationClickListener {
        void onLocationClick(LocationData location);
        void onLocationLongClick(LocationData location);
    }

    public void setOnLocationClickListener(OnLocationClickListener listener) {
        this.onLocationClickListener = listener;
    }
}