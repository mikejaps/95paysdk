package com.channel.di.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.channel.di.entry.DeviceBaseInfo;
import com.channel.di.utils.ShellUtils.CommandResult;
import com.gandalf.daemon.utils.DeviceUuidFactory;

public class DeviceBaseInfoUtil {
	public static DeviceBaseInfo getDeviceInfo(Context context) {
		DeviceBaseInfo info = new DeviceBaseInfo();
		String pid = getPid(context);
		info.pid = pid;
		info.uuid = new DeviceUuidFactory(context).getDeviceUuid() + "";
		String imei = getIMEI(context);
		if (imei != null) {
			info.imei = imei;
		} else {
			info.imei = "";
		}

		info.imsi1 = "";
		info.imsi2 = "";
		String imsi = getIMSIWithAPI(context);
		if (TextUtils.isEmpty(imsi)) {
			info.imsi = "";
		}
		info.network = getCurrentNetworkType(context);

		// boolean power = RkHandler.getInstance(context).checkPermission();
		// if (power) {
		// info.rootPower = "yes";
		// } else {
		// info.rootPower = "no";
		// }

		info.rootPower = "yes";

		info.rootProvider = "KeXing";

		info.phoneBrand = Build.BRAND;

		info.phoneModel = Build.MODEL;

		info.chipVer = getPlatform();

		info.cpuBits = getCpuBits();

		info.androidVer = getSystemVersion();

		info.androidBuildVer = getAndroidBuilderVersion();

		info.linuxBuildVer = getLinuxBuildVersion();

		info.linuxVer = getLinuxVersion();
		return info;
	}

	public static JSONObject getDeviceBaseInfoJSONObj(Context context) {
		DeviceBaseInfo info = getDeviceInfo(context);
		if (info == null)
			return null;
		JSONObject obj = new JSONObject();
		try {
			obj.put("pid", info.pid);
			obj.put("uuid", info.uuid);
			obj.put("imsi", info.imsi);
			obj.put("imei", info.imei);
			obj.put("imsi1", info.imsi1);
			obj.put("imsi2", info.imsi2);
			obj.put("network", info.network);
			obj.put("register", info.rootPower);
			obj.put("registerHome", info.rootProvider);
			obj.put("phoneBrand", info.phoneBrand);
			obj.put("phoneModel", info.phoneModel);
			obj.put("chipVer", info.chipVer);
			obj.put("cpuBits", info.cpuBits);
			obj.put("androidVer", info.androidVer);
			obj.put("androidBuildVer", info.androidBuildVer);
			obj.put("linuxVer", info.linuxVer);
			obj.put("linuxBuildVer", info.linuxBuildVer);
			return obj;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static int getApkVerion(Context context) {
		PackageManager manager;
		PackageInfo info = null;
		manager = context.getPackageManager();
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return info.versionCode;
	}

	public static String getPid(Context context) {
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			try {
				int pid = appInfo.metaData.getInt("pid");
				return pid + "";
			} catch (NullPointerException e) {

			}

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "0";
	}

	public static String getSystemVersion() {
		return Build.VERSION.RELEASE;
	}

	public static String getLinuxKernelVersion() {
		CommandResult kernelVersion = ShellUtils.execCommand("cat /proc/version", false);
		if (kernelVersion.result == 0) {
			return kernelVersion.successMsg;
		} else {
			return getKernelVersion();
		}
	}

	public static String getKernelVersion() {
		String kernelVersion = "";
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("/proc/version");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return kernelVersion;
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
		String info = "";
		String line = "";
		try {
			while ((line = bufferedReader.readLine()) != null) {
				info += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if (info != "") {
				final String keyword = "version ";
				int index = info.indexOf(keyword);
				line = info.substring(index + keyword.length());
				index = line.indexOf(" ");
				kernelVersion = line.substring(0, index);
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return kernelVersion;
	}

	public static String getCurrentNetworkType(Context context) {
		String strNetworkType = "";
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				strNetworkType = "WIFI";
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				String _strSubTypeName = networkInfo.getSubtypeName();
				// TD-SCDMA networkType is 17
				int networkType = networkInfo.getSubtype();
				switch (networkType) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by
															// 11
					strNetworkType = "2G";
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by
															// 14
				case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by
															// 12
				case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by
															// 15
					strNetworkType = "3G";
					break;
				case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by
														// 13
					strNetworkType = "4G";
					break;
				default:
					// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
					if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA")
							|| _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
						strNetworkType = "3G";
					} else {
						strNetworkType = _strSubTypeName;
					}

					break;
				}
			}
		}
		return strNetworkType;
	}

	public static String getTelecomByIMSI(String imsi) {
		// //因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
		if (TextUtils.isEmpty(imsi)) {
			return "";
		}
		if (imsi.startsWith("46000") || imsi.startsWith("46002")) { // 中国移动
			return "移动";
		} else if (imsi.startsWith("46001")) { // 中国联通
			return "联通";
		} else if (imsi.startsWith("46003") || imsi.startsWith("46011")) { // 中国电信
			return "电信";
		} else { // 没有卡
			return "未知";
		}
	}

	public static String getPlatform() {
		CommandResult ret = ShellUtils.execCommand("getprop ro.board.platform;", false);
		if (ret != null) {
			if (!TextUtils.isEmpty(ret.successMsg)) {
				return ret.successMsg;
			} else {
				ret = ShellUtils.execCommand("getprop ro.mediatek.platform", false);
				if (ret != null && !TextUtils.isEmpty(ret.successMsg)) {
					return ret.successMsg;
				}
			}
		}
		return "";
	}

	public static String getCpuBits() {
		int SDK_INT = Build.VERSION.SDK_INT;
		if (SDK_INT > 20) {
			CommandResult ret = ShellUtils.execCommand("ro.product.cpu.abilist64;", false);
			if (ret != null && !TextUtils.isEmpty(ret.successMsg)) {
				return ret.successMsg;
			}
		}
		return "32";
	}

	public static String getAndroidBuilderVersion() {
		CommandResult ret = ShellUtils.execCommand("getprop ro.build.description;", false);
		if (ret != null && !TextUtils.isEmpty(ret.successMsg)) {
			return ret.successMsg;
		}
		return "";
	}

	public static String getLinuxBuildVersion() {
		CommandResult ret = ShellUtils.execCommand("cat /proc/version;", false);
		if (ret == null || TextUtils.isEmpty(ret.successMsg)) {
			return "";
		}
		String linux_kernel_version = ret.successMsg;
		String linux_version[] = linux_kernel_version.split("#");
		if (linux_version == null)
			return "";
		if (linux_version.length > 1) {
			String linux_v = linux_version[0];
			if (!TextUtils.isEmpty(linux_v)) {
				String linuxBuildVer = "#" + linux_v;
				return linuxBuildVer;
			}
		}
		return "";
	}

	public static String getLinuxVersion() {
		CommandResult ret = ShellUtils.execCommand("cat /proc/version;", false);
		if (ret == null || TextUtils.isEmpty(ret.successMsg)) {
			return "";
		}
		String linux_kernel_version = ret.successMsg;
		String linux_version[] = linux_kernel_version.split("#");
		if (linux_version == null)
			return "";
		if (linux_version.length >= 2) {
			String linuxVer = linux_version[1];
			if (!TextUtils.isEmpty(linuxVer)) {
				return linuxVer;
			}
		}
		return "";
	}

	/**
	 * 系统的api
	 * 
	 * @return
	 */
	public static String getIMSIWithAPI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}

	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
}
