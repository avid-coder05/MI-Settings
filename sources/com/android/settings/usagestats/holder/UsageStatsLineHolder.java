package com.android.settings.usagestats.holder;

import android.content.Context;
import android.view.View;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class UsageStatsLineHolder extends BaseHolder {
    public UsageStatsLineHolder(Context context) {
        super(context);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    protected View inflateView() {
        return View.inflate(this.mContext, R.layout.usagestats_line, null);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    public void renderView() {
    }
}
