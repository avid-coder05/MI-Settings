package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.accounts.AccountRestrictionHelper;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import miui.os.Build;

/* loaded from: classes2.dex */
public class EmergencyBroadcastPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final String CELL_BROAD;
    private final String GOOGLE_CELL_BROAD;
    private final String TARGET_CLASS;
    private AccountRestrictionHelper mHelper;
    private PackageManager mPm;
    private final String mPrefKey;
    private UserManager mUserManager;

    EmergencyBroadcastPreferenceController(Context context, AccountRestrictionHelper accountRestrictionHelper, String str) {
        super(context);
        this.CELL_BROAD = "com.android.cellbroadcastreceiver";
        this.GOOGLE_CELL_BROAD = "com.google.android.cellbroadcastreceiver";
        this.TARGET_CLASS = "com.android.cellbroadcastreceiver.CellBroadcastSettings";
        this.mPrefKey = str;
        this.mHelper = accountRestrictionHelper;
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mPm = this.mContext.getPackageManager();
    }

    public EmergencyBroadcastPreferenceController(Context context, String str) {
        this(context, new AccountRestrictionHelper(context), str);
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x001f, code lost:
    
        if (r4.mPm.getApplicationEnabledSetting(r2) == 2) goto L9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isCellBroadcastAppLinkEnabled() {
        /*
            r4 = this;
            android.content.Context r0 = r4.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 285540354(0x11050002, float:1.0491852E-28)
            boolean r0 = r0.getBoolean(r1)
            r1 = 0
            if (r0 == 0) goto L22
            android.content.Context r2 = r4.mContext     // Catch: java.lang.IllegalArgumentException -> L21
            java.lang.String r2 = com.android.internal.telephony.CellBroadcastUtils.getDefaultCellBroadcastReceiverPackageName(r2)     // Catch: java.lang.IllegalArgumentException -> L21
            if (r2 == 0) goto L21
            android.content.pm.PackageManager r3 = r4.mPm     // Catch: java.lang.IllegalArgumentException -> L21
            int r2 = r3.getApplicationEnabledSetting(r2)     // Catch: java.lang.IllegalArgumentException -> L21
            r3 = 2
            if (r2 != r3) goto L22
        L21:
            r0 = r1
        L22:
            android.content.pm.PackageManager r4 = r4.mPm
            boolean r4 = com.android.settings.custs.CellBroadcastUtil.nccBroadcastEnabled(r4)
            if (r4 == 0) goto L2b
            r0 = 1
        L2b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.EmergencyBroadcastPreferenceController.isCellBroadcastAppLinkEnabled():boolean");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPrefKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (this.mPrefKey.equals(preference.getKey())) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            if (MiuiUtils.isApplicationInstalled(this.mContext, "com.google.android.cellbroadcastreceiver")) {
                intent.setClassName("com.google.android.cellbroadcastreceiver", "com.android.cellbroadcastreceiver.CellBroadcastSettings");
                this.mContext.startActivity(intent);
                return true;
            } else if (MiuiUtils.isApplicationInstalled(this.mContext, "com.android.cellbroadcastreceiver")) {
                intent.setClassName("com.android.cellbroadcastreceiver", "com.android.cellbroadcastreceiver.CellBroadcastSettings");
                this.mContext.startActivity(intent);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            if (MiuiUtils.isApplicationInstalled(this.mContext, "com.android.cellbroadcastreceiver") || MiuiUtils.isApplicationInstalled(this.mContext, "com.google.android.cellbroadcastreceiver")) {
                return this.mUserManager.isAdminUser() && isCellBroadcastAppLinkEnabled() && !this.mHelper.hasBaseUserRestriction("no_config_cell_broadcasts", UserHandle.myUserId());
            }
            Log.d("EmergencyBroadcastPreferenceController", "broadcast recevier is not installed");
            return false;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof RestrictedPreference) {
            ((RestrictedPreference) preference).checkRestrictionAndSetDisabled("no_config_cell_broadcasts");
        }
    }
}
