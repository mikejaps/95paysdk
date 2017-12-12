package com.msm.modu1e.utils;

public class SendResult {
	public boolean mReflectResult = false;
	public String mReflectMsg = null;

	public SendResult(boolean mReflectResult, String mReflectMsg) {
		super();
		this.mReflectResult = mReflectResult;
		this.mReflectMsg = mReflectMsg;
	}

	public SendResult() {
		super();
	}

	@Override
	public String toString() {
		return "SendResult [mReflectResult=" + mReflectResult + ", mReflectMsg=" + mReflectMsg + "]";
	}

}
