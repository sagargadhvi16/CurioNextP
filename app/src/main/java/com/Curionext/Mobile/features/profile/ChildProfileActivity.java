package com.curionext.mobile.features.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.curionext.mobile.R;
import com.curionext.mobile.core.data.model.Child;
import com.curionext.mobile.core.data.model.Interest;
import com.curionext.mobile.core.data.model.WeeklySummary;
import com.curionext.mobile.core.util.DateUtils;
import com.curionext.mobile.features.interests.adapter.InterestAdapter;
import com.curionext.mobile.features.profile.viewmodel.ProfileViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChildProfileActivity extends AppCompatActivity {

    @Inject
    ProfileViewModel viewModel;

    private MaterialToolbar toolbar;
    private ShapeableImageView avatarImageView;
    private MaterialTextView childNameText;
    private MaterialTextView childAgeText;
    private MaterialTextView parentIdText;
    private MaterialTextView deviceIdText;
    private MaterialTextView createdDateText;
    private MaterialTextView lastUpdatedText;
    private MaterialTextView profileStatusText;
    private MaterialCardView profileCard;
    private MaterialCardView interestsCard;
    private MaterialCardView summaryCard;
    private TextInputLayout nameInputLayout;
    private TextInputLayout ageInputLayout;
    private TextInputEditText nameEditText;
    private TextInputEditText ageEditText;
    private MaterialButton editButton;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private MaterialButton changeAvatarButton;
    private MaterialButton viewInterestsButton;
    private MaterialButton playSummaryButton;
    private RecyclerView topInterestsRecyclerView;
    private MaterialTextView summaryText;
    private MaterialTextView interestCountText;

    private InterestAdapter interestAdapter;

    // Activity result launcher for image selection
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        updateAvatarImage(imageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_profile);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        initViews();
        setupRecyclerView();
        observeViewModel();

        // Load initial data
        viewModel.loadChildProfile();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        avatarImageView = findViewById(R.id.avatar_image);
        childNameText = findViewById(R.id.child_name_text);
        childAgeText = findViewById(R.id.child_age_text);
        parentIdText = findViewById(R.id.parent_id_text);
        deviceIdText = findViewById(R.id.device_id_text);
        createdDateText = findViewById(R.id.created_date_text);
        lastUpdatedText = findViewById(R.id.last_updated_text);
        profileStatusText = findViewById(R.id.profile_status_text);
        profileCard = findViewById(R.id.profile_card);
        interestsCard = findViewById(R.id.interests_card);
        summaryCard = findViewById(R.id.summary_card);
        nameInputLayout = findViewById(R.id.name_input_layout);
        ageInputLayout = findViewById(R.id.age_input_layout);
        nameEditText = findViewById(R.id.name_edit_text);
        ageEditText = findViewById(R.id.age_edit_text);
        editButton = findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);
        changeAvatarButton = findViewById(R.id.change_avatar_button);
        viewInterestsButton = findViewById(R.id.view_interests_button);
        playSummaryButton = findViewById(R.id.play_summary_button);
        topInterestsRecyclerView = findViewById(R.id.top_interests_recycler_view);
        summaryText = findViewById(R.id.summary_text);
        interestCountText = findViewById(R.id.interest_count_text);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Child Profile");
        }

        // Set up button click listeners
        editButton.setOnClickListener(v -> viewModel.startEditing());
        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(v -> viewModel.cancelEditing());
        changeAvatarButton.setOnClickListener(v -> selectNewAvatar());
        viewInterestsButton.setOnClickListener(v -> navigateToInterests());
        playSummaryButton.setOnClickListener(v -> playWeeklySummary());

        // Set up avatar click listener
        avatarImageView.setOnClickListener(v -> {
            Boolean editing = viewModel.getIsEditing().getValue();
            if (editing != null && editing) {
                selectNewAvatar();
            }
        });
    }

    private void setupRecyclerView() {
        interestAdapter = new InterestAdapter(this);
        topInterestsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        topInterestsRecyclerView.setAdapter(interestAdapter);

        // Set click listener for interests
        interestAdapter.setOnInterestClickListener(interest -> {
            navigateToInterestDetails(interest);
        });
    }

    private void observeViewModel() {
        // Observe child profile
        viewModel.getChildProfile().observe(this, this::updateProfileDisplay);

        // Observe editing state
        viewModel.getIsEditing().observe(this, this::updateEditingMode);

        // Observe avatar URL
        viewModel.getAvatarUrl().observe(this, this::updateAvatarImage);

        // Observe child interests
        viewModel.getChildInterests().observe(this, this::updateInterestsDisplay);

        // Observe weekly summary
        viewModel.getWeeklySummary().observe(this, this::updateSummaryDisplay);

        // Observe loading states
        viewModel.getIsLoading().observe(this, isLoading -> {
            // Show/hide loading indicators
            profileCard.setAlpha(isLoading ? 0.6f : 1.0f);
        });

        viewModel.getIsSaving().observe(this, isSaving -> {
            saveButton.setEnabled(!isSaving);
            saveButton.setText(isSaving ? "Saving..." : "Save");
        });

        // Observe messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(this, successMessage -> {
            if (successMessage != null && !successMessage.isEmpty()) {
                Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getStatusMessage().observe(this, statusMessage -> {
            if (statusMessage != null && !statusMessage.isEmpty()) {
                profileStatusText.setText(statusMessage);
            }
        });
    }

    private void updateProfileDisplay(Child child) {
        if (child != null) {
            childNameText.setText(child.getName());
            childAgeText.setText(child.getAge() + " years old");
            parentIdText.setText("Parent ID: " + child.getParentId());
            deviceIdText.setText("Device ID: " + (child.getDeviceId() != null ? child.getDeviceId() : "Not assigned"));

            if (child.getCreatedAt() != null) {
                createdDateText.setText("Joined " + DateUtils.formatDisplayDate(child.getCreatedAt()));
            }

            if (child.getUpdatedAt() != null) {
                lastUpdatedText.setText("Updated " + DateUtils.getRelativeTimeString(child.getUpdatedAt()));
            }

            // Update edit fields with current values
            nameEditText.setText(child.getName());
            ageEditText.setText(child.getAge() != null ? child.getAge().toString() : "");

            // Update status
            profileStatusText.setText(child.isActive() ? "Active Profile" : "Inactive Profile");
        }
    }

    private void updateEditingMode(Boolean isEditing) {
        if (isEditing != null) {
            // Show/hide edit controls
            nameInputLayout.setVisibility(isEditing ? View.VISIBLE : View.GONE);
            ageInputLayout.setVisibility(isEditing ? View.VISIBLE : View.GONE);
            saveButton.setVisibility(isEditing ? View.VISIBLE : View.GONE);
            cancelButton.setVisibility(isEditing ? View.VISIBLE : View.GONE);
            changeAvatarButton.setVisibility(isEditing ? View.VISIBLE : View.GONE);

            // Show/hide display controls
            childNameText.setVisibility(isEditing ? View.GONE : View.VISIBLE);
            childAgeText.setVisibility(isEditing ? View.GONE : View.VISIBLE);
            editButton.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        }
    }

    private void updateAvatarImage(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.ic_child_avatar)
                    .error(R.drawable.ic_child_avatar)
                    .into(avatarImageView);
        } else {
            avatarImageView.setImageResource(R.drawable.ic_child_avatar);
        }
    }

    private void updateAvatarImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .transform(new CircleCrop())
                .placeholder(R.drawable.ic_child_avatar)
                .error(R.drawable.ic_child_avatar)
                .into(avatarImageView);

        // Update ViewModel with new avatar
        viewModel.updateAvatar(imageUri.toString());
    }

    private void updateInterestsDisplay(List<Interest> interests) {
        if (interests != null) {
            interestAdapter.updateInterests(interests);
            interestCountText.setText(interests.size() + " top interests");

            if (interests.isEmpty()) {
                interestsCard.setVisibility(View.GONE);
            } else {
                interestsCard.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateSummaryDisplay(WeeklySummary summary) {
        if (summary != null) {
            summaryText.setText(summary.getSummaryText());
            summaryCard.setVisibility(View.VISIBLE);
        } else {
            summaryCard.setVisibility(View.GONE);
        }
    }

    private void saveProfile() {
        String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
        String ageStr = ageEditText.getText() != null ? ageEditText.getText().toString().trim() : "";

        Integer age = null;
        try {
            if (!ageStr.isEmpty()) {
                age = Integer.parseInt(ageStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentAvatarUrl = viewModel.getAvatarUrl().getValue();
        viewModel.saveProfile(name, age, currentAvatarUrl);
    }

    private void selectNewAvatar() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TITLE, "Select Avatar");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Choose Avatar"));
    }

    private void navigateToInterests() {
        // Navigate to InterestAnalysisActivity
        Intent intent = new Intent(this, com.curionext.mobile.features.interests.InterestAnalysisActivity.class);
        startActivity(intent);
    }

    private void navigateToInterestDetails(Interest interest) {
        Toast.makeText(this, "Viewing interest: " + interest.getTopic(), Toast.LENGTH_SHORT).show();
        // Navigate to interest details screen
    }

    private void playWeeklySummary() {
        WeeklySummary summary = viewModel.getWeeklySummary().getValue();
        if (summary != null) {
            if (summary.getAudioUrl() != null && !summary.getAudioUrl().isEmpty()) {
                // Play audio summary
                Toast.makeText(this, "Playing weekly summary...", Toast.LENGTH_SHORT).show();
                // Implement audio playback
            } else {
                // Show text summary in dialog
                showSummaryDialog(summary);
            }
        }
    }

    private void showSummaryDialog(WeeklySummary summary) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("This Week with " + viewModel.getChildName())
                .setMessage(summary.getSummaryText())
                .setPositiveButton("Close", null)
                .setNeutralButton("Share", (dialog, which) -> shareSummary(summary))
                .show();
    }

    private void shareSummary(WeeklySummary summary) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Weekly Summary - " + viewModel.getChildName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, summary.getSummaryText());
        startActivity(Intent.createChooser(shareIntent, "Share Weekly Summary"));
    }

    private void showDeleteConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Child Profile")
                .setMessage("Are you sure you want to delete " + viewModel.getChildName() + "'s profile? This action cannot be undone and will remove all associated data.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteChild();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.ic_warning)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Boolean isEditing = viewModel.getIsEditing().getValue();
            if (isEditing != null && isEditing) {
                // Show confirmation dialog if editing
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Discard Changes")
                        .setMessage("You have unsaved changes. Discard them?")
                        .setPositiveButton("Discard", (dialog, which) -> {
                            viewModel.cancelEditing();
                            onBackPressed();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                onBackPressed();
            }
            return true;
        } else if (id == R.id.action_refresh) {
            viewModel.refreshProfile();
            return true;
        } else if (id == R.id.action_share) {
            shareProfile();
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        } else if (id == R.id.action_settings) {
            // Navigate to profile settings
            Toast.makeText(this, "Profile settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareProfile() {
        Child child = viewModel.getChildProfile().getValue();
        if (child != null) {
            String profileInfo = String.format(
                    "%s's Profile\n" +
                            "Age: %d years old\n" +
                            "Joined: %s\n" +
                            "Active interests: %d\n" +
                            "Profile created on CurioNext",
                    child.getName(),
                    child.getAge(),
                    DateUtils.formatDisplayDate(child.getCreatedAt()),
                    viewModel.getChildInterests().getValue() != null ?
                            viewModel.getChildInterests().getValue().size() : 0
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, child.getName() + "'s CurioNext Profile");
            shareIntent.putExtra(Intent.EXTRA_TEXT, profileInfo);
            startActivity(Intent.createChooser(shareIntent, "Share Profile"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh profile data when returning to this activity
        viewModel.refreshProfile();
    }

    @Override
    public void onBackPressed() {
        Boolean isEditing = viewModel.getIsEditing().getValue();
        if (isEditing != null && isEditing) {
            // Show confirmation dialog
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Discard Changes")
                    .setMessage("You have unsaved changes. Discard them?")
                    .setPositiveButton("Discard", (dialog, which) -> {
                        viewModel.cancelEditing();
                        super.onBackPressed();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}