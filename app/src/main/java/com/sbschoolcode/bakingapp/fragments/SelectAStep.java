package com.sbschoolcode.bakingapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.models.Step;

import java.util.ArrayList;

public class SelectAStep extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.select_a_step_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ArrayList<Step> mStepsArrayList;
        if (getActivity() == null) {
            AppUtils.makeLongToast(getContext(), getString(R.string.error_unrecoverable));
            return;
        }
        if (getArguments() == null) {
            AppUtils.makeToast(getContext(), getString(R.string.error_recipe_data));
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            mStepsArrayList = getArguments().getParcelableArrayList(AppConstants.INTENT_EXTRA_STEPS_LIST);
            if (mStepsArrayList == null || mStepsArrayList.size() == 0) {
                AppUtils.makeToast(getContext(), getString(R.string.error_recipe_data));
                getActivity().getSupportFragmentManager().popBackStack();
            }
            ListView mStepsListView = view.findViewById(R.id.steps_list_view);
            assert mStepsArrayList != null;
            mStepsListView.setAdapter(new ArrayAdapter<>(view.getContext(), R.layout.step_list_item, mStepsArrayList));
        }
    }
}
