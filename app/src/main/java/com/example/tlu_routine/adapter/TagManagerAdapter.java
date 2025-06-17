package com.example.tlu_routine.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlu_routine.R;
import com.example.tlu_routine.model.Tag;

import java.util.List;

public class TagManagerAdapter extends RecyclerView.Adapter<TagManagerAdapter.TagViewHolder> {

    // 1. Định nghĩa 2 interface listener riêng biệt cho Sửa và Xóa
    public interface OnTagEditListener {
        // Khi sửa, cần cả đối tượng Tag và vị trí của nó
        void onEditClick(Tag tag, int position);
    }

    public interface OnTagDeleteListener {
        // Khi xóa, chỉ cần vị trí
        void onDeleteClick(int position);
    }

    private final List<Tag> tagList;
    private final OnTagEditListener editListener;
    private final OnTagDeleteListener deleteListener;

    // 2. Cập nhật constructor để nhận cả 2 listener
    public TagManagerAdapter(List<Tag> tagList, OnTagEditListener editListener, OnTagDeleteListener deleteListener) {
        this.tagList = tagList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_management, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        // ViewHolder chỉ chịu trách nhiệm hiển thị dữ liệu
        holder.bind(tag);

        // --- Gán sự kiện listener tại đây ---
        // 3. Sự kiện click Sửa: Gọi về Fragment với đầy đủ thông tin
        holder.editButton.setOnClickListener(v -> {
            if (editListener != null) {
                // Lấy vị trí mới nhất để tránh lỗi khi xóa item
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    editListener.onEditClick(tagList.get(currentPosition), currentPosition);
                }
            }
        });

        // 4. Sự kiện click Xóa: Gọi về Fragment chỉ với vị trí
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    deleteListener.onDeleteClick(currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    // ViewHolder chỉ làm nhiệm vụ hiển thị, không xử lý logic click
    static class TagViewHolder extends RecyclerView.ViewHolder {
        private final View tagColorDot;
        private final TextView tagIcon;
        private final TextView tagName;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagColorDot = itemView.findViewById(R.id.tag_color_dot);
            tagIcon = itemView.findViewById(R.id.tag_icon);
            tagName = itemView.findViewById(R.id.tag_name);
            editButton = itemView.findViewById(R.id.btn_edit_tag);
            deleteButton = itemView.findViewById(R.id.btn_delete_tag);
        }

        public void bind(final Tag tag) {
            tagName.setText(tag.getName());

            if (tag.getIconEmoji() != null && !tag.getIconEmoji().isEmpty()) {
                tagIcon.setText(tag.getIconEmoji());
            } else {
                tagIcon.setText("🏷️"); // Fallback emoji
            }

            // Đổi màu cho color dot
            Drawable unwrappedDrawable = tagColorDot.getBackground();
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            try {
                DrawableCompat.setTint(wrappedDrawable, Color.parseColor(tag.getColorHex()));
            } catch (Exception e) {
                // Fallback color nếu mã màu hex không hợp lệ
                DrawableCompat.setTint(wrappedDrawable, Color.GRAY);
            }
        }
    }
}