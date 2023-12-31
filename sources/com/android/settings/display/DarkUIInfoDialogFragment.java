package com.android.settings.display;

import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class DarkUIInfoDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private void enableDarkTheme() {
        Context context = getContext();
        if (context != null) {
            Settings.Secure.putInt(context.getContentResolver(), DarkUIPreferenceController.PREF_DARK_MODE_DIALOG_SEEN, 1);
            ((UiModeManager) context.getSystemService(UiModeManager.class)).setNightMode(2);
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1740;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        enableDarkTheme();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View inflate = LayoutInflater.from(builder.getContext()).inflate(R.layout.settings_dialog_title, (ViewGroup) null);
        ((ImageView) inflate.findViewById(R.id.settings_icon)).setImageDrawable(context.getDrawable(R.drawable.dark_theme));
        ((TextView) inflate.findViewById(R.id.settings_title)).setText(R.string.dark_ui_mode);
        builder.setCustomTitle(inflate).setMessage(R.string.dark_ui_settings_dark_summary).setPositiveButton(R.string.dark_ui_settings_dialog_acknowledge, this);
        return builder.create();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        enableDarkTheme();
        super.onDismiss(dialogInterface);
    }
}
