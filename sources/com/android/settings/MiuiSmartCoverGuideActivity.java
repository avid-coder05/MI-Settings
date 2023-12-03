package com.android.settings;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiSmartCoverGuideActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.smart_cover_guide_activity);
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.add(R.id.container, new MiuiSmartCoverSettingsFragment(), (String) null);
        beginTransaction.commit();
    }
}
