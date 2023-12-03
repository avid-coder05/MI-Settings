package com.android.settings;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.AodPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.AodUtils;
import java.util.TimeZone;
import miui.yellowpage.Tag;

/* loaded from: classes.dex */
public class AodStylePreferenceController extends AodPreferenceController {
    public static final String AUTO_DUAL_CLOCK = "auto_dual_clock";
    public static final String KEY_AOD_STYLE = "aod_show_style";
    public static final String RESIDENT_TIMEZONE = "resident_timezone";
    private static final String TAG = "AodStylePreferenceController";
    private boolean mAodShowModeStyleSelectAvaliable;
    private AsyncTask mLoadTask;

    public AodStylePreferenceController(Context context) {
        super(context, KEY_AOD_STYLE);
        this.mAodShowModeStyleSelectAvaliable = false;
    }

    public static boolean isDualClock(Context context) {
        boolean z = Settings.System.getInt(context.getContentResolver(), AUTO_DUAL_CLOCK, 0) == 1;
        String string = Settings.System.getString(context.getContentResolver(), RESIDENT_TIMEZONE);
        return (!z || string == null || TimeZone.getDefault().getID().equals(string)) ? false : true;
    }

    private void updateAodThumbnail(final Context context, final AodStylePreference aodStylePreference) {
        if (this.mAodShowModeStyleSelectAvaliable && AodUtils.isAodAvailable(this.mContext)) {
            AsyncTask asyncTask = this.mLoadTask;
            if (asyncTask != null) {
                asyncTask.cancel(true);
            }
            AsyncTask<Object, Void, Uri> asyncTask2 = new AsyncTask<Object, Void, Uri>() { // from class: com.android.settings.AodStylePreferenceController.1
                /* JADX INFO: Access modifiers changed from: protected */
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.os.AsyncTask
                public Uri doInBackground(Object... objArr) {
                    try {
                        Bundle call = context.getContentResolver().call(Uri.parse("content://com.miui.aod.settings"), Tag.TagWebService.ContentGetThumbnail.METHOD, (String) null, new Bundle());
                        if (call != null) {
                            return (Uri) call.getParcelable("uri");
                        }
                        return null;
                    } catch (Exception e) {
                        Log.d(AodStylePreferenceController.TAG, "getThumbnail failed:" + e);
                        return null;
                    }
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Uri uri) {
                    AodStylePreference aodStylePreference2;
                    if (uri == null || (aodStylePreference2 = aodStylePreference) == null) {
                        return;
                    }
                    aodStylePreference2.setAodStyleImage(uri);
                }
            };
            this.mLoadTask = asyncTask2;
            asyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
        }
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
        return (AodUtils.isAodAvailable(this.mContext) && this.mAodShowModeStyleSelectAvaliable) ? 0 : 3;
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
        preference.setEnabled(getAvailabilityStatus() != 5);
        if (preference instanceof AodStylePreference) {
            updateAodThumbnail(this.mContext, (AodStylePreference) preference);
        }
    }

    @Override // com.android.settings.core.AodPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
