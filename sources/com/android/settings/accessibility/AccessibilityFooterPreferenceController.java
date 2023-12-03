package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.HelpUtils;

/* loaded from: classes.dex */
public abstract class AccessibilityFooterPreferenceController extends BasePreferenceController {
    public AccessibilityFooterPreferenceController(Context context, String str) {
        super(context, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFooterPreferences$0(View view) {
        Context context = this.mContext;
        view.startActivityForResult(HelpUtils.getHelpIntent(context, context.getString(getHelpResource()), this.mContext.getClass().getName()), 0);
    }

    private void updateFooterPreferences(AccessibilityFooterPreference accessibilityFooterPreference) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.mContext.getString(R.string.accessibility_introduction_title, getLabelName()));
        stringBuffer.append("\n\n");
        stringBuffer.append(accessibilityFooterPreference.getTitle());
        accessibilityFooterPreference.setContentDescription(stringBuffer);
        if (getHelpResource() != 0) {
            accessibilityFooterPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityFooterPreferenceController$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AccessibilityFooterPreferenceController.this.lambda$updateFooterPreferences$0(view);
                }
            });
            accessibilityFooterPreference.setLearnMoreContentDescription(this.mContext.getString(R.string.footer_learn_more_content_description, getLabelName()));
        }
    }

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        updateFooterPreferences((AccessibilityFooterPreference) preferenceScreen.findPreference(getPreferenceKey()));
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    protected int getHelpResource() {
        return 0;
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    protected abstract String getLabelName();

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

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
