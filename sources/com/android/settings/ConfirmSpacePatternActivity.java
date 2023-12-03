package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.ConfirmLockPattern;

/* loaded from: classes.dex */
public class ConfirmSpacePatternActivity extends ConfirmLockPattern.InternalActivity {
    private Context mContext;

    /* loaded from: classes.dex */
    public static class ConfirmSpaceLockPatternFragment extends ConfirmLockPattern.ConfirmLockPatternFragment {
        @Override // com.android.settings.ConfirmLockPattern.ConfirmLockPatternFragment, com.android.settings.BaseConfirmLockFragment
        public View createView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            View createView = super.createView(layoutInflater, viewGroup, bundle);
            ConfirmSpacePasswordActivity.initActionBar(getAppCompatActivity(), this.mUserIdToConfirmPattern);
            return createView;
        }
    }

    public static String getExtraFragmentName() {
        return ConfirmSpaceLockPatternFragment.class.getName();
    }

    @Override // com.android.settings.ConfirmLockPattern, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ConfirmSpaceLockPatternFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.ConfirmLockPattern, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ConfirmSpaceLockPatternFragment.class.getName().equals(str);
    }

    @Override // com.android.settings.BaseConfirmLockActivity, com.android.settings.password.ConfirmDeviceCredentialBaseActivity, com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
    }
}
