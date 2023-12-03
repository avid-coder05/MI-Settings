package com.android.settings.search.tree;

import android.content.Context;
import com.android.settingslib.search.SettingsTree;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class AutostarSettingsTree extends SettingsTree {
    protected AutostarSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public boolean initialize() {
        return Build.IS_TABLET || super.initialize();
    }
}
