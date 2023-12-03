package com.android.settings;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;

/* loaded from: classes.dex */
public class SetupFooterLayout extends LinearLayout {
    private TextView mBackButton;
    private ImageView mBackImg;
    private FrameLayout mBackLayout;
    private TextView mNextButton;
    private ImageView mNextImg;
    private FrameLayout mNextLayout;
    private TextView mSkipButton;

    public SetupFooterLayout(Context context) {
        super(context);
    }

    public SetupFooterLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SetupFooterLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public static void updateViewVisibility(TextView textView, ImageView imageView) {
        if (textView == null || imageView == null) {
            return;
        }
        boolean z = !TextUtils.equals(Locale.getDefault().getLanguage(), Locale.CHINESE.getLanguage());
        textView.setVisibility(z ? 8 : 0);
        imageView.setVisibility(z ? 0 : 8);
    }

    public TextView getBackButton() {
        return this.mBackButton;
    }

    public ImageView getBackImg() {
        return this.mBackImg;
    }

    public FrameLayout getBackLayout() {
        return this.mBackLayout;
    }

    public TextView getNextButton() {
        return this.mNextButton;
    }

    public ImageView getNextImg() {
        return this.mNextImg;
    }

    public FrameLayout getNextLayout() {
        return this.mNextLayout;
    }

    public TextView getSkipButton() {
        return this.mSkipButton;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mBackLayout = (FrameLayout) findViewById(R.id.lyt_btn_back);
        this.mBackButton = (TextView) findViewById(R.id.btn_back);
        ImageView imageView = (ImageView) findViewById(R.id.btn_back_global);
        this.mBackImg = imageView;
        updateViewVisibility(this.mBackButton, imageView);
        this.mSkipButton = (TextView) findViewById(R.id.btn_skip);
        this.mNextLayout = (FrameLayout) findViewById(R.id.lyt_btn_next);
        this.mNextButton = (TextView) findViewById(R.id.btn_next);
        ImageView imageView2 = (ImageView) findViewById(R.id.next_global);
        this.mNextImg = imageView2;
        updateViewVisibility(this.mNextButton, imageView2);
    }

    public void setBackLayoutClickable() {
        this.mBackButton.setFocusable(false);
        this.mBackButton.setClickable(false);
        this.mBackImg.setFocusable(false);
        this.mBackImg.setClickable(false);
        this.mBackLayout.setFocusable(true);
        this.mBackLayout.setClickable(true);
    }

    public void setNextLayoutClickable() {
        this.mNextButton.setFocusable(false);
        this.mNextButton.setClickable(false);
        this.mNextImg.setFocusable(false);
        this.mNextImg.setClickable(false);
        this.mNextLayout.setFocusable(true);
        this.mNextLayout.setClickable(true);
    }
}
