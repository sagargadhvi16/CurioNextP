package com.curionext.mobile.features.interests;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.curionext.mobile.R;
import com.curionext.mobile.core.data.model.Interest;
import com.curionext.mobile.core.util.Constants;
import com.curionext.mobile.features.interests.adapter.InterestAdapter;
import com.curionext.mobile.features.interests.viewmodel.InterestViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InterestAnalysisActivity extends AppCompatActivity {

    @Inject
    InterestViewModel viewModel;

    private MaterialToolbar toolbar;
    private MaterialTextView summaryText;
    private MaterialTextView topInterestText;
    private MaterialTextView interestCountText;
    private MaterialCardView summaryCard;
    private ChipGroup categoryChipGroup;
    private RecyclerView interestsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton exportFab;
    private MaterialButton viewTrendsButton;
    private MaterialButton generateReportButton;

    private InterestAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_analysis);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(InterestViewModel.class);

        initViews();
        setupRecyclerView();
        setupChipGroup();
        observeViewModel();

        // Load initial data
        viewModel.loadAllInterests();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        summaryText = findViewById(R.id.summary_text);
        topInterestText = findViewById(R.id.top_interest_text);
        interestCountText = findViewById(R.id.interest_count_text);
        summaryCard = findViewById(R.id.summary_card);
        categoryChipGroup = findViewById(R.id.category_chip_group);
        interestsRecyclerView = findViewById(R.id.interests_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        exportFab = findViewById(R.id.export_fab);
        viewTrendsButton = findViewById(R.id.view_trends_button);
        generateReportButton = findViewById(R.id.generate_report_button);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Interest Analysis");
        }

        // Set up swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshInterestData();
        });

        // Set up button click listeners
        exportFab.setOnClickListener(v -> {
            viewModel.exportInterestData();
            Toast.makeText(this, "Exporting interest data...", Toast.LENGTH_SHORT).show();
        });

        viewTrendsButton.setOnClickListener(v -> {
            viewModel.loadInterestTrends();
            Toast.makeText(this, "Loading interest trends...", Toast.LENGTH_SHORT).show();
        });

        generateReportButton.setOnClickListener(v -> {
            viewModel.generateInterestReport();
        });
    }

    private void setupRecyclerView() {
        adapter = new InterestAdapter(this);
        interestsRecyclerView.setLayoutManager(
                new GridLayoutManager(this, Constants.INTEREST_GRID_COLUMNS)
        );
        interestsRecyclerView.setAdapter(adapter);

        // Set item click listeners
        adapter.setOnInterestClickListener(interest -> {
            viewModel.onInterestClicked(interest);
            // Navigate to interest details
            showInterestDetails(interest);
        });

        adapter.setOnInterestLongClickListener(interest -> {
            viewModel.onInterestLongPressed(interest);
            // Show context menu or additional options
            showInterestOptions(interest);
        });
    }

    private void setupChipGroup() {
        // Categories will be populated from ViewModel
        categoryChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip selectedChip = findViewById(checkedIds.get(0));
                String category = selectedChip.getText().toString();
                viewModel.selectCategory(category);
            }
        });
    }

    private void observeViewModel() {
        // Observe all interests
        viewModel.getAllInterests().observe(this, interests -> {
            if (interests != null) {
                adapter.updateInterests(interests);
                updateSummaryCard(interests);
            }
        });

        // Observe top interests
        viewModel.getTopInterests().observe(this, interests -> {
            if (interests != null && !interests.isEmpty()) {
                Interest topInterest = interests.get(0);
                topInterestText.setText("Top Interest: " + topInterest.getTopic());
            }
        });

        // Observe categories
        viewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                updateCategoryChips(categories);
            }
        });

        // Observe selected category
        viewModel.getSelectedCategory().observe(this, category -> {
            if (category != null) {
                selectCategoryChip(category);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            swipeRefreshLayout.setRefreshing(isLoading);
            exportFab.setEnabled(!isLoading);
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

        // Observe interest trends
        viewModel.getInterestTrends().observe(this, trends -> {
            if (trends != null && !trends.isEmpty()) {
                // Update UI with trend data
                updateTrendsDisplay(trends);
            }
        });
    }

    private void updateSummaryCard(List<Interest> interests) {
        int totalInterests = interests.size();
        interestCountText.setText(totalInterests + " interests discovered");

        // Calculate average interest level
        double avgLevel = interests.stream()
                .mapToDouble(Interest::getInterestLevel)
                .average()
                .orElse(0.0);

        // Count trending interests
        long trendingCount = interests.stream()
                .filter(interest -> Constants.TREND_INCREASING.equals(interest.getTrendDirection()))
                .count();

        String summaryMessage = String.format(
                "Avani has explored %d different topics with an average engagement level of %.1f. " +
                        "%d interests are currently trending upward.",
                totalInterests, avgLevel, trendingCount
        );

        summaryText.setText(summaryMessage);
    }

    private void updateCategoryChips(List<String> categories) {
        categoryChipGroup.removeAllViews();

        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());

            // Select "All" by default
            if ("All".equals(category)) {
                chip.setChecked(true);
            }

            categoryChipGroup.addView(chip);
        }
    }

    private void selectCategoryChip(String category) {
        for (int i = 0; i < categoryChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) categoryChipGroup.getChildAt(i);
            if (category.equals(chip.getText().toString())) {
                chip.setChecked(true);
                break;
            }
        }
    }

    private void updateTrendsDisplay(java.util.Map<String, Double> trends) {
        // This could update a trends chart or summary
        // For now, just show a toast with trending topics count
        int trendingTopics = trends.size();
        Toast.makeText(this,
                "Found trends for " + trendingTopics + " topics",
                Toast.LENGTH_SHORT).show();
    }

    private void showInterestDetails(Interest interest) {
        // Navigate to interest details activity or show bottom sheet
        Toast.makeText(this,
                "Showing details for: " + interest.getTopic(),
                Toast.LENGTH_SHORT).show();
    }

    private void showInterestOptions(Interest interest) {
        // Show context menu with options like share, bookmark, etc.
        Toast.makeText(this,
                "Options for: " + interest.getTopic(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.interest_analysis_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Search interests...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.searchInterests(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    viewModel.searchInterests(newText);
                } else if (newText.isEmpty()) {
                    viewModel.loadAllInterests(); // Reset to full list
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
            viewModel.refreshInterestData();
            return true;
        } else if (id == R.id.action_export) {
            viewModel.exportInterestData();
            return true;
        } else if (id == R.id.action_settings) {
            // Navigate to interest settings
            Toast.makeText(this, "Interest settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        viewModel.refreshInterestData();
    }
}