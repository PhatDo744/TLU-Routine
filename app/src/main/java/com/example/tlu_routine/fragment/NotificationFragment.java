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
import android.content.Context;

public class NotificationFragment extends Fragment implements NotificationAdapter.OnDeleteButtonClickListener {
    private NotificationAdapter adapter;
    private List<NotificationAdapter.NotificationAdapterItem> notificationList;
    private View emptyStateLayout;
    private RecyclerView recyclerView;
    private RecyclerView.ViewHolder currentlySwipedViewHolder;

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
        adapter.setOnDeleteButtonClickListener(this);
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
                    long deletedNotificationId = bundle.getLong(NotificationDetailFragment.BUNDLE_KEY_DELETED_ID, -1L);

                    int foundPosition = -1;
                    if (deletedNotificationId != -1L) {
                        for (int i = 0; i < notificationList.size(); i++) {
                            NotificationAdapter.NotificationAdapterItem item = notificationList.get(i);
                            if (item instanceof NotificationAdapter.NotificationItem) {
                                NotificationAdapter.NotificationItem notificationItem = (NotificationAdapter.NotificationItem) item;
                                if (notificationItem.id == deletedNotificationId) {
                                    foundPosition = i;
                                    break;
                                }
                            }
                        }
                    }

                    if (foundPosition != -1) {
                        try {
                            notificationList.remove(foundPosition);
                            adapter.notifyItemRemoved(foundPosition);
                            updateUI();
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            updateUI(); // Fallback to updating UI to refresh state
                        }
                    } else {
                        // Item not found, likely already removed or list changed, just update UI
                        System.err.println("Notification item with ID " + deletedNotificationId + " not found for deletion, or already removed.");
                        updateUI();
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
        // Clear all actual notification items from the list
        List<NotificationAdapter.NotificationAdapterItem> headersAndFooters = new ArrayList<>();
        for (NotificationAdapter.NotificationAdapterItem item : notificationList) {
            if (item instanceof NotificationAdapter.NotificationHeader || item instanceof NotificationAdapter.FooterViewHolder) {
                headersAndFooters.add(item);
            }
        }
        notificationList.clear();
        notificationList.addAll(headersAndFooters);

        // Notify the adapter of the complete data set change
        adapter.notifyDataSetChanged();
        // Update UI to reflect the empty state if no notifications are left
        updateUI();
        System.out.println("All notification items cleared and UI updated.");
    }

    private List<NotificationAdapter.NotificationAdapterItem> getSampleNotifications() {
        List<NotificationAdapter.NotificationAdapterItem> list = new ArrayList<>();
        long currentId = 0; // Start ID counter

        // Hôm nay (Today) notifications
        list.add(new NotificationAdapter.NotificationHeader("HÔM NAY"));
        list.add(new NotificationAdapter.NotificationItem(currentId++, R.drawable.ic_calendar, "Nhắc nhở: Họp team hàng tuần", "Sự kiện sẽ bắt đầu trong 15 phút", "14:30", true, true, R.drawable.bg_notification_active));
        list.add(new NotificationAdapter.NotificationItem(currentId++, R.drawable.ic_calendar, "Sự kiện đã hoàn thành", "Presentation cho khách hàng đã kết thúc", "10:00", false, false, R.drawable.bg_notification_done));

        // Hôm qua (Yesterday) notifications
        list.add(new NotificationAdapter.NotificationHeader("HÔM QUA"));
        list.add(new NotificationAdapter.NotificationItem(currentId++, R.drawable.ic_alarm, "Nhắc nhở: Deadline dự án", "Còn 2 ngày để hoàn thành báo cáo", "16:45", false, false, R.drawable.bg_notification_reminder));
        list.add(new NotificationAdapter.NotificationItem(currentId++, R.drawable.ic_alarm, "Nhắc nhở: Deadline dự án", "Còn 2 ngày để hoàn thành báo cáo", "16:45", false, false, R.drawable.bg_notification_reminder));
        list.add(new NotificationAdapter.NotificationItem(currentId++, R.drawable.ic_alarm, "Nhắc nhở: Deadline dự án", "Còn 2 ngày để hoàn thành báo cáo", "16:45", false, false, R.drawable.bg_notification_reminder));
        return list;
    }

    private void attachSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private float initialDx = 0f;

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (adapter.getItemViewType(viewHolder.getAdapterPosition()) == NotificationAdapter.VIEW_TYPE_ITEM) {
                    return makeMovementFlags(0, ItemTouchHelper.LEFT);
                }
                return 0; // Disable swipe for headers and footers
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    System.out.println("Swiped item has NO_POSITION, ignoring.");
                    return;
                }

                System.out.println("Swiped item at adapter position: " + position + ", ViewType: " + adapter.getItemViewType(position));

                if (adapter.getItemViewType(position) == NotificationAdapter.VIEW_TYPE_ITEM) {
                    NotificationAdapter.NotificationItem swipedItem = null;
                    if (position < notificationList.size() && notificationList.get(position) instanceof NotificationAdapter.NotificationItem) {
                        swipedItem = (NotificationAdapter.NotificationItem) notificationList.get(position);
                    }

                    if (swipedItem != null) {
                        // If another item was swiped, revert its state first
                        if (currentlySwipedViewHolder != null && currentlySwipedViewHolder != viewHolder) {
                            int oldPosition = currentlySwipedViewHolder.getAdapterPosition();
                            if (oldPosition != RecyclerView.NO_POSITION && oldPosition < notificationList.size() && notificationList.get(oldPosition) instanceof NotificationAdapter.NotificationItem) {
                                adapter.setNotificationSwipedState(((NotificationAdapter.NotificationItem) notificationList.get(oldPosition)).id, false);
                            }
                        }

                        // Set the new item's swiped state
                        adapter.setNotificationSwipedState(swipedItem.id, true);
                        currentlySwipedViewHolder = viewHolder; // Track this item
                        System.out.println("Item swiped: showing delete button for ID: " + swipedItem.id);
                    } else {
                        System.err.println("Swiped item at position " + position + " is not a valid NotificationItem. Reverting swipe.");
                        adapter.notifyItemChanged(position);
                    }
                } else {
                    System.out.println("Swiped a header or footer at position " + position + ". Reverting swipe.");
                    adapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (viewHolder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                    System.out.println("onChildDraw: View holder has NO_POSITION, skipping draw.");
                    return;
                }

                if (adapter.getItemViewType(viewHolder.getAdapterPosition()) == NotificationAdapter.VIEW_TYPE_ITEM) {
                    // Control the translation of the content area
                    final View foregroundView = ((NotificationAdapter.NotificationViewHolder) viewHolder).contentArea;
                    getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
                } else {
                    // For headers/footers, just call super if they are not meant to be swiped
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (viewHolder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                    return;
                }

                if (adapter.getItemViewType(viewHolder.getAdapterPosition()) == NotificationAdapter.VIEW_TYPE_ITEM) {
                    final View foregroundView = ((NotificationAdapter.NotificationViewHolder) viewHolder).contentArea;
                    getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((NotificationAdapter.NotificationViewHolder) viewHolder).contentArea;
                getDefaultUIUtil().clearView(foregroundView);
                // Reset swipe state when clearView is called, unless it's the currently swiped one
                if (currentlySwipedViewHolder != viewHolder) {
                    int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && position < notificationList.size() && notificationList.get(position) instanceof NotificationAdapter.NotificationItem) {
                        adapter.setNotificationSwipedState(((NotificationAdapter.NotificationItem) notificationList.get(position)).id, false);
                    }
                }
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                // Only trigger swipe if it goes past a certain threshold (e.g., 0.5 of item width)
                return 0.5f;
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue) {
                // Set a lower escape velocity to make it easier to reveal the button
                return 0.1f * defaultValue;
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // Allow swiping only for NotificationItem (not headers or footers)
                if (adapter.getItemViewType(viewHolder.getAdapterPosition()) == NotificationAdapter.VIEW_TYPE_ITEM) {
                    return ItemTouchHelper.LEFT;
                }
                return 0;
            }

            @Override
            public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                if (currentlySwipedViewHolder != null) {
                    // If there's an item swiped open, restrict further swipes until it's closed
                    int position = currentlySwipedViewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && adapter.getItemViewType(position) == NotificationAdapter.VIEW_TYPE_ITEM) {
                        NotificationAdapter.NotificationItem item = (NotificationAdapter.NotificationItem) notificationList.get(position);
                        if (item.isSwiped) {
                            return 0; // Block further swipes if an item is already swiped
                        }
                    }
                }
                return super.convertToAbsoluteDirection(flags, layoutDirection);
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
        args.putLong(NotificationDetailFragment.ARG_NOTIFICATION_ID, item.id);
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDeleteButtonClick(long notificationId, int adapterPosition) {
        // Reset the swiped state of the currently swiped view holder
        if (currentlySwipedViewHolder != null) {
            int oldPosition = currentlySwipedViewHolder.getAdapterPosition();
            if (oldPosition != RecyclerView.NO_POSITION && oldPosition < notificationList.size() && notificationList.get(oldPosition) instanceof NotificationAdapter.NotificationItem) {
                adapter.setNotificationSwipedState(((NotificationAdapter.NotificationItem) notificationList.get(oldPosition)).id, false);
            }
            currentlySwipedViewHolder = null; // Clear the tracked view holder
        }

        int foundPosition = -1;
        for (int i = 0; i < notificationList.size(); i++) {
            NotificationAdapter.NotificationAdapterItem item = notificationList.get(i);
            if (item instanceof NotificationAdapter.NotificationItem) {
                NotificationAdapter.NotificationItem notificationItem = (NotificationAdapter.NotificationItem) item;
                if (notificationItem.id == notificationId) {
                    foundPosition = i;
                    break;
                }
            }
        }

        if (foundPosition != -1) {
            try {
                notificationList.remove(foundPosition);
                adapter.notifyItemRemoved(foundPosition);
                updateUI();
                System.out.println("Notification with ID " + notificationId + " deleted by button click.");
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                adapter.notifyDataSetChanged(); // Fallback if removal causes issues
                updateUI();
            }
        } else {
            System.err.println("Notification with ID " + notificationId + " not found for button deletion.");
            updateUI();
        }
    }

    public static List<NotificationAdapter.NotificationAdapterItem> getSampleNotificationsStatic(Context context) {
        List<NotificationAdapter.NotificationAdapterItem> list = new ArrayList<>();
        long currentId = 0;
        list.add(new NotificationAdapter.NotificationHeader("HÔM NAY"));
        list.add(new NotificationAdapter.NotificationItem(currentId++, com.example.tlu_routine.R.drawable.ic_calendar, "Nhắc nhở: Họp team hàng tuần", "Sự kiện sẽ bắt đầu trong 15 phút", "14:30", true, true, com.example.tlu_routine.R.drawable.bg_notification_active));
        list.add(new NotificationAdapter.NotificationItem(currentId++, com.example.tlu_routine.R.drawable.ic_calendar, "Sự kiện đã hoàn thành", "Presentation cho khách hàng đã kết thúc", "10:00", false, false, com.example.tlu_routine.R.drawable.bg_notification_done));
        list.add(new NotificationAdapter.NotificationHeader("HÔM QUA"));
        list.add(new NotificationAdapter.NotificationItem(currentId++, com.example.tlu_routine.R.drawable.ic_alarm, "Nhắc nhở: Deadline dự án", "Còn 2 ngày để hoàn thành báo cáo", "16:45", false, false, com.example.tlu_routine.R.drawable.bg_notification_reminder));
        list.add(new NotificationAdapter.NotificationItem(currentId++, com.example.tlu_routine.R.drawable.ic_alarm, "Nhắc nhở: Deadline dự án", "Còn 2 ngày để hoàn thành báo cáo", "16:45", false, false, com.example.tlu_routine.R.drawable.bg_notification_reminder));
        list.add(new NotificationAdapter.NotificationItem(currentId++, com.example.tlu_routine.R.drawable.ic_alarm, "Nhắc nhở: Deadline dự án", "Còn 2 ngày để hoàn thành báo cáo", "16:45", false, false, com.example.tlu_routine.R.drawable.bg_notification_reminder));
        return list;
    }
} 