package com.android.settings.privacypassword;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.security.ChooseLockSettingsHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.R;
import com.android.settings.privacypassword.analytics.AnalyticHelper;
import com.xiaomi.accountsdk.account.IXiaomiAccountService;
import java.lang.reflect.Method;
import miui.accounts.ExtraAccountManager;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class AddAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private Account mAccount;
    private ImageView mAccountIcon;
    private TextView mAccountInfo;
    private TextView mAccountName;
    private TextView mAccountTitleContent;
    private TextView mBackText;
    private TextView mBigAccountTitleContent;
    private int mEnterWay;
    private boolean mIsLoginAccount;
    private Button mLeftButton;
    private PrivacyPasswordManager mPasswordManager;
    private Button mRightButton;
    private RelativeLayout mSplitMaskView;
    private View topView;
    private boolean mResultIsOk = false;
    private boolean mIsStartModify = false;
    private boolean mIsCancelLogin = true;
    private boolean mIsStartedLogin = false;
    private boolean mCheckOnPcMode = false;
    private AccountManagerCallback<Bundle> mAccountCallback = new AccountManagerCallback<Bundle>() { // from class: com.android.settings.privacypassword.AddAccountActivity.1
        @Override // android.accounts.AccountManagerCallback
        public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
            AddAccountActivity addAccountActivity = AddAccountActivity.this;
            try {
                if (!accountManagerFuture.getResult().getBoolean("booleanResult")) {
                    AddAccountActivity.this.mResultIsOk = false;
                    AddAccountActivity.this.mIsCancelLogin = true;
                    AddAccountActivity.this.mPasswordManager.bindXiaoMiAccount(null);
                    return;
                }
                AddAccountActivity.this.mResultIsOk = true;
                AddAccountActivity.this.mIsCancelLogin = false;
                if (AddAccountActivity.this.mIsStartModify) {
                    addAccountActivity.startActivity(new Intent(addAccountActivity, ModifyAndInstructionPrivacyPassword.class));
                }
                AddAccountActivity.this.mPasswordManager.bindXiaoMiAccount(XiaomiAccountUtils.loginedXiaomiAccount(addAccountActivity).name);
                Toast.makeText(addAccountActivity, addAccountActivity.getResources().getString(R.string.bind_xiaomi_account_success), 1).show();
                AnalyticHelper.statsForgetPageBindingResult(AddAccountActivity.this.getAnalyticBindingResultKey(), "not_logged_binding");
                AddAccountActivity.this.finish();
            } catch (Exception e) {
                Log.e("AddAccountActivity", "fail loginXiaomiAccount", e);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class AccountServiceConnection implements ServiceConnection {
        private IXiaomiAccountService mMiAccountService;

        private AccountServiceConnection() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:41:0x008c A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Type inference failed for: r1v2, types: [android.graphics.drawable.Drawable] */
        /* JADX WARN: Type inference failed for: r1v3 */
        @Override // android.content.ServiceConnection
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onServiceConnected(android.content.ComponentName r11, android.os.IBinder r12) {
            /*
                r10 = this;
                java.lang.String r11 = "close file error"
                java.lang.String r0 = "AddAccountActivity"
                com.xiaomi.accountsdk.account.IXiaomiAccountService r12 = com.xiaomi.accountsdk.account.IXiaomiAccountService.Stub.asInterface(r12)
                r10.mMiAccountService = r12
                com.android.settings.privacypassword.AddAccountActivity r12 = com.android.settings.privacypassword.AddAccountActivity.this
                android.widget.ImageView r12 = com.android.settings.privacypassword.AddAccountActivity.access$600(r12)
                com.android.settings.privacypassword.AddAccountActivity r1 = com.android.settings.privacypassword.AddAccountActivity.this
                android.content.res.Resources r1 = r1.getResources()
                int r2 = com.android.settings.R.drawable.ic_head
                android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
                r12.setImageDrawable(r1)
                r12 = 0
                com.android.settings.privacypassword.AddAccountActivity r1 = com.android.settings.privacypassword.AddAccountActivity.this     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L79
                android.accounts.Account r1 = com.android.settings.privacypassword.AddAccountActivity.access$700(r1)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L79
                if (r1 == 0) goto L63
                com.xiaomi.accountsdk.account.IXiaomiAccountService r1 = r10.mMiAccountService     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L79
                com.android.settings.privacypassword.AddAccountActivity r2 = com.android.settings.privacypassword.AddAccountActivity.this     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L79
                android.accounts.Account r2 = com.android.settings.privacypassword.AddAccountActivity.access$700(r2)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L79
                android.os.ParcelFileDescriptor r1 = r1.getAvatarFd(r2)     // Catch: java.lang.Throwable -> L76 java.lang.Exception -> L79
                if (r1 == 0) goto L64
                java.io.FileDescriptor r2 = r1.getFileDescriptor()     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L89
                if (r2 == 0) goto L64
                android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeFileDescriptor(r2)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L89
                if (r4 == 0) goto L64
                int r5 = r4.getWidth()     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L89
                int r6 = r4.getHeight()     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L89
                int r2 = r4.getWidth()     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L89
                int r7 = r2 / 2
                r8 = -1
                r9 = 1
                r3 = r10
                android.graphics.Bitmap r2 = r3.toRoundCorner(r4, r5, r6, r7, r8, r9)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L89
                com.android.settings.privacypassword.AddAccountActivity r3 = com.android.settings.privacypassword.AddAccountActivity.this     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L89
                android.widget.ImageView r3 = com.android.settings.privacypassword.AddAccountActivity.access$600(r3)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L89
                r3.setImageBitmap(r2)     // Catch: java.lang.Exception -> L61 java.lang.Throwable -> L89
                goto L64
            L61:
                r2 = move-exception
                goto L7b
            L63:
                r1 = r12
            L64:
                if (r1 == 0) goto L6e
                r1.close()     // Catch: java.lang.Exception -> L6a
                goto L6e
            L6a:
                r1 = move-exception
            L6b:
                android.util.Log.e(r0, r11, r1)
            L6e:
                com.android.settings.privacypassword.AddAccountActivity r11 = com.android.settings.privacypassword.AddAccountActivity.this
                r11.unbindService(r10)
                r10.mMiAccountService = r12
                goto L88
            L76:
                r2 = move-exception
                r1 = r12
                goto L8a
            L79:
                r2 = move-exception
                r1 = r12
            L7b:
                java.lang.String r3 = "Fail getAvatarFd"
                android.util.Log.e(r0, r3, r2)     // Catch: java.lang.Throwable -> L89
                if (r1 == 0) goto L6e
                r1.close()     // Catch: java.lang.Exception -> L86
                goto L6e
            L86:
                r1 = move-exception
                goto L6b
            L88:
                return
            L89:
                r2 = move-exception
            L8a:
                if (r1 == 0) goto L94
                r1.close()     // Catch: java.lang.Exception -> L90
                goto L94
            L90:
                r1 = move-exception
                android.util.Log.e(r0, r11, r1)
            L94:
                com.android.settings.privacypassword.AddAccountActivity r11 = com.android.settings.privacypassword.AddAccountActivity.this
                r11.unbindService(r10)
                r10.mMiAccountService = r12
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.privacypassword.AddAccountActivity.AccountServiceConnection.onServiceConnected(android.content.ComponentName, android.os.IBinder):void");
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        public Bitmap toRoundCorner(Bitmap bitmap, int i, int i2, int i3, int i4, boolean z) {
            try {
                Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                Paint paint = new Paint();
                Rect rect = new Rect(0, 0, i, i2);
                RectF rectF = new RectF(rect);
                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(i4);
                float f = i3;
                canvas.drawRoundRect(rectF, f, f, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                paint.setFilterBitmap(true);
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), rect, paint);
                if (z) {
                    paint.setStrokeWidth(1.0f);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(-16777216);
                    paint.setAlpha(76);
                    canvas.drawCircle(i / 2, i2 / 2, bitmap.getWidth() / 2, paint);
                }
                bitmap.recycle();
                return createBitmap;
            } catch (OutOfMemoryError unused) {
                return null;
            }
        }
    }

    private void adaptSmallWindow() {
        if (isRealInMultiWindow()) {
            this.mSplitMaskView.setVisibility(0);
            this.topView.setVisibility(8);
            findViewById(R.id.pvc_content).setVisibility(8);
        }
    }

    private void addBackEvent() {
        if (this.mIsLoginAccount) {
            AnalyticHelper.statsForgetPageBindingResult(getAnalyticBindingResultKey(), "logged_in_back");
        } else if (this.mIsStartedLogin && this.mIsCancelLogin) {
            AnalyticHelper.statsForgetPageBindingResult(getAnalyticBindingResultKey(), "not_logged_cancel_login_back");
        } else {
            AnalyticHelper.statsForgetPageBindingResult(getAnalyticBindingResultKey(), "not_logged_back");
        }
    }

    private void addSkipEvent() {
        if (this.mIsLoginAccount) {
            AnalyticHelper.statsForgetPageBindingResult(getAnalyticBindingResultKey(), "logged_in_skip");
        } else if (this.mIsStartedLogin && this.mIsCancelLogin) {
            AnalyticHelper.statsForgetPageBindingResult(getAnalyticBindingResultKey(), "not_logged_cancel_login_skip");
        } else {
            AnalyticHelper.statsForgetPageBindingResult(getAnalyticBindingResultKey(), "not_logged_skip");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getAnalyticBindingResultKey() {
        return this.mEnterWay == 1 ? "binding_result" : "app_binding_result";
    }

    private void handleExternalScreen() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mAccountInfo.getLayoutParams();
        int i = R.dimen.px_160;
        layoutParams.setMarginStart(PrivacyPasswordUtils.getDimen(this, i));
        layoutParams.setMarginEnd(PrivacyPasswordUtils.getDimen(this, i));
        this.mAccountInfo.setLayoutParams(layoutParams);
        this.mAccountInfo.requestLayout();
        this.mAccountTitleContent.setVisibility(8);
        this.mBigAccountTitleContent.setVisibility(0);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mAccountIcon.getLayoutParams();
        layoutParams2.setMargins(0, PrivacyPasswordUtils.getDimen(this, R.dimen.px_369), 0, 0);
        this.mAccountIcon.setLayoutParams(layoutParams2);
        this.topView.requestLayout();
    }

    private void handleSplitModel() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mLeftButton.getLayoutParams();
        int i = R.dimen.px_80;
        layoutParams.setMarginStart(PrivacyPasswordUtils.getDimen(this, i));
        this.mLeftButton.setLayoutParams(layoutParams);
        this.mLeftButton.requestLayout();
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mRightButton.getLayoutParams();
        layoutParams2.setMarginEnd(PrivacyPasswordUtils.getDimen(this, i));
        layoutParams2.setMarginStart(PrivacyPasswordUtils.getDimen(this, R.dimen.px_40));
        this.mRightButton.setLayoutParams(layoutParams2);
        this.mRightButton.requestLayout();
    }

    private void initViewData() {
        this.mIsStartModify = getIntent().getBooleanExtra("is_start_modify", false);
        this.mEnterWay = getIntent().getIntExtra("enter_forgetpage_way", 1);
        this.mPasswordManager = PrivacyPasswordManager.getInstance(this);
        this.mAccountIcon = (ImageView) findViewById(R.id.pvc_icon);
        this.mAccountName = (TextView) findViewById(R.id.pvc_account_text);
        this.mAccountInfo = (TextView) findViewById(R.id.pvc_add_account_info);
        this.mAccountTitleContent = (TextView) findViewById(R.id.pvc_add_account_title_content);
        this.mBigAccountTitleContent = (TextView) findViewById(R.id.big_title);
        Button button = (Button) findViewById(R.id.footerLeftButton);
        this.mLeftButton = button;
        button.setText(R.string.privacy_password_not_add_account);
        this.mRightButton = (Button) findViewById(R.id.footerRightButton);
        this.mBackText = (TextView) findViewById(R.id.pvc_add_account_title);
        this.topView = findViewById(R.id.pvc_add_account_top_layout);
        this.mAccount = XiaomiAccountUtils.loginedXiaomiAccount(this);
        this.mSplitMaskView = (RelativeLayout) findViewById(R.id.split_screen_layout);
        this.mIsLoginAccount = this.mAccount != null;
        AccountManager accountManager = (AccountManager) getSystemService("account");
        if (this.mIsLoginAccount) {
            String userData = accountManager.getUserData(this.mAccount, "acc_user_name");
            if (TextUtils.isEmpty(userData)) {
                this.mAccountName.setText(this.mAccount.name);
            } else {
                this.mAccountName.setText(userData);
            }
        } else {
            this.mAccountName.setText(R.string.privacy_password_not_login_account);
        }
        this.mRightButton.setText(R.string.privacy_password_add_account);
        this.mLeftButton.setOnClickListener(this);
        this.mRightButton.setOnClickListener(this);
        this.mBackText.setOnClickListener(this);
        this.mBackText.setContentDescription(getResources().getString(R.string.setup_password_back));
        setUserAvatar();
        AnalyticHelper.statsSet1ForgetPageAccount(this.mIsLoginAccount ? "logged_in" : "not_logged");
        if (PrivacyPasswordUtils.isNotch()) {
            PrivacyPasswordUtils.adapteNotch(this, findViewById(R.id.top_actionBar), R.dimen.back_button_alight_top, this.topView, R.dimen.top_account_actionBar);
        }
    }

    private boolean isRealInMultiWindow() {
        try {
            Method method = getClass().getMethod("isInMultiWindowMode", new Class[0]);
            Log.i("AddAccountActivity", "isRealInMultiWindow: " + ((Boolean) method.invoke(this, new Object[0])));
            return ((Boolean) method.invoke(this, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("AddAccountActivity", "isRealInMultiWindow", e);
            return false;
        }
    }

    private void loginXiaomiAccount(Activity activity) {
        this.mIsStartedLogin = true;
        XiaomiAccountUtils.loginAccount(activity, new Bundle(), this.mAccountCallback);
    }

    private void setUserAvatar() {
        Intent intent = new Intent("com.xiaomi.account.action.BIND_XIAOMI_ACCOUNT_SERVICE");
        intent.setPackage(ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
        bindService(intent, new AccountServiceConnection(), 1);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    public void finish() {
        setResult(this.mResultIsOk ? -1 : 0);
        super.finish();
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        addBackEvent();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        ChooseLockSettingsHelper chooseLockSettingsHelper = new ChooseLockSettingsHelper(this, 3);
        if (view == this.mLeftButton) {
            chooseLockSettingsHelper.setPrivacyPasswordEnabledAsUser(true, UserHandle.myUserId());
            this.mResultIsOk = true;
            if (this.mIsStartModify) {
                startActivity(new Intent(this, ModifyAndInstructionPrivacyPassword.class));
            }
            addSkipEvent();
            finish();
        } else if (view != this.mRightButton) {
            if (view == this.mBackText) {
                this.mResultIsOk = false;
                onBackPressed();
            }
        } else {
            chooseLockSettingsHelper.setPrivacyPasswordEnabledAsUser(true, UserHandle.myUserId());
            if (!this.mIsLoginAccount) {
                loginXiaomiAccount(this);
                return;
            }
            this.mResultIsOk = true;
            this.mPasswordManager.bindXiaoMiAccount(this.mAccount.name);
            if (this.mIsStartModify) {
                startActivity(new Intent(this, ModifyAndInstructionPrivacyPassword.class));
            }
            Toast.makeText(this, getResources().getString(R.string.bind_xiaomi_account_success), 1).show();
            AnalyticHelper.statsForgetPageBindingResult(getAnalyticBindingResultKey(), "logged_in_binding");
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean z = (getResources().getConfiguration().uiMode & 8192) != 0;
        this.mCheckOnPcMode = z;
        if (z) {
            setContentView(R.layout.add_account_setting);
        } else {
            setContentView(R.layout.add_account_setting_cetus);
        }
        initViewData();
    }

    @Override // android.app.Activity
    public void onMultiWindowModeChanged(boolean z, Configuration configuration) {
        super.onMultiWindowModeChanged(z, configuration);
        if (Build.VERSION.SDK_INT >= 24 && PrivacyPasswordUtils.getCurrentWindowMode(configuration) == 1) {
            recreate();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (this.mPasswordManager.getBindXiaoMiAccount() != null && XiaomiAccountUtils.isLoginXiaomiAccount(this) && TextUtils.equals(this.mPasswordManager.getBindXiaoMiAccount(), XiaomiAccountUtils.getLoginedAccountMd5(this))) {
            finish();
        }
        adaptSmallWindow();
        if (PrivacyPasswordUtils.isFoldInternalScreen(this) && getIntent().getMiuiFlags() == 4 && !this.mCheckOnPcMode) {
            handleSplitModel();
        } else if (PrivacyPasswordUtils.isFoldInternalScreen(this) || this.mCheckOnPcMode) {
        } else {
            handleExternalScreen();
        }
    }
}
