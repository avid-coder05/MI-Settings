package com.android.settings.privacypassword;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.security.ChooseLockSettingsHelper;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.internal.widget.LockPatternView;
import com.android.settings.LockPatternView;
import com.android.settings.R;
import com.android.settings.compat.LockPatternUtilsCompat;
import com.android.settings.privacypassword.analytics.AnalyticHelper;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes2.dex */
public class PrivacyPasswordChooseAccessControl extends Activity implements View.OnClickListener {
    protected TextView bigTitle;
    private View mButtonLayout;
    private TextView mFooterLeftButton;
    private TextView mFooterRightButton;
    private FrameLayout mFrameLockPattern;
    private boolean mIsInMultiWindow;
    protected boolean mIsModifyPassword;
    private ChooseLockSettingsHelper mLockHelper;
    private LinearLayout mPrivacyIconContainer;
    protected TextView mPrivacyPasswordHeaderText;
    protected ImageView mPrivacyPasswordIconView;
    protected TextView mPrivacyPasswordSettingTitle;
    protected View mRelative;
    private RelativeLayout mSplitMaskView;
    private WindowManager mWindowManager;
    protected TextView privacyChooseAccessControlBack;
    protected TextView privacyChooseAccessControlBackTitle;
    protected LockPatternView privacyPasswordLockPatternView;
    private boolean mCheckOnPcMode = false;
    protected List<LockPatternView.Cell> mChosenPattern = null;
    private final List<LockPatternView.Cell> mAnimatePattern = Collections.unmodifiableList(Lists.newArrayList(new LockPatternView.Cell[]{LockPatternView.Cell.of(0, 0), LockPatternView.Cell.of(0, 1), LockPatternView.Cell.of(1, 1), LockPatternView.Cell.of(2, 1)}));
    private Stage mUiStage = Stage.Introduction;
    private Runnable mClearPatternRunnable = new Runnable() { // from class: com.android.settings.privacypassword.PrivacyPasswordChooseAccessControl.1
        @Override // java.lang.Runnable
        public void run() {
            PrivacyPasswordChooseAccessControl.this.privacyPasswordLockPatternView.clearPattern();
        }
    };
    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() { // from class: com.android.settings.privacypassword.PrivacyPasswordChooseAccessControl.2
        private void patternInProgress() {
            PrivacyPasswordChooseAccessControl.this.mPrivacyPasswordHeaderText.setText(R.string.lockpattern_recording_inprogress);
            PrivacyPasswordChooseAccessControl.this.mFooterLeftButton.setEnabled(false);
            PrivacyPasswordChooseAccessControl.this.mFooterRightButton.setEnabled(false);
        }

        @Override // com.android.settings.LockPatternView.OnPatternListener
        public void onPatternCellAdded(List<LockPatternView.Cell> list) {
            Log.i("PrivacyPasswordChooseAccessControl", "onpatternCellAdded");
        }

        @Override // com.android.settings.LockPatternView.OnPatternListener
        public void onPatternCleared() {
            PrivacyPasswordChooseAccessControl privacyPasswordChooseAccessControl = PrivacyPasswordChooseAccessControl.this;
            privacyPasswordChooseAccessControl.privacyPasswordLockPatternView.removeCallbacks(privacyPasswordChooseAccessControl.mClearPatternRunnable);
        }

        @Override // com.android.settings.LockPatternView.OnPatternListener
        public void onPatternDetected(List<LockPatternView.Cell> list) {
            if (PrivacyPasswordChooseAccessControl.this.mUiStage == Stage.NeedToConfirm || PrivacyPasswordChooseAccessControl.this.mUiStage == Stage.ConfirmWrong) {
                List<LockPatternView.Cell> list2 = PrivacyPasswordChooseAccessControl.this.mChosenPattern;
                if (list2 == null) {
                    throw new IllegalStateException("null chose pattern in stage 'need to confirm");
                }
                if (!list2.equals(list)) {
                    PrivacyPasswordChooseAccessControl.this.updateStage(Stage.ConfirmWrong);
                    return;
                }
                Stage stage = Stage.ChoiceConfirmed;
                stage.rightMode.text = PrivacyPasswordChooseAccessControl.this.getConfirmTextId();
                PrivacyPasswordChooseAccessControl.this.updateStage(stage);
            } else if (PrivacyPasswordChooseAccessControl.this.mUiStage != Stage.Introduction && PrivacyPasswordChooseAccessControl.this.mUiStage != Stage.ChoiceTooShort) {
                throw new IllegalStateException("Unexpected stage " + PrivacyPasswordChooseAccessControl.this.mUiStage + "when entering the pattern .");
            } else if (list.size() < 4) {
                PrivacyPasswordChooseAccessControl.this.updateStage(Stage.ChoiceTooShort);
            } else {
                PrivacyPasswordChooseAccessControl.this.mChosenPattern = new ArrayList(list);
                PrivacyPasswordChooseAccessControl.this.updateStage(Stage.FirstChoiceValid);
            }
        }

        @Override // com.android.settings.LockPatternView.OnPatternListener
        public void onPatternStart() {
            PrivacyPasswordChooseAccessControl privacyPasswordChooseAccessControl = PrivacyPasswordChooseAccessControl.this;
            privacyPasswordChooseAccessControl.privacyPasswordLockPatternView.removeCallbacks(privacyPasswordChooseAccessControl.mClearPatternRunnable);
            patternInProgress();
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.privacypassword.PrivacyPasswordChooseAccessControl$4  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordChooseAccessControl$Stage;

        static {
            int[] iArr = new int[Stage.values().length];
            $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordChooseAccessControl$Stage = iArr;
            try {
                iArr[Stage.Introduction.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordChooseAccessControl$Stage[Stage.NeedToConfirm.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordChooseAccessControl$Stage[Stage.HelpScreen.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordChooseAccessControl$Stage[Stage.ChoiceTooShort.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordChooseAccessControl$Stage[Stage.ConfirmWrong.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$settings$privacypassword$PrivacyPasswordChooseAccessControl$Stage[Stage.FirstChoiceValid.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Enum visitor error
    jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'Cancel' uses external variables
    	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:451)
    	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:395)
    	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:324)
    	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:262)
    	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
    	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
     */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* loaded from: classes2.dex */
    public static final class LeftButtonMode {
        private static final /* synthetic */ LeftButtonMode[] $VALUES;
        public static final LeftButtonMode Cancel;
        public static final LeftButtonMode CancelDisable;
        public static final LeftButtonMode Gone;
        public static final LeftButtonMode Retry;
        public static final LeftButtonMode RetryDisabled;
        final boolean enabled;
        final int text;

        static {
            int i = R.string.cancel;
            LeftButtonMode leftButtonMode = new LeftButtonMode("Cancel", 0, i, true);
            Cancel = leftButtonMode;
            LeftButtonMode leftButtonMode2 = new LeftButtonMode("CancelDisable", 1, i, false);
            CancelDisable = leftButtonMode2;
            int i2 = R.string.lockpattern_retry_button_text;
            LeftButtonMode leftButtonMode3 = new LeftButtonMode("Retry", 2, i2, true);
            Retry = leftButtonMode3;
            LeftButtonMode leftButtonMode4 = new LeftButtonMode("RetryDisabled", 3, i2, false);
            RetryDisabled = leftButtonMode4;
            LeftButtonMode leftButtonMode5 = new LeftButtonMode("Gone", 4, -1, false);
            Gone = leftButtonMode5;
            $VALUES = new LeftButtonMode[]{leftButtonMode, leftButtonMode2, leftButtonMode3, leftButtonMode4, leftButtonMode5};
        }

        private LeftButtonMode(String str, int i, int i2, boolean z) {
            this.text = i2;
            this.enabled = z;
        }

        public static LeftButtonMode valueOf(String str) {
            return (LeftButtonMode) Enum.valueOf(LeftButtonMode.class, str);
        }

        public static LeftButtonMode[] values() {
            return (LeftButtonMode[]) $VALUES.clone();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Enum visitor error
    jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'Continue' uses external variables
    	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:451)
    	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:395)
    	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:324)
    	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:262)
    	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
    	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
     */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* loaded from: classes2.dex */
    public static final class RightButtonMode {
        private static final /* synthetic */ RightButtonMode[] $VALUES;
        public static final RightButtonMode Confirm;
        public static final RightButtonMode ConfirmDisabled;
        public static final RightButtonMode Continue;
        public static final RightButtonMode ContinueDisabled;
        public static final RightButtonMode Gone;
        public static final RightButtonMode Ok;
        final boolean enabled;
        int text;

        static {
            int i = R.string.lockpattern_continue_button_text;
            RightButtonMode rightButtonMode = new RightButtonMode("Continue", 0, i, true);
            Continue = rightButtonMode;
            RightButtonMode rightButtonMode2 = new RightButtonMode("ContinueDisabled", 1, i, false);
            ContinueDisabled = rightButtonMode2;
            int i2 = R.string.privacy_password_settings_next;
            RightButtonMode rightButtonMode3 = new RightButtonMode("Confirm", 2, i2, true);
            Confirm = rightButtonMode3;
            RightButtonMode rightButtonMode4 = new RightButtonMode("ConfirmDisabled", 3, i2, false);
            ConfirmDisabled = rightButtonMode4;
            RightButtonMode rightButtonMode5 = new RightButtonMode("Ok", 4, 17039370, true);
            Ok = rightButtonMode5;
            RightButtonMode rightButtonMode6 = new RightButtonMode("Gone", 5, -1, false);
            Gone = rightButtonMode6;
            $VALUES = new RightButtonMode[]{rightButtonMode, rightButtonMode2, rightButtonMode3, rightButtonMode4, rightButtonMode5, rightButtonMode6};
        }

        private RightButtonMode(String str, int i, int i2, boolean z) {
            this.text = i2;
            this.enabled = z;
        }

        public static RightButtonMode valueOf(String str) {
            return (RightButtonMode) Enum.valueOf(RightButtonMode.class, str);
        }

        public static RightButtonMode[] values() {
            return (RightButtonMode[]) $VALUES.clone();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Enum visitor error
    jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'Introduction' uses external variables
    	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:451)
    	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:395)
    	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:324)
    	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:262)
    	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
    	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
     */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* loaded from: classes2.dex */
    public static final class Stage {
        private static final /* synthetic */ Stage[] $VALUES;
        public static final Stage ChoiceConfirmed;
        public static final Stage ChoiceTooShort;
        public static final Stage ConfirmWrong;
        public static final Stage FirstChoiceValid;
        public static final Stage HelpScreen;
        public static final Stage Introduction;
        public static final Stage NeedToConfirm;
        final int footerMessage;
        int headerMessage;
        final LeftButtonMode leftMode;
        final boolean patternEnabled;
        final RightButtonMode rightMode;

        static {
            int i = R.string.lockpattern_recording_intro_header;
            LeftButtonMode leftButtonMode = LeftButtonMode.Gone;
            RightButtonMode rightButtonMode = RightButtonMode.Gone;
            Stage stage = new Stage("Introduction", 0, i, leftButtonMode, rightButtonMode, -1, true);
            Introduction = stage;
            Stage stage2 = new Stage("HelpScreen", 1, R.string.lockpattern_settings_help_how_to_record, leftButtonMode, RightButtonMode.Ok, -1, true);
            HelpScreen = stage2;
            Stage stage3 = new Stage("ChoiceTooShort", 2, R.string.lockpattern_recording_incorrect_too_short, leftButtonMode, rightButtonMode, -1, true);
            ChoiceTooShort = stage3;
            Stage stage4 = new Stage("FirstChoiceValid", 3, R.string.lockpattern_pattern_entered_header, leftButtonMode, rightButtonMode, -1, false);
            FirstChoiceValid = stage4;
            int i2 = R.string.lockpattern_need_to_confirm;
            LeftButtonMode leftButtonMode2 = LeftButtonMode.Retry;
            RightButtonMode rightButtonMode2 = RightButtonMode.ConfirmDisabled;
            Stage stage5 = new Stage("NeedToConfirm", 4, i2, leftButtonMode2, rightButtonMode2, -1, true);
            NeedToConfirm = stage5;
            Stage stage6 = new Stage("ConfirmWrong", 5, R.string.lockpattern_need_to_unlock_wrong, leftButtonMode2, rightButtonMode2, -1, true);
            ConfirmWrong = stage6;
            Stage stage7 = new Stage("ChoiceConfirmed", 6, R.string.lockpattern_pattern_confirmed_header, leftButtonMode2, RightButtonMode.Confirm, -1, false);
            ChoiceConfirmed = stage7;
            $VALUES = new Stage[]{stage, stage2, stage3, stage4, stage5, stage6, stage7};
        }

        private Stage(String str, int i, int i2, LeftButtonMode leftButtonMode, RightButtonMode rightButtonMode, int i3, boolean z) {
            this.headerMessage = i2;
            this.leftMode = leftButtonMode;
            this.rightMode = rightButtonMode;
            this.footerMessage = i3;
            this.patternEnabled = z;
        }

        public static Stage valueOf(String str) {
            return (Stage) Enum.valueOf(Stage.class, str);
        }

        public static Stage[] values() {
            return (Stage[]) $VALUES.clone();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getConfirmTextId() {
        return (isBindAccount() || this.mIsModifyPassword) ? R.string.lockpattern_confirm_button_text : R.string.privacy_password_settings_next;
    }

    private void handleSpecialDevice() {
        if (PrivacyPasswordUtils.isFoldInternalScreen(this) || this.mCheckOnPcMode) {
            return;
        }
        this.privacyChooseAccessControlBackTitle.setVisibility(4);
        this.bigTitle.setVisibility(0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mPrivacyIconContainer.getLayoutParams();
        layoutParams.setMargins(0, PrivacyPasswordUtils.getDimen(this, R.dimen.px_223), 0, 0);
        this.mPrivacyIconContainer.setLayoutParams(layoutParams);
        this.mPrivacyIconContainer.requestLayout();
    }

    private void handleSplitModel() {
        if (PrivacyPasswordUtils.isFoldInternalScreen(this) && getIntent().getMiuiFlags() == 4 && !this.mCheckOnPcMode) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mFooterLeftButton.getLayoutParams();
            int i = R.dimen.px_80;
            layoutParams.setMarginStart(PrivacyPasswordUtils.getDimen(this, i));
            this.mFooterLeftButton.setLayoutParams(layoutParams);
            this.mFooterLeftButton.requestLayout();
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mFooterRightButton.getLayoutParams();
            layoutParams2.setMarginEnd(PrivacyPasswordUtils.getDimen(this, i));
            layoutParams2.setMarginStart(PrivacyPasswordUtils.getDimen(this, R.dimen.px_40));
            this.mFooterRightButton.setLayoutParams(layoutParams2);
            this.mFooterRightButton.requestLayout();
        }
    }

    private boolean isBindAccount() {
        PrivacyPasswordManager privacyPasswordManager = PrivacyPasswordManager.getInstance(this);
        return privacyPasswordManager.getBindXiaoMiAccount() != null && XiaomiAccountUtils.isLoginXiaomiAccount(this) && TextUtils.equals(privacyPasswordManager.getBindXiaoMiAccount(), XiaomiAccountUtils.getLoginedAccountMd5(this));
    }

    private boolean isRealInMultiWindow() {
        try {
            Boolean bool = (Boolean) getClass().getMethod("isInMultiWindowMode", new Class[0]).invoke(this, new Object[0]);
            if (Build.VERSION.SDK_INT >= 24) {
                if (!bool.booleanValue()) {
                    if (!this.mIsInMultiWindow) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("PrivacyPasswordChooseAccessControl", "isRealInMultiWindow", e);
            return false;
        }
    }

    private void postClearPatternRunnable() {
        this.privacyPasswordLockPatternView.removeCallbacks(this.mClearPatternRunnable);
        this.privacyPasswordLockPatternView.postDelayed(this.mClearPatternRunnable, 2000L);
    }

    private void startAddAccountActiity() {
        if (this.mIsModifyPassword) {
            return;
        }
        String stringExtra = getIntent().getStringExtra(YellowPageContract.MipubPhoneEvent.URI_PARAM_EXTRA_DATA);
        boolean z = stringExtra != null && stringExtra.equals("choose_suspend");
        if (!isBindAccount()) {
            Intent intent = new Intent(this, AddAccountActivity.class);
            intent.putExtra("is_start_modify", z);
            intent.putExtra("enter_forgetpage_way", 1);
            startActivityForResult(intent, 10222);
            return;
        }
        if (z) {
            startActivity(new Intent(this, ModifyAndInstructionPrivacyPassword.class));
        }
        this.mLockHelper.setPrivacyPasswordEnabledAsUser(true, UserHandle.myUserId());
        setResult(-1);
        finish();
    }

    private void updateResoureForPCMode() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mPrivacyIconContainer.getLayoutParams();
        layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.privacy_password_top_layout_margin_top_pc);
        this.mPrivacyIconContainer.setLayoutParams(layoutParams);
    }

    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 10222) {
            return;
        }
        if (i2 == -1) {
            setResult(-1);
        } else {
            this.mLockHelper.setPrivacyPasswordEnabledAsUser(false, UserHandle.myUserId());
            setResult(0);
        }
        finish();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mFooterLeftButton) {
            LeftButtonMode leftButtonMode = this.mUiStage.leftMode;
            if (leftButtonMode == LeftButtonMode.Retry) {
                this.mChosenPattern = null;
                this.privacyPasswordLockPatternView.clearPattern();
                updateStage(Stage.Introduction);
            } else if (leftButtonMode == LeftButtonMode.Cancel) {
                setResult(0);
                finish();
            } else {
                throw new IllegalStateException("left footer button pressed , but stage of " + this.mUiStage + " doesn't make sense");
            }
        } else if (view != this.mFooterRightButton) {
            if (view == this.privacyChooseAccessControlBack) {
                finish();
            }
        } else {
            Stage stage = this.mUiStage;
            RightButtonMode rightButtonMode = stage.rightMode;
            RightButtonMode rightButtonMode2 = RightButtonMode.Continue;
            if (rightButtonMode == rightButtonMode2) {
                if (stage == Stage.FirstChoiceValid) {
                    updateStage(Stage.NeedToConfirm);
                    return;
                }
                throw new IllegalStateException("expected ui stage " + Stage.ChoiceConfirmed + " when button is " + rightButtonMode2);
            }
            RightButtonMode rightButtonMode3 = RightButtonMode.Confirm;
            if (rightButtonMode == rightButtonMode3) {
                Stage stage2 = Stage.ChoiceConfirmed;
                if (stage == stage2) {
                    saveChosenPatternAndFinish();
                    return;
                }
                throw new IllegalStateException("expected ui stage " + stage2 + " when button is " + rightButtonMode3);
            } else if (rightButtonMode == RightButtonMode.Ok) {
                if (stage == Stage.HelpScreen) {
                    this.privacyPasswordLockPatternView.clearPattern();
                    this.privacyPasswordLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    updateStage(Stage.Introduction);
                    return;
                }
                throw new IllegalStateException("Help screen is only mode with ok button, but stage is " + this.mUiStage);
            }
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean z = false;
        boolean z2 = (getResources().getConfiguration().uiMode & 8192) != 0;
        this.mCheckOnPcMode = z2;
        if (z2) {
            setContentView(R.layout.privacy_choose_access_control);
        } else {
            setContentView(R.layout.privacy_choose_access_control_cetus);
        }
        getWindow().addFlags(8192);
        if (!PrivacyPasswordUtils.isPad()) {
            setRequestedOrientation(1);
        }
        this.mLockHelper = new ChooseLockSettingsHelper(this, 3);
        this.mPrivacyIconContainer = (LinearLayout) findViewById(R.id.privacy_password_icon_container);
        this.mPrivacyPasswordSettingTitle = (TextView) findViewById(R.id.privacy_password_setting);
        this.privacyChooseAccessControlBack = (TextView) findViewById(R.id.privacy_password_choose_access_control_back);
        this.privacyChooseAccessControlBackTitle = (TextView) findViewById(R.id.privacy_password_choose_access_control_back_text);
        this.bigTitle = (TextView) findViewById(R.id.big_title);
        this.mPrivacyPasswordHeaderText = (TextView) findViewById(R.id.privacy_passwordheaderText);
        this.mSplitMaskView = (RelativeLayout) findViewById(R.id.split_screen_layout);
        String stringExtra = getIntent().getStringExtra("privacy_password_extra_data");
        if (stringExtra != null && stringExtra.equals("ModifyPassword")) {
            z = true;
        }
        this.mIsModifyPassword = z;
        if (z) {
            this.mPrivacyPasswordSettingTitle.setVisibility(8);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mPrivacyPasswordHeaderText.getLayoutParams();
            layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.back_button_margin_start);
            this.mPrivacyPasswordHeaderText.setLayoutParams(layoutParams);
            TextView textView = this.privacyChooseAccessControlBackTitle;
            int i = R.string.modify_privacy_password;
            textView.setText(i);
            TextView textView2 = this.bigTitle;
            if (textView2 != null) {
                textView2.setText(i);
            }
        } else {
            AnalyticHelper.statsSet1PageAccount(XiaomiAccountUtils.isLoginXiaomiAccount(this) ? "logged_in" : "not_logged");
        }
        this.privacyChooseAccessControlBackTitle.setSelected(true);
        this.mPrivacyPasswordIconView = (ImageView) findViewById(R.id.privacy_choose_icon);
        this.mRelative = findViewById(R.id.choose_relative_view);
        com.android.settings.LockPatternView lockPatternView = (com.android.settings.LockPatternView) findViewById(R.id.choose__lockPattern);
        this.privacyPasswordLockPatternView = lockPatternView;
        lockPatternView.setOnPatternListener(this.mChooseNewLockPatternListener);
        this.privacyPasswordLockPatternView.setTactileFeedbackEnabled(this.mLockHelper.utils().isTactileFeedbackEnabled());
        this.privacyChooseAccessControlBack.setOnClickListener(this);
        this.privacyChooseAccessControlBack.setContentDescription(getResources().getString(R.string.setup_password_back));
        if (PrivacyPasswordUtils.isNotch()) {
            PrivacyPasswordUtils.adapteNotch(this, this.mRelative);
        }
        this.mFooterLeftButton = (TextView) findViewById(R.id.footerLeftButton);
        this.mFooterRightButton = (TextView) findViewById(R.id.footerRightButton);
        this.mFooterLeftButton.setOnClickListener(this);
        this.mFooterRightButton.setOnClickListener(this);
        findViewById(R.id.topLayout).setDefaultTouchRecepient(this.privacyPasswordLockPatternView);
        onCreateNoSavedState();
        this.mWindowManager = (WindowManager) getSystemService("window");
        this.mFrameLockPattern = (FrameLayout) findViewById(R.id.pattern_layout);
        this.mButtonLayout = findViewById(R.id.button_layout);
        if (bundle != null) {
            updateStage(Stage.values()[bundle.getInt("stage")]);
            if (bundle.getString("pattern") != null) {
                this.mChosenPattern = LockPatternUtilsCompat.stringToPattern(this.mLockHelper.utils(), bundle.getString("pattern"));
            }
        }
        if (this.mCheckOnPcMode) {
            updateResoureForPCMode();
        }
    }

    protected void onCreateNoSavedState() {
        updateStage(Stage.Introduction);
    }

    @Override // android.app.Activity
    public void onMultiWindowModeChanged(boolean z, Configuration configuration) {
        super.onMultiWindowModeChanged(z, configuration);
        if (Build.VERSION.SDK_INT >= 24 && PrivacyPasswordUtils.getCurrentWindowMode(configuration) == 1) {
            recreate();
        }
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        if (!isRealInMultiWindow() || this.mCheckOnPcMode) {
            this.mSplitMaskView.setVisibility(8);
        } else {
            this.mIsInMultiWindow = true;
            this.mSplitMaskView.setVisibility(0);
        }
        handleSpecialDevice();
    }

    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("stage", this.mUiStage.ordinal());
        if (this.mChosenPattern != null) {
            bundle.putString("pattern", LockPatternUtilsCompat.patternToString(this.mLockHelper.utils(), this.mChosenPattern));
        }
    }

    protected void saveChosenPatternAndFinish() {
        if (this.mChosenPattern == null) {
            onCreateNoSavedState();
            return;
        }
        MiuiLockPatternUtils utils = this.mLockHelper.utils();
        int myUserId = UserHandle.myUserId();
        utils.saveMiuiLockPatternAsUser(this.mChosenPattern, myUserId);
        if (this.mIsModifyPassword) {
            this.mLockHelper.setPrivacyPasswordEnabledAsUser(true, myUserId);
            setResult(-1);
            finish();
        } else {
            startAddAccountActiity();
        }
        PrivacyPasswordManager.getInstance(this).setLockoutAttepmpDeadline(0L);
    }

    protected void updateStage(Stage stage) {
        this.mUiStage = stage;
        if (stage == Stage.ChoiceTooShort) {
            this.mPrivacyPasswordHeaderText.setText(getResources().getString(stage.headerMessage, 4));
        } else {
            this.mPrivacyPasswordHeaderText.setText(stage.headerMessage);
        }
        if (stage.leftMode == LeftButtonMode.Gone) {
            this.mFooterLeftButton.setVisibility(8);
        } else {
            this.mFooterLeftButton.setVisibility(0);
            this.mFooterLeftButton.setText(stage.leftMode.text);
            this.mFooterLeftButton.setEnabled(stage.leftMode.enabled);
        }
        if (stage.rightMode == RightButtonMode.Gone) {
            this.mFooterRightButton.setVisibility(8);
        } else {
            this.mFooterRightButton.setVisibility(0);
            this.mFooterRightButton.setText(stage.rightMode.text);
            this.mFooterRightButton.setEnabled(stage.rightMode.enabled);
        }
        if (stage.patternEnabled) {
            this.privacyPasswordLockPatternView.enableInput();
        } else {
            this.privacyPasswordLockPatternView.disableInput();
        }
        handleSplitModel();
        this.privacyPasswordLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
        switch (AnonymousClass4.$SwitchMap$com$android$settings$privacypassword$PrivacyPasswordChooseAccessControl$Stage[this.mUiStage.ordinal()]) {
            case 1:
            case 2:
                this.privacyPasswordLockPatternView.clearPattern();
                return;
            case 3:
                this.privacyPasswordLockPatternView.setPattern(LockPatternView.DisplayMode.Animate, this.mAnimatePattern);
                return;
            case 4:
            case 5:
                this.privacyPasswordLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                postClearPatternRunnable();
                return;
            case 6:
                this.privacyPasswordLockPatternView.postDelayed(new Runnable() { // from class: com.android.settings.privacypassword.PrivacyPasswordChooseAccessControl.3
                    @Override // java.lang.Runnable
                    public void run() {
                        PrivacyPasswordChooseAccessControl.this.updateStage(Stage.NeedToConfirm);
                    }
                }, 500L);
                return;
            default:
                return;
        }
    }
}
