package com.android.settings.notification;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class ZenRuleNamePreference extends Preference {
    private EditText mEditText;
    private String mRuleName;

    public ZenRuleNamePreference(Context context) {
        super(context);
        initLayoutResource();
    }

    public ZenRuleNamePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initLayoutResource();
    }

    public ZenRuleNamePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initLayoutResource();
    }

    public ZenRuleNamePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initLayoutResource();
    }

    private void initLayoutResource() {
        setLayoutResource(R.layout.zen_rule_name_layout);
    }

    public String getHint() {
        return this.mEditText.getHint().toString();
    }

    public String getText() {
        EditText editText = this.mEditText;
        if (editText != null) {
            return editText.getText().toString();
        }
        return null;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        EditText editText = (EditText) preferenceViewHolder.itemView.findViewById(R.id.zen_rule_name);
        this.mEditText = editText;
        if (editText != null) {
            editText.setText(this.mRuleName);
        }
    }

    public void setRuleName(String str) {
        this.mRuleName = str;
    }
}
