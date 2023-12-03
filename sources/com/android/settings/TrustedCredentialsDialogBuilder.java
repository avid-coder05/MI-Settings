package com.android.settings;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.pm.UserInfo;
import android.net.http.SslCertificate;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.TrustedCredentialsDialogBuilder;
import com.android.settings.TrustedCredentialsSettings;
import com.android.settingslib.RestrictedLockUtils;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntConsumer;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
class TrustedCredentialsDialogBuilder extends AlertDialog.Builder {
    private final DialogEventHandler mDialogEventHandler;

    /* loaded from: classes.dex */
    public interface DelegateInterface {
        List<X509Certificate> getX509CertsFromCertHolder(TrustedCredentialsSettings.CertHolder certHolder);

        void removeOrInstallCert(TrustedCredentialsSettings.CertHolder certHolder);

        boolean startConfirmCredentialIfNotConfirmed(int i, IntConsumer intConsumer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DialogEventHandler implements DialogInterface.OnShowListener, View.OnClickListener {
        private final Activity mActivity;
        private final DelegateInterface mDelegate;
        private AlertDialog mDialog;
        private final DevicePolicyManager mDpm;
        private boolean mNeedsApproval;
        private Button mNegativeButton;
        private Button mPositiveButton;
        private final LinearLayout mRootContainer;
        private final UserManager mUserManager;
        private int mCurrentCertIndex = -1;
        private TrustedCredentialsSettings.CertHolder[] mCertHolders = new TrustedCredentialsSettings.CertHolder[0];
        private View mCurrentCertLayout = null;

        public DialogEventHandler(Activity activity, DelegateInterface delegateInterface) {
            this.mActivity = activity;
            this.mDpm = (DevicePolicyManager) activity.getSystemService(DevicePolicyManager.class);
            this.mUserManager = (UserManager) activity.getSystemService(UserManager.class);
            this.mDelegate = delegateInterface;
            LinearLayout linearLayout = new LinearLayout(activity);
            this.mRootContainer = linearLayout;
            linearLayout.setOrientation(1);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addAndAnimateNewContent(View view) {
            this.mCurrentCertLayout = view;
            this.mRootContainer.removeAllViews();
            this.mRootContainer.addView(view);
            this.mRootContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.settings.TrustedCredentialsDialogBuilder.DialogEventHandler.4
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    DialogEventHandler.this.mRootContainer.removeOnLayoutChangeListener(this);
                    DialogEventHandler.this.mCurrentCertLayout.setTranslationX(DialogEventHandler.this.mRootContainer.getWidth());
                    DialogEventHandler.this.mCurrentCertLayout.animate().translationX(0.0f).setInterpolator(AnimationUtils.loadInterpolator(DialogEventHandler.this.mActivity, 17563662)).setDuration(200L).start();
                }
            });
        }

        private void animateOldContent(Runnable runnable) {
            this.mCurrentCertLayout.animate().alpha(0.0f).setDuration(300L).setInterpolator(AnimationUtils.loadInterpolator(this.mActivity, 17563663)).withEndAction(runnable).start();
        }

        private void animateViewTransition(final View view) {
            animateOldContent(new Runnable() { // from class: com.android.settings.TrustedCredentialsDialogBuilder.DialogEventHandler.3
                @Override // java.lang.Runnable
                public void run() {
                    DialogEventHandler.this.addAndAnimateNewContent(view);
                }
            });
        }

        private static int getButtonLabel(TrustedCredentialsSettings.CertHolder certHolder) {
            return certHolder.isSystemCert() ? certHolder.isDeleted() ? R.string.trusted_credentials_enable_label : R.string.trusted_credentials_disable_label : R.string.trusted_credentials_remove_label;
        }

        private LinearLayout getCertLayout(TrustedCredentialsSettings.CertHolder certHolder) {
            final ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            List<X509Certificate> x509CertsFromCertHolder = this.mDelegate.getX509CertsFromCertHolder(certHolder);
            if (x509CertsFromCertHolder != null) {
                Iterator<X509Certificate> it = x509CertsFromCertHolder.iterator();
                while (it.hasNext()) {
                    SslCertificate sslCertificate = new SslCertificate(it.next());
                    arrayList.add(sslCertificate.inflateCertificateView(this.mActivity));
                    arrayList2.add(sslCertificate.getIssuedTo().getCName());
                }
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this.mActivity, 17367048, arrayList2);
            arrayAdapter.setDropDownViewResource(17367049);
            Spinner spinner = new Spinner(this.mActivity);
            spinner.setAdapter((SpinnerAdapter) arrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.android.settings.TrustedCredentialsDialogBuilder.DialogEventHandler.2
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                    int i2 = 0;
                    while (i2 < arrayList.size()) {
                        ((View) arrayList.get(i2)).setVisibility(i2 == i ? 0 : 8);
                        i2++;
                    }
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            LinearLayout linearLayout = new LinearLayout(this.mActivity);
            linearLayout.setOrientation(1);
            linearLayout.addView(spinner);
            int i = 0;
            while (i < arrayList.size()) {
                View view = (View) arrayList.get(i);
                view.setVisibility(i == 0 ? 0 : 8);
                linearLayout.addView(view);
                i++;
            }
            return linearLayout;
        }

        private TrustedCredentialsSettings.CertHolder getCurrentCertInfo() {
            int i = this.mCurrentCertIndex;
            TrustedCredentialsSettings.CertHolder[] certHolderArr = this.mCertHolders;
            if (i < certHolderArr.length) {
                return certHolderArr[i];
            }
            return null;
        }

        private boolean isUserSecure(int i) {
            LockPatternUtils lockPatternUtils = new LockPatternUtils(this.mActivity);
            if (lockPatternUtils.isSecure(i)) {
                return true;
            }
            UserInfo profileParent = this.mUserManager.getProfileParent(i);
            if (profileParent == null) {
                return false;
            }
            return lockPatternUtils.isSecure(profileParent.id);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void nextOrDismiss() {
            this.mCurrentCertIndex++;
            while (this.mCurrentCertIndex < this.mCertHolders.length && getCurrentCertInfo() == null) {
                this.mCurrentCertIndex++;
            }
            if (this.mCurrentCertIndex >= this.mCertHolders.length) {
                this.mDialog.dismiss();
                return;
            }
            updateViewContainer();
            updatePositiveButton();
            updateNegativeButton();
        }

        private void onClickEnableOrDisable() {
            final TrustedCredentialsSettings.CertHolder currentCertInfo = getCurrentCertInfo();
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.TrustedCredentialsDialogBuilder.DialogEventHandler.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    DialogEventHandler.this.mDelegate.removeOrInstallCert(currentCertInfo);
                    DialogEventHandler.this.nextOrDismiss();
                }
            };
            if (currentCertInfo.isSystemCert()) {
                onClickListener.onClick(null, -1);
            } else {
                new AlertDialog.Builder(this.mActivity).setMessage(R.string.trusted_credentials_remove_confirmation).setPositiveButton(17039379, onClickListener).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).show();
            }
        }

        private void onClickOk() {
            nextOrDismiss();
        }

        private void onClickTrust() {
            TrustedCredentialsSettings.CertHolder currentCertInfo = getCurrentCertInfo();
            if (this.mDelegate.startConfirmCredentialIfNotConfirmed(currentCertInfo.getUserId(), new IntConsumer() { // from class: com.android.settings.TrustedCredentialsDialogBuilder$DialogEventHandler$$ExternalSyntheticLambda0
                @Override // java.util.function.IntConsumer
                public final void accept(int i) {
                    TrustedCredentialsDialogBuilder.DialogEventHandler.this.onCredentialConfirmed(i);
                }
            })) {
                return;
            }
            this.mDpm.approveCaCert(currentCertInfo.getAlias(), currentCertInfo.getUserId(), true);
            nextOrDismiss();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onCredentialConfirmed(int i) {
            if (this.mDialog.isShowing() && this.mNeedsApproval && getCurrentCertInfo() != null && getCurrentCertInfo().getUserId() == i) {
                onClickTrust();
            }
        }

        private Button updateButton(int i, CharSequence charSequence) {
            this.mDialog.setButton(i, charSequence, null);
            Button button = this.mDialog.getButton(i);
            button.setText(charSequence);
            button.setOnClickListener(this);
            return button;
        }

        private void updateNegativeButton() {
            TrustedCredentialsSettings.CertHolder currentCertInfo = getCurrentCertInfo();
            boolean z = !this.mUserManager.hasUserRestriction("no_config_credentials", new UserHandle(currentCertInfo.getUserId()));
            Button updateButton = updateButton(-2, this.mActivity.getText(getButtonLabel(currentCertInfo)));
            this.mNegativeButton = updateButton;
            updateButton.setVisibility(z ? 0 : 8);
        }

        private void updatePositiveButton() {
            TrustedCredentialsSettings.CertHolder currentCertInfo = getCurrentCertInfo();
            boolean z = false;
            this.mNeedsApproval = (currentCertInfo.isSystemCert() || !isUserSecure(currentCertInfo.getUserId()) || this.mDpm.isCaCertApproved(currentCertInfo.getAlias(), currentCertInfo.getUserId())) ? false : true;
            try {
                z = RestrictedLockUtils.getProfileOrDeviceOwner(this.mActivity, UserHandle.of(currentCertInfo.getUserId())) != null;
            } catch (IllegalStateException unused) {
            }
            this.mPositiveButton = updateButton(-1, this.mActivity.getText((z || !this.mNeedsApproval) ? 17039370 : R.string.trusted_credentials_trust_label));
        }

        private void updateViewContainer() {
            LinearLayout certLayout = getCertLayout(getCurrentCertInfo());
            if (this.mCurrentCertLayout != null) {
                animateViewTransition(certLayout);
                return;
            }
            this.mCurrentCertLayout = certLayout;
            this.mRootContainer.addView(certLayout);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view != this.mPositiveButton) {
                if (view == this.mNegativeButton) {
                    onClickEnableOrDisable();
                }
            } else if (this.mNeedsApproval) {
                onClickTrust();
            } else {
                onClickOk();
            }
        }

        @Override // android.content.DialogInterface.OnShowListener
        public void onShow(DialogInterface dialogInterface) {
            nextOrDismiss();
        }

        public void setCertHolders(TrustedCredentialsSettings.CertHolder[] certHolderArr) {
            this.mCertHolders = certHolderArr;
        }

        public void setDialog(AlertDialog alertDialog) {
            this.mDialog = alertDialog;
        }
    }

    public TrustedCredentialsDialogBuilder(Activity activity, DelegateInterface delegateInterface) {
        super(activity);
        this.mDialogEventHandler = new DialogEventHandler(activity, delegateInterface);
        initDefaultBuilderParams();
    }

    private void initDefaultBuilderParams() {
        setTitle(17041521);
        setView(this.mDialogEventHandler.mRootContainer);
        setPositiveButton(R.string.trusted_credentials_trust_label, (DialogInterface.OnClickListener) null);
        setNegativeButton(17039370, (DialogInterface.OnClickListener) null);
    }

    @Override // miuix.appcompat.app.AlertDialog.Builder
    public AlertDialog create() {
        AlertDialog create = super.create();
        create.setOnShowListener(this.mDialogEventHandler);
        this.mDialogEventHandler.setDialog(create);
        return create;
    }

    public TrustedCredentialsDialogBuilder setCertHolder(TrustedCredentialsSettings.CertHolder certHolder) {
        return setCertHolders(certHolder == null ? new TrustedCredentialsSettings.CertHolder[0] : new TrustedCredentialsSettings.CertHolder[]{certHolder});
    }

    public TrustedCredentialsDialogBuilder setCertHolders(TrustedCredentialsSettings.CertHolder[] certHolderArr) {
        this.mDialogEventHandler.setCertHolders(certHolderArr);
        return this;
    }
}
