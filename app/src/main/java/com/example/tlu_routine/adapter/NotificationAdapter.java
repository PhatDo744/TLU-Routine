package com.example.tlu_routine.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlu_routine.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_FOOTER = 1;
    public static final int VIEW_TYPE_HEADER = 2;

    public interface NotificationAdapterItem {}

    public static class NotificationItem implements NotificationAdapterItem {
        public long id;
        public int iconRes;
        public String title;
        public String content;
        public String time;
        public boolean isActive;
        public boolean isDotVisible;
        public int backgroundRes;
        public boolean isSwiped;

        public NotificationItem(long id, int iconRes, String title, String content, String time, boolean isActive, boolean isDotVisible, int backgroundRes) {
            this.id = id;
            this.iconRes = iconRes;
            this.title = title;
            this.content = content;
            this.time = time;
            this.isActive = isActive;
            this.isDotVisible = isDotVisible;
            this.backgroundRes = backgroundRes;
            this.isSwiped = false;
        }

        public void setSwiped(boolean swiped) {
            isSwiped = swiped;
        }
    }

    public static class NotificationHeader implements NotificationAdapterItem {
        public String headerText;

        public NotificationHeader(String headerText) {
            this.headerText = headerText;
        }
    }

    private List<NotificationAdapterItem> items;
    private OnItemClickListener itemClickListener;
    private OnDeleteAllClickListener deleteAllClickListener;
    private OnDeleteButtonClickListener deleteButtonClickListener;

    public interface OnItemClickListener {
        void onItemClick(NotificationItem item);
    }

    public interface OnDeleteAllClickListener {
        void onDeleteAllClick();
    }

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClick(long notificationId, int adapterPosition);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnDeleteAllClickListener(OnDeleteAllClickListener listener) {
        this.deleteAllClickListener = listener;
    }

    public void setOnDeleteButtonClickListener(OnDeleteButtonClickListener listener) {
        this.deleteButtonClickListener = listener;
    }

    public NotificationAdapter(List<NotificationAdapterItem> items) {
        this.items = items;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            return ((NotificationItem) items.get(position)).id;
        } else if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            // Return a stable ID for headers. Using a negative value to avoid conflict with actual item IDs.
            return RecyclerView.NO_ID - 1 - position; // Unique stable ID for each header based on its position
        } else if (getItemViewType(position) == VIEW_TYPE_FOOTER) {
            // Return a stable ID for the footer. Using a distinct negative value.
            return RecyclerView.NO_ID - 2; // A single stable ID for the footer
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == items.size()) {
            return VIEW_TYPE_FOOTER;
        } else if (items.get(position) instanceof NotificationHeader) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        } else if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delete_all_button, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            NotificationViewHolder notificationHolder = (NotificationViewHolder) holder;
            NotificationItem item = (NotificationItem) items.get(position);
            notificationHolder.imgIcon.setImageResource(item.iconRes);
            notificationHolder.tvTitle.setText(item.title);
            notificationHolder.tvContent.setText(item.content);
            notificationHolder.tvTime.setText(item.time);
            notificationHolder.viewDot.setVisibility(item.isDotVisible ? View.VISIBLE : View.GONE);
            notificationHolder.itemView.setBackgroundResource(item.backgroundRes);

            if (item.isSwiped) {
                notificationHolder.deleteArea.setVisibility(View.VISIBLE);
                notificationHolder.contentArea.setVisibility(View.GONE);
            } else {
                notificationHolder.deleteArea.setVisibility(View.GONE);
                notificationHolder.contentArea.setVisibility(View.VISIBLE);
            }

            notificationHolder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) itemClickListener.onItemClick(item);
            });

            notificationHolder.btnDeleteSwipe.setOnClickListener(v -> {
                if (deleteButtonClickListener != null) {
                    deleteButtonClickListener.onDeleteButtonClick(item.id, position);
                }
            });
        } else if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            NotificationHeader header = (NotificationHeader) items.get(position);
            headerHolder.tvHeader.setText(header.headerText);
        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.btnDeleteAll.setOnClickListener(v -> {
                if (deleteAllClickListener != null) deleteAllClickListener.onDeleteAllClick();
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.isEmpty() ? 0 : items.size() + 1;
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void setNotificationSwipedState(long notificationId, boolean isSwiped) {
        int position = -1;
        for (int i = 0; i < items.size(); i++) {
            NotificationAdapterItem item = items.get(i);
            if (item instanceof NotificationItem) {
                NotificationItem notificationItem = (NotificationItem) item;
                if (notificationItem.id == notificationId) {
                    notificationItem.setSwiped(isSwiped);
                    position = i;
                    break;
                }
            }
        }
        if (position != -1) {
            notifyItemChanged(position);
        }
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvTitle, tvContent, tvTime;
        View viewDot;
        public View contentArea;
        public View deleteArea;
        public ImageButton btnDeleteSwipe;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            viewDot = itemView.findViewById(R.id.viewDot);
            contentArea = itemView.findViewById(R.id.content_area);
            deleteArea = itemView.findViewById(R.id.delete_area);
            btnDeleteSwipe = itemView.findViewById(R.id.btn_delete_swipe);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        Button btnDeleteAll;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDeleteAll = itemView.findViewById(R.id.btnDeleteAll);
        }
    }
} 