package com.android.settings.nfc;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.nfc.NfcPaymentPreference;
import com.android.settings.nfc.PaymentBackend;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.List;
import miuix.appcompat.app.AlertDialog;
import miuix.preference.DropDownPreference;

/* loaded from: classes2.dex */
public class NfcPaymentPreferenceController extends BasePreferenceController implements PaymentBackend.Callback, View.OnClickListener, NfcPaymentPreference.Listener, LifecycleObserver, OnStart, OnStop, Preference.OnPreferenceChangeListener {
    private static final String KEY = "nfc_payment";
    private static final String TAG = "NfcPaymentController";
    private String[] labels;
    private final NfcPaymentAdapter mAdapter;
    private PaymentBackend mPaymentBackend;
    private DropDownPreference mPreference;
    private ImageView mSettingsButtonView;
    private int selectedPosition;
    private String[] summaries;
    private String[] valueIndex;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class NfcPaymentAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        private PaymentBackend.PaymentAppInfo[] appInfos;
        private final LayoutInflater mLayoutInflater;

        /* loaded from: classes2.dex */
        private class ViewHolder {
            public View contentView;
            public RadioButton radioButton;
            public TextView textView1;
            public TextView textView2;

            private ViewHolder() {
            }
        }

        public NfcPaymentAdapter(Context context) {
            this.mLayoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        private void makeDefault(PaymentBackend.PaymentAppInfo paymentAppInfo) {
            if (paymentAppInfo.isDefault) {
                return;
            }
            NfcPaymentPreferenceController.this.mPaymentBackend.setDefaultPaymentApp(paymentAppInfo.componentName);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            PaymentBackend.PaymentAppInfo[] paymentAppInfoArr = this.appInfos;
            if (paymentAppInfoArr != null) {
                return paymentAppInfoArr.length;
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public PaymentBackend.PaymentAppInfo getItem(int i) {
            return this.appInfos[i];
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return this.appInfos[i].componentName.hashCode();
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            PaymentBackend.PaymentAppInfo paymentAppInfo = this.appInfos[i];
            if (view == null) {
                view = this.mLayoutInflater.inflate(R.layout.nfc_payment_option, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.radioButton = (RadioButton) view.findViewById(R.id.button);
                viewHolder.textView1 = (TextView) view.findViewById(R.id.text1);
                viewHolder.textView2 = (TextView) view.findViewById(R.id.text2);
                viewHolder.contentView = view.findViewById(R.id.content);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.radioButton.setOnCheckedChangeListener(null);
            viewHolder.radioButton.setChecked(paymentAppInfo.isDefault);
            viewHolder.radioButton.setOnCheckedChangeListener(this);
            viewHolder.radioButton.setTag(paymentAppInfo);
            viewHolder.textView1.setText(paymentAppInfo.label);
            viewHolder.textView1.setContentDescription(paymentAppInfo.label);
            viewHolder.textView2.setText(paymentAppInfo.description);
            viewHolder.textView2.setContentDescription(paymentAppInfo.description);
            viewHolder.contentView.setTag(R.id.content, paymentAppInfo);
            viewHolder.contentView.setOnClickListener(this);
            return view;
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            makeDefault((PaymentBackend.PaymentAppInfo) compoundButton.getTag());
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            makeDefault((PaymentBackend.PaymentAppInfo) view.getTag(R.id.content));
        }

        public void updateApps(PaymentBackend.PaymentAppInfo[] paymentAppInfoArr) {
            this.appInfos = paymentAppInfoArr;
            notifyDataSetChanged();
        }
    }

    public NfcPaymentPreferenceController(Context context, String str) {
        super(context, str);
        this.mAdapter = new NfcPaymentAdapter(context);
    }

    private String[] getLabels() {
        CharSequence charSequence;
        List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
        if (paymentAppInfos == null || paymentAppInfos.size() <= 0) {
            return null;
        }
        String[] strArr = new String[paymentAppInfos.size() + 1];
        strArr[0] = this.mContext.getText(R.string.nfc_payment_default_not_set).toString();
        for (int i = 0; i < paymentAppInfos.size(); i++) {
            PaymentBackend.PaymentAppInfo paymentAppInfo = paymentAppInfos.get(i);
            if (paymentAppInfo != null && (charSequence = paymentAppInfo.label) != null) {
                strArr[i + 1] = charSequence.toString();
            }
        }
        return strArr;
    }

    private int getSelectAppIndex() {
        List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
        PaymentBackend.PaymentAppInfo defaultApp = this.mPaymentBackend.getDefaultApp();
        int i = -1;
        if (paymentAppInfos == null || defaultApp == null || paymentAppInfos.isEmpty()) {
            return -1;
        }
        int i2 = 0;
        while (true) {
            if (i2 >= paymentAppInfos.size()) {
                break;
            } else if (paymentAppInfos.get(i2).label.toString().equals(defaultApp.label.toString())) {
                i = i2;
                break;
            } else {
                i2++;
            }
        }
        return i + 1;
    }

    private String[] getSummaries() {
        CharSequence charSequence;
        List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
        if (paymentAppInfos == null || paymentAppInfos.size() <= 0) {
            return null;
        }
        String[] strArr = new String[paymentAppInfos.size() + 1];
        strArr[0] = "";
        for (int i = 0; i < paymentAppInfos.size(); i++) {
            PaymentBackend.PaymentAppInfo paymentAppInfo = paymentAppInfos.get(i);
            if (paymentAppInfo != null && (charSequence = paymentAppInfo.description) != null) {
                strArr[i + 1] = charSequence.toString();
            }
        }
        return strArr;
    }

    private String[] getValueIndex(String[] strArr) {
        if (strArr == null || strArr.length <= 0) {
            return null;
        }
        int length = strArr.length + 1;
        String[] strArr2 = new String[length];
        for (int i = 0; i < length; i++) {
            strArr2[i] = Integer.toString(i);
        }
        return strArr2;
    }

    private void makeDefault(PaymentBackend.PaymentAppInfo paymentAppInfo) {
        if (paymentAppInfo.isDefault) {
            return;
        }
        this.mPaymentBackend.setDefaultPaymentApp(paymentAppInfo.componentName);
    }

    private void updateSettingsVisibility() {
        if (this.mSettingsButtonView != null) {
            PaymentBackend.PaymentAppInfo defaultApp = this.mPaymentBackend.getDefaultApp();
            if (defaultApp == null || defaultApp.settingsComponent == null) {
                this.mSettingsButtonView.setVisibility(8);
            } else {
                this.mSettingsButtonView.setVisibility(0);
            }
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        String[] strArr;
        super.displayPreference(preferenceScreen);
        this.labels = getLabels();
        this.summaries = getSummaries();
        this.valueIndex = getValueIndex(this.labels);
        this.selectedPosition = getSelectAppIndex();
        DropDownPreference dropDownPreference = (DropDownPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = dropDownPreference;
        String[] strArr2 = this.labels;
        if (strArr2 == null || (strArr = this.summaries) == null || strArr2.length != strArr.length) {
            return;
        }
        dropDownPreference.setEntries(strArr2);
        this.mPreference.setEntryValues(this.valueIndex);
        this.mPreference.setOnPreferenceChangeListener(this);
        if (!RegionUtils.IS_JP_KDDI) {
            this.mPreference.setSummaries(this.summaries);
        }
        if (getSelectAppIndex() != -1) {
            this.mPreference.setValueIndex(this.selectedPosition);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc") && NfcAdapter.getDefaultAdapter(this.mContext) != null) {
            if (this.mPaymentBackend == null) {
                this.mPaymentBackend = new PaymentBackend(this.mContext);
            }
            List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
            return (paymentAppInfos == null || paymentAppInfos.isEmpty()) ? 3 : 0;
        }
        return 3;
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
        return "";
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
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

    @Override // com.android.settings.nfc.NfcPaymentPreference.Listener
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        this.mSettingsButtonView = (ImageView) preferenceViewHolder.findViewById(R.id.settings_button);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        PaymentBackend.PaymentAppInfo defaultApp = this.mPaymentBackend.getDefaultApp();
        if (defaultApp == null || defaultApp.settingsComponent == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setComponent(defaultApp.settingsComponent);
        intent.addFlags(268435456);
        try {
            this.mContext.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Log.e(TAG, "Settings activity not found.");
        }
    }

    @Override // com.android.settings.nfc.PaymentBackend.Callback
    public void onPaymentAppsChanged() {
        updateState(this.mPreference);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (KEY.equals(preference.getKey())) {
            int parseInt = Integer.parseInt((String) obj);
            List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
            if (parseInt == 0) {
                this.mPaymentBackend.setDefaultPaymentApp(null);
            } else {
                makeDefault(paymentAppInfos.get(parseInt - 1));
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.nfc.NfcPaymentPreference.Listener
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        PaymentBackend paymentBackend = this.mPaymentBackend;
        if (paymentBackend != null) {
            paymentBackend.registerCallback(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        PaymentBackend paymentBackend = this.mPaymentBackend;
        if (paymentBackend != null) {
            paymentBackend.unregisterCallback(this);
        }
    }

    public void setPaymentBackend(PaymentBackend paymentBackend) {
        this.mPaymentBackend = paymentBackend;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
        if (paymentAppInfos != null) {
            this.mAdapter.updateApps((PaymentBackend.PaymentAppInfo[]) paymentAppInfos.toArray(new PaymentBackend.PaymentAppInfo[paymentAppInfos.size()]));
        }
        super.updateState(preference);
        updateSettingsVisibility();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
