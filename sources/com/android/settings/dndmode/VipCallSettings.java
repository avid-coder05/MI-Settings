package com.android.settings.dndmode;

import android.os.Bundle;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class VipCallSettings extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().replace(16908290, new VipCallSettingsFragment()).commit();
        }
    }
}
