package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import com.android.settings.MiuiWallpaperTypeSettings;
import com.android.settingslib.miuisettings.preference.Preference;
import miui.os.Build;

/* loaded from: classes.dex */
public class WallpaperPreference extends Preference {
    public WallpaperPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (Build.IS_TABLET) {
            setFragment(MiuiWallpaperTypeSettings.class.getName());
            return;
        }
        Intent intent = new Intent();
        intent.setClassName("com.android.thememanager", "com.android.thememanager.activity.WallpaperSettings");
        setIntent(intent);
    }
}
