package miuix.appcompat.internal.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import com.android.settings.search.SearchUpdater;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$styleable;
import miuix.core.util.WindowUtils;
import miuix.internal.util.AttributeResolver;

/* loaded from: classes5.dex */
public class DialogParentPanel2 extends LinearLayout {
    private final FloatingABOLayoutSpec mFloatingWindowSize;

    /* loaded from: classes5.dex */
    private static class FloatingABOLayoutSpec {
        private final Context mContext;
        private TypedValue mFixedHeightMajor;
        private TypedValue mFixedHeightMinor;
        private TypedValue mFixedWidthMajor;
        private TypedValue mFixedWidthMinor;
        private TypedValue mMaxHeightMajor;
        private TypedValue mMaxHeightMinor;
        private TypedValue mMaxWidthMajor;
        private TypedValue mMaxWidthMinor;
        private int mScreenHeightDp;
        private final Point mScreenSize;

        public FloatingABOLayoutSpec(Context context, AttributeSet attributeSet) {
            Point point = new Point();
            this.mScreenSize = point;
            this.mContext = context;
            parseWindowSize(context, attributeSet);
            WindowUtils.getScreenSize(context, point);
            this.mScreenHeightDp = (int) (point.y / context.getResources().getDisplayMetrics().density);
        }

        private int getMeasureSpec(int i, boolean z, TypedValue typedValue, TypedValue typedValue2, TypedValue typedValue3, TypedValue typedValue4) {
            if (View.MeasureSpec.getMode(i) == Integer.MIN_VALUE) {
                boolean useMinor = useMinor();
                if (!useMinor) {
                    typedValue = typedValue2;
                }
                int resolveDimension = resolveDimension(typedValue, z);
                if (resolveDimension > 0) {
                    return View.MeasureSpec.makeMeasureSpec(resolveDimension, SearchUpdater.SIM);
                }
                if (!useMinor) {
                    typedValue3 = typedValue4;
                }
                int resolveDimension2 = resolveDimension(typedValue3, z);
                return resolveDimension2 > 0 ? View.MeasureSpec.makeMeasureSpec(Math.min(resolveDimension2, View.MeasureSpec.getSize(i)), Integer.MIN_VALUE) : i;
            }
            return i;
        }

        private boolean isActivity(Context context) {
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return true;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
            return false;
        }

        private boolean isPortrait() {
            return (Build.VERSION.SDK_INT >= 31 || isActivity(this.mContext)) ? this.mContext.getResources().getConfiguration().orientation == 1 : this.mContext.getApplicationContext().getResources().getConfiguration().orientation == 1;
        }

        private void parseWindowSize(Context context, AttributeSet attributeSet) {
            if (attributeSet == null) {
                return;
            }
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.Window);
            int i = R$styleable.Window_windowFixedWidthMinor;
            if (obtainStyledAttributes.hasValue(i)) {
                TypedValue typedValue = new TypedValue();
                this.mFixedWidthMinor = typedValue;
                obtainStyledAttributes.getValue(i, typedValue);
            }
            int i2 = R$styleable.Window_windowFixedHeightMajor;
            if (obtainStyledAttributes.hasValue(i2)) {
                TypedValue typedValue2 = new TypedValue();
                this.mFixedHeightMajor = typedValue2;
                obtainStyledAttributes.getValue(i2, typedValue2);
            }
            int i3 = R$styleable.Window_windowFixedWidthMajor;
            if (obtainStyledAttributes.hasValue(i3)) {
                TypedValue typedValue3 = new TypedValue();
                this.mFixedWidthMajor = typedValue3;
                obtainStyledAttributes.getValue(i3, typedValue3);
            }
            int i4 = R$styleable.Window_windowFixedHeightMinor;
            if (obtainStyledAttributes.hasValue(i4)) {
                TypedValue typedValue4 = new TypedValue();
                this.mFixedHeightMinor = typedValue4;
                obtainStyledAttributes.getValue(i4, typedValue4);
            }
            int i5 = R$styleable.Window_windowMaxWidthMinor;
            if (obtainStyledAttributes.hasValue(i5)) {
                TypedValue typedValue5 = new TypedValue();
                this.mMaxWidthMinor = typedValue5;
                obtainStyledAttributes.getValue(i5, typedValue5);
            }
            int i6 = R$styleable.Window_windowMaxWidthMajor;
            if (obtainStyledAttributes.hasValue(i6)) {
                TypedValue typedValue6 = new TypedValue();
                this.mMaxWidthMajor = typedValue6;
                obtainStyledAttributes.getValue(i6, typedValue6);
            }
            int i7 = R$styleable.Window_windowMaxHeightMajor;
            if (obtainStyledAttributes.hasValue(i7)) {
                TypedValue typedValue7 = new TypedValue();
                this.mMaxHeightMajor = typedValue7;
                obtainStyledAttributes.getValue(i7, typedValue7);
            }
            int i8 = R$styleable.Window_windowMaxHeightMinor;
            if (obtainStyledAttributes.hasValue(i8)) {
                TypedValue typedValue8 = new TypedValue();
                this.mMaxHeightMinor = typedValue8;
                obtainStyledAttributes.getValue(i8, typedValue8);
            }
            obtainStyledAttributes.recycle();
        }

        private int resolveDimension(TypedValue typedValue, boolean z) {
            int i;
            float fraction;
            if (typedValue != null && (i = typedValue.type) != 0) {
                if (i == 5) {
                    fraction = typedValue.getDimension(this.mContext.getResources().getDisplayMetrics());
                } else if (i == 6) {
                    Point point = this.mScreenSize;
                    float f = z ? point.x : point.y;
                    fraction = typedValue.getFraction(f, f);
                }
                return (int) fraction;
            }
            return 0;
        }

        private boolean useMinor() {
            return isPortrait() || this.mScreenHeightDp >= 500;
        }

        public int getHeightMeasureSpecForDialog(int i) {
            return getMeasureSpec(i, false, this.mFixedHeightMinor, this.mFixedHeightMajor, this.mMaxHeightMinor, this.mMaxHeightMajor);
        }

        public int getWidthMeasureSpecForDialog(int i) {
            return getMeasureSpec(i, true, this.mFixedWidthMinor, this.mFixedWidthMajor, this.mMaxWidthMinor, this.mMaxWidthMajor);
        }

        public void onConfigurationChanged() {
            this.mFixedWidthMinor = AttributeResolver.resolveTypedValue(this.mContext, R$attr.windowFixedWidthMinor);
            this.mFixedHeightMajor = AttributeResolver.resolveTypedValue(this.mContext, R$attr.windowFixedHeightMajor);
            this.mFixedWidthMajor = AttributeResolver.resolveTypedValue(this.mContext, R$attr.windowFixedWidthMajor);
            this.mFixedHeightMinor = AttributeResolver.resolveTypedValue(this.mContext, R$attr.windowFixedHeightMinor);
            this.mMaxWidthMinor = AttributeResolver.resolveTypedValue(this.mContext, R$attr.windowMaxWidthMinor);
            this.mMaxWidthMajor = AttributeResolver.resolveTypedValue(this.mContext, R$attr.windowMaxWidthMajor);
            this.mMaxHeightMinor = AttributeResolver.resolveTypedValue(this.mContext, R$attr.windowMaxHeightMinor);
            this.mMaxHeightMajor = AttributeResolver.resolveTypedValue(this.mContext, R$attr.windowMaxHeightMajor);
            WindowUtils.getScreenSize(this.mContext, this.mScreenSize);
            this.mScreenHeightDp = (int) (this.mScreenSize.y / this.mContext.getResources().getDisplayMetrics().density);
        }
    }

    public DialogParentPanel2(Context context) {
        this(context, null);
    }

    public DialogParentPanel2(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DialogParentPanel2(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFloatingWindowSize = new FloatingABOLayoutSpec(context, attributeSet);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mFloatingWindowSize.onConfigurationChanged();
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(this.mFloatingWindowSize.getWidthMeasureSpecForDialog(i), this.mFloatingWindowSize.getHeightMeasureSpecForDialog(i2));
    }
}
