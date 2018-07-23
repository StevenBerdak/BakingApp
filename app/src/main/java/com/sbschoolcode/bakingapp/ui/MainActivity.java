package com.sbschoolcode.bakingapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.controllers.ServiceController;
import com.sbschoolcode.bakingapp.data.DataUtils;
import com.sbschoolcode.bakingapp.data.DbContract;
import com.sbschoolcode.bakingapp.ui.recipe.RecipeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String ACTION_DOWNLOAD_RECIPES = "com.sbschoolcode.broadcast.DOWNLOAD_RECIPES";
    public static final String ACTION_INIT_LOADER = "com.sbschoolcode.broadcast.INIT_LOADER";
    public static final String ACTION_NO_NETWORK = "com.sbschoolcode.broadcast.NO_NETWORK";
    private ServiceController mServiceController;
    @BindView(R.id.recipe_list_recycler_view)
    RecyclerView mRecipeListRecyclerView;
    @BindView(R.id.no_network_tv)
    TextView mNoNetworkTextView;
    @BindView(R.id.no_network_button)
    Button mNoNetworkButton;
    @BindView(R.id.main_progress_bar)
    ProgressBar mMainProgressBar;
    private MainAdapter mMainAdapter;
    private BroadcastReceiver mMainReceiver;
    private IntentFilter mMainIntentFilter;

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

        /* Init view components. */
        boolean isLargeScreen = null != findViewById(R.id.layout_large_spy);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, isLargeScreen ? 3 : 1);

        mRecipeListRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecipeListRecyclerView.setAdapter(mMainAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        AppUtils.lastRecipeLoaded(this);
        outState.putInt(AppConstants.BUNDLE_RECIPE_LOADED_ID, AppUtils.lastRecipeLoaded(this));
        outState.putString(AppConstants.BUNDLE_RECIPE_LOADED_NAME, AppUtils.lastRecipeLoadedByName(this));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (AppUtils.recipeIsLoaded(this)) {
            String recipeLoadedName = savedInstanceState.getString(AppConstants.BUNDLE_RECIPE_LOADED_NAME, "");
            int recipeLoadedId = savedInstanceState.getInt(AppConstants.BUNDLE_RECIPE_LOADED_ID, -1);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> loadRecipeActivity(recipeLoadedId, recipeLoadedName));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    /**
     * Initialize the inner broadcast receiver.
     */
    private void initReceiver() {
        mMainReceiver = new MainReceiver();
        mMainIntentFilter = new IntentFilter();
        mMainIntentFilter.addAction(ACTION_DOWNLOAD_RECIPES);
        mMainIntentFilter.addAction(ACTION_INIT_LOADER);
        mMainIntentFilter.addAction(ACTION_NO_NETWORK);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri recipesUri = DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.RecipesEntry.TABLE_NAME);
        return new CursorLoader(this, recipesUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor data) {
        data.setNotificationUri(getContentResolver(), DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.RecipesEntry.TABLE_NAME));
        mMainProgressBar.setVisibility(View.GONE);
        mMainAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mMainAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View v) {
        TextView recipeNameTv = v.findViewById(R.id.recipe_list_item_name_tv);
        loadRecipeActivity((int) v.getTag(), recipeNameTv.getText().toString());
    }

    /**
     * Load the recipe activity for the selected recipe.
     *
     * @param apiTag The api id from the tag attached to the view selected.
     * @param name   The human readable name of the recipe.
     */
    private void loadRecipeActivity(int apiTag, String name) {
        AppUtils.setPreferenceRecipeLoaded(this, apiTag, name, true);
        Intent recipeActivity = new Intent(this, RecipeActivity.class);
        recipeActivity.putExtra(AppConstants.INTENT_EXTRA_RECIPE, name);
        recipeActivity.putExtra(AppConstants.INTENT_EXTRA_RECIPE_API_INDEX, apiTag);
        startActivity(recipeActivity);
    }

    /**
     * Manually initiate a data download.
     *
     * @param view Necessary to use inClick property.
     */
    public void manualLoadData(View view) {
        mMainProgressBar.setVisibility(View.VISIBLE);
        mNoNetworkButton.setVisibility(View.GONE);
        mNoNetworkTextView.setVisibility(View.GONE);
        mServiceController.initDownloadOrSkip(this);
    }

    /**
     * Show the error view text and button.
     */
    public void showNetworkErrorViews() {
        mMainProgressBar.setVisibility(View.GONE);
        mNoNetworkButton.setVisibility(View.VISIBLE);
        mNoNetworkTextView.setVisibility(View.VISIBLE);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mCountingIdlingResource == null)
            mCountingIdlingResource = new CountingIdlingResource("WaitForRecipes");
        return mCountingIdlingResource;
    }

    private class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            switch (intent.getAction()) {
                case ACTION_DOWNLOAD_RECIPES:
                    mServiceController.startDownloadJsonData(MainActivity.this);
                    break;
                case ACTION_INIT_LOADER:
                    getSupportLoaderManager().initLoader(AppConstants.RECIPE_LOADER_ID, null, MainActivity.this);
                    if (mCountingIdlingResource != null) mCountingIdlingResource.decrement();
                    break;
                case ACTION_NO_NETWORK:
                    showNetworkErrorViews();
                    break;
            }
        }
    }
}
