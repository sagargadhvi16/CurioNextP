package com.curionext.mobile.features.notifications.adapter;

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
import com.curionext.mobile.core.data.model.NotificationItem;
import com.curionext.mobile.core.util.Constants;
import com.curionext.mobile.core.util.DateUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private final List<NotificationItem> notifications = new ArrayList<>();

    private OnNotificationClickListener onNotificationClickListener;

    public NotificationAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification_full, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);

        // Set basic content
        holder.titleText.setText(notification.getTitle());
        holder.descriptionText.setText(notification.getDescription());

        // Set time
        holder.timeText.setText(DateUtils.getChildFriendlyTime(notification.getTimestamp()));

        // Set type and category
        holder.typeText.setText(notification.getType().toUpperCase());
        holder.categoryText.setText(notification.getCategory());

        // Set priority indicator and styling
        setPriorityIndicator(holder, notification.getPriority());

        // Set notification type icon
        setNotificationTypeIcon(holder.typeIcon, notification.getType());

        // Set category color
        setCategoryColor(holder, notification.getCategory());

        // Set read/unread status
        setReadStatus(holder, notification);

        // Handle action button
        if (notification.getActionUrl() != null && !notification.getActionUrl().isEmpty()) {
            holder.actionButton.setVisibility(View.VISIBLE);
            holder.actionButton.setText(getActionButtonText(notification.getType()));
            holder.actionButton.setOnClickListener(v -> {
                if (onNotificationClickListener != null) {
                    onNotificationClickListener.onActionClick(notification);
                }
            });
        } else {
            holder.actionButton.setVisibility(View.GONE);
        }

        // Set metadata if available
        if (notification.getMetadata() != null && !notification.getMetadata().isEmpty()) {
            holder.metadataText.setText(parseMetadata(notification.getMetadata()));
            holder.metadataText.setVisibility(View.VISIBLE);
        } else {
            holder.metadataText.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.cardView.setOnClickListener(v -> {
            if (onNotificationClickListener != null) {
                onNotificationClickListener.onNotificationClick(notification);
            }
        });

        holder.cardView.setOnLongClickListener(v -> {
            if (onNotificationClickListener != null) {
                onNotificationClickListener.onNotificationLongClick(notification);
            }
            return true;
        });

        holder.dismissButton.setOnClickListener(v -> {
            if (onNotificationClickListener != null) {
                onNotificationClickListener.onDismissClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateNotifications(List<NotificationItem> newNotifications) {
        notifications.clear();
        if (newNotifications != null) {
            notifications.addAll(newNotifications);
        }
        notifyDataSetChanged();
    }

    public void addNotification(NotificationItem notification) {
        notifications.add(0, notification); // Add at beginning
        notifyItemInserted(0);
    }

    public void removeNotification(String notificationId) {
        for (int i = 0; i < notifications.size(); i++) {
            if (notifications.get(i).getId().equals(notificationId)) {
                notifications.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void markAsRead(String notificationId) {
        for (int i = 0; i < notifications.size(); i++) {
            NotificationItem notification = notifications.get(i);
            if (notification.getId().equals(notificationId)) {
                notification.setRead(true);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void clearAllNotifications() {
        notifications.clear();
        notifyDataSetChanged();
    }

    private void setPriorityIndicator(NotificationViewHolder holder, String priority) {
        switch (priority) {
            case Constants.PRIORITY_URGENT:
                holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.error));
                holder.priorityText.setText("URGENT");
                holder.priorityText.setTextColor(ContextCompat.getColor(context, R.color.error));
                holder.priorityIcon.setImageResource(R.drawable.ic_priority_urgent);
                break;
            case Constants.PRIORITY_HIGH:
                holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
                holder.priorityText.setText("HIGH");
                holder.priorityText.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
                holder.priorityIcon.setImageResource(R.drawable.ic_priority_high);
                break;
            case Constants.PRIORITY_MEDIUM:
                holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                holder.priorityText.setText("MEDIUM");
                holder.priorityText.setTextColor(ContextCompat.getColor(context, R.color.primary));
                holder.priorityIcon.setImageResource(R.drawable.ic_priority_normal);
                break;
            default: // LOW
                holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                holder.priorityText.setText("LOW");
                holder.priorityText.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                holder.priorityIcon.setImageResource(R.drawable.ic_priority_low);
                break;
        }
    }

    private void setNotificationTypeIcon(ImageView iconView, String type) {
        switch (type) {
            case Constants.NOTIFICATION_TYPE_INTEREST:
                iconView.setImageResource(R.drawable.ic_interests);
                break;
            case Constants.NOTIFICATION_TYPE_MILESTONE:
                iconView.setImageResource(R.drawable.ic_milestone);
                break;
            case Constants.NOTIFICATION_TYPE_ALERT:
                iconView.setImageResource(R.drawable.ic_alert);
                break;
            case Constants.NOTIFICATION_TYPE_SUMMARY:
                iconView.setImageResource(R.drawable.ic_summary);
                break;
            case Constants.NOTIFICATION_TYPE_ACTIVITY:
                iconView.setImageResource(R.drawable.ic_activity);
                break;
            case Constants.NOTIFICATION_TYPE_SYSTEM:
                iconView.setImageResource(R.drawable.ic_system);
                break;
            default:
                iconView.setImageResource(R.drawable.ic_notifications);
                break;
        }
    }

    private void setCategoryColor(NotificationViewHolder holder, String category) {
        int color;
        switch (category) {
            case Constants.NOTIFICATION_CATEGORY_LEARNING:
                color = ContextCompat.getColor(context, R.color.interest_positive);
                break;
            case Constants.NOTIFICATION_CATEGORY_SAFETY:
                color = ContextCompat.getColor(context, R.color.error);
                break;
            case Constants.NOTIFICATION_CATEGORY_REPORT:
                color = ContextCompat.getColor(context, R.color.primary);
                break;
            case Constants.NOTIFICATION_CATEGORY_SYSTEM:
            default:
                color = ContextCompat.getColor(context, android.R.color.darker_gray);
                break;
        }

        holder.categoryIndicator.setBackgroundColor(color);
        holder.categoryText.setTextColor(color);
    }

    private void setReadStatus(NotificationViewHolder holder, NotificationItem notification) {
        if (notification.isRead()) {
            holder.unreadIndicator.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.white)
            );
            holder.cardView.setAlpha(0.8f);
        } else {
            holder.unreadIndicator.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.notification_unread_background)
            );
            holder.cardView.setAlpha(1.0f);
        }
    }

    private String getActionButtonText(String type) {
        switch (type) {
            case Constants.NOTIFICATION_TYPE_INTEREST:
                return "Explore";
            case Constants.NOTIFICATION_TYPE_MILESTONE:
                return "View";
            case Constants.NOTIFICATION_TYPE_ALERT:
                return "Check";
            case Constants.NOTIFICATION_TYPE_SUMMARY:
                return "Read";
            case Constants.NOTIFICATION_TYPE_ACTIVITY:
                return "Join";
            default:
                return "View";
        }
    }

    private String parseMetadata(String metadata) {
        // Simple metadata parsing - in real app you'd use JSON parsing
        if (metadata.contains("score")) {
            return "Engagement Score: " + extractValue(metadata, "score");
        } else if (metadata.contains("location")) {
            return "Location: " + extractValue(metadata, "location");
        } else if (metadata.contains("duration")) {
            return "Duration: " + extractValue(metadata, "duration");
        }
        return ""; // Hide if can't parse
    }

    private String extractValue(String metadata, String key) {
        try {
            // Simple key-value extraction from JSON-like string
            int keyIndex = metadata.indexOf("\"" + key + "\":");
            if (keyIndex != -1) {
                int valueStart = metadata.indexOf("\"", keyIndex + key.length() + 3);
                int valueEnd = metadata.indexOf("\"", valueStart + 1);
                if (valueStart != -1 && valueEnd != -1) {
                    return metadata.substring(valueStart + 1, valueEnd);
                }
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return "";
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView titleText;
        TextView descriptionText;
        TextView timeText;
        TextView typeText;
        TextView categoryText;
        TextView priorityText;
        TextView metadataText;
        ImageView typeIcon;
        ImageView priorityIcon;
        View unreadIndicator;
        View priorityIndicator;
        View categoryIndicator;
        MaterialButton actionButton;
        MaterialButton dismissButton;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.notification_card);
            titleText = itemView.findViewById(R.id.notification_title);
            descriptionText = itemView.findViewById(R.id.notification_description);
            timeText = itemView.findViewById(R.id.notification_time);
            typeText = itemView.findViewById(R.id.notification_type);
            categoryText = itemView.findViewById(R.id.notification_category);
            priorityText = itemView.findViewById(R.id.priority_text);
            metadataText = itemView.findViewById(R.id.metadata_text);
            typeIcon = itemView.findViewById(R.id.type_icon);
            priorityIcon = itemView.findViewById(R.id.priority_icon);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);
            categoryIndicator = itemView.findViewById(R.id.category_indicator);
            actionButton = itemView.findViewById(R.id.action_button);
            dismissButton = itemView.findViewById(R.id.dismiss_button);
        }
    }

    // Click listener interface
    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem notification);
        void onNotificationLongClick(NotificationItem notification);
        void onActionClick(NotificationItem notification);
        void onDismissClick(NotificationItem notification);
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.onNotificationClickListener = listener;
    }

    // Utility methods
    public int getUnreadCount() {
        return (int) notifications.stream()
                .filter(notification -> !notification.isRead())
                .count();
    }

    public List<NotificationItem> getUnreadNotifications() {
        return notifications.stream()
                .filter(notification -> !notification.isRead())
                .collect(java.util.stream.Collectors.toList());
    }

    public void markAllAsRead() {
        for (NotificationItem notification : notifications) {
            notification.setRead(true);
        }
        notifyDataSetChanged();
    }
}