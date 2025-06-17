package com.example.tlu_routine.fragment;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tlu_routine.R;

public class NotificationSettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_settings, container, false);
        Switch switchPush = view.findViewById(R.id.switchPush);
        Switch switchSound = view.findViewById(R.id.switchSound);
        Switch switchStudy = view.findViewById(R.id.switchWork);
        Switch switchWorkUrgent = view.findViewById(R.id.switchPersonal);
        Switch switchPersonalNew = view.findViewById(R.id.switchPersonalNew);
        Switch switchExam = view.findViewById(R.id.switchExam);
        View cardReminderTime = view.findViewById(R.id.cardReminderTime);
        TextView tvDefaultReminder = view.findViewById(R.id.tvDefaultReminder);

        // Màu xanh nước biển và xám
        int blue = Color.parseColor("#2979FF");
        int gray = Color.parseColor("#BDBDBD");
        ColorStateList thumbStates = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{-android.R.attr.state_checked}
                },
                new int[]{blue, gray}
        );
        ColorStateList trackStates = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{-android.R.attr.state_checked}
                },
                new int[]{Color.parseColor("#803979FF"), Color.parseColor("#33000000")}
        );
        // Áp dụng màu cho tất cả switch
        for (Switch sw : new Switch[]{switchPush, switchSound, switchStudy, switchWorkUrgent, switchPersonalNew, switchExam}) {
            sw.setThumbTintList(thumbStates);
            sw.setTrackTintList(trackStates);
        }

        // Lưu trạng thái ban đầu
        final boolean[] initialSound = {switchSound.isChecked()};
        final boolean[] initialStudy = {switchStudy.isChecked()};
        final boolean[] initialWorkUrgent = {switchWorkUrgent.isChecked()};
        final boolean[] initialPersonalNew = {switchPersonalNew.isChecked()};
        final boolean[] initialExam = {switchExam.isChecked()};

        switchPush.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                switchStudy.setChecked(false);
                switchWorkUrgent.setChecked(false);
                switchPersonalNew.setChecked(false);
                switchExam.setChecked(false);
                switchSound.setChecked(false);
                switchStudy.setEnabled(false);
                switchWorkUrgent.setEnabled(false);
                switchPersonalNew.setEnabled(false);
                switchExam.setEnabled(false);
                switchSound.setEnabled(false);
            } else {
                switchStudy.setEnabled(true);
                switchWorkUrgent.setEnabled(true);
                switchPersonalNew.setEnabled(true);
                switchExam.setEnabled(true);
                switchSound.setEnabled(true);
                switchStudy.setChecked(initialStudy[0]);
                switchWorkUrgent.setChecked(initialWorkUrgent[0]);
                switchPersonalNew.setChecked(initialPersonalNew[0]);
                switchExam.setChecked(initialExam[0]);
                switchSound.setChecked(initialSound[0]);
            }
        });

        cardReminderTime.setOnClickListener(v -> {
            showReminderTimeDialog(tvDefaultReminder);
        });

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void showReminderTimeDialog(TextView tvDefaultReminder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reminder_time, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        // Make sure the dialog has a transparent background to show the custom rounded drawable
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        RadioGroup rg = dialogView.findViewById(R.id.rgReminderOptions);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        // Chọn đúng radio theo giá trị hiện tại
        String current = tvDefaultReminder.getText().toString();
        if (current.contains("5")) rg.check(R.id.rb5min);
        else if (current.contains("15")) rg.check(R.id.rb15min);
        else if (current.contains("30")) rg.check(R.id.rb30min);
        else if (current.contains("1 giờ")) rg.check(R.id.rb1hour);
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            int checkedId = rg.getCheckedRadioButtonId();
            RadioButton rb = dialogView.findViewById(checkedId);
            if (rb != null) {
                tvDefaultReminder.setText(rb.getText());
            }
            dialog.dismiss();
        });
        dialog.show();
    }
} 