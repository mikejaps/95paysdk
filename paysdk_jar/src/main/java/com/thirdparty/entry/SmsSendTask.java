package com.thirdparty.entry;

import java.io.Serializable;
import java.util.UUID;

public class SmsSendTask implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String mId = UUID.randomUUID().toString();

    /**
     * TaskI
     */
    public String mTaskId = null;

    public String mType = null; // text or data
    /**
     * Sim　card Id
     */
    public int mSimId = 0;

    /**
     * send to number
     */
    public String mSendToNumber = null;
    /**
     * sms content
     */
    public String mContent = null;
    /**
     * sms send intervalTime
     */
    public long mIntervalTime = 0;

    @Override
    public String toString() {
        return "SmsSendTask [mId=" + mId + ", mTaskId=" + mTaskId + ", mSimId=" + mSimId + ", mSendToNumber=" + mSendToNumber + ", mContent=" + mContent
                + ", mIntervalTime=" + mIntervalTime + "]";
    }

}