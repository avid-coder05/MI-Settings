package com.android.settings.connecteddevice.usb;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import miui.os.Build;
import miui.yellowpage.YellowPageContract;
import miuix.appcompat.app.AlertDialog;
import miuix.core.util.IOUtils;

/* loaded from: classes.dex */
public class UsbModeChooserActivity extends Activity {
    private GridDialogAdapter mAdapter;
    private UsbBackend mBackend;
    private Context mContext;
    private long mCurrentMode;
    private AlertDialog mDialog;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private GridView mGridView;
    private LayoutInflater mLayoutInflater;
    public static final long[] DEFAULT_MODES = {0, 8, 4, 16};
    public static final long[] MIUI_DEFAULT_MODES_PD = {0, 128, 4, 16};
    public static final long[] MIUI_DEFAULT_MODES = {0, 4, 16};
    public static final String[] MIUI_SUPPORT_REVERSE_CHARGE = {"cmi", "umi", "apollo", "courbet", "courbetin"};
    public static final String[] MIUI_REVERSE_CHARGE_SWAP_DR = {"courbet", "courbetin"};
    public final String TAG = "UsbModeChooserActivity";
    private ArrayList<Long> mCurrentModesList = new ArrayList<>();
    private ArrayList<String> mCurrentChoicesList = new ArrayList<>();
    private HashMap<Long, Integer> mModesPositionMap = new HashMap<>();
    private BroadcastReceiver mDisconnectedReceiver = new BroadcastReceiver() { // from class: com.android.settings.connecteddevice.usb.UsbModeChooserActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.hardware.usb.action.USB_STATE".equals(intent.getAction())) {
                boolean booleanExtra = intent.getBooleanExtra(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, false);
                boolean booleanExtra2 = intent.getBooleanExtra("configured", false);
                boolean booleanExtra3 = intent.getBooleanExtra("accessory", false);
                if ((booleanExtra || UsbModeChooserActivity.this.isDestroyed()) && (booleanExtra2 || !booleanExtra3)) {
                    return;
                }
                UsbModeChooserActivity.this.mDialog.dismiss();
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GridDialogAdapter extends BaseAdapter {
        ImageView icon;
        private int mChooseIndex;
        TextView title;

        private GridDialogAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return UsbModeChooserActivity.this.mCurrentModesList.size();
        }

        @Override // android.widget.Adapter
        public Integer getItem(int i) {
            return Integer.valueOf(UsbModeChooserActivity.getTitleMiui12(((Long) UsbModeChooserActivity.this.mCurrentModesList.get(i)).longValue()));
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = UsbModeChooserActivity.this.mCurrentModesList.size() == 3 ? LayoutInflater.from(UsbModeChooserActivity.this.mContext).inflate(R.layout.item_usb_grid_dialog1, (ViewGroup) null) : LayoutInflater.from(UsbModeChooserActivity.this.mContext).inflate(R.layout.item_usb_grid_dialog2, (ViewGroup) null);
            }
            int titleMiui12 = UsbModeChooserActivity.getTitleMiui12(((Long) UsbModeChooserActivity.this.mCurrentModesList.get(i)).longValue());
            UsbModeChooserActivity usbModeChooserActivity = UsbModeChooserActivity.this;
            int icon = usbModeChooserActivity.getIcon(((Long) usbModeChooserActivity.mCurrentModesList.get(i)).longValue());
            this.title = (TextView) view.findViewById(R.id.title);
            this.icon = (ImageView) view.findViewById(R.id.icon);
            if (((Long) UsbModeChooserActivity.this.mCurrentModesList.get(i)).longValue() == 4 && Build.IS_INTERNATIONAL_BUILD) {
                this.title.setText(UsbModeChooserActivity.this.mContext.getString(titleMiui12) + "/Android Auto");
            } else {
                this.title.setText(titleMiui12);
            }
            this.icon.setImageResource(icon);
            if (i == this.mChooseIndex) {
                view.setBackgroundResource(R.drawable.usb_background_selected_view);
            }
            return view;
        }

        public void setChooseItem(int i) {
            this.mChooseIndex = i;
            notifyDataSetChanged();
        }
    }

    /* JADX WARN: Not initialized variable reg: 2, insn: 0x005c: MOVE (r1 I:??[OBJECT, ARRAY]) = (r2 I:??[OBJECT, ARRAY]), block:B:32:0x005c */
    private String getChargerType() {
        FileReader fileReader;
        IOException e;
        BufferedReader bufferedReader;
        FileNotFoundException e2;
        Reader reader;
        Reader reader2 = null;
        try {
            try {
                File file = new File("/sys/class/qcom-battery/real_type");
                File file2 = new File("/sys/class/power_supply/usb/real_type");
                if (!file.exists()) {
                    file = file2;
                }
                fileReader = new FileReader(file);
            } catch (Throwable th) {
                th = th;
                reader2 = reader;
            }
            try {
                bufferedReader = new BufferedReader(fileReader);
                try {
                    String readLine = bufferedReader.readLine();
                    IOUtils.closeQuietly((Reader) bufferedReader);
                    IOUtils.closeQuietly((Reader) fileReader);
                    return readLine;
                } catch (FileNotFoundException e3) {
                    e2 = e3;
                    Log.e("UsbModeChooserActivity", "getChargerType", e2);
                    IOUtils.closeQuietly((Reader) bufferedReader);
                    IOUtils.closeQuietly((Reader) fileReader);
                    return "";
                } catch (IOException e4) {
                    e = e4;
                    Log.e("UsbModeChooserActivity", "getChargerType", e);
                    IOUtils.closeQuietly((Reader) bufferedReader);
                    IOUtils.closeQuietly((Reader) fileReader);
                    return "";
                }
            } catch (FileNotFoundException e5) {
                bufferedReader = null;
                e2 = e5;
            } catch (IOException e6) {
                bufferedReader = null;
                e = e6;
            } catch (Throwable th2) {
                th = th2;
                IOUtils.closeQuietly(reader2);
                IOUtils.closeQuietly((Reader) fileReader);
                throw th;
            }
        } catch (FileNotFoundException e7) {
            fileReader = null;
            e2 = e7;
            bufferedReader = null;
        } catch (IOException e8) {
            fileReader = null;
            e = e8;
            bufferedReader = null;
        } catch (Throwable th3) {
            th = th3;
            fileReader = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getIcon(long j) {
        if (j == 0) {
            return this.mCurrentModesList.size() == 3 ? R.drawable.ic_usb_charging_only : R.drawable.ic_usb_charging_only_small;
        } else if (j == 4) {
            return this.mCurrentModesList.size() == 3 ? R.drawable.ic_usb_mtp : R.drawable.ic_usb_mtp_small;
        } else if (j == 16) {
            return this.mCurrentModesList.size() == 3 ? R.drawable.ic_usb_ptp : R.drawable.ic_usb_ptp_small;
        } else if (j == 8) {
            return R.drawable.ic_usb_MIDI;
        } else {
            if (j == 128) {
                return R.drawable.ic_usb_reverse_charging;
            }
            return 0;
        }
    }

    private int getPosition() {
        if (isInternalSW() && isAccessoryMode()) {
            return this.mModesPositionMap.get(4L).intValue();
        }
        if (this.mCurrentModesList.contains(Long.valueOf(this.mCurrentMode))) {
            return this.mModesPositionMap.get(Long.valueOf(this.mCurrentMode)).intValue();
        }
        return -1;
    }

    private static int getTitle(long j) {
        if (j == 0) {
            return R.string.usb_use_charging_only;
        }
        if (j == 4) {
            return R.string.usb_use_file_transfers;
        }
        if (j == 16) {
            return R.string.usb_use_photo_transfers;
        }
        if (j == 8) {
            return R.string.usb_use_MIDI;
        }
        if (j == 128) {
            return R.string.usb_reverse_charge;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getTitleMiui12(long j) {
        if (j == 0) {
            return R.string.usb_use_charging_only;
        }
        if (j == 4) {
            return R.string.usb_use_file_transfers_miui12_5;
        }
        if (j == 16) {
            return R.string.usb_use_photo_transfers_miui12_5;
        }
        if (j == 8) {
            return R.string.usb_use_MIDI_miui12_5;
        }
        if (j == 128) {
            return R.string.usb_reverse_charge;
        }
        return 0;
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        if (isInternalSW()) {
            builder.setTitle(R.string.usb_use);
            ArrayList<String> arrayList = this.mCurrentChoicesList;
            builder.setSingleChoiceItems((CharSequence[]) arrayList.toArray(new String[arrayList.size()]), getPosition(), new DialogInterface.OnClickListener() { // from class: com.android.settings.connecteddevice.usb.UsbModeChooserActivity.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!ActivityManager.isUserAMonkey() && i < UsbModeChooserActivity.this.mCurrentModesList.size()) {
                        if (((Long) UsbModeChooserActivity.this.mCurrentModesList.get(i)).longValue() == 128) {
                            UsbModeChooserActivity.this.mBackend.setPowerRole(1);
                            if (Arrays.asList(UsbModeChooserActivity.MIUI_REVERSE_CHARGE_SWAP_DR).contains(SystemProperties.get("ro.product.device", ""))) {
                                UsbModeChooserActivity.this.mBackend.setDataRole(1);
                            }
                        } else {
                            if (UsbModeChooserActivity.this.mBackend.getPowerRole() == 1) {
                                UsbModeChooserActivity.this.mBackend.setPowerRole(2);
                            }
                            UsbModeChooserActivity.this.mBackend.setCurrentFunctions(((Long) UsbModeChooserActivity.this.mCurrentModesList.get(i)).longValue());
                        }
                    }
                    if (!UsbModeChooserActivity.this.isDestroyed()) {
                        dialogInterface.dismiss();
                    }
                    UsbModeChooserActivity.this.finish();
                }
            });
        } else {
            View inflate = this.mLayoutInflater.inflate(R.layout.custom_usb_mode_chooser, (ViewGroup) null, false);
            this.mGridView = (GridView) inflate.findViewById(R.id.grid_view);
            if (this.mCurrentModesList.size() == 3) {
                this.mGridView.setNumColumns(3);
            } else {
                this.mGridView.setNumColumns(2);
            }
            GridDialogAdapter gridDialogAdapter = new GridDialogAdapter();
            this.mAdapter = gridDialogAdapter;
            gridDialogAdapter.setChooseItem(getPosition());
            this.mGridView.setAdapter((ListAdapter) this.mAdapter);
            this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.settings.connecteddevice.usb.UsbModeChooserActivity.3
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    if (!ActivityManager.isUserAMonkey() && i < UsbModeChooserActivity.this.mCurrentModesList.size()) {
                        if (((Long) UsbModeChooserActivity.this.mCurrentModesList.get(i)).longValue() == 128) {
                            UsbModeChooserActivity.this.mBackend.setPowerRole(1);
                            if (Arrays.asList(UsbModeChooserActivity.MIUI_REVERSE_CHARGE_SWAP_DR).contains(SystemProperties.get("ro.product.device", ""))) {
                                UsbModeChooserActivity.this.mBackend.setDataRole(1);
                            }
                        } else {
                            if (UsbModeChooserActivity.this.mBackend.getPowerRole() == 1) {
                                UsbModeChooserActivity.this.mBackend.setPowerRole(2);
                            }
                            UsbModeChooserActivity.this.mBackend.setCurrentFunctions(((Long) UsbModeChooserActivity.this.mCurrentModesList.get(i)).longValue());
                        }
                    }
                    if (UsbModeChooserActivity.this.mDialog != null && !UsbModeChooserActivity.this.isDestroyed()) {
                        UsbModeChooserActivity.this.mDialog.dismiss();
                    }
                    UsbModeChooserActivity.this.finish();
                }
            });
            builder.setTitle(R.string.usb_use_miui12_5);
            builder.setView(inflate);
        }
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.connecteddevice.usb.UsbModeChooserActivity.4
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                UsbModeChooserActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.connecteddevice.usb.UsbModeChooserActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                UsbModeChooserActivity.this.finish();
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.getWindow().getDecorView().setHapticFeedbackEnabled(false);
        if (isFinishing()) {
            return;
        }
        this.mDialog.show();
    }

    private void initModesList(long[] jArr) {
        int i = 0;
        for (int i2 = 0; i2 < jArr.length; i2++) {
            if (this.mBackend.areFunctionsSupported(jArr[i2])) {
                this.mCurrentModesList.add(Long.valueOf(jArr[i2]));
                this.mModesPositionMap.put(Long.valueOf(jArr[i2]), Integer.valueOf(i));
                if (jArr[i2] == 4 && Build.IS_INTERNATIONAL_BUILD) {
                    this.mCurrentChoicesList.add(getString(getTitle(jArr[i2])) + "/Android Auto");
                } else {
                    this.mCurrentChoicesList.add(getString(getTitle(jArr[i2])));
                }
                i++;
            }
        }
    }

    private boolean isAccessoryMode() {
        return (this.mCurrentMode & 2) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInternalSW() {
        return false;
    }

    private boolean isPDCharge() {
        return TextUtils.equals(getChargerType(), "USB_PD");
    }

    private boolean isSupportReverseCharging() {
        return Arrays.asList(MIUI_SUPPORT_REVERSE_CHARGE).contains(SystemProperties.get("ro.product.device", ""));
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.layoutInDisplayCutoutMode = 1;
        getWindow().setAttributes(attributes);
        this.mEnforcedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this, "no_usb_file_transfer", UserHandle.myUserId());
        UsbBackend usbBackend = new UsbBackend(this);
        this.mBackend = usbBackend;
        UsbModeChooserReceiver.mSoftSwitch = true;
        if (usbBackend.getPowerRole() == 1) {
            this.mCurrentMode = 128L;
        } else {
            this.mCurrentMode = this.mBackend.getCurrentFunctions();
        }
        this.mLayoutInflater = LayoutInflater.from(this);
        if (this.mEnforcedAdmin != null) {
            initModesList(DEFAULT_MODES);
        } else {
            initModesList((isPDCharge() && isSupportReverseCharging()) ? MIUI_DEFAULT_MODES_PD : MIUI_DEFAULT_MODES);
        }
        initDialog();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        this.mDialog.dismiss();
        this.mDialog = null;
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        registerReceiver(this.mDisconnectedReceiver, new IntentFilter("android.hardware.usb.action.USB_STATE"));
    }

    @Override // android.app.Activity
    protected void onStop() {
        unregisterReceiver(this.mDisconnectedReceiver);
        super.onStop();
    }
}
