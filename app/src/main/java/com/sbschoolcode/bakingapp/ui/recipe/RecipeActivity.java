package com.sbschoolcode.bakingapp.ui.recipe;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sbschoolcode.bakingapp.AppConstants;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.controllers.ExoController;
import com.sbschoolcode.bakingapp.controllers.ServiceController;
import com.sbschoolcode.bakingapp.models.Recipe;
import com.sbschoolcode.bakingapp.ui.recipe.steps.StepsListFrag;
import com.sbschoolcode.bakingapp.widget.BakingWidget;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity {

    public static final String ACTION_RECIPE_QUERIED = "com.sbschoolcode.broadcast.RECIPE_QUERIED";
    @BindView(R.id.content_frame)
    FrameLayout mContentFrame;
    private BroadcastReceiver mRecipeReceiver;
    private IntentFilter mRecipeIntentFilter;
    private Bundle mCurrentBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);
        initReceiver();

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            int index = getIntent().getIntExtra(AppConstants.INTENT_EXTRA_RECIPE_API_INDEX, -1);
            ServiceController.getInstance().startBuildRecipeItem(this, index);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mRecipeReceiver);
        ExoController.getInstance().releaseExoPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mRecipeReceiver, mRecipeIntentFilter);
        ExoController.getInstance().prepareExoPlayer(this);
    }

    /**
     * Load the steps list.
     */
    private void loadRecipeList() {
        Recipe recipe = mCurrentBundle.getParcelable(AppConstants.INTENT_EXTRA_RECIPE);
        if (recipe != null) {
            setTitle(recipe.name);
            updateWidget();
            PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .edit()
                    .putInt(AppConstants.PREF_RECIPE_API_ID, recipe.apiId)
                    .putInt(AppConstants.PREF_RECIPE_WIDGET_ID, recipe.apiId)
                    .putString(AppConstants.PREF_RECIPE_LOADED_NAME, recipe.name)
                    .apply();
        }

        StepsListFrag fragment = new StepsListFrag();
        fragment.setArguments(mCurrentBundle);
        getSupportFragmentManager().beginTransaction()
                .add(mContentFrame.getId(), fragment, AppConstants.FRAGMENT_SELECT_A_STEP_TAG).commit();
    }

    /**
     * Update the data displayed in the widget.
     */
    private void updateWidget() {
        Intent updateWidgets = new Intent(this, BakingWidget.class);
        updateWidgets.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] widgetIds = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), BakingWidget.class));
        updateWidgets.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        sendBroadcast(updateWidgets);
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    /**
     * Initialize the inner broadcast receiver.
     */
    private void initReceiver() {
        mRecipeReceiver = new RecipeReceiver();
        mRecipeIntentFilter = new IntentFilter();
        mRecipeIntentFilter.addAction(ACTION_RECIPE_QUERIED);
    }

    private class RecipeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;

            if (intent.getAction().equals(ACTION_RECIPE_QUERIED)) {
                Recipe recipe = intent.getParcelableExtra(AppConstants.INTENT_EXTRA_RECIPE);
                if (recipe == null) {
                    Toast.makeText(RecipeActivity.this, R.string.error_loading_recipe, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    mCurrentBundle = intent.getExtras();
                    loadRecipeList();
                }
            }
        }
    }
}
