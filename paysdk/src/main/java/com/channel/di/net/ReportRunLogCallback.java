package com.channel.di.net;

import android.text.TextUtils;
import android.util.Log;

import com.gandalf.daemon.utils.XL_log;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

public class ReportRunLogCallback extends Callback<String> {
    private static final XL_log log = new XL_log(ReportRunLogCallback.class);

    @Override
    public void onError(Call call, Exception exception, int code) {
        if (exception != null) {
            log.error("ReportRunLogCallback:" + Log.getStackTraceString(exception) + " code:" + code);
        } else {
            log.error("ReportRunLogCallback exception and exception is null");
        }
    }

    @Override
    public String parseNetworkResponse(Response response, int code) throws Exception {
//        log.debug(" ReportRunLogCallback parseNetworkResponse:");
        if (response == null)
            return null;
        String resoneStr = response.body().string();
        if (!TextUtils.isEmpty(resoneStr) && resoneStr.contains("ok")) {
            log.debug("report run log success");
        } else {
            log.error("report run log failed ,msg:" + resoneStr);
        }
        return null;
    }

    @Override
    public void onResponse(String kpa, int arg1) {
        // Logcat.d(" onResponse:" + kpa.toString());
        // TODO Auto-generated method stub
    }
}
