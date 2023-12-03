package com.android.settings.applications.specialaccess.notificationaccess;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miui.provider.MiCloudSmsCmd;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ScaryWarningDialogFragment extends InstrumentedDialogFragment {
    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 557;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        CharSequence charSequence = arguments.getCharSequence(MiCloudSmsCmd.TYPE_LOCATION);
        final ComponentName unflattenFromString = ComponentName.unflattenFromString(arguments.getString("c"));
        final NotificationAccessDetails notificationAccessDetails = (NotificationAccessDetails) getTargetFragment();
        return new AlertDialog.Builder(getContext()).setMessage(getResources().getString(R.string.notification_listener_security_warning_summary, charSequence)).setTitle(getResources().getString(R.string.notification_listener_security_warning_title, charSequence)).setCancelable(true).setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.specialaccess.notificationaccess.ScaryWarningDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                notificationAccessDetails.enable(unflattenFromString);
            }
        }).setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.specialaccess.notificationaccess.ScaryWarningDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ScaryWarningDialogFragment.lambda$onCreateDialog$0(dialogInterface, i);
            }
        }).create();
    }

    public ScaryWarningDialogFragment setServiceInfo(ComponentName componentName, CharSequence charSequence, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("c", componentName.flattenToString());
        bundle.putCharSequence(MiCloudSmsCmd.TYPE_LOCATION, charSequence);
        setArguments(bundle);
        setTargetFragment(fragment, 0);
        return this;
    }
}
