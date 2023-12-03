package com.android.settings.display;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.settings.MiuiValuePreference;

/* loaded from: classes.dex */
public class ScreenZoomPreference extends MiuiValuePreference {
    public ScreenZoomPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void updateZoomLevel(Context context) {
        int lastZoomLevel = ScreenZoomUtils.getLastZoomLevel(context);
        String[] entries = ScreenZoomUtils.getEntries(context);
        if (lastZoomLevel < 0 || entries == null || lastZoomLevel >= entries.length) {
            setEnabled(false);
        } else if (TextUtils.isEmpty(getSummary())) {
            setSummary(entries[lastZoomLevel]);
        }
    }
}
