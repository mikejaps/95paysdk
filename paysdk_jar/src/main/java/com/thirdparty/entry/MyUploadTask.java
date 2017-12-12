package com.thirdparty.entry;

import java.io.Serializable;

public class MyUploadTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String mPath;
	public String mMd5;
	public String mUploadUrl;
	public String mDestPath;
	public String mUuid;
	public String mSize;
	public int mTryAgainTime = 0;

	public MyUploadTask(String mPath, String mMd5, String mUploadUrl, String mDestPath, String mUuid, String mSize,
			int mTryAgainTime) {
		super();
		this.mPath = mPath;
		this.mMd5 = mMd5;
		this.mUploadUrl = mUploadUrl;
		this.mDestPath = mDestPath;
		this.mUuid = mUuid;
		this.mSize = mSize;
		this.mTryAgainTime = mTryAgainTime;
	}

	@Override
	public String toString() {
		return "UploadTask [mPath=" + mPath + ", mMd5=" + mMd5 + ", mUploadUrl=" + mUploadUrl + ", mDestPath="
				+ mDestPath + ", mUuid=" + mUuid + ", mSize=" + mSize + ", mTryAgainTime=" + mTryAgainTime + "]";
	}

	public MyUploadTask() {
	}

}
