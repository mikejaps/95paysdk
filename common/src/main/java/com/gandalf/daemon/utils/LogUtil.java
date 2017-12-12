package com.gandalf.daemon.utils;

import android.util.Log;

/**
 * Created by gg on 2017/12/2.
 */

public class LogUtil {
    private static boolean isDebugEnable = true;

    public static void d(String tag, String msg) {
        if (isDebugEnable)
            Log.d(tag, msg);
    }
    public static void v(String tag, String msg) {
        if (isDebugEnable)
            Log.v(tag, msg);
    }
    public static void e(String tag, String msg) {
        if (isDebugEnable)
            Log.e(tag, msg);
    }
    public static void i(String tag, String msg) {
        if (isDebugEnable)
            Log.i(tag, msg);
    }

}


