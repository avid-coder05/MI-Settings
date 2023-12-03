package com.android.settings.wifi.dpp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.EasyConnectStatusCallback;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import com.android.settings.R;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.wifi.dpp.WifiNetworkConfig;
import com.android.settings.wifi.qrcode.MiuiQrCamera;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.android.settingslib.util.ToastUtil;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.SlaveWifiUtils;
import com.android.settingslib.wifi.WifiTracker;
import com.android.settingslib.wifi.WifiTrackerFactory;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Pattern;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
public class MiuiWifiDppQrCodeScannerFragment extends MiuiWifiDppQrCodeBaseFragment implements MiuiQrCamera.ScannerCallback, SurfaceHolder.Callback, WifiManager.ActionListener, WifiTracker.WifiListener {
    private MiuiQrCamera mCamera;
    private MiuiQrDecorateView mDecorateView;
    private ImageView mDppQrCodeItem;
    private TextView mDppSummary;
    private WifiConfiguration mEnrolleeWifiConfiguration;
    private TextView mErrorMessage;
    private WeakReference<MiuiWifiDppQrCodeScannerFragment> mFragmentRef;
    private boolean mHasSurface;
    private boolean mIsSlave;
    private String mMaxSimilarySsid;
    private WifiDppInitiatorViewModel mModel;
    private String mOcrPossibleWifiPwd;
    private boolean mPaused;
    private OnScanWifiDppSuccessListener mScanWifiDppSuccessListener;
    private String mSsid;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private WifiManager mWifiManager;
    private WifiQrCode mWifiQrCode;
    private WeakReference<Activity> mWifiSettingsActivityRef;
    private WifiTracker mWifiTracker;
    private static final Pattern OCR_PWD_PATTERN = Pattern.compile("[一-龥]|[(]|[)]");
    private static String TRACK_SCAN_WIFI_DPP = "scan_wifi_dpp";
    private static String TRACK_SCAN_ZXING_WIFI_FORMAT = "scan_zxing_wifi_format";
    private static String TRACK_SCAN_OCR_WIFI_FORMAT = "scan_ocr_wifi_format";
    private int mFailureRetryTimes = 3;
    private int mLatestStatusCode = 0;
    private final Handler mHandler = new Handler() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppQrCodeScannerFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                MiuiWifiDppQrCodeScannerFragment.this.mErrorMessage.setVisibility(4);
            } else if (i == 2) {
                String str = (String) message.obj;
                MiuiWifiDppQrCodeScannerFragment.this.mErrorMessage.setVisibility(0);
                MiuiWifiDppQrCodeScannerFragment.this.mErrorMessage.setText(str);
                MiuiWifiDppQrCodeScannerFragment.this.mErrorMessage.sendAccessibilityEvent(32);
                removeMessages(1);
                sendEmptyMessageDelayed(1, 10000L);
                if (message.arg1 == 1) {
                    MiuiWifiDppQrCodeScannerFragment.this.setProgressBarShown(false);
                    MiuiWifiDppQrCodeScannerFragment.this.mDecorateView.setFocused(false);
                    MiuiWifiDppQrCodeScannerFragment.this.restartCamera();
                }
            } else if (i == 3) {
                MiStatInterfaceUtils.trackEvent(MiuiWifiDppQrCodeScannerFragment.TRACK_SCAN_WIFI_DPP);
                OneTrackInterfaceUtils.track(MiuiWifiDppQrCodeScannerFragment.TRACK_SCAN_WIFI_DPP, null);
                if (MiuiWifiDppQrCodeScannerFragment.this.mScanWifiDppSuccessListener == null) {
                    return;
                }
                MiuiWifiDppQrCodeScannerFragment.this.mScanWifiDppSuccessListener.onScanWifiDppSuccess((WifiQrCode) message.obj);
                if (!MiuiWifiDppQrCodeScannerFragment.this.mIsConfiguratorMode) {
                    MiuiWifiDppQrCodeScannerFragment.this.setProgressBarShown(true);
                    MiuiWifiDppQrCodeScannerFragment.this.startWifiDppEnrolleeInitiator((WifiQrCode) message.obj);
                    MiuiWifiDppQrCodeScannerFragment.this.updateEnrolleeSummary();
                    MiuiWifiDppQrCodeScannerFragment.this.mSummary.sendAccessibilityEvent(32);
                }
                MiuiWifiDppQrCodeScannerFragment.this.notifyUserForQrCodeRecognition();
            } else if (i != 4) {
                if (i != 5) {
                    return;
                }
                MiStatInterfaceUtils.trackEvent(MiuiWifiDppQrCodeScannerFragment.TRACK_SCAN_OCR_WIFI_FORMAT);
                OneTrackInterfaceUtils.track(MiuiWifiDppQrCodeScannerFragment.TRACK_SCAN_OCR_WIFI_FORMAT, null);
                Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                intent.putExtra("ssid", MiuiWifiDppQrCodeScannerFragment.this.mMaxSimilarySsid);
                intent.putExtra("key_ocr_wifi_token", MiuiWifiDppQrCodeScannerFragment.this.mOcrPossibleWifiPwd);
                intent.setFlags(268468224);
                MiuiWifiDppQrCodeScannerFragment.this.startActivity(intent);
            } else {
                MiStatInterfaceUtils.trackEvent(MiuiWifiDppQrCodeScannerFragment.TRACK_SCAN_ZXING_WIFI_FORMAT);
                OneTrackInterfaceUtils.track(MiuiWifiDppQrCodeScannerFragment.TRACK_SCAN_ZXING_WIFI_FORMAT, null);
                WifiManager wifiManager = (WifiManager) MiuiWifiDppQrCodeScannerFragment.this.getContext().getSystemService(WifiManager.class);
                List<WifiConfiguration> wifiConfigurations = ((WifiNetworkConfig) message.obj).getWifiConfigurations(MiuiWifiDppQrCodeScannerFragment.this.getContext());
                SlaveWifiUtils slaveWifiUtils = SlaveWifiUtils.getInstance(MiuiWifiDppQrCodeScannerFragment.this.getContext());
                NetworkInfo networkInfo = ((ConnectivityManager) MiuiWifiDppQrCodeScannerFragment.this.getContext().getSystemService(ConnectivityManager.class)).getNetworkInfo(slaveWifiUtils.getSlaveWifiCurrentNetwork());
                boolean z = false;
                for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
                    int addNetwork = wifiManager.addNetwork(wifiConfiguration);
                    if (addNetwork != -1) {
                        wifiManager.enableNetwork(addNetwork, false);
                        if (wifiConfiguration.hiddenSSID || MiuiWifiDppQrCodeScannerFragment.this.isReachableWifiNetwork(wifiConfiguration)) {
                            MiuiWifiDppQrCodeScannerFragment.this.mEnrolleeWifiConfiguration = wifiConfiguration;
                            if (MiuiWifiDppQrCodeScannerFragment.this.mIsSlave) {
                                MiuiWifiDppQrCodeScannerFragment.this.onSuccess();
                            } else {
                                if (networkInfo != null && networkInfo.isConnected()) {
                                    slaveWifiUtils.disconnectSlaveWifi();
                                }
                                wifiManager.connect(addNetwork, MiuiWifiDppQrCodeScannerFragment.this);
                            }
                            z = true;
                        }
                    }
                }
                if (!z) {
                    MiuiWifiDppQrCodeScannerFragment.this.showErrorMessageAndRestartCamera(R.string.wifi_dpp_check_connection_try_again);
                    return;
                }
                ((InstrumentedFragment) MiuiWifiDppQrCodeScannerFragment.this).mMetricsFeatureProvider.action(((InstrumentedFragment) MiuiWifiDppQrCodeScannerFragment.this).mMetricsFeatureProvider.getAttribution(MiuiWifiDppQrCodeScannerFragment.this.getActivity()), 1711, 1596, null, Integer.MIN_VALUE);
                MiuiWifiDppQrCodeScannerFragment.this.notifyUserForQrCodeRecognition();
            }
        }
    };
    private boolean mIsConfiguratorMode = true;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class EasyConnectEnrolleeStatusCallback extends EasyConnectStatusCallback {
        private EasyConnectEnrolleeStatusCallback() {
        }

        public void onConfiguratorSuccess(int i) {
        }

        public void onEnrolleeSuccess(int i) {
            WifiManager wifiManager = (WifiManager) MiuiWifiDppQrCodeScannerFragment.this.getContext().getSystemService(WifiManager.class);
            for (WifiConfiguration wifiConfiguration : wifiManager.getPrivilegedConfiguredNetworks()) {
                if (wifiConfiguration.networkId == i) {
                    MiuiWifiDppQrCodeScannerFragment.this.mLatestStatusCode = 1;
                    MiuiWifiDppQrCodeScannerFragment.this.mEnrolleeWifiConfiguration = wifiConfiguration;
                    wifiManager.connect(wifiConfiguration, MiuiWifiDppQrCodeScannerFragment.this);
                    return;
                }
            }
            Log.e("MiuiWifiDppQrCodeScanner", "Invalid networkId " + i);
            MiuiWifiDppQrCodeScannerFragment.this.mLatestStatusCode = -7;
            MiuiWifiDppQrCodeScannerFragment.this.updateEnrolleeSummary();
            MiuiWifiDppQrCodeScannerFragment.this.showErrorMessageAndRestartCamera(R.string.wifi_dpp_check_connection_try_again);
        }

        public void onFailure(int i) {
            int i2;
            Log.d("MiuiWifiDppQrCodeScanner", "EasyConnectEnrolleeStatusCallback.onFailure " + i);
            switch (i) {
                case -9:
                    throw new IllegalStateException("EASY_CONNECT_EVENT_FAILURE_INVALID_NETWORK should be a configurator only error");
                case -8:
                    throw new IllegalStateException("EASY_CONNECT_EVENT_FAILURE_NOT_SUPPORTED should be a configurator only error");
                case -7:
                    i2 = R.string.wifi_dpp_failure_generic;
                    break;
                case -6:
                    i2 = R.string.wifi_dpp_failure_timeout;
                    break;
                case -5:
                    if (i == MiuiWifiDppQrCodeScannerFragment.this.mLatestStatusCode) {
                        throw new IllegalStateException("stopEasyConnectSession and try again forEASY_CONNECT_EVENT_FAILURE_BUSY but still failed");
                    }
                    MiuiWifiDppQrCodeScannerFragment.this.mLatestStatusCode = i;
                    ((WifiManager) MiuiWifiDppQrCodeScannerFragment.this.getContext().getSystemService(WifiManager.class)).stopEasyConnectSession();
                    MiuiWifiDppQrCodeScannerFragment miuiWifiDppQrCodeScannerFragment = MiuiWifiDppQrCodeScannerFragment.this;
                    miuiWifiDppQrCodeScannerFragment.startWifiDppEnrolleeInitiator(miuiWifiDppQrCodeScannerFragment.mWifiQrCode);
                    return;
                case -4:
                    i2 = R.string.wifi_dpp_failure_authentication_or_configuration;
                    break;
                case -3:
                    i2 = R.string.wifi_dpp_failure_not_compatible;
                    break;
                case -2:
                    i2 = R.string.wifi_dpp_failure_authentication_or_configuration;
                    break;
                case -1:
                    i2 = R.string.wifi_dpp_qr_code_is_not_valid_format;
                    break;
                default:
                    throw new IllegalStateException("Unexpected Wi-Fi DPP error");
            }
            MiuiWifiDppQrCodeScannerFragment.this.mLatestStatusCode = i;
            MiuiWifiDppQrCodeScannerFragment.this.updateEnrolleeSummary();
            MiuiWifiDppQrCodeScannerFragment.this.showErrorMessageAndRestartCamera(i2);
        }

        public void onProgress(int i) {
        }
    }

    /* loaded from: classes2.dex */
    public interface OnScanWifiDppSuccessListener {
        void onScanWifiDppSuccess(WifiQrCode wifiQrCode);
    }

    public MiuiWifiDppQrCodeScannerFragment() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MiuiWifiDppQrCodeScannerFragment(String str, boolean z) {
        this.mSsid = str;
        this.mIsSlave = z;
    }

    private void destroyCamera() {
        MiuiQrCamera miuiQrCamera = this.mCamera;
        if (miuiQrCamera != null) {
            miuiQrCamera.stop();
            this.mCamera = null;
        }
    }

    private void handleOCRWifiFormat(String str) {
        Message obtainMessage = this.mHandler.obtainMessage(5);
        obtainMessage.obj = str;
        this.mHandler.sendMessageDelayed(obtainMessage, 1000L);
    }

    private void handleWifiDpp() {
        Message obtainMessage = this.mHandler.obtainMessage(3);
        obtainMessage.obj = new WifiQrCode(this.mWifiQrCode.getQrCode());
        this.mHandler.sendMessageDelayed(obtainMessage, 1000L);
    }

    private void handleZxingWifiFormat() {
        Message obtainMessage = this.mHandler.obtainMessage(4);
        obtainMessage.obj = new WifiQrCode(this.mWifiQrCode.getQrCode()).getWifiNetworkConfig();
        this.mHandler.sendMessageDelayed(obtainMessage, 1000L);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (this.mCamera == null) {
            this.mCamera = new MiuiQrCamera(getContext(), this);
            if (!isWifiDppHandshaking()) {
                this.mCamera.start(surfaceHolder);
                return;
            }
            MiuiQrDecorateView miuiQrDecorateView = this.mDecorateView;
            if (miuiQrDecorateView != null) {
                miuiQrDecorateView.setFocused(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isReachableWifiNetwork(WifiConfiguration wifiConfiguration) {
        List<AccessPoint> accessPoints = this.mWifiTracker.getAccessPoints();
        if (wifiConfiguration.hiddenSSID) {
            return true;
        }
        for (AccessPoint accessPoint : accessPoints) {
            if (accessPoint.matches(wifiConfiguration) && accessPoint.isReachable()) {
                return true;
            }
        }
        return false;
    }

    private boolean isWifiDppHandshaking() {
        return this.mModel.isWifiDppHandshaking();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(Integer num) {
        if (this.mModel.isWifiDppHandshaking()) {
            return;
        }
        new EasyConnectEnrolleeStatusCallback().onEnrolleeSuccess(num.intValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(Integer num) {
        if (this.mModel.isWifiDppHandshaking()) {
            return;
        }
        int intValue = num.intValue();
        Log.d("MiuiWifiDppQrCodeScanner", "Easy connect enrollee callback onFailure " + intValue);
        new EasyConnectEnrolleeStatusCallback().onFailure(intValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$restartCamera$2() {
        this.mCamera.start(this.mSurfaceHolder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUserForQrCodeRecognition() {
        MiuiQrCamera miuiQrCamera = this.mCamera;
        if (miuiQrCamera != null) {
            miuiQrCamera.stop();
        }
        this.mDecorateView.setFocused(true);
        this.mErrorMessage.setVisibility(4);
        WifiDppUtils.triggerVibrationForQrCodeRecognition(getContext());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartCamera() {
        MiuiQrCamera miuiQrCamera = this.mCamera;
        if (miuiQrCamera == null) {
            Log.d("MiuiWifiDppQrCodeScanner", "mCamera is not available for restarting camera");
            return;
        }
        if (miuiQrCamera.isDecodeTaskAlive()) {
            this.mCamera.stop();
        }
        this.mHandler.removeMessages(10);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppQrCodeScannerFragment$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MiuiWifiDppQrCodeScannerFragment.this.lambda$restartCamera$2();
            }
        }, 10, 30L);
    }

    private void showErrorMessage(int i) {
        this.mHandler.obtainMessage(2, getString(i)).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showErrorMessageAndRestartCamera(int i) {
        Message obtainMessage = this.mHandler.obtainMessage(2, getString(i));
        obtainMessage.arg1 = 1;
        obtainMessage.sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWifiDppEnrolleeInitiator(WifiQrCode wifiQrCode) {
        this.mModel.startEasyConnectAsEnrolleeInitiator(wifiQrCode.getQrCode());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEnrolleeSummary() {
        if (isWifiDppHandshaking()) {
            this.mSummary.setText(R.string.wifi_dpp_connecting);
        } else {
            this.mSummary.setText(TextUtils.isEmpty(this.mSsid) ? getString(R.string.dpp_scanner_summary) : getString(R.string.wifi_dpp_scan_qr_code_join_network, this.mSsid));
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return this.mIsConfiguratorMode ? 1595 : 1596;
    }

    @Override // com.android.settings.wifi.qrcode.MiuiQrCamera.ScannerCallback
    public Size getViewSize() {
        return new Size(this.mSurfaceView.getWidth(), this.mSurfaceView.getHeight());
    }

    @Override // com.android.settings.wifi.qrcode.MiuiQrCamera.ScannerCallback
    public void handleCameraFailure() {
        Log.w("MiuiWifiDppQrCodeScanner", "Camera open failure " + this.mFailureRetryTimes);
        int i = this.mFailureRetryTimes;
        this.mFailureRetryTimes = i + (-1);
        if (i <= 0) {
            destroyCamera();
        } else if (this.mPaused) {
        } else {
            Log.d("MiuiWifiDppQrCodeScanner", "Try to restart camera!");
            restartCamera();
        }
    }

    @Override // com.android.settings.wifi.qrcode.MiuiQrCamera.ScannerCallback
    public void handleOcrSuccessfulResult(String str) {
        if (str != null) {
            handleOCRWifiFormat(str);
        }
    }

    @Override // com.android.settings.wifi.qrcode.MiuiQrCamera.ScannerCallback
    public void handleSuccessfulResult(String str) {
        String scheme = this.mWifiQrCode.getScheme();
        scheme.hashCode();
        if (scheme.equals("DPP")) {
            handleWifiDpp();
        } else if (scheme.equals("WIFI")) {
            handleZxingWifiFormat();
        }
    }

    protected boolean isDecodeTaskAlive() {
        MiuiQrCamera miuiQrCamera = this.mCamera;
        return miuiQrCamera != null && miuiQrCamera.isDecodeTaskAlive();
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppQrCodeBaseFragment
    protected boolean isFooterAvailable() {
        return false;
    }

    @Override // com.android.settings.wifi.qrcode.MiuiQrCamera.ScannerCallback
    public boolean isValidQrcode(String str) {
        try {
            WifiQrCode wifiQrCode = new WifiQrCode(str);
            this.mWifiQrCode = wifiQrCode;
            String scheme = wifiQrCode.getScheme();
            if (this.mIsConfiguratorMode || !"DPP".equals(scheme)) {
                return true;
            }
            showErrorMessage(R.string.wifi_dpp_qr_code_is_not_valid_format);
            return false;
        } catch (IllegalArgumentException unused) {
            showErrorMessage(R.string.wifi_dpp_qr_code_is_not_valid_format);
            return false;
        }
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onAccessPointsChanged() {
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mFragmentRef = new WeakReference<>(this);
        WeakReference<Activity> weakReference = new WeakReference<>(getActivity());
        this.mWifiSettingsActivityRef = weakReference;
        this.mWifiTracker = WifiTrackerFactory.create(weakReference.get(), this.mFragmentRef.get(), getSettingsLifecycle(), false, true);
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mScanWifiDppSuccessListener = (OnScanWifiDppSuccessListener) context;
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onConnectedChanged() {
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getActivity() != null && !SettingsFeatures.isSplitTablet(getContext())) {
            getActivity().setRequestedOrientation(1);
            if (getActivity().isInMultiWindowMode()) {
                ToastUtil.show(getActivity(), R.string.wlan_scanner_unresizeble, 0);
                getActivity().finish();
            }
        }
        if (bundle != null) {
            this.mIsConfiguratorMode = bundle.getBoolean("key_is_configurator_mode");
            this.mLatestStatusCode = bundle.getInt("key_latest_error_code");
            this.mEnrolleeWifiConfiguration = (WifiConfiguration) bundle.getParcelable("key_wifi_configuration");
        }
        this.mWifiManager = (WifiManager) getContext().getSystemService("wifi");
        WifiDppInitiatorViewModel wifiDppInitiatorViewModel = new WifiDppInitiatorViewModel(getActivity().getApplication());
        this.mModel = wifiDppInitiatorViewModel;
        wifiDppInitiatorViewModel.getEnrolleeSuccessNetworkId().observe(this, new Observer() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppQrCodeScannerFragment$$ExternalSyntheticLambda0
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                MiuiWifiDppQrCodeScannerFragment.this.lambda$onCreate$0((Integer) obj);
            }
        });
        this.mModel.getStatusCode().observe(this, new Observer() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppQrCodeScannerFragment$$ExternalSyntheticLambda1
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                MiuiWifiDppQrCodeScannerFragment.this.lambda$onCreate$1((Integer) obj);
            }
        });
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.removeItem(1);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        MiuiQrCamera miuiQrCamera = this.mCamera;
        if (miuiQrCamera != null) {
            miuiQrCamera.releaseCamera();
        }
        SurfaceView surfaceView = this.mSurfaceView;
        if (surfaceView != null) {
            surfaceView.surfaceDestroyed();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        this.mScanWifiDppSuccessListener = null;
        super.onDetach();
    }

    public void onFailure(int i) {
        Log.d("MiuiWifiDppQrCodeScanner", "Wi-Fi connect onFailure reason - " + i);
        showErrorMessageAndRestartCamera(R.string.wifi_dpp_check_connection_try_again);
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.miui_wifi_dpp_qrcode_scanner_fragment, viewGroup, false);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPause() {
        this.mPaused = true;
        this.mSurfaceHolder.removeCallback(this);
        MiuiQrCamera miuiQrCamera = this.mCamera;
        if (miuiQrCamera != null) {
            miuiQrCamera.onPause(this.mPaused);
            this.mCamera.stop();
        }
        super.onPause();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mFailureRetryTimes = 3;
        this.mPaused = false;
        MiuiQrCamera miuiQrCamera = this.mCamera;
        if (miuiQrCamera != null) {
            miuiQrCamera.onPause(false);
        }
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(3);
        if (isWifiDppHandshaking() || !this.mHasSurface) {
            return;
        }
        restartCamera();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("key_is_configurator_mode", this.mIsConfiguratorMode);
        bundle.putInt("key_latest_error_code", this.mLatestStatusCode);
        bundle.putParcelable("key_wifi_configuration", this.mEnrolleeWifiConfiguration);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        MiuiQrCamera miuiQrCamera = this.mCamera;
        if (miuiQrCamera != null) {
            miuiQrCamera.stop();
            this.mCamera.releaseCamera();
        }
    }

    public void onSuccess() {
        Intent intent = new Intent();
        intent.putExtra("wifi_configuration", this.mEnrolleeWifiConfiguration);
        FragmentActivity activity = getActivity();
        activity.setResult(-1, intent);
        activity.finish();
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppQrCodeBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.surface_view);
        this.mSurfaceView = surfaceView;
        this.mSurfaceHolder = surfaceView.getHolder();
        this.mDecorateView = (MiuiQrDecorateView) view.findViewById(R.id.decorate_view);
        setProgressBarShown(isWifiDppHandshaking());
        if (!this.mIsConfiguratorMode) {
            updateEnrolleeSummary();
        } else if (!WifiNetworkConfig.isValidConfig(((WifiNetworkConfig.Retriever) getActivity()).getWifiNetworkConfig())) {
            throw new IllegalStateException("Invalid Wi-Fi network for configuring");
        } else {
            this.mSummary.setText(getString(R.string.dpp_scanner_summary));
        }
        this.mErrorMessage = (TextView) view.findViewById(R.id.error_message);
        if (TextUtils.equals("mediatek", FeatureParser.getString("vendor")) || !this.mWifiManager.isEasyConnectSupported() || !this.mWifiManager.isEasyConnectEnrolleeResponderModeSupported() || this.mIsSlave) {
            return;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.icon_dpp_qrcode);
        this.mDppQrCodeItem = imageView;
        imageView.setImageResource(R.drawable.dpp_qr_code);
        ImageView imageView2 = this.mDppQrCodeItem;
        Resources resources = getActivity().getResources();
        int i = R.string.dpp_theme_title;
        imageView2.setContentDescription(resources.getString(i));
        this.mDppQrCodeItem.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppQrCodeScannerFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (MiuiWifiDppQrCodeScannerFragment.this.getActivity() == null) {
                    return;
                }
                Intent intent = new Intent("show_dpp_qr_code");
                intent.setPackage("com.android.settings");
                MiuiWifiDppQrCodeScannerFragment.this.getActivity().sendBroadcast(intent);
                MiuiWifiDppQrCodeScannerFragment.this.getActivity().finish();
            }
        });
        this.mDppQrCodeItem.setVisibility(0);
        TextView textView = (TextView) view.findViewById(R.id.dpp_qrCode_summary);
        this.mDppSummary = textView;
        textView.setText(i);
        this.mDppSummary.setVisibility(0);
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onWifiStateChanged(int i) {
    }

    @Override // com.android.settings.wifi.qrcode.MiuiQrCamera.ScannerCallback
    public void setTransform(Matrix matrix) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (this.mHasSurface) {
            return;
        }
        this.mHasSurface = true;
        initCamera(surfaceHolder);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mHasSurface = false;
        destroyCamera();
    }
}
