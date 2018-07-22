package com.sbschoolcode.bakingapp.services;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.data.DataUtils;
import com.sbschoolcode.bakingapp.data.DbContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InsertRecipesService extends JobIntentService {

    public static final String EXTRA_IN_JSON_DATA = "json_data";
    public static final String ACTION_RECIPES_INSERTED = "com.sbschoolcode.broadcast.RECIPES_INSERTED";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        String jsonString = intent.getStringExtra(EXTRA_IN_JSON_DATA);

        ArrayList<ContentValues> recipesValuesArr = new ArrayList<>();
        ArrayList<ContentValues> ingredientsValuesArr = new ArrayList<>();
        ArrayList<ContentValues> stepsValuesArr = new ArrayList<>();

        try {

            JSONArray rootArray = new JSONArray(jsonString);

            for (int index = 0; index < rootArray.length(); ++index) {
                JSONObject recipeObject = rootArray.optJSONObject(index);

                int recipeApiId = recipeObject.optInt("id");
                String recipeName = recipeObject.optString("name");
                String recipeServings = recipeObject.optString("servings");

                ContentValues recipeValues = new ContentValues();

                recipeValues.put(DbContract.RecipesEntry.COLUMN_API_ID, recipeApiId);
                recipeValues.put(DbContract.RecipesEntry.COLUMN_RECIPE_NAME, recipeName);
                recipeValues.put(DbContract.RecipesEntry.COLUMN_SERVINGS, recipeServings);

                recipesValuesArr.add(recipeValues);

                JSONArray ingredientsArray = recipeObject.optJSONArray("ingredients");

                JSONObject ingredientObject;

                for (int ingredientIndex = 0; ingredientIndex < ingredientsArray.length(); ++ingredientIndex) {

                    ContentValues ingredientsValues = new ContentValues();

                    ingredientObject = ingredientsArray.optJSONObject(ingredientIndex);

                    double quantity = ingredientObject.optDouble("quantity");
                    String measure = ingredientObject.optString("measure");
                    String ingredient = ingredientObject.optString("ingredient");

                    ingredientsValues.put(DbContract.IngredientsEntry.COLUMN_PARENT_API_ID, recipeApiId);
                    ingredientsValues.put(DbContract.IngredientsEntry.COLUMN_QUANTITY, quantity);
                    ingredientsValues.put(DbContract.IngredientsEntry.COLUMN_MEASURE, measure);
                    ingredientsValues.put(DbContract.IngredientsEntry.COLUMN_INGREDIENT, ingredient);

                    ingredientsValuesArr.add(ingredientsValues);
                }

                JSONArray stepsArray = recipeObject.getJSONArray("steps");

                JSONObject stepsObject;

                for (int stepsIndex = 0; stepsIndex < stepsArray.length(); ++stepsIndex) {

                    ContentValues stepsValues = new ContentValues();

                    stepsObject = stepsArray.getJSONObject(stepsIndex);

                    int stepId = stepsObject.getInt("id");
                    String shortDesc = stepsObject.getString("shortDescription");
                    String description = stepsObject.getString("description");
                    String videoUrl = stepsObject.getString("videoURL");

                    stepsValues.put(DbContract.StepsEntry.COLUMN_PARENT_API_ID, recipeApiId);
                    stepsValues.put(DbContract.StepsEntry.COLUMN_STEP_ID, stepId);
                    stepsValues.put(DbContract.StepsEntry.COLUMN_SHORT_DESC, shortDesc);
                    stepsValues.put(DbContract.StepsEntry.COLUMN_DESCRIPTION, description);
                    stepsValues.put(DbContract.StepsEntry.COLUMN_VIDEO_URL, videoUrl);

                    stepsValuesArr.add(stepsValues);
                }
            }

            bulkInsertData(DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY,
                    DbContract.RecipesEntry.TABLE_NAME), recipesValuesArr.toArray(new ContentValues[recipesValuesArr.size()]));

            bulkInsertData(DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY,
                    DbContract.IngredientsEntry.TABLE_NAME), ingredientsValuesArr.toArray(new ContentValues[ingredientsValuesArr.size()]));

            bulkInsertData(DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY,
                    DbContract.StepsEntry.TABLE_NAME), stepsValuesArr.toArray(new ContentValues[stepsValuesArr.size()]));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(getClass().getSimpleName(), getString(R.string.error_json));
        }

        Intent broadcastRecipesInserted = new Intent();
        broadcastRecipesInserted.setAction(ACTION_RECIPES_INSERTED);
        sendBroadcast(broadcastRecipesInserted);
    }

    void bulkInsertData(Uri uri, ContentValues[] contentValuesArr) {
        getContentResolver().bulkInsert(uri, contentValuesArr);
    }
}
