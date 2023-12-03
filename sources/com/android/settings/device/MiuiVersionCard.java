package com.android.settings.device;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.report.InternationalCompat;
import com.android.settings.widget.BaseSettingsCard;
import com.miui.maml.animation.interpolater.CubicEaseOutInterpolater;

/* loaded from: classes.dex */
public class MiuiVersionCard extends BaseSettingsCard implements View.OnClickListener {
    private AnimatorSet mAnimatorSet;
    private DashboardFragment mFragment;
    private ImageView mIconImageView;
    private CubicEaseOutInterpolater mInterpolater;
    private boolean mNeedStartAnim;
    private boolean mNeedUpdate;
    private ViewGroup mUpdateHintLayout;
    private ImageView mUpdaterRightValue;
    private ViewGroup mVersionLayout;

    /* loaded from: classes.dex */
    public static class CustomImageSpan extends ImageSpan {
        private int ALIGN_CENTER;

        public CustomImageSpan(Drawable drawable, int i) {
            super(drawable, i);
            this.ALIGN_CENTER = 2;
        }

        @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int i6 = i5 - drawable.getBounds().bottom;
            int i7 = ((ImageSpan) this).mVerticalAlignment;
            if (i7 == 1) {
                i6 -= fontMetricsInt.descent;
            } else if (i7 == this.ALIGN_CENTER) {
                i6 = (((fontMetricsInt.descent + i4) + (i4 + fontMetricsInt.ascent)) / 2) - (drawable.getBounds().bottom / 2);
            }
            canvas.translate(f, i6);
            drawable.draw(canvas);
            canvas.restore();
        }

        @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            Rect bounds = getDrawable().getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fontMetricsInt2 = paint.getFontMetricsInt();
                int i3 = fontMetricsInt2.bottom - fontMetricsInt2.top;
                int i4 = (bounds.bottom - bounds.top) / 2;
                int i5 = i3 / 4;
                int i6 = i4 - i5;
                int i7 = -(i4 + i5);
                fontMetricsInt.ascent = i7;
                fontMetricsInt.top = i7;
                fontMetricsInt.bottom = i6;
                fontMetricsInt.descent = i6;
            }
            return bounds.right;
        }
    }

    public MiuiVersionCard(Context context) {
        super(context);
        this.mNeedStartAnim = true;
        this.mNeedUpdate = true;
        initView();
    }

    public MiuiVersionCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mNeedStartAnim = true;
        this.mNeedUpdate = true;
        initView();
    }

    private void initView() {
        addLayout(R.layout.miui_version_card);
        int i = R.id.miui_logo_view;
        ((ImageView) findViewById(i)).setImageResource(R.drawable.miui_version_logo);
        TextView textView = (TextView) findViewById(R.id.miui_version_text);
        String miuiVersionInCard = MiuiAboutPhoneUtils.getMiuiVersionInCard(this.mContext, true, true);
        if (!TextUtils.isEmpty(miuiVersionInCard)) {
            textView.setText(miuiVersionInCard);
        }
        this.mUpdaterRightValue = (ImageView) findViewById(R.id.red_point);
        Drawable drawable = getResources().getDrawable(R.drawable.account_unlogin_tip);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        this.mUpdaterRightValue.setImageDrawable(drawable);
        this.mIconImageView = (ImageView) findViewById(i);
        this.mVersionLayout = (ViewGroup) findViewById(R.id.version_layout);
        this.mUpdateHintLayout = (ViewGroup) findViewById(R.id.update_hint);
        this.mAnimatorSet = new AnimatorSet();
        this.mInterpolater = new CubicEaseOutInterpolater();
        setOnClickListener(this);
        this.mNeedUpdate = !TextUtils.isEmpty(MiuiAboutPhoneUtils.getUpdateInfo(this.mContext)) && (UserHandle.myUserId() == 0);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mNeedStartAnim && this.mNeedUpdate) {
            this.mNeedStartAnim = false;
            this.mIconImageView.setPivotX(0.0f);
            this.mAnimatorSet.playTogether(ObjectAnimator.ofFloat(this.mIconImageView, "translationY", -20.0f), ObjectAnimator.ofFloat(this.mIconImageView, "scaleX", 1.0f, 0.9f), ObjectAnimator.ofFloat(this.mIconImageView, "scaleY", 1.0f, 0.9f), ObjectAnimator.ofFloat(this.mVersionLayout, "translationY", -40.0f), ObjectAnimator.ofFloat(this.mUpdateHintLayout, "alpha", 0.0f, 1.0f), ObjectAnimator.ofFloat(this.mUpdateHintLayout, "translationY", -70.0f));
            this.mAnimatorSet.setInterpolator(this.mInterpolater);
            this.mAnimatorSet.setDuration(500L).start();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        DashboardFragment dashboardFragment;
        if (UserHandle.myUserId() != 0 || (dashboardFragment = this.mFragment) == null) {
            return;
        }
        MiuiAboutPhoneUtils.startUpdater(dashboardFragment.getActivity());
        InternationalCompat.trackReportEvent("setting_About_phone_update");
    }

    public void refreshUpdateStatus() {
        boolean z = false;
        boolean z2 = UserHandle.myUserId() == 0;
        String updateInfo = MiuiAboutPhoneUtils.getUpdateInfo(this.mContext);
        if ((!TextUtils.isEmpty(updateInfo) && z2) != this.mNeedUpdate) {
            this.mNeedStartAnim = true;
            if (!TextUtils.isEmpty(updateInfo) && z2) {
                z = true;
            }
            this.mNeedUpdate = z;
            this.mRootView.removeAllViews();
            initView();
            invalidate();
        }
    }

    public void setFragment(DashboardFragment dashboardFragment) {
        this.mFragment = dashboardFragment;
    }
}
