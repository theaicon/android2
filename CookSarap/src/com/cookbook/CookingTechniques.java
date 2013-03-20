package com.cookbook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CookingTechniques extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cookingtech);
        
        ListView listView = (ListView) findViewById(R.id.mylist);
        String[] values = new String[] { "Baking - the cooking of bread, cakes, and other foods by dry heat in an oven.",
          "Boiling - cooking foods in boiling water", "Braising - is a combination cooking method using both moist and dry heat.",
          "Brining - is a process similar to marination in which meat or poultry is soaked in brine before cooking.",
          "Browning - is the process of partially cooking the surface of meat to help remove excessive fat and to give the meat a brown color crust and flavor through various browning reactions.",
          "Caramelization -  is the browning of sugar, a process used extensively in cooking for the resulting nutty flavor and brown color.",
          "Frying - is the cooking of food in oil or another fat.", 
          "Grilling - is a form of cooking that involves dry heat applied to the surface of food, commonly from above or below.",
          "Marination - is the process of soaking foods in a seasoned, often acidic, liquid before cooking.",
          "Poaching - is the process of gently simmering food in liquid, generally milk, stock or wine.",
          "Roasting - is a cooking method that uses dry heat, whether an open flame, oven, or other heat source.",
          "Sauteing -  is a method of cooking food, that uses a small amount of oil or fat in a shallow pan over relatively high heat.",
          "Seasoning - is process of imparting flavor to, or improving the flavor of, food.",
          "Simmering - is a food preparation technique in which foods are cooked in hot liquids kept at or just below the boiling point of water.",
          "Smoking - is the process of flavoring, cooking, or preserving food by exposing it to the smoke from burning or smoldering plant materials, most often wood.",
          "Steaming - is a method of cooking using steam.", 
          "Stewing - A long, slow method of cooking where food is cut into pieces and cooked in the minimum amount of liquid, water, stock or sauce."};

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter); 
    }
}