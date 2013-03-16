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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import org.googlecode.android_scripting.ForegroundService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.googlecode.android_scripting.AndroidProxy;
import com.googlecode.android_scripting.BaseApplication;
import com.googlecode.android_scripting.Constants;
import com.googlecode.android_scripting.FeaturedInterpreters;
import com.googlecode.android_scripting.Log;
import com.googlecode.android_scripting.NotificationIdFactory;
import com.googlecode.android_scripting.ScriptLauncher;
import com.googlecode.android_scripting.interpreter.Interpreter;
import com.googlecode.android_scripting.interpreter.InterpreterConfiguration;
import com.googlecode.android_scripting.jsonrpc.RpcReceiverManager;

/**
 * Loosely based on Alexey and Manuel work. A service that allows scripts and
 * the RPC server to run in the background.
 * 
 * Manages messages sent from the scripts by using sockets.
 * 
 * @author Alexey Reznichenko (alexey.reznichenko@gmail.com)
 * @author Manuel Naranjo (manuel@aircable.net)
 * @author Arnaud TANGUY
 */
public class ScriptService extends ForegroundService {

	private final static int NOTIFICATION_ID = NotificationIdFactory.create();
	private final CountDownLatch mLatch = new CountDownLatch(1);
	private final IBinder mBinder;

	private InterpreterConfiguration mInterpreterConfiguration;
	private RpcReceiverManager mFacadeManager;
	private AndroidProxy mProxy;

	private Notification mNotification = null;
	private NotificationManager mNotificationManager;

	/**
	 * Communication with the service
	 */
	private ServerSocket serverSock;
	private Socket sock;
	private Thread thrd;

	private Script mScript;

	public class LocalBinder extends Binder {
		public ScriptService getService() {
			return ScriptService.this;
		}
	}

	public ScriptService() {
		super(NOTIFICATION_ID);
		mBinder = new LocalBinder();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInterpreterConfiguration = ((BaseApplication) getApplication())
				.getInterpreterConfiguration();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("onDestroy()");

		Log.d("Closing socket connections");
		if (mProxy != null)
			mProxy.shutdown();
		Log.d("Script ended");

		try {
			if (sock != null) {
				sock.close();
				sock = null;
			}
			if (serverSock != null) {
				serverSock.close();
				serverSock = null;
			}
		} catch (IOException e) {
			Log.d(e.toString());
		}
	}

	@Override
	public void onStart(Intent intent, final int startId) {
		super.onStart(intent, startId);

		String path = intent.getStringExtra("scriptPath");
		Log.d("Script Path: " + path);
		mScript = new Script(path);
		Log.d("FileName: " + mScript.getFileName());
		Log.d("FileExtension: " + mScript.getFileExtension());

		handleNetwork();

		Interpreter interpreter = mInterpreterConfiguration
				.getInterpreterForScript(mScript.getFileName());
		if (interpreter == null || !interpreter.isInstalled()) {
			mLatch.countDown();
			if (FeaturedInterpreters.isSupported(mScript.getFileName())) {
				Log.d("Is Supported");
				Intent i = new Intent(this, DialogActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra(Constants.EXTRA_SCRIPT_PATH, mScript.getFileName());
				startActivity(i);
			} else {
				Log.e(this,
						"Cannot find an interpreter for script "
								+ mScript.getFileName());
			}
			stopSelf(startId);
			return;
		}

		File script = new File(path);
		Log.d("Launch with proxy ");

		mProxy = new AndroidProxy(this, null, true);
		mProxy.startLocal();
		mLatch.countDown();
		ScriptLauncher.launchScript(script, mInterpreterConfiguration, mProxy,
				new Runnable() {
					@Override
					public void run() {
						mProxy.shutdown();
						stopSelf(startId);
					}
				});
	}
	
	RpcReceiverManager getRpcReceiverManager() throws InterruptedException {
		mLatch.await();
		if (mFacadeManager == null) { // Facade manage may not be available on
										// startup.
			mFacadeManager = mProxy.getRpcReceiverManagerFactory()
					.getRpcReceiverManagers().get(0);
		}
		return mFacadeManager;
	}

	@Override
	protected Notification createNotification() {
		mNotification = new Notification(R.drawable.script_logo_48,
				this.getString(R.string.local_service_label),
				System.currentTimeMillis());
		// This contentIntent is a noop.
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, ScriptActivity.class), 0);
		mNotification.setLatestEventInfo(this,
				this.getString(R.string.app_name),
				this.getString(R.string.local_service_label), contentIntent);
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		return mNotification;
	}

	protected void handleNetwork() {
		try {
			if (serverSock == null) {
				serverSock = new ServerSocket(8080);

				thrd = new Thread(new Runnable() {
					private BufferedReader r;
					//private BufferedWriter out;

					public void run() {
						Log.d("Waiting for socket...");
						try {
							sock = ScriptService.this.serverSock.accept();

							Log.d("Accepted socket " + sock.getLocalAddress()
									+ " " + sock.getLocalPort());
							r = new BufferedReader(new InputStreamReader(
									sock.getInputStream()));
							//out = new BufferedWriter(new OutputStreamWriter(
							//		sock.getOutputStream()));

							while (!Thread.interrupted()) {
								final String data = r.readLine();
								if (data != null) {
									// do something in ui thread with the
									// data var
									Log.d("Data: " + data);
									updateNotification(data);
								}
							}
						} catch (IOException e) {
						}
					}
				});
				thrd.start();
			}
		} catch (IOException ioe) {
			Log.e(ioe.toString());
		}
	}

	protected void updateNotification(String newText) {
		PendingIntent contentIntent = PendingIntent.getService(
				ScriptService.this, 0, new Intent(), 0);
		mNotification.setLatestEventInfo(ScriptService.this,
				ScriptService.this.getString(R.string.app_name), newText,
				contentIntent);
		mNotificationManager.notify(ScriptService.super.getNotificationId(),
				mNotification);
	}
}
