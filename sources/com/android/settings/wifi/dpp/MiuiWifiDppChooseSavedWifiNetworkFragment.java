package com.android.settings.wifi.dpp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class MiuiWifiDppChooseSavedWifiNetworkFragment extends MiuiWifiDppQrCodeBaseFragment {
    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$0(View view) {
        Intent intent = getActivity().getIntent();
        String action = intent != null ? intent.getAction() : null;
        if ("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_SCANNER".equals(action) || "android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_GENERATOR".equals(action)) {
            getFragmentManager().popBackStack();
        } else {
            getActivity().finish();
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1595;
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppQrCodeBaseFragment
    protected boolean isFooterAvailable() {
        return true;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        FragmentManager childFragmentManager = getChildFragmentManager();
        MiuiWifiNetworkListFragment miuiWifiNetworkListFragment = new MiuiWifiNetworkListFragment();
        Bundle arguments = getArguments();
        if (arguments != null) {
            miuiWifiNetworkListFragment.setArguments(arguments);
        }
        FragmentTransaction beginTransaction = childFragmentManager.beginTransaction();
        beginTransaction.replace(R.id.wifi_network_list_container, miuiWifiNetworkListFragment, "wifi_network_list_fragment");
        beginTransaction.commit();
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public final View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.miui_wifi_dpp_choose_saved_wifi_network_fragment, viewGroup, false);
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppQrCodeBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setHeaderTitle(R.string.wifi_dpp_choose_network, new Object[0]);
        this.mSummary.setText(R.string.wifi_dpp_choose_network_to_connect_device);
        this.mLeftButton.setText(getContext(), R.string.cancel);
        this.mLeftButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppChooseSavedWifiNetworkFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MiuiWifiDppChooseSavedWifiNetworkFragment.this.lambda$onViewCreated$0(view2);
            }
        });
        this.mRightButton.setVisibility(8);
    }
}
