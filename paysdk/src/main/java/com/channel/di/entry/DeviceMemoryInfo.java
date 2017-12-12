package com.channel.di.entry;

public class DeviceMemoryInfo {
	public String mDeviceRamAvailMemorySize;
	public String mDeviceRamTotalMemorySize;

	public String mDeviceRomAvailMemorySize;
	public String mDeviceRomTotalMemorySize;

	public String mDeviceExternalAvailMemorySize;
	public String mDeviceExternalTotalMemorySize;

	@Override
	public String toString() {
		return "DeviceMemoryInfo [mDeviceRamAvailMemorySize=" + mDeviceRamAvailMemorySize
				+ ", mDeviceRamTotalMemorySize=" + mDeviceRamTotalMemorySize + ", mDeviceRomAvailMemorySize="
				+ mDeviceRomAvailMemorySize + ", mDeviceRomTotalMemorySize=" + mDeviceRomTotalMemorySize
				+ ", mDeviceExternalAvailMemorySize=" + mDeviceExternalAvailMemorySize
				+ ", mDeviceExternalTotalMemorySize=" + mDeviceExternalTotalMemorySize + "]";
	}

}
