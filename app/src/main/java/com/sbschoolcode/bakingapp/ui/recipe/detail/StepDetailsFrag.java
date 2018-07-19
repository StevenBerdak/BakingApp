package com.sbschoolcode.bakingapp.ui.recipe.detail;

import android.content.SharedPreferences;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheEvictor;
import com.google.android.exoplayer2.upstream.cache.CacheSpan;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.models.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepDetailsFrag extends Fragment {

    public static final String ACTION_SET_MEDIA_SOURCE = "com.sbschoolcode.broadcast.SET_MEDIA_SOURCE";
    @BindView(R.id.recipe_player_view)
    PlayerView mRecipePlayerView;
    ExoPlayer mExoPlayer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), new DefaultTrackSelector());
        mRecipePlayerView.setPlayer(mExoPlayer);

        Step stepModel = null;
        if (getArguments() != null)
            stepModel = getArguments().getParcelable(AppConstants.BUNDLE_EXTRA_STEP_MODEL);
        if (stepModel == null) {
            AppUtils.makeToast(getContext(), getString(R.string.error_recipe_data));
            if (getActivity() != null) {
                AppUtils.makeLongToast(getContext(), getString(R.string.error_recipe_data));
                getActivity().getSupportFragmentManager().popBackStack();
            } else AppUtils.makeLongToast(getContext(), getString(R.string.error_unrecoverable));
            return;
        }
        setStepLoaded(stepModel.id);
        String url = stepModel.videoUrl;
        Log.v(AppConstants.TESTING, "video url = " + url);
        if (url != null && !url.equals("")) {
            prepareExoPlayer(url);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setStepLoaded(-1);
    }

    private void setStepLoaded(int stepId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putInt(AppConstants.PREF_DETAILS_LOADED, stepId).apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        mExoPlayer.stop();
        mExoPlayer.release();
    }

    public void prepareExoPlayer(String url) {
        MediaSource mediaSource;
        ExtractorMediaSource.Factory mediaSourceFactory =
                new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(Util.getUserAgent(getContext(), AppConstants.APP_NAME)))
                        .setExtractorsFactory(Mp4Extractor.FACTORY);
        if (getActivity() != null)
            mediaSource =
                    new ExtractorMediaSource.Factory(new CacheDataSourceFactory(new SimpleCache(getActivity().getCacheDir(), new NoOpCacheEvictor()),
                            new DefaultHttpDataSourceFactory(Util.getUserAgent(getContext(), AppConstants.APP_NAME)))).createMediaSource(Uri.parse(url));
        else
            mediaSource = mediaSourceFactory.createMediaSource(Uri.parse(url));
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
    }
}
