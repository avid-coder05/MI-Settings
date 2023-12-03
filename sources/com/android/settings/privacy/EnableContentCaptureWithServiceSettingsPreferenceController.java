package com.android.settings.privacy;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import androidx.preference.Preference;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.dashboard.profileselector.UserAdapter;
import com.android.settings.privacy.EnableContentCaptureWithServiceSettingsPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.ContentCaptureUtils;
import com.android.settingslib.R$string;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public final class EnableContentCaptureWithServiceSettingsPreferenceController extends TogglePreferenceController {
    private static final String TAG = "ContentCaptureController";
    private final UserManager mUserManager;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class ProfileSelectDialog {
        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$show$0(ArrayList arrayList, Preference preference, Context context, DialogInterface dialogInterface, int i) {
            context.startActivityAsUser(preference.getIntent().addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON), (UserHandle) arrayList.get(i));
        }

        public static void show(final Context context, final Preference preference) {
            UserManager userManager = UserManager.get(context);
            List users = userManager.getUsers();
            final ArrayList arrayList = new ArrayList(users.size());
            Iterator it = users.iterator();
            while (it.hasNext()) {
                arrayList.add(((UserInfo) it.next()).getUserHandle());
            }
            if (arrayList.size() == 1) {
                context.startActivityAsUser(preference.getIntent().addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON), (UserHandle) arrayList.get(0));
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R$string.choose_profile).setAdapter(UserAdapter.createUserAdapter(userManager, context, arrayList), new DialogInterface.OnClickListener() { // from class: com.android.settings.privacy.EnableContentCaptureWithServiceSettingsPreferenceController$ProfileSelectDialog$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    EnableContentCaptureWithServiceSettingsPreferenceController.ProfileSelectDialog.lambda$show$0(arrayList, preference, context, dialogInterface, i);
                }
            }).show();
        }
    }

    public EnableContentCaptureWithServiceSettingsPreferenceController(Context context, String str) {
        super(context, str);
        this.mUserManager = UserManager.get(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateState$0(Preference preference) {
        ProfileSelectDialog.show(this.mContext, preference);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ContentCaptureUtils.isFeatureAvailable() && ContentCaptureUtils.getServiceSettingsComponentName() != null ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return ContentCaptureUtils.isEnabledForUser(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        ContentCaptureUtils.setEnabledForUser(this.mContext, z);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ComponentName serviceSettingsComponentName = ContentCaptureUtils.getServiceSettingsComponentName();
        if (serviceSettingsComponentName != null) {
            preference.setIntent(new Intent("android.intent.action.MAIN").setComponent(serviceSettingsComponentName));
        } else {
            Log.w(TAG, "No component name for custom service settings");
            preference.setSelectable(false);
        }
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.privacy.EnableContentCaptureWithServiceSettingsPreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                boolean lambda$updateState$0;
                lambda$updateState$0 = EnableContentCaptureWithServiceSettingsPreferenceController.this.lambda$updateState$0(preference2);
                return lambda$updateState$0;
            }
        });
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
