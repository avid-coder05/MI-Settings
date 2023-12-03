package com.android.settings.connecteddevice.usb;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class UsbModeChooserReceiver extends BroadcastReceiver {
    public static final int[] SUPPORT_VENDOR_IDS = {1256, 4817, 1452, 8921, 11669, 6127, 4100, 6610, 10821, 10864, 4046, 11379, 2996, 10665, 3725, 1478, 6353};
    public static final int[] SUPPORT_PRODUCT_IDS = {26720, 4221, 4222, 4776, 8264, 10099, 10100, 10084, 10085, 24578, 24579, 24581, 31612, 31613, 25406, 580, 581, 3074, 8200, 36881, 65486, 28932, 28718, 1635, 3877, 28700, 8447, 65280, 65288, 8221, 61440, 61443, 20193, 20199, 20200, 20201};
    public static boolean mSoftSwitch = false;
    public static boolean mOTGSoftSwitch = false;
    private ArrayList<Integer> mSupportVendorIdsList = new ArrayList<>();
    private ArrayList<Integer> mSupportProductIdsList = new ArrayList<>();

    private void handlePhoneAttached(final Context context) {
        new Handler(true).post(new Runnable() { // from class: com.android.settings.connecteddevice.usb.UsbModeChooserReceiver.2
            @Override // java.lang.Runnable
            public void run() {
                UsbModeChooserReceiver.this.startPhoneAttachedService(context);
            }
        });
    }

    private void handlePhoneAttachedAsync(final Context context, Intent intent) {
        final UsbDevice usbDevice = (UsbDevice) intent.getExtras().getParcelable("device");
        new Handler(true).post(new Runnable() { // from class: com.android.settings.connecteddevice.usb.UsbModeChooserReceiver.3
            @Override // java.lang.Runnable
            public void run() {
                UsbModeChooserReceiver.this.initVidList(UsbModeChooserReceiver.SUPPORT_VENDOR_IDS);
                UsbModeChooserReceiver.this.initPidList(UsbModeChooserReceiver.SUPPORT_PRODUCT_IDS);
                Log.d("UsbModeChooserReceiver", "VID = " + usbDevice.getVendorId() + "  PID = " + usbDevice.getProductId());
                if (UsbModeChooserReceiver.this.mSupportVendorIdsList.contains(Integer.valueOf(usbDevice.getVendorId())) && UsbModeChooserReceiver.this.mSupportProductIdsList.contains(Integer.valueOf(usbDevice.getProductId()))) {
                    UsbModeChooserReceiver.this.startPhoneAttachedService(context);
                }
            }
        });
    }

    private static void handleUsbState(Context context, final Intent intent) {
        final Context applicationContext = context.getApplicationContext();
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.connecteddevice.usb.UsbModeChooserReceiver.1
            @Override // java.lang.Runnable
            public void run() {
                Bundle extras = intent.getExtras();
                boolean z = extras.getBoolean("configured");
                boolean z2 = extras.getBoolean(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED);
                boolean z3 = UserHandle.myUserId() == ActivityManager.getCurrentUser();
                boolean z4 = extras.getBoolean("accessory");
                if (z3 && !z4) {
                    if (!z2) {
                        UsbModeChooserReceiver.mSoftSwitch = false;
                        Log.d("UsbModeChooserReceiver", "handleUsbState stopService status=" + applicationContext.stopService(new Intent(applicationContext, MiUsbService.class)));
                        return;
                    }
                    KeyguardManager keyguardManager = (KeyguardManager) applicationContext.getSystemService("keyguard");
                    Log.d("UsbModeChooserReceiver", "configured = " + z + "  connected = " + z2 + " mSoftSwitch = " + UsbModeChooserReceiver.mSoftSwitch);
                    if (!z || UsbModeChooserReceiver.mSoftSwitch) {
                        return;
                    }
                    if (keyguardManager.isKeyguardLocked()) {
                        applicationContext.startService(new Intent(applicationContext, MiUsbService.class));
                        return;
                    }
                    Intent intent2 = new Intent();
                    intent2.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$UsbDetailsActivity"));
                    intent2.addFlags(268435456);
                    applicationContext.startActivity(intent2);
                    UsbModeChooserReceiver.mSoftSwitch = true;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initPidList(int[] iArr) {
        for (int i : iArr) {
            this.mSupportProductIdsList.add(Integer.valueOf(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initVidList(int[] iArr) {
        for (int i : iArr) {
            this.mSupportVendorIdsList.add(Integer.valueOf(i));
        }
    }

    private static void setOtgSoftFlag(boolean z) {
        mOTGSoftSwitch = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void startPhoneAttachedService(Context context) {
        if (!mOTGSoftSwitch) {
            setOtgSoftFlag(true);
            context.startService(new Intent(context, PhoneAttachedService.class));
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("UsbModeChooserReceiver", "action = " + action);
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            SystemProperties.set("miui.usb.dialog", "1");
        } else if ("android.hardware.usb.action.USB_STATE".equals(action)) {
            if (!"1".equals(SystemProperties.get("miui.usb.dialog", "0")) || "1".equals(SystemProperties.get("ro.boot.factorybuild", "0"))) {
                return;
            }
            handleUsbState(context, intent);
        } else if ("android.settings.action.MEDIA_MTP_TRANSFER_FAILED".equals(action) && !SystemProperties.get("ro.product.mod_device", "").endsWith("global")) {
            Intent intent2 = new Intent(context, MtpConnectionActivity.class);
            intent2.addFlags(268435456);
            context.startActivity(intent2);
        } else if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action) && SettingsFeatures.isSupportOtgReverseCharge()) {
            handlePhoneAttachedAsync(context, intent);
        } else if ("android.settings.action.PHONE_ATTACHED".equals(action) && SettingsFeatures.isSupportOtgReverseCharge()) {
            handlePhoneAttached(context);
        } else if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action) && SettingsFeatures.isSupportOtgReverseCharge()) {
            setOtgSoftFlag(false);
            SystemProperties.set("miui.reverse.charge", "0");
            context.stopService(new Intent(context, PhoneAttachedService.class));
        }
    }
}
