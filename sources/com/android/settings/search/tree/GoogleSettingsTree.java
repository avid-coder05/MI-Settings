package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class GoogleSettingsTree extends SettingsTree {
    static final String ACTIVITY_NAME = "activityName";
    private final String mActivityName;
    private final String mTitle;

    protected GoogleSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mActivityName = jSONObject.optString(ACTIVITY_NAME);
        this.mTitle = jSONObject.optString("title");
    }

    public Intent getIntent() {
        return new Intent().setClassName(((SettingsTree) this).mContext, this.mActivityName);
    }

    protected String getTitle(boolean z) {
        return !TextUtils.isEmpty(this.mTitle) ? this.mTitle : super.getTitle(z);
    }
}
