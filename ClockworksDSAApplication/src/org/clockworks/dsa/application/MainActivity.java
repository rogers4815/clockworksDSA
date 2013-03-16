package org.clockworks.dsa.application;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.dummy.fooforandroid.R;

public class MainActivity extends Activity {
	DSAMain go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        go = new DSAMain("10.6.12.255", this);
        go.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
