\# Smart Pantry Manager\
\
Smart Pantry Manager is a Java-based Android application designed to help users minimise food waste by managing their available ingredients and recommending recipes based on what they already have at home. The application focuses on convenience and efficient food management by allowing users to prepare meals without purchasing additional ingredients.\
\
\## Key Features\
\
\- \*\*Ingredient Management:\*\* Users can add, update, and remove pantry items, including their names, quantities, measurement units, and optional expiry dates.\
\- \*\*Pantry Overview:\*\* Displays stored ingredients using RecyclerView, with SQLite providing local data storage.\
\- \*\*Recipe Collection:\*\* Includes 20 preloaded recipes, each containing a complete ingredient list and step-by-step preparation instructions.\
\- \*\*Recipe Recommendations:\*\* Uses a strict ingredient-matching system that recommends recipes only when all required ingredients are available in sufficient quantities.\
\- \*\*Almost There Recommendations:\*\* Provides a separate list of recipes that require only one additional ingredient.\
\- \*\*Recipe Details:\*\* Allows users to view the ingredients and cooking instructions for individual recipes.\
\- \*\*Application Settings:\*\* Includes options for enabling expiry notifications and selecting a preferred measurement system.\
\- \*\*Input Validation:\*\* Ensures that ingredient information entered by users is accurate and follows the required format.\
\- \*\*Bottom Navigation:\*\* Provides convenient access to the Pantry, Suggested Recipes, and Settings sections.\
\- \*\*Location Independence:\*\* The application does not include Google Maps, GPS tracking, or location-based functionality.\
\
\## Database Implementation: SQLite\
\
The application uses SQLite as its primary database, implemented through the \`SQLiteOpenHelper\` class in \`data/DatabaseHelper.java\`.\
\
SQLite was selected because it provides reliable local storage without requiring an internet connection, external server, or additional backend configuration. Its relational database structure also makes it suitable for organising pantry ingredients, recipes, and their associated ingredient requirements.\
\
All application data is stored directly on the user's device, allowing the system to operate entirely offline.\
\
\## Recipe Matching Algorithm\
\
The recipe recommendation system uses three utility classes to ensure accurate ingredient comparisons:\
\
\- \*\*IngredientNameUtils.java:\*\* Standardises ingredient names by converting text to lowercase, removing unnecessary spaces, and applying basic singularisation. This allows variations such as "Tomatoes" and "tomato" to be recognised as the same ingredient.\
\- \*\*UnitConverter.java:\*\* Converts supported measurement units, including grams, kilograms, millilitres, litres, teaspoons, tablespoons, cups, and item-based units, into comparable values.\
\- \*\*RecipeMatcher.java:\*\* Evaluates the ingredients required for each recipe against the user's pantry inventory. A recipe is recommended only when every required ingredient is available in the correct quantity.\
\
Recipes that do not meet all ingredient requirements are excluded from the main recommendations. However, recipes missing exactly one ingredient may appear in the separate \*\*Almost There\*\* section.\
\
This approach ensures that the main recipe suggestions are practical and can be prepared using the ingredients currently available.\
\
\## Installation and Execution\
\
1\. Clone the project repository using \`git clone \<your-repo-url>\`.\
2\. Open the project in Android Studio (Giraffe or a newer compatible version).\
3\. Allow Gradle to synchronise and download the required dependencies.\
4\. Launch the application using an Android emulator or physical device running Android 5.0 (API level 21) or higher.\
5\. On the first launch, the SQLite database is automatically created and populated with the predefined recipes, eliminating the need for manual database configuration.\
\
\## Project Scope and Limitations\
\
Smart Pantry Manager focuses specifically on ingredient inventory management and recipe recommendations.\
\
The application does not incorporate Google Maps, mapping libraries, GPS tracking, or any location-related services, in accordance with the assignment requirements.\
\
Its offline functionality ensures that users can manage their pantry and access recipe recommendations without depending on internet connectivity.\


