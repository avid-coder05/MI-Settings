package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class PermissionInfoActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ApplicationInfo applicationInfo = (ApplicationInfo) getIntent().getParcelableExtra("extra_package_application");
        if (applicationInfo == null) {
            finish();
            return;
        }
        String name = PermissionInfoFragment.class.getName();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag(name);
        if (findFragmentByTag == null) {
            Bundle bundle2 = new Bundle();
            bundle2.putParcelable("extra_package_application", applicationInfo);
            findFragmentByTag = Fragment.instantiate(this, name, bundle2);
        }
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        beginTransaction.replace(16908290, findFragmentByTag, name);
        beginTransaction.commit();
    }
}
