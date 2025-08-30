package com.curionext.mobile.features.location.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.curionext.mobile.R;
import com.curionext.mobile.core.data.model.SafeZone;
import com.curionext.mobile.core.util.DateUtils;
import com.curionext.mobile.core.util.LocationUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class SafeZoneAdapter extends RecyclerView.Adapter<SafeZoneAdapter.SafeZoneViewHolder> {

    private final Context context;
    private final List<SafeZone> safeZones = new ArrayList<>();

    private OnSafeZoneClickListener onSafeZoneClickListener;
    private OnToggleClickListener onToggleClickListener;

    public SafeZoneAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SafeZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_safe_zone, parent, false);
        return new SafeZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SafeZoneViewHolder holder, int position) {
        SafeZone safeZone = safeZones.get(position);

        // Set basic info
        holder.nameText.setText(safeZone.getName());
        holder.addressText.setText(safeZone.getAddress());

        // Set radius
        String radiusText = LocationUtils.formatDistance(safeZone.getRadius());
        holder.radiusText.setText("Radius: " + radiusText);

        // Set coordinates
        String coordinates = String.format("%.6f, %.6f",
                safeZone.getLatitude(), safeZone.getLongitude());
        holder.coordinatesText.setText(coordinates);

        // Set schedule
        holder.scheduleText.setText(safeZone.getSchedule());

        // Set created date
        holder.createdText.setText("Created " + DateUtils.formatDisplayDate(safeZone.getCreatedAt()));

        // Set visit statistics
        if (safeZone.getVisitCount() > 0) {
            holder.visitCountText.setText(safeZone.getVisitCount() + " visits");
            holder.visitCountText.setVisibility(View.VISIBLE);

            if (safeZone.getLastEntered() != null) {
                holder.lastVisitText.setText("Last visit: " + DateUtils.getRelativeTimeString(safeZone.getLastEntered()));
                holder.lastVisitText.setVisibility(View.VISIBLE);
            } else {
                holder.lastVisitText.setVisibility(View.GONE);
            }
        } else {
            holder.visitCountText.setText("No visits yet");
            holder.visitCountText.setVisibility(View.VISIBLE);
            holder.lastVisitText.setVisibility(View.GONE);
        }

        // Set zone color and icon
        try {
            int color = Color.parseColor(safeZone.getColor());
            holder.colorIndicator.setBackgroundColor(color);
            holder.cardView.setStrokeColor(color);
        } catch (Exception e) {
            // Default color if parsing fails
            int defaultColor = ContextCompat.getColor(context, R.color.primary);
            holder.colorIndicator.setBackgroundColor(defaultColor);
        }

        // Set icon based on safe zone type
        setZoneIcon(holder.zoneIcon, safeZone.getIcon());

        // Set active status
        holder.activeSwitch.setChecked(safeZone.isActive());
        holder.activeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onToggleClickListener != null) {
                onToggleClickListener.onToggleClick(safeZone, isChecked);
            }
        });

        // Set alerts status
        if (safeZone.isAlertsEnabled()) {
            holder.alertsIcon.setImageResource(R.drawable.ic_notifications_active);
            holder.alertsIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary));
            holder.alertsText.setText("Alerts ON");
        } else {
            holder.alertsIcon.setImageResource(R.drawable.ic_notifications_off);
            holder.alertsIcon.setColorFilter(ContextCompat.getColor(context, android.R.color.darker_gray));
            holder.alertsText.setText("Alerts OFF");
        }

        // Set card appearance based on active status
        if (safeZone.isActive()) {
            holder.cardView.setAlpha(1.0f);
            holder.statusText.setText("Active");
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.interest_positive));
        } else {
            holder.cardView.setAlpha(0.6f);
            holder.statusText.setText("Inactive");
            holder.statusText.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        }

        // Set entry/exit notification status
        StringBuilder notificationStatus = new StringBuilder();
        if (safeZone.isEntryNotifications()) {
            notificationStatus.append("Entry alerts");
        }
        if (safeZone.isExitNotifications()) {
            if (notificationStatus.length() > 0) {
                notificationStatus.append(" • ");
            }
            notificationStatus.append("Exit alerts");
        }

        if (notificationStatus.length() > 0) {
            holder.notificationStatusText.setText(notificationStatus.toString());
            holder.notificationStatusText.setVisibility(View.VISIBLE);
        } else {
            holder.notificationStatusText.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.cardView.setOnClickListener(v -> {
            if (onSafeZoneClickListener != null) {
                onSafeZoneClickListener.onSafeZoneClick(safeZone);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            if (onSafeZoneClickListener != null) {
                onSafeZoneClickListener.onEditClick(safeZone);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onSafeZoneClickListener != null) {
                onSafeZoneClickListener.onDeleteClick(safeZone);
            }
        });

        holder.viewOnMapButton.setOnClickListener(v -> {
            if (onSafeZoneClickListener != null) {
                onSafeZoneClickListener.onViewOnMapClick(safeZone);
            }
        });
    }

    @Override
    public int getItemCount() {
        return safeZones.size();
    }

    public void updateSafeZones(List<SafeZone> newSafeZones) {
        safeZones.clear();
        if (newSafeZones != null) {
            safeZones.addAll(newSafeZones);
        }
        notifyDataSetChanged();
    }

    public void addSafeZone(SafeZone safeZone) {
        safeZones.add(0, safeZone);
        notifyItemInserted(0);
    }

    public void removeSafeZone(String safeZoneId) {
        for (int i = 0; i < safeZones.size(); i++) {
            if (safeZones.get(i).getId().equals(safeZoneId)) {
                safeZones.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void updateSafeZone(SafeZone updatedSafeZone) {
        for (int i = 0; i < safeZones.size(); i++) {
            if (safeZones.get(i).getId().equals(updatedSafeZone.getId())) {
                safeZones.set(i, updatedSafeZone);
                notifyItemChanged(i);
                break;
            }
        }
    }

    private void setZoneIcon(ImageView iconView, String iconType) {
        switch (iconType != null ? iconType : "home") {
            case "home":
                iconView.setImageResource(R.drawable.ic_home);
                break;
            case "school":
                iconView.setImageResource(R.drawable.ic_school);
                break;
            case "playground":
                iconView.setImageResource(R.drawable.ic_playground);
                break;
            case "hospital":
                iconView.setImageResource(R.drawable.ic_hospital);
                break;
            case "mall":
                iconView.setImageResource(R.drawable.ic_shopping);
                break;
            case "park":
                iconView.setImageResource(R.drawable.ic_park);
                break;
            case "friend":
                iconView.setImageResource(R.drawable.ic_people);
                break;
            default:
                iconView.setImageResource(R.drawable.ic_location);
                break;
        }
    }

    static class SafeZoneViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView nameText;
        TextView addressText;
        TextView radiusText;
        TextView coordinatesText;
        TextView scheduleText;
        TextView createdText;
        TextView visitCountText;
        TextView lastVisitText;
        TextView statusText;
        TextView alertsText;
        TextView notificationStatusText;
        ImageView zoneIcon;
        ImageView alertsIcon;
        View colorIndicator;
        SwitchMaterial activeSwitch;
        MaterialButton editButton;
        MaterialButton deleteButton;
        MaterialButton viewOnMapButton;

        SafeZoneViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.safe_zone_card);
            nameText = itemView.findViewById(R.id.zone_name);
            addressText = itemView.findViewById(R.id.zone_address);
            radiusText = itemView.findViewById(R.id.zone_radius);
            coordinatesText = itemView.findViewById(R.id.zone_coordinates);
            scheduleText = itemView.findViewById(R.id.zone_schedule);
            createdText = itemView.findViewById(R.id.zone_created);
            visitCountText = itemView.findViewById(R.id.visit_count);
            lastVisitText = itemView.findViewById(R.id.last_visit);
            statusText = itemView.findViewById(R.id.status_text);
            alertsText = itemView.findViewById(R.id.alerts_text);
            notificationStatusText = itemView.findViewById(R.id.notification_status);
            zoneIcon = itemView.findViewById(R.id.zone_icon);
            alertsIcon = itemView.findViewById(R.id.alerts_icon);
            colorIndicator = itemView.findViewById(R.id.color_indicator);
            activeSwitch = itemView.findViewById(R.id.active_switch);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            viewOnMapButton = itemView.findViewById(R.id.view_on_map_button);
        }
    }

    // Click listener interfaces
    public interface OnSafeZoneClickListener {
        void onSafeZoneClick(SafeZone safeZone);
        void onEditClick(SafeZone safeZone);
        void onDeleteClick(SafeZone safeZone);
        void onViewOnMapClick(SafeZone safeZone);
    }

    public interface OnToggleClickListener {
        void onToggleClick(SafeZone safeZone, boolean isEnabled);
    }

    public void setOnSafeZoneClickListener(OnSafeZoneClickListener listener) {
        this.onSafeZoneClickListener = listener;
    }

    public void setOnToggleClickListener(OnToggleClickListener listener) {
        this.onToggleClickListener = listener;
    }
}