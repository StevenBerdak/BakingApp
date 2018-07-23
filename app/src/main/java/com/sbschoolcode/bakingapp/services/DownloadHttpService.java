package com.sbschoolcode.bakingapp.services;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.ui.MainActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadHttpService extends JobIntentService {

    public static final String EXTRA_IN_REQUEST_URL = "request_url";
    public static final String EXTRA_OUT_HTTP_RESULT = "extra_http_result";
    public static final String ACTION_HTTP_RESULT = "com.sbschoolcode.broadcast.HTTP_RESULT";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        try {
            String httpData = downloadHttpData(intent.getStringExtra(EXTRA_IN_REQUEST_URL));

            if (httpData != null && httpData.length() > 0) {
                Intent broadcastHttpResult = new Intent(ACTION_HTTP_RESULT);
                broadcastHttpResult.putExtra(EXTRA_OUT_HTTP_RESULT, httpData);

                sendBroadcast(broadcastHttpResult);
            } else {
                sendBroadcast(new Intent(MainActivity.ACTION_NO_NETWORK));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(getClass().getSimpleName(), getString(R.string.error_http));
            sendBroadcast(new Intent(MainActivity.ACTION_NO_NETWORK));
        }
    }

    /**
     * Initialize and download the http data.
     * @param requestUrl The url to connect to and request data from.
     * @return The String with the returned data.
     * @throws IOException if the response is an error or invalid.
     */
    private String downloadHttpData(String requestUrl) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(requestUrl).get().build();
        Response response = httpClient.newCall(request).execute();

        if (response != null && response.code() == 200) {
            ResponseBody responseBody = response.body();

            if (responseBody != null) {
                return responseBody.string();
            }
        } else {
            throw new IOException();
        }

        Log.e(getClass().getSimpleName(), getString(R.string.error_http_download));
        return null;
    }
}
