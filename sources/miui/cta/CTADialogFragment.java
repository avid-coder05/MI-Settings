package miui.cta;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/* loaded from: classes3.dex */
public class CTADialogFragment extends DialogFragment {
    private static final String MESSAGE = "key_message";
    private static final String OPTIONAL = "key_optional";
    static final String TAG = "CTADialog";

    /* loaded from: classes3.dex */
    private class OnDialogClickListener implements DialogInterface.OnClickListener {
        private OnDialogClickListener() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                CTAManager.getInstance().onAccept(CTADialogFragment.this.getActivity());
            } else if (i == -2) {
                CTADialogFragment.this.onCancel(dialogInterface);
            }
        }
    }

    private boolean isOptional() {
        return getArguments().getBoolean(OPTIONAL);
    }

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        CTAManager.getInstance().onReject();
        Activity activity = getActivity();
        if (activity == null || isOptional()) {
            return;
        }
        activity.finish();
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        CTADialogBuilder cTADialogBuilder = new CTADialogBuilder(activity);
        cTADialogBuilder.setMessage(getArguments().getString(MESSAGE));
        OnDialogClickListener onDialogClickListener = new OnDialogClickListener();
        cTADialogBuilder.setPositiveButton(onDialogClickListener);
        if (isOptional()) {
            cTADialogBuilder.setNegativeButton(17039360, null);
        } else {
            cTADialogBuilder.setNegativeButton(onDialogClickListener);
        }
        return cTADialogBuilder.create();
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onStart() {
        super.onStart();
        if (CTAManager.getInstance().isAccepted()) {
            dismiss();
        }
    }

    public void showDialog(Activity activity, String str, boolean z) {
        Bundle arguments = getArguments();
        if (arguments == null) {
            arguments = new Bundle();
            setArguments(arguments);
        }
        arguments.putString(MESSAGE, str);
        arguments.putBoolean(OPTIONAL, z);
        show(activity.getFragmentManager(), TAG);
    }
}
