package com.android.settings.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.search.SearchUpdater;
import com.android.settingslib.R$id;
import com.android.settingslib.R$string;
import com.android.settingslib.R$styleable;
import miuix.preference.TextPreference;

/* loaded from: classes.dex */
public class BluetoothUpdateTextPreference extends TextPreference {
    private boolean isCustomColor;
    private String mBackgroundColor;
    private View mItemView;
    private boolean mShowRightArrow;
    private String mTextValueColor;

    public BluetoothUpdateTextPreference(Context context) {
        super(context);
        this.mShowRightArrow = false;
        this.isCustomColor = false;
        init(context, null);
    }

    public BluetoothUpdateTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowRightArrow = false;
        this.isCustomColor = false;
        init(context, attributeSet);
    }

    public BluetoothUpdateTextPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowRightArrow = false;
        this.isCustomColor = false;
        init(context, attributeSet);
    }

    public BluetoothUpdateTextPreference(Context context, boolean z) {
        super(context);
        this.mShowRightArrow = false;
        this.isCustomColor = false;
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

    public void onBindView(View view) {
        this.mItemView = view;
    }

    @Override // miuix.preference.TextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        onBindView(preferenceViewHolder.itemView);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(R$id.text_right);
        if (textView != null) {
            textView.setTextColor(Color.parseColor("#0D84FF"));
            textView.setText(getContext().getString(R$string.bluetooth_version_message));
            try {
                if (!TextUtils.isEmpty(this.mTextValueColor)) {
                    textView.setTextColor(Color.parseColor(this.mTextValueColor));
                }
                if (!TextUtils.isEmpty(this.mBackgroundColor)) {
                    textView.setBackgroundColor(Color.parseColor(this.mBackgroundColor));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            textView.setVisibility(0);
        }
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
}
