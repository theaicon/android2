package com.cookbook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;

public class FinishActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finish);
		
		final String RecipeName = (String) getIntent().getExtras().get("com.cookbook.RecipeName");
		Button BackToMain = (Button) findViewById(R.id.returnbutton);
		RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingbar);
		final Intent ToMainMenu = new Intent(this, CookbookActivity.class);
		
		mRatingBar.setRating(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getFloat(RecipeName, 0));
		
		mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {	
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				SharedPreferences.Editor RatingSaver = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				RatingSaver.putFloat(RecipeName, rating);
				RatingSaver.commit();
				
			}
		});
		
		BackToMain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(ToMainMenu);
			}
		});
	}
}
