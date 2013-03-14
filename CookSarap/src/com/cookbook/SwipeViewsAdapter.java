package com.cookbook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

 class SwipeViewsAdapter extends FragmentStatePagerAdapter {
	private Recipe TargetRecipe;
	final int NumberOfSteps;
	public SwipeViewsAdapter(FragmentManager fm, Recipe CurrentRecipe) {
        super(fm);
        this.TargetRecipe = CurrentRecipe;
        this.NumberOfSteps = CurrentRecipe.NumberOfSteps;
    }
	
	@Override
	public Fragment getItem(int position) {
		//TargetRecipe.setStepNumber(position);
		return RecipeStepFragment.newInstance(TargetRecipe, position);
	}

	@Override
	public int getCount() {
		return NumberOfSteps + 1;
	}
	
}
