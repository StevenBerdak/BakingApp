package com.sbschoolcode.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidget extends AppWidgetProvider {

    /**
     * Update the data within the widget using the service.
     *
     * @param context          The context to use for updating the widget.
     * @param appWidgetManager An AppWidgetManager instance.
     * @param appWidgetId      The id of the widget to update.
     */
    static void updateData(Context context, AppWidgetManager appWidgetManager,
                           int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int apiId = preferences.getInt(AppConstants.PREF_RECIPE_WIDGET_ID, -1);

        if (apiId < 0) {
            views.setTextViewText(R.id.remote_list_data_info, context.getResources().getString(R.string.select_recipe_use_hint));
            views.setViewVisibility(R.id.remote_list_view, View.GONE);
        } else {
            String recipeName = PreferenceManager.getDefaultSharedPreferences(context).getString(AppConstants.PREF_RECIPE_LOADED_NAME, context.getString(R.string.recipe));
            views.setTextViewText(R.id.remote_list_data_info, recipeName + " " + context.getString(R.string.ingredients));
            views.setViewVisibility(R.id.remote_list_view, View.VISIBLE);

            Intent intent = new Intent(context, BakingWidgetViewsService.class);
            views.setRemoteAdapter(R.id.remote_list_view, intent);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateData(context, appWidgetManager, appWidgetId);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.remote_list_data_info);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.remote_list_view);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

    }
}

