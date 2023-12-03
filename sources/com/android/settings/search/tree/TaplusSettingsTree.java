package com.android.settings.search.tree;

import android.content.Context;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class TaplusSettingsTree extends SettingsTree {
    protected TaplusSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        if ("taplus_title".equals(getColumnValue("resource")) && SettingsFeatures.isNeedRemoveContentExtension(((SettingsTree) this).mContext)) {
            return 0;
        }
        return super.getStatus();
    }

    public boolean initialize() {
        return Build.IS_INTERNATIONAL_BUILD || Build.IS_TABLET || super.initialize();
    }
}
