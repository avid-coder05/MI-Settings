package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.R$styleable;

/* loaded from: classes2.dex */
public class SettingsStatusCard extends BaseSettingsCard {
    private int mCardBackground;
    private int mCardIcon;
    private ImageView mCardImageView;
    private String mCardTitle;
    private int mCardTitleColor;
    private CustomMarqueeTextView mCardTitleTextView;
    private String mCardValue;
    private int mCardValueColor;
    private CustomMarqueeTextView mCardValueTextView;
    private boolean mIsChecked;
    private boolean mIsDisable;
    private LinearLayout mRootLayout;

    public SettingsStatusCard(Context context) {
        super(context);
        initView();
    }

    public SettingsStatusCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initTypedArray(context, attributeSet);
        initView();
    }

    private void initTypedArray(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SettingsStatusCard);
        this.mCardTitle = obtainStyledAttributes.getString(R$styleable.SettingsStatusCard_cardTitle);
        this.mCardTitleColor = obtainStyledAttributes.getColor(R$styleable.SettingsStatusCard_cardTitleColor, this.mContext.getResources().getColor(R.color.card_view_title_color));
        this.mCardValue = obtainStyledAttributes.getString(R$styleable.SettingsStatusCard_cardValue);
        this.mCardValueColor = obtainStyledAttributes.getColor(R$styleable.SettingsStatusCard_cardValueColor, this.mContext.getResources().getColor(R.color.card_view_value_color));
        this.mCardIcon = obtainStyledAttributes.getResourceId(R$styleable.SettingsStatusCard_cardIcon, 0);
        this.mCardBackground = obtainStyledAttributes.getResourceId(R$styleable.SettingsStatusCard_cardBackground, R.drawable.card_shape_corner);
        obtainStyledAttributes.recycle();
    }

    private void initView() {
        addLayout(R.layout.settings_status_card);
        this.mRootLayout = (LinearLayout) findViewById(R.id.settings_card);
        this.mCardTitleTextView = (CustomMarqueeTextView) findViewById(R.id.card_title);
        this.mCardValueTextView = (CustomMarqueeTextView) findViewById(R.id.card_value);
        this.mCardImageView = (ImageView) findViewById(R.id.card_icon);
        this.mRootLayout.setBackground(this.mContext.getDrawable(this.mCardBackground));
        this.mCardTitleTextView.setText(this.mCardTitle);
        this.mCardTitleTextView.setTextColor(this.mCardTitleColor);
        this.mCardValueTextView.setText(this.mCardValue);
        this.mCardValueTextView.setTextColor(this.mCardValueColor);
        this.mCardImageView.setImageResource(this.mCardIcon);
    }

    public TextView getTitleTextView() {
        return this.mCardTitleTextView;
    }

    public TextView getValueTextView() {
        return this.mCardValueTextView;
    }

    public void setCardImageView(int i) {
        if (i > 0) {
            this.mCardImageView.setImageResource(i);
        }
    }

    public void setCardTitle(int i) {
        if (i > 0) {
            this.mCardTitleTextView.setText(this.mContext.getString(i));
        }
    }

    public void setCardTitle(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mCardTitleTextView.setText(str);
    }

    public void setCardValue(int i) {
        if (i > 0) {
            this.mCardValueTextView.setText(this.mContext.getString(i));
        }
    }

    public void setCardValue(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mCardValueTextView.setText(str);
    }

    public void setChecked(boolean z) {
        this.mIsChecked = z;
        if (z) {
            this.mRootLayout.setBackground(this.mContext.getDrawable(R.drawable.card_checked_corner));
            this.mCardTitleTextView.setTextColor(this.mContext.getResources().getColor(R.color.card_view_title_checked_color));
            this.mCardValueTextView.setTextColor(this.mContext.getResources().getColor(R.color.card_view_value_checked_color));
            return;
        }
        this.mRootLayout.setBackground(this.mContext.getDrawable(R.drawable.card_shape_corner));
        this.mCardTitleTextView.setTextColor(this.mContext.getResources().getColor(R.color.card_view_title_color));
        this.mCardValueTextView.setTextColor(this.mContext.getResources().getColor(R.color.card_view_value_color));
    }

    public void setDisable(boolean z) {
        this.mIsDisable = z;
        if (z) {
            this.mRootLayout.setAlpha(0.3f);
        } else {
            this.mRootLayout.setAlpha(1.0f);
        }
    }
}
