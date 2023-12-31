package com.android.settings.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.TetherEnabler;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.GenericSwitchController;
import com.android.settings.widget.PrimarySwitchPreference;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.TetherUtil;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public class AllInOneTetherPreferenceController extends BasePreferenceController implements LifecycleObserver, TetherEnabler.OnTetherStateUpdateListener {
    private static final String TAG = "AllInOneTetherPreferenceController";
    private final boolean mAdminDisallowedTetherConfig;
    private final BluetoothAdapter mBluetoothAdapter;
    private final AtomicReference<BluetoothPan> mBluetoothPan;
    final BluetoothProfile.ServiceListener mBtProfileServiceListener;
    private PrimarySwitchPreference mPreference;
    private TetherEnabler mTetherEnabler;
    private int mTetheringState;

    AllInOneTetherPreferenceController() {
        super(null, "test");
        this.mBtProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.network.AllInOneTetherPreferenceController.1
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                AllInOneTetherPreferenceController.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                AllInOneTetherPreferenceController.this.mBluetoothPan.set(null);
            }
        };
        this.mAdminDisallowedTetherConfig = false;
        this.mBluetoothPan = new AtomicReference<>();
        this.mBluetoothAdapter = null;
    }

    public AllInOneTetherPreferenceController(Context context, String str) {
        super(context, str);
        this.mBtProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.network.AllInOneTetherPreferenceController.1
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                AllInOneTetherPreferenceController.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                AllInOneTetherPreferenceController.this.mBluetoothPan.set(null);
            }
        };
        this.mBluetoothPan = new AtomicReference<>();
        this.mAdminDisallowedTetherConfig = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, "no_config_tethering", UserHandle.myUserId()) != null;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (PrimarySwitchPreference) preferenceScreen.findPreference(this.mPreferenceKey);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (TetherUtil.isTetherAvailable(this.mContext) && FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one")) ? 0 : 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i = this.mTetheringState;
        switch (i) {
            case 0:
                return this.mContext.getString(R.string.tether_settings_summary_off);
            case 1:
                return this.mContext.getString(R.string.tether_settings_summary_hotspot_only);
            case 2:
                return this.mContext.getString(R.string.tether_settings_summary_usb_tethering_only);
            case 3:
                return this.mContext.getString(R.string.tether_settings_summary_hotspot_and_usb);
            case 4:
                return this.mContext.getString(R.string.tether_settings_summary_bluetooth_tethering_only);
            case 5:
                return this.mContext.getString(R.string.tether_settings_summary_hotspot_and_bluetooth);
            case 6:
                return this.mContext.getString(R.string.tether_settings_summary_usb_and_bluetooth);
            case 7:
                return this.mContext.getString(R.string.tether_settings_summary_hotspot_and_usb_and_bluetooth);
            default:
                switch (i) {
                    case 32:
                        return this.mContext.getString(R.string.tether_settings_summary_ethernet_tethering_only);
                    case 33:
                        return this.mContext.getString(R.string.tether_settings_summary_hotspot_and_ethernet);
                    case 34:
                        return this.mContext.getString(R.string.tether_settings_summary_usb_and_ethernet);
                    case 35:
                        return this.mContext.getString(R.string.tether_settings_summary_hotspot_and_usb_and_ethernet);
                    case 36:
                        return this.mContext.getString(R.string.tether_settings_summary_bluetooth_and_ethernet);
                    case 37:
                        return this.mContext.getString(R.string.tether_settings_summary_hotspot_and_bluetooth_and_ethernet);
                    case 38:
                        return this.mContext.getString(R.string.tether_settings_summary_usb_and_bluetooth_and_ethernet);
                    case 39:
                        return this.mContext.getString(R.string.tether_settings_summary_all);
                    default:
                        Log.e(TAG, "Unknown tethering state");
                        return this.mContext.getString(R.string.summary_placeholder);
                }
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initEnabler(Lifecycle lifecycle) {
        if (this.mPreference == null) {
            Log.e(TAG, "TetherEnabler is not initialized");
            return;
        }
        TetherEnabler tetherEnabler = new TetherEnabler(this.mContext, new GenericSwitchController(this.mPreference), this.mBluetoothPan);
        this.mTetherEnabler = tetherEnabler;
        if (lifecycle != null) {
            lifecycle.addObserver(tetherEnabler);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || bluetoothAdapter.getState() != 12) {
            return;
        }
        this.mBluetoothAdapter.getProfileProxy(this.mContext, this.mBtProfileServiceListener, 5);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        BluetoothAdapter bluetoothAdapter;
        BluetoothProfile andSet = this.mBluetoothPan.getAndSet(null);
        if (andSet == null || (bluetoothAdapter = this.mBluetoothAdapter) == null) {
            return;
        }
        bluetoothAdapter.closeProfileProxy(5, andSet);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        TetherEnabler tetherEnabler = this.mTetherEnabler;
        if (tetherEnabler != null) {
            tetherEnabler.removeListener(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        TetherEnabler tetherEnabler = this.mTetherEnabler;
        if (tetherEnabler != null) {
            tetherEnabler.addListener(this);
        }
    }

    @Override // com.android.settings.network.TetherEnabler.OnTetherStateUpdateListener
    public void onTetherStateUpdated(int i) {
        this.mTetheringState = i;
        updateState(this.mPreference);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
