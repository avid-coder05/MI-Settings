package com.android.settings.settingspanel;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import com.android.settings.MiuiSoundSettings;
import com.android.settings.MiuiWirelessSettings;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.settingspanel.WifiSettingsPanelFragment;
import com.android.settings.wifi.MiuiWifiSettings;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class MiuiSettingsPanelActivity extends AppCompatActivity implements WifiSettingsPanelFragment.WifiStateChangeListener, View.OnClickListener {
    private View mWifiSettingsView;

    private void configContentView() {
        ((TextView) findViewById(R.id.panel_title)).setText(getTitleRes());
        Button button = (Button) findViewById(R.id.see_more);
        Button button2 = (Button) findViewById(R.id.done);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    private String getTitleRes() {
        String action = getIntent().getAction();
        if (TextUtils.isEmpty(action)) {
            return "";
        }
        action.hashCode();
        char c = 65535;
        switch (action.hashCode()) {
            case 66351017:
                if (action.equals("android.settings.panel.action.NFC")) {
                    c = 0;
                    break;
                }
                break;
            case 464243859:
                if (action.equals("android.settings.panel.action.INTERNET_CONNECTIVITY")) {
                    c = 1;
                    break;
                }
                break;
            case 1215888444:
                if (action.equals("android.settings.panel.action.VOLUME")) {
                    c = 2;
                    break;
                }
                break;
            case 2057152695:
                if (action.equals("android.settings.panel.action.WIFI")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return getResources().getString(R.string.nfc_quick_toggle_title);
            case 1:
                return getResources().getString(R.string.internet_connectivity_panel_title);
            case 2:
                return getResources().getString(R.string.volume_connectivity_panel_title);
            case 3:
                return getResources().getString(R.string.wifi_settings);
            default:
                return "";
        }
    }

    private void more() {
        String action = getIntent().getAction();
        if (action != null) {
            char c = 65535;
            switch (action.hashCode()) {
                case 66351017:
                    if (action.equals("android.settings.panel.action.NFC")) {
                        c = 0;
                        break;
                    }
                    break;
                case 464243859:
                    if (action.equals("android.settings.panel.action.INTERNET_CONNECTIVITY")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1215888444:
                    if (action.equals("android.settings.panel.action.VOLUME")) {
                        c = 2;
                        break;
                    }
                    break;
                case 2057152695:
                    if (action.equals("android.settings.panel.action.WIFI")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    new SubSettingLauncher(this).setDestination(MiuiWirelessSettings.class.getName()).setTitleRes(R.string.connection_and_sharing).launch();
                    return;
                case 1:
                case 3:
                    new SubSettingLauncher(this).setDestination(MiuiWifiSettings.class.getName()).setTitleRes(R.string.wifi_settings).launch();
                    return;
                case 2:
                    new SubSettingLauncher(this).setDestination(MiuiSoundSettings.class.getName()).setTitleRes(R.string.sound_vibrate_settings).launch();
                    return;
                default:
                    return;
            }
        }
    }

    private void showContent() {
        String action = getIntent().getAction();
        if (action != null) {
            char c = 65535;
            switch (action.hashCode()) {
                case 66351017:
                    if (action.equals("android.settings.panel.action.NFC")) {
                        c = 0;
                        break;
                    }
                    break;
                case 464243859:
                    if (action.equals("android.settings.panel.action.INTERNET_CONNECTIVITY")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1215888444:
                    if (action.equals("android.settings.panel.action.VOLUME")) {
                        c = 2;
                        break;
                    }
                    break;
                case 2057152695:
                    if (action.equals("android.settings.panel.action.WIFI")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    showOtherSettings(action);
                    return;
                case 1:
                    showWifiSettings();
                    showOtherSettings(action);
                    return;
                case 2:
                    showVolumeSettings();
                    return;
                case 3:
                    showWifiSettings();
                    return;
                default:
                    return;
            }
        }
    }

    private void showOtherSettings(String str) {
        int i = R.id.fragment_other;
        View findViewById = findViewById(i);
        findViewById.setVisibility(0);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.settings_panel_default_height);
            findViewById.setLayoutParams(layoutParams);
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        OtherSettingPanelFragment otherSettingPanelFragment = new OtherSettingPanelFragment();
        Bundle bundle = new Bundle();
        bundle.putString("action", str);
        otherSettingPanelFragment.setArguments(bundle);
        supportFragmentManager.beginTransaction().replace(i, otherSettingPanelFragment).commit();
    }

    private void showVolumeSettings() {
        int i = R.id.fragment_other;
        View findViewById = findViewById(i);
        findViewById.setVisibility(0);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.volumes_settings_panel_height);
            findViewById.setLayoutParams(layoutParams);
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.beginTransaction().replace(i, new VolumeSettingPanelFragment()).commit();
    }

    private void showWifiSettings() {
        int i = R.id.fragment_content;
        View findViewById = findViewById(i);
        this.mWifiSettingsView = findViewById;
        findViewById.setVisibility(0);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        WifiSettingsPanelFragment wifiSettingsPanelFragment = new WifiSettingsPanelFragment();
        wifiSettingsPanelFragment.registerStateListener(this);
        supportFragmentManager.beginTransaction().replace(i, wifiSettingsPanelFragment).commit();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.see_more) {
            more();
            finish();
        } else if (id == R.id.done) {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.settings_panel_layout);
        configContentView();
        showContent();
        getWindow().setLayout(-1, -2);
        getWindow().setGravity(80);
    }

    @Override // com.android.settings.settingspanel.WifiSettingsPanelFragment.WifiStateChangeListener
    public void onWifiStateChanged(int i) {
        View view = this.mWifiSettingsView;
        if (view == null) {
            return;
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (i == 1) {
            if (layoutParams != null) {
                layoutParams.height = getResources().getDimensionPixelSize(R.dimen.wifi_settings_panel_height);
            }
        } else if (layoutParams != null) {
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.settings_panel_default_height);
        }
        this.mWifiSettingsView.setLayoutParams(layoutParams);
    }
}
