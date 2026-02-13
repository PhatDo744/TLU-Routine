package com.example.tlu_routine.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tlu_routine.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerDialog extends Dialog {

    private Calendar currentCalendar = Calendar.getInstance();
    private Calendar selectedCalendar = Calendar.getInstance();
    private TextView tvMonthYear;
    private LinearLayout calendarGrid;
    private OnDateSelectedListener onDateSelectedListener;
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    public interface OnDateSelectedListener {
        void onDateSelected(Date date);
    }

    public DatePickerDialog(@NonNull Context context, OnDateSelectedListener listener) {
        super(context);
        this.onDateSelectedListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.date_picker);

        // Make dialog match parent width
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Initialize views
        tvMonthYear = findViewById(R.id.tv_month_year);
        calendarGrid = findViewById(R.id.calendar_grid);
        ImageView btnPrevMonth = findViewById(R.id.btn_prev_month);
        ImageView btnNextMonth = findViewById(R.id.btn_next_month);

        // Set listeners
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendarView();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendarView();
        });

        // Initial calendar setup
        updateCalendarView();
    }

    private void updateCalendarView() {
        // Update month/year title
        tvMonthYear.setText(monthYearFormat.format(currentCalendar.getTime()));

        // Find the calendar grid view properly
        calendarGrid = findViewById(R.id.calendar_grid);
        if (calendarGrid == null) {
            android.util.Log.e("DatePickerDialog", "Cannot find calendar_grid view!");
            return; // Exit to prevent crash
        }

        // Get current month information
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        // Set calendar to first day of month
        Calendar tempCalendar = (Calendar) currentCalendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // Find what day of week the month starts on (0 = Sunday)
        int firstDayOfMonth = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;

        // Get number of days in month
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Instead of using selectableItemBackgroundBorderless which is causing issues
        // use android.R.color.transparent as a fallback

        // Process each week row
        int weekCount = calendarGrid.getChildCount();
        for (int week = 0; week < weekCount; week++) {
            View weekView = calendarGrid.getChildAt(week);
            if (!(weekView instanceof LinearLayout)) {
                continue; // Skip if not a LinearLayout
            }

            LinearLayout weekRow = (LinearLayout) weekView;
            int dayCount = weekRow.getChildCount();

            for (int day = 0; day < dayCount; day++) {
                View dayView = weekRow.getChildAt(day);
                if (!(dayView instanceof TextView)) {
                    continue; // Skip if not a TextView
                }

                TextView dayTextView = (TextView) dayView;
                int position = week * 7 + day;
                int dayOfMonth = position - firstDayOfMonth + 1;

                // Configure day cell based on position
                if (dayOfMonth > 0 && dayOfMonth <= daysInMonth) {
                    // Set the day number
                    dayTextView.setText(String.valueOf(dayOfMonth));
                    dayTextView.setVisibility(View.VISIBLE);
                    dayTextView.setClickable(true);

                    // Check if this day is selected
                    boolean isSelected = currentYear == selectedCalendar.get(Calendar.YEAR) &&
                            currentMonth == selectedCalendar.get(Calendar.MONTH) &&
                            dayOfMonth == selectedCalendar.get(Calendar.DAY_OF_MONTH);

                    // Set background for selected date
                    dayTextView.setBackgroundResource(isSelected ?
                            R.drawable.selected_date_background :
                            android.R.color.transparent);

                    // Set click listener for date selection
                    final int finalDayOfMonth = dayOfMonth;
                    dayTextView.setOnClickListener(v -> {
                        // Update selected date
                        selectedCalendar = (Calendar) currentCalendar.clone();
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, finalDayOfMonth);

                        // Notify listener
                        if (onDateSelectedListener != null) {
                            onDateSelectedListener.onDateSelected(selectedCalendar.getTime());
                        }

                        // Dismiss dialog
                        dismiss();
                    });
                } else {
                    // Empty cell
                    dayTextView.setText("");
                    dayTextView.setClickable(false);
                    dayTextView.setBackgroundResource(android.R.color.transparent);
                }
            }
        }
    }

    // Method to set initial date
    public void setDate(Date date) {
        if (date != null) {
            selectedCalendar.setTime(date);
            currentCalendar.setTime(date);
            if (tvMonthYear != null) {
                updateCalendarView();
            }
        }
    }
}