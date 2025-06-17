package com.example.tlu_routine.fragment;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tlu_routine.R;
import com.example.tlu_routine.adapter.NotificationAdapter;
import java.util.ArrayList;
import java.util.List;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.widget.ImageButton;

public class NotificationFragment extends Fragment {
    private NotificationAdapter adapter;
    private List<NotificationAdapter.NotificationAdapterItem> notificationList;
    private View emptyStateLayout;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = view.findViewById(R.id.recyclerNotification);
        emptyStateLayout = view.findViewById(R.id.empty_state_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList = getSampleNotifications();
        adapter = new NotificationAdapter(notificationList);
        adapter.setOnItemClickListener(item -> openDetailFragment(item, notificationList.indexOf(item)));
        adapter.setOnDeleteAllClickListener(this::deleteAllNotificationsWithAnimation);
        recyclerView.setAdapter(adapter);
        attachSwipeToDelete(recyclerView);

        ImageButton btnSettings = view.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, new NotificationSettingsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Listener để nhận kết quả từ NotificationDetailFragment
        getParentFragmentManager().setFragmentResultListener(
                NotificationDetailFragment.REQUEST_KEY_NOTIFICATION_DELETED,
                this,
                (requestKey, bundle) -> {
                    int deletedPosition = bundle.getInt(NotificationDetailFragment.BUNDLE_KEY_DELETED_POSITION, -1);
                    if (deletedPosition != -1 && deletedPosition < notificationList.size()) {
                        // Ensure we are removing a NotificationItem, not a header
                        if (notificationList.get(deletedPosition) instanceof NotificationAdapter.NotificationItem) {
                            notificationList.remove(deletedPosition);
                            adapter.notifyItemRemoved(deletedPosition);
                            updateUI();
                        }
                    }
                }
        );

        updateUI();

        return view;
    }

    private void updateUI() {
        // Check only for actual notification items, ignoring headers for empty state
        boolean hasNotificationItems = false;
        for (NotificationAdapter.NotificationAdapterItem item : notificationList) {
            if (item instanceof NotificationAdapter.NotificationItem) {
                hasNotificationItems = true;
                break;
            }
        }

        if (!hasNotificationItems) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
        }
    }

    private void showEmptyState(boolean show) {
        if (show) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void deleteAllNotificationsWithAnimation() {
        Handler handler = new Handler(Looper.getMainLooper());
        List<NotificationAdapter.NotificationAdapterItem> itemsToRemove = new ArrayList<>();
        for (NotificationAdapter.NotificationAdapterItem item : notificationList) {
            if (item instanceof NotificationAdapter.NotificationItem) {
                itemsToRemove.add(item);
            }
        }

        int initialSize = itemsToRemove.size();
        for (int i = 0; i < initialSize; i++) {
            final int positionToRemove = notificationList.indexOf(itemsToRemove.get(i));
            handler.postDelayed(() -> {
                if (positionToRemove != -1 && positionToRemove < notificationList.size()) {
                    // Only remove if it's an actual notification item and not a header
                    if (notificationList.get(positionToRemove) instanceof NotificationAdapter.NotificationItem) {
                        notificationList.remove(positionToRemove);
                        adapter.notifyItemRemoved(positionToRemove);
                        updateUI();
                    }
                }
            }, 200L * i);
        }
    }

    private List<NotificationAdapter.NotificationAdapterItem> getSampleNotifications() {
        List<NotificationAdapter.NotificationAdapterItem> list = new ArrayList<>();

        // Hôm nay (Today) notifications
        list.add(new NotificationAdapter.NotificationHeader("HÔM NAY"));
        list.add(new NotificationAdapter.NotificationItem(R.drawable.ic_calendar, "Nhắc nhở: Họp team hàng tuần", "Sự kiện sẽ bắt đầu trong 15 phút", "14:30", true, true, R.drawable.bg_notification_active));
        list.add(new NotificationAdapter.NotificationItem(R.drawable.ic_calendar, "Sự kiện đã hoàn thành", "Presentation cho khách hàng đã kết thúc", "10:00", false, false, R.drawable.bg_notification_done));

        // Hôm qua (Yesterday) notifications
        list.add(new NotificationAdapter.NotificationHeader("HÔM QUA"));
        list.add(new NotificationAdapter.NotificationItem(R.drawable.ic_alarm, "Nhắc nhở: Deadline dự án", "Còn 2 ngày để hoàn thành báo cáo", "16:45", false, false, R.drawable.bg_notification_reminder));
        list.add(new NotificationAdapter.NotificationItem(R.drawable.ic_alarm, "Nhắc nhở: Deadline dự án", "Còn 2 ngày để hoàn thành báo cáo", "16:45", false, false, R.drawable.bg_notification_reminder));
        list.add(new NotificationAdapter.NotificationItem(R.drawable.ic_alarm, "Nhắc nhở: Deadline dự án", "Còn 2 ngày để hoàn thành báo cáo", "16:45", false, false, R.drawable.bg_notification_reminder));
        return list;
    }

    private void attachSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                // Only allow swipe to delete for NotificationItem, not headers or footer
                if (adapter.getItemViewType(position) == NotificationAdapter.VIEW_TYPE_ITEM) {
                    notificationList.remove(position);
                    adapter.notifyItemRemoved(position);
                    updateUI();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Only draw swipe background for NotificationItem
                if (adapter.getItemViewType(viewHolder.getAdapterPosition()) == NotificationAdapter.VIEW_TYPE_ITEM) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    View itemView = viewHolder.itemView;
                    Drawable icon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete);
                    int iconMargin = (itemView.getHeight() - (icon != null ? icon.getIntrinsicHeight() : 0)) / 2;
                    if (dX < 0 && icon != null) { // Vuốt sang trái
                        int iconTop = itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                        int iconRight = itemView.getRight() - iconMargin;
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }
                }
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    private void openDetailFragment(NotificationAdapter.NotificationItem item, int position) {
        NotificationDetailFragment fragment = new NotificationDetailFragment();
        Bundle args = new Bundle();
        args.putString(NotificationDetailFragment.ARG_TITLE, item.title);
        args.putString(NotificationDetailFragment.ARG_CONTENT, item.content);
        args.putString(NotificationDetailFragment.ARG_TIME, item.time);
        args.putInt(NotificationDetailFragment.ARG_ICON, item.iconRes);
        args.putInt(NotificationDetailFragment.ARG_POSITION, position);
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
} 