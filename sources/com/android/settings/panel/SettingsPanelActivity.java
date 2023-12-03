package com.android.settings.panel;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
import com.android.settingslib.core.lifecycle.ObservableActivity;
import miui.provider.ExtraNetwork;

/* loaded from: classes2.dex */
public class SettingsPanelActivity extends ObservableActivity {
    @VisibleForTesting
    final Bundle mBundle = new Bundle();
    @VisibleForTesting
    boolean mForceCreation = false;
    @VisibleForTesting
    PanelFragment mPanelFragment;

    private void createOrUpdatePanel(boolean z) {
        Intent intent = getIntent();
        if (intent == null) {
            Log.e("SettingsPanelActivity", "Null intent, closing Panel Activity");
            finish();
            return;
        }
        String action = intent.getAction();
        String stringExtra = intent.getStringExtra(ExtraNetwork.FIREWALL_PACKAGE_NAME);
        this.mBundle.putString("PANEL_TYPE_ARGUMENT", action);
        this.mBundle.putString("PANEL_CALLING_PACKAGE_NAME", getCallingPackage());
        this.mBundle.putString("PANEL_MEDIA_PACKAGE_NAME", stringExtra);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        int i = R.id.main_content;
        Fragment findFragmentById = supportFragmentManager.findFragmentById(i);
        if (z || findFragmentById == null || !(findFragmentById instanceof PanelFragment)) {
            setContentView(R.layout.settings_panel);
            Window window = getWindow();
            window.setGravity(80);
            window.setLayout(-1, -2);
            PanelFragment panelFragment = new PanelFragment();
            this.mPanelFragment = panelFragment;
            panelFragment.setArguments(new Bundle(this.mBundle));
            supportFragmentManager.beginTransaction().add(i, this.mPanelFragment).commit();
            return;
        }
        PanelFragment panelFragment2 = (PanelFragment) findFragmentById;
        this.mPanelFragment = panelFragment2;
        if (panelFragment2.isPanelCreating()) {
            Log.w("SettingsPanelActivity", "A panel is creating, skip " + action);
            return;
        }
        Bundle arguments = findFragmentById.getArguments();
        if (arguments == null || !TextUtils.equals(action, arguments.getString("PANEL_TYPE_ARGUMENT"))) {
            this.mPanelFragment.setArguments(new Bundle(this.mBundle));
            this.mPanelFragment.updatePanelWithAnimation();
            return;
        }
        Log.w("SettingsPanelActivity", "Panel is showing the same action, skip " + action);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mForceCreation = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getApplicationContext().getTheme().rebase();
        createOrUpdatePanel(true);
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        createOrUpdatePanel(this.mForceCreation);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mForceCreation = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        PanelFragment panelFragment = this.mPanelFragment;
        if (panelFragment == null || panelFragment.isPanelCreating()) {
            return;
        }
        this.mForceCreation = true;
    }
}
