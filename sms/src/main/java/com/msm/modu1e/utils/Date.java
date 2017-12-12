package com.msm.modu1e.utils;

import android.text.format.Time;

public class Date {
	public static String getCurTime() {
		// SimpleDateFormat sDateFormat = new
		// SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");
		// String date = sDateFormat.format(new java.util.Date());

		Time localTime = new Time("Asia/Hong_Kong");
		localTime.setToNow();
		return localTime.format("%Y-%m-%d     %H:%M:%S");
	}

}
