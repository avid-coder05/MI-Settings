package com.android.settings.utils;

import android.graphics.drawable.Drawable;
import com.android.settingslib.widget.CandidateInfo;

/* loaded from: classes2.dex */
public class CandidateInfoExtra extends CandidateInfo {
    private final String mKey;
    private final CharSequence mLabel;
    private final CharSequence mSummary;

    public CandidateInfoExtra(CharSequence charSequence, CharSequence charSequence2, String str, boolean z) {
        super(z);
        this.mLabel = charSequence;
        this.mSummary = charSequence2;
        this.mKey = str;
    }

    @Override // com.android.settingslib.widget.CandidateInfo
    public String getKey() {
        return this.mKey;
    }

    @Override // com.android.settingslib.widget.CandidateInfo
    public Drawable loadIcon() {
        return null;
    }

    @Override // com.android.settingslib.widget.CandidateInfo
    public CharSequence loadLabel() {
        return this.mLabel;
    }

    public CharSequence loadSummary() {
        return this.mSummary;
    }
}
