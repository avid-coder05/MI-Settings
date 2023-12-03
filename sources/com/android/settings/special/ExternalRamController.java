package com.android.settings.special;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.SystemProperties;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.telephony.ToggleSubscriptionDialogActivity;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.special.ExternalRamController;
import com.android.settingslib.util.ToastUtil;
import com.android.settingslib.utils.ThreadUtils;
import java.text.DecimalFormat;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class ExternalRamController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public static final String MIUI_EXTM_ENABLE = "persist.miui.extm.enable";
    public static final String PERSIST_MIUI_EXTM_BDSIZE = "persist.miui.extm.bdsize";
    private static final String PREFERENCE_KEY = "external_ram";
    private static final String TAG = "ExternalRamController";
    private AlertDialog mAlertDlg;
    private Context mContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.special.ExternalRamController$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass1 implements DialogInterface.OnClickListener {
        final /* synthetic */ CheckBoxPreference val$checkBox;

        AnonymousClass1(CheckBoxPreference checkBoxPreference) {
            this.val$checkBox = checkBoxPreference;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onClick$0() {
            try {
                Thread.sleep(500L);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ExternalRamController.this.rebootPhone();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                this.val$checkBox.setChecked(!r1.isChecked());
                ExternalRamController.setChecked(this.val$checkBox.isChecked());
                ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.special.ExternalRamController$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ExternalRamController.AnonymousClass1.this.lambda$onClick$0();
                    }
                });
            }
        }
    }

    public ExternalRamController(Context context) {
        this(context, PREFERENCE_KEY);
    }

    public ExternalRamController(Context context, String str) {
        super(context, str);
        this.mContext = context;
    }

    private void buildAlertDialog(final CheckBoxPreference checkBoxPreference) {
        int i = R.string.external_ram_dialog_title;
        int i2 = R.string.external_ram_dialog_message;
        int i3 = R.string.external_ram_dialog_btn_ok;
        int i4 = R.string.external_ram_dialog_btn_cancel;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(i);
        builder.setMessage(i2);
        builder.setPositiveButton(i3, new AnonymousClass1(checkBoxPreference));
        builder.setNegativeButton(i4, new DialogInterface.OnClickListener() { // from class: com.android.settings.special.ExternalRamController.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i5) {
                if (i5 == -2) {
                    ExternalRamController.setChecked(!checkBoxPreference.isChecked());
                }
            }
        });
        builder.setCancelable(false);
        this.mAlertDlg = builder.create();
    }

    public static float getBdSize() {
        float f;
        try {
            f = Float.parseFloat(SystemProperties.get(PERSIST_MIUI_EXTM_BDSIZE));
        } catch (Exception e) {
            e.printStackTrace();
            f = 0.0f;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("getBdSize: ");
        sb.append(f);
        sb.append(" = ");
        float f2 = f / 1024.0f;
        sb.append(f2);
        sb.append("GB");
        Log.d(TAG, sb.toString());
        return f2;
    }

    public static String getBdSizeInfo() {
        return new DecimalFormat("#0.00").format(getBdSize()) + " GB";
    }

    public static String getDialogInfo(Context context) {
        if (context == null) {
            return "";
        }
        return context.getResources().getString(R.string.external_ram_dialog_icon_info, new DecimalFormat("#0.00").format(getBdSize()), "GB");
    }

    public static boolean isChecked() {
        int i;
        try {
            i = Integer.parseInt(SystemProperties.get(MIUI_EXTM_ENABLE));
        } catch (Exception e) {
            e.printStackTrace();
            i = -1;
        }
        return i == 1;
    }

    public static boolean isChecked(Context context) {
        return context.getSharedPreferences("ExternalRamStatus", 0).getBoolean("isExternalRamOn", true);
    }

    public static boolean isShow() {
        int i;
        try {
            i = Integer.parseInt(SystemProperties.get(MIUI_EXTM_ENABLE));
        } catch (Exception e) {
            e.printStackTrace();
            i = -1;
        }
        return i != -1;
    }

    private static boolean isSupportExternalRam() {
        StatFs statFs = new StatFs("/data");
        long availableBytes = statFs.getAvailableBytes();
        long totalBytes = statFs.getTotalBytes();
        float f = (((float) availableBytes) * 1.0f) / ((float) totalBytes);
        Log.d(TAG, " available size: " + availableBytes + ", total size: " + totalBytes);
        return f >= 0.1f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rebootPhone() {
        boolean isChecked = isChecked(this.mContext);
        try {
            Log.d(TAG, "rebootPhone begin");
            ((PowerManager) this.mContext.getSystemService("power")).reboot(isChecked ? ToggleSubscriptionDialogActivity.ARG_enable : "disableexternal ram...");
        } catch (Exception e) {
            Log.e(TAG, "rebootPhone fail: " + e);
        }
    }

    public static void setChecked(boolean z) {
        try {
            SystemProperties.set(MIUI_EXTM_ENABLE, z ? "1" : "0");
        } catch (Exception unused) {
            Log.e(TAG, "external change property error");
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isShow() ? 0 : 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mContext == null) {
            return "";
        }
        return this.mContext.getResources().getString(R.string.external_ram_summary, new DecimalFormat("#0.00").format(getBdSize()), "GB");
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (PREFERENCE_KEY.equals(preference.getKey()) && (preference instanceof CheckBoxPreference)) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            if (!checkBoxPreference.isChecked() && !isSupportExternalRam()) {
                ToastUtil.show(this.mContext, R.string.external_ram_toast, 1);
                return false;
            }
            buildAlertDialog(checkBoxPreference);
            AlertDialog alertDialog = this.mAlertDlg;
            if (alertDialog != null) {
                alertDialog.show();
            }
        }
        return false;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
