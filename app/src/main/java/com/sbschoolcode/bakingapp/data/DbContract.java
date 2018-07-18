package com.sbschoolcode.bakingapp.data;

import android.provider.BaseColumns;

public final class DbContract {

    public static final String CONTENT_PROVIDER_AUTHORITY = "com.sbschoolcode.bakingapp.data";

    public static class RecipesEntry implements BaseColumns {
        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_API_ID = "api_id";
        public static final String COLUMN_RECIPE_NAME = "recipe_name";
        public static final String COLUMN_SERVINGS = "servings";
    }

    public static class IngredientsEntry implements BaseColumns {
        public static final String TABLE_NAME = "ingredients";
        public static final String COLUMN_PARENT_API_ID = "parent_id";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";
    }

    public static class StepsEntry implements BaseColumns {
        public static final String TABLE_NAME = "steps";
        public static final String COLUMN_PARENT_API_ID = "parent_id";
        public static final String COLUMN_STEP_ID = "id";
        public static final String COLUMN_SHORT_DESC = "short_desc";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VIDEO_URL = "video_url";
    }
}
