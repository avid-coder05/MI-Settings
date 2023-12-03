package com.android.settings.bluetooth;

import android.app.UiModeManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.WindowInsets;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.bluetooth.ble.app.IMiuiHeadsetCallback;
import com.android.bluetooth.ble.app.IMiuiHeadsetService;
import com.android.settings.R;
import java.lang.ref.WeakReference;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public final class MiuiHeadsetActivity extends AppCompatActivity {
    public ContentObserver mContentObserver;
    public Handler mDealHandler;
    private View mOuterView;
    protected BluetoothDevice mDevice = null;
    protected String mSupport = "";
    protected String mComeFrom = "";
    protected String mVirtualDeviceAddress = "";
    protected String mVirtualDeviceName = "";
    private IMiuiHeadsetService mService = null;
    protected String mDeviceID = "";
    protected String mDeviceConfig = "000011101110";
    private MiuiHeadsetCallback mCallBack = null;
    private ServiceConnection mConnection = new ServiceConnection() { // from class: com.android.settings.bluetooth.MiuiHeadsetActivity.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MiuiHeadsetActivity.this.mService = IMiuiHeadsetService.Stub.asInterface(iBinder);
            Fragment findFragmentById = MiuiHeadsetActivity.this.getSupportFragmentManager().findFragmentById(R.id.layout_content);
            if (findFragmentById instanceof MiuiHeadsetAntiLostFragment) {
                ((MiuiHeadsetAntiLostFragment) findFragmentById).onServiceConnected();
            } else if (findFragmentById instanceof MiuiHeadsetFragment) {
                ((MiuiHeadsetFragment) findFragmentById).onServiceConnected();
            } else if (findFragmentById instanceof MiuiHeadsetFitnessFragment) {
                ((MiuiHeadsetFitnessFragment) findFragmentById).onServiceConnected();
            } else if (findFragmentById instanceof MiuiHeadsetFindDeviceFragment) {
                ((MiuiHeadsetFindDeviceFragment) findFragmentById).onServiceConnected();
            } else if (findFragmentById instanceof MiuiHeadsetPressKeyFragment) {
                ((MiuiHeadsetPressKeyFragment) findFragmentById).onServiceConnected();
            } else if (findFragmentById instanceof MiuiHeadsetKeyConfigFragment) {
                ((MiuiHeadsetKeyConfigFragment) findFragmentById).onServiceConnected();
            }
            try {
                MiuiHeadsetActivity.this.mService.register(MiuiHeadsetActivity.this.mCallBack);
                if (!(findFragmentById instanceof MiuiHeadsetFragment)) {
                    MiuiHeadsetActivity.this.mService.connect(MiuiHeadsetActivity.this.mDevice);
                }
            } catch (RemoteException e) {
                Log.e("MiuiHeadsetActivity", "connect the mma failed " + e);
            }
            Log.v("MiuiHeadsetActivity", "onServiceConnected");
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("MiuiHeadsetActivity", "service disconnected");
        }
    };

    /* loaded from: classes.dex */
    public static class H extends Handler {
        public WeakReference<MiuiHeadsetActivity> contextWeakReference;

        public H(MiuiHeadsetActivity miuiHeadsetActivity) {
            this.contextWeakReference = new WeakReference<>(miuiHeadsetActivity);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 100001) {
                return;
            }
            Log.d("MiuiHeadsetActivity", "handleMessage: MSG_NAV_BAR_STYLE_CHANGE");
            MiuiHeadsetActivity miuiHeadsetActivity = this.contextWeakReference.get();
            if (miuiHeadsetActivity != null) {
                miuiHeadsetActivity.onNavBarStyleChanged();
            }
        }
    }

    /* loaded from: classes.dex */
    private static class MiuiHeadsetCallback extends IMiuiHeadsetCallback.Stub {
        private WeakReference<MiuiHeadsetActivity> activityWeakRef;

        public MiuiHeadsetCallback(MiuiHeadsetActivity miuiHeadsetActivity) {
            this.activityWeakRef = new WeakReference<>(miuiHeadsetActivity);
        }

        @Override // com.android.bluetooth.ble.app.IMiuiHeadsetCallback
        public void refreshStatus(String str, String str2) {
            MiuiHeadsetActivity miuiHeadsetActivity = this.activityWeakRef.get();
            if (miuiHeadsetActivity == null) {
                return;
            }
            Fragment findFragmentById = miuiHeadsetActivity.getSupportFragmentManager().findFragmentById(R.id.layout_content);
            if (findFragmentById instanceof MiuiHeadsetFragment) {
                ((MiuiHeadsetFragment) findFragmentById).refreshStatus(str, str2);
            } else if (findFragmentById instanceof MiuiHeadsetFitnessFragment) {
                ((MiuiHeadsetFitnessFragment) findFragmentById).refreshStatus(str, str2);
            } else if (findFragmentById instanceof MiuiHeadsetFindDeviceFragment) {
                ((MiuiHeadsetFindDeviceFragment) findFragmentById).refreshStatus(str, str2);
            }
        }
    }

    private void init() {
        Log.d("MiuiHeadsetActivity", "start to get the binder");
        Intent intent = new Intent("miui.bluetooth.mible.BluetoothHeadsetService");
        intent.setPackage("com.xiaomi.bluetooth");
        bindService(intent, this.mConnection, 1);
    }

    public static void registerNavBarStyleListener(Context context, ContentObserver contentObserver) {
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("force_fsg_nav_bar"), false, contentObserver);
    }

    public static void unregisterNavBarStyleListener(Context context, ContentObserver contentObserver) {
        context.getContentResolver().unregisterContentObserver(contentObserver);
    }

    public void changeFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        int i = R.id.layout_content;
        Fragment findFragmentById = supportFragmentManager.findFragmentById(i);
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment.isAdded()) {
            beginTransaction.hide(findFragmentById).show(fragment).commitAllowingStateLoss();
            beginTransaction.addToBackStack(null);
            return;
        }
        beginTransaction.hide(findFragmentById).add(i, fragment).commitAllowingStateLoss();
        beginTransaction.addToBackStack(null);
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public String getDeviceConfig() {
        return this.mDeviceConfig;
    }

    public String getDeviceID() {
        return this.mDeviceID;
    }

    public IMiuiHeadsetService getService() {
        return this.mService;
    }

    public String getSupport() {
        return this.mSupport;
    }

    public String getVirtualDeviceAddress() {
        return this.mVirtualDeviceAddress;
    }

    public String getVirtualDeviceName() {
        return this.mVirtualDeviceName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        BluetoothAdapter defaultAdapter;
        super.onCreate(bundle);
        setContentView(R.layout.activity_headset);
        Intent intent = getIntent();
        if (intent == null) {
            Log.e("MiuiHeadsetActivity", "intent is null");
            return;
        }
        this.mDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        String stringExtra = intent.getStringExtra("bluetoothaddress");
        if (this.mDevice == null && (defaultAdapter = BluetoothAdapter.getDefaultAdapter()) != null) {
            for (BluetoothDevice bluetoothDevice : defaultAdapter.getBondedDevices()) {
                String address = bluetoothDevice.getAddress();
                if (stringExtra != null && stringExtra.equals(address)) {
                    this.mDevice = bluetoothDevice;
                }
            }
        }
        this.mSupport = intent.getStringExtra("MIUI_HEADSET_SUPPORT");
        this.mComeFrom = intent.getStringExtra("COME_FROM");
        this.mVirtualDeviceAddress = intent.getStringExtra("VIRTUAL_DEVICE_ADDRESS");
        this.mVirtualDeviceName = intent.getStringExtra("VIRTUAL_DEVICE_NAME");
        ActionBar appCompatActionBar = getAppCompatActionBar();
        String str = this.mSupport;
        if (str != null) {
            String[] split = str.split("\\,");
            if (split == null || split.length != 2) {
                Log.e("MiuiHeadsetActivity", "Length error");
            } else {
                this.mDeviceID = split[0];
                Log.d("MiuiHeadsetActivity", "Length OK" + this.mDeviceID);
            }
        }
        if (TextUtils.isEmpty(this.mComeFrom)) {
            Log.d("MiuiHeadsetActivity", "mComeFrom is null");
        } else if (this.mComeFrom.equals("MIUI_HEADSET_VIRTUAL_DEVICE_INFO")) {
            if (appCompatActionBar != null) {
                appCompatActionBar.setTitle(R.string.bluetooth_device_advanced_title);
            }
            if (bundle == null) {
                FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
                beginTransaction.add(R.id.layout_content, new MiuiHeadsetVirtualDeviceFragment());
                beginTransaction.commit();
            }
        } else if (this.mDevice == null) {
            Log.e("MiuiHeadsetActivity", "Headset device is null, not creat active ");
        } else {
            if (this.mComeFrom.equals("MIUI_BLUETOOTH_SETTINGS")) {
                if (appCompatActionBar != null) {
                    appCompatActionBar.setTitle(this.mDevice.getAlias());
                }
                if (bundle == null) {
                    MiuiHeadsetFragment miuiHeadsetFragment = new MiuiHeadsetFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putParcelable("BT_Device", this.mDevice);
                    bundle2.putString("BT_Device_Support", this.mSupport);
                    miuiHeadsetFragment.setArguments(bundle2);
                    FragmentTransaction beginTransaction2 = getSupportFragmentManager().beginTransaction();
                    beginTransaction2.add(R.id.layout_content, miuiHeadsetFragment);
                    beginTransaction2.commit();
                }
            } else if (this.mComeFrom.equals("MIUI_HEADSET_NOTIFICAION")) {
                if (appCompatActionBar != null) {
                    appCompatActionBar.setTitle(R.string.switch_headset_anti_lost_title);
                }
                if (bundle == null) {
                    FragmentTransaction beginTransaction3 = getSupportFragmentManager().beginTransaction();
                    beginTransaction3.add(R.id.layout_content, new MiuiHeadsetAntiLostFragment());
                    beginTransaction3.commit();
                }
            }
            this.mCallBack = new MiuiHeadsetCallback(this);
            init();
            this.mDealHandler = new H(this);
            ContentObserver contentObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.bluetooth.MiuiHeadsetActivity.2
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    MiuiHeadsetActivity.this.mDealHandler.sendEmptyMessage(100001);
                }
            };
            this.mContentObserver = contentObserver;
            registerNavBarStyleListener(this, contentObserver);
            this.mContentObserver.onChange(false);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        ServiceConnection serviceConnection;
        super.onDestroy();
        try {
            IMiuiHeadsetService iMiuiHeadsetService = this.mService;
            if (iMiuiHeadsetService != null) {
                iMiuiHeadsetService.unregister(this.mCallBack, this.mDevice);
                this.mCallBack = null;
            }
        } catch (Exception e) {
            Log.e("MiuiHeadsetActivity", "mService.unregister error: " + e.toString());
        }
        if (this.mService != null && (serviceConnection = this.mConnection) != null) {
            unbindService(serviceConnection);
            this.mConnection = null;
        }
        ContentObserver contentObserver = this.mContentObserver;
        if (contentObserver != null) {
            unregisterNavBarStyleListener(this, contentObserver);
            this.mContentObserver = null;
        }
    }

    public void onNavBarStyleChanged() {
        try {
            this.mOuterView = findViewById(R.id.layout_content);
            boolean z = Settings.Global.getInt(getContentResolver(), "force_fsg_nav_bar", 0) == 0;
            Log.d("MiuiHeadsetActivity", "is no full screen mode: " + z);
            if (z) {
                this.mOuterView.setFitsSystemWindows(true);
                getWindow().clearFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
                getWindow().setNavigationBarColor(((UiModeManager) getSystemService("uimode")).getNightMode() == 2 ? -16777216 : -1);
            } else {
                this.mOuterView.setFitsSystemWindows(false);
                this.mOuterView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: com.android.settings.bluetooth.MiuiHeadsetActivity.3
                    @Override // android.view.View.OnApplyWindowInsetsListener
                    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                        MiuiHeadsetActivity.this.getWindow().addFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
                        MiuiHeadsetActivity.this.getWindow().setNavigationBarColor(0);
                        windowInsets.consumeSystemWindowInsets();
                        MiuiHeadsetActivity.this.mOuterView.setOnApplyWindowInsetsListener(null);
                        return windowInsets;
                    }
                });
                this.mOuterView.requestApplyInsets();
            }
        } catch (Exception e) {
            Log.d("MiuiHeadsetActivity", "dealing with onNavBarStyleChanged occurs error: " + e);
        }
    }

    public void setDeviceConfig(String str) {
        this.mDeviceConfig = str;
    }
}
