package miuix.appcompat.app.floatingactivity.multiapp;

import android.view.View;
import android.view.ViewGroup;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.FloatingAnimHelper;
import miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver;
import miuix.appcompat.app.floatingactivity.FloatingSwitcherAnimHelper;

/* loaded from: classes5.dex */
public class MultiAppFloatingLifecycleObserver extends FloatingLifecycleObserver {
    public MultiAppFloatingLifecycleObserver(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
    }

    private void execEnterNotInFloatingWindowMode(AppCompatActivity appCompatActivity) {
        int obtainPageIndex = FloatingAnimHelper.obtainPageIndex(appCompatActivity);
        boolean z = obtainPageIndex >= 0 && !appCompatActivity.isInFloatingWindowMode();
        MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher = MultiAppFloatingActivitySwitcher.getInstance();
        if (!z || obtainPageIndex != 0) {
            if (z) {
                multiAppFloatingActivitySwitcher.markActivityOpenEnterAnimExecutedInternal(appCompatActivity.getTaskId(), appCompatActivity.getActivityIdentity());
                return;
            }
            return;
        }
        multiAppFloatingActivitySwitcher.markActivityOpenEnterAnimExecutedInternal(appCompatActivity.getTaskId(), appCompatActivity.getActivityIdentity());
        if (FloatingAnimHelper.isSupportTransWithClipAnim()) {
            FloatingAnimHelper.preformFloatingExitAnimWithClip(appCompatActivity, false);
        } else {
            FloatingAnimHelper.execFloatingWindowEnterAnimRomNormal(appCompatActivity);
        }
    }

    private void executeCloseExit(AppCompatActivity appCompatActivity) {
        final View lastActivityPanel = MultiAppFloatingActivitySwitcher.getInstance().getLastActivityPanel();
        if (lastActivityPanel != null) {
            final View floatingBrightPanel = appCompatActivity.getFloatingBrightPanel();
            lastActivityPanel.post(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingLifecycleObserver.2
                @Override // java.lang.Runnable
                public void run() {
                    View childAt = ((ViewGroup) lastActivityPanel).getChildAt(0);
                    AnimConfig animConfig = FloatingSwitcherAnimHelper.getAnimConfig(0, null);
                    animConfig.addListeners(new TransitionListener() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingLifecycleObserver.2.1
                        @Override // miuix.animation.listener.TransitionListener
                        public void onComplete(Object obj) {
                            super.onComplete(obj);
                            ((ViewGroup) floatingBrightPanel.getParent()).getOverlay().remove(lastActivityPanel);
                            MultiAppFloatingActivitySwitcher.getInstance().setLastActivityPanel(null);
                        }
                    });
                    FloatingSwitcherAnimHelper.executeCloseExitAnimation(childAt, animConfig);
                }
            });
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onCreate() {
        final AppCompatActivity activity = MultiAppFloatingActivitySwitcher.getInstance().getActivity(getActivityTaskId(), getActivityIdentity());
        if (activity != null) {
            MultiAppFloatingActivitySwitcher.getInstance().postEnterAnimationTask(getActivityTaskId(), getActivityIdentity(), new Runnable() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingLifecycleObserver.1
                @Override // java.lang.Runnable
                public void run() {
                    MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher = MultiAppFloatingActivitySwitcher.getInstance();
                    if (multiAppFloatingActivitySwitcher.getCurrentPageCount(MultiAppFloatingLifecycleObserver.this.getActivityTaskId()) > 1 || multiAppFloatingActivitySwitcher.getServicePageCount(MultiAppFloatingLifecycleObserver.this.getActivityTaskId()) > 1) {
                        if (FloatingAnimHelper.isSupportTransWithClipAnim()) {
                            AppCompatActivity appCompatActivity = activity;
                            FloatingAnimHelper.preformFloatingExitAnimWithClip(appCompatActivity, appCompatActivity.isInFloatingWindowMode());
                        } else if (activity.isInFloatingWindowMode()) {
                            activity.executeOpenEnterAnimation();
                            MultiAppFloatingActivitySwitcher.getInstance().notifyPreviousActivitySlide(MultiAppFloatingLifecycleObserver.this.getActivityTaskId(), MultiAppFloatingLifecycleObserver.this.getActivityIdentity());
                        }
                    }
                }
            });
            execEnterNotInFloatingWindowMode(activity);
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onDestroy() {
        MultiAppFloatingActivitySwitcher.getInstance().remove(getActivityTaskId(), getActivityIdentity());
        if (MultiAppFloatingActivitySwitcher.getInstance().getCurrentPageCount(getActivityTaskId()) <= 0) {
            MultiAppFloatingActivitySwitcher.getInstance().setLastActivityPanel(null);
        }
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onPause() {
        MultiAppFloatingActivitySwitcher.getInstance().updateResumeState(getActivityTaskId(), getActivityIdentity(), false);
    }

    @Override // miuix.appcompat.app.floatingactivity.FloatingLifecycleObserver
    public void onResume() {
        AppCompatActivity activity = MultiAppFloatingActivitySwitcher.getInstance().getActivity(getActivityTaskId(), getActivityIdentity());
        if (activity != null) {
            MultiAppFloatingActivitySwitcher.getInstance().updateResumeState(getActivityTaskId(), getActivityIdentity(), true);
            MultiAppFloatingActivitySwitcher.getInstance().checkBg(getActivityTaskId(), getActivityIdentity());
            if (!MultiAppFloatingActivitySwitcher.getInstance().isAboveActivityFinishing(getActivityTaskId(), getActivityIdentity()) || FloatingAnimHelper.isSupportTransWithClipAnim()) {
                return;
            }
            activity.executeCloseEnterAnimation();
            executeCloseExit(activity);
        }
    }
}
