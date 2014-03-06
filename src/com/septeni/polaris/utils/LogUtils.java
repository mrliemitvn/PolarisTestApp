package com.septeni.polaris.utils;

import android.util.Log;

public class LogUtils {

	/**
	 * Log in debug mode.
	 * 
	 * @param message
	 */
	public static void logDebug(String message) {
		StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
		Log.d(stackTraceElement.getFileName() + " in "
				+ stackTraceElement.getMethodName() + " at line: "
				+ stackTraceElement.getLineNumber(), message);
	}

	/**
	 * Log in info mode.
	 * 
	 * @param message
	 */
	public static void logInfo(String message) {
		StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
		Log.i(stackTraceElement.getFileName() + " in "
				+ stackTraceElement.getMethodName() + " at line: "
				+ stackTraceElement.getLineNumber(), message);
	}

	/**
	 * Log in error mode.
	 * 
	 * @param message
	 */
	public static void logError(String message) {
		StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
		Log.e(stackTraceElement.getFileName() + " in "
				+ stackTraceElement.getMethodName() + " at line: "
				+ stackTraceElement.getLineNumber(), message);
	}
}
