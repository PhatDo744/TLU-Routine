package com.example.tlu_routine.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private LinearLayout customColorTriggerLayout;
    private MaterialCardView customColorPreview;
    private MaterialCardView customColorPickerCard;
    private ColorPickerView colorPickerView;
    private TextView tvCustomColorHex;
    private TextView tvDialogTitle;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private MaterialButton selectCustomColorButton;
    private MaterialButton cancelCustomColorButton;

    private LinearLayout customIconContainer;
    private Chip chipCustomIcon;


    private String selectedColorHex = null;
    private Tag tagToEdit = null;

    private final int customIconResId = R.drawable.ic_tag;

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
        setupCustomIconChip();

        if (tagToEdit != null) {
            populateUiForEditMode();
        } else {
            updateCustomColorPreview(null);
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
        tvDialogTitle = view.findViewById(R.id.tv_dialog_title);
        saveButton = view.findViewById(R.id.btn_save_tag);
        cancelButton = view.findViewById(R.id.btn_cancel);
        selectCustomColorButton = view.findViewById(R.id.btn_select_custom_color);
        cancelCustomColorButton = view.findViewById(R.id.btn_cancel_custom_color);
        customIconContainer = view.findViewById(R.id.custom_icon_container);


        if (colorPickerView != null && brightnessSlider != null) {
            colorPickerView.attachBrightnessSlider(brightnessSlider);
        }
    }

    private void setupListeners() {
        customColorTriggerLayout.setOnClickListener(v -> {
            boolean isPickerVisible = customColorPickerCard.getVisibility() == View.VISIBLE;
            customColorPickerCard.setVisibility(isPickerVisible ? View.GONE : View.VISIBLE);
            if (!isPickerVisible) {
                colorChipGroup.clearCheck();
                if (colorPickerView != null) {
                    selectedColorHex = "#" + colorPickerView.getColorEnvelope().getHexCode();
                    updateCustomColorPreview(selectedColorHex);
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
                updateCustomColorPreview(selectedColorHex);
                customColorPickerCard.setVisibility(View.GONE);
            }
        });

        cancelCustomColorButton.setOnClickListener(v -> {
            customColorPickerCard.setVisibility(View.GONE);
            if(colorChipGroup.getCheckedChipId() == -1){
                selectedColorHex = null;
                updateCustomColorPreview(null);
            }
        });

        saveButton.setOnClickListener(v -> saveTag());
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void setupColorChips() {
        if (getContext() == null) return;
        colorChipGroup.removeAllViews();
        int size = (int) (45 * getResources().getDisplayMetrics().density); // FIXED: Changed size back to 40dp

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
                }
            });
            colorChipGroup.addView(chip);
        }
    }

    private void setupIconChips() {
        if (getContext() == null) return;
        iconChipGroup.removeAllViews();

        for (int iconRes : iconPalette) {
            Chip chip = createIconChip(iconRes);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && chipCustomIcon != null) {
                    chipCustomIcon.setChecked(false);
                }
            });
            iconChipGroup.addView(chip);
        }
    }

    private void setupCustomIconChip() {
        if (getContext() == null) return;
        customIconContainer.removeAllViews();
        chipCustomIcon = createIconChip(customIconResId);
        chipCustomIcon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                iconChipGroup.clearCheck();
            }
        });
        customIconContainer.addView(chipCustomIcon);
    }

    private Chip createIconChip(int iconRes) {
        if (getContext() == null) return new Chip(getContext());

        float chipSizePx = 36f * getResources().getDisplayMetrics().density;
        float iconSizePx = 20f * getResources().getDisplayMetrics().density;
        float cornerRadiusPx = 8f * getResources().getDisplayMetrics().density;
        float strokeWidthPx = 1.5f * getResources().getDisplayMetrics().density;

        Chip chip = new Chip(getContext());
        chip.setCheckable(true);

        try {
            chip.setChipIconResource(iconRes);
        } catch (Exception e) {
            chip.setChipIcon(null);
        }

        chip.setChipBackgroundColor(createIconChipBackgroundList());
        chip.setChipStrokeColor(createIconChipStrokeList());
        chip.setChipIconTint(createIconChipTintList());
        chip.setChipStrokeWidth(strokeWidthPx);

        chip.setText(null);
        chip.setChipIconSize(iconSizePx);
        chip.setChipCornerRadius(cornerRadiusPx);
        chip.setEnsureMinTouchTargetSize(false);

        ViewGroup.LayoutParams params = chip.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams((int) chipSizePx, (int) chipSizePx);
        } else {
            params.width = (int) chipSizePx;
            params.height = (int) chipSizePx;
        }
        chip.setLayoutParams(params);

        chip.setTag(iconRes);
        return chip;
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
                updateCustomColorPreview(colorToSelect);
            }
        } else {
            updateCustomColorPreview(null);
        }

        int iconToSelect = tagToEdit.getIconResId();
        if (iconToSelect != -1) {
            if (iconToSelect == customIconResId) {
                chipCustomIcon.setChecked(true);
            } else {
                for (int i = 0; i < iconChipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) iconChipGroup.getChildAt(i);
                    if (chip.getTag() != null && iconToSelect == (int) chip.getTag()) {
                        chip.setChecked(true);
                        break;
                    }
                }
            }
        }
    }

    private void updateCustomColorPreview(String colorHex) {
        if (getContext() == null) return;
        if (colorHex != null) {
            customColorPreview.setCardBackgroundColor(Color.parseColor(colorHex));
        } else {
            customColorPreview.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.material_grey_600));
        }
    }

    // ----- Helper Methods for creating ColorStateLists -----

    private ColorStateList createColorChipStrokeList() {
        if (getContext() == null) return null;
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        ContextCompat.getColor(requireContext(), R.color.icon_chip_stroke_selected), // Blue border
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

    private ColorStateList createIconChipTintList() {
        if (getContext() == null) return null;
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        ContextCompat.getColor(requireContext(), R.color.icon_chip_stroke_selected),
                        ContextCompat.getColor(requireContext(), R.color.default_text_color)
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
        } else if (chipCustomIcon != null && chipCustomIcon.isChecked()) {
            selectedIconRes = (int) chipCustomIcon.getTag();
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
