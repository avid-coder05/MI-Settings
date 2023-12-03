package com.android.settings.vpn2;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.R$styleable;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes2.dex */
public class VpnCheckBox extends LinearLayout {
    CheckListener mCheckListener;
    SlidingButton mSlidingButton;
    TextView mTextView;
    String mTitle;

    /* loaded from: classes2.dex */
    interface CheckListener {
        void check();
    }

    public VpnCheckBox(Context context) {
        this(context, null);
    }

    public VpnCheckBox(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VpnCheckBox(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public VpnCheckBox(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setWillNotDraw(false);
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.VpnCheckBox, 0, 0);
        try {
            this.mTitle = obtainStyledAttributes.getString(R$styleable.VpnCheckBox_checkbox_title);
            obtainStyledAttributes.recycle();
            initView(context);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        boolean dispatchTouchEvent = this.mSlidingButton.dispatchTouchEvent(motionEvent);
        CheckListener checkListener = this.mCheckListener;
        if (checkListener != null) {
            checkListener.check();
        }
        return dispatchTouchEvent;
    }

    public void initView(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.vpn_checkbox, (ViewGroup) this, true);
        this.mTextView = (TextView) findViewById(R.id.text_view);
        this.mSlidingButton = (SlidingButton) findViewById(R.id.sliding_button);
        this.mTextView.setText(this.mTitle);
        Folme.useAt(inflate).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setBackgroundColor(0.08f, 0.0f, 0.0f, 0.0f).handleTouchOf(inflate, new AnimConfig[0]);
    }

    public boolean isChecked() {
        return this.mSlidingButton.isChecked();
    }

    @Override // android.view.View
    public boolean performClick() {
        return this.mSlidingButton.performClick();
    }

    public void setCheckListener(CheckListener checkListener) {
        this.mCheckListener = checkListener;
    }

    public void setChecked(boolean z) {
        this.mSlidingButton.setChecked(z);
    }
}
