package k.m;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by as on 17-6-22.
 */

public class PayTask implements Serializable {

    private String mTaskId = UUID.randomUUID().toString();
    public String mPid = null;
    public String mCid = null;
    public int mPrice;
    private static ArrayList<PayListener> cachePayListener = new ArrayList<PayListener>();

    public PayTask(int mPrice, String pid, String cid, PayListener payListener) {
        this.mPrice = mPrice;
        this.mPid = pid;
        this.mCid = cid;
        payListener.setId(mTaskId);
        cachePayListener.add(payListener);
    }

    public static void notifListener(String taskId, boolean payResult, int errorCode, String errorMsg) {
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        for (int i = 0; i < cachePayListener.size(); i++) {
            PayListener payListener = cachePayListener.get(i);
            String id = payListener.getId();
            if (taskId.equals(id)) {
                if (payResult) {
                    payListener.onPaySuccess();
                } else {
                    if (!TextUtils.isEmpty(errorMsg))
                        payListener.onPayFailed(errorCode, errorMsg);
                }
                cachePayListener.remove(i);
                return;
            }
        }
    }

    public String getTaskId() {
        return mTaskId;
    }

    @Override
    public String toString() {
        return "PayTask{" +
                "mTaskId='" + mTaskId + '\'' +
                ", mPid='" + mPid + '\'' +
                ", mCid='" + mCid + '\'' +
                ", mPrice=" + mPrice +
                '}';
    }
}
