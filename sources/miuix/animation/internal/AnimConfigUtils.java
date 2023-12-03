package miuix.animation.internal;

import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.utils.EaseManager;

/* loaded from: classes5.dex */
public class AnimConfigUtils {
    public static float chooseSpeed(float f, float f2) {
        return AnimValueUtils.isInvalid((double) f) ? f2 : AnimValueUtils.isInvalid((double) f2) ? f : Math.max(f, f2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getDelay(AnimConfig animConfig, AnimSpecialConfig animSpecialConfig) {
        return Math.max(animConfig.delay, animSpecialConfig != null ? animSpecialConfig.delay : 0L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static EaseManager.EaseStyle getEase(AnimConfig animConfig, AnimSpecialConfig animSpecialConfig) {
        EaseManager.EaseStyle easeStyle;
        if (animSpecialConfig == null || (easeStyle = animSpecialConfig.ease) == null || easeStyle == AnimConfig.sDefEase) {
            easeStyle = animConfig.ease;
        }
        return easeStyle == null ? AnimConfig.sDefEase : easeStyle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float getFromSpeed(AnimConfig animConfig, AnimSpecialConfig animSpecialConfig) {
        return (animSpecialConfig == null || AnimValueUtils.isInvalid((double) animSpecialConfig.fromSpeed)) ? animConfig.fromSpeed : animSpecialConfig.fromSpeed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getTintMode(AnimConfig animConfig, AnimSpecialConfig animSpecialConfig) {
        return Math.max(animConfig.tintMode, animSpecialConfig != null ? animSpecialConfig.tintMode : -1);
    }
}
