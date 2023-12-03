package com.android.settings.dndmode;

import android.app.Activity;
import android.app.ExtraNotificationManager;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miui.provider.ExtraTelephony;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class QuietActivity extends AppCompatActivity {

    /* loaded from: classes.dex */
    public static class Quietragment extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, View.OnClickListener {
        private Activity mActivity;
        private ContentObserver mDoNotDisturbModeObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.dndmode.QuietActivity.Quietragment.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                Quietragment.this.switchModel(Settings.Global.getInt(Quietragment.this.mActivity.getContentResolver(), ExtraTelephony.ZEN_MODE, 0));
                super.onChange(z);
            }
        };
        private RadioButtonWithArrow off;
        private RadioButtonWithArrow quiet;
        private RadioButtonWithArrow silent;

        /* JADX INFO: Access modifiers changed from: private */
        public void switchModel(int i) {
            this.off.setChecked(false);
            this.silent.setChecked(false);
            this.quiet.setChecked(false);
            if (i == 0) {
                this.off.setChecked(true);
            } else if (i == 1) {
                this.quiet.setChecked(true);
            } else if (i == 2 || i == 3) {
                this.silent.setChecked(true);
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            String str = (String) view.getTag();
            Intent intent = new Intent("android.settings.ZEN_MODE_SETTINGS");
            if (str.equals("silent")) {
                intent.putExtra("switch", 2);
            } else if (str.equals("quiet")) {
                intent.putExtra("switch", 1);
            }
            this.mActivity.startActivity(intent);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.dnd_quiet_main);
            this.mActivity = getActivity();
            this.off = (RadioButtonWithArrow) findPreference("off");
            this.silent = (RadioButtonWithArrow) findPreference("silent");
            this.quiet = (RadioButtonWithArrow) findPreference("quiet");
            this.off.setOnPreferenceClickListener(this);
            this.off.setArrowVisibility(8);
            this.silent.setOnPreferenceClickListener(this);
            this.silent.setOnClickListeners(this);
            this.quiet.setOnPreferenceClickListener(this);
            this.quiet.setOnClickListeners(this);
            getActivity().getContentResolver().registerContentObserver(Settings.Global.getUriFor(ExtraTelephony.ZEN_MODE), false, this.mDoNotDisturbModeObserver);
            switchModel(Settings.Global.getInt(this.mActivity.getContentResolver(), ExtraTelephony.ZEN_MODE, 0));
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            if ("off".equals(preference.getKey())) {
                ExtraNotificationManager.setZenMode(this.mActivity, 0, (Uri) null);
            } else if ("silent".equals(preference.getKey())) {
                ExtraNotificationManager.setZenMode(this.mActivity, 2, (Uri) null);
            } else if ("quiet".equals(preference.getKey())) {
                ExtraNotificationManager.setZenMode(this.mActivity, 1, (Uri) null);
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dndm_activity_with_fragment);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new Quietragment()).commit();
        }
    }
}
