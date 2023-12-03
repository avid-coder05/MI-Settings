package miuix.androidbasewidget.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewDebug;
import android.widget.SeekBar;
import androidx.appcompat.widget.AppCompatSeekBar;
import java.lang.ref.WeakReference;
import java.util.Collection;
import miuix.androidbasewidget.R$attr;
import miuix.androidbasewidget.R$color;
import miuix.androidbasewidget.R$style;
import miuix.androidbasewidget.R$styleable;
import miuix.animation.Folme;
import miuix.animation.IHoverStyle;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.view.CompatViewMethod;
import miuix.view.HapticCompat;
import miuix.view.HapticFeedbackConstants;

/* loaded from: classes5.dex */
public class SeekBar extends AppCompatSeekBar {
    private int mDefaultForegroundPrimaryColor;
    private int mDefaultForegroundPrimaryDisableColor;
    private int mDefaultIconPrimaryColor;
    private float mDisabledProgressAlpha;
    private float mDraggableMaxPercentProcess;
    private float mDraggableMinPercentProgress;
    private int mForegroundPrimaryColor;
    private int mForegroundPrimaryDisableColor;
    private boolean mHasEdgeReached;
    private IStateStyle mIStateStyle;
    private int mIconPrimaryColor;
    private int mIconTransparent;
    private boolean mIsInMiddle;
    private float mMaxMiddle;
    private boolean mMiddleEnabled;
    private float mMinMiddle;
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;
    private int mProgess;
    private ColorStateList mProgressColorStateList;
    private final SeekBar.OnSeekBarChangeListener mTrainsOnSeekBarChangeListener;

    /* loaded from: classes5.dex */
    private static class ColorUpdateRunner implements Runnable {
        private WeakReference<SeekBar> mSeekBarRef;

        public ColorUpdateRunner(SeekBar seekBar) {
            this.mSeekBarRef = new WeakReference<>(seekBar);
        }

        @Override // java.lang.Runnable
        public void run() {
            WeakReference<SeekBar> weakReference = this.mSeekBarRef;
            SeekBar seekBar = weakReference == null ? null : weakReference.get();
            if (seekBar != null) {
                seekBar.updatePrimaryColor();
            }
        }
    }

    public SeekBar(Context context) {
        this(context, null);
    }

    public SeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.seekBarStyle);
    }

    public SeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: miuix.androidbasewidget.widget.SeekBar.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(android.widget.SeekBar seekBar, int i2, boolean z) {
                boolean z2 = true;
                if (SeekBar.this.mMiddleEnabled) {
                    int max = SeekBar.this.getMax() - SeekBar.this.getMinWrapper();
                    float f = max;
                    int round = Math.round(0.5f * f);
                    float minWrapper = max > 0 ? (i2 - SeekBar.this.getMinWrapper()) / f : 0.0f;
                    if (minWrapper <= SeekBar.this.mMinMiddle || minWrapper >= SeekBar.this.mMaxMiddle) {
                        SeekBar.this.mProgess = Math.round(i2);
                        SeekBar.this.mIStateStyle.setTo("targe", Integer.valueOf(SeekBar.this.mProgess));
                    } else {
                        SeekBar.this.mProgess = round;
                    }
                    if (SeekBar.this.getProgress() != SeekBar.this.mProgess) {
                        SeekBar.this.mIStateStyle.to("targe", Integer.valueOf(SeekBar.this.mProgess), new AnimConfig().setEase(0, 350.0f, 0.9f, 0.15f).addListeners(new TransitionListener() { // from class: miuix.androidbasewidget.widget.SeekBar.1.1
                            @Override // miuix.animation.listener.TransitionListener
                            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                                UpdateInfo findByName = UpdateInfo.findByName(collection, "targe");
                                if (findByName != null) {
                                    SeekBar.this.setProgress(findByName.getIntValue());
                                }
                            }
                        }));
                    }
                }
                SeekBar seekBar2 = SeekBar.this;
                int progressForm = seekBar2.getProgressForm(seekBar2.mDraggableMinPercentProgress);
                SeekBar seekBar3 = SeekBar.this;
                int progressForm2 = seekBar3.getProgressForm(seekBar3.mDraggableMaxPercentProcess);
                if (i2 < progressForm) {
                    SeekBar.this.setProgress(progressForm);
                    i2 = progressForm;
                } else if (i2 > progressForm2) {
                    SeekBar.this.setProgress(progressForm2);
                    i2 = progressForm2;
                }
                if (SeekBar.this.getProgress() != SeekBar.this.getMax() && SeekBar.this.getProgress() != SeekBar.this.getMinWrapper()) {
                    z2 = false;
                }
                if (z2 && !SeekBar.this.mHasEdgeReached) {
                    HapticCompat.performHapticFeedback(seekBar, HapticFeedbackConstants.MIUI_MESH_NORMAL);
                }
                SeekBar.this.mHasEdgeReached = z2;
                if (SeekBar.this.mOnSeekBarChangeListener != null) {
                    SeekBar.this.mOnSeekBarChangeListener.onProgressChanged(seekBar, i2, z);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
                if (SeekBar.this.mOnSeekBarChangeListener != null) {
                    SeekBar.this.mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
                if (SeekBar.this.mOnSeekBarChangeListener != null) {
                    SeekBar.this.mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        };
        this.mTrainsOnSeekBarChangeListener = onSeekBarChangeListener;
        CompatViewMethod.setForceDarkAllowed(this, false);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SeekBar, i, R$style.Widget_SeekBar_DayNight);
        this.mDefaultForegroundPrimaryColor = context.getResources().getColor(R$color.miuix_appcompat_progress_primary_colors_light);
        this.mDefaultForegroundPrimaryDisableColor = context.getResources().getColor(R$color.miuix_appcompat_progress_disable_color_light);
        this.mDefaultIconPrimaryColor = context.getResources().getColor(R$color.miuix_appcompat_progress_background_icon_light);
        this.mMiddleEnabled = obtainStyledAttributes.getBoolean(R$styleable.SeekBar_middleEnabled, false);
        this.mForegroundPrimaryColor = obtainStyledAttributes.getColor(R$styleable.SeekBar_foregroundPrimaryColor, this.mDefaultForegroundPrimaryColor);
        this.mForegroundPrimaryDisableColor = obtainStyledAttributes.getColor(R$styleable.SeekBar_foregroundPrimaryDisableColor, this.mDefaultForegroundPrimaryDisableColor);
        this.mIconPrimaryColor = obtainStyledAttributes.getColor(R$styleable.SeekBar_iconPrimaryColor, this.mDefaultIconPrimaryColor);
        this.mDisabledProgressAlpha = obtainStyledAttributes.getFloat(R$styleable.SeekBar_disabledProgressAlpha, 0.5f);
        this.mMinMiddle = obtainStyledAttributes.getFloat(R$styleable.SeekBar_minMiddle, 0.46f);
        this.mMaxMiddle = obtainStyledAttributes.getFloat(R$styleable.SeekBar_maxMiddle, 0.54f);
        this.mDraggableMinPercentProgress = getValueFromTypedArray(obtainStyledAttributes, R$styleable.SeekBar_draggableMinPercentProgress, 0.0f);
        this.mDraggableMaxPercentProcess = getValueFromTypedArray(obtainStyledAttributes, R$styleable.SeekBar_draggableMaxPercentProgress, 1.0f);
        setDraggableMinPercentProgress(this.mDraggableMinPercentProgress);
        setDraggableMaxPercentProcess(this.mDraggableMaxPercentProcess);
        obtainStyledAttributes.recycle();
        this.mIconTransparent = context.getResources().getColor(R$color.miuix_appcompat_transparent);
        float f = this.mMinMiddle;
        if (f > 0.5f || f < 0.0f) {
            this.mMinMiddle = 0.46f;
        }
        float f2 = this.mMaxMiddle;
        if (f2 < 0.5f || f2 > 1.0f) {
            this.mMaxMiddle = 0.54f;
        }
        int max = getMax() - getMinWrapper();
        this.mIsInMiddle = isInMiddle(max, getProgress());
        this.mProgess = getProgress();
        if (this.mIsInMiddle) {
            int round = Math.round(max * 0.5f);
            this.mProgess = round;
            setProgress(round);
        }
        IStateStyle useValue = Folme.useValue(Integer.valueOf(this.mProgess));
        this.mIStateStyle = useValue;
        useValue.setTo("targe", Integer.valueOf(this.mProgess));
        setOnSeekBarChangeListener(onSeekBarChangeListener);
        post(new ColorUpdateRunner(this));
        Folme.useAt(this).hover().setEffect(IHoverStyle.HoverEffect.NORMAL).handleHoverOf(this, new AnimConfig[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getMinWrapper() {
        if (Build.VERSION.SDK_INT >= 26) {
            return super.getMin();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized int getProgressForm(float f) {
        return ((int) (f * getRange())) + getMinWrapper();
    }

    private synchronized int getRange() {
        return getMax() - getMinWrapper();
    }

    private float getValueFromTypedArray(TypedArray typedArray, int i, float f) {
        TypedValue peekValue = typedArray.peekValue(i);
        return (peekValue == null || peekValue.type != 6) ? f : peekValue.getFraction(1.0f, 1.0f);
    }

    private boolean isInMiddle(int i, int i2) {
        float minWrapper = i > 0 ? (i2 - getMinWrapper()) / i : 0.0f;
        return minWrapper > this.mMinMiddle && minWrapper < this.mMaxMiddle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePrimaryColor() {
        int i;
        Drawable drawable;
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
            Drawable findDrawableByLayerId = layerDrawable.findDrawableByLayerId(16908301);
            if (findDrawableByLayerId != null && (findDrawableByLayerId instanceof ClipDrawable) && (i = Build.VERSION.SDK_INT) >= 23 && (drawable = ((ClipDrawable) findDrawableByLayerId).getDrawable()) != null && (drawable instanceof GradientDrawable)) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                ColorStateList color = i >= 24 ? gradientDrawable.getColor() : null;
                if (this.mProgressColorStateList == null && color != null) {
                    this.mProgressColorStateList = color;
                }
                ColorStateList colorStateList = this.mProgressColorStateList;
                if (colorStateList != null && (colorStateList.getColorForState(new int[]{-16842910}, this.mDefaultForegroundPrimaryDisableColor) != this.mForegroundPrimaryDisableColor || this.mProgressColorStateList.getColorForState(android.widget.SeekBar.ENABLED_STATE_SET, this.mDefaultForegroundPrimaryColor) != this.mForegroundPrimaryColor)) {
                    GradientDrawable gradientDrawable2 = (GradientDrawable) gradientDrawable.mutate();
                    ColorStateList colorStateList2 = new ColorStateList(new int[][]{new int[]{-16842910}, new int[0]}, new int[]{this.mForegroundPrimaryDisableColor, this.mForegroundPrimaryColor});
                    this.mProgressColorStateList = colorStateList2;
                    gradientDrawable2.setColor(colorStateList2);
                }
            }
            Drawable findDrawableByLayerId2 = layerDrawable.findDrawableByLayerId(16908294);
            if (findDrawableByLayerId2 == null || !(findDrawableByLayerId2 instanceof GradientDrawable)) {
                return;
            }
            findDrawableByLayerId2.setColorFilter(this.mMiddleEnabled ? this.mIconPrimaryColor : this.mIconTransparent, PorterDuff.Mode.SRC);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.AppCompatSeekBar, android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        updatePrimaryColor();
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.setAlpha(isEnabled() ? 255 : (int) (this.mDisabledProgressAlpha * 255.0f));
        }
    }

    @ViewDebug.ExportedProperty(category = "draggableProgress")
    public synchronized float getDraggableMaxPercentProgress() {
        return this.mDraggableMaxPercentProcess;
    }

    @ViewDebug.ExportedProperty(category = "draggableProgress")
    public synchronized float getDraggableMinPercentProgress() {
        return this.mDraggableMinPercentProgress;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0028 A[Catch: all -> 0x0013, TryCatch #0 {all -> 0x0013, blocks: (B:5:0x000a, B:12:0x0022, B:14:0x0028, B:16:0x0031, B:18:0x003d, B:11:0x001a), top: B:23:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0030  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x003d A[Catch: all -> 0x0013, TRY_LEAVE, TryCatch #0 {all -> 0x0013, blocks: (B:5:0x000a, B:12:0x0022, B:14:0x0028, B:16:0x0031, B:18:0x003d, B:11:0x001a), top: B:23:0x0008 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized void setDraggableMaxPercentProcess(float r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            double r0 = (double) r5
            r2 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            r1 = 1065353216(0x3f800000, float:1.0)
            if (r0 <= 0) goto L15
            java.lang.String r5 = "SeekBar"
            java.lang.String r0 = "The draggableMaxPercentProcess value should not be higher than the max value, reset to 1.0"
            android.util.Log.e(r5, r0)     // Catch: java.lang.Throwable -> L13
        L11:
            r5 = r1
            goto L22
        L13:
            r5 = move-exception
            goto L42
        L15:
            r0 = 0
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 >= 0) goto L22
            java.lang.String r5 = "SeekBar"
            java.lang.String r0 = "The draggableMaxPercentProcess value should not be lower than the min value, reset to 1.0"
            android.util.Log.e(r5, r0)     // Catch: java.lang.Throwable -> L13
            goto L11
        L22:
            float r0 = r4.mDraggableMinPercentProgress     // Catch: java.lang.Throwable -> L13
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 >= 0) goto L30
            java.lang.String r5 = "SeekBar"
            java.lang.String r0 = "The draggableMaxPercentProcess value should not be lower than draggableMinPercentProcess value, reset to 1.0"
            android.util.Log.e(r5, r0)     // Catch: java.lang.Throwable -> L13
            goto L31
        L30:
            r1 = r5
        L31:
            r4.mDraggableMaxPercentProcess = r1     // Catch: java.lang.Throwable -> L13
            int r5 = r4.getProgressForm(r1)     // Catch: java.lang.Throwable -> L13
            int r0 = r4.getProgress()     // Catch: java.lang.Throwable -> L13
            if (r0 <= r5) goto L40
            r4.setProgress(r5)     // Catch: java.lang.Throwable -> L13
        L40:
            monitor-exit(r4)
            return
        L42:
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.androidbasewidget.widget.SeekBar.setDraggableMaxPercentProcess(float):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0028 A[Catch: all -> 0x0012, TryCatch #0 {all -> 0x0012, blocks: (B:5:0x0009, B:12:0x0022, B:14:0x0028, B:16:0x0031, B:18:0x003d, B:11:0x001a), top: B:23:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0030  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x003d A[Catch: all -> 0x0012, TRY_LEAVE, TryCatch #0 {all -> 0x0012, blocks: (B:5:0x0009, B:12:0x0022, B:14:0x0028, B:16:0x0031, B:18:0x003d, B:11:0x001a), top: B:23:0x0007 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized void setDraggableMinPercentProgress(float r7) {
        /*
            r6 = this;
            monitor-enter(r6)
            double r0 = (double) r7
            r2 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            r3 = 0
            if (r2 <= 0) goto L14
            java.lang.String r7 = "SeekBar"
            java.lang.String r0 = "The draggableMinPercentProgress value should not be higher than 1.0, reset to 0.0"
            android.util.Log.e(r7, r0)     // Catch: java.lang.Throwable -> L12
        L10:
            r7 = r3
            goto L22
        L12:
            r7 = move-exception
            goto L42
        L14:
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L22
            java.lang.String r7 = "SeekBar"
            java.lang.String r0 = "The draggableMinPercentProgress value should not be lower than 0.0, reset to 0.0"
            android.util.Log.e(r7, r0)     // Catch: java.lang.Throwable -> L12
            goto L10
        L22:
            float r0 = r6.mDraggableMaxPercentProcess     // Catch: java.lang.Throwable -> L12
            int r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r0 <= 0) goto L30
            java.lang.String r7 = "SeekBar"
            java.lang.String r0 = "The draggableMinPercentProgress value should not be higher than draggableMaxPercentProcess value, reset to 0.0"
            android.util.Log.e(r7, r0)     // Catch: java.lang.Throwable -> L12
            goto L31
        L30:
            r3 = r7
        L31:
            r6.mDraggableMinPercentProgress = r3     // Catch: java.lang.Throwable -> L12
            int r7 = r6.getProgressForm(r3)     // Catch: java.lang.Throwable -> L12
            int r0 = r6.getProgress()     // Catch: java.lang.Throwable -> L12
            if (r0 >= r7) goto L40
            r6.setProgress(r7)     // Catch: java.lang.Throwable -> L12
        L40:
            monitor-exit(r6)
            return
        L42:
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.androidbasewidget.widget.SeekBar.setDraggableMinPercentProgress(float):void");
    }

    public void setForegroundPrimaryColor(int i, int i2) {
        this.mForegroundPrimaryColor = i;
        this.mForegroundPrimaryDisableColor = i2;
        updatePrimaryColor();
    }

    public void setIconPrimaryColor(int i) {
        this.mIconPrimaryColor = i;
        updatePrimaryColor();
    }

    public void setMiddleEnabled(boolean z) {
        if (z != this.mMiddleEnabled) {
            this.mMiddleEnabled = z;
            updatePrimaryColor();
        }
    }

    @Override // android.widget.SeekBar
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener2 = this.mTrainsOnSeekBarChangeListener;
        if (onSeekBarChangeListener == onSeekBarChangeListener2) {
            super.setOnSeekBarChangeListener(onSeekBarChangeListener2);
        } else {
            this.mOnSeekBarChangeListener = onSeekBarChangeListener;
        }
    }
}
