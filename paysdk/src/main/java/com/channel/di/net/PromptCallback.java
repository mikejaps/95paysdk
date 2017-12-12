package com.channel.di.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.channel.di.utils.Constants;
import com.gandalf.daemon.utils.LogUtil;
import com.gandalf.daemon.utils.XL_log;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

public class PromptCallback extends Callback<String> {
    private static final XL_log log = new XL_log(PromptCallback.class);
    private Context mContext = null;

    public PromptCallback(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public void onError(Call call, Exception exception, int code) {


        if (exception != null) {
            log.error("GetPayTaskCallback:" + Log.getStackTraceString(exception) + " code:" + code);
        } else {
            log.error("GetPayTaskCallback exception and exception is null");
        }
    }

    @Override
    public String parseNetworkResponse(Response response, int code) throws Exception {
        log.debug("GetPayTaskCallback");

        if (response == null)
            return null;
        String responeBody = response.body().string();
        if (!TextUtils.isEmpty(responeBody)) {
            log.debug("respone body :" + responeBody);
            parseTaskFromRespone(responeBody);
        }
        return null;
    }

    @Override
    public void onResponse(String kpa, int arg1) {
        // TODO Auto-generated method stub
    }

    private void parseTaskFromRespone(String response) {
        if (TextUtils.isEmpty(response)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(response);
            Constants.SDK_JAR_PROMPT = obj.getInt("isPayPrompt");
            Constants.SDK_JAR_PROMPT_MSG = obj.getString("promptMsg");
        } catch (JSONException e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
        }
    }


}
