package org.clockworks.dsa.application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.dummy.fooforandroid.R;
import com.googlecode.android_scripting.IntentBuilders;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";

	private ServerSocket serverSock;
	private Socket sock;

	private Thread thrd;

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		Log.d(TAG, "after super.onResume");
		try {
			if (serverSock != null) {
				serverSock.close();
			}
			serverSock = new ServerSocket(8080);

			thrd = new Thread(new Runnable() {
				private BufferedReader r;
				private BufferedWriter out;

				public void run() {
					Log.d(TAG, "Waiting for socket...");
					try {
						sock = MainActivity.this.serverSock.accept();

						Log.d(TAG, "Accepted socket " + sock.getLocalAddress()
								+ " " + sock.getLocalPort());
						r = new BufferedReader(new InputStreamReader(
								sock.getInputStream()));
						out = new BufferedWriter(new OutputStreamWriter(
								sock.getOutputStream()));

						while (!Thread.interrupted()) {
							final String data = r.readLine();
							if (data != null)
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										// do something in ui thread with the
										// data var
										Log.d(TAG, "Data: " + data);
									}
								});

						}
					} catch (IOException e) {
					}
				}
			});
			thrd.start();
		} catch (IOException ioe) {
			Log.e(TAG, ioe.toString());
		}
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
		/*
		 * if (thrd != null) thrd.interrupt(); try { if (sock != null) {
		 * sock.getOutputStream().close(); sock.getInputStream().close();
		 * sock.close(); } } catch (IOException e) { } thrd = null;
		 */
	}

	/*
	 * private void sendText(String text) { try { out.write(text + "\n");
	 * out.flush(); } catch (IOException e) { } }
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("MainActivity", "onCreate()");
		testPython();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			serverSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	protected void testPython() {
		Log.d(TAG, "testPython()");
		String pythonScript = "/sdcard/sl4a/scripts/returnTest.py";
		// Intent intent = IntentBuilders.buildStartInBackgroundIntent(new File(
		// pythonScript));
		Intent intent = IntentBuilders.buildStartInTerminalIntent(new File(
				pythonScript));
		// intent.setClass(this, PythonService.class);
		Log.d("MainActivity", "The intent is " + intent.toString());

		startActivity(intent);
		// startService(intent);
		// stopService(service);
	}

}
