package miuix.preference;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;

/* loaded from: classes5.dex */
public class ConnectPreferenceHelper {
    private static final int[] STATE_ATTR_CONNECTED;
    private static final int[] STATE_ATTR_DISCONNECTED;
    private LayerDrawable BgDrawableParent;
    private Drawable bgDrawableConnected;
    private AnimatedVectorDrawable connectingAnimDrawable;
    private ColorStateList iconColorList;
    private Context mContext;
    private ValueAnimator mDisConnectedToConnectedBgAnim;
    private ValueAnimator mDisConnectedToConnectedIconAnim;
    private ValueAnimator mDisConnectedToConnectedSummaryAnim;
    private ValueAnimator mDisConnectedToConnectedTitleAnim;
    private Preference mPreference;
    private TextView mSummaryView;
    private TextView mTitleView;
    private View mWidgetView;
    private ColorStateList summaryColorList;
    private ColorStateList titleColorList;
    private int mState = 0;
    private int mLastState = 0;
    private boolean mIconAnimEnabled = true;

    static {
        int i = R$attr.state_connected;
        STATE_ATTR_CONNECTED = new int[]{i};
        STATE_ATTR_DISCONNECTED = new int[]{-i};
    }

    public ConnectPreferenceHelper(Context context, Preference preference) {
        this.mContext = context;
        this.mPreference = preference;
        this.titleColorList = ContextCompat.getColorStateList(context, R$color.miuix_preference_connect_title_color);
        this.summaryColorList = ContextCompat.getColorStateList(context, R$color.miuix_preference_connect_summary_color);
        this.iconColorList = ContextCompat.getColorStateList(context, R$color.miuix_preference_connect_icon_color);
        initAnim(context);
    }

    private void initAnim(Context context) {
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(context, R$drawable.miuix_preference_ic_bg_connect);
        this.BgDrawableParent = layerDrawable;
        if (layerDrawable != null && Build.VERSION.SDK_INT >= 21) {
            this.connectingAnimDrawable = (AnimatedVectorDrawable) layerDrawable.findDrawableByLayerId(R$id.anim_preference_connecting);
            this.bgDrawableConnected = this.BgDrawableParent.findDrawableByLayerId(R$id.shape_preference_connected);
            ColorStateList colorStateList = this.titleColorList;
            int[] iArr = STATE_ATTR_DISCONNECTED;
            int colorForState = colorStateList.getColorForState(iArr, R$color.miuix_preference_connect_title_disconnected_color);
            ColorStateList colorStateList2 = this.titleColorList;
            int[] iArr2 = STATE_ATTR_CONNECTED;
            int colorForState2 = colorStateList2.getColorForState(iArr2, R$color.miuix_preference_connect_title_connected_color);
            int colorForState3 = this.summaryColorList.getColorForState(iArr, R$color.miuix_preference_connect_summary_disconnected_color);
            int colorForState4 = this.summaryColorList.getColorForState(iArr2, R$color.miuix_preference_connect_summary_connected_color);
            ValueAnimator ofArgb = ValueAnimator.ofArgb(this.iconColorList.getColorForState(iArr, R$color.miuix_preference_connect_icon_disconnected_color), this.iconColorList.getColorForState(iArr2, R$color.miuix_preference_connect_icon_connected_color));
            this.mDisConnectedToConnectedIconAnim = ofArgb;
            ofArgb.setDuration(300L);
            this.mDisConnectedToConnectedIconAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.preference.ConnectPreferenceHelper.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    Drawable icon = ConnectPreferenceHelper.this.mPreference.getIcon();
                    if (icon == null || !ConnectPreferenceHelper.this.mIconAnimEnabled) {
                        return;
                    }
                    DrawableCompat.setTint(icon, ((Integer) valueAnimator.getAnimatedValue()).intValue());
                }
            });
            ValueAnimator ofArgb2 = ValueAnimator.ofArgb(colorForState, colorForState2);
            this.mDisConnectedToConnectedTitleAnim = ofArgb2;
            ofArgb2.setDuration(300L);
            this.mDisConnectedToConnectedTitleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.preference.ConnectPreferenceHelper.2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (ConnectPreferenceHelper.this.mTitleView != null) {
                        ConnectPreferenceHelper.this.mTitleView.setTextColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
                    }
                }
            });
            ValueAnimator ofArgb3 = ValueAnimator.ofArgb(colorForState3, colorForState4);
            this.mDisConnectedToConnectedSummaryAnim = ofArgb3;
            ofArgb3.setDuration(300L);
            this.mDisConnectedToConnectedSummaryAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.preference.ConnectPreferenceHelper.3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (ConnectPreferenceHelper.this.mSummaryView != null) {
                        ConnectPreferenceHelper.this.mSummaryView.setTextColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
                    }
                }
            });
            ValueAnimator ofInt = ValueAnimator.ofInt(0, 255);
            this.mDisConnectedToConnectedBgAnim = ofInt;
            ofInt.setDuration(300L);
            this.mDisConnectedToConnectedBgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miuix.preference.ConnectPreferenceHelper.4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ConnectPreferenceHelper.this.bgDrawableConnected.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
                }
            });
            this.mDisConnectedToConnectedBgAnim.addListener(new Animator.AnimatorListener() { // from class: miuix.preference.ConnectPreferenceHelper.5
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (ConnectPreferenceHelper.this.connectingAnimDrawable == null || !ConnectPreferenceHelper.this.connectingAnimDrawable.isRunning()) {
                        return;
                    }
                    ConnectPreferenceHelper.this.connectingAnimDrawable.stop();
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animator) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                }
            });
        }
    }

    private static void setAlphaFolme(View view) {
        if (view == null) {
            return;
        }
        Folme.useAt(view).touch().setAlpha(0.6f, ITouchStyle.TouchType.DOWN).handleTouchOf(view, new AnimConfig[0]);
    }

    private void startConnectedToDisConnectedAnim() {
        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator(1.5f);
        if (this.mDisConnectedToConnectedBgAnim.isRunning()) {
            this.mDisConnectedToConnectedBgAnim.cancel();
        }
        this.mDisConnectedToConnectedBgAnim.setInterpolator(accelerateInterpolator);
        this.mDisConnectedToConnectedBgAnim.reverse();
        if (this.mDisConnectedToConnectedTitleAnim.isRunning()) {
            this.mDisConnectedToConnectedTitleAnim.cancel();
        }
        this.mDisConnectedToConnectedTitleAnim.setInterpolator(accelerateInterpolator);
        this.mDisConnectedToConnectedTitleAnim.reverse();
        if (this.mDisConnectedToConnectedSummaryAnim.isRunning()) {
            this.mDisConnectedToConnectedSummaryAnim.cancel();
        }
        this.mDisConnectedToConnectedSummaryAnim.setInterpolator(accelerateInterpolator);
        this.mDisConnectedToConnectedSummaryAnim.reverse();
        if (this.mDisConnectedToConnectedIconAnim.isRunning()) {
            this.mDisConnectedToConnectedIconAnim.cancel();
        }
        this.mDisConnectedToConnectedIconAnim.setInterpolator(accelerateInterpolator);
        this.mDisConnectedToConnectedIconAnim.reverse();
    }

    private void startDisConnectedToConnectedAnim() {
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(1.5f);
        if (this.mDisConnectedToConnectedBgAnim.isRunning()) {
            this.mDisConnectedToConnectedBgAnim.cancel();
        }
        this.mDisConnectedToConnectedBgAnim.setInterpolator(decelerateInterpolator);
        this.mDisConnectedToConnectedBgAnim.start();
        if (this.mDisConnectedToConnectedTitleAnim.isRunning()) {
            this.mDisConnectedToConnectedTitleAnim.cancel();
        }
        this.mDisConnectedToConnectedTitleAnim.setInterpolator(decelerateInterpolator);
        this.mDisConnectedToConnectedTitleAnim.start();
        if (this.mDisConnectedToConnectedSummaryAnim.isRunning()) {
            this.mDisConnectedToConnectedSummaryAnim.cancel();
        }
        this.mDisConnectedToConnectedSummaryAnim.setInterpolator(decelerateInterpolator);
        this.mDisConnectedToConnectedSummaryAnim.start();
        if (this.mDisConnectedToConnectedIconAnim.isRunning()) {
            this.mDisConnectedToConnectedIconAnim.cancel();
        }
        this.mDisConnectedToConnectedIconAnim.setInterpolator(decelerateInterpolator);
        this.mDisConnectedToConnectedIconAnim.start();
    }

    private void updateState(boolean z) {
        int i = this.mState;
        if (i == 0) {
            updateStateDisconnected(z);
        } else if (i == 1) {
            updateStateConnected(z);
        } else if (i != 2) {
        } else {
            updateStateConnecting(z);
        }
    }

    private void updateStateConnected(boolean z) {
        if (z) {
            startDisConnectedToConnectedAnim();
        } else {
            this.bgDrawableConnected.setAlpha(255);
            updateViewColorList(STATE_ATTR_CONNECTED);
        }
        updateWidgetDrawable(STATE_ATTR_CONNECTED);
    }

    private void updateStateConnecting(boolean z) {
        this.bgDrawableConnected.setAlpha(0);
        AnimatedVectorDrawable animatedVectorDrawable = this.connectingAnimDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.setAlpha(255);
            if (!this.connectingAnimDrawable.isRunning()) {
                this.connectingAnimDrawable.start();
            }
        }
        if (!z) {
            updateViewColorList(STATE_ATTR_DISCONNECTED);
        }
        updateWidgetDrawable(STATE_ATTR_DISCONNECTED);
    }

    private void updateStateDisconnected(boolean z) {
        AnimatedVectorDrawable animatedVectorDrawable;
        if (z) {
            int i = this.mLastState;
            if (i == 1) {
                startConnectedToDisConnectedAnim();
            } else if (i == 2 && (animatedVectorDrawable = this.connectingAnimDrawable) != null && animatedVectorDrawable.isRunning()) {
                this.connectingAnimDrawable.stop();
            }
        } else {
            this.bgDrawableConnected.setAlpha(0);
            updateViewColorList(STATE_ATTR_DISCONNECTED);
        }
        AnimatedVectorDrawable animatedVectorDrawable2 = this.connectingAnimDrawable;
        if (animatedVectorDrawable2 != null) {
            animatedVectorDrawable2.setAlpha(0);
        }
        updateWidgetDrawable(STATE_ATTR_DISCONNECTED);
    }

    private void updateViewColorList(int[] iArr) {
        Drawable icon = this.mPreference.getIcon();
        if (icon != null && this.mIconAnimEnabled) {
            DrawableCompat.setTint(icon, this.iconColorList.getColorForState(iArr, R$color.miuix_preference_connect_icon_disconnected_color));
        }
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setTextColor(this.titleColorList.getColorForState(iArr, R$color.miuix_preference_connect_title_disconnected_color));
        }
        TextView textView2 = this.mSummaryView;
        if (textView2 != null) {
            textView2.setTextColor(this.summaryColorList.getColorForState(iArr, R$color.miuix_preference_connect_summary_disconnected_color));
        }
    }

    private void updateWidgetDrawable(int[] iArr) {
        View view = this.mWidgetView;
        if (view instanceof ImageView) {
            if (iArr == STATE_ATTR_CONNECTED) {
                ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(this.mContext, R$drawable.miuix_preference_ic_detail_connected));
                return;
            }
            TypedValue typedValue = new TypedValue();
            this.mContext.getTheme().resolveAttribute(R$attr.connectDetailDisconnectedDrawable, typedValue, true);
            ((ImageView) this.mWidgetView).setImageDrawable(ContextCompat.getDrawable(this.mContext, typedValue.resourceId));
        }
    }

    public int getConnectState() {
        return this.mState;
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder, View view) {
        if (view == null || preferenceViewHolder == null) {
            return;
        }
        view.setBackground(this.BgDrawableParent);
        preferenceViewHolder.itemView.setBackground(null);
        this.mTitleView = (TextView) preferenceViewHolder.findViewById(16908310);
        this.mSummaryView = (TextView) preferenceViewHolder.findViewById(16908304);
        View findViewById = preferenceViewHolder.findViewById(R$id.preference_detail);
        this.mWidgetView = findViewById;
        setAlphaFolme(findViewById);
        updateState(false);
    }

    public void setConnectState(int i) {
        this.mLastState = this.mState;
        this.mState = i;
        updateState(true);
    }

    public void setIconAnimEnabled(boolean z) {
        this.mIconAnimEnabled = z;
    }
}
