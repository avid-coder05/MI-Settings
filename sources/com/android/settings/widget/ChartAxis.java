package com.android.settings.widget;

import android.content.res.Resources;
import android.text.SpannableStringBuilder;

/* loaded from: classes2.dex */
public interface ChartAxis {
    long buildLabel(Resources resources, SpannableStringBuilder spannableStringBuilder, long j);

    float convertToPoint(long j);

    long convertToValue(float f);

    float[] getTickPoints();

    boolean setSize(float f);
}
