package com.android.settings.sim;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.network.SubscriptionUtil;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class PreferredSimDialogFragment extends SimDialogFragment implements DialogInterface.OnClickListener {
    public static PreferredSimDialogFragment newInstance() {
        PreferredSimDialogFragment preferredSimDialogFragment = new PreferredSimDialogFragment();
        preferredSimDialogFragment.setArguments(SimDialogFragment.initArguments(3, R.string.sim_preferred_title));
        return preferredSimDialogFragment;
    }

    private void updateDialog(AlertDialog alertDialog) {
        Log.d("PreferredSimDialogFrag", "Dialog updated, dismiss status: " + this.mWasDismissed);
        SubscriptionInfo preferredSubscription = getPreferredSubscription();
        if (this.mWasDismissed) {
            return;
        }
        if (preferredSubscription == null) {
            dismiss();
        } else {
            alertDialog.setMessage(getContext().getString(R.string.sim_preferred_message, SubscriptionUtil.getUniqueSubscriptionDisplayName(preferredSubscription, getContext())));
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1709;
    }

    public SubscriptionInfo getPreferredSubscription() {
        return getSubscriptionManager().getActiveSubscriptionInfoForSimSlotIndex(getActivity().getIntent().getIntExtra(SimDialogActivity.PREFERRED_SIM, -1));
    }

    protected SubscriptionManager getSubscriptionManager() {
        return (SubscriptionManager) getContext().getSystemService(SubscriptionManager.class);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            return;
        }
        SimDialogActivity simDialogActivity = (SimDialogActivity) getActivity();
        SubscriptionInfo preferredSubscription = getPreferredSubscription();
        if (preferredSubscription != null) {
            simDialogActivity.onSubscriptionSelected(getDialogType(), preferredSubscription.getSubscriptionId());
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(getTitleResId()).setPositiveButton(R.string.yes, this).setNegativeButton(R.string.no, (DialogInterface.OnClickListener) null).create();
        updateDialog(create);
        return create;
    }

    @Override // com.android.settings.sim.SimDialogFragment
    public void updateDialog() {
        updateDialog((AlertDialog) getDialog());
    }
}
