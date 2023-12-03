package com.android.settings.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.R$styleable;
import miuix.visual.check.BorderLayout;
import miuix.visual.check.VisualCheckBox;
import miuix.visual.check.VisualCheckGroup;
import miuix.visual.check.VisualCheckedTextView;

/* loaded from: classes2.dex */
public abstract class VisualCheckBoxPreference extends Preference implements VisualCheckGroup.OnCheckedChangeListener {
    private boolean mChecked;
    private VisualCheckBox mNegativeCheckBox;
    private int mNegativeContentRes;
    private String mNegativeDescText;
    private String mNegativeTitleText;
    private VisualCheckBox mPositiveCheckBox;
    private int mPositiveContentRes;
    private String mPositiveDescText;
    private String mPositiveTitleText;

    public VisualCheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.layout_visual_checkbox_preference);
        fromAttrs(context, attributeSet);
    }

    private void fromAttrs(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.VisualCheckBoxPreference);
        this.mPositiveTitleText = obtainStyledAttributes.getString(R$styleable.VisualCheckBoxPreference_positiveTitle);
        this.mPositiveDescText = obtainStyledAttributes.getString(R$styleable.VisualCheckBoxPreference_positiveDescription);
        this.mNegativeTitleText = obtainStyledAttributes.getString(R$styleable.VisualCheckBoxPreference_negativeTitle);
        this.mNegativeDescText = obtainStyledAttributes.getString(R$styleable.VisualCheckBoxPreference_negativeDescription);
        this.mPositiveContentRes = obtainStyledAttributes.getResourceId(R$styleable.VisualCheckBoxPreference_positiveContent, 0);
        this.mNegativeContentRes = obtainStyledAttributes.getResourceId(R$styleable.VisualCheckBoxPreference_negativeContent, 0);
        obtainStyledAttributes.recycle();
    }

    private void setChecked(boolean z, boolean z2) {
        boolean z3 = z != this.mChecked;
        this.mChecked = z;
        if (z) {
            VisualCheckBox visualCheckBox = this.mPositiveCheckBox;
            if (visualCheckBox != null) {
                visualCheckBox.setChecked(true);
            }
        } else {
            VisualCheckBox visualCheckBox2 = this.mNegativeCheckBox;
            if (visualCheckBox2 != null) {
                visualCheckBox2.setChecked(true);
            }
        }
        if (z3 && z2) {
            callChangeListener(Boolean.valueOf(this.mChecked));
        }
    }

    private void setupContent(View view) {
        BorderLayout borderLayout = (BorderLayout) view.requireViewById(R.id.border_positive);
        BorderLayout borderLayout2 = (BorderLayout) view.requireViewById(R.id.border_negative);
        LayoutInflater from = LayoutInflater.from(getContext());
        int i = this.mPositiveContentRes;
        View inflate = i != 0 ? from.inflate(i, borderLayout) : null;
        int i2 = this.mNegativeContentRes;
        onCreateVisualContent(inflate, i2 != 0 ? from.inflate(i2, borderLayout2) : null);
    }

    private void setupText(View view) {
        VisualCheckedTextView visualCheckedTextView = (VisualCheckedTextView) view.requireViewById(R.id.tv_positive_title);
        if (TextUtils.isEmpty(this.mPositiveTitleText)) {
            visualCheckedTextView.setVisibility(8);
        } else {
            visualCheckedTextView.setText(this.mPositiveTitleText);
        }
        VisualCheckedTextView visualCheckedTextView2 = (VisualCheckedTextView) view.requireViewById(R.id.tv_positive_desc);
        if (TextUtils.isEmpty(this.mPositiveDescText)) {
            visualCheckedTextView2.setVisibility(8);
        } else {
            visualCheckedTextView2.setText(this.mPositiveDescText);
        }
        VisualCheckedTextView visualCheckedTextView3 = (VisualCheckedTextView) view.requireViewById(R.id.tv_negative_title);
        if (TextUtils.isEmpty(this.mNegativeTitleText)) {
            visualCheckedTextView3.setVisibility(8);
        } else {
            visualCheckedTextView3.setText(this.mNegativeTitleText);
        }
        VisualCheckedTextView visualCheckedTextView4 = (VisualCheckedTextView) view.requireViewById(R.id.tv_negative_desc);
        if (TextUtils.isEmpty(this.mNegativeDescText)) {
            visualCheckedTextView4.setVisibility(8);
        } else {
            visualCheckedTextView4.setText(this.mNegativeDescText);
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        VisualCheckGroup visualCheckGroup = (VisualCheckGroup) preferenceViewHolder.itemView;
        visualCheckGroup.setEnabled(false);
        this.mNegativeCheckBox = (VisualCheckBox) visualCheckGroup.requireViewById(R.id.checkbox_negative);
        VisualCheckBox visualCheckBox = (VisualCheckBox) visualCheckGroup.requireViewById(R.id.checkbox_positive);
        this.mPositiveCheckBox = visualCheckBox;
        if (this.mChecked) {
            visualCheckBox.setChecked(true);
        } else {
            this.mNegativeCheckBox.setChecked(true);
        }
        visualCheckGroup.setOnCheckedChangeListener(this);
        setupText(visualCheckGroup);
        setupContent(visualCheckGroup);
    }

    @Override // miuix.visual.check.VisualCheckGroup.OnCheckedChangeListener
    public void onCheckedChanged(VisualCheckGroup visualCheckGroup, int i) {
        setChecked(i == R.id.checkbox_positive, true);
    }

    protected abstract void onCreateVisualContent(View view, View view2);

    public void setChecked(boolean z) {
        setChecked(z, false);
    }
}
