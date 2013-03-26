package com.dummy.fooforandroid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.clockworks.dsa.application.RTPResponse;
import org.clockworks.dsa.application.ServerContacter;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ApplicationService extends IntentService {
	public final static String TAG = "ApplicationService";
	private Intent serviceIntent;

	public class DSAMain extends Thread {
		private String simulationResults, environmentID, segmentID;
		private boolean done;
		private ServerContacter requester;

		public DSAMain(String server, Context context) {
			this.requester = new ServerContacter(server, context);
		}

		public void downloadScript(String scriptUrl) throws IOException {
			URL url = new URL(scriptUrl);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			String[] path = url.getPath().split("/");
			String mp3 = path[path.length - 1];
			int lengthOfFile = c.getContentLength();

			String PATH = Environment.getExternalStorageDirectory()
					+ "/sl4a/scripts/";
			Log.v("", "PATH: " + PATH);
			File file = new File(PATH);
			file.mkdirs();

			String fileName = mp3;

			File outputFile = new File(file, fileName);
			FileOutputStream fos = new FileOutputStream(outputFile);

			InputStream is = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1) {

				fos.write(buffer, 0, len1);
			}
			fos.close();
			is.close();
		}

		public void run() {
			Log.v(TAG, "Running DSAMain");
			
			serviceIntent.putExtra("scriptPath", Environment
					.getExternalStorageDirectory().getPath()
					+ "/sl4a/scripts/returnTest.py");
			Log.d(TAG, "Running script...");
			startService(serviceIntent);

			done = false;
			// discard any old results from last run
			simulationResults = null;
			environmentID = null;
			segmentID = null;
			while (!done) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				while (!requester.onAllowedNetwork()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				RTPResponse serverResponse = requester.sendRTPPing(
						simulationResults, environmentID, segmentID);
				environmentID = serverResponse.getEnvironmentID();
				segmentID = serverResponse.getSegmentID();
				if (serverResponse.getResponseCode() == 200) {
					String simulationPath = serverResponse
							.getSimulationFilePath();
					Log.v("Reponse", simulationPath);
					// TODO Create thread for processing the python script
					// Intent serviceIntent = new Intent(null,
					// ScriptService.class);
					try {
						downloadScript(simulationPath);
					} catch (IOException e1) {
						Log.e(TAG, "Script not downloaded");
						e1.printStackTrace();
					}
					serviceIntent.putExtra("scriptPath", Environment
							.getExternalStorageDirectory().getPath()
							+ "/sl4a/scripts/returnTest.py");
					Log.d(TAG, "Running script...");
					startService(serviceIntent);

					boolean abort = false;
					// TODO while not finished processing and abort is false
					while (!abort) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (requester.onAllowedNetwork()) {
							Log.v("RTO", "Sending RTO ping");
							requester.sendRTOPing(environmentID, segmentID);
						} else {
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
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public ApplicationService() {
		super("HelloIntentService");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
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
		serviceIntent = new Intent(getApplicationContext(),
					ScriptService.class);
		DSAMain dsaMain = new DSAMain("localhost", this);
		dsaMain.run();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopService(serviceIntent);
		Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
	}
}