package com.android.utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/* loaded from: classes4.dex */
public class aboutWallpaperView extends ImageView {
    Context contextM;

    public aboutWallpaperView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.contextM = context;
    }

    public aboutWallpaperView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.contextM = context;
    }

    public aboutWallpaperView(Context context) {
        super(context);
        this.contextM = context;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        setImageDrawable(WallpaperManager.getInstance(this.contextM).getDrawable());
    }
}
