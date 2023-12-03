package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.CarrierConfigManager;
import android.telephony.PinResult;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import com.android.settings.EditPinPreference;
import com.android.settings.network.ProxySubscriptionManager;
import java.util.List;
import miui.telephony.SubscriptionManager;

/* loaded from: classes.dex */
public class IccLockSettings extends SettingsPreferenceFragment implements EditPinPreference.OnPinEnteredListener, Preference.OnPreferenceChangeListener {
    private String mError;
    private String mNewPin;
    private String mOldPin;
    private String mPin;
    private EditPinPreference mPinDialog;
    private CheckBoxPreference mPinToggle;
    private ProxySubscriptionManager mProxySubscriptionMgr;
    private Resources mRes;
    private int mSubId;
    private TabHost mTabHost;
    private TelephonyManager mTelephonyManager;
    private boolean mToState;
    private int mDialogState = 0;
    private int mSlotId = -1;
    private Handler mHandler = new Handler() { // from class: com.android.settings.IccLockSettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 102) {
                return;
            }
            IccLockSettings.this.updatePreferences();
        }
    };
    private final BroadcastReceiver mSimStateReceiver = new BroadcastReceiver() { // from class: com.android.settings.IccLockSettings.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
                IccLockSettings.this.mHandler.sendMessage(IccLockSettings.this.mHandler.obtainMessage(102));
            }
        }
    };
    private TabHost.OnTabChangeListener mTabListener = new TabHost.OnTabChangeListener() { // from class: com.android.settings.IccLockSettings.4
        @Override // android.widget.TabHost.OnTabChangeListener
        public void onTabChanged(String str) {
            IccLockSettings iccLockSettings = IccLockSettings.this;
            iccLockSettings.mSlotId = iccLockSettings.getSlotIndexFromTag(str);
            IccLockSettings.this.updatePreferences();
        }
    };
    private TabHost.TabContentFactory mEmptyTabContent = new TabHost.TabContentFactory() { // from class: com.android.settings.IccLockSettings.5
        @Override // android.widget.TabHost.TabContentFactory
        public View createTabContent(String str) {
            return new View(IccLockSettings.this.mTabHost.getContext());
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ChangeIccLockPin extends AsyncTask<Void, Void, PinResult> {
        private final String mNewPin;
        private final String mOldPin;

        private ChangeIccLockPin(String str, String str2) {
            this.mOldPin = str;
            this.mNewPin = str2;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public PinResult doInBackground(Void... voidArr) {
            IccLockSettings iccLockSettings = IccLockSettings.this;
            iccLockSettings.mTelephonyManager = iccLockSettings.mTelephonyManager.createForSubscriptionId(IccLockSettings.this.mSubId);
            return IccLockSettings.this.mTelephonyManager.changeIccLockPin(this.mOldPin, this.mNewPin);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(PinResult pinResult) {
            IccLockSettings.this.iccPinChanged(pinResult.getResult() == 0, pinResult.getAttemptsRemaining());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SetIccLockEnabled extends AsyncTask<Void, Void, PinResult> {
        private final String mPin;
        private final boolean mState;

        private SetIccLockEnabled(boolean z, String str) {
            this.mState = z;
            this.mPin = str;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public PinResult doInBackground(Void... voidArr) {
            IccLockSettings iccLockSettings = IccLockSettings.this;
            iccLockSettings.mTelephonyManager = iccLockSettings.mTelephonyManager.createForSubscriptionId(IccLockSettings.this.mSubId);
            return IccLockSettings.this.mTelephonyManager.setIccLockEnabled(this.mState, this.mPin);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(PinResult pinResult) {
            IccLockSettings.this.iccLockChanged(pinResult.getResult() == 0, pinResult.getAttemptsRemaining());
        }
    }

    private void createCustomTextToast(CharSequence charSequence) {
        final View inflate = ((LayoutInflater) getSystemService("layout_inflater")).inflate(17367354, (ViewGroup) null);
        ((TextView) inflate.findViewById(16908299)).setText(charSequence);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        int absoluteGravity = Gravity.getAbsoluteGravity(getContext().getResources().getInteger(17694941), inflate.getContext().getResources().getConfiguration().getLayoutDirection());
        layoutParams.gravity = absoluteGravity;
        if ((absoluteGravity & 7) == 7) {
            layoutParams.horizontalWeight = 1.0f;
        }
        if ((absoluteGravity & 112) == 112) {
            layoutParams.verticalWeight = 1.0f;
        }
        layoutParams.y = getContext().getResources().getDimensionPixelSize(17105611);
        layoutParams.height = -2;
        layoutParams.width = -2;
        layoutParams.format = -3;
        layoutParams.windowAnimations = 16973828;
        layoutParams.type = 2017;
        layoutParams.setFitInsetsTypes(layoutParams.getFitInsetsTypes() & (~WindowInsets.Type.statusBars()));
        layoutParams.setTitle(charSequence);
        layoutParams.flags = 152;
        final WindowManager windowManager = (WindowManager) getSystemService("window");
        windowManager.addView(inflate, layoutParams);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.IccLockSettings.3
            @Override // java.lang.Runnable
            public void run() {
                windowManager.removeViewImmediate(inflate);
            }
        }, 7000L);
    }

    private String getPinPasswordErrorMessage(int i) {
        return i == 0 ? this.mRes.getString(R.string.wrong_pin_code_pukked) : i == 1 ? this.mRes.getString(R.string.wrong_pin_code_one, Integer.valueOf(i)) : i > 1 ? this.mRes.getQuantityString(R.plurals.wrong_pin_code, i, Integer.valueOf(i)) : this.mRes.getString(R.string.pin_failed);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSlotIndexFromTag(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    private String getTagForSlotId(int i) {
        return String.valueOf(i);
    }

    private SubscriptionInfo getVisibleSubscriptionInfoForSimSlotIndex(int i) {
        List<SubscriptionInfo> activeSubscriptionsInfo = this.mProxySubscriptionMgr.getActiveSubscriptionsInfo();
        if (activeSubscriptionsInfo != null && getContext() != null) {
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getContext().getSystemService(CarrierConfigManager.class);
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionsInfo) {
                if (isSubscriptionVisible(carrierConfigManager, subscriptionInfo) && subscriptionInfo.getSimSlotIndex() == i) {
                    return subscriptionInfo;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void iccLockChanged(boolean z, int i) {
        Log.d("IccLockSettings", "iccLockChanged: success = " + z);
        if (z) {
            this.mPinToggle.setChecked(this.mToState);
        } else if (i >= 0) {
            createCustomTextToast(getPinPasswordErrorMessage(i));
        } else if (this.mToState) {
            Toast.makeText(getContext(), this.mRes.getString(R.string.sim_pin_enable_failed), 1).show();
        } else {
            Toast.makeText(getContext(), this.mRes.getString(R.string.sim_pin_disable_failed), 1).show();
        }
        this.mPinToggle.setEnabled(true);
        resetDialogState();
        updatePreferences();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void iccPinChanged(boolean z, int i) {
        Log.d("IccLockSettings", "iccPinChanged: success = " + z);
        if (z) {
            Toast.makeText(getContext(), this.mRes.getString(R.string.sim_change_succeeded), 0).show();
        } else {
            createCustomTextToast(getPinPasswordErrorMessage(i));
        }
        resetDialogState();
    }

    private boolean isIccLockEnabled() {
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(this.mSubId);
        this.mTelephonyManager = createForSubscriptionId;
        return createForSubscriptionId.isIccLockEnabled();
    }

    private boolean isSubscriptionVisible(CarrierConfigManager carrierConfigManager, SubscriptionInfo subscriptionInfo) {
        if (carrierConfigManager.getConfigForSubId(subscriptionInfo.getSubscriptionId()) == null) {
            return false;
        }
        return !r0.getBoolean("hide_sim_lock_settings_bool");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(EditText editText) {
        if (!TextUtils.isEmpty(editText.getText())) {
            editText.setSelection(editText.length());
        }
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        ((InputMethodManager) getSystemService("input_method")).showSoftInput(editText, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(final EditText editText) {
        editText.postDelayed(new Runnable() { // from class: com.android.settings.IccLockSettings$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                IccLockSettings.this.lambda$onCreate$0(editText);
            }
        }, 150L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showPinDialog$2(EditText editText) {
        editText.requestFocus();
        ((InputMethodManager) getSystemService("input_method")).showSoftInput(editText, 0);
    }

    private boolean reasonablePin(String str) {
        return str != null && str.length() >= 4 && str.length() <= 8;
    }

    private void resetDialogState() {
        this.mError = null;
        this.mDialogState = 2;
        this.mPin = "";
        setDialogValues();
        this.mDialogState = 0;
    }

    private boolean restoreDialogStates(Bundle bundle) {
        SubscriptionInfo visibleSubscriptionInfoForSimSlotIndex;
        SubscriptionInfo activeSubscriptionInfo = this.mProxySubscriptionMgr.getActiveSubscriptionInfo(bundle.getInt("dialogSubId"));
        if (activeSubscriptionInfo == null || (visibleSubscriptionInfoForSimSlotIndex = getVisibleSubscriptionInfoForSimSlotIndex(activeSubscriptionInfo.getSimSlotIndex())) == null || visibleSubscriptionInfoForSimSlotIndex.getSubscriptionId() != activeSubscriptionInfo.getSubscriptionId()) {
            return false;
        }
        this.mSlotId = activeSubscriptionInfo.getSimSlotIndex();
        this.mSubId = activeSubscriptionInfo.getSubscriptionId();
        this.mDialogState = bundle.getInt("dialogState");
        this.mPin = bundle.getString("dialogPin");
        this.mError = bundle.getString("dialogError");
        this.mToState = bundle.getBoolean("enableState");
        int i = this.mDialogState;
        if (i == 3) {
            this.mOldPin = bundle.getString("oldPinCode");
            return true;
        } else if (i != 4) {
            return true;
        } else {
            this.mOldPin = bundle.getString("oldPinCode");
            this.mNewPin = bundle.getString("newPinCode");
            return true;
        }
    }

    private boolean restoreTabFocus(Bundle bundle) {
        try {
            SubscriptionInfo visibleSubscriptionInfoForSimSlotIndex = getVisibleSubscriptionInfoForSimSlotIndex(Integer.parseInt(bundle.getString("currentTab")));
            if (visibleSubscriptionInfoForSimSlotIndex == null) {
                return false;
            }
            this.mSlotId = visibleSubscriptionInfoForSimSlotIndex.getSimSlotIndex();
            this.mSubId = visibleSubscriptionInfoForSimSlotIndex.getSubscriptionId();
            TabHost tabHost = this.mTabHost;
            if (tabHost != null) {
                tabHost.setCurrentTabByTag(getTagForSlotId(this.mSlotId));
                return true;
            }
            return true;
        } catch (NumberFormatException unused) {
            return false;
        }
    }

    private void setDialogValues() {
        String string;
        this.mPinDialog.setText(this.mPin);
        int i = this.mDialogState;
        if (i == 1) {
            string = this.mRes.getString(R.string.sim_enter_pin);
            this.mPinDialog.setDialogTitle(this.mToState ? this.mRes.getString(R.string.sim_enable_sim_lock) : this.mRes.getString(R.string.sim_disable_sim_lock));
        } else if (i == 2) {
            string = this.mRes.getString(R.string.sim_enter_old);
            this.mPinDialog.setDialogTitle(this.mRes.getString(R.string.sim_change_pin));
        } else if (i == 3) {
            string = this.mRes.getString(R.string.sim_enter_new);
            this.mPinDialog.setDialogTitle(this.mRes.getString(R.string.sim_change_pin));
        } else if (i != 4) {
            string = "";
        } else {
            string = this.mRes.getString(R.string.sim_reenter_new);
            this.mPinDialog.setDialogTitle(this.mRes.getString(R.string.sim_change_pin));
        }
        if (this.mError != null) {
            string = this.mError + "\n" + string;
            this.mError = null;
        }
        this.mPinDialog.setDialogMessage(string);
    }

    private void showPinDialog() {
        if (this.mDialogState == 0) {
            return;
        }
        setDialogValues();
        this.mPinDialog.showPinDialog();
        final EditText editText = this.mPinDialog.getEditText();
        if (!TextUtils.isEmpty(this.mPin) && editText != null) {
            editText.setSelection(this.mPin.length());
        }
        if (editText != null) {
            editText.post(new Runnable() { // from class: com.android.settings.IccLockSettings$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    IccLockSettings.this.lambda$showPinDialog$2(editText);
                }
            });
        }
    }

    private void tryChangeIccLockState() {
        new SetIccLockEnabled(this.mToState, this.mPin).execute(new Void[0]);
        this.mPinToggle.setEnabled(false);
    }

    private void tryChangePin() {
        new ChangeIccLockPin(this.mOldPin, this.mNewPin).execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreferences() {
        SubscriptionInfo visibleSubscriptionInfoForSimSlotIndex = getVisibleSubscriptionInfoForSimSlotIndex(this.mSlotId);
        int subscriptionId = visibleSubscriptionInfoForSimSlotIndex != null ? visibleSubscriptionInfoForSimSlotIndex.getSubscriptionId() : -1;
        if (this.mSubId != subscriptionId) {
            this.mSubId = subscriptionId;
            resetDialogState();
            EditPinPreference editPinPreference = this.mPinDialog;
            if (editPinPreference != null && editPinPreference.isDialogOpen()) {
                this.mPinDialog.getDialog().dismiss();
            }
        }
        EditPinPreference editPinPreference2 = this.mPinDialog;
        if (editPinPreference2 != null) {
            editPinPreference2.setEnabled(visibleSubscriptionInfoForSimSlotIndex != null);
        }
        CheckBoxPreference checkBoxPreference = this.mPinToggle;
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(visibleSubscriptionInfoForSimSlotIndex != null);
            if (visibleSubscriptionInfoForSimSlotIndex != null) {
                this.mPinToggle.setChecked(isIccLockEnabled());
            }
            EditPinPreference editPinPreference3 = this.mPinDialog;
            if (editPinPreference3 != null) {
                editPinPreference3.setEnabled(this.mPinToggle.isChecked());
            }
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_icc_lock;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 56;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Utils.isMonkeyRunning()) {
            finish();
            return;
        }
        ProxySubscriptionManager proxySubscriptionManager = ProxySubscriptionManager.getInstance(getContext());
        this.mProxySubscriptionMgr = proxySubscriptionManager;
        proxySubscriptionManager.setLifecycle(getLifecycle());
        this.mTelephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
        addPreferencesFromResource(R.xml.sim_lock_settings);
        this.mPinDialog = (EditPinPreference) findPreference("sim_pin");
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("sim_toggle");
        this.mPinToggle = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        if (bundle != null) {
            if (bundle.containsKey("dialogState") && restoreDialogStates(bundle)) {
                Log.d("IccLockSettings", "onCreate: restore dialog for slotId=" + this.mSlotId + ", subId=" + this.mSubId);
            } else if (bundle.containsKey("currentTab") && restoreTabFocus(bundle)) {
                Log.d("IccLockSettings", "onCreate: restore focus on slotId=" + this.mSlotId + ", subId=" + this.mSubId);
            }
        }
        this.mPinDialog.setOnPinEnteredListener(this);
        this.mPinDialog.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() { // from class: com.android.settings.IccLockSettings$$ExternalSyntheticLambda0
            @Override // androidx.preference.EditTextPreference.OnBindEditTextListener
            public final void onBindEditText(EditText editText) {
                IccLockSettings.this.lambda$onCreate$1(editText);
            }
        });
        getPreferenceScreen().setPersistent(false);
        this.mSlotId = SubscriptionManager.getDefault().getDefaultDataSlotId();
        if (getIntent() != null) {
            this.mSlotId = SubscriptionManager.getSlotIdExtra(getIntent(), this.mSlotId);
        }
        SubscriptionInfo visibleSubscriptionInfoForSimSlotIndex = getVisibleSubscriptionInfoForSimSlotIndex(this.mSlotId);
        this.mSubId = visibleSubscriptionInfoForSimSlotIndex != null ? visibleSubscriptionInfoForSimSlotIndex.getSubscriptionId() : -1;
        this.mRes = getResources();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(this.mSimStateReceiver);
    }

    @Override // com.android.settings.EditPinPreference.OnPinEnteredListener
    public void onPinEntered(EditPinPreference editPinPreference, boolean z) {
        if (!z) {
            resetDialogState();
            if (getContext() != null) {
                updatePreferences();
                return;
            }
            return;
        }
        String text = editPinPreference.getText();
        this.mPin = text;
        if (!reasonablePin(text)) {
            this.mError = this.mRes.getString(R.string.sim_bad_pin);
            showPinDialog();
            return;
        }
        int i = this.mDialogState;
        if (i == 1) {
            tryChangeIccLockState();
        } else if (i == 2) {
            this.mOldPin = this.mPin;
            this.mDialogState = 3;
            this.mError = null;
            this.mPin = null;
            showPinDialog();
        } else if (i == 3) {
            this.mNewPin = this.mPin;
            this.mDialogState = 4;
            this.mPin = null;
            showPinDialog();
        } else if (i != 4) {
        } else {
            if (this.mPin.equals(this.mNewPin)) {
                this.mError = null;
                tryChangePin();
                return;
            }
            this.mError = this.mRes.getString(R.string.sim_pins_dont_match);
            this.mDialogState = 3;
            this.mPin = null;
            showPinDialog();
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mPinToggle) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            this.mToState = booleanValue;
            this.mPinToggle.setChecked(!booleanValue);
            this.mDialogState = 1;
            showPinDialog();
        }
        return true;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == this.mPinDialog) {
            this.mDialogState = 2;
            return false;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(this.mSimStateReceiver, new IntentFilter("android.intent.action.SIM_STATE_CHANGED"));
        if (this.mDialogState != 0) {
            showPinDialog();
        } else {
            resetDialogState();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("dialogSubId", this.mSubId);
        bundle.putInt("dialogState", this.mDialogState);
        EditPinPreference editPinPreference = this.mPinDialog;
        if (editPinPreference != null && editPinPreference.getEditText() != null) {
            bundle.putString("dialogPin", this.mPinDialog.getEditText().getText().toString());
        }
        bundle.putString("dialogError", this.mError);
        bundle.putBoolean("enableState", this.mToState);
        int i = this.mDialogState;
        if (i == 3) {
            bundle.putString("oldPinCode", this.mOldPin);
        } else if (i == 4) {
            bundle.putString("oldPinCode", this.mOldPin);
            bundle.putString("newPinCode", this.mNewPin);
        }
        super.onSaveInstanceState(bundle);
        TabHost tabHost = this.mTabHost;
        if (tabHost != null) {
            bundle.putString("currentTab", tabHost.getCurrentTabTag());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (Utils.isMonkeyRunning()) {
            return;
        }
        updatePreferences();
    }
}
