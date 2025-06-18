package com.example.tlu_routine.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tlu_routine.R;

public class CustomToast {

    public static void showSuccess(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_success_toast, null);

        TextView textView = layout.findViewById(R.id.tv_toast_message);
        textView.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 250);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void showWarning(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_warning_toast, null);

        TextView textView = layout.findViewById(R.id.tv_toast_message);
        textView.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 250);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void showError(Context context, String message) {
        // Có thể implement sau nếu cần
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}