package com.android.settings.development;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.msim.MSimUtils;
import java.lang.reflect.Method;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class NrcaSwitchView implements Preference.OnPreferenceChangeListener {
    public static final boolean IS_MTK = "mediatek".equals(FeatureParser.getString("vendor"));
    private SwitchPreference mButtonNrca;
    private Context mContext;
    private Handler mHandler;
    private PreferenceGroup mPrefGroup;
    private int mSlotId;

    public NrcaSwitchView(PreferenceGroup preferenceGroup, Context context, int i) {
        this.mContext = null;
        this.mHandler = null;
        this.mPrefGroup = preferenceGroup;
        this.mSlotId = i;
        this.mContext = context;
        SwitchPreference switchPreference = (SwitchPreference) preferenceGroup.findPreference("nrca_switch" + this.mSlotId);
        this.mButtonNrca = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        this.mHandler = new Handler();
    }

    private int getSubId(int i) {
        int[] subId = SubscriptionManager.getSubId(i);
        if (subId == null || subId.length < 1) {
            return -1;
        }
        return subId[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNrCAEnabled(int i) {
        try {
            Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
            Method declaredMethod = cls.getDeclaredMethod("getDefault", null);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(null, null);
            Method declaredMethod2 = cls.getDeclaredMethod("isNrCAEnabled", Integer.TYPE);
            declaredMethod2.setAccessible(true);
            boolean booleanValue = ((Boolean) declaredMethod2.invoke(invoke, Integer.valueOf(i))).booleanValue();
            Log.d("NrcaSwitchView", "isNrCAEnabled rst=" + booleanValue);
            return booleanValue;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isUserFiveGEnabled(int i) {
        try {
            Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
            Method declaredMethod = cls.getDeclaredMethod("getDefault", null);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(null, null);
            Method declaredMethod2 = cls.getDeclaredMethod("isUserFiveGEnabled", Integer.TYPE);
            declaredMethod2.setAccessible(true);
            return ((Boolean) declaredMethod2.invoke(invoke, Integer.valueOf(i))).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setUserNrCAEnabled(int i, int i2) {
        int subId = getSubId(i2);
        if (subId != -1) {
            try {
                Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
                Method declaredMethod = cls.getDeclaredMethod("getDefault", null);
                declaredMethod.setAccessible(true);
                Object invoke = declaredMethod.invoke(null, null);
                Class<?> cls2 = Integer.TYPE;
                Method declaredMethod2 = cls.getDeclaredMethod("setUserNrCAEnabled", cls2, cls2);
                declaredMethod2.setAccessible(true);
                declaredMethod2.invoke(invoke, Integer.valueOf(i), Integer.valueOf(i2));
                Settings.Global.putInt(this.mContext.getContentResolver(), "button_5G_carrier_aggregation" + subId, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        try {
            SwitchPreference switchPreference = this.mButtonNrca;
            if (preference == switchPreference) {
                setUserNrCAEnabled(switchPreference.isChecked() ? 0 : 1, this.mSlotId);
            }
            if (IS_MTK) {
                Toast.makeText(this.mContext, R.string.nrca_need_reset, 1).show();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean shouldEnableNrcaSwitch() {
        boolean z = Settings.System.getInt(this.mContext.getContentResolver(), "airplane_mode_on", -1) == 1;
        boolean isIccCardActivated = MSimUtils.isIccCardActivated(this.mSlotId);
        if (z || !isIccCardActivated) {
            return false;
        }
        if (isUserFiveGEnabled(this.mSlotId)) {
            return true;
        }
        Log.d("NrcaSwitchView", "disable vonr button because sa is turned off");
        return false;
    }

    public void updateNrcaButtonUI() {
        if (this.mButtonNrca == null) {
            return;
        }
        PreferenceGroup preferenceGroup = this.mPrefGroup;
        StringBuilder sb = new StringBuilder();
        sb.append("nrca_switch");
        sb.append(this.mSlotId);
        if (!(preferenceGroup.findPreference(sb.toString()) != null)) {
            this.mPrefGroup.addPreference(this.mButtonNrca);
        }
        new Thread(new Runnable() { // from class: com.android.settings.development.NrcaSwitchView.1
            @Override // java.lang.Runnable
            public void run() {
                final boolean shouldEnableNrcaSwitch = NrcaSwitchView.this.shouldEnableNrcaSwitch();
                NrcaSwitchView nrcaSwitchView = NrcaSwitchView.this;
                final boolean isNrCAEnabled = nrcaSwitchView.isNrCAEnabled(nrcaSwitchView.mSlotId);
                NrcaSwitchView.this.mHandler.post(new Runnable() { // from class: com.android.settings.development.NrcaSwitchView.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        NrcaSwitchView.this.mButtonNrca.setEnabled(shouldEnableNrcaSwitch);
                        NrcaSwitchView.this.mButtonNrca.setChecked(isNrCAEnabled);
                    }
                });
            }
        }).start();
    }
}
