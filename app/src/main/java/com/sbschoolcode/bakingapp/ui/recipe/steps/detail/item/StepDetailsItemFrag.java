package com.sbschoolcode.bakingapp.ui.recipe.steps.detail.item;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlayerView;
import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.controllers.ExoController;
import com.sbschoolcode.bakingapp.models.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepDetailsItemFrag extends Fragment {

    @BindView(R.id.step_player_view)
    PlayerView mStepPlayerView;
    @BindView(R.id.step_details_text_view)
    TextView mStepDescriptionTextView;
    @BindView(R.id.step_image_view)
    ImageView mStepImageView;
    private String mVideoUrl = "";
    private int mThisIndex;
    private final ExoController mExoController = ExoController.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_details_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            Step stepModel = getArguments().getParcelable(AppConstants.BUNDLE_EXTRA_STEP_MODEL);
            if (stepModel != null) {
                mStepDescriptionTextView.setText(stepModel.description);
                if ((mVideoUrl = stepModel.videoUrl).equals("")) prepareImage();

                boolean isImportant = getArguments().getBoolean(AppConstants.BUNDLE_EXTRA_IS_IMPORTANT, false);
                if (isImportant) {
                    init();
                }

                mThisIndex = getArguments().getInt(AppConstants.BUNDLE_EXTRA_STEP_INDEX, -1);
                return;
            }
        }

        if (getActivity() != null) {
            AppUtils.makeLongToast(getContext(), getString(R.string.error_recipe_data));
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            AppUtils.makeLongToast(getContext(), getString(R.string.error_unrecoverable));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppUtils.setPreferenceDetailLoaded(getContext(), mThisIndex, false);
    }

    private void init() {
        mExoController.setMediaSource(getContext(), Uri.parse(mVideoUrl));
        mExoController.attachMediaSourceToPlayer();
        mStepPlayerView.setPlayer(mExoController.getExoPlayerInstance());
        mExoController.startPlayback();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            if (isVisibleToUser) {
                AppUtils.setPreferenceDetailLoaded(getContext(), mThisIndex, true);
                if (!mVideoUrl.equals("")) {
                    init();
                } else {
                    mExoController.pausePlayback();
                }
            }
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
            int recipeApiIndex = AppUtils.lastRecipeLoaded(getContext());
            AppUtils.setImage(mStepImageView, AppUtils.getRecipeDrawable(recipeApiIndex));
        });
    }
}
