package com.sbschoolcode.bakingapp.ui.recipe.detail;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.models.Step;

public class StepDetailsFrag extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Step stepModel = null;
        if (getArguments() != null) stepModel = getArguments().getParcelable(AppConstants.BUNDLE_EXTRA_STEP_MODEL);
        if (stepModel == null) {
            AppUtils.makeToast(getContext(), getString(R.string.error_recipe_data));
            if (getActivity() != null) {
                AppUtils.makeLongToast(getContext(), getString(R.string.error_recipe_data));
                getActivity().getSupportFragmentManager().popBackStack();
            }
            else AppUtils.makeLongToast(getContext(), getString(R.string.error_unrecoverable));
            return;
        }
        setStepLoaded(stepModel.id);

        //TODO; create method of loading video file into ExoPlayer(??)
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setStepLoaded(-1);
    }

    private void setStepLoaded(int stepId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putInt(AppConstants.PREF_DETAILS_LOADED, stepId).apply();
    }
}
