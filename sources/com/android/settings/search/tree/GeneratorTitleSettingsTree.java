package com.android.settings.search.tree;

import android.content.Context;
import android.text.TextUtils;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class GeneratorTitleSettingsTree extends SettingsTree {
    private String mTitle;

    protected GeneratorTitleSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mTitle = jSONObject.optString("title");
    }

    protected String getTitle(boolean z) {
        return !TextUtils.isEmpty(this.mTitle) ? this.mTitle : super.getTitle(z);
    }
}
