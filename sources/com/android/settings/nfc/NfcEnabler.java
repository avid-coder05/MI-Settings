package com.android.settings.nfc;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.util.Log;
import android.widget.Switch;
import androidx.preference.ListPreference;
import com.android.settings.R;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import miuix.appcompat.app.AlertDialog;
import miuix.preference.DropDownPreference;

/* loaded from: classes2.dex */
public class NfcEnabler extends BaseNfcEnabler implements OnMainSwitchChangeListener {
    private Context mContext;
    private DropDownPreference mNfcPayment;
    private final MainSwitchPreference mPreference;
    private ListPreference mSeRoute;

    public NfcEnabler(Context context, MainSwitchPreference mainSwitchPreference) {
        super(context);
        this.mContext = context;
        this.mPreference = mainSwitchPreference;
        if (isNfcAvailable()) {
            mainSwitchPreference.updateStatus(this.mNfcAdapter.isEnabled());
        }
    }

    public NfcEnabler(Context context, MainSwitchPreference mainSwitchPreference, ListPreference listPreference) {
        this(context, mainSwitchPreference);
        this.mSeRoute = listPreference;
    }

    public NfcEnabler(Context context, MainSwitchPreference mainSwitchPreference, DropDownPreference dropDownPreference, boolean z) {
        this(context, mainSwitchPreference);
        this.mNfcPayment = dropDownPreference;
    }

    private void changeNfcStatus(boolean z) {
        this.mPreference.setEnabled(false);
        this.mPreference.updateStatus(z);
        if (isNfcAvailable()) {
            if (z) {
                this.mNfcAdapter.enable();
            } else {
                this.mNfcAdapter.disable();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$0() {
        this.mPreference.setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$1(DialogInterface dialogInterface, int i) {
        changeNfcStatus(true);
        ThreadUtils.getUiThreadHandler().postDelayed(new Runnable() { // from class: com.android.settings.nfc.NfcEnabler$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                NfcEnabler.this.lambda$showDialog$0();
            }
        }, 100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$2(DialogInterface dialogInterface, int i) {
        changeNfcStatus(false);
    }

    private void showDialog() {
        Context context = this.mContext;
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(this.mContext.getResources().getString(R.string.miui_nfc_dialog_title)).setMessage(this.mContext.getResources().getString(R.string.miui_nfc_dialog_message)).setPositiveButton(this.mContext.getResources().getString(R.string.miui_nfc_dialog_positive), new DialogInterface.OnClickListener() { // from class: com.android.settings.nfc.NfcEnabler$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                NfcEnabler.this.lambda$showDialog$1(dialogInterface, i);
            }
        }).setNegativeButton(this.mContext.getResources().getString(R.string.miui_nfc_dialog_negative), new DialogInterface.OnClickListener() { // from class: com.android.settings.nfc.NfcEnabler$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                NfcEnabler.this.lambda$showDialog$2(dialogInterface, i);
            }
        });
        AlertDialog create = builder.create();
        if (create == null) {
            return;
        }
        create.setCanceledOnTouchOutside(false);
        try {
            create.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.android.settings.nfc.BaseNfcEnabler
    protected void handleNfcStateChanged(int i) {
        if (i == 1) {
            this.mPreference.updateStatus(false);
            this.mPreference.setEnabled(isToggleable());
            ListPreference listPreference = this.mSeRoute;
            if (listPreference != null) {
                listPreference.setEnabled(false);
            }
            DropDownPreference dropDownPreference = this.mNfcPayment;
            if (dropDownPreference != null) {
                dropDownPreference.setEnabled(false);
            }
        } else if (i == 2) {
            this.mPreference.setEnabled(false);
            this.mPreference.updateStatus(true);
            ListPreference listPreference2 = this.mSeRoute;
            if (listPreference2 != null) {
                listPreference2.setEnabled(false);
            }
            DropDownPreference dropDownPreference2 = this.mNfcPayment;
            if (dropDownPreference2 != null) {
                dropDownPreference2.setEnabled(false);
            }
        } else if (i == 3) {
            this.mPreference.updateStatus(true);
            this.mPreference.setEnabled(true);
            ListPreference listPreference3 = this.mSeRoute;
            if (listPreference3 != null) {
                listPreference3.setEnabled(true);
            }
            DropDownPreference dropDownPreference3 = this.mNfcPayment;
            if (dropDownPreference3 != null) {
                dropDownPreference3.setEnabled(true);
            }
        } else if (i != 4) {
        } else {
            this.mPreference.setEnabled(false);
            this.mPreference.updateStatus(false);
            ListPreference listPreference4 = this.mSeRoute;
            if (listPreference4 != null) {
                listPreference4.setEnabled(false);
            }
            DropDownPreference dropDownPreference4 = this.mNfcPayment;
            if (dropDownPreference4 != null) {
                dropDownPreference4.setEnabled(false);
            }
        }
    }

    boolean isToggleable() {
        return (!NfcPreferenceController.isToggleableInAirplaneMode(this.mContext) && NfcPreferenceController.shouldTurnOffNFCInAirplaneMode(this.mContext) && Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1) ? false : true;
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        if (this.mPreference.isEnabled()) {
            Log.d("NfcEnabler", "onSwitchChanged: " + z);
            if (SettingsFeatures.isNeedShowMiuiNFC()) {
                if (z) {
                    changeNfcStatus(z);
                    return;
                } else {
                    showDialog();
                    return;
                }
            }
            this.mPreference.setEnabled(false);
            this.mPreference.updateStatus(z);
            if (isNfcAvailable()) {
                if (z) {
                    this.mNfcAdapter.enable();
                } else {
                    this.mNfcAdapter.disable();
                }
            }
        }
    }

    @Override // com.android.settings.nfc.BaseNfcEnabler
    public void pause() {
        super.pause();
        if (isNfcAvailable()) {
            this.mPreference.removeOnSwitchChangeListener(this);
        }
    }

    @Override // com.android.settings.nfc.BaseNfcEnabler
    public void resume() {
        super.resume();
        if (isNfcAvailable()) {
            this.mPreference.addOnSwitchChangeListener(this);
        }
    }
}
