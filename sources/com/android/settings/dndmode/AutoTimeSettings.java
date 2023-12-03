package com.android.settings.dndmode;

import android.content.Intent;
import android.os.Bundle;
import com.android.settings.R;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class AutoTimeSettings extends AppCompatActivity {
    private AutoTimeSettingsFragment mFragment;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (1 != i || intent == null) {
            return;
        }
        this.mFragment.startQuietWristband(intent.getExtras().get("android.bluetooth.device.extra.DEVICE").toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dndm_activity_with_fragment);
        if (bundle != null) {
            this.mFragment = (AutoTimeSettingsFragment) getSupportFragmentManager().getFragment(bundle, "autoTimeSettingsFragment");
            return;
        }
        this.mFragment = new AutoTimeSettingsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, this.mFragment).commit();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        getSupportFragmentManager().putFragment(bundle, "autoTimeSettingsFragment", this.mFragment);
        super.onSaveInstanceState(bundle);
    }
}
