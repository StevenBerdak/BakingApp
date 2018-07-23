package com.sbschoolcode.bakingapp.services;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.sbschoolcode.bakingapp.data.DataUtils;
import com.sbschoolcode.bakingapp.data.DbContract;
import com.sbschoolcode.bakingapp.ui.MainActivity;

public class IsDatabaseInitializedService extends JobIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Cursor cursor = getContentResolver().query(
                DataUtils.getContentUri(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.RecipesEntry.TABLE_NAME),
                null, null, null, null);
        if (null == cursor || cursor.getCount() == 0) {
            sendBroadcast(new Intent(MainActivity.ACTION_DOWNLOAD_RECIPES));
        } else {
            sendBroadcast(new Intent(MainActivity.ACTION_INIT_LOADER));
        }
        if (null != cursor) cursor.close();
    }
}
