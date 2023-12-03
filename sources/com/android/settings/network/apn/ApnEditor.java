package com.android.settings.network.apn;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.provider.Telephony;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.preference.CheckBoxPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import com.android.internal.util.ArrayUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settingslib.miuisettings.preference.EditTextPreference;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import com.android.settingslib.utils.ThreadUtils;
import com.google.android.material.timepicker.TimeModel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import miui.app.constants.ThemeManagerConstants;
import miui.provider.ExtraContacts;
import miui.telephony.MiuiHeDuoHaoUtil;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ApnEditor extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, View.OnKeyListener {
    static final int APN_INDEX = 2;
    static final int CARRIER_ENABLED_INDEX = 17;
    static final int MCC_INDEX = 9;
    static final int MNC_INDEX = 10;
    static final int NAME_INDEX = 1;
    static final int PROTOCOL_INDEX = 16;
    static final int ROAMING_PROTOCOL_INDEX = 20;
    static final int TYPE_INDEX = 15;
    static String sNotSet;
    EditTextPreference mApn;
    ApnData mApnData;
    EditTextPreference mApnType;
    DropDownPreference mAuthType;
    private int mBearerInitialVal = 0;
    MultiSelectListPreference mBearerMulti;
    CheckBoxPreference mCarrierEnabled;
    private Uri mCarrierUri;
    private String mCurMcc;
    private String mCurMnc;
    String mDefaultApnProtocol;
    String mDefaultApnRoamingProtocol;
    String[] mDefaultApnTypes;
    EditTextPreference mMcc;
    EditTextPreference mMmsPort;
    EditTextPreference mMmsProxy;
    EditTextPreference mMmsc;
    EditTextPreference mMnc;
    EditTextPreference mMvnoMatchData;
    private String mMvnoMatchDataStr;
    DropDownPreference mMvnoType;
    private String mMvnoTypeStr;
    EditTextPreference mName;
    private boolean mNewApn;
    EditTextPreference mPassword;
    EditTextPreference mPort;
    DropDownPreference mProtocol;
    EditTextPreference mProxy;
    ProxySubscriptionManager mProxySubscriptionMgr;
    private boolean mReadOnlyApn;
    private String[] mReadOnlyApnFields;
    String[] mReadOnlyApnTypes;
    DropDownPreference mRoamingProtocol;
    EditTextPreference mServer;
    private int mSubId;
    EditTextPreference mUser;
    private static final String TAG = ApnEditor.class.getSimpleName();
    public static final String[] APN_TYPES = {ExtraContacts.DefaultAccount.NAME, ThemeManagerConstants.COMPONENT_CODE_MMS, "supl", "dun", "hipri", "fota", "ims", "cbs", "ia", "emergency", "mcx", "xcap"};
    private static final String[] sProjection = {"_id", "name", "apn", "proxy", "port", "user", "server", "password", "mmsc", "mcc", "mnc", "numeric", "mmsproxy", "mmsport", "authtype", "type", "protocol", "carrier_enabled", "bearer", "bearer_bitmask", "roaming_protocol", "mvno_type", "mvno_match_data", "edited", "user_editable"};
    private static final String[] sUIConfigurableItems = {"name", "apn", "proxy", "port", "user", "server", "password", "mmsc", "mmsproxy", "mmsport", "authtype", "type", "protocol", "carrier_enabled", "bearer", "bearer_bitmask", "roaming_protocol"};

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ApnData {
        Object[] mData;
        Uri mUri;

        ApnData(int i) {
            this.mData = new Object[i];
        }

        ApnData(Uri uri, Cursor cursor) {
            this.mUri = uri;
            this.mData = new Object[cursor.getColumnCount()];
            for (int i = 0; i < this.mData.length; i++) {
                int type = cursor.getType(i);
                if (type == 1) {
                    this.mData[i] = Integer.valueOf(cursor.getInt(i));
                } else if (type == 2) {
                    this.mData[i] = Float.valueOf(cursor.getFloat(i));
                } else if (type == 3) {
                    this.mData[i] = cursor.getString(i);
                } else if (type != 4) {
                    this.mData[i] = null;
                } else {
                    this.mData[i] = cursor.getBlob(i);
                }
            }
        }

        Integer getInteger(int i) {
            return (Integer) this.mData[i];
        }

        Integer getInteger(int i, Integer num) {
            Integer integer = getInteger(i);
            return integer == null ? num : integer;
        }

        String getString(int i) {
            return (String) this.mData[i];
        }

        Uri getUri() {
            return this.mUri;
        }
    }

    /* loaded from: classes.dex */
    public static class ErrorDialog extends InstrumentedDialogFragment {
        public static void showError(ApnEditor apnEditor) {
            ErrorDialog errorDialog = new ErrorDialog();
            errorDialog.setTargetFragment(apnEditor, 0);
            errorDialog.show(apnEditor.getFragmentManager(), "error");
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 530;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getContext()).setTitle(R.string.error_title).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).setMessage(((ApnEditor) getTargetFragment()).validateApnData()).create();
        }
    }

    private boolean apnTypesMatch(String[] strArr, String str) {
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        if (hasAllApns(strArr) || TextUtils.isEmpty(str)) {
            return true;
        }
        List asList = Arrays.asList(strArr);
        for (String str2 : str.split(",")) {
            if (asList.contains(str2.trim())) {
                Log.d(TAG, "apnTypesMatch: true because match found for " + str2.trim());
                return true;
            }
        }
        Log.d(TAG, "apnTypesMatch: false");
        return false;
    }

    private String bearerMultiDescription(Set<String> set) {
        String[] stringArray = getResources().getStringArray(R.array.bearer_entries);
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = set.iterator();
        boolean z = true;
        while (it.hasNext()) {
            int findIndexOfValue = this.mBearerMulti.findIndexOfValue(it.next());
            if (z) {
                try {
                    sb.append(stringArray[findIndexOfValue]);
                    z = false;
                } catch (ArrayIndexOutOfBoundsException unused) {
                }
            } else {
                sb.append(", " + stringArray[findIndexOfValue]);
            }
        }
        String sb2 = sb.toString();
        if (TextUtils.isEmpty(sb2)) {
            return null;
        }
        return sb2;
    }

    private static boolean bitmaskHasTech(int i, int i2) {
        if (i == 0) {
            return true;
        }
        return i2 >= 1 && (i & (1 << (i2 - 1))) != 0;
    }

    private boolean checkAllNoSet() {
        return TextUtils.isEmpty(this.mName.getText()) && TextUtils.isEmpty(this.mApn.getText()) && TextUtils.isEmpty(this.mProxy.getText()) && TextUtils.isEmpty(this.mPort.getText()) && TextUtils.isEmpty(this.mUser.getText()) && TextUtils.isEmpty(this.mServer.getText()) && TextUtils.isEmpty(this.mPassword.getText()) && TextUtils.isEmpty(this.mMmsProxy.getText()) && TextUtils.isEmpty(this.mMmsPort.getText()) && TextUtils.isEmpty(this.mMmsc.getText()) && TextUtils.equals(this.mMcc.getText(), this.mCurMcc) && TextUtils.equals(this.mMnc.getText(), this.mCurMnc) && TextUtils.isEmpty(this.mApnType.getText()) && TextUtils.isEmpty(this.mProtocol.getValue()) && TextUtils.isEmpty(this.mRoamingProtocol.getValue()) && TextUtils.equals(this.mMvnoType.getValue(), this.mMvnoTypeStr) && TextUtils.equals(this.mMvnoMatchData.getText(), this.mMvnoMatchDataStr);
    }

    private String checkNotSet(String str) {
        if (sNotSet.equals(str)) {
            return null;
        }
        return str;
    }

    private String checkNull(String str) {
        return TextUtils.isEmpty(str) ? sNotSet : str;
    }

    private String checkNullforMvnoValue(String str) {
        return TextUtils.isEmpty(str) ? getResources().getString(R.string.apn_not_set_for_mvno) : str;
    }

    private void deleteApn() {
        if (this.mApnData.getUri() != null) {
            getContentResolver().delete(this.mApnData.getUri(), null, null);
            this.mApnData = new ApnData(sProjection.length);
        }
    }

    private void disableAllFields() {
        this.mName.setEnabled(false);
        this.mApn.setEnabled(false);
        this.mProxy.setEnabled(false);
        this.mPort.setEnabled(false);
        this.mUser.setEnabled(false);
        this.mServer.setEnabled(false);
        this.mPassword.setEnabled(false);
        this.mMmsProxy.setEnabled(false);
        this.mMmsPort.setEnabled(false);
        this.mMmsc.setEnabled(false);
        this.mMcc.setEnabled(false);
        this.mMnc.setEnabled(false);
        this.mApnType.setEnabled(false);
        this.mAuthType.setEnabled(false);
        this.mProtocol.setEnabled(false);
        this.mRoamingProtocol.setEnabled(false);
        this.mCarrierEnabled.setEnabled(false);
        this.mBearerMulti.setEnabled(false);
        this.mMvnoType.setEnabled(false);
        this.mMvnoMatchData.setEnabled(false);
    }

    private void disableFields(String[] strArr) {
        for (String str : strArr) {
            Preference preferenceFromFieldName = getPreferenceFromFieldName(str);
            if (preferenceFromFieldName != null) {
                preferenceFromFieldName.setEnabled(false);
            }
        }
    }

    static String formatInteger(String str) {
        try {
            return String.format(getCorrectDigitsFormat(str), Integer.valueOf(Integer.parseInt(str)));
        } catch (NumberFormatException unused) {
            return str;
        }
    }

    private static int getBitmaskForTech(int i) {
        if (i >= 1) {
            return 1 << (i - 1);
        }
        return 0;
    }

    private void getCarrierCustomizedConfig() {
        PersistableBundle configForSubId;
        this.mReadOnlyApn = false;
        this.mReadOnlyApnTypes = null;
        this.mReadOnlyApnFields = null;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getSystemService("carrier_config");
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mSubId)) == null) {
            return;
        }
        String[] stringArray = configForSubId.getStringArray("read_only_apn_types_string_array");
        this.mReadOnlyApnTypes = stringArray;
        if (!ArrayUtils.isEmpty(stringArray)) {
            Log.d(TAG, "onCreate: read only APN type: " + Arrays.toString(this.mReadOnlyApnTypes));
        }
        this.mReadOnlyApnFields = configForSubId.getStringArray("read_only_apn_fields_string_array");
        String[] stringArray2 = configForSubId.getStringArray("apn_settings_default_apn_types_string_array");
        this.mDefaultApnTypes = stringArray2;
        if (!ArrayUtils.isEmpty(stringArray2)) {
            Log.d(TAG, "onCreate: default apn types: " + Arrays.toString(this.mDefaultApnTypes));
        }
        String string = configForSubId.getString("apn.settings_default_protocol_string");
        this.mDefaultApnProtocol = string;
        if (!TextUtils.isEmpty(string)) {
            Log.d(TAG, "onCreate: default apn protocol: " + this.mDefaultApnProtocol);
        }
        String string2 = configForSubId.getString("apn.settings_default_roaming_protocol_string");
        this.mDefaultApnRoamingProtocol = string2;
        if (TextUtils.isEmpty(string2)) {
            return;
        }
        Log.d(TAG, "onCreate: default apn roaming protocol: " + this.mDefaultApnRoamingProtocol);
    }

    static String getCorrectDigitsFormat(String str) {
        return str.length() == 2 ? TimeModel.ZERO_LEADING_NUMBER_FORMAT : "%03d";
    }

    private String getEditableApnType(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (String str : strArr) {
            if (!str.equals("ia") && !str.equals("emergency") && !str.equals("ims") && !str.equals("dun")) {
                if (z) {
                    z = false;
                } else {
                    sb.append(",");
                }
                sb.append(str);
            }
        }
        return sb.toString();
    }

    private Preference getPreferenceFromFieldName(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2135515857:
                if (str.equals("mvno_type")) {
                    c = 0;
                    break;
                }
                break;
            case -1954254981:
                if (str.equals("mmsproxy")) {
                    c = 1;
                    break;
                }
                break;
            case -1640523526:
                if (str.equals("carrier_enabled")) {
                    c = 2;
                    break;
                }
                break;
            case -1393032351:
                if (str.equals("bearer")) {
                    c = 3;
                    break;
                }
                break;
            case -1230508389:
                if (str.equals("bearer_bitmask")) {
                    c = 4;
                    break;
                }
                break;
            case -1039601666:
                if (str.equals("roaming_protocol")) {
                    c = 5;
                    break;
                }
                break;
            case -989163880:
                if (str.equals("protocol")) {
                    c = 6;
                    break;
                }
                break;
            case -905826493:
                if (str.equals("server")) {
                    c = 7;
                    break;
                }
                break;
            case -520149991:
                if (str.equals("mvno_match_data")) {
                    c = '\b';
                    break;
                }
                break;
            case 96799:
                if (str.equals("apn")) {
                    c = '\t';
                    break;
                }
                break;
            case 107917:
                if (str.equals("mcc")) {
                    c = '\n';
                    break;
                }
                break;
            case 108258:
                if (str.equals("mnc")) {
                    c = 11;
                    break;
                }
                break;
            case 3355632:
                if (str.equals("mmsc")) {
                    c = '\f';
                    break;
                }
                break;
            case 3373707:
                if (str.equals("name")) {
                    c = '\r';
                    break;
                }
                break;
            case 3446913:
                if (str.equals("port")) {
                    c = 14;
                    break;
                }
                break;
            case 3575610:
                if (str.equals("type")) {
                    c = 15;
                    break;
                }
                break;
            case 3599307:
                if (str.equals("user")) {
                    c = 16;
                    break;
                }
                break;
            case 106941038:
                if (str.equals("proxy")) {
                    c = 17;
                    break;
                }
                break;
            case 1183882708:
                if (str.equals("mmsport")) {
                    c = 18;
                    break;
                }
                break;
            case 1216985755:
                if (str.equals("password")) {
                    c = 19;
                    break;
                }
                break;
            case 1433229538:
                if (str.equals("authtype")) {
                    c = 20;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return this.mMvnoType;
            case 1:
                return this.mMmsProxy;
            case 2:
                return this.mCarrierEnabled;
            case 3:
            case 4:
                return this.mBearerMulti;
            case 5:
                return this.mRoamingProtocol;
            case 6:
                return this.mProtocol;
            case 7:
                return this.mServer;
            case '\b':
                return this.mMvnoMatchData;
            case '\t':
                return this.mApn;
            case '\n':
                return this.mMcc;
            case 11:
                return this.mMnc;
            case '\f':
                return this.mMmsc;
            case '\r':
                return this.mName;
            case 14:
                return this.mPort;
            case 15:
                return this.mApnType;
            case 16:
                return this.mUser;
            case 17:
                return this.mProxy;
            case 18:
                return this.mMmsPort;
            case 19:
                return this.mPassword;
            case 20:
                return this.mAuthType;
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasAllApns(String[] strArr) {
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        List asList = Arrays.asList(strArr);
        if (asList.contains("*")) {
            Log.d(TAG, "hasAllApns: true because apnList.contains(APN_TYPE_ALL)");
            return true;
        }
        for (String str : APN_TYPES) {
            if (!asList.contains(str)) {
                return false;
            }
        }
        Log.d(TAG, "hasAllApns: true");
        return true;
    }

    private void initApnEditorUi() {
        addPreferencesFromResource(R.xml.apn_editor);
        sNotSet = getResources().getString(R.string.apn_not_set);
        this.mName = (EditTextPreference) findPreference("apn_name");
        this.mApn = (EditTextPreference) findPreference("apn_apn");
        this.mProxy = (EditTextPreference) findPreference("apn_http_proxy");
        this.mPort = (EditTextPreference) findPreference("apn_http_port");
        this.mUser = (EditTextPreference) findPreference("apn_user");
        this.mServer = (EditTextPreference) findPreference("apn_server");
        this.mPassword = (EditTextPreference) findPreference("apn_password");
        this.mMmsProxy = (EditTextPreference) findPreference("apn_mms_proxy");
        this.mMmsPort = (EditTextPreference) findPreference("apn_mms_port");
        this.mMmsc = (EditTextPreference) findPreference("apn_mmsc");
        this.mMcc = (EditTextPreference) findPreference("apn_mcc");
        this.mMnc = (EditTextPreference) findPreference("apn_mnc");
        this.mApnType = (EditTextPreference) findPreference("apn_type");
        this.mAuthType = (DropDownPreference) findPreference("auth_type");
        this.mProtocol = (DropDownPreference) findPreference("apn_protocol");
        this.mRoamingProtocol = (DropDownPreference) findPreference("apn_roaming_protocol");
        this.mCarrierEnabled = (CheckBoxPreference) findPreference("carrier_enabled");
        this.mBearerMulti = (MultiSelectListPreference) findPreference("bearer_multi");
        this.mMvnoType = (DropDownPreference) findPreference("mvno_type");
        this.mMvnoMatchData = (EditTextPreference) findPreference("mvno_match_data");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateApnDataToDatabase$0(Uri uri, ContentValues contentValues) {
        if (!uri.equals(this.mCarrierUri)) {
            getContentResolver().update(uri, contentValues, null, null);
        } else if (getContentResolver().insert(this.mCarrierUri, contentValues) == null) {
            Log.e(TAG, "Can't add a new apn to database " + this.mCarrierUri);
        }
    }

    private String mvnoDescription(String str) {
        String[] strArr;
        int findIndexOfValue = this.mMvnoType.findIndexOfValue(str);
        String value = this.mMvnoType.getValue();
        if (findIndexOfValue == -1) {
            return null;
        }
        String[] stringArray = getResources().getStringArray(R.array.mvno_type_entries);
        boolean z = false;
        boolean z2 = this.mReadOnlyApn || ((strArr = this.mReadOnlyApnFields) != null && Arrays.asList(strArr).contains("mvno_match_data"));
        EditTextPreference editTextPreference = this.mMvnoMatchData;
        if (!z2 && findIndexOfValue != 0) {
            z = true;
        }
        editTextPreference.setEnabled(z);
        if (str != null && !str.equals(value)) {
            if (stringArray[findIndexOfValue].equals("SPN")) {
                TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
                TelephonyManager createForSubscriptionId = telephonyManager.createForSubscriptionId(this.mSubId);
                if (createForSubscriptionId != null) {
                    telephonyManager = createForSubscriptionId;
                }
                this.mMvnoMatchData.setText(telephonyManager.getSimOperatorName(this.mSubId));
            } else {
                if (stringArray[findIndexOfValue].equals("IMSI")) {
                    SubscriptionInfo accessibleSubscriptionInfo = this.mProxySubscriptionMgr.getAccessibleSubscriptionInfo(this.mSubId);
                    String objects = accessibleSubscriptionInfo == null ? "" : Objects.toString(accessibleSubscriptionInfo.getMccString(), "");
                    String objects2 = accessibleSubscriptionInfo != null ? Objects.toString(accessibleSubscriptionInfo.getMncString(), "") : "";
                    this.mMvnoMatchData.setText(objects + objects2 + "x");
                } else if (stringArray[findIndexOfValue].equals("GID")) {
                    TelephonyManager telephonyManager2 = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
                    TelephonyManager createForSubscriptionId2 = telephonyManager2.createForSubscriptionId(this.mSubId);
                    if (createForSubscriptionId2 != null) {
                        telephonyManager2 = createForSubscriptionId2;
                    }
                    this.mMvnoMatchData.setText(telephonyManager2.getGroupIdLevel1());
                } else {
                    this.mMvnoMatchData.setText("");
                }
            }
        }
        try {
            return stringArray[findIndexOfValue];
        } catch (ArrayIndexOutOfBoundsException unused) {
            return null;
        }
    }

    private String protocolDescription(String str, DropDownPreference dropDownPreference) {
        String upperCase = checkNull(str).toUpperCase();
        if (upperCase.equals("IPV4")) {
            upperCase = "IP";
        }
        int findIndexOfValue = dropDownPreference.findIndexOfValue(upperCase);
        if (findIndexOfValue == -1) {
            return null;
        }
        try {
            return getResources().getStringArray(R.array.apn_protocol_entries)[findIndexOfValue];
        } catch (ArrayIndexOutOfBoundsException unused) {
            return null;
        }
    }

    private void setCarrierCustomizedConfigToUi() {
        if (TextUtils.isEmpty(this.mApnType.getText()) && !ArrayUtils.isEmpty(this.mDefaultApnTypes)) {
            String editableApnType = getEditableApnType(this.mDefaultApnTypes);
            this.mApnType.setText(editableApnType);
            this.mApnType.setSummary(editableApnType);
        }
        String protocolDescription = protocolDescription(this.mDefaultApnProtocol, this.mProtocol);
        if (TextUtils.isEmpty(this.mProtocol.getValue()) && !TextUtils.isEmpty(protocolDescription)) {
            this.mProtocol.setValue(this.mDefaultApnProtocol);
            this.mProtocol.setSummary(protocolDescription);
        }
        String protocolDescription2 = protocolDescription(this.mDefaultApnRoamingProtocol, this.mRoamingProtocol);
        if (!TextUtils.isEmpty(this.mRoamingProtocol.getValue()) || TextUtils.isEmpty(protocolDescription2)) {
            return;
        }
        this.mRoamingProtocol.setValue(this.mDefaultApnRoamingProtocol);
        this.mRoamingProtocol.setSummary(protocolDescription2);
    }

    private void setLifecycleForAllControllers() {
        if (this.mProxySubscriptionMgr == null) {
            this.mProxySubscriptionMgr = ProxySubscriptionManager.getInstance(getContext());
        }
        this.mProxySubscriptionMgr.setLifecycle(getLifecycle());
    }

    private String starify(String str) {
        if (str == null || str.length() == 0) {
            return sNotSet;
        }
        int length = str.length();
        char[] cArr = new char[length];
        for (int i = 0; i < length; i++) {
            cArr[i] = '*';
        }
        return new String(cArr);
    }

    private void updateApnDataToDatabase(final Uri uri, final ContentValues contentValues) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.network.apn.ApnEditor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ApnEditor.this.lambda$updateApnDataToDatabase$0(uri, contentValues);
            }
        });
    }

    void fillUI(boolean z) {
        String str;
        if (z) {
            this.mName.setText(this.mApnData.getString(1));
            this.mApn.setText(this.mApnData.getString(2));
            this.mProxy.setText(this.mApnData.getString(3));
            this.mPort.setText(this.mApnData.getString(4));
            this.mUser.setText(this.mApnData.getString(5));
            this.mServer.setText(this.mApnData.getString(6));
            this.mPassword.setText(this.mApnData.getString(7));
            this.mMmsProxy.setText(this.mApnData.getString(12));
            this.mMmsPort.setText(this.mApnData.getString(13));
            this.mMmsc.setText(this.mApnData.getString(8));
            this.mMcc.setText(this.mApnData.getString(9));
            this.mMnc.setText(this.mApnData.getString(10));
            this.mApnType.setText(this.mApnData.getString(15));
            this.mCurMnc = this.mApnData.getString(10);
            this.mCurMcc = this.mApnData.getString(9);
            if (this.mNewApn) {
                SubscriptionInfo accessibleSubscriptionInfo = this.mProxySubscriptionMgr.getAccessibleSubscriptionInfo(this.mSubId);
                String mccString = accessibleSubscriptionInfo == null ? null : accessibleSubscriptionInfo.getMccString();
                String mncString = accessibleSubscriptionInfo == null ? null : accessibleSubscriptionInfo.getMncString();
                if (!TextUtils.isEmpty(mccString) && !TextUtils.isEmpty(mccString)) {
                    this.mMcc.setText(mccString);
                    this.mMnc.setText(mncString);
                    this.mCurMnc = mncString;
                    this.mCurMcc = mccString;
                }
            }
            int intValue = this.mApnData.getInteger(14, -1).intValue();
            if (intValue != -1) {
                this.mAuthType.setValueIndex(intValue);
            } else {
                String str2 = this.mCurMcc + this.mCurMnc;
                Log.d(TAG, "operatorNum:" + str2);
                if (this.mNewApn && !TextUtils.isEmpty(str2) && "44020".equalsIgnoreCase(str2)) {
                    this.mAuthType.setValueIndex(3);
                } else {
                    this.mAuthType.setValue(null);
                }
            }
            if (this.mNewApn) {
                this.mProtocol.setValue("IPV4V6");
                this.mRoamingProtocol.setValue("IPV4V6");
            } else {
                this.mProtocol.setValue(this.mApnData.getString(16));
                this.mRoamingProtocol.setValue(this.mApnData.getString(20));
            }
            this.mCarrierEnabled.setChecked(this.mApnData.getInteger(17, 1).intValue() == 1);
            this.mBearerInitialVal = this.mApnData.getInteger(18, 0).intValue();
            HashSet hashSet = new HashSet();
            int intValue2 = this.mApnData.getInteger(19, 0).intValue();
            if (intValue2 != 0) {
                int i = 1;
                while (intValue2 != 0) {
                    if ((intValue2 & 1) == 1) {
                        hashSet.add("" + i);
                    }
                    intValue2 >>= 1;
                    i++;
                }
            } else if (this.mBearerInitialVal == 0) {
                hashSet.add("0");
            }
            if (this.mBearerInitialVal != 0) {
                if (!hashSet.contains("" + this.mBearerInitialVal)) {
                    hashSet.add("" + this.mBearerInitialVal);
                }
            }
            this.mBearerMulti.setValues(hashSet);
            this.mMvnoType.setValue(this.mApnData.getString(21));
            this.mMvnoMatchData.setEnabled(false);
            this.mMvnoMatchData.setText(this.mApnData.getString(22));
            if (this.mNewApn && (str = this.mMvnoTypeStr) != null && this.mMvnoMatchDataStr != null) {
                this.mMvnoType.setValue(str);
                this.mMvnoMatchData.setText(this.mMvnoMatchDataStr);
            }
        }
        EditTextPreference editTextPreference = this.mName;
        editTextPreference.setSummary(checkNull(editTextPreference.getText()));
        EditTextPreference editTextPreference2 = this.mApn;
        editTextPreference2.setSummary(checkNull(editTextPreference2.getText()));
        EditTextPreference editTextPreference3 = this.mProxy;
        editTextPreference3.setSummary(checkNull(editTextPreference3.getText()));
        EditTextPreference editTextPreference4 = this.mPort;
        editTextPreference4.setSummary(checkNull(editTextPreference4.getText()));
        EditTextPreference editTextPreference5 = this.mUser;
        editTextPreference5.setSummary(checkNull(editTextPreference5.getText()));
        EditTextPreference editTextPreference6 = this.mServer;
        editTextPreference6.setSummary(checkNull(editTextPreference6.getText()));
        EditTextPreference editTextPreference7 = this.mPassword;
        editTextPreference7.setSummary(starify(editTextPreference7.getText()));
        EditTextPreference editTextPreference8 = this.mMmsProxy;
        editTextPreference8.setSummary(checkNull(editTextPreference8.getText()));
        EditTextPreference editTextPreference9 = this.mMmsPort;
        editTextPreference9.setSummary(checkNull(editTextPreference9.getText()));
        EditTextPreference editTextPreference10 = this.mMmsc;
        editTextPreference10.setSummary(checkNull(editTextPreference10.getText()));
        EditTextPreference editTextPreference11 = this.mMcc;
        editTextPreference11.setSummary(formatInteger(checkNull(editTextPreference11.getText())));
        EditTextPreference editTextPreference12 = this.mMnc;
        editTextPreference12.setSummary(formatInteger(checkNull(editTextPreference12.getText())));
        EditTextPreference editTextPreference13 = this.mApnType;
        editTextPreference13.setSummary(checkNull(editTextPreference13.getText()));
        String value = this.mAuthType.getValue();
        if (value != null) {
            int parseInt = Integer.parseInt(value);
            this.mAuthType.setValueIndex(parseInt);
            this.mAuthType.setSummary(getResources().getStringArray(R.array.apn_auth_entries)[parseInt]);
        } else {
            this.mAuthType.setSummary(sNotSet);
            this.mAuthType.setValueIndex(0);
        }
        DropDownPreference dropDownPreference = this.mProtocol;
        dropDownPreference.setSummary(checkNull(protocolDescription(dropDownPreference.getValue(), this.mProtocol)));
        DropDownPreference dropDownPreference2 = this.mRoamingProtocol;
        dropDownPreference2.setSummary(checkNull(protocolDescription(dropDownPreference2.getValue(), this.mRoamingProtocol)));
        MultiSelectListPreference multiSelectListPreference = this.mBearerMulti;
        multiSelectListPreference.setSummary(checkNull(bearerMultiDescription(multiSelectListPreference.getValues())));
        if (this.mMvnoType.getValue() == null) {
            this.mMvnoType.setValueIndex(0);
        }
        DropDownPreference dropDownPreference3 = this.mMvnoType;
        dropDownPreference3.setSummary(checkNull(mvnoDescription(dropDownPreference3.getValue())));
        EditTextPreference editTextPreference14 = this.mMvnoMatchData;
        editTextPreference14.setSummary(checkNullforMvnoValue(editTextPreference14.getText()));
        if (getResources().getBoolean(R.bool.config_allow_edit_carrier_enabled)) {
            this.mCarrierEnabled.setEnabled(true);
        } else {
            this.mCarrierEnabled.setEnabled(false);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0029  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x002e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    com.android.settings.network.apn.ApnEditor.ApnData getApnDataFromUri(android.net.Uri r7) {
        /*
            r6 = this;
            android.content.ContentResolver r0 = r6.getContentResolver()
            java.lang.String[] r2 = com.android.settings.network.apn.ApnEditor.sProjection
            r3 = 0
            r4 = 0
            r5 = 0
            r1 = r7
            android.database.Cursor r6 = r0.query(r1, r2, r3, r4, r5)
            if (r6 == 0) goto L26
            boolean r0 = r6.moveToFirst()     // Catch: java.lang.Throwable -> L1c
            if (r0 == 0) goto L26
            com.android.settings.network.apn.ApnEditor$ApnData r0 = new com.android.settings.network.apn.ApnEditor$ApnData     // Catch: java.lang.Throwable -> L1c
            r0.<init>(r7, r6)     // Catch: java.lang.Throwable -> L1c
            goto L27
        L1c:
            r7 = move-exception
            r6.close()     // Catch: java.lang.Throwable -> L21
            goto L25
        L21:
            r6 = move-exception
            r7.addSuppressed(r6)
        L25:
            throw r7
        L26:
            r0 = 0
        L27:
            if (r6 == 0) goto L2c
            r6.close()
        L2c:
            if (r0 != 0) goto L44
            java.lang.String r6 = com.android.settings.network.apn.ApnEditor.TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Can't get apnData from Uri "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r7 = r1.toString()
            android.util.Log.d(r6, r7)
        L44:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.apn.ApnEditor.getApnDataFromUri(android.net.Uri):com.android.settings.network.apn.ApnEditor$ApnData");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 13;
    }

    String getUserEnteredApnType() {
        String text = this.mApnType.getText();
        if (text != null) {
            text = text.trim();
        }
        if (TextUtils.isEmpty(text) || "*".equals(text)) {
            text = getEditableApnType(APN_TYPES);
        }
        Log.d(TAG, "getUserEnteredApnType: changed apn type to editable apn types: " + text);
        return text;
    }

    public boolean noModifyApn() {
        Uri uri = this.mApnData.getUri();
        if (uri == null) {
            return checkAllNoSet();
        }
        Cursor query = getContentResolver().query(uri, sProjection, null, null);
        boolean z = false;
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    if (TextUtils.equals(this.mName.getText(), query.getString(1)) && TextUtils.equals(this.mApn.getText(), query.getString(2)) && TextUtils.equals(this.mProxy.getText(), query.getString(3)) && TextUtils.equals(this.mPort.getText(), query.getString(4)) && TextUtils.equals(this.mUser.getText(), query.getString(5)) && TextUtils.equals(this.mServer.getText(), query.getString(6)) && TextUtils.equals(this.mPassword.getText(), query.getString(7)) && TextUtils.equals(this.mMmsProxy.getText(), query.getString(12)) && TextUtils.equals(this.mMmsPort.getText(), query.getString(13)) && TextUtils.equals(this.mMmsc.getText(), query.getString(8)) && TextUtils.equals(this.mMcc.getText(), this.mCurMcc) && TextUtils.equals(this.mMnc.getText(), this.mCurMnc) && TextUtils.equals(this.mApnType.getText(), query.getString(15)) && TextUtils.equals(this.mProtocol.getValue(), query.getString(16)) && TextUtils.equals(this.mRoamingProtocol.getValue(), query.getString(20))) {
                        if (!(this.mCarrierEnabled.isChecked() ^ (query.getInt(17) == 1)) && TextUtils.equals(this.mMvnoType.getValue(), query.getString(21))) {
                            if (TextUtils.equals(this.mMvnoMatchData.getText(), query.getString(22))) {
                                z = true;
                            }
                        }
                    }
                    query.close();
                    return z;
                }
            } catch (Throwable th) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (query != null) {
            query.close();
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLifecycleForAllControllers();
        Intent intent = getIntent();
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            finish();
            return;
        }
        this.mSubId = intent.getIntExtra(MiuiHeDuoHaoUtil.SUB_ID, -1);
        initApnEditorUi();
        getCarrierCustomizedConfig();
        Uri uri = null;
        if (action.equals("android.intent.action.EDIT")) {
            uri = intent.getData();
            if (!uri.isPathPrefixMatch(Telephony.Carriers.CONTENT_URI)) {
                Log.e(TAG, "Edit request not for carrier table. Uri: " + uri);
                finish();
                return;
            }
        } else if (!action.equals("android.intent.action.INSERT")) {
            finish();
            return;
        } else {
            Uri data = intent.getData();
            this.mCarrierUri = data;
            if (!data.isPathPrefixMatch(Telephony.Carriers.CONTENT_URI)) {
                Log.e(TAG, "Insert request not for carrier table. Uri: " + this.mCarrierUri);
                finish();
                return;
            }
            this.mNewApn = true;
            this.mMvnoTypeStr = intent.getStringExtra("mvno_type");
            this.mMvnoMatchDataStr = intent.getStringExtra("mvno_match_data");
        }
        if (uri != null) {
            this.mApnData = getApnDataFromUri(uri);
        } else {
            this.mApnData = new ApnData(sProjection.length);
        }
        ApnData apnData = this.mApnData;
        if (apnData == null) {
            finish();
            return;
        }
        boolean z = apnData.getInteger(23, 1).intValue() == 1;
        String str = TAG;
        Log.d(str, "onCreate: EDITED " + z);
        if (!z && (this.mApnData.getInteger(24, 1).intValue() == 0 || apnTypesMatch(this.mReadOnlyApnTypes, this.mApnData.getString(15)))) {
            Log.d(str, "onCreate: apnTypesMatch; read-only APN");
            this.mReadOnlyApn = true;
            disableAllFields();
        } else if (!ArrayUtils.isEmpty(this.mReadOnlyApnFields)) {
            disableFields(this.mReadOnlyApnFields);
        }
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            getPreferenceScreen().getPreference(i).setOnPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        if (!this.mNewApn && !this.mReadOnlyApn) {
            menu.add(0, 1, 0, R.string.menu_delete).setIcon(R.drawable.ic_delete);
        }
        if (!this.mReadOnlyApn) {
            menu.add(0, 2, 0, R.string.menu_save).setIcon(17301582);
        }
        menu.add(0, 3, 0, R.string.menu_cancel).setIcon(17301560);
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && i == 4) {
            if (validateAndSaveApnData()) {
                finish();
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            deleteApn();
            finish();
            return true;
        } else if (itemId == 2) {
            if (validateAndSaveApnData()) {
                finish();
            }
            return true;
        } else if (itemId != 3) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            finish();
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("auth_type".equals(key)) {
            try {
                int parseInt = Integer.parseInt((String) obj);
                this.mAuthType.setValueIndex(parseInt);
                this.mAuthType.setSummary(getResources().getStringArray(R.array.apn_auth_entries)[parseInt]);
                return true;
            } catch (NumberFormatException unused) {
                return false;
            }
        } else if ("apn_type".equals(key)) {
            String str = (String) obj;
            if (TextUtils.isEmpty(str) && !ArrayUtils.isEmpty(this.mDefaultApnTypes)) {
                str = getEditableApnType(this.mDefaultApnTypes);
            }
            if (TextUtils.isEmpty(str)) {
                return true;
            }
            this.mApnType.setSummary(str);
            return true;
        } else if ("apn_protocol".equals(key)) {
            String str2 = (String) obj;
            String protocolDescription = protocolDescription(str2, this.mProtocol);
            if (protocolDescription == null) {
                return false;
            }
            this.mProtocol.setSummary(protocolDescription);
            this.mProtocol.setValue(str2);
            return true;
        } else if ("apn_roaming_protocol".equals(key)) {
            String str3 = (String) obj;
            String protocolDescription2 = protocolDescription(str3, this.mRoamingProtocol);
            if (protocolDescription2 == null) {
                return false;
            }
            this.mRoamingProtocol.setSummary(protocolDescription2);
            this.mRoamingProtocol.setValue(str3);
            return true;
        } else if ("bearer_multi".equals(key)) {
            Set<String> set = (Set) obj;
            String bearerMultiDescription = bearerMultiDescription(set);
            if (bearerMultiDescription == null) {
                return false;
            }
            this.mBearerMulti.setValues(set);
            this.mBearerMulti.setSummary(bearerMultiDescription);
            return true;
        } else if (!"mvno_type".equals(key)) {
            if ("apn_password".equals(key)) {
                this.mPassword.setSummary(starify(obj != null ? String.valueOf(obj) : ""));
                return true;
            } else if ("carrier_enabled".equals(key)) {
                return true;
            } else {
                preference.setSummary(checkNull(obj != null ? String.valueOf(obj) : null));
                return true;
            }
        } else {
            String str4 = (String) obj;
            String mvnoDescription = mvnoDescription(str4);
            if (mvnoDescription == null) {
                return false;
            }
            this.mMvnoType.setValue(str4);
            this.mMvnoType.setSummary(mvnoDescription);
            EditTextPreference editTextPreference = this.mMvnoMatchData;
            editTextPreference.setSummary(checkNullforMvnoValue(editTextPreference.getText()));
            return true;
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        if (this.mApnData != null) {
            fillUI(bundle == null);
        }
        setCarrierCustomizedConfigToUi();
    }

    boolean setIntValueAndCheckIfDiff(ContentValues contentValues, String str, int i, boolean z, int i2) {
        boolean z2 = z || i != this.mApnData.getInteger(i2).intValue();
        if (z2) {
            contentValues.put(str, Integer.valueOf(i));
        }
        return z2;
    }

    boolean setStringValueAndCheckIfDiff(ContentValues contentValues, String str, String str2, boolean z, int i) {
        String string = this.mApnData.getString(i);
        boolean z2 = z || (!(TextUtils.isEmpty(str2) && TextUtils.isEmpty(string)) && (str2 == null || !str2.equals(string)));
        if (z2 && str2 != null) {
            contentValues.put(str, str2);
        }
        return z2;
    }

    void showError() {
        ErrorDialog.showError(this);
    }

    public boolean validateAndSaveApnData() {
        int i;
        int i2;
        if (this.mReadOnlyApn) {
            return true;
        }
        String checkNotSet = checkNotSet(this.mName.getText());
        String checkNotSet2 = checkNotSet(this.mApn.getText());
        String checkNotSet3 = checkNotSet(this.mMcc.getText());
        String checkNotSet4 = checkNotSet(this.mMnc.getText());
        if (validateApnData() != null) {
            showError();
            return false;
        }
        ContentValues contentValues = new ContentValues();
        boolean stringValueAndCheckIfDiff = setStringValueAndCheckIfDiff(contentValues, "mmsc", checkNotSet(this.mMmsc.getText()), setStringValueAndCheckIfDiff(contentValues, "password", checkNotSet(this.mPassword.getText()), setStringValueAndCheckIfDiff(contentValues, "server", checkNotSet(this.mServer.getText()), setStringValueAndCheckIfDiff(contentValues, "user", checkNotSet(this.mUser.getText()), setStringValueAndCheckIfDiff(contentValues, "mmsport", checkNotSet(this.mMmsPort.getText()), setStringValueAndCheckIfDiff(contentValues, "mmsproxy", checkNotSet(this.mMmsProxy.getText()), setStringValueAndCheckIfDiff(contentValues, "port", checkNotSet(this.mPort.getText()), setStringValueAndCheckIfDiff(contentValues, "proxy", checkNotSet(this.mProxy.getText()), setStringValueAndCheckIfDiff(contentValues, "apn", checkNotSet2, setStringValueAndCheckIfDiff(contentValues, "name", checkNotSet, this.mNewApn, 1), 2), 3), 4), 12), 13), 5), 6), 7), 8);
        String value = this.mAuthType.getValue();
        if (value != null) {
            stringValueAndCheckIfDiff = setIntValueAndCheckIfDiff(contentValues, "authtype", Integer.parseInt(value), stringValueAndCheckIfDiff, 14);
        }
        boolean stringValueAndCheckIfDiff2 = setStringValueAndCheckIfDiff(contentValues, "mnc", checkNotSet4, setStringValueAndCheckIfDiff(contentValues, "mcc", checkNotSet3, setStringValueAndCheckIfDiff(contentValues, "type", checkNotSet(getUserEnteredApnType()), setStringValueAndCheckIfDiff(contentValues, "roaming_protocol", checkNotSet(this.mRoamingProtocol.getValue()), setStringValueAndCheckIfDiff(contentValues, "protocol", checkNotSet(this.mProtocol.getValue()), stringValueAndCheckIfDiff, 16), 20), 15), 9), 10);
        contentValues.put("numeric", checkNotSet3 + checkNotSet4);
        String str = this.mCurMnc;
        if (str != null && this.mCurMcc != null && str.equals(checkNotSet4) && this.mCurMcc.equals(checkNotSet3)) {
            contentValues.put("current", (Integer) 1);
        }
        Iterator<String> it = this.mBearerMulti.getValues().iterator();
        int i3 = 0;
        while (true) {
            if (!it.hasNext()) {
                i = i3;
                break;
            }
            String next = it.next();
            if (Integer.parseInt(next) == 0) {
                i = 0;
                break;
            }
            i3 |= getBitmaskForTech(Integer.parseInt(next));
        }
        boolean intValueAndCheckIfDiff = setIntValueAndCheckIfDiff(contentValues, "carrier_enabled", this.mCarrierEnabled.isChecked() ? 1 : 0, setStringValueAndCheckIfDiff(contentValues, "mvno_match_data", checkNotSet(this.mMvnoMatchData.getText()), setStringValueAndCheckIfDiff(contentValues, "mvno_type", checkNotSet(this.mMvnoType.getValue()), setIntValueAndCheckIfDiff(contentValues, "bearer", (i == 0 || (i2 = this.mBearerInitialVal) == 0 || !bitmaskHasTech(i, i2)) ? 0 : this.mBearerInitialVal, setIntValueAndCheckIfDiff(contentValues, "bearer_bitmask", i, stringValueAndCheckIfDiff2, 19), 18), 21), 22), 17);
        contentValues.put("edited", (Integer) 1);
        if (intValueAndCheckIfDiff) {
            updateApnDataToDatabase(this.mApnData.getUri() == null ? this.mCarrierUri : this.mApnData.getUri(), contentValues);
        }
        return true;
    }

    String validateApnData() {
        String checkNotSet = checkNotSet(this.mName.getText());
        String checkNotSet2 = checkNotSet(this.mApn.getText());
        String checkNotSet3 = checkNotSet(this.mMcc.getText());
        String checkNotSet4 = checkNotSet(this.mMnc.getText());
        String text = this.mApnType.getText();
        String str = null;
        if (TextUtils.isEmpty(checkNotSet)) {
            str = getResources().getString(R.string.error_name_empty);
        } else if (TextUtils.isEmpty(checkNotSet2)) {
            str = getResources().getString(R.string.error_apn_empty);
        } else if (checkNotSet3 == null || checkNotSet3.length() != 3) {
            str = getResources().getString(R.string.error_mcc_not3);
        } else if (checkNotSet4 == null || (checkNotSet4.length() & 65534) != 2) {
            str = getResources().getString(R.string.error_mnc_not23);
        } else {
            String str2 = "";
            if ("es_telefonica".equals(SystemProperties.get("ro.miui.customized.region", "")) && !TextUtils.isEmpty(text)) {
                String[] split = text.trim().split(",");
                Log.i(TAG, "validateApnData addApnType = " + split);
                int length = split.length;
                for (int i = 0; i < length; i++) {
                    if ("XCAP".equalsIgnoreCase(split[i].trim())) {
                        str = getResources().getString(R.string.error_xcap_not_add);
                    }
                }
            } else if (miui.telephony.TelephonyManager.isCustForJpKd()) {
                String[] strArr = {".au-net.ne.jp"};
                int i2 = 0;
                while (true) {
                    if (i2 >= 1) {
                        break;
                    }
                    String str3 = strArr[i2];
                    if (checkNotSet2.contains(str3)) {
                        str2 = str3;
                        break;
                    }
                    i2++;
                }
                if (!TextUtils.isEmpty(str2)) {
                    str = getResources().getString(R.string.error_apn_restrict_string, str2);
                }
            }
        }
        if (str == null && !ArrayUtils.isEmpty(this.mReadOnlyApnTypes) && apnTypesMatch(this.mReadOnlyApnTypes, getUserEnteredApnType())) {
            StringBuilder sb = new StringBuilder();
            for (String str4 : this.mReadOnlyApnTypes) {
                sb.append(str4);
                sb.append(", ");
                Log.d(TAG, "validateApnData: appending type: " + str4);
            }
            if (sb.length() >= 2) {
                sb.delete(sb.length() - 2, sb.length());
            }
            str = String.format(getResources().getString(R.string.error_adding_apn_type), sb);
        }
        return (miui.telephony.TelephonyManager.isCustForKrKt() && "45008".equals(((TelephonyManager) getSystemService("phone")).getSimOperator(this.mSubId)) && ArrayUtils.contains(getResources().getStringArray(R.array.apn_not_allowed_entries), checkNotSet2)) ? getResources().getString(R.string.error_apn_not_allowed) : str;
    }
}
