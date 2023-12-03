package miuix.animation.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimConfig;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.controller.AnimState;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ColorProperty;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.LinkNode;
import miuix.animation.utils.LogUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public class TransitionInfo extends LinkNode<TransitionInfo> {
    public List<AnimTask> animTasks;
    public volatile AnimConfig config;
    public volatile AnimState from;
    public final int id;
    public volatile Object key;
    private final AnimStats mAnimStats;
    public volatile long startTime;
    public final Object tag;
    public final IAnimTarget target;
    public volatile AnimState to;
    public volatile List<UpdateInfo> updateList;
    public static final Map<Integer, TransitionInfo> sMap = new ConcurrentHashMap();
    private static final AtomicInteger sIdGenerator = new AtomicInteger();

    /* loaded from: classes5.dex */
    public interface IUpdateInfoCreator {
        UpdateInfo getUpdateInfo(FloatProperty floatProperty);
    }

    public TransitionInfo(IAnimTarget iAnimTarget, AnimState animState, AnimState animState2, AnimConfigLink animConfigLink) {
        int incrementAndGet = sIdGenerator.incrementAndGet();
        this.id = incrementAndGet;
        this.config = new AnimConfig();
        this.animTasks = new ArrayList();
        this.mAnimStats = new AnimStats();
        this.target = iAnimTarget;
        this.from = getState(animState);
        this.to = getState(animState2);
        Object tag = this.to.getTag();
        this.tag = tag;
        if (animState2.isTemporary) {
            this.key = tag + String.valueOf(incrementAndGet);
        } else {
            this.key = tag;
        }
        this.updateList = null;
        initValueForColorProperty();
        this.config.copy(animState2.getConfig());
        if (animConfigLink != null) {
            animConfigLink.addTo(this.config);
        }
        iAnimTarget.getNotifier().addListeners(this.key, this.config);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void decreaseStartCountForDelayAnim(AnimTask animTask, AnimStats animStats, UpdateInfo updateInfo, byte b) {
        AnimStats animStats2;
        int i;
        if (animTask == null || b != 1 || updateInfo.animInfo.delay <= 0 || (i = (animStats2 = animTask.animStats).startCount) <= 0) {
            return;
        }
        animStats2.startCount = i - 1;
        animStats.startCount--;
    }

    private AnimState getState(AnimState animState) {
        if (animState == null || !animState.isTemporary) {
            return animState;
        }
        AnimState animState2 = new AnimState();
        animState2.set(animState);
        return animState2;
    }

    private void initValueForColorProperty() {
        if (this.from == null) {
            return;
        }
        Iterator<Object> it = this.to.keySet().iterator();
        while (it.hasNext()) {
            FloatProperty tempProperty = this.to.getTempProperty(it.next());
            if ((tempProperty instanceof ColorProperty) && AnimValueUtils.isInvalid(AnimValueUtils.getValueOfTarget(this.target, tempProperty, Double.MAX_VALUE))) {
                double d = this.from.get(this.target, tempProperty);
                if (!AnimValueUtils.isInvalid(d)) {
                    this.target.setIntValue((ColorProperty) tempProperty, (int) d);
                }
            }
        }
    }

    public boolean containsProperty(FloatProperty floatProperty) {
        return this.to.contains(floatProperty);
    }

    public int getAnimCount() {
        return this.to.keySet().size();
    }

    public AnimStats getAnimStats() {
        this.mAnimStats.clear();
        Iterator<AnimTask> it = this.animTasks.iterator();
        while (it.hasNext()) {
            this.mAnimStats.add(it.next().animStats);
        }
        return this.mAnimStats;
    }

    public void initUpdateList(IUpdateInfoCreator iUpdateInfoCreator) {
        this.startTime = System.nanoTime();
        AnimState animState = this.from;
        AnimState animState2 = this.to;
        boolean isLogEnabled = LogUtils.isLogEnabled();
        if (isLogEnabled) {
            LogUtils.debug("-- doSetup, target = " + this.target + ", key = " + this.key + ", f = " + animState + ", t = " + animState2 + "\nconfig = " + this.config, new Object[0]);
        }
        ArrayList arrayList = new ArrayList();
        Iterator<Object> it = animState2.keySet().iterator();
        while (it.hasNext()) {
            FloatProperty property = animState2.getProperty(it.next());
            UpdateInfo updateInfo = iUpdateInfoCreator.getUpdateInfo(property);
            if (updateInfo != null) {
                arrayList.add(updateInfo);
                updateInfo.animInfo.targetValue = animState2.get(this.target, property);
                if (animState != null) {
                    updateInfo.animInfo.startValue = animState.get(this.target, property);
                } else {
                    double valueOfTarget = AnimValueUtils.getValueOfTarget(this.target, property, updateInfo.animInfo.startValue);
                    if (!AnimValueUtils.isInvalid(valueOfTarget)) {
                        updateInfo.animInfo.startValue = valueOfTarget;
                    }
                }
                AnimValueUtils.handleSetToValue(updateInfo);
                if (isLogEnabled) {
                    LogUtils.debug("-- doSetup, target = " + this.target + ", property = " + property.getName() + ", startValue = " + updateInfo.animInfo.startValue + ", targetValue = " + updateInfo.animInfo.targetValue + ", value = " + updateInfo.animInfo.value, new Object[0]);
                }
            }
        }
        this.updateList = arrayList;
    }

    public void setupTasks(boolean z) {
        int size = this.updateList.size();
        int max = Math.max(1, size / 4000);
        int ceil = (int) Math.ceil(size / max);
        if (this.animTasks.size() > max) {
            List<AnimTask> list = this.animTasks;
            list.subList(max, list.size()).clear();
        } else {
            for (int size2 = this.animTasks.size(); size2 < max; size2++) {
                this.animTasks.add(new AnimTask());
            }
        }
        int i = 0;
        for (AnimTask animTask : this.animTasks) {
            animTask.info = this;
            int i2 = i + ceil > size ? size - i : ceil;
            animTask.setup(i, i2);
            if (z) {
                animTask.animStats.startCount = i2;
            } else {
                animTask.updateAnimStats();
            }
            i += i2;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TransitionInfo{target = ");
        IAnimTarget iAnimTarget = this.target;
        sb.append(iAnimTarget != null ? iAnimTarget.getTargetObject() : null);
        sb.append(", key = ");
        sb.append(this.key);
        sb.append(", propSize = ");
        sb.append(this.to.keySet().size());
        sb.append(", next = ");
        sb.append(this.next);
        sb.append('}');
        return sb.toString();
    }
}
