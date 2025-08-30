package com.curionext.mobile.features.preferences;

import android.graphics.Color;
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
import com.curionext.mobile.core.data.model.Preference;
import com.curionext.mobile.features.preferences.adapter.PreferenceAdapter;
import com.curionext.mobile.features.preferences.viewmodel.PreferenceViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PreferenceAnalysisActivity extends AppCompatActivity {

    @Inject
    PreferenceViewModel viewModel;

    private MaterialToolbar toolbar;
    private MaterialTextView summaryText;
    private MaterialTextView likesCountText;
    private MaterialTextView dislikesCountText;
    private MaterialTextView neutralCountText;
    private MaterialTextView averageSentimentText;
    private MaterialTextView averageConfidenceText;
    private MaterialCardView summaryCard;
    private MaterialCardView sentimentCard;
    private ChipGroup filterChipGroup;
    private RecyclerView preferencesRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton exportFab;
    private MaterialButton generateReportButton;
    private MaterialButton viewTrendsButton;
    private CircularProgressIndicator sentimentIndicator;
    private CircularProgressIndicator confidenceIndicator;

    private PreferenceAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_analysis);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);

        initViews();
        setupRecyclerView();
        setupFilterChips();
        observeViewModel();

        // Load initial data
        viewModel.loadAllPreferences();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        summaryText = findViewById(R.id.summary_text);
        likesCountText = findViewById(R.id.likes_count_text);
        dislikesCountText = findViewById(R.id.dislikes_count_text);
        neutralCountText = findViewById(R.id.neutral_count_text);
        averageSentimentText = findViewById(R.id.average_sentiment_text);
        averageConfidenceText = findViewById(R.id.average_confidence_text);
        summaryCard = findViewById(R.id.summary_card);
        sentimentCard = findViewById(R.id.sentiment_card);
        filterChipGroup = findViewById(R.id.filter_chip_group);
        preferencesRecyclerView = findViewById(R.id.preferences_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        exportFab = findViewById(R.id.export_fab);
        generateReportButton = findViewById(R.id.generate_report_button);
        viewTrendsButton = findViewById(R.id.view_trends_button);
        sentimentIndicator = findViewById(R.id.sentiment_indicator);
        confidenceIndicator = findViewById(R.id.confidence_indicator);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Preference Analysis");
        }

        // Set up swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshPreferences();
        });

        // Set up button click listeners
        exportFab.setOnClickListener(v -> {
            viewModel.exportPreferenceData();
            Toast.makeText(this, "Exporting preference data...", Toast.LENGTH_SHORT).show();
        });

        generateReportButton.setOnClickListener(v -> {
            viewModel.generatePreferenceReport();
        });

        viewTrendsButton.setOnClickListener(v -> {
            showSentimentTrends();
        });
    }

    private void setupRecyclerView() {
        adapter = new PreferenceAdapter(this);
        preferencesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        preferencesRecyclerView.setAdapter(adapter);

        // Set item click listener
        adapter.setOnPreferenceClickListener(preference -> {
            viewModel.onPreferenceClicked(preference);
            showPreferenceDetails(preference);
        });
    }

    private void setupFilterChips() {
        String[] filters = {"All", "Likes", "Dislikes", "Neutral", "High Confidence", "Strong"};

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
                viewModel.setFilter(filter);
            }
        });
    }

    private void observeViewModel() {
        // Observe all preferences
        viewModel.getAllPreferences().observe(this, preferences -> {
            if (preferences != null) {
                adapter.updatePreferences(preferences);
                updateSummaryCard(preferences);
            }
        });

        // Observe likes
        viewModel.getLikes().observe(this, likes -> {
            if (likes != null) {
                likesCountText.setText(String.valueOf(likes.size()));
            }
        });

        // Observe dislikes
        viewModel.getDislikes().observe(this, dislikes -> {
            if (dislikes != null) {
                dislikesCountText.setText(String.valueOf(dislikes.size()));
            }
        });

        // Observe neutral preferences
        viewModel.getNeutralPreferences().observe(this, neutral -> {
            if (neutral != null) {
                neutralCountText.setText(String.valueOf(neutral.size()));
            }
        });

        // Observe average sentiment
        viewModel.getAverageSentiment().observe(this, avgSentiment -> {
            if (avgSentiment != null) {
                averageSentimentText.setText(String.format("%.2f", avgSentiment));
                updateSentimentIndicator(avgSentiment);
            }
        });

        // Observe average confidence
        viewModel.getAverageConfidence().observe(this, avgConfidence -> {
            if (avgConfidence != null) {
                averageConfidenceText.setText(String.format("%.1f%%", avgConfidence * 100));
                updateConfidenceIndicator(avgConfidence);
            }
        });

        // Observe categories
        viewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                updateCategoryDistribution(categories);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            swipeRefreshLayout.setRefreshing(isLoading);
            exportFab.setEnabled(!isLoading);
            generateReportButton.setEnabled(!isLoading);
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

        // Observe sentiment trends
        viewModel.getSentimentTrends().observe(this, trends -> {
            if (trends != null) {
                displaySentimentTrends(trends);
            }
        });
    }

    private void updateSummaryCard(List<Preference> preferences) {
        int total = preferences.size();
        long likes = preferences.stream().filter(Preference::isPositive).count();
        long dislikes = preferences.stream().filter(Preference::isNegative).count();
        long neutral = preferences.stream().filter(Preference::isNeutral).count();

        String summaryMessage = String.format(
                "Avani has expressed preferences about %d topics. " +
                        "She shows strong positive feelings toward %d topics and dislikes %d topics. " +
                        "%d topics remain neutral.",
                total, likes, dislikes, neutral
        );

        summaryText.setText(summaryMessage);
    }

    private void updateSentimentIndicator(double sentiment) {
        // Convert sentiment (-1 to +1) to progress (0 to 100)
        int progress = (int) ((sentiment + 1.0) / 2.0 * 100);
        sentimentIndicator.setProgress(progress);

        // Set color based on sentiment
        if (sentiment > 0.3) {
            sentimentIndicator.setIndicatorColor(getColor(R.color.interest_positive));
        } else if (sentiment < -0.3) {
            sentimentIndicator.setIndicatorColor(getColor(R.color.interest_negative));
        } else {
            sentimentIndicator.setIndicatorColor(getColor(R.color.interest_neutral));
        }
    }

    private void updateConfidenceIndicator(double confidence) {
        int progress = (int) (confidence * 100);
        confidenceIndicator.setProgress(progress);

        // Set color based on confidence level
        if (confidence >= 0.8) {
            confidenceIndicator.setIndicatorColor(getColor(R.color.interest_positive));
        } else if (confidence >= 0.5) {
            confidenceIndicator.setIndicatorColor(getColor(R.color.primary));
        } else {
            confidenceIndicator.setIndicatorColor(getColor(R.color.interest_negative));
        }
    }

    private void updateCategoryDistribution(List<String> categories) {
        // This could update a pie chart or other visualization
        // For now, just log the category count
        statusMessage.setValue("Found preferences in " + (categories.size() - 1) + " categories");
    }

    private void displaySentimentTrends(Map<String, Double> trends) {
        // Display sentiment trends - could be a chart or summary
        StringBuilder trendsText = new StringBuilder("Sentiment by category:\n");
        for (Map.Entry<String, Double> entry : trends.entrySet()) {
            trendsText.append(entry.getKey())
                    .append(": ")
                    .append(String.format("%.2f", entry.getValue()))
                    .append("\n");
        }

        Toast.makeText(this, trendsText.toString(), Toast.LENGTH_LONG).show();
    }

    private void showSentimentTrends() {
        Map<String, Double> trends = viewModel.getSentimentTrends().getValue();
        if (trends != null) {
            displaySentimentTrends(trends);
        } else {
            viewModel.loadPreferenceAnalysis();
        }
    }

    private void showPreferenceDetails(Preference preference) {
        // Show detailed information about the preference
        String details = String.format(
                "Topic: %s\nSentiment: %.2f\nConfidence: %.1f%%\nCategory: %s\nTrend: %s\nFrequency: %d times",
                preference.getTopic(),
                preference.getSentiment(),
                preference.getConfidence() * 100,
                preference.getCategory(),
                preference.getTrend(),
                preference.getFrequency()
        );

        Toast.makeText(this, details, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preference_analysis_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Search preferences...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.searchPreferences(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    viewModel.searchPreferences(newText);
                } else if (newText.isEmpty()) {
                    viewModel.loadAllPreferences(); // Reset to full list
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
            viewModel.refreshPreferences();
            return true;
        } else if (id == R.id.action_export) {
            viewModel.exportPreferenceData();
            return true;
        } else if (id == R.id.action_filter) {
            // Toggle filter chips visibility
            filterChipGroup.setVisibility(
                    filterChipGroup.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
            );
            return true;
        } else if (id == R.id.action_settings) {
            // Navigate to preference settings
            Toast.makeText(this, "Preference analysis settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        viewModel.refreshPreferences();
    }
}