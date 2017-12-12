package com.channel.di.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.security.auth.x500.X500Principal;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.channel.di.entry.DeviceAppinfo;
import com.channel.di.utils.ShellUtils.CommandResult;
import com.gandalf.daemon.utils.XL_log;

/**
 * APP工具类 APP相关信息工具类。获取版本信息
 * 
 * @author jingle1267@163.com
 */
public final class AppUtil {
	private static XL_log log = new XL_log(AppUtil.class);
	private static final boolean DEBUG = true;
	private static final String TAG = "AppUtils";

	/**
	 * Don't let anyone instantiate this class.
	 */
	private AppUtil() {
		throw new Error("Do not need instantiate!");
	}

	/**
	 * 得到软件版本号
	 * 
	 * @param context
	 *            上下文
	 * @return 当前版本Code
	 */
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			String packageName = context.getPackageName();
			verCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}

	/**
	 * 得到软件显示版本信息
	 * 
	 * @param context
	 *            上下文
	 * @return 当前版本信息
	 */
	public static String getVerName(Context context) {
		String verName = "";
		try {
			String packageName = context.getPackageName();
			verName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}

	/**
	 * 安装apk
	 * 
	 * @param context
	 *            上下文
	 * @param file
	 *            APK文件
	 */
	public static void installApk(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 安装apk
	 * 
	 * @param context
	 *            上下文
	 * @param file
	 *            APK文件uri
	 */
	public static void installApk(Context context, Uri file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(file, "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 卸载apk
	 * 
	 * @param context
	 *            上下文
	 * @param packageName
	 *            包名
	 */
	public static void uninstallApk(Context context, String packageName) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageURI = Uri.parse("package:" + packageName);
		intent.setData(packageURI);
		context.startActivity(intent);
	}

	/**
	 * 检测服务是否运行
	 * 
	 * @param context
	 *            上下文
	 * @param className
	 *            类名
	 * @return 是否运行的状态
	 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo si : servicesList) {
			if (className.equals(si.service.getClassName())) {
				isRunning = true;
			}
		}
		return isRunning;
	}

	/**
	 * 停止运行服务
	 * 
	 * @param context
	 *            上下文
	 * @param className
	 *            类名
	 * @return 是否执行成功
	 */
	public static boolean stopRunningService(Context context, String className) {
		Intent intent_service = null;
		boolean ret = false;
		try {
			intent_service = new Intent(context, Class.forName(className));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (intent_service != null) {
			ret = context.stopService(intent_service);
		}
		return ret;
	}

	/**
	 * 得到CPU核心数
	 * 
	 * @return CPU核心数
	 */
	public static int getNumCores() {
		try {
			File dir = new File("/sys/devices/system/cpu/");
			File[] files = dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return Pattern.matches("cpu[0-9]", pathname.getName());
				}
			});
			return files.length;
		} catch (Exception e) {
			return 1;
		}
	}

	/**
	 * whether this process is named with processName
	 * 
	 * @param context
	 *            上下文
	 * @param processName
	 *            进程名
	 * @return <ul>
	 *         return whether this process is named with processName
	 *         <li>if context is null, return false</li>
	 *         <li>if {@link ActivityManager#getRunningAppProcesses()} is null,
	 *         return false</li>
	 *         <li>if one process of
	 *         {@link ActivityManager#getRunningAppProcesses()} is equal to
	 *         processName, return true, otherwise return false</li>
	 *         </ul>
	 */
	public static boolean isNamedProcess(Context context, String processName) {
		if (context == null || TextUtils.isEmpty(processName)) {
			return false;
		}

		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfoList = manager.getRunningAppProcesses();
		if (processInfoList == null) {
			return true;
		}

		for (RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
			if (processInfo.pid == pid && processName.equalsIgnoreCase(processInfo.processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * whether application is in background
	 * <ul>
	 * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
	 * </ul>
	 * 
	 * @param context
	 *            上下文
	 * @return if application is in background return true, otherwise return
	 *         false
	 */
	public static boolean isApplicationInBackground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskList = am.getRunningTasks(1);
		if (taskList != null && !taskList.isEmpty()) {
			ComponentName topActivity = taskList.get(0).topActivity;
			if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	// public static boolean isApplicationRunning(Context context, String
	// packageName) {
	// ActivityManager am = (ActivityManager)
	// context.getSystemService(Context.ACTIVITY_SERVICE);
	// List<RunningTaskInfo> taskList = am.getRunningTasks(1);
	// if (taskList != null && !taskList.isEmpty()) {
	// ComponentName topActivity = taskList.get(0).topActivity;
	// if (topActivity != null &&
	// !topActivity.getPackageName().equals(context.getPackageName())) {
	// return true;
	// }
	// }
	// return false;
	// }

	/**
	 * 获取应用签名
	 * 
	 * @param context
	 *            上下文
	 * @param pkgName
	 *            包名
	 */
	public static String getSign(Context context, String pkgName) {
		try {
			PackageInfo pis = context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
			return hexdigest(pis.signatures[0].toByteArray());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将签名字符串转换成需要的32位签名
	 * 
	 * @param paramArrayOfByte
	 *            签名byte数组
	 * @return 32位签名字符串
	 */
	private static String hexdigest(byte[] paramArrayOfByte) {
		final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramArrayOfByte);
			byte[] arrayOfByte = localMessageDigest.digest();
			char[] arrayOfChar = new char[32];
			for (int i = 0, j = 0;; i++, j++) {
				if (i >= 16) {
					return new String(arrayOfChar);
				}
				int k = arrayOfByte[i];
				arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
				arrayOfChar[++j] = hexDigits[(k & 0xF)];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 清理后台进程与服务
	 * 
	 * @param context
	 *            应用上下文对象context
	 * @return 被清理的数量
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	public static int gc(Context context) {
		long i = getDeviceUsableMemory(context);
		int count = 0; // 清理掉的进程数
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取正在运行的service列表
		List<RunningServiceInfo> serviceList = am.getRunningServices(100);
		if (serviceList != null)
			for (RunningServiceInfo service : serviceList) {
				if (service.pid == android.os.Process.myPid())
					continue;
				try {
					android.os.Process.killProcess(service.pid);
					count++;
				} catch (Exception e) {
					e.getStackTrace();
				}
			}

		// 获取正在运行的进程列表
		List<RunningAppProcessInfo> processList = am.getRunningAppProcesses();
		if (processList != null)
			for (RunningAppProcessInfo process : processList) {
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
				if (process.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					// pkgList 得到该进程下运行的包名
					String[] pkgList = process.pkgList;
					for (String pkgName : pkgList) {
						if (DEBUG) {
							Log.d(TAG, "======正在杀死包名：" + pkgName);
						}
						try {
							am.killBackgroundProcesses(pkgName);
							count++;
						} catch (Exception e) { // 防止意外发生
							e.getStackTrace();
						}
					}
				}
			}
		if (DEBUG) {
			Log.d(TAG, "清理了" + (getDeviceUsableMemory(context) - i) + "M内存");
		}
		return count;
	}

	/**
	 * 获取设备的可用内存大小
	 * 
	 * @param context
	 *            应用上下文对象context
	 * @return 当前内存大小
	 */
	public static int getDeviceUsableMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// 返回当前系统的可用内存
		return (int) (mi.availMem / (1024 * 1024));
	}

	/**
	 * 获取系统中所有的应用
	 * 
	 * @param context
	 *            上下文
	 * @return 应用信息List
	 */
	public static List<PackageInfo> getAllApps(Context context) {
		PackageManager pManager = context.getPackageManager();
		List<PackageInfo> paklist = pManager.getInstalledPackages(PackageManager.GET_META_DATA);
		return paklist;
	}

	/**
	 * 获取手机系统SDK版本
	 * 
	 * @return 如API 17 则返回 17
	 */
	@TargetApi(Build.VERSION_CODES.DONUT)
	public static int getSDKVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 是否Dalvik模式
	 * 
	 * @return 结果
	 */
	public static boolean isDalvik() {
		return "Dalvik".equals(getCurrentRuntimeValue());
	}

	/**
	 * 是否ART模式
	 * 
	 * @return 结果
	 */
	public static boolean isART() {
		String currentRuntime = getCurrentRuntimeValue();
		return "ART".equals(currentRuntime) || "ART debug build".equals(currentRuntime);
	}

	/**
	 * 获取手机当前的Runtime
	 * 
	 * @return 正常情况下可能取值Dalvik, ART, ART debug build;
	 */
	public static String getCurrentRuntimeValue() {
		try {
			Class<?> systemProperties = Class.forName("android.os.SystemProperties");
			try {
				Method get = systemProperties.getMethod("get", String.class, String.class);
				if (get == null) {
					return "WTF?!";
				}
				try {
					final String value = (String) get.invoke(systemProperties, "persist.sys.dalvik.vm.lib",
					/* Assuming default is */"Dalvik");
					if ("libdvm.so".equals(value)) {
						return "Dalvik";
					} else if ("libart.so".equals(value)) {
						return "ART";
					} else if ("libartd.so".equals(value)) {
						return "ART debug build";
					}

					return value;
				} catch (IllegalAccessException e) {
					return "IllegalAccessException";
				} catch (IllegalArgumentException e) {
					return "IllegalArgumentException";
				} catch (InvocationTargetException e) {
					return "InvocationTargetException";
				}
			} catch (NoSuchMethodException e) {
				return "SystemProperties.get(String key, String def) method is not found";
			}
		} catch (ClassNotFoundException e) {
			return "SystemProperties class is not found";
		}
	}

	private final static X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");

	/**
	 * 检测当前应用是否是Debug版本
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isDebuggable(Context ctx) {
		boolean debuggable = false;
		try {
			PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature signatures[] = pinfo.signatures;
			for (int i = 0; i < signatures.length; i++) {
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
				X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
				debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
				if (debuggable)
					break;
			}

		} catch (NameNotFoundException e) {
		} catch (CertificateException e) {
		}
		return debuggable;
	}

	/**
	 * 获取设备唯一标识
	 * 
	 * @param context
	 * @return
	 */
	// public static String getUUID(Context context) {
	// final TelephonyManager tm = (TelephonyManager)
	// context.getSystemService(Context.TELEPHONY_SERVICE);
	//
	// final String tmDevice, tmSerial, tmPhone, androidId;
	// tmDevice = "" + tm.getDeviceId();
	// tmSerial = "" + tm.getSimSerialNumber();
	// androidId = "" +
	// android.provider.Settings.Secure.getString(context.getContentResolver(),
	// android.provider.Settings.Secure.ANDROID_ID);
	//
	// UUID deviceUuid = new UUID(androidId.hashCode(), ((long)
	// tmDevice.hashCode() << 32) | tmSerial.hashCode());
	// String uniqueId = deviceUuid.toString();
	// if (BuildConfig.DEBUG)
	// Log.d(TAG, "uuid=" + uniqueId);
	//
	// return uniqueId;
	// }

	/**
	 * 是否是主线程
	 * 
	 * @return
	 */
	public static boolean isMainProcess(Context context) {
		ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = context.getPackageName();
		int myPid = android.os.Process.myPid();
		for (RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean startAppWithOpenCommand(Context context, String command, String packageName) {
		if (context == null || TextUtils.isEmpty(command) || TextUtils.isEmpty(packageName)) {
			return false;
		}
		String strs[] = command.split("\\s");
		if (strs == null || strs.length == 0) {
			return false;
		}
		String packageNameAndComponent = strs[strs.length - 1];
		if (TextUtils.isEmpty(packageNameAndComponent) || !packageNameAndComponent.contains("/")) {
			return false;
		}
		String strs2[] = packageNameAndComponent.split("/");
		if (strs2.length == 2) {
			if (command.contains("startservice")) {
				return startService(context, strs2[1], packageName);
			} else {
				return startActivity(context, strs2[1], packageName);
			}
		}
		return false;

	}

	public static boolean startActivity(Context context, String ActivityName, String packageName) {
		if (TextUtils.isEmpty(ActivityName) || TextUtils.isEmpty(packageName) || context == null)
			return false;
		try {
			Intent intent = new Intent();
			ComponentName name = new ComponentName(packageName, ActivityName);
			intent.setComponent(name);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean startService(Context context, String ServiceName, String packageName) {
		if (TextUtils.isEmpty(ServiceName) || TextUtils.isEmpty(packageName) || context == null)
			return false;
		try {
			Intent intent = new Intent();
			ComponentName name = new ComponentName(packageName, ServiceName);
			intent.setComponent(name);
			context.startService(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean launchAppFromPackageName(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName) || context == null) {
			return false;
		}
		try {
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
			if (intent != null) {
				context.startActivity(intent);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void lauchApp(Context context, String openCommand, String packageName) {
		if (!TextUtils.isEmpty(openCommand)) {
			AppUtil.startAppWithOpenCommand(context, openCommand, packageName);
		} else {
			AppUtil.launchAppFromPackageName(context, packageName);
		}
	}

	public static boolean isInstalled(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName) || context == null) {
			return false;
		}
		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
			if (ai != null)
				return true;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isSystemApp(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName) || context == null) {
			return false;
		}
		try {
			PackageManager pm = context.getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
			if (ai == null)
				return false;
			Class class_ApplicationInfo = ai.getClass();
			Field field_publicSourceDir = class_ApplicationInfo.getDeclaredField("publicSourceDir");
			String publicSourceDir = (String) field_publicSourceDir.get(ai);
			if (!TextUtils.isEmpty(publicSourceDir)) {
				return publicSourceDir.startsWith("/system/app");
			}
			return false;
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isDataApp(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName) || context == null) {
			return false;
		}
		try {
			PackageManager pm = context.getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
			if (ai == null)
				return false;
			Class class_ApplicationInfo = ai.getClass();
			Field field_publicSourceDir = class_ApplicationInfo.getDeclaredField("publicSourceDir");
			String publicSourceDir = (String) field_publicSourceDir.get(ai);
			if (!TextUtils.isEmpty(publicSourceDir)) {
				return publicSourceDir.startsWith("/data/app");
			}
			return false;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/***
	 * data or system
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String getAppInstallType(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName) || context == null) {
			return null;
		}
		try {
			PackageManager pm = context.getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_PERMISSIONS);
			if (ai == null)
				return null;
			Class class_ApplicationInfo = ai.getClass();
			Field field_publicSourceDir = class_ApplicationInfo.getDeclaredField("publicSourceDir");
			String publicSourceDir = (String) field_publicSourceDir.get(ai);
			if (!TextUtils.isEmpty(publicSourceDir)) {
				if (publicSourceDir.startsWith("/data/app")) {
					return "data";
				}
				if (publicSourceDir.startsWith("/system/app")) {
					return "system";
				}
			}
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAppInstallLocation(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName) || context == null) {
			return null;
		}
		PackageManager pm = context.getPackageManager();
		ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo(packageName, 0);
			if (ai == null)
				return null;
			return ai.publicSourceDir;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getInstallApkMd5(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName) || context == null) {
			return null;
		}
		try {
			PackageManager pm = context.getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
			if (ai == null)
				return null;
			String publicSourceDir = ai.publicSourceDir;
			if (!TextUtils.isEmpty(publicSourceDir)) {
				return Util.md5sum(publicSourceDir);
			}
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAppInstallTime(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName) || context == null) {
			return null;
		}
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(packageName, 0);
			long install_Time = info.firstInstallTime;
			SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
			sdf.applyPattern("yyyy年MM月dd日 HH时mm分ss秒");
			return sdf.format(install_Time);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAppName(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName) || context == null) {
			return null;
		}
		PackageManager pm = context.getPackageManager();
		ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo(packageName, 0);
			if (ai == null) {
				return null;
			}
			return ai.name;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getInstallAppSize(Context context, String packageName) {
		String path = getAppInstallLocation(context, packageName);
		if (!TextUtils.isEmpty(path)) {
			return convertFileSize(path);
		}
		return null;
	}

	/****
	 * get file size and format
	 * 
	 * @param size
	 * @return
	 */
	public static String convertFileSize(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			return null;
		}
		long size = file.length();
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;
		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}

//	public static ArrayList<DeviceAppinfo> getDeviceSystemAppInfos(Context context) {
//		if (context == null)
//			return null;
//		ArrayList<DeviceAppinfo> deviceAppinfos = new ArrayList<DeviceAppinfo>();
//		PackageManager pm = context.getPackageManager();
//		List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
//		if (packageInfos != null) {
//			for (int i = 0; i < packageInfos.size(); i++) {
//				DeviceAppinfo info = new DeviceAppinfo();
//				PackageInfo pi = packageInfos.get(i);
//				if (pi == null) {
//					continue;
//				}
//				String packageName = pi.packageName;
//				// get packageName;
//				info.mPackageName = packageName;
//				// get install path
//				String publicSourceDir = pi.applicationInfo.publicSourceDir;
//				info.mInstallType = getAppInstallTypeFromInstallPath(publicSourceDir);
//				if (info.mInstallType == null || info.mInstallType.equals("data")) {
//					continue;
//				}
//				// get install time
//				long install_Time = pi.firstInstallTime;
//				SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
//				sdf.applyPattern("yyyy年MM月dd日 HH时mm分ss秒");
//				String installTime = sdf.format(install_Time);
//				info.mLastInstsallDate = installTime;
//				// get app name
//				ApplicationInfo ai = pi.applicationInfo;
//				if (ai != null) {
//					info.mAppName = ai.loadLabel(pm).toString();
//				}
//				// get app size
//				if (!TextUtils.isEmpty(publicSourceDir)) {
//					info.mSize = convertFileSize(publicSourceDir);
//				}
//				// get app verion
//				info.mVerion = pi.versionName;
//				info.mVersionCode = pi.versionCode + "";
//				// get app md5
//				if (!TextUtils.isEmpty(publicSourceDir)) {
//					info.mMd5 = Util.md5sum(publicSourceDir);
//				}
//				String[] permissionInfo = pi.requestedPermissions;
//				// android.permission.SYSTEM_ALERT_WINDOW
//				// android.permission.SEND_SMS
//				if (permissionInfo != null) {
//					String alertPermission = "android.permission.SYSTEM_ALERT_WINDOW";
//					String sendSmsPermission = "android.permission.SEND_SMS";
//					for (int j = 0; j < permissionInfo.length; j++) {
//						if (permissionInfo[j] != null) {
//							if (permissionInfo[j].equals(alertPermission)) {
//								info.mFloatWindowAble = true;
//							} else if (permissionInfo[j].equals(sendSmsPermission)) {
//								info.mSendSmsAble = true;
//							}
//						}
//					}
//				}
//				// check
//				Intent intent = pm.getLaunchIntentForPackage(packageName);
//				if (intent != null) {
//					info.mIconAble = true;
//				} else {
//					info.mIconAble = false;
//				}
//				deviceAppinfos.add(info);
//				// check is running
//			}
//		}
//		return deviceAppinfos;
//	}
	
	public static ArrayList<DeviceAppinfo> getDeviceSystemAppInfos(Context context) {
		if (context == null)
			return null;
		ArrayList<DeviceAppinfo> deviceAppinfos = new ArrayList<DeviceAppinfo>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		if (packageInfos != null) {
			for (int i = 0; i < packageInfos.size(); i++) {
				DeviceAppinfo info = new DeviceAppinfo();
				PackageInfo pi = packageInfos.get(i);
				if (pi == null) {
					continue;
				}
				String packageName = pi.packageName;
				// get packageName;
				info.mPackageName = packageName;
				// get install path
				String publicSourceDir = pi.applicationInfo.publicSourceDir;
				info.mInstallType = getAppInstallTypeFromInstallPath(publicSourceDir);
				if (info.mInstallType == null || info.mInstallType.equals("data")) {
					continue;
				}
				// get install time
				long install_Time = pi.firstInstallTime;
				SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
				sdf.applyPattern("yyyy年MM月dd日 HH时mm分ss秒");
				String installTime = sdf.format(install_Time);
				info.mLastInstsallDate = installTime;
				// get app name
				ApplicationInfo ai = pi.applicationInfo;
				if (ai != null) {
					info.mAppName = ai.loadLabel(pm).toString();
				}
				// get app size
				if (!TextUtils.isEmpty(publicSourceDir)) {
					info.mSize = convertFileSize(publicSourceDir);
				}
				// get app verion
				info.mVerion = pi.versionName;
				info.mVersionCode = pi.versionCode + "";
				// get app md5
				if (!TextUtils.isEmpty(publicSourceDir)) {
					info.mMd5 = Util.md5sum(publicSourceDir);
				}
				// check
				Intent intent = pm.getLaunchIntentForPackage(packageName);
				if (intent != null) {
					info.mIconAble = true;
				} else {
					info.mIconAble = false;
				}
				deviceAppinfos.add(info);
				// check is running
			}
		}
		return deviceAppinfos;
	}

	private static String getAppInstallTypeFromInstallPath(String path) {
		if (!TextUtils.isEmpty(path)) {
			if (path.startsWith("/data/app"))
				return "data";
			if (path.startsWith("/system/app"))
				return "system";
		}
		return null;
	}

	public static String getElfPaths(Context context) {
		ArrayList<String> arrayList = getSuPath(context);
		if (arrayList != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < arrayList.size(); i++) {
				sb.append(arrayList.get(i));
			}
			return sb.toString();
		}
		return null;
	}

	public static ArrayList<String> getSuPath(Context context) {
		ArrayList<String> paths = new ArrayList<String>();
		ArrayList<String> ls_sys_bin = new ArrayList<String>();
		String command1 = "ls -al /system/bin";
		ls_sys_bin.add(command1);
		CommandResult result1 = ShellUtils.execCommand(ls_sys_bin, false);
		if (result1 != null) {
			String sys_bin_ret = result1.successMsg;
			if (sys_bin_ret != null) {
				paths.add(command1 + ":success msg:" + sys_bin_ret);
			}
			String errorMsg = result1.errorMsg;
			if (errorMsg != null) {
				paths.add(command1 + ":error msg:" + errorMsg);
			}
		}
		ArrayList<String> ls_sys_xbin = new ArrayList<String>();
		String command2 = "ls -al /system/xbin";
		ls_sys_bin.add(command2);
		CommandResult result2 = ShellUtils.execCommand(ls_sys_xbin, false);
		if (result2 != null) {
			String sys_bin_ret = result2.successMsg;
			if (sys_bin_ret != null) {
				paths.add(command2 + ":success msg:" + sys_bin_ret);
			}
			String errorMsg = result2.errorMsg;
			if (errorMsg != null) {
				paths.add(command2 + ":error msg:" + errorMsg);
			}
		}
		ArrayList<String> ls_sys_vendor_bin = new ArrayList<String>();
		String command3 = "ls -al /system/vendor/bin/";
		ls_sys_bin.add(command3);
		CommandResult result3 = ShellUtils.execCommand(ls_sys_vendor_bin, false);
		if (result3 != null) {
			String sys_bin_ret = result3.successMsg;
			if (sys_bin_ret != null) {
				paths.add(command3 + ":success msg:" + sys_bin_ret);
			}
			String errorMsg = result3.errorMsg;
			if (errorMsg != null) {
				paths.add(command3 + ":error msg:" + errorMsg);
			}
		}
		ArrayList<String> ls_sys_vendor_tmp = new ArrayList<String>();
		String command4 = "ls -al /system/vendor/tmp/";
		ls_sys_vendor_tmp.add(command4);
		CommandResult result4 = ShellUtils.execCommand(ls_sys_vendor_tmp, false);
		if (result4 != null) {
			String sys_bin_ret = result4.successMsg;
			if (sys_bin_ret != null) {
				paths.add(command4 + ":success msg:" + sys_bin_ret);
			}
			String errorMsg = result4.errorMsg;
			if (errorMsg != null) {
				paths.add(command4 + ":error msg:" + errorMsg);
			}
		}
		return paths;
	}

	public static String getInstallRecoverySh() {
		StringBuilder content_sh = null;
		StringBuilder content_cm = null;
		StringBuilder content_3 = null;
		StringBuilder content_1 = null;
		StringBuilder content_2 = null;
		try {
			content_sh = FileUtil.readFile("/system/etc/install-recovery.sh");
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			content_cm = FileUtil.readFile("/system/etc/install-recovery-cm.sh");
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			content_3 = FileUtil.readFile("/system/etc/install-recovery-3.sh");
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			content_1 = FileUtil.readFile("/system/etc/install-recovery-1.sh");
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			content_2 = FileUtil.readFile("/system/etc/install-recovery-2.sh");
		} catch (Exception e) {
			// TODO: handle exception
		}
		StringBuilder sb = new StringBuilder();
		if (content_sh != null) {
			sb.append(content_sh + " || ");
		}
		if (content_cm != null) {
			sb.append(content_cm + " || ");
		}
		if (content_3 != null) {
			sb.append(content_3 + " || ");
		}
		if (content_1 != null) {
			sb.append(content_1 + " || ");
		}
		if (content_2 != null) {
			sb.append(content_2 + " || ");
		}
		return sb.toString();
	}
}