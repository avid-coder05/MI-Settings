package com.android.settings.wifi.dpp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.EasyConnectStatusCallback;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import com.android.settings.R;
import com.android.settings.Settings;
import com.android.settingslib.util.ToastUtil;
import com.android.settingslib.wifi.SlaveWifiUtils;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MiuiWifiDppUtils {
    private static String TAG = "MiuiWifiDppUtils";
    private ConnectivityManager mConnectivityManager;
    private Context mContext;
    private WifiDppInitiatorViewModel mDppModel;
    private Handler mHandler;
    private AlertDialog mSharingDialog;
    private SlaveWifiUtils mSlaveWifiUtils;
    private WifiManager mWifiManager;
    private WifiNetworkConfig mWifiNetworkConfig;
    private WifiQrCode mWifiQrCode;
    private boolean mIsShareWifi = false;
    private boolean mIsEasyConnectCallback = false;
    private int mTimesOfReStartEasyConnect = 0;

    /* renamed from: com.android.settings.wifi.dpp.MiuiWifiDppUtils$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    class AnonymousClass1 implements DialogInterface.OnClickListener {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class EasyConnectConfiguratorStatusCallback extends EasyConnectStatusCallback {
        private EasyConnectConfiguratorStatusCallback() {
        }

        /* synthetic */ EasyConnectConfiguratorStatusCallback(MiuiWifiDppUtils miuiWifiDppUtils, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void onConfiguratorSuccess(int i) {
            Log.d(MiuiWifiDppUtils.TAG, "success to share wifi.");
            MiuiWifiDppUtils.this.mIsEasyConnectCallback = true;
            MiuiWifiDppUtils.this.mIsShareWifi = true;
            MiuiWifiDppUtils.this.mWifiManager = null;
            MiuiWifiDppUtils.this.mConnectivityManager = null;
            MiuiWifiDppUtils.this.mDppModel = null;
            MiuiWifiDppUtils.this.dppToast((MiuiWifiDppUtils.this.mWifiNetworkConfig == null || !MiuiWifiDppUtils.this.mWifiNetworkConfig.getHiddenSsid()) ? MiuiWifiDppUtils.this.mContext.getResources().getString(R.string.dpp_shared_success) : MiuiWifiDppUtils.this.mContext.getResources().getString(R.string.dpp_not_support));
        }

        public void onEnrolleeSuccess(int i) {
        }

        public void onFailure(int i, String str, SparseArray<int[]> sparseArray, int[] iArr) {
            Log.d(MiuiWifiDppUtils.TAG, "fail to share wifi, caused by code " + i);
            MiuiWifiDppUtils.this.mIsEasyConnectCallback = true;
            if (MiuiWifiDppUtils.this.mWifiManager != null) {
                MiuiWifiDppUtils.this.mWifiManager.stopEasyConnectSession();
                MiuiWifiDppUtils.this.mWifiManager = null;
            }
            MiuiWifiDppUtils.this.mConnectivityManager = null;
            MiuiWifiDppUtils.this.mDppModel = null;
            MiuiWifiDppUtils.this.dppToast(MiuiWifiDppUtils.this.mContext.getResources().getString(R.string.dpp_shared_fail));
        }

        public void onProgress(int i) {
            Log.d(MiuiWifiDppUtils.TAG, "on progress");
        }
    }

    public MiuiWifiDppUtils(Context context) {
        this.mContext = context;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mSlaveWifiUtils = SlaveWifiUtils.getInstance(this.mContext);
        this.mConnectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
        this.mDppModel = new WifiDppInitiatorViewModel(((Settings.WifiSettingsActivity) this.mContext).getApplication());
        final EasyConnectConfiguratorStatusCallback easyConnectConfiguratorStatusCallback = new EasyConnectConfiguratorStatusCallback(this, null);
        this.mDppModel.getStatusCode().observe((LifecycleOwner) this.mContext, new Observer() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppUtils$$ExternalSyntheticLambda0
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                MiuiWifiDppUtils.this.lambda$new$0(easyConnectConfiguratorStatusCallback, (Integer) obj);
            }
        });
        initHandler();
    }

    static /* synthetic */ int access$1408(MiuiWifiDppUtils miuiWifiDppUtils) {
        int i = miuiWifiDppUtils.mTimesOfReStartEasyConnect;
        miuiWifiDppUtils.mTimesOfReStartEasyConnect = i + 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dppToast(String str) {
        Context context = this.mContext;
        if (context == null) {
            return;
        }
        ToastUtil.show(context, str, 0);
    }

    private void initHandler() {
        this.mHandler = new Handler() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppUtils.5
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 2081) {
                    MiuiWifiDppUtils.this.startWifiDppConfiguratorInitiator();
                    if (MiuiWifiDppUtils.this.mWifiQrCode == null || MiuiWifiDppUtils.this.mWifiQrCode.getQrCode() == null || MiuiWifiDppUtils.this.mWifiQrCode.getQrCode().contains("C:")) {
                        return;
                    }
                    sendEmptyMessageDelayed(2084, 2000L);
                } else if (i == 2082) {
                    if (MiuiWifiDppUtils.this.mWifiManager == null || MiuiWifiDppUtils.this.mIsShareWifi || MiuiWifiDppUtils.this.mDppModel == null) {
                        return;
                    }
                    MiuiWifiDppUtils.access$1408(MiuiWifiDppUtils.this);
                    Log.d(MiuiWifiDppUtils.TAG, "restart Easy Connect for " + MiuiWifiDppUtils.this.mTimesOfReStartEasyConnect + " time");
                    MiuiWifiDppUtils.this.mWifiManager.stopEasyConnectSession();
                    MiuiWifiDppUtils.this.startWifiDppConfiguratorInitiator();
                    sendEmptyMessageDelayed(2084, 2000L);
                } else if (i == 2083) {
                    if (MiuiWifiDppUtils.this.mWifiManager == null || MiuiWifiDppUtils.this.mIsShareWifi || MiuiWifiDppUtils.this.mDppModel == null) {
                        return;
                    }
                    Log.d(MiuiWifiDppUtils.TAG, "fail to share wifi, caused by no response from enrollee");
                    MiuiWifiDppUtils.this.mWifiManager.stopEasyConnectSession();
                    if (MiuiWifiDppUtils.this.mSharingDialog == null || !MiuiWifiDppUtils.this.mSharingDialog.isShowing()) {
                        return;
                    }
                    MiuiWifiDppUtils.this.mSharingDialog.dismiss();
                    MiuiWifiDppUtils.this.dppToast(MiuiWifiDppUtils.this.mContext.getResources().getString(R.string.dpp_shared_fail));
                } else if (i == 2084) {
                    if (MiuiWifiDppUtils.this.mHandler != null && !MiuiWifiDppUtils.this.mIsEasyConnectCallback && MiuiWifiDppUtils.this.mTimesOfReStartEasyConnect < 4) {
                        sendEmptyMessage(2082);
                    } else if (MiuiWifiDppUtils.this.mHandler == null || MiuiWifiDppUtils.this.mIsEasyConnectCallback || MiuiWifiDppUtils.this.mTimesOfReStartEasyConnect != 4) {
                    } else {
                        sendEmptyMessage(2083);
                    }
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(EasyConnectConfiguratorStatusCallback easyConnectConfiguratorStatusCallback, Integer num) {
        if (this.mDppModel.isWifiDppHandshaking()) {
            return;
        }
        int intValue = num.intValue();
        if (intValue == 1) {
            AlertDialog alertDialog = this.mSharingDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
                this.mSharingDialog = null;
            }
            easyConnectConfiguratorStatusCallback.onConfiguratorSuccess(intValue);
            return;
        }
        AlertDialog alertDialog2 = this.mSharingDialog;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
            this.mSharingDialog = null;
        }
        easyConnectConfiguratorStatusCallback.onFailure(intValue, this.mDppModel.getTriedSsid(), this.mDppModel.getTriedChannels(), this.mDppModel.getBandArray());
    }

    private void setAlphaFolme(View view) {
        if (view == null) {
            return;
        }
        Folme.useAt(view).touch().setAlpha(0.6f, ITouchStyle.TouchType.DOWN).handleTouchOf(view, new AnimConfig[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSharingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setCancelable(true);
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.miui_wifi_dpp_sharing_dialog, (ViewGroup) null);
        setAlphaFolme((FrameLayout) inflate.findViewById(R.id.sharing_anim_bg));
        ImageView imageView = (ImageView) inflate.findViewById(R.id.icon_sharing);
        imageView.setImageResource(R.drawable.dpp_share_loading);
        startSharingAnimation(imageView);
        ((TextView) inflate.findViewById(R.id.dpp_sharing_wifi)).setText(R.string.dpp_sharing_wifi_summary);
        builder.setView(inflate);
        AlertDialog create = builder.create();
        this.mSharingDialog = create;
        if (!create.isShowing()) {
            this.mSharingDialog.show();
        }
        this.mHandler.sendEmptyMessage(2081);
    }

    private void startSharingAnimation(ImageView imageView) {
        if (imageView == null || imageView.getDrawable() == null) {
            return;
        }
        ((AnimatedVectorDrawable) imageView.getDrawable()).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWifiDppConfiguratorInitiator() {
        if (this.mDppModel == null) {
            return;
        }
        WifiNetworkConfig wifiNetworkConfig = this.mWifiNetworkConfig;
        if (wifiNetworkConfig == null || !wifiNetworkConfig.getHiddenSsid()) {
            this.mDppModel.startEasyConnectAsConfiguratorInitiator(this.mWifiQrCode.getQrCode(), this.mWifiNetworkConfig.getNetworkId());
            return;
        }
        AlertDialog alertDialog = this.mSharingDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mSharingDialog = null;
        }
        dppToast(this.mContext.getResources().getString(R.string.dpp_not_support));
        Log.e(TAG, "Selected network is hiddenNetwork");
    }

    public void setWifiNetworkConfig(WifiNetworkConfig wifiNetworkConfig) {
        this.mWifiNetworkConfig = wifiNetworkConfig;
    }

    public void setWifiQrCode(WifiQrCode wifiQrCode) {
        this.mWifiQrCode = wifiQrCode;
    }

    public void showWifiShareDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setCancelable(false);
        builder.setTitle(this.mContext.getResources().getString(R.string.dpp_theme_title));
        builder.setMessage(this.mContext.getResources().getString(R.string.dpp_before_share_summary, this.mWifiNetworkConfig.getSsid()));
        builder.setNegativeButton(this.mContext.getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppUtils.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(this.mContext.getResources().getString(R.string.screen_confirm), new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiDppUtils.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                MiuiWifiDppUtils.this.showSharingDialog();
            }
        });
        builder.create().show();
    }
}
