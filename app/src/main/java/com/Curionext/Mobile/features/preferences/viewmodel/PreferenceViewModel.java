package com.curionext.mobile.features.preferences.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.curionext.mobile.core.data.model.Preference;
import com.curionext.mobile.core.data.repository.PreferenceRepository;
import com.curionext.mobile.core.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class PreferenceViewModel extends ViewModel {

    private final PreferenceRepository preferenceRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<Preference>> allPreferences = new MutableLiveData<>();
    private final MutableLiveData<List<Preference>> likes = new MutableLiveData<>();
    private final MutableLiveData<List<Preference>> dislikes = new MutableLiveData<>();
    private final MutableLiveData<List<Preference>> neutralPreferences = new MutableLiveData<>();
    private final MutableLiveData<Map<String, List<Preference>>> preferencesByCategory = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Double>> sentimentTrends = new MutableLiveData<>();
    private final MutableLiveData<List<String> categories = new MutableLiveData<>();
    private final MutableLiveData<String> selectedFilter = new MutableLiveData<>("All");
    private final MutableLiveData<Double> averageSentiment = new MutableLiveData<>();
    private final MutableLiveData<Double> averageConfidence = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    private static final String CHILD_ID = "avani_001"; // In real app, get from preferences

    @Inject
    public PreferenceViewModel(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;

        // Load initial data
        loadAllPreferences();
    }

    // Getters for LiveData
    public LiveData<List<Preference>> getAllPreferences() { return allPreferences; }
    public LiveData<List<Preference>> getLikes() { return likes; }
    public LiveData<List<Preference>> getDislikes() { return dislikes; }
    public LiveData<List<Preference>> getNeutralPreferences() { return neutralPreferences; }
    public LiveData<Map<String, List<Preference>>> getPreferencesByCategory() { return preferencesByCategory; }
    public LiveData<Map<String, Double>> getSentimentTrends() { return sentimentTrends; }
    public LiveData<List<String>> getCategories() { return categories; }
    public LiveData<String> getSelectedFilter() { return selectedFilter; }
    public LiveData<Double> getAverageSentiment() { return averageSentiment; }
    public LiveData<Double> getAverageConfidence() { return averageConfidence; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getStatusMessage() { return statusMessage; }

    public void loadAllPreferences() {
        isLoading.setValue(true);

        disposables.add(
                preferenceRepository.getPreferences(CHILD_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                preferences -> {
                                    allPreferences.setValue(preferences);
                                    processPreferenceData(preferences);
                                    isLoading.setValue(false);
                                    statusMessage.setValue("Loaded " + preferences.size() + " preferences");
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Failed to load preferences: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void loadTopLikes() {
        disposables.add(
                preferenceRepository.getTopLikes(CHILD_ID, 10)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                topLikes -> {
                                    likes.setValue(topLikes);
                                    statusMessage.setValue("Top likes updated");
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load top likes: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void loadTopDislikes() {
        disposables.add(
                preferenceRepository.getTopDislikes(CHILD_ID, 10)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                topDislikes -> {
                                    dislikes.setValue(topDislikes);
                                    statusMessage.setValue("Top dislikes updated");
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load top dislikes: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void loadPreferenceAnalysis() {
        isLoading.setValue(true);

        disposables.add(
                preferenceRepository.getPreferenceAnalysis(CHILD_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                analysis -> {
                                    allPreferences.setValue(analysis);
                                    processPreferenceData(analysis);
                                    generateSentimentTrends(analysis);
                                    isLoading.setValue(false);
                                    statusMessage.setValue("Preference analysis completed");
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Failed to load preference analysis: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void setFilter(String filter) {
        selectedFilter.setValue(filter);
        List<Preference> currentPreferences = allPreferences.getValue();

        if (currentPreferences != null) {
            List<Preference> filtered = filterPreferences(currentPreferences, filter);
            updateFilteredPreferences(filtered, filter);
        }
    }

    public void searchPreferences(String query) {
        List<Preference> currentPreferences = allPreferences.getValue();
        if (currentPreferences != null && !query.trim().isEmpty()) {
            List<Preference> filtered = currentPreferences.stream()
                    .filter(preference ->
                            preference.getTopic().toLowerCase().contains(query.toLowerCase()) ||
                                    preference.getCategory().toLowerCase().contains(query.toLowerCase()) ||
                                    (preference.getKeywords() != null &&
                                            String.join(" ", preference.getKeywords()).toLowerCase().contains(query.toLowerCase()))
                    )
                    .collect(Collectors.toList());

            allPreferences.setValue(filtered);
            statusMessage.setValue("Found " + filtered.size() + " preferences matching '" + query + "'");
        } else {
            loadAllPreferences(); // Reset to full list
        }
    }

    public void refreshPreferences() {
        loadAllPreferences();
        loadTopLikes();
        loadTopDislikes();
        loadPreferenceAnalysis();
    }

    public void onPreferenceClicked(Preference preference) {
        statusMessage.setValue("Viewing details for: " + preference.getTopic());
        // This could trigger navigation to preference details
    }

    public void exportPreferenceData() {
        List<Preference> preferences = allPreferences.getValue();
        if (preferences != null) {
            statusMessage.setValue("Exporting " + preferences.size() + " preferences...");
            // Implementation for exporting preference data
        }
    }

    public void generatePreferenceReport() {
        isLoading.setValue(true);
        statusMessage.setValue("Generating preference analysis report...");

        // Simulate report generation
        loadPreferenceAnalysis();
    }

    private void processPreferenceData(List<Preference> preferences) {
        // Separate by sentiment
        List<Preference> positiveList = new ArrayList<>();
        List<Preference> negativeList = new ArrayList<>();
        List<Preference> neutralList = new ArrayList<>();

        double totalSentiment = 0.0;
        double totalConfidence = 0.0;

        for (Preference preference : preferences) {
            totalSentiment += preference.getSentiment();
            totalConfidence += preference.getConfidence();

            if (preference.isPositive()) {
                positiveList.add(preference);
            } else if (preference.isNegative()) {
                negativeList.add(preference);
            } else {
                neutralList.add(preference);
            }
        }

        // Sort by confidence and sentiment strength
        positiveList.sort((a, b) -> Double.compare(b.getSentiment(), a.getSentiment()));
        negativeList.sort((a, b) -> Double.compare(a.getSentiment(), b.getSentiment()));

        likes.setValue(positiveList);
        dislikes.setValue(negativeList);
        neutralPreferences.setValue(neutralList);

        // Calculate averages
        if (!preferences.isEmpty()) {
            averageSentiment.setValue(totalSentiment / preferences.size());
            averageConfidence.setValue(totalConfidence / preferences.size());
        }

        // Group by category
        Map<String, List<Preference>> grouped = preferences.stream()
                .collect(Collectors.groupingBy(Preference::getCategory));
        preferencesByCategory.setValue(grouped);

        // Extract categories
        List<String> categoryList = new ArrayList<>(grouped.keySet());
        categoryList.add(0, "All"); // Add "All" option at beginning
        categories.setValue(categoryList);
    }

    private void generateSentimentTrends(List<Preference> preferences) {
        Map<String, Double> trends = new HashMap<>();

        // Calculate sentiment trends by category
        Map<String, List<Preference>> byCategory = preferences.stream()
                .collect(Collectors.groupingBy(Preference::getCategory));

        for (Map.Entry<String, List<Preference>> entry : byCategory.entrySet()) {
            double avgSentiment = entry.getValue().stream()
                    .mapToDouble(Preference::getSentiment)
                    .average()
                    .orElse(0.0);
            trends.put(entry.getKey(), avgSentiment);
        }

        sentimentTrends.setValue(trends);
    }

    private List<Preference> filterPreferences(List<Preference> preferences, String filter) {
        switch (filter) {
            case "Likes":
                return preferences.stream()
                        .filter(Preference::isPositive)
                        .collect(Collectors.toList());
            case "Dislikes":
                return preferences.stream()
                        .filter(Preference::isNegative)
                        .collect(Collectors.toList());
            case "Neutral":
                return preferences.stream()
                        .filter(Preference::isNeutral)
                        .collect(Collectors.toList());
            case "High Confidence":
                return preferences.stream()
                        .filter(p -> p.getConfidence() >= Constants.HIGH_CONFIDENCE_THRESHOLD)
                        .collect(Collectors.toList());
            case "Strong":
                return preferences.stream()
                        .filter(p -> Math.abs(p.getSentiment()) >= 0.7)
                        .collect(Collectors.toList());
            default: // "All"
                return preferences;
        }
    }

    private void updateFilteredPreferences(List<Preference> filtered, String filterType) {
        switch (filterType) {
            case "Likes":
                likes.setValue(filtered);
                break;
            case "Dislikes":
                dislikes.setValue(filtered);
                break;
            case "Neutral":
                neutralPreferences.setValue(filtered);
                break;
            default:
                allPreferences.setValue(filtered);
                break;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}