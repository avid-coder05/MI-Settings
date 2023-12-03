package com.android.settings.applications.defaultapps;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.RadioButtonPreference;
import miui.vip.VipService;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public abstract class DefaultAppPickerFragment extends RadioButtonPickerFragment {
    protected BatteryUtils mBatteryUtils;
    protected PackageManager mPm;

    /* loaded from: classes.dex */
    public static class ConfirmationDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        private DialogInterface.OnClickListener mCancelListener;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 791;
        }

        public void init(DefaultAppPickerFragment defaultAppPickerFragment, String str, CharSequence charSequence) {
            Bundle bundle = new Bundle();
            bundle.putString("extra_key", str);
            bundle.putCharSequence("extra_message", charSequence);
            setArguments(bundle);
            setTargetFragment(defaultAppPickerFragment, 0);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment instanceof DefaultAppPickerFragment) {
                ((DefaultAppPickerFragment) targetFragment).onRadioButtonConfirmed(getArguments().getString("extra_key"));
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            Bundle arguments = getArguments();
            View inflate = getActivity().getLayoutInflater().inflate(R.layout.default_app_picker_dialog, (ViewGroup) null);
            ((TextView) inflate.findViewById(16908299)).setText(arguments.getCharSequence("extra_message"));
            AlertDialog.Builder negativeButton = new AlertDialog.Builder(getActivity()).setPositiveButton(17039370, this).setNegativeButton(17039360, this.mCancelListener);
            negativeButton.setView(inflate);
            return negativeButton.create();
        }

        public void setCancelListener(DialogInterface.OnClickListener onClickListener) {
            this.mCancelListener = onClickListener;
        }
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void bindPreferenceExtra(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        if (candidateInfo instanceof DefaultAppInfo) {
            if (TextUtils.equals(str3, str)) {
                radioButtonPreference.setSummary(R.string.system_app);
                return;
            }
            DefaultAppInfo defaultAppInfo = (DefaultAppInfo) candidateInfo;
            if (TextUtils.isEmpty(defaultAppInfo.summary)) {
                return;
            }
            radioButtonPreference.setSummary(defaultAppInfo.summary);
        }
    }

    protected CharSequence getConfirmationMessage(CandidateInfo candidateInfo) {
        return null;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected int getRadioButtonPreferenceCustomLayoutResId() {
        return R.layout.miuix_preference_radiobutton_two_state_background;
    }

    protected ConfirmationDialogFragment newConfirmationDialogFragment(String str, CharSequence charSequence) {
        ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
        confirmationDialogFragment.init(this, str, charSequence);
        return confirmationDialogFragment;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mPm = context.getPackageManager();
        this.mBatteryUtils = BatteryUtils.getInstance(context);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settingslib.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        String key = radioButtonPreference.getKey();
        CharSequence confirmationMessage = getConfirmationMessage(getCandidate(key));
        FragmentActivity activity = getActivity();
        if (TextUtils.isEmpty(confirmationMessage)) {
            super.onRadioButtonClicked(radioButtonPreference);
        } else if (activity != null) {
            newConfirmationDialogFragment(key, confirmationMessage).show(activity.getSupportFragmentManager(), "DefaultAppConfirm");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void onRadioButtonConfirmed(String str) {
        MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), VipService.VIP_SERVICE_FAILURE, getMetricsCategory(), str, 0);
        super.onRadioButtonConfirmed(str);
    }
}
