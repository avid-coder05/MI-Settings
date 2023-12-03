package com.android.settings.deviceinfo.imei;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.Sliceable;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.Utils;
import com.android.settingslib.miuisettings.preference.PreferenceUtils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class ImeiInfoPreferenceController extends BasePreferenceController {
    private static final String KEY_PREFERENCE_CATEGORY = "device_detail_category";
    private Fragment mFragment;
    private final boolean mIsMultiSim;
    private final List<Preference> mPreferenceList;
    private final TelephonyManager mTelephonyManager;

    public ImeiInfoPreferenceController(Context context, String str) {
        super(context, str);
        this.mPreferenceList = new ArrayList();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mTelephonyManager = telephonyManager;
        this.mIsMultiSim = telephonyManager.getPhoneCount() > 1 && !miui.telephony.TelephonyManager.isCustSingleSimDevice();
    }

    private int getPhoneType(int i) {
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoForSimSlotIndex(i);
        return this.mTelephonyManager.getCurrentPhoneType(activeSubscriptionInfoForSimSlotIndex != null ? activeSubscriptionInfoForSimSlotIndex.getSubscriptionId() : Integer.MAX_VALUE);
    }

    private CharSequence getSummary(int i) {
        return getPhoneType(i) == 2 ? this.mTelephonyManager.getMeid(i) : this.mTelephonyManager.getImei(i);
    }

    private CharSequence getTitle(int i) {
        return getPhoneType(i) == 2 ? getTitleForCdmaPhone(i) : getTitleForGsmPhone(i);
    }

    private CharSequence getTitleForCdmaPhone(int i) {
        return this.mIsMultiSim ? this.mContext.getString(R.string.meid_multi_sim, Integer.valueOf(i + 1)) : this.mContext.getString(R.string.status_meid_number);
    }

    private CharSequence getTitleForGsmPhone(int i) {
        return SettingsFeatures.isShowSlotDevice() ? this.mIsMultiSim ? this.mContext.getString(R.string.imei_multi_sim_softbank, Integer.valueOf(i + 1)) : this.mContext.getString(R.string.status_imei) : this.mIsMultiSim ? this.mContext.getString(R.string.imei_multi_sim, Integer.valueOf(i + 1)) : this.mContext.getString(R.string.status_imei);
    }

    private void updatePreference(Preference preference, int i) {
        preference.setTitle(getTitle(i));
        preference.setSummary(getSummary(i));
    }

    @Override // com.android.settings.slices.Sliceable
    public void copy() {
        Sliceable.setCopyContent(this.mContext, getSummary(0), getTitle(0));
    }

    Preference createNewPreference(Context context) {
        return new com.android.settingslib.miuisettings.preference.Preference(context, true);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_PREFERENCE_CATEGORY);
        if (isAvailable() && findPreference != null && PreferenceUtils.isVisible(findPreference)) {
            this.mPreferenceList.add(findPreference);
            updatePreference(findPreference, 0);
            int order = findPreference.getOrder();
            for (int i = 1; i < this.mTelephonyManager.getPhoneCount() && this.mIsMultiSim; i++) {
                Preference createNewPreference = createNewPreference(preferenceScreen.getContext());
                createNewPreference.setOrder(order + i);
                createNewPreference.setKey(getPreferenceKey() + i);
                preferenceCategory.addPreference(createNewPreference);
                this.mPreferenceList.add(createNewPreference);
                updatePreference(createNewPreference, i);
            }
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!((UserManager) this.mContext.getSystemService(UserManager.class)).isAdminUser() || Utils.isWifiOnly(this.mContext)) ? 3 : 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return getSummary(0);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        int indexOf = this.mPreferenceList.indexOf(preference);
        if (indexOf == -1) {
            return false;
        }
        ImeiInfoDialogFragment.show(this.mFragment, indexOf, preference.getTitle().toString());
        return true;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public void setHost(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference == null) {
            return;
        }
        int size = this.mPreferenceList.size();
        for (int i = 0; i < size; i++) {
            updatePreference(this.mPreferenceList.get(i), i);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public boolean useDynamicSliceSummary() {
        return true;
    }
}
