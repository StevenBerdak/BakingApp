package com.sbschoolcode.bakingapp.ui.recipe.steps.detail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.models.Step;
import com.sbschoolcode.bakingapp.ui.recipe.steps.detail.item.StepDetailsItemFrag;

import java.util.ArrayList;

class StepDetailsPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Step> mStepsList;
    private int mImportantIndex;
    private boolean mInit = false;

    StepDetailsPagerAdapter(FragmentManager fm, ArrayList<Step> stepsList, int importantIndex) {
        super(fm);
        this.mStepsList = stepsList;
        this.mImportantIndex = importantIndex;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment stepDetailsFragment = new StepDetailsItemFrag();

        Bundle stepBundle = new Bundle();
        stepBundle.putParcelable(AppConstants.BUNDLE_EXTRA_STEP_MODEL, mStepsList.get(position));

        if (!mInit) stepBundle.putBoolean(AppConstants.BUNDLE_EXTRA_IS_IMPORTANT, mImportantIndex == position);
        stepBundle.putInt(AppConstants.BUNDLE_EXTRA_STEP_INDEX, position);
        stepDetailsFragment.setArguments(stepBundle);

        return stepDetailsFragment;
    }


    @Override
    public int getCount() {
        return mStepsList.size();
    }
}
