package miui.cloud.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.reflect.Method;

/* loaded from: classes3.dex */
public class XMainThreadCallback<T> extends XDirectCallback<T> {
    private Handler mHandler;

    /* loaded from: classes3.dex */
    private static class CallRequest {
        public Object[] args;
        public Method method;

        public CallRequest(Method method, Object[] objArr) {
            this.method = method;
            this.args = objArr;
        }
    }

    public XMainThreadCallback(Class<T> cls, T t) {
        super(cls, t);
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: miui.cloud.common.XMainThreadCallback.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                CallRequest callRequest = (CallRequest) message.obj;
                XMainThreadCallback.this.handleCallbackInMainThread(callRequest.method, callRequest.args);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miui.cloud.common.XDirectCallback, miui.cloud.common.XCallback
    public Object handleCallback(Method method, Object[] objArr) throws Throwable {
        Message obtainMessage = this.mHandler.obtainMessage();
        obtainMessage.obj = new CallRequest(method, objArr);
        obtainMessage.sendToTarget();
        return null;
    }

    protected void handleCallbackInMainThread(Method method, Object[] objArr) {
        try {
            super.handleCallback(method, objArr);
        } catch (Throwable unused) {
            XLogger.loge("Exception in callback, but unable to propagate to the original thread. ");
        }
    }
}
