// Copyright 2010 Google Inc. All Rights Reserved.

package com.dummy.fooforandroid;


public class Script {

	public String mFilePath;

	public Script(String filePath) {
		mFilePath = filePath;
	}
	
	public String getFileName() {
		String fileName = mFilePath.substring( mFilePath.lastIndexOf('/')+1, mFilePath.length() );
		return fileName;
	}

	public String getFileExtension() {
		String sFileName = getFileName();
		int dotIndex = sFileName.lastIndexOf('.');
		if (dotIndex == -1) {
			return null;
		}
		return sFileName.substring(dotIndex);
	}

}
