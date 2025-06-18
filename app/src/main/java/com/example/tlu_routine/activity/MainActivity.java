package com.example.tlu_routine.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tlu_routine.R;
import com.example.tlu_routine.fragment.AddEventFragment;
import com.example.tlu_routine.utils.SwipeGestureDetector;
import com.example.tlu_routine.adapter.EventAdapter;
import com.example.tlu_routine.model.Event;
import com.example.tlu_routine.utils.EventJsonManager;
import com.example.tlu_routine.utils.CustomToast;
import com.example.tlu_routine.dialog.CustomDatePickerDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements SwipeGestureDetector.OnSwipeListener, AddEventFragment.OnFragmentActionListener {

    private TextView tvMonthYear;
    private LinearLayout calendarDaysContainer;
    private ImageView ivCalendarIcon;
    private GestureDetector gestureDetector;
    private LocalDate currentWeekStart;
    private int selectedDayIndex = 0; // 0-6 for days of week

    // Day views arrays
    private LinearLayout[] dayLayouts = new LinearLayout[7];
    private TextView[] dayWeekTexts = new TextView[7];
    private TextView[] dayMonthTexts = new TextView[7];

    // Fragment container and main content views
    private View fragmentContainer;
    private View mainContent;
    private View bottomNavigation;

    // RecyclerView and Event management
    private RecyclerView rvEvents;
    private LinearLayout layoutEmptyState;
    private EventAdapter eventAdapter;
    private EventJsonManager eventJsonManager;

    // Interface để communicate với MainActivity
    public interface OnFragmentActionListener {
        void onEventSaved(String action); // "add", "edit", "delete"

        void onFragmentClosed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Thiết lập status bar màu trắng
        setupStatusBar();

        setContentView(R.layout.activity_main);

        initViews();
        setupGestures();
        setupCalendar();
        setupClickListeners();
        setupRecyclerView();

        // Initialize EventJsonManager
        eventJsonManager = new EventJsonManager(this);

        // Load events for today
        loadEventsForDate(LocalDate.now());
    }

    private void initViews() {
        // Fragment container và main content views
        fragmentContainer = findViewById(R.id.nav_host_fragment);
        mainContent = findViewById(R.id.main_content);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        tvMonthYear = findViewById(R.id.tv_month_year);
        calendarDaysContainer = findViewById(R.id.calendar_days_container);
        ivCalendarIcon = findViewById(R.id.iv_calendar_icon);

        // Initialize day views
        dayLayouts[0] = findViewById(R.id.day_1);
        dayLayouts[1] = findViewById(R.id.day_2);
        dayLayouts[2] = findViewById(R.id.day_3);
        dayLayouts[3] = findViewById(R.id.day_4);
        dayLayouts[4] = findViewById(R.id.day_5);
        dayLayouts[5] = findViewById(R.id.day_6);
        dayLayouts[6] = findViewById(R.id.day_7);

        dayWeekTexts[0] = findViewById(R.id.tv_day_week_1);
        dayWeekTexts[1] = findViewById(R.id.tv_day_week_2);
        dayWeekTexts[2] = findViewById(R.id.tv_day_week_3);
        dayWeekTexts[3] = findViewById(R.id.tv_day_week_4);
        dayWeekTexts[4] = findViewById(R.id.tv_day_week_5);
        dayWeekTexts[5] = findViewById(R.id.tv_day_week_6);
        dayWeekTexts[6] = findViewById(R.id.tv_day_week_7);

        dayMonthTexts[0] = findViewById(R.id.tv_day_month_1);
        dayMonthTexts[1] = findViewById(R.id.tv_day_month_2);
        dayMonthTexts[2] = findViewById(R.id.tv_day_month_3);
        dayMonthTexts[3] = findViewById(R.id.tv_day_month_4);
        dayMonthTexts[4] = findViewById(R.id.tv_day_month_5);
        dayMonthTexts[5] = findViewById(R.id.tv_day_month_6);
        dayMonthTexts[6] = findViewById(R.id.tv_day_month_7);

        // RecyclerView and empty state
        rvEvents = findViewById(R.id.rv_events);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
    }

    private void setupGestures() {
        SwipeGestureDetector swipeDetector = new SwipeGestureDetector(this);
        gestureDetector = new GestureDetector(this, swipeDetector);

        calendarDaysContainer.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void setupCalendar() {
        // Get current week start (Monday)
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        currentWeekStart = today.with(weekFields.dayOfWeek(), 1);

        // Set today as selected day
        selectedDayIndex = (int) today.getDayOfWeek().getValue() - 1;
        if (selectedDayIndex == 6)
            selectedDayIndex = 6; // Sunday adjustment

        updateCalendarDisplay();
    }

    private void updateCalendarDisplay() {
        // Update month/year text
        tvMonthYear.setText(getMonthYearText(currentWeekStart));

        // Update week days
        String[] dayNames = { "Th 2", "Th 3", "Th 4", "Th 5", "Th 6", "Th 7", "CN" };

        for (int i = 0; i < 7; i++) {
            LocalDate dayDate = currentWeekStart.plusDays(i);

            dayWeekTexts[i].setText(dayNames[i]);
            dayMonthTexts[i].setText(String.valueOf(dayDate.getDayOfMonth()));

            // Set click listener for each day
            final int dayIndex = i;
            dayLayouts[i].setOnClickListener(v -> selectDay(dayIndex));
        }

        // Update selection
        updateDaySelection();
    }

    private void selectDay(int dayIndex) {
        selectedDayIndex = dayIndex;
        updateDaySelection();

        LocalDate selectedDate = currentWeekStart.plusDays(dayIndex);

        // Load events for selected date
        loadEventsForDate(selectedDate);
    }

    private void updateDaySelection() {
        for (int i = 0; i < 7; i++) {
            if (i == selectedDayIndex) {
                // Selected day
                dayLayouts[i].setBackgroundResource(R.drawable.selected_date_background);
                dayWeekTexts[i].setTextColor(getResources().getColor(android.R.color.white, getTheme()));
                dayMonthTexts[i].setTextColor(getResources().getColor(android.R.color.white, getTheme()));
            } else {
                // Unselected day
                dayLayouts[i].setBackgroundResource(R.drawable.unselected_date_background);
                dayWeekTexts[i].setTextColor(getResources().getColor(R.color.gray_text, getTheme()));
                dayMonthTexts[i].setTextColor(getResources().getColor(R.color.gray_text, getTheme()));
            }
        }
    }

    @Override
    public void onSwipeLeft() {
        // Next week
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateCalendarDisplay();
        // Toast.makeText(this, "Tuần tiếp theo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwipeRight() {
        // Previous week
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateCalendarDisplay();
        // Toast.makeText(this, "Tuần trước", Toast.LENGTH_SHORT).show();
    }

    private String getMonthYearText(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Tháng' M yyyy", new Locale("vi", "VN"));
        return date.format(formatter);
    }

    private void showAddEventFragment() {
        // Show fragment container, hide main content
        fragmentContainer.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);

        // Disable and dim bottom navigation
        if (bottomNavigation != null) {
            bottomNavigation.setAlpha(0.3f); // Làm mờ
            bottomNavigation.setEnabled(false); // Disable clicks

            // Disable all child views
            for (int i = 0; i < ((android.view.ViewGroup) bottomNavigation).getChildCount(); i++) {
                View child = ((android.view.ViewGroup) bottomNavigation).getChildAt(i);
                child.setEnabled(false);
            }
        }

        // Create and show AddEventFragment
        AddEventFragment fragment = new AddEventFragment();

        // Set the selected date from calendar
        LocalDate selectedDate = currentWeekStart.plusDays(selectedDayIndex);
        fragment.setSelectedDate(selectedDate);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showEditEventFragment(Event event) {
        // Show fragment container, hide main content
        fragmentContainer.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);

        // Disable and dim bottom navigation
        if (bottomNavigation != null) {
            bottomNavigation.setAlpha(0.3f); // Làm mờ
            bottomNavigation.setEnabled(false); // Disable clicks

            // Disable all child views
            for (int i = 0; i < ((android.view.ViewGroup) bottomNavigation).getChildCount(); i++) {
                View child = ((android.view.ViewGroup) bottomNavigation).getChildAt(i);
                child.setEnabled(false);
            }
        }

        // Create and show AddEventFragment in edit mode
        AddEventFragment fragment = new AddEventFragment();
        fragment.setEditMode(event); // Set edit mode with event data

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void hideFragmentAndShowMain() {
        // Hide fragment container, show main content
        fragmentContainer.setVisibility(View.GONE);
        mainContent.setVisibility(View.VISIBLE);

        // Enable and restore bottom navigation
        if (bottomNavigation != null) {
            bottomNavigation.setAlpha(1.0f); // Khôi phục độ trong suốt
            bottomNavigation.setEnabled(true); // Enable clicks

            // Enable all child views
            for (int i = 0; i < ((android.view.ViewGroup) bottomNavigation).getChildCount(); i++) {
                View child = ((android.view.ViewGroup) bottomNavigation).getChildAt(i);
                child.setEnabled(true);
            }
        }

        // Clear fragment back stack
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    private void setupClickListeners() {
        // FAB click listener - Mở AddEventFragment
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(view -> {
            showAddEventFragment();
        });

        // Add task button click listener - Mở AddEventFragment
        MaterialButton btnAddTask = findViewById(R.id.btn_add_task);
        btnAddTask.setOnClickListener(v -> {
            showAddEventFragment();
        });

        // Calendar icon click listener - open full calendar
        ivCalendarIcon.setOnClickListener(v -> showFullCalendar());

        // Long press calendar icon to show debug menu
        ivCalendarIcon.setOnLongClickListener(v -> {
            showDebugMenu();
            return true;
        });
    }

    private void showFullCalendar() {
        // Get current selected date
        LocalDate currentSelectedDate = currentWeekStart.plusDays(selectedDayIndex);

        CustomDatePickerDialog datePickerDialog = new CustomDatePickerDialog(
                this,
                currentSelectedDate,
                selectedDate -> {
                    // Get week start for selected date (Monday)
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    currentWeekStart = selectedDate.with(weekFields.dayOfWeek(), 1);

                    // Find which day of week was selected (0=Monday, 6=Sunday)
                    selectedDayIndex = (int) selectedDate.getDayOfWeek().getValue() - 1;

                    // Update calendar display to show the week containing selected date
                    updateCalendarDisplay();

                    // Load events for selected date
                    loadEventsForDate(selectedDate);
                });
        datePickerDialog.show();
    }

    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));

            // Đặt icon status bar thành màu tối để hiển thị trên nền trắng
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    // Implementation của OnFragmentActionListener interface
    @Override
    public void onEventSaved(String action) {
        String message;
        switch (action) {
            case "add":
                message = "Thêm sự kiện thành công";
                break;
            case "edit":
                message = "Sửa sự kiện thành công";
                break;
            case "delete":
                message = "Xóa sự kiện thành công";
                break;
            default:
                message = "Thao tác thành công";
                break;
        }

        CustomToast.showSuccess(this, message);
        hideFragmentAndShowMain();

        // Refresh events list for current selected date
        LocalDate selectedDate = currentWeekStart.plusDays(selectedDayIndex);
        loadEventsForDate(selectedDate);
    }

    @Override
    public void onFragmentClosed() {
        hideFragmentAndShowMain();
    }

    // Override onBackPressed để handle fragment back stack
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // Có fragment trong back stack, hide fragment và show main
            hideFragmentAndShowMain();
        } else {
            // Không có fragment, exit app
            super.onBackPressed();
        }
    }

    private void setupRecyclerView() {
        eventAdapter = new EventAdapter();
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(eventAdapter);

        // Set click listeners
        eventAdapter.setOnEventClickListener(new EventAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(Event event) {
                // Show edit event fragment
                showEditEventFragment(event);
            }

            @Override
            public void onEventCheckedChange(Event event, boolean isChecked) {
                // Update event completion status
                event.setCompleted(isChecked);
                eventJsonManager.updateEvent(event);

                String status = isChecked ? "Đã hoàn thành" : "Chưa hoàn thành";
                // Toast.makeText(MainActivity.this, event.getName() + ": " + status,
                // Toast.LENGTH_SHORT).show();

                // Refresh the list to update UI
                eventAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadEventsForDate(LocalDate date) {
        // Force reload from JSON (clear any cache)
        eventJsonManager = new EventJsonManager(this);
        List<Event> events = eventJsonManager.getEventsByDate(date);

        if (events.isEmpty()) {
            // Show empty state
            rvEvents.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            // Show events list
            layoutEmptyState.setVisibility(View.GONE);
            rvEvents.setVisibility(View.VISIBLE);
            eventAdapter.setEvents(events);
        }
    }

    private void showDebugMenu() {
        String[] options = {
                "Xem đường dẫn file JSON",
                "Xóa toàn bộ sự kiện",
                "Xóa file JSON",
                "Xem số lượng sự kiện",
                "Reload dữ liệu"
        };

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Debug Menu")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Show file path
                            String filePath = getFilesDir() + "/events.json";
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("File Path")
                                    .setMessage(filePath)
                                    .setPositiveButton("OK", null)
                                    .show();
                            break;

                        case 1: // Delete all events
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Xác nhận")
                                    .setMessage("Bạn có chắc muốn xóa toàn bộ sự kiện?")
                                    .setPositiveButton("Xóa", (d, w) -> {
                                        eventJsonManager.deleteAllEvents();
                                        loadEventsForDate(currentWeekStart.plusDays(selectedDayIndex));
                                        // Toast.makeText(this, "Đã xóa toàn bộ sự kiện", Toast.LENGTH_SHORT).show();
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                            break;

                        case 2: // Delete JSON file
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Xác nhận")
                                    .setMessage("Bạn có chắc muốn xóa file JSON?")
                                    .setPositiveButton("Xóa", (d, w) -> {
                                        eventJsonManager.deleteEventsFile();
                                        loadEventsForDate(currentWeekStart.plusDays(selectedDayIndex));
                                        // Toast.makeText(this, "Đã xóa file JSON", Toast.LENGTH_SHORT).show();
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                            break;

                        case 3: // Show event count
                            List<Event> allEvents = eventJsonManager.loadEvents();
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Thống kê")
                                    .setMessage("Tổng số sự kiện: " + allEvents.size())
                                    .setPositiveButton("OK", null)
                                    .show();
                            break;

                        case 4: // Reload data
                            loadEventsForDate(currentWeekStart.plusDays(selectedDayIndex));
                            // Toast.makeText(this, "Đã reload dữ liệu", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }
}
