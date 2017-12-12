package com.channel.di.entry;


public class DeviceBaseInfo {
	public String pid;

	public String uuid;

	public String imsi;
	public String imei;

	public String imsi1;

	public String imsi2;

	public String network;

	public String rootPower;

	public String rootProvider;

	/** 手机品牌 */
	public String phoneBrand;

	/** 手机型号 */
	public String phoneModel;

	public String chipVer;

	public String cpuBits="32";//default 32, API>20 support 64bit

	public String androidVer;

	public String androidBuildVer;

	public String linuxVer;

	public String linuxBuildVer;
}
