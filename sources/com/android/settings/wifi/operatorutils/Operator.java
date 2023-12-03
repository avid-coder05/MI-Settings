package com.android.settings.wifi.operatorutils;

import android.view.View;
import androidx.preference.PreferenceScreen;

/* loaded from: classes2.dex */
public abstract class Operator {
    public abstract int getDefaultEapMethod();

    public abstract int getSlotId();

    public abstract boolean isForbidDelSsid(String str);

    public abstract boolean isOpCustomization(String str);

    public abstract void opCustomizationView(View view, PreferenceScreen preferenceScreen);

    public abstract void registerReceiver();

    public abstract void stopTethering();

    public abstract void updateWifiConfig();
}
