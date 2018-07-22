package com.sbschoolcode.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.constraint.solver.widgets.WidgetContainer;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.controllers.ServiceController;
import com.sbschoolcode.bakingapp.data.DataUtils;
import com.sbschoolcode.bakingapp.data.DbContract;
import com.sbschoolcode.bakingapp.ui.recipe.RecipeActivity;
import com.sbschoolcode.bakingapp.widget.BakingWidget;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.appwidget.AppWidgetManager.getInstance;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String ACTION_DOWNLOAD_RECIPES = "com.sbschoolcode.broadcast.DOWNLOAD_RECIPES";
    public static final String ACTION_INIT_LOADER = "com.sbschoolcode.broadcast.INIT_LOADER";
    private ServiceController mServiceController;
    @BindView(R.id.recipe_list_recycler_view)
    RecyclerView mRecipeListRecyclerView;
    private MainAdapter mMainAdapter;
    private BroadcastReceiver mMainReceiver;
    private IntentFilter mMainIntentFilter;
    private int mRecipeLoaded;
    @Nullable
    CountingIdlingResource mCountingIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initReceiver();
        mServiceController = ServiceController.getInstance();

        mMainAdapter = new MainAdapter(this);
        //Todo: need to be responsive for phone vs tablet
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 1);

        /* Init view components. */
        mRecipeListRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecipeListRecyclerView.setAdapter(mMainAdapter);

        //TODO: responsive image sizes

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int lastLoadId = preferences.getInt(AppConstants.PREF_RECIPE_API_INDEX, -1);
        Log.v(AppConstants.TESTING, "MainActivity onCreate, last load id = " + lastLoadId);
        if (lastLoadId > 0) loadRecipeActivity(lastLoadId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipeLoaded > 0) {
            outState.putInt(AppConstants.BUNDLE_RECIPE_LOADED, mRecipeLoaded);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int storedId = savedInstanceState.getInt(AppConstants.BUNDLE_RECIPE_LOADED, -1);
        if (mRecipeLoaded == 0 && storedId > 0) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> loadRecipeActivity(storedId));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecipeLoaded = 0;

        if (mCountingIdlingResource != null) mCountingIdlingResource.increment();

        mServiceController.initDownloadOrSkip(this);
        mServiceController.registerReceiver(this);
        registerReceiver(mMainReceiver, mMainIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mServiceController.unregisterReceiver(this);
        unregisterReceiver(mMainReceiver);
    }

    private void initReceiver() {
        mMainReceiver = new MainReceiver();
        mMainIntentFilter = new IntentFilter();
        mMainIntentFilter.addAction(ACTION_DOWNLOAD_RECIPES);
        mMainIntentFilter.addAction(ACTION_INIT_LOADER);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.v("TESTING", "Loader created");
        Uri recipesUri = DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.RecipesEntry.TABLE_NAME);
        return new CursorLoader(this, recipesUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor data) {
        Log.v("TESTING", "Loader load finished");
        data.setNotificationUri(getContentResolver(), DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.RecipesEntry.TABLE_NAME));
        mMainAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mMainAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View v) {
        Log.v("TESTING", "Item clicked = " + v.getTag());
        loadRecipeActivity((int) v.getTag());
    }

    private void loadRecipeActivity(int apiTag) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putInt(AppConstants.PREF_RECIPE_API_INDEX, apiTag)
                .putInt(AppConstants.PREF_DETAILS_LAST_LOAD, apiTag).apply();

        mRecipeLoaded = apiTag;
        Intent recipeActivity = new Intent(this, RecipeActivity.class);
        recipeActivity.putExtra(AppConstants.INTENT_EXTRA_RECIPE_API_INDEX, apiTag);
        startActivity(recipeActivity);
    }

    private class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            if (intent.getAction().equals(ACTION_DOWNLOAD_RECIPES)) {
                mServiceController.startDownloadJsonData(MainActivity.this);
            } else if (intent.getAction().equals(ACTION_INIT_LOADER)) {
                getSupportLoaderManager().initLoader(AppConstants.RECIPE_LOADER_ID, null, MainActivity.this);
                if (mCountingIdlingResource != null) mCountingIdlingResource.decrement();
            }
        }
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mCountingIdlingResource == null) mCountingIdlingResource = new CountingIdlingResource("WaitForRecipes");
        return mCountingIdlingResource;
    }
}
