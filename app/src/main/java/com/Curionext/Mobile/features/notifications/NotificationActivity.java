package com.curionext.mobile.features.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.curionext.mobile.R;
import com.curionext.mobile.core.data.model.NotificationItem;
import com.curionext.mobile.core.util.Constants;
import com.curionext.mobile.features.notifications.adapter.NotificationAdapter;
import com.curionext.mobile.features.notifications.viewmodel.NotificationViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationActivity extends AppCompatActivity {

    @Inject
    NotificationViewModel viewModel;

    private MaterialToolbar toolbar;
    private MaterialTextView summaryText;
    private MaterialTextView unreadCountText;
    private MaterialCardView summaryCard;
    private ChipGroup filterChipGroup;
    private RecyclerView notificationsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialButton markAllReadButton;
    private MaterialButton clearAllButton;
    private View emptyView;
    private MaterialTextView emptyText;

    private NotificationAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        initViews();
        setupRecyclerView();
        setupFilterChips();
        observeViewModel();

        // Load initial data
        viewModel.loadNotifications();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        summaryText = findViewById(R.id.summary_text);
        unreadCountText = findViewById(R.id.unread_count_text);
        summaryCard = findViewById(R.id.summary_card);
        filterChipGroup = findViewById(R.id.filter_chip_group);
        notificationsRecyclerView = findViewById(R.id.notifications_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        markAllReadButton = findViewById(R.id.mark_all_read_button);
        clearAllButton = findViewById(R.id.clear_all_button);
        emptyView = findViewById(R.id.empty_view);
        emptyText = findViewById(R.id.empty_text);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }

        // Set up swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshNotifications();
        });

        // Set up button click listeners
        markAllReadButton.setOnClickListener(v -> {
            showMarkAllReadDialog();
        });

        clearAllButton.setOnClickListener(v -> {
            showClearAllDialog();
        });
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(this);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationsRecyclerView.setAdapter(adapter);

        // Set item click listeners
        adapter.setOnNotificationClickListener(new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClick(NotificationItem notification) {
                viewModel.markAsRead(notification.getId());
                handleNotificationAction(notification);
            }

            @Override
            public void onNotificationLongClick(NotificationItem notification) {
                showNotificationOptions(notification);
            }

            @Override
            public void onActionClick(NotificationItem notification) {
                handleNotificationAction(notification);
            }

            @Override
            public void onDismissClick(NotificationItem notification) {
                viewModel.dismissNotification(notification.getId());
            }
        });
    }

    private void setupFilterChips() {
        // Add filter chips for notification types
        String[] filters = {"All", "Learning", "Safety", "Reports", "System"};

        for (String filter : filters) {
            Chip chip = new Chip(this);
            chip.setText(filter);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());

            // Select "All" by default
            if ("All".equals(filter)) {
                chip.setChecked(true);
            }

            filterChipGroup.addView(chip);
        }

        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip selectedChip = findViewById(checkedIds.get(0));
                String filter = selectedChip.getText().toString();
                viewModel.filterNotifications(filter);
            }
        });
    }

    private void observeViewModel() {
        // Observe notifications
        viewModel.getNotifications().observe(this, notifications -> {
            if (notifications != null) {
                adapter.updateNotifications(notifications);
                updateEmptyView(notifications.isEmpty());
                updateSummaryCard(notifications);
            }
        });

        // Observe unread count
        viewModel.getUnreadCount().observe(this, unreadCount -> {
            if (unreadCount != null) {
                unreadCountText.setText(unreadCount + " unread");
                markAllReadButton.setEnabled(unreadCount > 0);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            swipeRefreshLayout.setRefreshing(isLoading);
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        // Observe status messages
        viewModel.getStatusMessage().observe(this, statusMessage -> {
            if (statusMessage != null && !statusMessage.isEmpty()) {
                Toast.makeText(this, statusMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSummaryCard(List<NotificationItem> notifications) {
        int total = notifications.size();
        int unread = adapter.getUnreadCount();

        String summaryMessage = String.format(
                "You have %d total notifications with %d unread. " +
                        "Stay updated on Avani's learning journey and safety.",
                total, unread
        );

        summaryText.setText(summaryMessage);
        clearAllButton.setEnabled(total > 0);
    }

    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            notificationsRecyclerView.setVisibility(View.GONE);
            emptyText.setText("No notifications yet.\nYou'll see updates about Avani's interests and activities here.");
        } else {
            emptyView.setVisibility(View.GONE);
            notificationsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void handleNotificationAction(NotificationItem notification) {
        String actionUrl = notification.getActionUrl();

        if (actionUrl != null && !actionUrl.isEmpty()) {
            if (actionUrl.startsWith("http")) {
                // Open web URL
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(actionUrl));
                startActivity(intent);
            } else {
                // Handle internal navigation
                handleInternalNavigation(actionUrl, notification);
            }
        } else {
            // Default action based on type
            handleDefaultAction(notification);
        }
    }

    private void handleInternalNavigation(String actionUrl, NotificationItem notification) {
        // Handle internal app navigation based on action URL pattern
        if (actionUrl.contains("interest")) {
            // Navigate to interest details
            Toast.makeText(this, "Opening interest details", Toast.LENGTH_SHORT).show();
        } else if (actionUrl.contains("location")) {
            // Navigate to location tracking
            Toast.makeText(this, "Opening location tracking", Toast.LENGTH_SHORT).show();
        } else if (actionUrl.contains("summary")) {
            // Navigate to weekly summary
            Toast.makeText(this, "Opening weekly summary", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDefaultAction(NotificationItem notification) {
        String type = notification.getType();
        switch (type) {
            case Constants.NOTIFICATION_TYPE_INTEREST:
                Toast.makeText(this, "Exploring new interest: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            case Constants.NOTIFICATION_TYPE_MILESTONE:
                Toast.makeText(this, "Viewing milestone details", Toast.LENGTH_SHORT).show();
                break;
            case Constants.NOTIFICATION_TYPE_ALERT:
                Toast.makeText(this, "Checking alert details", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Opening notification details", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showNotificationOptions(NotificationItem notification) {
        String[] options = {"Mark as read", "Share", "Delete"};

        new MaterialAlertDialogBuilder(this)
                .setTitle(notification.getTitle())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Mark as read
                            viewModel.markAsRead(notification.getId());
                            break;
                        case 1: // Share
                            shareNotification(notification);
                            break;
                        case 2: // Delete
                            viewModel.deleteNotification(notification.getId());
                            break;
                    }
                })
                .show();
    }

    private void showMarkAllReadDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Mark All as Read")
                .setMessage("This will mark all notifications as read. Continue?")
                .setPositiveButton("Mark All Read", (dialog, which) -> {
                    viewModel.markAllAsRead();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showClearAllDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Clear All Notifications")
                .setMessage("This will permanently delete all notifications. This action cannot be undone.")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    viewModel.clearAllNotifications();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void shareNotification(NotificationItem notification) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, notification.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                notification.getTitle() + "\n\n" + notification.getDescription());

        startActivity(Intent.createChooser(shareIntent, "Share notification"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Search notifications...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.searchNotifications(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    viewModel.searchNotifications(newText);
                } else if (newText.isEmpty()) {
                    viewModel.loadNotifications(); // Reset to full list
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_refresh) {
            viewModel.refreshNotifications();
            return true;
        } else if (id == R.id.action_filter) {
            // Toggle filter chips visibility
            filterChipGroup.setVisibility(
                    filterChipGroup.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
            );
            return true;
        } else if (id == R.id.action_settings) {
            // Navigate to notification settings
            Toast.makeText(this, "Notification settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        viewModel.refreshNotifications();
    }
}