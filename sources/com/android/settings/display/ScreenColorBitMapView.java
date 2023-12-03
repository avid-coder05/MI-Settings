package com.android.settings.display;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.view.ViewCompat;
import com.android.settings.R;
import com.android.settings.display.ScreenColorBitMapView;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.android.settingslib.utils.ThreadUtils;
import miui.util.FeatureParser;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ScreenColorBitMapView extends FrameLayout {
    private Callback ICallback;
    private Bitmap mBitmap;
    private int mBitmapHeight;
    private int mBitmapWidth;
    private int mCicleRadius;
    private int mCircleBitmapAlpha;
    private Point mCoolPoint;
    private float mCx;
    private float mCy;
    private int mOffset;
    private ImageView mPointView;
    private float mScreenColorCicleX;
    private float mScreenColorCicleY;
    private float mScreenColorRadius;
    private Point mWarmPoint;
    private long transparentAnimTime;
    private static final int[] WARM_RGB = {255, 212, 184};
    private static final int[] COOL_RGB = {184, 212, 255};
    private static final int COLOR_OFFSET = FeatureParser.getInteger("warm_cool_color_offset", 2);
    private static int LOOP_STEP = 2;

    /* loaded from: classes.dex */
    interface Callback {
        void onAdjust();
    }

    public ScreenColorBitMapView(Context context) {
        this(context, null);
    }

    public ScreenColorBitMapView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ScreenColorBitMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCircleBitmapAlpha = 0;
        this.transparentAnimTime = 500L;
        init(context);
    }

    private Point computeCoolPoint() {
        Point point = new Point();
        int[] iArr = COOL_RGB;
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        float f = this.mScreenColorCicleX;
        int i4 = this.mOffset;
        int i5 = this.mCicleRadius;
        int i6 = ((int) f) - (i4 / 2);
        float f2 = this.mScreenColorCicleY;
        int i7 = ((int) f2) - (i4 / 2);
        int i8 = ((((int) f2) * 2) - i4) - (i5 / 2);
        for (int i9 = ((((int) f) * 2) - i4) - (i5 / 2); i9 >= i6; i9 -= LOOP_STEP) {
            int i10 = i8;
            while (true) {
                if (i10 >= i7) {
                    int bitmapPixel = getBitmapPixel(i9, i10);
                    int abs = Math.abs(Color.red(bitmapPixel) - i);
                    int i11 = COLOR_OFFSET;
                    if (abs < i11 && Math.abs(Color.green(bitmapPixel) - i2) < i11 && Math.abs(Color.blue(bitmapPixel) - i3) < i11) {
                        point.x = i9;
                        point.y = i10;
                        break;
                    }
                    i10 -= LOOP_STEP;
                }
            }
        }
        return point;
    }

    private Point computeWarmPoint() {
        Point point = new Point();
        int[] iArr = WARM_RGB;
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr[2];
        int i4 = this.mOffset;
        int i5 = this.mCicleRadius;
        int i6 = (i5 / 2) + i4;
        float f = this.mScreenColorCicleX - (i4 / 2);
        int i7 = (i5 / 2) + i4;
        float f2 = this.mScreenColorCicleY - (i4 / 2);
        while (true) {
            float f3 = i6;
            if (f3 >= f) {
                return point;
            }
            int i8 = i7;
            while (true) {
                float f4 = i8;
                if (f4 < f2) {
                    int bitmapPixel = getBitmapPixel(f3, f4);
                    int abs = Math.abs(Color.red(bitmapPixel) - i);
                    int i9 = COLOR_OFFSET;
                    if (abs < i9 && Math.abs(Color.green(bitmapPixel) - i2) < i9 && Math.abs(Color.blue(bitmapPixel) - i3) < i9) {
                        point.x = i6;
                        point.y = i8;
                        break;
                    }
                    i8 += LOOP_STEP;
                }
            }
            i6 += LOOP_STEP;
        }
    }

    private int getBitmapPixel(float f, float f2) {
        return this.mBitmap.getPixel(getUsagePoint(f, this.mBitmap.getWidth()), getUsagePoint(f2, this.mBitmap.getHeight()));
    }

    private Point getColorPoint(int i) {
        Point point = new Point();
        int i2 = (16711680 & i) >> 16;
        int i3 = (65280 & i) >> 8;
        int i4 = i & 255;
        int i5 = this.mOffset;
        while (true) {
            float f = i5;
            float f2 = this.mScreenColorCicleX * 2.0f;
            int i6 = this.mOffset;
            if (f >= f2 - i6) {
                return point;
            }
            while (true) {
                float f3 = i6;
                if (f3 >= (this.mScreenColorCicleY * 2.0f) - this.mOffset) {
                    break;
                }
                if (isContained(f, f3)) {
                    int bitmapPixel = getBitmapPixel(f, f3);
                    int abs = Math.abs(Color.red(bitmapPixel) - i2);
                    int i7 = COLOR_OFFSET;
                    if (abs < i7 && Math.abs(Color.green(bitmapPixel) - i3) < i7 && Math.abs(Color.blue(bitmapPixel) - i4) < i7) {
                        point.x = i5;
                        point.y = i6;
                        break;
                    }
                }
                i6 += 2;
            }
            i5 += 2;
        }
    }

    private float getDistanceToCircle(float f, float f2) {
        return (float) Math.sqrt(Math.pow(this.mScreenColorCicleX - f, 2.0d) + Math.pow(this.mScreenColorCicleY - f2, 2.0d));
    }

    private int getUsagePoint(float f, int i) {
        int i2 = ((int) ((f - this.mOffset) * i)) / this.mBitmapWidth;
        if (i2 >= i) {
            int i3 = i - 1;
            i2 = i3 > 0 ? i3 : 0;
        }
        if (i2 < 0) {
            return 0;
        }
        return i2;
    }

    private void init(Context context) {
        setLayoutDirection(0);
        View.inflate(getContext(), R.layout.screen_color_bit_map_view, this);
        ViewCompat.setImportantForAccessibility(this, 2);
        this.mBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.screen_color_preview)).getBitmap();
        this.mCicleRadius = context.getResources().getDimensionPixelSize(R.dimen.color_bitmap_point_size);
        ImageView imageView = (ImageView) findViewById(R.id.color_bit_point);
        this.mPointView = imageView;
        imageView.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.display.ScreenColorBitMapView.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.setClassName(Button.class.getName());
            }
        });
        float dimension = context.getResources().getDimension(R.dimen.screen_color_preview_diameter_new);
        float f = dimension / 2.0f;
        this.mScreenColorCicleX = f;
        this.mScreenColorCicleY = f;
        this.mOffset = (int) context.getResources().getDimension(R.dimen.screen_color_preview_offset_new);
        this.mScreenColorRadius = ((dimension - (r4 * 2)) / 2.0f) - 3.0f;
        int i = (int) (dimension - (r4 * 2));
        this.mBitmapWidth = i;
        this.mBitmapHeight = i;
        SharedPreferences sharedPreferences = context.getSharedPreferences("circle_point", 0);
        this.mCx = sharedPreferences.getFloat("last_circle_pointx", this.mScreenColorCicleX);
        this.mCy = sharedPreferences.getFloat("last_circle_pointy", this.mScreenColorCicleY);
        Point point = new Point();
        this.mCoolPoint = point;
        this.mWarmPoint = point;
        this.mPointView.setVisibility(4);
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.display.ScreenColorBitMapView.2

            /* JADX INFO: Access modifiers changed from: package-private */
            /* renamed from: com.android.settings.display.ScreenColorBitMapView$2$1  reason: invalid class name */
            /* loaded from: classes.dex */
            public class AnonymousClass1 implements Runnable {
                AnonymousClass1() {
                }

                /* JADX INFO: Access modifiers changed from: private */
                public /* synthetic */ void lambda$run$0(ValueAnimator valueAnimator) {
                    if (ScreenColorBitMapView.this.mPointView != null) {
                        ScreenColorBitMapView.this.mPointView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                    }
                }

                @Override // java.lang.Runnable
                public void run() {
                    ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(ScreenColorBitMapView.this.transparentAnimTime);
                    duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.display.ScreenColorBitMapView$2$1$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ScreenColorBitMapView.AnonymousClass2.AnonymousClass1.this.lambda$run$0(valueAnimator);
                        }
                    });
                    duration.start();
                    ScreenColorBitMapView.this.updatePointViewLocation();
                    if (ScreenColorBitMapView.this.mPointView != null) {
                        ScreenColorBitMapView.this.mPointView.setVisibility(0);
                    }
                }
            }

            @Override // java.lang.Runnable
            public void run() {
                ScreenColorBitMapView.this.updateThreePoints();
                ThreadUtils.postOnMainThread(new AnonymousClass1());
            }
        });
    }

    private boolean isContained(float f, float f2) {
        return getDistanceToCircle(f, f2) <= this.mScreenColorRadius;
    }

    private boolean isTouchCircleContained(float f, float f2) {
        float f3 = this.mCicleRadius / 2;
        float f4 = this.mCx;
        if (f >= f4 - f3 && f <= f4 + f3) {
            float f5 = this.mCy;
            if (f2 >= f5 - f3 && f2 <= f5 + f3) {
                return true;
            }
        }
        return false;
    }

    private void reversePoint(float f, float f2) {
        float distanceToCircle = getDistanceToCircle(f, f2);
        float abs = Math.abs(f - this.mScreenColorCicleX);
        float abs2 = Math.abs(f2 - this.mScreenColorCicleY);
        float f3 = this.mScreenColorRadius;
        float f4 = (abs * f3) / distanceToCircle;
        float f5 = (abs2 * f3) / distanceToCircle;
        float f6 = this.mScreenColorCicleX;
        if (f - f6 > 0.0f) {
            float f7 = this.mScreenColorCicleY;
            if (f2 - f7 > 0.0f) {
                this.mCy = f7 + f5;
            } else if (f2 - f7 < 0.0f) {
                this.mCy = f7 - f5;
            }
            this.mCx = f6 + f4;
        } else if (f - f6 < 0.0f) {
            float f8 = this.mScreenColorCicleY;
            if (f2 - f8 > 0.0f) {
                this.mCy = f8 + f5;
            } else if (f2 - f8 < 0.0f) {
                this.mCy = f8 - f5;
            }
            this.mCx = f6 - f4;
        }
    }

    private void savePoint() {
        SharedPreferences.Editor edit = getContext().getSharedPreferences("circle_point", 0).edit();
        edit.putFloat("last_circle_pointx", this.mCx);
        edit.putFloat("last_circle_pointy", this.mCy);
        edit.apply();
        MiStatInterfaceUtils.trackEvent("ScreenColorBitMapView_save_color_point");
        OneTrackInterfaceUtils.track("ScreenColorBitMapView_save_color_point", null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePointViewLocation() {
        ImageView imageView = this.mPointView;
        if (imageView != null) {
            imageView.setTranslationX(this.mCx - (this.mCicleRadius / 2));
            this.mPointView.setTranslationY(this.mCy - (this.mCicleRadius / 2));
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0 || (isTouchCircleContained(motionEvent.getX(), motionEvent.getY()) && isEnabled())) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (isEnabled()) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                if (isContained(x, y)) {
                    this.mCx = x;
                    this.mCy = y;
                } else {
                    reversePoint(x, y);
                }
                Callback callback = this.ICallback;
                if (callback != null) {
                    callback.onAdjust();
                }
                Settings.System.putInt(getContext().getContentResolver(), "screen_color_level", getBitmapPixel(this.mCx, this.mCy));
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    savePoint();
                }
                updatePointViewLocation();
                return true;
            }
            return true;
        }
        return false;
    }

    public void setCallback(Callback callback) {
        this.ICallback = callback;
    }

    public void setCircleLocation(int i) {
        if (i == 1) {
            Point point = this.mWarmPoint;
            this.mCx = point.x;
            this.mCy = point.y;
        } else if (i != 3) {
            this.mCx = this.mScreenColorCicleX;
            this.mCy = this.mScreenColorCicleY;
        } else {
            Point point2 = this.mCoolPoint;
            this.mCx = point2.x;
            this.mCy = point2.y;
        }
        savePoint();
        updatePointViewLocation();
    }

    public void updateThreePoints() {
        int i = Settings.System.getInt(getContext().getContentResolver(), "screen_color_level", 0);
        this.mWarmPoint = computeWarmPoint();
        this.mCoolPoint = computeCoolPoint();
        if (getBitmapPixel(this.mCx, this.mCy) != i) {
            if (i == 1) {
                Point point = this.mWarmPoint;
                this.mCx = point.x;
                this.mCy = point.y;
            } else if (i == 3) {
                Point point2 = this.mCoolPoint;
                this.mCx = point2.x;
                this.mCy = point2.y;
            } else if (i == 2) {
                this.mCx = this.mScreenColorCicleX;
                this.mCy = this.mScreenColorCicleY;
            } else if (i != 0) {
                Point colorPoint = getColorPoint(i);
                this.mCx = colorPoint.x;
                this.mCy = colorPoint.y;
            } else {
                this.mCx = this.mScreenColorCicleX;
                this.mCy = this.mScreenColorCicleY;
            }
            savePoint();
        }
    }
}
