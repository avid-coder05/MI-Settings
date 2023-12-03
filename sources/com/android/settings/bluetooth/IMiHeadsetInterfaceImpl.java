package com.android.settings.bluetooth;

import android.util.Log;

/* loaded from: classes.dex */
public class IMiHeadsetInterfaceImpl {
    public void a2dpconnected(int i, Object obj) {
        Log.i("IMiHeadsetInterfaceImpl", "a2dpconnected" + i + " " + obj);
    }

    public void a2dpdisconnected(int i) {
        Log.i("IMiHeadsetInterfaceImpl", "a2dpdisconnected" + i);
    }

    public void hfpconnected(int i, Object obj) {
        Log.i("IMiHeadsetInterfaceImpl", "hfpconnected" + i + " " + obj);
    }

    public void hfpdisconnected(int i) {
        Log.i("IMiHeadsetInterfaceImpl", "hfpdisconnected" + i);
    }

    public void onDeviceAttributesChanged() {
        Log.i("IMiHeadsetInterfaceImpl", "onDeviceAttributesChanged");
    }

    public void serviceInited() {
        Log.i("IMiHeadsetInterfaceImpl", "serviceInited");
    }
}
