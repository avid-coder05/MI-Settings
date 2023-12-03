package miuix.provision;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import miuix.appcompat.app.AppCompatActivity;
import miuix.provision.ProvisionAnimHelper;

/* loaded from: classes5.dex */
public class ProvisionBaseActivity extends AppCompatActivity implements ProvisionAnimHelper.AnimListener {
    private static float HALF_ALPHA = 0.5f;
    private static float NO_ALPHA = 1.0f;
    protected TextView mBackBtn;
    private boolean mDeviceProvisioned;
    protected ImageButton mGlobalBackBtn;
    protected ImageButton mGlobalNextBtn;
    private boolean mHasPreview;
    protected ImageView mImageView;
    private boolean mIsCompatibleMode;
    protected TextView mNextBtn;
    protected ProvisionAnimHelper mProvisionAnimHelper;
    protected TextView mSkipBtn;
    protected TextView mSubTitle;
    protected TextView mTitle;
    protected View mTitleLayout;
    private View mTitleSpace;
    private AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationListener;
    private int windowInsetTopHeight;
    private View.OnClickListener mNextClickListener = new View.OnClickListener() { // from class: miuix.provision.ProvisionBaseActivity.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (OobeUtil.isTabletLand(ProvisionBaseActivity.this)) {
                ProvisionBaseActivity.this.onNextAminStart();
                return;
            }
            if (OobeUtil.needFastAnimation()) {
                ProvisionBaseActivity.this.updateButtonState(false);
                ProvisionBaseActivity.this.mH.postDelayed(new Runnable() { // from class: miuix.provision.ProvisionBaseActivity.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ProvisionBaseActivity.this.updateButtonState(true);
                    }
                }, 5000L);
            } else if (!ProvisionBaseActivity.this.isOtherAnimEnd()) {
                Log.w("OobeUtil2", "other anim not end");
                return;
            } else if (!ProvisionBaseActivity.this.isAnimEnded()) {
                Log.w("OobeUtil2", "video anim not end");
                return;
            }
            Log.d("OobeUtil2", "begin start OOBSETTINGS");
            ProvisionBaseActivity provisionBaseActivity = ProvisionBaseActivity.this;
            ProvisionAnimHelper provisionAnimHelper = provisionBaseActivity.mProvisionAnimHelper;
            if (provisionAnimHelper != null) {
                provisionAnimHelper.setAnimY(provisionBaseActivity.getTitleLayoutHeight());
                ProvisionBaseActivity.this.mProvisionAnimHelper.goNextStep(0);
            }
        }
    };
    private View.OnClickListener mSkipClickListener = new View.OnClickListener() { // from class: miuix.provision.ProvisionBaseActivity.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (OobeUtil.isTabletLand(ProvisionBaseActivity.this)) {
                ProvisionBaseActivity.this.onSkipAminStart();
                return;
            }
            if (OobeUtil.needFastAnimation()) {
                ProvisionBaseActivity.this.updateButtonState(false);
                ProvisionBaseActivity.this.mH.postDelayed(new Runnable() { // from class: miuix.provision.ProvisionBaseActivity.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ProvisionBaseActivity.this.updateButtonState(true);
                    }
                }, 5000L);
            } else if (!ProvisionBaseActivity.this.isOtherAnimEnd()) {
                Log.w("OobeUtil2", "other anim not end");
                return;
            } else if (!ProvisionBaseActivity.this.isAnimEnded()) {
                Log.w("OobeUtil2", "video anim not end");
                return;
            }
            Log.d("OobeUtil2", "begin start OOBSETTINGS");
            ProvisionBaseActivity provisionBaseActivity = ProvisionBaseActivity.this;
            ProvisionAnimHelper provisionAnimHelper = provisionBaseActivity.mProvisionAnimHelper;
            if (provisionAnimHelper != null) {
                provisionAnimHelper.setAnimY(provisionBaseActivity.getTitleLayoutHeight());
                ProvisionBaseActivity.this.mProvisionAnimHelper.goNextStep(1);
            }
        }
    };
    private View.OnClickListener mBackListener = new View.OnClickListener() { // from class: miuix.provision.ProvisionBaseActivity.3
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (OobeUtil.isTabletLand(ProvisionBaseActivity.this)) {
                ProvisionBaseActivity.this.onBackAnimStart();
                return;
            }
            if (OobeUtil.needFastAnimation()) {
                ProvisionBaseActivity.this.updateButtonState(false);
                ProvisionBaseActivity.this.mH.postDelayed(new Runnable() { // from class: miuix.provision.ProvisionBaseActivity.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ProvisionBaseActivity.this.updateButtonState(true);
                    }
                }, 5000L);
            } else if (!ProvisionBaseActivity.this.isOtherAnimEnd()) {
                Log.w("OobeUtil2", "other anim not end");
                return;
            }
            Log.d("OobeUtil2", "begin start OOBSETTINGS");
            ProvisionBaseActivity provisionBaseActivity = ProvisionBaseActivity.this;
            ProvisionAnimHelper provisionAnimHelper = provisionBaseActivity.mProvisionAnimHelper;
            if (provisionAnimHelper != null) {
                provisionAnimHelper.setAnimY(provisionBaseActivity.getTitleLayoutHeight());
                ProvisionBaseActivity.this.mProvisionAnimHelper.goBackStep();
            }
        }
    };
    private Handler mH = new Handler();

    private boolean needDelayBottomButton() {
        return (hasPreview() || OobeUtil.isInternationalBuild()) ? false : true;
    }

    private void registerAccessibiltyStateChange(Context context) {
        if (OobeUtil.isInternationalBuild() || context == null || this.mTouchExplorationListener != null) {
            return;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        AccessibilityManager.TouchExplorationStateChangeListener touchExplorationStateChangeListener = new AccessibilityManager.TouchExplorationStateChangeListener() { // from class: miuix.provision.ProvisionBaseActivity.6
            @Override // android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
            public void onTouchExplorationStateChanged(boolean z) {
                Log.i("ProvisionBaseActivity", "onTouchExplorationStateChanged enabled=" + z);
                if (z) {
                    OobeUtil.setNavigationBarFullScreen(ProvisionBaseActivity.this, false);
                    return;
                }
                OobeUtil.setNavigationBarFullScreen(ProvisionBaseActivity.this, true);
                if (Build.VERSION.SDK_INT <= 29) {
                    OobeUtil.setGestureHomeClose(ProvisionBaseActivity.this, true, true);
                }
            }
        };
        this.mTouchExplorationListener = touchExplorationStateChangeListener;
        accessibilityManager.addTouchExplorationStateChangeListener(touchExplorationStateChangeListener);
    }

    private void unRegisterAccessibiltyStateChange(Context context) {
        if (OobeUtil.isInternationalBuild() || context == null || this.mTouchExplorationListener == null) {
            return;
        }
        ((AccessibilityManager) context.getSystemService("accessibility")).removeTouchExplorationStateChangeListener(this.mTouchExplorationListener);
        this.mTouchExplorationListener = null;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (OobeUtil.needFastAnimation() || isAnimEnded()) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    protected int getTitleLayoutHeight() {
        View view = this.mTitleLayout;
        return view != null ? view.getHeight() - this.windowInsetTopHeight : getResources().getDimensionPixelSize(R$dimen.provision_actionbar_height) + getResources().getDimensionPixelSize(R$dimen.provision_padding_top) + getResources().getDimensionPixelSize(R$dimen.provision_container_margin_top);
    }

    public boolean hasNavigationButton() {
        return true;
    }

    public boolean hasPreview() {
        return !OobeUtil.isTabletLand(this);
    }

    public boolean hasSubTitle() {
        return !OobeUtil.isTabletLand(this);
    }

    public boolean hasTitle() {
        return true;
    }

    protected boolean isAnimEnded() {
        ProvisionAnimHelper provisionAnimHelper;
        if (this.mHasPreview && (provisionAnimHelper = this.mProvisionAnimHelper) != null) {
            return provisionAnimHelper.isAnimEnded();
        }
        return true;
    }

    protected boolean isOtherAnimEnd() {
        return true;
    }

    @Override // miuix.provision.ProvisionAnimHelper.AnimListener
    public void onAminEnd() {
        if (OobeUtil.needFastAnimation()) {
            return;
        }
        updateButtonState(true);
    }

    @Override // miuix.provision.ProvisionAnimHelper.AnimListener
    public void onAminServiceConnected() {
        if (OobeUtil.needFastAnimation() || isAnimEnded()) {
            return;
        }
        updateButtonState(false);
    }

    public void onBackAnimStart() {
        onBackButtonClick();
        onBackPressed();
    }

    protected void onBackButtonClick() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        this.mDeviceProvisioned = OobeUtil.isDeviceProvisioned(this);
        super.onCreate(bundle);
        if (this.mDeviceProvisioned || this.mIsCompatibleMode) {
            return;
        }
        setContentView(R$layout.provision_main_activity);
        this.mImageView = (ImageView) findViewById(R$id.provision_preview_img);
        this.mBackBtn = (TextView) findViewById(R$id.provision_back_btn);
        this.mNextBtn = (TextView) findViewById(R$id.provision_next_btn);
        this.mGlobalBackBtn = (ImageButton) findViewById(R$id.provision_global_back_btn);
        this.mGlobalNextBtn = (ImageButton) findViewById(R$id.provision_global_next_btn);
        this.mSkipBtn = (TextView) findViewById(R$id.provision_skip_btn);
        this.mSubTitle = (TextView) findViewById(R$id.provision_sub_title);
        this.mTitleSpace = findViewById(R$id.provision_title_space);
        this.mTitleLayout = findViewById(R$id.provision_lyt_title);
        this.mTitle = (TextView) findViewById(R$id.provision_title);
        if (OobeUtil.isTabletDevice()) {
            this.mTitle.setGravity(81);
        } else {
            this.mTitle.setGravity(17);
        }
        boolean hasPreview = hasPreview();
        this.mHasPreview = hasPreview;
        if (!hasPreview) {
            if (!OobeUtil.isTabletDevice()) {
                ViewGroup.LayoutParams layoutParams = this.mTitle.getLayoutParams();
                layoutParams.height = -2;
                this.mTitle.setLayoutParams(layoutParams);
                int paddingTop = this.mTitle.getPaddingTop();
                int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.provision_titlewithsub_add_padding);
                TextView textView = this.mTitle;
                textView.setPadding(textView.getPaddingLeft(), dimensionPixelSize + paddingTop, this.mTitle.getPaddingRight(), this.mTitle.getPaddingBottom());
            }
            if (hasSubTitle()) {
                this.mTitleSpace.setVisibility(0);
                TextView textView2 = this.mSubTitle;
                if (textView2 != null) {
                    textView2.setVisibility(0);
                }
            }
        }
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            OobeUtil.setHideNavigationBar(getWindow());
        }
        OobeUtil.updateViewVisibility(this.mBackBtn, this.mGlobalBackBtn);
        OobeUtil.updateViewVisibility(this.mNextBtn, this.mGlobalNextBtn);
        findViewById(R$id.provision_preview_layout).setVisibility(this.mHasPreview ? 0 : 8);
        findViewById(R$id.provision_lyt_btn).setVisibility(hasNavigationButton() ? 0 : 8);
        this.mTitleLayout.setVisibility(hasTitle() ? 0 : 8);
        if (Build.VERSION.SDK_INT >= 20) {
            final TitleLayoutHolder titleLayoutHolder = new TitleLayoutHolder(this.mTitleLayout, false);
            this.mTitleLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: miuix.provision.ProvisionBaseActivity.4
                @Override // android.view.View.OnApplyWindowInsetsListener
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    ProvisionBaseActivity.this.windowInsetTopHeight = windowInsets.getSystemWindowInsetTop();
                    if (!OobeUtil.isTabletLand(ProvisionBaseActivity.this)) {
                        TitleLayoutHolder.adjustPaddingTop(titleLayoutHolder, ProvisionBaseActivity.this.windowInsetTopHeight);
                    }
                    return windowInsets;
                }
            });
        }
        if (this.mHasPreview || OobeUtil.isTabletLand(this)) {
            this.mNextBtn.setOnClickListener(this.mNextClickListener);
            this.mBackBtn.setOnClickListener(this.mBackListener);
            this.mGlobalNextBtn.setOnClickListener(this.mNextClickListener);
            this.mGlobalBackBtn.setOnClickListener(this.mBackListener);
            this.mSkipBtn.setOnClickListener(this.mSkipClickListener);
        }
        if (!OobeUtil.isInternationalBuild()) {
            registerAccessibiltyStateChange(getApplicationContext());
        }
        if (OobeUtil.needFastAnimation() || needDelayBottomButton()) {
            updateButtonState(false);
            this.mH.postDelayed(new Runnable() { // from class: miuix.provision.ProvisionBaseActivity.5
                @Override // java.lang.Runnable
                public void run() {
                    ProvisionBaseActivity.this.updateButtonState(true);
                }
            }, 800L);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setImageDrawable(null);
        }
        if (OobeUtil.isInternationalBuild()) {
            return;
        }
        unRegisterAccessibiltyStateChange(getApplicationContext());
    }

    public void onNextAminStart() {
        onNextButtonClick();
    }

    protected void onNextButtonClick() {
    }

    @Override // miuix.provision.ProvisionAnimHelper.AnimListener
    public void onSkipAminStart() {
        onSkipButtonClick();
    }

    protected void onSkipButtonClick() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        if (!this.mHasPreview || this.mDeviceProvisioned || this.mIsCompatibleMode) {
            return;
        }
        ProvisionAnimHelper provisionAnimHelper = new ProvisionAnimHelper(this, this.mH);
        this.mProvisionAnimHelper = provisionAnimHelper;
        provisionAnimHelper.registerAnimService();
        this.mProvisionAnimHelper.setAnimListener(this);
        this.mProvisionAnimHelper.setAnimY(getTitleLayoutHeight());
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        ProvisionAnimHelper provisionAnimHelper = this.mProvisionAnimHelper;
        if (provisionAnimHelper == null || !this.mHasPreview || this.mDeviceProvisioned || this.mIsCompatibleMode) {
            return;
        }
        provisionAnimHelper.unregisterAnimService();
        this.mProvisionAnimHelper = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPreviewView(Drawable drawable) {
        ImageView imageView = this.mImageView;
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    public void setSubTitle(int i) {
        setSubTitle(getText(i));
    }

    public void setSubTitle(CharSequence charSequence) {
        TextView textView = this.mSubTitle;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    @Override // android.app.Activity
    public void setTitle(int i) {
        super.setTitle(i);
        TextView textView = this.mTitle;
        if (textView != null) {
            textView.setText(getTitle());
        }
    }

    @Override // android.app.Activity
    public void setTitle(CharSequence charSequence) {
        super.setTitle(charSequence);
        TextView textView = this.mTitle;
        if (textView != null) {
            textView.setText(getTitle());
        }
    }

    public void updateButtonState(boolean z) {
        TextView textView;
        if (OobeUtil.isTabletLand(this) || (textView = this.mNextBtn) == null || this.mBackBtn == null || this.mGlobalNextBtn == null || this.mGlobalBackBtn == null || this.mSkipBtn == null) {
            return;
        }
        textView.setAlpha(z ? NO_ALPHA : HALF_ALPHA);
        this.mBackBtn.setAlpha(z ? NO_ALPHA : HALF_ALPHA);
        this.mGlobalNextBtn.setAlpha(z ? NO_ALPHA : HALF_ALPHA);
        this.mGlobalBackBtn.setAlpha(z ? NO_ALPHA : HALF_ALPHA);
        this.mSkipBtn.setAlpha(z ? NO_ALPHA : HALF_ALPHA);
        if (OobeUtil.needFastAnimation() || needDelayBottomButton()) {
            this.mNextBtn.setEnabled(z);
            this.mBackBtn.setEnabled(z);
            this.mGlobalNextBtn.setEnabled(z);
            this.mGlobalBackBtn.setEnabled(z);
            this.mSkipBtn.setEnabled(z);
        }
    }
}
