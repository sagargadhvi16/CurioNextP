package com.curionext.mobile.features.dashboard;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.curionext.mobile.CurioNextApplication;
import com.curionext.mobile.R;
import com.curionext.mobile.core.data.model.Child;
import com.curionext.mobile.core.data.model.Interest;
import com.curionext.mobile.core.data.model.NotificationItem;
import com.curionext.mobile.core.data.model.WeeklySummary;
import com.curionext.mobile.features.dashboard.adapter.DashboardAdapter;
import com.curionext.mobile.features.dashboard.viewmodel.DashboardViewModel;
import com.curionext.mobile.features.dashboard.viewmodel.DashboardViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class DashboardActivity extends AppCompatActivity {

    @Inject
    DashboardViewModelFactory viewModelFactory;

    private DashboardViewModel viewModel;
    private MaterialToolbar toolbar;
    private MaterialTextView greetingText;
    private MaterialTextView dateText;
    private MaterialTextView summaryText;
    private MaterialCardView headerCard;
    private RecyclerView dashboardRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DashboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Inject dependencies
        ((CurioNextApplication) getApplication()).getAppComponent().inject(this);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this, viewModelFactory).get(DashboardViewModel.class);

        initViews();
        setupRecyclerView();
        observeViewModel();

        // Load initial data
        viewModel.loadDashboardData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        greetingText = findViewById(R.id.greeting_text);
        dateText = findViewById(R.id.date_text);
        summaryText = findViewById(R.id.summary_text);
        headerCard = findViewById(R.id.header_card);
        dashboardRecyclerView = findViewById(R.id.dashboard_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        setSupportActionBar(toolbar);

        // Set up swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshDashboardData();
        });

        // Set greeting and date
        updateGreetingAndDate();
    }

    private void setupRecyclerView() {
        adapter = new DashboardAdapter(this);
        dashboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dashboardRecyclerView.setAdapter(adapter);

        // Set item click listeners
        adapter.setOnInterestClickListener(interest -> {
            // Navigate to interest details
            viewModel.navigateToInterestDetails(interest.getId());
        });

        adapter.setOnNotificationClickListener(notification -> {
            // Handle notification click
            viewModel.markNotificationAsRead(notification.getId());
        });

        adapter.setOnWeeklySummaryClickListener(summary -> {
            // Play weekly summary
            viewModel.playWeeklySummary(summary.getId());
        });
    }

    private void observeViewModel() {
        // Observe child profile
        viewModel.getChildProfile().observe(this, this::updateChildProfile);

        // Observe interests
        viewModel.getInterests().observe(this, this::updateInterests);

        // Observe notifications
        viewModel.getNotifications().observe(this, this::updateNotifications);

        // Observe weekly summary
        viewModel.getWeeklySummary().observe(this, this::updateWeeklySummary);

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            swipeRefreshLayout.setRefreshing(isLoading);
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // Show error message
                showErrorMessage(errorMessage);
            }
        });
    }

    private void updateChildProfile(Child child) {
        if (child != null) {
            // Update header with child information
            String childName = child.getName();
            summaryText.setText(String.format(
                    "%s has explored 3 new topics this week, including a new interest in painting",
                    childName
            ));
        }
    }

    private void updateInterests(List<Interest> interests) {
        adapter.updateInterests(interests);
    }

    private void updateNotifications(List<NotificationItem> notifications) {
        adapter.updateNotifications(notifications);
    }

    private void updateWeeklySummary(WeeklySummary summary) {
        adapter.updateWeeklySummary(summary);
    }

    private void updateGreetingAndDate() {
        Date now = new Date();
        int hour = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(now));

        String greeting;
        if (hour < 12) {
            greeting = "Good morning, Swati";
        } else if (hour < 18) {
            greeting = "Good afternoon, Swati";
        } else {
            greeting = "Good evening, Swati";
        }

        greetingText.setText(greeting);

        String dateString = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now);
        dateText.setText(dateString);
    }

    private void showErrorMessage(String message) {
        // Implement error message display (Snackbar, Toast, etc.)
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to dashboard
        viewModel.refreshDashboardData();
    }
}
