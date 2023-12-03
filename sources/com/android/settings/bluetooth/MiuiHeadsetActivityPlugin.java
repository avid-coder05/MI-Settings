package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import com.android.settings.recommend.PageIndexManager;
import com.iqiyi.android.qigsaw.core.extension.ComponentInfo;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class MiuiHeadsetActivityPlugin extends Activity {
    protected BluetoothDevice mDevice = null;
    protected String mSupport = "";
    protected String mComeFrom = "";
    protected String mVirtualDeviceAddress = "";
    protected String mVirtualDeviceName = "";
    protected String mDeviceID = "";
    protected String mAddress = "";
    private Intent mPendingIntent = null;

    private void onSuccessfullyLoad() {
        Log.e("MiuiHeadsetActivityPlugin", "onSuccessfullyLoad ");
        if (this.mPendingIntent != null) {
            Intent intent = new Intent();
            intent.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
            intent.putExtra("MIUI_HEADSET_SUPPORT", this.mSupport);
            intent.putExtra("COME_FROM", this.mComeFrom);
            intent.putExtra("VIRTUAL_DEVICE_ADDRESS", this.mVirtualDeviceAddress);
            intent.putExtra("VIRTUAL_DEVICE_NAME", this.mVirtualDeviceName);
            intent.putExtra("bluetoothaddress", this.mAddress);
            intent.putExtra("DEVICE_ID", this.mDeviceID);
            intent.setClassName(getPackageName(), ComponentInfo.java_ACTIVITIES);
            intent.addFlags(268468224);
            if (FitSplitUtils.isFitSplit()) {
                intent.addMiuiFlags(16);
                intent.removeFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON);
                intent.removeFlags(268435456);
            }
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    private void startQigsawInstaller(String str) {
        Intent intent = new Intent(this, QigsawInstaller.class);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(str);
        intent.putStringArrayListExtra("moduleNames", arrayList);
        startActivityForResult(intent, 10);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        ArrayList<String> stringArrayListExtra;
        super.onActivityResult(i, i2, intent);
        if (i == 10) {
            try {
                Log.e("MiuiHeadsetActivityPlugin", "error requestCode = " + i + " RESULT_OK = -1 RESULT_CANCELED= 0");
                if (i2 != -1) {
                    finish();
                } else if (intent != null && (stringArrayListExtra = intent.getStringArrayListExtra("moduleNames")) != null && stringArrayListExtra.size() == 1) {
                    onSuccessfullyLoad();
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        window.setGravity(51);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = 0;
        attributes.y = 0;
        attributes.width = 1;
        attributes.height = 1;
        attributes.type = PageIndexManager.PAGE_KEY_FUNCTION_SETTINGS;
        attributes.flags = 32;
        window.setAttributes(attributes);
        Intent intent = getIntent();
        if (intent == null) {
            Log.e("MiuiHeadsetActivityPlugin", "intent is null");
            return;
        }
        this.mComeFrom = intent.getStringExtra("COME_FROM");
        this.mDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        intent.getStringExtra("bluetoothaddress");
        this.mComeFrom = intent.getStringExtra("COME_FROM");
        this.mVirtualDeviceAddress = intent.getStringExtra("VIRTUAL_DEVICE_ADDRESS");
        this.mVirtualDeviceName = intent.getStringExtra("VIRTUAL_DEVICE_NAME");
        this.mAddress = intent.getStringExtra("bluetoothaddress");
        String stringExtra = intent.getStringExtra("MIUI_HEADSET_SUPPORT");
        this.mSupport = stringExtra;
        if (stringExtra != null) {
            String[] split = stringExtra.split("\\,");
            if (split == null || split.length != 2) {
                Log.e("MiuiHeadsetActivityPlugin", "Length error");
            } else {
                this.mDeviceID = split[0];
                Log.d("MiuiHeadsetActivityPlugin", "Length OK" + this.mDeviceID);
            }
        }
        this.mPendingIntent = intent;
        startQigsawInstaller("java");
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        try {
            overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        try {
            overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
