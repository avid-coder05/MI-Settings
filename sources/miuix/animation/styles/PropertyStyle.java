package miuix.animation.styles;

import android.animation.TimeInterpolator;
import miuix.animation.IAnimTarget;
import miuix.animation.internal.AnimData;
import miuix.animation.internal.AnimValueUtils;
import miuix.animation.physics.AccelerateOperator;
import miuix.animation.physics.EquilibriumChecker;
import miuix.animation.physics.FrictionOperator;
import miuix.animation.physics.PhysicsOperator;
import miuix.animation.physics.SpringOperator;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;
import miuix.animation.utils.LogUtils;

/* loaded from: classes5.dex */
public class PropertyStyle {
    static EquilibriumChecker checker;
    static final SpringOperator sSpring = new SpringOperator();
    static final AccelerateOperator sAccelerate = new AccelerateOperator();
    static final FrictionOperator sFriction = new FrictionOperator();
    static final ThreadLocal<EquilibriumChecker> mCheckerLocal = new ThreadLocal<>();

    public static void doAnimationFrame(IAnimTarget iAnimTarget, AnimData animData, long j, long j2, long j3) {
        long j4 = j - animData.startTime;
        if (EaseManager.isPhysicsStyle(animData.ease.style)) {
            updatePhysicsAnim(iAnimTarget, animData, j4, j2, j3);
        } else {
            updateInterpolatorAnim(animData, j4);
        }
    }

    private static void doPhysicsCalculation(AnimData animData, double d) {
        double d2 = animData.velocity;
        PhysicsOperator phyOperator = getPhyOperator(animData.ease.style);
        if (phyOperator == null || ((phyOperator instanceof SpringOperator) && AnimValueUtils.isInvalid(animData.targetValue))) {
            animData.value = animData.targetValue;
            animData.velocity = 0.0d;
            return;
        }
        double[] dArr = animData.ease.parameters;
        double updateVelocity = phyOperator.updateVelocity(d2, dArr[0], dArr[1], d, animData.targetValue, animData.value);
        animData.value += (animData.velocity + updateVelocity) * 0.5d * d;
        animData.velocity = updateVelocity;
    }

    public static PhysicsOperator getPhyOperator(int i) {
        if (i != -4) {
            if (i != -3) {
                if (i != -2) {
                    return null;
                }
                return sSpring;
            }
            return sAccelerate;
        }
        return sFriction;
    }

    static boolean isAnimRunning(EquilibriumChecker equilibriumChecker, FloatProperty floatProperty, int i, double d, double d2, long j) {
        boolean z = !equilibriumChecker.isAtEquilibrium(i, d, d2);
        if (!z || j <= 10000) {
            return z;
        }
        if (LogUtils.isLogEnabled()) {
            LogUtils.debug("animation for " + floatProperty.getName() + " stopped for running time too long, totalTime = " + j, new Object[0]);
        }
        return false;
    }

    private static boolean isUsingSpringPhy(AnimData animData) {
        return animData.ease.style == -2;
    }

    private static void setFinishValue(AnimData animData) {
        if (isUsingSpringPhy(animData)) {
            animData.value = animData.targetValue;
        }
    }

    private static void updateInterpolatorAnim(AnimData animData, long j) {
        EaseManager.InterpolateEaseStyle interpolateEaseStyle = (EaseManager.InterpolateEaseStyle) animData.ease;
        TimeInterpolator interpolator = EaseManager.getInterpolator(interpolateEaseStyle);
        long j2 = interpolateEaseStyle.duration;
        if (j < j2) {
            double interpolation = interpolator.getInterpolation(((float) j) / ((float) j2));
            animData.progress = interpolation;
            animData.value = interpolation;
            return;
        }
        animData.setOp((byte) 3);
        animData.progress = 1.0d;
        animData.value = 1.0d;
    }

    private static void updatePhysicsAnim(IAnimTarget iAnimTarget, AnimData animData, long j, long j2, long j3) {
        int round = j2 > j3 ? Math.round(((float) j2) / ((float) j3)) : 1;
        double d = j3 / 1000.0d;
        EquilibriumChecker equilibriumChecker = (EquilibriumChecker) CommonUtils.getLocal(mCheckerLocal, EquilibriumChecker.class);
        checker = equilibriumChecker;
        equilibriumChecker.init(iAnimTarget, animData.property, animData.targetValue);
        for (int i = 0; i < round; i++) {
            doPhysicsCalculation(animData, d);
            if (!isAnimRunning(checker, animData.property, animData.ease.style, animData.value, animData.velocity, j)) {
                animData.setOp((byte) 3);
                setFinishValue(animData);
                return;
            }
        }
    }
}
