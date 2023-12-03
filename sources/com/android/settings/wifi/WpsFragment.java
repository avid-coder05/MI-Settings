package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.BaseFragment;
import com.android.settings.R;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.wifi.AccessPoint;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import miuix.androidbasewidget.widget.ProgressBar;

/* loaded from: classes2.dex */
public class WpsFragment extends BaseFragment {
    private Button mCenterButton;
    private IntentFilter mFilter;
    private boolean mIsWpsSetupFinish;
    private String mPin;
    private ProgressBar mProgressBar;
    private BroadcastReceiver mReceiver;
    private Button mRetryButton;
    private TextView mSummaryText;
    private ProgressBar mTimeoutBar;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private WifiManager mWifiManager;
    private ImageView mWpsIndictor;
    private WifiManager.WpsCallback mWpsListener;
    private int mWpsSetup;
    private Handler mHandler = new Handler();
    State mState = State.WPS_INIT;

    /* renamed from: com.android.settings.wifi.WpsFragment$6  reason: invalid class name */
    /* loaded from: classes2.dex */
    static /* synthetic */ class AnonymousClass6 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$wifi$WpsFragment$State;

        static {
            int[] iArr = new int[State.values().length];
            $SwitchMap$com$android$settings$wifi$WpsFragment$State = iArr;
            try {
                iArr[State.WPS_START.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$wifi$WpsFragment$State[State.WPS_COMPLETE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$wifi$WpsFragment$State[State.CONNECTED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$wifi$WpsFragment$State[State.WPS_FAILED.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum State {
        WPS_INIT,
        WPS_START,
        WPS_COMPLETE,
        CONNECTED,
        WPS_FAILED
    }

    /* loaded from: classes2.dex */
    private static class WpsListener extends WifiManager.WpsCallback {
        private WeakReference<WpsFragment> mFragmentRef;

        public WpsListener(WpsFragment wpsFragment) {
            this.mFragmentRef = new WeakReference<>(wpsFragment);
        }

        @Override // android.net.wifi.WifiManager.WpsCallback
        public void onFailed(int i) {
            WpsFragment wpsFragment = this.mFragmentRef.get();
            if (wpsFragment == null || !wpsFragment.isAdded()) {
                return;
            }
            wpsFragment.getActivity();
            wpsFragment.updateState(State.WPS_FAILED, "");
        }

        @Override // android.net.wifi.WifiManager.WpsCallback
        public void onStarted(String str) {
            WpsFragment wpsFragment = this.mFragmentRef.get();
            if (wpsFragment == null || !wpsFragment.isAdded()) {
                return;
            }
            if (str != null) {
                wpsFragment.mPin = str;
                wpsFragment.updateState(State.WPS_START, null);
                return;
            }
            wpsFragment.mPin = null;
            wpsFragment.updateState(State.WPS_START, null);
        }

        @Override // android.net.wifi.WifiManager.WpsCallback
        public void onSucceeded() {
            WpsFragment wpsFragment = this.mFragmentRef.get();
            if (wpsFragment != null) {
                wpsFragment.isAdded();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEvent(Context context, Intent intent) {
        if ("android.net.wifi.STATE_CHANGE".equals(intent.getAction()) && ((NetworkInfo) intent.getParcelableExtra("networkInfo")).getDetailedState() == NetworkInfo.DetailedState.CONNECTED && this.mState == State.WPS_COMPLETE) {
            wpsConnected();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void start() {
        this.mTimer = new Timer(false);
        TimerTask timerTask = new TimerTask() { // from class: com.android.settings.wifi.WpsFragment.3
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                WpsFragment.this.mHandler.post(new Runnable() { // from class: com.android.settings.wifi.WpsFragment.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        WpsFragment.this.mTimeoutBar.incrementProgressBy(1);
                    }
                });
            }
        };
        this.mTimerTask = timerTask;
        this.mTimer.schedule(timerTask, 1000L, 1000L);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.WpsFragment.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                WpsFragment.this.handleEvent(context, intent);
            }
        };
        getActivity().registerReceiver(this.mReceiver, this.mFilter);
        if (this.mIsWpsSetupFinish) {
            wpsConnected();
            return;
        }
        WpsInfo wpsInfo = new WpsInfo();
        wpsInfo.setup = this.mWpsSetup;
        this.mWifiManager.startWps(wpsInfo, this.mWpsListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stop() {
        if (this.mState != State.WPS_COMPLETE) {
            this.mWifiManager.cancelWps(null);
        }
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
            this.mTimer = null;
        }
        TimerTask timerTask = this.mTimerTask;
        if (timerTask != null) {
            timerTask.cancel();
            this.mTimerTask = null;
        }
        if (this.mReceiver != null) {
            getActivity().unregisterReceiver(this.mReceiver);
            this.mReceiver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateState(final State state, final String str) {
        this.mState = state;
        this.mHandler.post(new Runnable() { // from class: com.android.settings.wifi.WpsFragment.5
            @Override // java.lang.Runnable
            public void run() {
                if (WpsFragment.this.isAdded()) {
                    int i = AnonymousClass6.$SwitchMap$com$android$settings$wifi$WpsFragment$State[state.ordinal()];
                    if (i == 1) {
                        if (WpsFragment.this.mPin != null) {
                            WpsFragment.this.mSummaryText.setText(String.format(WpsFragment.this.getString(R.string.wifi_wps_onstart_pin), WpsFragment.this.mPin));
                            WpsFragment.this.mWpsIndictor.setVisibility(8);
                            return;
                        }
                        WpsFragment.this.mSummaryText.setText(R.string.wifi_wps_onstart_pbc);
                        WpsFragment.this.mWpsIndictor.setVisibility(0);
                    } else if (i == 2) {
                        WpsFragment.this.mTimeoutBar.setVisibility(8);
                        WpsFragment.this.mWpsIndictor.setVisibility(8);
                        WpsFragment.this.mProgressBar.setVisibility(0);
                        WpsFragment.this.mSummaryText.setText(str);
                    } else if (i == 3) {
                        WpsFragment.this.mRetryButton.setVisibility(8);
                        WpsFragment.this.mTimeoutBar.setVisibility(8);
                        WpsFragment.this.mProgressBar.setVisibility(8);
                        WpsFragment.this.mWpsIndictor.setVisibility(8);
                        WpsFragment.this.mCenterButton.setText(R.string.dlg_ok);
                        WpsFragment.this.mSummaryText.setText(str);
                    } else if (i != 4) {
                    } else {
                        WpsFragment.this.mRetryButton.setVisibility(0);
                        WpsFragment.this.mTimeoutBar.setVisibility(8);
                        WpsFragment.this.mProgressBar.setVisibility(8);
                        WpsFragment.this.mWpsIndictor.setVisibility(8);
                        WpsFragment.this.mCenterButton.setText(R.string.cancel);
                        WpsFragment.this.mSummaryText.setText(str);
                        if (WpsFragment.this.mReceiver != null) {
                            WpsFragment.this.getActivity().unregisterReceiver(WpsFragment.this.mReceiver);
                            WpsFragment.this.mReceiver = null;
                        }
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSummaryText() {
        if (this.mPin != null) {
            this.mSummaryText.setText(String.format(getString(R.string.wifi_wps_onstart_pin), this.mPin));
            this.mWpsIndictor.setVisibility(8);
            return;
        }
        this.mSummaryText.setText(R.string.wifi_wps_onstart_pbc);
        this.mWpsIndictor.setVisibility(0);
    }

    private void wpsConnected() {
        android.net.wifi.WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        if (connectionInfo != null) {
            String ssid = connectionInfo.getSSID();
            if (!TextUtils.isEmpty(ssid)) {
                ssid = AccessPoint.removeDoubleQuotes(ssid);
            }
            String format = String.format(getString(R.string.wifi_wps_connected), ssid);
            this.mIsWpsSetupFinish = true;
            updateState(State.CONNECTED, format);
        }
    }

    @Override // com.android.settings.BaseFragment
    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.wifi_wps, viewGroup, false);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getActivity() != null && !SettingsFeatures.isSplitTablet(getContext())) {
            getActivity().setRequestedOrientation(1);
        }
        if (getArguments() == null) {
            this.mWpsSetup = 0;
        } else {
            this.mWpsSetup = getArguments().getInt("wps_setup", 0);
        }
        this.mWifiManager = (WifiManager) getActivity().getSystemService("wifi");
        this.mWpsListener = new WpsListener(this);
        IntentFilter intentFilter = new IntentFilter();
        this.mFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        if (bundle != null) {
            this.mIsWpsSetupFinish = bundle.getBoolean("wps_setup_finish", false);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("wps_setup_finish", this.mIsWpsSetupFinish);
        super.onSaveInstanceState(bundle);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        stop();
        super.onStop();
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mSummaryText = (TextView) view.findViewById(R.id.wps_txt);
        this.mWpsIndictor = (ImageView) view.findViewById(R.id.indictor);
        updateSummaryText();
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.wps_timeout_bar);
        this.mTimeoutBar = progressBar;
        progressBar.setMax(120);
        this.mTimeoutBar.setProgress(0);
        ProgressBar progressBar2 = (ProgressBar) view.findViewById(R.id.wps_progress_bar);
        this.mProgressBar = progressBar2;
        progressBar2.setVisibility(8);
        Button button = (Button) view.findViewById(R.id.center_btn);
        this.mCenterButton = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.WpsFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                WpsFragment.this.finish();
            }
        });
        Button button2 = (Button) view.findViewById(R.id.retry_btn);
        this.mRetryButton = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.WpsFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                view2.setVisibility(8);
                WpsFragment.this.updateSummaryText();
                WpsFragment.this.mTimeoutBar.setProgress(0);
                WpsFragment.this.mTimeoutBar.setVisibility(0);
                WpsFragment.this.stop();
                WpsFragment.this.start();
            }
        });
        start();
    }
}
