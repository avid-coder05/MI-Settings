package com.android.settings.wifi;

import android.content.Context;
import androidx.fragment.app.Fragment;
import com.android.wifitrackerlib.WifiEntry;

/* loaded from: classes2.dex */
public class LongPressWifiEntryPreference extends MiuiWifiEntryPreference {
    private final Fragment mFragment;

    public LongPressWifiEntryPreference(Context context, WifiEntry wifiEntry, Fragment fragment) {
        super(context, wifiEntry);
        this.mFragment = fragment;
    }

    public LongPressWifiEntryPreference(Context context, WifiEntry wifiEntry, Fragment fragment, boolean z) {
        super(context, wifiEntry, z);
        this.mFragment = fragment;
    }
}
