package miuix.internal.view;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import miuix.appcompat.R$style;
import miuix.appcompat.R$styleable;
import miuix.internal.view.CheckWidgetAnimatedStateListDrawable;

/* loaded from: classes5.dex */
public class CheckBoxAnimatedStateListDrawable extends CheckWidgetAnimatedStateListDrawable {
    private CheckWidgetDrawableAnims mCheckWidgetDrawableAnims;
    private float mContentAlpha;
    private boolean mIsEnabled;
    private boolean mPreChecked;
    private boolean mPrePressed;
    private float mScale;

    /* loaded from: classes5.dex */
    protected static class CheckBoxConstantState extends CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState {
        protected CheckBoxConstantState() {
        }

        @Override // miuix.internal.view.CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState
        protected Drawable newAnimatedStateListDrawable(Resources resources, Resources.Theme theme, CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState checkWidgetConstantState) {
            return new CheckBoxAnimatedStateListDrawable(resources, theme, checkWidgetConstantState);
        }
    }

    public CheckBoxAnimatedStateListDrawable() {
        this.mScale = 1.0f;
        this.mContentAlpha = 1.0f;
        this.mPrePressed = false;
        this.mPreChecked = false;
    }

    public CheckBoxAnimatedStateListDrawable(Resources resources, Resources.Theme theme, CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState checkWidgetConstantState) {
        super(resources, theme, checkWidgetConstantState);
        this.mScale = 1.0f;
        this.mContentAlpha = 1.0f;
        this.mPrePressed = false;
        this.mPreChecked = false;
        this.mCheckWidgetDrawableAnims = new CheckWidgetDrawableAnims(this, isSingleSelectionWidget(), checkWidgetConstantState.grayColor, checkWidgetConstantState.blackColor, checkWidgetConstantState.backGroundColor, checkWidgetConstantState.backgroundNormalAlpha, checkWidgetConstantState.backgroundDisableAlpha, checkWidgetConstantState.strokeColor, checkWidgetConstantState.strokeNormalAlpha, checkWidgetConstantState.strokeDisableAlpha);
    }

    private boolean safeGetBoolean(TypedArray typedArray, int i, boolean z) {
        try {
            return typedArray.getBoolean(i, z);
        } catch (Exception e) {
            Log.w("MiuixCheckbox", "try catch Exception insafeGetBoolean", e);
            return z;
        }
    }

    private int safeGetColor(TypedArray typedArray, int i, int i2) {
        try {
            return typedArray.getColor(i, i2);
        } catch (UnsupportedOperationException e) {
            Log.w("MiuixCheckbox", "try catch UnsupportedOperationException insafeGetColor", e);
            return i2;
        }
    }

    private int safeGetInt(TypedArray typedArray, int i, int i2) {
        try {
            return typedArray.getInt(i, i2);
        } catch (Exception e) {
            Log.w("MiuixCheckbox", "try catch Exception insafeGetInt", e);
            return i2;
        }
    }

    @Override // android.graphics.drawable.AnimatedStateListDrawable, android.graphics.drawable.StateListDrawable, android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme theme) {
        super.applyTheme(theme);
        TypedArray obtainStyledAttributes = theme.obtainStyledAttributes(getCheckWidgetDrawableStyle(), R$styleable.CheckWidgetDrawable);
        this.mCheckWidgetConstantState.grayColor = safeGetColor(obtainStyledAttributes, R$styleable.CheckWidgetDrawable_checkwidget_disableBackgroundColor, 0);
        this.mCheckWidgetConstantState.blackColor = safeGetColor(obtainStyledAttributes, R$styleable.CheckWidgetDrawable_checkwidget_checkOnAlphaBackgroundColor, 0);
        this.mCheckWidgetConstantState.backGroundColor = safeGetColor(obtainStyledAttributes, R$styleable.CheckWidgetDrawable_checkwidget_checkOnBackgroundColor, 0);
        this.mCheckWidgetConstantState.strokeColor = safeGetColor(obtainStyledAttributes, R$styleable.CheckWidgetDrawable_checkwidget_strokeColor, 0);
        this.mCheckWidgetConstantState.backgroundNormalAlpha = safeGetInt(obtainStyledAttributes, R$styleable.CheckWidgetDrawable_checkwidget_backgroundNormalAlpha, 0);
        this.mCheckWidgetConstantState.backgroundDisableAlpha = safeGetInt(obtainStyledAttributes, R$styleable.CheckWidgetDrawable_checkwidget_backgroundDisableAlpha, 0);
        this.mCheckWidgetConstantState.strokeNormalAlpha = safeGetInt(obtainStyledAttributes, R$styleable.CheckWidgetDrawable_checkwidget_strokeNormalAlpha, 0);
        this.mCheckWidgetConstantState.strokeDisableAlpha = safeGetInt(obtainStyledAttributes, R$styleable.CheckWidgetDrawable_checkwidget_strokeDisableAlpha, 0);
        this.mCheckWidgetConstantState.touchAnimEnable = safeGetBoolean(obtainStyledAttributes, R$styleable.CheckWidgetDrawable_checkwidget_touchAnimEnable, false);
        obtainStyledAttributes.recycle();
        boolean isSingleSelectionWidget = isSingleSelectionWidget();
        CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState checkWidgetConstantState = this.mCheckWidgetConstantState;
        this.mCheckWidgetDrawableAnims = new CheckWidgetDrawableAnims(this, isSingleSelectionWidget, checkWidgetConstantState.grayColor, checkWidgetConstantState.blackColor, checkWidgetConstantState.backGroundColor, checkWidgetConstantState.backgroundNormalAlpha, checkWidgetConstantState.backgroundDisableAlpha, checkWidgetConstantState.strokeColor, checkWidgetConstantState.strokeNormalAlpha, checkWidgetConstantState.strokeDisableAlpha);
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Drawable current = getCurrent();
        if (current != null && (current instanceof BitmapDrawable)) {
            super.draw(canvas);
        } else if (!this.mCheckWidgetConstantState.touchAnimEnable) {
            CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
            if (checkWidgetDrawableAnims != null) {
                checkWidgetDrawableAnims.draw(canvas);
            }
            super.draw(canvas);
        } else {
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 2));
            if (this.mIsEnabled) {
                CheckWidgetDrawableAnims checkWidgetDrawableAnims2 = this.mCheckWidgetDrawableAnims;
                if (checkWidgetDrawableAnims2 != null) {
                    checkWidgetDrawableAnims2.draw(canvas);
                }
                setAlpha((int) (this.mContentAlpha * 255.0f));
            } else {
                setAlpha(76);
            }
            canvas.save();
            Rect bounds = getBounds();
            float f = this.mScale;
            canvas.scale(f, f, (bounds.left + bounds.right) / 2, (bounds.top + bounds.bottom) / 2);
            super.draw(canvas);
            canvas.restore();
        }
    }

    protected int getCheckWidgetDrawableStyle() {
        return R$style.CheckWidgetDrawable_CheckBox;
    }

    public float getContentAlpha() {
        return this.mContentAlpha;
    }

    public float getScale() {
        return this.mScale;
    }

    protected boolean isSingleSelectionWidget() {
        return false;
    }

    @Override // miuix.internal.view.CheckWidgetAnimatedStateListDrawable
    protected CheckWidgetAnimatedStateListDrawable.CheckWidgetConstantState newCheckWidgetConstantState() {
        return new CheckBoxConstantState();
    }

    @Override // android.graphics.drawable.AnimatedStateListDrawable, android.graphics.drawable.StateListDrawable, android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean onStateChange = super.onStateChange(iArr);
        if (this.mCheckWidgetDrawableAnims == null) {
            return onStateChange;
        }
        Drawable current = getCurrent();
        if (current == null || !(current instanceof BitmapDrawable)) {
            this.mIsEnabled = false;
            boolean z = false;
            boolean z2 = false;
            for (int i : iArr) {
                if (i == 16842919) {
                    z = true;
                } else if (i == 16842912) {
                    z2 = true;
                } else if (i == 16842910) {
                    this.mIsEnabled = true;
                }
            }
            if (z) {
                startPressedAnim(z2);
            }
            if (!this.mPrePressed && !z) {
                verifyChecked(z2, this.mIsEnabled);
            }
            if (!z && (this.mPrePressed || z2 != this.mPreChecked)) {
                startUnPressedAnim(z2);
            }
            this.mPrePressed = z;
            this.mPreChecked = z2;
            return onStateChange;
        }
        return super.onStateChange(iArr);
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        setCheckWidgetDrawableBounds(i, i2, i3, i4);
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(Rect rect) {
        super.setBounds(rect);
        setCheckWidgetDrawableBounds(rect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCheckWidgetDrawableBounds(int i, int i2, int i3, int i4) {
        CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
        if (checkWidgetDrawableAnims != null) {
            checkWidgetDrawableAnims.setBounds(i, i2, i3, i4);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setCheckWidgetDrawableBounds(Rect rect) {
        CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
        if (checkWidgetDrawableAnims != null) {
            checkWidgetDrawableAnims.setBounds(rect);
        }
    }

    public void setContentAlpha(float f) {
        this.mContentAlpha = f;
    }

    public void setScale(float f) {
        this.mScale = f;
    }

    protected void startPressedAnim(boolean z) {
        CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
        if (checkWidgetDrawableAnims != null) {
            checkWidgetDrawableAnims.startPressedAnim(z, this.mCheckWidgetConstantState.touchAnimEnable);
        }
    }

    protected void startUnPressedAnim(boolean z) {
        CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
        if (checkWidgetDrawableAnims != null) {
            checkWidgetDrawableAnims.startUnPressedAnim(z, this.mCheckWidgetConstantState.touchAnimEnable);
        }
    }

    protected void verifyChecked(boolean z, boolean z2) {
        CheckWidgetDrawableAnims checkWidgetDrawableAnims = this.mCheckWidgetDrawableAnims;
        if (checkWidgetDrawableAnims != null) {
            checkWidgetDrawableAnims.verifyChecked(z, z2);
            invalidateSelf();
        }
    }
}
