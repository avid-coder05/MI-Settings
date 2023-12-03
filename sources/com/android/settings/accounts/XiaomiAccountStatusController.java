package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.TextView;
import com.android.settings.BaseSettingsController;
import com.android.settings.R;
import com.android.settings.notify.SettingsNotifyHelper;
import miui.accounts.ExtraAccountManager;

/* loaded from: classes.dex */
public class XiaomiAccountStatusController extends BaseSettingsController {
    public XiaomiAccountStatusController(Context context, TextView textView) {
        super(context, textView);
    }

    @Override // com.android.settings.BaseSettingsController
    public void pause() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void resume() {
    }

    @Override // com.android.settings.BaseSettingsController
    public void updateStatus() {
        if (this.mStatusView == null) {
            return;
        }
        if (SettingsNotifyHelper.isPhoneRecycledToNotify(this.mContext)) {
            Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.device_update_signal1);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            this.mStatusView.setText(R.string.suggestion_for_phone_recycled);
            this.mStatusView.setCompoundDrawables(null, null, drawable, null);
            return;
        }
        AccountManager accountManager = (AccountManager) this.mContext.getSystemService("account");
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(this.mContext);
        if (xiaomiAccount == null) {
            this.mStatusView.setText((CharSequence) null);
            return;
        }
        String userData = accountManager.getUserData(xiaomiAccount, "acc_user_name");
        String str = xiaomiAccount.name;
        if (TextUtils.isEmpty(userData)) {
            this.mStatusView.setText(str);
        } else {
            this.mStatusView.setText(userData);
        }
    }
}
