package com.example.tlu_routine.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tlu_routine.R;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class CustomTimePickerDialog extends Dialog {

    public interface OnTimeSetListener {
        void onTimeSet(int hourOfDay, int minute);
    }

    private EditText etHour;
    private EditText etMinute;
    private MaterialButton btnSave;
    private MaterialButton btnCancel;

    private OnTimeSetListener onTimeSetListener;
    private int initialHour;
    private int initialMinute;

    public CustomTimePickerDialog(@NonNull Context context, int hour, int minute, OnTimeSetListener listener) {
        super(context, R.style.CustomRoundedDialogTheme);
        this.initialHour = hour;
        this.initialMinute = minute;
        this.onTimeSetListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove default title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_time_picker);

        // Apply rounded corners
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }

        initViews();
        setupListeners();
        setInitialValues();

        // Make dialog cancelable
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    private void initViews() {
        etHour = findViewById(R.id.et_hour);
        etMinute = findViewById(R.id.et_minute);
        btnSave = findViewById(R.id.btn_save_time);
        btnCancel = findViewById(R.id.btn_cancel_time);
    }

    private void setupListeners() {
        // Hour input validation
        etHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateHourInput(s.toString());
            }
        });

        // Minute input validation
        etMinute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateMinuteInput(s.toString());
            }
        });

        // Save button
        btnSave.setOnClickListener(v -> {
            if (validateAndSaveTime()) {
                dismiss();
            }
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void setInitialValues() {
        etHour.setText(String.format(Locale.getDefault(), "%02d", initialHour));
        etMinute.setText(String.format(Locale.getDefault(), "%02d", initialMinute));
    }

    private void validateHourInput(String input) {
        if (input.isEmpty())
            return;

        try {
            int hour = Integer.parseInt(input);
            if (hour > 23) {
                etHour.setText("23");
                etHour.setSelection(2);
            } else if (input.length() == 2 && hour < 10 && !input.startsWith("0")) {
                etHour.setText(String.format(Locale.getDefault(), "%02d", hour));
                etHour.setSelection(2);
            }
        } catch (NumberFormatException e) {
            // Invalid input, ignore
        }
    }

    private void validateMinuteInput(String input) {
        if (input.isEmpty())
            return;

        try {
            int minute = Integer.parseInt(input);
            if (minute > 59) {
                etMinute.setText("59");
                etMinute.setSelection(2);
            } else if (input.length() == 2 && minute < 10 && !input.startsWith("0")) {
                etMinute.setText(String.format(Locale.getDefault(), "%02d", minute));
                etMinute.setSelection(2);
            }
        } catch (NumberFormatException e) {
            // Invalid input, ignore
        }
    }

    private boolean validateAndSaveTime() {
        String hourText = etHour.getText().toString().trim();
        String minuteText = etMinute.getText().toString().trim();

        if (hourText.isEmpty() || minuteText.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ giờ và phút", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int hour = Integer.parseInt(hourText);
            int minute = Integer.parseInt(minuteText);

            if (hour < 0 || hour > 23) {
                Toast.makeText(getContext(), "Giờ phải từ 00 đến 23", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (minute < 0 || minute > 59) {
                Toast.makeText(getContext(), "Phút phải từ 00 đến 59", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Format with leading zeros
            etHour.setText(String.format(Locale.getDefault(), "%02d", hour));
            etMinute.setText(String.format(Locale.getDefault(), "%02d", minute));

            // Call listener
            if (onTimeSetListener != null) {
                onTimeSetListener.onTimeSet(hour, minute);
            }

            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Định dạng thời gian không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void setOnTimeSetListener(OnTimeSetListener listener) {
        this.onTimeSetListener = listener;
    }
}