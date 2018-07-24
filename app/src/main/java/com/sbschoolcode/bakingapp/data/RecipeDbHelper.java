package com.sbschoolcode.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sbschoolcode.bakingapp.data.DbContract.IngredientsEntry;
import com.sbschoolcode.bakingapp.data.DbContract.RecipesEntry;
import com.sbschoolcode.bakingapp.data.DbContract.StepsEntry;

class RecipeDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipes_database";
    private static final int DATABASE_VERSION = 18;

    private static final String CREATE_RECIPES_TABLE =
            "CREATE TABLE " + RecipesEntry.TABLE_NAME + " (" +
                    RecipesEntry._ID + " INTEGER PRIMARY KEY, " +
                    RecipesEntry.COLUMN_API_ID + " INTEGER UNIQUE, " +
                    RecipesEntry.COLUMN_RECIPE_NAME + " TEXT, " +
                    RecipesEntry.COLUMN_SERVINGS + " INTEGER, " +
                    RecipesEntry.COLUMN_IMAGE_URL + " TEXT)";

    private static final String CREATE_INGREDIENTS_TABLE =
            "CREATE TABLE " + IngredientsEntry.TABLE_NAME + " (" +
                    IngredientsEntry._ID + " INTEGER PRIMARY KEY, " +
                    IngredientsEntry.COLUMN_PARENT_API_ID + " INTEGER, " +
                    IngredientsEntry.COLUMN_QUANTITY + " REAL, " +
                    IngredientsEntry.COLUMN_MEASURE + " TEXT, " +
                    IngredientsEntry.COLUMN_INGREDIENT + " TEXT)";

    private static final String CREATE_STEPS_TABLE =
            "CREATE TABLE " + StepsEntry.TABLE_NAME + " (" +
                    StepsEntry._ID + " INTEGER PRIMARY KEY, " +
                    StepsEntry.COLUMN_PARENT_API_ID + " INTEGER, " +
                    StepsEntry.COLUMN_STEP_ID + " INTEGER, " +
                    StepsEntry.COLUMN_SHORT_DESC + " TEXT, " +
                    StepsEntry.COLUMN_DESCRIPTION + " TEXT, " +
                    StepsEntry.COLUMN_VIDEO_URL + " TEXT, " +
                    StepsEntry.COLUMN_THUMBNAIL_URL + " TEXT)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

    RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_INGREDIENTS_TABLE);
        db.execSQL(CREATE_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + " " + RecipesEntry.TABLE_NAME);
        db.execSQL(DROP_TABLE + " " + IngredientsEntry.TABLE_NAME);
        db.execSQL(DROP_TABLE + " " + StepsEntry.TABLE_NAME);
        onCreate(db);
    }
}
