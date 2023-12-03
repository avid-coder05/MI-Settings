package miuix.core.util;

import java.lang.ref.SoftReference;

/* loaded from: classes5.dex */
public abstract class SoftReferenceSingleton<T> {
    private SoftReference<T> mInstance = null;

    protected T createInstance() {
        return null;
    }

    protected T createInstance(Object obj) {
        return null;
    }

    public final T get() {
        T createInstance;
        synchronized (this) {
            SoftReference<T> softReference = this.mInstance;
            if (softReference != null && (createInstance = softReference.get()) != null) {
                updateInstance(createInstance);
            }
            createInstance = createInstance();
            this.mInstance = new SoftReference<>(createInstance);
        }
        return createInstance;
    }

    public final T get(Object obj) {
        T createInstance;
        synchronized (this) {
            SoftReference<T> softReference = this.mInstance;
            if (softReference != null && (createInstance = softReference.get()) != null) {
                updateInstance(createInstance, obj);
            }
            createInstance = createInstance(obj);
            this.mInstance = new SoftReference<>(createInstance);
        }
        return createInstance;
    }

    protected void updateInstance(T t) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateInstance(T t, Object obj) {
    }
}
