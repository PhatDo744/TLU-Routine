package com.example.tlu_routine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import androidx.fragment.app.Fragment;
import com.example.tlu_routine.util.DatePickerDialog;

import com.example.tlu_routine.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatisticFragment extends Fragment {

    private TextView tvStartDate, tvEndDate, tvProgressPercentage, tvCompletedCount, tvTotalCount;
    private TextView tabPerformance, tabEvents;
    private ImageView btnBack;
    private ProgressBar progressCircle;
    private Calendar startDateCalendar, endDateCalendar; // Add these Calendar objects
    private SimpleDateFormat dateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Initialize views
        initViews(view);

        // Initialize dates
        initializeDates();

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
        dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
    }

    private void initializeDates() {
        // Initialize calendar objects
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();

        // Set start date to first day of current month
        startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // Check if date arguments were passed from StatsFragment
        Bundle args = getArguments();
        if (args != null) {
            long startDateMillis = args.getLong("start_date", 0);
            long endDateMillis = args.getLong("end_date", 0);

            if (startDateMillis > 0 && endDateMillis > 0) {
                startDateCalendar.setTimeInMillis(startDateMillis);
                endDateCalendar.setTimeInMillis(endDateMillis);
            }
        }

        // Update the TextViews
        updateDateDisplay();
    }

    private void updateDateDisplay() {
        tvStartDate.setText(dateFormat.format(startDateCalendar.getTime()));
        tvEndDate.setText(dateFormat.format(endDateCalendar.getTime()));
    }

    // In StatisticFragment.java

    // Update the setupListeners() method
    private void setupListeners() {
        // Handle back button click - navigate directly to HomeFragment
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, new HomeFragment())
                        .setReorderingAllowed(true)
                        .disallowAddToBackStack()
                        .commit();
            }
        });

        // Rest of the method remains the same
        tabPerformance.setOnClickListener(v -> {
            setActiveTab(true);
        });

        tabEvents.setOnClickListener(v -> {
            setActiveTab(false);
            switchToEventsTab();
        });

        tvStartDate.setOnClickListener(v -> {
            showDatePickerDialog(true);
        });

        tvEndDate.setOnClickListener(v -> {
            showDatePickerDialog(false);
        });
    }

    // Add this new method to handle navigation to StatsFragment
    private void switchToEventsTab() {
        // Create bundle to pass the selected dates
        Bundle args = new Bundle();

        // Parse dates from TextViews to get timestamps
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            Date startDate = dateFormat.parse(tvStartDate.getText().toString());
            Date endDate = dateFormat.parse(tvEndDate.getText().toString());

            if (startDate != null && endDate != null) {
                args.putLong("start_date", startDate.getTime());
                args.putLong("end_date", endDate.getTime());
            }
        } catch (Exception e) {
            // If date parsing fails, don't add to bundle
        }

        if (getActivity() != null) {
            // Create new StatsFragment instance
            StatsFragment statsFragment = new StatsFragment();
            statsFragment.setArguments(args);

            // Use FragmentManager for direct transaction
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, statsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void setActiveTab(boolean isPerformanceTab) {
        if (isPerformanceTab) {
            tabPerformance.setBackgroundResource(R.drawable.tab_selected_background);
            tabPerformance.setTextColor(getResources().getColor(android.R.color.white));
            tabEvents.setBackgroundResource(R.drawable.tab_unselected_background);
            tabEvents.setTextColor(getResources().getColor(R.color.text_secondary));
        } else {
            tabEvents.setBackgroundResource(R.drawable.tab_selected_background);
            tabEvents.setTextColor(getResources().getColor(android.R.color.white));
            tabPerformance.setBackgroundResource(R.drawable.tab_unselected_background);
            tabPerformance.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    private void showDatePickerDialog(boolean isStartDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), date -> {
            // Format the selected date
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(date);

            // Update the appropriate TextView
            if (isStartDate) {
                tvStartDate.setText(formattedDate);
            } else {
                tvEndDate.setText(formattedDate);
            }

            // After date selection, reload statistics data
            loadStatisticsData();
        });

        try {
            // Try to set the initial date from the TextView
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            String currentDateText = isStartDate ? tvStartDate.getText().toString() : tvEndDate.getText().toString();
            Date initialDate = dateFormat.parse(currentDateText);
            datePickerDialog.setDate(initialDate);
        } catch (Exception e) {
            // If parsing fails, just use current date
        }

        datePickerDialog.show();
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