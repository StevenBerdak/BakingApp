package com.sbschoolcode.bakingapp.data;

import android.net.Uri;

public class DataUtils {

    /**
     * Get a valid uri to access database content.
     *
     * @param authority The authority to use for the uri.
     * @param tableName The table name to attach the uri path.
     * @return A created Uri.
     */
    public static Uri getContentUri(String authority, String tableName) {
        Uri.Builder builder = new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .appendEncodedPath(tableName);
        return builder.build();
    }
}
