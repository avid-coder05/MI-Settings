package com.android.settings.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.wifi.AccessPoint;
import miui.cloud.finddevice.FindDeviceStatusManager;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class WifiDialog extends AlertDialog implements WifiConfigUiBase, DialogInterface.OnClickListener {
    private final AccessPoint mAccessPoint;
    private WifiConfigController mController;
    private boolean mHideSubmitButton;
    private final WifiDialogListener mListener;
    private final int mMode;
    private String mOcrWifiPwd;
    private View mView;

    /* loaded from: classes2.dex */
    public interface WifiDialogListener {
        default void onForget(WifiDialog wifiDialog) {
        }

        default void onScan(WifiDialog wifiDialog, String str) {
        }

        default void onSubmit(WifiDialog wifiDialog) {
        }
    }

    public WifiDialog(Context context, WifiDialogListener wifiDialogListener, AccessPoint accessPoint, int i, int i2, boolean z) {
        super(context, R.style.Theme_WifiDialog);
        this.mMode = i;
        this.mListener = wifiDialogListener;
        this.mAccessPoint = accessPoint;
        this.mHideSubmitButton = z;
    }

    public static WifiDialog createModal(Context context, WifiDialogListener wifiDialogListener, AccessPoint accessPoint, int i) {
        return new WifiDialog(context, wifiDialogListener, accessPoint, i, 0, i == 0);
    }

    public static WifiDialog createModal(Context context, WifiDialogListener wifiDialogListener, AccessPoint accessPoint, int i, int i2) {
        return new WifiDialog(context, wifiDialogListener, accessPoint, i, i2, i == 0);
    }

    private void initNegativeButton() {
        final Button cancelButton = getCancelButton();
        if (this.mController.isCarrierCustomization()) {
            cancelButton.setText(getContext().getString(R.string.wifi_cancel));
        } else if (cancelButton == null || TextUtils.equals(cancelButton.getText(), getContext().getString(R.string.wifi_cancel))) {
        } else {
            cancelButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.WifiDialog.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    WifiDialog.this.mController.forceUpdateOptionFields(TextUtils.equals(cancelButton.getText(), WifiDialog.this.getContext().getString(R.string.wifi_eap_options_simple)));
                }
            });
        }
    }

    private boolean isFindDeviceLocked() {
        return Settings.Global.getInt(getContext().getContentResolver(), FindDeviceStatusManager.LOCK_SYS_SETTING, 0) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$0(View view) {
        if (this.mListener == null) {
            return;
        }
        this.mListener.onScan(this, ((TextView) findViewById(R.id.ssid)).getText().toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPassword(boolean z) {
        EditText editText = (EditText) this.mView.findViewById(R.id.password);
        int selectionEnd = editText.getSelectionEnd();
        editText.setInputType((z ? 144 : 128) | 1);
        editText.setTypeface(Typeface.DEFAULT);
        if (selectionEnd >= 0) {
            editText.setSelection(selectionEnd);
        }
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void dispatchSubmit() {
        WifiDialogListener wifiDialogListener = this.mListener;
        if (wifiDialogListener != null) {
            wifiDialogListener.onSubmit(this);
        }
        dismiss();
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public Button getCancelButton() {
        return getButton(-2);
    }

    public WifiConfigController getController() {
        return this.mController;
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public Button getForgetButton() {
        return getButton(-3);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public Button getSubmitButton() {
        return getButton(-1);
    }

    public void initWifiShare() {
        this.mView.findViewById(R.id.show_password_layout).setVisibility(8);
        AccessPoint accessPoint = this.mAccessPoint;
        boolean z = accessPoint != null && accessPoint.mShowPassword;
        ImageView imageView = (ImageView) this.mView.findViewById(R.id.show_password_img);
        imageView.setImageResource(z ? R.drawable.wifi_show_password : R.drawable.wifi_not_show_password);
        showPassword(z);
        imageView.setOnClickListener(new View.OnClickListener(z) { // from class: com.android.settings.wifi.WifiDialog.1
            private boolean mIsShowPassword;
            final /* synthetic */ boolean val$showPassword;

            {
                this.val$showPassword = z;
                this.mIsShowPassword = z;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                this.mIsShowPassword = !this.mIsShowPassword;
                if (WifiDialog.this.mAccessPoint != null) {
                    WifiDialog.this.mAccessPoint.mShowPassword = this.mIsShowPassword;
                }
                WifiDialog.this.showPassword(this.mIsShowPassword);
                ((ImageView) view).setImageResource(this.mIsShowPassword ? R.drawable.wifi_show_password : R.drawable.wifi_not_show_password);
            }
        });
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        WifiDialogListener wifiDialogListener = this.mListener;
        if (wifiDialogListener != null) {
            if (i != -3) {
                if (i != -1) {
                    return;
                }
                wifiDialogListener.onSubmit(this);
            } else if (WifiUtils.isNetworkLockedDown(getContext(), this.mAccessPoint.getConfig())) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), RestrictedLockUtilsInternal.getDeviceOwner(getContext()));
            } else {
                this.mListener.onForget(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void onCreate(Bundle bundle) {
        this.mView = getLayoutInflater().inflate(R.layout.wifi_dialog, (ViewGroup) null);
        View inflate = getLayoutInflater().inflate(R.layout.wifi_dialog_title, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.title);
        AccessPoint accessPoint = this.mAccessPoint;
        if (accessPoint != null) {
            textView.setText(accessPoint.getTitle());
        }
        setCustomTitle(inflate);
        setEnableImmersive(false);
        setView(this.mView);
        View view = this.mView;
        AccessPoint accessPoint2 = this.mAccessPoint;
        int i = this.mMode;
        if (i == 3) {
            i = 1;
        }
        this.mController = new WifiConfigController(this, view, accessPoint2, i);
        super.onCreate(bundle);
        if (isFindDeviceLocked()) {
            getWindow().addFlags(524288);
        }
        if (this.mHideSubmitButton) {
            this.mController.hideSubmitButton();
        } else {
            this.mController.enableSubmitIfAppropriate();
        }
        if (!TextUtils.isEmpty(this.mOcrWifiPwd)) {
            this.mController.displayOcrPwd(this.mOcrWifiPwd);
            this.mOcrWifiPwd = null;
        }
        initNegativeButton();
        initWifiShare();
        if (this.mAccessPoint == null) {
            this.mController.hideForgetButton();
        }
        if (this.mMode == 3) {
            this.mView.findViewById(R.id.l_info_reconnect).setVisibility(0);
            ((TextView) this.mView.findViewById(R.id.info_reconnect)).setText(getContext().getString(R.string.wifi_info_reconnect));
            setHapticFeedbackEnabled(true);
        }
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mController.updatePassword();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AlertDialog, android.app.Dialog
    public void onStart() {
        super.onStart();
        ImageButton imageButton = (ImageButton) findViewById(R.id.ssid_scanner_button);
        if (this.mHideSubmitButton) {
            imageButton.setVisibility(8);
        } else {
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.wifi.WifiDialog$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    WifiDialog.this.lambda$onStart$0(view);
                }
            });
        }
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setCancelButton(CharSequence charSequence) {
        setButton(-2, charSequence, null);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setForgetButton(CharSequence charSequence) {
        setButton(-3, charSequence, this);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setSubmitButton(CharSequence charSequence) {
        setButton(-1, charSequence, this);
    }
}
