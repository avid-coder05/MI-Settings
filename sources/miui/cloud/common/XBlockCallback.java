package miui.cloud.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes3.dex */
public class XBlockCallback<T> extends XCallback<T> {
    private CallbackRequest mCallbackRequest;
    private ReentrantLock mCallbackRequestLock;
    private Condition mCallbackRequestReady;
    private Condition mCallbackRequestWait;

    /* loaded from: classes3.dex */
    private static class CallbackRequest {
        public Object[] args;
        public Method method;

        public CallbackRequest(Method method, Object[] objArr) {
            this.method = method;
            this.args = objArr;
        }
    }

    public XBlockCallback(Class<T> cls) {
        super(cls);
        ReentrantLock reentrantLock = new ReentrantLock();
        this.mCallbackRequestLock = reentrantLock;
        this.mCallbackRequestWait = reentrantLock.newCondition();
        this.mCallbackRequestReady = this.mCallbackRequestLock.newCondition();
    }

    @Override // miui.cloud.common.XCallback
    protected Object handleCallback(Method method, Object[] objArr) throws Throwable {
        try {
            try {
                this.mCallbackRequestLock.lock();
                while (this.mCallbackRequest != null) {
                    this.mCallbackRequestReady.signalAll();
                    this.mCallbackRequestWait.await();
                }
                this.mCallbackRequest = new CallbackRequest(method, objArr);
                this.mCallbackRequestReady.signalAll();
            } catch (InterruptedException unused) {
                XLogger.loge("Interrupted while waiting for callback handlers. ");
            }
            this.mCallbackRequestLock.unlock();
            return null;
        } catch (Throwable th) {
            this.mCallbackRequestLock.unlock();
            throw th;
        }
    }

    public void waitForCallBack(T t) throws InterruptedException {
        CallbackRequest callbackRequest;
        try {
            this.mCallbackRequestLock.lock();
            while (true) {
                callbackRequest = this.mCallbackRequest;
                if (callbackRequest != null) {
                    break;
                }
                this.mCallbackRequestWait.signalAll();
                this.mCallbackRequestReady.await();
            }
            try {
                callbackRequest.method.invoke(t, callbackRequest.args);
            } catch (IllegalAccessException unused) {
                XLogger.loge("Bad callback. ");
            } catch (IllegalArgumentException unused2) {
                XLogger.loge("Bad callback. ");
            } catch (InvocationTargetException e) {
                XLogger.loge("Exception in callback, but unable to propagate to the original thread. ", e);
            }
            this.mCallbackRequest = null;
            this.mCallbackRequestWait.signalAll();
        } finally {
            this.mCallbackRequestLock.unlock();
        }
    }
}
