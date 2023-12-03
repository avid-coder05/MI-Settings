package miuix.appcompat.app.floatingactivity;

import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes5.dex */
public class SingleAppFloatingLifecycleObserver extends FloatingLifecycleObserver {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class CloseExitListener extends TransitionListener {
        WeakReference<AppCompatActivity> mHostActivity;

        CloseExitListener(AppCompatActivity appCompatActivity) {
            this.mHostActivity = new WeakReference<>(appCompatActivity);
        }

        @Override // miuix.animation.listener.TransitionListener
        public void onComplete(Object obj) {
            View lastActivityPanel;
            super.onComplete(obj);
            AppCompatActivity appCompatActivity = this.mHostActivity.get();
            if (appCompatActivity == null || appCompatActivity.isDestroyed() || (lastActivityPanel = FloatingActivitySwitcher.getInstance().getLastActivityPanel()) == null) {
                return;
            }
            ((ViewGroup) appCompatActivity.getFloatingBrightPanel().getParent()).getOverlay().remove(lastActivityPanel);
        }
    }

    public SingleAppFloatingLifecycleObserver(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    private void execEnterNotInFloatingWindowMode(AppCompatActivity appCompatActivity) {
        FloatingActivitySwitcher floatingActivitySwitcher = FloatingActivitySwitcher.getInstance();
        if (FloatingAnimHelper.obtainPageIndex(appCompatActivity) < 0 || appCompatActivity.isInFloatingWindowMode() || floatingActivitySwitcher == null) {
            return;
        }
        floatingActivitySwitcher.markActivityOpenEnterAnimExecutedInternal(appCompatActivity);
        FloatingAnimHelper.preformFloatingExitAnimWithClip(appCompatActivity, false);
    }

    private void executeCloseExit(final AppCompatActivity appCompatActivity) {
        final View lastActivityPanel;
        if (FloatingAnimHelper.isSupportTransWithClipAnim() || (lastActivityPanel = FloatingActivitySwitcher.getInstance().getLastActivityPanel()) == null) {
            return;
        }
        lastActivityPanel.post(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.SingleAppFloatingLifecycleObserver$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SingleAppFloatingLifecycleObserver.this.lambda$executeCloseExit$0(lastActivityPanel, appCompatActivity);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$executeCloseExit$0(View view, AppCompatActivity appCompatActivity) {
        View childAt = ((ViewGroup) view).getChildAt(0);
        if (childAt != null) {
            AnimConfig animConfig = FloatingSwitcherAnimHelper.getAnimConfig(0, null);
            animConfig.addListeners(new CloseExitListener(appCompatActivity));
            FloatingSwitcherAnimHelper.executeCloseExitAnimation(childAt, animConfig);
        }
    }

    private void reenterTransition(AppCompatActivity appCompatActivity) {
        int activityIndex;
        AppCompatActivity appCompatActivity2;
        ArrayList<AppCompatActivity> activityList = FloatingActivitySwitcher.getInstance().getActivityList(appCompatActivity.getTaskId());
        if (activityList == null || (activityIndex = FloatingActivitySwitcher.getInstance().getActivityIndex(appCompatActivity) + 1) >= activityList.size() || (appCompatActivity2 = activityList.get(activityIndex)) == null || !appCompatActivity2.isFinishing()) {
            return;
        }
        executeCloseExit(appCompatActivity);
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onCreate() {
        AppCompatActivity activity = FloatingActivitySwitcher.getInstance().getActivity(getActivityIdentity(), getActivityTaskId());
        if (activity != null) {
            if (FloatingActivitySwitcher.getInstance().getPreviousActivity(activity) == null) {
                execEnterNotInFloatingWindowMode(activity);
                return;
            }
            FloatingActivitySwitcher floatingActivitySwitcher = FloatingActivitySwitcher.getInstance();
            if (!activity.isInFloatingWindowMode()) {
                if (floatingActivitySwitcher != null) {
                    floatingActivitySwitcher.markActivityOpenEnterAnimExecutedInternal(activity);
                }
                FloatingAnimHelper.preformFloatingExitAnimWithClip(activity, false);
            } else if (floatingActivitySwitcher == null || floatingActivitySwitcher.isActivityOpenEnterAnimExecuted(activity)) {
            } else {
                floatingActivitySwitcher.markActivityOpenEnterAnimExecutedInternal(activity);
                FloatingAnimHelper.singleAppFloatingActivityEnter(activity);
            }
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onDestroy() {
        FloatingActivitySwitcher floatingActivitySwitcher = FloatingActivitySwitcher.getInstance();
        if (floatingActivitySwitcher == null) {
            return;
        }
        floatingActivitySwitcher.remove(getActivityIdentity(), getActivityTaskId());
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onResume() {
        AppCompatActivity activity = FloatingActivitySwitcher.getInstance().getActivity(getActivityIdentity(), getActivityTaskId());
        if (activity == null || !activity.isInFloatingWindowMode()) {
            return;
        }
        if (FloatingActivitySwitcher.getInstance().getPreviousActivity(activity) != null) {
            activity.hideFloatingDimBackground();
        }
        reenterTransition(activity);
    }
}
