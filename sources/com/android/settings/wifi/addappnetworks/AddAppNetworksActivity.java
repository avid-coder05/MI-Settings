package com.android.settings.wifi.addappnetworks;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class AddAppNetworksActivity extends AppCompatActivity {
    @VisibleForTesting
    final Bundle mBundle = new Bundle();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.settings_panel);
        showAddNetworksFragment();
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
        Window window = getWindow();
        window.setGravity(80);
        window.setLayout(-1, -2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showAddNetworksFragment();
    }

    @VisibleForTesting
    void showAddNetworksFragment() {
        this.mBundle.putString("panel_calling_package_name", getCallingPackage());
        this.mBundle.putParcelableArrayList("android.provider.extra.WIFI_NETWORK_LIST", getIntent().getParcelableArrayListExtra("android.provider.extra.WIFI_NETWORK_LIST"));
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag("AddAppNetworksActivity");
        if (findFragmentByTag != null) {
            ((AddAppNetworksFragment) findFragmentByTag).createContent(this.mBundle);
            return;
        }
        AddAppNetworksFragment addAppNetworksFragment = new AddAppNetworksFragment();
        addAppNetworksFragment.setArguments(this.mBundle);
        supportFragmentManager.beginTransaction().add(R.id.main_content, addAppNetworksFragment, "AddAppNetworksActivity").commit();
    }
}
