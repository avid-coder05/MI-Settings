package com.android.settings.applications.specialaccess.zenaccess;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import com.android.settings.R;
import com.android.settings.applications.specialaccess.zenaccess.ZenAccessDetails;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miui.cloud.CloudPushConstants;
import miui.provider.MiCloudSmsCmd;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class FriendlyWarningDialogFragment extends InstrumentedDialogFragment {
    private ZenAccessDetails.OnCheckResult mResultCallback;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(String str, DialogInterface dialogInterface, int i) {
        ZenAccessController.deleteRules(getContext(), str);
        ZenAccessController.setAccess(getContext(), str, false);
        ZenAccessDetails.OnCheckResult onCheckResult = this.mResultCallback;
        if (onCheckResult != null) {
            onCheckResult.onResult(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        ZenAccessDetails.OnCheckResult onCheckResult = this.mResultCallback;
        if (onCheckResult != null) {
            onCheckResult.onResult(false);
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 555;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        final String string = arguments.getString(CloudPushConstants.WATERMARK_TYPE.PERSONAL);
        String string2 = getResources().getString(R.string.zen_access_revoke_warning_dialog_title, arguments.getString(MiCloudSmsCmd.TYPE_LOCATION));
        String string3 = getResources().getString(R.string.zen_access_revoke_warning_dialog_summary);
        ZenAccessDetails zenAccessDetails = (ZenAccessDetails) getTargetFragment();
        AlertDialog create = new AlertDialog.Builder(getContext()).setMessage(string3).setTitle(string2).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.specialaccess.zenaccess.FriendlyWarningDialogFragment$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                FriendlyWarningDialogFragment.this.lambda$onCreateDialog$0(string, dialogInterface, i);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.specialaccess.zenaccess.FriendlyWarningDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                FriendlyWarningDialogFragment.this.lambda$onCreateDialog$1(dialogInterface, i);
            }
        }).create();
        create.setCanceledOnTouchOutside(false);
        return create;
    }

    public FriendlyWarningDialogFragment setPkgInfo(String str, CharSequence charSequence, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString(CloudPushConstants.WATERMARK_TYPE.PERSONAL, str);
        if (!TextUtils.isEmpty(charSequence)) {
            str = charSequence.toString();
        }
        bundle.putString(MiCloudSmsCmd.TYPE_LOCATION, str);
        setTargetFragment(fragment, 0);
        setArguments(bundle);
        return this;
    }

    public void setResultCallback(ZenAccessDetails.OnCheckResult onCheckResult) {
        this.mResultCallback = onCheckResult;
    }
}
