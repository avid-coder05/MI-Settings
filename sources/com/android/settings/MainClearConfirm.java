package com.android.settings;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.app.admin.FactoryResetProtectionPolicy;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.oemlock.OemLockManager;
import android.service.persistentdata.PersistentDataBlockManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
public class MainClearConfirm extends InstrumentedFragment {
    View mContentView;
    boolean mEraseEsims;
    private boolean mEraseSdCard;
    private View.OnClickListener mFinalClickListener = new View.OnClickListener() { // from class: com.android.settings.MainClearConfirm.1
        /* JADX INFO: Access modifiers changed from: private */
        public ProgressDialog getProgressDialog() {
            ProgressDialog progressDialog = new ProgressDialog(MainClearConfirm.this.getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(MainClearConfirm.this.getActivity().getString(R.string.main_clear_progress_title));
            progressDialog.setMessage(MainClearConfirm.this.getActivity().getString(R.string.main_clear_progress_text));
            return progressDialog;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (Utils.isMonkeyRunning()) {
                return;
            }
            final PersistentDataBlockManager persistentDataBlockManager = (PersistentDataBlockManager) MainClearConfirm.this.getActivity().getSystemService("persistent_data_block");
            if (MainClearConfirm.this.shouldWipePersistentDataBlock(persistentDataBlockManager)) {
                new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.MainClearConfirm.1.1
                    int mOldOrientation;
                    ProgressDialog mProgressDialog;

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Void doInBackground(Void... voidArr) {
                        persistentDataBlockManager.wipe();
                        return null;
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public void onPostExecute(Void r2) {
                        this.mProgressDialog.hide();
                        if (MainClearConfirm.this.getActivity() != null) {
                            MainClearConfirm.this.getActivity().setRequestedOrientation(this.mOldOrientation);
                            MainClearConfirm.this.doMainClear();
                        }
                    }

                    @Override // android.os.AsyncTask
                    protected void onPreExecute() {
                        ProgressDialog progressDialog = getProgressDialog();
                        this.mProgressDialog = progressDialog;
                        progressDialog.show();
                        this.mOldOrientation = MainClearConfirm.this.getActivity().getRequestedOrientation();
                        MainClearConfirm.this.getActivity().setRequestedOrientation(14);
                    }
                }.execute(new Void[0]);
            } else {
                MainClearConfirm.this.doMainClear();
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void doMainClear() {
        Intent intent = new Intent("android.intent.action.FACTORY_RESET");
        intent.setPackage(ThemeResources.FRAMEWORK_PACKAGE);
        intent.addFlags(268435456);
        intent.putExtra("android.intent.extra.REASON", "MainClearConfirm");
        intent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", this.mEraseSdCard);
        intent.putExtra("com.android.internal.intent.extra.WIPE_ESIMS", this.mEraseEsims);
        getActivity().sendBroadcast(intent);
    }

    private void establishFinalConfirmationState() {
        ((FooterBarMixin) ((GlifLayout) this.mContentView.findViewById(R.id.setup_wizard_layout)).getMixin(FooterBarMixin.class)).setPrimaryButton(new FooterButton.Builder(getActivity()).setText(R.string.main_clear_button_text).setListener(this.mFinalClickListener).setButtonType(0).setTheme(R.style.SudGlifButton_Primary).build());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onInflateView$0(DialogInterface dialogInterface) {
        getActivity().finish();
    }

    private void setAccessibilityTitle() {
        CharSequence title = getActivity().getTitle();
        TextView textView = (TextView) this.mContentView.findViewById(R.id.sud_layout_description);
        if (textView != null) {
            getActivity().setTitle(Utils.createAccessibleSequence(title, title + "," + textView.getText()));
        }
    }

    private void setUpActionBarAndTitle() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.e("MainClearConfirm", "No activity attached, skipping setUpActionBarAndTitle");
            return;
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar == null) {
            Log.e("MainClearConfirm", "No actionbar, skipping setUpActionBarAndTitle");
            return;
        }
        actionBar.hide();
        activity.getWindow().setStatusBarColor(0);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 67;
    }

    boolean isDeviceStillBeingProvisioned() {
        return !WizardManagerHelper.isDeviceProvisioned(getActivity());
    }

    boolean isOemUnlockedAllowed() {
        return ((OemLockManager) getActivity().getSystemService("oem_lock")).isOemUnlockAllowed();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        this.mEraseSdCard = arguments != null && arguments.getBoolean("erase_sd");
        this.mEraseEsims = arguments != null && arguments.getBoolean("erase_esim");
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_factory_reset", UserHandle.myUserId());
        if (RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_factory_reset", UserHandle.myUserId())) {
            return layoutInflater.inflate(R.layout.main_clear_disallowed_screen, (ViewGroup) null);
        }
        if (checkIfRestrictionEnforced != null) {
            new ActionDisabledByAdminDialogHelper(getActivity()).prepareDialogBuilder("no_factory_reset", checkIfRestrictionEnforced).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.MainClearConfirm$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    MainClearConfirm.this.lambda$onInflateView$0(dialogInterface);
                }
            }).show();
            return new View(getActivity());
        }
        this.mContentView = layoutInflater.inflate(R.layout.main_clear_confirm, (ViewGroup) null);
        setUpActionBarAndTitle();
        establishFinalConfirmationState();
        setAccessibilityTitle();
        setSubtitle();
        return this.mContentView;
    }

    void setSubtitle() {
        if (this.mEraseEsims) {
            ((TextView) this.mContentView.findViewById(R.id.sud_layout_description)).setText(R.string.main_clear_final_desc_esim);
        }
    }

    boolean shouldWipePersistentDataBlock(PersistentDataBlockManager persistentDataBlockManager) {
        if (persistentDataBlockManager == null || isDeviceStillBeingProvisioned() || isOemUnlockedAllowed()) {
            return false;
        }
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        if (devicePolicyManager.isFactoryResetProtectionPolicySupported()) {
            FactoryResetProtectionPolicy factoryResetProtectionPolicy = devicePolicyManager.getFactoryResetProtectionPolicy(null);
            return (devicePolicyManager.isOrganizationOwnedDeviceWithManagedProfile() && factoryResetProtectionPolicy != null && factoryResetProtectionPolicy.isNotEmpty()) ? false : true;
        }
        return false;
    }
}
