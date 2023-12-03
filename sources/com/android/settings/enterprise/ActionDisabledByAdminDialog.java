package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.settingslib.RestrictedLockUtils;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class ActionDisabledByAdminDialog extends AppCompatActivity implements DialogInterface.OnDismissListener {
    private ActionDisabledByAdminDialogHelper mDialogHelper;

    RestrictedLockUtils.EnforcedAdmin getAdminDetailsFromIntent(Intent intent) {
        Bundle bundle;
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin = new RestrictedLockUtils.EnforcedAdmin(null, UserHandle.of(UserHandle.myUserId()));
        if (intent == null) {
            return enforcedAdmin;
        }
        enforcedAdmin.component = (ComponentName) intent.getParcelableExtra("android.app.extra.DEVICE_ADMIN");
        int intExtra = intent.getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        if (enforcedAdmin.component == null) {
            bundle = ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getEnforcingAdminAndUserDetails(intExtra, getRestrictionFromIntent(intent));
            if (bundle != null) {
                enforcedAdmin.component = (ComponentName) bundle.getParcelable("android.app.extra.DEVICE_ADMIN");
            }
        } else {
            bundle = null;
        }
        if (intent.hasExtra("android.intent.extra.USER")) {
            enforcedAdmin.user = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
        } else {
            if (bundle != null) {
                intExtra = bundle.getInt("android.intent.extra.USER_ID", UserHandle.myUserId());
            }
            if (intExtra == -10000) {
                enforcedAdmin.user = null;
            } else {
                enforcedAdmin.user = UserHandle.of(intExtra);
            }
        }
        return enforcedAdmin;
    }

    String getRestrictionFromIntent(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getStringExtra("android.app.extra.RESTRICTION");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        RestrictedLockUtils.EnforcedAdmin adminDetailsFromIntent = getAdminDetailsFromIntent(getIntent());
        String restrictionFromIntent = getRestrictionFromIntent(getIntent());
        ActionDisabledByAdminDialogHelper actionDisabledByAdminDialogHelper = new ActionDisabledByAdminDialogHelper(this, restrictionFromIntent);
        this.mDialogHelper = actionDisabledByAdminDialogHelper;
        actionDisabledByAdminDialogHelper.prepareDialogBuilder(restrictionFromIntent, adminDetailsFromIntent).setOnDismissListener(this).show();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        RestrictedLockUtils.EnforcedAdmin adminDetailsFromIntent = getAdminDetailsFromIntent(intent);
        this.mDialogHelper.updateDialog(getRestrictionFromIntent(intent), adminDetailsFromIntent);
    }
}
