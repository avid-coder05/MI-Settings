package com.android.settings.wallpaper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.display.WallpaperPreferenceController;
import com.google.android.setupcompat.util.WizardManagerHelper;

/* loaded from: classes2.dex */
public abstract class StyleSuggestionActivityBase extends Activity {
    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isWallpaperServiceEnabled(Context context) {
        return context.getResources().getBoolean(17891552);
    }

    protected void addExtras(Intent intent) {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PackageManager packageManager = getPackageManager();
        Intent addFlags = new Intent().setComponent(new WallpaperPreferenceController(this, "unused key").getComponentName()).addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_PIP_SCREEN_PROJECTION);
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), addFlags);
        addExtras(addFlags);
        if (packageManager.resolveActivity(addFlags, 0) != null) {
            startActivity(addFlags);
        } else {
            startFallbackSuggestion();
        }
        finish();
    }

    void startFallbackSuggestion() {
        new SubSettingLauncher(this).setDestination(WallpaperTypeSettings.class.getName()).setTitleRes(R.string.wallpaper_suggestion_title).setSourceMetricsCategory(35).addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_PIP_SCREEN_PROJECTION).launch();
    }
}
