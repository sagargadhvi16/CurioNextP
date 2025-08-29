package com.curionext.mobile.features.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curionext.mobile.CurioNextApplication;
import com.curionext.mobile.R;
import com.curionext.mobile.core.data.model.LocationData;
import com.curionext.mobile.core.data.model.SafeZone;
import com.curionext.mobile.features.location.adapter.LocationHistoryAdapter;
import com.curionext.mobile.features.location.adapter.SafeZoneAdapter;
import com.curionext.mobile.features.location.viewmodel.LocationViewModel;
import com.curionext.mobile.features.location.viewmodel.LocationViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class LocationTrackingActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Inject
    LocationViewModelFactory viewModelFactory;

    private LocationViewModel viewModel;
    private MaterialToolbar toolbar;
    private SwitchMaterial trackingSwitch;
    private MaterialTextView currentLocationText;
    private MaterialTextView lastUpdatedText;
    private MaterialCardView currentLocationCard;
    private TabLayout tabLayout;
    private RecyclerView contentRecyclerView;
    private MaterialButton refreshButton;

    private SafeZoneAdapter safeZoneAdapter;
    private LocationHistoryAdapter locationHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracking);

        // Inject dependencies
        ((CurioNextApplication) getApplication()).getAppComponent().inject(this);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this, viewModelFactory).get(LocationViewModel.class);

        initViews();
        setupTabs();
        setupRecyclerView();
        observeViewModel();

        // Check permissions and load data
        checkLocationPermissions();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        trackingSwitch = findViewById(R.id.tracking_switch);
        currentLocationText = findViewById(R.id.current_location_text);
        lastUpdatedText = findViewById(R.id.last_updated_text);
        currentLocationCard = findViewById(R.id.current_location_card);
        tabLayout = findViewById(R.id.tab_layout);
        contentRecyclerView = findViewById(R.id.content_recycler_view);
        refreshButton = findViewById(R.id.refresh_button);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Location Tracking");
        }

        // Set up tracking switch
        trackingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setTrackingEnabled(isChecked);
        });

        // Set up refresh button
        refreshButton.setOnClickListener(v -> viewModel.refreshCurrentLocation());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Live Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Safe Zones"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.addTab(tabLayout.newTab().setText("Alerts"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Live Map
                        showLiveMap();
                        break;
                    case 1: // Safe Zones
                        showSafeZones();
                        break;
                    case 2: // History
                        showLocationHistory();
                        break;
                    case 3: // Alerts
                        showAlerts();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapters
        safeZoneAdapter = new SafeZoneAdapter(this);
        locationHistoryAdapter = new LocationHistoryAdapter(this);

        // Set click listeners
        safeZoneAdapter.setOnSafeZoneClickListener(safeZone -> {
            viewModel.editSafeZone(safeZone.getId());
        });

        safeZoneAdapter.setOnToggleClickListener((safeZone, isEnabled) -> {
            viewModel.toggleSafeZone(safeZone.getId(), isEnabled);
        });

        locationHistoryAdapter.setOnLocationClickListener(location -> {
            viewModel.showLocationOnMap(location);
        });
    }

    private void observeViewModel() {
        // Observe current location
        viewModel.getCurrentLocation().observe(this, this::updateCurrentLocation);

        // Observe tracking state
        viewModel.getIsTrackingEnabled().observe(this, isEnabled -> {
            trackingSwitch.setChecked(isEnabled);
        });

        // Observe safe zones
        viewModel.getSafeZones().observe(this, this::updateSafeZones);

        // Observe location history
        viewModel.getLocationHistory().observe(this, this::updateLocationHistory);

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            refreshButton.setEnabled(!isLoading);
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showErrorMessage(errorMessage);
            }
        });
    }

    private void updateCurrentLocation(LocationData location) {
        if (location != null) {
            currentLocationText.setText(location.getAddress());

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            lastUpdatedText.setText("Last updated: " + timeFormat.format(location.getTimestamp()));

            // Update safe zone status
            if (location.isInSafeZone()) {
                currentLocationCard.setCardBackgroundColor(
                        ContextCompat.getColor(this, R.color.safe_zone_background)
                );
            } else {
                currentLocationCard.setCardBackgroundColor(
                        ContextCompat.getColor(this, R.color.outside_safe_zone_background)
                );
            }
        }
    }

    private void updateSafeZones(List<SafeZone> safeZones) {
        safeZoneAdapter.updateSafeZones(safeZones);
    }

    private void updateLocationHistory(List<LocationData> locationHistory) {
        locationHistoryAdapter.updateLocationHistory(locationHistory);
    }

    private void showLiveMap() {
        // Show map view (implement with Google Maps or similar)
        contentRecyclerView.setVisibility(View.GONE);
        // Show map fragment or view
    }

    private void showSafeZones() {
        contentRecyclerView.setVisibility(View.VISIBLE);
        contentRecyclerView.setAdapter(safeZoneAdapter);
        viewModel.loadSafeZones();
    }

    private void showLocationHistory() {
        contentRecyclerView.setVisibility(View.VISIBLE);
        contentRecyclerView.setAdapter(locationHistoryAdapter);
        viewModel.loadLocationHistory();
    }

    private void showAlerts() {
        // Show alerts view
        contentRecyclerView.setVisibility(View.VISIBLE);
        // Implement alerts adapter and view
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            viewModel.startLocationTracking();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.startLocationTracking();
            } else {
                showErrorMessage("Location permission is required for tracking");
            }
        }
    }

    private void showErrorMessage(String message) {
        // Implement error message display
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stopLocationTracking();
    }
}
