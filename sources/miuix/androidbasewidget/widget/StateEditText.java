package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import miuix.androidbasewidget.R$attr;
import miuix.androidbasewidget.R$style;
import miuix.androidbasewidget.R$styleable;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.base.AnimConfig;
import miuix.internal.util.ViewUtils;

/* loaded from: classes5.dex */
public class StateEditText extends EditText {
    private static final Class<?>[] WIDGET_MANAGER_CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class};
    private Drawable[] mExtraDrawables;
    private String mLabel;
    private StaticLayout mLabelLayout;
    private int mLabelLength;
    private int mLabelMaxWidth;
    private boolean mPressed;
    private WidgetManager mWidgetManager;
    private int mWidgetPadding;

    /* loaded from: classes5.dex */
    public static abstract class WidgetManager {
        public WidgetManager(Context context, AttributeSet attributeSet) {
        }

        protected abstract Drawable[] getWidgetDrawables();

        protected void onAttached(StateEditText stateEditText) {
        }

        protected void onDetached() {
        }

        protected abstract void onWidgetClick(int i);
    }

    public StateEditText(Context context) {
        this(context, null);
    }

    public StateEditText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.miuixAppcompatStateEditTextStyle);
    }

    public StateEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLabelLayout = null;
        initView(context, attributeSet, i);
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(this, new AnimConfig[0]);
    }

    private void createLabelLayout() {
        if (Build.VERSION.SDK_INT < 23) {
            this.mLabelLayout = new StaticLayout(this.mLabel, getPaint(), this.mLabelLength, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            return;
        }
        String str = this.mLabel;
        this.mLabelLayout = StaticLayout.Builder.obtain(str, 0, str.length(), getPaint(), this.mLabelLength).build();
    }

    private WidgetManager createWidgetManager(Context context, String str, AttributeSet attributeSet) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            Constructor constructor = context.getClassLoader().loadClass(str).asSubclass(WidgetManager.class).getConstructor(WIDGET_MANAGER_CONSTRUCTOR_SIGNATURE);
            Object[] objArr = {context, attributeSet};
            constructor.setAccessible(true);
            return (WidgetManager) constructor.newInstance(objArr);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Can't find WidgetManager: " + str, e);
        } catch (IllegalAccessException e2) {
            throw new IllegalStateException("Can't access non-public constructor " + str, e2);
        } catch (InstantiationException e3) {
            throw new IllegalStateException("Could not instantiate the WidgetManager: " + str, e3);
        } catch (NoSuchMethodException e4) {
            throw new IllegalStateException("Error creating WidgetManager " + str, e4);
        } catch (InvocationTargetException e5) {
            throw new IllegalStateException("Could not instantiate the WidgetManager: " + str, e5);
        }
    }

    private boolean dispatchEndDrawableTouchEvent(MotionEvent motionEvent) {
        if (this.mWidgetManager != null) {
            return isWidgetResumedEvent(motionEvent);
        }
        return false;
    }

    private void drawExtraWidget(Canvas canvas) {
        if (this.mExtraDrawables == null) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        int scrollX = getScrollX();
        int paddingEnd = getPaddingEnd();
        Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();
        int i = 0;
        int intrinsicWidth = compoundDrawablesRelative[2] == null ? 0 : compoundDrawablesRelative[2].getIntrinsicWidth() + this.mWidgetPadding;
        int i2 = height / 2;
        int i3 = 0;
        while (true) {
            Drawable[] drawableArr = this.mExtraDrawables;
            if (i >= drawableArr.length) {
                return;
            }
            int intrinsicWidth2 = drawableArr[i].getIntrinsicWidth();
            int intrinsicHeight = this.mExtraDrawables[i].getIntrinsicHeight();
            if (ViewUtils.isLayoutRtl(this)) {
                int i4 = scrollX + paddingEnd + intrinsicWidth;
                int i5 = intrinsicHeight / 2;
                this.mExtraDrawables[i].setBounds(i4 + i3, i2 - i5, i4 + intrinsicWidth2 + i3, i5 + i2);
            } else {
                int i6 = ((scrollX + width) - paddingEnd) - intrinsicWidth;
                int i7 = intrinsicHeight / 2;
                this.mExtraDrawables[i].setBounds((i6 - intrinsicWidth2) - i3, i2 - i7, i6 - i3, i7 + i2);
            }
            i3 = this.mWidgetPadding + intrinsicWidth2;
            this.mExtraDrawables[i].draw(canvas);
            i++;
        }
    }

    private void drawLabel(Canvas canvas) {
        if (TextUtils.isEmpty(this.mLabel) || this.mLabelLayout == null) {
            return;
        }
        int color = getPaint().getColor();
        getPaint().setColor(getCurrentTextColor());
        int paddingStart = getPaddingStart();
        Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();
        int i = 0;
        if (compoundDrawablesRelative[0] != null) {
            i = this.mWidgetPadding + compoundDrawablesRelative[0].getIntrinsicWidth();
        }
        float max = Math.max(0.0f, (getMeasuredHeight() - this.mLabelLayout.getHeight()) / 2.0f);
        canvas.save();
        if (ViewUtils.isLayoutRtl(this)) {
            canvas.translate((((getScrollX() + getWidth()) - i) - this.mLabelLength) - paddingStart, max);
        } else {
            canvas.translate(paddingStart + getScrollX() + i, max);
        }
        this.mLabelLayout.draw(canvas);
        canvas.restore();
        getPaint().setColor(color);
    }

    private int getLabelLength() {
        int i = this.mLabelLength;
        return i + (i == 0 ? 0 : this.mWidgetPadding);
    }

    private int getWidgetLength() {
        Drawable[] drawableArr = this.mExtraDrawables;
        if (drawableArr != null) {
            int i = 0;
            for (Drawable drawable : drawableArr) {
                i = i + drawable.getIntrinsicWidth() + this.mWidgetPadding;
            }
            return i;
        }
        return 0;
    }

    private void initView(Context context, AttributeSet attributeSet, int i) {
        String str;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.miuixAppcompatStateEditText, i, R$style.Widget_StateEditText_DayNight);
            str = obtainStyledAttributes.getString(R$styleable.miuixAppcompatStateEditText_miuixAppcompatWidgetManager);
            this.mLabel = obtainStyledAttributes.getString(R$styleable.miuixAppcompatStateEditText_miuixAppcompatLabel);
            this.mLabelMaxWidth = obtainStyledAttributes.getDimensionPixelSize(R$styleable.miuixAppcompatStateEditText_miuixAppcompatLabelMaxWidth, 0);
            this.mWidgetPadding = obtainStyledAttributes.getDimensionPixelSize(R$styleable.miuixAppcompatStateEditText_miuixAppcompatWidgetPadding, 0);
            obtainStyledAttributes.recycle();
        } else {
            str = null;
        }
        setWidgetManager(createWidgetManager(context, str, attributeSet));
        this.mExtraDrawables = null;
        WidgetManager widgetManager = this.mWidgetManager;
        if (widgetManager != null) {
            this.mExtraDrawables = widgetManager.getWidgetDrawables();
        }
        setLabel(this.mLabel);
    }

    private boolean isWidgetResumedEvent(MotionEvent motionEvent) {
        if (this.mExtraDrawables != null) {
            int scrollX = getScrollX();
            int i = 0;
            while (true) {
                Drawable[] drawableArr = this.mExtraDrawables;
                if (i >= drawableArr.length) {
                    break;
                }
                Rect bounds = drawableArr[i].getBounds();
                if (motionEvent.getX() < bounds.right - scrollX && motionEvent.getX() > bounds.left - scrollX) {
                    return onWidgetTouchEvent(motionEvent, i);
                }
                i++;
            }
        }
        this.mPressed = false;
        return false;
    }

    private boolean onWidgetTouchEvent(MotionEvent motionEvent, int i) {
        WidgetManager widgetManager;
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mPressed = true;
        } else if (action != 1) {
            if (action == 3 && this.mPressed) {
                this.mPressed = false;
            }
        } else if (this.mPressed && (widgetManager = this.mWidgetManager) != null) {
            widgetManager.onWidgetClick(i);
            this.mPressed = false;
            return true;
        }
        return this.mPressed;
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return dispatchEndDrawableTouchEvent(motionEvent) || super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.widget.TextView
    public int getCompoundPaddingLeft() {
        return super.getCompoundPaddingLeft() + (ViewUtils.isLayoutRtl(this) ? getWidgetLength() : getLabelLength());
    }

    @Override // android.widget.TextView
    public int getCompoundPaddingRight() {
        return super.getCompoundPaddingRight() + (ViewUtils.isLayoutRtl(this) ? getLabelLength() : getWidgetLength());
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawExtraWidget(canvas);
        drawLabel(canvas);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (TextUtils.isEmpty(this.mLabel) || this.mLabelLayout == null) {
            return;
        }
        if (this.mLabelMaxWidth == 0 && this.mLabelLength > getMeasuredWidth() / 2) {
            this.mLabelLength = getMeasuredWidth() / 2;
            createLabelLayout();
        }
        int height = this.mLabelLayout.getHeight() + getPaddingTop() + getPaddingBottom();
        if (height > getMeasuredHeight()) {
            setMeasuredDimension(getMeasuredWidth(), height);
        }
    }

    @Override // android.widget.TextView
    public void setInputType(int i) {
        Typeface typeface = getTypeface();
        super.setInputType(i);
        setTypeface(typeface);
    }

    public void setLabel(String str) {
        this.mLabel = str;
        if (this.mLabelMaxWidth > 0) {
            this.mLabelLength = TextUtils.isEmpty(str) ? 0 : Math.min((int) getPaint().measureText(this.mLabel), this.mLabelMaxWidth);
        } else {
            this.mLabelLength = TextUtils.isEmpty(str) ? 0 : (int) getPaint().measureText(this.mLabel);
        }
        if (!TextUtils.isEmpty(this.mLabel)) {
            createLabelLayout();
        }
        invalidate();
    }

    public void setWidgetManager(WidgetManager widgetManager) {
        WidgetManager widgetManager2 = this.mWidgetManager;
        if (widgetManager2 != null) {
            widgetManager2.onDetached();
        }
        this.mWidgetManager = widgetManager;
        if (widgetManager != null) {
            widgetManager.onAttached(this);
        }
    }
}
