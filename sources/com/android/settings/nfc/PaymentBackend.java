package com.android.settings.nfc;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.ApduServiceInfo;
import android.nfc.cardemulation.CardEmulation;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.internal.content.PackageMonitor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class PaymentBackend {
    private final NfcAdapter mAdapter;
    private ArrayList<PaymentAppInfo> mAppInfos;
    private final CardEmulation mCardEmuManager;
    private final Context mContext;
    private PaymentAppInfo mDefaultAppInfo;
    private final PackageMonitor mSettingsPackageMonitor = new SettingsPackageMonitor();
    private ArrayList<Callback> mCallbacks = new ArrayList<>();

    /* loaded from: classes2.dex */
    public interface Callback {
        void onPaymentAppsChanged();
    }

    /* loaded from: classes2.dex */
    public static class PaymentAppInfo {
        public ComponentName componentName;
        CharSequence description;
        boolean isDefault;
        public CharSequence label;
        public ComponentName settingsComponent;
    }

    /* loaded from: classes2.dex */
    private class SettingsPackageMonitor extends PackageMonitor {
        private Handler mHandler;

        private SettingsPackageMonitor() {
        }

        public void onPackageAdded(String str, int i) {
            this.mHandler.obtainMessage().sendToTarget();
        }

        public void onPackageAppeared(String str, int i) {
            this.mHandler.obtainMessage().sendToTarget();
        }

        public void onPackageDisappeared(String str, int i) {
            this.mHandler.obtainMessage().sendToTarget();
        }

        public void onPackageRemoved(String str, int i) {
            this.mHandler.obtainMessage().sendToTarget();
        }

        public void register(Context context, Looper looper, UserHandle userHandle, boolean z) {
            if (this.mHandler == null) {
                this.mHandler = new Handler(looper) { // from class: com.android.settings.nfc.PaymentBackend.SettingsPackageMonitor.1
                    @Override // android.os.Handler
                    public void dispatchMessage(Message message) {
                        PaymentBackend.this.refresh();
                    }
                };
            }
            super.register(context, looper, userHandle, z);
        }
    }

    public PaymentBackend(Context context) {
        this.mContext = context;
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(context);
        this.mAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            this.mCardEmuManager = null;
            return;
        }
        this.mCardEmuManager = CardEmulation.getInstance(defaultAdapter);
        refresh();
    }

    public PaymentAppInfo getDefaultApp() {
        return this.mDefaultAppInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ComponentName getDefaultPaymentApp() {
        String string = Settings.Secure.getString(this.mContext.getContentResolver(), "nfc_payment_default_component");
        if (string != null) {
            return ComponentName.unflattenFromString(string);
        }
        return null;
    }

    public List<PaymentAppInfo> getPaymentAppInfos() {
        return this.mAppInfos;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isForegroundMode() {
        try {
            return Settings.Secure.getInt(this.mContext.getContentResolver(), "nfc_payment_foreground") != 0;
        } catch (Settings.SettingNotFoundException unused) {
            return false;
        }
    }

    void makeCallbacks() {
        Iterator<Callback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            it.next().onPaymentAppsChanged();
        }
    }

    public void onPause() {
        this.mSettingsPackageMonitor.unregister();
    }

    public void onResume() {
        PackageMonitor packageMonitor = this.mSettingsPackageMonitor;
        Context context = this.mContext;
        packageMonitor.register(context, context.getMainLooper(), false);
        refresh();
    }

    public void refresh() {
        if (this.mCardEmuManager == null) {
            return;
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ApduServiceInfo> services = this.mCardEmuManager.getServices("payment");
        ArrayList<PaymentAppInfo> arrayList = new ArrayList<>();
        if (services == null) {
            makeCallbacks();
            return;
        }
        ComponentName defaultPaymentApp = getDefaultPaymentApp();
        PaymentAppInfo paymentAppInfo = null;
        for (ApduServiceInfo apduServiceInfo : services) {
            PaymentAppInfo paymentAppInfo2 = new PaymentAppInfo();
            CharSequence loadLabel = apduServiceInfo.loadLabel(packageManager);
            paymentAppInfo2.label = loadLabel;
            if (loadLabel == null) {
                paymentAppInfo2.label = apduServiceInfo.loadAppLabel(packageManager);
            }
            boolean equals = apduServiceInfo.getComponent().equals(defaultPaymentApp);
            paymentAppInfo2.isDefault = equals;
            if (equals) {
                paymentAppInfo = paymentAppInfo2;
            }
            paymentAppInfo2.componentName = apduServiceInfo.getComponent();
            String settingsActivityName = apduServiceInfo.getSettingsActivityName();
            if (settingsActivityName != null) {
                paymentAppInfo2.settingsComponent = new ComponentName(paymentAppInfo2.componentName.getPackageName(), settingsActivityName);
            } else {
                paymentAppInfo2.settingsComponent = null;
            }
            paymentAppInfo2.description = apduServiceInfo.getDescription();
            arrayList.add(paymentAppInfo2);
        }
        this.mAppInfos = arrayList;
        this.mDefaultAppInfo = paymentAppInfo;
        makeCallbacks();
    }

    public void registerCallback(Callback callback) {
        this.mCallbacks.add(callback);
    }

    public void setDefaultPaymentApp(ComponentName componentName) {
        Settings.Secure.putString(this.mContext.getContentResolver(), "nfc_payment_default_component", componentName != null ? componentName.flattenToString() : null);
        refresh();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setForegroundMode(boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "nfc_payment_foreground", z ? 1 : 0);
    }

    public void unregisterCallback(Callback callback) {
        this.mCallbacks.remove(callback);
    }
}
