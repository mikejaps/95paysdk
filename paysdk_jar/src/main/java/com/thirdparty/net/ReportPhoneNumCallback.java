package com.thirdparty.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gandalf.daemon.utils.XL_log;
import com.shoujishuju.Service;
import com.shoujishuju.enums.ISPType;
import com.thirdparty.engine.ServiceStub;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

public class ReportPhoneNumCallback extends Callback<String> {
    private static final XL_log log = new XL_log(ReportPhoneNumCallback.class);
    private Context mContext = null;

    public ReportPhoneNumCallback(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public void onError(Call call, Exception exception, int code) {
        if (exception != null) {
            log.error("ReportPhoneNumCallback:" + Log.getStackTraceString(exception) + " code:" + code);
        } else {
            log.error("ReportPhoneNumCallback exception and exception is null");
        }
    }

    @Override
    public String parseNetworkResponse(Response response, int code) throws Exception {
        String responeBody = response.body().string();
        if (!TextUtils.isEmpty(responeBody)) {
            log.debug("respone body :" + responeBody);
        }
        return null;
    }

    @Override
    public void onResponse(String kpa, int arg1) {
        // TODO Auto-generated method stub
    }



}
