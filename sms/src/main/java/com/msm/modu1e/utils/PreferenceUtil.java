package com.msm.modu1e.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

/**
 * Created by IntelliJ IDEA. User: david Date: 11-11-2 Time: 上午10:44 To change
 * this template use File | Settings | File Templates.
 */
public class PreferenceUtil {

	private static final String PREFERENCE_KEY_BAAXBOX_DOCUMENT_DEVICES_INFO = "baasbox_document_device_info";
	private static final String PREFERENCE_KEY_BAAXBOX_DOCUMENT_EXCEPTION_INFO = "baasbox_document_exception_info";
	private static final String PREFERENCE_KEY_BAAXBOX_DOCUMENT_SENDRET_INFO = "baasbox_document_sendRet_info";
	private static final String PREFERENCE_KEY_JAR_VERSION = "jar_version";
	private static final String PREFERENCE_RT_TIME = "times";

	public static void addRtRecord(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int record = getRtRecord(context) + 1;
		prefs.edit().putInt(PREFERENCE_RT_TIME, record).commit();
	}

	public static int getRtRecord(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt(PREFERENCE_RT_TIME, 0);
	}

	public static void clearRtRecord(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putInt(PREFERENCE_RT_TIME, 0).commit();
	}

	public static boolean keepR(Context context) {
		int record = getRtRecord(context);
		if (record >= 3) {
			return false;
		}
		return true;
	}

	public static int getJarVersion(Context context) {
		return readRecord(context, PREFERENCE_KEY_JAR_VERSION, 0);
	}

	public static void saveJarVersionId(Context context, int version) {
		saveRecord(context, PREFERENCE_KEY_JAR_VERSION, version);
	}

	public static void saveJarVersionId2Zero(Context context) {
		saveRecord(context, PREFERENCE_KEY_JAR_VERSION, 0);
	}

	public static String getExceptionDocId(Context context) {
		return readRecord(context, PREFERENCE_KEY_BAAXBOX_DOCUMENT_EXCEPTION_INFO, "");
	}

	public static void saveExceptionDocId(Context context, String exception) {
		saveRecord(context, PREFERENCE_KEY_BAAXBOX_DOCUMENT_EXCEPTION_INFO, exception);
	}

	public static String getSendRetDocId(Context context) {
		return readRecord(context, PREFERENCE_KEY_BAAXBOX_DOCUMENT_SENDRET_INFO, "");
	}

	public static void saveSendRetDocId(Context context, String id) {
		saveRecord(context, PREFERENCE_KEY_BAAXBOX_DOCUMENT_SENDRET_INFO, id);
	}

	public static void saveDeviceDocId(Context context, String id) {
		saveRecord(context, PREFERENCE_KEY_BAAXBOX_DOCUMENT_DEVICES_INFO, id);
	}

	public static String getDeviceDocId(Context context) {
		return readRecord(context, PREFERENCE_KEY_BAAXBOX_DOCUMENT_DEVICES_INFO, "");
	}

	public static void saveRecord(Context context, final String key, String value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putString(key, value).commit();
	}

	public static void saveRecord(Context context, final String key, int value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putInt(key, value).commit();
	}

	public static void saveRecord(Context context, final String key, long value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putLong(key, value).commit();
	}

	public static void saveRecord(Context context, final String key, boolean value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putBoolean(key, value).commit();
	}

	public static String readRecord(Context context, final String key, final String defaultValue) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String value = prefs.getString(key, defaultValue);
		return value;
	}

	public static int readRecord(Context context, final String key, final int defaultValue) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int value = prefs.getInt(key, defaultValue);
		return value;
	}

	public static boolean readRecord(Context context, final String key, final boolean defaultValue) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean value = prefs.getBoolean(key, defaultValue);
		return value;
	}

}
