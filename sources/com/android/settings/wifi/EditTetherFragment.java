package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import com.android.settings.BaseEditFragment;
import com.android.settings.MiuiDeviceNameEditFragment;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settingslib.util.ToastUtil;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import miui.util.FeatureParser;
import miuix.appcompat.widget.Spinner;

/* loaded from: classes2.dex */
public class EditTetherFragment extends BaseEditFragment implements View.OnClickListener, TextWatcher, AdapterView.OnItemSelectedListener {
    private int mIdentifyIndex;
    private boolean mIsIdentifyChanged;
    private boolean mIsShowPasswordChecked;
    private EditText mPassword;
    private BroadcastReceiver mReceiver;
    private SoftApConfiguration mSoftApConfig;
    private int mSpinnerLayoutBgColor;
    private TextView mSsid;
    private WifiManager mWifiManager;
    private int mSecurityTypeIndex = 1;
    private int mBandIndex = 1;
    private int mHiddenSsidIndex = 0;
    private boolean is5GHzSapForbiddenRegion = false;
    private String TAG = EditTetherFragment.class.getName();

    public static int getSecurityTypeIndex(SoftApConfiguration softApConfiguration) {
        if (softApConfiguration.getSecurityType() == 1) {
            return 1;
        }
        return (softApConfiguration.getSecurityType() == 3 || softApConfiguration.getSecurityType() == 2) ? 2 : 0;
    }

    private boolean is5GHzSapForbiddenRegion(String str) {
        String country = Locale.getDefault().getCountry();
        String[] strArr = new String[0];
        try {
            strArr = getActivity().getResources().getStringArray(getActivity().getResources().getIdentifier("country_codes_hid_sap_5GHz_band", "array", "android.miui"));
        } catch (Exception unused) {
            Log.e(this.TAG, "Failed to get limit usage of sap 5GHz band config.");
        }
        for (String str2 : strArr) {
            if (str2.equals(str) || str2.equals(country)) {
                Log.w(this.TAG, "The hotspot band option will hidden in country: " + str2);
                return true;
            }
        }
        return RegionUtils.IS_JP_SB || RegionUtils.IS_JP_KDDI || RegionUtils.IS_JP_HARDWARE || RegionUtils.IS_JP;
    }

    private boolean isSAPSameBandASWifi() {
        try {
            return getActivity().getResources().getBoolean(getActivity().getResources().getIdentifier("config_sap_same_band_as_wifi", "bool", "android.miui"));
        } catch (Exception unused) {
            return false;
        }
    }

    private void isSoftApSsidchanged() {
        String charSequence = this.mSsid.getText().toString();
        if (charSequence == null || charSequence.equals(this.mSoftApConfig.getSsid())) {
            return;
        }
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
        edit.putBoolean("wifi_ap_ssid_changed", true);
        edit.commit();
    }

    private boolean isWifiConnected() {
        NetworkInfo networkInfo = ((ConnectivityManager) getActivity().getSystemService("connectivity")).getNetworkInfo(1);
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showSecurityFields() {
        if (getActivity() == null) {
            return;
        }
        if (this.mSecurityTypeIndex == 0) {
            getActivity().findViewById(R.id.fields).setVisibility(8);
            this.mPassword.clearFocus();
            return;
        }
        getActivity().findViewById(R.id.fields).setVisibility(0);
        this.mPassword.setFocusable(true);
        this.mPassword.requestFocus();
    }

    private void updatePasswordState() {
        int selectionStart = this.mPassword.getSelectionStart();
        this.mPassword.setInputType((this.mIsShowPasswordChecked ? 144 : 128) | 1);
        if (selectionStart >= 0) {
            this.mPassword.setSelection(selectionStart);
        }
    }

    private void validate() {
        boolean z;
        if (StandardCharsets.US_ASCII.newEncoder().canEncode(this.mPassword.getText().toString())) {
            z = true;
        } else {
            ToastUtil.show(getActivity(), R.string.tether_password_illegal_character, 0);
            Log.d(this.TAG, "passphrase not ASCII encodable");
            z = false;
        }
        TextView textView = this.mSsid;
        if ((textView == null || textView.length() != 0) && ((this.mSecurityTypeIndex != 1 || this.mPassword.length() >= 8) && ((this.mSecurityTypeIndex != 2 || this.mPassword.length() >= 8) && z))) {
            onEditStateChange(true);
        } else {
            onEditStateChange(false);
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        validate();
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public SoftApConfiguration buildNewSoftApConfig() {
        SoftApConfiguration.Builder builder = new SoftApConfiguration.Builder(this.mSoftApConfig);
        builder.setSsid(this.mSsid.getText().toString());
        builder.setBand(this.mBandIndex);
        builder.setHiddenSsid(1 == this.mHiddenSsidIndex);
        int i = this.mSecurityTypeIndex;
        if (i == 0) {
            builder.setPassphrase((String) null, 0);
            return builder.build();
        } else if (i == 1) {
            builder.setPassphrase(this.mPassword.getText().toString(), 1);
            return builder.build();
        } else if (i != 2) {
            return null;
        } else {
            builder.setPassphrase(this.mPassword.getText().toString(), 2);
            return builder.build();
        }
    }

    @Override // com.android.settings.BaseEditFragment
    public String getTitle() {
        return getString(R.string.wifi_tether_configure_ap_text);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.show_password_img) {
            this.mIsShowPasswordChecked = !this.mIsShowPasswordChecked;
            updatePasswordState();
        }
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWifiManager = (WifiManager) getActivity().getSystemService("wifi");
        this.mSpinnerLayoutBgColor = getContext().getColor(R.color.bg_spinner_parent);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onInflateView(layoutInflater, viewGroup, bundle);
        return layoutInflater.inflate(R.layout.wifi_ap_dialog, viewGroup, false);
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.mSecurityTypeIndex = i;
        showSecurityFields();
        validate();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (!isSAPSameBandASWifi() || this.is5GHzSapForbiddenRegion) {
            return;
        }
        getActivity().unregisterReceiver(this.mReceiver);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.is5GHzSapForbiddenRegion) {
            getActivity().findViewById(R.id.channel_fields).setVisibility(8);
        } else if (isSAPSameBandASWifi()) {
            getActivity().findViewById(R.id.channel_fields).setVisibility(isWifiConnected() ? 8 : 0);
            getActivity().registerReceiver(this.mReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
        }
        validate();
    }

    @Override // com.android.settings.BaseEditFragment
    public void onSave() {
        Bundle bundle;
        isSoftApSsidchanged();
        SoftApConfiguration buildNewSoftApConfig = buildNewSoftApConfig();
        if (buildNewSoftApConfig != null) {
            bundle = new Bundle();
            bundle.putParcelable("config", buildNewSoftApConfig);
        } else {
            bundle = null;
        }
        if (this.mIsIdentifyChanged) {
            setHotSpotVendorSpecific(this.mIdentifyIndex);
        }
        onSave(bundle);
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(final View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        boolean isWpa3SoftApSupport = MiuiUtils.getInstance().isWpa3SoftApSupport(getContext());
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        this.mSoftApConfig = softApConfiguration;
        if (softApConfiguration != null) {
            this.mSecurityTypeIndex = getSecurityTypeIndex(softApConfiguration);
        }
        if (!isWpa3SoftApSupport && this.mSecurityTypeIndex == 2) {
            Log.i(this.TAG, "ERROR: WPA3 in config, but chip not support");
            this.mSecurityTypeIndex = 1;
        }
        Spinner spinner = (Spinner) view.findViewById(R.id.security);
        view.findViewById(R.id.type).setVisibility(0);
        view.findViewById(R.id.l_security).setVisibility(0);
        this.mSsid = (TextView) view.findViewById(R.id.ssid);
        this.mPassword = (EditText) view.findViewById(R.id.password);
        SoftApConfiguration softApConfiguration2 = this.mSoftApConfig;
        if (softApConfiguration2 != null) {
            this.mSsid.setText(softApConfiguration2.getSsid());
            this.mBandIndex = this.mSoftApConfig.getBand();
            TextView textView = this.mSsid;
            ((EditText) textView).setSelection(textView.getText().toString().length());
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.miuix_appcompat_simple_spinner_layout_integrated, 16908308, isWpa3SoftApSupport ? getActivity().getResources().getStringArray(R.array.wifi_ap_security_with_sae) : getActivity().getResources().getStringArray(R.array.wifi_ap_security));
            arrayAdapter.setDropDownViewResource(R.layout.miuix_appcompat_simple_spinner_dropdown_item);
            spinner.setAdapter((SpinnerAdapter) arrayAdapter);
            spinner.setSelection(this.mSecurityTypeIndex);
            int i = this.mSecurityTypeIndex;
            if (i == 1 || i == 2) {
                this.mPassword.setText(this.mSoftApConfig.getPassphrase());
            }
        }
        this.mSsid.addTextChangedListener(this);
        this.mSsid.setFilters(new InputFilter[]{new MiuiDeviceNameEditFragment.LengthFilter(new MiuiDeviceNameEditFragment.LengthFilter.NullContentCallBack() { // from class: com.android.settings.wifi.EditTetherFragment.1
            @Override // com.android.settings.MiuiDeviceNameEditFragment.LengthFilter.NullContentCallBack
            public void beyondLimit() {
            }

            @Override // com.android.settings.MiuiDeviceNameEditFragment.LengthFilter.NullContentCallBack
            public void isNullContent(boolean z) {
            }
        }, 32)});
        this.mPassword.addTextChangedListener(this);
        this.mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.android.settings.wifi.EditTetherFragment.2
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view2, boolean z) {
                InputMethodManager inputMethodManager;
                if (z || (inputMethodManager = (InputMethodManager) EditTetherFragment.this.getContext().getSystemService("input_method")) == null) {
                    return;
                }
                inputMethodManager.hideSoftInputFromWindow(view2.getWindowToken(), 2);
            }
        });
        spinner.setOnItemSelectedListener(this);
        spinner.setPrompt(spinner.getPrompt());
        ArrayAdapter arrayAdapter2 = (ArrayAdapter) spinner.getAdapter();
        int i2 = R.layout.miuix_appcompat_simple_spinner_dropdown_item;
        arrayAdapter2.setDropDownViewResource(i2);
        showSecurityFields();
        validate();
        Spinner spinner2 = (Spinner) view.findViewById(R.id.enalbe_identify_iPhone);
        if (TextUtils.equals("mediatek", FeatureParser.getString("vendor"))) {
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.identy);
            linearLayout.removeAllViews();
            linearLayout.setVisibility(8);
        } else {
            String hotSpotVendorSpecific = MiuiSettings.System.getHotSpotVendorSpecific(getContext());
            if (hotSpotVendorSpecific != null) {
                this.mIdentifyIndex = "DD0A0017F206010103010000".equals(hotSpotVendorSpecific) ? 1 : 0;
            } else {
                MiuiSettings.System.setHotSpotVendorSpecific(getContext(), "DD0A0017F206010103010000");
                this.mIdentifyIndex = 1;
            }
            ArrayAdapter arrayAdapter3 = new ArrayAdapter(getActivity(), R.layout.miuix_appcompat_simple_spinner_layout_integrated, 16908308, getActivity().getResources().getStringArray(R.array.ap_identify));
            arrayAdapter3.setDropDownViewResource(i2);
            spinner2.setAdapter((SpinnerAdapter) arrayAdapter3);
            spinner2.setSelection(this.mIdentifyIndex);
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.android.settings.wifi.EditTetherFragment.3
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view2, int i3, long j) {
                    EditTetherFragment.this.mIdentifyIndex = i3;
                    EditTetherFragment.this.mIsIdentifyChanged = true;
                    Log.i(EditTetherFragment.this.TAG, "ap identify changed : " + EditTetherFragment.this.mIdentifyIndex);
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
        Spinner spinner3 = (Spinner) view.findViewById(R.id.choose_channel);
        String countryCode = this.mWifiManager.getCountryCode();
        this.is5GHzSapForbiddenRegion = is5GHzSapForbiddenRegion(countryCode);
        Resources resources = getActivity().getResources();
        int i3 = R.string.wifi_ap_choose;
        String format = String.format(resources.getString(i3), "2.4");
        String[] strArr = {format, String.format(getActivity().getResources().getString(i3), "5.0")};
        if (!this.mWifiManager.is5GHzBandSupported() || countryCode == null) {
            String str = this.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(!this.mWifiManager.is5GHzBandSupported() ? "Device do not support 5GHz " : "");
            sb.append(countryCode == null ? " NO country code" : "");
            sb.append(" forbid 5GHz");
            Log.i(str, sb.toString());
            strArr = new String[]{format};
            this.mBandIndex = 1;
        }
        FragmentActivity activity = getActivity();
        int i4 = R.layout.miuix_appcompat_simple_spinner_layout_integrated;
        ArrayAdapter arrayAdapter4 = new ArrayAdapter(activity, i4, 16908308, strArr);
        arrayAdapter4.setDropDownViewResource(i2);
        spinner3.setAdapter((SpinnerAdapter) arrayAdapter4);
        int i5 = this.mBandIndex;
        spinner3.setSelection(i5 != 3 ? i5 - 1 : 1);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.android.settings.wifi.EditTetherFragment.4
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view2, int i6, long j) {
                EditTetherFragment.this.mBandIndex = i6 + 1;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.mHiddenSsidIndex = this.mSoftApConfig.isHiddenSsid() ? 1 : 0;
        Spinner spinner4 = (Spinner) view.findViewById(R.id.wifi_ap_hidden_ssid);
        ArrayAdapter arrayAdapter5 = new ArrayAdapter(getActivity(), i4, 16908308, getActivity().getResources().getStringArray(R.array.wifi_ap_hidden_ssid_config));
        arrayAdapter5.setDropDownViewResource(i2);
        spinner4.setAdapter((SpinnerAdapter) arrayAdapter5);
        spinner4.setSelection(this.mHiddenSsidIndex);
        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.android.settings.wifi.EditTetherFragment.5
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view2, int i6, long j) {
                EditTetherFragment.this.mHiddenSsidIndex = i6;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        MiuiUtils.getInstance().setSpinnerDisplayLocation(spinner, this.mSpinnerLayoutBgColor);
        MiuiUtils.getInstance().setSpinnerDisplayLocation(spinner2, this.mSpinnerLayoutBgColor);
        MiuiUtils.getInstance().setSpinnerDisplayLocation(spinner3, this.mSpinnerLayoutBgColor);
        MiuiUtils.getInstance().setSpinnerDisplayLocation(spinner4, this.mSpinnerLayoutBgColor);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.EditTetherFragment.6
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.net.wifi.STATE_CHANGE".equals(intent.getAction())) {
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    if (networkInfo == null || networkInfo.getState() != NetworkInfo.State.CONNECTED) {
                        view.findViewById(R.id.channel_fields).setVisibility(0);
                    } else {
                        view.findViewById(R.id.channel_fields).setVisibility(8);
                    }
                }
            }
        };
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        updatePasswordState();
    }

    public void setHotSpotVendorSpecific(int i) {
        MiuiSettings.System.setHotSpotVendorSpecific(getContext(), i == 1 ? "DD0A0017F206010103010000" : "");
    }
}
