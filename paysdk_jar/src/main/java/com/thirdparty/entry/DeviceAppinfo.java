package com.thirdparty.entry;

public class DeviceAppinfo {
    public String mInstallType;
    public String mLastInstsallDate;
    public String mAppName;
    public String mPackageName;
    public String mSize;
    public String mVersionCode;
    public String mVerion;
    public String mMd5;
    public boolean mIconAble;// There is no icon

    //	public boolean mFloatWindowAble;
//	public boolean mSendSmsAble;
//	public boolean mIsRunning;
//	@Override
//	public String toString() {
//		return "DeviceAppinfo [mInstallType=" + mInstallType + ", mLastInstsallDate=" + mLastInstsallDate + ", mAppName=" + mAppName + ", mPackageName="
//				+ mPackageName + ", mSize=" + mSize + ", mVersionCode=" + mVersionCode + ", mVerion=" + mVerion + ", mMd5=" + mMd5 + ", mIconAble=" + mIconAble
//				+ ", mFloatWindowAble=" + mFloatWindowAble + ", mSendSmsAble=" + mSendSmsAble + ", mIsRunning=" + mIsRunning + "]";
//	}
    @Override
    public String toString() {
        return "DeviceAppinfo [mInstallType=" + mInstallType + ", mAppName=" + mAppName
                + ", mPackageName=" + mPackageName + ",mIconAble=" + mIconAble + "]";
    }

}
