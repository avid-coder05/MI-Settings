package com.android.settings.privacypassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes2.dex */
public class SetPrivacyPasswordChooseAccessControl extends PrivacyPasswordChooseAccessControl {
    private boolean mPasswordConfirmed = true;
    private PrivacyPasswordManager mPrivacyPasswordManager;

    protected boolean enterFromPrivacySettings() {
        String stringExtra = getIntent().getStringExtra(YellowPageContract.MipubPhoneEvent.URI_PARAM_EXTRA_DATA);
        return !TextUtils.isEmpty(stringExtra) && stringExtra.equals("choose_suspend");
    }

    @Override // android.app.Activity
    public void finish() {
        if (!enterFromPrivacySettings()) {
            if (this.mPasswordConfirmed) {
                setResult(-1);
            } else {
                setResult(0);
            }
        }
        super.finish();
    }

    @Override // com.android.settings.privacypassword.PrivacyPasswordChooseAccessControl, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 290241) {
            return;
        }
        if (i2 == -1) {
            this.mPasswordConfirmed = true;
            return;
        }
        this.mPasswordConfirmed = false;
        finish();
    }

    @Override // com.android.settings.privacypassword.PrivacyPasswordChooseAccessControl, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PrivacyPasswordManager privacyPasswordManager = PrivacyPasswordManager.getInstance(getApplicationContext());
        this.mPrivacyPasswordManager = privacyPasswordManager;
        if (!privacyPasswordManager.havePattern()) {
            this.mPasswordConfirmed = true;
        }
        String callingPackage = getCallingPackage();
        if (TextUtils.isEmpty(callingPackage) || !"com.android.settings".equals(callingPackage)) {
            finish();
        }
    }

    @Override // com.android.settings.privacypassword.PrivacyPasswordChooseAccessControl, android.app.Activity
    public void onResume() {
        super.onResume();
        if (this.mPrivacyPasswordManager.havePattern() && enterFromPrivacySettings()) {
            finish();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        if (!enterFromPrivacySettings() && this.mPrivacyPasswordManager.havePattern() && !this.mPasswordConfirmed) {
            Intent intent = new Intent(this, PrivacyPasswordConfirmAccessControl.class);
            intent.putExtra("enter_from_settings", true);
            startActivityForResult(intent, 290241);
        }
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onStop() {
        if (this.mPasswordConfirmed) {
            this.mPasswordConfirmed = false;
        }
        super.onStop();
    }
}
