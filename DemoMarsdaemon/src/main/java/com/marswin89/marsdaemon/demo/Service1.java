package com.marswin89.marsdaemon.demo;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 *
 * Created by Mars on 12/24/15.
 */
public class Service1 extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
        final String packageStr = getLuncherActivity(this);
        Log.d("Service1", "packageStr " + packageStr);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.d("Service1", "packageStr " + packageStr + " " + "running");
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }

                    if (!isAPPALive(getApplicationContext(), getPackageName())) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ComponentName componentName = new ComponentName(getPackageName(), packageStr);
                        intent.setComponent(componentName);
                        startActivity(intent);
                    }
                }

            }
        }).start();
    }

    public static String getLuncherActivity(Context context) {
        Intent localIntent = new Intent("android.intent.action.MAIN", null);
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> appList = context.getPackageManager().queryIntentActivities(localIntent, 0);

        String pkg = context.getPackageName();

        for (int i = 0; i < appList.size(); i++) {
            ResolveInfo resolveInfo = appList.get(i);
            String packageStr = resolveInfo.activityInfo.packageName;
            if (packageStr.equals(pkg)) {
                return resolveInfo.activityInfo.name;
            }
        }
        return null;
    }

    public static boolean isAPPALive(Context mContext, String packageName) {
        boolean isAPPRunning = false;
        // 获取activity管理对象
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取所有正在运行的app
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        // 遍历，进程名即包名
        for (ActivityManager.RunningAppProcessInfo appInfo : appProcessInfoList) {
            if (packageName.equals(appInfo.processName)) {
                isAPPRunning = true;
                break;
            }
        }
        return isAPPRunning;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("Service1", "ondestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
