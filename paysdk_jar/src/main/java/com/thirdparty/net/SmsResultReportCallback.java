package com.thirdparty.net;

import okhttp3.Call;
import okhttp3.Response;

import android.util.Log;

import com.gandalf.daemon.utils.XL_log;
import com.zhy.http.okhttp.callback.Callback;

public class SmsResultReportCallback extends Callback<String> {
    private static final XL_log log = new XL_log(SmsResultReportCallback.class);

    @Override
    public void onError(Call call, Exception exception, int code) {
        if (exception != null) {
            log.error(Log.getStackTraceString(exception) + " code:" + code);
        } else {
            log.error("SmsResultReportCallback exception and exception is null");
        }
    }

    @Override
    public String parseNetworkResponse(Response response, int code) throws Exception {
//        log.debug(" SmsResultReportCallback parseNetworkResponse:");
        if (response == null)
            return null;
        String responeBody = response.body().string();
        if (responeBody != null && responeBody.contains("ok")) {
            log.debug("SmsResultReport success");
        } else {
            log.error("SmsResultReport failed");
        }
        return null;
    }

    @Override
    public void onResponse(String kpa, int arg1) {
        // Logcat.d(" onResponse:" + kpa.toString());
        // TODO Auto-generated method stub
    }

}
