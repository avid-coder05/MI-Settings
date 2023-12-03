package com.android.settings.inputmethod;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.UserHandle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeUtil;
import com.android.settingslib.inputmethod.InputMethodPreference;
import com.android.settingslib.inputmethod.InputMethodSettingValuesWrapper;
import miui.os.Build;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.AlertDialog;
import miuix.preference.FolmeAnimationController;

/* loaded from: classes.dex */
public class CustomInputMethodPreference extends InputMethodPreference implements FolmeAnimationController {
    private RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
    private AlertDialog mDialog;
    private boolean mDisabledByAdmin;
    private boolean mEnableTextState;
    private final boolean mHasPriorityInSorting;
    private InputMethodInfo mImi;
    private final InputMethodSettingValuesWrapper mInputMethodSettingValues;
    private boolean mIsAllowedByOrganization;
    private final InputMethodPreference.OnSavePreferenceListener mOnSaveListener;
    private Toast toast;

    public CustomInputMethodPreference(Context context, InputMethodInfo inputMethodInfo, boolean z, boolean z2, InputMethodPreference.OnSavePreferenceListener onSavePreferenceListener) {
        super(context, inputMethodInfo, z, z2, onSavePreferenceListener);
        this.mDialog = null;
        this.enforcedAdmin = null;
        this.mEnableTextState = true;
        setLayoutResource(R.xml.input_method_item_preference_layout);
        setPersistent(false);
        setWidgetLayoutResource(0);
        this.mImi = inputMethodInfo;
        this.mIsAllowedByOrganization = z2;
        this.mOnSaveListener = onSavePreferenceListener;
        setKey(inputMethodInfo.getId());
        this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(context);
        this.mHasPriorityInSorting = inputMethodInfo.isSystem() && InputMethodAndSubtypeUtil.isValidNonAuxAsciiCapableIme(inputMethodInfo);
    }

    private boolean isImeEnabler() {
        return getWidgetLayoutResource() != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(8)
    public boolean isTv() {
        return (getContext().getResources().getConfiguration().uiMode & 15) == 4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDirectBootWarnDialog$0(DialogInterface dialogInterface, int i) {
        setCheckedInternal(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDirectBootWarnDialog$1(DialogInterface dialogInterface, int i) {
        setCheckedInternal(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDirectBootWarnDialog$2(DialogInterface dialogInterface) {
        setCheckedInternal(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showSecurityWarnDialog$3(DialogInterface dialogInterface, int i) {
        setCheckedInternal(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showSecurityWarnDialog$4(DialogInterface dialogInterface, int i) {
        if (this.mImi.getServiceInfo().directBootAware || isTv()) {
            setCheckedInternal(true);
        } else {
            showDirectBootWarnDialog();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showSecurityWarnDialog$5(DialogInterface dialogInterface) {
        setCheckedInternal(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCheckedInternal(boolean z) {
        super.setChecked(z);
        this.mOnSaveListener.onSaveInputMethodPreference(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDirectBootWarnDialog() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(context.getText(R.string.miui_input_method_attention));
        builder.setMessage(context.getText(R.string.direct_boot_unaware_dialog_message));
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.inputmethod.CustomInputMethodPreference$$ExternalSyntheticLambda5
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CustomInputMethodPreference.this.lambda$showDirectBootWarnDialog$0(dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.inputmethod.CustomInputMethodPreference$$ExternalSyntheticLambda3
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CustomInputMethodPreference.this.lambda$showDirectBootWarnDialog$1(dialogInterface, i);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settings.inputmethod.CustomInputMethodPreference$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                CustomInputMethodPreference.this.lambda$showDirectBootWarnDialog$2(dialogInterface);
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @TargetApi(24)
    public void showSecurityWarnDialog() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        boolean z = Build.IS_INTERNATIONAL_BUILD;
        builder.setTitle(z ? R.string.ime_security_warning_title_global : R.string.risk_tip);
        builder.setMessage(context.getString(z ? R.string.ime_security_warning_global : R.string.ime_security_warning, this.mImi.getServiceInfo().applicationInfo.loadLabel(context.getPackageManager())));
        builder.setPositiveButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.inputmethod.CustomInputMethodPreference$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CustomInputMethodPreference.this.lambda$showSecurityWarnDialog$3(dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.inputmethod.CustomInputMethodPreference$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CustomInputMethodPreference.this.lambda$showSecurityWarnDialog$4(dialogInterface, i);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settings.inputmethod.CustomInputMethodPreference$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                CustomInputMethodPreference.this.lambda$showSecurityWarnDialog$5(dialogInterface);
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    public boolean CTSVerify() {
        if (this.mDisabledByAdmin) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), this.enforcedAdmin);
            return true;
        }
        return false;
    }

    public void checkPreferenceViews() {
        if (this.mInputMethodSettingValues.isAlwaysCheckedIme(this.mImi) && isImeEnabler()) {
            setDisabledThisByAdmin(null);
        } else if (this.mIsAllowedByOrganization) {
        } else {
            setDisabledThisByAdmin(RestrictedLockUtilsInternal.checkIfInputMethodDisallowed(getContext(), this.mImi.getPackageName(), UserHandle.myUserId()));
        }
    }

    @Override // miuix.preference.FolmeAnimationController
    public boolean isTouchAnimationEnable() {
        return false;
    }

    public void jumpToInputMethodSettings(InputMethodInfo inputMethodInfo) {
        Intent intent;
        String settingsActivity = inputMethodInfo.getSettingsActivity();
        if (TextUtils.isEmpty(settingsActivity)) {
            intent = null;
        } else {
            Intent intent2 = new Intent("android.intent.action.MAIN");
            intent2.setClassName(inputMethodInfo.getPackageName(), settingsActivity);
            intent = intent2;
        }
        Context context = getContext();
        if (intent != null) {
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException unused) {
                String string = context.getString(R.string.failed_to_open_app_settings_toast, this.mImi.loadLabel(context.getPackageManager()));
                if (this.toast == null) {
                    this.toast = Toast.makeText(context, string, 0);
                } else {
                    this.toast.cancel();
                }
                this.toast.show();
            }
        }
    }

    @Override // com.android.settingslib.RestrictedSwitchPreference, com.android.settingslib.miuisettings.preference.SwitchPreference, androidx.preference.SwitchPreference, androidx.preference.Preference
    @SuppressLint({"ClickableViewAccessibility"})
    @TargetApi(3)
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        InputMethodManager inputMethodManager;
        Folme.clean(preferenceViewHolder.itemView);
        checkPreferenceViews();
        View findViewById = preferenceViewHolder.itemView.findViewById(R.id.head);
        View findViewById2 = preferenceViewHolder.itemView.findViewById(R.id.end);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(R.id.enable_mode);
        TextView textView2 = (TextView) preferenceViewHolder.itemView.findViewById(16908310);
        TextView textView3 = (TextView) preferenceViewHolder.itemView.findViewById(16908304);
        ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(16908294);
        if (textView != null) {
            textView.setEnabled(this.mEnableTextState);
        }
        InputMethodInfo inputMethodInfo = this.mImi;
        if (inputMethodInfo != null) {
            if (textView2 != null) {
                textView2.setText(inputMethodInfo.loadLabel(getContext().getPackageManager()));
            }
            if (textView3 != null && (inputMethodManager = (InputMethodManager) getContext().getSystemService("input_method")) != null) {
                String subtypeLocaleNameListAsSentence = InputMethodAndSubtypeUtil.getSubtypeLocaleNameListAsSentence(inputMethodManager.getEnabledInputMethodSubtypeList(this.mImi, true), getContext(), this.mImi);
                textView3.setText(subtypeLocaleNameListAsSentence);
                if (subtypeLocaleNameListAsSentence == null || "".equals(subtypeLocaleNameListAsSentence)) {
                    textView3.setVisibility(8);
                }
            }
            if (imageView != null) {
                imageView.setBackground(this.mImi.loadIcon(getContext().getPackageManager()));
            }
        }
        if (textView != null) {
            Folme.useAt(textView).touch().setScale(1.0f, ITouchStyle.TouchType.DOWN).handleTouchOf(textView, new AnimConfig[0]);
            if (isChecked()) {
                textView.setText(getContext().getResources().getString(R.string.enabled));
                textView.setBackground(getContext().getResources().getDrawable(R.drawable.button_text_enable_normal_bg));
                textView.setTextColor(getContext().getResources().getColor(R.color.input_method_manage_text_enable_color));
            } else {
                textView.setText(getContext().getResources().getString(R.string.not_enabled));
                textView.setBackground(getContext().getResources().getDrawable(R.drawable.button_text_not_enable_normal_bg));
                textView.setTextColor(getContext().getResources().getColor(R.color.input_method_manage_text_un_enable_color));
            }
        }
        if (findViewById != null) {
            if (CommonUtils.isRtl()) {
                findViewById.setBackgroundResource(R.drawable.preference_system_app_new_head_bg_rtl);
            } else {
                findViewById.setBackgroundResource(R.drawable.preference_system_app_new_head_bg);
            }
            Folme.useAt(findViewById).touch().setScale(1.0f, ITouchStyle.TouchType.DOWN).handleTouchOf(findViewById, new AnimConfig[0]);
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.inputmethod.CustomInputMethodPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    CustomInputMethodPreference customInputMethodPreference = CustomInputMethodPreference.this;
                    customInputMethodPreference.jumpToInputMethodSettings(customInputMethodPreference.mImi);
                }
            });
            if (isChecked()) {
                findViewById.setEnabled(true);
            } else {
                findViewById.setEnabled(false);
            }
        }
        if (findViewById2 != null) {
            if (CommonUtils.isRtl()) {
                findViewById2.setBackgroundResource(R.drawable.preference_system_app_new_end_bg_rtl);
            } else {
                findViewById2.setBackgroundResource(R.drawable.preference_system_app_new_end_bg);
            }
        }
        if (textView != null) {
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.inputmethod.CustomInputMethodPreference.2
                @Override // android.view.View.OnClickListener
                @TargetApi(24)
                public void onClick(View view) {
                    if (CustomInputMethodPreference.this.CTSVerify()) {
                        return;
                    }
                    if (CustomInputMethodPreference.this.isChecked()) {
                        CustomInputMethodPreference.this.setCheckedInternal(false);
                    } else if (!CustomInputMethodPreference.this.mImi.isSystem()) {
                        CustomInputMethodPreference.this.showSecurityWarnDialog();
                    } else if (CustomInputMethodPreference.this.mImi.getServiceInfo().directBootAware || CustomInputMethodPreference.this.isTv()) {
                        CustomInputMethodPreference.this.setCheckedInternal(true);
                    } else if (CustomInputMethodPreference.this.isTv()) {
                    } else {
                        CustomInputMethodPreference.this.showDirectBootWarnDialog();
                    }
                }
            });
        }
    }

    @Override // com.android.settingslib.inputmethod.InputMethodPreference, androidx.preference.Preference.OnPreferenceChangeListener
    @TargetApi(24)
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    @Override // com.android.settingslib.inputmethod.InputMethodPreference, androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    public boolean setDisabledThisByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        boolean z = enforcedAdmin != null;
        this.enforcedAdmin = enforcedAdmin;
        if (this.mDisabledByAdmin != z) {
            this.mDisabledByAdmin = z;
            return true;
        }
        return false;
    }

    public void setEnableModeText(boolean z) {
        this.mEnableTextState = z;
        notifyChanged();
    }
}
