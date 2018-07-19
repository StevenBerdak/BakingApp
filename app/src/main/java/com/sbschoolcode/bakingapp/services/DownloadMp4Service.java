package com.sbschoolcode.bakingapp.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.sbschoolcode.bakingapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadMp4Service extends JobIntentService {

    public static final String EXTRA_IN_REQUEST_URL = "request_url";
    public static final String EXTRA_OUT_VIDEO_FILE = "extra_http_result";
    public static final String ACTION_VIDEO_DOWNLOADED = "com.sbschoolcode.broadcast.HTTP_RESULT";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Log.v("TESTING", "Download http service started");

        try {
            String videoFile = downloadVideoData(intent.getStringExtra(EXTRA_IN_REQUEST_URL));

            Intent broadcastVideoResult = new Intent(ACTION_VIDEO_DOWNLOADED);
            broadcastVideoResult.putExtra(EXTRA_OUT_VIDEO_FILE, videoFile);
            //Todo: check if null and send error broadcast
            sendBroadcast(broadcastVideoResult);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(getClass().getSimpleName(), getString(R.string.error_http));
        }
    }

    private String downloadVideoData(String requestUrl) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(requestUrl).get().build();
        Response response = httpClient.newCall(request).execute();

        if (response != null && response.code() == 200) {
            ResponseBody responseBody = response.body();

            if (responseBody != null) {

                InputStream in = responseBody.byteStream();
                File file = new File(getApplicationContext().getFilesDir() + "/bakingapp/media", Uri.parse(requestUrl).getLastPathSegment());
                OutputStream out = getApplicationContext().openFileOutput(file.getAbsoluteFile().toString(), Context.MODE_PRIVATE);

                byte[] buffer = new byte[in.available()];
                int len;
                while ((len = in.read(buffer, 0, in.available())) != -1) {
                    out.write(buffer, 0, len);
                }

                in.close();
                out.flush();
                out.close();

                return file.getAbsoluteFile().toString();
            }
        }

        Log.e(getClass().getSimpleName(), getString(R.string.error_video_file));
        return null;
    }
}

