package com.android.settingslib.miuisettings.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.search.SearchUpdater;
import com.android.settingslib.R$styleable;
import miuix.preference.TextPreference;

/* loaded from: classes2.dex */
public class ValuePreference extends TextPreference {
    private boolean mShowRightArrow;
    private int mValueRes;

    public ValuePreference(Context context) {
        super(context);
        this.mShowRightArrow = false;
        init(context, null);
    }

    public ValuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowRightArrow = false;
        init(context, attributeSet);
    }

    public ValuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowRightArrow = false;
        init(context, attributeSet);
    }

    public ValuePreference(Context context, boolean z) {
        super(context);
        this.mShowRightArrow = false;
        this.mShowRightArrow = z;
        init(context, null);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ValuePreference);
            TypedValue peekValue = obtainStyledAttributes.peekValue(R$styleable.ValuePreference_showRightArrow);
            if (peekValue != null) {
                this.mShowRightArrow = peekValue.type == 18 && peekValue.data != 0;
            }
            obtainStyledAttributes.recycle();
        }
        if (this.mShowRightArrow && getIntent() == null && getFragment() == null && getOnPreferenceClickListener() == null) {
            setIntent(new Intent("com.android.settings.TEST_ARROW"));
        }
    }

    public CharSequence getValue() {
        return getText();
    }

    public int getValueRes() {
        return this.mValueRes;
    }

    public void onBindView(View view) {
    }

    @Override // miuix.preference.TextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        onBindView(preferenceViewHolder.itemView);
        if (this.mShowRightArrow && getIntent() == null && getFragment() == null && getOnPreferenceClickListener() == null) {
            setIntent(new Intent("com.android.settings.TEST_ARROW"));
        }
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (getIntent() != null && getIntent().resolveActivityInfo(getContext().getPackageManager(), SearchUpdater.GOOGLE) == null) {
            setIntent(null);
        }
        super.performClick();
    }

    public void setShowRightArrow(boolean z) {
        this.mShowRightArrow = z;
    }

    public void setValue(int i) {
        setText(i);
        this.mValueRes = i;
    }

    public void setValue(String str) {
        setText(str);
    }
}
