package com.sbschoolcode.bakingapp.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.MainActivity;
import com.sbschoolcode.bakingapp.services.DownloadHttpService;
import com.sbschoolcode.bakingapp.services.GetRecipeItemService;
import com.sbschoolcode.bakingapp.services.InsertRecipesService;
import com.sbschoolcode.bakingapp.services.IsDatabaseInitializedService;

public class ServiceController {

    private static ServiceController mServiceControllerInstance;
    private BroadcastReceiver mHttpReceiver;
    private IntentFilter mIntentFilter;

    private ServiceController() {
        mHttpReceiver = new MainControllerReceiver();
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

    public void prepareHttpReceiver(Context context) {
        context.registerReceiver(mHttpReceiver, mIntentFilter);
    }

    public void releaseHttpReceiver(Context context) {
        context.unregisterReceiver(mHttpReceiver);
    }

    public void startQueryRecipeItem(Context context, Intent intent) {
        GetRecipeItemService.enqueueWork(context, GetRecipeItemService.class,
                AppConstants.GET_RECIPE_ITEM_JOB_ID, intent);
    }

    public void startDownloadJsonData(Context context) {
        Log.v("TESTING", "Start download service called");
        Intent intent = new Intent(context, DownloadHttpService.class);
        intent.putExtra(DownloadHttpService.EXTRA_IN_REQUEST_URL, AppConstants.RECIPES_URL);
        DownloadHttpService.enqueueWork(context, DownloadHttpService.class, AppConstants.HTTP_DOWNLOAD_JOB_ID, intent);
    }

    public void initDownloadOrSkip(Context context) {
        Intent emptyIntent = new Intent();
        IsDatabaseInitializedService.enqueueWork(context, IsDatabaseInitializedService.class, AppConstants.IS_DB_INITIALIZED_JOB_ID, emptyIntent);
    }

    public class MainControllerReceiver extends BroadcastReceiver {

        public MainControllerReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            Log.v("TESTING", "Receiver received action = " + action);

            if (null == action) return;

            if (action.equals(DownloadHttpService.ACTION_HTTP_RESULT)) {
                String jsonData = intent.getStringExtra(DownloadHttpService.EXTRA_OUT_HTTP_RESULT);

                if (jsonData == null) {
                    Log.e(getClass().getSimpleName(), "Json returned from server was null");
                    return;
                }

                Intent insertRecipesIntent = new Intent(context, InsertRecipesService.class);
                insertRecipesIntent.putExtra(InsertRecipesService.EXTRA_IN_JSON_DATA, jsonData);
                InsertRecipesService.enqueueWork(context, InsertRecipesService.class, AppConstants.INSERT_RECIPES_JOB_ID, insertRecipesIntent);
            } else if (action.equals(InsertRecipesService.ACTION_RECIPES_INSERTED)) {
                context.sendBroadcast(new Intent(MainActivity.ACTION_INIT_LOADER));
            }
        }
    }
}
