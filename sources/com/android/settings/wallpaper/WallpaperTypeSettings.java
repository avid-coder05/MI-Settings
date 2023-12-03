package com.android.settings.wallpaper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;

/* loaded from: classes2.dex */
public class WallpaperTypeSettings extends DashboardFragment {
    private boolean onGoToMiSystemWallpaperSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.BROWSABLE");
        intent.setData(Uri.parse("theme://zhuti.xiaomi.com/provisionwallpaper?wallpaperchoose=system&miback=true&miref=" + context.getPackageName()));
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException unused) {
            return false;
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_uri_wallpaper;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected String getLogTag() {
        return "WallpaperTypeSettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 101;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    protected int getPreferenceScreenResId() {
        return R.xml.wallpaper_settings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Log.w("WallpaperTypeSettings", "Check if go to miui system wallpaper setting");
        if (onGoToMiSystemWallpaperSetting(getActivity())) {
            Log.w("WallpaperTypeSettings", "Go to miui system wallpaper setting!");
            getActivity().finish();
        }
    }
}
