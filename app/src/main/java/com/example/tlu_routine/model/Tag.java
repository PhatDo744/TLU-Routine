package com.example.tlu_routine.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Tag implements Parcelable {
    private String name;
    private String colorHex;
    private int iconResId;
    private String iconEmoji; // emoji icon, nullable

    // Constructor cho emoji
    public Tag(String name, String colorHex, String iconEmoji) {
        this.name = name;
        this.colorHex = colorHex;
        this.iconEmoji = iconEmoji;
        this.iconResId = 0;
    }

    // Constructor cũ cho resource id
    public Tag(String name, String colorHex, int iconResId) {
        this.name = name;
        this.colorHex = colorHex;
        this.iconResId = iconResId;
        this.iconEmoji = null;
    }

    protected Tag(Parcel in) {
        name = in.readString();
        colorHex = in.readString();
        iconResId = in.readInt();
        iconEmoji = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getIconEmoji() {
        return iconEmoji;
    }

    public void setIconEmoji(String iconEmoji) {
        this.iconEmoji = iconEmoji;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(colorHex);
        dest.writeInt(iconResId);
        dest.writeString(iconEmoji);
    }
}
