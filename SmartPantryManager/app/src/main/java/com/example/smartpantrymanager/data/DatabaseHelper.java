package com.example.smartpantrymanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_PANTRY_ID = "_id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QTY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "_id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_STEPS = "steps";

    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "_id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QTY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QTY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT, " +
                COL_PANTRY_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_STEPS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_NAME + " TEXT NOT NULL, " +
                COL_RI_QTY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " +
                TABLE_RECIPES + "(" + COL_RECIPE_ID + "))");

        seedRecipes(db);
        seedSamplePantry(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ---------------- PANTRY CRUD ----------------

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_PANTRY, null, pantryToContentValues(item));
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_PANTRY, pantryToContentValues(item), COL_PANTRY_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }

    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_PANTRY_ID + "=?", new String[]{String.valueOf(id)});
    }

    public PantryItem getPantryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PANTRY, null, COL_PANTRY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (c.moveToFirst()) item = cursorToPantryItem(c);
        c.close();
        return item;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PANTRY, null, null, null, null, null, COL_PANTRY_NAME + " ASC");
        while (c.moveToNext()) list.add(cursorToPantryItem(c));
        c.close();
        return list;
    }

    private ContentValues pantryToContentValues(PantryItem item) {
        ContentValues cv = new ContentValues();
        cv.put(COL_PANTRY_NAME, item.getName());
        cv.put(COL_PANTRY_QTY, item.getQuantity());
        cv.put(COL_PANTRY_UNIT, item.getUnit());
        cv.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        return cv;
    }

    private PantryItem cursorToPantryItem(Cursor c) {
        PantryItem item = new PantryItem();
        item.setId(c.getLong(c.getColumnIndexOrThrow(COL_PANTRY_ID)));
        item.setName(c.getString(c.getColumnIndexOrThrow(COL_PANTRY_NAME)));
        item.setQuantity(c.getDouble(c.getColumnIndexOrThrow(COL_PANTRY_QTY)));
        item.setUnit(c.getString(c.getColumnIndexOrThrow(COL_PANTRY_UNIT)));
        item.setExpiryDate(c.getString(c.getColumnIndexOrThrow(COL_PANTRY_EXPIRY)));
        return item;
    }

    // ---------------- RECIPE READ ----------------

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPES, null, null, null, null, null, COL_RECIPE_NAME + " ASC");
        while (c.moveToNext()) {
            Recipe r = new Recipe(
                    c.getLong(c.getColumnIndexOrThrow(COL_RECIPE_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_RECIPE_STEPS)));
            r.setIngredients(getIngredientsForRecipe(db, r.getId()));
            recipes.add(r);
        }
        c.close();
        return recipes;
    }

    public Recipe getRecipeById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPES, null, COL_RECIPE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Recipe r = null;
        if (c.moveToFirst()) {
            r = new Recipe(
                    c.getLong(c.getColumnIndexOrThrow(COL_RECIPE_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_RECIPE_STEPS)));
            r.setIngredients(getIngredientsForRecipe(db, r.getId()));
        }
        c.close();
        return r;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        Cursor c = db.query(TABLE_RECIPE_INGREDIENTS, null, COL_RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        while (c.moveToNext()) {
            ingredients.add(new RecipeIngredient(
                    c.getLong(c.getColumnIndexOrThrow(COL_RI_ID)),
                    c.getLong(c.getColumnIndexOrThrow(COL_RI_RECIPE_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_RI_NAME)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_RI_QTY)),
                    c.getString(c.getColumnIndexOrThrow(COL_RI_UNIT))));
        }
        c.close();
        return ingredients;
    }

    // ---------------- SEEDING ----------------

    private void seedRecipes(SQLiteDatabase db) {
        Object[][] recipeData = {
            {"Tomato Omelette", "1. Beat eggs in a bowl.\n2. Chop tomato and onion.\n3. Mix into eggs with salt.\n4. Pour into an oiled pan and cook until set.",
                new Object[][]{{"egg", 3.0, "pcs"}, {"tomato", 2.0, "pcs"}, {"onion", 1.0, "pcs"}, {"salt", 1.0, "g"}, {"oil", 10.0, "ml"}}},
            {"Vegetable Fried Rice", "1. Cook rice and set aside.\n2. Saute carrot and peas in oil.\n3. Push aside, scramble egg.\n4. Mix rice, vegetables and egg with soy sauce.",
                new Object[][]{{"rice", 300.0, "g"}, {"carrot", 1.0, "pcs"}, {"peas", 50.0, "g"}, {"egg", 2.0, "pcs"}, {"soy sauce", 15.0, "ml"}, {"oil", 10.0, "ml"}}},
            {"Grilled Cheese Sandwich", "1. Butter the outside of two bread slices.\n2. Place cheese between slices.\n3. Grill until golden on both sides.",
                new Object[][]{{"bread", 2.0, "pcs"}, {"cheese", 2.0, "slice"}, {"butter", 10.0, "g"}}},
            {"Chicken Stir Fry", "1. Slice chicken and vegetables.\n2. Stir fry garlic and onion in oil.\n3. Add chicken, cook through.\n4. Add bell pepper and soy sauce, cook 2 more minutes.",
                new Object[][]{{"chicken", 300.0, "g"}, {"bell pepper", 1.0, "pcs"}, {"onion", 1.0, "pcs"}, {"soy sauce", 15.0, "ml"}, {"garlic", 2.0, "clove"}, {"oil", 10.0, "ml"}}},
            {"Pasta Aglio e Olio", "1. Boil pasta until al dente.\n2. Gently fry sliced garlic in olive oil with chili flakes.\n3. Toss pasta through oil, garnish with parsley.",
                new Object[][]{{"pasta", 200.0, "g"}, {"garlic", 3.0, "clove"}, {"olive oil", 30.0, "ml"}, {"chili flakes", 1.0, "g"}, {"parsley", 5.0, "g"}}},
            {"Banana Pancakes", "1. Mash banana.\n2. Mix with flour, egg, milk and sugar into a batter.\n3. Cook on a hot griddle until bubbles form, flip and finish cooking.",
                new Object[][]{{"flour", 150.0, "g"}, {"banana", 2.0, "pcs"}, {"egg", 1.0, "pcs"}, {"milk", 100.0, "ml"}, {"sugar", 10.0, "g"}}},
            {"Veggie Soup", "1. Chop all vegetables.\n2. Simmer in vegetable stock for 20 minutes.\n3. Season to taste and serve.",
                new Object[][]{{"carrot", 1.0, "pcs"}, {"potato", 1.0, "pcs"}, {"onion", 1.0, "pcs"}, {"celery", 1.0, "pcs"}, {"vegetable stock", 500.0, "ml"}}},
            {"Tuna Salad", "1. Flake tuna into a bowl.\n2. Add chopped lettuce and tomato.\n3. Stir through mayonnaise and serve.",
                new Object[][]{{"tuna", 150.0, "g"}, {"lettuce", 50.0, "g"}, {"tomato", 1.0, "pcs"}, {"mayonnaise", 20.0, "g"}}},
            {"Potato Hash", "1. Dice and parboil potato.\n2. Fry with chopped onion in oil until golden.\n3. Crack egg on top, cover and cook until set. Season with salt.",
                new Object[][]{{"potato", 300.0, "g"}, {"onion", 1.0, "pcs"}, {"egg", 1.0, "pcs"}, {"oil", 10.0, "ml"}, {"salt", 1.0, "g"}}},
            {"Egg Fried Rice", "1. Scramble egg in a hot pan with oil.\n2. Add cooked rice and spring onion.\n3. Season with soy sauce and stir fry until hot.",
                new Object[][]{{"rice", 300.0, "g"}, {"egg", 2.0, "pcs"}, {"spring onion", 20.0, "g"}, {"soy sauce", 15.0, "ml"}, {"oil", 10.0, "ml"}}},
            {"Beef Tacos", "1. Brown beef mince in a pan.\n2. Warm taco shells.\n3. Fill shells with beef, lettuce, cheese and tomato.",
                new Object[][]{{"beef mince", 250.0, "g"}, {"taco shell", 4.0, "pcs"}, {"lettuce", 50.0, "g"}, {"cheese", 50.0, "g"}, {"tomato", 1.0, "pcs"}}},
            {"Margherita Toast", "1. Toast bread slices.\n2. Top with sliced tomato and cheese.\n3. Grill until cheese melts, garnish with basil.",
                new Object[][]{{"bread", 2.0, "pcs"}, {"tomato", 1.0, "pcs"}, {"cheese", 2.0, "slice"}, {"basil", 2.0, "g"}}},
            {"Chicken Soup", "1. Simmer chicken in stock with chopped vegetables for 30 minutes.\n2. Season to taste and serve hot.",
                new Object[][]{{"chicken", 200.0, "g"}, {"carrot", 1.0, "pcs"}, {"celery", 1.0, "pcs"}, {"onion", 1.0, "pcs"}, {"chicken stock", 500.0, "ml"}}},
            {"Fruit Smoothie", "1. Add banana, milk, yogurt and honey to a blender.\n2. Blend until smooth and serve chilled.",
                new Object[][]{{"banana", 1.0, "pcs"}, {"milk", 200.0, "ml"}, {"yogurt", 50.0, "g"}, {"honey", 10.0, "g"}}},
            {"Lentil Curry", "1. Saute onion and garlic in oil.\n2. Add curry powder, tomato and lentils with water.\n3. Simmer until lentils are soft.",
                new Object[][]{{"lentils", 200.0, "g"}, {"onion", 1.0, "pcs"}, {"tomato", 2.0, "pcs"}, {"garlic", 2.0, "clove"}, {"curry powder", 5.0, "g"}, {"oil", 10.0, "ml"}}},
            {"Mushroom Risotto", "1. Saute onion and mushroom in butter.\n2. Add rice, stir, then gradually add stock until creamy and cooked.",
                new Object[][]{{"rice", 200.0, "g"}, {"mushroom", 150.0, "g"}, {"onion", 1.0, "pcs"}, {"vegetable stock", 500.0, "ml"}, {"butter", 20.0, "g"}}},
            {"Spaghetti Bolognese", "1. Brown beef mince with onion and garlic.\n2. Add chopped tomato, simmer into a sauce.\n3. Serve over cooked pasta.",
                new Object[][]{{"pasta", 200.0, "g"}, {"beef mince", 250.0, "g"}, {"tomato", 3.0, "pcs"}, {"onion", 1.0, "pcs"}, {"garlic", 2.0, "clove"}}},
            {"Pancake Stack", "1. Whisk flour, egg, milk and sugar into a smooth batter.\n2. Fry spoonfuls in buttered pan until golden on both sides.",
                new Object[][]{{"flour", 200.0, "g"}, {"egg", 2.0, "pcs"}, {"milk", 250.0, "ml"}, {"sugar", 20.0, "g"}, {"butter", 10.0, "g"}}},
            {"Cheese Omelette", "1. Beat eggs with salt.\n2. Pour into a buttered pan.\n3. Sprinkle cheese, fold and serve.",
                new Object[][]{{"egg", 3.0, "pcs"}, {"cheese", 50.0, "g"}, {"salt", 1.0, "g"}, {"butter", 10.0, "g"}}},
            {"Vegetable Stir Fry", "1. Heat oil and garlic in a wok.\n2. Add broccoli, carrot and bell pepper.\n3. Stir fry until tender-crisp, season with soy sauce.",
                new Object[][]{{"broccoli", 100.0, "g"}, {"carrot", 1.0, "pcs"}, {"bell pepper", 1.0, "pcs"}, {"soy sauce", 15.0, "ml"}, {"garlic", 2.0, "clove"}, {"oil", 10.0, "ml"}}}
        };

        for (Object[] recipe : recipeData) {
            String name = (String) recipe[0];
            String steps = (String) recipe[1];
            Object[][] ingredients = (Object[][]) recipe[2];

            ContentValues recipeCv = new ContentValues();
            recipeCv.put(COL_RECIPE_NAME, name);
            recipeCv.put(COL_RECIPE_STEPS, steps);
            long recipeId = db.insert(TABLE_RECIPES, null, recipeCv);

            for (Object[] ing : ingredients) {
                ContentValues ingCv = new ContentValues();
                ingCv.put(COL_RI_RECIPE_ID, recipeId);
                ingCv.put(COL_RI_NAME, (String) ing[0]);
                ingCv.put(COL_RI_QTY, (Double) ing[1]);
                ingCv.put(COL_RI_UNIT, (String) ing[2]);
                db.insert(TABLE_RECIPE_INGREDIENTS, null, ingCv);
            }
        }
    }

    private void seedSamplePantry(SQLiteDatabase db) {
        Object[][] sample = {
            {"Eggs", 6.0, "pcs", null},
            {"Tomatoes", 4.0, "pcs", null},
            {"Onion", 2.0, "pcs", null},
            {"Milk", 500.0, "ml", null},
            {"Bread", 4.0, "pcs", null}
        };
        for (Object[] s : sample) {
            ContentValues cv = new ContentValues();
            cv.put(COL_PANTRY_NAME, (String) s[0]);
            cv.put(COL_PANTRY_QTY, (Double) s[1]);
            cv.put(COL_PANTRY_UNIT, (String) s[2]);
            cv.put(COL_PANTRY_EXPIRY, (String) s[3]);
            db.insert(TABLE_PANTRY, null, cv);
        }
    }
}
