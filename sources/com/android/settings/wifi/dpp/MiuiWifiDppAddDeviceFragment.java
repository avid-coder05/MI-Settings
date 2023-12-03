package com.android.settings.wifi.dpp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.EasyConnectStatusCallback;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import com.android.settings.R;
import com.google.android.setupcompat.template.FooterButton;
import com.iqiyi.android.qigsaw.core.splitreport.SplitInstallError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class MiuiWifiDppAddDeviceFragment extends MiuiWifiDppQrCodeBaseFragment {
    private Button mChooseDifferentNetwork;
    private OnClickChooseDifferentNetworkListener mClickChooseDifferentNetworkListener;
    private Handler mHandler;
    private WifiDppInitiatorViewModel mModel;
    private Button mShareWifi;
    private ImageView mWifiApPictureView;
    private WifiManager mWifiManager;
    private int mLatestStatusCode = 0;
    private int MSG_DPP_BASE = 768;
    private int MSG_CLICK_SHARE_WIFI_BUTTON = 768 + 1;
    private int MSG_RE_START_EASY_CONNECT = 768 + 2;
    private int MSG_FAIL_TO_SHARE_WIFI = 768 + 3;
    private boolean mIsShareWifi = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class EasyConnectConfiguratorStatusCallback extends EasyConnectStatusCallback {
        private EasyConnectConfiguratorStatusCallback() {
        }

        public void onConfiguratorSuccess(int i) {
            Log.d("MiuiWifiDppAddDeviceFragment", "success to share wifi");
            MiuiWifiDppAddDeviceFragment.this.mWifiManager = null;
            MiuiWifiDppAddDeviceFragment.this.mModel = null;
            MiuiWifiDppAddDeviceFragment.this.mIsShareWifi = true;
            if (MiuiWifiDppAddDeviceFragment.this.getActivity() != null) {
                MiuiWifiDppAddDeviceFragment.this.getActivity().finish();
            }
        }

        public void onEnrolleeSuccess(int i) {
        }

        public void onFailure(int i, String str, SparseArray<int[]> sparseArray, int[] iArr) {
            Log.d("MiuiWifiDppAddDeviceFragment", "EasyConnectConfiguratorStatusCallback.onFailure: " + i);
            if (!TextUtils.isEmpty(str)) {
                Log.d("MiuiWifiDppAddDeviceFragment", "Tried SSID: " + str);
            }
            if (sparseArray.size() != 0) {
                Log.d("MiuiWifiDppAddDeviceFragment", "Tried channels: " + sparseArray);
            }
            if (iArr != null && iArr.length > 0) {
                StringBuilder sb = new StringBuilder("Supported bands: ");
                for (int i2 : iArr) {
                    sb.append(i2 + " ");
                }
                Log.d("MiuiWifiDppAddDeviceFragment", sb.toString());
            }
            MiuiWifiDppAddDeviceFragment miuiWifiDppAddDeviceFragment = MiuiWifiDppAddDeviceFragment.this;
            miuiWifiDppAddDeviceFragment.showErrorUi(i, miuiWifiDppAddDeviceFragment.getResultIntent(i, str, sparseArray, iArr), false);
        }

        public void onProgress(int i) {
        }
    }

    /* loaded from: classes2.dex */
    public interface OnClickChooseDifferentNetworkListener {
        void onClickChooseDifferentNetwork();
    }

    private void DelayShareWifi() {
        new Thread(new Runnable() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment.1
            @Override // java.lang.Runnable
            public void run() {
                MiuiWifiDppAddDeviceFragment.this.mHandler.sendEmptyMessageDelayed(MiuiWifiDppAddDeviceFragment.this.MSG_CLICK_SHARE_WIFI_BUTTON, 1000L);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Intent getResultIntent(int i, String str, SparseArray<int[]> sparseArray, int[] iArr) {
        Intent intent = new Intent();
        intent.putExtra("android.provider.extra.EASY_CONNECT_ERROR_CODE", i);
        if (!TextUtils.isEmpty(str)) {
            intent.putExtra("android.provider.extra.EASY_CONNECT_ATTEMPTED_SSID", str);
        }
        if (sparseArray != null && sparseArray.size() != 0) {
            JSONObject jSONObject = new JSONObject();
            int i2 = 0;
            while (true) {
                try {
                    int keyAt = sparseArray.keyAt(i2);
                    JSONArray jSONArray = new JSONArray();
                    for (int i3 : sparseArray.get(keyAt)) {
                        jSONArray.put(i3);
                    }
                    try {
                        jSONObject.put(Integer.toString(keyAt), jSONArray);
                        i2++;
                    } catch (JSONException unused) {
                        jSONObject = new JSONObject();
                        intent.putExtra("android.provider.extra.EASY_CONNECT_CHANNEL_LIST", jSONObject.toString());
                        if (iArr != null) {
                            intent.putExtra("android.provider.extra.EASY_CONNECT_BAND_LIST", iArr);
                        }
                        return intent;
                    }
                } catch (ArrayIndexOutOfBoundsException unused2) {
                }
            }
        }
        if (iArr != null && iArr.length != 0) {
            intent.putExtra("android.provider.extra.EASY_CONNECT_BAND_LIST", iArr);
        }
        return intent;
    }

    private String getSsid() {
        WifiNetworkConfig wifiNetworkConfig = ((MiuiWifiDppConfiguratorActivity) getActivity()).getWifiNetworkConfig();
        if (WifiNetworkConfig.isValidConfig(wifiNetworkConfig)) {
            return wifiNetworkConfig.getSsid();
        }
        throw new IllegalStateException("Invalid Wi-Fi network for configuring");
    }

    private boolean hasRetryButton(int i) {
        return (i == -3 || i == -1) ? false : true;
    }

    private void initHandler() {
        this.mHandler = new Handler() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment.4
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == MiuiWifiDppAddDeviceFragment.this.MSG_CLICK_SHARE_WIFI_BUTTON) {
                    MiuiWifiDppAddDeviceFragment.this.startWifiDppConfiguratorInitiator();
                } else if (message.what == MiuiWifiDppAddDeviceFragment.this.MSG_RE_START_EASY_CONNECT) {
                    if (MiuiWifiDppAddDeviceFragment.this.mWifiManager == null || MiuiWifiDppAddDeviceFragment.this.mModel == null || MiuiWifiDppAddDeviceFragment.this.mIsShareWifi) {
                        return;
                    }
                    Log.d("MiuiWifiDppAddDeviceFragment", "restart Easy Connect");
                    MiuiWifiDppAddDeviceFragment.this.mWifiManager.stopEasyConnectSession();
                    MiuiWifiDppAddDeviceFragment.this.startWifiDppConfiguratorInitiator();
                } else if (message.what != MiuiWifiDppAddDeviceFragment.this.MSG_FAIL_TO_SHARE_WIFI || MiuiWifiDppAddDeviceFragment.this.mWifiManager == null || MiuiWifiDppAddDeviceFragment.this.mModel == null || MiuiWifiDppAddDeviceFragment.this.mIsShareWifi) {
                } else {
                    Log.d("MiuiWifiDppAddDeviceFragment", "fail to share wifi");
                    MiuiWifiDppAddDeviceFragment.this.mWifiManager.stopEasyConnectSession();
                    if (MiuiWifiDppAddDeviceFragment.this.getActivity() != null) {
                        MiuiWifiDppAddDeviceFragment.this.getActivity().finish();
                    }
                }
            }
        };
    }

    private boolean isEasyConnectHandshaking() {
        return this.mModel.isWifiDppHandshaking();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$3(Integer num) {
        if (this.mModel.isWifiDppHandshaking()) {
            return;
        }
        int intValue = num.intValue();
        if (intValue == 1) {
            new EasyConnectConfiguratorStatusCallback().onConfiguratorSuccess(intValue);
        } else {
            new EasyConnectConfiguratorStatusCallback().onFailure(intValue, this.mModel.getTriedSsid(), this.mModel.getTriedChannels(), this.mModel.getBandArray());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$4(View view) {
        this.mClickChooseDifferentNetworkListener.onClickChooseDifferentNetwork();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$5(View view) {
        setProgressBarShown(true);
        this.mShareWifi.setVisibility(4);
        updateShareSummary();
        DelayShareWifi();
        reStartEasyConnect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$6(View view) {
        getActivity().finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$7(View view) {
        setProgressBarShown(true);
        this.mRightButton.setVisibility(4);
        startWifiDppConfiguratorInitiator();
        updateSummary();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showErrorUi$2(Intent intent, View view) {
        getActivity().setResult(0, intent);
        getActivity().finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showSuccessUi$0(View view) {
        getFragmentManager().popBackStack();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showSuccessUi$1(View view) {
        FragmentActivity activity = getActivity();
        activity.setResult(-1);
        activity.finish();
    }

    private void reStartEasyConnect() {
        new Thread(new Runnable() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment.2
            @Override // java.lang.Runnable
            public void run() {
                MiuiWifiDppAddDeviceFragment.this.mHandler.sendEmptyMessageDelayed(MiuiWifiDppAddDeviceFragment.this.MSG_RE_START_EASY_CONNECT, 7000L);
                MiuiWifiDppAddDeviceFragment.this.mHandler.sendEmptyMessageDelayed(MiuiWifiDppAddDeviceFragment.this.MSG_RE_START_EASY_CONNECT, 10000L);
                MiuiWifiDppAddDeviceFragment.this.mHandler.sendEmptyMessageDelayed(MiuiWifiDppAddDeviceFragment.this.MSG_FAIL_TO_SHARE_WIFI, 13000L);
            }
        }) { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment.3
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showErrorUi(int i, final Intent intent, boolean z) {
        CharSequence text;
        Log.e("MiuiWifiDppAddDeviceFragment", "show error UI error code is " + i);
        if (getActivity() != null) {
            getActivity().finish();
        }
        switch (i) {
            case SplitInstallError.SIGNATURE_MISMATCH /* -12 */:
                text = getText(R.string.wifi_dpp_failure_enrollee_rejected_configuration);
                break;
            case -11:
                text = getText(R.string.wifi_dpp_failure_enrollee_authentication);
                break;
            case -10:
                text = getText(R.string.wifi_dpp_failure_cannot_find_network);
                break;
            case -9:
                throw new IllegalStateException("Wi-Fi DPP configurator used a non-PSK/non-SAEnetwork to handshake");
            case -8:
                text = getString(R.string.wifi_dpp_failure_not_supported, getSsid());
                break;
            case -7:
                text = getText(R.string.wifi_dpp_failure_generic);
                break;
            case -6:
                text = getText(R.string.wifi_dpp_failure_timeout);
                break;
            case -5:
                if (z) {
                    return;
                }
                if (i == this.mLatestStatusCode) {
                    throw new IllegalStateException("Tried restarting EasyConnectSession but stillreceiving EASY_CONNECT_EVENT_FAILURE_BUSY");
                }
                this.mLatestStatusCode = i;
                ((WifiManager) getContext().getSystemService(WifiManager.class)).stopEasyConnectSession();
                startWifiDppConfiguratorInitiator();
                return;
            case -4:
                text = getText(R.string.wifi_dpp_failure_authentication_or_configuration);
                break;
            case -3:
                text = getText(R.string.wifi_dpp_failure_not_compatible);
                break;
            case -2:
                text = getText(R.string.wifi_dpp_failure_authentication_or_configuration);
                break;
            case -1:
                text = getText(R.string.wifi_dpp_qr_code_is_not_valid_format);
                break;
            default:
                throw new IllegalStateException("Unexpected Wi-Fi DPP error");
        }
        setHeaderTitle(R.string.wifi_dpp_could_not_add_device, new Object[0]);
        this.mSummary.setText(text);
        this.mWifiApPictureView.setImageResource(R.drawable.wifi_dpp_error);
        this.mChooseDifferentNetwork.setVisibility(4);
        FooterButton footerButton = this.mLeftButton;
        if (hasRetryButton(i)) {
            this.mRightButton.setText(getContext(), R.string.retry);
        } else {
            this.mRightButton.setText(getContext(), R.string.done);
            footerButton = this.mRightButton;
            this.mLeftButton.setVisibility(4);
        }
        footerButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MiuiWifiDppAddDeviceFragment.this.lambda$showErrorUi$2(intent, view);
            }
        });
        if (isEasyConnectHandshaking()) {
            this.mSummary.setText(R.string.wifi_dpp_sharing_wifi_with_this_device);
        }
        setProgressBarShown(isEasyConnectHandshaking());
        this.mRightButton.setVisibility(isEasyConnectHandshaking() ? 4 : 0);
        if (z) {
            return;
        }
        this.mLatestStatusCode = i;
    }

    private void showSuccessUi(boolean z) {
        setHeaderIconImageResource(R.drawable.ic_devices_check_circle_green_32dp);
        setHeaderTitle(R.string.wifi_dpp_wifi_shared_with_device, new Object[0]);
        setProgressBarShown(isEasyConnectHandshaking());
        this.mSummary.setVisibility(4);
        this.mWifiApPictureView.setImageResource(R.drawable.wifi_dpp_success);
        this.mChooseDifferentNetwork.setVisibility(4);
        this.mLeftButton.setText(getContext(), R.string.wifi_dpp_add_another_device);
        this.mLeftButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MiuiWifiDppAddDeviceFragment.this.lambda$showSuccessUi$0(view);
            }
        });
        this.mRightButton.setText(getContext(), R.string.done);
        this.mRightButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MiuiWifiDppAddDeviceFragment.this.lambda$showSuccessUi$1(view);
            }
        });
        this.mRightButton.setVisibility(0);
        if (z) {
            return;
        }
        this.mLatestStatusCode = 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWifiDppConfiguratorInitiator() {
        if (((MiuiWifiDppConfiguratorActivity) getActivity()) == null) {
            return;
        }
        this.mModel.startEasyConnectAsConfiguratorInitiator(((MiuiWifiDppConfiguratorActivity) getActivity()).getWifiDppQrCode().getQrCode(), ((MiuiWifiDppConfiguratorActivity) getActivity()).getWifiNetworkConfig().getNetworkId());
    }

    private void updateShareSummary() {
        this.mSummary.setText(R.string.wifi_dpp_sharing_wifi_with_this_device);
    }

    private void updateSummary() {
        if (isEasyConnectHandshaking()) {
            this.mSummary.setText(R.string.wifi_dpp_sharing_wifi_with_this_device);
        } else {
            this.mSummary.setText(getString(R.string.wifi_dpp_add_device_to_wifi, getSsid()));
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

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mClickChooseDifferentNetworkListener = (OnClickChooseDifferentNetworkListener) context;
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWifiManager = (WifiManager) getActivity().getSystemService(WifiManager.class);
        if (bundle != null) {
            this.mLatestStatusCode = bundle.getInt("key_latest_status_code");
        }
        WifiDppInitiatorViewModel wifiDppInitiatorViewModel = new WifiDppInitiatorViewModel(getActivity().getApplication());
        this.mModel = wifiDppInitiatorViewModel;
        wifiDppInitiatorViewModel.getStatusCode().observe(this, new Observer() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment$$ExternalSyntheticLambda7
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                MiuiWifiDppAddDeviceFragment.this.lambda$onCreate$3((Integer) obj);
            }
        });
        initHandler();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager != null) {
            wifiManager.stopEasyConnectSession();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        this.mClickChooseDifferentNetworkListener = null;
        super.onDetach();
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public final View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.miui_wifi_dpp_add_device_fragment, viewGroup, false);
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("key_latest_status_code", this.mLatestStatusCode);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppQrCodeBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setHeaderIconImageResource(R.drawable.ic_devices_other_32dp);
        String information = ((MiuiWifiDppConfiguratorActivity) getActivity()).getWifiDppQrCode().getInformation();
        if (TextUtils.isEmpty(information)) {
            setHeaderTitle(R.string.wifi_dpp_device_found, new Object[0]);
        } else {
            setHeaderTitle(information);
        }
        updateSummary();
        this.mWifiApPictureView = (ImageView) view.findViewById(R.id.wifi_ap_picture_view);
        Button button = (Button) view.findViewById(R.id.choose_different_network);
        this.mChooseDifferentNetwork = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MiuiWifiDppAddDeviceFragment.this.lambda$onViewCreated$4(view2);
            }
        });
        this.mChooseDifferentNetwork.setVisibility(4);
        Button button2 = (Button) view.findViewById(R.id.share_wifi);
        this.mShareWifi = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MiuiWifiDppAddDeviceFragment.this.lambda$onViewCreated$5(view2);
            }
        });
        this.mLeftButton.setText(getContext(), R.string.cancel);
        this.mLeftButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MiuiWifiDppAddDeviceFragment.this.lambda$onViewCreated$6(view2);
            }
        });
        this.mRightButton.setText(getContext(), R.string.wifi_dpp_share_wifi);
        this.mRightButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MiuiWifiDppAddDeviceFragment.this.lambda$onViewCreated$7(view2);
            }
        });
        if (bundle != null) {
            int i = this.mLatestStatusCode;
            if (i == 1) {
                showSuccessUi(true);
            } else if (i != 0) {
                showErrorUi(i, null, true);
            } else {
                setProgressBarShown(isEasyConnectHandshaking());
                this.mRightButton.setVisibility(isEasyConnectHandshaking() ? 4 : 0);
            }
        }
    }
}
