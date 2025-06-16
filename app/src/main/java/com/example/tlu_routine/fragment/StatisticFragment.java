package com.example.tlu_routine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.tlu_routine.R;

public class StatisticFragment extends Fragment {

    private TextView tvStartDate, tvEndDate, tvProgressPercentage, tvCompletedCount, tvTotalCount;
    private TextView tabPerformance, tabEvents;
    private ImageView btnBack;
    private ProgressBar progressCircle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Initialize views
        initViews(view);

        // Setup listeners
        setupListeners();

        // Load initial data
        loadStatisticsData();

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        tabPerformance = view.findViewById(R.id.tab_performance);
        tabEvents = view.findViewById(R.id.tab_events);
        tvStartDate = view.findViewById(R.id.tv_start_date);
        tvEndDate = view.findViewById(R.id.tv_end_date);
        progressCircle = view.findViewById(R.id.progress_circle);
        tvProgressPercentage = view.findViewById(R.id.tv_progress_percentage);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);
        tvTotalCount = view.findViewById(R.id.tv_total_count);
    }

    private void setupListeners() {
        // Handle back button click
        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Handle tab selection
        tabPerformance.setOnClickListener(v -> {
            setActiveTab(true);
        });

        tabEvents.setOnClickListener(v -> {
            setActiveTab(false);
        });

        // Handle date selection
        tvStartDate.setOnClickListener(v -> {
            showDatePickerDialog(true);
        });

        tvEndDate.setOnClickListener(v -> {
            showDatePickerDialog(false);
        });
    }

    private void setActiveTab(boolean isPerformanceTab) {
        if (isPerformanceTab) {
            tabPerformance.setBackgroundResource(R.drawable.tab_selected_background);
            tabPerformance.setTextColor(getResources().getColor(android.R.color.white));
            tabEvents.setBackgroundResource(R.drawable.tab_unselected_background);
            tabEvents.setTextColor(getResources().getColor(R.color.default_text_color));
        } else {
            tabEvents.setBackgroundResource(R.drawable.tab_selected_background);
            tabEvents.setTextColor(getResources().getColor(android.R.color.white));
            tabPerformance.setBackgroundResource(R.drawable.tab_unselected_background);
            tabPerformance.setTextColor(getResources().getColor(R.color.default_text_color));
        }
    }

    private void showDatePickerDialog(boolean isStartDate) {
        // Implementation for date picker dialog
        // This would show a DatePickerDialog and update the appropriate TextView
    }

    private void loadStatisticsData() {
        // This would load the actual statistics data from your data source
        // For now, we'll just set sample data
        progressCircle.setProgress(75);
        tvProgressPercentage.setText("75%");
        tvCompletedCount.setText("45");
        tvTotalCount.setText("60");
    }
}