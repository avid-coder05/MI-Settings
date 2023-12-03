package miui.animation;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import miui.animation.ViewPropertyAnimator;

/* loaded from: classes3.dex */
public class AnimatorBuilder {
    protected Animator mAnimator;

    public AnimatorBuilder(Animator animator) {
        this.mAnimator = animator;
    }

    public static AnimatorBuilder of(Animator animator) {
        return new AnimatorBuilder(animator);
    }

    public static ViewPropertyAnimator.Builder of(ViewPropertyAnimator viewPropertyAnimator) {
        return new ViewPropertyAnimator.Builder(viewPropertyAnimator);
    }

    public AnimatorBuilder addListener(Animator.AnimatorListener animatorListener) {
        this.mAnimator.addListener(animatorListener);
        return this;
    }

    public Animator animator() {
        return this.mAnimator;
    }

    public AnimatorBuilder setDuration(long j) {
        this.mAnimator.setDuration(j);
        return this;
    }

    public AnimatorBuilder setInterpolator(TimeInterpolator timeInterpolator) {
        this.mAnimator.setInterpolator(timeInterpolator);
        return this;
    }

    public AnimatorBuilder setStartDelay(long j) {
        this.mAnimator.setStartDelay(j);
        return this;
    }

    public Animator start() {
        this.mAnimator.start();
        return this.mAnimator;
    }
}
