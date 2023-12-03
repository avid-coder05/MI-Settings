package com.android.settings.search.tree;

import android.content.Context;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SecondSpaceSettingsTree extends SettingsTree {
    protected SecondSpaceSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        if (SettingsFeatures.isSecondSpaceItemNeedHide(((SettingsTree) this).mContext)) {
            return 0;
        }
        return super.getStatus();
    }

    public boolean initialize() {
        if (SettingsFeatures.isSecondSpaceItemNeedHide(((SettingsTree) this).mContext)) {
            return true;
        }
        return super.initialize();
    }
}
