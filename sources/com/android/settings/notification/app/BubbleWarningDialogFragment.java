package com.android.settings.notification.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miui.cloud.CloudPushConstants;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class BubbleWarningDialogFragment extends InstrumentedDialogFragment {
    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(String str, int i, int i2, DialogInterface dialogInterface, int i3) {
        BubblePreferenceController.applyBubblesApproval(getContext(), str, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(String str, int i, DialogInterface dialogInterface, int i2) {
        BubblePreferenceController.revertBubblesApproval(getContext(), str, i);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1702;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        final String string = arguments.getString(CloudPushConstants.WATERMARK_TYPE.PERSONAL);
        final int i = arguments.getInt("u");
        final int i2 = arguments.getInt("pref");
        return new AlertDialog.Builder(getContext(), R.style.AlertDialog_Theme_DayNight).setMessage(getResources().getString(R.string.bubbles_feature_disabled_dialog_text)).setTitle(getResources().getString(R.string.bubbles_feature_disabled_dialog_title)).setCancelable(true).setPositiveButton(R.string.bubbles_feature_disabled_button_approve, new DialogInterface.OnClickListener() { // from class: com.android.settings.notification.app.BubbleWarningDialogFragment$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                BubbleWarningDialogFragment.this.lambda$onCreateDialog$0(string, i, i2, dialogInterface, i3);
            }
        }).setNegativeButton(R.string.bubbles_feature_disabled_button_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.notification.app.BubbleWarningDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                BubbleWarningDialogFragment.this.lambda$onCreateDialog$1(string, i, dialogInterface, i3);
            }
        }).create();
    }

    public BubbleWarningDialogFragment setPkgPrefInfo(String str, int i, int i2) {
        Bundle bundle = new Bundle();
        bundle.putString(CloudPushConstants.WATERMARK_TYPE.PERSONAL, str);
        bundle.putInt("u", i);
        bundle.putInt("pref", i2);
        setArguments(bundle);
        return this;
    }
}
