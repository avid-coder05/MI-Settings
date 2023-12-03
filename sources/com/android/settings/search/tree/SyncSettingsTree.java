package com.android.settings.search.tree;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.accounts.AccountSyncSettings;
import com.android.settings.accounts.MiuiManageAccounts;
import com.android.settings.accounts.MiuiManageAccountsSettings;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.Utils;
import com.android.settingslib.search.SettingsTree;
import java.util.HashMap;
import java.util.LinkedList;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miui.content.ExtraIntent;
import miui.provider.ExtraTelephony;
import miui.yellowpage.YellowPageStatistic;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SyncSettingsTree extends SettingsTree {
    public static final String GMSCORE_SETTINGS_TITLE = "gmscore_settings_title";

    protected SyncSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public LinkedList<SettingsTree> getSons() {
        if ("sync_settings".equals(getColumnValue("resource"))) {
            LinkedList sons = super.getSons();
            if (sons != null) {
                for (int size = sons.size() - 1; size >= 0; size--) {
                    SettingsTree settingsTree = (SettingsTree) sons.get(size);
                    if (Boolean.parseBoolean(settingsTree.getColumnValue("temporary"))) {
                        settingsTree.removeSelf();
                    }
                }
            }
            AccountManager accountManager = AccountManager.get(((SettingsTree) this).mContext);
            Account[] accountsAsUser = accountManager.getAccountsAsUser(UserHandle.myUserId());
            AuthenticatorDescription[] authenticatorTypesAsUser = accountManager.getAuthenticatorTypesAsUser(UserHandle.myUserId());
            HashMap hashMap = new HashMap();
            for (Account account : accountsAsUser) {
                if (hashMap.containsKey(account.type)) {
                    hashMap.put(account.type, null);
                } else if (!"com.xiaomi".equals(account.type) && !ExtraIntent.XIAOMI_ACCOUNT_TYPE_UNACTIVATED.equals(account.type) && MiuiManageAccountsSettings.isUserVisible(account.type)) {
                    hashMap.put(account.type, account);
                }
            }
            for (String str : hashMap.keySet()) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("temporary", true);
                    jSONObject.put(ExtraTelephony.UnderstandInfo.CLASS, AccountSettingsTree.class.getName());
                    jSONObject.put(YellowPageStatistic.Display.CATEGORY, "account_other");
                    int length = authenticatorTypesAsUser.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        AuthenticatorDescription authenticatorDescription = authenticatorTypesAsUser[i];
                        if (authenticatorDescription.type.equals(str)) {
                            jSONObject.put(FunctionColumns.PACKAGE, authenticatorDescription.packageName);
                            jSONObject.put("labelId", authenticatorDescription.labelId);
                            jSONObject.put("iconId", authenticatorDescription.iconId);
                            jSONObject.put("accountType", str);
                            if (hashMap.get(str) == null) {
                                jSONObject.put(FunctionColumns.FRAGMENT, MiuiManageAccounts.class.getName());
                            } else {
                                jSONObject.put(FunctionColumns.FRAGMENT, AccountSyncSettings.class.getName());
                            }
                        } else {
                            i++;
                        }
                    }
                    addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
                } catch (JSONException unused) {
                }
            }
        }
        return super.getSons();
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if ("sync_wifi_only".equals(columnValue) && Utils.isWifiOnly(((SettingsTree) this).mContext)) {
            return 0;
        }
        if (columnValue.startsWith("account_settings_menu_auto_sync")) {
            if (columnValue.equals("account_settings_menu_auto_sync") == (((UserManager) ((SettingsTree) this).mContext.getSystemService("user")).getProfiles(UserHandle.myUserId()).size() > 1)) {
                return 0;
            }
        }
        if (GMSCORE_SETTINGS_TITLE.equals(columnValue)) {
            if (SettingsFeatures.isNeedRemoveGmsCoreSettigns(((SettingsTree) this).mContext)) {
                return 0;
            }
            setColumnValue("icon", "gmscore_icon");
        }
        return super.getStatus();
    }

    protected String getTitle(boolean z) {
        ActivityInfo activityInfo;
        String columnValue = getColumnValue("resource");
        if ("xiaomi_cloud_service_description_format".equals(columnValue)) {
            return MiuiUtils.isApplicationInstalled(((SettingsTree) this).mContext, ContactsSyncInfoProvider.AUTHORITY) ? ((SettingsTree) this).mContext.getString(R.string.xiaomi_cloud_service_description) : ((SettingsTree) this).mContext.getString(R.string.xiaomi_cloud_service_description_without_contacts);
        }
        if (GMSCORE_SETTINGS_TITLE.equals(columnValue)) {
            try {
                PackageManager packageManager = ((SettingsTree) this).mContext.getPackageManager();
                ResolveInfo resolveActivity = packageManager.resolveActivity(getIntent(), 0);
                if (resolveActivity != null && (activityInfo = resolveActivity.activityInfo) != null) {
                    return activityInfo.loadLabel(packageManager).toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.getTitle(z);
    }
}
