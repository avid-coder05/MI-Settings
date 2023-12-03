package com.android.settings.development;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.lang.reflect.Method;
import miui.os.Build;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;
import miui.telephony.TelephonyManagerEx;

/* loaded from: classes.dex */
public class FiveGViceSAPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnResume, OnPause {
    static final int DUAL_SA_OFF = 0;
    static final int DUAL_SA_ON = 1;
    private static final int PHONE_COUNT = TelephonyManager.getDefault().getPhoneCount();
    private final Context mContext;
    private final BroadcastReceiver mViceSaReceiver;

    public FiveGViceSAPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mViceSaReceiver = new BroadcastReceiver() { // from class: com.android.settings.development.FiveGViceSAPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                Log.d("FiveGViceSAPreferenceController", "action = " + action);
                if ("android.intent.action.AIRPLANE_MODE".equals(action)) {
                    Log.d("FiveGViceSAPreferenceController", "ACTION_AIRPLANE_MODE_CHANGED");
                    FiveGViceSAPreferenceController.this.updateState();
                } else if ("android.telephony.action.CARRIER_CONFIG_CHANGED".equals(action)) {
                    int intExtra = intent.getIntExtra("phone", SubscriptionManager.INVALID_PHONE_ID);
                    Log.d("FiveGViceSAPreferenceController", "ACTION_CARRIER_CONFIG_CHANGED phoneId: " + intExtra);
                    if (intExtra != SubscriptionManager.getDefault().getDefaultDataSlotId()) {
                        FiveGViceSAPreferenceController.this.updateState();
                    }
                }
            }
        };
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mContext = context;
    }

    private boolean isDualSaEnabled() {
        try {
            String str = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
            Method declaredMethod = TelephonyManager.class.getDeclaredMethod("getDefault", null);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(null, null);
            Method declaredMethod2 = TelephonyManager.class.getDeclaredMethod("isDualSaEnabled", null);
            declaredMethod2.setAccessible(true);
            boolean booleanValue = ((Boolean) declaredMethod2.invoke(invoke, null)).booleanValue();
            Log.d("FiveGViceSAPreferenceController", "isDualSaEnabled status = " + booleanValue);
            return booleanValue;
        } catch (Exception unused) {
            return false;
        }
    }

    private boolean isUserFiveGSaEnabled(int i) {
        try {
            Log.d("FiveGViceSAPreferenceController", "isUserFiveGSaEnabled slotId = " + i);
            String str = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
            Method declaredMethod = TelephonyManager.class.getDeclaredMethod("getDefault", null);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(null, null);
            Method declaredMethod2 = TelephonyManager.class.getDeclaredMethod("isUserFiveGSaEnabled", Integer.TYPE);
            declaredMethod2.setAccessible(true);
            return ((Boolean) declaredMethod2.invoke(invoke, Integer.valueOf(i))).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isViceSaDevelopmentAvailable() {
        return TelephonyManager.getDefault().isDualSaSupported() && !Build.IS_INTERNATIONAL_BUILD;
    }

    private boolean isViceSimActived() {
        if (TelephonyManager.getDefault().hasIccCard(othersSim())) {
            try {
                return TelephonyManagerEx.getDefault().getMiuiTelephony().isIccCardActivate(othersSim());
            } catch (Exception unused) {
                return false;
            }
        }
        return false;
    }

    private static int othersSim() {
        for (int i = 0; i < PHONE_COUNT; i++) {
            if (i != SubscriptionManager.getDefault().getDefaultDataSlotId()) {
                return i;
            }
        }
        return -1;
    }

    private boolean setDualSaEnabled(boolean z) {
        try {
            Log.d("FiveGViceSAPreferenceController", "setDualSaEnabled enabled = " + z);
            String str = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
            Method declaredMethod = TelephonyManager.class.getDeclaredMethod("getDefault", null);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(null, null);
            Method declaredMethod2 = TelephonyManager.class.getDeclaredMethod("setDualSaEnabled", Boolean.TYPE);
            declaredMethod2.setAccessible(true);
            declaredMethod2.invoke(invoke, Boolean.valueOf(z));
            return true;
        } catch (Exception e) {
            Log.d("FiveGViceSAPreferenceController", "Exception:" + e);
            return false;
        }
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory;
        Preference findPreference;
        super.displayPreference(preferenceScreen);
        Log.d("FiveGViceSAPreferenceController", "displayPreference isViceSaDevelopmentAvailable = " + isViceSaDevelopmentAvailable());
        if (isViceSaDevelopmentAvailable() || preferenceScreen == null || (preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("debug_networking_category")) == null || (findPreference = preferenceCategory.findPreference(getPreferenceKey())) == null) {
            return;
        }
        preferenceCategory.removePreference(findPreference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "button_vice_sim_5g_sa_network";
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        Log.d("FiveGViceSAPreferenceController", "onPause");
        if (isViceSaDevelopmentAvailable()) {
            this.mContext.unregisterReceiver(this.mViceSaReceiver);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (setDualSaEnabled(booleanValue)) {
            Log.d("FiveGViceSAPreferenceController", "onPreferenceChange newValue = " + booleanValue);
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        Log.d("FiveGViceSAPreferenceController", "onResume");
        if (isViceSaDevelopmentAvailable()) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
            intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
            this.mContext.registerReceiver(this.mViceSaReceiver, intentFilter);
        }
    }

    public void updateState() {
        int othersSim = othersSim();
        boolean z = false;
        if (othersSim == -1) {
            ((SwitchPreference) this.mPreference).setChecked(false);
            ((SwitchPreference) this.mPreference).setEnabled(false);
            return;
        }
        boolean isDualSaEnabled = isDualSaEnabled();
        ((SwitchPreference) this.mPreference).setChecked(isDualSaEnabled);
        boolean isViceSimActived = isViceSimActived();
        boolean hasIccCard = TelephonyManager.getDefault().hasIccCard(othersSim);
        boolean z2 = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
        boolean z3 = !z2 && hasIccCard && isViceSimActived;
        boolean isUserFiveGSaEnabled = isUserFiveGSaEnabled(SubscriptionManager.getDefault().getDefaultDataSlotId());
        boolean isUserFiveGEnabled = TelephonyManager.getDefault().isUserFiveGEnabled();
        boolean isDualNrEnabled = TelephonyManager.getDefault().isDualNrEnabled();
        boolean z4 = isUserFiveGEnabled && isUserFiveGSaEnabled && isDualNrEnabled;
        SwitchPreference switchPreference = (SwitchPreference) this.mPreference;
        if (z3 && z4) {
            z = true;
        }
        switchPreference.setEnabled(z);
        Log.d("FiveGViceSAPreferenceController", "updateState slotId: " + othersSim + ", check: " + isDualSaEnabled + ", isViceSimActived: " + isViceSimActived + ", isSimInsert: " + hasIccCard + ", airplaneEnable: " + z2 + ", enableOptions: " + z3 + ", isUserFiveGEnabled: " + isUserFiveGEnabled + ", isUserFiveGSaEnabled: " + isUserFiveGSaEnabled + ", isUserViceFiveGEnabled: " + isDualNrEnabled + ", isDefaultSim: " + SubscriptionManager.getDefault().getDefaultDataSlotId());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateState();
    }
}
