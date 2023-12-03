package com.android.settings.search.tree;

import android.content.Context;
import com.android.settings.MiuiUtils;
import com.android.settings.RegionUtils;
import com.android.settingslib.search.SettingsTree;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class LocaleSettingsTree extends SettingsTree {
    protected LocaleSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        if ("locale_settings".equals(getColumnValue("resource")) && (MiuiUtils.needOverlayTwLocale() || RegionUtils.IS_LM_CLARO)) {
            return 0;
        }
        return super.getStatus();
    }

    public boolean initialize() {
        if (Build.IS_GLOBAL_BUILD) {
            return super.initialize();
        }
        return true;
    }
}
