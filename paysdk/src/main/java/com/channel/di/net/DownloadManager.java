package com.channel.di.net;

import android.content.Context;
import android.text.TextUtils;

import com.channel.di.entry.DownloadTask;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;

public class DownloadManager {

   synchronized public static void downloadDex(Context context, DownloadDexCallBack downloadDexCallBack) {
        if (downloadDexCallBack == null || downloadDexCallBack.mDownloadTask == null) {
            return;
        }
        DownloadTask downloadTask = downloadDexCallBack.mDownloadTask;
        String downloadDir = downloadDexCallBack.mDestFileDir;
        String downloadFileName = downloadDexCallBack.mDestFileName;
        if (TextUtils.isEmpty(downloadDir) || TextUtils.isEmpty(downloadFileName)) {
            return;
        }
        if (downloadTask.mDownloadUrl == null || downloadTask.mMd5 == null) {
            return;
        }
        mkdir(downloadDir);
        File dex = new File(downloadDexCallBack.mDestFileDir + File.separator + downloadFileName);
        if (dex.exists()) {
            dex.delete();
        }
        OkHttpUtils//
                .get()//
                .url(downloadTask.mDownloadUrl)//
                .build()//
                .execute(downloadDexCallBack);
    }

    private static boolean mkdir(String dir) {
        if (TextUtils.isEmpty(dir)) {
            return false;
        }
        File file = new File(dir);
        if (file.isDirectory())
            return true;
        else {
            return file.mkdir();
        }
    }
}
