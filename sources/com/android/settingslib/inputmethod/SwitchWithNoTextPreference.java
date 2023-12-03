package com.android.settingslib.inputmethod;

import android.content.Context;
import com.android.settingslib.miuisettings.preference.SwitchPreference;

/* loaded from: classes2.dex */
public class SwitchWithNoTextPreference extends SwitchPreference {
    public SwitchWithNoTextPreference(Context context) {
        super(context);
        setSwitchTextOn("");
        setSwitchTextOff("");
    }
}
