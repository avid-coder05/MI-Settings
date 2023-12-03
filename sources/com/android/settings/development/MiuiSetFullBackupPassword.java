package com.android.settings.development;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiSetFullBackupPassword extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String name = MiuiSetFullBackupPasswordFragment.class.getName();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag(name);
        if (findFragmentByTag == null) {
            findFragmentByTag = Fragment.instantiate(this, name, new Bundle());
        }
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        beginTransaction.replace(16908290, findFragmentByTag, name);
        beginTransaction.commit();
    }
}
