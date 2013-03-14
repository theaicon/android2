package com.cookbook;

class ParseData {
	boolean TimePresent; // true if a timer is needed
	float Time; // time in minutes
	boolean IngredientsPresent; // true if ingredients are mentioned
	String[] Ingredients;
	boolean TemperaturePresent; // true if a temperature is present
	float Temperature;
	String TimePhrase; // Records string phrase where time data appears (for hyperlink)
	String TemperaturePhrase; // Records string phrase where temperature appears (for hyperlink)
	
	enum UnitOfTemperature
	{
		fahrenheit, celsius;
	}
	
	UnitOfTemperature TemperatureUnit;
	
	void addIngredient(String IngredientName)
	{
		IngredientsPresent = true;
		int i = 0;
		while(Ingredients[i] != null)
			i++;
		Ingredients[i] = IngredientName;
	}
	
	ParseData(int NumberOfIngredients)
	{
		this.TimePresent = false;
		this.Time = 0;
		this.IngredientsPresent = false;
		this.Ingredients = new String[NumberOfIngredients];
		this.TimePhrase = new String();
		this.TemperaturePhrase = new String();
		this.TemperaturePresent = false;
		this.Temperature = 0;
		this.TemperatureUnit = null;
	}
	
}
