package com.android.settings.usagestats.model;

/* loaded from: classes2.dex */
public class DeviceUsageFloorData extends UsageFloorData {
    private static DeviceUsageFloorData deviceUsageFloorData;

    private DeviceUsageFloorData() {
        super(-1);
    }

    public static synchronized DeviceUsageFloorData getDeviceUsageFloorData() {
        DeviceUsageFloorData deviceUsageFloorData2;
        synchronized (DeviceUsageFloorData.class) {
            if (deviceUsageFloorData == null) {
                deviceUsageFloorData = new DeviceUsageFloorData();
            }
            deviceUsageFloorData2 = deviceUsageFloorData;
        }
        return deviceUsageFloorData2;
    }
}
