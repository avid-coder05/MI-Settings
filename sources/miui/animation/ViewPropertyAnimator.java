package miui.animation;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.Animation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes3.dex */
public class ViewPropertyAnimator extends Animator {
    public static final int ALPHA = 512;
    public static final float CURRENT_HALF_HEIGHT = 0.01f;
    public static final float CURRENT_HALF_WIDTH = 0.007f;
    public static final float CURRENT_HEIGHT = 0.008f;
    public static final float CURRENT_HEIGHT_NEGATIVE = 0.009f;
    public static final float CURRENT_VALUE = Float.MAX_VALUE;
    public static final float CURRENT_WIDTH = 0.005f;
    public static final float CURRENT_WIDTH_NEGATIVE = 0.006f;
    public static final float NO_FINAL_VALUE = Float.MIN_VALUE;
    public static final int NO_FINAL_VISIBILITY = -1;
    public static final float OUT_BOTTOM_SCREEN = 0.002f;
    public static final float OUT_LEFT_SCREEN = 0.003f;
    public static final float OUT_RIGHT_SCREEN = 0.004f;
    public static final float OUT_TOP_SCREEN = 0.001f;
    public static final int ROTATION = 16;
    public static final int ROTATION_X = 32;
    public static final int ROTATION_Y = 64;
    public static final int SCALE_X = 4;
    public static final int SCALE_Y = 8;
    public static final int TRANSLATION_X = 1;
    public static final int TRANSLATION_Y = 2;
    public static final int X = 128;
    public static final int Y = 256;
    private ValueAnimator mAnimator;
    private float mFinalValue;
    private int mFinalVisibility;
    private float mFromValue;
    private int mProperty;
    private float mToValue;
    private View mView;
    private static final int DISPLAY_METRICES_WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    private static final int DISPLAY_METRICES_HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;
    private static final HashMap<Object, Animator> mAnimatorMap = new HashMap<>();

    /* loaded from: classes3.dex */
    public static class Builder extends AnimatorBuilder {
        public Builder(ViewPropertyAnimator viewPropertyAnimator) {
            super(viewPropertyAnimator);
        }

        @Override // miui.animation.AnimatorBuilder
        public Builder addListener(Animator.AnimatorListener animatorListener) {
            this.mAnimator.addListener(animatorListener);
            return this;
        }

        @Override // miui.animation.AnimatorBuilder
        public Builder setDuration(long j) {
            this.mAnimator.setDuration(j);
            return this;
        }

        public Builder setFinalValue(float f) {
            ((ViewPropertyAnimator) this.mAnimator).setFinalValue(f);
            return this;
        }

        public Builder setFinalVisibility(int i) {
            ((ViewPropertyAnimator) this.mAnimator).setFinalVisibility(i);
            return this;
        }

        @Override // miui.animation.AnimatorBuilder
        public Builder setInterpolator(TimeInterpolator timeInterpolator) {
            this.mAnimator.setInterpolator(timeInterpolator);
            return this;
        }

        public Builder setRepeatCount(int i) {
            ((ViewPropertyAnimator) this.mAnimator).setRepeatCount(i);
            return this;
        }

        public Builder setRepeatMode(int i) {
            ((ViewPropertyAnimator) this.mAnimator).setRepeatMode(i);
            return this;
        }

        @Override // miui.animation.AnimatorBuilder
        public Builder setStartDelay(long j) {
            this.mAnimator.setStartDelay(j);
            return this;
        }
    }

    public ViewPropertyAnimator(View view, int i, float f, float f2) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f);
        this.mAnimator = ofFloat;
        this.mFinalVisibility = -1;
        this.mFinalValue = Float.MIN_VALUE;
        this.mView = view;
        this.mProperty = i;
        this.mFromValue = f;
        this.mToValue = f2;
        ofFloat.addListener(new Animator.AnimatorListener() { // from class: miui.animation.ViewPropertyAnimator.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                ViewPropertyAnimator.setAnimator(ViewPropertyAnimator.this.mView, null);
                ViewPropertyAnimator.this.setFinalValues();
                ArrayList<Animator.AnimatorListener> listeners = ViewPropertyAnimator.this.getListeners();
                if (listeners != null) {
                    Iterator it = ((ArrayList) listeners.clone()).iterator();
                    while (it.hasNext()) {
                        ((Animator.AnimatorListener) it.next()).onAnimationCancel(ViewPropertyAnimator.this);
                    }
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ViewPropertyAnimator.setAnimator(ViewPropertyAnimator.this.mView, null);
                ViewPropertyAnimator.this.setFinalValues();
                ArrayList<Animator.AnimatorListener> listeners = ViewPropertyAnimator.this.getListeners();
                if (listeners != null) {
                    Iterator it = ((ArrayList) listeners.clone()).iterator();
                    while (it.hasNext()) {
                        ((Animator.AnimatorListener) it.next()).onAnimationEnd(ViewPropertyAnimator.this);
                    }
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
                ArrayList<Animator.AnimatorListener> listeners = ViewPropertyAnimator.this.getListeners();
                if (listeners != null) {
                    Iterator it = ((ArrayList) listeners.clone()).iterator();
                    while (it.hasNext()) {
                        ((Animator.AnimatorListener) it.next()).onAnimationRepeat(ViewPropertyAnimator.this);
                    }
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                ArrayList<Animator.AnimatorListener> listeners = ViewPropertyAnimator.this.getListeners();
                if (listeners != null) {
                    Iterator it = ((ArrayList) listeners.clone()).iterator();
                    while (it.hasNext()) {
                        ((Animator.AnimatorListener) it.next()).onAnimationStart(ViewPropertyAnimator.this);
                    }
                }
            }
        });
        this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miui.animation.ViewPropertyAnimator.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewPropertyAnimator viewPropertyAnimator = ViewPropertyAnimator.this;
                viewPropertyAnimator.setValue(viewPropertyAnimator.mFromValue + (valueAnimator.getAnimatedFraction() * (ViewPropertyAnimator.this.mToValue - ViewPropertyAnimator.this.mFromValue)));
            }
        });
    }

    private float calulateAnimatorValue(float f) {
        float widthOrMeasureWidth;
        float widthOrMeasureWidth2;
        float f2;
        int top;
        if (f == 0.003f) {
            int i = this.mProperty;
            if (i != 128 && i != 1) {
                return f;
            }
            int[] iArr = new int[2];
            this.mView.getLocationOnScreen(iArr);
            f2 = DISPLAY_METRICES_WIDTH - iArr[0];
            if (this.mProperty != 128) {
                return f2;
            }
            top = this.mView.getLeft();
        } else if (f == 0.004f) {
            int i2 = this.mProperty;
            if (i2 != 128 && i2 != 1) {
                return f;
            }
            this.mView.getLocationOnScreen(new int[2]);
            f2 = -(getWidthOrMeasureWidth(this.mView) + r6[0]);
            if (this.mProperty != 128) {
                return f2;
            }
            top = this.mView.getLeft();
        } else if (f == 0.001f) {
            int i3 = this.mProperty;
            if (i3 != 256 && i3 != 2) {
                return f;
            }
            this.mView.getLocationOnScreen(new int[2]);
            f2 = -(getHeightOrMeasureHeight(this.mView) + r6[1]);
            if (this.mProperty != 256) {
                return f2;
            }
            top = this.mView.getTop();
        } else if (f != 0.002f) {
            if (f == 0.008f) {
                return getHeightOrMeasureHeight(this.mView);
            }
            if (f == 0.005f) {
                return getWidthOrMeasureWidth(this.mView);
            }
            if (f == 0.009f) {
                widthOrMeasureWidth2 = getHeightOrMeasureHeight(this.mView);
            } else if (f != 0.006f) {
                if (f == 0.01f) {
                    widthOrMeasureWidth = getHeightOrMeasureHeight(this.mView);
                } else if (f != 0.007f) {
                    return f == Float.MAX_VALUE ? getValue() : f;
                } else {
                    widthOrMeasureWidth = getWidthOrMeasureWidth(this.mView);
                }
                return widthOrMeasureWidth / 2.0f;
            } else {
                widthOrMeasureWidth2 = getWidthOrMeasureWidth(this.mView);
            }
            return -widthOrMeasureWidth2;
        } else {
            int i4 = this.mProperty;
            if (i4 != 256 && i4 != 2) {
                return f;
            }
            int[] iArr2 = new int[2];
            this.mView.getLocationOnScreen(iArr2);
            f2 = DISPLAY_METRICES_HEIGHT - iArr2[1];
            if (this.mProperty != 256) {
                return f2;
            }
            top = this.mView.getTop();
        }
        return f2 + top;
    }

    public static void cancelAnimator(View view) {
        Animator remove = mAnimatorMap.remove(view);
        if (remove != null) {
            remove.cancel();
        }
    }

    public static Animator getAnimator(View view) {
        return mAnimatorMap.get(view);
    }

    public static float getHeightOrMeasureHeight(View view) {
        int height = view.getHeight();
        if (height == 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            height = view.getMeasuredHeight();
        }
        return height;
    }

    private float getValue() {
        int i = this.mProperty;
        if (i != 1) {
            if (i != 2) {
                if (i != 4) {
                    if (i != 8) {
                        if (i != 16) {
                            if (i != 32) {
                                if (i != 64) {
                                    if (i != 128) {
                                        if (i != 256) {
                                            if (i != 512) {
                                                return Float.MIN_VALUE;
                                            }
                                            return this.mView.getAlpha();
                                        }
                                        return this.mView.getY();
                                    }
                                    return this.mView.getX();
                                }
                                return this.mView.getRotationY();
                            }
                            return this.mView.getRotationX();
                        }
                        return this.mView.getRotation();
                    }
                    return this.mView.getScaleY();
                }
                return this.mView.getScaleX();
            }
            return this.mView.getTranslationY();
        }
        return this.mView.getTranslationX();
    }

    public static Builder of(View view, int i, float f, float f2) {
        return new Builder(new ViewPropertyAnimator(view, i, f, f2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setAnimator(View view, Animator animator) {
        if (animator != null) {
            mAnimatorMap.put(view, animator);
        } else {
            mAnimatorMap.remove(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFinalValues() {
        int i = this.mFinalVisibility;
        if (i != -1) {
            this.mView.setVisibility(i);
        }
        float f = this.mFinalValue;
        if (f != Float.MIN_VALUE) {
            setValue(f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setValue(float f) {
        int i = this.mProperty;
        if (i == 1) {
            this.mView.setTranslationX(f);
        } else if (i == 2) {
            this.mView.setTranslationY(f);
        } else if (i == 4) {
            this.mView.setScaleX(f);
        } else if (i == 8) {
            this.mView.setScaleY(f);
        } else if (i == 16) {
            this.mView.setRotation(f);
        } else if (i == 32) {
            this.mView.setRotationX(f);
        } else if (i == 64) {
            this.mView.setRotationY(f);
        } else if (i == 128) {
            this.mView.setX(f);
        } else if (i == 256) {
            this.mView.setY(f);
        } else if (i != 512) {
        } else {
            this.mView.setAlpha(f);
        }
    }

    private void setupValues() {
        this.mFromValue = calulateAnimatorValue(this.mFromValue);
        this.mToValue = calulateAnimatorValue(this.mToValue);
        this.mFinalValue = calulateAnimatorValue(this.mFinalValue);
    }

    @Override // android.animation.Animator
    public void cancel() {
        this.mAnimator.cancel();
    }

    @Override // android.animation.Animator
    public Animator clone() {
        ViewPropertyAnimator viewPropertyAnimator = (ViewPropertyAnimator) super.clone();
        viewPropertyAnimator.mAnimator = this.mAnimator.clone();
        viewPropertyAnimator.mView = this.mView;
        viewPropertyAnimator.mProperty = this.mProperty;
        viewPropertyAnimator.mFromValue = this.mFromValue;
        viewPropertyAnimator.mToValue = this.mToValue;
        viewPropertyAnimator.mFinalVisibility = this.mFinalVisibility;
        viewPropertyAnimator.mFinalValue = this.mFinalValue;
        return viewPropertyAnimator;
    }

    @Override // android.animation.Animator
    public void end() {
        this.mAnimator.end();
    }

    @Override // android.animation.Animator
    public long getDuration() {
        return this.mAnimator.getDuration();
    }

    public float getFinalValue() {
        return this.mFinalValue;
    }

    public int getFinalVisibility() {
        return this.mFinalVisibility;
    }

    public int getRepeatCount() {
        return this.mAnimator.getRepeatCount();
    }

    public int getRepeatMode() {
        return this.mAnimator.getRepeatMode();
    }

    @Override // android.animation.Animator
    public long getStartDelay() {
        return this.mAnimator.getStartDelay();
    }

    public float getWidthOrMeasureWidth(View view) {
        int width = view.getWidth();
        if (width == 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            width = view.getMeasuredWidth();
        }
        return width;
    }

    @Override // android.animation.Animator
    public boolean isRunning() {
        return this.mAnimator.isRunning();
    }

    @Override // android.animation.Animator
    public Animator setDuration(long j) {
        this.mAnimator.setDuration(j);
        return this;
    }

    public void setFinalValue(float f) {
        this.mFinalValue = f;
    }

    public void setFinalVisibility(int i) {
        this.mFinalVisibility = i;
    }

    @Override // android.animation.Animator
    public void setInterpolator(TimeInterpolator timeInterpolator) {
        this.mAnimator.setInterpolator(timeInterpolator);
    }

    public void setRepeatCount(int i) {
        this.mAnimator.setRepeatCount(i);
    }

    public void setRepeatMode(int i) {
        this.mAnimator.setRepeatMode(i);
    }

    @Override // android.animation.Animator
    public void setStartDelay(long j) {
        this.mAnimator.setStartDelay(j);
    }

    @Override // android.animation.Animator
    public void start() {
        cancelAnimator(this.mView);
        Animation animation = this.mView.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
        this.mView.animate().cancel();
        setAnimator(this.mView, this);
        setupValues();
        if (this.mView.getVisibility() != 0) {
            this.mView.setVisibility(0);
        }
        this.mAnimator.start();
    }
}
