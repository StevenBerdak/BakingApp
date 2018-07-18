package com.sbschoolcode.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.sbschoolcode.bakingapp.controllers.ServiceController;
import com.sbschoolcode.bakingapp.data.DataUtils;
import com.sbschoolcode.bakingapp.data.DbContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ACTION_DOWNLOAD_RECIPES = "com.sbschoolcode.broadcast.DOWNLOAD_RECIPES";
    public static final String ACTION_INIT_LOADER = "com.sbschoolcode.broadcast.INIT_LOADER";
    private ServiceController mServiceController;
    @BindView(R.id.recipe_list_recycler_view)
    RecyclerView mRecipeListRecyclerView;
    private MainAdapter mMainAdapter;
    private BroadcastReceiver mMainReceiver;
    private IntentFilter mMainIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initReceiver();
        mServiceController = ServiceController.getInstance();
        mServiceController.initDownloadOrSkip(this);

        mMainAdapter = new MainAdapter();
        //Todo: need to be responsive for phone vs tablet
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 1);
        /* Init view components. */

        mRecipeListRecyclerView = findViewById(R.id.recipe_list_recycler_view);
        mRecipeListRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecipeListRecyclerView.setAdapter(mMainAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        data.setNotificationUri(getContentResolver(),DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.RecipesEntry.TABLE_NAME));
        mMainAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mMainAdapter.swapCursor(null);
    }

    private class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == null) return;
            if (intent.getAction().equals(ACTION_DOWNLOAD_RECIPES)) {
                mServiceController.startDownloadJsonData(MainActivity.this);
            } else if (intent.getAction().equals(ACTION_INIT_LOADER)) {
                getSupportLoaderManager().initLoader(AppConstants.RECIPE_LOADER_ID, null, MainActivity.this);
            }
        }
    }
}
