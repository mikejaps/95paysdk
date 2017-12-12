package com.channel.di.net;

import android.content.Context;

import com.zhy.http.okhttp.callback.Callback;

import k.m.IPayListener;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by as on 17-6-21.
 */

public class PayCallback extends Callback<String> {
    private IPayListener mIPayListener = null;
    private Context mContext = null;

    public PayCallback(Context context, IPayListener iPayListener) {
        this.mIPayListener = iPayListener;
        mContext = context;
    }

    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
        return null;
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    @Override
    public void onResponse(String response, int id) {

    }

    private void parse(String respone) {

    }

    private void startUpdateDex(String downloadUrl) {

    }
}
