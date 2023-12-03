package com.android.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import com.android.settings.privacypassword.BussinessPackageInfo;
import com.android.settings.privacypassword.BussinessPackageInfoCache;
import com.android.settings.privacypassword.PrivacyPasswordChooseAccessControl;
import com.android.settings.privacypassword.PrivacyPasswordManager;

/* loaded from: classes.dex */
public class PrivacyPasswordChooseLockPattern extends PrivacyPasswordChooseAccessControl {
    private PrivacyPasswordManager mPrivacyPasswordManager;

    @Override // com.android.settings.privacypassword.PrivacyPasswordChooseAccessControl, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PrivacyPasswordManager privacyPasswordManager = PrivacyPasswordManager.getInstance(this);
        this.mPrivacyPasswordManager = privacyPasswordManager;
        if (privacyPasswordManager.isUsedPrivacyInBussiness()) {
            return;
        }
        this.mPrivacyPasswordManager.setUsedPrivacyInBussiness(true);
    }

    @Override // com.android.settings.privacypassword.PrivacyPasswordChooseAccessControl, android.app.Activity
    public void onResume() {
        super.onResume();
        PrivacyPasswordManager privacyPasswordManager = PrivacyPasswordManager.getInstance(this);
        this.mPrivacyPasswordManager = privacyPasswordManager;
        if (privacyPasswordManager.havePattern()) {
            finish();
        }
        String stringExtra = getIntent().getStringExtra("android.intent.action.CREATE_SHORTCUT");
        if (TextUtils.isEmpty(stringExtra)) {
            stringExtra = getIntent().getStringExtra("android.intent.extra.shortcut.NAME");
        }
        setBackText(stringExtra);
    }

    protected void setBackText(String str) {
        if (str != null) {
            new BussinessPackageInfoCache();
            BussinessPackageInfo bussinessPackageInfo = BussinessPackageInfoCache.getBussinessPackageInfo().get(str);
            if (bussinessPackageInfo != null) {
                this.privacyChooseAccessControlBackTitle.setText(getResources().getString(bussinessPackageInfo.backText));
                TextView textView = this.bigTitle;
                if (textView != null) {
                    textView.setText(getResources().getString(bussinessPackageInfo.backText));
                }
            }
        }
    }
}
