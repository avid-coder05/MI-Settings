package miuix.animation.internal;

import java.util.Iterator;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.controller.AnimState;
import miuix.animation.internal.TransitionInfo;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.FloatProperty;

/* loaded from: classes5.dex */
public class PredictTask {
    private static final TransitionInfo.IUpdateInfoCreator sCreator = new TransitionInfo.IUpdateInfoCreator() { // from class: miuix.animation.internal.PredictTask.1
        @Override // miuix.animation.internal.TransitionInfo.IUpdateInfoCreator
        public UpdateInfo getUpdateInfo(FloatProperty floatProperty) {
            return new UpdateInfo(floatProperty);
        }
    };

    public static long predictDuration(IAnimTarget iAnimTarget, AnimState animState, AnimState animState2, AnimConfigLink animConfigLink) {
        TransitionInfo transitionInfo = new TransitionInfo(iAnimTarget, animState, animState2, animConfigLink);
        transitionInfo.initUpdateList(sCreator);
        transitionInfo.setupTasks(true);
        long averageDelta = AnimRunner.getInst().getAverageDelta();
        long j = averageDelta;
        while (true) {
            Iterator<AnimTask> it = transitionInfo.animTasks.iterator();
            while (it.hasNext()) {
                AnimRunnerTask.doAnimationFrame(it.next(), j, averageDelta, false, true);
            }
            if (!transitionInfo.getAnimStats().isRunning()) {
                return j;
            }
            j += averageDelta;
        }
    }
}
