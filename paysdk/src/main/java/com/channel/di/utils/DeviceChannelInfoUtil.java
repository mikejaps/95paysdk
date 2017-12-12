package com.channel.di.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.channel.di.entry.DeviceBaseInfo;
import com.channel.di.entry.DeviceChannelInfo;
import com.channel.di.entry.DeviceMemoryInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DeviceChannelInfoUtil {
	public static DeviceChannelInfo getDeviceChannelInfo(Context context) {
		if (context == null) {
			return null;
		}
		DeviceBaseInfo baseInfo = DeviceBaseInfoUtil.getDeviceInfo(context);
		DeviceChannelInfo deviceSilenceInfo = new DeviceChannelInfo();
		deviceSilenceInfo.mDeviceBasicInfo = baseInfo;
		deviceSilenceInfo.myPackageName = context.getPackageName();
		// String packageName = context.getPackageName();
		// boolean isSystemApp = RkHandler.isSysApp(packageName);
		// boolean isNormalApp = RkHandler.isNormalApp(packageName);
		// if (isNormalApp) {
		// deviceSilenceInfo.myInstallCatalog = "data";
		// } else if (isSystemApp) {
		// deviceSilenceInfo.myInstallCatalog = "system";
		// }
		deviceSilenceInfo.myInstallCatalog = "data";
		// String apkPath = PackageUtils.getAppInstallApkPath(packageName);
		deviceSilenceInfo.myMd5 = "";
		deviceSilenceInfo.myVerionCode = DeviceBaseInfoUtil.getApkVerion(context) + "";
		return deviceSilenceInfo;
	}

	public static DeviceMemoryInfo getDeviceMemoryInfo(Context context) {
		DeviceMemoryInfo deviceMemoryInfo = new DeviceMemoryInfo();
		deviceMemoryInfo.mDeviceRamAvailMemorySize = getRawAvailMemory(context);
		deviceMemoryInfo.mDeviceRamTotalMemorySize = getRamTotalMemory(context);

		deviceMemoryInfo.mDeviceExternalAvailMemorySize = getAvailableExternalMemory();
		deviceMemoryInfo.mDeviceExternalTotalMemorySize = getTotalExternalMemory();

		deviceMemoryInfo.mDeviceRomAvailMemorySize = getAvailableInternalMemory();
		deviceMemoryInfo.mDeviceRomTotalMemorySize = getTotalInternalMemory();

		return deviceMemoryInfo;
	}

	/***
	 * get device avail run memory
	 * 
	 * @param context
	 * @return
	 */
	public static String getRawAvailMemory(Context context) {
		// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		// return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
		// long size = mi.availMem / (1024 * 1024);
		return formatSize(mi.availMem);
	}

	/***
	 * get device run total memory
	 * 
	 * @param context
	 * @return
	 */
	// 获取总运存大小
	public static String getRamTotalMemory(Context context) {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}
			initial_memory = Long.valueOf(arrayOfString[1]).longValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();
		} catch (IOException e) {
		}
		// return Formatter.formatFileSize(context, initial_memory);//
		// Byte转换为KB或者MB，内存大小规格化
		// long size = initial_memory / (1024 * 1024);
		return formatSize(initial_memory);
	}

	/***
	 * check external storage state
	 * 
	 * @return
	 */

	public static boolean externalMemoryAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/***
	 * get available internal memeory size
	 * 
	 * @return
	 */
	public static String getAvailableInternalMemory() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return formatSize(availableBlocks * blockSize);
	}

	/***
	 * get available internal memeory size return long
	 * 
	 * @return
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/***
	 * getTotalInternalMemorySize
	 * 
	 * @return
	 */
	public static String getTotalInternalMemory() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return formatSize(totalBlocks * blockSize);
	}

	public static String getAvailableExternalMemory() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return formatSize(availableBlocks * blockSize);
		} else {
			return "ERROR";
		}
	}

	public static String getTotalExternalMemory() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return formatSize(totalBlocks * blockSize);
		} else {
			return "ERROR";
		}
	}

	public static String formatSize(long size) {
		String suffix = null;
		if (size >= 1024) {
			suffix = "KB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "MB";
				size /= 1024;
			}
		}
		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}
		if (suffix != null)
			resultBuffer.append(suffix);
		return resultBuffer.toString();
	}
}
