package com.android.settings;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.MiuiRestrictedPreference;
import java.lang.ref.WeakReference;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;

/* loaded from: classes.dex */
public class KeyguardRestrictedPreference extends MiuiRestrictedPreference {
    protected Context mContext;
    private float mFontScale;
    private Handler mHandler;
    private boolean mIconNeedPadding;
    private boolean mIsCardStyle;
    private boolean mIsDarkMode;
    private boolean mSelected;
    private boolean mShowBackground;
    private boolean mShowRightArrow;
    private boolean mShowTouchAnim;
    private TextView mValueView;

    public KeyguardRestrictedPreference(Context context) {
        super(context);
        this.mShowRightArrow = true;
        this.mShowBackground = false;
        this.mIconNeedPadding = false;
        this.mIsCardStyle = false;
        this.mShowTouchAnim = false;
        this.mSelected = false;
        this.mFontScale = 1.0f;
        this.mHandler = new Handler();
    }

    public KeyguardRestrictedPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowRightArrow = true;
        this.mShowBackground = false;
        this.mIconNeedPadding = false;
        this.mIsCardStyle = false;
        this.mShowTouchAnim = false;
        this.mSelected = false;
        this.mFontScale = 1.0f;
        this.mHandler = new Handler();
        this.mContext = context;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.RestrictedPreferenceStyle);
        try {
            this.mShowBackground = obtainStyledAttributes.getBoolean(R$styleable.RestrictedPreferenceStyle_showBackground, false);
            this.mShowRightArrow = obtainStyledAttributes.getBoolean(R$styleable.RestrictedPreferenceStyle_showRightArrow, true);
            this.mIconNeedPadding = obtainStyledAttributes.getBoolean(R$styleable.RestrictedPreferenceStyle_iconNeedPadding, false);
            this.mIsCardStyle = obtainStyledAttributes.getBoolean(R$styleable.RestrictedPreferenceStyle_isCardStyle, false);
            this.mShowTouchAnim = obtainStyledAttributes.getBoolean(R$styleable.RestrictedPreferenceStyle_showTouchAnim, false);
            obtainStyledAttributes.recycle();
            this.mFontScale = this.mContext.getResources().getConfiguration().fontScale;
            UiModeManager uiModeManager = (UiModeManager) context.getSystemService("uimode");
            this.mIsDarkMode = uiModeManager != null && uiModeManager.getNightMode() == 2;
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    public KeyguardRestrictedPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowRightArrow = true;
        this.mShowBackground = false;
        this.mIconNeedPadding = false;
        this.mIsCardStyle = false;
        this.mShowTouchAnim = false;
        this.mSelected = false;
        this.mFontScale = 1.0f;
        this.mHandler = new Handler();
    }

    public KeyguardRestrictedPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mShowRightArrow = true;
        this.mShowBackground = false;
        this.mIconNeedPadding = false;
        this.mIsCardStyle = false;
        this.mShowTouchAnim = false;
        this.mSelected = false;
        this.mFontScale = 1.0f;
        this.mHandler = new Handler();
    }

    private void updateIcon(boolean z) {
        if ("unlock_set_pattern".equalsIgnoreCase(getKey())) {
            setIcon((this.mIsDarkMode || this.mSelected) ? R.drawable.ic_unlock_pattern_icon_selected : R.drawable.ic_unlock_pattern_icon);
            setSummary(z ? R.string.current_password : R.string.unlock_set_unlock_pattern_summary);
        } else if ("unlock_set_pin".equalsIgnoreCase(getKey())) {
            setIcon((this.mIsDarkMode || this.mSelected) ? R.drawable.ic_unlock_pin_icon_selected : R.drawable.ic_unlock_pin_icon);
            if (z) {
                setSummary(R.string.current_password);
            }
        } else if ("unlock_set_password".equalsIgnoreCase(getKey())) {
            setIcon((this.mIsDarkMode || this.mSelected) ? R.drawable.ic_unlock_password_icon_selected : R.drawable.ic_unlock_password_icon);
            setSummary(z ? R.string.current_password : R.string.unlock_set_unlock_password_summary);
        }
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        ImageView imageView;
        super.onBindViewHolder(preferenceViewHolder);
        final View view = preferenceViewHolder.itemView;
        if (this.mIsCardStyle) {
            this.mHandler.post(new Runnable() { // from class: com.android.settings.KeyguardRestrictedPreference.1
                @Override // java.lang.Runnable
                public void run() {
                    WeakReference weakReference = new WeakReference(view);
                    int dimensionPixelSize = (int) (KeyguardRestrictedPreference.this.mFontScale * KeyguardRestrictedPreference.this.mContext.getResources().getDimensionPixelSize(R.dimen.choose_unlock_item_height));
                    ViewGroup.LayoutParams layoutParams = ((View) weakReference.get()).getLayoutParams();
                    layoutParams.height = Math.max(dimensionPixelSize, ((View) weakReference.get()).getHeight());
                    ((View) weakReference.get()).setLayoutParams(layoutParams);
                }
            });
        }
        if (this.mShowTouchAnim) {
            Folme.useAt(view).touch().handleTouchOf(view, new AnimConfig[0]);
        }
        this.mValueView = (TextView) view.findViewById(R.id.value_right);
        if (this.mIsCardStyle) {
            TextView textView = (TextView) view.findViewById(16908310);
            TextView textView2 = (TextView) view.findViewById(16908304);
            textView.setTextColor(this.mContext.getColor((this.mIsDarkMode || this.mSelected) ? R.color.card_style_title_selected_color : R.color.card_style_title_normal_color));
            textView2.setTextColor(this.mContext.getColor((this.mIsDarkMode || this.mSelected) ? R.color.card_style_summary_selected_color : R.color.card_style_summary_normal_color));
        }
        if (this.mShowBackground) {
            if (this.mSelected) {
                view.setBackgroundResource(R.drawable.choose_unlock_item_background_selected);
            } else {
                view.setBackgroundResource(R.drawable.choose_unlock_item_background);
            }
        }
        setShowRightArrow(this.mShowRightArrow);
        if (!this.mIconNeedPadding || (imageView = (ImageView) view.findViewById(16908294)) == null) {
            return;
        }
        try {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.leftMargin = this.mContext.getResources().getDimensionPixelSize(R.dimen.choose_unlock_item_icon_padding);
            imageView.setLayoutParams(layoutParams);
        } catch (Exception unused) {
        }
    }

    public void setSelected(boolean z) {
        if (this.mSelected != z) {
            this.mSelected = z;
            updateIcon(z);
            notifyChanged();
        }
    }

    @Override // com.android.settingslib.RestrictedPreference
    public void setValue(CharSequence charSequence) {
        super.setValue(charSequence);
        TextView textView = this.mValueView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }
}
