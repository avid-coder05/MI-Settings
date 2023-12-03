package com.android.settings;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.security.FingerprintIdUtils;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.ConfirmLockPassword;
import com.android.settings.ConfirmLockPattern;
import com.android.settings.utils.MiuiGxzwUtils;
import com.android.settings.utils.TabletUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.securityspace.CrossUserUtils;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiConfirmCommonPassword extends SettingsCompatActivity {

    /* loaded from: classes.dex */
    public static class MiuiConfirmCommonPasswordFragment extends KeyguardSettingsPreferenceFragment {
        private static boolean sIsFod = SystemProperties.getBoolean("ro.hardware.fp.fod", false);
        private ActivityManager mActivityManager;
        private String mBusinessId;
        private Context mContext;
        private long mFaceEnrollChallenge;
        private int mFingerprintFailTimes;
        private int mUserId;
        private boolean mVerifyChallenge;
        private AlertDialog mFingerprintIdentificationDialog = null;
        private FingerprintHelper mFingerprintHelper = null;
        private int mRequestCode = 0;
        private boolean mConfirmLockLaunched = false;
        private boolean mShouldDismissDialog = false;
        private boolean mIsDarkModeEnabled = false;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class FullScreenDialog extends AlertDialog {
            FullScreenDialog(Context context, int i) {
                super(context);
            }

            @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog, android.content.DialogInterface
            public void dismiss() {
                super.dismiss();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // miuix.appcompat.app.AlertDialog, android.app.Dialog
            public void onStart() {
                getWindow().setLayout(-1, -1);
                getWindow().setBackgroundDrawable(new ColorDrawable(MiuiConfirmCommonPasswordFragment.this.getResources().getColor(R.color.secspace_fod_finger_bg_color)));
            }
        }

        static /* synthetic */ int access$308(MiuiConfirmCommonPasswordFragment miuiConfirmCommonPasswordFragment) {
            int i = miuiConfirmCommonPasswordFragment.mFingerprintFailTimes;
            miuiConfirmCommonPasswordFragment.mFingerprintFailTimes = i + 1;
            return i;
        }

        private void dismissDialog() {
            AlertDialog alertDialog = this.mFingerprintIdentificationDialog;
            if (alertDialog == null || !alertDialog.isShowing()) {
                return;
            }
            this.mFingerprintIdentificationDialog.dismiss();
            this.mFingerprintIdentificationDialog = null;
        }

        private List<String> getFingerprintIdsForSecond(List<String> list) {
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(FingerprintIdUtils.getUserFingerprintIds(this.mContext, Settings.Secure.getIntForUser(getContentResolver(), "second_user_id", -10000, 0)).keySet());
            if (UserHandle.myUserId() != 0) {
                Iterator<String> it = list.iterator();
                while (it.hasNext()) {
                    if (arrayList.contains(it.next())) {
                        it.remove();
                    }
                }
                return list;
            }
            return arrayList;
        }

        private void identifiedFailed() {
            if (this.mFingerprintHelper == null) {
                return;
            }
            if (sIsFod) {
                ((TextView) this.mFingerprintIdentificationDialog.findViewById(R.id.tv_secspace_fod_finger_result)).setVisibility(0);
            } else {
                ((TextView) this.mFingerprintIdentificationDialog.findViewById(R.id.confirm_fingerprint_view_title)).setText(R.string.fingerprint_identify_try_again_msg);
            }
            releaseFingerprintHelper();
            int i = this.mFingerprintFailTimes + 1;
            this.mFingerprintFailTimes = i;
            if (i < 5) {
                startFingerprintIdentification();
                return;
            }
            this.mShouldDismissDialog = true;
            startConfirmActivity();
        }

        private void isIdentified() {
            releaseFingerprintHelper();
            this.mShouldDismissDialog = true;
            setResult(-1);
            finish();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isSecurityCoreAddBusiness() {
            return "security_core_add".equals(this.mBusinessId);
        }

        private boolean isSupportFingerprintForSecond(List<String> list) {
            if (isSecurityCoreAddBusiness()) {
                List<String> fingerprintIdsForSecond = getFingerprintIdsForSecond(list);
                return fingerprintIdsForSecond != null && fingerprintIdsForSecond.size() > 0;
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void releaseFingerprintHelper() {
            FingerprintHelper fingerprintHelper = this.mFingerprintHelper;
            if (fingerprintHelper != null) {
                fingerprintHelper.cancelIdentify();
                this.mFingerprintHelper = null;
            }
        }

        private void showAuthenticationDialog() {
            if (sIsFod) {
                showFodFingerprintAuthenticationDialog();
            } else {
                showFingerprintAuthenticationDialog();
            }
        }

        private void showFingerprintAuthenticationDialog() {
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiConfirmCommonPassword.MiuiConfirmCommonPasswordFragment.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == -2) {
                        MiuiConfirmCommonPasswordFragment.this.setResult(0);
                        MiuiConfirmCommonPasswordFragment.this.finish();
                    } else if (i == -1) {
                        MiuiConfirmCommonPasswordFragment.this.mShouldDismissDialog = true;
                        MiuiConfirmCommonPasswordFragment.this.startConfirmActivity();
                    }
                }
            };
            AlertDialog create = new AlertDialog.Builder(this.mContext).setCancelable(false).setView(getLayoutInflater().inflate(R.layout.confirm_fingerprint_dialog_content_view, (ViewGroup) null)).setPositiveButton(R.string.fingerprint_identify_input_password_msg, onClickListener).setNegativeButton(17039360, onClickListener).create();
            this.mFingerprintIdentificationDialog = create;
            create.show();
            this.mShouldDismissDialog = false;
        }

        private void showFodFingerprintAuthenticationDialog() {
            this.mFingerprintIdentificationDialog = new FullScreenDialog(this.mContext, R.style.Fod_Dialog_Fullscreen);
            View inflate = getLayoutInflater().inflate(R.layout.confirm_fod_fingerprint_activity, (ViewGroup) null);
            TextView textView = (TextView) inflate.findViewById(R.id.tv_use_password);
            Window window = this.mFingerprintIdentificationDialog.getWindow();
            FragmentActivity activity = getActivity();
            int i = R.color.secspace_fod_finger_bg_color;
            window.setNavigationBarColor(activity.getColor(i));
            getActivity().getWindow().getDecorView().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(i)));
            MiuiGxzwUtils.caculateGxzwIconSize(getActivity());
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.fop_password_text_margin_bottom);
            int i2 = MiuiGxzwUtils.GXZW_ICON_Y;
            if (i2 > 0) {
                layoutParams.bottomMargin = (displayMetrics.heightPixels - i2) + dimensionPixelOffset;
            } else {
                layoutParams.bottomMargin = dimensionPixelOffset * 4;
            }
            textView.setLayoutParams(layoutParams);
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MiuiConfirmCommonPassword.MiuiConfirmCommonPasswordFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MiuiConfirmCommonPasswordFragment.this.releaseFingerprintHelper();
                    MiuiConfirmCommonPasswordFragment.this.mShouldDismissDialog = true;
                    MiuiConfirmCommonPasswordFragment.this.startConfirmActivity();
                }
            });
            this.mShouldDismissDialog = false;
            this.mFingerprintIdentificationDialog.show();
            this.mFingerprintIdentificationDialog.setContentView(inflate);
            this.mFingerprintIdentificationDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settings.MiuiConfirmCommonPassword.MiuiConfirmCommonPasswordFragment.3
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface dialogInterface) {
                    MiuiConfirmCommonPasswordFragment.this.setResult(0);
                    MiuiConfirmCommonPasswordFragment.this.finish();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startConfirmActivity() {
            Class<?> cls;
            String extraFragmentName;
            MiuiLockPatternUtils miuiLockPatternUtils = new MiuiLockPatternUtils(getActivity().getApplicationContext());
            int i = this.mUserId;
            int keyguardStoredPasswordQuality = i == -9999 ? miuiLockPatternUtils.getKeyguardStoredPasswordQuality(i) : miuiLockPatternUtils.getActivePasswordQuality(i);
            if (keyguardStoredPasswordQuality == 0) {
                setResult(0);
                finish();
                return;
            }
            if (keyguardStoredPasswordQuality == 65536) {
                cls = ConfirmLockPattern.InternalActivity.class;
                extraFragmentName = ConfirmLockPattern.InternalActivity.getExtraFragmentName();
                if (CrossUserUtils.isAirSpace(getActivity().getApplicationContext(), this.mUserId)) {
                    cls = ConfirmSpacePatternActivity.class;
                    extraFragmentName = ConfirmSpacePatternActivity.getExtraFragmentName();
                }
            } else {
                cls = ConfirmLockPassword.InternalActivity.class;
                extraFragmentName = ConfirmLockPassword.InternalActivity.getExtraFragmentName();
                if (CrossUserUtils.isAirSpace(getActivity().getApplicationContext(), this.mUserId)) {
                    cls = ConfirmSpacePasswordActivity.class;
                    extraFragmentName = ConfirmSpacePasswordActivity.getExtraFragmentName();
                }
            }
            Intent intent = new Intent(this.mContext, cls);
            intent.putExtra(":settings:show_fragment", extraFragmentName);
            if (isSecurityCoreAddBusiness()) {
                intent = getIntent();
                intent.setClass(this.mContext, cls);
                intent.putExtra("businessId", "security_core_add");
            }
            int i2 = R.string.empty_title;
            intent.putExtra(":android:show_fragment_title", i2);
            intent.putExtra("from_confirm_frp_credential", this.mUserId == -9999);
            intent.putExtra("has_challenge", this.mVerifyChallenge);
            intent.putExtra("challenge", this.mFaceEnrollChallenge);
            if (TabletUtils.IS_TABLET) {
                MiuiKeyguardSettingsUtils.startFragment(this, extraFragmentName, this.mRequestCode, intent.getExtras(), i2);
            } else {
                startActivityForResult(intent, this.mUserId == -9999 ? 1001 : this.mRequestCode);
            }
        }

        private void startFingerprintIdentification() {
            FingerprintHelper fingerprintHelper = new FingerprintHelper(this.mContext);
            this.mFingerprintHelper = fingerprintHelper;
            List<String> fingerprintIds = fingerprintHelper.getFingerprintIds();
            if (isSecurityCoreAddBusiness()) {
                fingerprintIds = getFingerprintIdsForSecond(fingerprintIds);
            }
            this.mFingerprintHelper.identify(new FingerprintIdentifyCallback() { // from class: com.android.settings.MiuiConfirmCommonPassword.MiuiConfirmCommonPasswordFragment.1
                @Override // com.android.settings.FingerprintIdentifyCallback
                public void onFailed() {
                    if (MiuiConfirmCommonPasswordFragment.this.mFingerprintHelper == null) {
                        return;
                    }
                    if (MiuiConfirmCommonPasswordFragment.sIsFod) {
                        ((TextView) MiuiConfirmCommonPasswordFragment.this.mFingerprintIdentificationDialog.findViewById(R.id.tv_secspace_fod_finger_result)).setVisibility(0);
                    } else {
                        ((TextView) MiuiConfirmCommonPasswordFragment.this.mFingerprintIdentificationDialog.findViewById(R.id.confirm_fingerprint_view_title)).setText(R.string.fingerprint_identify_try_again_msg);
                    }
                    MiuiConfirmCommonPasswordFragment.access$308(MiuiConfirmCommonPasswordFragment.this);
                }

                @Override // com.android.settings.FingerprintIdentifyCallback
                public void onIdentified(int i) {
                    if (MiuiConfirmCommonPasswordFragment.this.isSecurityCoreAddBusiness()) {
                        MiuiConfirmCommonPasswordFragment.this.onIdentifiedBySecurityCoreAdd(i);
                        return;
                    }
                    MiuiConfirmCommonPasswordFragment.this.releaseFingerprintHelper();
                    MiuiConfirmCommonPasswordFragment.this.mShouldDismissDialog = true;
                    MiuiConfirmCommonPasswordFragment.this.setResult(-1);
                    MiuiConfirmCommonPasswordFragment.this.finish();
                }

                @Override // com.android.settings.FingerprintIdentifyCallback
                public void onLockout() {
                    MiuiConfirmCommonPasswordFragment.this.releaseFingerprintHelper();
                    MiuiConfirmCommonPasswordFragment.this.mShouldDismissDialog = true;
                    MiuiConfirmCommonPasswordFragment.this.startConfirmActivity();
                }
            }, fingerprintIds, 1);
        }

        protected boolean isSecondFingerprint(int i) {
            HashMap userFingerprintIds = FingerprintIdUtils.getUserFingerprintIds(this.mContext, Settings.Secure.getIntForUser(getContentResolver(), "second_user_id", -10000, 0));
            return userFingerprintIds != null && userFingerprintIds.containsKey(String.valueOf(i));
        }

        @Override // androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            setResult(i2, intent);
            finish();
        }

        @Override // miuix.preference.PreferenceFragment, androidx.fragment.app.Fragment, android.content.ComponentCallbacks
        public void onConfigurationChanged(Configuration configuration) {
            if (((configuration.uiMode & 48) == 32) == this.mIsDarkModeEnabled || this.mShouldDismissDialog) {
                super.onConfigurationChanged(configuration);
                return;
            }
            setResult(0);
            finish();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            boolean z;
            super.onCreate(bundle);
            this.mContext = getActivity();
            this.mActivityManager = (ActivityManager) getSystemService("activity");
            if (bundle != null) {
                this.mConfirmLockLaunched = bundle.getBoolean("key_confirm_lock_launched");
            }
            this.mIsDarkModeEnabled = (getResources().getConfiguration().uiMode & 48) == 32;
            if (getIntent() != null) {
                this.mVerifyChallenge = getIntent().getBooleanExtra("has_challenge", false);
                this.mFaceEnrollChallenge = getIntent().getLongExtra("challenge", 0L);
                this.mRequestCode = MiuiKeyguardSettingsUtils.getIntExtra(getArguments(), getActivity().getIntent(), "confirm_password_request_code", 0);
                String stringExtra = getIntent().getStringExtra("businessId");
                this.mBusinessId = stringExtra;
                if ("android.app.action.CONFIRM_FRP_CREDENTIAL".equalsIgnoreCase(getIntent().getAction())) {
                    this.mUserId = -9999;
                }
                if (isSecurityCoreAddBusiness()) {
                    int intExtra = getIntent().getIntExtra("com.android.settings.userIdToConfirm", -10000);
                    this.mUserId = intExtra;
                    if (intExtra != 0 && intExtra != -10000 && !sIsFod) {
                        getActivity().getWindow().getDecorView().setBackgroundColor(getIntent().getIntExtra("com.android.settings.bgColor", R.color.set_second_space_background));
                    }
                }
                if (!TextUtils.isEmpty(stringExtra)) {
                    String[] stringArray = getResources().getStringArray(R.array.common_password_business_keys);
                    String[] stringArray2 = getResources().getStringArray(R.array.common_password_business_clickable_default);
                    int i = 0;
                    while (true) {
                        if (i >= stringArray.length) {
                            z = false;
                            break;
                        } else if (stringExtra.equals(stringArray[i])) {
                            z = !Boolean.parseBoolean(stringArray2[i]);
                            break;
                        } else {
                            i++;
                        }
                    }
                    try {
                        List<String> fingerprintIds = new FingerprintHelper(this.mContext).getFingerprintIds();
                        if ((z || Settings.Secure.getInt(getContentResolver(), stringExtra, 0) == 2) && fingerprintIds != null && fingerprintIds.size() > 0 && isSupportFingerprintForSecond(fingerprintIds) && this.mActivityManager.isUserRunning(this.mUserId)) {
                            showAuthenticationDialog();
                            startFingerprintIdentification();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("MiuiConfirmCommonPassword", e.getMessage(), e);
                    }
                }
            }
            if (this.mConfirmLockLaunched) {
                return;
            }
            startConfirmActivity();
            this.mConfirmLockLaunched = true;
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            releaseFingerprintHelper();
            dismissDialog();
            super.onDestroy();
        }

        @Override // com.android.settings.SettingsPreferenceFragment
        public void onFragmentResult(int i, Bundle bundle) {
            if (i == 1001 && bundle != null && bundle.getInt("miui_security_fragment_result") == 0) {
                MiuiSecurityCommonSettings.setFragmentResultOnDetach(this, 0, bundle.getString("password"), bundle);
                finish();
            }
        }

        protected void onIdentifiedBySecurityCoreAdd(int i) {
            int i2 = this.mUserId;
            if (i2 == 0 || i2 == -10000) {
                if (isSecondFingerprint(i)) {
                    identifiedFailed();
                } else {
                    isIdentified();
                }
            } else if (isSecondFingerprint(i)) {
                isIdentified();
            } else {
                identifiedFailed();
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onPause() {
            releaseFingerprintHelper();
            AlertDialog alertDialog = this.mFingerprintIdentificationDialog;
            if (alertDialog != null && alertDialog.isShowing() && !this.mShouldDismissDialog) {
                this.mFingerprintIdentificationDialog.dismiss();
                this.mFingerprintIdentificationDialog = null;
                finish();
            }
            super.onPause();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putBoolean("key_confirm_lock_launched", this.mConfirmLockLaunched);
        }
    }

    public static String getExtraFragmentName() {
        return MiuiConfirmCommonPasswordFragment.class.getName();
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", getExtraFragmentName());
        intent.putExtra(":settings:show_fragment_title", R.string.empty_title);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return true;
    }
}
