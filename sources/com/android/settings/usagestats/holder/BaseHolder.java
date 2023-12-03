package com.android.settings.usagestats.holder;

import android.content.Context;
import android.view.View;

/* loaded from: classes2.dex */
public abstract class BaseHolder {
    protected View mContentView = inflateView();
    protected Context mContext;

    public BaseHolder(Context context) {
        this.mContext = context;
    }

    public View getmContentView() {
        return this.mContentView;
    }

    protected abstract View inflateView();

    public abstract void renderView();
}
