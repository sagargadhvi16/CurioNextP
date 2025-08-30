package com.curionext.mobile.features.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.curionext.mobile.R;
import com.curionext.mobile.core.data.model.Interest;
import com.curionext.mobile.core.data.model.NotificationItem;
import com.curionext.mobile.core.data.model.WeeklySummary;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_WEEKLY_SUMMARY = 0;
    private static final int TYPE_INTEREST = 1;
    private static final int TYPE_NOTIFICATION = 2;
    private static final int TYPE_HEADER = 3;

    private final Context context;
    private final List<Object> items = new ArrayList<>();

    private OnInterestClickListener onInterestClickListener;
    private OnNotificationClickListener onNotificationClickListener;
    private OnWeeklySummaryClickListener onWeeklySummaryClickListener;

    public DashboardAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof WeeklySummary) return TYPE_WEEKLY_SUMMARY;
        if (item instanceof Interest) return TYPE_INTEREST;
        if (item instanceof NotificationItem) return TYPE_NOTIFICATION;
        if (item instanceof String) return TYPE_HEADER;
        return TYPE_HEADER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case TYPE_WEEKLY_SUMMARY:
                return new WeeklySummaryViewHolder(inflater.inflate(R.layout.item_weekly_summary, parent, false));
            case TYPE_INTEREST:
                return new InterestViewHolder(inflater.inflate(R.layout.item_interest_card, parent, false));
            case TYPE_NOTIFICATION:
                return new NotificationViewHolder(inflater.inflate(R.layout.item_notification_card, parent, false));
            case TYPE_HEADER:
            default:
                return new HeaderViewHolder(inflater.inflate(R.layout.item_section_header, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);

        switch (holder.getItemViewType()) {
            case TYPE_WEEKLY_SUMMARY:
                bindWeeklySummary((WeeklySummaryViewHolder) holder, (WeeklySummary) item);
                break;
            case TYPE_INTEREST:
                bindInterest((InterestViewHolder) holder, (Interest) item);
                break;
            case TYPE_NOTIFICATION:
                bindNotification((NotificationViewHolder) holder, (NotificationItem) item);
                break;
            case TYPE_HEADER:
                bindHeader((HeaderViewHolder) holder, (String) item);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Update methods
    public void updateWeeklySummary(WeeklySummary summary) {
        if (summary != null) {
            // Remove existing summary if present
            items.removeIf(item -> item instanceof WeeklySummary);

            // Add header and summary at the beginning
            items.add(0, "Weekly Summary");
            items.add(1, summary);
            notifyDataSetChanged();
        }
    }

    public void updateInterests(List<Interest> interests) {
        if (interests != null && !interests.isEmpty()) {
            // Remove existing interests and header
            items.removeIf(item -> item instanceof Interest || "Recent Interests".equals(item));

            // Add header and top 3 interests
            int insertPosition = findInsertPosition("Recent Interests");
            items.add(insertPosition, "Recent Interests");

            List<Interest> topThree = interests.subList(0, Math.min(3, interests.size()));
            for (int i = 0; i < topThree.size(); i++) {
                items.add(insertPosition + 1 + i, topThree.get(i));
            }

            notifyDataSetChanged();
        }
    }

    public void updateNotifications(List<NotificationItem> notifications) {
        if (notifications != null && !notifications.isEmpty()) {
            // Remove existing notifications and header
            items.removeIf(item -> item instanceof NotificationItem || "Recent Updates".equals(item));

            // Add header and recent notifications
            int insertPosition = findInsertPosition("Recent Updates");
            items.add(insertPosition, "Recent Updates");

            List<NotificationItem> recentNotifications = notifications.subList(0, Math.min(3, notifications.size()));
            for (int i = 0; i < recentNotifications.size(); i++) {
                items.add(insertPosition + 1 + i, recentNotifications.get(i));
            }

            notifyDataSetChanged();
        }
    }

    private int findInsertPosition(String header) {
        // Find appropriate position to insert new section
        if (items.isEmpty()) return 0;

        // Insert at end by default
        return items.size();
    }

    // ViewHolder classes
    static class WeeklySummaryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView titleText;
        TextView summaryText;
        TextView durationText;
        MaterialButton playButton;
        MaterialButton viewDetailsButton;

        WeeklySummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.weekly_summary_card);
            titleText = itemView.findViewById(R.id.summary_title);
            summaryText = itemView.findViewById(R.id.summary_text);
            durationText = itemView.findViewById(R.id.duration_text);
            playButton = itemView.findViewById(R.id.play_button);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
        }
    }

    static class InterestViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView topicText;
        TextView categoryText;
        TextView levelText;
        TextView trendText;
        ImageView trendIcon;
        View levelIndicator;

        InterestViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.interest_card);
            topicText = itemView.findViewById(R.id.interest_topic);
            categoryText = itemView.findViewById(R.id.interest_category);
            levelText = itemView.findViewById(R.id.interest_level);
            trendText = itemView.findViewById(R.id.trend_text);
            trendIcon = itemView.findViewById(R.id.trend_icon);
            levelIndicator = itemView.findViewById(R.id.level_indicator);
        }
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView titleText;
        TextView descriptionText;
        TextView timeText;
        TextView typeText;
        ImageView priorityIcon;
        View unreadIndicator;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.notification_card);
            titleText = itemView.findViewById(R.id.notification_title);
            descriptionText = itemView.findViewById(R.id.notification_description);
            timeText = itemView.findViewById(R.id.notification_time);
            typeText = itemView.findViewById(R.id.notification_type);
            priorityIcon = itemView.findViewById(R.id.priority_icon);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;

        HeaderViewHolder(@NonNull View itemView) {