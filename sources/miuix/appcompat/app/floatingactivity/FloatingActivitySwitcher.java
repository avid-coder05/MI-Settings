package miuix.appcompat.app.floatingactivity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.helper.FloatingHelperFactory;
import miuix.appcompat.app.floatingactivity.helper.PhoneFloatingActivityHelper;

/* loaded from: classes5.dex */
public class FloatingActivitySwitcher {
    private static final HashMap<String, ActivitySpec> mActivityInfoStack = new HashMap<>();
    private static FloatingActivitySwitcher sInstance;
    private boolean mEnableDragToDismiss;
    private WeakReference<View> mLastActivityPanel;
    private final SparseArray<ArrayList<AppCompatActivity>> mActivityCache = new SparseArray<>();
    private final ArrayList<AppCompatActivity> mWillDestroyList = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class ActivitySpec implements Parcelable {
        public static final Parcelable.Creator<ActivitySpec> CREATOR = new Parcelable.Creator<ActivitySpec>() { // from class: miuix.appcompat.app.floatingactivity.FloatingActivitySwitcher.ActivitySpec.1
            @Override // android.os.Parcelable.Creator
            public ActivitySpec createFromParcel(Parcel parcel) {
                return new ActivitySpec(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public ActivitySpec[] newArray(int i) {
                return new ActivitySpec[i];
            }
        };
        private String activityClassName;
        private String identity;
        private int index;
        private boolean isOpenEnterAnimExecuted;
        private boolean isPreDestroy = false;
        private int taskId;

        protected ActivitySpec(Parcel parcel) {
            this.activityClassName = "";
            this.index = 0;
            this.taskId = 0;
            this.isOpenEnterAnimExecuted = false;
            this.activityClassName = parcel.readString();
            this.index = parcel.readInt();
            this.identity = parcel.readString();
            this.taskId = parcel.readInt();
            this.isOpenEnterAnimExecuted = parcel.readByte() != 0;
        }

        public ActivitySpec(String str, int i, String str2, int i2, boolean z) {
            this.activityClassName = "";
            this.index = 0;
            this.taskId = 0;
            this.isOpenEnterAnimExecuted = false;
            this.activityClassName = str;
            this.index = i;
            this.identity = str2;
            this.taskId = i2;
            this.isOpenEnterAnimExecuted = z;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "{ activityClassName : " + this.activityClassName + "; index : " + this.index + "; identity : " + this.identity + "; taskId : " + this.taskId + "; isOpenEnterAnimExecuted : " + this.isOpenEnterAnimExecuted + "; }";
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.activityClassName);
            parcel.writeInt(this.index);
            parcel.writeString(this.identity);
            parcel.writeInt(this.taskId);
            parcel.writeByte(this.isOpenEnterAnimExecuted ? (byte) 1 : (byte) 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class DefineOnFloatingActivityCallback implements OnFloatingCallback {
        protected String mActivityIdentity;
        protected int mActivityTaskId;

        public DefineOnFloatingActivityCallback(AppCompatActivity appCompatActivity) {
            this.mActivityIdentity = appCompatActivity.getActivityIdentity();
            this.mActivityTaskId = appCompatActivity.getTaskId();
        }

        private void addLastActivityPanel(AppCompatActivity appCompatActivity) {
            ViewGroup viewGroup;
            View lastActivityPanel = FloatingActivitySwitcher.getInstance().getLastActivityPanel();
            if (lastActivityPanel == null || (viewGroup = (ViewGroup) appCompatActivity.getFloatingBrightPanel().getParent()) == null) {
                return;
            }
            viewGroup.getOverlay().clear();
            viewGroup.getOverlay().add(lastActivityPanel);
        }

        private boolean checkFinishEnable(int i) {
            return !FloatingActivitySwitcher.this.mEnableDragToDismiss && (i == 1 || i == 2);
        }

        private boolean shouldTopFloatingClose(int i) {
            ArrayList arrayList = (ArrayList) FloatingActivitySwitcher.this.mActivityCache.get(getActivityTaskId());
            return (i == 4 || i == 3) && (arrayList != null && arrayList.size() > 1);
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void closeAllPage() {
            Iterator it = FloatingActivitySwitcher.this.mWillDestroyList.iterator();
            while (it.hasNext()) {
                ((AppCompatActivity) it.next()).realFinish();
            }
            FloatingActivitySwitcher.this.mWillDestroyList.clear();
        }

        protected String getActivityIdentity() {
            return this.mActivityIdentity;
        }

        protected int getActivityTaskId() {
            return this.mActivityTaskId;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void getSnapShotAndSetPanel(AppCompatActivity appCompatActivity) {
            AppCompatActivity previousActivity;
            View generateSnapShotView;
            if (appCompatActivity == null || (previousActivity = FloatingActivitySwitcher.getInstance().getPreviousActivity(appCompatActivity)) == null) {
                return;
            }
            int i = 0;
            do {
                generateSnapShotView = SnapShotViewHelper.generateSnapShotView(previousActivity, appCompatActivity);
                i++;
                if (generateSnapShotView != null) {
                    break;
                }
            } while (i < 3);
            FloatingActivitySwitcher.getInstance().setLastActivityPanel(generateSnapShotView);
            addLastActivityPanel(previousActivity);
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public boolean isFirstPageEnterAnimExecuteEnable() {
            ArrayList arrayList;
            ActivitySpec activitySpec = (ActivitySpec) FloatingActivitySwitcher.mActivityInfoStack.get(getActivityIdentity());
            if (activitySpec == null || (arrayList = (ArrayList) FloatingActivitySwitcher.this.mActivityCache.get(activitySpec.taskId)) == null) {
                return true;
            }
            if (arrayList.size() > 1) {
                return false;
            }
            AppCompatActivity appCompatActivity = arrayList.size() == 0 ? null : (AppCompatActivity) arrayList.get(0);
            if (appCompatActivity == null || ((ActivitySpec) FloatingActivitySwitcher.mActivityInfoStack.get(appCompatActivity.getActivityIdentity())) == null) {
                return true;
            }
            return !activitySpec.isOpenEnterAnimExecuted;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public boolean isFirstPageExitAnimExecuteEnable() {
            ArrayList arrayList;
            ActivitySpec activitySpec = (ActivitySpec) FloatingActivitySwitcher.mActivityInfoStack.get(getActivityIdentity());
            return activitySpec == null || (arrayList = (ArrayList) FloatingActivitySwitcher.this.mActivityCache.get(activitySpec.taskId)) == null || arrayList.size() == 1;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void markActivityOpenEnterAnimExecuted(AppCompatActivity appCompatActivity) {
            FloatingActivitySwitcher.this.markActivityOpenEnterAnimExecutedInternal(appCompatActivity);
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void onDragEnd() {
            FloatingActivitySwitcher.this.showBehindPages(getActivityIdentity());
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void onDragStart() {
            FloatingActivitySwitcher.this.hideBehindPages(getActivityIdentity());
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback
        public boolean onFinish(int i) {
            if (checkFinishEnable(i)) {
                return false;
            }
            if (shouldTopFloatingClose(i)) {
                FloatingActivitySwitcher.this.closeTopActivity(getActivityIdentity());
            } else {
                FloatingActivitySwitcher.this.closeAllFloatingPage(getActivityIdentity());
            }
            return false;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void onHideBehindPage() {
            FloatingActivitySwitcher.this.hideBehindPages(getActivityIdentity());
        }
    }

    private FloatingActivitySwitcher() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeTopActivity(String str) {
        ArrayList<AppCompatActivity> arrayList;
        ActivitySpec activitySpec = mActivityInfoStack.get(str);
        if (activitySpec == null || (arrayList = this.mActivityCache.get(activitySpec.taskId)) == null || arrayList.size() <= 0) {
            return;
        }
        arrayList.get(arrayList.size() - 1).realFinish();
    }

    private void execEnterNormalRom(AppCompatActivity appCompatActivity) {
        if (FloatingAnimHelper.isSupportTransWithClipAnim()) {
            return;
        }
        if (appCompatActivity.isInFloatingWindowMode()) {
            FloatingAnimHelper.clearFloatingWindowAnim(appCompatActivity);
        } else {
            FloatingAnimHelper.execFloatingWindowEnterAnimRomNormal(appCompatActivity);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static FloatingActivitySwitcher getInstance() {
        return sInstance;
    }

    private static ActivitySpec getOrCreateActivitySpec(AppCompatActivity appCompatActivity) {
        ActivitySpec activitySpec = mActivityInfoStack.get(appCompatActivity.getActivityIdentity());
        return activitySpec != null ? activitySpec : new ActivitySpec(appCompatActivity.getClass().getSimpleName(), getInstance().getActivityIndex(appCompatActivity), appCompatActivity.getActivityIdentity(), appCompatActivity.getTaskId(), false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideBehindPages(String str) {
        ActivitySpec activitySpec = mActivityInfoStack.get(str);
        if (activitySpec != null) {
            ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(activitySpec.taskId);
            int i = -1;
            if (arrayList != null) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    if (arrayList.get(i2).getActivityIdentity().equals(str)) {
                        i = i2;
                    }
                }
            }
            for (int i3 = i - 1; i3 >= 0; i3--) {
                arrayList.get(i3).hideFloatingBrightPanel();
            }
        }
    }

    private void hideTopBgs(AppCompatActivity appCompatActivity) {
        ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(appCompatActivity.getTaskId());
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                AppCompatActivity appCompatActivity2 = arrayList.get(i);
                ActivitySpec activitySpec = mActivityInfoStack.get(appCompatActivity2.getActivityIdentity());
                if (activitySpec != null && activitySpec.index != 0) {
                    appCompatActivity2.hideFloatingDimBackground();
                }
            }
        }
    }

    private void init(AppCompatActivity appCompatActivity, Bundle bundle) {
        if (FloatingHelperFactory.get(appCompatActivity) instanceof PhoneFloatingActivityHelper) {
            return;
        }
        stashActivity(appCompatActivity, bundle);
        appCompatActivity.getLifecycle().addObserver(new SingleAppFloatingLifecycleObserver(appCompatActivity));
        appCompatActivity.setEnableSwipToDismiss(this.mEnableDragToDismiss);
        appCompatActivity.setOnFloatingCallback(new DefineOnFloatingActivityCallback(appCompatActivity));
    }

    public static void install(AppCompatActivity appCompatActivity, Bundle bundle) {
        install(appCompatActivity, true, bundle);
    }

    private static void install(AppCompatActivity appCompatActivity, boolean z, Bundle bundle) {
        if (sInstance == null) {
            FloatingActivitySwitcher floatingActivitySwitcher = new FloatingActivitySwitcher();
            sInstance = floatingActivitySwitcher;
            floatingActivitySwitcher.mEnableDragToDismiss = z;
        }
        sInstance.init(appCompatActivity, bundle);
    }

    private boolean isActivityStashed(AppCompatActivity appCompatActivity) {
        return mActivityInfoStack.get(appCompatActivity.getActivityIdentity()) != null;
    }

    public static void onSaveInstanceState(AppCompatActivity appCompatActivity, Bundle bundle) {
        if (getInstance() == null || bundle == null) {
            return;
        }
        bundle.putParcelable("miuix_floating_activity_info_key", getOrCreateActivitySpec(appCompatActivity));
    }

    private ActivitySpec recoverFromSavedInstanceState(AppCompatActivity appCompatActivity, Bundle bundle) {
        ActivitySpec activitySpec = (ActivitySpec) bundle.getParcelable("miuix_floating_activity_info_key");
        if (activitySpec == null) {
            Log.w("FloatingActivity", "FloatingActivitySwitcher restore a full ActivitySpec instance with savedInstanceState fail, Check if you have replaced the theme in the float window !");
            return new ActivitySpec(appCompatActivity.getClass().getSimpleName(), 0, appCompatActivity.getActivityIdentity(), appCompatActivity.getTaskId(), false);
        }
        return activitySpec;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showBehindPages(String str) {
        ActivitySpec activitySpec = mActivityInfoStack.get(str);
        if (activitySpec != null) {
            ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(activitySpec.taskId);
            int i = -1;
            if (arrayList != null) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    if (arrayList.get(i2).getActivityIdentity().equals(str)) {
                        i = i2;
                    }
                }
            }
            for (int i3 = i - 1; i3 >= 0; i3--) {
                arrayList.get(i3).showFloatingBrightPanel();
            }
        }
    }

    private void stashActivity(AppCompatActivity appCompatActivity, Bundle bundle) {
        if (!isActivityStashed(appCompatActivity)) {
            int taskId = appCompatActivity.getTaskId();
            ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(taskId);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mActivityCache.put(taskId, arrayList);
            }
            if (bundle != null) {
                ActivitySpec recoverFromSavedInstanceState = recoverFromSavedInstanceState(appCompatActivity, bundle);
                recoverFromSavedInstanceState.activityClassName = appCompatActivity.getClass().getSimpleName();
                recoverFromSavedInstanceState.identity = appCompatActivity.getActivityIdentity();
                int i = recoverFromSavedInstanceState != null ? recoverFromSavedInstanceState.index : 0;
                arrayList.add(i <= arrayList.size() ? i : 0, appCompatActivity);
                mActivityInfoStack.put(appCompatActivity.getActivityIdentity(), recoverFromSavedInstanceState);
            } else {
                arrayList.add(appCompatActivity);
                mActivityInfoStack.put(appCompatActivity.getActivityIdentity(), new ActivitySpec(appCompatActivity.getClass().getSimpleName(), getInstance().getActivityIndex(appCompatActivity), appCompatActivity.getActivityIdentity(), appCompatActivity.getTaskId(), false));
            }
        }
        ActivitySpec activitySpec = mActivityInfoStack.get(appCompatActivity.getActivityIdentity());
        if (activitySpec != null) {
            FloatingAnimHelper.markedPageIndex(appCompatActivity, activitySpec.index);
        }
        execEnterNormalRom(appCompatActivity);
        hideTopBgs(appCompatActivity);
    }

    public void clear() {
        this.mActivityCache.clear();
        mActivityInfoStack.clear();
        this.mLastActivityPanel = null;
        sInstance = null;
    }

    public void closeAllFloatingPage(String str) {
        ArrayList<AppCompatActivity> arrayList;
        ActivitySpec activitySpec = mActivityInfoStack.get(str);
        if (activitySpec == null || (arrayList = this.mActivityCache.get(activitySpec.taskId)) == null) {
            return;
        }
        for (int size = arrayList.size() - 2; size >= 0; size--) {
            AppCompatActivity appCompatActivity = arrayList.get(size);
            appCompatActivity.hideFloatingBrightPanel();
            this.mWillDestroyList.add(appCompatActivity);
            arrayList.remove(appCompatActivity);
            mActivityInfoStack.remove(appCompatActivity.getActivityIdentity());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatActivity getActivity(String str, int i) {
        ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(i);
        if (arrayList != null) {
            Iterator<AppCompatActivity> it = arrayList.iterator();
            while (it.hasNext()) {
                AppCompatActivity next = it.next();
                if (next.getActivityIdentity().equals(str)) {
                    return next;
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getActivityIndex(AppCompatActivity appCompatActivity) {
        ArrayList<AppCompatActivity> arrayList;
        if (appCompatActivity == null || (arrayList = this.mActivityCache.get(appCompatActivity.getTaskId())) == null) {
            return -1;
        }
        return arrayList.indexOf(appCompatActivity);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<AppCompatActivity> getActivityList(int i) {
        return this.mActivityCache.get(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getLastActivityPanel() {
        WeakReference<View> weakReference = this.mLastActivityPanel;
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatActivity getPreviousActivity(AppCompatActivity appCompatActivity) {
        if (appCompatActivity != null) {
            ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(appCompatActivity.getTaskId());
            int indexOf = arrayList != null ? arrayList.indexOf(appCompatActivity) : -1;
            if (indexOf > 0) {
                return arrayList.get(indexOf - 1);
            }
            return null;
        }
        return null;
    }

    public boolean isActivityOpenEnterAnimExecuted(AppCompatActivity appCompatActivity) {
        ActivitySpec activitySpec = mActivityInfoStack.get(appCompatActivity.getActivityIdentity());
        return activitySpec != null && activitySpec.isOpenEnterAnimExecuted;
    }

    public void markActivityOpenEnterAnimExecutedInternal(AppCompatActivity appCompatActivity) {
        ActivitySpec activitySpec = mActivityInfoStack.get(appCompatActivity.getActivityIdentity());
        if (activitySpec != null) {
            activitySpec.isOpenEnterAnimExecuted = true;
        }
    }

    public void remove(String str, int i) {
        ArrayList<AppCompatActivity> arrayList = this.mActivityCache.get(i);
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                if (arrayList.get(size).getActivityIdentity().equals(str)) {
                    arrayList.remove(size);
                }
            }
            if (arrayList.isEmpty()) {
                this.mActivityCache.remove(i);
            }
        }
        mActivityInfoStack.remove(str);
        if (this.mActivityCache.size() == 0) {
            clear();
        }
    }

    void setLastActivityPanel(View view) {
        this.mLastActivityPanel = new WeakReference<>(view);
    }
}
