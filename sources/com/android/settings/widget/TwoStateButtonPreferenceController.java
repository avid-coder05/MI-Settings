package com.android.settings.widget;

import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes2.dex */
public abstract class TwoStateButtonPreferenceController extends BasePreferenceController implements View.OnClickListener {
    private Button mButtonOff;
    private Button mButtonOn;

    public TwoStateButtonPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        TwoStateButtonPreference twoStateButtonPreference = (TwoStateButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        Button stateOnButton = twoStateButtonPreference.getStateOnButton();
        this.mButtonOn = stateOnButton;
        stateOnButton.setOnClickListener(this);
        Button stateOffButton = twoStateButtonPreference.getStateOffButton();
        this.mButtonOff = stateOffButton;
        stateOffButton.setOnClickListener(this);
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

    public abstract void onButtonClicked(boolean z);

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        onButtonClicked(view.getId() == R.id.state_on_button);
    }

    protected void setButtonEnabled(boolean z) {
        this.mButtonOn.setEnabled(z);
        this.mButtonOff.setEnabled(z);
    }

    protected void setButtonVisibility(boolean z) {
        if (z) {
            this.mButtonOff.setVisibility(8);
            this.mButtonOn.setVisibility(0);
            return;
        }
        this.mButtonOff.setVisibility(0);
        this.mButtonOn.setVisibility(8);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
