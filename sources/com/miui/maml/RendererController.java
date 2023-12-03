package com.miui.maml;

import android.util.Log;
import android.view.MotionEvent;
import com.miui.maml.FramerateTokenList;
import com.miui.maml.elements.FramerateController;
import com.miui.maml.util.HideSdkDependencyUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

/* loaded from: classes2.dex */
public class RendererController implements FramerateTokenList.FramerateChangeListener {
    private float mCurFramerate;
    private FramerateTokenList mFramerateTokenList;
    private boolean mInited;
    private long mLastUpdateSystemTime;
    private Listener mListener;
    private LinkedList<MotionEvent> mMsgQueue;
    private boolean mNeedReset;
    private boolean mPendingRender;
    private boolean mShouldUpdate;
    private ArrayList<FramerateController> mFramerateControllers = new ArrayList<>();
    private boolean mSelfPaused = true;
    private byte[] mLock = new byte[0];
    private long mFrameTime = Long.MAX_VALUE;
    private Object mMsgLock = new Object();
    private float mTouchX = -1.0f;
    private float mTouchY = -1.0f;
    private ArrayList<Runnable> mWriteRunnableQueue = new ArrayList<>();
    private ArrayList<Runnable> mReadRunnableQueue = new ArrayList<>();
    private Object mWriteRunnableQueueLock = new Object();

    /* loaded from: classes2.dex */
    public static abstract class EmptyListener implements Listener {
        @Override // com.miui.maml.RendererController.Listener
        public void finish() {
        }

        @Override // com.miui.maml.RendererController.Listener
        public void init() {
        }

        @Override // com.miui.maml.RendererController.Listener
        public void onHover(MotionEvent motionEvent) {
        }

        @Override // com.miui.maml.RendererController.Listener
        public void onTouch(MotionEvent motionEvent) {
        }

        @Override // com.miui.maml.RendererController.Listener
        public void pause() {
        }

        @Override // com.miui.maml.RendererController.Listener
        public void resume() {
        }
    }

    /* loaded from: classes2.dex */
    public interface IRenderable {
        void doRender();
    }

    /* loaded from: classes2.dex */
    public interface ISelfUpdateRenderable extends IRenderable {
        void forceUpdate();

        void triggerUpdate();
    }

    /* loaded from: classes2.dex */
    public interface Listener extends ISelfUpdateRenderable {
        void finish();

        void init();

        void onHover(MotionEvent motionEvent);

        void onTouch(MotionEvent motionEvent);

        void pause();

        void resume();

        void tick(long j);
    }

    public RendererController() {
        this.mFramerateTokenList = new FramerateTokenList();
        this.mFramerateTokenList = new FramerateTokenList(this);
    }

    private void runRunnables() {
        ArrayList<Runnable> arrayList;
        if (this.mNeedReset) {
            return;
        }
        synchronized (this.mWriteRunnableQueueLock) {
            arrayList = this.mWriteRunnableQueue;
            this.mWriteRunnableQueue = this.mReadRunnableQueue;
            this.mReadRunnableQueue = arrayList;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.mReadRunnableQueue.get(i).run();
        }
        this.mReadRunnableQueue.clear();
    }

    public void addFramerateController(FramerateController framerateController) {
        if (this.mFramerateControllers.contains(framerateController)) {
            return;
        }
        this.mFramerateControllers.add(framerateController);
    }

    public final FramerateTokenList.FramerateToken createToken(String str) {
        return this.mFramerateTokenList.createToken(str);
    }

    public final void doRender() {
        Listener listener = this.mListener;
        if (listener != null) {
            this.mPendingRender = true;
            listener.doRender();
        }
    }

    public final void doneRender() {
        this.mPendingRender = false;
        triggerUpdate();
    }

    public void finish() {
        synchronized (this.mLock) {
            if (this.mInited) {
                Listener listener = this.mListener;
                if (listener != null) {
                    try {
                        listener.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("RendererController", e.toString());
                    }
                }
                synchronized (this.mMsgLock) {
                    if (this.mMsgQueue != null) {
                        while (this.mMsgQueue.size() > 0) {
                            this.mMsgQueue.poll().recycle();
                        }
                    }
                }
                synchronized (this.mWriteRunnableQueueLock) {
                    this.mWriteRunnableQueue.clear();
                }
                this.mInited = false;
                this.mFramerateTokenList.clear();
            }
        }
    }

    public void forceUpdate() {
        Listener listener = this.mListener;
        if (listener != null) {
            listener.forceUpdate();
        }
        RenderVsyncUpdater.getInstance().forceUpdate();
    }

    public final MotionEvent getMessage() {
        MotionEvent motionEvent = null;
        if (this.mMsgQueue == null) {
            return null;
        }
        synchronized (this.mMsgLock) {
            LinkedList<MotionEvent> linkedList = this.mMsgQueue;
            if (linkedList != null) {
                motionEvent = linkedList.poll();
            }
        }
        return motionEvent;
    }

    public final boolean hasInited() {
        return this.mInited;
    }

    public final boolean hasMessage() {
        boolean z = false;
        if (this.mMsgQueue == null) {
            return false;
        }
        synchronized (this.mMsgLock) {
            LinkedList<MotionEvent> linkedList = this.mMsgQueue;
            if (linkedList != null && linkedList.size() > 0) {
                z = true;
            }
        }
        return z;
    }

    public final boolean hasRunnable() {
        boolean z;
        synchronized (this.mWriteRunnableQueueLock) {
            z = !this.mWriteRunnableQueue.isEmpty();
        }
        return z;
    }

    public void init() {
        synchronized (this.mLock) {
            if (this.mInited) {
                return;
            }
            Listener listener = this.mListener;
            if (listener != null) {
                try {
                    listener.init();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("RendererController", e.toString());
                }
            }
            this.mInited = true;
        }
    }

    public final boolean isSelfPaused() {
        return this.mSelfPaused;
    }

    @Override // com.miui.maml.FramerateTokenList.FramerateChangeListener
    public void onFrameRateChage(float f, float f2) {
        if (f2 > 0.0f) {
            triggerUpdate();
        }
    }

    public void onHover(MotionEvent motionEvent) {
        Listener listener;
        if (motionEvent == null || (listener = this.mListener) == null) {
            return;
        }
        try {
            listener.onHover(motionEvent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("RendererController", e.toString());
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            Log.e("RendererController", e2.toString());
        }
    }

    public void onTouch(MotionEvent motionEvent) {
        Listener listener;
        if (motionEvent == null || (listener = this.mListener) == null) {
            return;
        }
        try {
            listener.onTouch(motionEvent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("RendererController", e.toString());
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            Log.e("RendererController", e2.toString());
        }
    }

    public void postRunnable(Runnable runnable) {
        Objects.requireNonNull(runnable, "postRunnable null");
        synchronized (this.mWriteRunnableQueueLock) {
            if (!this.mWriteRunnableQueue.contains(runnable)) {
                this.mWriteRunnableQueue.add(runnable);
            }
        }
        requestUpdate();
    }

    public void postRunnableAtFrontOfQueue(Runnable runnable) {
        Objects.requireNonNull(runnable, "postRunnable null");
        synchronized (this.mWriteRunnableQueueLock) {
            if (!this.mWriteRunnableQueue.contains(runnable)) {
                this.mWriteRunnableQueue.add(0, runnable);
            }
        }
        requestUpdate();
    }

    public final void removeToken(FramerateTokenList.FramerateToken framerateToken) {
        this.mFramerateTokenList.removeToken(framerateToken);
    }

    public final void requestUpdate() {
        this.mShouldUpdate = true;
        forceUpdate();
    }

    public void selfPause() {
        if (this.mInited) {
            synchronized (this.mLock) {
                if (!this.mSelfPaused) {
                    this.mSelfPaused = true;
                    Listener listener = this.mListener;
                    if (listener != null) {
                        listener.pause();
                    }
                }
            }
            this.mPendingRender = false;
        }
    }

    public void selfResume() {
        if (this.mInited) {
            synchronized (this.mLock) {
                if (this.mSelfPaused) {
                    this.mSelfPaused = false;
                    Listener listener = this.mListener;
                    if (listener != null) {
                        listener.resume();
                    }
                }
            }
            RenderVsyncUpdater.getInstance().onResume();
        }
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setNeedReset(boolean z) {
        this.mNeedReset = z;
    }

    public void tick(long j) {
        this.mShouldUpdate = false;
        Listener listener = this.mListener;
        if (listener != null) {
            try {
                listener.tick(j);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("RendererController", e.toString());
            }
        }
        this.mLastUpdateSystemTime = j;
    }

    public void triggerUpdate() {
        Listener listener = this.mListener;
        if (listener != null) {
            listener.triggerUpdate();
        }
        RenderVsyncUpdater.getInstance().triggerUpdate();
    }

    public final long updateFramerate(long j) {
        int size = this.mFramerateControllers.size();
        long j2 = Long.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            long updateFramerate = this.mFramerateControllers.get(i).updateFramerate(j);
            if (updateFramerate < j2) {
                j2 = updateFramerate;
            }
        }
        float framerate = this.mFramerateTokenList.getFramerate();
        float f = this.mCurFramerate;
        if (f != framerate) {
            if (f >= 1.0f && framerate < 1.0f) {
                requestUpdate();
            }
            this.mCurFramerate = framerate;
            this.mFrameTime = framerate != 0.0f ? 1000.0f / framerate : Long.MAX_VALUE;
        }
        long j3 = this.mFrameTime;
        return j3 < j2 ? j3 : j2;
    }

    public long updateIfNeeded(long j) {
        long updateFramerate = updateFramerate(j);
        long j2 = this.mFrameTime;
        long j3 = j2 < Long.MAX_VALUE ? j2 - (j - this.mLastUpdateSystemTime) : Long.MAX_VALUE;
        boolean hasRunnable = hasRunnable();
        if (j3 > 0 && !this.mShouldUpdate && !hasMessage() && !hasRunnable) {
            return j3 < updateFramerate ? j3 : updateFramerate;
        } else if (!this.mPendingRender || hasRunnable) {
            runRunnables();
            MotionEvent message = getMessage();
            if (message != null) {
                if (HideSdkDependencyUtils.MotionEvent_isTouchEvent(message)) {
                    onTouch(message);
                } else {
                    onHover(message);
                }
            }
            tick(j);
            doRender();
            if (this.mShouldUpdate || hasMessage()) {
                return 0L;
            }
            return updateFramerate;
        } else {
            return updateFramerate;
        }
    }
}
