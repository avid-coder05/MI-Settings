package com.android.settings.display;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Slog;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.report.InternationalCompat;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class HandyModeFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private MyHandler mHandler;
    private CheckBoxPreference mHandyModeState;
    private ContentResolver mResolver;
    private int mUserId;

    /* loaded from: classes.dex */
    private static class MyHandler extends Handler {
        WeakReference<HandyModeFragment> weakReference;

        public MyHandler(HandyModeFragment handyModeFragment, Looper looper) {
            super(looper);
            this.weakReference = new WeakReference<>(handyModeFragment);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            Settings.Secure.putIntForUser((ContentResolver) message.obj, "one_handed_mode_enabled", message.arg1, UserHandle.myUserId());
            Slog.d("HandyModeFragment", "handleMessage ONE_HANDED_MODE_ENABLED=" + message.arg1);
        }
    }

    private void initPreference() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("handy_mode_state");
        this.mHandyModeState = checkBoxPreference;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(this);
        }
    }

    private void updateStateOnlyCheckBox() {
        if (this.mHandyModeState != null) {
            boolean z = MiuiSettings.Global.getBoolean(this.mResolver, "force_fsg_nav_bar");
            String string = getString(R.string.handy_mode_tips_full_screen);
            if (z) {
                this.mHandyModeState.setSummary(string);
            }
            boolean z2 = Settings.Secure.getIntForUser(this.mResolver, "one_handed_mode_enabled", 0, this.mUserId) == 1;
            this.mHandyModeState.setChecked(z2);
            Slog.d("HandyModeFragment", "updateStateOnlyCheckBox : " + z2);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return HandyModeFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.handy_mode_settings);
        this.mUserId = UserHandle.myUserId();
        this.mHandler = new MyHandler(this, getActivity().getMainLooper());
        this.mResolver = getActivity().getContentResolver();
        initPreference();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        InternationalCompat.trackReportSwitchStatus("setting_Additional_settings_onehand", obj);
        boolean booleanValue = ((Boolean) obj).booleanValue();
        String key = preference.getKey();
        key.hashCode();
        if (key.equals("handy_mode_state")) {
            Message message = new Message();
            message.what = 1;
            message.arg1 = booleanValue ? 1 : 0;
            message.obj = this.mResolver;
            this.mHandler.sendMessageDelayed(message, 100L);
            Slog.d("HandyModeFragment", "onPreferenceChange ONE_HANDED_MODE_ENABLED=" + booleanValue);
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateStateOnlyCheckBox();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (getActionBar() != null) {
            getActionBar().setTitle(R.string.handy_mode);
        }
    }
}
