package com.android.settings.accessibility.haptic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.accessibility.utils.MiuiAccessibilityUtils;
import miuix.preference.R$attr;

/* loaded from: classes.dex */
public class HapticRadioButtonPreference extends CustomRadioButtonPreference {
    private Context mContext;
    private ImageView mDetailArrow;
    private RadioButton mRadioButton;
    private View mRootView;
    private TextView mSummaryView;
    private TextView mTitleView;

    public HapticRadioButtonPreference(Context context) {
        super(context);
        init(context);
    }

    public HapticRadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R$attr.radioButtonPreferenceStyle);
        init(context);
    }

    public HapticRadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setLayoutResource(R.layout.bg_custom_radio_btn_layout);
        setWidgetLayoutResource(R.layout.preference_widget_detail);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchScreenReaderSettings() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
        intent.putExtra(":settings:show_fragment", getFragment());
        intent.putExtra(":settings:show_fragment_args", getExtras());
        intent.putExtra(":settings:show_fragment_title_resid", 0);
        intent.putExtra(":miui:starting_window_label", getTitle());
        intent.putExtra(":settings:show_fragment_title", getTitle());
        this.mContext.startActivity(intent);
    }

    @Override // com.android.settings.accessibility.haptic.CustomRadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mRootView = preferenceViewHolder.itemView;
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.haptic_preference_container_padding);
        this.mRootView.setPadding(dimensionPixelSize, 0, dimensionPixelSize, 0);
        this.mRadioButton = (RadioButton) this.mRootView.findViewById(16908289);
        this.mTitleView = (TextView) this.mRootView.findViewById(16908310);
        this.mSummaryView = (TextView) this.mRootView.findViewById(16908304);
        ImageView imageView = (ImageView) this.mRootView.findViewById(R.id.detail_arrow);
        this.mDetailArrow = imageView;
        imageView.setImportantForAccessibility(2);
        this.mDetailArrow.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.haptic.HapticRadioButtonPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HapticRadioButtonPreference.this.launchScreenReaderSettings();
            }
        });
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.accessibility.haptic.HapticRadioButtonPreference.2
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                HapticRadioButtonPreference.this.launchScreenReaderSettings();
                return true;
            }
        });
        setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.accessibility.haptic.HapticRadioButtonPreference.3
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean isAccessibilityServiceOn = MiuiAccessibilityUtils.isAccessibilityServiceOn(HapticRadioButtonPreference.this.getContext(), ComponentName.unflattenFromString(HapticRadioButtonPreference.this.getKey()));
                ((HapticRadioButtonPreference) preference).setChecked(isAccessibilityServiceOn);
                HapticRadioButtonPreference.this.setPreferenceState(isAccessibilityServiceOn);
                return false;
            }
        });
        setPreferenceState(MiuiAccessibilityUtils.isAccessibilityServiceOn(getContext(), ComponentName.unflattenFromString(getKey())));
    }

    public void setPreferenceState(boolean z) {
        int color = z ? this.mContext.getColor(R.color.haptic_radio_preference_bg_selected_color) : this.mContext.getColor(R.color.haptic_radio_preference_bg_unselected_color);
        View view = this.mRootView;
        if (view != null) {
            view.setBackgroundColor(color);
            View view2 = this.mRootView;
            view2.setPadding(view2.getPaddingRight(), 0, this.mRootView.getPaddingRight(), 0);
        }
        int color2 = z ? this.mContext.getColor(R.color.haptic_radio_preference_title_selected_color) : this.mContext.getColor(R.color.haptic_radio_preference_title_unselected_color);
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setTextColor(color2);
        }
        int color3 = z ? this.mContext.getColor(R.color.haptic_radio_preference_summary_selected_color) : this.mContext.getColor(R.color.haptic_radio_preference_summary_unselected_color);
        TextView textView2 = this.mSummaryView;
        if (textView2 != null) {
            textView2.setTextColor(color3);
        }
        Drawable drawable = z ? this.mContext.getDrawable(R.drawable.ic_arrow_detail_selected) : this.mContext.getDrawable(R.drawable.ic_arrow_detail_normal);
        ImageView imageView = this.mDetailArrow;
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
        }
    }
}
