package com.android.settings.stat.commonpreference;

import android.content.Context;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.stat.commonpreference.PreferenceStat;
import java.util.ArrayList;
import java.util.List;
import miui.provider.ExtraTelephony;
import miui.util.FeatureParser;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class ScreenOptimizePreference extends PreferenceStat {
    private static final int DEFAULT_EXPERT_COLOR_GAMUT = FeatureParser.getInteger("expert_gamut_default", 0);

    public static String getGamutFromDatabase(Context context) {
        String string = Settings.System.getString(context.getContentResolver(), "expert_data");
        if (TextUtils.isEmpty(string)) {
            return String.valueOf(DEFAULT_EXPERT_COLOR_GAMUT);
        }
        try {
            return String.valueOf(new JSONObject(string).getInt("color_gamut"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getScreenOptimizeMode(Context context) {
        return MiuiSettings.System.getString(context.getContentResolver(), "screen_optimize_mode");
    }

    @Override // com.android.settings.stat.commonpreference.PreferenceStat
    List<PreferenceStat.Info> getInfoList(Context context) {
        ArrayList arrayList = new ArrayList();
        String screenOptimizeMode = getScreenOptimizeMode(context);
        if (screenOptimizeMode != null) {
            arrayList.add(new PreferenceStat.Info("screen_optimize_mode", getScreenOptimizeMode(context)));
        }
        String gamutFromDatabase = getGamutFromDatabase(context);
        if (ExtraTelephony.Phonelist.TYPE_CLOUDS_BLACK.equals(screenOptimizeMode) && gamutFromDatabase != null) {
            arrayList.add(new PreferenceStat.Info("color_gamut", gamutFromDatabase));
        }
        return arrayList;
    }
}
