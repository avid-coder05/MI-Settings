package com.android.settings;

import android.content.Intent;
import android.view.Menu;
import androidx.fragment.app.Fragment;

/* loaded from: classes.dex */
public class ConfirmGalleryLockPattern extends com.android.settings.password.ConfirmLockPattern {
    @Override // com.android.settings.ConfirmLockPattern, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        return new Intent(super.getIntent());
    }

    @Override // com.android.settings.ConfirmLockPattern, com.android.settings.SettingsActivity
    protected boolean isValidFragment(String str) {
        return false;
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onAttachFragment(Fragment fragment) {
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
