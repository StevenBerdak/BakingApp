package com.sbschoolcode.bakingapp.widget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.data.DataUtils;
import com.sbschoolcode.bakingapp.data.DbContract;
import com.sbschoolcode.bakingapp.models.Ingredient;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class BakingWidgetViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.v(AppConstants.TESTING, "App widget get view factory");
        return new ViewsFactory();
    }

    class ViewsFactory implements RemoteViewsFactory {

        ArrayList<Ingredient> mIngredientsList;

        @Override
        public void onCreate() {
            Log.v(AppConstants.TESTING, "App widget onCreate");

            loadIngredients();
        }

        @Override
        public void onDataSetChanged() {
            Log.v(AppConstants.TESTING, "App widget onDataSetChanged");
            AppUtils.testShiv(getClass(),"ran");

            loadIngredients();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            Log.v(AppConstants.TESTING, "App widget getCount returning " + mIngredientsList.size());
            return mIngredientsList.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            Log.v(AppConstants.TESTING, "App getting updating widget view at position " + i + ". List size = " + mIngredientsList.size());
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.steps_header_ingredient_pair);

            NumberFormat format = new DecimalFormat("0.#");

            views.setTextViewText(R.id.ingredient_quantity, format.format(mIngredientsList.get(i).quantity));
            views.setTextViewText(R.id.ingredient_measure, mIngredientsList.get(i).measure);
            views.setTextViewText(R.id.ingredient_name, AppUtils.normalizeWords(mIngredientsList.get(i).ingredient));

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private void loadIngredients() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int apiId = preferences.getInt(AppConstants.PREF_DETAILS_LAST_LOAD, -1);
            if (apiId < 0) return;

            Uri ingredientUri = DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.IngredientsEntry.TABLE_NAME)
                    .buildUpon().appendPath(Integer.toString(apiId)).build();
            Cursor cursor = getContentResolver().query(ingredientUri, null, null, null, null);
            Log.v(AppConstants.TESTING, cursor == null ? "Widget update cursor null" : "Widget cursor update, count = " +cursor.getCount());
            mIngredientsList = new ArrayList<>();
            if (cursor != null) {
                for (int i = 0; i < cursor.getCount(); ++i) {
                    cursor.moveToNext();
                    int quantityIndex = cursor.getColumnIndex(DbContract.IngredientsEntry.COLUMN_QUANTITY);
                    int measureIndex = cursor.getColumnIndex(DbContract.IngredientsEntry.COLUMN_MEASURE);
                    int ingredientIndex = cursor.getColumnIndex(DbContract.IngredientsEntry.COLUMN_INGREDIENT);
                    mIngredientsList.add(new Ingredient(cursor.getDouble(quantityIndex),
                            cursor.getString(measureIndex),
                            cursor.getString(ingredientIndex)));
                }
                cursor.close();
            }
        }
    }
}
