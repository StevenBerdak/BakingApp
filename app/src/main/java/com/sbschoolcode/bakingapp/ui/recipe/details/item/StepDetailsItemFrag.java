package com.sbschoolcode.bakingapp.ui.recipe.details.item;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.PlayerView;
import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.controllers.ExoController;
import com.sbschoolcode.bakingapp.models.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepDetailsItemFrag extends Fragment {

    private static final String STATE_CURRENT_PLAYER_POSITION = "state_current_player_pos";
    @BindView(R.id.step_player_view)
    PlayerView mStepPlayerView;
    @BindView(R.id.step_details_text_view)
    TextView mStepDescriptionTextView;
    @BindView(R.id.step_image_view)
    ImageView mStepImageView;
    private int mThisIndex;
    private Step mStepModel;
    private long mCurrentPosition;
    private ExoController mExoController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_details_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mStepModel = getArguments().getParcelable(AppConstants.BUNDLE_EXTRA_STEP_MODEL);
            if (mStepModel != null) {
                mStepDescriptionTextView.setText(mStepModel.description);
                if (mStepModel.videoUrl.length() == 0) prepareImage();

                mThisIndex = getArguments().getInt(AppConstants.BUNDLE_EXTRA_STEP_DETAIL_INDEX, -1);
                return;
            }

            if (getActivity() != null) {
                AppUtils.makeLongToast(getContext(), getString(R.string.error_recipe_data));
                getActivity().getSupportFragmentManager().popBackStack();
            } else
                AppUtils.makeLongToast(getContext(), getString(R.string.error_unrecoverable));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STATE_CURRENT_PLAYER_POSITION, mCurrentPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        clearExoPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mThisIndex == PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(AppConstants.PREF_STEP_INDEX, 0)) {
            if (mStepModel.videoUrl.length() > 0) {
                prepareExoPlayer();
            }
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
            mCurrentPosition = savedInstanceState.getLong(STATE_CURRENT_PLAYER_POSITION);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            if (isVisibleToUser) {
                prepareExoPlayer();
            } else {
                clearExoPlayer();
            }
        }
    }

    private void prepareExoPlayer() {
        if (mStepModel.videoUrl.length() > 0 && mExoController == null) {
            mExoController = ExoController.getInstance();
            mExoController.prepareExoPlayer(getContext());
            initializeExoPlayer();
        }
    }

    private void initializeExoPlayer() {
        mExoController.setMediaSource(getContext(), Uri.parse(mStepModel.videoUrl));
        mExoController.attachMediaSourceToPlayer();
        mStepPlayerView.setPlayer(mExoController.getExoPlayerInstance());
        mExoController.seekToPosition(mCurrentPosition);
        mExoController.startPlayback();
    }

    private void clearExoPlayer() {
        if (mExoController != null) {
            mCurrentPosition = mExoController.getExoPlayerInstance().getCurrentPosition();
            mExoController.pausePlayback();
            mExoController = null;
        }
    }

    /**
     * Prepare the image to load in to the image view.
     */
    private void prepareImage() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            mStepPlayerView.setVisibility(View.GONE);
            mStepImageView.setVisibility(View.VISIBLE);
            if (getArguments() == null || getActivity() == null) return;
            int apiId = PreferenceManager
                    .getDefaultSharedPreferences(getContext())
                    .getInt(AppConstants.PREF_RECIPE_API_ID, 0);
            if (mStepModel.thumbnailUrl.length() > 0)
                Glide.with(getActivity()).load(mStepModel.thumbnailUrl).into(mStepImageView);
            else
                AppUtils.setImage(mStepImageView, AppUtils.getRecipeDrawable(apiId));
        });
    }
}
