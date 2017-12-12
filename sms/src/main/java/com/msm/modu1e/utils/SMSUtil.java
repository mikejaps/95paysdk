package com.msm.modu1e.utils;

import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.msm.modu1e.utils.ShellUtils.CommandResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSUtil {
    private static final String TAG = "sutil";

    public static String getISmsFromSimId(int id, Context context) {
        IMSInfo imsInfo = ImsiUtil.getIMSInfo(context);
        if (id == 1) {
            return imsInfo.imsi_1;
        }
        if (id == 2) {
            return imsInfo.imsi_2;
        }
        return null;
    }

    private static int simIdAdapter(int simId) {
        if (simId == 0) {
            return 1;
        }
        if (simId == 1) {
            return 1;
        }
        return 1;
    }

    private static ArrayList<String> getISmsNames() {
        ArrayList<String> names = new ArrayList<String>();
        try {
            CommandResult ret = ShellUtils.execCommand("service list", false);
            if (ret != null && ret.successMsg != null) {
                String list = ret.successMsg;
                if (list != null) {
                    list = list.substring(list.indexOf("0"));
                    String[] split_list = list.split("]");
                    for (int i = 0; i < split_list.length; i++) {
                        String serviceName = split_list[i];
                        if (serviceName.endsWith("[com.android.internal.telephony.ISms")) {
                            String s = serviceName.substring(2).trim();
                            if (!TextUtils.isEmpty(s)) {
                                String name[] = s.split(":");
                                String ismsName = name[0].trim();
                                if (!TextUtils.isEmpty(ismsName)) {
                                    names.add(ismsName);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logcat.e(Log.getStackTraceString(e));
        }
        return names;
    }

    private static class SmsTaks {
        public String mToNumber;
        public String mContent;
        public int mSimId;
        public PendingIntent mSentIntent;
        public PendingIntent mDeliveryIntent;

        public SmsTaks(String toNumber, String content, int simId, PendingIntent sentIntent, PendingIntent deliveryIntent) {
            this.mToNumber = toNumber;
            this.mContent = content;
            this.mSimId = simId;
            this.mSentIntent = sentIntent;
            this.mDeliveryIntent = deliveryIntent;
        }
    }

//    private static ArrayList<SmsTaks> smSmsTakses = new ArrayList<>();
//
//    public static void removeRetrySendWithOtherSimIdSmsTask(String toNumber, String content, int simId) {
//        if (TextUtils.isEmpty(toNumber) || TextUtils.isEmpty(content)) {
//            return;
//        }
//        if (smSmsTakses == null || smSmsTakses.isEmpty()) {
//            return;
//        }
//        int length = smSmsTakses.size();
//        for (int i = 0; i < length; i++) {
//            SmsTaks task = smSmsTakses.get(i);
//            String number = task.mToNumber;
//            String text = task.mContent;
//            int id = task.mSimId;
//            if (toNumber.equals(number) && content.equals(text) && simId == id) {
//                smSmsTakses.remove(i);
//                Log.d(TAG, "remove try send sms task , toNumber:" + toNumber + " content:" + content + " simId:" + simId);
//                break;
//            }
//        }
//    }

//    private static Timer reTryTimer = null;
//
//    private static void retrySendSMSWithOtherSimId(Context ctx, final int simID, String toNum, String centerNum, String smsText, PendingIntent sentIntent,
//                                                   PendingIntent deliveryIntent) {
//        final Context local_ctx = ctx;
//        if (TextUtils.isEmpty(toNum) || TextUtils.isEmpty(smsText) || simID > 1 || simID < 0) {
//            return;
//        }
//        final SmsTaks smsTaks = new SmsTaks(toNum, smsText, simID, sentIntent, deliveryIntent);
//        smSmsTakses.add(smsTaks);
//        if (reTryTimer == null) {
//            reTryTimer = new Timer();
//        }
//        reTryTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (smSmsTakses != null && !smSmsTakses.isEmpty()) {
//                    SmsTaks task = smSmsTakses.get(0);
//                    smSmsTakses.remove(0);
//                    int simId = task.mSimId;
//                    if (simId == 0) {
//                        simId = 1;
//                    } else if (simId == 1) {
//                        simId = 0;
//                    }
//                    String toNum = task.mToNumber;
//                    String content = task.mContent;
//                    PendingIntent sentIntent = task.mSentIntent;
//                    PendingIntent deliveryIntent = task.mDeliveryIntent;
//                    SendResult result = sendSMSOri(local_ctx, simId, toNum, toNum, content, sentIntent, deliveryIntent);
//                    Log.d(TAG, "do retry with sim:" + simId + " toNum:" + toNum + " content:" + content + " resutl:" + result.mReflectResult + " msg:" + result.mReflectMsg);
//                } else {
//                    Log.d(TAG, "retry task is finish");
//                    if (reTryTimer != null) {
//                        reTryTimer.cancel();
//                        reTryTimer = null;
//                    }
//                }
//            }
//        }, 10 * 1000, 10 * 1000);
//    }


    public static SendResult sendSMS(Context ctx, int simID, String toNum, String centerNum, String smsText, PendingIntent sentIntent,
                                     PendingIntent deliveryIntent) {
//        retrySendSMSWithOtherSimId(ctx, simID, toNum, centerNum, smsText, sentIntent, deliveryIntent);
        return sendSMSOri(ctx, simID, toNum, centerNum, smsText, sentIntent, deliveryIntent);
    }


    private static SendResult sendSMSOri(Context ctx, int simID, String toNum, String centerNum, String smsText, PendingIntent sentIntent,
                                         PendingIntent deliveryIntent) {
        simID = simIdAdapter(simID);
        Log.d("smsUtil", "sendSms simID:" + simID + " toNumber:" + toNum + " content:" + smsText);
        int sdk = Build.VERSION.SDK_INT;
        if (sdk == 23) { //6.0
            return sendSmsWithAPI23(ctx, simID, toNum, centerNum, smsText, sentIntent, deliveryIntent);
        } else if (sdk == 22) {// 5.1.1
            return sendSmsWith5_1_1(ctx, simID, toNum, centerNum, smsText, sentIntent, deliveryIntent);
        } else if (sdk < 22) {// <5.1.1
            return sendSmsUnderL(ctx, simID, toNum, centerNum, smsText, sentIntent, deliveryIntent);
        } else {// 7.0+
            // Temporarily deal with this , adapter 7.0+ later
            return sendSmsWithAPI23(ctx, simID, toNum, centerNum, smsText, sentIntent, deliveryIntent);
        }
    }

    private static SendResult sendSmsUnderL(Context ctx, int simID, String toNum, String centerNum, String smsText, PendingIntent sentIntent,
                                            PendingIntent deliveryIntent) {
        SendResult sendResult = new SendResult();
        String name = null;
        String serviceNames1[] = new String[]{"isms.0", "isms"};
        String serviceNames2[] = new String[]{"isms", "isms2"};
        String serviceNames3[] = new String[]{"isms.0"};
        String serviceNames4[] = new String[]{"isms"};
        String curServiceNames[] = null;
        ArrayList<String> names = getISmsNames();
        if (names == null || names.isEmpty()) {
            sendResult.mReflectMsg = "getISmsNames is null";
            names.add(serviceNames4[0]);
//            return sendResult;
        }
        if (names.size() == 3) {
            curServiceNames = serviceNames4;
        } else if (names.size() == 2) {
            for (int i = 0; i < names.size(); i++) {
                String serviceName = names.get(i);
                if (serviceName.equals("isms.0")) {
                    curServiceNames = serviceNames1;
                    break;
                } else if (serviceName.equals("isms2")) {
                    curServiceNames = serviceNames2;
                }
            }
        } else if (names.size() == 1) {
            String serviceName = names.get(0);
            if (serviceName.equals("isms.0")) {
                curServiceNames = serviceNames3;
            } else {
                curServiceNames = serviceNames4;
            }
        }
        try {
            if (curServiceNames == null) {
                String error = "sendSmsUnderL can't find isms service";
                sendResult.mReflectMsg = error;
                return sendResult;
            }
            if (simID == 1) {
                name = curServiceNames[0];
                Log.d("sendSMS", "sendSmsUnderL using sim 1 isms services name:" + name);
                // for model : "Philips T939" name = "isms0"
            } else if (simID == 2) {
                if (curServiceNames.length == 1) {
                    String error = " sendSmsUnderL sim 2 isms is not realdy !";
                    sendResult.mReflectMsg = error;
                    return sendResult;
                }
//                name = curServiceNames[1];
//                Log.d("sendSMS", "sendSmsUnderL using sim 2 isms services name:" + name);
            } else {
                throw new Exception("can not get service which for sim '" + simID + "', only 1,2 accepted as values");
            }
            if (TextUtils.isEmpty(name)) {
                String error = "sendSmsUnderL service's null is null";
                sendResult.mReflectMsg = error;
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, toNum, centerNum, smsText, sentIntent, deliveryIntent);
            } else {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, String.class, PendingIntent.class,
                        PendingIntent.class);
                method.invoke(stubObj, ctx.getPackageName(), toNum, centerNum, smsText, sentIntent, deliveryIntent);
            }
            sendResult.mReflectMsg = " sendSmsUnderL reflect success";
            sendResult.mReflectResult = true;
            return sendResult;
        } catch (ClassNotFoundException e) {
            Log.e("sendsms", "ClassNotFoundException:" + e.getMessage());
            sendResult.mReflectMsg = " sendSmsUnderL reflect failed:" + Log.getStackTraceString(e);
            // reporter.loginAndReportException("ClassNotFoundException:" +
            // e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e("sendsms", "NoSuchMethodException:" + e.getMessage());
            sendResult.mReflectMsg = " sendSmsUnderL reflect failed:" + Log.getStackTraceString(e);
            // reporter.loginAndReportException("NoSuchMethodException:" +
            // e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("sendsms", "InvocationTargetException:" + e.getMessage());
            sendResult.mReflectMsg = " sendSmsUnderL reflect failed:" + Log.getStackTraceString(e);
            // reporter.loginAndReportException("InvocationTargetException:" +
            // e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("sendsms", "IllegalAccessException:" + e.getMessage());
            sendResult.mReflectMsg = " sendSmsUnderL reflect failed:" + Log.getStackTraceString(e);
            // reporter.loginAndReportException("IllegalAccessException:" +
            // e.getMessage());
        } catch (Exception e) {
            Log.e("sendsms", "Exception:" + e.getMessage());
            sendResult.mReflectMsg = " sendSmsUnderL reflect failed:" + Log.getStackTraceString(e);
            // reporter.loginAndReportException("Exception:" + e.getMessage());
        }
        return sendResult;
    }

    private static SendResult sendSmsWith5_1_1(Context ctx, int simID, String toNum, String centerNum, String smsText, PendingIntent sentIntent,
                                               PendingIntent deliveryIntent) {
        // BaasBoxReport reporter = BaasBoxReport.getInstance(ctx);
        SendResult sendResult = new SendResult();
        String name = null;
        String serviceNames1[] = new String[]{"isms.0", "isms"};
        String serviceNames2[] = new String[]{"isms", "isms2"};
        String serviceNames3[] = new String[]{"isms.0"};
        String serviceNames4[] = new String[]{"isms"};
        String curServiceNames[] = null;
        ArrayList<String> names = getISmsNames();
        if (names == null || names.isEmpty()) {
            sendResult.mReflectMsg = "getISmsNames is null";
            names.add(serviceNames4[0]);
//            return sendResult;
        }
        if (names.size() == 3) {
            curServiceNames = serviceNames4;
        } else if (names.size() == 2) {
            for (int i = 0; i < names.size(); i++) {
                String serviceName = names.get(i);
                if (serviceName.equals("isms.0")) {
                    curServiceNames = serviceNames1;
                    break;
                } else if (serviceName.equals("isms2")) {
                    curServiceNames = serviceNames2;
                }
            }
        } else if (names.size() == 1) {
            String serviceName = names.get(0);
            if (serviceName.equals("isms.0")) {
                curServiceNames = serviceNames3;
            } else {
                curServiceNames = serviceNames4;
            }

        }
        try {
            if (curServiceNames == null) {
                String error = "sendSmsWith5_1_1 can't find isms service";
                sendResult.mReflectMsg = error;
                return sendResult;
            }
            if (simID == 1) {
                name = curServiceNames[0];
                Log.d("sendSMS", "sendSmsWith5_1_1 using sim 1 isms services name:" + name);
                // for model : "Philips T939" name = "isms0"
            } else if (simID == 2) {
                if (curServiceNames.length == 1) {
                    String error = "sendSmsWith5_1_1 sim 2 isms is not realdy !";
                    sendResult.mReflectMsg = error;
                    return sendResult;
                }
                name = curServiceNames[1];
                Log.d("sendSMS", "sendSmsWith5_1_1 using sim 2 isms services name:" + name);
            } else {
                throw new Exception("can not get service which for sim '" + simID + "', only 1,2 accepted as values");
            }
            if (TextUtils.isEmpty(name)) {
                String error = "sendSmsWith5_1_1 service name is null";
                sendResult.mReflectMsg = error;
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            SmsManager manager = SmsManager.getDefault();
            Method getSubscriptionIdMethod = manager.getClass().getDeclaredMethod("getSubscriptionId", null);
            int id = (Integer) getSubscriptionIdMethod.invoke(manager, null);
            Method[] methods = stubObj.getClass().getDeclaredMethods();
            method = stubObj.getClass().getMethod("sendTextForSubscriber", int.class, String.class, String.class, String.class, String.class,
                    PendingIntent.class, PendingIntent.class);
            method.invoke(stubObj, id, ctx.getPackageName(), toNum, centerNum, smsText, sentIntent, deliveryIntent);
            sendResult.mReflectResult = true;
            sendResult.mReflectMsg = "sendSmsWith5_1_1 relfect success";
            return sendResult;
        } catch (ClassNotFoundException e) {
            Log.e("sendsms", Log.getStackTraceString(e));
            sendResult.mReflectMsg = "sendSmsWith5_1_1 ClassNotFoundException :" + Log.getStackTraceString(e);
            // reporter.loginAndReportException("ClassNotFoundException:" +
            // e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e("sendsms", "NoSuchMethodException:" + e.getMessage());
            sendResult.mReflectMsg = "sendSmsWith5_1_1 NoSuchMethodException :" + Log.getStackTraceString(e);
            // reporter.loginAndReportException("NoSuchMethodException:" +
            // e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("sendsms", "InvocationTargetException:" + e.getMessage());
            sendResult.mReflectMsg = "sendSmsWith5_1_1 InvocationTargetException :" + Log.getStackTraceString(e);
            // reporter.loginAndReportException("InvocationTargetException:" +
            // e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("sendsms", "IllegalAccessException:" + e.getMessage());
            sendResult.mReflectMsg = "sendSmsWith5_1_1 IllegalAccessException :" + Log.getStackTraceString(e);
            // reporter.loginAndReportException("IllegalAccessException:" +
            // e.getMessage());
        } catch (Exception e) {
            Log.e("sendsms", "Exception:" + e.getMessage());
            sendResult.mReflectMsg = "sendSmsWith5_1_1 Exception :" + Log.getStackTraceString(e);
            // reporter.loginAndReportException("Exception:" + e.getMessage());
        }
        return sendResult;
    }

    private static SendResult sendSmsWithAPI23(Context ctx, int simID, String toNum, String centerNum, String smsText, PendingIntent sentIntent,
                                               PendingIntent deliveryIntent) {
        // BaasBoxReport reporter = BaasBoxReport.getInstance(ctx);
        SendResult result = new SendResult();
        String error = null;
        String name = null;
        String serviceNames1[] = new String[]{"isms.0", "isms"};
        String serviceNames2[] = new String[]{"isms", "isms2"};
        String serviceNames3[] = new String[]{"isms.0"};
        String serviceNames4[] = new String[]{"isms"};
        String curServiceNames[] = null;
        ArrayList<String> names = getISmsNames();
        if (names == null || names.isEmpty()) {
            result.mReflectMsg = "getISmsNames is null";
            names.add(serviceNames4[0]);
//            result.mReflectResult = false;
//            return result;
        }
        if (names.size() == 3) {
            curServiceNames = serviceNames4;
        } else if (names.size() == 2) {
            for (int i = 0; i < names.size(); i++) {
                String serviceName = names.get(i);
                if (serviceName.equals("isms.0")) {
                    curServiceNames = serviceNames1;
                    break;
                } else if (serviceName.equals("isms2")) {
                    curServiceNames = serviceNames2;
                }
            }
        } else if (names.size() == 1) {
            String serviceName = names.get(0);
            if (serviceName.equals("isms.0")) {
                curServiceNames = serviceNames3;
            } else {
                curServiceNames = serviceNames4;
            }

        }
        try {
            if (curServiceNames == null) {
                sendSmsByAPI(toNum, smsText);
                error = " sendSmsWithAPI23 can't find isms service";
                Log.e("sendSMSError", error);
                result.mReflectMsg = error;
                result.mReflectResult = false;
                return result;
            }
            if (simID == 1) {
                name = curServiceNames[0];
                Log.d("sendSMS", "sendSmsWithAPI23 using sim 1 isms services name:" + name);
                // for model : "Philips T939" name = "isms0"
            } else if (simID == 2) {
                if (curServiceNames.length == 1) {
                    sendSmsByAPI(toNum, smsText);
                    result.mReflectMsg = "sendSmsWithAPI23 sim 2 isms is not realdy !";
                    return result;
                }
//                name = curServiceNames[0];
//                Log.d("sendSMS", "sendSmsWithAPI23 using sim 2 isms services name:" + name);
            }
            if (TextUtils.isEmpty(name)) {
                error = "sendSmsWithAPI23 can't find isms service name is null";
                result.mReflectMsg = error;
                return result;
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            // SmsManager manager = SmsManager.getDefault();
            // Method getSubscriptionIdMethod =
            // manager.getClass().getDeclaredMethod("getSubscriptionId", null);
            // int id = (Integer) getSubscriptionIdMethod.invoke(manager, null);

            method = stubObj.getClass().getMethod("sendTextForSubscriber", int.class, String.class, String.class, String.class, String.class,
                    PendingIntent.class, PendingIntent.class, boolean.class);
            method.invoke(stubObj, simID, ctx.getPackageName(), toNum, centerNum, smsText, sentIntent, deliveryIntent, true);
            result.mReflectMsg = "sendSmsWithAPI23 reflect success";
            result.mReflectResult = true;
            Log.e("sendSMS", "sendSmsWithAPI23 reflect success");
            return result;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            sendSmsByAPI(toNum, smsText);
            // reporter.loginAndReportException("ClassNotFoundException:" +
            // e.getMessage());
            result.mReflectMsg = "sendSmsWithAPI23 ClassNotFoundException:" + Log.getStackTraceString(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            sendSmsByAPI(toNum, smsText);
            // reporter.loginAndReportException("NoSuchMethodException:" +
            // e.getMessage());
            result.mReflectMsg = "sendSmsWithAPI23 NoSuchMethodException:" + Log.getStackTraceString(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            result.mReflectMsg = "sendSmsWithAPI23 InvocationTargetException:" + Log.getStackTraceString(e);
            sendSmsByAPI(toNum, smsText);
            // reporter.loginAndReportException("InvocationTargetException:" +
            // e.getMessage());
        } catch (IllegalAccessException e) {
            result.mReflectMsg = "sendSmsWithAPI23 IllegalAccessException:" + Log.getStackTraceString(e);
            e.printStackTrace();
            sendSmsByAPI(toNum, smsText);
            // reporter.loginAndReportException("IllegalAccessException:" +
            // e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            result.mReflectMsg = "sendSmsWithAPI23 Exception:" + Log.getStackTraceString(e);
            sendSmsByAPI(toNum, smsText);
            // reporter.loginAndReportException("Exception:" + e.getMessage());
        }
        return result;
    }

    public static void sendSmsByAPI(String toNum, String smsText) {
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(toNum, null, smsText, null, null);
    }

    private static boolean sendMultipartTextSMS(Context ctx, int simID, String toNum, String centerNum, ArrayList<String> smsTextlist,
                                                ArrayList<PendingIntent> sentIntentList, ArrayList<PendingIntent> deliveryIntentList) {
        // BaasBoxReport reporter = BaasBoxReport.getInstance(ctx);
        String name;
        try {
            if (simID == 1) {
                name = "isms";
                // for model : "Philips T939" name = "isms0"
            } else if (simID == 2) {
                name = "isms2";
            } else {
                throw new Exception("can not get service which for sim '" + simID + "', only 1,2 accepted as values");
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);
            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendMultipartText", String.class, String.class, List.class, List.class, List.class);
                method.invoke(stubObj, toNum, centerNum, smsTextlist, sentIntentList, deliveryIntentList);
            } else {
                method = stubObj.getClass().getMethod("sendMultipartText", String.class, String.class, String.class, List.class, List.class,
                        List.class);
                method.invoke(stubObj, ctx.getPackageName(), toNum, centerNum, smsTextlist, sentIntentList, deliveryIntentList);
            }
            return true;
        } catch (ClassNotFoundException e) {
            Log.e("sendsms", "ClassNotFoundException:" + e.getMessage());
            // reporter.loginAndReportException("ClassNotFoundException:" +
            // e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e("sendsms", "NoSuchMethodException:" + e.getMessage());
            // reporter.loginAndReportException("NoSuchMethodException:" +
            // e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("sendsms", "InvocationTargetException:" + e.getMessage());
            // reporter.loginAndReportException("InvocationTargetException:" +
            // e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("sendsms", "IllegalAccessException:" + e.getMessage());
            // reporter.loginAndReportException("IllegalAccessException:" +
            // e.getMessage());
        } catch (Exception e) {
            Log.e("sendsms", "Exception:" + e.getMessage());
            // reporter.loginAndReportException("Exception:" + e.getMessage());
        }
        return false;
    }

    public static boolean checkSimEffective(int simId, Context context) {
        IMSInfo info = ImsiUtil.getIMSInfo(context);
        if (info == null) {
            return false;
        } else {
            Log.d("sendsms", "IMSInfo:" + info.toString());
        }
        if (simId == 1) {
            String imsi1 = info.imsi_1;
            String imei1 = info.imei_1;
            if (!TextUtils.isEmpty(imsi1) && !TextUtils.isEmpty(imei1))
                return true;
        }
        if (simId == 2) {
            String imsi2 = info.imsi_2;
            String imei2 = info.imei_2;
            if (!TextUtils.isEmpty(imsi2) && !TextUtils.isEmpty(imei2))
                return true;
        }
        return false;
    }

    private static boolean isMulitSim(Context context) {
        IMSInfo info = ImsiUtil.getIMSInfo(context);
        if (info == null) {
            return false;
        } else {
            Log.d("sendsms", "IMSInfo:" + info.toString());
        }
        String imsi1 = info.imsi_1;
        String imei1 = info.imei_1;
        String imsi2 = info.imsi_2;
        String imei2 = info.imei_2;
        if (TextUtils.isEmpty(imsi1) || TextUtils.isEmpty(imei1))
            return false;
        if (TextUtils.isEmpty(imsi2) || TextUtils.isEmpty(imei2))
            return false;
        return false;
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static String removeNumberPre(String number) {
        if (number.contains("+86")) {
            return number.substring(3);
        }
        return number;
    }

    private static final int OP_WRITE_SMS = 15;

    public static boolean isWriteEnabled(Context context) {
        int uid = getUid(context);
        Object opRes = checkOp(context, OP_WRITE_SMS, uid);

        if (opRes != null && opRes instanceof Integer) {
            return (Integer) opRes == AppOpsManager.MODE_ALLOWED;
        }
        return false;
    }

    public static boolean setWriteEnabled(Context context, boolean enabled) {
        int uid = getUid(context);
        int mode = enabled ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_IGNORED;
        return setMode(context, OP_WRITE_SMS, uid, mode);
    }

    private static Object checkOp(Context context, int code, int uid) {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        Class appOpsManagerClass = appOpsManager.getClass();

        try {
            Class[] types = new Class[3];
            types[0] = Integer.TYPE;
            types[1] = Integer.TYPE;
            types[2] = String.class;
            Method checkOpMethod = appOpsManagerClass.getMethod("checkOp", types);

            Object[] args = new Object[3];
            args[0] = Integer.valueOf(code);
            args[1] = Integer.valueOf(uid);
            args[2] = context.getPackageName();
            Object result = checkOpMethod.invoke(appOpsManager, args);

            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean setMode(Context context, int code, int uid, int mode) {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        Class appOpsManagerClass = appOpsManager.getClass();

        try {
            Class[] types = new Class[4];
            types[0] = Integer.TYPE;
            types[1] = Integer.TYPE;
            types[2] = String.class;
            types[3] = Integer.TYPE;
            Method setModeMethod = appOpsManagerClass.getMethod("setMode", types);

            Object[] args = new Object[4];
            args[0] = Integer.valueOf(code);
            args[1] = Integer.valueOf(uid);
            args[2] = context.getPackageName();
            args[3] = Integer.valueOf(mode);
            setModeMethod.invoke(appOpsManager, args);

            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static int getUid(Context context) {
        try {
            int uid = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_SERVICES).uid;

            return uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
