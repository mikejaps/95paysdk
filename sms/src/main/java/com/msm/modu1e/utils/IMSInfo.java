package com.msm.modu1e.utils;

public class IMSInfo {
	public String chipName;
	public String imsi_1;
	public String imei_1;
	public String imsi_2;
	public String imei_2;
	public boolean getFromAPI;
	@Override
	public String toString() {
		return "IMSInfo [chipName=" + chipName + ", imsi_1=" + imsi_1 + ", imei_1=" + imei_1 + ", imsi_2=" + imsi_2
				+ ", imei_2=" + imei_2 + "]";
	}

}