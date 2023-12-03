package com.android.settings.accounts;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiChooseAccountActivity extends AppCompatActivity {
    private void initAccountUI() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (((MiuiChooseAccountFragment) supportFragmentManager.findFragmentById(16908290)) == null) {
            supportFragmentManager.beginTransaction().replace(16908290, new MiuiChooseAccountFragment()).commit();
        }
    }

    private boolean isSetupWizard() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getBooleanExtra("account_setup_wizard", false);
        }
        return false;
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (isSetupWizard()) {
            return;
        }
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        if (isSetupWizard()) {
            setTheme(R.style.ShowTitleTheme);
        }
        super.onCreate(bundle);
        initAccountUI();
    }

    @Override // android.app.Activity
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }
}
