package com.android.settings.applications;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.search.tree.SecuritySettingsTree;
import miui.yellowpage.YellowPageContract;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class ManageApplicationsActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = new Intent();
        intent.setClassName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.appmanager.AppManagerMainActivity");
        intent.putExtra("enter_way", YellowPageContract.Settings.DIRECTORY);
        if (MiuiUtils.isActivityAvalible(this, intent)) {
            startActivity(intent);
            finish();
            return;
        }
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setTitle(R.string.applications_settings);
        }
        String name = ApplicationsContainer.class.getName();
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
