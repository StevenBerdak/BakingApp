package com.sbschoolcode.bakingapp.ui.recipe.steps;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.models.Ingredient;
import com.sbschoolcode.bakingapp.models.Step;
import com.sbschoolcode.bakingapp.ui.recipe.detail.StepDetailsFrag;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectStepFrag extends Fragment implements View.OnClickListener {

    @BindView(R.id.steps_recycler_view)
    RecyclerView mStepsRecyclerView;

    @Override
    public void onClick(View v) {
        Log.v(AppConstants.TESTING, "Fragment item clicked: " + v.getTag());

        loadDetailFragment((int) v.getTag());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_step, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        ArrayList<Step> mStepsList;
        ArrayList<Ingredient> mIngredientsList;
        if (getActivity() == null) {
            AppUtils.makeLongToast(getContext(), getString(R.string.error_unrecoverable));
            return;
        }
        if (getArguments() == null) {
            AppUtils.makeToast(getContext(), getString(R.string.error_recipe_data));
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            mStepsList = getArguments().getParcelableArrayList(AppConstants.INTENT_EXTRA_STEPS_LIST);
            mIngredientsList = getArguments().getParcelableArrayList(AppConstants.INTENT_EXTRA_INGREDIENTS_LIST);
            if (mStepsList == null || mStepsList.size() == 0 ||
                    mIngredientsList == null || mIngredientsList.size() == 0) {
                AppUtils.makeToast(getContext(), getString(R.string.error_recipe_data));
                getActivity().getSupportFragmentManager().popBackStack();
            }

            StepsAdapter stepsAdapter = new StepsAdapter(this);

            mStepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mStepsRecyclerView.setAdapter(stepsAdapter);

            stepsAdapter.swapArrays(mStepsList, mIngredientsList);
        }
        Log.v(AppConstants.TESTING, "SelectStepFrag loaded");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int detailLoaded = sharedPreferences.getInt(AppConstants.PREF_DETAILS_LOADED, -1);
        if (detailLoaded > 0) loadDetailFragment(detailLoaded);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void loadDetailFragment(int index) {

        Fragment detailFragment = new StepDetailsFrag();
        if (getArguments() != null)
            getArguments().getParcelableArrayList(AppConstants.INTENT_EXTRA_INGREDIENTS_LIST);

        Log.v(AppConstants.TESTING, "Attempting to load details model with id = " + index);

        Bundle stepBundle = new Bundle();
        Parcelable stepParcelable;
        ArrayList<Step> stepList = getArguments().getParcelableArrayList(AppConstants.INTENT_EXTRA_STEPS_LIST);
        if (stepList != null && stepList.size() > 0) stepParcelable = stepList.get(index);
        else {
            Log.e(getClass().getSimpleName(), getString(R.string.error_loading_step_model));
            return;
        }
        stepBundle.putParcelable(AppConstants.BUNDLE_EXTRA_STEP_MODEL, stepParcelable);
        detailFragment.setArguments(stepBundle);
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().addToBackStack(getTag())
                    .replace(getId(), detailFragment, AppConstants.FRAGMENT_DETAIL_TAG).commit();
        }
    }
}
