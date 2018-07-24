package com.sbschoolcode.bakingapp.ui.recipe.steps;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.models.Ingredient;
import com.sbschoolcode.bakingapp.models.Step;
import com.sbschoolcode.bakingapp.ui.recipe.details.StepDetailsPagerFrag;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsListFrag extends Fragment implements View.OnClickListener {

    @BindView(R.id.steps_recycler_view)
    RecyclerView mStepsRecyclerView;
    private boolean mIsLargeLayout;

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

        mIsLargeLayout = null != view.findViewById(R.id.step_details_container);

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

            StepsListAdapter stepsListAdapter = new StepsListAdapter(this);

            mStepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mStepsRecyclerView.setAdapter(stepsListAdapter);

            stepsListAdapter.swapArrays(mStepsList, mIngredientsList);
        }

        if (mIsLargeLayout) loadDetailFragment(0);
    }

    /**
     * Load the fragment containing the details of the recipe.
     *
     * @param index The index of the detail from the details list.
     */
    private void loadDetailFragment(int index) {
        PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .edit()
                .putInt(AppConstants.PREF_STEP_INDEX, index)
                .apply();

        Fragment detailFragment = new StepDetailsPagerFrag();

        Bundle detailBundle = new Bundle();
        detailBundle.putAll(getArguments());
        detailBundle.putInt(AppConstants.BUNDLE_EXTRA_STEP_DETAIL_INDEX, index);
        detailFragment.setArguments(detailBundle);
        if (getFragmentManager() != null) {
            if (mIsLargeLayout) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.step_details_container, detailFragment, AppConstants.FRAGMENT_DETAIL_TAG).commit();
            } else {
                getFragmentManager().beginTransaction().addToBackStack(getTag())
                        .replace(getId(), detailFragment, AppConstants.FRAGMENT_DETAIL_TAG).commit();
            }
        }
    }
}
