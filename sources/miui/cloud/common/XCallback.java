package miui.cloud.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* loaded from: classes3.dex */
public abstract class XCallback<T> {
    private Class<T> mInterface;

    public XCallback(Class<T> cls) {
        this.mInterface = cls;
    }

    public final T asInterface() {
        return (T) Proxy.newProxyInstance(this.mInterface.getClassLoader(), new Class[]{this.mInterface}, new InvocationHandler() { // from class: miui.cloud.common.XCallback.1
            @Override // java.lang.reflect.InvocationHandler
            public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                return XCallback.this.handleCallback(method, objArr);
            }
        });
    }

    protected abstract Object handleCallback(Method method, Object[] objArr) throws Throwable;
}
