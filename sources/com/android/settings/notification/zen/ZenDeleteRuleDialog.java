package com.android.settings.notification.zen;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.BidiFormatter;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class ZenDeleteRuleDialog extends InstrumentedDialogFragment {
    protected static PositiveClickListener mPositiveClickListener;

    /* loaded from: classes2.dex */
    public interface PositiveClickListener {
        void onOk(String str);
    }

    public static void show(Fragment fragment, String str, String str2, PositiveClickListener positiveClickListener) {
        BidiFormatter bidiFormatter = BidiFormatter.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString("zen_rule_name", bidiFormatter.unicodeWrap(str));
        bundle.putString("zen_rule_id", str2);
        mPositiveClickListener = positiveClickListener;
        ZenDeleteRuleDialog zenDeleteRuleDialog = new ZenDeleteRuleDialog();
        zenDeleteRuleDialog.setArguments(bundle);
        zenDeleteRuleDialog.setTargetFragment(fragment, 0);
        zenDeleteRuleDialog.show(fragment.getFragmentManager(), "ZenDeleteRuleDialog");
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1266;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        final Bundle arguments = getArguments();
        String string = arguments.getString("zen_rule_name");
        final String string2 = arguments.getString("zen_rule_id");
        AlertDialog create = new AlertDialog.Builder(getContext()).setMessage(getString(R.string.zen_mode_delete_rule_confirmation, string)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.zen_mode_delete_rule_button, new DialogInterface.OnClickListener() { // from class: com.android.settings.notification.zen.ZenDeleteRuleDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (arguments != null) {
                    ZenDeleteRuleDialog.mPositiveClickListener.onOk(string2);
                }
            }
        }).create();
        View findViewById = create.findViewById(16908299);
        if (findViewById != null) {
            findViewById.setTextDirection(5);
        }
        return create;
    }
}
