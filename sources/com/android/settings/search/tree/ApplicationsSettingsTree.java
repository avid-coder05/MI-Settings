package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import com.android.settings.MiuiUtils;
import com.android.settingslib.search.SettingsTree;
import miui.yellowpage.YellowPageContract;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class ApplicationsSettingsTree extends SettingsTree {
    protected ApplicationsSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public Intent getIntent() {
        Intent intent = new Intent();
        intent.setClassName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.appmanager.AppManagerMainActivity");
        intent.putExtra("enter_way", YellowPageContract.Settings.DIRECTORY);
        return MiuiUtils.isActivityAvalible(((SettingsTree) this).mContext, intent) ? intent : super.getIntent();
    }
}
