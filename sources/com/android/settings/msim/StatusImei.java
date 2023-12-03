package com.android.settings.msim;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miui.telephony.SubscriptionManager;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class StatusImei extends AppCompatActivity {
    private ActionBar.Tab addTab(Class<? extends SettingsPreferenceFragment> cls, int i, int i2, int i3) {
        miuix.appcompat.app.ActionBar appCompatActionBar = getAppCompatActionBar();
        ActionBar.Tab newTab = appCompatActionBar.newTab();
        String valueOf = String.valueOf(i);
        newTab.setText(getString(i));
        Bundle bundle = new Bundle();
        SubscriptionManager.putSlotId(bundle, i3);
        appCompatActionBar.addFragmentTab(valueOf, newTab, i2, cls, bundle, true);
        return newTab;
    }

    private void setupContents() {
        miuix.appcompat.app.ActionBar appCompatActionBar = getAppCompatActionBar();
        appCompatActionBar.setFragmentViewPagerMode(this, false);
        ActionBar.Tab addTab = addTab(ImeiFragment.class, R.string.status_sim_1, 0, 0);
        addTab(ImeiFragment.class, R.string.status_sim_2, 1, 1);
        appCompatActionBar.selectTab(addTab);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setupContents();
    }
}
