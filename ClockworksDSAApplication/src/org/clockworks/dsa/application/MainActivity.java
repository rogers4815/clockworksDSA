package org.clockworks.dsa.application;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.clockworksdsaapplication.R;
import com.googlecode.android_scripting.IntentBuilders;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate()");
        testPython();
    }

    protected void testPython() {
    	Log.d(TAG, "testPython()");
    	String pythonScript = "/sdcard/sl4a/scripts/serviceTest.py";
		Intent intent = IntentBuilders.buildStartInBackgroundIntent(new File(pythonScript));
		intent.putExtra("value1", "test");
		Log.d("MainActivity", "The intent is " + intent.toString());
		
		intent.setClass(this, PythonService.class);
		
		startService(intent);
		//stopService(service);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
