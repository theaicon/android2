package com.cookbook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

class Recipe {
	private String RecipeName = new String();
	private int StepNumber;
	final int NumberOfSteps;
	private String[] RecipeSteps;
	private Ingredient[] IngredientList;
	final int NumberOfIngredients;
	private String[] Tags;
	// private int NumberOfTags;
	
	// Returns the String to be displayed at the specified position
	String getStepInfo(int position)
	{
		int OriginalPosition = StepNumber;
		if(position > NumberOfSteps || position < 0)
			return null;
		else
		{
			if(StepNumber < position-1)
				while(nextStep() != position-1);
			else if(StepNumber > position-1)
				while(previousStep() != position-1);
		}
		String ReturnValue = new String(getCurrentStep());
		StepNumber = OriginalPosition;
		return ReturnValue;
	}
	/* Increments the current step and moves the screen to the next step. */
	int nextStep()
	{
		if(StepNumber < NumberOfSteps)
		{
			StepNumber++;
		}
		return StepNumber;
	}

	/* Increments the current step and moves the screen to the previous step. */
	int previousStep()
	{
		if(StepNumber > -1)
		{
			StepNumber--;
		}
		return StepNumber;
	}
	
	/* Displays the text for the current step to the string */
	String getCurrentStep()
	{
		if(StepNumber == -1)
		{
			return getIngredientList();
		}
		else return RecipeSteps[StepNumber];
	}
	
    // Get the tags, in human-readable list format
	/*String getTags()
	{
		StringBuilder Builder = new StringBuilder();
		// TODO: Adjust for locales
		Builder.append("Tags: ");
		for(String currentTag : this.Tags)
		{
			Builder.append(currentTag);
			Builder.append(", ");
		}
		Builder.delete(Builder.toString().length() - 2, Builder.toString().length() - 1);
		return Builder.toString();		
	}*/
	
	// Get the tags, in human-readable list format
	static String scanRecipeForTags(InputStream in) throws IOException
	{
		/* Set up the character stream from file */
		BufferedReader input = new BufferedReader(new InputStreamReader(in));
		
		String currentLine = input.readLine();
		while(!currentLine.equals("END_RECIPE"))
			currentLine = input.readLine(); // Scroll to the beginning of the tag list
		StringBuilder Builder = new StringBuilder();
		// TODO: Adjust for locales
		Builder.append("Tags: ");
		currentLine = input.readLine();
		do
		{
			Builder.append(currentLine);
			Builder.append(", ");
			currentLine = input.readLine();
		} while(!currentLine.equals("END_TAGS"));
		Builder.delete(Builder.toString().length() - 2, Builder.toString().length() - 1);
		input.close();
		return Builder.toString();		
	}
	
	// Get the list of ingredients, in human-readable list format
	static String scanRecipeForIngredients(InputStream in) throws IOException
	{
		/* Set up the character stream from file */
		BufferedReader input = new BufferedReader(new InputStreamReader(in));
		
		String currentLine = input.readLine();
		while(!currentLine.equals("END_DESCRIPTION"))
			currentLine = input.readLine(); // Scroll to the beginning of the tag list
		input.readLine(); // Skip "END_DESCRIPTION"
		StringBuilder Builder = new StringBuilder();
		// TODO: Adjust for locales
		Builder.append("Ingredients: ");
		currentLine = input.readLine();
		do
		{
			Scanner scanner = new Scanner(currentLine);
			scanner.useDelimiter(" ");
			scanner.next(); // Skip the quantity number
			scanner.next(); // Skip the unit
			String Ingredient = scanner.next();
			Builder.append(Ingredient);
			Builder.append(", ");
			currentLine = input.readLine();
		} while(!currentLine.equals("END_INGREDIENTS"));
		Builder.delete(Builder.toString().length() - 2, Builder.toString().length() - 1);
		// Find and replace underscores with spaces
		while(Builder.indexOf("_") != -1)
			Builder.replace(Builder.indexOf("_"), Builder.indexOf("_")+1, " ");
		input.close();
		return Builder.toString();
	}
	
	// Get the recipe description, in human-readable format
	static String scanRecipeForDescription(InputStream in) throws IOException
	{
		/* Set up the character stream from file */
		BufferedReader input = new BufferedReader(new InputStreamReader(in));
		String ReturnValue = input.readLine();
		input.close();
		return ReturnValue;
	}
	
	int getStepNumber()
	{
		return StepNumber;
	}
	
	// Manually set the current step number.
	void setStepNumber(int input)
	{
		if(input < 0)
			StepNumber = -1; // Set stepnumber to ingredients page
		else if(input >= NumberOfSteps - 1)
			StepNumber = NumberOfSteps -1;
		else StepNumber = input;
	}
	String getRecipeName()
	{
		// TODO: Check if string is empty
		return RecipeName;
	}
	
	String getIngredientList()
	{
		StringBuilder Builder = new StringBuilder();
		int i;
		for(i=0; i<NumberOfIngredients; i++)
		{
			Builder.append(IngredientList[i].getIngredient());
			Builder.append("\n");
		}
		return Builder.toString();
	}
	
	Recipe(InputStream in, String RecipeName) throws IOException
	{
		/* Set up the character stream from file */
		BufferedReader input = new BufferedReader(new InputStreamReader(in));
		
		this.RecipeName = RecipeName;
		String currentLine = new String();
		// Skip the recipe description
		while(!currentLine.equals("END_DESCRIPTION"))
			currentLine = input.readLine();
		currentLine = input.readLine();
		
		// Construct the list of ingredients
		this.NumberOfIngredients = Integer.parseInt(currentLine);
		this.IngredientList = new Ingredient[this.NumberOfIngredients];
		int i = 0; // Counts number of ingredients scanned
		currentLine = input.readLine();
		while(!currentLine.equals("END_INGREDIENTS"))
		{
			Scanner IngredientParser = new Scanner(currentLine);
			IngredientParser.useDelimiter(" ");
			String quantity = IngredientParser.next();
			String unit = IngredientParser.next();
			String ingredient = IngredientParser.next();
			IngredientList[i] = new Ingredient(quantity, unit, ingredient);
			
			i++;
			currentLine = input.readLine();
			
		}
		
		currentLine = input.readLine();
		this.NumberOfSteps = Integer.parseInt(currentLine);
		this.RecipeSteps = new String[this.NumberOfSteps];
		currentLine = input.readLine();
		while(!currentLine.equals("END_RECIPE"))
		{
			RecipeSteps[StepNumber] = currentLine;
			this.StepNumber++;
			currentLine = input.readLine();
		}
		this.StepNumber = -1;
		
		/*currentLine = input.readLine();
		this.NumberOfTags = Integer.parseInt(currentLine);
		i = 0;
		currentLine = input.readLine();
		// Search for and add tags
		while(!currentLine.equals("END_TAGS"))
		{
			this.Tags[i] = currentLine;
			currentLine = input.readLine();
		}*/
		input.close();
		
	}
	
	ParseData parseCurrentRecipeStep()
	{
		String CurrentStep = getCurrentStep();
		String IngredientList = getIngredientList();
		ParseData ReturnValue = new ParseData(NumberOfIngredients);
		String CurrentWord = new String(); // This stores the current word to be analyzed
		String PreviousWord = new String();
		Scanner StepParser = new Scanner(CurrentStep);

		
		StepParser.useDelimiter(" ");
		while(!StepParser.hasNext())
		{
			CurrentWord = StepParser.next();
			if(CurrentWord.contains("minute"))
			{
				if(PreviousWord.contains("-"))
				{
					Scanner NumberParser = new Scanner(PreviousWord);
					NumberParser.useDelimiter("-");
					PreviousWord = NumberParser.next(); // Always use the lower of the two numbers in the range
				}
				else ReturnValue.Time = Float.parseFloat(PreviousWord);
				ReturnValue.TimePresent = true;
			}
				
			else if(IngredientList.contains(CurrentWord))
			{
				ReturnValue.addIngredient(CurrentWord);
			}
			PreviousWord = CurrentWord;
		}
		return ReturnValue;
	}
}
