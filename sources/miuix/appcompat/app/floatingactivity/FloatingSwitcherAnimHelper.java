package miuix.appcompat.app.floatingactivity;

import android.view.View;
import miuix.animation.Folme;
import miuix.animation.IStateStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.ViewProperty;
import miuix.animation.utils.EaseManager;

/* loaded from: classes5.dex */
public class FloatingSwitcherAnimHelper {
    public static void executeCloseEnterAnimation(View view) {
        executeCloseEnterAnimation(view, null);
    }

    public static void executeCloseEnterAnimation(View view, AnimConfig animConfig) {
        AnimState animState = new AnimState();
        ViewProperty viewProperty = ViewProperty.TRANSLATION_X;
        AnimState add = animState.add(viewProperty, 0);
        IStateStyle to = Folme.useAt(view).state().setTo(viewProperty, -200);
        AnimConfig[] animConfigArr = new AnimConfig[1];
        if (animConfig == null) {
            animConfig = getAnimConfig(0, null);
        }
        animConfigArr[0] = animConfig;
        to.to(add, animConfigArr);
    }

    public static void executeCloseExitAnimation(View view, AnimConfig animConfig) {
        int width = view.getWidth();
        AnimState animState = new AnimState();
        ViewProperty viewProperty = ViewProperty.TRANSLATION_X;
        AnimState add = animState.add(viewProperty, width);
        IStateStyle to = Folme.useAt(view).state().setTo(viewProperty, 0);
        AnimConfig[] animConfigArr = new AnimConfig[1];
        if (animConfig == null) {
            animConfig = getAnimConfig();
        }
        animConfigArr[0] = animConfig;
        to.to(add, animConfigArr);
    }

    public static void executeOpenEnterAnimation(View view) {
        executeOpenEnterAnimation(view, null);
    }

    public static void executeOpenEnterAnimation(final View view, final AnimConfig animConfig) {
        if (view.isAttachedToWindow()) {
            executeSlideIn(view, animConfig);
        } else {
            view.post(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.FloatingSwitcherAnimHelper.2
                @Override // java.lang.Runnable
                public void run() {
                    FloatingSwitcherAnimHelper.executeSlideIn(view, animConfig);
                }
            });
        }
    }

    public static void executeOpenExitAnimation(View view) {
        executeOpenExitAnimation(view, null);
    }

    public static void executeOpenExitAnimation(View view, AnimConfig animConfig) {
        AnimState add = new AnimState().add(ViewProperty.TRANSLATION_X, -200.0d);
        IStateStyle state = Folme.useAt(view).state();
        AnimConfig[] animConfigArr = new AnimConfig[1];
        if (animConfig == null) {
            animConfig = getAnimConfig(0, null);
        }
        animConfigArr[0] = animConfig;
        state.to(add, animConfigArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void executeSlideIn(View view, AnimConfig animConfig) {
        AnimState animState = new AnimState();
        ViewProperty viewProperty = ViewProperty.TRANSLATION_X;
        AnimState add = animState.add(viewProperty, 0.0d);
        IStateStyle to = Folme.useAt(view).state().setTo(viewProperty, Integer.valueOf(view.getWidth()));
        AnimConfig[] animConfigArr = new AnimConfig[1];
        if (animConfig == null) {
            animConfig = getAnimConfig();
        }
        animConfigArr[0] = animConfig;
        to.to(add, animConfigArr);
    }

    public static AnimConfig getAnimConfig() {
        return getAnimConfig(0, null);
    }

    private static AnimConfig getAnimConfig(int i) {
        AnimConfig animConfig = new AnimConfig();
        if (i == 0) {
            animConfig.setEase(EaseManager.getStyle(-2, 1.0f, 0.46f));
            return animConfig;
        } else if (i != 1) {
            return getAnimConfig(0);
        } else {
            animConfig.setEase(EaseManager.getStyle(-2, 0.85f, 0.3f));
            return animConfig;
        }
    }

    public static AnimConfig getAnimConfig(int i, final Runnable runnable) {
        final AnimConfig animConfig = getAnimConfig(i);
        if (runnable != null) {
            animConfig.addListeners(new TransitionListener() { // from class: miuix.appcompat.app.floatingactivity.FloatingSwitcherAnimHelper.1
                @Override // miuix.animation.listener.TransitionListener
                public void onCancel(Object obj) {
                    super.onCancel(obj);
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                    animConfig.removeListeners(this);
                }

                @Override // miuix.animation.listener.TransitionListener
                public void onComplete(Object obj) {
                    super.onComplete(obj);
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                    animConfig.removeListeners(this);
                }
            });
        }
        return animConfig;
    }
}
