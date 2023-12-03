package com.android.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkPolicyManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MiuiSettings;
import android.provider.Telephony;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.ims.ImsManager;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.wifi.AutoConnectUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;
import com.milink.api.v1.type.DeviceType;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ResetNetwork extends SettingsPreferenceFragment implements FragmentResultCallBack {
    CheckBox mEsimCheckbox;
    View mEsimContainer;
    private MenuItem mMenuItem;
    private PreferenceCategory mRadioButtonPreferenceGroup;
    private List<SubscriptionInfo> mSubscriptions;
    private int mChoice = 0;
    private final View.OnClickListener mInitiateListener = new View.OnClickListener() { // from class: com.android.settings.ResetNetwork.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (ResetNetwork.this.runKeyguardConfirmation(55)) {
                return;
            }
            ResetNetwork.this.showFinalConfirmation();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ResetNetWorkTask extends AsyncTask<Integer, Void, Void> {
        private Context mContext;
        private WeakReference<ResetNetwork> mWeakRef;

        public ResetNetWorkTask(ResetNetwork resetNetwork) {
            this.mContext = null;
            this.mWeakRef = new WeakReference<>(resetNetwork);
            if (resetNetwork.getActivity() != null) {
                this.mContext = resetNetwork.getActivity().getApplicationContext();
            }
        }

        private void cleanUpSmsRawTable(Context context) {
            context.getContentResolver().delete(Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, "raw/permanentDelete"), null, null);
        }

        private void doResetNetwork(int i) {
            BluetoothAdapter adapter;
            Context context = this.mContext;
            if (context == null) {
                return;
            }
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivityManager != null) {
                connectivityManager.factoryReset();
            }
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
            if (wifiManager != null) {
                wifiManager.factoryReset();
            }
            HashSet disableWifiAutoConnectSsid = MiuiSettings.System.getDisableWifiAutoConnectSsid(this.mContext);
            AutoConnectUtils autoConnectUtils = AutoConnectUtils.getInstance(this.mContext);
            Iterator it = disableWifiAutoConnectSsid.iterator();
            while (it.hasNext()) {
                String str = (String) it.next();
                if (!autoConnectUtils.isAutoConnect(str)) {
                    autoConnectUtils.enableAutoConnect(this.mContext, str, true);
                }
                autoConnectUtils.removeNoSecretWifi(this.mContext, str);
            }
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
            if (telephonyManager != null) {
                telephonyManager.factoryReset(i);
            }
            NetworkPolicyManager networkPolicyManager = (NetworkPolicyManager) this.mContext.getSystemService("netpolicy");
            if (networkPolicyManager != null && telephonyManager != null) {
                try {
                    networkPolicyManager.factoryReset(telephonyManager.getSubscriberId(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService(DeviceType.BLUETOOTH);
            if (bluetoothManager != null && (adapter = bluetoothManager.getAdapter()) != null) {
                adapter.factoryReset();
                LocalBluetoothManager localBluetoothManager = LocalBluetoothManager.getInstance(this.mContext, null);
                if (localBluetoothManager != null) {
                    localBluetoothManager.getCachedDeviceManager().clearAllDevices();
                }
            }
            ImsManager.getInstance(this.mContext, SubscriptionManager.getPhoneId(i)).factoryReset();
            restoreDefaultApn(this.mContext, i);
            cleanUpSmsRawTable(this.mContext);
        }

        private void restoreDefaultApn(Context context, int i) {
            Uri parse = Uri.parse("content://telephony/carriers/restore");
            if (SubscriptionManager.isUsableSubIdValue(i)) {
                parse = Uri.withAppendedPath(parse, "subId/" + String.valueOf(i));
            }
            context.getContentResolver().delete(parse, null, null);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Integer... numArr) {
            doResetNetwork(numArr[0].intValue());
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            super.onPostExecute((ResetNetWorkTask) r1);
            WeakReference<ResetNetwork> weakReference = this.mWeakRef;
            if (weakReference == null || weakReference.get() == null) {
                return;
            }
            this.mWeakRef.get().resetComplete();
        }
    }

    private void establishInitialState(List<SubscriptionInfo> list) {
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().removeAll();
        }
        addPreferencesFromResource(R.xml.miui_reset_network);
        this.mRadioButtonPreferenceGroup = (PreferenceCategory) findPreference("subscription_chooser");
        this.mSubscriptions = list;
        if (list == null || list.size() <= 0) {
            getPreferenceScreen().removePreference(this.mRadioButtonPreferenceGroup);
            this.mRadioButtonPreferenceGroup = null;
            return;
        }
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        if (!SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
            defaultDataSubscriptionId = SubscriptionManager.getDefaultVoiceSubscriptionId();
        }
        if (!SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
            defaultDataSubscriptionId = SubscriptionManager.getDefaultSmsSubscriptionId();
        }
        if (!SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
            defaultDataSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        }
        int size = this.mSubscriptions.size();
        ArrayList arrayList = new ArrayList();
        int i = 0;
        for (SubscriptionInfo subscriptionInfo : this.mSubscriptions) {
            if (subscriptionInfo.getSubscriptionId() == defaultDataSubscriptionId) {
                i = arrayList.size();
            }
            String charSequence = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, getContext()).toString();
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = subscriptionInfo.getNumber();
            }
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = subscriptionInfo.getCarrierName().toString();
            }
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = String.format("MCC:%s MNC:%s Slot:%s Id:%s", Integer.valueOf(subscriptionInfo.getMcc()), Integer.valueOf(subscriptionInfo.getMnc()), Integer.valueOf(subscriptionInfo.getSimSlotIndex()), Integer.valueOf(subscriptionInfo.getSubscriptionId()));
            }
            arrayList.add(charSequence);
        }
        this.mChoice = i;
        if (size <= 1) {
            getPreferenceScreen().removePreference(this.mRadioButtonPreferenceGroup);
            this.mRadioButtonPreferenceGroup = null;
            return;
        }
        final int i2 = 0;
        while (i2 < size) {
            RadioButtonPreference radioButtonPreference = new RadioButtonPreference(getPrefContext());
            radioButtonPreference.setTitle((CharSequence) arrayList.get(i2));
            radioButtonPreference.setChecked(this.mChoice == i2);
            radioButtonPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.ResetNetwork.2
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    if (!((Boolean) obj).booleanValue()) {
                        return ResetNetwork.this.mChoice != i2;
                    }
                    int i3 = ResetNetwork.this.mChoice;
                    int i4 = i2;
                    if (i3 == i4) {
                        return false;
                    }
                    ResetNetwork.this.mChoice = i4;
                    for (int i5 = 0; i5 < ResetNetwork.this.mRadioButtonPreferenceGroup.getPreferenceCount(); i5++) {
                        if (i5 != i2) {
                            ((RadioButtonPreference) ResetNetwork.this.mRadioButtonPreferenceGroup.getPreference(i5)).setChecked(false);
                        }
                    }
                    return true;
                }
            });
            this.mRadioButtonPreferenceGroup.addPreference(radioButtonPreference);
            i2++;
        }
    }

    private List<SubscriptionInfo> getActiveSubscriptionInfoList() {
        SubscriptionManager subscriptionManager = (SubscriptionManager) getActivity().getSystemService(SubscriptionManager.class);
        if (subscriptionManager == null) {
            Log.w("ResetNetwork", "No SubscriptionManager");
            return Collections.emptyList();
        }
        return (List) Optional.ofNullable(subscriptionManager.getActiveSubscriptionInfoList()).orElse(Collections.emptyList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$1(DialogInterface dialogInterface) {
        getActivity().finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFinalConfirmation$0(int i, DialogInterface dialogInterface, int i2) {
        if (Utils.isMonkeyRunning() || getActivity() == null) {
            return;
        }
        this.mMenuItem.setEnabled(false);
        getActivity().getApplicationContext();
        new ResetNetWorkTask(this).execute(Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetComplete() {
        if (isAdded()) {
            this.mMenuItem.setEnabled(true);
            Toast.makeText(getActivity(), R.string.reset_network_complete_toast, 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean runKeyguardConfirmation(int i) {
        return new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(i).setTitle(getActivity().getResources().getText(R.string.reset_network_title)).show();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 83;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 55) {
            return;
        }
        if (i2 == -1) {
            showFinalConfirmation();
        } else {
            establishInitialState(getActiveSubscriptionInfoList());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(R.string.reset_network_title);
        setHasOptionsMenu(true);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        MenuItem add = menu.add(0, 1, 0, R.string.reset_network_button_text);
        this.mMenuItem = add;
        add.setIcon(R.drawable.action_button_clear);
        this.mMenuItem.setShowAsAction(1);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        UserManager userManager = UserManager.get(getActivity());
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_network_reset", UserHandle.myUserId());
        if (!userManager.isAdminUser() || RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_network_reset", UserHandle.myUserId())) {
            return layoutInflater.inflate(R.layout.network_reset_disallowed_screen, (ViewGroup) null);
        }
        if (checkIfRestrictionEnforced != null) {
            new ActionDisabledByAdminDialogHelper(getActivity()).prepareDialogBuilder("no_network_reset", checkIfRestrictionEnforced).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.ResetNetwork$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ResetNetwork.this.lambda$onCreateView$1(dialogInterface);
                }
            }).show();
            return new View(getContext());
        }
        establishInitialState(getActiveSubscriptionInfoList());
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onFragmentResult(int i, Bundle bundle) {
        if (i == 55 && bundle != null && bundle.getInt("miui_security_fragment_result") == 0) {
            showFinalConfirmation();
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            this.mInitiateListener.onClick(null);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        List<SubscriptionInfo> activeSubscriptionInfoList = getActiveSubscriptionInfoList();
        List<SubscriptionInfo> list = this.mSubscriptions;
        if (list != null && list.size() == activeSubscriptionInfoList.size() && this.mSubscriptions.containsAll(activeSubscriptionInfoList)) {
            return;
        }
        Log.d("ResetNetwork", "subcription list changed");
        establishInitialState(activeSubscriptionInfoList);
    }

    void showFinalConfirmation() {
        List<SubscriptionInfo> list = this.mSubscriptions;
        final int subscriptionId = (list == null || list.size() <= 0) ? -1 : this.mSubscriptions.get(this.mChoice).getSubscriptionId();
        new AlertDialog.Builder(getContext()).setTitle(R.string.reset_network_confirm_title).setMessage(R.string.reset_network_final_desc).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.ResetNetwork$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ResetNetwork.this.lambda$showFinalConfirmation$0(subscriptionId, dialogInterface, i);
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }
}
