package miuix.visual.check;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import androidx.annotation.Keep;
import miuix.animation.Folme;
import miuix.animation.IFolme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.utils.EaseManager;
import miuix.visualcheck.R$drawable;
import miuix.visualcheck.R$styleable;

/* loaded from: classes5.dex */
public class BorderLayout extends LinearLayout implements VisualCheckItem {
    private IFolme iFolme;
    private Drawable mBackGround;
    @Keep
    private DrawableTarget mDrawableTarget;

    @Keep
    /* loaded from: classes5.dex */
    public static class DrawableTarget {
        private float alpha = 1.0f;
        private Drawable mImg;

        DrawableTarget(Drawable drawable) {
            this.mImg = drawable;
        }

        public float getAlpha() {
            return this.alpha;
        }

        public void setAlpha(float f) {
            this.alpha = f;
            this.mImg.setAlpha((int) (f * 255.0f));
        }
    }

    public BorderLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.iFolme = Folme.useAt(this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.BorderLayout);
        this.mBackGround = obtainStyledAttributes.getDrawable(R$styleable.BorderLayout_checkedBackGround);
        obtainStyledAttributes.recycle();
        if (this.mBackGround == null) {
            this.mBackGround = getResources().getDrawable(R$drawable.borderlayout_bg);
        }
        this.mDrawableTarget = new DrawableTarget(this.mBackGround);
        this.iFolme.hover().setEffect(IHoverStyle.HoverEffect.FLOATED).handleHoverOf(this, new AnimConfig[0]);
    }

    @Override // miuix.visual.check.VisualCheckItem
    public void onChecked(boolean z) {
        if (getBackground() == null) {
            setBackground(this.mBackGround);
            this.mBackGround.setAlpha(z ? 255 : 0);
        } else if (z) {
            Folme.useValue(this.mDrawableTarget).setFlags(1L).to("alpha", Float.valueOf(1.0f), EaseManager.getStyle(-2, 1.0f, 0.25f));
        } else {
            Folme.useValue(this.mDrawableTarget).setFlags(1L).to("alpha", Float.valueOf(0.0f), EaseManager.getStyle(-2, 1.0f, 0.25f));
        }
    }

    @Override // miuix.visual.check.VisualCheckItem
    public void onVisualCheckBoxTouchEvent(VisualCheckBox visualCheckBox, MotionEvent motionEvent) {
        DrawableTarget drawableTarget;
        IFolme iFolme = this.iFolme;
        if (iFolme != null) {
            iFolme.touch().onMotionEvent(motionEvent);
        }
        if (motionEvent.getAction() != 1 || this.mBackGround.getAlpha() == 255 || (drawableTarget = this.mDrawableTarget) == null) {
            return;
        }
        Folme.useValue(drawableTarget).setFlags(1L).to("alpha", Float.valueOf(1.0f), EaseManager.getStyle(-2, 1.0f, 0.25f));
    }
}
