package com.sbschoolcode.bakingapp.ui.recipe.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.models.Step;
import com.sbschoolcode.bakingapp.ui.recipe.details.item.StepDetailsItemFrag;

import java.util.ArrayList;

class StepDetailsPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Step> mStepsList;

    StepDetailsPagerAdapter(FragmentManager fm, ArrayList<Step> stepsList) {
        super(fm);
        this.mStepsList = stepsList;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment stepDetailsFragment = new StepDetailsItemFrag();

        Bundle stepBundle = new Bundle();
        stepBundle.putParcelable(AppConstants.BUNDLE_EXTRA_STEP_MODEL, mStepsList.get(position));

        stepBundle.putInt(AppConstants.BUNDLE_EXTRA_STEP_DETAIL_INDEX, position);
        stepDetailsFragment.setArguments(stepBundle);

        return stepDetailsFragment;
    }

    @Override
    public int getCount() {
        return mStepsList.size();
    }
}
