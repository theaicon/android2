package com.cookbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CookbookActivity extends Activity {	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    Button SelectRecipe = (Button) findViewById(R.id.SelectRecipe);
    Button CookingTech = (Button) findViewById(R.id.CookingTechniques);
    Button CookingUtil = (Button) findViewById(R.id.CookingUtensils);
    Button WeightsMeasure = (Button) findViewById(R.id.WeightsMeasurements);
    Button Options = (Button) findViewById(R.id.Options);
    Button AboutUs = (Button) findViewById(R.id.AboutUs);
    
    final Intent ToRecipeSelection = new Intent(this, RecipeSelectionActivity.class);
    final Intent ToCookingTechniques = new Intent(this, CookingTechniques.class);
	final Intent ToCookingUtilities = new Intent(this, CookingUtilities.class);
	final Intent ToWeightsMeasure = new Intent(this, WeightsMeasurements.class);
	final Intent ToOptions = new Intent(this, Options.class);
	final Intent ToAboutUs = new Intent(this, AboutUs.class);
        
    SelectRecipe.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			/* Go to screen for recipe selection */
			startActivity(ToRecipeSelection);
		}
	});
    
    CookingTech.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			/* Go to cooking techniques screen */
			startActivity(ToCookingTechniques);
		}
	});

    CookingUtil.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			/* Go to Cooking Utilities screen */
			startActivity(ToCookingUtilities);
		}
	});
    
    WeightsMeasure.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			/* Go to Weights and Measurements guide */
			startActivity(ToWeightsMeasure);
		}
	});

    Options.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			/* Go to Options screen */
			startActivity(ToOptions);
		}
	});
    
    AboutUs.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			/* Go to About CookSarap! and the developers */
			startActivity(ToAboutUs);
		}
	});
        
    }
}