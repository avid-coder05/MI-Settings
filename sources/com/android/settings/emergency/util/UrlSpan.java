package com.android.settings.emergency.util;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/* loaded from: classes.dex */
public class UrlSpan extends ClickableSpan {
    private UrlSpanOnClickListener mOnClickListener;

    /* loaded from: classes.dex */
    public interface UrlSpanOnClickListener {
        void onClick();
    }

    public UrlSpan(UrlSpanOnClickListener urlSpanOnClickListener) {
        this.mOnClickListener = urlSpanOnClickListener;
    }

    @Override // android.text.style.ClickableSpan
    public void onClick(View view) {
        UrlSpanOnClickListener urlSpanOnClickListener = this.mOnClickListener;
        if (urlSpanOnClickListener != null) {
            urlSpanOnClickListener.onClick();
        }
    }

    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        textPaint.setUnderlineText(true);
        textPaint.setColor(Color.parseColor("#0d84ff"));
    }
}
