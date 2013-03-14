package com.cookbook;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecipeViewActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipeview);
		
		final Intent ToRecipeActivity = new Intent(this, RecipeActivity.class);
		Button ViewRecipe = (Button) findViewById(R.id.startcooking);
		String RecipeName = getIntent().getStringExtra("com.cookbook.RecipeName");
		boolean IsExternalRecipe = getIntent().getBooleanExtra("com.cookbook.IsExternalRecipe", false);
		ToRecipeActivity.putExtra("com.cookbook.RecipeName", RecipeName);
		TextView RecipeTitle = (TextView) findViewById(R.id.recipetitle);
		TextView RecipeDescriptionHeader = (TextView) findViewById(R.id.recipedescriptionheader);
		TextView RecipeDescription = (TextView) findViewById(R.id.recipedescription);
		TextView RecipeTagList = (TextView) findViewById(R.id.recipetaglist);
		TextView RecipeIngredients = (TextView) findViewById(R.id.recipeingredients);
		
		// Update text fields with appropriate recipe data
		RecipeTitle.setText(RecipeName);
		if(!IsExternalRecipe)
		{
			AssetManager assetmanager = getAssets();
			// Change the recipe name to a path
            String Tmp = "Recipes/";
            StringBuilder Appender = new StringBuilder(Tmp);
            Appender.append(RecipeName);
            String FileName = Appender.toString();
			try {
				InputStream Input = assetmanager.open(FileName);
				RecipeTagList.setText(Recipe.scanRecipeForTags(Input));
				Input = assetmanager.open(FileName);
				RecipeDescription.setText(Recipe.scanRecipeForDescription(Input));
				Input = assetmanager.open(FileName);
				RecipeIngredients.setText(Recipe.scanRecipeForIngredients(Input));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		// Set text size
		float TextSize =  Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("font_size", "15"));
		RecipeTitle.setTextSize(TextSize);
		RecipeDescriptionHeader.setTextSize(TextSize);
		RecipeDescription.setTextSize(TextSize);
		RecipeTagList.setTextSize(TextSize);
		RecipeIngredients.setTextSize(TextSize);
		
		ViewRecipe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(ToRecipeActivity);
				finish();
			}
		});
		
	}
}
