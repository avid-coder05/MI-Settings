package com.android.settings.network;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivitySettingsManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.HelpUtils;
import com.google.common.net.InternetDomainName;
import java.util.HashMap;
import java.util.Map;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class PrivateDnsSettingsActivity extends AppCompatActivity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, TextWatcher {
    static final String HOSTNAME_KEY = "private_dns_specifier";
    static final String MODE_KEY = "private_dns_mode";
    private static final Map<Integer, Integer> PRIVATE_DNS_MAP;
    private Context mContext;
    private AlertDialog mDialog;
    EditText mEditText;
    int mMode;
    RadioGroup mRadioGroup;

    static {
        HashMap hashMap = new HashMap();
        PRIVATE_DNS_MAP = hashMap;
        hashMap.put(1, Integer.valueOf(R.id.private_dns_mode_off));
        hashMap.put(2, Integer.valueOf(R.id.private_dns_mode_opportunistic));
        hashMap.put(3, Integer.valueOf(R.id.private_dns_mode_provider));
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(R.string.dns_select_dialog_title);
        builder.setPositiveButton(R.string.save, this);
        builder.setNegativeButton(R.string.button_text_cancel, this);
        View onCreateDialogView = onCreateDialogView();
        if (onCreateDialogView != null) {
            onBindDialogView(onCreateDialogView);
            builder.setView(onCreateDialogView);
        }
        builder.setOnDismissListener(this);
        builder.setCancelable(true);
        int privateDnsMode = ConnectivitySettingsManager.getPrivateDnsMode(this.mContext);
        this.mMode = privateDnsMode;
        if (privateDnsMode == 3) {
            showOrHideEditText(true);
        } else {
            showOrHideEditText(false);
        }
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.choose_dns_mode), this.mMode - 1, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.PrivateDnsSettingsActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    PrivateDnsSettingsActivity privateDnsSettingsActivity = PrivateDnsSettingsActivity.this;
                    privateDnsSettingsActivity.mMode = 1;
                    privateDnsSettingsActivity.showOrHideEditText(false);
                } else if (i == 1) {
                    PrivateDnsSettingsActivity privateDnsSettingsActivity2 = PrivateDnsSettingsActivity.this;
                    privateDnsSettingsActivity2.mMode = 2;
                    privateDnsSettingsActivity2.showOrHideEditText(false);
                } else if (i == 2) {
                    PrivateDnsSettingsActivity privateDnsSettingsActivity3 = PrivateDnsSettingsActivity.this;
                    privateDnsSettingsActivity3.mMode = 3;
                    privateDnsSettingsActivity3.showOrHideEditText(true);
                }
                PrivateDnsSettingsActivity.this.updateDialogInfo();
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    public static String getHostnameFromSettings(ContentResolver contentResolver) {
        return Settings.Global.getString(contentResolver, HOSTNAME_KEY);
    }

    private Button getSaveButton() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog == null) {
            return null;
        }
        return alertDialog.getButton(-1);
    }

    private void onBindDialogView(View view) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        EditText editText = (EditText) view.findViewById(R.id.private_dns_mode_provider_hostname);
        this.mEditText = editText;
        editText.setText(getHostnameFromSettings(contentResolver));
        this.mEditText.addTextChangedListener(this);
        TextView textView = (TextView) view.findViewById(R.id.private_dns_help_info);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        Context context = this.mContext;
        AnnotationSpan.LinkInfo linkInfo = new AnnotationSpan.LinkInfo(this.mContext, "url", HelpUtils.getHelpIntent(context, context.getString(R.string.help_uri_private_dns), this.mContext.getClass().getName()));
        if (!linkInfo.isActionable()) {
            textView.setVisibility(8);
            return;
        }
        textView.setVisibility(0);
        textView.setText(AnnotationSpan.linkify(this.mContext.getText(R.string.private_dns_help_message), linkInfo));
    }

    private View onCreateDialogView() {
        return LayoutInflater.from(this).inflate(R.layout.private_dns_mode, (ViewGroup) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showOrHideEditText(boolean z) {
        EditText editText = this.mEditText;
        if (editText != null) {
            editText.setVisibility(z ? 0 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDialogInfo() {
        boolean z = 3 == this.mMode;
        EditText editText = this.mEditText;
        if (editText != null) {
            editText.setEnabled(z);
        }
        Button saveButton = getSaveButton();
        if (saveButton != null) {
            saveButton.setEnabled(z ? InternetDomainName.isValid(this.mEditText.getText().toString()) : true);
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        updateDialogInfo();
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            if (this.mMode == 3) {
                ConnectivitySettingsManager.setPrivateDnsHostname(this.mContext, this.mEditText.getText().toString());
            }
            FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 1249, this.mMode);
            ConnectivitySettingsManager.setPrivateDnsMode(this.mContext, this.mMode);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
        createDialog();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        super.onStop();
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}
