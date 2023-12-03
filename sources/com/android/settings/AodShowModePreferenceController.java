package com.android.settings;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.AodPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.AodUtils;

/* loaded from: classes.dex */
public class AodShowModePreferenceController extends AodPreferenceController {
    public static final String KEY_AOD_SHOW_MODE = "aod_show_mode";
    private static final String TAG = "AodShowModePreferenceController";
    private boolean mAodShowModeStyleSelectAvaliable;
    private AsyncTask mLoadTask;

    public AodShowModePreferenceController(Context context) {
        super(context, KEY_AOD_SHOW_MODE);
        this.mAodShowModeStyleSelectAvaliable = false;
    }

    private void updateAodShowMode(final Context context, final KeyguardRestrictedPreference keyguardRestrictedPreference) {
        if (keyguardRestrictedPreference == null) {
            return;
        }
        AsyncTask asyncTask = this.mLoadTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
        AsyncTask<Object, Void, String> asyncTask2 = new AsyncTask<Object, Void, String>() { // from class: com.android.settings.AodShowModePreferenceController.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public String doInBackground(Object... objArr) {
                try {
                    Bundle call = context.getContentResolver().call(Uri.parse("content://com.miui.aod.settings"), "getShowModeTip", (String) null, new Bundle());
                    if (call != null) {
                        return call.getString("modeTip");
                    }
                    return null;
                } catch (Exception e) {
                    Log.e(AodShowModePreferenceController.TAG, "doInBackground: " + e.getMessage());
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(String str) {
                if (keyguardRestrictedPreference == null || TextUtils.isEmpty(str)) {
                    return;
                }
                keyguardRestrictedPreference.setValue(str);
            }
        };
        this.mLoadTask = asyncTask2;
        asyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
    }

    public void cancelTask() {
        AsyncTask asyncTask = this.mLoadTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (AodUtils.isAodAvailable(this.mContext) && this.mAodShowModeStyleSelectAvaliable) {
            return AodUtils.isAodEnabled(this.mContext) ? 0 : 5;
        }
        return 3;
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public void setAodShowModeStyleSelectAvaliable(boolean z) {
        this.mAodShowModeStyleSelectAvaliable = z;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference == null) {
            return;
        }
        if (preference instanceof KeyguardRestrictedPreference) {
            updateAodShowMode(this.mContext, (KeyguardRestrictedPreference) preference);
        }
        preference.setEnabled(getAvailabilityStatus() != 5);
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
