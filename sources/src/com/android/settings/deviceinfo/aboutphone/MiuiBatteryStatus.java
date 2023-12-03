package src.com.android.settings.deviceinfo.aboutphone;

import android.content.Intent;
import miui.vip.VipService;

/* loaded from: classes5.dex */
public class MiuiBatteryStatus {
    public int chargeDeviceType;
    public int chargeSpeed;
    public int health;
    public int level;
    public int maxChargingWattage;
    public int plugged;
    public int status;
    public int wireState;

    public MiuiBatteryStatus(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.status = i;
        this.plugged = i2;
        this.level = i3;
        this.wireState = i4;
        this.chargeSpeed = i5;
        this.chargeDeviceType = i6;
        this.health = i7;
        this.maxChargingWattage = i8;
    }

    public static int getMaxChargingWattage(Intent intent) {
        int intExtra = intent.getIntExtra("max_charging_current", -1);
        int intExtra2 = intent.getIntExtra("max_charging_voltage", -1);
        if (intExtra2 <= 0) {
            intExtra2 = 5000000;
        }
        if (intExtra > 0) {
            return (intExtra / VipService.VIP_SERVICE_FAILURE) * (intExtra2 / VipService.VIP_SERVICE_FAILURE);
        }
        return -1;
    }
}
