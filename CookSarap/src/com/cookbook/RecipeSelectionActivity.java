package com.cookbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeSelectionActivity extends ListActivity {
	private String TargetRecipe = new String();
	private String[] RecipeList;
	private boolean SearchIsRequested; // true if a search has been requested
	private int SearchParameter; // 1 if the search should be for tags, 0 for recipes
	private float TextSize;
	private RecipeArrayAdapter RecipeAdapter;
	private OnSharedPreferenceChangeListener listener;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe);
        
        // Initialize parameters to initial vales
        SearchParameter = 0;
        
	    final Intent ToRecipeViewActivity = new Intent(this, RecipeViewActivity.class);
	    final Intent ReceivedIntent = getIntent();
	    ListView Lv = getListView();
	    
	    final TextView SelectRecipe = (TextView) findViewById(R.id.selectrecipe);
	    TextSize = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("font_size", "15"));
	    SelectRecipe.setTextSize(TextSize);
	
	    Lv.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view,
	            int position, long id)
	        {
	        	TargetRecipe = (String) parent.getItemAtPosition(position);
	        	if(TargetRecipe == null)
	        		TargetRecipe = "listnull";
	        	ToRecipeViewActivity.putExtra("com.cookbook.RecipeName", TargetRecipe);
	        	
	        	// Check if the target recipe is an external recipe or not
	        	boolean IsExternalRecipe = false;
	            String [] ExternalRecipeList = fileList();
	            int i;
	            for(i=0;i<ExternalRecipeList.length; i++)
	            {
	            	if(TargetRecipe == ExternalRecipeList[i] && IsExternalRecipe == false)
	            		IsExternalRecipe = true;
	            }
	            ToRecipeViewActivity.putExtra("com.cookbook.IsExternalRecipe", IsExternalRecipe);
	        	
	        	
	        	try
	        	{
	        		startActivity(ToRecipeViewActivity);
	        	}
	        	catch (ActivityNotFoundException e)
	        	{
	        		Toast.makeText(getBaseContext(), "Intent failed", Toast.LENGTH_SHORT).show();
	        	}
	        }
	    });
	    
	    AssetManager assetmanager = getAssets();
	    String[] InternalRecipeList;
		try {
			InternalRecipeList = assetmanager.list("Recipes");
			
			// Remove the file extension on each recipe name
			int i;/*
			for(i=0; i<InternalRecipeList.length; i++)
			{	
				StringBuilder trimmer = new StringBuilder(InternalRecipeList[i]);
				InternalRecipeList[i] = trimmer.substring(0, InternalRecipeList[i].indexOf("."));
			}*/
			
	       	String[] ExternalRecipeList = fileList();
	        if(ExternalRecipeList != null)
	        {
	        	RecipeList = new String[ExternalRecipeList.length + InternalRecipeList.length];
	        	
	        	// Combine the two arrays into one, given by RecipeList
	            for(i = 0; i < ExternalRecipeList.length; i++)
	            {
	            	RecipeList[i] = ExternalRecipeList[i];
	            }
	            for(i = ExternalRecipeList.length; i < InternalRecipeList.length; i++)
	            {
	            	RecipeList[i] = InternalRecipeList[i];
	            }
	            
	            if(Intent.ACTION_SEARCH.equals(ReceivedIntent.getAction()))
	        	{
	        		String query = ReceivedIntent.getStringExtra(SearchManager.QUERY);
	        		if(SearchParameter == 0)
	        			RecipeList = searchRecipes(RecipeList, query);
	        		else RecipeList = searchTags(RecipeList, query);
	        		// TODO: Adjust for locale
	        		SelectRecipe.setText("Search Results: ");
	        	}
	            RecipeAdapter = new RecipeArrayAdapter(this, R.layout.recipe_list_item, R.id.recipelistentry, RecipeList);
	            setListAdapter(RecipeAdapter);
	        }
	        else
	        {
	        	if(Intent.ACTION_SEARCH.equals(ReceivedIntent.getAction()))
	        	{
	        		String query = ReceivedIntent.getStringExtra(SearchManager.QUERY);
	        		if(SearchParameter == 0)
	        		{
	        			InternalRecipeList = searchRecipes(InternalRecipeList, query); // Search Recipe Names
	        		}
	        		else RecipeList = searchTags(RecipeList, query);
	        		// TODO: Adjust for locale
	        		SelectRecipe.setText("Search Results: ");
	        	}
	        	RecipeList = InternalRecipeList;
	        	RecipeAdapter = new RecipeArrayAdapter(this, R.layout.recipe_list_item, R.id.recipelistentry, RecipeList);
	        	setListAdapter(RecipeAdapter);
	        }
	    } catch (IOException e){
	    	String[] RecipeList = {"No Recipes Found."};
	    	RecipeAdapter = new RecipeArrayAdapter(this, R.layout.recipe_list_item, R.id.recipelistentry, RecipeList);
	    	setListAdapter(RecipeAdapter);
	    	
	    }
	    
	    // Restore the search dialog, if necessary
        if(savedInstanceState != null)
        {
        	SearchIsRequested = savedInstanceState.getBoolean("SearchIsRequested");
        	if(SearchIsRequested)
        		onSearchRequested();
        }
        else SearchIsRequested = false;
        
        // Updates the textviews when the font size changes
        SharedPreferences Preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				TextSize = Float.parseFloat(sharedPreferences.getString("font_size", "15"));
				RecipeAdapter.notifyDataSetChanged();
				SelectRecipe.setTextSize(TextSize);
			}
		};
		Preferences.registerOnSharedPreferenceChangeListener(listener);
    }
    
  

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onSearchRequested() {
    	SearchIsRequested = true;
		return super.onSearchRequested();
    	
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
    	super.onSaveInstanceState(outState);
    	outState.putBoolean("SearchIsRequested", SearchIsRequested);
    }
    
    private String[] searchRecipes(String[] RecipeList, String Query)
    {
    	String[] recipelist = RecipeList.clone();
    	
    	List<String> ReturnList = new ArrayList<String>();
    	
    	// Convert everything to lowercase
    	Query = Query.toLowerCase(Locale.US);
    	int i;
    	for(i=0; i<recipelist.length; i++)
    		recipelist[i] = recipelist[i].toLowerCase(Locale.US);
    	
    	List<String> SearchList = Arrays.asList(recipelist);
    	
    	Collections.sort(SearchList);
    	int Index = Collections.binarySearch(SearchList, Query);
    	if(Index >= 0)
    	{
    		ReturnList.add(SearchList.get(Index));
    	}
    	else 
    	{
    		for(i=0; i<SearchList.size(); i++)
    		{
    			if(SearchList.get(i).contains(Query))
    				ReturnList.add(RecipeList[i]);
    		}
    	}
    	String[] returnlist = new String[ReturnList.size()];
    	
    	// Convert list back into an array
    	for(i=0; i<ReturnList.size(); i++)
    		returnlist[i] = ReturnList.get(i);
    	
    	return returnlist;
    }
    
    private String[] searchTags(String[] recipeList, String query) {
		// Search tags (first tag takes precedent)
		return null;
	}
    
    private class RecipeArrayAdapter extends ArrayAdapter<String>
    {

		public RecipeArrayAdapter(Context context, int resource,
				int textViewResourceId, String[] recipeList) {
			super(context, resource, textViewResourceId, recipeList);
		}
		
		@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		View list_item = super.getView(position, convertView, parent);
    		TextView RecipeNameDisplay = (TextView) list_item.findViewById(R.id.recipelistentry);
    		TextView Tags = (TextView) list_item.findViewById(R.id.recipetag);
    		
    		RatingBar mRatingBar = (RatingBar) list_item.findViewById(R.id.reciperating);
    		String RecipeName = RecipeNameDisplay.getText().toString();
    		
    		// Set the text size for the text views
    		RecipeNameDisplay.setTextSize(TextSize);
    		Tags.setTextSize(TextSize);
    		
    		mRatingBar.setRating(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getFloat(RecipeName, 0));
    		
    		// Get the tags for the recipe
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
            		String RecipeTags = Recipe.scanRecipeForTags(Input);
            		Tags.setText(RecipeTags);
            		Input.close();
            	} catch (IOException e) {
            		e.printStackTrace();
            	}
            }
            else
            {
            	// TODO: Read a downloaded file
            }
    		
    		return list_item;
		}
    }
    
    private class RecipeAndTags implements Comparable<RecipeAndTags> {
    	String Recipe;
    	List<String> Tags;
    	
    	public int compareTo(RecipeAndTags another) {
    		if(Tags.equals(another))
				return 0;
    		else if(Tags.size() > another.Tags.size()) // The larger tags list is "greater"
    			return 1;
    		else if(Tags.size() < another.Tags.size()) // The smaller tags list is "less"
    			return -1;
    		else
    		{
    			int i;
    			for(i=0; i<Tags.size(); i++)
    			{
    				if(Tags.get(i).compareTo(another.Tags.get(i)) != 0)
    					return Tags.get(i).compareTo(another.Tags.get(i));
    			}
    		}
    		return 0;
		}
		
		RecipeAndTags(String Recipe, String Tags) {
			String tags = new String(Tags);
			tags = trimString(tags);
			Scanner scanner = new Scanner(tags);
			scanner.useDelimiter(" ");
			List<String> TagList = new ArrayList<String>();
			do
			{
				TagList.add(scanner.next());
			}
			while(scanner.hasNext());
			
			this.Recipe = Recipe;
			this.Tags = TagList;
			
		}
    }
    
    private class RecipeAndRating implements Comparable<RecipeAndRating> {
    	String Recipe;
    	float Rating;
    	
		@Override
		public int compareTo(RecipeAndRating another) {
			if(Rating < another.Rating)
				return 1;
			if(Rating > another.Rating)
				return -1;
			else return 0;
		}
		
		RecipeAndRating(String Recipe, float Rating) {
			this.Recipe = Recipe;
			this.Rating = Rating;
		}
    }
    
    // Trims a string that contains punctuation, so that it can be read properly by parsers
	private String trimString(String input)
	{
		StringBuilder trimmer = new StringBuilder(input);
		int i, j=0;
		for(i=0; i<PunctuationMarks.length; i++)
		{
			if(input.indexOf(PunctuationMarks[i], j) != -1)
				trimmer.deleteCharAt(input.indexOf(PunctuationMarks[i], j++));
		}
		return trimmer.toString();
	}
	
	private static char[] PunctuationMarks = {'!', '@','#','$','%','^','&','*','(',')','-','=','+','_','[',
												']','{','}',';',':','<',',','.','>','/','?','|','~','`'};
}
