package k.m;

import java.io.Serializable;

/**
 * Created by as on 17-6-19.
 */

public interface IPayListener extends Serializable {
    public void onPaySuccess();

    public void onPayFailed(int errorCode, String errorMsg);


    public static int ERROR_CODE_PRICE_INVALIED = -1000;

    public static int ERROR_CODE_INIT_FAILED = -1001;

    public static int ERROR_CODE_USER_CANCEL = -1002;


    public static int ERROR_CODE_BACKEND_RESPONE_ERROR = -2000;
    public static int ERROR_CODE_BACKEND_RESPONE_PARSE_ERROR = -2001;

    public static int ERROR_CODE_NETWORK_ERROR = -3000;
}
