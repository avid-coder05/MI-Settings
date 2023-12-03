package miuix.animation.internal;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.TypeEvaluator;
import java.util.List;
import miuix.animation.ViewTarget;
import miuix.animation.base.AnimSpecialConfig;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ColorProperty;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.property.ViewPropertyExt;
import miuix.animation.styles.PropertyStyle;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;
import miuix.animation.utils.LogUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public class AnimRunnerTask {
    static final ThreadLocal<AnimData> animDataLocal = new ThreadLocal<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void doAnimationFrame(AnimTask animTask, long j, long j2, boolean z, boolean z2) {
        UpdateInfo updateInfo;
        int i;
        int i2;
        boolean z3;
        AnimData animData = (AnimData) CommonUtils.getLocal(animDataLocal, AnimData.class);
        animData.logEnabled = LogUtils.isLogEnabled();
        long averageDelta = AnimRunner.getInst().getAverageDelta();
        for (AnimTask animTask2 = animTask; animTask2 != null; animTask2 = animTask2.remove()) {
            AnimStats animStats = animTask2.animStats;
            animStats.updateCount = 0;
            boolean z4 = !animStats.isStarted();
            List<UpdateInfo> list = animTask2.info.updateList;
            boolean z5 = animTask2.info.target instanceof ViewTarget;
            int i3 = animTask2.startPos;
            int animCount = i3 + animTask2.getAnimCount();
            int i4 = i3;
            while (i4 < animCount) {
                UpdateInfo updateInfo2 = list.get(i4);
                if (updateInfo2 == null) {
                    i = animCount;
                    i2 = i4;
                    z3 = z5;
                } else {
                    AnimSpecialConfig specialConfig = animTask2.info.config.getSpecialConfig(updateInfo2.property.getName());
                    animData.from(updateInfo2, animTask2.info.config, specialConfig);
                    if (z4) {
                        updateInfo = updateInfo2;
                        i = animCount;
                        i2 = i4;
                        setup(animTask2, animData, animTask2.info, specialConfig, j, j2);
                    } else {
                        updateInfo = updateInfo2;
                        i = animCount;
                        i2 = i4;
                    }
                    if (animData.op == 1) {
                        startAnim(animTask2, animData, animTask2.info, j, j2);
                    }
                    if (animData.op == 2) {
                        z3 = z5;
                        updateAnimation(animTask2, animData, animTask2.info, j, j2, averageDelta);
                    } else {
                        z3 = z5;
                    }
                    UpdateInfo updateInfo3 = updateInfo;
                    animData.to(updateInfo3);
                    if (z && z2 && !z3 && !AnimValueUtils.isInvalid(animData.value)) {
                        updateInfo3.setTargetValue(animTask2.info.target);
                    }
                }
                i4 = i2 + 1;
                animCount = i;
                z5 = z3;
            }
        }
    }

    private static double evaluateValue(AnimData animData, float f) {
        TypeEvaluator evaluator = getEvaluator(animData.property);
        return evaluator instanceof IntEvaluator ? ((IntEvaluator) evaluator).evaluate(f, Integer.valueOf((int) animData.startValue), Integer.valueOf((int) animData.targetValue)).doubleValue() : ((FloatEvaluator) evaluator).evaluate(f, (Number) Float.valueOf((float) animData.startValue), (Number) Float.valueOf((float) animData.targetValue)).doubleValue();
    }

    private static void finishProperty(AnimTask animTask, AnimData animData) {
        animData.setOp((byte) 5);
        animTask.animStats.failCount++;
    }

    private static TypeEvaluator getEvaluator(FloatProperty floatProperty) {
        return (floatProperty == ViewPropertyExt.BACKGROUND && (floatProperty instanceof ColorProperty)) ? CommonUtils.sArgbEvaluator : floatProperty instanceof IIntValueProperty ? new IntEvaluator() : new FloatEvaluator();
    }

    private static boolean initAnimation(AnimTask animTask, AnimData animData, long j, long j2) {
        if (!setValues(animData)) {
            if (animData.logEnabled) {
                LogUtils.logThread("miuix_anim", "StartTask, set start value failed, break, tag = " + animTask.info.key + ", property = " + animData.property.getName() + ", start value = " + animData.startValue + ", target value = " + animData.targetValue + ", value = " + animData.value);
            }
            finishProperty(animTask, animData);
            return false;
        } else if (!isValueInvalid(animData)) {
            animData.startTime = j - j2;
            animData.frameCount = 0;
            animData.setOp((byte) 2);
            return true;
        } else {
            if (animData.logEnabled) {
                LogUtils.logThread("miuix_anim", "StartTask, values invalid, break, tag = " + animTask.info.key + ", property = " + animData.property.getName() + ", startValue = " + animData.startValue + ", targetValue = " + animData.targetValue + ", value = " + animData.value + ", velocity = " + animData.velocity);
            }
            animData.reset();
            finishProperty(animTask, animData);
            return false;
        }
    }

    private static boolean isValueInvalid(AnimData animData) {
        return animData.startValue == animData.targetValue && Math.abs(animData.velocity) < 16.66666603088379d;
    }

    private static float regulateProgress(float f) {
        if (f > 1.0f) {
            return 1.0f;
        }
        if (f < 0.0f) {
            return 0.0f;
        }
        return f;
    }

    private static void setStartData(AnimTask animTask, AnimData animData) {
        animData.progress = 0.0d;
        animData.reset();
        if (animData.logEnabled) {
            LogUtils.debug("+++++ start anim, target = " + animTask.info.target + ", tag = " + animTask.info.key + ", property = " + animData.property.getName() + ", op = " + ((int) animData.op) + ", ease = " + animData.ease + ", delay = " + animData.delay + ", start value = " + animData.startValue + ", target value = " + animData.targetValue + ", value = " + animData.value + ", progress = " + animData.progress + ", velocity = " + animData.velocity, new Object[0]);
        }
    }

    private static boolean setValues(AnimData animData) {
        if (!AnimValueUtils.isInvalid(animData.value)) {
            if (AnimValueUtils.isInvalid(animData.startValue)) {
                animData.startValue = animData.value;
            }
            return true;
        } else if (AnimValueUtils.isInvalid(animData.startValue)) {
            return false;
        } else {
            animData.value = animData.startValue;
            return true;
        }
    }

    static void setup(AnimTask animTask, AnimData animData, TransitionInfo transitionInfo, AnimSpecialConfig animSpecialConfig, long j, long j2) {
        if (AnimValueUtils.isInvalid(animData.startValue)) {
            animData.startValue = AnimValueUtils.getValue(transitionInfo.target, animData.property, animData.startValue);
        }
        long j3 = j - j2;
        animData.initTime = j3;
        AnimStats animStats = animTask.animStats;
        animStats.initCount++;
        if (animData.op == 2 && animData.delay <= 0) {
            animData.startTime = j3;
            animData.delay = 0L;
            animStats.startCount--;
            setStartData(animTask, animData);
            return;
        }
        animData.setOp((byte) 1);
        float fromSpeed = AnimConfigUtils.getFromSpeed(transitionInfo.config, animSpecialConfig);
        if (fromSpeed != Float.MAX_VALUE) {
            animData.velocity = fromSpeed;
        }
    }

    static void startAnim(AnimTask animTask, AnimData animData, TransitionInfo transitionInfo, long j, long j2) {
        if (animData.delay > 0) {
            if (animData.logEnabled) {
                LogUtils.debug("StartTask, tag = " + animTask.info.key + ", property = " + animData.property.getName() + ", delay = " + animData.delay + ", initTime = " + animData.initTime + ", totalT = " + j, new Object[0]);
            }
            if (j < animData.initTime + animData.delay) {
                return;
            }
            double value = AnimValueUtils.getValue(transitionInfo.target, animData.property, Double.MAX_VALUE);
            if (value != Double.MAX_VALUE) {
                animData.startValue = value;
            }
        }
        AnimStats animStats = animTask.animStats;
        animStats.startCount--;
        if (initAnimation(animTask, animData, j, j2)) {
            setStartData(animTask, animData);
        }
    }

    private static void updateAnimation(AnimTask animTask, AnimData animData, TransitionInfo transitionInfo, long j, long j2, long j3) {
        animTask.animStats.updateCount++;
        animData.frameCount++;
        FloatProperty floatProperty = animData.property;
        if (floatProperty == ViewPropertyExt.FOREGROUND || floatProperty == ViewPropertyExt.BACKGROUND || (floatProperty instanceof ColorProperty)) {
            double d = animData.startValue;
            double d2 = animData.targetValue;
            animData.startValue = 0.0d;
            animData.targetValue = 1.0d;
            animData.value = animData.progress;
            PropertyStyle.doAnimationFrame(transitionInfo.target, animData, j, j2, j3);
            double regulateProgress = regulateProgress((float) animData.value);
            animData.progress = regulateProgress;
            animData.startValue = d;
            animData.targetValue = d2;
            animData.value = ((Integer) CommonUtils.sArgbEvaluator.evaluate((float) regulateProgress, Integer.valueOf((int) d), Integer.valueOf((int) animData.targetValue))).doubleValue();
        } else {
            PropertyStyle.doAnimationFrame(transitionInfo.target, animData, j, j2, j3);
            if (!EaseManager.isPhysicsStyle(animData.ease.style)) {
                animData.value = evaluateValue(animData, (float) animData.progress);
            }
        }
        if (animData.op == 3) {
            animData.justEnd = true;
            animTask.animStats.endCount++;
        }
        if (animData.logEnabled) {
            LogUtils.debug("----- update anim, target = " + animTask.info.target + ", tag = " + animTask.info.key + ", property = " + animData.property.getName() + ", op = " + ((int) animData.op) + ", init time = " + animData.initTime + ", start time = " + animData.startTime + ", start value = " + animData.startValue + ", target value = " + animData.targetValue + ", value = " + animData.value + ", progress = " + animData.progress + ", velocity = " + animData.velocity + ", delta = " + j2, new Object[0]);
        }
    }
}
