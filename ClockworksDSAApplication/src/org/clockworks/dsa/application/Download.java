package org.clockworks.dsa.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class Download extends AsyncTask<Void, Void, String> {
	ProgressDialog mProgressDialog;

	Context context;
	String url;

	public Download(Context context, String url) {
		this.context = context;

		this.url = url;

	}

	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(context, "",
				"Please wait, Download …");
	}

	protected String doInBackground(Void... params) {
		// //////////////////////
		try {

			URL url = new URL(this.url);
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

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "done";
	}

	protected void onPostExecute(String result) {
		if (result.equals("done")) {
			mProgressDialog.dismiss();
		}
	}
}