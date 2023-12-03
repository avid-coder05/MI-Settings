package com.android.settings.widget;

import android.content.Context;
import android.text.TextUtils;

/* loaded from: classes2.dex */
public abstract class SummaryUpdater {
    protected final Context mContext;
    private final OnSummaryChangeListener mListener;
    private String mSummary;

    /* loaded from: classes2.dex */
    public interface OnSummaryChangeListener {
        void onSummaryChanged(String str);
    }

    public SummaryUpdater(Context context, OnSummaryChangeListener onSummaryChangeListener) {
        this.mContext = context;
        this.mListener = onSummaryChangeListener;
    }

    protected abstract String getSummary();

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyChangeIfNeeded() {
        String summary = getSummary();
        if (TextUtils.equals(this.mSummary, summary)) {
            return;
        }
        this.mSummary = summary;
        OnSummaryChangeListener onSummaryChangeListener = this.mListener;
        if (onSummaryChangeListener != null) {
            onSummaryChangeListener.onSummaryChanged(summary);
        }
    }
}
