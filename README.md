# Smart Pantry Manager

A Java Android application that helps users reduce food waste by tracking
pantry ingredients and suggesting recipes that can be made strictly from
what is already on hand — no shopping trip required.

## Features
- Add, edit and delete pantry ingredients (name, quantity, unit, optional expiry)
- Pantry list screen backed by RecyclerView + SQLite
- 20 seeded recipes with full ingredient lists and preparation steps
- Suggested Recipes screen using a strict-matching algorithm: a recipe
  only appears if EVERY required ingredient is present in sufficient quantity
- Bonus "Almost There" list (recipes missing exactly one ingredient),
  clearly separated from the strict list
- Recipe detail screen with full ingredient list and method
- Settings screen (expiry alerts toggle, preferred unit system)
- Input validation on the Add/Edit Ingredient form
- Bottom navigation between Pantry, Suggestions and Settings
- No Google Maps / location / GPS code anywhere in this project

## Database Choice: SQLite
Implemented via `SQLiteOpenHelper` (see `data/DatabaseHelper.java`), fully
on-device. Chosen because it requires no backend/network setup, keeps the
app entirely offline, and is well suited to the relational structure of
pantry items, recipes, and recipe ingredients.

## Strict-Matching Logic
`util/IngredientNameUtils.java` normalises ingredient names (lowercase,
trims, naive singularisation) so "Tomatoes" matches "tomato".
`util/UnitConverter.java` converts common units (g/kg, ml/l, tsp/tbsp/cup,
pcs/clove/slice) to a shared base so quantities can be fairly compared.
`util/RecipeMatcher.java` only returns a recipe as "suggested" when every
single ingredient is found in the pantry with enough quantity — a single
missing ingredient excludes it from the main list (optionally shown under
"Almost There").

## Setup / Run Instructions
1. Clone: `git clone <your-repo-url>`
2. Open in Android Studio (Giraffe+), let Gradle sync.
3. Run on an emulator or device (minSdk 21+).
4. Database auto-creates and seeds on first launch — no manual setup needed.

## Out of Scope
No Google Maps, mapping SDK, or location/GPS services are used, per the
assignment brief.
