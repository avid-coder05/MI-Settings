package com.android.settings.core;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.SeekBarPreference;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public abstract class SliderPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public SliderPreferenceController(Context context, String str) {
        super(context, str);
    }

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public abstract int getMax();

    public abstract int getMin();

    @Override // com.android.settings.core.BasePreferenceController
    public int getSliceType() {
        return 2;
    }

    public abstract int getSliderPosition();

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
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

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return setSliderPosition(((Integer) obj).intValue());
    }

    public abstract boolean setSliderPosition(int i);

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z = preference instanceof SeekBarPreference;
        if (z) {
            ((SeekBarPreference) preference).setValue(getSliderPosition());
        } else if (preference instanceof com.android.settings.widget.SeekBarPreference) {
            ((com.android.settings.widget.SeekBarPreference) preference).setProgress(getSliderPosition());
        } else if (z) {
            ((SeekBarPreference) preference).setValue(getSliderPosition());
        }
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
