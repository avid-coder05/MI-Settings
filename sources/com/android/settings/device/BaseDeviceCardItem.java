package com.android.settings.device;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;

/* loaded from: classes.dex */
public class BaseDeviceCardItem extends LinearLayout {
    protected ImageView mIconView;
    protected String mKey;
    protected TextView mTitleView;
    protected TextView mValueView;

    public BaseDeviceCardItem(Context context) {
        this(context, null);
    }

    public BaseDeviceCardItem(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BaseDeviceCardItem(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mKey = "";
        init();
    }

    public String getKey() {
        return this.mKey;
    }

    protected void init() {
        View.inflate(getContext(), R.layout.base_card_item, this);
        this.mIconView = (ImageView) findViewById(R.id.card_icon);
        this.mTitleView = (TextView) findViewById(R.id.card_title);
        this.mValueView = (TextView) findViewById(R.id.card_value);
    }

    public void setIcon(int i) {
        this.mIconView.setImageResource(i);
        if (i == 0) {
            this.mIconView.setVisibility(8);
        }
    }

    public void setKey(String str) {
        this.mKey = str;
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitleView.setText(charSequence);
    }

    public void setValue(String str) {
        this.mValueView.setText(str);
    }

    public void setValueMaxLine(int i) {
        this.mValueView.setMaxLines(i);
    }
}
