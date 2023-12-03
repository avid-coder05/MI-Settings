package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class BaseSettingsCard extends LinearLayout {
    protected Context mContext;
    protected ViewGroup mRootView;

    public BaseSettingsCard(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public BaseSettingsCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(this.mContext).inflate(R.layout.base_settings_card, (ViewGroup) this, true);
        this.mRootView = (ViewGroup) findViewById(R.id.base_settings_card_view);
    }

    public void addLayout(int i) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -2);
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this.mContext).inflate(i, (ViewGroup) null);
        viewGroup.setLayoutParams(layoutParams);
        this.mRootView.addView(viewGroup);
    }
}
