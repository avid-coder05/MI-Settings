package miuix.animation.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.animation.IAnimTarget;
import miuix.animation.ViewTarget;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.property.ViewPropertyExt;
import miuix.animation.styles.ForegroundColorStyle;
import miuix.animation.utils.LogUtils;

/* loaded from: classes5.dex */
public final class TargetHandler extends Handler {
    private final IAnimTarget mTarget;
    private final List<TransitionInfo> mTransList = new ArrayList();
    public final long threadId = Thread.currentThread().getId();

    public TargetHandler(IAnimTarget iAnimTarget) {
        this.mTarget = iAnimTarget;
    }

    private static void checkValue(List<UpdateInfo> list) {
        ArrayList arrayList = new ArrayList();
        for (UpdateInfo updateInfo : list) {
            if (AnimValueUtils.isInvalid(updateInfo.animInfo.value)) {
                arrayList.add(updateInfo);
            }
        }
        if (arrayList.size() > 0) {
            list.removeAll(arrayList);
        }
    }

    private static void notifyStartOrEnd(TransitionInfo transitionInfo, boolean z) {
        if (transitionInfo.getAnimCount() > 4000) {
            return;
        }
        for (UpdateInfo updateInfo : transitionInfo.updateList) {
            if (updateInfo.property == ViewPropertyExt.FOREGROUND) {
                if (z) {
                    ForegroundColorStyle.start(transitionInfo.target, updateInfo);
                } else {
                    ForegroundColorStyle.end(transitionInfo.target, updateInfo);
                }
            }
        }
    }

    private void onEnd(TransitionInfo transitionInfo, int i) {
        if (LogUtils.isLogEnabled()) {
            LogUtils.debug("<<< onEnd, " + this.mTarget + ", info.key = " + transitionInfo.key, new Object[0]);
        }
        update(false, transitionInfo);
        notifyStartOrEnd(transitionInfo, false);
        if (i == 4) {
            transitionInfo.target.getNotifier().notifyCancelAll(transitionInfo.key, transitionInfo.tag);
        } else {
            transitionInfo.target.getNotifier().notifyEndAll(transitionInfo.key, transitionInfo.tag);
        }
        transitionInfo.target.getNotifier().removeListeners(transitionInfo.key);
    }

    private void onReplaced(TransitionInfo transitionInfo) {
        if (LogUtils.isLogEnabled()) {
            LogUtils.debug("<<< onReplaced, " + this.mTarget + ", info.key = " + transitionInfo.key, new Object[0]);
        }
        if (transitionInfo.getAnimCount() <= 4000) {
            this.mTarget.getNotifier().notifyPropertyEnd(transitionInfo.key, transitionInfo.tag, transitionInfo.updateList);
        }
        this.mTarget.getNotifier().notifyCancelAll(transitionInfo.key, transitionInfo.tag);
        this.mTarget.getNotifier().removeListeners(transitionInfo.key);
    }

    private void onStart(TransitionInfo transitionInfo) {
        if (LogUtils.isLogEnabled()) {
            LogUtils.debug(">>> onStart, " + this.mTarget + ", info.key = " + transitionInfo.key, new Object[0]);
        }
        transitionInfo.target.getNotifier().addListeners(transitionInfo.key, transitionInfo.config);
        transitionInfo.target.getNotifier().notifyBegin(transitionInfo.key, transitionInfo.tag);
        List<UpdateInfo> list = transitionInfo.updateList;
        if (!list.isEmpty() && list.size() <= 4000) {
            transitionInfo.target.getNotifier().notifyPropertyBegin(transitionInfo.key, transitionInfo.tag, list);
        }
        notifyStartOrEnd(transitionInfo, true);
    }

    private static void setValueAndNotify(IAnimTarget iAnimTarget, Object obj, Object obj2, List<UpdateInfo> list, boolean z) {
        if (!z || (iAnimTarget instanceof ViewTarget)) {
            updateValueAndVelocity(iAnimTarget, list);
        }
        if (list.size() > 40000) {
            iAnimTarget.getNotifier().notifyMassUpdate(obj, obj2);
            return;
        }
        iAnimTarget.getNotifier().notifyPropertyEnd(obj, obj2, list);
        iAnimTarget.getNotifier().notifyUpdate(obj, obj2, list);
    }

    private void update(boolean z, TransitionInfo transitionInfo) {
        ArrayList arrayList = new ArrayList(transitionInfo.updateList);
        if (arrayList.isEmpty()) {
            return;
        }
        checkValue(arrayList);
        if (arrayList.isEmpty()) {
            return;
        }
        setValueAndNotify(transitionInfo.target, transitionInfo.key, transitionInfo.tag, arrayList, z);
    }

    private static void updateValueAndVelocity(IAnimTarget iAnimTarget, List<UpdateInfo> list) {
        for (UpdateInfo updateInfo : list) {
            if (!AnimValueUtils.isInvalid(updateInfo.animInfo.value)) {
                updateInfo.setTargetValue(iAnimTarget);
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 0) {
            TransitionInfo remove = TransitionInfo.sMap.remove(Integer.valueOf(message.arg1));
            if (remove != null) {
                onStart(remove);
                return;
            }
            return;
        }
        if (i == 2) {
            TransitionInfo remove2 = TransitionInfo.sMap.remove(Integer.valueOf(message.arg1));
            if (remove2 == null) {
                remove2 = (TransitionInfo) message.obj;
            }
            if (remove2 != null) {
                onEnd(remove2, message.arg2);
            }
        } else if (i == 3) {
            this.mTarget.animManager.mWaitState.clear();
            return;
        } else if (i == 4) {
            TransitionInfo remove3 = TransitionInfo.sMap.remove(Integer.valueOf(message.arg1));
            if (remove3 != null) {
                this.mTarget.getNotifier().removeListeners(remove3.key);
                this.mTarget.getNotifier().addListeners(remove3.key, remove3.config);
                return;
            }
            return;
        } else if (i != 5) {
            return;
        }
        TransitionInfo remove4 = TransitionInfo.sMap.remove(Integer.valueOf(message.arg1));
        if (remove4 != null) {
            onReplaced(remove4);
        }
    }

    public boolean isInTargetThread() {
        return Looper.myLooper() == getLooper();
    }

    public void update(boolean z) {
        this.mTarget.animManager.getTransitionInfos(this.mTransList);
        Iterator<TransitionInfo> it = this.mTransList.iterator();
        while (it.hasNext()) {
            update(z, it.next());
        }
        this.mTransList.clear();
    }
}
