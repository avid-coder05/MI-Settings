package miui.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import miui.cloud.Constants;
import miui.content.ExtraIntent;

/* loaded from: classes3.dex */
public class MiCloudSmsCmd {
    private static final String ACTIVATION_SMS_PREFIX = "AC/";
    private static final String ACTIVATION_SMS_PREFIX_2 = "ACT/";
    private static final int CMD_INDEX = 1;
    private static final int HEADER_INDEX = 0;
    private static final int MSG_ID_INDEX = 2;
    private static final String SEPARATOR = ",";
    private static final String SMS_CMD_HEADER = "mfc";
    private static final String SMS_CMD_HEADER_V1 = "mf";
    private static final String SMS_CMD_TAIL = "##";
    private static final String SMS_CMD_TAIL_V1 = "#";
    private static final String TAG = "MiCloudSmsCmd";
    private static final int TIME_INDEX = 3;
    public static final String TYPE_DISCARD_TOKEN = "d";
    public static final String TYPE_LOCATION = "l";
    public static final String TYPE_LOCK = "k";
    public static final String TYPE_NOISE = "n";
    public static final String TYPE_WIPE = "w";
    private static boolean[] sHexChars = new boolean[123];

    static {
        for (char c = 'A'; c <= 'Z'; c = (char) (c + 1)) {
            sHexChars[c] = true;
        }
        for (char c2 = 'a'; c2 <= 'z'; c2 = (char) (c2 + 1)) {
            sHexChars[c2] = true;
        }
        for (char c3 = '0'; c3 <= '9'; c3 = (char) (c3 + 1)) {
            sHexChars[c3] = true;
        }
    }

    private MiCloudSmsCmd() {
    }

    public static boolean checkAndDispatchActivationSms(Context context, int i, String str, String str2) {
        int indexOf = str2.indexOf(ACTIVATION_SMS_PREFIX);
        if (indexOf == -1) {
            return checkAndDispatchActivationSms2(context, i, str, str2);
        }
        Log.v(TAG, "checkAndDispatchActivationSms: The message looks like an activation");
        int i2 = indexOf + 3;
        int i3 = i2 + 32;
        int i4 = i3 + 1;
        int i5 = i4 + 11;
        if (str2.length() < i5) {
            Log.v(TAG, "checkAndDispatchActivationSms: length check failed, " + str2.length() + " < " + i5);
            return false;
        } else if (!isStrHex(str2, i2, i3)) {
            Log.v(TAG, "checkAndDispatchActivationSms: left hex check failed");
            return false;
        } else if (str2.charAt(i3) != ':') {
            Log.v(TAG, "checkAndDispatchActivationSms: colon check failed");
            return false;
        } else if (!isStrHex(str2, i4, i5)) {
            Log.v(TAG, "checkAndDispatchActivationSms: right hex check failed");
            return false;
        } else {
            Log.v(TAG, "checkAndDispatchActivationSms: activation SMS acknowledged, broadcasting...");
            Intent intent = new Intent("com.xiaomi.action.ACTIVATION_SMS_RECEIVED");
            intent.putExtra("extra_sim_index", i);
            intent.putExtra("extra_address", str);
            intent.putExtra("extra_msg_id", str2.substring(i4, i5));
            intent.putExtra("extra_vkey1", str2.substring(i2, i3));
            intent.setPackage("com.xiaomi.simactivate.service");
            context.sendBroadcastAsUser(intent, getAllUser());
            return true;
        }
    }

    private static boolean checkAndDispatchActivationSms2(Context context, int i, String str, String str2) {
        int indexOf = str2.indexOf(ACTIVATION_SMS_PREFIX_2);
        if (indexOf == -1) {
            return false;
        }
        Log.v(TAG, "checkAndDispatchActivationSmsi2: The message looks like an activation");
        int i2 = indexOf + 4;
        int i3 = i2 + 32;
        int i4 = i3 + 1;
        int i5 = i4 + 11;
        if (str2.length() < i5) {
            Log.v(TAG, "checkAndDispatchActivationSms2: length check failed, " + str2.length() + " < " + i5);
            return false;
        } else if (!isStrHex(str2, i2, i3)) {
            Log.v(TAG, "checkAndDispatchActivationSms2: left hex check failed");
            return false;
        } else if (str2.charAt(i3) != ':') {
            Log.v(TAG, "checkAndDispatchActivationSms2: colon check failed");
            return false;
        } else if (!isStrHex(str2, i4, i5)) {
            Log.v(TAG, "checkAndDispatchActivationSms2: right hex check failed");
            return false;
        } else {
            Log.v(TAG, "checkAndDispatchActivationSms2: activation SMS acknowledged, broadcasting...");
            Intent intent = new Intent("com.xiaomi.action.ACTIVATION_SMS_2_RECEIVED");
            intent.putExtra("extra_sim_index", i);
            intent.putExtra("extra_address", str);
            intent.putExtra("extra_sms", str2);
            intent.setPackage("com.xiaomi.simactivate.service");
            context.sendBroadcastAsUser(intent, getAllUser());
            return true;
        }
    }

    public static boolean checkSmsCmd(Context context, String str, String str2) {
        return checkSmsCmd(context, str, str2, -1, -1);
    }

    public static boolean checkSmsCmd(Context context, String str, String str2, int i) {
        return checkSmsCmd(context, str, str2, i, -1);
    }

    public static boolean checkSmsCmd(Context context, String str, String str2, int i, int i2) {
        return checkSmsCmd(null, context, str, str2, i, i2);
    }

    public static boolean checkSmsCmd(Intent intent, Context context, String str, String str2, int i, int i2) {
        if (TextUtils.isEmpty(str2)) {
            return false;
        }
        if (str2.indexOf(SMS_CMD_HEADER) >= 0 || str2.indexOf(SMS_CMD_HEADER_V1) >= 0) {
            if (str2.indexOf(SMS_CMD_TAIL) >= 0 || str2.indexOf(SMS_CMD_TAIL_V1) >= 0) {
                transferToFindDevice(intent, context, str, str2, i, i2);
                return true;
            }
            return false;
        }
        return false;
    }

    private static UserHandle getAllUser() {
        try {
            return (UserHandle) UserHandle.class.getField("ALL").get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("will not reach here", e);
        } catch (NoSuchFieldException e2) {
            throw new IllegalStateException("will not reach here", e2);
        }
    }

    private static boolean isStrHex(String str, int i, int i2) {
        while (i < i2) {
            char charAt = str.charAt(i);
            boolean[] zArr = sHexChars;
            if (charAt >= zArr.length || !zArr[charAt]) {
                return false;
            }
            i++;
        }
        return true;
    }

    private static void sendOldVersionCommandToFindDeviceService(Context context, String str, String str2, String str3, String str4, String str5, String str6) {
        Intent intent = new Intent();
        intent.putExtra(ExtraIntent.EXTRA_DEVICE_MSGID, str);
        intent.putExtra(ExtraIntent.EXTRA_DEVICE_TIME, str2);
        intent.putExtra(ExtraIntent.EXTRA_DEVICE_DIGEST, str5);
        intent.putExtra(ExtraIntent.EXTRA_DEVICE_CMD, str3);
        intent.putExtra(ExtraIntent.EXTRA_LOCK_DEVICE_PASSWORD, str4);
        intent.putExtra(ExtraIntent.EXTRA_FROM_ADDRESS, str6);
        String str7 = TYPE_LOCATION.equals(str3) ? ExtraIntent.ACTION_REQUEST_LOCATION : TYPE_NOISE.equals(str3) ? ExtraIntent.ACTION_NOISE : TYPE_LOCK.equals(str3) ? ExtraIntent.ACTION_LOCK_DEVICE : TYPE_WIPE.equals(str3) ? ExtraIntent.ACTION_WIPE_DATA : TYPE_DISCARD_TOKEN.equals(str3) ? ExtraIntent.ACTION_DISCARD_FIND_DEVICE_TOKEN : null;
        if (TextUtils.isEmpty(str7)) {
            return;
        }
        intent.setAction(str7);
        intent.setPackage("com.xiaomi.finddevice");
        if (context.startService(intent) == null) {
            intent.setPackage(Constants.CLOUDSERVICE_PACKAGE_NAME);
            context.startService(intent);
        }
    }

    private static void transferToFindDevice(Intent intent, Context context, final String str, final String str2, int i, int i2) {
        final PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, MiCloudSmsCmd.class.getName());
        newWakeLock.acquire();
        Intent intent2 = new Intent(ExtraIntent.INTENT_FIND_DEVICE_V2_CMD_RECEIVER);
        intent2.putExtra(ExtraIntent.EXTRA_FIND_DEVICE_V2_COMMAND_TYPE, "sms");
        intent2.putExtra(ExtraIntent.EXTRA_FROM_ADDRESS, str);
        intent2.putExtra(ExtraIntent.EXTRA_FROM_SLOT_ID, i);
        intent2.putExtra(ExtraIntent.EXTRA_FROM_SUB_ID, i2);
        intent2.putExtra(ExtraIntent.EXTRA_FIND_DEVICE_V2_COMMAND, str2);
        if (intent != null) {
            intent2.putExtra(ExtraIntent.EXTRA_FIND_DEVICE_INTERCEPT_SMS_INTENT, intent);
        }
        context.sendOrderedBroadcast(intent2, ExtraIntent.FIND_DEVICE_V2_RECEIVE_COMMAND_PERMISSION, new BroadcastReceiver() { // from class: miui.provider.MiCloudSmsCmd.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent3) {
                try {
                    if (getResultCode() == 0) {
                        MiCloudSmsCmd.transferToOldVersionFindDevice(context2, str, str2);
                    }
                } finally {
                    newWakeLock.release();
                }
            }
        }, null, 0, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void transferToOldVersionFindDevice(Context context, String str, String str2) {
        String str3;
        String[] split = str2.split(SEPARATOR);
        if (split == null || split.length < 5 || split[0].indexOf(SMS_CMD_HEADER) < 0) {
            Log.e(TAG, "Bad find device command: " + str2 + ". ");
            return;
        }
        String str4 = split[1];
        String str5 = split[2];
        String str6 = split[3];
        String str7 = null;
        if (TYPE_LOCK.equals(str4)) {
            str7 = split[4];
            str3 = split[5];
        } else {
            str3 = split[4];
        }
        sendOldVersionCommandToFindDeviceService(context, str5, str6, str4, str7, str3.substring(0, str3.lastIndexOf(SMS_CMD_TAIL)), str);
    }
}
