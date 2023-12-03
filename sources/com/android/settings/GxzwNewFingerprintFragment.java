package com.android.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.PreferenceManager;
import com.android.settings.ConfirmLockPassword;
import com.android.settings.ConfirmLockPattern;
import com.android.settings.MiuiSecurityChooseUnlock;
import com.android.settings.ProvisionSetUpMiuiSecurityChooseUnlock;
import com.android.settings.SetUpMiuiSecurityChooseUnlock;
import com.android.settings.utils.FingerprintUtils;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.util.HapticFeedbackUtil;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class GxzwNewFingerprintFragment extends BaseEditFragment {
    private boolean mAddKeyguardpasswordThenAddFingerprint;
    private ImageButton mCancelButton;
    private View mContentView;
    private String mEnrollHelpInfoText;
    private IBinder mGxzwService;
    private HapticFeedbackUtil mHapticFeedbackUtil;
    private String mIdOfFingerprintWithoutName;
    private TextView mInstructionIndicate;
    private TextView mInstructionText;
    private TextView mInstructionTitle;
    private boolean mNeedToManager;
    private int mNextInputStep;
    private View mOkButton;
    private ImageView mStepView;
    private FingerprintHelper mFingerprintHelper = null;
    private int mInputStep = 1;
    private int mProgress = 0;
    private int mTotalStepNum = 0;
    private boolean mIsStartFingerprint = false;
    private Vibrator mVibrator = null;
    private AlertDialog mOnInputFailedAlertDialog = null;
    private Handler mHandler = new Handler();
    private boolean mStartEnrolling = false;
    private int mFailTime = 0;
    private boolean mIsShowHelpInfo = false;
    private Activity mActivity = null;
    private String mFingerprintName = "";
    private boolean mShowGxzw = false;
    private boolean mConfirmLockLaunched = false;
    private boolean mIsSetup = false;
    private int mEnrollSuccessRtpEffectId = 168;
    private int mEnrollFailRtpEffectId = 165;
    private final String GXZW_SERVICE_NAME = "android.app.fod.ICallback";
    private final String INTERFACE_DESCRIPTOR = "android.app.fod.ICallback";
    private final int CODE_PROCESS_CMD = 1;
    private final int CMD_ADD_SHOW = 101;
    private final int CMD_ADD_DISMISS = 102;
    private FingerprintAddListener mFingerprintAddistener = new FingerprintAddListener() { // from class: com.android.settings.GxzwNewFingerprintFragment.3
        private Runnable mEnrollHelpInfoRunnable = new Runnable() { // from class: com.android.settings.GxzwNewFingerprintFragment.3.1
            @Override // java.lang.Runnable
            public void run() {
                GxzwNewFingerprintFragment.this.mIsShowHelpInfo = true;
                Log.d(NewFingerprintInternalActivity.class.getSimpleName(), "mEnrollHelpInfoText=" + GxzwNewFingerprintFragment.this.mEnrollHelpInfoText);
                GxzwNewFingerprintFragment.this.mInstructionTitle.setText(GxzwNewFingerprintFragment.this.mEnrollHelpInfoText);
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment.setContentDescription(gxzwNewFingerprintFragment.mEnrollHelpInfoText);
            }
        };
        private final Runnable mActionOnAddCompletedRunnable = new Runnable() { // from class: com.android.settings.GxzwNewFingerprintFragment.3.2
            @Override // java.lang.Runnable
            public void run() {
                List<String> fingerprintIds = GxzwNewFingerprintFragment.this.mFingerprintHelper.getFingerprintIds();
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment.mIdOfFingerprintWithoutName = FingerprintUtils.getIdOfFingerprintWithoutName(gxzwNewFingerprintFragment.mActivity, fingerprintIds);
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment2 = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment2.mFingerprintName = FingerprintUtils.generateFingerprintName(gxzwNewFingerprintFragment2.mActivity, fingerprintIds);
                TextView textView = GxzwNewFingerprintFragment.this.mInstructionTitle;
                int i = R.string.gxzw_add_fingerprint_finish;
                textView.setText(i);
                GxzwNewFingerprintFragment.this.mInstructionText.setText(GxzwNewFingerprintFragment.this.mActivity.getString(R.string.fingerprint_gxzw_add_fingerprint_finish, new Object[]{GxzwNewFingerprintFragment.this.mFingerprintName}));
                GxzwNewFingerprintFragment.this.mInstructionIndicate.setVisibility(8);
                GxzwNewFingerprintFragment.this.mOkButton.setVisibility(0);
                if (GxzwNewFingerprintFragment.this.mCancelButton != null) {
                    GxzwNewFingerprintFragment.this.mCancelButton.setVisibility(4);
                }
                GxzwNewFingerprintFragment.this.releaseFingerprintHelper();
                if (fingerprintIds.size() == 1) {
                    Settings.Secure.putInt(GxzwNewFingerprintFragment.this.mActivity.getContentResolver(), "miui_keyguard", 2);
                }
                GxzwNewFingerprintFragment.this.getActivity().setResult(-1);
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment3 = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment3.setContentDescription(gxzwNewFingerprintFragment3.getString(i));
                if (MiuiKeyguardSettingsUtils.isShowDialogToAddFace(GxzwNewFingerprintFragment.this.mActivity)) {
                    GxzwNewFingerprintFragment gxzwNewFingerprintFragment4 = GxzwNewFingerprintFragment.this;
                    if (gxzwNewFingerprintFragment4.isDeviceProvisioned(gxzwNewFingerprintFragment4.mActivity)) {
                        MiuiKeyguardSettingsUtils.showDialogToAddFace(GxzwNewFingerprintFragment.this.mActivity, null, R.style.AlertDialog_Theme_Dark, true);
                    }
                }
            }
        };

        private void handleSimilarFingerprintInputed() {
            onFingerprintImageProcessed();
            GxzwNewFingerprintFragment.this.playStepAnimation();
            if (!GxzwNewFingerprintFragment.this.mIsShowHelpInfo) {
                TextView textView = GxzwNewFingerprintFragment.this.mInstructionTitle;
                int i = R.string.gxzw_add_fingerprint_move_title;
                textView.setText(i);
                GxzwNewFingerprintFragment.this.mInstructionText.setText(R.string.gxzw_add_fingerprint_move_message);
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment.setContentDescription(gxzwNewFingerprintFragment.getString(i));
            }
            GxzwNewFingerprintFragment.this.vibrateDoubleClick();
        }

        private void onFingerprintImageProcessed() {
            if (GxzwNewFingerprintFragment.this.mIsStartFingerprint) {
                return;
            }
            GxzwNewFingerprintFragment.this.mStepView.setVisibility(0);
            GxzwNewFingerprintFragment.this.mInstructionTitle.setText(R.string.gxzw_add_fingerprint_put_finger_title);
            GxzwNewFingerprintFragment.this.mInstructionText.setText(R.string.gxzw_add_fingerprint_put_finger_message);
        }

        @Override // com.android.settings.FingerprintAddListener
        public void addFingerprintCompleted() {
            if (GxzwNewFingerprintFragment.this.mStartEnrolling) {
                if (FingerprintUtils.IS_SUPPORT_LINEAR_MOTOR_VIBRATE && GxzwNewFingerprintFragment.this.mHapticFeedbackUtil.isSupportExtHapticFeedback(GxzwNewFingerprintFragment.this.mEnrollSuccessRtpEffectId)) {
                    GxzwNewFingerprintFragment.this.mHapticFeedbackUtil.performExtHapticFeedback(GxzwNewFingerprintFragment.this.mEnrollSuccessRtpEffectId);
                } else {
                    GxzwNewFingerprintFragment.this.vibrateClick();
                }
                GxzwNewFingerprintFragment.this.playStepAnimation();
                GxzwNewFingerprintFragment.this.mActivity.runOnUiThread(this.mActionOnAddCompletedRunnable);
                GxzwNewFingerprintFragment.this.mIsStartFingerprint = false;
                GxzwNewFingerprintFragment.this.mStartEnrolling = false;
                GxzwNewFingerprintFragment.this.showGxzwTips(false);
            }
        }

        @Override // com.android.settings.FingerprintAddListener
        public void addFingerprintFailed() {
            if (GxzwNewFingerprintFragment.this.mActivity != null) {
                Toast.makeText(GxzwNewFingerprintFragment.this.mActivity.getApplicationContext(), R.string.add_fingerprint_failed_retry_text, 0).show();
            }
            GxzwNewFingerprintFragment.this.finish();
            GxzwNewFingerprintFragment.this.mIsStartFingerprint = false;
        }

        @Override // com.android.settings.FingerprintAddListener
        public void addFingerprintProgress(int i) {
            Log.d(GxzwNewFingerprintFragment.class.getSimpleName(), i + " " + GxzwNewFingerprintFragment.this.mProgress);
            if (GxzwNewFingerprintFragment.this.mStartEnrolling) {
                if (FingerprintUtils.IS_SUPPORT_LINEAR_MOTOR_VIBRATE && GxzwNewFingerprintFragment.this.mHapticFeedbackUtil.isSupportExtHapticFeedback(GxzwNewFingerprintFragment.this.mEnrollSuccessRtpEffectId)) {
                    GxzwNewFingerprintFragment.this.mHapticFeedbackUtil.performExtHapticFeedback(GxzwNewFingerprintFragment.this.mEnrollSuccessRtpEffectId);
                } else {
                    GxzwNewFingerprintFragment.this.vibrateClick();
                }
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment.setContentDescription(gxzwNewFingerprintFragment.getString(R.string.gxzw_enroll_again));
                GxzwNewFingerprintFragment.this.mHandler.removeCallbacks(this.mEnrollHelpInfoRunnable);
                onFingerprintImageProcessed();
                if (!GxzwNewFingerprintFragment.this.mIsStartFingerprint) {
                    GxzwNewFingerprintFragment.this.mIsStartFingerprint = true;
                    GxzwNewFingerprintFragment.this.mTotalStepNum = i + 1;
                }
                if (i == GxzwNewFingerprintFragment.this.mProgress) {
                    handleSimilarFingerprintInputed();
                    return;
                }
                GxzwNewFingerprintFragment.this.mIsShowHelpInfo = false;
                GxzwNewFingerprintFragment.this.mInstructionTitle.setText(R.string.gxzw_add_fingerprint_put_finger_title);
                GxzwNewFingerprintFragment.this.mInputStep = (int) Math.ceil(((r0.mTotalStepNum - i) * 20) / GxzwNewFingerprintFragment.this.mTotalStepNum);
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment2 = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment2.mInputStep = gxzwNewFingerprintFragment2.mInputStep <= 0 ? 1 : GxzwNewFingerprintFragment.this.mInputStep;
                Log.d(GxzwNewFingerprintFragment.class.getSimpleName(), GxzwNewFingerprintFragment.this.mInputStep + " " + GxzwNewFingerprintFragment.this.mTotalStepNum);
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment3 = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment3.mNextInputStep = (int) Math.ceil((double) ((((gxzwNewFingerprintFragment3.mTotalStepNum - i) + 1) * 20) / GxzwNewFingerprintFragment.this.mTotalStepNum));
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment4 = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment4.mNextInputStep = gxzwNewFingerprintFragment4.mNextInputStep > 0 ? GxzwNewFingerprintFragment.this.mNextInputStep : 1;
                GxzwNewFingerprintFragment.this.playStepAnimation();
                GxzwNewFingerprintFragment.this.mProgress = i;
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment5 = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment5.mInputStep = gxzwNewFingerprintFragment5.mNextInputStep;
                GxzwNewFingerprintFragment.this.mFailTime = 0;
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment6 = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment6.dismissAlertDialog(gxzwNewFingerprintFragment6.mOnInputFailedAlertDialog);
                GxzwNewFingerprintFragment.this.mOnInputFailedAlertDialog = null;
                GxzwNewFingerprintFragment.this.mInstructionText.setText(R.string.gxzw_add_fingerprint_put_finger_message);
            }
        }

        @Override // com.android.settings.FingerprintAddListener
        public void onEnrollmentHelp(int i, CharSequence charSequence) {
            Log.d(GxzwNewFingerprintFragment.class.getSimpleName(), "onEnrollmentHelp helpMsgId=" + i + ";helpString" + ((Object) charSequence));
            if (i == 3) {
                TextView textView = GxzwNewFingerprintFragment.this.mInstructionTitle;
                int i2 = R.string.add_fingerprint_dirty_fingerprint;
                textView.setText(i2);
                GxzwNewFingerprintFragment.this.mInstructionText.setText(R.string.gxzw_add_fingerprint_message);
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment.setContentDescription(gxzwNewFingerprintFragment.getString(i2));
                GxzwNewFingerprintFragment.this.performExtHapticFeedback();
            } else if (i == 5) {
                TextView textView2 = GxzwNewFingerprintFragment.this.mInstructionTitle;
                int i3 = R.string.add_fingerprint_move_fast;
                textView2.setText(i3);
                GxzwNewFingerprintFragment.this.mInstructionText.setText(R.string.gxzw_add_fingerprint_message);
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment2 = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment2.setContentDescription(gxzwNewFingerprintFragment2.getString(i3));
                GxzwNewFingerprintFragment.this.performExtHapticFeedback();
            } else if (i != 1025) {
                if (TextUtils.isEmpty(charSequence)) {
                    return;
                }
                GxzwNewFingerprintFragment.this.mEnrollHelpInfoText = charSequence.toString();
                GxzwNewFingerprintFragment.this.mHandler.postDelayed(this.mEnrollHelpInfoRunnable, 100L);
            } else {
                TextView textView3 = GxzwNewFingerprintFragment.this.mInstructionTitle;
                int i4 = R.string.gxzw_add_fingerprint_move_title;
                textView3.setText(i4);
                GxzwNewFingerprintFragment.this.mInstructionText.setText(R.string.gxzw_add_fingerprint_move_message);
                GxzwNewFingerprintFragment gxzwNewFingerprintFragment3 = GxzwNewFingerprintFragment.this;
                gxzwNewFingerprintFragment3.setContentDescription(gxzwNewFingerprintFragment3.getString(i4));
                if (FingerprintUtils.IS_SUPPORT_LINEAR_MOTOR_VIBRATE) {
                    GxzwNewFingerprintFragment.this.performExtHapticFeedback();
                } else {
                    GxzwNewFingerprintFragment.this.vibrateDoubleClick();
                }
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIfShowGxzwGuide(final byte[] bArr) {
        new AsyncTask<Void, Void, Boolean>() { // from class: com.android.settings.GxzwNewFingerprintFragment.8
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                return GxzwNewFingerprintFragment.this.mActivity == null ? Boolean.FALSE : Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(GxzwNewFingerprintFragment.this.mActivity.getApplicationContext()).getBoolean("need_show_gxzw_guide", true));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                super.onPostExecute((AnonymousClass8) bool);
                if (bool.booleanValue()) {
                    GxzwNewFingerprintFragment.this.showGxzwGuideDialog(bArr);
                    return;
                }
                GxzwNewFingerprintFragment.this.showGxzwTips(true);
                GxzwNewFingerprintFragment.this.mFingerprintHelper.startEnrol(GxzwNewFingerprintFragment.this.mFingerprintAddistener, bArr);
            }
        }.execute(new Void[0]);
    }

    private void checkIfShowUserNotice(final byte[] bArr) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            checkIfShowGxzwGuide(bArr);
        } else {
            new AsyncTask<Void, Void, Boolean>() { // from class: com.android.settings.GxzwNewFingerprintFragment.5
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Boolean doInBackground(Void... voidArr) {
                    return GxzwNewFingerprintFragment.this.mActivity == null ? Boolean.FALSE : Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(GxzwNewFingerprintFragment.this.mActivity.getApplicationContext()).getBoolean("need_show_user_notice", true));
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Boolean bool) {
                    super.onPostExecute((AnonymousClass5) bool);
                    if (bool.booleanValue()) {
                        GxzwNewFingerprintFragment.this.showGxzwUserNotice(bArr);
                    } else {
                        GxzwNewFingerprintFragment.this.checkIfShowGxzwGuide(bArr);
                    }
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

    private int generateStepResource() {
        return this.mActivity.getResources().getIdentifier(String.format(Locale.ENGLISH, "scan_output_%02d", Integer.valueOf(this.mInputStep)), "drawable", this.mActivity.getPackageName());
    }

    private int gxzwCallBack(int i, int i2) {
        if (this.mGxzwService == null) {
            this.mGxzwService = ServiceManager.getService("android.app.fod.ICallback");
        }
        if (this.mGxzwService != null) {
            Parcel obtain = Parcel.obtain();
            Parcel obtain2 = Parcel.obtain();
            try {
                obtain.writeInterfaceToken("android.app.fod.ICallback");
                obtain.writeInt(i);
                obtain.writeInt(i2);
                this.mGxzwService.transact(1, obtain, obtain2, 0);
                obtain2.readException();
                return obtain2.readInt();
            } catch (RemoteException unused) {
                this.mGxzwService = null;
            } finally {
                obtain.recycle();
                obtain2.recycle();
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideNavigationBar() {
        this.mActivity.getWindow().getDecorView().setSystemUiVisibility(4866);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDeviceProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$showGxzwGuideDialog$0(AlertDialog alertDialog, DialogInterface dialogInterface) {
        TextView textView = (TextView) alertDialog.getWindow().findViewById(R.id.alertTitle);
        if (textView != null) {
            textView.setSingleLine(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performExtHapticFeedback() {
        if (FingerprintUtils.IS_SUPPORT_LINEAR_MOTOR_VIBRATE && this.mHapticFeedbackUtil.isSupportExtHapticFeedback(this.mEnrollFailRtpEffectId)) {
            this.mHapticFeedbackUtil.performExtHapticFeedback(this.mEnrollFailRtpEffectId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playStepAnimation() {
        int generateStepResource = generateStepResource();
        if (generateStepResource != 0) {
            this.mStepView.setImageResource(generateStepResource);
            Drawable drawable = this.mStepView.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }
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
        String str = this.mFingerprintName;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        FingerprintUtils.setFingerprintName(this.mActivity, this.mIdOfFingerprintWithoutName, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setContentDescription(final String str) {
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.GxzwNewFingerprintFragment.4
            @Override // java.lang.Runnable
            public void run() {
                GxzwNewFingerprintFragment.this.mInstructionTitle.announceForAccessibility(str);
            }
        }, 100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showGxzwGuideDialog(final byte[] bArr) {
        Activity activity = this.mActivity;
        if (activity == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialog_Theme_DayNight);
        builder.setCancelable(false);
        builder.setTitle(R.string.gxzw_dialog_title);
        builder.setMessage(R.string.gxzw_dialog_message);
        builder.setNegativeButton(R.string.gxzw_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.GxzwNewFingerprintFragment.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                GxzwNewFingerprintFragment.this.showGxzwTips(true);
                GxzwNewFingerprintFragment.this.mFingerprintHelper.startEnrol(GxzwNewFingerprintFragment.this.mFingerprintAddistener, bArr);
            }
        });
        builder.setCheckBox(false, this.mActivity.getString(R.string.gxzw_dialog_not_show_again));
        final AlertDialog create = builder.create();
        create.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.GxzwNewFingerprintFragment.10
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (create.isChecked()) {
                    GxzwNewFingerprintFragment.this.updateNeedShowGxzwGuide(false);
                }
                GxzwNewFingerprintFragment.this.hideNavigationBar();
            }
        });
        create.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.android.settings.GxzwNewFingerprintFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                GxzwNewFingerprintFragment.lambda$showGxzwGuideDialog$0(AlertDialog.this, dialogInterface);
            }
        });
        create.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showGxzwTips(boolean z) {
        this.mShowGxzw = z;
        gxzwCallBack(z ? 101 : 102, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showGxzwUserNotice(final byte[] bArr) {
        Activity activity = this.mActivity;
        if (activity == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialog_Theme_DayNight);
        builder.setCancelable(false).setTitle(R.string.finger_add_user_info_dialog_title).setMessage(R.string.finger_add_user_info_dialog_message).setPositiveButton(R.string.finger_add_user_info_dialog_next, new DialogInterface.OnClickListener() { // from class: com.android.settings.GxzwNewFingerprintFragment.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(GxzwNewFingerprintFragment.this.mActivity.getApplicationContext()).edit();
                edit.putBoolean("need_show_user_notice", false);
                edit.commit();
                GxzwNewFingerprintFragment.this.checkIfShowGxzwGuide(bArr);
                dialogInterface.dismiss();
            }
        }).setNegativeButton(R.string.finger_add_user_info_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.GxzwNewFingerprintFragment.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                GxzwNewFingerprintFragment.this.finish();
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNeedShowGxzwGuide(final boolean z) {
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.GxzwNewFingerprintFragment.11
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                if (GxzwNewFingerprintFragment.this.mActivity != null) {
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(GxzwNewFingerprintFragment.this.mActivity.getApplicationContext()).edit();
                    edit.putBoolean("need_show_gxzw_guide", z);
                    edit.commit();
                    return null;
                }
                return null;
            }
        }.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void vibrateClick() {
        Vibrator vibrator = this.mVibrator;
        if (vibrator != null) {
            vibrator.vibrate(60L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void vibrateDoubleClick() {
        Vibrator vibrator = this.mVibrator;
        if (vibrator != null) {
            vibrator.vibrate(new long[]{0, 60, 60, 60}, -1);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 100) {
            if (i2 != -1) {
                if (i2 == 11 && this.mIsSetup) {
                    this.mActivity.setResult(11);
                }
                finish();
                return;
            }
            this.mStartEnrolling = true;
            byte[] byteArrayExtra = intent.getByteArrayExtra("hw_auth_token");
            this.mVibrator = (Vibrator) this.mActivity.getSystemService("vibrator");
            checkIfShowUserNotice(byteArrayExtra);
            if (this.mIsSetup) {
                this.mActivity.setResult(-1);
            }
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
        Intent intent;
        super.onCreate(bundle);
        setThemeRes(R.style.Theme_Dark_Settings_NoTitle);
        this.mFingerprintHelper = new FingerprintHelper(this.mActivity);
        Intent intent2 = getIntent();
        if (intent2 != null) {
            this.mAddKeyguardpasswordThenAddFingerprint = intent2.getBooleanExtra("add_keyguard_password_then_add_fingerprint", false);
            this.mNeedToManager = intent2.getBooleanExtra("need_to_manager", true);
        } else {
            this.mAddKeyguardpasswordThenAddFingerprint = false;
            this.mNeedToManager = true;
        }
        if (bundle != null) {
            this.mConfirmLockLaunched = bundle.getBoolean("key_confirm_lock_launched");
        }
        if (this.mAddKeyguardpasswordThenAddFingerprint) {
            this.mStartEnrolling = true;
            byte[] byteArrayExtra = intent2.getByteArrayExtra("hw_auth_token");
            this.mVibrator = (Vibrator) this.mActivity.getSystemService("vibrator");
            checkIfShowUserNotice(byteArrayExtra);
        } else if (!this.mConfirmLockLaunched) {
            this.mIsSetup = intent2 != null && intent2.getBooleanExtra("setup", false);
            int activePasswordQuality = new MiuiLockPatternUtils(this.mActivity).getActivePasswordQuality(UserHandle.myUserId());
            if (!this.mIsSetup) {
                Class cls = activePasswordQuality != 0 ? activePasswordQuality == 65536 ? ConfirmLockPattern.InternalActivity.class : ConfirmLockPassword.InternalActivity.class : MiuiSecurityChooseUnlock.InternalActivity.class;
                if (UserHandle.myUserId() == 0) {
                    intent = new Intent(this.mActivity, cls);
                } else {
                    Intent intent3 = getIntent();
                    Intent intent4 = new Intent(this.mActivity, cls);
                    intent4.putExtra("com.android.settings.ConfirmLockPattern.header", intent3.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.header"));
                    intent4.putExtra("com.android.settings.titleColor", intent3.getIntExtra("com.android.settings.titleColor", this.mActivity.getResources().getColor(17170443)));
                    intent4.putExtra("com.android.settings.bgColor", intent3.getIntExtra("com.android.settings.bgColor", this.mActivity.getResources().getColor(R.color.set_second_space_background)));
                    intent4.putExtra("com.android.settings.lockBtnWhite", true);
                    intent4.putExtra("com.android.settings.forgetPatternColor", intent3.getIntExtra("com.android.settings.forgetPatternColor", this.mActivity.getResources().getColor(17170443)));
                    intent4.putExtra("com.android.settings.footerTextColor", intent3.getIntExtra("com.android.settings.footerTextColor", this.mActivity.getResources().getColor(17170443)));
                    intent4.putExtra("com.android.settings.forgetPassword", false);
                    intent = intent4;
                }
                intent.putExtra("has_challenge", true);
                intent.putExtra("show_add_fingerprint_hint", true);
                intent.putExtra(":android:show_fragment_title", R.string.empty_title);
                startActivityForResult(intent, 100);
                this.mConfirmLockLaunched = true;
            } else if (activePasswordQuality != 0) {
                this.mActivity.setResult(-1);
                finish();
            } else {
                Intent intent5 = new Intent(this.mActivity, isDeviceProvisioned(getActivity()) ? SetUpMiuiSecurityChooseUnlock.InternalActivity.class : ProvisionSetUpMiuiSecurityChooseUnlock.InternalActivity.class);
                intent5.putExtra("has_challenge", true);
                intent5.putExtra("add_keyguard_password_then_add_fingerprint", true);
                startActivityForResult(intent5, 100);
            }
        }
        this.mActivity.getWindow().setStatusBarColor(0);
        this.mActivity.getWindow().setNavigationBarColor(0);
        this.mActivity.getWindow().addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        this.mActivity.getWindow().addFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        this.mActivity.getWindow().addFlags(128);
        this.mHapticFeedbackUtil = new HapticFeedbackUtil(this.mActivity.getApplicationContext(), false);
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.mActivity = null;
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x00cf  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00d6  */
    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.view.View onInflateView(android.view.LayoutInflater r3, android.view.ViewGroup r4, android.os.Bundle r5) {
        /*
            Method dump skipped, instructions count: 250
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.GxzwNewFingerprintFragment.onInflateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle):android.view.View");
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        if (this.mStartEnrolling) {
            this.mStartEnrolling = false;
            releaseFingerprintHelper();
            dismissAlertDialog(this.mOnInputFailedAlertDialog);
            showGxzwTips(false);
            finish();
        }
        if (this.mIdOfFingerprintWithoutName != null) {
            saveFingerprintname();
        }
        super.onPause();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("key_confirm_lock_launched", this.mConfirmLockLaunched);
    }

    @Override // com.android.settings.BaseEditFragment, com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
        if (appCompatActionBar == null) {
            return;
        }
        appCompatActionBar.setDisplayOptions(8);
        appCompatActionBar.setBackgroundDrawable(new ColorDrawable(-16777216));
        appCompatActionBar.setExpandState(0);
        appCompatActionBar.setTitle((CharSequence) null);
        if (this.mIsSetup) {
            this.mCancelButton.setBackgroundResource(R.drawable.miuix_appcompat_action_bar_back_dark);
            appCompatActionBar.setStartView(this.mCancelButton);
            return;
        }
        appCompatActionBar.setStartView(null);
        this.mCancelButton.setBackgroundResource(R.drawable.miuix_appcompat_action_mode_title_button_cancel_dark);
        appCompatActionBar.setEndView(this.mCancelButton);
    }
}
