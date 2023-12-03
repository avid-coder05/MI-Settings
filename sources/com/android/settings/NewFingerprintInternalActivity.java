package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import com.android.settings.ConfirmLockPassword;
import com.android.settings.ConfirmLockPattern;
import com.android.settings.MiuiSecurityChooseUnlock;
import com.android.settings.utils.FingerprintUtils;
import com.android.settings.utils.MiuiGxzwUtils;
import com.android.settings.utils.TabletUtils;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.provider.Weather;
import miui.util.FeatureParser;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class NewFingerprintInternalActivity extends SettingsCompatActivity {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum FingerprintSensorType {
        FRONT,
        BACK,
        BROADSIDE
    }

    /* loaded from: classes.dex */
    public static class NewFingerprintFragment extends BaseEditFragment implements FragmentResultCallBack {
        private boolean mAddKeyguardpasswordThenAddFingerprint;
        private View mCancelButton;
        protected View mContentView;
        private String mEnrollHelpInfoText;
        private FingerprintSensorType mFingerprintSensorType;
        private String mIdOfFingerprintWithoutName;
        private ImageView mImageView;
        private EditText mInstructionFingernameEditText;
        private TextView mInstructionFingernameTitle;
        protected ImageView mInstructionImageView;
        private TextView mInstructionSuccess;
        protected TextView mInstructionText;
        protected TextView mInstructionTitle;
        private boolean mIsBlackMode;
        private Button mOkButton;
        protected MutedVideoView mStepVideoView;
        private int nextInputStep;
        protected FingerprintHelper mFingerprintHelper = null;
        private int mInputStep = 1;
        private int mProgress = 0;
        private int mTotalStepNum = 0;
        private boolean mIsStartFingerprint = false;
        private Vibrator mVibrator = null;
        protected AlertDialog mOnInputFailedAlertDialog = null;
        private Handler mHandler = new Handler();
        private boolean mStartEnrolling = false;
        private int mFailTime = 0;
        private Uri mNextVideoUri = null;
        private boolean mShowEdgeFinger = false;
        private boolean mIsShowHelpInfo = false;
        protected Activity mActivity = null;
        private boolean mConfirmLockLaunched = false;
        private Runnable mContentDescriptionRunnable = new Runnable() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.1
            @Override // java.lang.Runnable
            public void run() {
                if (NewFingerprintFragment.this.getActivity() == null || NewFingerprintFragment.this.getActivity().isFinishing()) {
                    return;
                }
                NewFingerprintFragment newFingerprintFragment = NewFingerprintFragment.this;
                TextView textView = newFingerprintFragment.mInstructionTitle;
                int i = R.string.add_fingerprint_instruction_title;
                textView.setContentDescription(newFingerprintFragment.getString(i));
                NewFingerprintFragment.this.mInstructionTitle.sendAccessibilityEvent(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON);
                NewFingerprintFragment newFingerprintFragment2 = NewFingerprintFragment.this;
                newFingerprintFragment2.mInstructionTitle.announceForAccessibility(newFingerprintFragment2.getString(i));
            }
        };
        private FingerprintAddListener mFingerprintAddistener = new FingerprintAddListener() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.10
            private Runnable mEnrollHelpInfoRunnable = new Runnable() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.10.1
                @Override // java.lang.Runnable
                public void run() {
                    NewFingerprintFragment.this.mIsShowHelpInfo = true;
                    NewFingerprintFragment newFingerprintFragment = NewFingerprintFragment.this;
                    newFingerprintFragment.mInstructionTitle.setText(newFingerprintFragment.mEnrollHelpInfoText);
                    NewFingerprintFragment newFingerprintFragment2 = NewFingerprintFragment.this;
                    newFingerprintFragment2.setContentDescription(newFingerprintFragment2.mEnrollHelpInfoText);
                }
            };

            private void handleSimilarFingerprintInputed() {
                onFingerprintImageProcessed();
                if (NewFingerprintFragment.this.mInputStep <= 12) {
                    NewFingerprintFragment newFingerprintFragment = NewFingerprintFragment.this;
                    Locale locale = Locale.ENGLISH;
                    newFingerprintFragment.playVideo(newFingerprintFragment.generateViewUri(String.format(locale, "core_scan_output_%02d", Integer.valueOf(newFingerprintFragment.mInputStep))), NewFingerprintFragment.this.mStepVideoView);
                    NewFingerprintFragment newFingerprintFragment2 = NewFingerprintFragment.this;
                    newFingerprintFragment2.mNextVideoUri = newFingerprintFragment2.generateViewUri(String.format(locale, "core_scan_output_%02d_error", Integer.valueOf(newFingerprintFragment2.mInputStep)));
                } else {
                    NewFingerprintFragment newFingerprintFragment3 = NewFingerprintFragment.this;
                    Locale locale2 = Locale.ENGLISH;
                    newFingerprintFragment3.playVideo(newFingerprintFragment3.generateViewUri(String.format(locale2, "edge_scan_output_%02d", Integer.valueOf(newFingerprintFragment3.mInputStep - 12))), NewFingerprintFragment.this.mStepVideoView);
                    NewFingerprintFragment newFingerprintFragment4 = NewFingerprintFragment.this;
                    newFingerprintFragment4.mNextVideoUri = newFingerprintFragment4.generateViewUri(String.format(locale2, "edge_scan_output_%02d_error", Integer.valueOf(newFingerprintFragment4.mInputStep - 12)));
                }
                if (NewFingerprintFragment.this.mIsShowHelpInfo) {
                    return;
                }
                TextView textView = NewFingerprintFragment.this.mInstructionTitle;
                int i = R.string.add_fingerprint_similar_fingerprint_input_error_msg;
                textView.setText(i);
                NewFingerprintFragment newFingerprintFragment5 = NewFingerprintFragment.this;
                newFingerprintFragment5.setContentDescription(newFingerprintFragment5.getString(i));
            }

            private void onFingerprintImageProcessed() {
                if (!NewFingerprintFragment.this.mIsStartFingerprint || NewFingerprintFragment.this.mShowEdgeFinger) {
                    NewFingerprintFragment.this.mInstructionImageView.setVisibility(8);
                    NewFingerprintFragment.this.mStepVideoView.setVisibility(0);
                    NewFingerprintFragment.this.mInstructionTitle.setText(R.string.add_fingerprint_put_finger_title);
                    NewFingerprintFragment newFingerprintFragment = NewFingerprintFragment.this;
                    newFingerprintFragment.mInstructionText.setText(newFingerprintFragment.mShowEdgeFinger ? R.string.add_fingerprint_put_finger_edge_msg : R.string.add_fingerprint_put_finger_msg);
                    NewFingerprintFragment.this.mShowEdgeFinger = false;
                }
            }

            @Override // com.android.settings.FingerprintAddListener
            public void addFingerprintCompleted() {
                if (NewFingerprintFragment.this.mStartEnrolling) {
                    NewFingerprintFragment.this.mShowEdgeFinger = false;
                    NewFingerprintFragment.this.mInstructionImageView.setVisibility(8);
                    NewFingerprintFragment.this.mStepVideoView.setVisibility(0);
                    if (NewFingerprintFragment.this.mStepVideoView.isPlaying()) {
                        NewFingerprintFragment newFingerprintFragment = NewFingerprintFragment.this;
                        newFingerprintFragment.mNextVideoUri = newFingerprintFragment.generateViewUri("scan_finish_output");
                    } else {
                        NewFingerprintFragment newFingerprintFragment2 = NewFingerprintFragment.this;
                        newFingerprintFragment2.playVideo(newFingerprintFragment2.generateViewUri("scan_finish_output"), NewFingerprintFragment.this.mStepVideoView);
                    }
                    NewFingerprintFragment.this.onFingerprintAddCompleted();
                    NewFingerprintFragment.this.mShowEdgeFinger = false;
                    NewFingerprintFragment.this.mIsStartFingerprint = false;
                    NewFingerprintFragment.this.mStartEnrolling = false;
                }
            }

            @Override // com.android.settings.FingerprintAddListener
            public void addFingerprintFailed() {
                Activity activity = NewFingerprintFragment.this.mActivity;
                if (activity != null) {
                    Toast.makeText(activity.getApplicationContext(), R.string.add_fingerprint_failed_retry_text, 0).show();
                }
                NewFingerprintFragment.this.finish();
                NewFingerprintFragment.this.mIsStartFingerprint = false;
            }

            @Override // com.android.settings.FingerprintAddListener
            public void addFingerprintProgress(int i) {
                Log.d(NewFingerprintInternalActivity.class.getSimpleName(), i + " " + NewFingerprintFragment.this.mProgress);
                if (NewFingerprintFragment.this.mStartEnrolling) {
                    NewFingerprintFragment.this.mHandler.removeCallbacks(this.mEnrollHelpInfoRunnable);
                    onFingerprintImageProcessed();
                    if (!NewFingerprintFragment.this.mIsStartFingerprint) {
                        NewFingerprintFragment.this.mIsStartFingerprint = true;
                        NewFingerprintFragment.this.mTotalStepNum = i + 1;
                    }
                    if (i == NewFingerprintFragment.this.mProgress) {
                        handleSimilarFingerprintInputed();
                        return;
                    }
                    NewFingerprintFragment.this.mIsShowHelpInfo = false;
                    TextView textView = NewFingerprintFragment.this.mInstructionTitle;
                    int i2 = R.string.add_fingerprint_put_finger_title;
                    textView.setText(i2);
                    NewFingerprintFragment newFingerprintFragment = NewFingerprintFragment.this;
                    newFingerprintFragment.setContentDescription(newFingerprintFragment.getString(i2));
                    NewFingerprintFragment.this.mInputStep = (int) Math.ceil(((r1.mTotalStepNum - i) * 20) / NewFingerprintFragment.this.mTotalStepNum);
                    NewFingerprintFragment newFingerprintFragment2 = NewFingerprintFragment.this;
                    newFingerprintFragment2.mInputStep = newFingerprintFragment2.mInputStep <= 0 ? 1 : NewFingerprintFragment.this.mInputStep;
                    Log.d(NewFingerprintInternalActivity.class.getSimpleName(), NewFingerprintFragment.this.mInputStep + " " + NewFingerprintFragment.this.mTotalStepNum);
                    NewFingerprintFragment newFingerprintFragment3 = NewFingerprintFragment.this;
                    newFingerprintFragment3.nextInputStep = (int) Math.ceil((double) ((((newFingerprintFragment3.mTotalStepNum - i) + 1) * 20) / NewFingerprintFragment.this.mTotalStepNum));
                    NewFingerprintFragment newFingerprintFragment4 = NewFingerprintFragment.this;
                    newFingerprintFragment4.nextInputStep = newFingerprintFragment4.nextInputStep <= 0 ? 1 : NewFingerprintFragment.this.nextInputStep;
                    if (NewFingerprintFragment.this.mInputStep <= 12) {
                        if (NewFingerprintFragment.this.nextInputStep > 12) {
                            NewFingerprintFragment.this.mShowEdgeFinger = true;
                        }
                        NewFingerprintFragment newFingerprintFragment5 = NewFingerprintFragment.this;
                        newFingerprintFragment5.playVideo(newFingerprintFragment5.generateViewUri(String.format(Locale.ENGLISH, "core_scan_output_%02d", Integer.valueOf(newFingerprintFragment5.mInputStep))), NewFingerprintFragment.this.mStepVideoView);
                    } else {
                        NewFingerprintFragment newFingerprintFragment6 = NewFingerprintFragment.this;
                        newFingerprintFragment6.playVideo(newFingerprintFragment6.generateViewUri(String.format(Locale.ENGLISH, "edge_scan_output_%02d", Integer.valueOf(newFingerprintFragment6.mInputStep - 12))), NewFingerprintFragment.this.mStepVideoView);
                    }
                    NewFingerprintFragment.this.mProgress = i;
                    NewFingerprintFragment newFingerprintFragment7 = NewFingerprintFragment.this;
                    newFingerprintFragment7.mInputStep = newFingerprintFragment7.nextInputStep;
                    NewFingerprintFragment.this.mFailTime = 0;
                    NewFingerprintFragment newFingerprintFragment8 = NewFingerprintFragment.this;
                    newFingerprintFragment8.dismissAlertDialog(newFingerprintFragment8.mOnInputFailedAlertDialog);
                    NewFingerprintFragment newFingerprintFragment9 = NewFingerprintFragment.this;
                    newFingerprintFragment9.mOnInputFailedAlertDialog = null;
                    if (newFingerprintFragment9.mInputStep <= 12) {
                        NewFingerprintFragment.this.mInstructionText.setText(R.string.add_fingerprint_put_finger_msg);
                    } else {
                        NewFingerprintFragment.this.mInstructionText.setText(R.string.add_fingerprint_put_finger_edge_msg);
                    }
                }
            }

            @Override // com.android.settings.FingerprintAddListener
            public void onEnrollmentHelp(int i, CharSequence charSequence) {
                if (TextUtils.isEmpty(charSequence)) {
                    return;
                }
                NewFingerprintFragment.this.mEnrollHelpInfoText = charSequence.toString();
                Log.d(NewFingerprintInternalActivity.class.getSimpleName(), "helpMsgId=" + i + "; helpString=" + ((Object) charSequence));
                NewFingerprintFragment.this.mHandler.postDelayed(this.mEnrollHelpInfoRunnable, 100L);
            }
        };
        private final Runnable mActionOnAddCompletedRunnable = new Runnable() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.11
            @Override // java.lang.Runnable
            public void run() {
                NewFingerprintFragment.this.mImageView.setVisibility(0);
                NewFingerprintFragment.this.mStepVideoView.setVisibility(4);
                NewFingerprintFragment.this.mInstructionFingernameTitle.setVisibility(0);
                List<String> fingerprintIds = NewFingerprintFragment.this.mFingerprintHelper.getFingerprintIds();
                NewFingerprintFragment newFingerprintFragment = NewFingerprintFragment.this;
                newFingerprintFragment.mIdOfFingerprintWithoutName = FingerprintUtils.getIdOfFingerprintWithoutName(newFingerprintFragment.mActivity, fingerprintIds);
                String generateFingerprintName = FingerprintUtils.generateFingerprintName(NewFingerprintFragment.this.mActivity, fingerprintIds);
                NewFingerprintFragment.this.mInstructionFingernameEditText.setText(generateFingerprintName);
                NewFingerprintFragment.this.mInstructionFingernameEditText.setVisibility(0);
                if (generateFingerprintName != null) {
                    NewFingerprintFragment.this.mInstructionFingernameEditText.setSelection(0, generateFingerprintName.length());
                }
                NewFingerprintFragment.this.mInstructionSuccess.setVisibility(0);
                NewFingerprintFragment.this.mInstructionTitle.setVisibility(8);
                NewFingerprintFragment.this.mInstructionText.setVisibility(8);
                NewFingerprintFragment.this.mOkButton.setVisibility(0);
                NewFingerprintFragment newFingerprintFragment2 = NewFingerprintFragment.this;
                newFingerprintFragment2.setContentDescription(newFingerprintFragment2.getString(R.string.add_fingerprint_success_msg));
                if (NewFingerprintFragment.this.mCancelButton != null) {
                    NewFingerprintFragment.this.mCancelButton.setVisibility(4);
                }
                NewFingerprintFragment.this.releaseFingerprintHelper();
                if (fingerprintIds.size() == 1) {
                    Settings.Secure.putInt(NewFingerprintFragment.this.mActivity.getContentResolver(), "miui_keyguard", 2);
                }
                NewFingerprintFragment.this.getActivity().setResult(-1);
                if (MiuiKeyguardSettingsUtils.isShowDialogToAddFace(NewFingerprintFragment.this.mActivity)) {
                    NewFingerprintFragment newFingerprintFragment3 = NewFingerprintFragment.this;
                    if (newFingerprintFragment3.isDeviceProvisioned(newFingerprintFragment3.mActivity)) {
                        MiuiKeyguardSettingsUtils.showDialogToAddFace(NewFingerprintFragment.this.getActivity(), null, 0, true);
                    }
                }
            }
        };

        private void checkIfShowUserNotice(final byte[] bArr) {
            if (Build.IS_INTERNATIONAL_BUILD) {
                this.mFingerprintHelper.startEnrol(this.mFingerprintAddistener, bArr);
            } else {
                new AsyncTask<Void, Void, Boolean>() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.2
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Boolean doInBackground(Void... voidArr) {
                        Activity activity = NewFingerprintFragment.this.mActivity;
                        return activity == null ? Boolean.FALSE : Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getBoolean("need_show_user_notice", true));
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public void onPostExecute(Boolean bool) {
                        super.onPostExecute((AnonymousClass2) bool);
                        if (bool.booleanValue()) {
                            NewFingerprintFragment.this.showGxzwUserNotice(bArr);
                            return;
                        }
                        NewFingerprintFragment newFingerprintFragment = NewFingerprintFragment.this;
                        newFingerprintFragment.mFingerprintHelper.startEnrol(newFingerprintFragment.mFingerprintAddistener, bArr);
                    }
                }.execute(new Void[0]);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dismissAlertDialog(AlertDialog alertDialog) {
            if (alertDialog == null || !alertDialog.isShowing()) {
                return;
            }
            alertDialog.dismiss();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Uri generateViewUri(String str) {
            if (this.mIsBlackMode) {
                str = str + "_dark";
            }
            String packageName = this.mActivity.getPackageName();
            int identifier = this.mActivity.getResources().getIdentifier(str, Weather.RawInfo.PARAM, packageName);
            if (identifier == 0) {
                return null;
            }
            return Uri.parse("android.resource://" + packageName + "/" + identifier);
        }

        private int getCoreScanGestureImage() {
            FingerprintSensorType fingerprintSensorType = this.mFingerprintSensorType;
            return fingerprintSensorType == FingerprintSensorType.BROADSIDE ? R.drawable.core_scan_gesture_broadside : fingerprintSensorType == FingerprintSensorType.FRONT ? R.drawable.core_scan_gesture_font : R.drawable.core_scan_gesture_back;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getEdgeScanGestureImage() {
            FingerprintSensorType fingerprintSensorType = this.mFingerprintSensorType;
            return fingerprintSensorType == FingerprintSensorType.BROADSIDE ? R.drawable.core_scan_gesture_broadside : fingerprintSensorType == FingerprintSensorType.FRONT ? R.drawable.edge_scan_gesture_font : R.drawable.core_scan_gesture_back;
        }

        private int getFingerprintInstructionString() {
            FingerprintSensorType fingerprintSensorType = this.mFingerprintSensorType;
            return fingerprintSensorType == FingerprintSensorType.BROADSIDE ? R.string.add_broadsize_fingerprint_instruction_msg : fingerprintSensorType == FingerprintSensorType.FRONT ? R.string.add_front_fingerprint_instruction_msg : R.string.add_back_fingerprint_instruction_msg;
        }

        private void initFingerprintSensorType() {
            boolean isBroadSideFingerprint = FingerprintUtils.isBroadSideFingerprint();
            boolean z = FeatureParser.getBoolean("front_fingerprint_sensor", false);
            if (isBroadSideFingerprint) {
                this.mFingerprintSensorType = FingerprintSensorType.BROADSIDE;
            } else if (z) {
                this.mFingerprintSensorType = FingerprintSensorType.FRONT;
            } else {
                this.mFingerprintSensorType = FingerprintSensorType.BACK;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isDeviceProvisioned(Context context) {
            return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) == 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isNewFingerprintInternalActivity() {
            return getActivity() != null && (getActivity() instanceof NewFingerprintInternalActivity);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void playVideo(Uri uri, final MutedVideoView mutedVideoView) {
            if (uri != null) {
                this.mNextVideoUri = null;
                mutedVideoView.setVideoURI(uri);
                mutedVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.8
                    @Override // android.media.MediaPlayer.OnPreparedListener
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mutedVideoView.start();
                    }
                });
            }
        }

        private void popNewFingerprintBackStack() {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                fragmentManager.popBackStack(NewFingerprintFragment.class.getName(), 1);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void releaseFingerprintHelper() {
            FingerprintHelper fingerprintHelper = this.mFingerprintHelper;
            if (fingerprintHelper != null) {
                fingerprintHelper.cancelEnrol();
                this.mFingerprintHelper = null;
            }
        }

        private void saveFingerprintname() {
            String obj = this.mInstructionFingernameEditText.getText().toString();
            if (TextUtils.isEmpty(obj)) {
                return;
            }
            FingerprintUtils.setFingerprintName(this.mActivity, this.mIdOfFingerprintWithoutName, obj);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setContentDescription(final String str) {
            this.mInstructionTitle.postDelayed(new Runnable() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.9
                @Override // java.lang.Runnable
                public void run() {
                    NewFingerprintFragment.this.mInstructionTitle.setContentDescription(str);
                    NewFingerprintFragment.this.mInstructionTitle.announceForAccessibility(str);
                }
            }, 100L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showGxzwUserNotice(final byte[] bArr) {
            Activity activity = this.mActivity;
            if (activity == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialog_Theme_DayNight);
            builder.setCancelable(false).setTitle(R.string.finger_add_user_info_dialog_title).setMessage(R.string.finger_add_user_info_dialog_message).setPositiveButton(R.string.finger_add_user_info_dialog_next, new DialogInterface.OnClickListener() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(NewFingerprintFragment.this.mActivity.getApplicationContext()).edit();
                    edit.putBoolean("need_show_user_notice", false);
                    edit.commit();
                    NewFingerprintFragment newFingerprintFragment = NewFingerprintFragment.this;
                    newFingerprintFragment.mFingerprintHelper.startEnrol(newFingerprintFragment.mFingerprintAddistener, bArr);
                    dialogInterface.dismiss();
                }
            }).setNegativeButton(R.string.finger_add_user_info_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    NewFingerprintFragment.this.finish();
                }
            });
            builder.create().show();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public AlertDialog buildAlertDialog(int i) {
            Activity activity = this.mActivity;
            if (activity == null) {
                return null;
            }
            return new AlertDialog.Builder(activity).setCancelable(false).setIconAttribute(16843605).setMessage(i).setPositiveButton(R.string.information_dialog_button_text, (DialogInterface.OnClickListener) null).create();
        }

        @Override // com.android.settings.BaseFragment
        public void finish() {
            if (isNewFingerprintInternalActivity()) {
                super.finish();
            } else {
                popNewFingerprintBackStack();
            }
        }

        protected boolean isSetUp() {
            return false;
        }

        protected void launchConfirmOrChoose() {
            Intent intent;
            if (this.mConfirmLockLaunched) {
                return;
            }
            int activePasswordQuality = new MiuiLockPatternUtils(this.mActivity).getActivePasswordQuality(UserHandle.myUserId());
            Class cls = MiuiSecurityChooseUnlock.InternalActivity.class;
            String extraFragmentName = MiuiSecurityChooseUnlock.InternalActivity.getExtraFragmentName();
            if (activePasswordQuality != 0) {
                if (activePasswordQuality == 65536) {
                    cls = ConfirmLockPattern.InternalActivity.class;
                    extraFragmentName = ConfirmLockPattern.InternalActivity.getExtraFragmentName();
                } else {
                    cls = ConfirmLockPassword.InternalActivity.class;
                    extraFragmentName = ConfirmLockPassword.InternalActivity.getExtraFragmentName();
                }
            }
            if (UserHandle.myUserId() == 0) {
                intent = new Intent(this.mActivity, cls);
            } else {
                Intent intent2 = getIntent();
                Intent intent3 = new Intent(this.mActivity, cls);
                intent3.putExtra("com.android.settings.ConfirmLockPattern.header", intent2.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.header"));
                intent3.putExtra("com.android.settings.titleColor", intent2.getIntExtra("com.android.settings.titleColor", this.mActivity.getResources().getColor(17170443)));
                intent3.putExtra("com.android.settings.bgColor", intent2.getIntExtra("com.android.settings.bgColor", this.mActivity.getResources().getColor(R.color.set_second_space_background)));
                intent3.putExtra("com.android.settings.lockBtnWhite", true);
                intent3.putExtra("com.android.settings.forgetPatternColor", intent2.getIntExtra("com.android.settings.forgetPatternColor", this.mActivity.getResources().getColor(17170443)));
                intent3.putExtra("com.android.settings.footerTextColor", intent2.getIntExtra("com.android.settings.footerTextColor", this.mActivity.getResources().getColor(17170443)));
                intent3.putExtra("com.android.settings.forgetPassword", false);
                intent = intent3;
            }
            intent.putExtra(":settings:show_fragment", extraFragmentName);
            intent.putExtra("has_challenge", true);
            intent.putExtra("show_add_fingerprint_hint", true);
            int i = R.string.empty_title;
            intent.putExtra(":android:show_fragment_title", i);
            if (TabletUtils.IS_TABLET) {
                MiuiKeyguardSettingsUtils.startFragment(this, extraFragmentName, 100, intent.getExtras(), i);
            } else {
                startActivityForResult(intent, 100);
            }
            this.mConfirmLockLaunched = true;
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 100) {
                if (i2 != -1) {
                    finish();
                    return;
                }
                this.mStartEnrolling = true;
                checkIfShowUserNotice(intent.getByteArrayExtra("hw_auth_token"));
                this.mVibrator = (Vibrator) this.mActivity.getSystemService("vibrator");
            }
        }

        @Override // androidx.fragment.app.Fragment
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if (this.mActivity == null) {
                this.mActivity = activity;
            }
        }

        @Override // androidx.fragment.app.Fragment
        public void onAttach(Context context) {
            super.onAttach(context);
            if ((context instanceof Activity) && this.mActivity == null) {
                this.mActivity = (Activity) context;
            }
        }

        @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mFingerprintHelper = new FingerprintHelper(this.mActivity);
            initFingerprintSensorType();
            this.mIsBlackMode = getResources().getBoolean(R.bool.is_black_theme) || MiuiKeyguardSettingsUtils.isNightMode(getActivity());
            Intent intent = getIntent();
            if (intent != null) {
                this.mAddKeyguardpasswordThenAddFingerprint = intent.getBooleanExtra("add_keyguard_password_then_add_fingerprint", false);
            } else {
                this.mAddKeyguardpasswordThenAddFingerprint = false;
            }
            if (bundle != null) {
                this.mConfirmLockLaunched = bundle.getBoolean("key_confirm_lock_launched");
            }
            if (this.mAddKeyguardpasswordThenAddFingerprint) {
                this.mStartEnrolling = true;
                byte[] byteArrayExtra = intent.getByteArrayExtra("hw_auth_token");
                this.mVibrator = (Vibrator) this.mActivity.getSystemService("vibrator");
                checkIfShowUserNotice(byteArrayExtra);
            } else {
                launchConfirmOrChoose();
            }
            this.mActivity.getWindow().addFlags(128);
            if (MiuiKeyguardSettingsUtils.isInFullWindowGestureMode(getActivity().getApplicationContext())) {
                this.mActivity.getWindow().clearFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
            }
        }

        @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
        public void onDetach() {
            super.onDetach();
            this.mActivity = null;
        }

        protected void onFingerprintAddCompleted() {
            this.mActivity.runOnUiThread(this.mActionOnAddCompletedRunnable);
        }

        @Override // com.android.settings.FragmentResultCallBack
        public void onFragmentResult(int i, Bundle bundle) {
            if (i == 100 && bundle != null && bundle.getInt("miui_security_fragment_result") == 0) {
                this.mStartEnrolling = true;
                this.mFingerprintHelper.startEnrol(this.mFingerprintAddistener, bundle.getByteArray("hw_auth_token"));
                this.mVibrator = (Vibrator) this.mActivity.getSystemService("vibrator");
            }
        }

        @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
        public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            super.onInflateView(layoutInflater, viewGroup, bundle);
            if (!isSetUp()) {
                this.mContentView = layoutInflater.inflate(R.layout.new_fingerprint, viewGroup, false);
            } else if (isDeviceProvisioned(getActivity())) {
                this.mContentView = layoutInflater.inflate(R.layout.setup_new_fingerprint, viewGroup, false);
            } else {
                this.mContentView = layoutInflater.inflate(R.layout.setup_new_fingerprint, viewGroup, false);
            }
            setupViews();
            this.mInstructionFingernameTitle = (TextView) this.mContentView.findViewById(R.id.fingerprint_name_suggest_title);
            this.mInstructionFingernameEditText = (EditText) this.mContentView.findViewById(R.id.fingerprint_name_edit_text);
            this.mInstructionSuccess = (TextView) this.mContentView.findViewById(R.id.new_fingerprint_success_text);
            this.mImageView = (ImageView) this.mContentView.findViewById(R.id.new_fingerprint_image);
            this.mInstructionImageView.setBackgroundResource(getCoreScanGestureImage());
            this.mInstructionTitle.setText(R.string.add_fingerprint_instruction_title);
            this.mInstructionText.setText(getFingerprintInstructionString());
            Button button = (Button) this.mContentView.findViewById(R.id.new_fingerprint_button);
            this.mOkButton = button;
            FingerprintUtils.createCardFolmeTouchStyle(button);
            MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.5
                @Override // android.media.MediaPlayer.OnErrorListener
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    Log.e("NewFingerprintInternalActivity", "error in playing video " + i + " " + i2);
                    return true;
                }
            };
            this.mStepVideoView.setZOrderOnTop(true);
            this.mStepVideoView.getHolder().setFormat(-3);
            this.mStepVideoView.setOnErrorListener(onErrorListener);
            this.mStepVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.6
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (!NewFingerprintFragment.this.mShowEdgeFinger) {
                        if (NewFingerprintFragment.this.mNextVideoUri != null) {
                            NewFingerprintFragment newFingerprintFragment = NewFingerprintFragment.this;
                            newFingerprintFragment.playVideo(newFingerprintFragment.mNextVideoUri, NewFingerprintFragment.this.mStepVideoView);
                            return;
                        }
                        return;
                    }
                    NewFingerprintFragment.this.mStepVideoView.setVisibility(8);
                    NewFingerprintFragment.this.mInstructionImageView.setVisibility(0);
                    NewFingerprintFragment newFingerprintFragment2 = NewFingerprintFragment.this;
                    newFingerprintFragment2.mInstructionImageView.setBackgroundResource(newFingerprintFragment2.getEdgeScanGestureImage());
                    NewFingerprintFragment.this.mInstructionText.setText(R.string.add_fingerprint_edge_instruction_msg);
                    TextView textView = NewFingerprintFragment.this.mInstructionTitle;
                    int i = R.string.add_fingerprint_edge_instruction_title;
                    textView.setText(i);
                    NewFingerprintFragment newFingerprintFragment3 = NewFingerprintFragment.this;
                    newFingerprintFragment3.setContentDescription(newFingerprintFragment3.getString(i));
                }
            });
            this.mImageView.setVisibility(8);
            Uri generateViewUri = generateViewUri(String.format(Locale.ENGLISH, "core_scan_output_%02d", 1));
            if (generateViewUri != null) {
                this.mStepVideoView.setVideoURI(generateViewUri);
            }
            this.mOkButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.NewFingerprintInternalActivity.NewFingerprintFragment.7
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if ((Settings.Global.getInt(NewFingerprintFragment.this.mActivity.getContentResolver(), "device_provisioned", 0) != 0) && UserHandle.myUserId() == 0) {
                        Intent intent = new Intent(NewFingerprintFragment.this.mActivity, FingerprintManageSetting.class);
                        if (!NewFingerprintFragment.this.isNewFingerprintInternalActivity()) {
                            MiuiKeyguardSettingsUtils.startFragment(NewFingerprintFragment.this, FingerprintManageSetting.getExtraFragmentName(), 0, intent.getExtras(), R.string.empty_title);
                            NewFingerprintFragment.this.finish();
                            return;
                        }
                        NewFingerprintFragment.this.startActivity(intent);
                    }
                    NewFingerprintFragment.this.mActivity.setResult(-1);
                    NewFingerprintFragment.this.finish();
                }
            });
            return this.mContentView;
        }

        @Override // androidx.fragment.app.Fragment
        public void onPause() {
            this.mInstructionTitle.removeCallbacks(this.mContentDescriptionRunnable);
            if (this.mStartEnrolling) {
                this.mStartEnrolling = false;
                releaseFingerprintHelper();
                dismissAlertDialog(this.mOnInputFailedAlertDialog);
                finish();
            }
            if (this.mIdOfFingerprintWithoutName != null) {
                saveFingerprintname();
            }
            super.onPause();
        }

        @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
        public void onResume() {
            View view;
            super.onResume();
            this.mInstructionTitle.postDelayed(this.mContentDescriptionRunnable, 100L);
            if (this.mOkButton.getVisibility() != 0 || (view = this.mCancelButton) == null) {
                return;
            }
            view.setVisibility(4);
        }

        @Override // androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putBoolean("key_confirm_lock_launched", this.mConfirmLockLaunched);
        }

        @Override // com.android.settings.BaseEditFragment, com.android.settings.BaseFragment, androidx.fragment.app.Fragment
        public void onStart() {
            super.onStart();
            if (getActivity() instanceof AppCompatActivity) {
                ActionBar appCompatActionBar = ((AppCompatActivity) getActivity()).getAppCompatActionBar();
                if (TabletUtils.IS_TABLET) {
                    appCompatActionBar.setCustomView((View) null);
                    return;
                }
                this.mCancelButton = appCompatActionBar.getCustomView().findViewById(16908313);
                appCompatActionBar.getCustomView().findViewById(16908314).setVisibility(4);
            }
        }

        protected void setupViews() {
            this.mInstructionTitle = (TextView) this.mContentView.findViewById(R.id.new_fingerprint_top_title);
            this.mInstructionText = (TextView) this.mContentView.findViewById(R.id.new_fingerprint_top_text);
            this.mInstructionImageView = (ImageView) this.mContentView.findViewById(R.id.new_fingerprint_instruction_img);
            this.mStepVideoView = (MutedVideoView) this.mContentView.findViewById(R.id.new_fingerprint_step_video);
        }
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", MiuiGxzwUtils.isGxzwSensor() ? GxzwNewFingerprintFragment.class.getName() : NewFingerprintFragment.class.getName());
        intent.putExtra(":settings:show_fragment_title", R.string.add_fingerprint_text);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return true;
    }
}
