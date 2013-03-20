package com.cookbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecipeActivity extends FragmentActivity {
	private Recipe CurrentRecipe;
	private ViewPager mViewPager;
	private OnSharedPreferenceChangeListener listener;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);
        
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        final Intent ToFinishActivity = new Intent(this, FinishActivity.class);
        
        String RecipeName = getIntent().getStringExtra("com.cookbook.RecipeName");
        ToFinishActivity.putExtra("com.cookbook.RecipeName", RecipeName);
        final TextView RecipeNameDisplay = (TextView) findViewById(R.id.recipename);
        RecipeNameDisplay.setText(RecipeName);
        SharedPreferences Preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        RecipeNameDisplay.setTextSize(Float.parseFloat(Preferences.getString("font_size", "15")));
        
        // Updates the textviews when the font size changes
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				RecipeNameDisplay.setTextSize(Float.parseFloat(sharedPreferences.getString("font_size", "15")));	
			}
        };
        Preferences.registerOnSharedPreferenceChangeListener(listener);
        
        // Initialize buttons
        final Button NextStep = (Button) findViewById(R.id.nextstep);
        final Button PreviousStep = (Button) findViewById(R.id.previousstep);
        final Button Finish = (Button) findViewById(R.id.finish);
        Finish.setVisibility(View.GONE); // Initially, the finish button should be invisible
        PreviousStep.setVisibility(View.GONE); // Initially, the previous step button should be invisible
        NextStep.setVisibility(View.VISIBLE);
        
        /* Check if the recipe in use is a downloaded recipe */
        boolean IsExternalRecipe = false;
        String [] ExternalRecipeList = fileList();
        int i;
        for(i=0;i<ExternalRecipeList.length; i++)
        {
        	if(RecipeName == ExternalRecipeList[i] && IsExternalRecipe == false)
        		IsExternalRecipe = true;
        }
        AssetManager Assetmanager = getAssets();
        
        // Change the recipe name to a path
        String Tmp = "Recipes/";
        StringBuilder Appender = new StringBuilder(Tmp);
        Appender.append(RecipeName);
        RecipeName = Appender.toString();
        
        if(IsExternalRecipe == false)
        {
        	try {
				InputStream Input = Assetmanager.open(RecipeName);
				CurrentRecipe = new Recipe(Input, RecipeName);
				final SwipeViewsAdapter mAdapter = new SwipeViewsAdapter(getSupportFragmentManager(), CurrentRecipe);
				mViewPager.setAdapter(mAdapter);
				
				// Restore the step displayed last, if user is returning to the activity
				if(savedInstanceState != null)
				{
					int PreviousPosition = savedInstanceState.getInt("position");
					mViewPager.setCurrentItem(PreviousPosition);
		        	CurrentRecipe.setStepNumber(PreviousPosition-1);
		        	// Set the button visibility
		        	if(CurrentRecipe.getStepNumber() == -1)
		        	{
						PreviousStep.setVisibility(View.GONE);
						NextStep.setVisibility(View.VISIBLE);
						Finish.setVisibility(View.GONE);
		        	}
		        	else if(CurrentRecipe.getStepNumber() == CurrentRecipe.NumberOfSteps-1)
		        	{
						NextStep.setVisibility(View.GONE);
						PreviousStep.setVisibility(View.VISIBLE);
						Finish.setVisibility(View.VISIBLE);
		        	}
		        	else
		        	{
		        		NextStep.setVisibility(View.VISIBLE);
						PreviousStep.setVisibility(View.VISIBLE);
						Finish.setVisibility(View.GONE);
		        	}
				}
				
				NextStep.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// When this button is pressed, go onto the next step
						mViewPager.setCurrentItem(CurrentRecipe.nextStep()+1, true);
						if(CurrentRecipe.getStepNumber() == CurrentRecipe.NumberOfSteps-1)
						{
							NextStep.setVisibility(View.GONE);
							Finish.setVisibility(View.VISIBLE);
						}
						else Finish.setVisibility(View.GONE);
						PreviousStep.setVisibility(View.VISIBLE);
					}
				});
				
				PreviousStep.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// When this button is pressed, go onto the previous step
						mViewPager.setCurrentItem(CurrentRecipe.previousStep()+1, true);
						if(CurrentRecipe.getStepNumber() == -1)
						{
							PreviousStep.setVisibility(View.GONE);
							Finish.setVisibility(View.GONE);
						}
						else if(CurrentRecipe.getStepNumber() == CurrentRecipe.NumberOfSteps-1)
							Finish.setVisibility(View.VISIBLE);
						else Finish.setVisibility(View.GONE);
						NextStep.setVisibility(View.VISIBLE);
						
					}
				});
				
				Finish.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						startActivity(ToFinishActivity);
					}
				});
				
				mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrollStateChanged(int arg0) {
						/*CurrentRecipe.setStepNumber(mViewPager.getCurrentItem() - 1);
						if(arg0 == ViewPager.SCROLL_STATE_SETTLING)
						{
							if(CurrentRecipe.getStepNumber() == -1)
							{
								PreviousStep.setVisibility(View.GONE);
								NextStep.setVisibility(View.VISIBLE);
								Finish.setVisibility(View.GONE);
							}
							else if(CurrentRecipe.getStepNumber() == CurrentRecipe.NumberOfSteps-1)
							{
								NextStep.setVisibility(View.GONE);
								PreviousStep.setVisibility(View.VISIBLE);
								Finish.setVisibility(View.VISIBLE);
							}
							else
							{
								NextStep.setVisibility(View.VISIBLE);
								PreviousStep.setVisibility(View.VISIBLE);
								Finish.setVisibility(View.GONE);
							}
						}*/
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {}

					@Override
					public void onPageSelected(int arg0) {
						CurrentRecipe.setStepNumber(arg0 - 1);
						if(CurrentRecipe.getStepNumber() == -1)
						{
							PreviousStep.setVisibility(View.GONE);
							NextStep.setVisibility(View.VISIBLE);
							Finish.setVisibility(View.GONE);
						}
						else if(CurrentRecipe.getStepNumber() == CurrentRecipe.NumberOfSteps-1)
						{
							NextStep.setVisibility(View.GONE);
							PreviousStep.setVisibility(View.VISIBLE);
							Finish.setVisibility(View.VISIBLE);
						}
						else
						{
							NextStep.setVisibility(View.VISIBLE);
							PreviousStep.setVisibility(View.VISIBLE);
							Finish.setVisibility(View.GONE);
						}
					}
				});
				
			} catch (IOException e) {
				// TODO Dialog box
				e.printStackTrace();
			}
        }
        else
        {
        	try {
				openFileInput(RecipeName);
				// TODO: File reading parameters
			} catch (FileNotFoundException e) {
				// TODO Error dialog box
				e.printStackTrace();
			}
        }
        
    }

    // When the user leaves, save the current step of the recipe
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
    	outState.putInt("position", mViewPager.getCurrentItem());
    	super.onSaveInstanceState(outState);
    }
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.stepmenu, menu);
    	return true;
    }
    
}
