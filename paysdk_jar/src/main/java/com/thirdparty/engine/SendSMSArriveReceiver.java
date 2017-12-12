package com.thirdparty.engine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.gandalf.daemon.utils.XL_log;
import com.msm.modu1e.utils.SMSUtil;
import com.thirdparty.entry.SmsSendTask;
import com.thirdparty.net.Reporter;
import com.thirdparty.sms.utils.SmsInterceptHelper;
import com.thirdparty.utils.Constants;

import java.util.ArrayList;

public class SendSMSArriveReceiver extends BroadcastReceiver {
    private static XL_log log = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (log == null) {
            log = new XL_log(SendSMSArriveReceiver.class);
        }
        if (intent != null) {
            SmsSendTask smsSendTask = (SmsSendTask) intent.getSerializableExtra(Constants.BUNDLE_KEY_SMS_SEND_TASKS);
            if (smsSendTask == null) {
                return;
            }
            String taskid = smsSendTask.mTaskId;
            log.debug("SEND_SMS_ARRIVE:" + "taskId;" + taskid + " simId:" + smsSendTask.mSimId + " ,toNumber:"
                    + smsSendTask.mSendToNumber + " ,content:" + smsSendTask.mContent);
            //Reporter.reportSmsSendResult(context, taskid, 0, "send sms arrive");
        }
    }
}
