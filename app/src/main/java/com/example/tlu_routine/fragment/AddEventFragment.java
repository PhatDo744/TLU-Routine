package com.example.tlu_routine.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tlu_routine.R;
import com.example.tlu_routine.dialog.CustomTimePickerDialog;
import com.example.tlu_routine.dialog.CustomDatePickerDialog;
import com.example.tlu_routine.model.Event;
import com.example.tlu_routine.utils.EventJsonManager;
import com.example.tlu_routine.utils.CustomToast;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEventFragment extends Fragment {

    // Interface để communicate với MainActivity
    public interface OnFragmentActionListener {
        void onEventSaved(String action); // "add", "edit", "delete"

        void onFragmentClosed();
    }

    // UI Components
    private MaterialToolbar toolbar;
    private EditText etEventName, etDuration, etLocation;
    private TextView tvStartTime, tvEndTime, tvSelectedDate;
    private ChipGroup chipGroupRepeatType;
    private Chip chipOnce, chipDaily, chipSelectedDays;
    private CheckBox cbNotification;
    private MaterialButton btnSelectTag, btnSave, btnCancel;

    // Data
    private String selectedStartTime = "";
    private String selectedEndTime = "";
    private LocalDate selectedDate = LocalDate.now();
    private String repeatType = "once"; // "once", "daily", "selected_days"
    private List<String> selectedDays = new ArrayList<>();
    private String selectedTag = "";

    // Edit mode
    private boolean isEditMode = false;
    private Event editingEvent = null;

    // Flag để tránh infinite loop khi auto-update
    private boolean isUpdatingDuration = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupToolbar();
        setupClickListeners();
        setupChipGroup();
        setupDurationWatcher();
        setDefaultValues();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar_add_event);
        etEventName = view.findViewById(R.id.et_event_name);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvEndTime = view.findViewById(R.id.tv_end_time);
        etDuration = view.findViewById(R.id.et_duration);
        tvSelectedDate = view.findViewById(R.id.tv_selected_date);
        chipGroupRepeatType = view.findViewById(R.id.chip_group_repeat_type);
        chipOnce = view.findViewById(R.id.chip_once);
        chipDaily = view.findViewById(R.id.chip_daily);
        chipSelectedDays = view.findViewById(R.id.chip_selected_days);
        cbNotification = view.findViewById(R.id.cb_notification);
        btnSelectTag = view.findViewById(R.id.btn_select_tag);
        etLocation = view.findViewById(R.id.et_location);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
    }

    private void setupToolbar() {
        if (isEditMode) {
            toolbar.setTitle("Sửa sự kiện");

            // Add delete button to toolbar
            toolbar.inflateMenu(R.menu.menu_edit_event);
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_delete) {
                    showDeleteConfirmDialog();
                    return true;
                }
                return false;
            });
        } else {
            toolbar.setTitle("Thêm sự kiện");
        }

        toolbar.setNavigationOnClickListener(v -> {
            // Close fragment
            closeFragment();
        });
    }

    private void setupClickListeners() {
        // Time picker cho giờ bắt đầu
        tvStartTime.setOnClickListener(v -> showTimePicker(true));

        // Time picker cho giờ kết thúc
        tvEndTime.setOnClickListener(v -> showTimePicker(false));

        // Date picker cho ngày
        tvSelectedDate.setOnClickListener(v -> showDatePicker());

        // Button chọn thẻ sự kiện
        btnSelectTag.setOnClickListener(v -> {
            // Navigate to tag selection - sẽ implement sau
            // Toast.makeText(getContext(), "Chọn thẻ sự kiện", Toast.LENGTH_SHORT).show();
        });

        // Button lưu
        btnSave.setOnClickListener(v -> saveEvent());

        // Button hủy
        btnCancel.setOnClickListener(v -> {
            closeFragment();
        });
    }

    private void setupDurationWatcher() {
        etDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isUpdatingDuration && !selectedStartTime.isEmpty()) {
                    updateEndTimeFromDuration();
                }
            }
        });
    }

    private void setupChipGroup() {
        chipOnce.setOnClickListener(v -> {
            selectChip(chipOnce);
            repeatType = "once";
        });
        chipDaily.setOnClickListener(v -> {
            selectChip(chipDaily);
            repeatType = "daily";
        });
        chipSelectedDays.setOnClickListener(v -> {
            selectChip(chipSelectedDays);
            repeatType = "selected_days";
            showSelectedDaysDialog();
        });
    }

    private void selectChip(Chip selectedChip) {
        // Reset all chips to unselected state
        resetChipToUnselected(chipOnce);
        resetChipToUnselected(chipDaily);
        resetChipToUnselected(chipSelectedDays);

        // Set selected chip to selected state
        setChipToSelected(selectedChip);
    }

    private void resetChipToUnselected(Chip chip) {
        chip.setChecked(false);
        chip.setTextColor(getResources().getColor(android.R.color.black, null));
        chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(0xFFDBE5F5));
        chip.setChipStrokeColorResource(R.color.divider_color);
        chip.setAlpha(0.7f); // Mờ đi khi không được chọn
    }

    private void setChipToSelected(Chip chip) {
        chip.setChecked(true);
        chip.setTextColor(getResources().getColor(R.color.tag_button_blue, null));
        chip.setChipBackgroundColor(getResources().getColorStateList(android.R.color.white, null));
        chip.setChipStrokeColorResource(R.color.tag_button_blue);
        chip.setAlpha(1.0f); // Sáng lên khi được chọn
    }

    private void setDefaultValues() {
        if (isEditMode && editingEvent != null) {
            // Populate form with existing event data
            populateFormWithEvent(editingEvent);
        } else {
            // Set default values for new event
            // Set default repeat type and styling
            selectChip(chipOnce);

            // Get suggested start time based on last event
            EventJsonManager jsonManager = new EventJsonManager(getContext());
            String suggestedTime = jsonManager.getSuggestedStartTime(selectedDate);

            // Set suggested start time
            selectedStartTime = suggestedTime;
            tvStartTime.setText(suggestedTime);
            tvStartTime.setTextColor(getResources().getColor(R.color.black, null));

            // Set empty end time
            selectedEndTime = "";
            tvEndTime.setText("--:--");
            tvEndTime.setTextColor(getResources().getColor(R.color.gray_text, null));

            // Set default date display
            updateDateDisplay();
            tvSelectedDate.setTextColor(getResources().getColor(R.color.black, null));
        }
    }

    // Public method to set edit mode
    public void setEditMode(Event event) {
        this.isEditMode = true;
        this.editingEvent = event;
    }

    // Public method to set selected date from calendar
    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
    }

    private void populateFormWithEvent(Event event) {
        // Set event name
        etEventName.setText(event.getName());

        // Set times
        selectedStartTime = event.getStartTime();
        selectedEndTime = event.getEndTime();
        tvStartTime.setText(selectedStartTime);
        tvStartTime.setTextColor(getResources().getColor(R.color.black, null));
        tvEndTime.setText(selectedEndTime);
        tvEndTime.setTextColor(getResources().getColor(R.color.black, null));

        // Set duration
        etDuration.setText(event.getDuration());

        // Set date
        selectedDate = event.getDate();
        updateDateDisplay();
        tvSelectedDate.setTextColor(getResources().getColor(R.color.black, null));

        // Set repeat type
        repeatType = event.getRepeatType();
        selectedDays = event.getSelectedDays() != null ? new ArrayList<>(event.getSelectedDays()) : new ArrayList<>();

        switch (repeatType) {
            case "once":
                selectChip(chipOnce);
                break;
            case "daily":
                selectChip(chipDaily);
                break;
            case "selected_days":
                selectChip(chipSelectedDays);
                break;
        }

        // Set notification
        cbNotification.setChecked(event.isNotificationEnabled());

        // Set tag
        selectedTag = event.getTag() != null ? event.getTag() : "";

        // Set location
        etLocation.setText(event.getLocation() != null ? event.getLocation() : "");
    }

    private void showTimePicker(boolean isStartTime) {
        // Get current time as default
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // If editing existing time, parse it
        if (isStartTime && !selectedStartTime.isEmpty()) {
            String[] timeParts = selectedStartTime.split(":");
            if (timeParts.length == 2) {
                try {
                    hour = Integer.parseInt(timeParts[0]);
                    minute = Integer.parseInt(timeParts[1]);
                } catch (NumberFormatException e) {
                    // Use current time as fallback
                }
            }
        } else if (!isStartTime && !selectedEndTime.isEmpty()) {
            String[] timeParts = selectedEndTime.split(":");
            if (timeParts.length == 2) {
                try {
                    hour = Integer.parseInt(timeParts[0]);
                    minute = Integer.parseInt(timeParts[1]);
                } catch (NumberFormatException e) {
                    // Use current time as fallback
                }
            }
        }

        // Create and show custom time picker dialog
        CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(
                getContext(),
                hour,
                minute,
                (selectedHour, selectedMinute) -> {
                    // Validate time is within 24h
                    if (selectedHour >= 24) {
                        CustomToast.showWarning(getContext(), "Giờ không thể vượt quá 23");
                        return;
                    }

                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    if (isStartTime) {
                        selectedStartTime = time;
                        tvStartTime.setText(time);
                        tvStartTime.setTextColor(getResources().getColor(R.color.black, null));

                        // Auto-calculate duration if end time is set
                        if (!selectedEndTime.isEmpty()) {
                            calculateAndUpdateDuration();
                        }

                        // Validate that event doesn't exceed current day
                        validateEventWithinDay();
                    } else {
                        // Validate end time doesn't create event > 24h
                        if (!selectedStartTime.isEmpty()) {
                            int startHour = Integer.parseInt(selectedStartTime.split(":")[0]);
                            int startMinute = Integer.parseInt(selectedStartTime.split(":")[1]);

                            // End time must be after start time in the same day
                            if (selectedHour < startHour
                                    || (selectedHour == startHour && selectedMinute <= startMinute)) {
                                CustomToast.showWarning(getContext(),
                                        "Thời gian kết thúc phải sau thời gian bắt đầu trong cùng ngày");
                                return;
                            }
                        }

                        selectedEndTime = time;
                        tvEndTime.setText(time);
                        tvEndTime.setTextColor(getResources().getColor(R.color.black, null));

                        // Auto-calculate duration if start time is set
                        if (!selectedStartTime.isEmpty()) {
                            calculateAndUpdateDuration();
                        }
                    }
                });

        timePickerDialog.show();
    }

    private void showDatePicker() {
        CustomDatePickerDialog datePickerDialog = new CustomDatePickerDialog(
                getContext(),
                selectedDate,
                date -> {
                    selectedDate = date;
                    updateDateDisplay();
                    tvSelectedDate.setTextColor(getResources().getColor(R.color.black, null));

                    // Update suggested start time when date changes
                    EventJsonManager jsonManager = new EventJsonManager(getContext());
                    String suggestedTime = jsonManager.getSuggestedStartTime(selectedDate);
                    selectedStartTime = suggestedTime;
                    tvStartTime.setText(suggestedTime);
                    tvStartTime.setTextColor(getResources().getColor(R.color.black, null));
                });
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth());
        tvSelectedDate.setText(sdf.format(calendar.getTime()));
    }

    private void showSelectedDaysDialog() {
        String[] daysOfWeek = { "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật" };

        // Create custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomRoundedDialogTheme);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_selected_days, null);
        builder.setView(dialogView);

        // Get views
        LinearLayout daysContainer = dialogView.findViewById(R.id.days_container);
        MaterialButton btnSave = dialogView.findViewById(R.id.btn_save);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // Create checkboxes
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (String day : daysOfWeek) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(day);
            checkBox.setTextSize(16);
            checkBox.setPadding(0, 16, 0, 16);
            checkBox.setChecked(selectedDays.contains(day));
            checkBoxes.add(checkBox);
            daysContainer.addView(checkBox);
        }

        AlertDialog dialog = builder.create();

        // Save button
        btnSave.setOnClickListener(v -> {
            selectedDays.clear();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isChecked()) {
                    selectedDays.add(daysOfWeek[i]);
                }
            }
            if (selectedDays.isEmpty()) {
                selectChip(chipOnce);
                repeatType = "once";
                CustomToast.showWarning(getContext(), "Chưa chọn ngày nào, chuyển về 'Chỉ 1 lần'");
            }
            dialog.dismiss();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            selectChip(chipOnce);
            repeatType = "once";
            dialog.dismiss();
        });

        dialog.show();
    }

    private void calculateAndUpdateDuration() {
        if (selectedStartTime.isEmpty() || selectedEndTime.isEmpty()) {
            return;
        }

        try {
            // Parse start time
            String[] startParts = selectedStartTime.split(":");
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);

            // Parse end time
            String[] endParts = selectedEndTime.split(":");
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            // Calculate duration in minutes
            int startTotalMinutes = startHour * 60 + startMinute;
            int endTotalMinutes = endHour * 60 + endMinute;

            int durationMinutes = endTotalMinutes - startTotalMinutes;

            // Only allow events within the same day
            if (durationMinutes <= 0) {
                CustomToast.showWarning(getContext(), "Sự kiện phải kết thúc trong cùng ngày");
                selectedEndTime = "";
                tvEndTime.setText("--:--");
                tvEndTime.setTextColor(getResources().getColor(R.color.gray_text, null));
                etDuration.setText("");
                return;
            }

            // Format duration
            String durationText = formatDuration(durationMinutes);

            // Update UI without triggering TextWatcher
            isUpdatingDuration = true;
            etDuration.setText(durationText);
            isUpdatingDuration = false;

        } catch (Exception e) {
            // Error parsing time, ignore
        }
    }

    private void updateEndTimeFromDuration() {
        if (selectedStartTime.isEmpty()) {
            return;
        }

        String durationText = etDuration.getText().toString().trim();
        if (durationText.isEmpty()) {
            return;
        }

        try {
            // Parse start time
            String[] startParts = selectedStartTime.split(":");
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);

            // Parse duration
            int durationMinutes = parseDuration(durationText);
            if (durationMinutes <= 0) {
                return;
            }

            // Calculate end time
            int startTotalMinutes = startHour * 60 + startMinute;
            int endTotalMinutes = startTotalMinutes + durationMinutes;

            // Check if end time exceeds 23:59
            if (endTotalMinutes >= 24 * 60) {
                // Limit to 23:59
                endTotalMinutes = 23 * 60 + 59;

                // Recalculate actual duration
                int actualDurationMinutes = endTotalMinutes - startTotalMinutes;
                String actualDurationText = formatDuration(actualDurationMinutes);

                // Update duration field
                isUpdatingDuration = true;
                etDuration.setText(actualDurationText);
                isUpdatingDuration = false;

                CustomToast.showWarning(getContext(), "Sự kiện không thể vượt quá 23:59");
            }

            int endHour = endTotalMinutes / 60;
            int endMinute = endTotalMinutes % 60;

            // Update end time
            selectedEndTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);
            tvEndTime.setText(selectedEndTime);
            tvEndTime.setTextColor(getResources().getColor(R.color.black, null));

        } catch (Exception e) {
            // Error parsing, ignore
        }
    }

    private void validateEventWithinDay() {
        if (selectedStartTime.isEmpty() || selectedEndTime.isEmpty()) {
            return;
        }

        try {
            String[] startParts = selectedStartTime.split(":");
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);

            String[] endParts = selectedEndTime.split(":");
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            // End time must be after start time
            if (endHour < startHour || (endHour == startHour && endMinute <= startMinute)) {
                selectedEndTime = "";
                tvEndTime.setText("--:--");
                tvEndTime.setTextColor(getResources().getColor(R.color.gray_text, null));
                etDuration.setText("");
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    private String formatDuration(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        if (hours == 0) {
            return minutes + " phút";
        } else if (minutes == 0) {
            return hours + " giờ";
        } else {
            return hours + " giờ " + minutes + " phút";
        }
    }

    private int parseDuration(String durationText) {
        try {
            // Parse different formats: "2 giờ 30 phút", "2 giờ", "30 phút"
            durationText = durationText.toLowerCase().trim();

            int totalMinutes = 0;

            // Extract hours
            if (durationText.contains("giờ")) {
                String[] parts = durationText.split("giờ");
                if (parts.length > 0) {
                    String hourPart = parts[0].trim();
                    totalMinutes += Integer.parseInt(hourPart) * 60;

                    // Check for minutes after hours
                    if (parts.length > 1 && parts[1].contains("phút")) {
                        String minutePart = parts[1].replace("phút", "").trim();
                        if (!minutePart.isEmpty()) {
                            totalMinutes += Integer.parseInt(minutePart);
                        }
                    }
                }
            } else if (durationText.contains("phút")) {
                // Only minutes
                String minutePart = durationText.replace("phút", "").trim();
                totalMinutes = Integer.parseInt(minutePart);
            } else {
                // Try to parse as just a number (assume minutes)
                totalMinutes = Integer.parseInt(durationText);
            }

            return totalMinutes;
        } catch (Exception e) {
            return 0;
        }
    }

    private void saveEvent() {
        String eventName = etEventName.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (eventName.isEmpty()) {
            CustomToast.showWarning(getContext(), "Vui lòng nhập tên sự kiện");
            return;
        }

        if (selectedStartTime.isEmpty() || selectedEndTime.isEmpty()) {
            CustomToast.showWarning(getContext(), "Vui lòng chọn thời gian");
            return;
        }

        // Validate that start time is before end time (for same day events)
        try {
            String[] startParts = selectedStartTime.split(":");
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);

            String[] endParts = selectedEndTime.split(":");
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            if (endHour < startHour || (endHour == startHour && endMinute <= startMinute)) {
                CustomToast.showWarning(getContext(), "Thời gian kết thúc phải sau thời gian bắt đầu trong cùng ngày");
                return;
            }
        } catch (Exception e) {
            CustomToast.showWarning(getContext(), "Định dạng thời gian không hợp lệ");
            return;
        }

        // Validate time format and range
        if (!isValidTimeFormat(selectedStartTime) || !isValidTimeFormat(selectedEndTime)) {
            CustomToast.showWarning(getContext(), "Định dạng thời gian không hợp lệ");
            return;
        }

        EventJsonManager jsonManager = new EventJsonManager(getContext());
        Event event;

        if (isEditMode && editingEvent != null) {
            // Update existing event
            event = editingEvent;
            event.setName(eventName);
            event.setStartTime(selectedStartTime);
            event.setEndTime(selectedEndTime);
            event.setDuration(duration);
            event.setDate(selectedDate);
            event.setRepeatType(repeatType);
            event.setSelectedDays(new ArrayList<>(selectedDays));
            event.setNotificationEnabled(cbNotification.isChecked());
            event.setTag(selectedTag);
            event.setLocation(location);

            // Check for time conflicts (exclude current event)
            if (jsonManager.hasTimeConflict(event)) {
                Event conflictingEvent = jsonManager.getConflictingEvent(event);
                String message = conflictingEvent != null
                        ? String.format("Xung đột thời gian với sự kiện '%s' (%s - %s)",
                                conflictingEvent.getName(),
                                conflictingEvent.getStartTime(),
                                conflictingEvent.getEndTime())
                        : "Sự kiện bị xung đột thời gian với sự kiện khác";

                CustomToast.showWarning(getContext(), message);
                return;
            }

            jsonManager.updateEvent(event);

        } else {
            // Create new event
            event = new Event();
            event.setName(eventName);
            event.setStartTime(selectedStartTime);
            event.setEndTime(selectedEndTime);
            event.setDuration(duration);
            event.setDate(selectedDate);
            event.setRepeatType(repeatType);
            event.setSelectedDays(new ArrayList<>(selectedDays));
            event.setNotificationEnabled(cbNotification.isChecked());
            event.setTag(selectedTag);
            event.setLocation(location);
            event.setCompleted(false); // Mặc định chưa hoàn thành

            // Check for time conflicts
            if (jsonManager.hasTimeConflict(event)) {
                Event conflictingEvent = jsonManager.getConflictingEvent(event);
                String message = conflictingEvent != null
                        ? String.format("Xung đột thời gian với sự kiện '%s' (%s - %s)",
                                conflictingEvent.getName(),
                                conflictingEvent.getStartTime(),
                                conflictingEvent.getEndTime())
                        : "Sự kiện bị xung đột thời gian với sự kiện khác";

                CustomToast.showWarning(getContext(), message);
                return;
            }

            // Save event
            jsonManager.saveEvent(event);
        }

        // Notify MainActivity và close fragment
        if (requireActivity() instanceof OnFragmentActionListener) {
            if (isEditMode) {
                ((OnFragmentActionListener) requireActivity()).onEventSaved("edit");
            } else {
                ((OnFragmentActionListener) requireActivity()).onEventSaved("add");
            }
        } else {
            closeFragment();
        }
    }

    private void showDeleteConfirmDialog() {
        if (!isEditMode || editingEvent == null) {
            return;
        }

        // Create custom dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(
                getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_confirmation, null);
        builder.setView(dialogView);

        // Get views
        TextView tvMessage = dialogView.findViewById(R.id.tv_message);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btn_confirm);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // Set message with event name
        tvMessage.setText("Bạn có chắc muốn xóa sự kiện này không?");

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        // Make dialog background transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set button listeners
        btnConfirm.setOnClickListener(v -> {
            deleteEvent();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void deleteEvent() {
        if (!isEditMode || editingEvent == null) {
            return;
        }

        EventJsonManager jsonManager = new EventJsonManager(getContext());
        jsonManager.deleteEvent(editingEvent.getId());

        // Notify MainActivity và close fragment
        if (requireActivity() instanceof OnFragmentActionListener) {
            ((OnFragmentActionListener) requireActivity()).onEventSaved("delete");
        } else {
            closeFragment();
        }
    }

    private void closeFragment() {
        if (requireActivity() instanceof OnFragmentActionListener) {
            ((OnFragmentActionListener) requireActivity()).onFragmentClosed();
        } else {
            // Fallback: pop fragment from back stack
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private boolean isValidTimeFormat(String time) {
        if (time == null || time.isEmpty()) {
            return false;
        }

        String[] parts = time.split(":");
        if (parts.length != 2) {
            return false;
        }

        try {
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // Hour must be 0-23, minute must be 0-59
            return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}