package com.sbschoolcode.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sbschoolcode.bakingapp.controllers.ServiceController;
import com.sbschoolcode.bakingapp.fragments.SelectAStep;
import com.sbschoolcode.bakingapp.models.Recipe;
import com.sbschoolcode.bakingapp.services.GetRecipeItemService;

import butterknife.BindView;

public class RecipeActivity extends AppCompatActivity {

    public static final String ACTION_RECIPE_QUERIED = "com.sbschoolcode.broadcast.RECIPE_QUERIED";
    @BindView(R.id.content_frame)
    private FrameLayout mContentFrame;
    private ServiceController mServiceController;
    private BroadcastReceiver mRecipeReceiver;
    private IntentFilter mRecipeIntentFilter;
    private Bundle mCurrentBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        initReceiver();
        mServiceController = ServiceController.getInstance();

        if (savedInstanceState == null) mServiceController.startQueryRecipeItem(this, getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentBundle != null) outState.putAll(mCurrentBundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentBundle = new Bundle();
        mCurrentBundle.putParcelable(AppConstants.BUNDLE_EXTRA_RECIPE,
                savedInstanceState.getParcelable(AppConstants.BUNDLE_EXTRA_RECIPE));
        mCurrentBundle.putParcelableArrayList(AppConstants.BUNDLE_EXTRA_INGREDIENTS_LIST,
                savedInstanceState.getParcelableArrayList(AppConstants.BUNDLE_EXTRA_INGREDIENTS_LIST));
        mCurrentBundle.putParcelableArrayList(AppConstants.BUNDLE_EXTRA_STEPS_LIST,
                savedInstanceState.getParcelableArrayList(AppConstants.BUNDLE_EXTRA_STEPS_LIST));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mRecipeReceiver, mRecipeIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mRecipeReceiver);
    }

    void addFragment(Fragment fragment, String tag) {
        Log.v(AppConstants.TESTING, "Fragment " + tag + " added");
        getSupportFragmentManager().beginTransaction().add(mContentFrame.getId(), fragment, tag).commit();
    }

    private void initReceiver() {
        mRecipeReceiver = new RecipeReceiver();
        mRecipeIntentFilter = new IntentFilter();
        mRecipeIntentFilter.addAction(ACTION_RECIPE_QUERIED);
    }

    private void onError() {
        Toast.makeText(RecipeActivity.this, R.string.error_loading_recipe, Toast.LENGTH_LONG).show();
        Log.v(AppConstants.TESTING, "onError()");
        finish();
    }

    private class RecipeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;

            if (intent.getAction().equals(ACTION_RECIPE_QUERIED)) {
                Recipe recipe = intent.getParcelableExtra(AppConstants.BUNDLE_EXTRA_RECIPE);
                if (recipe == null) onError();
                else {
                    Log.v(AppConstants.TESTING, "Recipe received, recipe name = " + recipe);
                    SelectAStep fragment = new SelectAStep();
                    fragment.setArguments(intent.getExtras());
                    addFragment(fragment, AppConstants.FRAGMENT_SELECT_A_STEP_TAG);
                }
            }
        }
    }
}
