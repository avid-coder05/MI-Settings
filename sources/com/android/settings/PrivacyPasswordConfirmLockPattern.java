package com.android.settings;

import android.os.Bundle;
import android.widget.TextView;
import com.android.settings.privacypassword.BussinessPackageInfo;
import com.android.settings.privacypassword.BussinessPackageInfoCache;
import com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl;

/* loaded from: classes.dex */
public class PrivacyPasswordConfirmLockPattern extends PrivacyPasswordConfirmAccessControl {
    @Override // com.android.settings.privacypassword.PrivacyPasswordConfirmAccessControl, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getIntent() != null) {
            String stringExtra = getIntent().getStringExtra("android.intent.extra.shortcut.NAME");
            if (stringExtra == null) {
                stringExtra = getIntent().getStringExtra("android.intent.action.CREATE_SHORTCUT");
            }
            setBackText(stringExtra);
        }
        if (this.mPrivacyPasswordManager.isUsedPrivacyInBussiness()) {
            return;
        }
        this.mPrivacyPasswordManager.setUsedPrivacyInBussiness(true);
    }

    protected void setBackText(String str) {
        if (str != null) {
            new BussinessPackageInfoCache();
            BussinessPackageInfo bussinessPackageInfo = BussinessPackageInfoCache.getBussinessPackageInfo().get(str);
            if (bussinessPackageInfo != null) {
                this.privacyPasswordConfirmBackTitle.setText(getResources().getString(bussinessPackageInfo.backText));
                TextView textView = this.bigTitle;
                if (textView != null) {
                    textView.setText(getResources().getString(bussinessPackageInfo.backText));
                }
            }
        }
    }
}
