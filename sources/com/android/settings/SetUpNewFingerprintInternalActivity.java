package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.security.MiuiLockPatternUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.NewFingerprintInternalActivity;
import com.android.settings.ProvisionSetUpMiuiSecurityChooseUnlock;
import com.android.settings.SetUpMiuiSecurityChooseUnlock;
import com.android.settings.SetUpNewFingerprintInternalActivity;
import com.android.settings.utils.FingerprintUtils;
import com.android.settings.utils.MiuiGxzwUtils;
import java.util.List;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class SetUpNewFingerprintInternalActivity extends NewFingerprintInternalActivity {

    /* loaded from: classes.dex */
    public static class SetUpNewFingerprintFragment extends NewFingerprintInternalActivity.NewFingerprintFragment {
        private LinearLayout mBackImage;
        private TextView mNext;

        private boolean isDeviceProvisioned(Context context) {
            return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) == 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$launchConfirmOrChoose$0(Intent intent, int i, int i2, long j) {
            intent.putExtra("challenge", j);
            startActivityForResult(intent, 100);
        }

        @Override // com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment
        protected boolean isSetUp() {
            return true;
        }

        @Override // com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment
        protected void launchConfirmOrChoose() {
            if (new MiuiLockPatternUtils(this.mActivity).getActivePasswordQuality(UserHandle.myUserId()) != 0) {
                this.mActivity.setResult(-1);
                finish();
                return;
            }
            final Intent intent = new Intent(this.mActivity, isDeviceProvisioned(getActivity()) ? SetUpMiuiSecurityChooseUnlock.InternalActivity.class : ProvisionSetUpMiuiSecurityChooseUnlock.InternalActivity.class);
            intent.putExtra("has_challenge", true);
            intent.putExtra("add_keyguard_password_then_add_fingerprint", true);
            new FingerprintHelper(getActivity()).generateChallenge(UserHandle.myUserId(), new FingerprintManager.GenerateChallengeCallback() { // from class: com.android.settings.SetUpNewFingerprintInternalActivity$SetUpNewFingerprintFragment$$ExternalSyntheticLambda0
                public final void onChallengeGenerated(int i, int i2, long j) {
                    SetUpNewFingerprintInternalActivity.SetUpNewFingerprintFragment.this.lambda$launchConfirmOrChoose$0(intent, i, i2, j);
                }
            });
        }

        @Override // com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment, androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            if (i == 100) {
                if (i2 == -1) {
                    this.mActivity.setResult(-1);
                } else if (i2 == 11) {
                    this.mActivity.setResult(11);
                }
            }
            super.onActivityResult(i, i2, intent);
        }

        @Override // com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment, com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
        }

        @Override // com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment
        protected void onFingerprintAddCompleted() {
            this.mBackImage.setVisibility(4);
            this.mNext.setVisibility(0);
            List<String> fingerprintIds = this.mFingerprintHelper.getFingerprintIds();
            int i = R.string.add_fingerprint_success_msg;
            getString(i);
            Activity activity = this.mActivity;
            String string = activity.getString(R.string.fingerprint_gxzw_add_fingerprint_finish, new Object[]{FingerprintUtils.generateFingerprintName(activity, fingerprintIds)});
            this.mInstructionTitle.setText(i);
            this.mInstructionText.setText(string);
        }

        @Override // com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment, com.android.settings.BaseEditFragment, com.android.settings.BaseFragment, androidx.fragment.app.Fragment
        public void onStart() {
            super.onStart();
            if (getActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) getActivity()).getAppCompatActionBar().hide();
            }
        }

        @Override // com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment
        protected void setupViews() {
            this.mInstructionTitle = (TextView) this.mContentView.findViewById(R.id.setup_new_fingerprint_top_title);
            this.mInstructionText = (TextView) this.mContentView.findViewById(R.id.setup_new_fingerprint_top_text);
            this.mInstructionImageView = (ImageView) this.mContentView.findViewById(R.id.setup_new_fingerprint_instruction_img);
            this.mStepVideoView = (MutedVideoView) this.mContentView.findViewById(R.id.setup_new_fingerprint_step_video);
            LinearLayout linearLayout = (LinearLayout) this.mContentView.findViewById(R.id.new_fingerprint_cancel);
            this.mBackImage = linearLayout;
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
            marginLayoutParams.topMargin = MiuiKeyguardSettingsUtils.getStatusBarHeight(this.mActivity);
            this.mBackImage.setLayoutParams(marginLayoutParams);
            TextView textView = (TextView) this.mContentView.findViewById(R.id.new_fingerprint_ok);
            this.mNext = textView;
            textView.setVisibility(4);
            this.mBackImage.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SetUpNewFingerprintInternalActivity.SetUpNewFingerprintFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SetUpNewFingerprintFragment.this.mActivity.setResult(-1);
                    SetUpNewFingerprintFragment setUpNewFingerprintFragment = SetUpNewFingerprintFragment.this;
                    if (setUpNewFingerprintFragment.mOnInputFailedAlertDialog == null) {
                        setUpNewFingerprintFragment.mOnInputFailedAlertDialog = setUpNewFingerprintFragment.buildAlertDialog(R.string.add_fingerprint_toast_text);
                    }
                    AlertDialog alertDialog = SetUpNewFingerprintFragment.this.mOnInputFailedAlertDialog;
                    if (alertDialog != null) {
                        alertDialog.show();
                    }
                    SetUpNewFingerprintFragment.this.finish();
                }
            });
            this.mNext.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.SetUpNewFingerprintInternalActivity.SetUpNewFingerprintFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SetUpNewFingerprintFragment.this.mActivity.setResult(-1);
                    SetUpNewFingerprintFragment.this.finish();
                }
            });
        }
    }

    @Override // com.android.settings.NewFingerprintInternalActivity, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        String name;
        Intent intent = new Intent(super.getIntent());
        if (MiuiGxzwUtils.isGxzwSensor()) {
            name = GxzwNewFingerprintFragment.class.getName();
            intent.putExtra("setup", true);
        } else {
            name = SetUpNewFingerprintFragment.class.getName();
        }
        intent.putExtra(":settings:show_fragment", name);
        intent.putExtra(":settings:show_fragment_title", R.string.add_fingerprint_text);
        return intent;
    }
}
