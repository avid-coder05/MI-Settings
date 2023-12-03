package com.android.settings.identification;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.id.IdentifierManager;
import com.android.security.AdbUtils;
import com.android.settings.MiuiSettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.search.tree.SecuritySettingsTree;
import java.lang.ref.WeakReference;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class OAIDSettings extends MiuiSettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private CheckBoxPreference mAllowOAIDUsedPref;
    private int mAutoNextStepTime;
    private Button mBtnAllow;
    private DecrementTask mHandler;
    private Intent mOAIDApps;
    private Preference mOAIDAppsManagePref;
    private Preference mOAIDRestorePref;
    private Preference mOAIDStringPref;
    private ContentObserver mObserver;
    private final Uri mOAIDSwitchUri = Settings.Secure.getUriFor("allow_oaid_used");
    private boolean mIsSupportOAIDApps = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DecrementTask extends Handler {
        private WeakReference<OAIDSettings> mFragment;

        public DecrementTask(OAIDSettings oAIDSettings) {
            this.mFragment = new WeakReference<>(oAIDSettings);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            OAIDSettings oAIDSettings = this.mFragment.get();
            FragmentActivity activity = oAIDSettings.getActivity();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
            oAIDSettings.processNextTask();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processNextTask() {
        Button button = this.mBtnAllow;
        if (button == null) {
            return;
        }
        int i = this.mAutoNextStepTime - 1;
        this.mAutoNextStepTime = i;
        if (i <= 0) {
            button.setText(R.string.restore_oaid_positive);
            this.mBtnAllow.setEnabled(true);
            this.mAutoNextStepTime = 0;
            return;
        }
        button.setText(getContext().getString(R.string.restore_oaid_positive_step, Integer.valueOf(this.mAutoNextStepTime)));
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageDelayed(1, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOAIDStringPrefContent() {
        String oaid = IdentifierManager.getOAID(getContext());
        if (TextUtils.isEmpty(oaid)) {
            this.mOAIDStringPref.setSummary("");
        } else {
            this.mOAIDStringPref.setSummary(getContext().getString(R.string.oaid_string, oaid));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void trackEvent(String str, String str2, String str3) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return OAIDSettings.class.getName();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 666) {
            if (i == 999) {
                setOAIDStringPrefContent();
            }
        } else if (i2 != -1) {
            this.mAllowOAIDUsedPref.setChecked(true);
        } else {
            Settings.Secure.putInt(getContentResolver(), "allow_oaid_used", 0);
            this.mAllowOAIDUsedPref.setChecked(false);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.layout.oaid_settings);
        this.mAutoNextStepTime = 5;
        this.mOAIDRestorePref = findPreference("restore_oaid");
        this.mOAIDStringPref = findPreference("oaid_string");
        this.mOAIDAppsManagePref = findPreference("oaid_apps_manage");
        this.mAllowOAIDUsedPref = (CheckBoxPreference) findPreference("allow_oaid_used");
        this.mOAIDRestorePref.setOnPreferenceClickListener(this);
        this.mAllowOAIDUsedPref.setOnPreferenceChangeListener(this);
        this.mOAIDAppsManagePref.setOnPreferenceClickListener(this);
        this.mAllowOAIDUsedPref.setChecked(Settings.Secure.getInt(getContentResolver(), "allow_oaid_used", 1) == 1);
        this.mObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.identification.OAIDSettings.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                OAIDSettings.this.setOAIDStringPrefContent();
            }
        };
        getContentResolver().registerContentObserver(this.mOAIDSwitchUri, false, this.mObserver);
        setOAIDStringPrefContent();
        Intent intent = new Intent("miui.intent.action.OAID_APPS");
        this.mOAIDApps = intent;
        intent.setPackage(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME);
        boolean z = getPackageManager().queryIntentActivities(this.mOAIDApps, 0).size() > 0;
        this.mIsSupportOAIDApps = z;
        if (z) {
            this.mOAIDAppsManagePref.setVisible(true);
            this.mAllowOAIDUsedPref.setVisible(false);
            return;
        }
        this.mOAIDAppsManagePref.setVisible(false);
        this.mAllowOAIDUsedPref.setVisible(true);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(this.mObserver);
        DecrementTask decrementTask = this.mHandler;
        if (decrementTask != null) {
            decrementTask.removeMessages(1);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mAllowOAIDUsedPref) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            Intent interceptIntent = AdbUtils.getInterceptIntent("", "oaid_close", "");
            if (booleanValue || !AdbUtils.isIntentEnable(getContext(), interceptIntent)) {
                Settings.Secure.putInt(getContentResolver(), "allow_oaid_used", booleanValue ? 1 : 0);
            } else {
                startActivityForResult(interceptIntent, 666);
                this.mAllowOAIDUsedPref.setChecked(true);
            }
        }
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mOAIDRestorePref) {
            Button button = new AlertDialog.Builder(getContext(), R.style.AlertDialog_Theme_DayNight).setTitle(R.string.restore_oaid_title).setMessage(R.string.restore_oaid_dialog_content).setNegativeButton(R.string.restore_oaid_positive, new DialogInterface.OnClickListener() { // from class: com.android.settings.identification.OAIDSettings.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    OAIDSettings.this.getContentResolver().update(Uri.parse("content://com.miui.idprovider/oaid"), new ContentValues(), null, null);
                    OAIDSettings.this.setOAIDStringPrefContent();
                    OAIDSettings.this.trackEvent("oaid_reset", "oaid_reset", "click_oaid_reset");
                }
            }).setPositiveButton(17039360, (DialogInterface.OnClickListener) null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.identification.OAIDSettings.2
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    OAIDSettings.this.mAutoNextStepTime = 5;
                    OAIDSettings.this.mBtnAllow = null;
                }
            }).show().getButton(-2);
            this.mBtnAllow = button;
            button.setEnabled(false);
            if (this.mHandler == null) {
                this.mHandler = new DecrementTask(this);
            }
            this.mBtnAllow.setText(getContext().getString(R.string.restore_oaid_positive_step, Integer.valueOf(this.mAutoNextStepTime)));
            this.mHandler.sendEmptyMessageDelayed(1, 1000L);
        } else if (this.mOAIDAppsManagePref == preference && this.mIsSupportOAIDApps) {
            startActivityForResult(this.mOAIDApps, 999);
        }
        return false;
    }
}
