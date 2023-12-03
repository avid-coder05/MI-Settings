package com.android.settings.wifi.dpp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.core.InstrumentedFragment;
import com.google.android.setupcompat.template.FooterButton;

/* loaded from: classes2.dex */
public abstract class MiuiWifiDppQrCodeBaseFragment extends InstrumentedFragment {
    protected FooterButton mLeftButton;
    protected FooterButton mRightButton;
    protected TextView mSummary;

    protected abstract boolean isFooterAvailable();

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mSummary = (TextView) view.findViewById(16908304);
        if (isFooterAvailable()) {
            this.mLeftButton = new FooterButton.Builder(getContext()).setButtonType(2).setTheme(R.style.SudGlifButton_Secondary).build();
            this.mRightButton = new FooterButton.Builder(getContext()).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHeaderIconImageResource(int i) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHeaderTitle(int i, Object... objArr) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setHeaderTitle(String str) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setProgressBarShown(boolean z) {
    }
}
