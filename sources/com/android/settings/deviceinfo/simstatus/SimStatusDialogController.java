package com.android.settings.deviceinfo.simstatus;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.CellSignalStrength;
import android.telephony.ICellBroadcastService;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.euicc.EuiccManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsReasonInfo;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.R;
import com.android.settings.deviceinfo.FiveGController;
import com.android.settings.deviceinfo.TelephonyUtils;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public class SimStatusDialogController implements LifecycleObserver {
    static final int MAX_PHONE_COUNT_SINGLE_SIM = 1;
    private final CarrierConfigManager mCarrierConfigManager;
    private CellBroadcastServiceConnection mCellBroadcastServiceConnection;
    private final Context mContext;
    private final SimStatusDialogFragment mDialog;
    private final EuiccManager mEuiccManager;
    private FiveGController mFiveGController;
    private boolean mIsShow5GNsaType;
    private boolean mIsShow5GSaType;
    private ServiceState mPreviousServiceState;
    private final Resources mRes;
    private boolean mShowLatestAreaInfo;
    private int mSlotId;
    private final int mSlotIndex;
    private SubscriptionInfo mSubscriptionInfo;
    private final SubscriptionManager mSubscriptionManager;
    protected SimStatusDialogTelephonyCallback mTelephonyCallback;
    private TelephonyDisplayInfo mTelephonyDisplayInfo;
    private TelephonyManager mTelephonyManager;
    static final int NETWORK_PROVIDER_VALUE_ID = R.id.operator_name_value;
    static final int PHONE_NUMBER_VALUE_ID = R.id.number_value;
    static final int CELLULAR_NETWORK_STATE = R.id.data_state_value;
    static final int OPERATOR_INFO_LABEL_ID = R.id.latest_area_info_label;
    static final int OPERATOR_INFO_VALUE_ID = R.id.latest_area_info_value;
    static final int SERVICE_STATE_VALUE_ID = R.id.service_state_value;
    static final int SIGNAL_STRENGTH_LABEL_ID = R.id.signal_strength_label;
    static final int SIGNAL_STRENGTH_VALUE_ID = R.id.signal_strength_value;
    static final int CELL_VOICE_NETWORK_TYPE_VALUE_ID = R.id.voice_network_type_value;
    static final int CELL_DATA_NETWORK_TYPE_VALUE_ID = R.id.data_network_type_value;
    static final int ROAMING_INFO_VALUE_ID = R.id.roaming_state_value;
    static final int ICCID_INFO_LABEL_ID = R.id.icc_id_label;
    static final int ICCID_INFO_VALUE_ID = R.id.icc_id_value;
    static final int EID_INFO_LABEL_ID = R.id.esim_id_label;
    static final int EID_INFO_VALUE_ID = R.id.esim_id_value;
    static final int IMS_REGISTRATION_STATE_LABEL_ID = R.id.ims_reg_state_label;
    static final int IMS_REGISTRATION_STATE_VALUE_ID = R.id.ims_reg_state_value;
    private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.1
        @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            int subscriptionId = SimStatusDialogController.this.mSubscriptionInfo != null ? SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId() : -1;
            SimStatusDialogController simStatusDialogController = SimStatusDialogController.this;
            simStatusDialogController.mSubscriptionInfo = simStatusDialogController.getPhoneSubscriptionInfo(simStatusDialogController.mSlotIndex);
            int subscriptionId2 = SimStatusDialogController.this.mSubscriptionInfo != null ? SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId() : -1;
            if (subscriptionId != subscriptionId2) {
                if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
                    SimStatusDialogController.this.unregisterImsRegistrationCallback(subscriptionId);
                }
                if (SubscriptionManager.isValidSubscriptionId(subscriptionId2)) {
                    SimStatusDialogController simStatusDialogController2 = SimStatusDialogController.this;
                    simStatusDialogController2.mTelephonyManager = simStatusDialogController2.mTelephonyManager.createForSubscriptionId(subscriptionId2);
                    SimStatusDialogController.this.registerImsRegistrationCallback(subscriptionId2);
                }
            }
            SimStatusDialogController.this.updateSubscriptionStatus();
        }
    };
    private boolean mIsRegisteredListener = false;
    private final BroadcastReceiver mAreaInfoReceiver = new BroadcastReceiver() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.telephony.action.AREA_INFO_UPDATED".equals(intent.getAction()) && intent.getIntExtra("android.telephony.extra.SLOT_INDEX", 0) == SimStatusDialogController.this.mSlotIndex) {
                SimStatusDialogController.this.updateAreaInfoText();
            }
        }
    };
    ContentObserver m5GNetworkTypeObserver = new ContentObserver(null) { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.3
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            if (miui.telephony.SubscriptionManager.isValidSlotId(SimStatusDialogController.this.mSlotId)) {
                SimStatusDialogController simStatusDialogController = SimStatusDialogController.this;
                ContentResolver contentResolver = simStatusDialogController.mContext.getContentResolver();
                StringBuilder sb = new StringBuilder();
                sb.append("5g_icon_group_mode");
                sb.append(SimStatusDialogController.this.mSlotId);
                simStatusDialogController.mIsShow5GNsaType = Settings.Global.getInt(contentResolver, sb.toString(), 0) == 1;
                SimStatusDialogController simStatusDialogController2 = SimStatusDialogController.this;
                ContentResolver contentResolver2 = simStatusDialogController2.mContext.getContentResolver();
                StringBuilder sb2 = new StringBuilder();
                sb2.append("5g_icon_group_mode");
                sb2.append(SimStatusDialogController.this.mSlotId);
                simStatusDialogController2.mIsShow5GSaType = Settings.Global.getInt(contentResolver2, sb2.toString(), 0) == 2;
                SimStatusDialogController.this.updateNetworkType();
                Log.d("SimStatusDialogCtrl", "onChange mIsShow5GNsaType:" + SimStatusDialogController.this.mIsShow5GNsaType + ", mIsShow5GSaType:" + SimStatusDialogController.this.mIsShow5GSaType + ", mSlotId:" + SimStatusDialogController.this.mSlotId);
            }
        }
    };
    private ImsMmTelManager.RegistrationCallback mImsRegStateCallback = new ImsMmTelManager.RegistrationCallback() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.4
        public void onRegistered(int i) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(R.string.ims_reg_status_registered));
        }

        public void onRegistering(int i) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(R.string.ims_reg_status_not_registered));
        }

        public void onTechnologyChangeFailed(int i, ImsReasonInfo imsReasonInfo) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(R.string.ims_reg_status_not_registered));
        }

        public void onUnregistered(ImsReasonInfo imsReasonInfo) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(R.string.ims_reg_status_not_registered));
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CellBroadcastServiceConnection implements ServiceConnection {
        private IBinder mService;

        private CellBroadcastServiceConnection() {
        }

        public IBinder getService() {
            return this.mService;
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "Binding died");
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "Null binding");
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("SimStatusDialogCtrl", "connected to CellBroadcastService");
            this.mService = iBinder;
            SimStatusDialogController.this.updateAreaInfoText();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "mICellBroadcastService has disconnected unexpectedly");
        }
    }

    /* loaded from: classes.dex */
    class SimStatusDialogTelephonyCallback extends TelephonyCallback implements TelephonyCallback.DataConnectionStateListener, TelephonyCallback.SignalStrengthsListener, TelephonyCallback.ServiceStateListener, TelephonyCallback.DisplayInfoListener {
        SimStatusDialogTelephonyCallback() {
        }

        @Override // android.telephony.TelephonyCallback.DataConnectionStateListener
        public void onDataConnectionStateChanged(int i, int i2) {
            SimStatusDialogController.this.updateDataState(i);
            SimStatusDialogController.this.updateNetworkType();
        }

        @Override // android.telephony.TelephonyCallback.DisplayInfoListener
        public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {
            SimStatusDialogController.this.mTelephonyDisplayInfo = telephonyDisplayInfo;
            SimStatusDialogController.this.updateNetworkType();
        }

        @Override // android.telephony.TelephonyCallback.ServiceStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            SimStatusDialogController.this.updateNetworkProvider();
            SimStatusDialogController.this.updateServiceState(serviceState);
            SimStatusDialogController.this.updateRoamingStatus(serviceState);
            SimStatusDialogController.this.mPreviousServiceState = serviceState;
            SimStatusDialogController.this.updateNetworkType();
        }

        @Override // android.telephony.TelephonyCallback.SignalStrengthsListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            SimStatusDialogController.this.updateSignalStrength(signalStrength);
        }
    }

    public SimStatusDialogController(SimStatusDialogFragment simStatusDialogFragment, Lifecycle lifecycle, int i) {
        this.mDialog = simStatusDialogFragment;
        Context context = simStatusDialogFragment.getContext();
        this.mContext = context;
        this.mSlotIndex = i;
        this.mSubscriptionInfo = getPhoneSubscriptionInfo(i);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mEuiccManager = (EuiccManager) context.getSystemService(EuiccManager.class);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mRes = context.getResources();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        TelephonyUtils.initDataTypeName(context);
        TelephonyUtils.initDataTypeName(context);
        this.mSlotId = i;
        this.mFiveGController = FiveGController.getInstance(context);
        init5GNetworkType();
    }

    private void bindCellBroadcastService() {
        this.mCellBroadcastServiceConnection = new CellBroadcastServiceConnection();
        Intent intent = new Intent("android.telephony.CellBroadcastService");
        String cellBroadcastServicePackage = getCellBroadcastServicePackage();
        if (TextUtils.isEmpty(cellBroadcastServicePackage)) {
            return;
        }
        intent.setPackage(cellBroadcastServicePackage);
        CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
        if (cellBroadcastServiceConnection == null || cellBroadcastServiceConnection.getService() != null) {
            Log.d("SimStatusDialogCtrl", "skipping bindService because connection already exists");
        } else if (this.mContext.bindService(intent, this.mCellBroadcastServiceConnection, 1)) {
        } else {
            Log.e("SimStatusDialogCtrl", "Unable to bind to service");
        }
    }

    private int getAsuLevel(SignalStrength signalStrength) {
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        if (cellSignalStrengths == null) {
            return -1;
        }
        for (CellSignalStrength cellSignalStrength : cellSignalStrengths) {
            if (cellSignalStrength.getAsuLevel() != -1) {
                return cellSignalStrength.getAsuLevel();
            }
        }
        return -1;
    }

    private String getCellBroadcastServicePackage() {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(new Intent("android.telephony.CellBroadcastService"), 1048576);
        if (queryIntentServices.size() != 1) {
            Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: found " + queryIntentServices.size() + " CBS packages");
        }
        Iterator<ResolveInfo> it = queryIntentServices.iterator();
        while (it.hasNext()) {
            ServiceInfo serviceInfo = it.next().serviceInfo;
            if (serviceInfo != null) {
                String str = serviceInfo.packageName;
                if (TextUtils.isEmpty(str)) {
                    Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: found a CBS package but packageName is null/empty");
                } else if (packageManager.checkPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", str) == 0) {
                    Log.d("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: " + str);
                    return str;
                } else {
                    Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: " + str + " does not have READ_PRIVILEGED_PHONE_STATE permission");
                }
            }
        }
        Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: package name not found");
        return null;
    }

    private int getDbm(SignalStrength signalStrength) {
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        if (cellSignalStrengths == null) {
            return -1;
        }
        for (CellSignalStrength cellSignalStrength : cellSignalStrengths) {
            if (cellSignalStrength.getDbm() != -1) {
                return cellSignalStrength.getDbm();
            }
        }
        return -1;
    }

    static String getNetworkTypeName(int i) {
        switch (i) {
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA";
            case 5:
                return "CDMA - EvDo rev. 0";
            case 6:
                return "CDMA - EvDo rev. A";
            case 7:
                return "CDMA - 1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDEN";
            case 12:
                return "CDMA - EvDo rev. B";
            case 13:
                return "LTE";
            case 14:
                return "CDMA - eHRPD";
            case 15:
                return "HSPA+";
            case 16:
                return "GSM";
            case 17:
                return "TD_SCDMA";
            case 18:
                return "IWLAN";
            case 19:
            default:
                return "UNKNOWN";
            case 20:
                return "NR SA";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubscriptionInfo getPhoneSubscriptionInfo(int i) {
        return SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoForSimSlotIndex(i);
    }

    private void init5GNetworkType() {
        if (miui.telephony.SubscriptionManager.isValidSlotId(this.mSlotId)) {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            StringBuilder sb = new StringBuilder();
            sb.append("5g_icon_group_mode");
            sb.append(this.mSlotId);
            this.mIsShow5GNsaType = Settings.Global.getInt(contentResolver, sb.toString(), 0) == 1;
            ContentResolver contentResolver2 = this.mContext.getContentResolver();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("5g_icon_group_mode");
            sb2.append(this.mSlotId);
            this.mIsShow5GSaType = Settings.Global.getInt(contentResolver2, sb2.toString(), 0) == 2;
            Log.d("SimStatusDialogCtrl", "init5GNetworkType mIsShow5GNsaType:" + this.mIsShow5GNsaType + ",mSlotId:" + this.mSlotId);
        }
    }

    private boolean isImsRegistrationStateShowUp() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null) {
            return false;
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionInfo.getSubscriptionId());
        if (configForSubId == null) {
            return false;
        }
        return configForSubId.getBoolean("show_ims_registration_status_bool");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestForUpdateEid$0() {
        updateEid(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestForUpdateEid$1() {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SimStatusDialogController.this.lambda$requestForUpdateEid$0();
            }
        });
    }

    private void register5GNetworkTypeObserver() {
        if (miui.telephony.SubscriptionManager.isValidSlotId(this.mSlotId)) {
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("5g_icon_group_mode" + this.mSlotId), false, this.m5GNetworkTypeObserver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerImsRegistrationCallback(int i) {
        if (isImsRegistrationStateShowUp()) {
            try {
                ImsMmTelManager.createForSubscriptionId(i).registerImsRegistrationCallback(this.mDialog.getContext().getMainExecutor(), this.mImsRegStateCallback);
            } catch (ImsException e) {
                Log.w("SimStatusDialogCtrl", "fail to register IMS status for subId=" + i, e);
            }
        }
    }

    private void resetSignalStrength() {
        this.mDialog.setText(SIGNAL_STRENGTH_VALUE_ID, "0");
    }

    private void unregister5GNetworkTypeObserver() {
        if (this.m5GNetworkTypeObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.m5GNetworkTypeObserver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterImsRegistrationCallback(int i) {
        if (isImsRegistrationStateShowUp()) {
            ImsMmTelManager.createForSubscriptionId(i).unregisterImsRegistrationCallback(this.mImsRegStateCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAreaInfoText() {
        CellBroadcastServiceConnection cellBroadcastServiceConnection;
        ICellBroadcastService asInterface;
        if (!this.mShowLatestAreaInfo || (cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection) == null || (asInterface = ICellBroadcastService.Stub.asInterface(cellBroadcastServiceConnection.getService())) == null) {
            return;
        }
        try {
            this.mDialog.setText(OPERATOR_INFO_VALUE_ID, asInterface.getCellBroadcastAreaInfo(this.mSlotIndex));
        } catch (RemoteException e) {
            Log.d("SimStatusDialogCtrl", "Can't get area info. e=" + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDataState(int i) {
        this.mDialog.setText(CELLULAR_NETWORK_STATE, i != 0 ? i != 1 ? i != 2 ? i != 3 ? this.mRes.getString(R.string.radioInfo_unknown) : this.mRes.getString(R.string.radioInfo_data_suspended) : this.mRes.getString(R.string.radioInfo_data_connected) : this.mRes.getString(R.string.radioInfo_data_connecting) : this.mRes.getString(R.string.radioInfo_data_disconnected));
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x002a  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x001b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateIccidNumber() {
        /*
            r2 = this;
            android.telephony.SubscriptionInfo r0 = r2.mSubscriptionInfo
            if (r0 == 0) goto L18
            int r0 = r0.getSubscriptionId()
            android.telephony.CarrierConfigManager r1 = r2.mCarrierConfigManager
            android.os.PersistableBundle r0 = r1.getConfigForSubId(r0)
            if (r0 == 0) goto L18
            java.lang.String r1 = "show_iccid_in_sim_status_bool"
            boolean r0 = r0.getBoolean(r1)
            goto L19
        L18:
            r0 = 0
        L19:
            if (r0 != 0) goto L2a
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r0 = r2.mDialog
            int r1 = com.android.settings.deviceinfo.simstatus.SimStatusDialogController.ICCID_INFO_LABEL_ID
            r0.removeSettingFromScreen(r1)
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r2 = r2.mDialog
            int r0 = com.android.settings.deviceinfo.simstatus.SimStatusDialogController.ICCID_INFO_VALUE_ID
            r2.removeSettingFromScreen(r0)
            goto L37
        L2a:
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r0 = r2.mDialog
            int r1 = com.android.settings.deviceinfo.simstatus.SimStatusDialogController.ICCID_INFO_VALUE_ID
            android.telephony.TelephonyManager r2 = r2.mTelephonyManager
            java.lang.String r2 = r2.getSimSerialNumber()
            r0.setText(r1, r2)
        L37:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.updateIccidNumber():void");
    }

    private void updateImsRegistrationState() {
        if (isImsRegistrationStateShowUp()) {
            return;
        }
        this.mDialog.removeSettingFromScreen(IMS_REGISTRATION_STATE_LABEL_ID);
        this.mDialog.removeSettingFromScreen(IMS_REGISTRATION_STATE_VALUE_ID);
    }

    private void updateLatestAreaInfo() {
        boolean z = Resources.getSystem().getBoolean(17891629) && this.mTelephonyManager.getPhoneType() != 2;
        this.mShowLatestAreaInfo = z;
        if (z) {
            bindCellBroadcastService();
            return;
        }
        this.mDialog.removeSettingFromScreen(OPERATOR_INFO_LABEL_ID);
        this.mDialog.removeSettingFromScreen(OPERATOR_INFO_VALUE_ID);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkProvider() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        this.mDialog.setText(NETWORK_PROVIDER_VALUE_ID, subscriptionInfo != null ? subscriptionInfo.getCarrierName() : null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkType() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null) {
            String networkTypeName = getNetworkTypeName(0);
            this.mDialog.setText(CELL_VOICE_NETWORK_TYPE_VALUE_ID, networkTypeName);
            this.mDialog.setText(CELL_DATA_NETWORK_TYPE_VALUE_ID, networkTypeName);
            return;
        }
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        int dataNetworkType = this.mTelephonyManager.getDataNetworkType(subscriptionId);
        int voiceNetworkType = this.mTelephonyManager.getVoiceNetworkType(subscriptionId);
        int dataNetTypeFromServiceState = TelephonyUtils.getDataNetTypeFromServiceState(dataNetworkType, getCurrentServiceState());
        if (voiceNetworkType == 0) {
            int combinedServiceState = Utils.getCombinedServiceState(getCurrentServiceState());
            Log.i("SimStatusDialogCtrl", "updateNetworkType state = " + combinedServiceState);
            if (combinedServiceState == 0) {
                voiceNetworkType = dataNetTypeFromServiceState;
            }
            Log.i("SimStatusDialogCtrl", "updateNetworkType actualVoiceNetworkType = " + voiceNetworkType);
        }
        TelephonyDisplayInfo telephonyDisplayInfo = this.mTelephonyDisplayInfo;
        int overrideNetworkType = telephonyDisplayInfo == null ? 0 : telephonyDisplayInfo.getOverrideNetworkType();
        String networkTypeName2 = dataNetTypeFromServiceState != 0 ? getNetworkTypeName(dataNetTypeFromServiceState) : null;
        String networkTypeName3 = voiceNetworkType != 0 ? getNetworkTypeName(voiceNetworkType) : null;
        boolean z = true;
        boolean z2 = overrideNetworkType == 5 || overrideNetworkType == 3;
        if (dataNetTypeFromServiceState == 13 && z2) {
            networkTypeName2 = "NR NSA";
        }
        TelephonyUtils.updateDataTypeMcc(this.mContext, this.mTelephonyManager.getSimOperatorNumericForPhone(subscriptionId));
        TelephonyUtils.updateDataTypeMccMnc(this.mContext, this.mTelephonyManager.getSimOperatorNumericForPhone(subscriptionId));
        if (dataNetTypeFromServiceState == 13 || dataNetTypeFromServiceState == 19) {
            networkTypeName2 = TelephonyUtils.getNetworkTypeName(this.mContext, this.mSlotId, dataNetTypeFromServiceState, this.mIsShow5GNsaType || this.mIsShow5GSaType);
        }
        if (voiceNetworkType == 13 || voiceNetworkType == 19) {
            Context context = this.mContext;
            int i = this.mSlotId;
            if (!this.mIsShow5GNsaType && !this.mIsShow5GSaType) {
                z = false;
            }
            networkTypeName3 = TelephonyUtils.getNetworkTypeName(context, i, voiceNetworkType, z);
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionId);
        boolean z3 = configForSubId != null ? configForSubId.getBoolean("show_4g_for_lte_data_icon_bool") : false;
        if (z3) {
            if ("LTE".equals(networkTypeName2)) {
                networkTypeName2 = "4G";
            }
            if ("LTE".equals(networkTypeName3)) {
                networkTypeName3 = "4G";
            }
            if ("LTE_CA".equals(networkTypeName2)) {
                networkTypeName2 = "4G+";
            }
            if ("LTE_CA".equals(networkTypeName3)) {
                networkTypeName3 = "4G+";
            }
        }
        String networkTypeName4 = TelephonyUtils.getNetworkTypeName(networkTypeName2);
        String networkTypeName5 = TelephonyUtils.getNetworkTypeName(networkTypeName3);
        if (!this.mIsShow5GNsaType) {
            networkTypeName4 = this.mIsShow5GSaType ? "5G_SA" : "5G_NSA";
            Log.d("SimStatusDialogCtrl", "show4GForLTE:" + z3 + ", mIsShow5GNsaType:" + this.mIsShow5GNsaType + ", mIsShow5GSaType:" + this.mIsShow5GSaType);
            this.mDialog.setText(CELL_VOICE_NETWORK_TYPE_VALUE_ID, networkTypeName5);
            this.mDialog.setText(CELL_DATA_NETWORK_TYPE_VALUE_ID, networkTypeName4);
        }
        networkTypeName5 = networkTypeName4;
        Log.d("SimStatusDialogCtrl", "show4GForLTE:" + z3 + ", mIsShow5GNsaType:" + this.mIsShow5GNsaType + ", mIsShow5GSaType:" + this.mIsShow5GSaType);
        this.mDialog.setText(CELL_VOICE_NETWORK_TYPE_VALUE_ID, networkTypeName5);
        this.mDialog.setText(CELL_DATA_NETWORK_TYPE_VALUE_ID, networkTypeName4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRoamingStatus(ServiceState serviceState) {
        if (serviceState == null) {
            this.mDialog.setText(ROAMING_INFO_VALUE_ID, this.mRes.getString(R.string.radioInfo_unknown));
        } else if (serviceState.getRoaming()) {
            this.mDialog.setText(ROAMING_INFO_VALUE_ID, this.mRes.getString(R.string.radioInfo_roaming_in));
        } else {
            this.mDialog.setText(ROAMING_INFO_VALUE_ID, this.mRes.getString(R.string.radioInfo_roaming_not));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateServiceState(ServiceState serviceState) {
        int combinedServiceState = Utils.getCombinedServiceState(serviceState);
        if (!Utils.isInService(serviceState)) {
            resetSignalStrength();
        } else if (!Utils.isInService(this.mPreviousServiceState)) {
            updateSignalStrength(this.mTelephonyManager.getSignalStrength());
        }
        this.mDialog.setText(SERVICE_STATE_VALUE_ID, combinedServiceState != 0 ? (combinedServiceState == 1 || combinedServiceState == 2) ? this.mRes.getString(R.string.radioInfo_service_out) : combinedServiceState != 3 ? this.mRes.getString(R.string.radioInfo_unknown) : this.mRes.getString(R.string.radioInfo_service_off) : this.mRes.getString(R.string.radioInfo_service_in));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSignalStrength(SignalStrength signalStrength) {
        PersistableBundle configForSubId;
        if (signalStrength == null) {
            return;
        }
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (!((subscriptionInfo == null || (configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionInfo.getSubscriptionId())) == null) ? true : configForSubId.getBoolean("show_signal_strength_in_sim_status_bool"))) {
            this.mDialog.removeSettingFromScreen(SIGNAL_STRENGTH_LABEL_ID);
            this.mDialog.removeSettingFromScreen(SIGNAL_STRENGTH_VALUE_ID);
        } else if (Utils.isInService(this.mTelephonyManager.getServiceState())) {
            int dbm = getDbm(signalStrength);
            int asuLevel = getAsuLevel(signalStrength);
            if (dbm == Integer.MAX_VALUE) {
                return;
            }
            if (dbm == -1) {
                dbm = 0;
            }
            if (asuLevel == -1) {
                asuLevel = 0;
            }
            this.mDialog.setText(SIGNAL_STRENGTH_VALUE_ID, this.mRes.getString(R.string.sim_signal_strength, Integer.valueOf(dbm), Integer.valueOf(asuLevel)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSubscriptionStatus() {
        updateNetworkProvider();
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        SignalStrength signalStrength = this.mTelephonyManager.getSignalStrength();
        updatePhoneNumber();
        updateServiceState(serviceState);
        updateSignalStrength(signalStrength);
        updateNetworkType();
        updateRoamingStatus(serviceState);
        updateIccidNumber();
        updateImsRegistrationState();
    }

    public void deinitialize() {
        if (this.mShowLatestAreaInfo) {
            CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
            if (cellBroadcastServiceConnection != null && cellBroadcastServiceConnection.getService() != null) {
                this.mContext.unbindService(this.mCellBroadcastServiceConnection);
            }
            this.mCellBroadcastServiceConnection = null;
        }
    }

    ServiceState getCurrentServiceState() {
        return this.mTelephonyManager.getServiceStateForSubscriber(this.mSubscriptionInfo.getSubscriptionId());
    }

    protected AtomicReference<String> getEid(int i) {
        String eid;
        boolean z = true;
        if (this.mTelephonyManager.getActiveModemCount() > 1) {
            int intValue = ((Integer) this.mTelephonyManager.getLogicalToPhysicalSlotMapping().getOrDefault(Integer.valueOf(i), -1)).intValue();
            if (intValue != -1) {
                Iterator<UiccCardInfo> it = this.mTelephonyManager.getUiccCardsInfo().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    UiccCardInfo next = it.next();
                    if (next.getSlotIndex() == intValue) {
                        if (next.isEuicc()) {
                            eid = next.getEid();
                            if (TextUtils.isEmpty(eid)) {
                                eid = this.mEuiccManager.createForCardId(next.getCardId()).getEid();
                            }
                        }
                    }
                }
            }
            eid = null;
            z = false;
        } else {
            if (this.mEuiccManager.isEnabled()) {
                eid = this.mEuiccManager.getEid();
            }
            eid = null;
            z = false;
        }
        if (z || eid != null) {
            return new AtomicReference<>(eid);
        }
        return null;
    }

    public void initialize() {
        requestForUpdateEid();
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null) {
            return;
        }
        this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
        this.mTelephonyCallback = new SimStatusDialogTelephonyCallback();
        updateLatestAreaInfo();
        updateSubscriptionStatus();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null) {
            if (this.mIsRegisteredListener) {
                this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
                this.mTelephonyManager.unregisterTelephonyCallback(this.mTelephonyCallback);
                if (this.mShowLatestAreaInfo) {
                    this.mContext.unregisterReceiver(this.mAreaInfoReceiver);
                }
                this.mIsRegisteredListener = false;
                return;
            }
            return;
        }
        unregisterImsRegistrationCallback(subscriptionInfo.getSubscriptionId());
        this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
        this.mTelephonyManager.unregisterTelephonyCallback(this.mTelephonyCallback);
        if (this.mShowLatestAreaInfo) {
            this.mContext.unregisterReceiver(this.mAreaInfoReceiver);
        }
        this.mFiveGController.pause();
        unregister5GNetworkTypeObserver();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null) {
            return;
        }
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
        this.mTelephonyManager = createForSubscriptionId;
        createForSubscriptionId.registerTelephonyCallback(this.mContext.getMainExecutor(), this.mTelephonyCallback);
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mContext.getMainExecutor(), this.mOnSubscriptionsChangedListener);
        registerImsRegistrationCallback(this.mSubscriptionInfo.getSubscriptionId());
        if (this.mShowLatestAreaInfo) {
            updateAreaInfoText();
            this.mContext.registerReceiver(this.mAreaInfoReceiver, new IntentFilter("android.telephony.action.AREA_INFO_UPDATED"));
        }
        this.mIsRegisteredListener = true;
        this.mFiveGController.resume();
        register5GNetworkTypeObserver();
    }

    protected void requestForUpdateEid() {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SimStatusDialogController.this.lambda$requestForUpdateEid$1();
            }
        });
    }

    protected void updateEid(AtomicReference<String> atomicReference) {
        if (atomicReference == null) {
            this.mDialog.removeSettingFromScreen(EID_INFO_LABEL_ID);
            this.mDialog.removeSettingFromScreen(EID_INFO_VALUE_ID);
        } else if (atomicReference.get() != null) {
            this.mDialog.setText(EID_INFO_VALUE_ID, atomicReference.get());
        }
    }

    protected void updatePhoneNumber() {
        this.mDialog.setText(PHONE_NUMBER_VALUE_ID, DeviceInfoUtils.getBidiFormattedPhoneNumber(this.mContext, this.mSubscriptionInfo));
    }
}
