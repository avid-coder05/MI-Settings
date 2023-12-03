package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import com.android.settings.R;
import java.util.ArrayList;
import miui.content.ExtraIntent;

/* loaded from: classes.dex */
public class XiaomiAccountPreference extends AccountPreference {
    public XiaomiAccountPreference(Context context, Account account, Drawable drawable, ArrayList<String> arrayList) {
        super(context, account, drawable, arrayList, true);
        Intent intent = new Intent("android.settings.XIAOMI_ACCOUNT_SYNC_SETTINGS");
        intent.putExtra("account", account);
        setIntent(intent);
        if (TextUtils.isEmpty(AccountManager.get(context).getUserData(account, ExtraIntent.EXTRA_XIAOMI_ACCOUNT_REG_TYPE))) {
            return;
        }
        setTitle(R.string.activating);
    }

    @Override // androidx.preference.Preference
    public void setSummary(CharSequence charSequence) {
        super.setSummary(getContext().getString(R.string.xiaomi_account));
    }
}
