package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.settingslib.development.DevelopmentSettingsEnabler;

/* loaded from: classes.dex */
public class DevModeReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(context, false);
    }
}
