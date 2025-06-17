package com.example.tlu_routine.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
        public int iconRes;
        public String title;
        public String content;
        public String time;
        public boolean isActive;
        public boolean isDotVisible;
        public int backgroundRes;

        public NotificationItem(int iconRes, String title, String content, String time, boolean isActive, boolean isDotVisible, int backgroundRes) {
            this.iconRes = iconRes;
            this.title = title;
            this.content = content;
            this.time = time;
            this.isActive = isActive;
            this.isDotVisible = isDotVisible;
            this.backgroundRes = backgroundRes;
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

    public interface OnItemClickListener {
        void onItemClick(NotificationItem item);
    }

    public interface OnDeleteAllClickListener {
        void onDeleteAllClick();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnDeleteAllClickListener(OnDeleteAllClickListener listener) {
        this.deleteAllClickListener = listener;
    }

    public NotificationAdapter(List<NotificationAdapterItem> items) {
        this.items = items;
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
            notificationHolder.itemView.setOnClickListener(v -> {
                if (itemClickListener != null) itemClickListener.onItemClick(item);
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

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvTitle, tvContent, tvTime;
        View viewDot;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            viewDot = itemView.findViewById(R.id.viewDot);
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