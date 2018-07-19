package com.sbschoolcode.bakingapp.ui.recipe.steps;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.models.Ingredient;
import com.sbschoolcode.bakingapp.models.Step;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class StepsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 111;
    private static final int VIEW_TYPE_STEP_ITEM = 222;
    private ArrayList<Step> mStepsList;
    private ArrayList<Ingredient> mIngredientsList;
    private View.OnClickListener mClickListener;

    StepsAdapter(View.OnClickListener onClickListener) {
        this.mClickListener = onClickListener;
    }

    class StepsViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public StepsViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.step_text_view);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        TextView quantity, measure, ingredient;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.steps_header_tv_container);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER)
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.steps_list_header, parent, false));
        else return new StepsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            holder.setIsRecyclable(false);
            for (Ingredient ingredient : mIngredientsList) {
                NumberFormat format = new DecimalFormat("0.#");
                LinearLayout pairLayout = (LinearLayout) LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.steps_header_ingredient_pair,
                        null);
                ((HeaderViewHolder) holder).quantity = pairLayout.findViewById(R.id.ingredient_quantity);
                ((HeaderViewHolder) holder).measure = pairLayout.findViewById(R.id.ingredient_measure);
                ((HeaderViewHolder) holder).ingredient = pairLayout.findViewById(R.id.ingredient_name);
                ((HeaderViewHolder) holder).quantity.setText(format.format(ingredient.quantity));
                ((HeaderViewHolder) holder).measure.setText(ingredient.measure);
                ((HeaderViewHolder) holder).ingredient.setText(AppUtils.normalizeWords(ingredient.ingredient));
                ((HeaderViewHolder) holder).container.addView(pairLayout);
            }
        } else {
            int arrayPosition = position - 1;
            ((StepsViewHolder) holder).textView.setText(mStepsList.get(arrayPosition).shortDescription);
            holder.itemView.setTag(mStepsList.get(arrayPosition).id);
            holder.itemView.setOnClickListener(mClickListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_HEADER;
        else return VIEW_TYPE_STEP_ITEM;
    }

    @Override
    public int getItemCount() {
        if (null == mStepsList || null == mIngredientsList) return 0;
        return mStepsList.size() + 1;
    }

    public void swapArrays(ArrayList<Step> steps, ArrayList<Ingredient> ingredients) {
        this.mStepsList = steps;
        this.mIngredientsList = ingredients;
        notifyDataSetChanged();
    }
}
