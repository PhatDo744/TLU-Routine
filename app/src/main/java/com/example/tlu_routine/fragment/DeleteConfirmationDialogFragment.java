package com.example.tlu_routine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tlu_routine.R;
import com.google.android.material.button.MaterialButton;

public class DeleteConfirmationDialogFragment extends DialogFragment {

    private String tagName;
    private int tagPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tagName = getArguments().getString("tag_name");
            tagPosition = getArguments().getInt("tag_position", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_delete_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView messageView = view.findViewById(R.id.tv_delete_message);
        MaterialButton confirmButton = view.findViewById(R.id.btn_confirm_delete);
        MaterialButton cancelButton = view.findViewById(R.id.btn_cancel_delete);

        // Hiển thị thông điệp xóa với tên thẻ
        String message = getString(R.string.delete_confirmation_message, tagName);
        messageView.setText(message);

        // Sự kiện click nút xác nhận
        confirmButton.setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putInt("position_to_delete", tagPosition);
            getParentFragmentManager().setFragmentResult("delete_request", result);
            dismiss();
        });

        // Sự kiện click nút hủy
        cancelButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
