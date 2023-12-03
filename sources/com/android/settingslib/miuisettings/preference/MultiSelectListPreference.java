package com.android.settingslib.miuisettings.preference;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import androidx.preference.Preference;

/* loaded from: classes2.dex */
public class MultiSelectListPreference extends androidx.preference.MultiSelectListPreference {
    public MultiSelectListPreference(Context context) {
        super(context);
    }

    public MultiSelectListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MultiSelectListPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public MultiSelectListPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.MultiSelectListPreference, androidx.preference.Preference
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable == null) {
            super.onRestoreInstanceState(parcelable);
        } else {
            super.onRestoreInstanceState(((Preference.BaseSavedState) parcelable).getSuperState());
        }
    }
}
