package com.android.settings.accessibility.voiceaccess;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.accessibility.MiuiAccessibilityAsrController;
import com.android.settings.backup.CustomRadioButtonPreference;
import com.android.settings.display.DarkModeTimeModeUtil;
import miuix.preference.R$attr;

/* loaded from: classes.dex */
public class VoiceAccessRadioButtonPreference extends CustomRadioButtonPreference {
    private Context mContext;
    private ImageView mDetailArrow;
    private RadioButton mRadioButton;
    private View mRootView;
    private TextView mSummaryView;
    private TextView mTitleView;

    public VoiceAccessRadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R$attr.radioButtonPreferenceStyle);
        this.mContext = context;
        setWidgetLayoutResource(R.layout.preference_widget_detail);
    }

    public VoiceAccessRadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
        setWidgetLayoutResource(R.layout.preference_widget_detail);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchVoiceAccess() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName(MiuiAccessibilityAsrController.MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME, "com.miui.accessibility.voiceaccess.settings.VoiceAccessSettings");
        this.mContext.startActivity(intent);
    }

    @Override // com.android.settings.backup.CustomRadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mRootView = preferenceViewHolder.itemView;
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.voice_access_preference_container_padding);
        this.mRootView.setPadding(dimensionPixelSize, 0, dimensionPixelSize, 0);
        RadioButton radioButton = (RadioButton) this.mRootView.findViewById(16908289);
        this.mRadioButton = radioButton;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) radioButton.getLayoutParams();
        layoutParams.leftMargin = dimensionPixelSize;
        this.mRadioButton.setLayoutParams(layoutParams);
        this.mTitleView = (TextView) this.mRootView.findViewById(16908310);
        this.mSummaryView = (TextView) this.mRootView.findViewById(16908304);
        ImageView imageView = (ImageView) this.mRootView.findViewById(R.id.detail_arrow);
        this.mDetailArrow = imageView;
        imageView.setImportantForAccessibility(2);
        this.mDetailArrow.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.voiceaccess.VoiceAccessRadioButtonPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                VoiceAccessRadioButtonPreference.this.launchVoiceAccess();
            }
        });
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.accessibility.voiceaccess.VoiceAccessRadioButtonPreference.2
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                VoiceAccessRadioButtonPreference.this.launchVoiceAccess();
                return true;
            }
        });
        setPreferenceState(VoiceAccessController.isVoiceAccessOn(this.mContext));
    }

    public void setPreferenceState(boolean z) {
        setCustomItemIcon(this.mContext.getResources().getDrawable((z || DarkModeTimeModeUtil.isDarkModeEnable(this.mContext)) ? R.drawable.ic_voice_access_selected : R.drawable.ic_voice_access_normal));
        int color = z ? this.mContext.getColor(R.color.voice_access_radio_preference_bg_selected_color) : this.mContext.getColor(R.color.voice_access_radio_preference_bg_unselected_color);
        View view = this.mRootView;
        if (view != null) {
            view.setBackgroundColor(color);
            View view2 = this.mRootView;
            view2.setPadding(view2.getPaddingRight(), 0, this.mRootView.getPaddingRight(), 0);
        }
        int color2 = z ? this.mContext.getColor(R.color.voice_access_radio_preference_title_selected_color) : this.mContext.getColor(R.color.voice_access_radio_preference_title_unselected_color);
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setTextColor(color2);
        }
        int color3 = z ? this.mContext.getColor(R.color.voice_access_radio_preference_summary_selected_color) : this.mContext.getColor(R.color.voice_access_radio_preference_summary_unselected_color);
        String string = z ? this.mContext.getString(R.string.vpn_on) : this.mContext.getString(R.string.vpn_off);
        TextView textView2 = this.mSummaryView;
        if (textView2 != null) {
            textView2.setVisibility(0);
            this.mSummaryView.setText(string);
            this.mSummaryView.setTextColor(color3);
        }
        Drawable drawable = z ? this.mContext.getDrawable(R.drawable.ic_arrow_detail_selected) : this.mContext.getDrawable(R.drawable.ic_arrow_detail_normal);
        ImageView imageView = this.mDetailArrow;
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
        }
    }
}
