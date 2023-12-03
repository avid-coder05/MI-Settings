package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import com.android.settings.R;
import com.android.settings.search.SearchUpdater;
import com.android.settings.search.tree.DisplaySettingsTree;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;

/* loaded from: classes.dex */
public class ExpertRadioButtonPreference extends com.android.settingslib.miuisettings.preference.RadioButtonPreference implements View.OnClickListener {
    private ImageView arrow;
    boolean isPreferenceScreenEnable;
    private CheckedTextView mSummaryCheckedTextView;

    public ExpertRadioButtonPreference(Context context) {
        super(context);
        this.isPreferenceScreenEnable = true;
        init();
    }

    public ExpertRadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isPreferenceScreenEnable = true;
        init();
    }

    public ExpertRadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.isPreferenceScreenEnable = true;
        init();
    }

    protected void init() {
        setWidgetLayoutResource(R.layout.expert_arrow);
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.arrow_image_view);
        this.arrow = imageView;
        if (imageView != null) {
            imageView.setEnabled(isChecked() && this.isPreferenceScreenEnable);
            ImageView imageView2 = this.arrow;
            imageView2.setAlpha(imageView2.isEnabled() ? 1.0f : 0.4f);
            this.arrow.setOnClickListener(this);
        }
        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(16908304);
        this.mSummaryCheckedTextView = checkedTextView;
        if (checkedTextView != null) {
            checkedTextView.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.display.ExpertRadioButtonPreference.1
                @Override // android.view.View.AccessibilityDelegate
                public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfo);
                    accessibilityNodeInfo.setCheckable(false);
                }
            });
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Intent intent = new Intent(DisplaySettingsTree.ACTION_EXPERT);
        if (getContext().getPackageManager().queryIntentActivities(intent, SearchUpdater.GOOGLE).size() > 0) {
            getContext().startActivity(intent);
            MiStatInterfaceUtils.trackPreferenceClick("screen_effect", "expert_details");
            OneTrackInterfaceUtils.trackPreferenceClick("screen_effect", "expert_details");
        }
    }

    @Override // androidx.preference.TwoStatePreference
    public void setChecked(boolean z) {
        super.setChecked(z);
        ImageView imageView = this.arrow;
        if (imageView != null) {
            imageView.setEnabled(z && this.isPreferenceScreenEnable);
        }
    }

    public void setPreferenceScreenStatus(boolean z) {
        this.isPreferenceScreenEnable = z;
    }
}
