package com.android.settings.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import miuix.androidbasewidget.widget.ClearableEditText;

/* loaded from: classes2.dex */
public class CarrierClearableEditText extends ClearableEditText {
    public CarrierClearableEditText(Context context) {
        this(context, null);
    }

    public CarrierClearableEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CarrierClearableEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // miuix.androidbasewidget.widget.ClearableEditText, android.widget.TextView, android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        super.onRestoreInstanceState(null);
    }

    @Override // android.widget.TextView, android.view.View
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }
}
