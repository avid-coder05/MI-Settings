package com.android.settings;

import android.app.Activity;
import android.os.Bundle;
import miuix.appcompat.app.Fragment;

/* loaded from: classes.dex */
public class SettingsActivityCompat {
    public static void startPreferencePanel(Activity activity, Fragment fragment, String str, Bundle bundle, int i, CharSequence charSequence, Fragment fragment2, int i2) {
        if (activity instanceof SettingsActivity) {
            ((SettingsActivity) activity).startPreferencePanel(fragment, str, bundle, i, charSequence, fragment2, i2);
        }
    }
}
