package com.sbschoolcode.bakingapp;

import android.content.Context;
import android.widget.Toast;

public class AppUtils {

    public static void makeToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeLongToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
    }
}
