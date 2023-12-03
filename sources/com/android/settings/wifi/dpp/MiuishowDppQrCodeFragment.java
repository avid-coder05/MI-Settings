package com.android.settings.wifi.dpp;

import android.net.Uri;
import android.net.wifi.EasyConnectStatusCallback;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.BaseFragment;
import com.android.settings.R;
import com.android.settings.wifi.qrcode.QrCodeGenerator;
import com.android.settingslib.util.ToastUtil;
import com.google.zxing.WriterException;
import java.util.concurrent.Executor;

/* loaded from: classes2.dex */
public class MiuishowDppQrCodeFragment extends BaseFragment {
    private Handler mDppHandler;
    private String mDppQrCode;
    private TextView mDppQrCodeSummaryView;
    protected Executor mExecutor;
    private ImageView mQrCodeView;
    private View mView;
    private WifiManager mWifiManager;
    private String TAG = "MiuiShowDppQrCodeFragment";
    private boolean mIsDppQrShow = false;
    private final HandlerThread mHandlerThread = new HandlerThread("EasyConnect");
    private EasyConnectStatusCallback mEasyConnectStatusCallback = new EasyConnectStatusCallback() { // from class: com.android.settings.wifi.dpp.MiuishowDppQrCodeFragment.1
        public void onBootstrapUriGenerated(Uri uri) {
            Log.d(MiuishowDppQrCodeFragment.this.TAG, "onBootstrapUriGenerated " + uri.toString());
            Message obtain = Message.obtain();
            obtain.what = 12289;
            obtain.obj = uri.toString();
            MiuishowDppQrCodeFragment.this.mDppHandler.sendMessage(obtain);
        }

        public void onConfiguratorSuccess(int i) {
            Log.d(MiuishowDppQrCodeFragment.this.TAG, "onConfiguratorSuccess " + i);
        }

        public void onEnrolleeSuccess(int i) {
            Log.d(MiuishowDppQrCodeFragment.this.TAG, "onEnrolleeSuccess " + i);
            MiuishowDppQrCodeFragment.this.mWifiManager.connect(i, null);
            if (MiuishowDppQrCodeFragment.this.getActivity() != null) {
                ToastUtil.show(MiuishowDppQrCodeFragment.this.getActivity(), MiuishowDppQrCodeFragment.this.getActivity().getResources().getString(R.string.dpp_connect_success), 0);
                MiuishowDppQrCodeFragment.this.getActivity().finish();
            }
        }

        public void onFailure(int i) {
            Log.d(MiuishowDppQrCodeFragment.this.TAG, "onFailure " + i);
        }

        public void onFailure(int i, String str, SparseArray<int[]> sparseArray, int[] iArr) {
            Log.d(MiuishowDppQrCodeFragment.this.TAG, "onFailure " + i + "--" + str + "--" + sparseArray + "--" + iArr);
        }

        public void onProgress(int i) {
        }
    };

    private void initDppHandler() {
        this.mDppHandler = new Handler() { // from class: com.android.settings.wifi.dpp.MiuishowDppQrCodeFragment.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 12289) {
                    String str = (String) message.obj;
                    if (MiuishowDppQrCodeFragment.this.mView == null || MiuishowDppQrCodeFragment.this.mIsDppQrShow || TextUtils.isEmpty(str)) {
                        return;
                    }
                    MiuishowDppQrCodeFragment.this.showDppQrcode(true, str);
                }
            }
        };
    }

    private void setDppQrCode() {
        try {
            this.mQrCodeView.setImageBitmap(QrCodeGenerator.encodeQrCode(this.mDppQrCode, getContext().getResources().getDimensionPixelSize(R.dimen.qrcode_size)));
        } catch (WriterException e) {
            Log.e(this.TAG, "Error generating QR code bitmap " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDppQrcode(boolean z, String str) {
        this.mIsDppQrShow = z;
        View view = this.mView;
        int i = R.id.qrcode_view;
        view.findViewById(i).setVisibility(z ? 0 : 8);
        View view2 = this.mView;
        int i2 = R.id.dpp_qrcode_generator;
        view2.findViewById(i2).setVisibility(z ? 0 : 8);
        if (z) {
            this.mQrCodeView = (ImageView) this.mView.findViewById(i);
            TextView textView = (TextView) this.mView.findViewById(i2);
            this.mDppQrCodeSummaryView = textView;
            textView.setText(R.string.dpp_qr_code_summary);
            this.mDppQrCode = str;
            setDppQrCode();
        }
    }

    @Override // com.android.settings.BaseFragment
    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.miui_show_dpp_qrcode_fragment, viewGroup, false);
        this.mView = inflate;
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService("wifi");
        this.mWifiManager = wifiManager;
        if (!wifiManager.isEasyConnectEnrolleeResponderModeSupported()) {
            Log.d(this.TAG, "not supported Easy Connect Enrollee responder mode");
            return;
        }
        this.mHandlerThread.start();
        this.mExecutor = new HandlerExecutor(new Handler(this.mHandlerThread.getLooper()));
        this.mWifiManager.startEasyConnectAsEnrolleeResponder(SystemProperties.get("ro.product.model", "xiaomi"), 0, this.mExecutor, this.mEasyConnectStatusCallback);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initDppHandler();
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
    public void onPause() {
        super.onPause();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (getActivity() == null || !getActivity().isInMultiWindowMode()) {
            return;
        }
        ToastUtil.show(getActivity(), R.string.wlan_scanner_unresizeble, 0);
        getActivity().finish();
    }
}
