package android.content.res;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import miui.reflect.Field;
import miui.util.FeatureParser;
import miui.util.Log;

/* loaded from: classes.dex */
public class MiuiConfiguration implements Comparable<MiuiConfiguration> {
    public Bundle extraData = new Bundle();
    private int lastFontThemeChanged;
    public int themeChanged;
    public long themeChangedFlags;

    private static MiuiConfiguration getExtraConfig(Configuration configuration) {
        try {
            return (MiuiConfiguration) Field.of(Configuration.class, "extraConfig", MiuiConfiguration.class).get(configuration);
        } catch (Exception e) {
            Log.w("MiuiConfiguration", "getExtraConfig from Configuration failed", e);
            return null;
        }
    }

    public static float getFontScale(int i) {
        switch (i) {
            case 10:
                return FeatureParser.getFloat("extral_smallui_font_scale", 0.81f).floatValue();
            case 11:
                return FeatureParser.getFloat("godzillaui_font_scale", 1.5f).floatValue();
            case 12:
                return FeatureParser.getFloat("smallui_font_scale", 0.92f).floatValue();
            case 13:
                return FeatureParser.getFloat("mediumui_font_scale", 1.05f).floatValue();
            case 14:
                return FeatureParser.getFloat("largeui_font_scale", 1.17f).floatValue();
            case 15:
                return FeatureParser.getFloat("hugeui_font_scale", 1.33f).floatValue();
            default:
                return 1.0f;
        }
    }

    public static void sendThemeConfigurationChangeMsg(long j) {
        sendThemeConfigurationChangeMsg(j, null);
    }

    public static void sendThemeConfigurationChangeMsg(long j, Bundle bundle) {
        if (j != 0) {
            try {
                Configuration configuration = ActivityManagerNative.getDefault().getConfiguration();
                MiuiConfiguration extraConfig = getExtraConfig(configuration);
                extraConfig.updateTheme(j);
                if (bundle != null) {
                    extraConfig.extraData.putAll(bundle);
                }
                try {
                    IActivityManager.class.getDeclaredMethod("updateConfiguration", Configuration.class).invoke(ActivityManagerNative.getDefault(), configuration);
                } catch (Exception e) {
                    Log.w("MiuiConfiguration", "updateConfiguration failed", e);
                }
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        }
    }

    public boolean checkFontChange(int i) {
        return i >= 0 && i < this.lastFontThemeChanged;
    }

    @Override // java.lang.Comparable
    public int compareTo(MiuiConfiguration miuiConfiguration) {
        return this.themeChanged - miuiConfiguration.themeChanged;
    }

    public int hashCode() {
        return this.themeChanged + ((int) this.themeChangedFlags);
    }

    public String toString() {
        return " themeChanged=" + this.themeChanged + " themeChangedFlags=" + this.themeChangedFlags + " extraData = " + this.extraData;
    }

    public void updateTheme(long j) {
        int i = this.themeChanged + 1;
        this.themeChanged = i;
        this.themeChangedFlags = j;
        if ((j & 16) != 0) {
            this.lastFontThemeChanged = i;
        }
    }
}
