package com.sbschoolcode.bakingapp.controllers;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.sbschoolcode.bakingapp.AppConstants;

public class ExoController {

    private static ExoController mInstance;
    private ExoPlayer mExoPlayer;
    private MediaSource mMediaSource;

    private ExoController() {

    }

    public static ExoController getInstance() {
        if (null == mInstance) {
            mInstance = new ExoController();
        }
        return mInstance;
    }

    public void prepareExoPlayer(Context ctx) {
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(ctx, new DefaultTrackSelector());
    }

    public void releaseExoPlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    public void setMediaSource(Context ctx, Uri uri) {
        DefaultHttpDataSourceFactory factory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(ctx, AppConstants.APP_NAME));

        mMediaSource = new ExtractorMediaSource.Factory(new CacheDataSourceFactory(
                new SimpleCache(ctx.getCacheDir(), new NoOpCacheEvictor()),
                        factory)).createMediaSource(uri);
    }

    public void attachMediaSourceToPlayer() {
        mExoPlayer.prepare(mMediaSource);
    }

    public ExoPlayer getExoPlayerInstance() {
        return mExoPlayer;
    }

    public void pausePlayback() {
        mExoPlayer.setPlayWhenReady(false);
    }

    public void startPlayback() {
        mExoPlayer.setPlayWhenReady(true);
    }
}
