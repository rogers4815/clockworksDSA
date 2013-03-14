/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dummy.fooforandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Loosely based on Alexey code
 * @author Alexey Reznichenko (alexey.reznichenko@gmail.com)
 * @author Arnaud TANGUY
 */
public class ScriptActivity extends Activity {
	public static final String TAG = "ScriptActivity";

	private Intent serviceIntent = null;

	private Button startSimulationButton;
	private Button stopSimulationButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
		setContentView(R.layout.activity_main);

		serviceIntent = new Intent(ScriptActivity.this, ScriptService.class);
		serviceIntent.putExtra("scriptPath", Environment.getExternalStorageDirectory().getPath()+"/sl4a/scripts/returnTest.py");

		startSimulationButton = (Button) findViewById(R.id.startSimulationButton);
		stopSimulationButton = (Button) findViewById(R.id.stopSimulationButton);

		startSimulationButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				/*
				 * if (Constants.ACTION_LAUNCH_SCRIPT_FOR_RESULT
				 * .equals(getIntent().getAction())) {
				 * 
				 * ServiceConnection connection = new ServiceConnection() {
				 * 
				 * @Override public void onServiceConnected(ComponentName name,
				 * IBinder service) { ScriptService scriptService =
				 * ((ScriptService.LocalBinder) service) .getService(); try {
				 * RpcReceiverManager manager = scriptService
				 * .getRpcReceiverManager(); ActivityResultFacade resultFacade =
				 * manager .getReceiver(ActivityResultFacade.class);
				 * resultFacade.setActivity(ScriptActivity.this); } catch
				 * (InterruptedException e) { throw new RuntimeException(e); } }
				 * 
				 * @Override public void onServiceDisconnected(ComponentName
				 * name) { // Ignore. Log.d(TAG, "Service disconnected"); } };
				 * bindService(serviceIntent, connection,
				 * Context.BIND_AUTO_CREATE); startService(serviceIntent); }
				 * else {
				 */
				ScriptApplication application = (ScriptApplication) getApplication();
				if (application.readyToStart()) {
					startService(serviceIntent);
				}
				// }
			}
		});

		stopSimulationButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stopService(serviceIntent);
			}
		});

	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
	}
}
