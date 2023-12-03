package com.android.settings;

import android.app.admin.PasswordMetrics;
import com.android.internal.widget.LockPatternUtils;

/* loaded from: classes.dex */
public class PasswordMetricsWrapper {
    private PasswordMetrics adminMetrics;
    private PasswordMetrics mMinimumMetrics;

    private byte[] charSequenceToByteArray(CharSequence charSequence) {
        if (charSequence == null) {
            return new byte[0];
        }
        byte[] bArr = new byte[charSequence.length()];
        for (int i = 0; i < charSequence.length(); i++) {
            bArr[i] = (byte) charSequence.charAt(i);
        }
        return bArr;
    }

    public int getMaxLengthSequence(String str) {
        return PasswordMetrics.maxLengthSequence(charSequenceToByteArray(str));
    }

    public int getMinLength() {
        return this.mMinimumMetrics.length;
    }

    public int getMinLetters() {
        return this.mMinimumMetrics.letters;
    }

    public int getMinLowerCase() {
        return this.mMinimumMetrics.lowerCase;
    }

    public int getMinNonLetter() {
        return this.mMinimumMetrics.nonLetter;
    }

    public int getMinNumeric() {
        return this.mMinimumMetrics.numeric;
    }

    public int getMinSymbols() {
        return this.mMinimumMetrics.symbols;
    }

    public int getMinUpperCase() {
        return this.mMinimumMetrics.upperCase;
    }

    public int getQuality() {
        return 0;
    }

    public boolean isPasswordLengthMatched(String str) {
        return PasswordMetrics.computeForPasswordOrPin(charSequenceToByteArray(str), false).numeric == charSequenceToByteArray(str).length;
    }

    public void updatePasswordMetrics(String str, int i, int i2, boolean z, boolean z2, LockPatternUtils lockPatternUtils, int i3, boolean z3) {
        PasswordMetrics requestedPasswordMetrics = lockPatternUtils.getRequestedPasswordMetrics(i3);
        this.adminMetrics = requestedPasswordMetrics;
        this.mMinimumMetrics = PasswordMetrics.applyComplexity(requestedPasswordMetrics, z3, i);
    }
}
