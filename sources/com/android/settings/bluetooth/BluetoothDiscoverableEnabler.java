package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settingslib.bluetooth.BluetoothDiscoverableTimeoutReceiver;
import com.android.settingslib.miuisettings.preference.CheckBoxPreference;

/* loaded from: classes.dex */
public class BluetoothDiscoverableEnabler implements Preference.OnPreferenceClickListener {
    private final BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private boolean mDiscoverable;
    protected final CheckBoxPreference mDiscoveryPreference;
    private int mNumberOfPairedDevices;
    private final SharedPreferences mSharedPreferences;
    private int mTimeoutSecs;
    private final Handler mUiHandler;
    private final Runnable mUpdateCountdownSummaryRunnable;

    /* renamed from: com.android.settings.bluetooth.BluetoothDiscoverableEnabler$1  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass1 extends BroadcastReceiver {
        final /* synthetic */ BluetoothDiscoverableEnabler this$0;

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int intExtra;
            if (!"android.bluetooth.adapter.action.SCAN_MODE_CHANGED".equals(intent.getAction()) || (intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.SCAN_MODE", Integer.MIN_VALUE)) == Integer.MIN_VALUE) {
                return;
            }
            this.this$0.handleModeChanged(intExtra);
        }
    }

    /* renamed from: com.android.settings.bluetooth.BluetoothDiscoverableEnabler$2  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ BluetoothDiscoverableEnabler this$0;

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.updateCountdownSummary();
        }
    }

    private static String formatTimeRemaining(int i) {
        StringBuilder sb = new StringBuilder(6);
        int i2 = i / 60;
        sb.append(i2);
        sb.append(':');
        int i3 = i - (i2 * 60);
        if (i3 < 10) {
            sb.append('0');
        }
        sb.append(i3);
        return sb.toString();
    }

    private int getDiscoverableTimeout() {
        int i = this.mTimeoutSecs;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        int i3 = SystemProperties.getInt("debug.bt.discoverable_time", 0);
        if (i3 < 0) {
            String string = this.mSharedPreferences.getString("bt_discoverable_timeout", "twomin");
            if (!string.equals("never")) {
                i2 = string.equals("onehour") ? 3600 : string.equals("fivemin") ? 300 : 120;
            }
        } else {
            i2 = i3;
        }
        this.mTimeoutSecs = i2;
        return i2;
    }

    private void setEnabled(boolean z) {
        if (!z) {
            this.mBluetoothAdapter.setScanMode(21, 120L);
            BluetoothDiscoverableTimeoutReceiver.cancelDiscoverableAlarm(this.mContext);
            return;
        }
        int discoverableTimeout = getDiscoverableTimeout();
        long j = discoverableTimeout;
        long currentTimeMillis = System.currentTimeMillis() + (1000 * j);
        LocalBluetoothPreferences.persistDiscoverableEndTimestamp(this.mContext, currentTimeMillis);
        this.mBluetoothAdapter.setScanMode(23, j);
        updateCountdownSummary();
        Log.d("BluetoothDiscoverableEnabler", "setEnabled(): enabled = " + z + "timeout = " + discoverableTimeout);
        if (discoverableTimeout > 0) {
            BluetoothDiscoverableTimeoutReceiver.setDiscoverableAlarm(this.mContext, currentTimeMillis);
        } else {
            BluetoothDiscoverableTimeoutReceiver.cancelDiscoverableAlarm(this.mContext);
        }
    }

    private void setSummaryNotDiscoverable() {
        if (this.mNumberOfPairedDevices != 0) {
            this.mDiscoveryPreference.setSummary(R.string.bluetooth_only_visible_to_paired_devices);
        } else {
            this.mDiscoveryPreference.setSummary(R.string.bluetooth_not_visible_to_other_devices);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCountdownSummary() {
        if (this.mBluetoothAdapter.getScanMode() != 23) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long discoverableEndTimestamp = LocalBluetoothPreferences.getDiscoverableEndTimestamp(this.mContext);
        if (currentTimeMillis > discoverableEndTimestamp) {
            updateTimerDisplay(0);
            return;
        }
        updateTimerDisplay((int) ((discoverableEndTimestamp - currentTimeMillis) / 1000));
        synchronized (this) {
            this.mUiHandler.removeCallbacks(this.mUpdateCountdownSummaryRunnable);
            this.mUiHandler.postDelayed(this.mUpdateCountdownSummaryRunnable, 1000L);
        }
    }

    private void updateTimerDisplay(int i) {
        if (getDiscoverableTimeout() == 0) {
            this.mDiscoveryPreference.setSummary(R.string.bluetooth_is_discoverable_always);
            return;
        }
        this.mDiscoveryPreference.setSummary(this.mContext.getString(R.string.bluetooth_is_discoverable, formatTimeRemaining(i)));
    }

    void handleModeChanged(int i) {
        Log.d("BluetoothDiscoverableEnabler", "handleModeChanged(): mode = " + i);
        if (i == 23) {
            this.mDiscoverable = true;
            updateCountdownSummary();
            return;
        }
        this.mDiscoverable = false;
        setSummaryNotDiscoverable();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        boolean z = !this.mDiscoverable;
        this.mDiscoverable = z;
        setEnabled(z);
        return true;
    }
}
