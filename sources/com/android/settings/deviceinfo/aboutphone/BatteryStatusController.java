package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import miui.provider.Weather;
import src.com.android.settings.deviceinfo.aboutphone.MiuiBatteryStatus;

/* loaded from: classes.dex */
public class BatteryStatusController extends BaseBatteryController {
    public static final String EXTRA_QUICK_CHARGE_TYPE = "miui.intent.extra.quick_charge_type";
    public static final int NONE = -1;
    public static final int NORMAL = 0;
    public static final int QUICK = 1;
    public static final int STRONG_SUPER_QUICK = 3;
    public static final int SUPER_QUICK = 2;
    public static final int UNKNOWN = -1;
    public static final int WIRED = 11;
    public static final int WIRELESS = 10;
    private final int MSG_BATTERY_WHAT;
    private String Tag;
    private int chargeDeviceType;
    private Message current;
    private MiuiBatteryStatus mBatteryStatus;
    private Context mContext;
    private Handler mHandler;

    public BatteryStatusController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
        this.Tag = "BatteryStatusController";
        this.chargeDeviceType = -1;
        this.MSG_BATTERY_WHAT = 802;
        this.mContext = context;
        this.mBatteryStatus = new MiuiBatteryStatus(1, 0, 0, 0, 0, -1, 1, -1);
        this.mHandler = new Handler() { // from class: com.android.settings.deviceinfo.aboutphone.BatteryStatusController.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 802) {
                    return;
                }
                BatteryStatusController.this.notifyBatteryStatusChanged(((Integer) message.obj).intValue());
            }
        };
    }

    private int checkWireState(int i, int i2) {
        boolean z = i == 4;
        boolean z2 = i == 1 || i == 2;
        if (i2 == 2 || i2 == 5 || i2 == 4) {
            if (z) {
                return 10;
            }
            return z2 ? 11 : -1;
        }
        return -1;
    }

    private void dualAuth(int i) {
        this.mHandler.removeMessages(802);
        Message obtain = Message.obtain();
        this.current = obtain;
        obtain.what = 802;
        obtain.obj = Integer.valueOf(i);
        this.mHandler.sendMessageDelayed(this.current, 0L);
    }

    private int getCurrentChargeDeviceType(int i) {
        if (i == 11 || i == 10) {
            return this.chargeDeviceType;
        }
        return -1;
    }

    private boolean isBatteryStatusChanged(int i, int i2, int i3) {
        MiuiBatteryStatus miuiBatteryStatus = this.mBatteryStatus;
        if (i == miuiBatteryStatus.level) {
            return (i2 == miuiBatteryStatus.plugged && i3 == miuiBatteryStatus.status) ? false : true;
        }
        miuiBatteryStatus.level = i;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyBatteryStatusChanged(int i) {
        String chargingHintText = getChargingHintText(this.mContext, this.mBatteryStatus.level, i);
        if (isAvailable()) {
            this.mPreference.setValue(chargingHintText);
        }
    }

    @Override // com.android.settings.deviceinfo.aboutphone.BaseBatteryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.deviceinfo.aboutphone.BaseBatteryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public String getChargingHintText(Context context, int i, int i2) {
        Resources resources = context.getResources();
        MiuiBatteryStatus miuiBatteryStatus = this.mBatteryStatus;
        if (miuiBatteryStatus.wireState == -1) {
            return resources.getString(R.string.settings_charging_not_charging, Integer.valueOf(i));
        }
        if (i == 100) {
            return resources.getString(R.string.settings_charging_fully_charged, Integer.valueOf(i));
        }
        if (miuiBatteryStatus == null || !(i2 == 3 || i2 == 2)) {
            if (miuiBatteryStatus == null || i2 != 1) {
                if (miuiBatteryStatus == null || i2 != 0) {
                    return null;
                }
                return resources.getString(R.string.settings_charging_at_normal_speed, Integer.valueOf(i));
            }
            return resources.getString(R.string.settings_charging_at_fast_speed, Integer.valueOf(i));
        }
        return resources.getString(R.string.settings_charging_at_top_speed, Integer.valueOf(i));
    }

    @Override // com.android.settings.deviceinfo.aboutphone.BaseBatteryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.deviceinfo.aboutphone.BaseBatteryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.deviceinfo.aboutphone.BaseBatteryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.deviceinfo.aboutphone.BaseBatteryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.deviceinfo.aboutphone.BaseBatteryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.deviceinfo.aboutphone.BaseBatteryController
    public void onBatteryChange(Intent intent) {
        long currentTimeMillis = System.currentTimeMillis();
        Log.d(this.Tag, "in onBatteryChange, currentTime is " + currentTimeMillis);
        super.onBatteryChange(intent);
        int intExtra = intent.getIntExtra("status", 1);
        int intExtra2 = intent.getIntExtra("plugged", 0);
        int intExtra3 = intent.getIntExtra(Weather.AlertInfo.LEVEL, 0);
        int intExtra4 = intent.getIntExtra("health", 1);
        int maxChargingWattage = MiuiBatteryStatus.getMaxChargingWattage(intent);
        int checkWireState = checkWireState(intExtra2, intExtra);
        boolean isBatteryStatusChanged = isBatteryStatusChanged(intExtra3, intExtra2, intExtra);
        MiuiBatteryStatus miuiBatteryStatus = this.mBatteryStatus;
        miuiBatteryStatus.plugged = intExtra2;
        miuiBatteryStatus.wireState = checkWireState;
        miuiBatteryStatus.status = intExtra;
        miuiBatteryStatus.health = intExtra4;
        miuiBatteryStatus.maxChargingWattage = maxChargingWattage;
        if (isBatteryStatusChanged) {
            miuiBatteryStatus.chargeDeviceType = getCurrentChargeDeviceType(checkWireState);
            MiuiBatteryStatus miuiBatteryStatus2 = this.mBatteryStatus;
            miuiBatteryStatus2.chargeSpeed = MiuiUtils.getChargeSpeed(miuiBatteryStatus2.wireState, miuiBatteryStatus2.chargeDeviceType);
            dualAuth(this.mBatteryStatus.chargeSpeed);
        }
    }

    @Override // com.android.settings.deviceinfo.aboutphone.BaseBatteryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    @Override // com.android.settings.deviceinfo.aboutphone.BaseBatteryController
    public void wireQuickCharge(Intent intent) {
        long currentTimeMillis = System.currentTimeMillis();
        Log.d(this.Tag, "in wireQuickCharge, currentTime is " + currentTimeMillis);
        int intExtra = intent.getIntExtra(EXTRA_QUICK_CHARGE_TYPE, -1);
        this.chargeDeviceType = intExtra;
        MiuiBatteryStatus miuiBatteryStatus = this.mBatteryStatus;
        int i = miuiBatteryStatus.wireState;
        if ((i == 11 || i == 10) && miuiBatteryStatus != null && intExtra >= 0) {
            miuiBatteryStatus.chargeDeviceType = intExtra;
            miuiBatteryStatus.chargeSpeed = MiuiUtils.getChargeSpeed(i, intExtra);
            dualAuth(this.mBatteryStatus.chargeSpeed);
        }
    }
}
