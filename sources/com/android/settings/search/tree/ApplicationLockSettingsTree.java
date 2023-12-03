package com.android.settings.search.tree;

import android.content.Context;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.search.SettingsTree;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class ApplicationLockSettingsTree extends SettingsTree {
    protected ApplicationLockSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public boolean initialize() {
        if ("other_advanced_settings".equals(getParent().getColumnValue("resource"))) {
            if (!Build.IS_TABLET || "privacy_mode_enable_title".equals(getColumnValue("category_origin"))) {
                return true;
            }
            if ("ac_enable_title".equals(getColumnValue("resource")) && !Boolean.parseBoolean(getColumnValue(FunctionColumns.IS_CHECKBOX))) {
                return true;
            }
        } else if (Build.IS_TABLET) {
            return true;
        }
        return super.initialize();
    }
}
