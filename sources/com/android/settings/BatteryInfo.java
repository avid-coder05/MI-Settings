package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.Message;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.widget.TextView;
import com.android.internal.app.IBatteryStats;
import miui.provider.Weather;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class BatteryInfo extends AppCompatActivity {
    private IBatteryStats mBatteryStats;
    private TextView mHealth;
    private IntentFilter mIntentFilter;
    private TextView mLevel;
    private TextView mPower;
    private TextView mScale;
    private IPowerManager mScreenStats;
    private TextView mStatus;
    private TextView mTechnology;
    private TextView mTemperature;
    private TextView mUptime;
    private TextView mVoltage;
    private Handler mHandler = new Handler() { // from class: com.android.settings.BatteryInfo.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            BatteryInfo.this.updateBatteryStats();
            sendEmptyMessageDelayed(1, 1000L);
        }
    };
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.settings.BatteryInfo.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                int intExtra = intent.getIntExtra("plugged", 0);
                BatteryInfo.this.mLevel.setText("" + intent.getIntExtra(Weather.AlertInfo.LEVEL, 0));
                BatteryInfo.this.mScale.setText("" + intent.getIntExtra("scale", 0));
                BatteryInfo.this.mVoltage.setText("" + intent.getIntExtra("voltage", 0) + " " + BatteryInfo.this.getString(R.string.battery_info_voltage_units));
                BatteryInfo.this.mTemperature.setText("" + BatteryInfo.this.tenthsToFixedString(intent.getIntExtra(Weather.WeatherBaseColumns.TEMPERATURE, 0)) + BatteryInfo.this.getString(R.string.battery_info_temperature_units));
                BatteryInfo.this.mTechnology.setText("" + intent.getStringExtra("technology"));
                BatteryInfo.this.mStatus.setText(com.android.settingslib.Utils.getBatteryStatus(context, intent));
                if (intExtra == 0) {
                    BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(R.string.battery_info_power_unplugged));
                } else if (intExtra == 1) {
                    BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(R.string.battery_info_power_ac));
                } else if (intExtra == 2) {
                    BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(R.string.battery_info_power_usb));
                } else if (intExtra == 3) {
                    BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(R.string.battery_info_power_ac_usb));
                } else if (intExtra != 4) {
                    BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(R.string.battery_info_power_unknown));
                } else {
                    BatteryInfo.this.mPower.setText(BatteryInfo.this.getString(R.string.battery_info_power_wireless));
                }
                int intExtra2 = intent.getIntExtra("health", 1);
                BatteryInfo.this.mHealth.setText(intExtra2 == 2 ? BatteryInfo.this.getString(R.string.battery_info_health_good) : intExtra2 == 3 ? BatteryInfo.this.getString(R.string.battery_info_health_overheat) : intExtra2 == 4 ? BatteryInfo.this.getString(R.string.battery_info_health_dead) : intExtra2 == 5 ? BatteryInfo.this.getString(R.string.battery_info_health_over_voltage) : intExtra2 == 6 ? BatteryInfo.this.getString(R.string.battery_info_health_unspecified_failure) : intExtra2 == 7 ? BatteryInfo.this.getString(R.string.battery_info_health_cold) : BatteryInfo.this.getString(R.string.battery_info_health_unknown));
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public final String tenthsToFixedString(int i) {
        int i2 = i / 10;
        return Integer.toString(i2) + "." + Math.abs(i - (i2 * 10));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBatteryStats() {
        this.mUptime.setText(DateUtils.formatElapsedTime(SystemClock.elapsedRealtime() / 1000));
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.battery_info);
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        setTitle(R.string.battery_info_label);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.mHandler.removeMessages(1);
        unregisterReceiver(this.mIntentReceiver);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.mStatus = (TextView) findViewById(R.id.status);
        this.mPower = (TextView) findViewById(R.id.power);
        this.mLevel = (TextView) findViewById(R.id.level);
        this.mScale = (TextView) findViewById(R.id.scale);
        this.mHealth = (TextView) findViewById(R.id.health);
        this.mTechnology = (TextView) findViewById(R.id.technology);
        this.mVoltage = (TextView) findViewById(R.id.voltage);
        this.mTemperature = (TextView) findViewById(R.id.temperature);
        this.mUptime = (TextView) findViewById(R.id.uptime);
        this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
        this.mScreenStats = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        this.mHandler.sendEmptyMessageDelayed(1, 1000L);
        registerReceiver(this.mIntentReceiver, this.mIntentFilter);
    }
}
