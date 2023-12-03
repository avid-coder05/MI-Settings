package com.android.settings.search.tree;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SystemAppSubTree extends SettingsTree {
    private final String mPackage;

    protected SystemAppSubTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mPackage = jSONObject.optString(FunctionColumns.PACKAGE, settingsTree.getPackage());
    }

    public String getPackage() {
        return this.mPackage;
    }

    protected String getTitle(boolean z) {
        ActivityInfo activityInfo;
        int i = 0;
        ResolveInfo resolveActivity = ((SettingsTree) this).mContext.getPackageManager().resolveActivity(getIntent(), 0);
        if (resolveActivity != null && (activityInfo = resolveActivity.activityInfo) != null) {
            i = activityInfo.labelRes;
        }
        if (i == 0) {
            i = ((SettingsTree) this).mContext.getApplicationInfo().labelRes;
        }
        if (i != 0) {
            try {
                return z ? ((SettingsTree) this).mContext.getString(i) : ((SettingsTree) this).mContext.getResources().getResourceEntryName(i);
            } catch (Resources.NotFoundException unused) {
            }
        }
        return super.getTitle(z);
    }
}
