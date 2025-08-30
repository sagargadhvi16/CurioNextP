package com.curionext.mobile.features.interests.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.curionext.mobile.core.data.model.Interest;
import com.curionext.mobile.core.data.repository.InterestRepository;

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
public class InterestViewModel extends ViewModel {

    private final InterestRepository interestRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<Interest>> allInterests = new MutableLiveData<>();
    private final MutableLiveData<List<Interest>> topInterests = new MutableLiveData<>();
    private final MutableLiveData<List<Interest>> recentInterests = new MutableLiveData<>();
    private final MutableLiveData<List<Interest>> trendingInterests = new MutableLiveData<>();
    private final MutableLiveData<Map<String, List<Interest>>> interestsByCategory = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Double>> interestTrends = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>();
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>("All");
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    private static final String CHILD_ID = "avani_001"; // In real app, get from preferences

    @Inject
    public InterestViewModel(InterestRepository interestRepository) {
        this.interestRepository = interestRepository;

        // Load initial data
        loadAllInterests();
        loadInterestCategories();
    }

    // Getters for LiveData
    public LiveData<List<Interest>> getAllInterests() { return allInterests; }
    public LiveData<List<Interest>> getTopInterests() { return topInterests; }
    public LiveData<List<Interest>> getRecentInterests() { return recentInterests; }
    public LiveData<List<Interest>> getTrendingInterests() { return trendingInterests; }
    public LiveData<Map<String, List<Interest>>> getInterestsByCategory() { return interestsByCategory; }
    public LiveData<Map<String, Double>> getInterestTrends() { return interestTrends; }
    public LiveData<List<String>> getCategories() { return categories; }
    public LiveData<String> getSelectedCategory() { return selectedCategory; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getStatusMessage() { return statusMessage; }

    public void loadAllInterests() {
        isLoading.setValue(true);

        disposables.add(
                interestRepository.getChildInterests(CHILD_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                interests -> {
                                    allInterests.setValue(interests);
                                    processInterestData(interests);
                                    isLoading.setValue(false);
                                    statusMessage.setValue("Loaded " + interests.size() + " interests");
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Failed to load interests: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void loadTopInterests() {
        disposables.add(
                interestRepository.getTopInterests(CHILD_ID, 10)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                interests -> {
                                    topInterests.setValue(interests);
                                    statusMessage.setValue("Top interests updated");
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load top interests: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void loadInterestTrends() {
        disposables.add(
                interestRepository.getInterestTrends(CHILD_ID, "30days")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                trends -> {
                                    // Process trends data
                                    List<Interest> increasingInterests = trends.stream()
                                            .filter(interest -> "increasing".equals(interest.getTrendDirection()))
                                            .collect(Collectors.toList());

                                    trendingInterests.setValue(increasingInterests);

                                    // Create trend summary
                                    Map<String, Double> trendMap = new HashMap<>();
                                    for (Interest interest : trends) {
                                        trendMap.put(interest.getTopic(), interest.getInterestLevel());
                                    }
                                    interestTrends.setValue(trendMap);

                                    statusMessage.setValue("Interest trends updated");
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load interest trends: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void selectCategory(String category) {
        selectedCategory.setValue(category);
        filterInterestsByCategory(category);
    }

    public void refreshInterestData() {
        loadAllInterests();
        loadTopInterests();
        loadInterestTrends();
    }

    public void searchInterests(String query) {
        List<Interest> currentInterests = allInterests.getValue();
        if (currentInterests != null && !query.trim().isEmpty()) {
            List<Interest> filtered = currentInterests.stream()
                    .filter(interest ->
                            interest.getTopic().toLowerCase().contains(query.toLowerCase()) ||
                                    interest.getCategory().toLowerCase().contains(query.toLowerCase()) ||
                                    (interest.getKeywords() != null &&
                                            String.join(" ", interest.getKeywords()).toLowerCase().contains(query.toLowerCase()))
                    )
                    .collect(Collectors.toList());

            allInterests.setValue(filtered);
            statusMessage.setValue("Found " + filtered.size() + " interests matching '" + query + "'");
        } else {
            loadAllInterests(); // Reset to full list
        }
    }

    public void onInterestClicked(Interest interest) {
        statusMessage.setValue("Viewing details for: " + interest.getTopic());
        // This could trigger navigation to interest details
    }

    public void onInterestLongPressed(Interest interest) {
        statusMessage.setValue("Long pressed: " + interest.getTopic());
        // This could show context menu or additional options
    }

    private void processInterestData(List<Interest> interests) {
        // Extract top interests
        List<Interest> top = interests.stream()
                .sorted((a, b) -> Double.compare(b.getInterestLevel(), a.getInterestLevel()))
                .limit(5)
                .collect(Collectors.toList());
        topInterests.setValue(top);

        // Group by category
        Map<String, List<Interest>> grouped = interests.stream()
                .collect(Collectors.groupingBy(Interest::getCategory));
        interestsByCategory.setValue(grouped);

        // Extract recent interests (high frequency, recently explored)
        List<Interest> recent = interests.stream()
                .filter(interest -> interest.getFrequency() > 2)
                .sorted((a, b) -> b.getLastExplored().compareTo(a.getLastExplored()))
                .limit(5)
                .collect(Collectors.toList());
        recentInterests.setValue(recent);
    }

    private void filterInterestsByCategory(String category) {
        List<Interest> currentInterests = allInterests.getValue();
        if (currentInterests != null) {
            if ("All".equals(category)) {
                // Show all interests
                return;
            }

            List<Interest> filtered = currentInterests.stream()
                    .filter(interest -> category.equals(interest.getCategory()))
                    .collect(Collectors.toList());

            statusMessage.setValue("Showing " + filtered.size() + " interests in " + category);
        }
    }

    private void loadInterestCategories() {
        // This would typically come from the API, but we can derive from current interests
        List<Interest> interests = allInterests.getValue();
        if (interests != null) {
            List<String> categoryList = interests.stream()
                    .map(Interest::getCategory)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            // Add "All" option at the beginning
            List<String> categoriesWithAll = new ArrayList<>();
            categoriesWithAll.add("All");
            categoriesWithAll.addAll(categoryList);

            categories.setValue(categoriesWithAll);
        }
    }

    public void exportInterestData() {
        List<Interest> interests = allInterests.getValue();
        if (interests != null) {
            statusMessage.setValue("Exporting " + interests.size() + " interests...");
            // Implementation for exporting interest data
        }
    }

    public void generateInterestReport() {
        isLoading.setValue(true);
        statusMessage.setValue("Generating interest analysis report...");

        // This could trigger a more detailed analysis
        disposables.add(
                interestRepository.getInterestTrends(CHILD_ID, "90days")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                trends -> {
                                    isLoading.setValue(false);
                                    statusMessage.setValue("Interest report generated successfully");
                                    // Process and display report data
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Failed to generate report: " + throwable.getMessage());
                                }
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}