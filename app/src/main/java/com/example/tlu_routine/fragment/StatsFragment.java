package com.example.tlu_routine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tlu_routine.util.DatePickerDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.tlu_routine.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private TextView tvStartDate, tvEndDate;
    private TextView tabPerformance, tabEvents;
    private ImageView btnBack;
    private Calendar startDateCalendar, endDateCalendar;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        // Initialize views
        tvStartDate = view.findViewById(R.id.tv_start_date);
        tvEndDate = view.findViewById(R.id.tv_end_date);
        tabPerformance = view.findViewById(R.id.tab_performance);
        tabEvents = view.findViewById(R.id.tab_events);
        btnBack = view.findViewById(R.id.btn_back);

        // Initialize date format and calendars
        dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();

        // Set default dates - first day of current month to today
        startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
        updateDateDisplay();

        // Set up click listeners
        setupClickListeners();

        return view;
    }

    private void setupClickListeners() {
        // Back button click listener - navigate directly to HomeFragment
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
        tabPerformance.setOnClickListener(v -> switchToPerformanceTab());

        tvStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        tvEndDate.setOnClickListener(v -> showDatePickerDialog(false));
    }

    private void switchToPerformanceTab() {
        // Create bundle to pass the selected dates
        Bundle args = new Bundle();
        args.putLong("start_date", startDateCalendar.getTimeInMillis());
        args.putLong("end_date", endDateCalendar.getTimeInMillis());

        if (getActivity() != null) {
            // Create new StatisticFragment instance
            StatisticFragment statisticFragment = new StatisticFragment();
            statisticFragment.setArguments(args);

            // Use FragmentManager for direct transaction
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, statisticFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showDatePickerDialog(final boolean isStartDate) {
        com.example.tlu_routine.util.DatePickerDialog datePickerDialog =
                new com.example.tlu_routine.util.DatePickerDialog(requireContext(), date -> {
                    if (isStartDate) {
                        startDateCalendar.setTime(date);
                        // Ensure start date is not after end date
                        if (startDateCalendar.after(endDateCalendar)) {
                            startDateCalendar.setTimeInMillis(endDateCalendar.getTimeInMillis());
                        }
                    } else {
                        endDateCalendar.setTime(date);
                        // Ensure end date is not before start date
                        if (endDateCalendar.before(startDateCalendar)) {
                            endDateCalendar.setTimeInMillis(startDateCalendar.getTimeInMillis());
                        }
                    }
                    updateDateDisplay();
                });

        // Set the initial date
        Calendar calendar = isStartDate ? startDateCalendar : endDateCalendar;
        datePickerDialog.setDate(calendar.getTime());

        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        tvStartDate.setText(dateFormat.format(startDateCalendar.getTime()));
        tvEndDate.setText(dateFormat.format(endDateCalendar.getTime()));
    }

    // Method to update dates from external source (like StatisticFragment)
    public void updateDates(long startDateMillis, long endDateMillis) {
        startDateCalendar.setTimeInMillis(startDateMillis);
        endDateCalendar.setTimeInMillis(endDateMillis);
        updateDateDisplay();
    }
}