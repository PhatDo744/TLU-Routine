package com.example.tlu_routine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tlu_routine.R;

public class NotificationDetailFragment extends Fragment {
    public static final String ARG_TITLE = "title";
    public static final String ARG_CONTENT = "content";
    public static final String ARG_TIME = "time";
    public static final String ARG_ICON = "icon";
    public static final String ARG_POSITION = "position";

    public static final String REQUEST_KEY_NOTIFICATION_DELETED = "notification_deleted";
    public static final String BUNDLE_KEY_DELETED_POSITION = "deleted_position";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_detail, container, false);
        Bundle args = getArguments();
        int position = -1; // Default to invalid position

        if (args != null) {
            ((TextView) view.findViewById(R.id.tvDetailTitle)).setText(args.getString(ARG_TITLE, ""));
            ((TextView) view.findViewById(R.id.tvDetailDescription)).setText(args.getString(ARG_CONTENT, ""));
            ((TextView) view.findViewById(R.id.tvDetailTimeRange)).setText(args.getString(ARG_TIME, ""));
            ((ImageView) view.findViewById(R.id.imgDetailIcon)).setImageResource(args.getInt(ARG_ICON, R.drawable.ic_calendar));
            position = args.getInt(ARG_POSITION, -1);
        }

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        Button btnDeleteNotification = view.findViewById(R.id.btnDeleteNotification);
        int finalPosition = position;
        btnDeleteNotification.setOnClickListener(v -> {
            if (finalPosition != -1) {
                Bundle result = new Bundle();
                result.putInt(BUNDLE_KEY_DELETED_POSITION, finalPosition);
                getParentFragmentManager().setFragmentResult(REQUEST_KEY_NOTIFICATION_DELETED, result);
            }
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
} 