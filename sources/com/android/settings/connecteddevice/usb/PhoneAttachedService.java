package com.android.settings.connecteddevice.usb;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemProperties;
import com.android.settings.R;
import com.android.settings.recommend.PageIndexManager;
import java.util.ArrayList;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class PhoneAttachedService extends Service {
    private AlertDialog mDialog;
    private ArrayList<String> mCurrentChoicesList = new ArrayList<>();
    private final int MODE_DATA_MTP = 0;
    private final int MODE_CHARGE_ONLY = 1;
    public final int[] MIUI_DEFAULT_MODES = {0, 1};
    private final String MIUI_REVERSE_CHARGR = "miui.reverse.charge";
    private BroadcastReceiver mDisconnectedReceiver = new BroadcastReceiver() { // from class: com.android.settings.connecteddevice.usb.PhoneAttachedService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (!"android.hardware.usb.action.USB_DEVICE_DETACHED".equals(intent.getAction()) || PhoneAttachedService.this.mDialog == null) {
                return;
            }
            PhoneAttachedService.this.mDialog.dismiss();
            PhoneAttachedService.this.stopSelf();
        }
    };

    private int getTitle(int i) {
        if (i != 0) {
            if (i != 1) {
                return 0;
            }
            return R.string.use_otg_charge_only;
        }
        return R.string.use_otg_file_transfers;
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(R.string.use_otg_title);
        ArrayList<String> arrayList = this.mCurrentChoicesList;
        builder.setSingleChoiceItems((CharSequence[]) arrayList.toArray(new String[arrayList.size()]), 0, new DialogInterface.OnClickListener() { // from class: com.android.settings.connecteddevice.usb.PhoneAttachedService.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                PhoneAttachedService.this.setMode(i);
                dialogInterface.dismiss();
                PhoneAttachedService.this.stopSelf();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.connecteddevice.usb.PhoneAttachedService.3
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                PhoneAttachedService.this.stopSelf();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.connecteddevice.usb.PhoneAttachedService.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                PhoneAttachedService.this.stopSelf();
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.getWindow().setType(PageIndexManager.PAGE_FONT_SIZE_WEIGHT_SETTINGS);
        this.mDialog.show();
    }

    private void initModesList(int[] iArr) {
        for (int i : iArr) {
            this.mCurrentChoicesList.add(getString(getTitle(i)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMode(int i) {
        if (i == 0) {
            SystemProperties.set("miui.reverse.charge", "0");
        } else if (i != 1) {
            SystemProperties.set("miui.reverse.charge", "0");
        } else {
            SystemProperties.set("miui.reverse.charge", "1");
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        registerReceiver(this.mDisconnectedReceiver, new IntentFilter("android.hardware.usb.action.USB_DEVICE_DETACHED"));
        initModesList(this.MIUI_DEFAULT_MODES);
        initDialog();
    }

    @Override // android.app.Service
    public void onDestroy() {
        unregisterReceiver(this.mDisconnectedReceiver);
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
