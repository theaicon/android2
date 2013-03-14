package com.cookbook;

import java.util.Scanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecipeStepFragment extends Fragment {
	private TextView TimerDisplay;
	private boolean TimerIsRunning;
	private boolean TimerIsDisplayed;
	private CountDownTimer mTimer;
	private ParseData StepData;
	private int RecipeStepNumber;
	private long SavedTimerProgress; // Used to make the timer persistent
	private String PreviousNotification; // Used to check if a notification update is needed
	private OnSharedPreferenceChangeListener listener;
	
	static RecipeStepFragment newInstance(Recipe TargetRecipe, int position)
	{
		RecipeStepFragment f = new RecipeStepFragment();
		Bundle args = new Bundle();
		args.putString("RecipeStep", TargetRecipe.getStepInfo(position));
		args.putInt("StepNumber", position);
		args.putString("IngredientList", TargetRecipe.getIngredientList());
		args.putInt("NumberOfIngredients", TargetRecipe.NumberOfIngredients);
		f.setArguments(args);
		return f;
	}
	
	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.step, container, false);
		 
		final TextView StepNumberDisplay = (TextView) view.findViewById(R.id.stepnumber);
		final TextView RecipeStepDisplay = (TextView) view.findViewById(R.id.recipestep);
		
		// Load the preferences for text size
		SharedPreferences Preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		float TextSize = Float.parseFloat(Preferences.getString("font_size", "15"));
		RecipeStepDisplay.setTextSize(TextSize);
		StepNumberDisplay.setTextSize(TextSize);
		
		// Updates the textviews when the font size changes
		listener = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				float TextSize = Float.parseFloat(sharedPreferences.getString("font_size", "15"));
				RecipeStepDisplay.setTextSize(TextSize);
				StepNumberDisplay.setTextSize(TextSize);
			}
		};
		Preferences.registerOnSharedPreferenceChangeListener(listener);
		
		final RelativeLayout Timer = (RelativeLayout) view.findViewById(R.id.timerlayout);
		Button TimerStart = (Button) view.findViewById(R.id.timerstart);
		Button TimerReset = (Button) view.findViewById(R.id.timerreset);
		Button TimerClose = (Button) view.findViewById(R.id.timerclose);
		Button TimerUp = (Button) view.findViewById(R.id.timerup);
		Button TimerDown = (Button) view.findViewById(R.id.timerdown);
		
		// Show that the timer has started on the notification bar, update it every minute. When the timer is finished, play the default notification sound.
		final NotificationManager UpdateNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.icon;
		// TODO: Adjust strings for locales
		final Notification UpdateNotification = new Notification(icon, "Cookbook - Timer", System.currentTimeMillis());
		final Notification StopNotification = new Notification(icon, "Cookbook - Timer", System.currentTimeMillis());
		StopNotification.defaults |= Notification.DEFAULT_SOUND;
		StopNotification.defaults |= Notification.DEFAULT_VIBRATE;
		StopNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		final PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, null, 0); // TODO: Add intent to the calling activity? (Needed for 4.0)
		StopNotification.setLatestEventInfo(getActivity().getApplicationContext(), "Cookbook - Timer", "Time's Up!", mPendingIntent); // TODO: Adjust for locales
		
		 
		if(savedInstanceState != null)
		{
			TimerIsDisplayed = savedInstanceState.getBoolean("TimerIsDisplayed");
			TimerIsRunning = savedInstanceState.getBoolean("TimerIsRunning");
			if(TimerIsDisplayed)
				Timer.setVisibility(View.VISIBLE);
			else Timer.setVisibility(View.GONE);
		}
		else
		{
			TimerIsDisplayed = false;
			TimerIsRunning = false;
			Timer.setVisibility(View.GONE);
		}
     
		Bundle args = getArguments();
		RecipeStepNumber = args.getInt("StepNumber");
		String RecipeStepText = args.getString("RecipeStep");
     
		RecipeStepDisplay.setText(RecipeStepText);
		if(RecipeStepNumber == 0)
			// TODO: Adjust for locales
			StepNumberDisplay.setText("Ingredients");
		else
		{
			StringBuilder Builder = new StringBuilder("Step ");
			Builder.append(Integer.toString(RecipeStepNumber));
			StepNumberDisplay.setText(Builder.toString());
		
			StepData = parseRecipeStep(args.getString("IngredientList"), RecipeStepText, args.getInt("NumberOfIngredients"));
			StringBuilder StepScanner = new StringBuilder(RecipeStepText);
			Spannable LinkBuilder = Spannable.Factory.getInstance().newSpannable(RecipeStepText);
			 
			if(StepData.TimePresent)
			{
				if(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean("auto_timer", false))
				{
					TimerIsDisplayed = true;
					Timer.setVisibility(View.VISIBLE);
				}
				
				// Create link in text that will generate timer (lower left corner)
				int TimePhraseStart = StepScanner.indexOf(StepData.TimePhrase);
				int TimePhraseEnd = TimePhraseStart+StepData.TimePhrase.length();
				if(TimePhraseStart != -1)
				{
					ClickableSpan TimerLink = new ClickableSpan() {
						@Override
						public void onClick(View view){
							if(!TimerIsDisplayed)
							{
								Timer.setVisibility(View.VISIBLE);
								TimerIsDisplayed = true;
							}
							else
							{
								Timer.setVisibility(View.GONE);
								TimerIsDisplayed = false;
							}
						}
					};
				LinkBuilder.setSpan(TimerLink, TimePhraseStart, TimePhraseEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				RecipeStepDisplay.setText(LinkBuilder);
				RecipeStepDisplay.setMovementMethod(LinkMovementMethod.getInstance());
				}
				 
				// Set up Timer Interface
				final long OriginalTime = (new Float(StepData.Time * 1000 * 60)).longValue();
				TimerDisplay = (TextView) view.findViewById(R.id.timer);
				long TargetTime = OriginalTime;
				if(savedInstanceState != null)
					TargetTime = savedInstanceState.getLong("SavedTimerProgress");
				SavedTimerProgress = TargetTime;
					 
				if(TimerIsRunning)
				{
					if(SystemClock.elapsedRealtime() >= TargetTime + savedInstanceState.getLong("TimeUponLeaving")) // If the timer should already be up
					{
						TimerDisplay.setText("0:00");
						TimerIsRunning = false;
						UpdateNotificationManager.notify(0, StopNotification);
					}
					else
					{
						// This ensures no skips in the timer due to state changes
						TargetTime = TargetTime + savedInstanceState.getLong("TimeUponLeaving") - SystemClock.elapsedRealtime(); 
						mTimer = new CountDownTimer(TargetTime, 10L) {
							public void onTick(long millisUntilFinished){
								TimerDisplay.setText(printTimeAsString(millisUntilFinished));
								SavedTimerProgress = millisUntilFinished;
								TimerIsRunning = true;
								String NewNotification = printNotificationUpdate(millisUntilFinished);
								if(!NewNotification.equals(PreviousNotification))
								{
									UpdateNotification.setLatestEventInfo(getActivity().getApplicationContext(), "Cookbook - Timer",NewNotification, mPendingIntent);
									PreviousNotification = NewNotification;
									UpdateNotificationManager.notify(0, UpdateNotification);
								}
							}
			
							public void onFinish() {
								// TODO: Add alarm sound, maybe flashing TimerDisplay?
								TimerDisplay.setText("0:00");
								UpdateNotificationManager.notify(0, StopNotification);
							}	
						};
						mTimer.start();
					}
					TimerDisplay.setText(printTimeAsString(TargetTime));
				}
				else
				{
					TimerDisplay.setText(printTimeAsString(TargetTime));
					mTimer = new CountDownTimer(TargetTime, 10L) {
						public void onTick(long millisUntilFinished){
							TimerDisplay.setText(printTimeAsString(millisUntilFinished));
							SavedTimerProgress = millisUntilFinished;
							TimerIsRunning = true;
							String NewNotification = printNotificationUpdate(millisUntilFinished);
							if(!NewNotification.equals(PreviousNotification))
							{
								UpdateNotification.setLatestEventInfo(getActivity().getApplicationContext(), "Cookbook - Timer", NewNotification, mPendingIntent);
								PreviousNotification = NewNotification;
								UpdateNotificationManager.notify(0, UpdateNotification);
							}
						}
						public void onFinish() {
							// TODO: Add alarm sound, maybe flashing TimerDisplay?
							TimerDisplay.setText("0:00");
							TimerIsRunning = false;
							UpdateNotificationManager.notify(0, StopNotification);
						}		
					};
				}
					 
				TimerStart.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v){
						// TODO: Change graphic to synchronize with change in button function
						if(TimerIsRunning)
						{
							mTimer.cancel();
							TimerIsRunning = false;
							UpdateNotificationManager.cancel(0);
							// Create a new, identical timer out of the time remaining
							mTimer = new CountDownTimer(printTimeAsLong(TimerDisplay.getText().toString()), 10L){
								public void onTick(long millisUntilFinished){
									TimerDisplay.setText(printTimeAsString(millisUntilFinished));
									TimerIsRunning = true;
									SavedTimerProgress = millisUntilFinished;
									String NewNotification = printNotificationUpdate(millisUntilFinished);
									if(!NewNotification.equals(PreviousNotification))
									{
										UpdateNotification.setLatestEventInfo(getActivity().getApplicationContext(), "Cookbook - Timer", NewNotification, mPendingIntent);
										PreviousNotification = NewNotification;
										UpdateNotificationManager.notify(0, UpdateNotification);
									}
								}
								public void onFinish() {
									// TODO: Add alarm sound, maybe flashing TimerDisplay?
									TimerDisplay.setText("0:00");
									UpdateNotificationManager.notify(0, StopNotification);
								}
							};
						}
						else 
						{
							mTimer.start();
							UpdateNotification.setLatestEventInfo(getActivity().getApplicationContext(), "Cookbook - Timer", 
									printNotificationUpdate(printTimeAsLong(TimerDisplay.getText().toString())), mPendingIntent);
							UpdateNotificationManager.notify(0, UpdateNotification);
						}
					}});
				TimerReset.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v){
						if(TimerIsRunning) // Stop the timer, just like the other button
						{
							mTimer.cancel();
							UpdateNotificationManager.cancel(0);
							TimerIsRunning = false;
							// Create a new, identical timer out of the time remaining
							mTimer = new CountDownTimer(printTimeAsLong(TimerDisplay.getText().toString()), 10L){
								public void onTick(long millisUntilFinished){
									TimerDisplay.setText(printTimeAsString(millisUntilFinished));
									TimerIsRunning = true;
									SavedTimerProgress = millisUntilFinished;
									String NewNotification = printNotificationUpdate(millisUntilFinished);
									if(!NewNotification.equals(PreviousNotification))
									{
										UpdateNotification.setLatestEventInfo(getActivity().getApplicationContext(), "Cookbook - Timer", NewNotification, mPendingIntent);
										PreviousNotification = NewNotification;
										UpdateNotificationManager.notify(0, UpdateNotification);
									}
								}
								public void onFinish() {
									// TODO: Add alarm sound, maybe flashing TimerDisplay?
									TimerDisplay.setText("0:00");
									UpdateNotificationManager.notify(0, StopNotification);
								}
							};
						}
						else // Recreate the timer with the value from the step parse data
						{
							SavedTimerProgress = OriginalTime;
							mTimer = new CountDownTimer(OriginalTime, 10L){
								public void onTick(long millisUntilFinished){ 
									TimerDisplay.setText(printTimeAsString(millisUntilFinished));
									SavedTimerProgress = millisUntilFinished;
									TimerIsRunning = true;
									String NewNotification = printNotificationUpdate(millisUntilFinished);
									if(!NewNotification.equals(PreviousNotification))
									{
										UpdateNotification.setLatestEventInfo(getActivity().getApplicationContext(), "Cookbook - Timer",NewNotification, mPendingIntent);
										PreviousNotification = NewNotification;
										UpdateNotificationManager.notify(0, UpdateNotification);
									}
								}
								public void onFinish() {
									// TODO: Add alarm sound, maybe flashing TimerDisplay?
									TimerDisplay.setText("0:00");
									UpdateNotificationManager.notify(0, StopNotification);
								}
							};
							TimerIsRunning = false;
							TimerDisplay.setText(printTimeAsString(OriginalTime));
						}
					}
				});
					 
				TimerClose.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v){
						Timer.setVisibility(View.GONE);
						TimerIsDisplayed = false;
					}
				}); 
				
				TimerUp.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// Increase the time by 15 seconds.
						long NewTime = printTimeAsLong(TimerDisplay.getText().toString()) + 15000L;
						mTimer.cancel();
						mTimer = new CountDownTimer(NewTime, 10L) {
							public void onTick(long millisUntilFinished){
								TimerDisplay.setText(printTimeAsString(millisUntilFinished));
								SavedTimerProgress = millisUntilFinished;
								String NewNotification = printNotificationUpdate(millisUntilFinished);
								TimerIsRunning = true;
								if(!NewNotification.equals(PreviousNotification))
								{
									UpdateNotification.setLatestEventInfo(getActivity().getApplicationContext(), "Cookbook - Timer", NewNotification, mPendingIntent);
									PreviousNotification = NewNotification;
									UpdateNotificationManager.notify(0, UpdateNotification);
								}
							}
							public void onFinish() {
								// TODO: Add alarm sound, maybe flashing TimerDisplay?
								TimerDisplay.setText("0:00");
								TimerIsRunning = false;
								UpdateNotificationManager.notify(0, StopNotification);
							}		
						};
						TimerDisplay.setText(printTimeAsString(NewTime));
						if(TimerIsRunning)
							mTimer.start();
					}				
				});
				TimerDown.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// Decrease the time by 15 seconds. If this would result in a time of zero, do nothing
						long NewTime = printTimeAsLong(TimerDisplay.getText().toString()) - 15000L;
						if(NewTime > 0L)
						{
							mTimer.cancel();
							mTimer = new CountDownTimer(NewTime, 10L) {
								public void onTick(long millisUntilFinished){
									TimerDisplay.setText(printTimeAsString(millisUntilFinished));
									SavedTimerProgress = millisUntilFinished;
									TimerIsRunning = true;
									String NewNotification = printNotificationUpdate(millisUntilFinished);
									if(!NewNotification.equals(PreviousNotification))
									{
										UpdateNotification.setLatestEventInfo(getActivity().getApplicationContext(), "Cookbook - Timer", NewNotification, mPendingIntent);
										PreviousNotification = NewNotification;
										UpdateNotificationManager.notify(0, UpdateNotification);
									}
								}
								public void onFinish() {
									// TODO: Add alarm sound, maybe flashing TimerDisplay?
									TimerDisplay.setText("0:00");
									TimerIsRunning = false;
									UpdateNotificationManager.notify(0, StopNotification);
									}		
							};
						TimerDisplay.setText(printTimeAsString(NewTime));
						if(TimerIsRunning)
							mTimer.start();
						}
					}				
				});
			}
			if(StepData.IngredientsPresent)
			{
				// TODO: Create link in text that will generate ingredient list
			}
			if(StepData.TemperaturePresent)
			{
				// TODO: Convert temperature if necessary, based on preferences
			}
	     }
   
	 	return view;
	 }
	 
	 @Override
	 public void onSaveInstanceState(Bundle outState)
	 {
		 super.onSaveInstanceState(outState);
		 outState.putLong("SavedTimerProgress", SavedTimerProgress);
		 outState.putLong("TimeUponLeaving", SystemClock.elapsedRealtime());
		 outState.putBoolean("TimerIsRunning", TimerIsRunning);
		 outState.putBoolean("TimerIsDisplayed", TimerIsDisplayed);
		 outState.putInt("StepNumber", RecipeStepNumber);
	 }
	 
	 private ParseData parseRecipeStep(String IngredientList, String RecipeStep, int NumberOfIngredients)
	 {
		ParseData ReturnValue = new ParseData(NumberOfIngredients);
		String CurrentWord = new String(); // This stores the current word to be analyzed
		String PreviousWord = new String();
		Scanner StepParser = new Scanner(RecipeStep);
			
		StepParser.useDelimiter(" ");
		PreviousWord = StepParser.next();
		while(StepParser.hasNext())
		{
			CurrentWord = StepParser.next();
			if(CurrentWord.contains("minute"))
			{
				StringBuilder TimerPhrase = new StringBuilder();
				if(PreviousWord.contains("-"))
				{
					Scanner NumberParser = new Scanner(PreviousWord);
					NumberParser.useDelimiter("-");
					PreviousWord = NumberParser.next(); 
					ReturnValue.Time = Float.parseFloat(PreviousWord); // Always use the lower of the two numbers in the range
					TimerPhrase.append(PreviousWord);
					TimerPhrase.append("-");
					PreviousWord = NumberParser.next(); 
					TimerPhrase.append(PreviousWord);
					TimerPhrase.append(" ");
				}
				else 
				{
					ReturnValue.Time = Float.parseFloat(trimString(PreviousWord));
					TimerPhrase.append(trimString(PreviousWord));
				}
				ReturnValue.TimePresent = true;
				TimerPhrase.append(" ");
				TimerPhrase.append(trimString(CurrentWord));
				ReturnValue.TimePhrase = TimerPhrase.toString();
			}
					
			else if(IngredientList.contains(CurrentWord))
			{
				ReturnValue.addIngredient(trimString(CurrentWord));
			}
			
			else if(CurrentWord.contains("degrees"))
			{
				StringBuilder TemperaturePhrase = new StringBuilder(PreviousWord);
				TemperaturePhrase.append(CurrentWord);
				ReturnValue.TemperaturePresent = true;
				ReturnValue.TemperaturePhrase = trimString(TemperaturePhrase.toString());
			}
			
			PreviousWord = CurrentWord;
		}
		
		return ReturnValue;
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
	
	
	private String printTimeAsString(long TimeInMilliseconds)
	{
		StringBuilder TimeDisplay = new StringBuilder();
		int NumberOfMinutes = 0;
		int ReturnTime = (new Long(TimeInMilliseconds/1000)).intValue(); // Converts the time in milliseconds to an integer
		// Figure out how many minutes are in the given time
		while(ReturnTime >= 60L)
		{
			NumberOfMinutes++;
			ReturnTime -= 60L;
		}
		TimeDisplay.append(Integer.toString(NumberOfMinutes));
		TimeDisplay.append(":"); // Appends a colon for time display
		if(ReturnTime < 10L)
			TimeDisplay.append("0");
		TimeDisplay.append(Integer.toString(ReturnTime));
		return TimeDisplay.toString();
	}
	
	private long printTimeAsLong(String TimeDisplay) // Converts a human readable time into its long representation in milliseconds
	{
		Scanner TimeParser = new Scanner(TimeDisplay);
		TimeParser.useDelimiter(":");
		int NumberOfMinutes = TimeParser.nextInt();
		long ReturnValue = TimeParser.nextLong() * 1000;
		ReturnValue += NumberOfMinutes * 60 * 1000;
		return ReturnValue;
	}
	
	private String printNotificationUpdate(long TimeRemaining)
	{
		StringBuilder Builder = new StringBuilder();
		int MinutesRemaining = 0;
		int InputTime = new Long(TimeRemaining).intValue() / 1000;
		while(InputTime >= 60)
		{
			MinutesRemaining++;
			InputTime -= 60;
		}
		if(InputTime != 0 && MinutesRemaining == 0)
			Builder.append("<1");
		else Builder.append(Integer.toString(MinutesRemaining));
		
		// TODO: Adjust for locales
		if(MinutesRemaining == 1 || MinutesRemaining == 0)
			Builder.append(" minute remaining.");
		else Builder.append(" minutes remaining.");
		
		return Builder.toString();
	}
	
}
