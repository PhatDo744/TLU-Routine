package com.example.tlu_routine.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.tlu_routine.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class CustomDatePickerDialog extends Dialog {

    public interface OnDateSetListener {
        void onDateSet(LocalDate selectedDate);
    }

    private TextView tvMonthYear;
    private ImageButton btnPrevMonth, btnNextMonth;
    private GridLayout calendarGrid;
    private MaterialButton btnCancel, btnOk;

    private LocalDate currentMonth;
    private LocalDate selectedDate;
    private OnDateSetListener listener;

    public CustomDatePickerDialog(@NonNull Context context, LocalDate initialDate, OnDateSetListener listener) {
        super(context);
        this.selectedDate = initialDate != null ? initialDate : LocalDate.now();
        this.currentMonth = this.selectedDate;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_date_picker);

        // Set dialog window attributes
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);

            // Add margin to center properly
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }

        initViews();
        setupClickListeners();
        updateCalendar();
    }

    private void initViews() {
        tvMonthYear = findViewById(R.id.tv_month_year);
        btnPrevMonth = findViewById(R.id.btn_prev_month);
        btnNextMonth = findViewById(R.id.btn_next_month);
        calendarGrid = findViewById(R.id.calendar_grid);
        btnCancel = findViewById(R.id.btn_cancel);
        btnOk = findViewById(R.id.btn_ok);
    }

    private void setupClickListeners() {
        btnPrevMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });

        // Since buttons are hidden, we'll handle date selection on click
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> dismiss());
        }

        if (btnOk != null) {
            btnOk.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDateSet(selectedDate);
                }
                dismiss();
            });
        }
    }

    private void updateCalendar() {
        // Update month/year header
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        tvMonthYear.setText(currentMonth.format(formatter));

        // Clear existing calendar
        calendarGrid.removeAllViews();

        // Get first day of month and total days
        YearMonth yearMonth = YearMonth.from(currentMonth);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int daysInMonth = yearMonth.lengthOfMonth();
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0

        // Add empty cells for days before month starts
        for (int i = 0; i < firstDayOfWeek; i++) {
            addEmptyCell();
        }

        // Add days of month
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = yearMonth.atDay(day);
            addDayCell(date);
        }

        // Fill remaining cells to complete the grid
        int totalCells = calendarGrid.getChildCount();
        int remainingCells = 42 - totalCells; // 6 rows * 7 days
        for (int i = 0; i < remainingCells; i++) {
            addEmptyCell();
        }
    }

    private void addEmptyCell() {
        View emptyView = new View(getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dpToPx(40);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        emptyView.setLayoutParams(params);
        calendarGrid.addView(emptyView);
    }

    private void addDayCell(LocalDate date) {
        // Create container
        FrameLayout container = new FrameLayout(getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dpToPx(40);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        container.setLayoutParams(params);

        // Create TextView for day number
        TextView dayView = new TextView(getContext());
        dayView.setText(String.valueOf(date.getDayOfMonth()));
        dayView.setTextSize(16);
        dayView.setGravity(Gravity.CENTER);

        // Set fixed size for circular background
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                dpToPx(36),
                dpToPx(36));
        textParams.gravity = Gravity.CENTER;
        dayView.setLayoutParams(textParams);

        // Check if this is the selected date
        if (date.equals(selectedDate)) {
            dayView.setBackgroundResource(R.drawable.selected_date_background);
            dayView.setTextColor(Color.WHITE);
        } else {
            dayView.setTextColor(Color.BLACK);
        }

        // Set click listener on container
        container.setOnClickListener(v -> {
            selectedDate = date;
            if (listener != null) {
                listener.onDateSet(selectedDate);
            }
            dismiss();
        });

        container.addView(dayView);
        calendarGrid.addView(container);
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}