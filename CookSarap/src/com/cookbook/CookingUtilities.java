package com.cookbook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.GridView;

public class CookingUtilities extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cookinguten);
        
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        //gridview.setOnItemClickListener(new OnItemClickListener() {
          //  public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            //    Toast.makeText(HelloGridView.this, "" + position, Toast.LENGTH_SHORT).show();
          //  }
        //});
    }
       
}