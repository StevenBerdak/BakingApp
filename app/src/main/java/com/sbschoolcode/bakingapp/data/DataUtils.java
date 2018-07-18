package com.sbschoolcode.bakingapp.data;

import android.net.Uri;

public class DataUtils {

    public static Uri getContentUri(String authority, String tableName) {
        Uri.Builder builder = new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .appendEncodedPath(tableName);
        return builder.build();
    }
}
