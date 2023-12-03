package com.android.settingslib.bluetooth;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.os.UserHandle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.util.LruCache;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.R$array;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.mediatek.bt.BluetoothLeAudioFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import miui.provider.ExtraContacts;
import miui.yellowpage.YellowPageContract;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class CachedBluetoothDevice implements Comparable<CachedBluetoothDevice> {
    private long mConnectAttempted;
    private final Context mContext;
    BluetoothDevice mDevice;
    private int mDeviceConnectionState;
    LruCache<String, BitmapDrawable> mDrawableCache;
    private long mHiSyncId;
    boolean mJustDiscovered;
    private boolean mLocalNapRoleConnected;
    private String mName;
    private final LocalBluetoothProfileManager mProfileManager;
    short mRssi;
    private CachedBluetoothDevice mSubDevice;
    public int mTwspBatteryLevel;
    public int mTwspBatteryState;
    private final Object mProfileLock = new Object();
    private final Collection<LocalBluetoothProfile> mProfiles = new CopyOnWriteArrayList();
    private final Collection<LocalBluetoothProfile> mRemovedProfiles = new CopyOnWriteArrayList();
    private final Collection<Callback> mCallbacks = new CopyOnWriteArrayList();
    private boolean mIsActiveDeviceA2dp = false;
    private boolean mIsActiveDeviceHeadset = false;
    private boolean mIsActiveDeviceHearingAid = false;
    private boolean mIsActiveDeviceLeAudio = false;
    private boolean mIsBrHasActiveLeAudio = false;
    private boolean mIsA2dpProfileConnectedFail = false;
    private boolean mIsHeadsetProfileConnectedFail = false;
    private boolean mIsHearingAidProfileConnectedFail = false;
    private boolean mIsLeAduioProfileConnectedFail = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settingslib.bluetooth.CachedBluetoothDevice.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                CachedBluetoothDevice.this.mIsHeadsetProfileConnectedFail = true;
            } else if (i == 2) {
                CachedBluetoothDevice.this.mIsA2dpProfileConnectedFail = true;
            } else if (i == 21) {
                CachedBluetoothDevice.this.mIsHearingAidProfileConnectedFail = true;
            } else if (i == BluetoothLeAudioFactory.getInstance().getLeAudioProfileId()) {
                CachedBluetoothDevice.this.mIsLeAduioProfileConnectedFail = true;
            } else {
                Log.w("CachedBluetoothDevice", "handleMessage(): unknown message : " + message.what);
            }
            Log.w("CachedBluetoothDevice", "Connect to profile : " + message.what + " timeout, show error message !");
            CachedBluetoothDevice.this.refresh();
        }
    };
    private final BluetoothAdapter mLocalAdapter = BluetoothAdapter.getDefaultAdapter();

    /* loaded from: classes2.dex */
    public interface Callback {
        void onDeviceAttributesChanged();
    }

    public CachedBluetoothDevice(Context context, LocalBluetoothProfileManager localBluetoothProfileManager, BluetoothDevice bluetoothDevice) {
        this.mContext = context;
        this.mProfileManager = localBluetoothProfileManager;
        this.mDevice = bluetoothDevice;
        fillData();
        this.mHiSyncId = 0L;
        initDrawableCache();
        this.mTwspBatteryState = -1;
        this.mTwspBatteryLevel = -1;
    }

    private void connectAllEnabledProfiles() {
        synchronized (this.mProfileLock) {
            Log.d("CachedBluetoothDevice", "connectAllEnabledProfiles()");
            if (!this.mProfiles.isEmpty()) {
                if (this.mDevice.isBondingInitiatedLocally()) {
                    Log.w("CachedBluetoothDevice", "reset BondingInitiatedLocally flag");
                }
                this.mLocalAdapter.connectAllEnabledProfiles(this.mDevice);
                return;
            }
            Log.d("CachedBluetoothDevice", "No profiles. Maybe we will connect later for device " + this.mDevice);
        }
    }

    private void connectAllLeAudioDevice() {
        String str;
        String str2;
        String findLeAddress = findLeAddress();
        if (findLeAddress == null || "".equals(findLeAddress)) {
            return;
        }
        String[] split = findLeAddress.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        if (split.length > 1) {
            str2 = split[0];
            str = split[1];
        } else {
            str = "";
            str2 = str;
        }
        if (this.mLocalAdapter == null || "".equals(str2) || "".equals(str)) {
            return;
        }
        BluetoothDevice remoteDevice = this.mLocalAdapter.getRemoteDevice(str2);
        BluetoothDevice remoteDevice2 = this.mLocalAdapter.getRemoteDevice(str);
        if (remoteDevice != null && !remoteDevice.isConnected()) {
            this.mLocalAdapter.connectAllEnabledProfiles(remoteDevice);
        }
        if (remoteDevice2 == null || remoteDevice2.isConnected()) {
            return;
        }
        this.mLocalAdapter.connectAllEnabledProfiles(remoteDevice2);
    }

    private String describe(LocalBluetoothProfile localBluetoothProfile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Address:");
        sb.append(this.mDevice);
        if (localBluetoothProfile != null) {
            sb.append(" Profile:");
            sb.append(localBluetoothProfile);
        }
        return sb.toString();
    }

    private void disconnectAllLeAudioDevice() {
        String str;
        String str2;
        String findLeAddress = findLeAddress();
        if (findLeAddress == null || "".equals(findLeAddress)) {
            return;
        }
        String[] split = findLeAddress.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
        if (split.length > 1) {
            str2 = split[0];
            str = split[1];
        } else {
            str = "";
            str2 = str;
        }
        if (this.mLocalAdapter == null || "".equals(str2) || "".equals(str)) {
            return;
        }
        BluetoothDevice remoteDevice = this.mLocalAdapter.getRemoteDevice(str2);
        BluetoothDevice remoteDevice2 = this.mLocalAdapter.getRemoteDevice(str);
        if (remoteDevice != null && remoteDevice.isConnected()) {
            this.mLocalAdapter.disconnectAllEnabledProfiles(remoteDevice);
        }
        if (remoteDevice2 == null || !remoteDevice2.isConnected()) {
            return;
        }
        this.mLocalAdapter.disconnectAllEnabledProfiles(remoteDevice2);
    }

    private boolean ensurePaired() {
        if (getBondState() == 10) {
            startPairing();
            return false;
        }
        return true;
    }

    private void fetchActiveDevices() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile != null) {
            this.mIsActiveDeviceA2dp = this.mDevice.equals(a2dpProfile.getActiveDevice());
        }
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null) {
            this.mIsActiveDeviceHeadset = this.mDevice.equals(headsetProfile.getActiveDevice());
        }
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null) {
            this.mIsActiveDeviceHearingAid = hearingAidProfile.getActiveDevices().contains(this.mDevice);
        }
        LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        if (leAudioProfile != null) {
            this.mIsActiveDeviceLeAudio = leAudioProfile.getActiveDevices().contains(this.mDevice);
        }
        Log.d("CachedBluetoothDevice", "fetchActiveDevices mIsActiveDeviceA2dp = " + this.mIsActiveDeviceA2dp + ",mIsActiveDeviceHeadset = " + this.mIsActiveDeviceHeadset + ",mIsActiveDeviceHearingAid = " + this.mIsActiveDeviceHearingAid + ",mIsActiveDeviceLeAudio" + this.mIsActiveDeviceLeAudio);
    }

    private void fillData() {
        updateProfiles();
        fetchActiveDevices();
        migratePhonebookPermissionChoice();
        migrateMessagePermissionChoice();
        dispatchAttributesChanged();
    }

    private SharedPreferences getSharedPreferences() {
        return this.mContext.getSharedPreferences("bluetooth_codec_suport_property", 0);
    }

    private boolean hasActiveLeAudioDevice() {
        String str;
        String str2;
        LeAudioProfile leAudioProfile;
        String findLeAddress = findLeAddress();
        if (findLeAddress != null && !"".equals(findLeAddress)) {
            String[] split = findLeAddress.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
            if (split.length > 1) {
                str2 = split[0];
                str = split[1];
            } else {
                str = "";
                str2 = str;
            }
            if (!"".equals(str2) && !"".equals(str) && (leAudioProfile = this.mProfileManager.getLeAudioProfile()) != null) {
                List<BluetoothDevice> activeDevices = leAudioProfile.getActiveDevices();
                Log.d("CachedBluetoothDevice", "getConnectionSummary,activeDevices.size() = " + activeDevices.size());
                for (int i = 0; i < activeDevices.size(); i++) {
                    String address = activeDevices.get(i).getAddress();
                    Log.d("CachedBluetoothDevice", "active Le Audio address = " + address);
                    if (str2.equals(address) || str.equals(address)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void initDrawableCache() {
        this.mDrawableCache = new LruCache<String, BitmapDrawable>(((int) (Runtime.getRuntime().maxMemory() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)) / 8) { // from class: com.android.settingslib.bluetooth.CachedBluetoothDevice.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.util.LruCache
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getBitmap().getByteCount() / MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE;
            }
        };
    }

    private void migrateMessagePermissionChoice() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("bluetooth_message_permission", 0);
        if (sharedPreferences.contains(this.mDevice.getAddress())) {
            if (this.mDevice.getMessageAccessPermission() == 0) {
                int i = sharedPreferences.getInt(this.mDevice.getAddress(), 0);
                if (i == 1) {
                    this.mDevice.setMessageAccessPermission(1);
                } else if (i == 2) {
                    this.mDevice.setMessageAccessPermission(2);
                }
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.remove(this.mDevice.getAddress());
            edit.commit();
        }
    }

    private void migratePhonebookPermissionChoice() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("bluetooth_phonebook_permission", 0);
        if (sharedPreferences.contains(this.mDevice.getAddress())) {
            if (this.mDevice.getPhonebookAccessPermission() == 0) {
                int i = sharedPreferences.getInt(this.mDevice.getAddress(), 0);
                if (i == 1) {
                    this.mDevice.setPhonebookAccessPermission(1);
                } else if (i == 2) {
                    this.mDevice.setPhonebookAccessPermission(2);
                }
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.remove(this.mDevice.getAddress());
            edit.commit();
        }
    }

    private void processPhonebookAccess() {
        if (this.mDevice.getBondState() == 12 && BluetoothUuid.containsAnyUuid(this.mDevice.getUuids(), PbapServerProfile.PBAB_CLIENT_UUIDS)) {
            BluetoothClass bluetoothClass = this.mDevice.getBluetoothClass();
            if (this.mDevice.getPhonebookAccessPermission() == 0) {
                if (bluetoothClass != null && (bluetoothClass.getDeviceClass() == 1032 || bluetoothClass.getDeviceClass() == 1028)) {
                    EventLog.writeEvent(1397638484, "138529441", -1, "");
                }
                this.mDevice.setPhonebookAccessPermission(2);
            }
        }
    }

    private void unpairLeAudio() {
        String str;
        String str2;
        String findLeAddress = findLeAddress();
        if (findLeAddress != null) {
            String[] split = findLeAddress.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
            if (split.length > 1) {
                str2 = split[0];
                str = split[1];
                Log.d("CachedBluetoothDevice", "unpairLeAudio,le1Str = " + str2 + ",le2Str = " + str);
                if (this.mLocalAdapter != null || "".equals(str2) || "".equals(str)) {
                    return;
                }
                BluetoothDevice remoteDevice = this.mLocalAdapter.getRemoteDevice(str2);
                BluetoothDevice remoteDevice2 = this.mLocalAdapter.getRemoteDevice(str);
                if (remoteDevice != null) {
                    int bondState = remoteDevice.getBondState();
                    if (bondState == 12) {
                        Log.d("CachedBluetoothDevice", "Remove bond to LE1");
                        remoteDevice.removeBond();
                    } else if (bondState == 11) {
                        Log.d("CachedBluetoothDevice", "Cancel bond to LE1");
                        remoteDevice.cancelBondProcess();
                    }
                }
                SystemClock.sleep(50L);
                if (remoteDevice2 != null) {
                    int bondState2 = remoteDevice2.getBondState();
                    if (bondState2 == 12) {
                        Log.d("CachedBluetoothDevice", "Remove bond to LE2");
                        remoteDevice2.removeBond();
                        return;
                    } else if (bondState2 == 11) {
                        Log.d("CachedBluetoothDevice", "Cancel bond to LE2");
                        remoteDevice2.cancelBondProcess();
                        return;
                    } else {
                        return;
                    }
                }
                return;
            }
        }
        str = "";
        str2 = str;
        Log.d("CachedBluetoothDevice", "unpairLeAudio,le1Str = " + str2 + ",le2Str = " + str);
        if (this.mLocalAdapter != null) {
        }
    }

    private boolean updateProfiles() {
        ParcelUuid[] uuids;
        ParcelUuid[] uuids2 = this.mDevice.getUuids();
        if (uuids2 == null || (uuids = this.mLocalAdapter.getUuids()) == null) {
            return false;
        }
        processPhonebookAccess();
        synchronized (this.mProfileLock) {
            this.mProfileManager.updateProfiles(uuids2, uuids, this.mProfiles, this.mRemovedProfiles, this.mLocalNapRoleConnected, this.mDevice);
        }
        Log.d("CachedBluetoothDevice", "updating profiles for " + this.mDevice.getAlias());
        BluetoothClass bluetoothClass = this.mDevice.getBluetoothClass();
        if (bluetoothClass != null) {
            Log.v("CachedBluetoothDevice", "Class: " + bluetoothClass.toString());
        }
        Log.v("CachedBluetoothDevice", "UUID:");
        for (ParcelUuid parcelUuid : uuids2) {
            Log.v("CachedBluetoothDevice", "  " + parcelUuid);
        }
        return true;
    }

    @Override // java.lang.Comparable
    public int compareTo(CachedBluetoothDevice cachedBluetoothDevice) {
        int i = (cachedBluetoothDevice.isConnected() ? 1 : 0) - (isConnected() ? 1 : 0);
        if (i != 0) {
            return i;
        }
        int i2 = (cachedBluetoothDevice.getBondState() == 12 ? 1 : 0) - (getBondState() != 12 ? 0 : 1);
        if (i2 != 0) {
            return i2;
        }
        int i3 = (cachedBluetoothDevice.mJustDiscovered ? 1 : 0) - (this.mJustDiscovered ? 1 : 0);
        if (i3 != 0) {
            return i3;
        }
        int i4 = cachedBluetoothDevice.mRssi - this.mRssi;
        return i4 != 0 ? i4 : getName().compareTo(cachedBluetoothDevice.getName());
    }

    public void connect() {
        if (ensurePaired()) {
            this.mConnectAttempted = SystemClock.elapsedRealtime();
            if (getLeAudioStatus() == 0) {
                connectAllEnabledProfiles();
            } else {
                connectAllLeAudioDevice();
            }
        }
    }

    @Deprecated
    public void connect(boolean z) {
        connect();
    }

    synchronized void connectInt(LocalBluetoothProfile localBluetoothProfile) {
        if (ensurePaired()) {
            if (localBluetoothProfile.setEnabled(this.mDevice, true)) {
                Log.d("CachedBluetoothDevice", "Command sent successfully:CONNECT " + describe(localBluetoothProfile));
                return;
            }
            Log.i("CachedBluetoothDevice", "Failed to connect " + localBluetoothProfile.toString() + " to " + getName());
        }
    }

    public void connectProfile(LocalBluetoothProfile localBluetoothProfile) {
        this.mConnectAttempted = SystemClock.elapsedRealtime();
        connectInt(localBluetoothProfile);
        refresh();
    }

    public void disconnect() {
        synchronized (this.mProfileLock) {
            this.mLocalAdapter.disconnectAllEnabledProfiles(this.mDevice);
        }
        if (hasConnectedLeAudioDevice()) {
            disconnectAllLeAudioDevice();
        }
        PbapServerProfile pbapProfile = this.mProfileManager.getPbapProfile();
        if (pbapProfile == null || !isConnectedProfile(pbapProfile)) {
            return;
        }
        pbapProfile.setEnabled(this.mDevice, false);
    }

    public void disconnect(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile.setEnabled(this.mDevice, false)) {
            Log.d("CachedBluetoothDevice", "Command sent successfully:DISCONNECT " + describe(localBluetoothProfile));
        }
    }

    void dispatchAttributesChanged() {
        for (Callback callback : this.mCallbacks) {
            Log.d("CachedBluetoothDevice", "dispatchAttributesChanged");
            callback.onDeviceAttributesChanged();
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CachedBluetoothDevice)) {
            return false;
        }
        return this.mDevice.equals(((CachedBluetoothDevice) obj).mDevice);
    }

    public String findBrAddress() {
        Log.d("CachedBluetoothDevice", "findBrAddress");
        return this.mDevice.findBrAddress();
    }

    public String findLeAddress() {
        Log.d("CachedBluetoothDevice", "findLeAddress");
        return this.mDevice.findLeAddress();
    }

    public String getAddress() {
        return this.mDevice.getAddress();
    }

    public int getBatteryLevel() {
        return this.mDevice.getBatteryLevel();
    }

    public int getBondState() {
        return this.mDevice.getBondState();
    }

    public BluetoothClass getBtClass() {
        return this.mDevice.getBluetoothClass();
    }

    public List<LocalBluetoothProfile> getConnectableProfiles() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mProfileLock) {
            for (LocalBluetoothProfile localBluetoothProfile : this.mProfiles) {
                if (localBluetoothProfile.accessProfileEnabled()) {
                    arrayList.add(localBluetoothProfile);
                }
            }
        }
        return arrayList;
    }

    public int getConnectionState() {
        return this.mDeviceConnectionState;
    }

    public String getConnectionSummary() {
        return getConnectionSummary(false);
    }

    public String getConnectionSummary(boolean z) {
        String str;
        synchronized (this.mProfileLock) {
            boolean z2 = false;
            boolean z3 = false;
            boolean z4 = false;
            boolean z5 = false;
            boolean z6 = false;
            for (LocalBluetoothProfile localBluetoothProfile : getProfiles()) {
                int profileConnectionState = getProfileConnectionState(localBluetoothProfile);
                if (profileConnectionState != 0) {
                    if (profileConnectionState == 1) {
                        this.mDeviceConnectionState = 2;
                    } else if (profileConnectionState == 2) {
                        this.mDeviceConnectionState = 1;
                        z2 = true;
                    } else if (profileConnectionState != 3) {
                    }
                    return this.mContext.getString(BluetoothUtils.getConnectionStateSummary(profileConnectionState));
                } else if (localBluetoothProfile.isProfileReady()) {
                    if (!(localBluetoothProfile instanceof A2dpProfile) && !(localBluetoothProfile instanceof A2dpSinkProfile)) {
                        if (!(localBluetoothProfile instanceof HeadsetProfile) && !(localBluetoothProfile instanceof HfpClientProfile)) {
                            if (localBluetoothProfile instanceof HearingAidProfile) {
                                z3 = true;
                            } else if (localBluetoothProfile instanceof HeadsetProfile) {
                                z6 = true;
                            }
                        }
                        z5 = true;
                    }
                    z4 = true;
                }
            }
            int batteryLevel = getBatteryLevel();
            String formatPercentage = batteryLevel > -1 ? Utils.formatPercentage(batteryLevel) : null;
            String[] stringArray = this.mContext.getResources().getStringArray(R$array.bluetooth_audio_active_device_summaries);
            String str2 = stringArray[0];
            boolean hasActiveLeAudioDevice = hasActiveLeAudioDevice();
            boolean hasConnectedLeAudioDevice = hasConnectedLeAudioDevice();
            if (!z2 && hasConnectedLeAudioDevice) {
                z6 = false;
                z2 = true;
            }
            Log.d("CachedBluetoothDevice", "getConnectionSummary() : mIsBrHasActiveLeAudio =" + hasActiveLeAudioDevice + "profileConnected =" + z2);
            boolean z7 = this.mIsActiveDeviceA2dp;
            if ((z7 && this.mIsActiveDeviceHeadset) || hasActiveLeAudioDevice) {
                str = stringArray[1];
            } else {
                if (z7) {
                    str2 = stringArray[2];
                }
                str = this.mIsActiveDeviceHeadset ? stringArray[3] : str2;
            }
            int bondState = getBondState();
            if (bondState == 12 && z2 && !z3 && this.mIsActiveDeviceHearingAid) {
                return this.mContext.getString(R$string.bluetooth_connected, stringArray[1]);
            } else if (z2) {
                this.mDeviceConnectionState = 1;
                return (z6 || !hasConnectedLeAudioDevice) ? (z4 && z5) ? formatPercentage != null ? this.mContext.getString(R$string.bluetooth_connected_no_headset_no_a2dp_battery_level, formatPercentage, str) : this.mContext.getString(R$string.bluetooth_connected_no_headset_no_a2dp, str) : z4 ? formatPercentage != null ? this.mContext.getString(R$string.bluetooth_connected_no_a2dp_battery_level, formatPercentage, str) : this.mContext.getString(R$string.bluetooth_connected_no_a2dp, str) : z5 ? formatPercentage != null ? this.mContext.getString(R$string.bluetooth_connected_no_headset_battery_level, formatPercentage, str) : this.mContext.getString(R$string.bluetooth_connected_no_headset, str) : formatPercentage != null ? this.mContext.getString(R$string.bluetooth_connected_battery_level, formatPercentage, str) : this.mContext.getString(R$string.bluetooth_connected, str) : formatPercentage != null ? this.mContext.getString(R$string.bluetooth_connected_battery_level, formatPercentage, str) : this.mContext.getString(R$string.bluetooth_connected, str);
            } else {
                this.mDeviceConnectionState = 0;
                if (bondState == 11) {
                    return this.mContext.getString(R$string.bluetooth_pairing);
                }
                if (bondState == 12) {
                    return this.mContext.getString(R$string.bluetooth_paired);
                }
                return null;
            }
        }
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public boolean getDialogChoice(String str) {
        return this.mContext.getSharedPreferences("bluetooth_dialog_remember_user_choice", 0).getBoolean(str, false);
    }

    public long getHiSyncId() {
        return this.mHiSyncId;
    }

    public int getLeAudioStatus() {
        int leAudioStatus = this.mDevice.getLeAudioStatus();
        Log.d("CachedBluetoothDevice", "getLeAudioStatus = " + leAudioStatus);
        return leAudioStatus;
    }

    public int getMessagePermissionChoice() {
        int messageAccessPermission = this.mDevice.getMessageAccessPermission();
        if (messageAccessPermission == 1) {
            return 1;
        }
        return messageAccessPermission == 2 ? 2 : 0;
    }

    public String getName() {
        String alias = this.mDevice.getAlias();
        if (TextUtils.isEmpty(alias)) {
            alias = this.mName;
        }
        return TextUtils.isEmpty(alias) ? getAddress() : alias;
    }

    public int getPhonebookPermissionChoice() {
        int phonebookAccessPermission = this.mDevice.getPhonebookAccessPermission();
        if (phonebookAccessPermission == 1) {
            return 1;
        }
        return phonebookAccessPermission == 2 ? 2 : 0;
    }

    public int getProfileConnectionState(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile != null) {
            return localBluetoothProfile.getConnectionStatus(this.mDevice);
        }
        return 0;
    }

    public List<LocalBluetoothProfile> getProfiles() {
        return new ArrayList(this.mProfiles);
    }

    public List<LocalBluetoothProfile> getRemovedProfiles() {
        return new ArrayList(this.mRemovedProfiles);
    }

    public short getRssi() {
        return this.mRssi;
    }

    public int getSpecificCodecStatus(String str) {
        return this.mDevice.getSpecificCodecStatus(str);
    }

    public CachedBluetoothDevice getSubDevice() {
        return this.mSubDevice;
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0045 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0046  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean hasConnectedLeAudioDevice() {
        /*
            r6 = this;
            boolean r0 = r6.isDualModeDevice()
            r1 = 0
            if (r0 != 0) goto L8
            return r1
        L8:
            java.lang.String r0 = r6.findLeAddress()
            r2 = 1
            java.lang.String r3 = ""
            if (r0 == 0) goto L1f
            java.lang.String r4 = ";"
            java.lang.String[] r0 = r0.split(r4)
            int r4 = r0.length
            if (r4 <= r2) goto L1f
            r4 = r0[r1]
            r0 = r0[r2]
            goto L21
        L1f:
            r0 = r3
            r4 = r0
        L21:
            android.bluetooth.BluetoothAdapter r5 = r6.mLocalAdapter
            if (r5 == 0) goto L59
            boolean r5 = r3.equals(r4)
            if (r5 != 0) goto L59
            boolean r3 = r3.equals(r0)
            if (r3 != 0) goto L59
            android.bluetooth.BluetoothAdapter r3 = r6.mLocalAdapter
            android.bluetooth.BluetoothDevice r3 = r3.getRemoteDevice(r4)
            android.bluetooth.BluetoothAdapter r4 = r6.mLocalAdapter
            android.bluetooth.BluetoothDevice r0 = r4.getRemoteDevice(r0)
            com.android.settingslib.bluetooth.LocalBluetoothProfileManager r6 = r6.mProfileManager
            com.android.settingslib.bluetooth.LeAudioProfile r6 = r6.getLeAudioProfile()
            if (r6 != 0) goto L46
            return r1
        L46:
            r4 = 2
            if (r3 == 0) goto L50
            int r3 = r6.getConnectionStatus(r3)
            if (r3 != r4) goto L50
            return r2
        L50:
            if (r0 == 0) goto L59
            int r6 = r6.getConnectionStatus(r0)
            if (r6 != r4) goto L59
            return r2
        L59:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.bluetooth.CachedBluetoothDevice.hasConnectedLeAudioDevice():boolean");
    }

    public boolean hasHumanReadableName() {
        String alias = this.mDevice.getAlias();
        if (TextUtils.isEmpty(alias)) {
            alias = this.mName;
        }
        return !TextUtils.isEmpty(alias);
    }

    public int hashCode() {
        return this.mDevice.getAddress().hashCode();
    }

    public boolean isActiveDevice(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 21) {
                    if (i == BluetoothLeAudioFactory.getInstance().getLeAudioProfileId()) {
                        if (hasActiveLeAudioDevice()) {
                            return true;
                        }
                        return this.mIsActiveDeviceLeAudio;
                    }
                    Log.w("CachedBluetoothDevice", "getActiveDevice: unknown profile " + i);
                    return false;
                }
                return this.mIsActiveDeviceHearingAid;
            }
            return this.mIsActiveDeviceA2dp;
        }
        return this.mIsActiveDeviceHeadset;
    }

    public boolean isAllProfileConnected() {
        synchronized (this.mProfileLock) {
            if (this.mProfiles.size() == 0) {
                Log.d("CachedBluetoothDevice", "profiles not update,return false");
                return false;
            }
            for (LocalBluetoothProfile localBluetoothProfile : this.mProfiles) {
                int profileConnectionState = getProfileConnectionState(localBluetoothProfile);
                Log.d("CachedBluetoothDevice", "profile = " + localBluetoothProfile + ",status = " + profileConnectionState);
                if (profileConnectionState != 2) {
                    return false;
                }
            }
            return true;
        }
    }

    public boolean isBusy() {
        int profileConnectionState;
        synchronized (this.mProfileLock) {
            Iterator<LocalBluetoothProfile> it = this.mProfiles.iterator();
            do {
                boolean z = true;
                if (!it.hasNext()) {
                    if (getBondState() != 11) {
                        z = false;
                    }
                    return z;
                }
                profileConnectionState = getProfileConnectionState(it.next());
                if (profileConnectionState == 1) {
                    break;
                }
            } while (profileConnectionState != 3);
            return true;
        }
    }

    public boolean isConnectableDevice() {
        return this.mDevice.isConnectableDevice();
    }

    public boolean isConnected() {
        synchronized (this.mProfileLock) {
            Iterator<LocalBluetoothProfile> it = this.mProfiles.iterator();
            while (it.hasNext()) {
                if (getProfileConnectionState(it.next()) == 2) {
                    return true;
                }
            }
            return hasConnectedLeAudioDevice();
        }
    }

    public boolean isConnectedA2dpDevice() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        return a2dpProfile != null && a2dpProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedHearingAidDevice() {
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        return hearingAidProfile != null && hearingAidProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedHfpDevice() {
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        return headsetProfile != null && headsetProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedLeAudioDevice() {
        LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        return leAudioProfile != null && leAudioProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedProfile(LocalBluetoothProfile localBluetoothProfile) {
        return getProfileConnectionState(localBluetoothProfile) == 2;
    }

    public boolean isDualModeDevice() {
        Log.d("CachedBluetoothDevice", "isDualModeDevice");
        return this.mDevice.isDualModeDevice();
    }

    public boolean isHearingAidDevice() {
        return this.mHiSyncId != 0;
    }

    public boolean isLeDevice() {
        return this.mDevice.isLeDevice();
    }

    public boolean isSupportedCodec(String str, String str2) {
        String string = getSharedPreferences().getString(str2, null);
        if (string != null) {
            try {
                JSONObject jSONObject = new JSONObject(string);
                if (jSONObject.has(str)) {
                    return jSONObject.getBoolean(str);
                }
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void onActiveDeviceChanged(boolean z, int i) {
        boolean z2;
        Log.d("CachedBluetoothDevice", "onActiveDeviceChanged = " + z + ",bluetoothProfile = " + i + ",address = " + this.mDevice.getAddress() + ",name = " + this.mDevice.getName());
        boolean z3 = false;
        if (i == 1) {
            z2 = this.mIsActiveDeviceHeadset != z;
            this.mIsActiveDeviceHeadset = z;
        } else if (i == 2) {
            z2 = this.mIsActiveDeviceA2dp != z;
            this.mIsActiveDeviceA2dp = z;
        } else if (i == 21) {
            z2 = this.mIsActiveDeviceHearingAid != z;
            this.mIsActiveDeviceHearingAid = z;
        } else if (i != BluetoothLeAudioFactory.getInstance().getLeAudioProfileId()) {
            Log.w("CachedBluetoothDevice", "onActiveDeviceChanged: unknown profile " + i + " isActive " + z);
            if (!z3 || i == 22) {
                Log.d("CachedBluetoothDevice", "dispatchAttributesChanged");
                dispatchAttributesChanged();
            }
            return;
        } else {
            z2 = this.mIsActiveDeviceLeAudio != z;
            this.mIsActiveDeviceLeAudio = z;
        }
        z3 = z2;
        if (z3) {
        }
        Log.d("CachedBluetoothDevice", "dispatchAttributesChanged");
        dispatchAttributesChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAudioModeChanged() {
        dispatchAttributesChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBondingStateChanged(int i) {
        Log.d("CachedBluetoothDevice", "onBondingStateChanged bondState = " + i);
        if (i == 10) {
            synchronized (this.mProfileLock) {
                this.mProfiles.clear();
            }
            this.mDevice.setPhonebookAccessPermission(0);
            this.mDevice.setMessageAccessPermission(0);
            this.mDevice.setSimAccessPermission(0);
        }
        refresh();
        if (ActivityManager.getCurrentUser() == UserHandle.getCallingUserId() && i == 12) {
            boolean isBondingInitiatedLocally = this.mDevice.isBondingInitiatedLocally();
            Log.w("CachedBluetoothDevice", "mIsBondingInitiatedLocally" + isBondingInitiatedLocally);
            if (isBondingInitiatedLocally) {
                connect();
            }
        }
    }

    public void onProfileStateChanged(LocalBluetoothProfile localBluetoothProfile, int i) {
        Log.d("CachedBluetoothDevice", "onProfileStateChanged: profile " + localBluetoothProfile + ", device " + this.mDevice.getAlias() + ", newProfileState " + i);
        if (this.mLocalAdapter.getState() == 13) {
            Log.d("CachedBluetoothDevice", " BT Turninig Off...Profile conn state change ignored...");
            return;
        }
        synchronized (this.mProfileLock) {
            if ((localBluetoothProfile instanceof A2dpProfile) || (localBluetoothProfile instanceof HeadsetProfile) || (localBluetoothProfile instanceof HearingAidProfile) || (localBluetoothProfile instanceof LeAudioProfile)) {
                setProfileConnectedStatus(localBluetoothProfile.getProfileId(), false);
                if (i != 0) {
                    if (i == 1) {
                        this.mHandler.sendEmptyMessageDelayed(localBluetoothProfile.getProfileId(), 60000L);
                    } else if (i == 2) {
                        this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    } else if (i != 3) {
                        Log.w("CachedBluetoothDevice", "onProfileStateChanged(): unknown profile state : " + i);
                    } else if (this.mHandler.hasMessages(localBluetoothProfile.getProfileId())) {
                        this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    }
                } else if (this.mHandler.hasMessages(localBluetoothProfile.getProfileId())) {
                    this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    setProfileConnectedStatus(localBluetoothProfile.getProfileId(), true);
                }
            }
            if (i == 2) {
                if (localBluetoothProfile instanceof MapProfile) {
                    localBluetoothProfile.setEnabled(this.mDevice, true);
                }
                if (!this.mProfiles.contains(localBluetoothProfile)) {
                    this.mRemovedProfiles.remove(localBluetoothProfile);
                    this.mProfiles.add(localBluetoothProfile);
                    if ((localBluetoothProfile instanceof PanProfile) && ((PanProfile) localBluetoothProfile).isLocalRoleNap(this.mDevice)) {
                        this.mLocalNapRoleConnected = true;
                    }
                }
            } else if ((localBluetoothProfile instanceof MapProfile) && i == 0) {
                localBluetoothProfile.setEnabled(this.mDevice, false);
            } else if (this.mLocalNapRoleConnected && (localBluetoothProfile instanceof PanProfile) && ((PanProfile) localBluetoothProfile).isLocalRoleNap(this.mDevice) && i == 0) {
                Log.d("CachedBluetoothDevice", "Removing PanProfile from device after NAP disconnect");
                this.mProfiles.remove(localBluetoothProfile);
                this.mRemovedProfiles.add(localBluetoothProfile);
                this.mLocalNapRoleConnected = false;
            } else if ((localBluetoothProfile instanceof HeadsetProfile) && i == 0) {
                this.mTwspBatteryState = -1;
                this.mTwspBatteryLevel = -1;
            }
        }
        fetchActiveDevices();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onUuidChanged() {
        updateProfiles();
        ParcelUuid[] uuids = this.mDevice.getUuids();
        long j = ArrayUtils.contains(uuids, BluetoothUuid.HOGP) ? 30000L : ArrayUtils.contains(uuids, BluetoothUuid.HEARING_AID) ? 15000L : ArrayUtils.contains(uuids, BluetoothLeAudioFactory.getInstance().getLeAudioUuid()) ? 10000L : 20000L;
        Log.d("CachedBluetoothDevice", "onUuidChanged: Time since last connect=" + (SystemClock.elapsedRealtime() - this.mConnectAttempted));
        if (!this.mProfiles.isEmpty() && this.mConnectAttempted + j > SystemClock.elapsedRealtime()) {
            Log.d("CachedBluetoothDevice", "onUuidChanged: triggering connectAllEnabledProfiles");
            String currentPackageName = ActivityThread.currentPackageName();
            Log.d("CachedBluetoothDevice", "callerPackageName = :" + currentPackageName);
            if (currentPackageName != null && currentPackageName.contains(YellowPageContract.Settings.DIRECTORY)) {
                Log.d("CachedBluetoothDevice", "onUuidChanged() in setting ui process ,connect all profile");
                if (isLeDevice()) {
                    if (this.mLocalAdapter.getRemoteDevice(this.mDevice.findBrAddress()).getLeAudioStatus() == 0) {
                        Log.d("CachedBluetoothDevice", "remove bond le device to keep acl link disconnect");
                        this.mDevice.removeBond();
                        dispatchAttributesChanged();
                        return;
                    }
                } else if (this.mDevice.getLeAudioStatus() == 1) {
                    dispatchAttributesChanged();
                    return;
                }
                connectAllEnabledProfiles();
            }
        }
        dispatchAttributesChanged();
    }

    public void refresh() {
        Log.d("CachedBluetoothDevice", "device: " + getName() + " refresh()");
        dispatchAttributesChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refreshName() {
        Log.d("CachedBluetoothDevice", "Device name: " + getName());
        dispatchAttributesChanged();
    }

    public void registerCallback(Callback callback) {
        this.mCallbacks.add(callback);
    }

    void releaseLruCache() {
        this.mDrawableCache.evictAll();
    }

    public boolean setActive() {
        boolean z;
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile != null && isConnectedProfile(a2dpProfile) && a2dpProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: A2DP active device=" + this);
            z = true;
        } else {
            z = false;
        }
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null && isConnectedProfile(headsetProfile) && this.mDevice.getType() != 2 && headsetProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: Headset active device=" + this);
            z = true;
        }
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null && isConnectedProfile(hearingAidProfile) && hearingAidProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: Hearing Aid active device=" + this);
            z = true;
        }
        LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        if (leAudioProfile != null && isConnectedProfile(leAudioProfile) && leAudioProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: LeAudio active device=" + this);
            return true;
        }
        return z;
    }

    public void setDialogChoice(String str, int i) {
        SharedPreferences.Editor edit = this.mContext.getSharedPreferences("bluetooth_dialog_remember_user_choice", 0).edit();
        if (i == 2) {
            edit.remove(str);
        } else {
            edit.putBoolean(str, i == 1);
        }
        edit.commit();
    }

    public void setHiSyncId(long j) {
        this.mHiSyncId = j;
    }

    public void setJustDiscovered(boolean z) {
        if (this.mJustDiscovered != z) {
            this.mJustDiscovered = z;
        }
    }

    public void setLeAudioStatus(int i) {
        Log.d("CachedBluetoothDevice", "setLeAudioStatus,value = " + i);
        this.mDevice.setLeAudioStatus(i);
    }

    public void setMessagePermissionChoice(int i) {
        int i2 = 2;
        if (i == 1) {
            i2 = 1;
        } else if (i != 2) {
            i2 = 0;
        }
        this.mDevice.setMessageAccessPermission(i2);
    }

    public void setName(String str) {
        if (str == null || TextUtils.equals(str, getName())) {
            return;
        }
        this.mName = str;
        this.mDevice.setAlias(str);
        dispatchAttributesChanged();
    }

    public void setPhonebookPermissionChoice(int i) {
        int i2 = 2;
        if (i == 1) {
            i2 = 1;
        } else if (i != 2) {
            i2 = 0;
        }
        this.mDevice.setPhonebookAccessPermission(i2);
    }

    void setProfileConnectedStatus(int i, boolean z) {
        if (i == 1) {
            this.mIsHeadsetProfileConnectedFail = z;
        } else if (i == 2) {
            this.mIsA2dpProfileConnectedFail = z;
        } else if (i == 21) {
            this.mIsHearingAidProfileConnectedFail = z;
        } else if (i == BluetoothLeAudioFactory.getInstance().getLeAudioProfileId()) {
            this.mIsLeAduioProfileConnectedFail = z;
        } else {
            Log.w("CachedBluetoothDevice", "setProfileConnectedStatus(): unknown profile id : " + i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRssi(short s) {
        if (this.mRssi != s) {
            this.mRssi = s;
            dispatchAttributesChanged();
        }
    }

    public void setSpecificCodecStatus(String str, int i) {
        this.mDevice.setSpecificCodecStatus(str, i);
    }

    public void setSubDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mSubDevice = cachedBluetoothDevice;
    }

    public void setSupportedCodec(String str, String str2, boolean z) {
        SharedPreferences.Editor edit = getSharedPreferences().edit();
        String string = getSharedPreferences().getString(str2, null);
        JSONObject jSONObject = new JSONObject();
        if (string != null) {
            try {
                jSONObject = new JSONObject(string);
            } catch (JSONException e) {
                e.printStackTrace();
                edit.apply();
            }
        }
        if (z) {
            jSONObject.put(str, z);
        } else if (jSONObject.has(str)) {
            jSONObject.remove(str);
        }
        edit.putString(str2, jSONObject.toString());
        edit.apply();
    }

    public boolean startPairing() {
        if (this.mLocalAdapter.isDiscovering()) {
            this.mLocalAdapter.cancelDiscovery();
        }
        return this.mDevice.createBond();
    }

    public void switchSubDeviceContent() {
        BluetoothDevice bluetoothDevice = this.mDevice;
        short s = this.mRssi;
        boolean z = this.mJustDiscovered;
        CachedBluetoothDevice cachedBluetoothDevice = this.mSubDevice;
        this.mDevice = cachedBluetoothDevice.mDevice;
        this.mRssi = cachedBluetoothDevice.mRssi;
        this.mJustDiscovered = cachedBluetoothDevice.mJustDiscovered;
        cachedBluetoothDevice.mDevice = bluetoothDevice;
        cachedBluetoothDevice.mRssi = s;
        cachedBluetoothDevice.mJustDiscovered = z;
        fetchActiveDevices();
    }

    public String toString() {
        return this.mDevice.toString();
    }

    public void unpair() {
        BluetoothDevice bluetoothDevice;
        int bondState = getBondState();
        if (bondState == 11) {
            this.mDevice.cancelBondProcess();
        }
        if (bondState != 10 && (bluetoothDevice = this.mDevice) != null) {
            if (bluetoothDevice.removeBond()) {
                releaseLruCache();
                Log.d("CachedBluetoothDevice", "Command sent successfully:REMOVE_BOND " + describe(null));
            } else {
                Log.v("CachedBluetoothDevice", "Framework rejected command immediately:REMOVE_BOND " + describe(null));
            }
        }
        if (getAddress().equals(findBrAddress())) {
            unpairLeAudio();
        }
    }

    public void unregisterCallback(Callback callback) {
        this.mCallbacks.remove(callback);
    }
}
