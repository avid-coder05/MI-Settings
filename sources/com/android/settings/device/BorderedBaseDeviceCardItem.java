package com.android.settings.device;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.settings.R;

/* loaded from: classes.dex */
public class BorderedBaseDeviceCardItem extends BaseDeviceCardItem {
    protected RelativeLayout mBoardLayout;

    public BorderedBaseDeviceCardItem(Context context) {
        super(context);
    }

    public BorderedBaseDeviceCardItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BorderedBaseDeviceCardItem(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public RelativeLayout getmBoardLayout() {
        return this.mBoardLayout;
    }

    @Override // com.android.settings.device.BaseDeviceCardItem
    protected void init() {
        View.inflate(getContext(), R.layout.bordered_base_card_item, this);
        this.mBoardLayout = (RelativeLayout) findViewById(R.id.board_layout);
        this.mTitleView = (TextView) findViewById(R.id.card_title);
        this.mValueView = (TextView) findViewById(R.id.card_value);
    }
}
