package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.os.UserHandle;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

/* loaded from: classes.dex */
public class ConnectedUsbDeviceUpdater {
    private DevicePreferenceCallback mDevicePreferenceCallback;
    private DashboardFragment mFragment;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private UsbBackend mUsbBackend;
    UsbConnectionBroadcastReceiver.UsbConnectionListener mUsbConnectionListener;
    RestrictedPreference mUsbPreference;
    UsbConnectionBroadcastReceiver mUsbReceiver;

    public ConnectedUsbDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        this(context, dashboardFragment, devicePreferenceCallback, new UsbBackend(context));
    }

    ConnectedUsbDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback, UsbBackend usbBackend) {
        this.mUsbConnectionListener = new UsbConnectionBroadcastReceiver.UsbConnectionListener() { // from class: com.android.settings.connecteddevice.usb.ConnectedUsbDeviceUpdater$$ExternalSyntheticLambda1
            @Override // com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver.UsbConnectionListener
            public final void onUsbConnectionChanged(boolean z, long j, int i, int i2) {
                ConnectedUsbDeviceUpdater.this.lambda$new$0(z, j, i, i2);
            }
        };
        this.mFragment = dashboardFragment;
        this.mDevicePreferenceCallback = devicePreferenceCallback;
        this.mUsbBackend = usbBackend;
        this.mUsbReceiver = new UsbConnectionBroadcastReceiver(context, this.mUsbConnectionListener, this.mUsbBackend);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(this.mFragment.getContext()).getMetricsFeatureProvider();
    }

    private void forceUpdate() {
        this.mUsbReceiver.register();
    }

    public static int getSummary(long j, int i) {
        return i != 1 ? i != 2 ? R.string.usb_summary_charging_only : j == 4 ? R.string.usb_summary_file_transfers : j == 32 ? R.string.usb_summary_tether : j == 16 ? R.string.usb_summary_photo_transfers : j == 8 ? R.string.usb_summary_MIDI : R.string.usb_summary_charging_only : j == 4 ? R.string.usb_summary_file_transfers_power : j == 32 ? R.string.usb_summary_tether_power : j == 16 ? R.string.usb_summary_photo_transfers_power : j == 8 ? R.string.usb_summary_MIDI_power : R.string.usb_summary_power_only;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initUsbPreference$1(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, this.mFragment.getMetricsCategory());
        new SubSettingLauncher(this.mFragment.getContext()).setDestination(UsbDetailsFragment.class.getName()).setTitleRes(R.string.usb_preference).setSourceMetricsCategory(this.mFragment.getMetricsCategory()).launch();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z, long j, int i, int i2) {
        if (!z) {
            this.mDevicePreferenceCallback.onDeviceRemoved(this.mUsbPreference);
            return;
        }
        RestrictedPreference restrictedPreference = this.mUsbPreference;
        if (i2 != 2) {
            j = 0;
        }
        restrictedPreference.setSummary(getSummary(j, i));
        this.mDevicePreferenceCallback.onDeviceAdded(this.mUsbPreference);
    }

    public void initUsbPreference(Context context) {
        RestrictedPreference restrictedPreference = new RestrictedPreference(context, (AttributeSet) null);
        this.mUsbPreference = restrictedPreference;
        restrictedPreference.setTitle(R.string.usb_pref);
        this.mUsbPreference.setIcon(R.drawable.ic_usb);
        this.mUsbPreference.setKey("connected_usb");
        this.mUsbPreference.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfUsbDataSignalingIsDisabled(context, UserHandle.myUserId()));
        this.mUsbPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.connecteddevice.usb.ConnectedUsbDeviceUpdater$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$initUsbPreference$1;
                lambda$initUsbPreference$1 = ConnectedUsbDeviceUpdater.this.lambda$initUsbPreference$1(preference);
                return lambda$initUsbPreference$1;
            }
        });
        forceUpdate();
    }

    public void registerCallback() {
        this.mUsbReceiver.register();
    }

    public void unregisterCallback() {
        this.mUsbReceiver.unregister();
    }
}
