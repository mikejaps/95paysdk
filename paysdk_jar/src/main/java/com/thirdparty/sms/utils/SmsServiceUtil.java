package com.thirdparty.sms.utils;

import android.content.Context;
import android.text.TextUtils;

import com.msm.modu1e.utils.IMSInfo;
import com.msm.modu1e.utils.ImsiUtil;

public class SmsServiceUtil {
//	public static int getSimIdFromImsi(Context context, String imsi) {
//		if (TextUtils.isEmpty(imsi)) {
//			return -1;
//		}
//		IMSInfo info = ImsiUtil.getIMSInfo(context);
//		if (info != null) {
//			String imsi_1 = info.imsi_1;
//			String imsi_2 = info.imsi_2;
//			if (!TextUtils.isEmpty(imsi_1)) {
//				if (imsi.equals(imsi_1)) {
//					return 1;
//				}
//			}
//			if (!TextUtils.isEmpty(imsi_2)) {
//				if (imsi.equals(imsi_2)) {
//					return 2;
//				}
//			}
//		}
//		return -1;
//	}

	public static boolean checkSimIdEffective(int id) {
		if (id == 1 || id == 2) {
			return true;
		}
		return false;
	}
}
