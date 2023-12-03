package com.android.settings.core;

import android.content.Context;
import android.content.IntentFilter;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.utils.ThreadUtils;

/* loaded from: classes.dex */
public abstract class LiveDataController extends BasePreferenceController {
    private MutableLiveData<CharSequence> mData;
    private Preference mPreference;
    protected CharSequence mSummary;

    public LiveDataController(Context context, String str) {
        super(context, str);
        this.mData = new MutableLiveData<>();
        this.mSummary = context.getText(R.string.summary_placeholder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initLifeCycleOwner$0() {
        this.mData.postValue(getSummaryTextInBackground());
    }

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mSummary;
    }

    protected abstract CharSequence getSummaryTextInBackground();

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public void initLifeCycleOwner(Fragment fragment) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.core.LiveDataController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LiveDataController.this.lambda$initLifeCycleOwner$0();
            }
        });
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
