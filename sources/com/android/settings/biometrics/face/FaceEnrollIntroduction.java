package com.android.settings.biometrics.face;

import android.content.Intent;
import android.hardware.face.FaceManager;
import android.hardware.face.FaceSensorPropertiesInternal;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollIntroduction;
import com.android.settings.biometrics.BiometricUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.span.LinkSpan;
import java.util.Objects;

/* loaded from: classes.dex */
public class FaceEnrollIntroduction extends BiometricEnrollIntroduction {
    private FaceFeatureProvider mFaceFeatureProvider;
    private FaceManager mFaceManager;
    private FooterButton mPrimaryFooterButton;
    private FooterButton mSecondaryFooterButton;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(int i, int i2, long j) {
        this.mToken = BiometricUtils.requestGatekeeperHat(this, getIntent(), this.mUserId, j);
        this.mSensorId = i;
        this.mChallenge = j;
        this.mFooterBarMixin.getPrimaryButton().setEnabled(true);
    }

    private boolean maxFacesEnrolled() {
        FaceManager faceManager = this.mFaceManager;
        if (faceManager != null) {
            return this.mFaceManager.getEnrolledFaces(this.mUserId).size() >= ((FaceSensorPropertiesInternal) faceManager.getSensorPropertiesInternal().get(0)).maxEnrollmentsPerUser;
        }
        return false;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int checkMaxEnrolled() {
        if (this.mFaceManager != null) {
            if (maxFacesEnrolled()) {
                return R.string.face_intro_error_max;
            }
            return 0;
        }
        return R.string.face_intro_error_unknown;
    }

    protected boolean generateChallengeOnCreate() {
        return true;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getAgreeButtonTextRes() {
        return R.string.security_settings_fingerprint_enroll_introduction_agree;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected void getChallenge(final BiometricEnrollIntroduction.GenerateChallengeCallback generateChallengeCallback) {
        FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(this);
        this.mFaceManager = faceManagerOrNull;
        if (faceManagerOrNull == null) {
            generateChallengeCallback.onChallengeGenerated(0, 0, 0L);
            return;
        }
        int i = this.mUserId;
        Objects.requireNonNull(generateChallengeCallback);
        faceManagerOrNull.generateChallenge(i, new FaceManager.GenerateChallengeCallback() { // from class: com.android.settings.biometrics.face.FaceEnrollIntroduction$$ExternalSyntheticLambda0
            public final void onGenerateChallengeResult(int i2, int i3, long j) {
                BiometricEnrollIntroduction.GenerateChallengeCallback.this.onChallengeGenerated(i2, i3, j);
            }
        });
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getConfirmLockTitleResId() {
        return R.string.security_settings_face_preference_title;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getDescriptionResDisabledByAdmin() {
        return R.string.security_settings_face_enroll_introduction_message_unlock_disabled;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected Intent getEnrollingIntent() {
        Intent intent = new Intent(this, FaceEnrollEducation.class);
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), intent);
        return intent;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected TextView getErrorTextView() {
        return (TextView) findViewById(R.id.error_text);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected String getExtraKeyForBiometric() {
        return "for_face";
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getHeaderResDefault() {
        return R.string.security_settings_face_enroll_introduction_title;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getHeaderResDisabledByAdmin() {
        return R.string.security_settings_face_enroll_introduction_title_unlock_disabled;
    }

    protected int getHowMessage() {
        return R.string.security_settings_face_enroll_introduction_how_message;
    }

    protected int getInControlMessage() {
        return R.string.security_settings_face_enroll_introduction_control_message;
    }

    protected int getInControlTitle() {
        return R.string.security_settings_face_enroll_introduction_control_title;
    }

    protected int getInfoMessageGlasses() {
        return R.string.security_settings_face_enroll_introduction_info_glasses;
    }

    protected int getInfoMessageLooking() {
        return R.string.security_settings_face_enroll_introduction_info_looking;
    }

    protected int getInfoMessageRequireEyes() {
        return R.string.security_settings_face_enroll_introduction_info_gaze;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getLayoutResource() {
        return R.layout.face_enroll_introduction;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1506;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getModality() {
        return 8;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getMoreButtonTextRes() {
        return R.string.security_settings_face_enroll_introduction_more;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public FooterButton getNextButton() {
        FooterBarMixin footerBarMixin = this.mFooterBarMixin;
        if (footerBarMixin != null) {
            return footerBarMixin.getPrimaryButton();
        }
        return null;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected FooterButton getPrimaryFooterButton() {
        if (this.mPrimaryFooterButton == null) {
            this.mPrimaryFooterButton = new FooterButton.Builder(this).setText(R.string.security_settings_face_enroll_introduction_agree).setButtonType(6).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollIntroduction$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FaceEnrollIntroduction.this.onNextButtonClick(view);
                }
            }).setTheme(R.style.SudGlifButton_Primary).build();
        }
        return this.mPrimaryFooterButton;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected FooterButton getSecondaryFooterButton() {
        if (this.mSecondaryFooterButton == null) {
            this.mSecondaryFooterButton = new FooterButton.Builder(this).setText(R.string.security_settings_face_enroll_introduction_no_thanks).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollIntroduction$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FaceEnrollIntroduction.this.onSkipButtonClick(view);
                }
            }).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build();
        }
        return this.mSecondaryFooterButton;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected boolean isDisabledByAdmin() {
        return RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this, 128, this.mUserId) != null;
    }

    @Override // com.google.android.setupdesign.span.LinkSpan.OnClickListener
    public void onClick(LinkSpan linkSpan) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ImageView imageView = (ImageView) findViewById(R.id.icon_glasses);
        ImageView imageView2 = (ImageView) findViewById(R.id.icon_looking);
        imageView.getBackground().setColorFilter(getIconColorFilter());
        imageView2.getBackground().setColorFilter(getIconColorFilter());
        TextView textView = (TextView) findViewById(R.id.info_message_glasses);
        TextView textView2 = (TextView) findViewById(R.id.info_message_looking);
        TextView textView3 = (TextView) findViewById(R.id.how_message);
        TextView textView4 = (TextView) findViewById(R.id.title_in_control);
        TextView textView5 = (TextView) findViewById(R.id.message_in_control);
        textView.setText(getInfoMessageGlasses());
        textView2.setText(getInfoMessageLooking());
        textView4.setText(getInControlTitle());
        textView3.setText(getHowMessage());
        textView5.setText(getInControlMessage());
        if (getResources().getBoolean(R.bool.config_face_intro_show_require_eyes)) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.info_row_require_eyes);
            ImageView imageView3 = (ImageView) findViewById(R.id.icon_require_eyes);
            TextView textView6 = (TextView) findViewById(R.id.info_message_require_eyes);
            linearLayout.setVisibility(0);
            imageView3.getBackground().setColorFilter(getIconColorFilter());
            textView6.setText(getInfoMessageRequireEyes());
        }
        this.mFaceManager = Utils.getFaceManagerOrNull(this);
        this.mFaceFeatureProvider = FeatureFactory.getFactory(getApplicationContext()).getFaceFeatureProvider();
        if (this.mToken == null && BiometricUtils.containsGatekeeperPasswordHandle(getIntent()) && generateChallengeOnCreate()) {
            this.mFooterBarMixin.getPrimaryButton().setEnabled(false);
            this.mFaceManager.generateChallenge(this.mUserId, new FaceManager.GenerateChallengeCallback() { // from class: com.android.settings.biometrics.face.FaceEnrollIntroduction$$ExternalSyntheticLambda1
                public final void onGenerateChallengeResult(int i, int i2, long j) {
                    FaceEnrollIntroduction.this.lambda$onCreate$0(i, i2, j);
                }
            });
        }
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected void onEnrollmentSkipped(Intent intent) {
        if (BiometricUtils.tryStartingNextBiometricEnroll(this, 6)) {
            return;
        }
        super.onEnrollmentSkipped(intent);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected void onFinishedEnrolling(Intent intent) {
        if (BiometricUtils.tryStartingNextBiometricEnroll(this, 6)) {
            return;
        }
        super.onFinishedEnrolling(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public void onSkipButtonClick(View view) {
        if (BiometricUtils.tryStartingNextBiometricEnroll(this, 6)) {
            return;
        }
        super.onSkipButtonClick(view);
    }
}
