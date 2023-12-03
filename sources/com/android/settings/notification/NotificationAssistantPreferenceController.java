package com.android.settings.notification;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes2.dex */
public class NotificationAssistantPreferenceController extends TogglePreferenceController {
    private static final int AVAILABLE = 1;
    private static final String KEY_NAS = "notification_assistant";
    private static final String TAG = "NASPreferenceController";
    private Fragment mFragment;
    protected NotificationBackend mNotificationBackend;
    private int mUserId;
    private final UserManager mUserManager;

    public NotificationAssistantPreferenceController(Context context) {
        super(context, KEY_NAS);
        this.mUserId = UserHandle.myUserId();
        this.mUserManager = UserManager.get(context);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
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
        ComponentName allowedNotificationAssistant = this.mNotificationBackend.getAllowedNotificationAssistant();
        return allowedNotificationAssistant != null && allowedNotificationAssistant.equals(this.mNotificationBackend.getDefaultNotificationAssistant());
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBackend(NotificationBackend notificationBackend) {
        this.mNotificationBackend = notificationBackend;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        ComponentName defaultNotificationAssistant = z ? this.mNotificationBackend.getDefaultNotificationAssistant() : null;
        if (!z) {
            setNotificationAssistantGranted(null);
            return true;
        } else if (this.mFragment != null) {
            showDialog(defaultNotificationAssistant);
            return false;
        } else {
            throw new IllegalStateException("No fragment to start activity");
        }
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setNotificationAssistantGranted(ComponentName componentName) {
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "nas_settings_updated", 0, this.mUserId) == 0) {
            this.mNotificationBackend.setNASMigrationDoneAndResetDefault(this.mUserId, componentName != null);
        }
        this.mNotificationBackend.setNotificationAssistantGranted(componentName);
    }

    protected void showDialog(ComponentName componentName) {
        NotificationAssistantDialogFragment.newInstance(this.mFragment, componentName).show(this.mFragment.getFragmentManager(), TAG);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
