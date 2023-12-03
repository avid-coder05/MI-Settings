package com.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.lang.ref.WeakReference;
import miui.cloud.finddevice.FindDeviceStatusManagerProvider;

/* loaded from: classes2.dex */
public class FindMyDeviceController extends BasePreferenceController {

    /* loaded from: classes2.dex */
    private static class MyTask extends AsyncTask<Void, Void, String> {
        private WeakReference<Context> contextWeakReference;
        private WeakReference<ValuePreference> valuePreferenceWeakReference;

        public MyTask(Context context, ValuePreference valuePreference) {
            this.contextWeakReference = new WeakReference<>(context);
            this.valuePreferenceWeakReference = new WeakReference<>(valuePreference);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            Context context = this.contextWeakReference.get();
            if (context != null) {
                return FindDeviceStatusManagerProvider.isLastStatusOpen(context.getApplicationContext()) ? context.getString(R.string.find_my_device_enable) : context.getString(R.string.find_my_device_disable);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            super.onPostExecute((MyTask) str);
            ValuePreference valuePreference = this.valuePreferenceWeakReference.get();
            if (valuePreference == null || TextUtils.isEmpty(str)) {
                return;
            }
            valuePreference.setValue(str);
        }
    }

    public FindMyDeviceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
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

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ValuePreference valuePreference = (ValuePreference) preference;
        new MyTask(this.mContext.getApplicationContext(), valuePreference).execute(new Void[0]);
        if (RegionUtils.IS_KOREA) {
            valuePreference.setSummary(this.mContext.getString(R.string.find_my_device_summary));
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
