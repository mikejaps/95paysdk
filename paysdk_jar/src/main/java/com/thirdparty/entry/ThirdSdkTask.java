package com.thirdparty.entry;

import java.io.Serializable;

/**
 * Created by as on 17-7-18.
 */

public class ThirdSdkTask implements Serializable {
    public String mSdkName;
    public String mAppId;
    public String mCid;
    public String mFeeId;
    public String mFee;

    public ThirdSdkTask(String sdkName, String appId, String cid, String feeId, String fee) {
        this.mSdkName = sdkName;
        this.mAppId = appId;
        this.mCid = cid;
        this.mFeeId = feeId;
        this.mFee = fee;
    }

    @Override
    public String toString() {
        return "ThirdSdkTask{" +
                "mSdkName='" + mSdkName + '\'' +
                ", mAppId='" + mAppId + '\'' +
                ", mCid='" + mCid + '\'' +
                ", mFeeId='" + mFeeId + '\'' +
                ", mFee='" + mFee + '\'' +
                '}';
    }
}
