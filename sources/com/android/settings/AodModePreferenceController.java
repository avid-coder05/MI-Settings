package com.android.settings;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.AodPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.AodUtils;

/* loaded from: classes.dex */
public class AodModePreferenceController extends AodPreferenceController {
    public static final String KEY_AOD_MODE = "aod_mode_value_preference";
    private static final String TAG = "AodModePreferenceController";
    private AsyncTask mLoadTask;

    public AodModePreferenceController(Context context) {
        super(context, KEY_AOD_MODE);
    }

    private void updateAodShowMode(final Context context, final KeyguardRestrictedPreference keyguardRestrictedPreference) {
        if (keyguardRestrictedPreference == null) {
            return;
        }
        AsyncTask asyncTask = this.mLoadTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
        AsyncTask<Object, Void, String> asyncTask2 = new AsyncTask<Object, Void, String>() { // from class: com.android.settings.AodModePreferenceController.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public String doInBackground(Object... objArr) {
                try {
                    Bundle call = context.getContentResolver().call(Uri.parse("content://com.miui.aod.settings"), "getAodModeTip", (String) null, new Bundle());
                    if (call != null) {
                        return call.getString("modeTip");
                    }
                    return null;
                } catch (Exception e) {
                    Log.e(AodModePreferenceController.TAG, "doInBackground: " + e.getMessage());
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

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return !AodUtils.isAodAvailable(this.mContext) ? 3 : 0;
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
