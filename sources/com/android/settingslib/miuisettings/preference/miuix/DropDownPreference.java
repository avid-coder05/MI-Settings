package com.android.settingslib.miuisettings.preference.miuix;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

/* loaded from: classes2.dex */
public class DropDownPreference extends miuix.preference.DropDownPreference {
    private CharSequence[] mEntries;

    public DropDownPreference(Context context) {
        super(context);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public DropDownPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public CharSequence getEntry() {
        CharSequence[] charSequenceArr;
        int valueIndex = getValueIndex();
        if (valueIndex < 0 || (charSequenceArr = this.mEntries) == null) {
            return null;
        }
        return charSequenceArr[valueIndex];
    }

    @Override // androidx.preference.Preference
    public CharSequence getSummary() {
        int i;
        CharSequence[] entries = super.getEntries();
        try {
            i = getValueIndex();
        } catch (Exception e) {
            Log.e("DropDownPreference", "getValueIndex error: " + e.getMessage());
            e.printStackTrace();
            i = 0;
        }
        String str = null;
        CharSequence charSequence = (entries == null || i >= entries.length || i < 0) ? null : entries[i];
        CharSequence summary = super.getSummary();
        if (summary != null) {
            String charSequence2 = summary.toString();
            Object[] objArr = new Object[1];
            if (charSequence == null) {
                charSequence = "";
            }
            objArr[0] = charSequence;
            str = String.format(charSequence2, objArr);
        }
        return TextUtils.equals(str, summary) ? summary : str;
    }

    @Override // miuix.preference.DropDownPreference
    public void setEntries(CharSequence[] charSequenceArr) {
        this.mEntries = charSequenceArr;
        super.setEntries(charSequenceArr);
    }
}
