package com.sbschoolcode.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {

    public final int apiId;
    public final String name;
    public final int servings;
    public final String imageUrl;

    public Recipe(int apiId, String name, int servings, String imageUrl) {
        this.apiId = apiId;
        this.name = name;
        this.servings = servings;
        this.imageUrl = imageUrl;
    }

    public String toString() {
        return name;
    }

    protected Recipe(Parcel in) {
        apiId = in.readInt();
        name = in.readString();
        servings = in.readInt();
        imageUrl = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(apiId);
        dest.writeString(name);
        dest.writeInt(servings);
        dest.writeString(imageUrl);
    }
}
