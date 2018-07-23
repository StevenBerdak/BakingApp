package com.sbschoolcode.bakingapp.controllers;

import android.content.Context;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ExoControllerMediaSession mMediaSession;

    private ExoController() {

    }

    public static ExoController getInstance() {
        if (null == mInstance) {
            mInstance = new ExoController();
        }
        return mInstance;
    }

    /**
     * Prepare the exo player for audio.
     *
     * @param ctx The context prepare the player with.
     */
    public void prepareExoPlayer(Context ctx) {
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(ctx, new DefaultTrackSelector());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaSession = new ExoControllerMediaSession();
            mExoPlayer.addListener(mMediaSession);
            mMediaSession.initialize(ctx);
        }
    }

    /**
     * Release the exo players resources.
     */
    public void releaseExoPlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaSession.shutdown();
        }
        mExoPlayer = null;
    }

    /**
     * Set a new media source represented by a Uri on the player.
     *
     * @param ctx The context to use to set the media source.
     * @param uri The uri pointing at the media source.
     */
    public void setMediaSource(Context ctx, Uri uri) {
        DefaultHttpDataSourceFactory factory =
                new DefaultHttpDataSourceFactory(Util.getUserAgent(ctx, AppConstants.APP_NAME));

        mMediaSource = new ExtractorMediaSource.Factory(new CacheDataSourceFactory(
                new SimpleCache(ctx.getCacheDir(), new NoOpCacheEvictor()),
                factory)).createMediaSource(uri);
    }

    /**
     * Attaches the media source to the player.
     */
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

    /**
     * An inner class containing callbacks for the media session.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class ExoControllerMediaSession extends MediaSession.Callback implements Player.EventListener {

        MediaSession mMediaSession;

        ExoControllerMediaSession() {

        }

        void initialize(Context ctx) {
            mMediaSession = new MediaSession(ctx, "ExoControllerMediaSession");
            mMediaSession.setCallback(this);
            mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mMediaSession.setMediaButtonReceiver(null);
            mMediaSession.setActive(true);
        }

        void shutdown() {
            mMediaSession.setActive(false);
        }

        @Override
        public void onPlay() {
            if (mExoPlayer != null) mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onStop() {
            if (mExoPlayer != null) mExoPlayer.stop();
        }

        @Override
        public void onPause() {
            if (mExoPlayer != null) mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    }
}
