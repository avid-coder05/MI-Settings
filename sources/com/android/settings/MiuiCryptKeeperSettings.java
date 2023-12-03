package com.android.settings;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.storage.IStorageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.CryptKeeperConfirm;
import java.util.Locale;
import miui.os.Build;
import miui.provider.Weather;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiCryptKeeperSettings extends SettingsPreferenceFragment {
    private AlertDialog mAlertDialog;
    private AlertDialog mConfirmDialog;
    private Button mInitiateButton;
    private IntentFilter mIntentFilter;
    private TextView mWarning;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.settings.MiuiCryptKeeperSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                int i = 0;
                int intExtra = intent.getIntExtra(Weather.AlertInfo.LEVEL, 0);
                int intExtra2 = intent.getIntExtra("plugged", 0);
                int intExtra3 = intent.getIntExtra("invalid_charger", 0);
                boolean equals = "activated".equals(SystemProperties.get("vold.pfe"));
                boolean z = intExtra >= 80;
                boolean z2 = (intExtra2 & 7) != 0 && intExtra3 == 0;
                MiuiCryptKeeperSettings.this.mInitiateButton.setEnabled(z && z2 && !equals);
                MiuiCryptKeeperSettings.this.mWarning.setText(context.getString(z ? R.string.crypt_keeper_unplugged_text : R.string.crypt_keeper_low_charge_text));
                TextView textView = MiuiCryptKeeperSettings.this.mWarning;
                if (z && z2) {
                    i = 8;
                }
                textView.setVisibility(i);
            }
        }
    };
    private View.OnClickListener mInitiateListener = new View.OnClickListener() { // from class: com.android.settings.MiuiCryptKeeperSettings.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (MiuiCryptKeeperSettings.this.runKeyguardConfirmation(55)) {
                return;
            }
            MiuiCryptKeeperSettings miuiCryptKeeperSettings = MiuiCryptKeeperSettings.this;
            miuiCryptKeeperSettings.mAlertDialog = new AlertDialog.Builder(miuiCryptKeeperSettings.getActivity()).setTitle(R.string.crypt_keeper_dialog_need_password_title).setMessage(R.string.crypt_keeper_dialog_need_password_message).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create();
            MiuiCryptKeeperSettings.this.mAlertDialog.show();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public boolean runKeyguardConfirmation(int i) {
        return false;
    }

    private void showFinalConfirmation(final int i, final String str) {
        AlertDialog create = new AlertDialog.Builder(getActivity()).setTitle(R.string.crypt_keeper_encrypt_title).setIconAttribute(16843605).setMessage(R.string.crypt_keeper_final_desc).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiCryptKeeperSettings.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                if (Utils.isMonkeyRunning()) {
                    return;
                }
                LockPatternUtils lockPatternUtils = new LockPatternUtils(MiuiCryptKeeperSettings.this.getActivity());
                lockPatternUtils.setVisiblePatternEnabled(lockPatternUtils.isVisiblePatternEnabled(0), 0);
                if (lockPatternUtils.isOwnerInfoEnabled(0)) {
                    lockPatternUtils.setOwnerInfo(lockPatternUtils.getOwnerInfo(0), 0);
                }
                lockPatternUtils.setVisiblePasswordEnabled(Settings.System.getInt(MiuiCryptKeeperSettings.this.getContext().getContentResolver(), "show_password", 1) != 0, 0);
                Intent intent = new Intent(MiuiCryptKeeperSettings.this.getActivity(), CryptKeeperConfirm.Blank.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", i);
                bundle.putString("password", str);
                intent.putExtras(bundle);
                MiuiCryptKeeperSettings.this.startActivity(intent);
                try {
                    IStorageManager.Stub.asInterface(ServiceManager.getService("mount")).setField("SystemLocale", Locale.getDefault().toLanguageTag());
                } catch (Exception e) {
                    Log.e("MiuiCryptKeeper", "Error storing locale for decryption UI", e);
                }
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        this.mConfirmDialog = create;
        create.show();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiCryptKeeperSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        DevicePolicyManager devicePolicyManager;
        Preference findPreference;
        super.onActivityCreated(bundle);
        addPreferencesFromResource(R.layout.crypt_keeper_settings_fragment);
        FragmentActivity activity = getActivity();
        Intent intent = activity.getIntent();
        if (!LockPatternUtils.isDeviceEncryptionEnabled() && !Build.HAS_CUST_PARTITION && (findPreference = findPreference("crypt_keeper_decrypt_methods_summary")) != null) {
            findPreference.setSummary(R.string.crypt_keeper_decrypt_methods_cannot_decrypt);
        }
        if (!"android.app.action.START_ENCRYPTION".equals(intent.getAction()) || (devicePolicyManager = (DevicePolicyManager) activity.getSystemService("device_policy")) == null || devicePolicyManager.getStorageEncryptionStatus() == 1) {
            return;
        }
        activity.finish();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 55 && i2 == -1 && intent != null) {
            intent.getIntExtra("type", -1);
            if (TextUtils.isEmpty(intent.getStringExtra("password"))) {
                return;
            }
            showFinalConfirmation(1, "");
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.crypt_keeper_information, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        return inflate;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mConfirmDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mConfirmDialog.dismiss();
        }
        AlertDialog alertDialog2 = this.mAlertDialog;
        if (alertDialog2 != null && alertDialog2.isShowing()) {
            this.mAlertDialog.dismiss();
        }
        this.mConfirmDialog = null;
        this.mAlertDialog = null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mIntentReceiver);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.mIntentReceiver, this.mIntentFilter);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        Button button = (Button) view.findViewById(R.id.initiate_encrypt);
        this.mInitiateButton = button;
        button.setOnClickListener(this.mInitiateListener);
        this.mInitiateButton.setEnabled(false);
        this.mWarning = (TextView) view.findViewById(R.id.warning_text);
    }
}
