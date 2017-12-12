package com.thirdparty.engine;

import android.app.Application;

/**
 * Created by as on 17-7-28.
 */

public class AppAppllication extends Application {
    public static AppAppllication INSTANCE = null;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        crashHandle();
    }

    private void crashHandle() {
        if (AppBuildConfig.CHRASH_HANDLE) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(this);
        }
    }

    public void killSelf() {
        INSTANCE = null;
    }
}

