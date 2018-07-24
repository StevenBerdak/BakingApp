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

    /**
     * Make a regular toast.
     *
     * @param ctx     Context to use foe the toast.
     * @param message Message to show.
     */
    public static void makeToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Make a long toast.
     *
     * @param ctx     Context to use for the toast.
     * @param message Message to show.
     */
    public static void makeLongToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Normalize words for display of ingredients using a capitalize all words format.
     *
     * @param input The text to normalize.
     * @return A normalized string.
     */
    public static String normalizeIngredientsText(String input) {
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

    /**
     * Get the drawable to match with the provided recipe.
     *
     * @param recipeApiIndex The index to use to get an image for.
     * @return A resource id for an image.
     */
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

    /**
     * Set the image to the provided ImageView from the provided resource id.
     *
     * @param imageView  The image view to use.
     * @param resourceId The resource id to use.
     */
    public static void setImage(ImageView imageView, int resourceId) {
        Handler handler = new Handler(Looper.getMainLooper());
        if (imageView != null)
            handler.post(() -> imageView.setImageResource(resourceId));
    }
}
