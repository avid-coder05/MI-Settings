package com.android.settings.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.HashMap;
import java.util.Map;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class BluetoothPairingDialogFragment extends InstrumentedDialogFragment implements TextWatcher, DialogInterface.OnClickListener {
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private boolean mNormalExit = false;
    private BluetoothPairingController mPairingController;
    private BluetoothPairingDialog mPairingDialogActivity;
    private EditText mPairingView;
    private SharedPreferences mSpf;

    /* loaded from: classes.dex */
    public interface BluetoothPairingDialogListener {
    }

    private AlertDialog createConfirmationDialog() {
        this.mBuilder.setTitle(getString(R.string.bluetooth_pairing_request, this.mPairingController.getDeviceName()));
        String address = this.mPairingController.getBluetoothDevice().getAddress();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("BlackfileForBluetoothDevice", 0);
        this.mSpf = sharedPreferences;
        Map<String, ?> all = sharedPreferences.getAll();
        if (BluetoothDevicePreference.mTriggerFromAvaliableDevices && all.containsKey(address)) {
            this.mBuilder.setView(createView());
            this.mBuilder.setPositiveButton(getString(R.string.bluetooth_pairing_accept), this);
            this.mBuilder.setNegativeButton(getString(R.string.bluetooth_pairing_decline), this);
        } else {
            createConfirmationDialogWithBlackfile();
        }
        BluetoothDevicePreference.mTriggerFromAvaliableDevices = false;
        return this.mBuilder.create();
    }

    private void createConfirmationDialogWithBlackfile() {
        View createView = createView();
        this.mBuilder.setView(createView);
        Button button = (Button) createView.findViewById(R.id.add_to_blackfile_button);
        Button button2 = (Button) createView.findViewById(R.id.pairing_decline_button);
        Button button3 = (Button) createView.findViewById(R.id.pairing_accept_button);
        button.setVisibility(0);
        button2.setVisibility(0);
        button3.setVisibility(0);
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothPairingDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int i;
                BluetoothPairingDialogFragment.this.mNormalExit = true;
                if (view.getId() != R.id.add_to_blackfile_button) {
                    if (view.getId() == R.id.pairing_accept_button) {
                        BluetoothPairingDialogFragment.this.mPairingController.onDialogPositiveClick(BluetoothPairingDialogFragment.this);
                    } else {
                        BluetoothPairingDialogFragment.this.mPairingController.onDialogNegativeClick(BluetoothPairingDialogFragment.this);
                    }
                    try {
                        if (BluetoothPairingDialogFragment.this.mDialog != null && BluetoothPairingDialogFragment.this.mDialog.isShowing()) {
                            BluetoothPairingDialogFragment.this.mDialog.dismiss();
                        }
                        BluetoothPairingDialogFragment.this.mPairingDialogActivity.dismiss();
                        return;
                    } catch (Exception e) {
                        Log.e("BTPairingDialogFragment", "Exception: " + e);
                        return;
                    }
                }
                SharedPreferences.Editor edit = BluetoothPairingDialogFragment.this.mSpf.edit();
                BluetoothDevice bluetoothDevice = null;
                try {
                    bluetoothDevice = BluetoothPairingDialogFragment.this.mPairingController.getBluetoothDevice();
                    String address = bluetoothDevice.getAddress();
                    String name = bluetoothDevice.getName() != null ? bluetoothDevice.getName() : address;
                    int i2 = -1;
                    if (bluetoothDevice.getBluetoothClass() != null) {
                        i2 = bluetoothDevice.getBluetoothClass().getMajorDeviceClass();
                        i = bluetoothDevice.getBluetoothClass().getDeviceClass();
                    } else {
                        i = -1;
                    }
                    if (BluetoothPairingDialogFragment.this.mSpf.getAll().containsKey(address)) {
                        Toast.makeText(BluetoothPairingDialogFragment.this.getContext(), R.string.bluetooth_pairing_toast_add_to_blackfile, 0).show();
                    } else {
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("DeviceName", name);
                        jSONObject.put("DeviceType", String.valueOf(i2));
                        jSONObject.put("DeviceClass", String.valueOf(i));
                        edit.putString(address, jSONObject.toString());
                        edit.apply();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    edit.apply();
                }
                Toast.makeText(BluetoothPairingDialogFragment.this.getContext(), R.string.bluetooth_pairing_toast_add_to_blackfile, 0).show();
                HashMap hashMap = new HashMap();
                hashMap.put("block_device", Boolean.TRUE);
                if ("CN".equals(Build.getRegion())) {
                    OneTrackInterfaceUtils.track("bluetooth_blocklist", hashMap);
                }
                if (bluetoothDevice != null) {
                    bluetoothDevice.cancelPairing();
                }
            }
        };
        button.setOnClickListener(onClickListener);
        button2.setOnClickListener(onClickListener);
        button3.setOnClickListener(onClickListener);
        button.setText(R.string.bluetooth_pairing_add_to_blackfile);
        button2.setText(R.string.bluetooth_pairing_decline);
        button3.setText(R.string.bluetooth_pairing_accept);
    }

    private AlertDialog createConsentDialog() {
        return createConfirmationDialog();
    }

    private AlertDialog createDisplayPasskeyOrPinDialog() {
        this.mBuilder.setTitle(getString(R.string.bluetooth_pairing_request, this.mPairingController.getDeviceName()));
        this.mBuilder.setView(createView());
        this.mBuilder.setNegativeButton(getString(17039360), this);
        AlertDialog create = this.mBuilder.create();
        this.mPairingController.notifyDialogDisplayed();
        return create;
    }

    private View createPinEntryView() {
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.bluetooth_pin_entry, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.pin_values_hint);
        TextView textView2 = (TextView) inflate.findViewById(R.id.message_below_pin);
        CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.alphanumeric_pin);
        CheckBox checkBox2 = (CheckBox) inflate.findViewById(R.id.phonebook_sharing_message_entry_pin);
        try {
            if (getContext().getResources().getBoolean(17891702)) {
                checkBox2.setText(getString(R.string.bluetooth_pairing_shares_phonebook, this.mPairingController.getDeviceName()));
            } else {
                checkBox2.setText(getString(R.string.bluetooth_pairing_shares_phonebook_not_support_voice, this.mPairingController.getDeviceName()));
            }
        } catch (Exception unused) {
            checkBox2.setText(getString(R.string.bluetooth_pairing_shares_phonebook, this.mPairingController.getDeviceName()));
            Log.w("BTPairingDialogFragment", "Can't know if it supports VoiceCapable");
        }
        EditText editText = (EditText) inflate.findViewById(R.id.text);
        checkBox2.setVisibility(this.mPairingController.isProfileReady() ? 8 : 0);
        this.mPairingController.setContactSharingState();
        checkBox2.setOnCheckedChangeListener(this.mPairingController);
        checkBox2.setChecked(this.mPairingController.getContactSharingState());
        this.mPairingView = editText;
        editText.setInputType(2);
        editText.addTextChangedListener(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.bluetooth.BluetoothPairingDialogFragment$$ExternalSyntheticLambda1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                BluetoothPairingDialogFragment.this.lambda$createPinEntryView$1(compoundButton, z);
            }
        });
        int deviceVariantMessageId = this.mPairingController.getDeviceVariantMessageId();
        int deviceVariantMessageHintId = this.mPairingController.getDeviceVariantMessageHintId();
        int deviceMaxPasskeyLength = this.mPairingController.getDeviceMaxPasskeyLength();
        checkBox.setVisibility(this.mPairingController.pairingCodeIsAlphanumeric() ? 0 : 8);
        if (deviceVariantMessageId != -1) {
            textView2.setText(deviceVariantMessageId);
        } else {
            textView2.setVisibility(8);
        }
        if (deviceVariantMessageHintId != -1) {
            textView.setText(deviceVariantMessageHintId);
        } else {
            textView.setVisibility(8);
        }
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(deviceMaxPasskeyLength)});
        return inflate;
    }

    private AlertDialog createUserEntryDialog() {
        this.mBuilder.setTitle(getString(R.string.bluetooth_pairing_request, this.mPairingController.getDeviceName()));
        this.mBuilder.setView(createPinEntryView());
        this.mBuilder.setPositiveButton(getString(17039370), this);
        this.mBuilder.setNegativeButton(getString(17039360), this);
        AlertDialog create = this.mBuilder.create();
        create.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.android.settings.bluetooth.BluetoothPairingDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                BluetoothPairingDialogFragment.this.lambda$createUserEntryDialog$0(dialogInterface);
            }
        });
        return create;
    }

    private View createView() {
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.bluetooth_pin_confirm, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.pairing_caption);
        TextView textView2 = (TextView) inflate.findViewById(R.id.pairing_subhead);
        TextView textView3 = (TextView) inflate.findViewById(R.id.pairing_code_message);
        CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.phonebook_sharing_message_confirm_pin);
        try {
            if (getContext().getResources().getBoolean(17891702)) {
                checkBox.setText(getString(R.string.bluetooth_pairing_shares_phonebook, this.mPairingController.getDeviceName()));
            } else {
                checkBox.setText(getString(R.string.bluetooth_pairing_shares_phonebook_not_support_voice, this.mPairingController.getDeviceName()));
            }
        } catch (Exception unused) {
            checkBox.setText(getString(R.string.bluetooth_pairing_shares_phonebook, this.mPairingController.getDeviceName()));
            Log.w("BTPairingDialogFragment", "Can't know if it supports VoiceCapable");
        }
        checkBox.setVisibility(this.mPairingController.isProfileReady() ? 8 : 0);
        this.mPairingController.setContactSharingState();
        checkBox.setChecked(this.mPairingController.getContactSharingState());
        checkBox.setOnCheckedChangeListener(this.mPairingController);
        textView3.setVisibility(this.mPairingController.isDisplayPairingKeyVariant() ? 0 : 8);
        if (this.mPairingController.hasPairingContent()) {
            textView.setVisibility(0);
            textView2.setVisibility(0);
            textView2.setText(this.mPairingController.getPairingContent());
        }
        return inflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createPinEntryView$1(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.mPairingView.setInputType(1);
        } else {
            this.mPairingView.setInputType(2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createUserEntryDialog$0(DialogInterface dialogInterface) {
        InputMethodManager inputMethodManager;
        if (TextUtils.isEmpty(getPairingViewText())) {
            this.mDialog.getButton(-1).setEnabled(false);
        }
        EditText editText = this.mPairingView;
        if (editText == null || !editText.requestFocus() || (inputMethodManager = (InputMethodManager) getContext().getSystemService("input_method")) == null) {
            return;
        }
        inputMethodManager.showSoftInput(this.mPairingView, 1);
    }

    private AlertDialog setupDialog() {
        int dialogType = this.mPairingController.getDialogType();
        if (dialogType != 0) {
            if (dialogType != 1) {
                if (dialogType != 2) {
                    Log.e("BTPairingDialogFragment", "Incorrect pairing type received, not showing any dialog");
                    return null;
                }
                return createDisplayPasskeyOrPinDialog();
            }
            return createConsentDialog();
        }
        return createUserEntryDialog();
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        Button button = this.mDialog.getButton(-1);
        if (button != null) {
            button.setEnabled(this.mPairingController.isPasskeyValid(editable));
        }
        this.mPairingController.updateUserInput(editable.toString());
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 613;
    }

    CharSequence getPairingViewText() {
        EditText editText = this.mPairingView;
        if (editText != null) {
            return editText.getText();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isPairingControllerSet() {
        return this.mPairingController != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isPairingDialogActivitySet() {
        return this.mPairingDialogActivity != null;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        this.mNormalExit = true;
        if (i == -1) {
            this.mPairingController.onDialogPositiveClick(this);
        } else if (i == -2) {
            this.mPairingController.onDialogNegativeClick(this);
        }
        try {
            AlertDialog alertDialog = this.mDialog;
            if (alertDialog != null && alertDialog.isShowing()) {
                this.mDialog.dismiss();
            }
            this.mPairingDialogActivity.dismiss();
        } catch (Exception e) {
            Log.e("BTPairingDialogFragment", "Exception: " + e);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        if (isPairingControllerSet()) {
            if (isPairingDialogActivitySet()) {
                this.mBuilder = new AlertDialog.Builder(getActivity());
                AlertDialog alertDialog = setupDialog();
                this.mDialog = alertDialog;
                alertDialog.setCanceledOnTouchOutside(false);
                return this.mDialog;
            }
            throw new IllegalStateException("Must call setPairingDialogActivity() before showing dialog");
        }
        throw new IllegalStateException("Must call setPairingController() before showing dialog");
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        BluetoothPairingController bluetoothPairingController;
        super.onDismiss(dialogInterface);
        if (!this.mNormalExit && (bluetoothPairingController = this.mPairingController) != null) {
            bluetoothPairingController.onCancel();
        }
        getActivity().finish();
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPairingController(BluetoothPairingController bluetoothPairingController) {
        if (isPairingControllerSet()) {
            throw new IllegalStateException("The controller can only be set once. Forcibly replacing it will lead to undefined behavior");
        }
        this.mPairingController = bluetoothPairingController;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPairingDialogActivity(BluetoothPairingDialog bluetoothPairingDialog) {
        if (isPairingDialogActivitySet()) {
            throw new IllegalStateException("The pairing dialog activity can only be set once");
        }
        this.mPairingDialogActivity = bluetoothPairingDialog;
    }
}
