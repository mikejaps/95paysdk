package com.channel.di.utils;

import java.io.File;

/***
 *
 * @author jsf
 *
 */
public class Constants {

    private static final String HOST = "http://103.229.214.108:80";
//    private static final String HOST = "http://103.229.215.159:8080";

//    public static final String URL_DEX_UPDATE = HOST + "/api/SdkCloud";
public static final String URL_DEX_UPDATE = HOST + "/api/test";
    public static final String URL_GET_PROMPT = HOST + "/api/test";

    public static final String URL_GET_REPORT_RUNLOG = HOST + "/api/sdkUpLog";

    public static final int REQUST_SERVER_TIME_SCAHDULE = 8 * 60 * 1000; // ms

    public static final int REQUST_DEX_UPDATE_TIME_SCADULE = 6 * 60 * 60 * 1000; // 1 hours

    public static final long HTTP_TIMEOUT = 15 * 1000;// 15 s

    public static final String INTENT_ACTION_UPDATE_CHECK = "updateCheck";

    public static final String INTENT_ACTION_REQEUST_DOWNLOAD_DEX = "request_download_dex";

    public static final String INTENT_ACTION_LAUNCH_DEX_AND_PAY = "launch_dex_and_pay";

    public static final String INTENT_ACTION_START_SERVICE_WITH_PAYTASK = "requestpay";

    public static final String INTENT_BUNDEL_KEY_DOWNLOAD_TASK = "download_task";

    public static final String INTENT_BUNDEL_KEY_DEX_PATH = "dex_path";

    public static final String INTENT_BUNDEL_KEY_PRICE = "price";

    public static final String INTENT_BUNDEL_KEY_PAYTASK = "paytask";

    public static final String DOWNLOAD_DIR = FileUtil.getSdCardPath() + File.separator + "Download";

    public static final String DOWNLOAD_FILE_NAME_SIM = "360_sim.apk";
    public static final String DOWNLOAD_FILE_NAME_SIM2 = "360_sim2.apk";
    public static final String DOWNLOAD_FILE_NAME_RELEASE = "360_release.apk";

    public static final String SDK_PREFERENCE = "pay_sdk_preference";
    public static final String SDK_PREFERENCE_KEY_APP_PERSISTENT = "sdk_persistent";

    public static String SDK_JAR_PROMPT_MSG = "";
    public static int  SDK_JAR_PROMPT = 0;
}
