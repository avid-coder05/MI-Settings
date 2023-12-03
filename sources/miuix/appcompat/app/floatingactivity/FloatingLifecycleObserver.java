package miuix.appcompat.app.floatingactivity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes5.dex */
public class FloatingLifecycleObserver implements LifecycleObserver {
    protected String mActivityIdentity;
    protected int mActivityTaskId;

    public FloatingLifecycleObserver(AppCompatActivity appCompatActivity) {
        this.mActivityIdentity = appCompatActivity.getActivityIdentity();
        this.mActivityTaskId = appCompatActivity.getTaskId();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getActivityIdentity() {
        return this.mActivityIdentity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getActivityTaskId() {
        return this.mActivityTaskId;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
    }
}
