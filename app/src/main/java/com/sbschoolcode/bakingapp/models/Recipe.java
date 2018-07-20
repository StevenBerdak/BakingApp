package com.sbschoolcode.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Recipe implements Parcelable {

    public final int apiId;
    public final String name;
    public final int servings;

    public Recipe(int apiId, String name, int servings) {
        this.apiId = apiId;
        this.name = name;
        this.servings = servings;
    }

    public String toString() {
        return name;
    }

    protected Recipe(Parcel in) {
        apiId = in.readInt();
        name = in.readString();
        servings = in.readInt();
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
    }
}
