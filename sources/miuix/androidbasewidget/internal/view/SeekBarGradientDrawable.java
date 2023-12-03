package miuix.androidbasewidget.internal.view;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

/* loaded from: classes5.dex */
public class SeekBarGradientDrawable extends GradientDrawable {
    private int mHeight;
    protected SeekBarGradientState mSeekBarGradientState;
    private int mWidth;

    /* loaded from: classes5.dex */
    protected static class SeekBarGradientState extends Drawable.ConstantState {
        Drawable.ConstantState mParent;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            return this.mParent.canApplyTheme();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mParent.getChangingConfigurations();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            if (this.mParent == null) {
                return null;
            }
            return newSeekBarGradientDrawable(null, null, this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            if (this.mParent == null) {
                return null;
            }
            return newSeekBarGradientDrawable(resources, null, this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources, Resources.Theme theme) {
            if (this.mParent == null) {
                return null;
            }
            return newSeekBarGradientDrawable(resources, theme, this);
        }

        protected Drawable newSeekBarGradientDrawable(Resources resources, Resources.Theme theme, SeekBarGradientState seekBarGradientState) {
            throw null;
        }

        public void setConstantState(Drawable.ConstantState constantState) {
            this.mParent = constantState;
        }
    }

    public SeekBarGradientDrawable() {
        this.mWidth = -1;
        this.mHeight = -1;
        SeekBarGradientState newSeekBarGradientState = newSeekBarGradientState();
        this.mSeekBarGradientState = newSeekBarGradientState;
        newSeekBarGradientState.setConstantState(super.getConstantState());
    }

    public SeekBarGradientDrawable(Resources resources, Resources.Theme theme, SeekBarGradientState seekBarGradientState) {
        this.mWidth = -1;
        this.mHeight = -1;
        Drawable newDrawable = resources == null ? seekBarGradientState.mParent.newDrawable() : theme == null ? seekBarGradientState.mParent.newDrawable(resources) : seekBarGradientState.mParent.newDrawable(resources, theme);
        seekBarGradientState.mParent = newDrawable.getConstantState();
        SeekBarGradientState newSeekBarGradientState = newSeekBarGradientState();
        this.mSeekBarGradientState = newSeekBarGradientState;
        newSeekBarGradientState.setConstantState(seekBarGradientState.mParent);
        this.mWidth = newDrawable.getIntrinsicWidth();
        this.mHeight = newDrawable.getIntrinsicHeight();
        if (newDrawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) newDrawable;
            setCornerRadius(gradientDrawable.getCornerRadius());
            setShape(gradientDrawable.getShape());
            setColor(gradientDrawable.getColor());
        }
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mSeekBarGradientState;
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mHeight;
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mWidth;
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    protected SeekBarGradientState newSeekBarGradientState() {
        throw null;
    }

    @Override // android.graphics.drawable.GradientDrawable, android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean onStateChange = super.onStateChange(iArr);
        boolean z = false;
        for (int i : iArr) {
            if (i == 16842919) {
                z = true;
            }
        }
        if (z) {
            startPressedAnim();
        }
        if (!z) {
            startUnPressedAnim();
        }
        return onStateChange;
    }

    protected void startPressedAnim() {
        throw null;
    }

    protected void startUnPressedAnim() {
        throw null;
    }
}
