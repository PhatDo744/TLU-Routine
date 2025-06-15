package com.example.tlu_routine.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.tlu_routine.R;
import com.example.tlu_routine.model.Tag;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

public class AddEditTagDialogFragment extends DialogFragment {

    private TextInputEditText etTagName;
    private ChipGroup colorChipGroup;
    private ChipGroup iconChipGroup;
    private Chip customColorChip;
    private MaterialCardView customColorPickerCard;
    private ColorPickerView colorPickerView;
    private TextView tvCustomColorHex;
    private TextView tvDialogTitle;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private MaterialButton selectCustomColorButton;
    private MaterialButton cancelCustomColorButton;

    private String selectedColorHex = null;
    private Tag tagToEdit = null;

    private final String[] colorPalette = {"#EF4444", "#F97316", "#F59E0B", "#84CC16", "#22C55E", "#10B981", "#06B6D4", "#3B82F6", "#8B5CF6", "#EC4899"};
    private final int[] iconPalette = {R.drawable.ic_book, R.drawable.ic_briefcase, R.drawable.ic_person, R.drawable.ic_trophy, R.drawable.ic_lightbulb, R.drawable.ic_calendar_day, R.drawable.ic_money, R.drawable.ic_laptop, R.drawable.ic_target};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            tagToEdit = getArguments().getParcelable("tag_to_edit");
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

        if (tagToEdit != null) {
            populateUiForEditMode();
        } else {
            updateIconChipsAppearance();
        }
    }

    private void bindViews(View view) {
        etTagName = view.findViewById(R.id.et_tag_name);
        colorChipGroup = view.findViewById(R.id.chip_group_colors);
        iconChipGroup = view.findViewById(R.id.chip_group_icons);
        customColorChip = view.findViewById(R.id.chip_custom_color);
        customColorPickerCard = view.findViewById(R.id.custom_color_picker_card);
        colorPickerView = view.findViewById(R.id.color_picker_view);
        BrightnessSlideBar brightnessSlider = view.findViewById(R.id.brightness_slider);
        tvCustomColorHex = view.findViewById(R.id.tv_custom_color_hex);
        tvDialogTitle = view.findViewById(R.id.tv_dialog_title);
        saveButton = view.findViewById(R.id.btn_save_tag);
        cancelButton = view.findViewById(R.id.btn_cancel);
        selectCustomColorButton = view.findViewById(R.id.btn_select_custom_color);
        cancelCustomColorButton = view.findViewById(R.id.btn_cancel_custom_color);

        if (colorPickerView != null && brightnessSlider != null) {
            colorPickerView.attachBrightnessSlider(brightnessSlider);
        }
    }

    private void setupListeners() {
        customColorChip.setOnClickListener(v -> {
            boolean isPickerVisible = customColorPickerCard.getVisibility() == View.VISIBLE;
            customColorPickerCard.setVisibility(isPickerVisible ? View.GONE : View.VISIBLE);
            if (!isPickerVisible) {
                colorChipGroup.clearCheck();
                if (colorPickerView != null) {
                    selectedColorHex = "#" + colorPickerView.getColorEnvelope().getHexCode();
                    updateCustomColorChip(selectedColorHex);
                    updateIconChipsAppearance();
                }
            }
        });

        if (colorPickerView != null) {
            colorPickerView.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> {
                if (envelope != null) {
                    String hexCode = "#" + envelope.getHexCode();
                    tvCustomColorHex.setText(hexCode);
                    tvCustomColorHex.setBackgroundColor(envelope.getColor());
                }
            });
        }

        selectCustomColorButton.setOnClickListener(v -> {
            if (colorPickerView != null) {
                selectedColorHex = "#" + colorPickerView.getColorEnvelope().getHexCode();
                updateCustomColorChip(selectedColorHex);
                updateIconChipsAppearance();
                customColorPickerCard.setVisibility(View.GONE);
            }
        });

        cancelCustomColorButton.setOnClickListener(v -> {
            customColorPickerCard.setVisibility(View.GONE);
            if(colorChipGroup.getCheckedChipId() == -1){
                selectedColorHex = null;
                updateCustomColorChip(null);
                updateIconChipsAppearance();
            }
        });

        saveButton.setOnClickListener(v -> saveTag());
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void setupColorChips() {
        if (getContext() == null) return;
        colorChipGroup.removeAllViews();
        int size = (int) (40 * getResources().getDisplayMetrics().density); // 40dp

        for (String color : colorPalette) {
            Chip chip = new Chip(requireContext(), null, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice);
            int parsedColor = Color.parseColor(color);
            chip.setChipBackgroundColor(ColorStateList.valueOf(parsedColor));
            chip.setChipStrokeColor(createStrokeColorStateList(parsedColor, true));
            chip.setChipStrokeWidth(2 * getResources().getDisplayMetrics().density);

            // Force a circular shape using standard View methods
            chip.setText(""); // No text to prevent deformation
            chip.setMinimumWidth(size);
            chip.setMinimumHeight(size);
            chip.setChipCornerRadius(size / 2f); // Half of the size for a perfect circle
            chip.setEnsureMinTouchTargetSize(false); // Prevent extra padding

            chip.setTag(color);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedColorHex = (String) buttonView.getTag();
                    customColorPickerCard.setVisibility(View.GONE);
                    updateCustomColorChip(null);
                    updateIconChipsAppearance();
                }
            });
            colorChipGroup.addView(chip);
        }
    }

    private void setupIconChips() {
        if (getContext() == null) return;
        iconChipGroup.removeAllViews();
        int size = (int) (48 * getResources().getDisplayMetrics().density); // 48dp

        for (int iconRes : iconPalette) {
            Chip chip = new Chip(requireContext(), null, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice);
            try {
                chip.setChipIconResource(iconRes);
            } catch (Exception e) {
                chip.setChipIcon(null);
            }

            // Force a circular shape using standard View methods
            chip.setText("");
            chip.setMinimumWidth(size);
            chip.setMinimumHeight(size);
            chip.setChipCornerRadius(size / 2f);
            chip.setEnsureMinTouchTargetSize(false);
            chip.setChipIconSize(24 * getResources().getDisplayMetrics().density);

            chip.setChipStrokeWidth(1.5f * getResources().getDisplayMetrics().density);
            chip.setTag(iconRes);
            iconChipGroup.addView(chip);
        }
    }

    private void updateIconChipsAppearance() {
        if (getContext() == null) return;

        int color;
        if (selectedColorHex != null) {
            try {
                color = Color.parseColor(selectedColorHex);
            } catch (IllegalArgumentException e) {
                color = ContextCompat.getColor(getContext(), R.color.tag_button_blue);
            }
        } else {
            color = ContextCompat.getColor(getContext(), R.color.tag_button_blue);
        }

        ColorStateList iconTintStateList = createIconTint_ColorStateList(color);
        ColorStateList strokeStateList = createStrokeColorStateList(color, false);

        for (int i = 0; i < iconChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) iconChipGroup.getChildAt(i);
            chip.setChipIconTint(iconTintStateList);
            chip.setChipStrokeColor(strokeStateList);
        }
    }

    private void populateUiForEditMode() {
        tvDialogTitle.setText(R.string.dialog_edit_tag_title);
        saveButton.setText(R.string.update_tag_button);
        etTagName.setText(tagToEdit.getName());

        String colorToSelect = tagToEdit.getColorHex();
        boolean isPresetColor = false;
        if (colorToSelect != null) {
            selectedColorHex = colorToSelect;
            for (int i = 0; i < colorChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) colorChipGroup.getChildAt(i);
                if (colorToSelect.equalsIgnoreCase(chip.getTag().toString())) {
                    chip.setChecked(true);
                    isPresetColor = true;
                    break;
                }
            }
            if (!isPresetColor) {
                updateCustomColorChip(colorToSelect);
            }
        }

        updateIconChipsAppearance();

        int iconToSelect = tagToEdit.getIconResId();
        if (iconToSelect != -1) {
            for (int i = 0; i < iconChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) iconChipGroup.getChildAt(i);
                if (chip.getTag() != null && iconToSelect == (int) chip.getTag()) {
                    chip.setChecked(true);
                    break;
                }
            }
        }
    }

    private void updateCustomColorChip(String colorHex) {
        if (colorHex != null) {
            customColorChip.setText(colorHex.toUpperCase());
            customColorChip.setChipIcon(null);
            customColorChip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(colorHex)));
        } else {
            customColorChip.setText(R.string.pick_a_color_button);
            customColorChip.setChipIconResource(R.drawable.ic_color_palette);
            customColorChip.setChipBackgroundColor(null);
        }
    }

    private ColorStateList createStrokeColorStateList(int selectedColor, boolean isForColorChip) {
        int defaultStrokeColor = isForColorChip ? Color.TRANSPARENT : Color.parseColor("#E0E0E0");
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        selectedColor,
                        defaultStrokeColor
                }
        );
    }

    private ColorStateList createIconTint_ColorStateList(int selectedColor) {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        selectedColor,
                        Color.parseColor("#4B5563") // Màu icon mặc định (xám đậm)
                }
        );
    }

    private void saveTag() {
        String tagName = etTagName.getText().toString().trim();
        if (tagName.isEmpty()) {
            etTagName.setError("Tên thẻ không được để trống");
            return;
        }

        int selectedIconRes = -1;
        int checkedIconId = iconChipGroup.getCheckedChipId();
        if (checkedIconId != View.NO_ID) {
            Chip checkedChip = iconChipGroup.findViewById(checkedIconId);
            if (checkedChip != null) {
                selectedIconRes = (int) checkedChip.getTag();
            }
        }

        String toastMessage;
        if (tagToEdit != null) {
            toastMessage = "Đã cập nhật thẻ:\n";
        } else {
            toastMessage = "Đã lưu thẻ:\n";
        }

        String log = "Tên: " + tagName + "\nMàu: " + selectedColorHex + "\nIcon Res ID: " + selectedIconRes;
        Toast.makeText(getContext(), toastMessage + log, Toast.LENGTH_LONG).show();
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
