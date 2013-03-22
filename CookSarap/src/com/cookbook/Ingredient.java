package com.cookbook;

import java.util.Scanner;

class Ingredient {
	
	enum UnitOfMeasurement
	{
		teaspoon, tablespoon, cup, can, pint, pinch, gallon, fluid_ounce, kilo, pack, pc, bundle, bowl, clove, small, medium, large, chopped, lb, liter, undefined;
	}
	
	String Fraction;
	float Quantity;
	UnitOfMeasurement Unit;
	String Ingredient;
	
	Ingredient(String quantity, String unit, String ingredient)
	{
		
		// Find and replace underscores in quantity with spaces
		StringBuilder Builder = new StringBuilder(quantity);
		while(Builder.indexOf("_") != -1)
			Builder.replace(Builder.indexOf("_"), Builder.indexOf("_")+1, " ");
		this.Fraction = Builder.toString();
		
		// Translate fraction string into float value
		try {
			this.Quantity = Integer.valueOf(this.Fraction);
		} catch (NumberFormatException e) {
			Scanner FirstElementScanner = new Scanner(this.Fraction);
			int integer1 = FirstElementScanner.nextInt();
			FirstElementScanner.useDelimiter(" ");
			Scanner FractionScanner = new Scanner(FirstElementScanner.next());
			FractionScanner.useDelimiter("/");
			int integer2 = Integer.parseInt(FractionScanner.next());
			int integer3 = Integer.parseInt(FractionScanner.next());
			this.Quantity = integer1 + (integer2/integer3);
		}
		
		// Translate String into Enum
		try{
			this.Unit = UnitOfMeasurement.valueOf(unit);
		} catch (IllegalArgumentException e){
			this.Unit = UnitOfMeasurement.undefined;
		}
		
		this.Ingredient = ingredient;
	}
	
	// TODO: For multiple locales, units of measurement will be different (specifically British)
	/*Ingredient convertMeasurement(UnitOfMeasurement unit, Ingredient ingredient)
	{
		if(unit == ingredient.Unit)
			return ingredient;
		
	}*/
	
	String getIngredient()
	{		
		StringBuilder Builder = new StringBuilder(Fraction);
		
		Builder.append(' '); // Space between Quantity and Unit
		Builder.append(Unit.toString());
		if(Quantity != 1)
			Builder.append("s");
		Builder.append(' '); // Space between Unit and Ingredient
		Builder.append(Ingredient);

		// Find and replace underscores with spaces
		while(Builder.indexOf("_") != -1)
			Builder.replace(Builder.indexOf("_"), Builder.indexOf("_")+1, " ");
		
		return Builder.toString();
		
	}
}
