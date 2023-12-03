package com.android.settings.development;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.lang.reflect.Method;
import miui.telephony.TelephonyManager;

/* loaded from: classes.dex */
public class FiveGNrcaConfigController extends DeveloperOptionsPreferenceController implements LifecycleObserver, OnResume, OnPause {
    private final Context mContext;
    private final BroadcastReceiver mNrcaReceiver;

    public FiveGNrcaConfigController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mNrcaReceiver = new BroadcastReceiver() { // from class: com.android.settings.development.FiveGNrcaConfigController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                Log.d("FiveGNrcaConfigController", "action = " + action);
                if ("android.intent.action.AIRPLANE_MODE".equals(action)) {
                    Log.d("FiveGNrcaConfigController", "ACTION_AIRPLANE_MODE_CHANGED");
                    FiveGNrcaConfigController.this.updateState();
                } else if ("android.telephony.action.CARRIER_CONFIG_CHANGED".equals(action)) {
                    Log.d("FiveGNrcaConfigController", "ACTION_CARRIER_CONFIG_CHANGED");
                    FiveGNrcaConfigController.this.updateState();
                }
            }
        };
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mContext = context;
    }

    private boolean isDisable(Context context) {
        return !TelephonyManager.getDefault().hasIccCard() || (Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", -1) == 1);
    }

    private static boolean isNrCaSupported() {
        try {
            Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
            Method method = cls.getMethod("getDefault", null);
            method.setAccessible(true);
            Object invoke = method.invoke(null, null);
            Method method2 = cls.getMethod("isNrCaSupported", new Class[0]);
            method2.setAccessible(true);
            return ((Boolean) method2.invoke(invoke, new Object[0])).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isSearchable(Context context) {
        return isNrCaSupported() && !(Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", -1) == 1) && TelephonyManager.getDefault().hasIccCard();
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        this.mContext.registerReceiver(this.mNrcaReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateState() {
        if (isDisable(this.mContext)) {
            onDeveloperOptionsDisabled();
        } else {
            onDeveloperOptionsEnabled();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "new_radio_carrier_aggregation";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isNrCaSupported();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        if (isAvailable()) {
            this.mContext.unregisterReceiver(this.mNrcaReceiver);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            registerBroadcastReceiver();
            updateState();
        }
    }
}
