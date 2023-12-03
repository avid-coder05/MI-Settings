package com.android.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.password.ConfirmDeviceCredentialBaseFragment;
import com.android.settings.password.ConfirmDeviceCredentialUtils;

/* loaded from: classes.dex */
public abstract class BaseConfirmLockFragment extends ConfirmDeviceCredentialBaseFragment {
    public void checkForPendingIntentForCts() {
        ConfirmDeviceCredentialUtils.checkForPendingIntent(getAppCompatActivity());
    }

    protected abstract View createView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle);

    public int getMetricsPasswordCategory() {
        return 0;
    }

    public int getMetricsPatternCategory() {
        return 0;
    }

    public void onConfirmDeviceCredentialSuccess() {
    }

    @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getAppCompatActivity().getWindow().addFlags(Integer.MIN_VALUE);
        getAppCompatActivity().getWindow().setStatusBarColor(0);
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View createView = createView(layoutInflater, viewGroup, bundle);
        setDarkSystemUi(getAppCompatActivity(), createView);
        return createView;
    }

    public void setDarkSystemUi(Context context, View view) {
        if (MiuiKeyguardSettingsUtils.isDarkMode(context)) {
            return;
        }
        view.setSystemUiVisibility(view.getSystemUiVisibility() | 8192);
    }
}
