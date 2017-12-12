package com.channel.di.entry;

import java.io.Serializable;

public class DownloadTask implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String mDownloadUrl;
    public String mMd5;
//	public int mJarVersion;


    public DownloadTask(String downloadUrl, String md5) {
        this.mDownloadUrl = downloadUrl;
        this.mMd5 = md5;
    }

    @Override
    public String toString() {
        return "DownloadTask [mDownloadUrl=" + mDownloadUrl + ", mMd5=" + mMd5 + "]";
    }

}
