package com.android.settings.stat.commonpreference;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.display.LargeFontUtils;
import com.android.settings.display.LocalFontModel;
import com.android.settings.display.PageLayoutFragment;
import com.android.settings.stat.commonpreference.PreferenceStat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class FontSettingsPrefStat extends PreferenceStat {
    private String getFontTitle(Context context) {
        List<LocalFontModel> fontList;
        if (context == null) {
            return "";
        }
        try {
            fontList = PageLayoutFragment.getFontList(context);
        } catch (Exception e) {
            Log.e("FontSwitchStat", "get current font error, msg is:" + e.getMessage());
        }
        if (fontList == null) {
            return "";
        }
        for (LocalFontModel localFontModel : fontList) {
            if (localFontModel.isUsing()) {
                return localFontModel.getTitle();
            }
        }
        return "";
    }

    protected int getCurrentZoomLevel(Context context) {
        if (context == null) {
            return -1;
        }
        return Settings.System.getInt(context.getContentResolver(), "key_screen_zoom_level", 1);
    }

    @Override // com.android.settings.stat.commonpreference.PreferenceStat
    public List<PreferenceStat.Info> getInfoList(Context context) {
        String str;
        ArrayList arrayList = new ArrayList();
        int currentUIModeType = LargeFontUtils.getCurrentUIModeType();
        int fontWeight = LargeFontUtils.getFontWeight(context);
        int currentZoomLevel = getCurrentZoomLevel(context);
        if (currentUIModeType != 1) {
            switch (currentUIModeType) {
                case 10:
                    str = "extral_small";
                    break;
                case 11:
                    str = "godzilla";
                    break;
                case 12:
                    str = "small";
                    break;
                case 13:
                    str = "medium";
                    break;
                case 14:
                    str = "large";
                    break;
                case 15:
                    str = "huge";
                    break;
                default:
                    str = "";
                    break;
            }
        } else {
            str = "normal";
        }
        arrayList.add(new PreferenceStat.Info("font_size", str));
        arrayList.add(new PreferenceStat.Info("font_weight", Integer.valueOf(fontWeight)));
        String str2 = currentZoomLevel != 0 ? currentZoomLevel != 1 ? currentZoomLevel != 2 ? "" : "big" : "normal" : "small";
        if (!TextUtils.isEmpty(str2)) {
            arrayList.add(new PreferenceStat.Info("font_zoom_level", str2));
        }
        String fontTitle = getFontTitle(context);
        if (!TextUtils.isEmpty(fontTitle)) {
            arrayList.add(new PreferenceStat.Info("font_style", fontTitle));
        }
        return arrayList;
    }
}
