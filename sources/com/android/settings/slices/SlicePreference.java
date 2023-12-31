package com.android.settings.slices;

import android.content.Context;
import android.util.AttributeSet;
import androidx.slice.Slice;
import androidx.slice.widget.SliceView;
import com.android.settings.R;
import com.android.settingslib.widget.LayoutPreference;

/* loaded from: classes2.dex */
public class SlicePreference extends LayoutPreference {
    private SliceView mSliceView;

    public SlicePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R.attr.slicePreferenceStyle);
        init();
    }

    public SlicePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        SliceView sliceView = (SliceView) findViewById(R.id.slice_view);
        this.mSliceView = sliceView;
        sliceView.setShowTitleItems(true);
        this.mSliceView.setScrollable(false);
        this.mSliceView.setVisibility(8);
    }

    public void onSliceUpdated(Slice slice) {
        if (slice == null) {
            this.mSliceView.setVisibility(8);
        } else {
            this.mSliceView.setVisibility(0);
        }
        this.mSliceView.onChanged(slice);
        notifyChanged();
    }
}
