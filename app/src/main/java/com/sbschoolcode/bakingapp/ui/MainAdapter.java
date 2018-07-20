package com.sbschoolcode.bakingapp.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbschoolcode.bakingapp.AppUtils;
import com.sbschoolcode.bakingapp.R;
import com.sbschoolcode.bakingapp.data.DbContract;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private Cursor mCursor;
    private final View.OnClickListener mClickListener;

    class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView recipeImageView;
        final TextView nameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.recipe_list_item_image);
            nameTextView = itemView.findViewById(R.id.recipe_list_item_name_tv);
        }
    }

    MainAdapter(View.OnClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);
        view.setOnClickListener(mClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.setIsRecyclable(false);
        int apiIdIndex = mCursor.getColumnIndex(DbContract.RecipesEntry.COLUMN_API_ID);
        holder.itemView.setTag(mCursor.getInt(apiIdIndex));
        int nameIndex = mCursor.getColumnIndex(DbContract.RecipesEntry.COLUMN_RECIPE_NAME);
        holder.nameTextView.setText(mCursor.getString(nameIndex));
        AppUtils.setImage(holder.recipeImageView, AppUtils.getRecipeDrawable((int) holder.itemView.getTag()));
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
