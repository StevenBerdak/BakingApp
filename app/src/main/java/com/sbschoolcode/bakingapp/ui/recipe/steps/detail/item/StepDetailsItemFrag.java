package com.sbschoolcode.bakingapp.ui.recipe.steps.detail.item;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private ExoController mExoController = ExoController.getInstance();

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

    private void init() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            mExoController.setMediaSource(getContext(), Uri.parse(mVideoUrl));
            mExoController.attachMediaSourceToPlayer();
            mStepPlayerView.setPlayer(mExoController.getExoPlayerInstance());
            mExoController.startPlayback();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            prefs.edit().putInt(AppConstants.PREF_DETAILS_LOADED, mThisIndex).apply();
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            if (isVisibleToUser) {
                if (!mVideoUrl.equals("")) {
                    init();
                } else mExoController.pausePlayback();
            }
        }
    }

    private void prepareImage() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            mStepPlayerView.setVisibility(View.GONE);
            mStepImageView.setVisibility(View.VISIBLE);
            if (getArguments() == null || getActivity() == null) return;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            int recipeApiIndex = sharedPreferences.getInt(AppConstants.PREF_RECIPE_API_INDEX, -1);
            switch (recipeApiIndex) {
                case 1:
                    setImage(R.drawable.nutella_pie);
                    break;
                case 2:
                    setImage(R.drawable.brownies);
                    break;
                case 3:
                    setImage(R.drawable.yellow_cake);
                    break;
                case 4:
                    setImage(R.drawable.cheesecake);
                    break;
                default:
                    setImage(R.drawable.baking);
            }
        });
    }

    private void setImage(int resourceId) {
        Handler handler = new Handler(Looper.getMainLooper());
        if (mStepImageView != null)
            handler.post(() -> mStepImageView.setImageResource(resourceId));
    }
}
