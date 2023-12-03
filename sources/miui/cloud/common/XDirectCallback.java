package miui.cloud.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes3.dex */
public class XDirectCallback<T> extends XCallback<T> {
    private T mCallback;

    public XDirectCallback(Class<T> cls, T t) {
        super(cls);
        this.mCallback = t;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miui.cloud.common.XCallback
    public Object handleCallback(Method method, Object[] objArr) throws Throwable {
        T t = this.mCallback;
        if (t != null) {
            try {
                return method.invoke(t, objArr);
            } catch (IllegalAccessException e) {
                XLogger.loge("Bad callback. ");
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e2) {
                XLogger.loge("Bad callback. ");
                throw new RuntimeException(e2);
            } catch (InvocationTargetException e3) {
                if (e3.getCause() == null) {
                    throw e3;
                }
                throw e3.getCause();
            }
        }
        return null;
    }
}
