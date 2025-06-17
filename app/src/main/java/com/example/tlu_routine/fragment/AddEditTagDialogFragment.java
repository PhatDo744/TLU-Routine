package com.example.tlu_routine.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.emoji2.widget.EmojiEditText;
import androidx.fragment.app.DialogFragment;

import com.example.tlu_routine.R;
import com.example.tlu_routine.model.Tag;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import java.util.ArrayList;
import java.util.Locale;

public class AddEditTagDialogFragment extends DialogFragment {

    private TextInputEditText etTagName;
    private TextInputLayout tilTagName;
    private ChipGroup colorChipGroup;
    private ChipGroup iconChipGroup;
    private LinearLayout customColorTriggerLayout;
    private MaterialCardView customColorPreview;
    private MaterialCardView customColorPickerCard;
    private ColorPickerView colorPickerView;
    private TextView tvCustomColorHex;
    private LinearLayout hexDisplayContainer;
    private TextView tvDialogTitle;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private MaterialButton selectCustomColorButton;
    private MaterialButton cancelCustomColorButton;

    private LinearLayout customIconContainer;
    private EmojiEditText etCustomIcon; // Emoji input for custom icon

    private LinearLayout layoutTagNameError;
    private TextView tvTagNameError;

    private String selectedColorHex = "#3B82F6"; // Default color
    private String selectedIconEmoji = "🏷️"; // Default emoji
    private Tag tagToEdit = null;
    private int tagPosition = -1;

    private ArrayList<Tag> existingTags = new ArrayList<>();

    // Thêm mảng màu riêng cho chip màu
    private final String[] colorPalette = {
        "#3B82F6", // blue
        "#F59E42", // orange
        "#F43F5E", // pink
        "#22C55E", // green
        "#A855F7", // purple
        "#FACC15", // yellow
        "#0EA5E9", // sky
        "#64748B", // slate
        "#EF4444"  // red
    };

    private final String[] emojiPalette = { "📚", "💼", "🤸", "🏆", "🎉", "💡", "📅", "💰", "💻", "🎯"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            tagToEdit = getArguments().getParcelable("tag_to_edit");
            tagPosition = getArguments().getInt("tag_position", -1);
            // Lấy danh sách tag hiện có từ arguments
            existingTags = getArguments().getParcelableArrayList("existing_tags");
            if (existingTags == null) existingTags = new ArrayList<>();
        }
        return inflater.inflate(R.layout.dialog_add_edit_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupListeners();
        setupColorChips();
        setupIconChips();
        setupCustomIconInput();

        if (tagToEdit != null) {
            populateUiForEditMode();
        } else {
            // Luôn mặc định màu custom là #3B82F6 khi thêm mới
            selectedColorHex = "#3B82F6";
            updateCustomColorPreview(selectedColorHex);
        }
    }

    private void bindViews(View view) {
        etTagName = view.findViewById(R.id.et_tag_name);
        colorChipGroup = view.findViewById(R.id.chip_group_colors);
        iconChipGroup = view.findViewById(R.id.chip_group_icons);
        customColorTriggerLayout = view.findViewById(R.id.custom_color_trigger_layout);
        customColorPreview = view.findViewById(R.id.custom_color_preview);
        customColorPickerCard = view.findViewById(R.id.custom_color_picker_card);
        colorPickerView = view.findViewById(R.id.color_picker_view);
        BrightnessSlideBar brightnessSlider = view.findViewById(R.id.brightness_slider);
        tvCustomColorHex = view.findViewById(R.id.tv_custom_color_hex);
        hexDisplayContainer = view.findViewById(R.id.hex_display_container);
        tvDialogTitle = view.findViewById(R.id.tv_dialog_title);
        saveButton = view.findViewById(R.id.btn_save_tag);
        cancelButton = view.findViewById(R.id.btn_cancel);
        selectCustomColorButton = view.findViewById(R.id.btn_select_custom_color);
        cancelCustomColorButton = view.findViewById(R.id.btn_cancel_custom_color);
        customIconContainer = view.findViewById(R.id.custom_icon_container);
        tilTagName = view.findViewById(R.id.til_tag_name);
        layoutTagNameError = view.findViewById(R.id.layout_tag_name_error);
        tvTagNameError = view.findViewById(R.id.tv_tag_name_error);

        if (colorPickerView != null) {
            if (brightnessSlider != null) {
                colorPickerView.attachBrightnessSlider(brightnessSlider);
            }
        }
    }

    private void setupListeners() {
        etTagName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Xóa thông báo lỗi khi người dùng bắt đầu nhập
                tilTagName.setError(null);
                if (layoutTagNameError != null) layoutTagNameError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        customColorTriggerLayout.setOnClickListener(v -> {
            boolean isPickerVisible = customColorPickerCard.getVisibility() == View.VISIBLE;
            customColorPickerCard.setVisibility(isPickerVisible ? View.GONE : View.VISIBLE);
            if (!isPickerVisible) {
                colorChipGroup.clearCheck();
            }
        });
        customColorTriggerLayout.setOnClickListener(v -> {
            boolean isPickerVisible = customColorPickerCard.getVisibility() == View.VISIBLE;
            customColorPickerCard.setVisibility(isPickerVisible ? View.GONE : View.VISIBLE);
            if (!isPickerVisible) {
                colorChipGroup.clearCheck();
            }
        });

        if (colorPickerView != null) {
            colorPickerView.setColorListener(new ColorEnvelopeListener() {
                @Override
                public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                    if (envelope != null) {
                        updateHexDisplay(envelope.getColor(), "#" + envelope.getHexCode());
                    }
                }
            });
        }

        selectCustomColorButton.setOnClickListener(v -> {
            if (colorPickerView != null) {
                selectedColorHex = "#" + colorPickerView.getColorEnvelope().getHexCode();
                updateCustomColorPreview(selectedColorHex);
                customColorPickerCard.setVisibility(View.GONE);
            }
        });

        cancelCustomColorButton.setOnClickListener(v -> {
            customColorPickerCard.setVisibility(View.GONE);
            if (colorChipGroup.getCheckedChipId() == -1) {
                selectedColorHex = null;
                updateCustomColorPreview(null);
            }
        });

        saveButton.setOnClickListener(v -> saveTag());
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void showTagNameError(String errorMsg) {
        tilTagName.setError(null); // Không dùng lỗi mặc định
        // Đổi màu viền sang đỏ khi có lỗi
        tilTagName.setBoxStrokeColor(ContextCompat.getColor(requireContext(), R.color.delete_button_red));
        if (layoutTagNameError != null && tvTagNameError != null) {
            tvTagNameError.setText(errorMsg);
            layoutTagNameError.setVisibility(View.VISIBLE);
        }
    }

    private void hideTagNameError() {
        if (layoutTagNameError != null) layoutTagNameError.setVisibility(View.GONE);
        // Trả lại màu viền mặc định (xanh dương)
        tilTagName.setBoxStrokeColor(ContextCompat.getColor(requireContext(), R.color.tag_button_blue));
    }

    private void setupColorChips() {
        if (getContext() == null) return;
        colorChipGroup.removeAllViews();
        int size = (int) (45 * getResources().getDisplayMetrics().density);

        for (String color : colorPalette) {
            Chip chip = new Chip(getContext());
            chip.setCheckable(true);

            int parsedColor = Color.parseColor(color);
            chip.setChipBackgroundColor(ColorStateList.valueOf(parsedColor));
            chip.setChipStrokeColor(createColorChipStrokeList());
            chip.setChipStrokeWidth(2 * getResources().getDisplayMetrics().density);

            chip.setText("");

            ViewGroup.LayoutParams params = chip.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(size, size);
            } else {
                params.width = size;
                params.height = size;
            }
            chip.setLayoutParams(params);
            chip.setChipCornerRadius(size / 2f);

            chip.setTag(color);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedColorHex = (String) buttonView.getTag();
                    customColorPickerCard.setVisibility(View.GONE);
                    updateCustomColorPreview(null);
                    // Khi chọn màu preset, bo viền preview về mặc định (xám)
                    setCustomColorPreviewStroke(false);
                }
            });
            colorChipGroup.addView(chip);
        }
    }

    private void setupIconChips() {
        if (getContext() == null) return;
        iconChipGroup.removeAllViews();

        for (String emoji : emojiPalette) {
            Chip chip = createEmojiChip(emoji);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && etCustomIcon != null) {
                    etCustomIcon.setText("");
                }
            });
            iconChipGroup.addView(chip);
        }
    }

    private Chip createEmojiChip(String emoji) {
        if (getContext() == null) return new Chip(getContext());

        // Kích thước vuông cho chip
        int chipSizePx = (int) (44 * getResources().getDisplayMetrics().density);

        Chip chip = new Chip(getContext());
        chip.setCheckable(true);

        chip.setText(emoji);
        chip.setTextSize(22); // Vừa với ô vuông
        chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        chip.setGravity(Gravity.CENTER);

        // Corner radius 8dp
        chip.setChipCornerRadius(8f * getResources().getDisplayMetrics().density);

        // Padding nhỏ để emoji nằm giữa
        chip.setChipStartPadding(0f);
        chip.setChipEndPadding(0f);
        chip.setPadding(0, 0, 0, 0);

        // Đặt kích thước vuông
        chip.setChipMinHeight(chipSizePx);
        chip.setMinWidth(chipSizePx);
        chip.setMaxWidth(chipSizePx);
        chip.setEnsureMinTouchTargetSize(false);

        chip.setChipBackgroundColor(createIconChipBackgroundList());
        chip.setChipStrokeColor(createIconChipStrokeList());
        chip.setChipStrokeWidth(1.5f * getResources().getDisplayMetrics().density);

        chip.setTag(emoji);
        return chip;
    }

    private void setupCustomIconInput() {
        if (getContext() == null) return;
        customIconContainer.removeAllViews();

        int boxSizePx = (int) (44 * getResources().getDisplayMetrics().density);

        etCustomIcon = new EmojiEditText(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(boxSizePx, boxSizePx);
        etCustomIcon.setLayoutParams(params);
        etCustomIcon.setTextSize(22);
        etCustomIcon.setGravity(Gravity.CENTER);
        etCustomIcon.setPadding(0, 0, 0, 0);
        etCustomIcon.setBackgroundResource(R.drawable.bg_square_icon_edittext);
        etCustomIcon.setMaxLines(1);
        etCustomIcon.setHint("🏷️");
        etCustomIcon.setSingleLine(true);

        etCustomIcon.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                iconChipGroup.clearCheck();
            }
        });

        etCustomIcon.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etCustomIcon.hasFocus() && s.length() > 0) {
                    iconChipGroup.clearCheck();
                }
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        customIconContainer.addView(etCustomIcon);
    }

    private void populateUiForEditMode() {
        tvDialogTitle.setText(R.string.dialog_edit_tag_title);
        saveButton.setText(R.string.update_tag_button);
        etTagName.setText(tagToEdit.getName());

        String colorToSelect = tagToEdit.getColorHex();
        boolean isPresetColor = false;
        if (colorToSelect != null) {
            selectedColorHex = colorToSelect;
            // Kiểm tra xem màu có nằm trong colorPalette không
            for (int i = 0; i < colorChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) colorChipGroup.getChildAt(i);
                if (colorToSelect.equalsIgnoreCase(chip.getTag().toString())) {
                    chip.setChecked(true); // ánh xạ vào chip tương ứng
                    isPresetColor = true;
                    // Khi là màu preset, bo viền preview về mặc định (xám)
                    setCustomColorPreviewStroke(false);
                    break;
                }
            }
            // Nếu không phải màu preset thì mới update vào ô màu tùy chỉnh
            if (!isPresetColor) {
                colorChipGroup.clearCheck();
                updateCustomColorPreview(colorToSelect);
                // Khi là màu tùy chỉnh, bo viền preview thành xanh dương
                setCustomColorPreviewStroke(true);
            }
        } else {
            // Nếu không có màu, mặc định là #3B82F6
            selectedColorHex = "#3B82F6";
            updateCustomColorPreview(selectedColorHex);
            setCustomColorPreviewStroke(false);
        }

        String iconEmoji = tagToEdit.getIconEmoji();
        if (iconEmoji != null && !iconEmoji.isEmpty()) {
            selectedIconEmoji = iconEmoji;
            boolean found = false;
            for (int i = 0; i < iconChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) iconChipGroup.getChildAt(i);
                if (chip.getTag() != null && iconEmoji.equals(chip.getTag())) {
                    chip.setChecked(true);
                    found = true;
                    break;
                }
            }
            // Nếu không tìm thấy trong emojiPalette thì set vào custom icon
            if (!found && etCustomIcon != null) {
                etCustomIcon.setText(iconEmoji);
            }
        } else if (etCustomIcon != null) {
            // Nếu không có iconEmoji thì set emoji mặc định
            etCustomIcon.setText("🏷️");
        }
    }

    private void updateCustomColorPreview(String colorHex) {
        if (getContext() == null) return;
        if (colorHex != null) {
            try {
                customColorPreview.setCardBackgroundColor(Color.parseColor(colorHex));
            } catch (IllegalArgumentException e) {
                customColorPreview.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.material_grey_600));
            }
            // Khi update preview với màu tùy chỉnh, bo viền xanh
            setCustomColorPreviewStroke(true);
        } else {
            customColorPreview.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.material_grey_600));
            // Khi không có màu tùy chỉnh, bo viền xám
            setCustomColorPreviewStroke(false);
        }
    }

    private void updateHexDisplay(int color, String hexCode) {
        if (hexDisplayContainer != null) {
            hexDisplayContainer.setBackgroundColor(color);
        }
        if (tvCustomColorHex != null) {
            tvCustomColorHex.setText(hexCode.toUpperCase());
            double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
            if (darkness >= 0.5) {
                tvCustomColorHex.setTextColor(Color.WHITE);
            } else {
                tvCustomColorHex.setTextColor(Color.BLACK);
            }
        }
    }

    private ColorStateList createColorChipStrokeList() {
        if (getContext() == null) return null;
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        ContextCompat.getColor(requireContext(), R.color.icon_chip_stroke_selected),
                        Color.TRANSPARENT
                }
        );
    }

    private ColorStateList createIconChipBackgroundList() {
        if (getContext() == null) return null;
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        ContextCompat.getColor(requireContext(), R.color.icon_chip_bg_selected),
                        ContextCompat.getColor(requireContext(), R.color.light_gray_background)
                }
        );
    }

    private ColorStateList createIconChipStrokeList() {
        if (getContext() == null) return null;
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        ContextCompat.getColor(requireContext(), R.color.icon_chip_stroke_selected),
                        ContextCompat.getColor(requireContext(), R.color.divider_color)
                }
        );
    }

    private void setCustomColorPreviewStroke(boolean selected) {
        if (getContext() == null) return;
        if (selected) {
            customColorPreview.setStrokeColor(ContextCompat.getColor(getContext(), R.color.tag_button_blue));
            customColorPreview.setStrokeWidth((int) 4f); // Đậm hơn khi chọn
        } else {
            customColorPreview.setStrokeColor(ContextCompat.getColor(getContext(), R.color.divider_color));
            customColorPreview.setStrokeWidth((int) 2f);
        }
    }

    private void saveTag() {
        // Xóa lỗi cũ trước khi kiểm tra
        tilTagName.setError(null);
        hideTagNameError();
        String tagName = etTagName.getText().toString().trim();

        // 1. Kiểm tra rỗng
        if (tagName.isEmpty()) {
            showTagNameError("Vui lòng nhập tên thẻ");
            etTagName.requestFocus();
            return;
        }

        // 2. Kiểm tra trùng tên (không phân biệt hoa thường, bỏ qua chính nó khi sửa)
        for (int i = 0; i < existingTags.size(); i++) {
            Tag tag = existingTags.get(i);
            // Nếu đang ở chế độ sửa, bỏ qua tag tại vị trí ban đầu của nó
            if (tagToEdit != null && i == tagPosition) {
                continue;
            }
            // Kiểm tra trùng tên
            if (tagName.equalsIgnoreCase(tag.getName())) {
                showTagNameError("Tên thẻ này đã tồn tại. Vui lòng chọn tên khác");
                etTagName.requestFocus();
                return;
            }
        }

        String iconEmoji = null;
        int checkedIconId = iconChipGroup.getCheckedChipId();
        if (checkedIconId != View.NO_ID) {
            Chip checkedChip = iconChipGroup.findViewById(checkedIconId);
            if (checkedChip != null) {
                iconEmoji = (String) checkedChip.getTag();
            }
        } else if (etCustomIcon != null && etCustomIcon.getText() != null && !etCustomIcon.getText().toString().trim().isEmpty()) {
            iconEmoji = etCustomIcon.getText().toString().trim();
        }

        // Nếu iconEmoji vẫn rỗng thì fallback về emoji mặc định
        if (iconEmoji == null || iconEmoji.isEmpty()) {
            iconEmoji = "🏷️";
        }

        // Tạo đối tượng Tag mới với dữ liệu đã chọn
        Tag resultTag = new Tag(tagName, selectedColorHex, iconEmoji);

        // Gửi kết quả trở lại Fragment cha
        Bundle result = new Bundle();
        result.putParcelable("tag_result", resultTag);
        result.putInt("tag_position", tagPosition);
        getParentFragmentManager().setFragmentResult("tag_request", result);

        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = getResources().getDisplayMetrics().widthPixels - (int)(32 * getResources().getDisplayMetrics().density); // 16dp mỗi bên
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

            // ✅ Bo góc cho dialog window
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }
    }
}
