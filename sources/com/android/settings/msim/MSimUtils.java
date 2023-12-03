package com.android.settings.msim;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.security.VirtualSimUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;
import miui.telephony.TelephonyManagerEx;

/* loaded from: classes.dex */
public class MSimUtils extends MiuiUtils {
    public static final int DEFAULT_SIM_INDEX = SubscriptionManager.getDefault().getDefaultSlotId();

    /* loaded from: classes.dex */
    public static class SubscriptionInfoComparable implements Comparator<SubscriptionInfo> {
        @Override // java.util.Comparator
        public int compare(SubscriptionInfo subscriptionInfo, SubscriptionInfo subscriptionInfo2) {
            return subscriptionInfo.getSlotId() - subscriptionInfo2.getSlotId();
        }
    }

    public static SubscriptionInfo getAvailableSubscriptionInfoBySlot(int i) {
        return getSubscriptionInfoBySlot(SubscriptionManager.getDefault().getAvailableSubscriptionInfoList(), i);
    }

    private static int getCommonFeatures() {
        return SystemProperties.getInt("ro.vendor.radio.features_common", 0);
    }

    public static SubscriptionInfo getSubscriptionInfoBySlot(List<SubscriptionInfo> list, int i) {
        if (list == null) {
            return null;
        }
        for (SubscriptionInfo subscriptionInfo : list) {
            if (subscriptionInfo.getSlotId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    public static boolean isIccCardActivated(int i) {
        SubscriptionInfo availableSubscriptionInfoBySlot = getAvailableSubscriptionInfoBySlot(i);
        return availableSubscriptionInfoBySlot == null || availableSubscriptionInfoBySlot.isActivated();
    }

    public static boolean isSmartDualSimSwitchSupported() {
        return (getCommonFeatures() & 2) != 0;
    }

    public static boolean isViceSlotActivated() {
        for (int i = 0; i < TelephonyManager.getDefault().getPhoneCount(); i++) {
            if (i != SubscriptionManager.getDefault().getDefaultDataSlotId()) {
                return isIccCardActivated(i);
            }
        }
        return false;
    }

    @Override // com.android.settings.MiuiUtils
    public void addSimLockPreference(PreferenceScreen preferenceScreen, String str) {
        PreferenceGroup preferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(str);
        preferenceGroup.removeAll();
        Context context = preferenceGroup.getContext();
        List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.getDefault().getActiveSubscriptionInfoList();
        Iterator<SubscriptionInfo> it = activeSubscriptionInfoList.iterator();
        while (it.hasNext()) {
            if (VirtualSimUtils.isVirtualSim(context, it.next().getSlotId())) {
                it.remove();
            }
        }
        if (activeSubscriptionInfoList.isEmpty()) {
            preferenceScreen.removePreference(preferenceGroup);
            return;
        }
        Collections.sort(activeSubscriptionInfoList, new SubscriptionInfoComparable());
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            Preference preference = new Preference(context);
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.Settings$IccLockSettingsActivity");
            SubscriptionManager.putSlotIdExtra(intent, subscriptionInfo.getSlotId());
            preference.setIntent(intent);
            TelephonyManagerEx telephonyManagerEx = TelephonyManagerEx.getDefault();
            boolean isRadioOnForSlot = telephonyManagerEx.isRadioOnForSlot(subscriptionInfo.getSlotId());
            if (isRadioOnForSlot && (telephonyManagerEx.getSimStateForSlot(subscriptionInfo.getSlotId()) == 1 || telephonyManagerEx.getSimStateForSlot(subscriptionInfo.getSlotId()) == 0)) {
                isRadioOnForSlot = false;
            }
            preference.setEnabled(isRadioOnForSlot);
            preference.setTitle(isRadioOnForSlot ? subscriptionInfo.getDisplayName() : context.getString(R.string.sim_radio_off));
            preferenceGroup.addPreference(preference);
        }
    }

    @Override // com.android.settings.MiuiUtils
    public ArrayList<Integer> getSimSlotList(Context context) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        List<SubscriptionInfo> subscriptionInfoList = SubscriptionManager.getDefault().getSubscriptionInfoList();
        if (subscriptionInfoList != null) {
            Iterator<SubscriptionInfo> it = subscriptionInfoList.iterator();
            while (it.hasNext()) {
                arrayList.add(Integer.valueOf(it.next().getSlotId()));
            }
        }
        return arrayList;
    }

    @Override // com.android.settings.MiuiUtils
    public Comparator<SubscriptionInfo> getSubscriptionInfoComparable() {
        return new SubscriptionInfoComparable();
    }

    public boolean hasDualSim(Context context) {
        List<SubscriptionInfo> subscriptionInfoList = SubscriptionManager.getDefault().getSubscriptionInfoList();
        return subscriptionInfoList != null && subscriptionInfoList.size() >= 2 && subscriptionInfoList.get(0).isActivated() && subscriptionInfoList.get(1).isActivated();
    }
}
