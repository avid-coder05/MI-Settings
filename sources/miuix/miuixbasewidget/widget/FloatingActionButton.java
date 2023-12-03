package miuix.miuixbasewidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import java.lang.ref.WeakReference;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;
import miuix.miuixbasewidget.R$color;
import miuix.miuixbasewidget.R$drawable;
import miuix.miuixbasewidget.R$style;
import miuix.miuixbasewidget.R$styleable;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes5.dex */
public class FloatingActionButton extends ImageView {
    private Drawable mDefaultBackground;
    private EmptyHolder mEmptyHolder;
    private int mFabColor;
    private boolean mHasFabColor;
    private final boolean mIsShadowEnabled;
    private final int mShadowRadius;
    private final int mShadowXOffset;
    private final int mShadowYOffset;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class EmptyHolder extends Drawable {
        private Drawable mDrawable;
        private Paint mPaint = new Paint();

        EmptyHolder(Drawable drawable) {
            this.mDrawable = drawable;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int width = FloatingActionButton.this.getWidth();
            int paddingLeft = FloatingActionButton.this.getPaddingLeft();
            int paddingTop = FloatingActionButton.this.getPaddingTop();
            int paddingRight = (((width - paddingLeft) - FloatingActionButton.this.getPaddingRight()) / 2) * 2;
            this.mDrawable.setBounds(paddingLeft, paddingTop, paddingLeft + paddingRight, paddingRight + paddingTop);
            this.mDrawable.draw(canvas);
        }

        @Override // android.graphics.drawable.Drawable
        public Drawable.ConstantState getConstantState() {
            return this.mDrawable.getConstantState();
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return this.mDrawable.getOpacity();
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            this.mDrawable.setAlpha(i);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            this.mDrawable.setColorFilter(colorFilter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class OvalShapeWithPadding extends OvalShape {
        private WeakReference<View> mViewRef;

        public OvalShapeWithPadding(View view) {
            this.mViewRef = new WeakReference<>(view);
        }

        @Override // android.graphics.drawable.shapes.OvalShape, android.graphics.drawable.shapes.RectShape, android.graphics.drawable.shapes.Shape
        public void draw(Canvas canvas, Paint paint) {
            View view = this.mViewRef.get();
            if (view != null) {
                int width = view.getWidth();
                int paddingLeft = view.getPaddingLeft();
                int paddingTop = view.getPaddingTop();
                canvas.drawCircle(paddingLeft + r0, paddingTop + r0, ((width - paddingLeft) - view.getPaddingRight()) / 2, paint);
            }
        }
    }

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        float f = getContext().getResources().getDisplayMetrics().density;
        this.mShadowXOffset = (int) (0.0f * f);
        int i2 = (int) (f * 5.45f);
        this.mShadowYOffset = i2;
        this.mShadowRadius = i2;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.FloatingActionButton, i, R$style.Widget_FloatingActionButton);
        this.mIsShadowEnabled = obtainStyledAttributes.getBoolean(R$styleable.FloatingActionButton_fabShadowEnabled, true);
        int i3 = R$styleable.FloatingActionButton_fabColor;
        this.mHasFabColor = obtainStyledAttributes.hasValue(i3);
        this.mFabColor = obtainStyledAttributes.getColor(i3, context.getResources().getColor(R$color.miuix_appcompat_fab_color_light));
        obtainStyledAttributes.recycle();
        this.mEmptyHolder = new EmptyHolder(getContext().getResources().getDrawable(R$drawable.miuix_appcompat_fab_empty_holder));
        initBackground();
        updatePadding();
        Folme.useAt(this).touch().setTint(0).handleTouchOf(this, new AnimConfig[0]);
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.FLOATED_WRAPPED).handleHoverOf(this, new AnimConfig[0]);
    }

    private int computeShadowColor(int i) {
        return Color.argb(25, Color.red(i), Math.max(0, Color.green(i) - 30), Color.blue(i));
    }

    private Drawable createFabBackground() {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShapeWithPadding(this));
        if (this.mIsShadowEnabled) {
            int computeShadowColor = computeShadowColor(this.mFabColor);
            setLayerType(Build.VERSION.SDK_INT >= 28 ? 2 : 1, shapeDrawable.getPaint());
            shapeDrawable.getPaint().setShadowLayer(this.mShadowRadius, this.mShadowXOffset, this.mShadowYOffset, computeShadowColor);
        }
        shapeDrawable.getPaint().setColor(this.mFabColor);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{shapeDrawable, this.mEmptyHolder});
        ShapeDrawable shapeDrawable2 = new ShapeDrawable(new OvalShapeWithPadding(this));
        shapeDrawable2.getPaint().setColor(218103808);
        LayerDrawable layerDrawable2 = new LayerDrawable(new Drawable[]{layerDrawable, shapeDrawable2});
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(ImageView.PRESSED_ENABLED_STATE_SET, layerDrawable2);
        stateListDrawable.addState(ImageView.ENABLED_SELECTED_STATE_SET, layerDrawable2);
        stateListDrawable.addState(ImageView.EMPTY_STATE_SET, layerDrawable);
        return stateListDrawable;
    }

    private Drawable getDefaultBackground() {
        if (this.mDefaultBackground == null) {
            this.mFabColor = getContext().getResources().getColor(R$color.miuix_appcompat_fab_color_light);
            this.mHasFabColor = true;
            this.mDefaultBackground = createFabBackground();
        }
        return this.mDefaultBackground;
    }

    private void initBackground() {
        if (getBackground() != null) {
            this.mHasFabColor = false;
        } else if (this.mHasFabColor) {
            super.setBackground(createFabBackground());
        } else {
            super.setBackground(getDefaultBackground());
        }
    }

    private void initEmptyHolder() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int width = (((getWidth() - paddingLeft) - getPaddingRight()) / 2) * 2;
        this.mEmptyHolder.setBounds(paddingLeft, paddingTop, paddingLeft + width, width + paddingTop);
    }

    private void updatePadding() {
        if (this.mIsShadowEnabled && this.mHasFabColor) {
            setPadding(Math.max(0, this.mShadowRadius - this.mShadowXOffset), Math.max(0, this.mShadowRadius - this.mShadowYOffset), Math.max(0, this.mShadowRadius + this.mShadowXOffset), Math.max(0, this.mShadowRadius + this.mShadowYOffset));
        } else {
            setPadding(0, 0, 0, 0);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        initEmptyHolder();
        super.onDraw(canvas);
    }

    @Override // android.view.View
    public boolean performClick() {
        HapticCompat.performHapticFeedback(this, HapticFeedbackConstants.MIUI_TAP_LIGHT);
        return super.performClick();
    }

    @Override // android.view.View
    public void setBackground(Drawable drawable) {
        this.mHasFabColor = false;
        if (drawable == null) {
            drawable = getDefaultBackground();
        }
        super.setBackground(drawable);
        updatePadding();
    }

    @Override // android.view.View
    public void setBackgroundColor(int i) {
        if (!this.mHasFabColor || this.mFabColor != i) {
            this.mFabColor = i;
            super.setBackground(createFabBackground());
            updatePadding();
        }
        this.mHasFabColor = true;
    }

    @Override // android.view.View
    public void setBackgroundResource(int i) {
        this.mHasFabColor = false;
        if (i == 0) {
            super.setBackground(getDefaultBackground());
        } else {
            super.setBackgroundResource(i);
        }
        updatePadding();
    }
}
