package com.android.settings.accessibility.haptic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.android.settings.accessibility.ScreenReaderController;
import com.android.settings.accessibility.utils.MiuiAccessibilityUtils;

/* loaded from: classes.dex */
public class CustomLinearLayout extends LinearLayout {
    private SharedPreferences mSharedPrefs;

    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSharedPrefs = context.getSharedPreferences(ScreenReaderController.ACCESSIBILITY_SCREEN_READER_SP, 0);
    }

    public CustomLinearLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSharedPrefs = context.getSharedPreferences(ScreenReaderController.ACCESSIBILITY_SCREEN_READER_SP, 0);
    }

    public CustomLinearLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public boolean isScreenReaderCheckboxOpen() {
        return this.mSharedPrefs.getInt(ScreenReaderController.IS_ACCESSIBILITY_SCREEN_READER_OPEN, 0) == 1;
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        super.setAlpha((isScreenReaderCheckboxOpen() && MiuiAccessibilityUtils.isTallBackActive(getContext())) ? 1.0f : 0.3f);
    }
}
