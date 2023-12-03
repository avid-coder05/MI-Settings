package com.android.settings.network.apn;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseDataConnectionState;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.ims.ImsManager;
import com.android.settings.MiuiApnPreference;
import com.android.settings.R;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.Utils;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.security.VirtualSimUtils;
import com.android.settingslib.RestrictedLockUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import miui.app.constants.ThemeManagerConstants;
import miui.os.Build;
import miui.provider.ExtraContacts;
import miui.telephony.MiuiHeDuoHaoUtil;
import miui.telephony.TelephonyUtils;
import miuix.appcompat.app.ProgressDialog;
import miuix.internal.util.AttributeResolver;

/* loaded from: classes.dex */
public class ApnSettings extends RestrictedSettingsFragment implements Preference.OnPreferenceChangeListener {
    private static boolean mRestoreDefaultApnMode;
    private HashMap<String, Integer> bearerBitmaskCache;
    private boolean mAllowAddingApns;
    private int mCallState;
    private PersistableBundle mHideApnsGroupByIccid;
    private String[] mHideApnsWithIccidRule;
    private String[] mHideApnsWithRule;
    private boolean mHideImsApn;
    private boolean mHidePresetApnDetails;
    private boolean mHideXcapApn;
    private IntentFilter mIntentFilter;
    private boolean mIsAirplaneEnabled;
    private String mMvnoMatchData;
    private String mMvnoType;
    private Menu mOptionMenu;
    private int mPhoneId;
    private final PhoneStateListener mPhoneStateListener;
    private final BroadcastReceiver mReceiver;
    private RestoreApnProcessHandler mRestoreApnProcessHandler;
    private RestoreApnUiHandler mRestoreApnUiHandler;
    private HandlerThread mRestoreDefaultApnThread;
    private String mSelectedKey;
    private int mSubId;
    private SubscriptionInfo mSubscriptionInfo;
    private TelephonyManager mTelephonyManager;
    private boolean mUnavailable;
    private UserManager mUserManager;
    private static final String[] CARRIERS_PROJECTION = {"_id", "name", "apn", "type", "mvno_type", "mvno_match_data", "edited", "bearer", "bearer_bitmask"};
    private static final Uri DEFAULTAPN_URI = Uri.parse("content://telephony/carriers/restore");
    private static final Uri PREFERAPN_URI = Uri.parse("content://telephony/carriers/preferapn");
    static final boolean IS_JP_SB = "jp_sb".equals(SystemProperties.get("ro.miui.customized.region"));

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RestoreApnProcessHandler extends Handler {
        private Handler mRestoreApnUiHandler;

        RestoreApnProcessHandler(Looper looper, Handler handler) {
            super(looper);
            this.mRestoreApnUiHandler = handler;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            ApnSettings.this.getContentResolver().delete(ApnSettings.this.getUriForCurrSubId(ApnSettings.DEFAULTAPN_URI), null, null);
            this.mRestoreApnUiHandler.sendEmptyMessage(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RestoreApnUiHandler extends Handler {
        private RestoreApnUiHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 2) {
                return;
            }
            FragmentActivity activity = ApnSettings.this.getActivity();
            if (activity == null) {
                boolean unused = ApnSettings.mRestoreDefaultApnMode = false;
                return;
            }
            ApnSettings.this.fillList();
            ApnSettings.this.getPreferenceScreen().setEnabled(true);
            boolean unused2 = ApnSettings.mRestoreDefaultApnMode = false;
            ApnSettings.this.removeDialog(1001);
            Toast.makeText(activity, ApnSettings.this.getResources().getString(R.string.restore_default_apn_completed), 1).show();
        }
    }

    public ApnSettings() {
        super("no_config_mobile_networks");
        this.bearerBitmaskCache = new HashMap<>();
        this.mIsAirplaneEnabled = false;
        this.mCallState = 0;
        this.mPhoneStateListener = new PhoneStateListener() { // from class: com.android.settings.network.apn.ApnSettings.1
            @Override // android.telephony.PhoneStateListener
            public void onCallStateChanged(int i, String str) {
                Log.d("ApnSettings", "onCallStateChanged: mCallState = " + ApnSettings.this.mCallState + ", state = " + i);
                if (ApnSettings.this.mCallState == i) {
                    return;
                }
                ApnSettings.this.mCallState = i;
                ApnSettings.this.updateApnListEnableState();
            }

            @Override // android.telephony.PhoneStateListener
            public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState preciseDataConnectionState) {
                if (preciseDataConnectionState.getState() == 2) {
                    if (!ApnSettings.mRestoreDefaultApnMode) {
                        ApnSettings.this.fillList();
                    } else if (ApnSettings.this.isDialogShowing(1001)) {
                    } else {
                        ApnSettings.this.showDialog(1001);
                    }
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.apn.ApnSettings.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction()) && intent.getStringExtra("ss").equals("ABSENT")) {
                    SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
                    if (subscriptionManager == null || subscriptionManager.isActiveSubscriptionId(ApnSettings.this.mSubId)) {
                        return;
                    }
                    Log.d("ApnSettings", "Due to SIM absent, closes APN settings page");
                    ApnSettings.this.finish();
                } else if (intent.getAction().equals("android.telephony.action.SUBSCRIPTION_CARRIER_IDENTITY_CHANGED")) {
                    if (ApnSettings.mRestoreDefaultApnMode) {
                        return;
                    }
                    int intExtra = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_ID", -1);
                    if (SubscriptionManager.isValidSubscriptionId(intExtra) && ApnSettings.this.mPhoneId == SubscriptionUtil.getPhoneId(context, intExtra) && intExtra != ApnSettings.this.mSubId) {
                        ApnSettings.this.mSubId = intExtra;
                        ApnSettings apnSettings = ApnSettings.this;
                        apnSettings.mSubscriptionInfo = apnSettings.getSubscriptionInfo(apnSettings.mSubId);
                        ApnSettings apnSettings2 = ApnSettings.this;
                        apnSettings2.restartPhoneStateListener(apnSettings2.mSubId);
                    }
                    ApnSettings.this.fillList();
                } else if (intent.getAction().equals("org.codeaurora.intent.action.ACTION_ENHANCE_4G_SWITCH")) {
                    if (ApnSettings.mRestoreDefaultApnMode) {
                        ApnSettings.this.showDialog(1001);
                    } else {
                        ApnSettings.this.fillList();
                    }
                } else if (!intent.getAction().equals("android.intent.action.ANY_DATA_STATE")) {
                    if (intent.getAction().equals("android.intent.action.AIRPLANE_MODE")) {
                        ApnSettings.this.mIsAirplaneEnabled = intent.getBooleanExtra("state", false);
                        ApnSettings.this.updateApnListEnableState();
                    }
                } else {
                    String stringExtra = intent.getStringExtra("state");
                    String stringExtra2 = intent.getStringExtra("apnType");
                    Log.d("ApnSettings", "apnType: " + stringExtra2);
                    if (TextUtils.isEmpty(stringExtra2) || !"CONNECTED".equals(stringExtra) || !stringExtra2.contains(ExtraContacts.DefaultAccount.NAME) || ApnSettings.mRestoreDefaultApnMode) {
                        return;
                    }
                    ApnSettings.this.fillList();
                }
            }
        };
    }

    private void addNewApn() {
        Intent intent = new Intent("android.intent.action.INSERT", Telephony.Carriers.CONTENT_URI);
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        intent.putExtra(MiuiHeDuoHaoUtil.SUB_ID, subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1);
        intent.addFlags(1);
        if (!TextUtils.isEmpty(this.mMvnoType) && !TextUtils.isEmpty(this.mMvnoMatchData)) {
            intent.putExtra("mvno_type", this.mMvnoType);
            intent.putExtra("mvno_match_data", this.mMvnoMatchData);
        }
        startActivity(intent);
    }

    private void appendFilter(StringBuilder sb) {
        boolean z;
        String[] strArr;
        String string;
        PersistableBundle persistableBundle = this.mHideApnsGroupByIccid;
        boolean z2 = true;
        if (persistableBundle == null || persistableBundle.isEmpty()) {
            z = true;
        } else {
            z = this.mHideApnsGroupByIccid.getBoolean("include_common_rules", true);
            Log.d("ApnSettings", "apn hidden rules specified iccid, include common rule: " + z);
            for (String str : this.mHideApnsGroupByIccid.keySet()) {
                if (Utils.carrierTableFieldValidate(str) && (string = this.mHideApnsGroupByIccid.getString(str)) != null) {
                    sb.append(" AND " + str + " <> \"" + string + "\"");
                }
            }
        }
        String[] strArr2 = this.mHideApnsWithIccidRule;
        if (strArr2 != null) {
            HashMap<String, String> apnRuleMap = getApnRuleMap(strArr2);
            SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
            if (isOperatorIccid(apnRuleMap, subscriptionInfo == null ? "" : subscriptionInfo.getIccId())) {
                String str2 = apnRuleMap.get("include_common_rules");
                if (str2 != null && str2.equalsIgnoreCase(String.valueOf(false))) {
                    z2 = false;
                }
                Log.d("ApnSettings", "apn hidden rules in iccids, include common rule: " + z2);
                filterWithKey(apnRuleMap, sb);
                z = z2;
            }
        }
        if (!z || (strArr = this.mHideApnsWithRule) == null) {
            return;
        }
        filterWithKey(getApnRuleMap(strArr), sb);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fillList() {
        PreferenceCategory preferenceCategory;
        ArrayList arrayList;
        PreferenceCategory preferenceCategory2;
        ArrayList arrayList2;
        boolean z;
        MiuiApnPreference miuiApnPreference;
        ArrayList arrayList3;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        int subscriptionId = subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1;
        String simOperator = this.mSubscriptionInfo == null ? "" : ((TelephonyManager) getSystemService("phone")).getSimOperator(subscriptionId);
        Uri withAppendedPath = Uri.withAppendedPath(Telephony.Carriers.SIM_APN_URI, String.valueOf(subscriptionId));
        StringBuilder sb = new StringBuilder("NOT (type='ia' AND (apn=\"\" OR apn IS NULL)) AND user_visible!=0");
        int phoneId = SubscriptionManager.getPhoneId(subscriptionId);
        Context applicationContext = getActivity().getApplicationContext();
        ImsManager.getInstance(applicationContext, phoneId).isEnhanced4gLteModeSettingEnabledByUser();
        boolean z2 = SubscriptionManager.getResourcesForSubId(getActivity(), subscriptionId).getBoolean(285540389);
        boolean z3 = ((CarrierConfigManager) applicationContext.getSystemService("carrier_config")).getConfigForSubId(subscriptionId).getBoolean("config_hide_mms_apn");
        if (miui.telephony.TelephonyManager.getDefault().isChinaTelecomTest(simOperator)) {
            sb.append(" AND NOT (type='mms')");
            if (!(miui.telephony.TelephonyManager.getDefault().isDualVolteSupported() ? miui.telephony.TelephonyManager.getDefault().isVolteEnabledByUser(miui.telephony.SubscriptionManager.getDefault().getDefaultDataSlotId()) : miui.telephony.TelephonyManager.getDefault().isVolteEnabledByUser())) {
                sb.append(" AND NOT (type='ims')");
            }
        } else if (!Build.IS_CU_CUSTOMIZATION_TEST) {
            if (isHideImsApn()) {
                sb.append(" AND NOT (type='ims')");
            }
            if (z2) {
                sb.append(" AND NOT (type='dun')");
            }
            if (z3) {
                sb.append(" AND NOT (type='mms')");
            }
        } else if (!miui.telephony.TelephonyManager.getDefault().isVolteEnabledByPlatform(miui.telephony.SubscriptionManager.getDefault().getDefaultDataSlotId())) {
            sb.append(" AND NOT (type='ims')");
        }
        if (this.mHideXcapApn) {
            sb.append(" AND NOT (type='xcap')");
        }
        appendFilter(sb);
        Log.d("ApnSettings", "where = " + sb.toString());
        Cursor query = getContentResolver().query(withAppendedPath, CARRIERS_PROJECTION, sb.toString(), null, "name ASC");
        if (query != null) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("apn_list");
            preferenceGroup.removeAll();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            String selectedApnKey = getSelectedApnKey();
            this.mSelectedKey = selectedApnKey;
            ApnPreference.setSelectedKey(selectedApnKey);
            query.moveToFirst();
            PreferenceCategory preferenceCategory3 = null;
            PreferenceCategory preferenceCategory4 = null;
            while (!query.isAfterLast()) {
                String string = query.getString(1);
                String string2 = query.getString(2);
                if (this.mSubscriptionInfo == null || VirtualSimUtils.isValidApnForMiSim(getActivity(), this.mSubscriptionInfo.getSimSlotIndex(), string2)) {
                    String string3 = query.getString(0);
                    String string4 = query.getString(3);
                    int i = query.getInt(6);
                    this.mMvnoType = query.getString(4);
                    this.mMvnoMatchData = query.getString(5);
                    StringBuilder sb2 = new StringBuilder();
                    arrayList = arrayList5;
                    sb2.append("name = ");
                    sb2.append(string);
                    sb2.append(",apn = ");
                    sb2.append(string2);
                    sb2.append(",type =");
                    sb2.append(string4);
                    Log.d("ApnSettings", sb2.toString());
                    String localizedName = Utils.getLocalizedName(getActivity(), query.getString(1));
                    String str = !TextUtils.isEmpty(localizedName) ? localizedName : string;
                    preferenceCategory2 = preferenceCategory4;
                    this.bearerBitmaskCache.put(string3, Integer.valueOf(query.getInt(8) | ServiceState.getBitmaskForTech(query.getInt(7))));
                    int i2 = query.getInt(7);
                    int i3 = query.getInt(8);
                    int bitmaskForTech = ServiceState.getBitmaskForTech(i2) | i3;
                    arrayList2 = arrayList4;
                    int networkTypeToRilRidioTechnology = networkTypeToRilRidioTechnology(TelephonyManager.getDefault().getDataNetworkType(subscriptionId));
                    if (!ServiceState.bitmaskHasTech(bitmaskForTech, networkTypeToRilRidioTechnology) && (i2 != 0 || i3 != 0)) {
                        boolean z4 = "20801".equals(simOperator) && "orange".equals(string2);
                        if (z4) {
                            Log.d("ApnSettings", "isOrangeApn = " + z4);
                        } else if (networkTypeToRilRidioTechnology != 0 || (i2 == 0 && networkTypeToRilRidioTechnology == 0)) {
                            query.moveToNext();
                        }
                    }
                    boolean z5 = string4 == null || !string4.equals(ThemeManagerConstants.COMPONENT_CODE_MMS);
                    if (miui.telephony.TelephonyManager.getDefault().isChinaTelecomTest(simOperator)) {
                        z5 = string4 == null || !string4.equals("ims");
                    }
                    boolean z6 = "dun".equals(string4) && getResources().getBoolean(R.bool.config_disable_dun_apn);
                    if (z5) {
                        MiuiApnPreference miuiApnPreference2 = new MiuiApnPreference(getPrefContext());
                        miuiApnPreference2.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
                        miuiApnPreference2.setApnReadOnly(z6);
                        z = true;
                        miuiApnPreference = miuiApnPreference2;
                    } else {
                        Preference preference = new Preference(getPrefContext());
                        z = true;
                        preference.setSelectable(true);
                        miuiApnPreference = preference;
                    }
                    miuiApnPreference.setKey(string3);
                    miuiApnPreference.setTitle(str);
                    miuiApnPreference.setPersistent(false);
                    miuiApnPreference.setOnPreferenceChangeListener(this);
                    if (IS_JP_SB && i == 0) {
                        string2 = apnNameEncryptionDisplay(string2);
                    }
                    miuiApnPreference.setSummary(string2);
                    if (z5) {
                        if (!z6) {
                            MiuiApnPreference miuiApnPreference3 = (MiuiApnPreference) miuiApnPreference;
                            String str2 = this.mSelectedKey;
                            miuiApnPreference3.setChecked((str2 == null || !str2.equals(string3)) ? false : z);
                            miuiApnPreference3.setSubId(subscriptionId);
                            miuiApnPreference3.setApnType(string4);
                            miuiApnPreference3.setEdited(i);
                        }
                        if (preferenceCategory3 == null) {
                            preferenceCategory3 = new PreferenceCategory(getPrefContext());
                            preferenceCategory3.setKey("apn_general_list");
                            preferenceCategory3.setTitle(R.string.apn_general);
                            preferenceGroup.addPreference(preferenceCategory3);
                        }
                        arrayList4 = arrayList2;
                        arrayList4.add(miuiApnPreference);
                        arrayList3 = arrayList;
                        preferenceCategory4 = preferenceCategory2;
                    } else {
                        arrayList4 = arrayList2;
                        if (preferenceCategory2 == null) {
                            preferenceCategory4 = new PreferenceCategory(getPrefContext());
                            preferenceCategory4.setKey("apn_mms_list");
                            if (!miui.telephony.TelephonyManager.getDefault().isChinaTelecomTest(simOperator)) {
                                preferenceCategory4.setTitle(R.string.apn_mms);
                            }
                            preferenceGroup.addPreference(preferenceCategory4);
                            arrayList3 = arrayList;
                        } else {
                            arrayList3 = arrayList;
                            preferenceCategory4 = preferenceCategory2;
                        }
                        arrayList3.add(miuiApnPreference);
                    }
                    query.moveToNext();
                    arrayList5 = arrayList3;
                } else {
                    query.moveToNext();
                    arrayList2 = arrayList4;
                    arrayList = arrayList5;
                    preferenceCategory2 = preferenceCategory4;
                }
                arrayList5 = arrayList;
                arrayList4 = arrayList2;
                preferenceCategory4 = preferenceCategory2;
            }
            ArrayList arrayList6 = arrayList5;
            PreferenceCategory preferenceCategory5 = preferenceCategory4;
            query.close();
            if (arrayList4.isEmpty() && preferenceCategory3 != null) {
                preferenceGroup.removePreference(preferenceCategory3);
            }
            if (!arrayList6.isEmpty() || preferenceCategory5 == null) {
                preferenceCategory = preferenceCategory5;
            } else {
                preferenceCategory = preferenceCategory5;
                preferenceGroup.removePreference(preferenceCategory);
            }
            Iterator it = arrayList4.iterator();
            while (it.hasNext()) {
                Preference preference2 = (Preference) it.next();
                if (preferenceCategory3 != null) {
                    preferenceCategory3.addPreference(preference2);
                }
            }
            Iterator it2 = arrayList6.iterator();
            while (it2.hasNext()) {
                Preference preference3 = (Preference) it2.next();
                if (preferenceCategory != null) {
                    preferenceCategory.addPreference(preference3);
                }
            }
        }
    }

    private void filterWithKey(Map<String, String> map, StringBuilder sb) {
        for (String str : map.keySet()) {
            if (Utils.carrierTableFieldValidate(str)) {
                String str2 = map.get(str);
                if (!TextUtils.isEmpty(str2)) {
                    for (String str3 : str2.split(",")) {
                        sb.append(" AND " + str + " <> \"" + str3 + "\"");
                    }
                }
            }
        }
    }

    private HashMap<String, String> getApnRuleMap(String[] strArr) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (strArr != null) {
            int length = strArr.length;
            Log.d("ApnSettings", "ruleArray size = " + length);
            if (length > 0 && length % 2 == 0) {
                for (int i = 0; i < length; i += 2) {
                    hashMap.put(strArr[i].toLowerCase(), strArr[i + 1]);
                }
            }
        }
        return hashMap;
    }

    private String getSelectedApnKey() {
        String str;
        Cursor query = getContentResolver().query(getUriForCurrSubId(PREFERAPN_URI), new String[]{"_id"}, null, null, "name ASC");
        if (query.getCount() > 0) {
            query.moveToFirst();
            str = query.getString(0);
        } else {
            str = null;
        }
        query.close();
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubscriptionInfo getSubscriptionInfo(int i) {
        return SubscriptionManager.from(getActivity()).getActiveSubscriptionInfo(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Uri getUriForCurrSubId(Uri uri) {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        int subscriptionId = subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1;
        if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            return Uri.withAppendedPath(uri, "subId/" + String.valueOf(subscriptionId));
        }
        return uri;
    }

    private boolean isCallStateIdle() {
        return this.mCallState == 0;
    }

    private boolean isHideImsApn() {
        return !(IS_JP_SB ? SystemProperties.getBoolean("radio.editims.secretcode", false) : false) && this.mHideImsApn;
    }

    private boolean isOperatorIccid(HashMap<String, String> hashMap, String str) {
        String str2 = hashMap.get("iccid");
        if (!TextUtils.isEmpty(str2)) {
            for (String str3 : str2.split(",")) {
                if (str.startsWith(str3.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    private int networkTypeToRilRidioTechnology(int i) {
        switch (i) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 5;
            case 5:
                return 7;
            case 6:
                return 8;
            case 7:
                return 6;
            case 8:
                return 9;
            case 9:
                return 10;
            case 10:
                return 11;
            case 11:
            default:
                return 0;
            case 12:
                return 12;
            case 13:
                return 14;
            case 14:
                return 13;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return 17;
            case 18:
                return 18;
            case 19:
                return 19;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartPhoneStateListener(int i) {
        if (mRestoreDefaultApnMode) {
            return;
        }
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(i);
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        this.mTelephonyManager = createForSubscriptionId;
        createForSubscriptionId.listen(this.mPhoneStateListener, 4096);
        this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
    }

    private boolean restoreDefaultApn() {
        if (!isDialogShowing(1001)) {
            showDialog(1001);
        }
        mRestoreDefaultApnMode = true;
        if (this.mRestoreApnUiHandler == null) {
            this.mRestoreApnUiHandler = new RestoreApnUiHandler();
        }
        if (this.mRestoreApnProcessHandler == null || this.mRestoreDefaultApnThread == null) {
            HandlerThread handlerThread = new HandlerThread("Restore default APN Handler: Process Thread");
            this.mRestoreDefaultApnThread = handlerThread;
            handlerThread.start();
            this.mRestoreApnProcessHandler = new RestoreApnProcessHandler(this.mRestoreDefaultApnThread.getLooper(), this.mRestoreApnUiHandler);
        }
        this.mRestoreApnProcessHandler.sendEmptyMessage(1);
        return true;
    }

    private void setSelectedApnKey(String str) {
        this.mSelectedKey = str;
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("apn_id", this.mSelectedKey);
        contentResolver.update(getUriForCurrSubId(PREFERAPN_URI), contentValues, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateApnListEnableState() {
        Menu menu;
        PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("apn_list");
        boolean z = !this.mIsAirplaneEnabled && isCallStateIdle();
        Log.d("ApnSettings", "updateApnListEnableState: mIsAirplaneEnabled:" + this.mIsAirplaneEnabled + ", isCallStateIdle():" + isCallStateIdle());
        if (preferenceGroup != null) {
            preferenceGroup.setEnabled(z);
        }
        if (this.mUnavailable || (menu = this.mOptionMenu) == null) {
            return;
        }
        menu.setGroupEnabled(0, z);
    }

    public String apnNameEncryptionDisplay(CharSequence charSequence) {
        int length = charSequence == null ? 0 : charSequence.length();
        if (length != 0) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append('*');
            }
            return sb.toString();
        }
        return "";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 12;
    }

    @Override // com.android.settings.RestrictedSettingsFragment
    public RestrictedLockUtils.EnforcedAdmin getRestrictionEnforcedAdmin() {
        UserHandle of = UserHandle.of(this.mUserManager.getUserHandle());
        if (!this.mUserManager.hasUserRestriction("no_config_mobile_networks", of) || this.mUserManager.hasBaseUserRestriction("no_config_mobile_networks", of)) {
            return null;
        }
        return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getEmptyTextView().setText(R.string.apn_settings_not_available);
        boolean isUiRestricted = isUiRestricted();
        this.mUnavailable = isUiRestricted;
        setHasOptionsMenu(!isUiRestricted);
        if (this.mUnavailable) {
            addPreferencesFromResource(R.xml.placeholder_prefs);
        } else {
            addPreferencesFromResource(R.xml.apn_settings);
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        int defaultDataSubscriptionId = miui.telephony.SubscriptionManager.getDefault().getDefaultDataSubscriptionId();
        this.mSubId = defaultDataSubscriptionId;
        this.mPhoneId = SubscriptionUtil.getPhoneId(activity, defaultDataSubscriptionId);
        IntentFilter intentFilter = new IntentFilter("android.telephony.action.SUBSCRIPTION_CARRIER_IDENTITY_CHANGED");
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        if (Utils.isSupportCTPA(getActivity().getApplicationContext())) {
            this.mIntentFilter.addAction("org.codeaurora.intent.action.ACTION_ENHANCE_4G_SWITCH");
        }
        this.mIntentFilter.addAction("android.intent.action.ANY_DATA_STATE");
        this.mIntentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        this.mIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        this.mIsAirplaneEnabled = Settings.Global.getInt(getPrefContext().getContentResolver(), "airplane_mode_on", 0) == 1;
        setIfOnlyAvailableForAdmins(true);
        this.mSubscriptionInfo = getSubscriptionInfo(this.mSubId);
        this.mTelephonyManager = (TelephonyManager) activity.getSystemService(TelephonyManager.class);
        PersistableBundle configForSubId = ((CarrierConfigManager) getSystemService("carrier_config")).getConfigForSubId(this.mSubId);
        this.mHideImsApn = configForSubId.getBoolean("hide_ims_apn_bool");
        this.mHideXcapApn = configForSubId.getBoolean("hide_xcap_apn_bool");
        this.mAllowAddingApns = configForSubId.getBoolean("allow_adding_apns_bool");
        this.mHideApnsWithRule = configForSubId.getStringArray("apn_hide_rule_strings_array");
        this.mHideApnsWithIccidRule = configForSubId.getStringArray("apn_hide_rule_strings_with_iccids_array");
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo != null) {
            String iccId = subscriptionInfo.getIccId();
            Log.d("ApnSettings", "iccid: " + TelephonyUtils.pii(iccId));
            this.mHideApnsGroupByIccid = configForSubId.getPersistableBundle(iccId);
        }
        if (this.mAllowAddingApns && ApnEditor.hasAllApns(configForSubId.getStringArray("read_only_apn_types_string_array"))) {
            Log.d("ApnSettings", "not allowing adding APN because all APN types are read only");
            this.mAllowAddingApns = false;
        }
        this.mHidePresetApnDetails = configForSubId.getBoolean("hide_preset_apn_details_bool");
        this.mUserManager = UserManager.get(activity);
        activity.registerReceiver(this.mReceiver, this.mIntentFilter);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1001) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity()) { // from class: com.android.settings.network.apn.ApnSettings.3
                @Override // android.app.Dialog
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return true;
                }
            };
            progressDialog.setMessage(getResources().getString(R.string.restore_default_apn));
            progressDialog.setCancelable(false);
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.android.settings.network.apn.ApnSettings.4
                @Override // android.content.DialogInterface.OnKeyListener
                public boolean onKey(DialogInterface dialogInterface, int i2, KeyEvent keyEvent) {
                    return i2 == 4 && ApnSettings.mRestoreDefaultApnMode;
                }
            });
            return progressDialog;
        }
        return null;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (!this.mUnavailable) {
            if (miui.telephony.TelephonyManager.isCustForKrLgu()) {
                this.mAllowAddingApns = this.mAllowAddingApns || SystemProperties.getBoolean("radio.newapn.secretcode", false);
                Log.d("ApnSettings", "isCustForKrLgu mAllowAddingApns: " + this.mAllowAddingApns);
            }
            if (this.mAllowAddingApns && (this.mSubscriptionInfo == null || !VirtualSimUtils.isMiSimEnabled(getActivity(), this.mSubscriptionInfo.getSimSlotIndex()))) {
                menu.add(0, 1, 0, getResources().getString(R.string.menu_new)).setIcon(AttributeResolver.resolveDrawable(getActivity(), R.attr.actionBarNewIcon)).setShowAsAction(1);
            }
            menu.add(0, 2, 0, getResources().getString(R.string.menu_restore)).setIcon(R.drawable.action_button_clear).setShowAsAction(1);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
        this.mOptionMenu = menu;
        updateApnListEnableState();
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(this.mReceiver);
        }
        HandlerThread handlerThread = this.mRestoreDefaultApnThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
        this.bearerBitmaskCache.clear();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            addNewApn();
            return true;
        } else if (itemId != 2) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            if (!mRestoreDefaultApnMode) {
                restoreDefaultApn();
            }
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.mUnavailable) {
            return;
        }
        if (mRestoreDefaultApnMode) {
            removeDialog(1001);
        }
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Log.d("ApnSettings", "onPreferenceChange(): Preference - " + preference + ", newValue - " + obj + ", newValue type - " + obj.getClass());
        if (obj instanceof String) {
            setSelectedApnKey((String) obj);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        int i;
        String key = preference.getKey();
        if (preference instanceof MiuiApnPreference) {
            boolean z = !TextUtils.isEmpty(this.mSelectedKey);
            TelephonyManager telephonyManager = this.mTelephonyManager;
            if (telephonyManager == null || telephonyManager.getServiceState() == null) {
                i = 0;
            } else {
                i = this.mTelephonyManager.getServiceState().getRilDataRadioTechnology();
                Log.d("ApnSettings", "radioTech = " + i);
            }
            if (!ServiceState.bitmaskHasTech(this.bearerBitmaskCache.get(key).intValue(), i) && getActivity() != null) {
                ((MiuiApnPreference) preference).setChecked(false);
                Toast.makeText(getActivity(), R.string.forbidden_switch_apn_string, 0).show();
                return false;
            } else if (z && this.mSelectedKey.equals(key)) {
                ((MiuiApnPreference) preference).setChecked(true);
            } else {
                if (z) {
                    Preference findPreference = getPreferenceScreen().findPreference(this.mSelectedKey);
                    if (findPreference instanceof MiuiApnPreference) {
                        ((MiuiApnPreference) findPreference).setChecked(false);
                    }
                }
                setSelectedApnKey(key);
            }
        } else {
            startActivity(new Intent("android.intent.action.EDIT", ContentUris.withAppendedId(Telephony.Carriers.CONTENT_URI, Integer.parseInt(key))));
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mUnavailable) {
            return;
        }
        restartPhoneStateListener(this.mSubId);
        if (mRestoreDefaultApnMode) {
            return;
        }
        fillList();
    }
}
