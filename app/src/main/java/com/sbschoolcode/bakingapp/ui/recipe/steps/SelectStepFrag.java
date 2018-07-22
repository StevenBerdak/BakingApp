package com.sbschoolcode.bakingapp.ui.recipe.steps;

import android.os.Bundle;
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
import com.sbschoolcode.bakingapp.ui.recipe.steps.detail.StepDetailsPagerFrag;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectStepFrag extends Fragment implements View.OnClickListener {

    @BindView(R.id.steps_recycler_view)
    RecyclerView mStepsRecyclerView;

    @Override
    public void onClick(View v) {
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

        if (savedInstanceState == null && AppUtils.detailIsLoaded(getContext())) {
            loadDetailFragment(AppUtils.lastDetailLoaded(getContext()));
        }
    }

    private void loadDetailFragment(int index) {

        Fragment detailFragment = new StepDetailsPagerFrag();

        AppUtils.setPreferenceDetailLoaded(getContext(), index, true);

        Bundle stepBundle = new Bundle();
        stepBundle.putInt(AppConstants.BUNDLE_EXTRA_STEP_INDEX, index);
        stepBundle.putAll(getArguments());
        detailFragment.setArguments(stepBundle);
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().addToBackStack(getTag())
                    .replace(getId(), detailFragment, AppConstants.FRAGMENT_DETAIL_TAG).commit();
        }
    }
}
