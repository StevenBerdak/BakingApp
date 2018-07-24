package com.sbschoolcode.bakingapp.services;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.data.DataUtils;
import com.sbschoolcode.bakingapp.data.DbContract;
import com.sbschoolcode.bakingapp.models.Ingredient;
import com.sbschoolcode.bakingapp.models.Recipe;
import com.sbschoolcode.bakingapp.models.Step;
import com.sbschoolcode.bakingapp.ui.recipe.RecipeActivity;

import java.util.ArrayList;

public class BuildRecipeItemService extends JobIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        int recipeApiIndex = intent.getIntExtra(AppConstants.INTENT_EXTRA_RECIPE_API_INDEX, -1);
        if (recipeApiIndex == -1) return;

        Uri recipeUri = DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.RecipesEntry.TABLE_NAME)
                .buildUpon().appendPath(Integer.toString(recipeApiIndex)).build();

        Cursor recipeCursor = getContentResolver().query(recipeUri, null, null, null, null);
        if (null == recipeCursor || recipeCursor.getCount() == 0) return;
        Recipe recipe = parseRecipe(recipeCursor);
        recipeCursor.close();

        Uri ingredientsUri = DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.IngredientsEntry.TABLE_NAME)
                .buildUpon().appendPath(Integer.toString(recipe.apiId)).build();

        Cursor ingredientsCursor = getContentResolver().query(ingredientsUri, null, null, null, null);
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        if (ingredientsCursor != null) {
            ingredients = parseIngredients(ingredientsCursor);
            ingredientsCursor.close();
        }

        Uri stepsUri = DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.StepsEntry.TABLE_NAME)
                .buildUpon().appendPath(Integer.toString(recipe.apiId)).build();

        Cursor stepsCursor = getContentResolver().query(stepsUri, null, null, null, null);
        ArrayList<Step> steps = new ArrayList<>();
        if (stepsCursor != null) {
            steps = parseSteps(stepsCursor);
            stepsCursor.close();
        }

        Intent broadcastRecipeIntent = new Intent(RecipeActivity.ACTION_RECIPE_QUERIED);
        broadcastRecipeIntent.putExtra(AppConstants.INTENT_EXTRA_RECIPE, recipe);
        broadcastRecipeIntent.putParcelableArrayListExtra(AppConstants.INTENT_EXTRA_INGREDIENTS_LIST, ingredients);
        broadcastRecipeIntent.putParcelableArrayListExtra(AppConstants.INTENT_EXTRA_STEPS_LIST, steps);

        sendBroadcast(broadcastRecipeIntent);
    }

    /**
     * Parse the steps from the cursor.
     *
     * @param cursor A cursor containing step data.
     * @return a steps list.
     */
    private ArrayList<Step> parseSteps(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DbContract.StepsEntry.COLUMN_STEP_ID);
        int shortDescIndex = cursor.getColumnIndex(DbContract.StepsEntry.COLUMN_SHORT_DESC);
        int descriptionIndex = cursor.getColumnIndex(DbContract.StepsEntry.COLUMN_DESCRIPTION);
        int videoUrlIndex = cursor.getColumnIndex(DbContract.StepsEntry.COLUMN_VIDEO_URL);
        int thumbnailUrlIndex = cursor.getColumnIndex(DbContract.StepsEntry.COLUMN_THUMBNAIL_URL);

        ArrayList<Step> result = new ArrayList<>();

        for (int i = 0; i < cursor.getCount(); ++i) {
            cursor.moveToNext();
            result.add(new Step(cursor.getInt(idIndex),
                    cursor.getString(shortDescIndex),
                    cursor.getString(descriptionIndex),
                    cursor.getString(videoUrlIndex),
                    cursor.getString(thumbnailUrlIndex)));
        }

        return result;
    }

    /**
     * Parse the ingredients from the cursor.
     *
     * @param cursor Cursor containing ingredients data.
     * @return ingredients list.
     */
    private ArrayList<Ingredient> parseIngredients(Cursor cursor) {
        int quantityIndex = cursor.getColumnIndex(DbContract.IngredientsEntry.COLUMN_QUANTITY);
        int measureIndex = cursor.getColumnIndex(DbContract.IngredientsEntry.COLUMN_MEASURE);
        int ingredientIndex = cursor.getColumnIndex(DbContract.IngredientsEntry.COLUMN_INGREDIENT);

        ArrayList<Ingredient> result = new ArrayList<>();

        for (int i = 0; i < cursor.getCount(); ++i) {
            cursor.moveToNext();
            result.add(new Ingredient(cursor.getDouble(quantityIndex),
                    cursor.getString(measureIndex),
                    cursor.getString(ingredientIndex)));
        }

        return result;
    }

    /**
     * Parse the recipes from the cursor.
     *
     * @param cursor The cursor containing the recipe data.
     * @return A recipe object.
     */
    private Recipe parseRecipe(Cursor cursor) {
        cursor.moveToFirst();
        int apiIdIndex = cursor.getColumnIndex(DbContract.RecipesEntry.COLUMN_API_ID);
        int nameIndex = cursor.getColumnIndex(DbContract.RecipesEntry.COLUMN_RECIPE_NAME);
        int servingsIndex = cursor.getColumnIndex(DbContract.RecipesEntry.COLUMN_SERVINGS);
        int imageUrlIndex = cursor.getColumnIndex(DbContract.RecipesEntry.COLUMN_IMAGE_URL);

        return new Recipe(cursor.getInt(apiIdIndex),
                cursor.getString(nameIndex),
                cursor.getInt(servingsIndex),
                cursor.getString(imageUrlIndex));
    }
}
