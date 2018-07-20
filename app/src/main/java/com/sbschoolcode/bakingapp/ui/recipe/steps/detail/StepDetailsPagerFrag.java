package com.sbschoolcode.bakingapp.ui.recipe.steps.detail;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.controllers.ExoController;
import com.sbschoolcode.bakingapp.models.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepDetailsPagerFrag extends Fragment {

    @BindView(R.id.step_details_view_pager)
    ViewPager mViewPager;
    private ExoController mExoController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        mExoController = ExoController.getInstance();
        mExoController.prepareExoPlayer(getContext());

        if (getArguments() != null) {
            ArrayList<Step> stepsList = getArguments().getParcelableArrayList(AppConstants.INTENT_EXTRA_STEPS_LIST);
            int currentIndex = getArguments().getInt(AppConstants.BUNDLE_EXTRA_STEP_INDEX);
            if (stepsList != null) {
                mViewPager.setAdapter(new StepDetailsPagerAdapter(getChildFragmentManager(), stepsList, currentIndex));
                if (currentIndex != mViewPager.getCurrentItem()) mViewPager.setCurrentItem(currentIndex);
            } else {
                AppUtils.makeLongToast(getContext(), getString(R.string.error_recipe_data));
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                } else {
                    AppUtils.makeLongToast(getContext(), getString(R.string.error_unrecoverable));
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mExoController.pausePlayback();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putInt(AppConstants.PREF_DETAILS_LOADED, -1).apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        mExoController.startPlayback();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExoController.releaseExoPlayer();
        mExoController = null;
    }
}

