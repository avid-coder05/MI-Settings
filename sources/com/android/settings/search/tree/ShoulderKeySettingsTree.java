package com.android.settings.search.tree;

import android.content.Context;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class ShoulderKeySettingsTree extends SettingsTree {
    private static final String SHOULDER_KEY_SETTINGS = "shoulder_key_settings";
    private static final String SHOULDER_KEY_SHORTCUT_SETTINGS = "shoulder_key_shortcut_settings";

    protected ShoulderKeySettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        if (columnValue.equals(SHOULDER_KEY_SHORTCUT_SETTINGS)) {
            if (!SettingsFeatures.IS_SUPPORT_SHOULDER_KEY_MORE) {
                return true;
            }
        } else if (columnValue.equals(SHOULDER_KEY_SETTINGS) && !SettingsFeatures.IS_SUPPORT_SHOULDER_KEY) {
            return true;
        }
        return super.initialize();
    }
}
