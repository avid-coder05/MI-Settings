package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.display.DarkModeTimeModeUtil;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.miuisettings.preference.CheckBoxPreference;

/* loaded from: classes2.dex */
public class CustomCheckBoxPreference extends CheckBoxPreference {
    View mCheckboxView;
    private Context mContext;
    private boolean mIsDialogStyle;

    public CustomCheckBoxPreference(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomCheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public CustomCheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public CustomCheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mContext = context;
    }

    private void setDialogStyleBackground(View view) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        if (DarkModeTimeModeUtil.isDarkModeEnable(getContext())) {
            view.setBackgroundColor(getContext().getColor(R.color.miuix_appcompat_dialog_bg_color_dark));
        }
    }

    private void setViewLp(View view) {
        if (view == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = -2;
        layoutParams.width = -1;
        view.setLayoutParams(layoutParams);
    }

    public View getView() {
        return this.mCheckboxView;
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        Context context;
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        this.mCheckboxView = view;
        if (this.mIsDialogStyle) {
            setDialogStyleBackground(view);
        }
        if (SettingsFeatures.isSplitTabletDevice()) {
            RelativeLayout relativeLayout = (RelativeLayout) this.mCheckboxView.findViewById(R.id.light_mode_outer_view);
            RelativeLayout relativeLayout2 = (RelativeLayout) this.mCheckboxView.findViewById(R.id.dark_mode_outer_view);
            RelativeLayout relativeLayout3 = (RelativeLayout) this.mCheckboxView.findViewById(R.id.light_mode_view);
            RelativeLayout relativeLayout4 = (RelativeLayout) this.mCheckboxView.findViewById(R.id.dark_mode_view);
            setViewLp(relativeLayout3);
            setViewLp(relativeLayout4);
            setViewLp(relativeLayout2);
            setViewLp(relativeLayout);
            if (!(this.mCheckboxView instanceof TopImageGuideView) || (context = this.mContext) == null) {
                return;
            }
            int dp2px = MiuiUtils.dp2px(context, 16.0f);
            if (MiuiUtils.isLandScape(this.mContext)) {
                dp2px = MiuiUtils.dp2px(this.mContext, 66.18f);
            }
            View view2 = this.mCheckboxView;
            view2.setPadding(dp2px, view2.getPaddingTop(), dp2px, this.mCheckboxView.getPaddingBottom());
        }
    }

    public void requestFocusDelay() {
        View view = this.mCheckboxView;
        if (view != null) {
            view.postDelayed(new Runnable() { // from class: com.android.settings.widget.CustomCheckBoxPreference.1
                @Override // java.lang.Runnable
                public void run() {
                    CustomCheckBoxPreference.this.mCheckboxView.sendAccessibilityEvent(8);
                }
            }, 500L);
        }
    }

    public void setIsDialogStyle(boolean z) {
        this.mIsDialogStyle = z;
    }
}
