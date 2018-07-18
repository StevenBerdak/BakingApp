package com.sbschoolcode.bakingapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbschoolcode.bakingapp.data.DbContract;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> implements View.OnClickListener {

    private Cursor mCursor;

    @Override
    public void onClick(View v) {
        Log.v("TESTING", "Item clicked = " + v.getTag());
        Intent recipeActivity = new Intent(v.getContext(),
                RecipeActivity.class).putExtra(AppConstants.INTENT_EXTRA_RECIPE_API_INDEX,
                (int) v.getTag());
        v.getContext().startActivity(recipeActivity);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;

        ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.recipe_list_item_name_tv);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int apiIdIndex = mCursor.getColumnIndex(DbContract.RecipesEntry.COLUMN_API_ID);
        holder.itemView.setTag(mCursor.getInt(apiIdIndex));
        int nameIndex = mCursor.getColumnIndex(DbContract.RecipesEntry.COLUMN_RECIPE_NAME);
        holder.nameTextView.setText(mCursor.getString(nameIndex));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) {
            return 0;
        }

        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }
}
