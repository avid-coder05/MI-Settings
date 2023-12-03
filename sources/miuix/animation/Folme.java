package miuix.animation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import miuix.animation.controller.FolmeFont;
import miuix.animation.controller.FolmeHover;
import miuix.animation.controller.FolmeTouch;
import miuix.animation.controller.FolmeVisible;
import miuix.animation.controller.StateComposer;
import miuix.animation.internal.ThreadPoolUtil;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.LogUtils;

/* loaded from: classes5.dex */
public class Folme {
    private static float DEFALUT_THRESHOLD_VELOCITY;
    private static final ConcurrentHashMap<IAnimTarget, FolmeImpl> sImplMap;
    private static final Handler sMainHandler;
    private static AtomicReference<Float> sTimeRatio;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class FolmeImpl implements IFolme {
        private IHoverStyle mHover;
        private IStateStyle mState;
        private IAnimTarget[] mTargets;
        private ITouchStyle mTouch;
        private IVisibleStyle mVisible;

        private FolmeImpl(IAnimTarget... iAnimTargetArr) {
            this.mTargets = iAnimTargetArr;
            Folme.sendToTargetMessage(false);
            Folme.performTargetCleanForTooMuchInvalid();
        }

        void clean() {
            ITouchStyle iTouchStyle = this.mTouch;
            if (iTouchStyle != null) {
                iTouchStyle.clean();
            }
            IVisibleStyle iVisibleStyle = this.mVisible;
            if (iVisibleStyle != null) {
                iVisibleStyle.clean();
            }
            IStateStyle iStateStyle = this.mState;
            if (iStateStyle != null) {
                iStateStyle.clean();
            }
            IHoverStyle iHoverStyle = this.mHover;
            if (iHoverStyle != null) {
                iHoverStyle.clean();
            }
        }

        void end() {
            ITouchStyle iTouchStyle = this.mTouch;
            if (iTouchStyle != null) {
                iTouchStyle.end(new Object[0]);
            }
            IVisibleStyle iVisibleStyle = this.mVisible;
            if (iVisibleStyle != null) {
                iVisibleStyle.end(new Object[0]);
            }
            IStateStyle iStateStyle = this.mState;
            if (iStateStyle != null) {
                iStateStyle.end(new Object[0]);
            }
            IHoverStyle iHoverStyle = this.mHover;
            if (iHoverStyle != null) {
                iHoverStyle.end(new Object[0]);
            }
        }

        @Override // miuix.animation.IFolme
        public IHoverStyle hover() {
            if (this.mHover == null) {
                this.mHover = new FolmeHover(this.mTargets);
            }
            return this.mHover;
        }

        @Override // miuix.animation.IFolme
        public IStateStyle state() {
            if (this.mState == null) {
                this.mState = StateComposer.composeStyle(this.mTargets);
            }
            return this.mState;
        }

        @Override // miuix.animation.IFolme
        public ITouchStyle touch() {
            if (this.mTouch == null) {
                FolmeTouch folmeTouch = new FolmeTouch(this.mTargets);
                folmeTouch.setFontStyle(new FolmeFont());
                this.mTouch = folmeTouch;
            }
            return this.mTouch;
        }

        @Override // miuix.animation.IFolme
        public IVisibleStyle visible() {
            if (this.mVisible == null) {
                this.mVisible = new FolmeVisible(this.mTargets);
            }
            return this.mVisible;
        }
    }

    static {
        ThreadPoolUtil.post(new Runnable() { // from class: miuix.animation.Folme.1
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.getLogEnableInfo();
            }
        });
        sTimeRatio = new AtomicReference<>(Float.valueOf(1.0f));
        sImplMap = new ConcurrentHashMap<>();
        DEFALUT_THRESHOLD_VELOCITY = 12.5f;
        sMainHandler = new Handler(Looper.getMainLooper()) { // from class: miuix.animation.Folme.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 1) {
                    Folme.clearTargets();
                    Folme.sendToTargetMessage(true);
                } else if (i != 2) {
                    super.handleMessage(message);
                } else {
                    Folme.clearInvalidTargets((List) message.obj);
                }
            }
        };
    }

    @SafeVarargs
    public static <T> void clean(T... tArr) {
        if (CommonUtils.isArrayEmpty(tArr)) {
            Iterator<IAnimTarget> it = sImplMap.keySet().iterator();
            while (it.hasNext()) {
                cleanAnimTarget(it.next());
            }
            return;
        }
        for (T t : tArr) {
            doClean(t);
        }
    }

    private static void cleanAnimTarget(IAnimTarget iAnimTarget) {
        if (iAnimTarget != null) {
            iAnimTarget.clean();
            FolmeImpl remove = sImplMap.remove(iAnimTarget);
            iAnimTarget.animManager.clear();
            if (remove != null) {
                remove.clean();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void clearInvalidTargets(List<IAnimTarget> list) {
        for (IAnimTarget iAnimTarget : list) {
            if (!iAnimTarget.isValid() && !iAnimTarget.animManager.isAnimRunning(new FloatProperty[0]) && !iAnimTarget.animManager.isAnimSetup() && iAnimTarget.isValidFlag()) {
                clean(iAnimTarget);
            }
        }
    }

    private static void clearTargetMessage(int i) {
        Handler handler = sMainHandler;
        if (handler.hasMessages(i)) {
            handler.removeMessages(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void clearTargets() {
        for (IAnimTarget iAnimTarget : sImplMap.keySet()) {
            if (!iAnimTarget.isValid() || (iAnimTarget.hasFlags(1L) && !iAnimTarget.animManager.isAnimRunning(new FloatProperty[0]) && !iAnimTarget.animManager.isAnimSetup() && iAnimTarget.isValidFlag())) {
                clean(iAnimTarget);
            }
        }
    }

    private static <T> void doClean(T t) {
        cleanAnimTarget(getTarget(t, null));
    }

    public static <T> void end(T... tArr) {
        FolmeImpl folmeImpl;
        for (T t : tArr) {
            IAnimTarget target = getTarget(t, null);
            if (target != null && (folmeImpl = sImplMap.get(target)) != null) {
                folmeImpl.end();
            }
        }
    }

    private static FolmeImpl fillTargetArrayAndGetImpl(View[] viewArr, IAnimTarget[] iAnimTargetArr) {
        FolmeImpl folmeImpl = null;
        boolean z = false;
        for (int i = 0; i < viewArr.length; i++) {
            iAnimTargetArr[i] = getTarget(viewArr[i], ViewTarget.sCreator);
            FolmeImpl folmeImpl2 = sImplMap.get(iAnimTargetArr[i]);
            if (folmeImpl == null) {
                folmeImpl = folmeImpl2;
            } else if (folmeImpl != folmeImpl2) {
                z = true;
            }
        }
        if (z) {
            return null;
        }
        return folmeImpl;
    }

    public static <T> IAnimTarget getTarget(T t, ITargetCreator<T> iTargetCreator) {
        IAnimTarget createTarget;
        if (t == null) {
            return null;
        }
        if (t instanceof IAnimTarget) {
            return (IAnimTarget) t;
        }
        for (IAnimTarget iAnimTarget : sImplMap.keySet()) {
            Object targetObject = iAnimTarget.getTargetObject();
            if (targetObject != null && targetObject.equals(t)) {
                return iAnimTarget;
            }
        }
        if (iTargetCreator == null || (createTarget = iTargetCreator.createTarget(t)) == null) {
            return null;
        }
        useAt(createTarget);
        return createTarget;
    }

    public static Collection<IAnimTarget> getTargets() {
        if (LogUtils.isLogEnabled()) {
            Iterator<IAnimTarget> it = sImplMap.keySet().iterator();
            int i = 0;
            while (it.hasNext()) {
                if (!it.next().isValid()) {
                    i++;
                }
            }
            LogUtils.debug("current sImplMap total : " + sImplMap.size() + "  , target invalid count :  " + i, new Object[0]);
        }
        return sImplMap.keySet();
    }

    public static float getTimeRatio() {
        return sTimeRatio.get().floatValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void performTargetCleanForTooMuchInvalid() {
        if (sImplMap.size() <= 0 || r0.size() % PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID != 0) {
            return;
        }
        ThreadPoolUtil.post(new Runnable() { // from class: miuix.animation.Folme.3
            @Override // java.lang.Runnable
            public void run() {
                ArrayList arrayList = new ArrayList();
                for (IAnimTarget iAnimTarget : Folme.sImplMap.keySet()) {
                    if (!iAnimTarget.isValid()) {
                        arrayList.add(iAnimTarget);
                    }
                }
                if (arrayList.size() > 0) {
                    Message obtain = Message.obtain();
                    obtain.obj = arrayList;
                    obtain.what = 2;
                    Folme.sMainHandler.sendMessageDelayed(obtain, 1000L);
                }
            }
        });
    }

    public static <T> void post(T t, Runnable runnable) {
        IAnimTarget target = getTarget(t, null);
        if (target != null) {
            target.post(runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendToTargetMessage(boolean z) {
        clearTargetMessage(1);
        if (z && LogUtils.isLogEnabled()) {
            for (IAnimTarget iAnimTarget : sImplMap.keySet()) {
                LogUtils.debug("exist target:" + iAnimTarget.getTargetObject() + " , target isValid : " + iAnimTarget.isValid(), new Object[0]);
            }
        }
        if (sImplMap.size() > 0) {
            sMainHandler.sendEmptyMessageDelayed(1, 20000L);
        } else {
            clearTargetMessage(1);
        }
    }

    public static IFolme useAt(IAnimTarget iAnimTarget) {
        ConcurrentHashMap<IAnimTarget, FolmeImpl> concurrentHashMap = sImplMap;
        FolmeImpl folmeImpl = concurrentHashMap.get(iAnimTarget);
        if (folmeImpl == null) {
            FolmeImpl folmeImpl2 = new FolmeImpl(new IAnimTarget[]{iAnimTarget});
            FolmeImpl putIfAbsent = concurrentHashMap.putIfAbsent(iAnimTarget, folmeImpl2);
            return putIfAbsent != null ? putIfAbsent : folmeImpl2;
        }
        return folmeImpl;
    }

    public static IFolme useAt(View... viewArr) {
        if (viewArr.length != 0) {
            if (viewArr.length == 1) {
                return useAt(getTarget(viewArr[0], ViewTarget.sCreator));
            }
            int length = viewArr.length;
            IAnimTarget[] iAnimTargetArr = new IAnimTarget[length];
            FolmeImpl fillTargetArrayAndGetImpl = fillTargetArrayAndGetImpl(viewArr, iAnimTargetArr);
            if (fillTargetArrayAndGetImpl == null) {
                fillTargetArrayAndGetImpl = new FolmeImpl(iAnimTargetArr);
                for (int i = 0; i < length; i++) {
                    FolmeImpl put = sImplMap.put(iAnimTargetArr[i], fillTargetArrayAndGetImpl);
                    if (put != null) {
                        put.clean();
                    }
                }
            }
            return fillTargetArrayAndGetImpl;
        }
        throw new IllegalArgumentException("useAt can not be applied to empty views array");
    }

    public static IStateStyle useValue(Object... objArr) {
        IFolme useAt;
        if (objArr.length > 0) {
            useAt = useAt(getTarget(objArr[0], ValueTarget.sCreator));
        } else {
            ValueTarget valueTarget = new ValueTarget();
            valueTarget.setFlags(1L);
            useAt = useAt(valueTarget);
        }
        return useAt.state();
    }
}
