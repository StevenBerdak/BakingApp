package com.sbschoolcode.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class RecipeProvider extends ContentProvider {

    private RecipeDbHelper mRecipeDbHelper;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.RecipesEntry.TABLE_NAME, 1);
        mUriMatcher.addURI(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.RecipesEntry.TABLE_NAME + "/#", 2);
        mUriMatcher.addURI(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.IngredientsEntry.TABLE_NAME, 3);
        mUriMatcher.addURI(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.IngredientsEntry.TABLE_NAME + "/#", 4);
        mUriMatcher.addURI(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.StepsEntry.TABLE_NAME, 5);
        mUriMatcher.addURI(DbContract.CONTENT_PROVIDER_AUTHORITY, DbContract.StepsEntry.TABLE_NAME + "/#", 6);
    }

    @Override
    public boolean onCreate() {
        mRecipeDbHelper = new RecipeDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mRecipeDbHelper.getReadableDatabase();
        switch (mUriMatcher.match(uri)) {
            case 1:
                sortOrder = DbContract.RecipesEntry.COLUMN_API_ID + " ASC";
                break;
            case 2:
                selection = DbContract.RecipesEntry.COLUMN_API_ID + "= ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                uri = Uri.parse(DbContract.RecipesEntry.TABLE_NAME);
                break;
            case 3:
                sortOrder = DbContract.IngredientsEntry.COLUMN_PARENT_API_ID + " ASC";
                break;
            case 4:
                selection = DbContract.IngredientsEntry.COLUMN_PARENT_API_ID + "= ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                uri = Uri.parse(DbContract.IngredientsEntry.TABLE_NAME);
                sortOrder = DbContract.IngredientsEntry.COLUMN_INGREDIENT + " ASC";
                break;
            case 5:
                sortOrder = DbContract.StepsEntry.COLUMN_PARENT_API_ID + " ASC";
                break;
            case 6:
                selection = DbContract.StepsEntry.COLUMN_PARENT_API_ID + "= ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                uri = Uri.parse(DbContract.StepsEntry.TABLE_NAME);
                sortOrder = DbContract.StepsEntry.COLUMN_STEP_ID + " ASC";
                break;
        }

        return db.query(uri.getLastPathSegment(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            long rowId = db.insertOrThrow(uri.getLastPathSegment(), null, values);
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);

            return uri.buildUpon().appendPath(Long.toString(rowId)).build();
        } catch (SQLiteConstraintException e) {
            Log.v(getClass().getSimpleName(), "Unique constraint failed, row not inserted, already in database");
            db.endTransaction();
            db.close();
            return null;
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArr) {
        SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int rows = 0;
            for (ContentValues values : valuesArr) {
                db.insertOrThrow(uri.getLastPathSegment(), null, values);
                rows++;
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
            return rows;
        } catch (SQLiteConstraintException e) {
            Log.v(getClass().getSimpleName(), "Unique constraint failed, rows not inserted, already in database");
            db.endTransaction();
            db.close();
            return 0;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
