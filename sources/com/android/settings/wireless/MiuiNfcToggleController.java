package com.android.settings.wireless;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.RegionUtils;
import com.android.settings.nfc.NfcEnabler;
import com.android.settings.nfc.NfcPreferenceController;
import com.android.settings.nfc.NfcSeRoute;
import com.android.settings.nfc.SecureNfcEnabler;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.MainSwitchPreference;
import com.miui.enterprise.RestrictionsHelper;
import java.util.ArrayList;

/* loaded from: classes2.dex */
public class MiuiNfcToggleController extends AbstractPreferenceController implements LifecycleObserver, OnResume, OnPause {
    private NfcAdapter mNfcAdapter;
    private NfcEnabler mNfcEnabler;
    private NfcSeRoute mNfcSeRoute;
    private SecureNfcEnabler mSecureNfcEnabler;
    private boolean mSupportMultiRoute;

    public MiuiNfcToggleController(Context context, Lifecycle lifecycle) {
        super(context);
        lifecycle.addObserver(this);
        this.mSupportMultiRoute = this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc.hce");
    }

    private ListPreference getListPreference(PreferenceScreen preferenceScreen) {
        ListPreference listPreference = (ListPreference) preferenceScreen.findPreference("se_route");
        if (listPreference == null) {
            log("getListPreference called! seRoute:null");
            return null;
        }
        String str = SystemProperties.get("ro.se.type");
        if (TextUtils.isEmpty(str)) {
            log("getRoSeType is null from SystemProperties");
            str = SystemProperties.get("ro.vendor.se.type");
            if (TextUtils.isEmpty(str)) {
                str = "HCE,eSE";
            }
        }
        log("getRoSeType value:" + str);
        String[] split = str.toUpperCase().replace(" ", "").split(",");
        ArrayList arrayList = new ArrayList();
        for (String str2 : split) {
            if (!arrayList.contains(str2)) {
                if (TextUtils.equals(str2, "HCE")) {
                    arrayList.add(str2);
                } else if (TextUtils.equals(str2, "ESE")) {
                    arrayList.add(str2);
                } else if (TextUtils.equals(str2, "UICC") || TextUtils.equals(str2, "UICC1")) {
                    arrayList.add(str2);
                } else if (TextUtils.equals(str2, "UICC2")) {
                    arrayList.add(str2);
                }
            }
        }
        log("after format,getRoSeType value:" + arrayList.toString());
        String[] strArr = (String[]) arrayList.toArray(new String[arrayList.size()]);
        listPreference.setEntryValues(strArr);
        listPreference.setEntries(strArr);
        return listPreference;
    }

    private void log(String str) {
        Log.d("MiuiNfcToggleController", str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) preferenceScreen.findPreference(NfcPreferenceController.KEY_TOGGLE_NFC);
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference("nfc_secure_settings");
        ListPreference listPreference = getListPreference(preferenceScreen);
        if (mainSwitchPreference == null) {
            return;
        }
        if (listPreference == null) {
            Log.i("MiuiNfcToggleController", "getListPreference called! seRoute:null");
            return;
        }
        if (switchPreference == null) {
            Log.e("MiuiNfcToggleController", "getListPreference called! secureNfc:null");
        }
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
        this.mNfcAdapter = defaultAdapter;
        if (defaultAdapter == null || SettingsFeatures.isNeedShowMiuiNFC()) {
            preferenceScreen.removePreference(mainSwitchPreference);
            preferenceScreen.removePreference(listPreference);
            if (switchPreference != null) {
                preferenceScreen.removePreference(switchPreference);
                return;
            }
            return;
        }
        boolean z = this.mNfcAdapter.isSecureNfcSupported() && !RegionUtils.IS_MEXICO_TELCEL;
        if (z) {
            this.mSecureNfcEnabler = new SecureNfcEnabler(this.mContext, switchPreference);
        } else {
            this.mSecureNfcEnabler = null;
            if (switchPreference != null) {
                preferenceScreen.removePreference(switchPreference);
            }
        }
        if (RestrictionsHelper.hasNFCRestriction(this.mContext)) {
            mainSwitchPreference.setEnabled(false);
            if (z) {
                switchPreference.setEnabled(false);
                this.mSecureNfcEnabler = null;
            }
            listPreference.setEnabled(false);
        }
        if (this.mSupportMultiRoute) {
            this.mNfcEnabler = new NfcEnabler(this.mContext, mainSwitchPreference, listPreference);
        } else {
            this.mNfcEnabler = new NfcEnabler(this.mContext, mainSwitchPreference, null);
        }
        if (!this.mSupportMultiRoute || RegionUtils.IS_MEXICO_TELCEL) {
            preferenceScreen.removePreference(listPreference);
        } else {
            this.mNfcSeRoute = new NfcSeRoute(this.mContext, this.mNfcAdapter, listPreference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return NfcPreferenceController.KEY_TOGGLE_NFC;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        NfcEnabler nfcEnabler = this.mNfcEnabler;
        if (nfcEnabler != null) {
            nfcEnabler.pause();
        }
        SecureNfcEnabler secureNfcEnabler = this.mSecureNfcEnabler;
        if (secureNfcEnabler != null) {
            secureNfcEnabler.pause();
        }
        NfcSeRoute nfcSeRoute = this.mNfcSeRoute;
        if (nfcSeRoute != null) {
            nfcSeRoute.pause();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        NfcEnabler nfcEnabler = this.mNfcEnabler;
        if (nfcEnabler != null) {
            nfcEnabler.resume();
        }
        SecureNfcEnabler secureNfcEnabler = this.mSecureNfcEnabler;
        if (secureNfcEnabler != null) {
            secureNfcEnabler.resume();
        }
        NfcSeRoute nfcSeRoute = this.mNfcSeRoute;
        if (nfcSeRoute != null) {
            nfcSeRoute.resume();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
    }
}
