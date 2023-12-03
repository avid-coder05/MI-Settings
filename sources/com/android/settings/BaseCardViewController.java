package com.android.settings;

import android.content.Context;
import android.view.View;
import com.android.settingslib.core.lifecycle.events.OnResume;

/* loaded from: classes.dex */
public abstract class BaseCardViewController implements OnResume, View.OnClickListener {
    protected CardInfo mCard;
    protected Context mContext;

    public BaseCardViewController(Context context, CardInfo cardInfo) {
        this.mContext = context;
        this.mCard = cardInfo;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
    }
}
