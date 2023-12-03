package com.milink.api.v1;

/* loaded from: classes2.dex */
public class MiLinkClientDevice {
    private String mDeviceId;
    private String mDeviceName;
    private String mDeviceType;
    private String mLastConnectTime;
    private String mP2pMac;
    private String mWifiMac;

    public String getDeviceId() {
        return this.mDeviceId;
    }

    public String getDeviceName() {
        return this.mDeviceName;
    }

    public String getDeviceType() {
        return this.mDeviceType;
    }

    public String getLastConnectTime() {
        return this.mLastConnectTime;
    }

    public String getP2pMac() {
        return this.mP2pMac;
    }

    public String getWifiMac() {
        return this.mWifiMac;
    }

    public void setDeviceId(String str) {
        this.mDeviceId = str;
    }

    public void setDeviceName(String str) {
        this.mDeviceName = str;
    }

    public void setDeviceType(String str) {
        this.mDeviceType = str;
    }

    public void setLastConnectTime(String str) {
        this.mLastConnectTime = str;
    }

    public void setP2pMac(String str) {
        this.mP2pMac = str;
    }

    public void setWifiMac(String str) {
        this.mWifiMac = str;
    }

    public String toString() {
        return "MiLinkClientDevice{mDeviceId='" + this.mDeviceId + "', mDeviceName='" + this.mDeviceName + "', mDeviceType='" + this.mDeviceType + "', mP2pMac='" + this.mP2pMac + "', mWifiMac='" + this.mWifiMac + "', mLastConnectTime='" + this.mLastConnectTime + "'}";
    }
}
