package com.android.settings.device;

import android.app.AppGlobals;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.settings.PlatformUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.credentials.MiuiCredentialsUpdater;
import com.android.settings.report.InternationalCompat;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.widget.CustomValuePreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.Utils;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiDeviceInfoSettings extends SettingsPreferenceFragment {
    private RestrictedLockUtils.EnforcedAdmin mDebuggingFeaturesDisallowedAdmin;
    private boolean mDebuggingFeaturesDisallowedBySystem;
    int mDevHitCountdown;
    Toast mDevHitToast;
    private ValuePreference mDeviceName;
    private RestrictedLockUtils.EnforcedAdmin mFunDisallowedAdmin;
    private boolean mFunDisallowedBySystem;
    private boolean mIsOwnerUser;
    String mLastHitKey;
    private ReadDeviceInfoTask mReadDeviceInfoTask;
    private UserManager mUm;
    private CustomValuePreference mUpdater;
    private ArrayList<String> mVerfDeviceList;
    long[] mHits = new long[3];
    int mPrefHitCountdown = 4;
    long mLastPrefHitTime = 0;
    private final String VERFICATION_DEVICE_LIST = "show_verification_device_list";

    /* loaded from: classes.dex */
    public static class ReadDeviceInfoTask extends AsyncTask<Void, Void, Void> {
        private String mCpuInfo;
        private WeakReference<MiuiDeviceInfoSettings> mOuterRef;
        private String mTotalRam;

        public ReadDeviceInfoTask(MiuiDeviceInfoSettings miuiDeviceInfoSettings) {
            this.mOuterRef = new WeakReference<>(miuiDeviceInfoSettings);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            Application initialApplication = AppGlobals.getInitialApplication();
            this.mCpuInfo = MiuiAboutPhoneUtils.getInstance(initialApplication).getCpuInfo();
            this.mTotalRam = MiuiAboutPhoneUtils.getInstance(initialApplication).getTotaolRam();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r2) {
            MiuiDeviceInfoSettings miuiDeviceInfoSettings = this.mOuterRef.get();
            if (miuiDeviceInfoSettings != null) {
                miuiDeviceInfoSettings.handleTaskResult(this.mCpuInfo, this.mTotalRam);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTaskResult(String str, String str2) {
        ((ValuePreference) findPreference("device_cpu")).setValue(str);
        ((ValuePreference) findPreference("device_memory")).setValue(str2);
    }

    private void removePreferenceIfPropertyMissing(PreferenceGroup preferenceGroup, String str, String str2) {
        if (SystemProperties.get(str2).equals("")) {
            try {
                preferenceGroup.removePreference(findPreference(str));
            } catch (RuntimeException unused) {
                Log.d("MiuiDeviceInfoSettings", "Property '" + str2 + "' missing and no '" + str + "' preference");
            }
        }
    }

    private void setCredentialTitle() {
        if (this.mVerfDeviceList == null) {
            this.mVerfDeviceList = new ArrayList<>();
            this.mVerfDeviceList.addAll(Arrays.asList(MiuiAboutPhoneUtils.queryStringArray(getActivity(), "show_verification_device_list")));
        }
        ((PreferenceGroup) findPreference("credentials")).setTitle(this.mVerfDeviceList.contains(Build.DEVICE) ? R.string.credentials_title_verification : R.string.approve_title);
    }

    private void setMiuiVersionInfo() {
        setStringTitle("device_miui_version", MiuiAboutPhoneUtils.getInstance(getActivity()).isPocoDevice() ? getResources().getString(R.string.device_miui_version_for_POCO) : getResources().getString(R.string.device_miui_version));
        setStringSummary("device_miui_version", MiuiAboutPhoneUtils.getMiuiVersion(getActivity()));
    }

    private void setStringSummary(String str, String str2) {
        ValuePreference valuePreference = (ValuePreference) findPreference(str);
        try {
            valuePreference.setValue(str2);
        } catch (RuntimeException unused) {
            valuePreference.setValue(getResources().getString(R.string.device_info_default));
        }
    }

    private void setStringTitle(String str, String str2) {
        ValuePreference valuePreference = (ValuePreference) findPreference(str);
        if (valuePreference != null) {
            valuePreference.setTitle(str2);
        }
    }

    private void setValueSummary(String str, String str2) {
        try {
            ((ValuePreference) findPreference(str)).setValue(PlatformUtils.getTelephonyProperty(str2, 0, getResources().getString(R.string.device_info_default)));
        } catch (RuntimeException unused) {
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiDeviceInfoSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        String str;
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.device_info_settings);
        this.mUm = UserManager.get(getActivity());
        boolean z = UserHandle.myUserId() == 0;
        this.mIsOwnerUser = z;
        setHasOptionsMenu(z);
        this.mUpdater = (CustomValuePreference) findPreference("miui_update");
        if (this.mIsOwnerUser && !TextUtils.isEmpty(MiuiAboutPhoneUtils.getUpdateInfo(getActivity()))) {
            this.mUpdater.showRedPoint(2);
            InternationalCompat.trackReportEvent("system_update_pv");
        }
        ValuePreference valuePreference = (ValuePreference) findPreference("device_opcust_version");
        if (SettingsFeatures.IS_NEED_OPCUST_VERSION) {
            String str2 = SystemProperties.get("ro.miui.opcust.version", "");
            String opconfigVersion = MiuiAboutPhoneUtils.getOpconfigVersion();
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            if (opconfigVersion != null) {
                str = "\n" + opconfigVersion;
            } else {
                str = "";
            }
            sb.append(str);
            valuePreference.setValue(sb.toString());
        } else {
            getPreferenceScreen().removePreference(valuePreference);
        }
        ((ValuePreference) findPreference("model_number")).setValue(MiuiAboutPhoneUtils.getModelNumber());
        ValuePreference valuePreference2 = (ValuePreference) findPreference("model_name");
        String globalCertNumber = MiuiCredentialsUpdater.getGlobalCertNumber();
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD || TextUtils.isEmpty(globalCertNumber)) {
            getPreferenceScreen().removePreference(valuePreference2);
        } else {
            valuePreference2.setValue(globalCertNumber.toUpperCase());
        }
        ValuePreference valuePreference3 = (ValuePreference) findPreference("firmware_version");
        if (miui.os.Build.IS_CU_CUSTOMIZATION_TEST) {
            valuePreference3.setValue(Build.VERSION.RELEASE);
        } else {
            valuePreference3.setValue(Build.VERSION.RELEASE + " " + Build.ID);
        }
        String str3 = Build.VERSION.SECURITY_PATCH;
        if (TextUtils.isEmpty(str3)) {
            getPreferenceScreen().removePreference(findPreference("security_patch"));
        } else {
            setStringSummary("security_patch", str3);
        }
        setMiuiVersionInfo();
        int i = R.string.reading_data;
        setStringSummary("device_cpu", getString(i));
        setStringSummary("device_memory", getString(i));
        if (this.mReadDeviceInfoTask == null) {
            ReadDeviceInfoTask readDeviceInfoTask = new ReadDeviceInfoTask(this);
            this.mReadDeviceInfoTask = readDeviceInfoTask;
            readDeviceInfoTask.execute(new Void[0]);
        }
        if (Utils.isWifiOnly(getActivity())) {
            Preference findPreference = findPreference("baseband_version");
            if (findPreference != null) {
                getPreferenceScreen().removePreference(findPreference);
            }
        } else {
            setValueSummary("baseband_version", "gsm.version.baseband");
        }
        ((ValuePreference) findPreference("kernel_version")).setValue(MiuiAboutPhoneUtils.getFormattedKernelVersion());
        ValuePreference valuePreference4 = (ValuePreference) findPreference("hardware_version");
        String str4 = SystemProperties.get("ro.miui.cust_hardware", "");
        if (!TextUtils.isEmpty(str4)) {
            valuePreference4.setValue(str4);
        } else if (valuePreference4 != null) {
            getPreferenceScreen().removePreference(valuePreference4);
        }
        ValuePreference valuePreference5 = (ValuePreference) findPreference("wifi_type_approval");
        if (TextUtils.isEmpty(getActivity().getResources().getString(R.string.wifi_type_approval))) {
            getPreferenceScreen().removePreference(valuePreference5);
        } else {
            valuePreference5.setTitle(R.string.wifi_type_approval_dialog_title);
            valuePreference5.setShowRightArrow(true);
        }
        PreferenceCategory preferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference("user");
        removePreferenceIfPropertyMissing(preferenceCategory, "safetylegal", "ro.url.safetylegal");
        FragmentActivity activity = getActivity();
        com.android.settings.Utils.updatePreferenceToSpecificActivityOrRemove(activity, (PreferenceGroup) findPreference("container"), "team", 1);
        com.android.settings.Utils.updatePreferenceToSpecificActivityOrRemove(activity, getPreferenceScreen(), "contributors", 1);
        this.mDeviceName = (ValuePreference) findPreference("device_name");
        if (MiuiAboutPhoneUtils.enableShowCredentials()) {
            setCredentialTitle();
        } else {
            preferenceCategory.removePreference(findPreference("credentials"));
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            create.setView(LayoutInflater.from(getActivity()).inflate(R.layout.type_approved_content, (ViewGroup) null));
            return create;
        }
        return super.onCreateDialog(i);
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0104  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0141  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x01b4  */
    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onPreferenceTreeClick(androidx.preference.PreferenceScreen r16, androidx.preference.Preference r17) {
        /*
            Method dump skipped, instructions count: 792
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.device.MiuiDeviceInfoSettings.onPreferenceTreeClick(androidx.preference.PreferenceScreen, androidx.preference.Preference):boolean");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mFunDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_fun", UserHandle.myUserId());
        this.mFunDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_fun", UserHandle.myUserId());
        this.mDebuggingFeaturesDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_debugging_features", UserHandle.myUserId());
        this.mDebuggingFeaturesDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_debugging_features", UserHandle.myUserId());
        this.mDeviceName.setValue(MiuiSettings.System.getDeviceName(getActivity()));
        this.mDeviceName.setShowRightArrow(true);
        this.mDeviceName.setEnabled(this.mIsOwnerUser);
        this.mUpdater.setShowRightArrow(true);
        this.mUpdater.setEnabled(this.mIsOwnerUser);
        ((ValuePreference) findPreference("device_internal_memory")).setValue(MiuiAboutPhoneUtils.getInstance(getActivity()).fillOverview());
        this.mDevHitCountdown = DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getActivity()) ? -1 : 7;
        this.mDevHitToast = null;
    }
}
