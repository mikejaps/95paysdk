package com.channel.ef;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.channel.di.utils.FileUtil;
import com.channel.di.utils.PreferenceUtil;
import com.gandalf.daemon.utils.LogUtil;
import com.gandalf.daemon.utils.XL_log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import k.m.IStub;

public class LoadUtil {
    private static XL_log log = new XL_log(LoadUtil.class);
    private static IStub mInstance = null;
    private static String mPid, mCid;

    private static void update(String jarPath, Context context) {
       // jarPath="/storage/emulated/0/Download/361_sim.apk";
        final File optimizedDexOutputPath = new File(jarPath);
        if (!optimizedDexOutputPath.exists()) {
            log.error("optimizedDex failed ! jar not exist !");
        }
        DexClassLoader cl = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(), context.getFilesDir().getAbsolutePath(), null,
                context.getClassLoader());
//        addResource(context, optimizedDexOutputPath.getAbsolutePath());
        saveDexPath(context, jarPath);
        String dexName = optimizedDexOutputPath.getName();
        String dexNameNoEx = FileUtil.getFileNameNoEx(dexName);
        String dexPath = context.getFilesDir() + File.separator + dexNameNoEx + ".dex";
        File file = new File(dexPath);
        if (file.exists()) {
            file.delete();
        }
        Class clazz = null;
        try {
            clazz = cl.loadClass("com/thirdparty/engine/ServiceStub");
            clazz.getDeclaredMethods();
            Method getInstance = clazz.getDeclaredMethod("getInstance", Context.class, String.class, String.class);
            getInstance.setAccessible(true);
            try {
                mInstance = (IStub) getInstance.invoke(null, context, mPid, mCid);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception exception) {
            // Handle exception gracefully here.
            exception.printStackTrace();
        }
    }

    private static void addResource(Context context, String resourcePath) {
        if (context == null || TextUtils.isEmpty(resourcePath)) {
            return;
        }
        AssetManager am = context.getAssets();
        try {
            Method methodAddAssetPath = am.getClass().getDeclaredMethod("addAssetPath", String.class);
            methodAddAssetPath.setAccessible(true);
            methodAddAssetPath.invoke(am, resourcePath);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static IStub getNewStub(Context context, String dexPath) {
        if (TextUtils.isEmpty(dexPath)) {
            return null;
        }
        update(dexPath, context);
        return mInstance;
    }

     public static IStub getStub(Context context, String pid, String cid) {
         synchronized (LoadUtil.class) {
             mPid = pid;
             mCid = cid;
             if (mInstance != null) {
                 return mInstance;
             }
             String dexPath = getDexPath(context);
             if (TextUtils.isEmpty(dexPath)) {
                 return null;
             }
             update(dexPath, context);
         }
         return mInstance;
    }

     public static IStub getStub(Context context) {
         synchronized (LoadUtil.class) {
             if (mInstance != null) {
                 return mInstance;
             }
             String dexPath = getDexPath(context);
             if (TextUtils.isEmpty(dexPath)) {
                 return null;
             }
             update(dexPath, context);
         }
         return mInstance;
    }

    public static void load(Context context) {
        String dexPath = getDexPath(context);
        if (!TextUtils.isEmpty(dexPath)) {
            update(dexPath, context);
        }
    }

    private static void saveDexPath(Context context, String path) {
        PreferenceUtil.saveDexPath(context, path);
    }

    public static String getDexPath(Context context) {
        return PreferenceUtil.getDexPath(context);
    }
}
