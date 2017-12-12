package com.thirdparty.engine;

import android.content.Context;
import android.util.Log;

import com.gandalf.daemon.utils.XL_log;
import com.wyzf.download.SdkDlm;
import com.wyzf.pay.PayResultListener;
import com.wyzf.pay.WYZFPay;

import java.io.File;
import java.io.IOException;

/**
 * Created by as on 17-7-12.
 */

public class WYpay {
    private XL_log log = new XL_log(WYpay.class);
    private static WYpay INSTANCE = null;
    private Context mContext = null;

    private WYpay(Context context) {
        this.mContext = context;
    }

    public static WYpay getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new WYpay(context);
        }
        return INSTANCE;
    }

    public void pay(String feeCode, String price, PayResultListener payResultListener) {
        int feeCodeInt = Integer.valueOf(feeCode);
        int priceInt = Integer.valueOf(price);
        WYZFPay.getInstance().pay(mContext, feeCodeInt, priceInt, payResultListener);
    }

    private static boolean INIT = false;

    public void init(String appId, String packageCode) {
        if (!INIT) {
            fixAssetBug();
            fixService();
            INIT = true;
        }
        {
            // weiyun sdk init
            SdkDlm.getInstance(mContext).init(appId, packageCode);
        }
    }

    private void fixAssetBug() {
        File fileDir = mContext.getFilesDir();
        File jarDir = new File(fileDir.getParentFile().getAbsolutePath() + File.separator + "app_Wyzf_plg");
        if (!jarDir.exists() || !jarDir.isDirectory()) {
            jarDir.mkdirs();
        }
        File jar = new File(jarDir.getAbsolutePath() + File.separator + "5.0.7.jar");
        if (!jar.exists()) {
            try {
                jar.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void fixService() {
        onCreate();
        onStartCommand();
    }

    public void onCreate() {
        try {
            log.info("InitService onCreate");
            Object a = a(mContext);
            a.getClass().getMethod("registerBroadcast", new Class[0]).invoke(a, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
        }
    }

    private void onDestroy() {
        try {
            log.info("InitService onDestory");
            Object a = a(mContext);
            a.getClass().getMethod("onDestroy", new Class[0]).invoke(a, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
        }
    }

    public void destroy() {
        onDestroy();
    }

    public int onStartCommand() {
        try {
            log.info("InitService onStartCommand");
            Object a = a(mContext);
            a.getClass().getMethod("startCommand", new Class[0]).invoke(a, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
        }
        return 0;
    }

    private static Object a;

    private Object a(Context context) {
        if (a == null) {
            try {
//                a = Class.forName(com.wyzf.util.l.a(com.wyzf.constant.Constant.o)).getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
                a = new com.wyzf.plugin.service.ServiceAction(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(Log.getStackTraceString(e));
            }
        }
        return a;
    }
}
