package com.sbschoolcode.bakingapp.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.services.BuildRecipeItemService;
import com.sbschoolcode.bakingapp.services.DownloadHttpService;
import com.sbschoolcode.bakingapp.services.InsertRecipesService;
import com.sbschoolcode.bakingapp.services.IsDatabaseInitializedService;
import com.sbschoolcode.bakingapp.ui.MainActivity;

public class ServiceController {

    private static ServiceController mServiceControllerInstance;
    private final BroadcastReceiver mServiceControllerReceiver;
    private final IntentFilter mIntentFilter;

    private ServiceController() {
        mServiceControllerReceiver = new ServiceControllerReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(DownloadHttpService.ACTION_HTTP_RESULT);
        mIntentFilter.addAction(InsertRecipesService.ACTION_RECIPES_INSERTED);
    }

    public static ServiceController getInstance() {
        if (null == mServiceControllerInstance) {
            mServiceControllerInstance = new ServiceController();
        }
        return mServiceControllerInstance;
    }

    /**
     * Register this class inner broadcast receiver.
     *
     * @param ctx The context to register the receiver with.
     */
    public void registerReceiver(Context ctx) {
        ctx.registerReceiver(mServiceControllerReceiver, mIntentFilter);
    }

    /**
     * Unregister this class inner broadcast receiver.
     *
     * @param ctx The context to unregister from.
     */
    public void unregisterReceiver(Context ctx) {
        ctx.unregisterReceiver(mServiceControllerReceiver);
    }

    /**
     * Start building a recipe item to pass down stream.
     *
     * @param ctx    The context to use to stat the service.
     * @param intent An intent containing setup details for the service.
     */
    public void startBuildRecipeItem(Context ctx, Intent intent) {
        BuildRecipeItemService.enqueueWork(ctx, BuildRecipeItemService.class,
                AppConstants.GET_RECIPE_ITEM_JOB_ID, intent);
    }

    /**
     * Start downloading the Json data used for this app.
     *
     * @param ctx The context to use to start the service.
     */
    public void startDownloadJsonData(Context ctx) {
        Log.v("TESTING", "Start download service called");
        Intent intent = new Intent(ctx, DownloadHttpService.class);
        intent.putExtra(DownloadHttpService.EXTRA_IN_REQUEST_URL, AppConstants.RECIPES_URL);
        DownloadHttpService.enqueueWork(ctx, DownloadHttpService.class, AppConstants.HTTP_DOWNLOAD_JOB_ID, intent);
    }

    /**
     * Checks if the database has data.
     * @param ctx The context to use to start the service.
     */
    public void initDownloadOrSkip(Context ctx) {
        Log.v("TESTING", "initDorSkip");
        Intent intent = new Intent();
        IsDatabaseInitializedService.enqueueWork(ctx, IsDatabaseInitializedService.class, AppConstants.IS_DB_INITIALIZED_JOB_ID, intent);
    }

    public class ServiceControllerReceiver extends BroadcastReceiver {

        public ServiceControllerReceiver() {
        }

        @Override
        public void onReceive(Context ctx, Intent intent) {

            String action = intent.getAction();

            Log.v("TESTING", "Receiver received action = " + action);

            if (null == action) return;

            if (action.equals(DownloadHttpService.ACTION_HTTP_RESULT)) {
                String jsonData = intent.getStringExtra(DownloadHttpService.EXTRA_OUT_HTTP_RESULT);

                if (jsonData == null) {
                    Log.e(getClass().getSimpleName(), "Json returned from server was null");
                    return;
                }

                Intent insertRecipesIntent = new Intent(ctx, InsertRecipesService.class);
                insertRecipesIntent.putExtra(InsertRecipesService.EXTRA_IN_JSON_DATA, jsonData);
                InsertRecipesService.enqueueWork(ctx, InsertRecipesService.class, AppConstants.INSERT_RECIPES_JOB_ID, insertRecipesIntent);
            } else if (action.equals(InsertRecipesService.ACTION_RECIPES_INSERTED)) {
                ctx.sendBroadcast(new Intent(MainActivity.ACTION_INIT_LOADER));
            }
        }
    }
}
