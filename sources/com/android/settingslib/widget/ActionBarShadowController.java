package com.android.settingslib.widget;

import android.app.Activity;
import android.view.View;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/* loaded from: classes2.dex */
public class ActionBarShadowController implements LifecycleObserver {
    static final float ELEVATION_HIGH = 8.0f;
    static final float ELEVATION_LOW = 0.0f;
    private boolean mIsScrollWatcherAttached;
    ScrollChangeWatcher mScrollChangeWatcher;
    private View mScrollView;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public final class ScrollChangeWatcher implements View.OnScrollChangeListener {
        private final Activity mActivity;
        private final View mAnchorView = null;

        ScrollChangeWatcher(Activity activity) {
            this.mActivity = activity;
        }

        @Override // android.view.View.OnScrollChangeListener
        public void onScrollChange(View view, int i, int i2, int i3, int i4) {
            updateDropShadow(view);
        }

        public void updateDropShadow(View view) {
        }
    }

    private ActionBarShadowController(Activity activity, Lifecycle lifecycle, View view) {
        this.mScrollChangeWatcher = new ScrollChangeWatcher(activity);
        this.mScrollView = view;
        attachScrollWatcher();
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void attachScrollWatcher() {
        if (this.mIsScrollWatcherAttached) {
            return;
        }
        this.mIsScrollWatcherAttached = true;
        this.mScrollView.setOnScrollChangeListener(this.mScrollChangeWatcher);
        this.mScrollChangeWatcher.updateDropShadow(this.mScrollView);
    }

    public static ActionBarShadowController attachToView(Activity activity, Lifecycle lifecycle, View view) {
        return new ActionBarShadowController(activity, lifecycle, view);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void detachScrollWatcher() {
        this.mScrollView.setOnScrollChangeListener(null);
        this.mIsScrollWatcherAttached = false;
    }
}
