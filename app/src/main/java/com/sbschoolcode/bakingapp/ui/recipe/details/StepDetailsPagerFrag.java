package com.sbschoolcode.bakingapp.ui.recipe.details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.models.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepDetailsPagerFrag extends Fragment {

    @BindView(R.id.step_details_view_pager)
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            ArrayList<Step> mStepsList = getArguments().getParcelableArrayList(AppConstants.INTENT_EXTRA_STEPS_LIST);
            if (mStepsList != null) {
                StepDetailsPagerAdapter adapter = new StepDetailsPagerAdapter(getChildFragmentManager(), mStepsList);
                mViewPager.setAdapter(adapter);
                mViewPager.setCurrentItem(getArguments().getInt(AppConstants.BUNDLE_EXTRA_STEP_DETAIL_INDEX, 0));
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
}

