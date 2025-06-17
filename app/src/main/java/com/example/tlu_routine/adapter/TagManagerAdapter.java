package com.example.tlu_routine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tlu_routine.R;
import com.example.tlu_routine.model.Tag;

import java.util.List;

public class TagManagerAdapter extends RecyclerView.Adapter<TagManagerAdapter.TagViewHolder> {

    // Interface để xử lý sự kiện click
    public interface OnTagActionClickListener {
        void onDeleteClick(int position);
    }

    private final List<Tag> tagList;
    private final OnTagActionClickListener listener;

    public TagManagerAdapter(List<Tag> tagList, OnTagActionClickListener listener) {
        this.tagList = tagList;
        this.listener = listener;
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
        holder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {
        private final View tagColorDot;
        private final TextView tagIcon; // Đổi từ ImageView sang TextView
        private final TextView tagName;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagColorDot = itemView.findViewById(R.id.tag_color_dot);
            tagIcon = itemView.findViewById(R.id.tag_icon); // Đảm bảo layout là TextView
            tagName = itemView.findViewById(R.id.tag_name);
            editButton = itemView.findViewById(R.id.btn_edit_tag);
            deleteButton = itemView.findViewById(R.id.btn_delete_tag);
        }

        public void bind(final Tag tag) {
            tagName.setText(tag.getName());
            // Hiển thị emoji nếu có, nếu không fallback về icon drawable
            if (tag.getIconEmoji() != null && !tag.getIconEmoji().isEmpty()) {
                tagIcon.setText(tag.getIconEmoji());
                tagIcon.setVisibility(View.VISIBLE);
            } else {
                tagIcon.setText(""); // hoặc có thể để icon mặc định nếu muốn
            }

            Drawable unwrappedDrawable = tagColorDot.getBackground();
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(tag.getColorHex()));

            // Sự kiện click Sửa
            editButton.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("tag_to_edit", tag);
                bundle.putInt("tag_position", getAdapterPosition()); // Gửi vị trí của thẻ
                Navigation.findNavController(v).navigate(R.id.action_tagManagerFragment_to_addEditTagDialogFragment, bundle);
            });

            // Sự kiện click Xóa
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
}
