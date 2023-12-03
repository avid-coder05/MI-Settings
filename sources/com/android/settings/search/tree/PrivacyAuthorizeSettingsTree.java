package com.android.settings.search.tree;

import android.content.Context;
import com.android.settings.security.PrivacyRevocationController;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class PrivacyAuthorizeSettingsTree extends SettingsTree {
    protected PrivacyAuthorizeSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public boolean initialize() {
        return PrivacyRevocationController.hidePrivacyRevoke() || super.initialize();
    }
}
