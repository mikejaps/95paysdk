package com.msm.modu1e.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class ImsiUtil {
    private static Integer simId_1 = 0;
    private static Integer simId_2 = 1;
    private static String imsi_1 = "";
    private static String imsi_2 = "";
    private static String imei_1 = "";
    private static String imei_2 = "";

    /**
     * 获取IMSInfo
     *
     * @return
     */
    public static IMSInfo getIMSInfo(Context context) {
		int sdkVersion = Build.VERSION.SDK_INT;
		if (sdkVersion >= 22) {
			return getIMSIWithSDK_23(context);
		}
		if (context == null)
			return null;
		IMSInfo imsInfo = initQualcommDoubleSim(context);
		if (imsInfo != null) {
			return imsInfo;
		} else {
			imsInfo = initMtkDoubleSim(context);
			if (imsInfo != null) {
				return imsInfo;
			} else {
				imsInfo = initMtkSecondDoubleSim(context);
				if (imsInfo != null) {
					return imsInfo;
				} else {
					imsInfo = initSpreadDoubleSim(context);
					if (imsInfo != null) {
						return imsInfo;
					} else {
						return null;
					}
				}
			}
		}
    }

    /**
     * MTK的芯片的判断
     *
     * @param mContext
     * @return
     */
    private static IMSInfo initMtkDoubleSim(Context context) {
        IMSInfo imsInfo = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> c = Class.forName("com.android.internal.telephony.Phone");
            Field fields1 = c.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            simId_1 = (Integer) fields1.get(null);
            Field fields2 = c.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            simId_2 = (Integer) fields2.get(null);

            Method m = TelephonyManager.class.getDeclaredMethod("getSubscriberIdGemini", int.class);
            imsi_1 = (String) m.invoke(tm, simId_1);
            imsi_2 = (String) m.invoke(tm, simId_2);

            Method m1 = TelephonyManager.class.getDeclaredMethod("getDeviceIdGemini", int.class);
            imei_1 = (String) m1.invoke(tm, simId_1);
            imei_2 = (String) m1.invoke(tm, simId_2);

            imsInfo = new IMSInfo();
            imsInfo.chipName = "MTK chip";
            imsInfo.imei_1 = imei_1;
            imsInfo.imei_2 = imei_2;
            imsInfo.imsi_1 = imsi_1;
            imsInfo.imsi_2 = imsi_2;

        } catch (Exception e) {
            imsInfo = null;
            return imsInfo;
        }
        return imsInfo;
    }

    /**
     * MTK的芯片的判断2
     *
     * @param mContext
     * @return
     */
    private static IMSInfo initMtkSecondDoubleSim(Context context) {
        IMSInfo imsInfo = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> c = Class.forName("com.android.internal.telephony.Phone");

            Field fields1 = c.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            simId_1 = (Integer) fields1.get(null);
            Field fields2 = c.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            simId_2 = (Integer) fields2.get(null);

            Method mx = TelephonyManager.class.getMethod("getDefault", int.class);
            TelephonyManager tm1 = (TelephonyManager) mx.invoke(tm, simId_1);
            TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, simId_2);

            imsi_1 = tm1.getSubscriberId();
            imsi_2 = tm2.getSubscriberId();

            imei_1 = tm1.getDeviceId();
            imei_2 = tm2.getDeviceId();

            imsInfo = new IMSInfo();
            imsInfo.chipName = "MTK chip";
            imsInfo.imei_1 = imei_1;
            imsInfo.imei_2 = imei_2;
            imsInfo.imsi_1 = imsi_1;
            imsInfo.imsi_2 = imsi_2;

        } catch (Exception e) {
            imsInfo = null;
            return imsInfo;
        }
        return imsInfo;
    }

    /**
     * 展讯芯片的判断
     *
     * @param mContext
     * @return
     */
    private static IMSInfo initSpreadDoubleSim(Context context) {
        IMSInfo imsInfo = null;
        try {
            Class<?> c = Class.forName("com.android.internal.telephony.PhoneFactory");
            Method m = c.getMethod("getServiceName", String.class, int.class);
            String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imsi_1 = tm.getSubscriberId();
            imei_1 = tm.getDeviceId();
            TelephonyManager tm1 = (TelephonyManager) context.getSystemService(spreadTmService);
            imsi_2 = tm1.getSubscriberId();
            imei_2 = tm1.getDeviceId();
            imsInfo = new IMSInfo();
            imsInfo.chipName = "zhangxun cip";
            imsInfo.imei_1 = imei_1;
            imsInfo.imei_2 = imei_2;
            imsInfo.imsi_1 = imsi_1;
            imsInfo.imsi_2 = imsi_2;
        } catch (Exception e) {
            imsInfo = null;
            return imsInfo;
        }
        return imsInfo;
    }

    /**
     * 高通芯片判断
     *
     * @param mContext
     * @return
     */
    private static IMSInfo initQualcommDoubleSim(Context context) {
        IMSInfo imsInfo = null;
        try {
            Class<?> cx = Class.forName("android.telephony.MSimTelephonyManager");
            Object obj = context.getSystemService("phone_msim");
            Method md = cx.getMethod("getDeviceId", int.class);
            Method ms = cx.getMethod("getSubscriberId", int.class);
            imei_1 = (String) md.invoke(obj, simId_1);
            imei_2 = (String) md.invoke(obj, simId_2);
            imsi_1 = (String) ms.invoke(obj, simId_1);
            imsi_2 = (String) ms.invoke(obj, simId_2);
            int statephoneType_2 = 0;
            boolean flag = false;
            try {
                Method mx = cx.getMethod("getPreferredDataSubscription", int.class);
                Method is = cx.getMethod("isMultiSimEnabled", int.class);
                statephoneType_2 = (Integer) mx.invoke(obj);
                flag = (Boolean) is.invoke(obj);
            } catch (Exception e) {
                // TODO: handle exception
            }
            imsInfo = new IMSInfo();
            imsInfo.chipName = "gaotong chip -getPreferredDataSubscription:" + statephoneType_2 + ",flag:" + flag;
            imsInfo.imei_1 = imei_1;
            imsInfo.imei_2 = imei_2;
            imsInfo.imsi_1 = imsi_1;
            imsInfo.imsi_2 = imsi_2;

        } catch (Exception e) {
            imsInfo = null;
            return imsInfo;
        }
        return imsInfo;
    }

    /**
     * Android version >=6.0
     */
    private static IMSInfo getIMSIWithSDK_23(Context context) {
        IMSInfo imsInfo = null;
        try {
            Object subscriptionManagerObj = null;
            Class subscriptionManagerClass = Class.forName("android.telephony.SubscriptionManager");
            Method fromMethod = subscriptionManagerClass.getDeclaredMethod("from", Context.class);
            fromMethod.setAccessible(true);
            subscriptionManagerObj = fromMethod.invoke(null, context);
            if (subscriptionManagerObj != null) {
                Method getActiveSubscriptionInfoCountMethod = subscriptionManagerClass.getDeclaredMethod("getActiveSubscriptionInfoCount", null);
                getActiveSubscriptionInfoCountMethod.setAccessible(true);
                // getActiveSubscriptionInfoCount is return active sim card
                // .that' mean if the device support multi sim card and there
                // isn't active sim card null 0.
                Integer activeSimCout = (Integer) getActiveSubscriptionInfoCountMethod.invoke(subscriptionManagerObj, null);
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                Class telephonyManagerClass = tm.getClass();
                Method getSubscriberIdMethod = telephonyManagerClass.getDeclaredMethod("getSubscriberId", int.class);
                Method getDeviceIdMethod = telephonyManagerClass.getDeclaredMethod("getDeviceId", int.class);

                getSubscriberIdMethod.setAccessible(true);
                Logcat.d("Active Sim count:" + activeSimCout);
                for (int i = 1; i <= activeSimCout; i++) {
                    String subscriberId = (String) getSubscriberIdMethod.invoke(tm, i);
                    String deviceId = (String) getDeviceIdMethod.invoke(tm, i);
                    if (i == 1) {
                        imsi_1 = subscriberId;
                        imei_1 = deviceId;
                    } else if (i == 2) {
                        imsi_2 = subscriberId;
                        imei_2 = deviceId;
                    }
                }
                imsInfo = new IMSInfo();
                imsInfo.chipName = "unkonw chip";
                imsInfo.imsi_1 = imsi_1;
                imsInfo.imei_1 = imei_1;
                imsInfo.imsi_2 = imsi_2;
                imsInfo.imei_2 = imei_2;
                return imsInfo;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return null;
    }

    /**
     * 系统的api
     *
     * @return
     */
    private static String IMSI = null;

    public static String getIMSIWithAPI(Context context) {
        if (IMSI != null) {
            return IMSI;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IMSI = tm.getSubscriberId();
        if (IMSI == null) {
            IMSI = "";
        }
        return IMSI;
    }

    private static String IMEI = null;

    public static String getIMEIWithAPI(Context context) {
        if (IMEI != null) {
            return IMEI;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = tm.getDeviceId();
        if (IMEI == null) {
            IMEI = "";
        }
        return IMEI;
    }
    public static String getICCIDWithAPI(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iccid = tm.getSimSerialNumber();
        if (iccid == null) {
            iccid = "";
        }
        return iccid;
    }

}