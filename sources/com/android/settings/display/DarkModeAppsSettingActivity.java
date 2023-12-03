package com.android.settings.display;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.R;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class DarkModeAppsSettingActivity extends AppCompatActivity {
    private FragmentTransaction transaction;

    private Fragment onCreateFragment() {
        return new DarkModeAppsSettingFragment();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.more_dark_mode_settings);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        this.transaction = beginTransaction;
        beginTransaction.replace(16908290, onCreateFragment()).commit();
    }
}
