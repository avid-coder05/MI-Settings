package com.android.settings.search.tree;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.settings.accounts.AccountSyncSettings;
import com.android.settings.accounts.MiuiManageAccounts;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.search.SettingsTree;
import miui.provider.Notes;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class AccountSettingsTree extends SettingsTree {
    static final String ACCOUNT_TYPE = "accountType";
    static final String ICON_ID = "iconId";
    static final String LABEL_ID = "labelId";
    private final String mAccountType;
    private final String mPackage;

    protected AccountSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mPackage = jSONObject.optString(FunctionColumns.PACKAGE, settingsTree.getPackage());
        Resources resources = ((SettingsTree) this).mContext.getResources();
        try {
            setColumnValue("resource", resources.getResourceEntryName(jSONObject.optInt(LABEL_ID)));
        } catch (Resources.NotFoundException unused) {
        }
        try {
            setColumnValue("icon", resources.getResourceName(jSONObject.optInt(ICON_ID)));
        } catch (Resources.NotFoundException unused2) {
        }
        this.mAccountType = jSONObject.optString(ACCOUNT_TYPE);
    }

    public Intent getIntent() {
        Intent intent = super.getIntent();
        if (AccountSyncSettings.class.getName().equals(getColumnValue(FunctionColumns.FRAGMENT))) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("account", AccountManager.get(((SettingsTree) this).mContext).getAccountsByTypeAsUser(this.mAccountType, UserHandle.of(UserHandle.myUserId()))[0]);
            intent.putExtra(":settings:show_fragment_args", bundle);
        } else if (MiuiManageAccounts.class.getName().equals(getColumnValue(FunctionColumns.FRAGMENT))) {
            Bundle bundle2 = new Bundle();
            bundle2.putString(Notes.Account.ACCOUNT_TYPE, this.mAccountType);
            intent.putExtra(":settings:show_fragment_args", bundle2);
        }
        return intent;
    }

    public String getPackage() {
        return this.mPackage;
    }
}
