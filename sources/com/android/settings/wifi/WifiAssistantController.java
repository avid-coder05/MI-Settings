package com.android.settings.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStop;
import miuix.appcompat.app.AlertDialog;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes2.dex */
public class WifiAssistantController extends AbstractPreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnStop, DialogInterface.OnDismissListener {
    private SlidingButton mButton;
    private final Context mContext;
    private AlertDialog mDialog;

    public WifiAssistantController(Context context) {
        super(context);
        this.mContext = context;
    }

    public static void enableWifiAssistant(Context context, boolean z) {
        if (context != null) {
            Settings.System.putInt(context.getContentResolver(), "wifi_assistant", z ? 1 : 0);
        }
    }

    public static void enableWifiAssistantDataPrompt(Context context, boolean z) {
        if (context != null) {
            Settings.System.putInt(context.getContentResolver(), "wifi_assistant_data_prompt", z ? 1 : 0);
        }
    }

    public static boolean isWifiAssistantDataPromptEnabled(Context context) {
        return context == null || Settings.System.getInt(context.getContentResolver(), "wifi_assistant_data_prompt", 1) == 1;
    }

    public static boolean isWifiAssistantEnabled(Context context) {
        return context == null || Settings.System.getInt(context.getContentResolver(), "wifi_assistant", 1) == 1;
    }

    private void popupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(R.string.wifi_assistant_prompt);
        builder.setMessage(R.string.wifi_assistant_prompt_summary);
        builder.setPositiveButton(R.string.wifi_assistant_prompt_button, (DialogInterface.OnClickListener) null);
        LinearLayout linearLayout = (LinearLayout) View.inflate(this.mContext, R.layout.wifi_assistant_prompt_dialog, null);
        SlidingButton slidingButton = (SlidingButton) linearLayout.findViewById(R.id.inquiry);
        this.mButton = slidingButton;
        slidingButton.setChecked(isWifiAssistantDataPromptEnabled(this.mContext));
        builder.setView(linearLayout);
        builder.setCancelable(true);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.setOnDismissListener(this);
        this.mDialog.show();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_assistant";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return MiuiWifiAssistFeatureSupport.isWifiAssistantAvailable(this.mContext);
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        SlidingButton slidingButton = this.mButton;
        if (slidingButton != null) {
            enableWifiAssistantDataPrompt(this.mContext, slidingButton.isChecked());
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (TextUtils.equals(preference.getKey(), "wifi_assistant")) {
            Boolean bool = (Boolean) obj;
            enableWifiAssistant(this.mContext, bool.booleanValue());
            if (bool.booleanValue()) {
                popupDialog();
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        this.mDialog.dismiss();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (TextUtils.equals(preference.getKey(), "wifi_assistant")) {
            ((CheckBoxPreference) preference).setChecked(isWifiAssistantEnabled(this.mContext));
        }
    }
}
