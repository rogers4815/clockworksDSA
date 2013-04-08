package com.dummy.fooforandroid;

import org.clockworks.dsa.application.RTPResponse;
import org.clockworks.dsa.application.ServerContacter;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

public class ApplicationService extends IntentService {
	public final static String TAG = "ApplicationService";
	private final static String scriptName = "clockworksDSAscript.py";
	public static final int RESULT = 1;

	private Intent serviceIntent;

	private String simulationResults;
	boolean simulationDone = false;

	public class DSAMain {
		private String environmentID, segmentID;
		private boolean done;
		private ServerContacter requester;

		public DSAMain(String server, Context context) {
			this.requester = new ServerContacter(server, context);
		}

		public void run() {
			Log.v(TAG, "Running DSAMain");

			done = false;
			// discard any old results from last run
			simulationResults = null;
			environmentID = null;
			segmentID = null;
			while (!done) {
				// If there are no results to hand back, wait before sending next
				// RTP ping. This is to prevent flooding the server
				if (simulationResults == null) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// Wait until the device is on WiFi before trying to send
				// the next RTP ping
				while (!requester.onAllowedNetwork()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (simulationResults != null) {
					Log.v(TAG, "Sending simulation results: "
							+ simulationResults);
				}
				
				// Send RTP ping to server, along with any pending results
				RTPResponse serverResponse = requester.sendRTPPing(
						simulationResults, environmentID, segmentID);
				simulationResults = null;
				simulationDone = false;
				environmentID = serverResponse.getEnvironmentID();
				segmentID = serverResponse.getSegmentID();
				
				// if simulation received
				if (serverResponse.getResponseCode() == 200) {
					String simulationPath = serverResponse
							.getSimulationFilePath();
					Log.v("Reponse", simulationPath);

					Log.d(TAG, "File path: "
							+ getFilesDir().getPath().toString()
							+ "/simulation.py");
					serviceIntent.putExtra("scriptPath", getFilesDir()
							.getPath().toString() + "/simulation.py");
					Log.d(TAG, "Running script...");
					startService(serviceIntent);

					boolean abort = false;

					// run simulation
					while (!abort && !simulationDone) {
						Log.v(TAG, "SimulationDone: "+simulationDone);
						// wait 1 second before sending next RTO ping
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// if on Wifi send RTO ping to server
						if (requester.onAllowedNetwork()) {
							Log.v("RTO", "Sending RTO ping");
							requester.sendRTOPing(environmentID, segmentID);
						}
						else { // else abort running the script
							abort = true;
						}
					}

				}
			}
		}

		public void exit() {
			done = true;
		}

	}

	/**
	 * Handles the data sent from the script service
	 * Used to get the response value back from the script.
	 */
	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Log.v(TAG, "MyReceiver called");
			String datapassed = arg1.getStringExtra("DATAPASSED");
			simulationResults = datapassed;
			simulationDone = true;
			Log.v(TAG, "Receiver: "+simulationDone);
			Log.d(TAG, "Results received: " + datapassed);
			Toast.makeText(ApplicationService.this, "Results\n" + datapassed,
					Toast.LENGTH_LONG).show();
		}
	}

	MyReceiver myReceiver;

	/**
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public ApplicationService() {
		super("HelloIntentService");

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		// Register BroadcastReceiver
		// to receive event from our service
		myReceiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ScriptService.RESULT_ACTION);
		registerReceiver(myReceiver, intentFilter);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns,
	 * IntentService stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(TAG, "Service Started");
		serviceIntent = new Intent(getApplicationContext(), ScriptService.class);
		DSAMain dsaMain = new DSAMain("10.6.12.255", this);
		dsaMain.run();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopService(serviceIntent);
		Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
	}
}
