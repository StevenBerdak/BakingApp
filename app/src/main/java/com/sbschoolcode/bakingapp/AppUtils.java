package com.sbschoolcode.bakingapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class AppUtils {

    private static final String LOG_TAG = "AppUtils";

    public static void makeToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeLongToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
    }

    public static String normalizeWords(String input) {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(input.split(" ")));
        try {
            char specialChar;
            String tail, pre, post, temp;
            int specialCharIndex;
            for (int i = 0; i < words.size(); ++i) {

                if (words.get(i).equals("and") || words.get(i).equals("or")) continue;

                specialChar = words.get(i).charAt(0);
                tail = "";
                if (words.get(i).length() > 1) {
                    tail = words.get(i).substring(1, words.get(i).length());
                }
                words.set(i, Character.toString(specialChar).toUpperCase() + tail);

                specialCharIndex = words.get(i).indexOf("(");
                if (specialCharIndex >= 0) {
                    char nextChar = words.get(i).charAt(specialCharIndex + 1);
                    pre = words.get(i).substring(0, specialCharIndex + 1);
                    post = words.get(i).substring(specialCharIndex + 2);

                    if (specialCharIndex > 0 && words.get(i).charAt(specialCharIndex - 1) != ' ') {
                        pre = words.get(i).substring(0, specialCharIndex) + " (";
                    }

                    words.set(i, pre + Character.toString(nextChar).toUpperCase() + post);
                }

                specialCharIndex = words.get(i).indexOf(",");
                if (specialCharIndex >= 0) {
                    if (words.get(i).length() > specialCharIndex + 1) {
                        temp = words.get(i);
                        pre = temp.substring(0, specialCharIndex + 1);
                        post = temp.substring(specialCharIndex + 1);
                        words.remove(i);
                        words.add(i, post);
                        words.add(i, pre);
                    }
                }

                specialCharIndex = words.get(i).indexOf("-");
                if (specialCharIndex >= 0) {
                    temp = words.get(i);
                    specialChar = temp.charAt(specialCharIndex + 1);
                    if (Character.isDigit(specialChar)) break;
                    pre = temp.substring(0, specialCharIndex + 1);
                    post = temp.substring(specialCharIndex + 1);
                    words.set(i, pre + Character.toString(specialChar).toUpperCase() + post);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(LOG_TAG, "Error tidying string:" + input);
            return TextUtils.join(" ", words);
        }

        return TextUtils.join(" ", words);
    }

    public static int getRecipeDrawable(int recipeApiIndex) {
        switch (recipeApiIndex) {
            case 1:
                return R.drawable.nutella_cake;
            case 2:
                return R.drawable.brownies;
            case 3:
                return R.drawable.yellow_cake;
            case 4:
                return R.drawable.cheesecake;
            default:
                return R.drawable.baking;
        }
    }

    public static void setImage(ImageView imageView, int resourceId) {
        Handler handler = new Handler(Looper.getMainLooper());
        if (imageView != null)
            handler.post(() -> imageView.setImageResource(resourceId));
    }

    public static void testShiv(Class cls, String target) {
        Log.v(AppConstants.TESTING, "true, location = " + cls.getSimpleName() + " : " + target);
    }
}
