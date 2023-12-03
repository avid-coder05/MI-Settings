package com.android.settings.utils;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.utils.ManagedServiceSettings;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.applications.ServiceListing;
import java.util.List;
import miui.provider.MiCloudSmsCmd;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public abstract class ManagedServiceSettings extends EmptyTextSettings {
    private final Config mConfig = getConfig();
    protected Context mContext;
    private DevicePolicyManager mDpm;
    private IconDrawableFactory mIconDrawableFactory;
    private PackageManager mPm;
    private ServiceListing mServiceListing;

    /* loaded from: classes2.dex */
    public static class Config {
        public final String configIntentAction;
        public final int emptyText;
        public final String intentAction;
        public final String noun;
        public final String permission;
        public final String setting;
        public final String tag;
        public final int warningDialogSummary;
        public final int warningDialogTitle;

        /* loaded from: classes2.dex */
        public static class Builder {
            private String mConfigIntentAction;
            private int mEmptyText;
            private String mIntentAction;
            private String mNoun;
            private String mPermission;
            private String mSetting;
            private String mTag;
            private int mWarningDialogSummary;
            private int mWarningDialogTitle;

            public Config build() {
                return new Config(this.mTag, this.mSetting, this.mIntentAction, this.mConfigIntentAction, this.mPermission, this.mNoun, this.mWarningDialogTitle, this.mWarningDialogSummary, this.mEmptyText);
            }

            public Builder setConfigurationIntentAction(String str) {
                this.mConfigIntentAction = str;
                return this;
            }

            public Builder setEmptyText(int i) {
                this.mEmptyText = i;
                return this;
            }

            public Builder setIntentAction(String str) {
                this.mIntentAction = str;
                return this;
            }

            public Builder setNoun(String str) {
                this.mNoun = str;
                return this;
            }

            public Builder setPermission(String str) {
                this.mPermission = str;
                return this;
            }

            public Builder setSetting(String str) {
                this.mSetting = str;
                return this;
            }

            public Builder setTag(String str) {
                this.mTag = str;
                return this;
            }

            public Builder setWarningDialogSummary(int i) {
                this.mWarningDialogSummary = i;
                return this;
            }

            public Builder setWarningDialogTitle(int i) {
                this.mWarningDialogTitle = i;
                return this;
            }
        }

        private Config(String str, String str2, String str3, String str4, String str5, String str6, int i, int i2, int i3) {
            this.tag = str;
            this.setting = str2;
            this.intentAction = str3;
            this.permission = str5;
            this.noun = str6;
            this.warningDialogTitle = i;
            this.warningDialogSummary = i2;
            this.emptyText = i3;
            this.configIntentAction = str4;
        }
    }

    /* loaded from: classes2.dex */
    public interface OnCheckResult {
        void onResult(boolean z);
    }

    /* loaded from: classes2.dex */
    public static class ScaryWarningDialogFragment extends InstrumentedDialogFragment {
        private OnCheckResult mCallback;

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateDialog$0(ManagedServiceSettings managedServiceSettings, ComponentName componentName, DialogInterface dialogInterface, int i) {
            managedServiceSettings.enable(componentName);
            OnCheckResult onCheckResult = this.mCallback;
            if (onCheckResult != null) {
                onCheckResult.onResult(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
            OnCheckResult onCheckResult = this.mCallback;
            if (onCheckResult != null) {
                onCheckResult.onResult(false);
            }
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 557;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            Bundle arguments = getArguments();
            String string = arguments.getString(MiCloudSmsCmd.TYPE_LOCATION);
            final ComponentName unflattenFromString = ComponentName.unflattenFromString(arguments.getString("c"));
            final ManagedServiceSettings managedServiceSettings = (ManagedServiceSettings) getTargetFragment();
            return new AlertDialog.Builder(getContext()).setMessage(getResources().getString(managedServiceSettings.mConfig.warningDialogSummary, string)).setTitle(getResources().getString(managedServiceSettings.mConfig.warningDialogTitle, string)).setCancelable(true).setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() { // from class: com.android.settings.utils.ManagedServiceSettings$ScaryWarningDialogFragment$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ManagedServiceSettings.ScaryWarningDialogFragment.this.lambda$onCreateDialog$0(managedServiceSettings, unflattenFromString, dialogInterface, i);
                }
            }).setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() { // from class: com.android.settings.utils.ManagedServiceSettings$ScaryWarningDialogFragment$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ManagedServiceSettings.ScaryWarningDialogFragment.this.lambda$onCreateDialog$1(dialogInterface, i);
                }
            }).create();
        }

        public void setCallback(OnCheckResult onCheckResult) {
            this.mCallback = onCheckResult;
        }

        public ScaryWarningDialogFragment setServiceInfo(ComponentName componentName, String str, Fragment fragment) {
            Bundle bundle = new Bundle();
            bundle.putString("c", componentName.flattenToString());
            bundle.putString(MiCloudSmsCmd.TYPE_LOCATION, str);
            setArguments(bundle);
            setTargetFragment(fragment, 0);
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateList$0(CharSequence charSequence, ComponentName componentName, OnCheckResult onCheckResult, String str, Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (charSequence != null) {
            setEnabled(componentName, charSequence.toString(), booleanValue, onCheckResult);
            return true;
        }
        setEnabled(componentName, str, booleanValue, onCheckResult);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateList(List<ServiceInfo> list) {
        int managedProfileId = com.android.settings.Utils.getManagedProfileId((UserManager) this.mContext.getSystemService("user"), UserHandle.myUserId());
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        list.sort(new PackageItemInfo.DisplayNameComparator(this.mPm));
        for (ServiceInfo serviceInfo : list) {
            final ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
            CharSequence charSequence = null;
            try {
                charSequence = this.mPm.getApplicationInfoAsUser(serviceInfo.packageName, 0, UserHandle.myUserId()).loadLabel(this.mPm);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("ManagedServiceSettings", "can't find package name", e);
            }
            final CharSequence charSequence2 = charSequence;
            final String charSequence3 = serviceInfo.loadLabel(this.mPm).toString();
            final CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
            checkBoxPreference.setPersistent(false);
            IconDrawableFactory iconDrawableFactory = this.mIconDrawableFactory;
            ApplicationInfo applicationInfo = serviceInfo.applicationInfo;
            checkBoxPreference.setIcon(iconDrawableFactory.getBadgedIcon(serviceInfo, applicationInfo, UserHandle.getUserId(applicationInfo.uid)));
            if (charSequence2 == null || charSequence2.equals(charSequence3)) {
                checkBoxPreference.setTitle(charSequence3);
            } else {
                checkBoxPreference.setTitle(charSequence2);
                checkBoxPreference.setSummary(charSequence3);
            }
            checkBoxPreference.setKey(componentName.flattenToString());
            checkBoxPreference.setChecked(isServiceEnabled(componentName));
            if (managedProfileId != -10000 && !this.mDpm.isNotificationListenerServicePermitted(serviceInfo.packageName, managedProfileId)) {
                checkBoxPreference.setSummary(R.string.work_profile_notification_access_blocked_summary);
            }
            final OnCheckResult onCheckResult = new OnCheckResult() { // from class: com.android.settings.utils.ManagedServiceSettings.1
                @Override // com.android.settings.utils.ManagedServiceSettings.OnCheckResult
                public void onResult(boolean z) {
                    checkBoxPreference.setChecked(ManagedServiceSettings.this.isServiceEnabled(componentName));
                }
            };
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.utils.ManagedServiceSettings$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    boolean lambda$updateList$0;
                    lambda$updateList$0 = ManagedServiceSettings.this.lambda$updateList$0(charSequence2, componentName, onCheckResult, charSequence3, preference, obj);
                    return lambda$updateList$0;
                }
            });
            checkBoxPreference.setKey(componentName.flattenToString());
            preferenceScreen.addPreference(checkBoxPreference);
        }
        highlightPreferenceIfNeeded();
    }

    protected void enable(ComponentName componentName) {
        this.mServiceListing.setEnabled(componentName, true);
    }

    protected abstract Config getConfig();

    protected boolean isServiceEnabled(ComponentName componentName) {
        return this.mServiceListing.isEnabled(componentName);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mPm = activity.getPackageManager();
        this.mDpm = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(this.mContext);
        ServiceListing build = new ServiceListing.Builder(this.mContext).setPermission(this.mConfig.permission).setIntentAction(this.mConfig.intentAction).setNoun(this.mConfig.noun).setSetting(this.mConfig.setting).setTag(this.mConfig.tag).build();
        this.mServiceListing = build;
        build.addCallback(new ServiceListing.Callback() { // from class: com.android.settings.utils.ManagedServiceSettings$$ExternalSyntheticLambda1
            @Override // com.android.settingslib.applications.ServiceListing.Callback
            public final void onServicesReloaded(List list) {
                ManagedServiceSettings.this.updateList(list);
            }
        });
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getPrefContext()));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mServiceListing.setListening(false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mServiceListing.reload();
        this.mServiceListing.setListening(true);
    }

    @Override // com.android.settings.widget.EmptyTextSettings, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setEmptyText(this.mConfig.emptyText);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean setEnabled(ComponentName componentName, String str, boolean z, OnCheckResult onCheckResult) {
        if (!z) {
            this.mServiceListing.setEnabled(componentName, false);
            return true;
        } else if (this.mServiceListing.isEnabled(componentName)) {
            return true;
        } else {
            ScaryWarningDialogFragment scaryWarningDialogFragment = new ScaryWarningDialogFragment();
            scaryWarningDialogFragment.setServiceInfo(componentName, str, this);
            scaryWarningDialogFragment.setCallback(onCheckResult);
            scaryWarningDialogFragment.show(getFragmentManager(), "dialog");
            return false;
        }
    }
}
