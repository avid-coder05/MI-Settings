package com.android.settings.bluetooth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.R;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public final class BluetoothPermissionRequest extends BroadcastReceiver {
    Context mContext;
    BluetoothDevice mDevice;
    private NotificationChannel mNotificationChannel = null;
    int mRequestType;

    private boolean checkUserChoice() {
        int simAccessPermission;
        int i = this.mRequestType;
        boolean z = false;
        if (i != 2 && i != 3 && i != 4) {
            Log.d("BluetoothPermissionRequest", "checkUserChoice(): Unknown RequestType " + this.mRequestType);
            return false;
        }
        CachedBluetoothDeviceManager cachedDeviceManager = Utils.getLocalBtManager(this.mContext).getCachedDeviceManager();
        if (cachedDeviceManager.findDevice(this.mDevice) == null) {
            cachedDeviceManager.addDevice(this.mDevice);
        }
        int i2 = this.mRequestType;
        if (i2 == 2) {
            int phonebookAccessPermission = this.mDevice.getPhonebookAccessPermission();
            if (phonebookAccessPermission != 0) {
                if (phonebookAccessPermission == 1) {
                    sendReplyIntentToReceiver(true);
                } else if (phonebookAccessPermission == 2) {
                    sendReplyIntentToReceiver(false);
                } else {
                    Log.e("BluetoothPermissionRequest", "Bad phonebookPermission: " + phonebookAccessPermission);
                }
                z = true;
            }
            Log.d("BluetoothPermissionRequest", "checkUserChoice(): returning " + z);
            return z;
        } else if (i2 == 3) {
            int messageAccessPermission = this.mDevice.getMessageAccessPermission();
            if (messageAccessPermission != 0) {
                if (messageAccessPermission == 1) {
                    sendReplyIntentToReceiver(true);
                } else if (messageAccessPermission == 2) {
                    sendReplyIntentToReceiver(false);
                } else {
                    Log.e("BluetoothPermissionRequest", "Bad messagePermission: " + messageAccessPermission);
                }
                z = true;
            }
            Log.d("BluetoothPermissionRequest", "checkUserChoice(): returning " + z);
            return z;
        } else {
            if (i2 == 4 && (simAccessPermission = this.mDevice.getSimAccessPermission()) != 0) {
                if (simAccessPermission == 1) {
                    sendReplyIntentToReceiver(true);
                } else if (simAccessPermission == 2) {
                    sendReplyIntentToReceiver(false);
                } else {
                    Log.e("BluetoothPermissionRequest", "Bad simPermission: " + simAccessPermission);
                }
                z = true;
            }
            Log.d("BluetoothPermissionRequest", "checkUserChoice(): returning " + z);
            return z;
        }
    }

    private String getNotificationTag(int i) {
        if (i == 2) {
            return "Phonebook Access";
        }
        int i2 = this.mRequestType;
        if (i2 == 3) {
            return "Message Access";
        }
        if (i2 == 4) {
            return "SIM Access";
        }
        return null;
    }

    private void sendReplyIntentToReceiver(boolean z) {
        Intent intent = new Intent("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY");
        intent.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", z ? 1 : 2);
        intent.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
        intent.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
        this.mContext.sendBroadcast(intent, "android.permission.BLUETOOTH_CONNECT");
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String string;
        String string2;
        this.mContext = context;
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        Log.d("BluetoothPermissionRequest", "onReceive" + action);
        if (!action.equals("android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST")) {
            if (action.equals("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL")) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
                int intExtra = intent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2);
                this.mRequestType = intExtra;
                notificationManager.cancel(getNotificationTag(intExtra), 17301632);
            }
        } else if (((UserManager) context.getSystemService("user")).isManagedProfile()) {
            Log.d("BluetoothPermissionRequest", "Blocking notification for managed profile.");
        } else {
            this.mDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            this.mRequestType = intent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 1);
            Log.d("BluetoothPermissionRequest", "onReceive request type: " + this.mRequestType);
            if (checkUserChoice()) {
                return;
            }
            Intent intent2 = new Intent(action);
            intent2.setClass(context, BluetoothPermissionActivity.class);
            intent2.setFlags(402653184);
            intent2.setType(Integer.toString(this.mRequestType));
            intent2.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
            intent2.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
            BluetoothDevice bluetoothDevice = this.mDevice;
            String address = bluetoothDevice != null ? bluetoothDevice.getAddress() : null;
            BluetoothDevice bluetoothDevice2 = this.mDevice;
            String name = bluetoothDevice2 != null ? bluetoothDevice2.getName() : null;
            if (((PowerManager) context.getSystemService("power")).isScreenOn() && LocalBluetoothPreferences.shouldShowDialogInForeground(context, address, name)) {
                context.startActivity(intent2);
                return;
            }
            Intent intent3 = new Intent("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY");
            intent3.setPackage("com.android.bluetooth");
            intent3.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
            intent3.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", 2);
            intent3.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
            String createRemoteName = Utils.createRemoteName(context, this.mDevice);
            int i = this.mRequestType;
            if (i == 2) {
                string = context.getString(R.string.bluetooth_phonebook_request);
                string2 = context.getString(R.string.bluetooth_phonebook_access_notification_content);
            } else if (i == 3) {
                string = context.getString(R.string.bluetooth_map_request);
                string2 = context.getString(R.string.bluetooth_message_access_notification_content);
            } else if (i != 4) {
                string = context.getString(R.string.bluetooth_connect_access_notification_title);
                string2 = context.getString(R.string.bluetooth_connect_access_notification_content, createRemoteName, createRemoteName);
            } else {
                string = context.getString(R.string.bluetooth_sim_card_access_notification_title);
                string2 = context.getString(R.string.bluetooth_sim_card_access_notification_content, createRemoteName, createRemoteName);
            }
            NotificationManager notificationManager2 = (NotificationManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
            if (this.mNotificationChannel == null) {
                NotificationChannel notificationChannel = new NotificationChannel("bluetooth_notification_channel", context.getString(R.string.bluetooth), 4);
                this.mNotificationChannel = notificationChannel;
                notificationManager2.createNotificationChannel(notificationChannel);
            }
            Notification build = new Notification.Builder(context, "bluetooth_notification_channel").setContentTitle(string).setTicker(string2).setContentText(string2).setStyle(new Notification.BigTextStyle().bigText(string2)).setSmallIcon(17301632).setAutoCancel(true).setPriority(2).setOnlyAlertOnce(false).setDefaults(-1).setContentIntent(PendingIntent.getActivity(context, 0, intent2, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE)).setDeleteIntent(PendingIntent.getBroadcast(context, 0, intent3, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE)).setColor(context.getColor(17170460)).setLocalOnly(true).build();
            build.flags |= 32;
            notificationManager2.notify(getNotificationTag(this.mRequestType), 17301632, build);
        }
    }
}
