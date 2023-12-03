package com.android.settings;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.util.Log;

/* loaded from: classes.dex */
public abstract class MiuiAnimationController {
    private boolean mAnimating;
    private Animatable mAnimationDrawable;
    private Drawable mAnimationIcon;
    private Runnable mAnimationRunnable = new Runnable() { // from class: com.android.settings.MiuiAnimationController.1
        @Override // java.lang.Runnable
        public void run() {
            MiuiAnimationController.this.playAnimationImmediately();
        }
    };
    private Handler mHandler;

    public MiuiAnimationController(Context context, int i) {
        this.mHandler = new Handler(context.getApplicationContext().getMainLooper());
        load(context, i);
    }

    private void load(Context context, int i) {
        StateListDrawable stateListDrawable = (StateListDrawable) context.getResources().getDrawable(i);
        this.mAnimationIcon = stateListDrawable;
        this.mAnimationDrawable = getAnimationDrawable(stateListDrawable);
        this.mAnimationIcon.setVisible(false, false);
        this.mAnimationDrawable.stop();
    }

    private void playAnimationDelayed() {
        this.mHandler.removeCallbacks(this.mAnimationRunnable);
        this.mHandler.postDelayed(this.mAnimationRunnable, 100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playAnimationImmediately() {
        if (this.mAnimationIcon.getCallback() == null) {
            Log.w("MiuiAnimationDrawable", "playAnimationImmediately: callback is null");
            return;
        }
        this.mAnimationDrawable.start();
        this.mAnimating = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Animatable getAnimationDrawable() {
        return this.mAnimationDrawable;
    }

    protected abstract Animatable getAnimationDrawable(Drawable drawable);

    public Drawable getAnimationIcon() {
        return this.mAnimationIcon;
    }

    public void playAnimation() {
        this.mAnimationIcon.setVisible(true, false);
        if (this.mAnimationIcon.getCallback() != null) {
            playAnimationImmediately();
        } else {
            playAnimationDelayed();
        }
    }

    public void stopAnimation() {
        this.mAnimationIcon.setVisible(false, false);
        this.mAnimationDrawable.stop();
        this.mAnimating = false;
        this.mHandler.removeCallbacks(this.mAnimationRunnable);
    }
}
