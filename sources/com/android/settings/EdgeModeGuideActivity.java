package com.android.settings;

import android.hardware.input.InputManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class EdgeModeGuideActivity extends AppCompatActivity {
    private static final int[] TITTLE_RES = {R.string.edge_mode_back, R.string.edge_mode_clean, R.string.edge_mode_photo};
    private int mEdgeType;
    private EdgeModeGuideFragment mFragment;
    private final Handler mHandler = new Handler() { // from class: com.android.settings.EdgeModeGuideActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                EdgeModeGuideActivity.this.mVideoView.start();
            }
        }
    };
    private VideoView mVideoView;

    /* loaded from: classes.dex */
    public static class EdgeModeGuideFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
        private int mEdgeType;
        private CheckBoxPreference mEdgeTypeBackPreference;
        private CheckBoxPreference mEdgeTypeCleanPreference;
        private CheckBoxPreference mEdgeTypePhotoPreference;

        private void init() {
            CheckBoxPreference checkBoxPreference = this.mEdgeTypePhotoPreference;
            if (checkBoxPreference != null) {
                checkBoxPreference.setChecked(Settings.System.getInt(getContentResolver(), "edge_handgrip_photo", 0) == 1);
            }
            CheckBoxPreference checkBoxPreference2 = this.mEdgeTypeCleanPreference;
            if (checkBoxPreference2 != null) {
                checkBoxPreference2.setChecked(Settings.System.getInt(getContentResolver(), "edge_handgrip_clean", 0) == 1);
            }
            CheckBoxPreference checkBoxPreference3 = this.mEdgeTypeBackPreference;
            if (checkBoxPreference3 != null) {
                checkBoxPreference3.setChecked(Settings.System.getInt(getContentResolver(), "edge_handgrip_back", 0) == 1);
            }
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            CheckBoxPreference checkBoxPreference;
            CheckBoxPreference checkBoxPreference2;
            CheckBoxPreference checkBoxPreference3;
            super.onCreatePreferences(bundle, str);
            addPreferencesFromResource(R.xml.edge_mode_guide);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            this.mEdgeTypePhotoPreference = (CheckBoxPreference) preferenceScreen.findPreference("edge_mode_photo");
            this.mEdgeTypeBackPreference = (CheckBoxPreference) preferenceScreen.findPreference("edge_mode_back");
            this.mEdgeTypeCleanPreference = (CheckBoxPreference) preferenceScreen.findPreference("edge_mode_clean");
            this.mEdgeTypePhotoPreference.setOnPreferenceChangeListener(this);
            this.mEdgeTypeBackPreference.setOnPreferenceChangeListener(this);
            this.mEdgeTypeCleanPreference.setOnPreferenceChangeListener(this);
            if (this.mEdgeType != 2 && (checkBoxPreference3 = this.mEdgeTypePhotoPreference) != null) {
                preferenceScreen.removePreference(checkBoxPreference3);
                this.mEdgeTypePhotoPreference = null;
            }
            if (this.mEdgeType != 1 && (checkBoxPreference2 = this.mEdgeTypeCleanPreference) != null) {
                preferenceScreen.removePreference(checkBoxPreference2);
                this.mEdgeTypeCleanPreference = null;
            }
            if (this.mEdgeType == 0 || (checkBoxPreference = this.mEdgeTypeBackPreference) == null) {
                return;
            }
            preferenceScreen.removePreference(checkBoxPreference);
            this.mEdgeTypeBackPreference = null;
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            CheckBoxPreference checkBoxPreference = this.mEdgeTypePhotoPreference;
            if (checkBoxPreference != null) {
                checkBoxPreference.setOnPreferenceChangeListener(null);
            }
            CheckBoxPreference checkBoxPreference2 = this.mEdgeTypeBackPreference;
            if (checkBoxPreference2 != null) {
                checkBoxPreference2.setOnPreferenceChangeListener(null);
            }
            CheckBoxPreference checkBoxPreference3 = this.mEdgeTypeCleanPreference;
            if (checkBoxPreference3 != null) {
                checkBoxPreference3.setOnPreferenceChangeListener(null);
            }
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            String key = preference.getKey();
            if ("edge_mode_photo".equals(key)) {
                Settings.System.putInt(getContentResolver(), "edge_handgrip_photo", ((Boolean) obj).booleanValue() ? 1 : 0);
            } else if ("edge_mode_clean".equals(key)) {
                Settings.System.putInt(getContentResolver(), "edge_handgrip_clean", ((Boolean) obj).booleanValue() ? 1 : 0);
            } else if ("edge_mode_back".equals(key)) {
                Settings.System.putInt(getContentResolver(), "edge_handgrip_back", ((Boolean) obj).booleanValue() ? 1 : 0);
            }
            int i = ((Settings.System.getInt(getContentResolver(), "edge_handgrip_photo", 0) | Settings.System.getInt(getContentResolver(), "edge_handgrip_clean", 0)) | Settings.System.getInt(getContentResolver(), "edge_handgrip_back", 0)) == 1 ? 1 : 0;
            Settings.System.putInt(getContentResolver(), "edge_handgrip", i);
            MiuiSettingsCompatibilityHelper.switchInputManagerTouchEdgeMode((InputManager) getSystemService("input"), i == 1 ? 2 : 0);
            return false;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            preference.getKey();
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            init();
        }

        public void setEdgeType(int i) {
            this.mEdgeType = i;
        }
    }

    private void updateEdgeModeVideo() {
        this.mVideoView = (VideoView) findViewById(R.id.video);
        String str = "android.resource://" + getPackageName() + "/" + R.raw.photo;
        int i = this.mEdgeType;
        if (i == 1) {
            str = "android.resource://" + getPackageName() + "/" + R.raw.clean;
        } else if (i == 0) {
            str = "android.resource://" + getPackageName() + "/" + R.raw.back;
        }
        this.mVideoView.setVideoURI(Uri.parse(str));
        this.mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.EdgeModeGuideActivity.3
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                EdgeModeGuideActivity.this.mHandler.sendMessageDelayed(EdgeModeGuideActivity.this.mHandler.obtainMessage(1), 500L);
            }
        });
        this.mVideoView.setZOrderOnTop(true);
        this.mVideoView.start();
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.edge_mode_guide);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mEdgeType = extras.getInt("edge_mode_type");
        }
        EdgeModeGuideFragment edgeModeGuideFragment = new EdgeModeGuideFragment();
        this.mFragment = edgeModeGuideFragment;
        edgeModeGuideFragment.setEdgeType(this.mEdgeType);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.preference_container, this.mFragment).commit();
        }
        String string = getResources().getString(TITTLE_RES[this.mEdgeType]);
        ((TextView) findViewById(R.id.title)).setText(string);
        View findViewById = findViewById(R.id.action_bar_back);
        findViewById.setContentDescription(string);
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.EdgeModeGuideActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EdgeModeGuideActivity.this.finish();
            }
        });
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        updateEdgeModeVideo();
    }
}
