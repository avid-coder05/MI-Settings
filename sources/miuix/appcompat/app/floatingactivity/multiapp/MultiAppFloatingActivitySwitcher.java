package miuix.appcompat.app.floatingactivity.multiapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.floatingactivity.FloatingActivitySwitcher;
import miuix.appcompat.app.floatingactivity.FloatingAnimHelper;
import miuix.appcompat.app.floatingactivity.MemoryFileUtil;
import miuix.appcompat.app.floatingactivity.OnFloatingCallback;
import miuix.appcompat.app.floatingactivity.SnapShotViewHelper;
import miuix.appcompat.app.floatingactivity.multiapp.IFloatingService;
import miuix.appcompat.app.floatingactivity.multiapp.IServiceNotify;

/* loaded from: classes5.dex */
public final class MultiAppFloatingActivitySwitcher {
    private static MultiAppFloatingActivitySwitcher sInstance;
    private long mCloseAllActivityTime;
    private IFloatingService mIFloatingService;
    private WeakReference<View> mLastActivityPanel;
    private long mOnDragEndTime;
    private long mOnDragStartTime;
    private boolean mServiceConnected;
    private final Handler mExitAnimationHandler = new Handler(Looper.getMainLooper());
    private final SparseArray<ArrayList<ActivitySpec>> mActivityCache = new SparseArray<>();
    private boolean mEnableDragToDismiss = true;
    private final ServiceConnection mServiceConnection = new ServiceConnection() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingActivitySwitcher.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("MFloatingSwitcher", "onServiceConnected");
            if (MultiAppFloatingActivitySwitcher.sInstance != null) {
                MultiAppFloatingActivitySwitcher.sInstance.setIFloatingService(IFloatingService.Stub.asInterface(iBinder));
                MultiAppFloatingActivitySwitcher.this.checkRegister();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("MFloatingSwitcher", "onServiceDisconnected");
            if (MultiAppFloatingActivitySwitcher.sInstance != null) {
                MultiAppFloatingActivitySwitcher.sInstance.unRegisterAll();
                MultiAppFloatingActivitySwitcher.this.clear();
                MultiAppFloatingActivitySwitcher.this.destroy();
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public static class ActivitySpec implements Parcelable {
        public static final Parcelable.Creator<ActivitySpec> CREATOR = new Parcelable.Creator<ActivitySpec>() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingActivitySwitcher.ActivitySpec.1
            @Override // android.os.Parcelable.Creator
            public ActivitySpec createFromParcel(Parcel parcel) {
                return new ActivitySpec(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public ActivitySpec[] newArray(int i) {
                return new ActivitySpec[i];
            }
        };
        AppCompatActivity activity;
        String identity;
        int index;
        boolean isOpenEnterAnimExecuted;
        List<Runnable> pendingTasks;
        boolean register;
        boolean resumed;
        ServiceNotify serviceNotify;
        int serviceNotifyIndex;
        int taskId;

        protected ActivitySpec(Parcel parcel) {
            this.index = -1;
            this.register = false;
            this.isOpenEnterAnimExecuted = false;
            this.index = parcel.readInt();
            this.taskId = parcel.readInt();
            this.identity = parcel.readString();
            this.resumed = parcel.readByte() != 0;
            this.serviceNotifyIndex = parcel.readInt();
            this.register = parcel.readByte() != 0;
            this.isOpenEnterAnimExecuted = parcel.readByte() != 0;
            this.pendingTasks = new LinkedList();
        }

        protected ActivitySpec(boolean z) {
            this.index = -1;
            this.register = false;
            this.isOpenEnterAnimExecuted = false;
            this.resumed = z;
            this.pendingTasks = new LinkedList();
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "{ index : " + this.index + "; taskId : " + this.taskId + "; taskId : " + this.taskId + "; identity : " + this.identity + "; serviceNotifyIndex : " + this.serviceNotifyIndex + "; register : " + this.register + "; isOpenEnterAnimExecuted : " + this.isOpenEnterAnimExecuted + "; }";
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.index);
            parcel.writeInt(this.taskId);
            parcel.writeString(this.identity);
            parcel.writeByte(this.resumed ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.serviceNotifyIndex);
            parcel.writeByte(this.register ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.isOpenEnterAnimExecuted ? (byte) 1 : (byte) 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class DefineOnFloatingActivityCallback implements OnFloatingCallback {
        protected int mAppCompatActivityTaskId;
        protected String mAppCompatIdentity;

        public DefineOnFloatingActivityCallback(AppCompatActivity appCompatActivity) {
            this.mAppCompatIdentity = appCompatActivity.getActivityIdentity();
            this.mAppCompatActivityTaskId = appCompatActivity.getTaskId();
        }

        private boolean checkFinishEnable(int i) {
            return !MultiAppFloatingActivitySwitcher.this.mEnableDragToDismiss && (i == 1 || i == 2);
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void closeAllPage() {
            MultiAppFloatingActivitySwitcher.this.notifyService(11);
        }

        protected int getActivityTaskId() {
            return this.mAppCompatActivityTaskId;
        }

        public int getPageCount() {
            return Math.max(MultiAppFloatingActivitySwitcher.this.getServicePageCount(getActivityTaskId()), MultiAppFloatingActivitySwitcher.this.getCurrentPageCount(getActivityTaskId()));
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void getSnapShotAndSetPanel(AppCompatActivity appCompatActivity) {
            if (appCompatActivity != null) {
                try {
                    MultiAppFloatingActivitySwitcher.getInstance().saveBitmap(SnapShotViewHelper.getSnapShot(appCompatActivity.getFloatingBrightPanel()), appCompatActivity.getTaskId(), appCompatActivity.getActivityIdentity());
                } catch (Exception e) {
                    Log.d("MFloatingSwitcher", "saveBitmap exception", e);
                }
            }
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public boolean isFirstPageEnterAnimExecuteEnable() {
            ArrayList arrayList = (ArrayList) MultiAppFloatingActivitySwitcher.this.mActivityCache.get(getActivityTaskId());
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    if (((ActivitySpec) arrayList.get(i)).index == 0) {
                        return !r2.isOpenEnterAnimExecuted;
                    }
                }
                return false;
            }
            return false;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public boolean isFirstPageExitAnimExecuteEnable() {
            return getPageCount() == 1;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void markActivityOpenEnterAnimExecuted(AppCompatActivity appCompatActivity) {
            MultiAppFloatingActivitySwitcher.this.markActivityOpenEnterAnimExecutedInternal(appCompatActivity.getTaskId(), appCompatActivity.getActivityIdentity());
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void onDragEnd() {
            MultiAppFloatingActivitySwitcher.this.notifyService(2);
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void onDragStart() {
            MultiAppFloatingActivitySwitcher.this.notifyService(1);
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingActivityCallback
        public boolean onFinish(int i) {
            if (!checkFinishEnable(i) && MultiAppFloatingActivitySwitcher.this.shouldAllFloatingClose(i, getActivityTaskId())) {
                MultiAppFloatingActivitySwitcher.this.notifyService(5);
            }
            return false;
        }

        @Override // miuix.appcompat.app.floatingactivity.OnFloatingCallback
        public void onHideBehindPage() {
            MultiAppFloatingActivitySwitcher.this.notifyService(5);
        }
    }

    /* loaded from: classes5.dex */
    static class OpenExitAnimationExecutor implements Runnable {
        private WeakReference<AppCompatActivity> mAppCompatActivity;

        public OpenExitAnimationExecutor(AppCompatActivity appCompatActivity) {
            this.mAppCompatActivity = null;
            this.mAppCompatActivity = new WeakReference<>(appCompatActivity);
        }

        @Override // java.lang.Runnable
        public void run() {
            AppCompatActivity appCompatActivity = this.mAppCompatActivity.get();
            if (appCompatActivity != null) {
                appCompatActivity.executeOpenExitAnimation();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class ServiceNotify extends IServiceNotify.Stub {
        protected String mActivityIdentity;
        protected int mActivityTaskId;

        public ServiceNotify(AppCompatActivity appCompatActivity) {
            this.mActivityIdentity = appCompatActivity.getActivityIdentity();
            this.mActivityTaskId = appCompatActivity.getTaskId();
        }

        private AppCompatActivity getActivity() {
            return MultiAppFloatingActivitySwitcher.getInstance().getActivity(getActivityTaskId(), getActivityIdentity());
        }

        protected String getActivityIdentity() {
            return this.mActivityIdentity;
        }

        protected int getActivityTaskId() {
            return this.mActivityTaskId;
        }

        @Override // miuix.appcompat.app.floatingactivity.multiapp.IServiceNotify
        public Bundle notifyFromService(int i, Bundle bundle) throws RemoteException {
            Bundle bundle2 = new Bundle();
            if (i == 1) {
                MultiAppFloatingActivitySwitcher.sInstance.hideBehindPages();
            } else if (i == 2) {
                MultiAppFloatingActivitySwitcher.sInstance.onDragEnd();
            } else if (i == 3) {
                MultiAppFloatingActivitySwitcher.sInstance.closeAllActivity();
                AppCompatActivity activity = getActivity();
                if (activity != null) {
                    MultiAppFloatingActivitySwitcher.sInstance.unbindService(activity);
                }
            } else if (i != 5) {
                switch (i) {
                    case 8:
                        AppCompatActivity activity2 = getActivity();
                        if (bundle != null && activity2 != null) {
                            View floatingBrightPanel = activity2.getFloatingBrightPanel();
                            MultiAppFloatingActivitySwitcher.this.setLastActivityPanel(SnapShotViewHelper.generateSnapShotView(floatingBrightPanel, MemoryFileUtil.readBitmap(bundle)));
                            if (MultiAppFloatingActivitySwitcher.this.mLastActivityPanel != null && MultiAppFloatingActivitySwitcher.this.mLastActivityPanel.get() != null) {
                                ((ViewGroup) floatingBrightPanel.getParent()).getOverlay().add((View) MultiAppFloatingActivitySwitcher.this.mLastActivityPanel.get());
                                break;
                            }
                        }
                        break;
                    case 9:
                        AppCompatActivity activity3 = getActivity();
                        bundle2.putBoolean("check_finishing", activity3 != null && activity3.isFinishing());
                        break;
                    case 10:
                        AppCompatActivity activity4 = getActivity();
                        if (activity4 != null) {
                            MultiAppFloatingActivitySwitcher.this.mExitAnimationHandler.postDelayed(new OpenExitAnimationExecutor(activity4), 160L);
                            break;
                        }
                        break;
                    case 11:
                        MultiAppFloatingActivitySwitcher.sInstance.closeAllPage();
                        break;
                }
            } else {
                MultiAppFloatingActivitySwitcher.sInstance.hideBehindPages();
            }
            return bundle2;
        }

        public void resetAppCompatActivity(AppCompatActivity appCompatActivity) {
            this.mActivityIdentity = appCompatActivity.getActivityIdentity();
            this.mActivityTaskId = appCompatActivity.getTaskId();
        }
    }

    private MultiAppFloatingActivitySwitcher() {
    }

    private void bindService(Context context, Intent intent) {
        Intent intent2 = new Intent();
        String stringExtra = intent.getStringExtra("floating_service_pkg");
        if (TextUtils.isEmpty(stringExtra)) {
            return;
        }
        intent2.setPackage(stringExtra);
        String stringExtra2 = intent.getStringExtra("floating_service_path");
        if (TextUtils.isEmpty(stringExtra2)) {
            return;
        }
        intent2.setComponent(new ComponentName(intent.getStringExtra("floating_service_pkg"), stringExtra2));
        context.getApplicationContext().bindService(intent2, this.mServiceConnection, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkRegister() {
        for (int i = 0; i < this.mActivityCache.size(); i++) {
            Iterator<ActivitySpec> it = this.mActivityCache.valueAt(i).iterator();
            while (it.hasNext()) {
                ActivitySpec next = it.next();
                if (!next.register) {
                    invokeRegister(next);
                    checkBg(next.taskId, next.identity);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeAllActivity() {
        if (isCalled(this.mCloseAllActivityTime)) {
            return;
        }
        this.mCloseAllActivityTime = System.currentTimeMillis();
        for (int i = 0; i < this.mActivityCache.size(); i++) {
            ArrayList<ActivitySpec> valueAt = this.mActivityCache.valueAt(i);
            for (int size = valueAt.size() - 1; size >= 0; size--) {
                AppCompatActivity appCompatActivity = valueAt.get(size).activity;
                int i2 = valueAt.get(size).index;
                int servicePageCount = getServicePageCount(valueAt.get(size).taskId);
                if (appCompatActivity != null && i2 != servicePageCount - 1) {
                    appCompatActivity.realFinish();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeAllPage() {
        if (isCalled(this.mCloseAllActivityTime)) {
            return;
        }
        this.mCloseAllActivityTime = System.currentTimeMillis();
        for (int i = 0; i < this.mActivityCache.size(); i++) {
            ArrayList<ActivitySpec> valueAt = this.mActivityCache.valueAt(i);
            for (int size = valueAt.size() - 1; size >= 0; size--) {
                AppCompatActivity appCompatActivity = valueAt.get(size).activity;
                int i2 = valueAt.get(size).index;
                int servicePageCount = getServicePageCount(valueAt.get(size).taskId);
                if (appCompatActivity != null && i2 != servicePageCount - 1) {
                    appCompatActivity.realFinish();
                }
            }
        }
    }

    private ActivitySpec getActivitySpec(int i, String str) {
        ArrayList<ActivitySpec> arrayList = this.mActivityCache.get(i);
        if (arrayList != null) {
            Iterator<ActivitySpec> it = arrayList.iterator();
            while (it.hasNext()) {
                ActivitySpec next = it.next();
                if (TextUtils.equals(next.identity, str)) {
                    return next;
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MultiAppFloatingActivitySwitcher getInstance() {
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideBehindPages() {
        final AppCompatActivity appCompatActivity;
        if (isCalled(this.mOnDragStartTime)) {
            return;
        }
        this.mOnDragStartTime = System.currentTimeMillis();
        for (int i = 0; i < this.mActivityCache.size(); i++) {
            Iterator<ActivitySpec> it = this.mActivityCache.valueAt(i).iterator();
            while (it.hasNext()) {
                ActivitySpec next = it.next();
                if (!next.resumed && (appCompatActivity = next.activity) != null) {
                    appCompatActivity.runOnUiThread(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingActivitySwitcher$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            AppCompatActivity.this.hideFloatingBrightPanel();
                        }
                    });
                }
            }
        }
    }

    private void hideTopBgs(int i) {
        ArrayList<ActivitySpec> arrayList = this.mActivityCache.get(i);
        if (arrayList != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                int i3 = arrayList.get(i2).index;
                AppCompatActivity appCompatActivity = arrayList.get(i2).activity;
                if (appCompatActivity != null && i3 != 0) {
                    appCompatActivity.hideFloatingDimBackground();
                }
            }
        }
    }

    private void init(AppCompatActivity appCompatActivity, Intent intent, Bundle bundle) {
        stashActivity(appCompatActivity, intent, bundle);
        registerActivityToService(appCompatActivity);
        appCompatActivity.getLifecycle().addObserver(new MultiAppFloatingLifecycleObserver(appCompatActivity));
        appCompatActivity.setEnableSwipToDismiss(this.mEnableDragToDismiss);
        appCompatActivity.setOnFloatingCallback(new DefineOnFloatingActivityCallback(appCompatActivity));
    }

    public static void install(AppCompatActivity appCompatActivity, Intent intent, Bundle bundle) {
        if (!isFromMultiApp(intent)) {
            FloatingActivitySwitcher.install(appCompatActivity, bundle);
            return;
        }
        if (sInstance == null) {
            MultiAppFloatingActivitySwitcher multiAppFloatingActivitySwitcher = new MultiAppFloatingActivitySwitcher();
            sInstance = multiAppFloatingActivitySwitcher;
            multiAppFloatingActivitySwitcher.bindService(appCompatActivity, intent);
        }
        sInstance.init(appCompatActivity, intent, bundle);
    }

    private void invokeRegister(ActivitySpec activitySpec) {
        IFloatingService iFloatingService;
        if (activitySpec == null || (iFloatingService = this.mIFloatingService) == null) {
            return;
        }
        try {
            ServiceNotify serviceNotify = activitySpec.serviceNotify;
            iFloatingService.registerServiceNotify(serviceNotify, getIdentity(serviceNotify, activitySpec.taskId));
            updateServerActivityIndex(getIdentity(activitySpec.serviceNotify, activitySpec.taskId), activitySpec.index);
            if (!activitySpec.register) {
                activitySpec.register = true;
                activitySpec.serviceNotifyIndex = activitySpec.index;
            }
            Iterator<Runnable> it = activitySpec.pendingTasks.iterator();
            while (it.hasNext()) {
                it.next().run();
            }
            activitySpec.pendingTasks.clear();
        } catch (RemoteException e) {
            Log.w("MFloatingSwitcher", "catch register service notify exception", e);
        }
    }

    private boolean isActivityStashed(AppCompatActivity appCompatActivity) {
        return (appCompatActivity == null || getActivitySpec(appCompatActivity.getTaskId(), appCompatActivity.getActivityIdentity()) == null) ? false : true;
    }

    private boolean isCalled(long j) {
        return System.currentTimeMillis() - j <= 100;
    }

    public static boolean isFromMultiApp(Intent intent) {
        return (TextUtils.isEmpty(intent.getStringExtra("floating_service_pkg")) || TextUtils.isEmpty(intent.getStringExtra("floating_service_path"))) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bundle notifyService(int i) {
        return notifyService(i, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bundle notifyService(int i, Bundle bundle) {
        IFloatingService iFloatingService = this.mIFloatingService;
        if (iFloatingService == null) {
            Log.d("MFloatingSwitcher", "ifloatingservice is null");
            return null;
        }
        try {
            return iFloatingService.callServiceMethod(i, bundle);
        } catch (RemoteException e) {
            Log.w("MFloatingSwitcher", "catch call service method exception", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDragEnd() {
        final AppCompatActivity appCompatActivity;
        if (isCalled(this.mOnDragEndTime)) {
            return;
        }
        this.mOnDragEndTime = System.currentTimeMillis();
        for (int i = 0; i < this.mActivityCache.size(); i++) {
            Iterator<ActivitySpec> it = this.mActivityCache.valueAt(i).iterator();
            while (it.hasNext()) {
                ActivitySpec next = it.next();
                if (!next.resumed && (appCompatActivity = next.activity) != null) {
                    appCompatActivity.runOnUiThread(new Runnable() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingActivitySwitcher$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            AppCompatActivity.this.showFloatingBrightPanel();
                        }
                    });
                }
            }
        }
    }

    public static void onSaveInstanceState(int i, String str, Bundle bundle) {
        ActivitySpec activitySpec;
        if (getInstance() == null || (activitySpec = getInstance().getActivitySpec(i, str)) == null) {
            return;
        }
        bundle.putParcelable("floating_switcher_saved_key", activitySpec);
    }

    private void registerActivityToService(AppCompatActivity appCompatActivity) {
        ActivitySpec activitySpec = getActivitySpec(appCompatActivity.getTaskId(), appCompatActivity.getActivityIdentity());
        if (activitySpec != null && activitySpec.serviceNotify == null) {
            activitySpec.serviceNotify = new ServiceNotify(appCompatActivity);
        } else if (activitySpec != null) {
            activitySpec.serviceNotify.resetAppCompatActivity(appCompatActivity);
        }
        invokeRegister(activitySpec);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIFloatingService(IFloatingService iFloatingService) {
        this.mIFloatingService = iFloatingService;
        this.mServiceConnected = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldAllFloatingClose(int i, int i2) {
        return !(i == 4 || i == 3) || getServicePageCount(i2) <= 1;
    }

    private void stashActivity(AppCompatActivity appCompatActivity, Intent intent, Bundle bundle) {
        ActivitySpec activitySpec;
        if (!isActivityStashed(appCompatActivity)) {
            if (bundle != null) {
                activitySpec = (ActivitySpec) bundle.getParcelable("floating_switcher_saved_key");
            } else {
                ActivitySpec activitySpec2 = new ActivitySpec(true);
                if (intent == null) {
                    intent = appCompatActivity.getIntent();
                }
                activitySpec2.index = intent.getIntExtra("service_page_index", 0);
                activitySpec = activitySpec2;
            }
            activitySpec.activity = appCompatActivity;
            activitySpec.taskId = appCompatActivity.getTaskId();
            activitySpec.identity = appCompatActivity.getActivityIdentity();
            ArrayList<ActivitySpec> arrayList = this.mActivityCache.get(activitySpec.taskId);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mActivityCache.put(activitySpec.taskId, arrayList);
            }
            arrayList.add(Math.min(activitySpec.index, arrayList.size()), activitySpec);
            FloatingAnimHelper.markedPageIndex(appCompatActivity, activitySpec.index);
        }
        hideTopBgs(appCompatActivity.getTaskId());
    }

    private void unRegisterActivityFromService(int i, String str) {
        if (this.mIFloatingService != null) {
            try {
                ActivitySpec activitySpec = getActivitySpec(i, str);
                if (activitySpec != null) {
                    IFloatingService iFloatingService = this.mIFloatingService;
                    ServiceNotify serviceNotify = activitySpec.serviceNotify;
                    iFloatingService.unregisterServiceNotify(serviceNotify, String.valueOf(serviceNotify.hashCode()));
                }
            } catch (RemoteException e) {
                Log.w("MFloatingSwitcher", "catch unregister service notify exception", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unRegisterAll() {
        for (int i = 0; i < this.mActivityCache.size(); i++) {
            Iterator<ActivitySpec> it = this.mActivityCache.valueAt(i).iterator();
            while (it.hasNext()) {
                ActivitySpec next = it.next();
                unRegisterActivityFromService(next.taskId, next.identity);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindService(Context context) {
        if (this.mServiceConnected) {
            this.mServiceConnected = false;
            context.getApplicationContext().unbindService(this.mServiceConnection);
        }
    }

    private void updateServerActivityIndex(String str, int i) {
        IFloatingService iFloatingService = this.mIFloatingService;
        if (iFloatingService != null) {
            try {
                iFloatingService.upDateRemoteActivityInfo(str, i);
            } catch (RemoteException e) {
                Log.w("MFloatingSwitcher", "catch updateServerActivityIndex service notify exception", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkBg(int i, String str) {
        ActivitySpec activitySpec;
        AppCompatActivity appCompatActivity;
        ArrayList<ActivitySpec> arrayList = this.mActivityCache.get(i);
        if (((arrayList == null || arrayList.size() <= 1) && getServicePageCount(i) <= 1) || (activitySpec = getActivitySpec(i, str)) == null || activitySpec.serviceNotifyIndex <= 0 || (appCompatActivity = activitySpec.activity) == null) {
            return;
        }
        appCompatActivity.hideFloatingDimBackground();
    }

    public void clear() {
        this.mActivityCache.clear();
        this.mLastActivityPanel = null;
    }

    void destroy() {
        if (this.mActivityCache.size() == 0) {
            sInstance = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatActivity getActivity(int i, String str) {
        ActivitySpec activitySpec = getActivitySpec(i, str);
        if (activitySpec != null) {
            return activitySpec.activity;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCurrentPageCount(int i) {
        ArrayList<ActivitySpec> arrayList = this.mActivityCache.get(i);
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    String getIdentity(Object obj, int i) {
        return obj.hashCode() + ":" + i;
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
    public int getServicePageCount(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("key_task_id", i);
        Bundle notifyService = notifyService(6, bundle);
        int i2 = notifyService != null ? notifyService.getInt(String.valueOf(6)) : 0;
        ArrayList<ActivitySpec> arrayList = this.mActivityCache.get(i);
        if (arrayList != null) {
            Iterator<ActivitySpec> it = arrayList.iterator();
            while (it.hasNext()) {
                int i3 = it.next().index;
                if (i3 + 1 > i2) {
                    i2 = i3 + 1;
                }
            }
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAboveActivityFinishing(int i, String str) {
        ActivitySpec activitySpec = getActivitySpec(i, str);
        if (activitySpec == null) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("key_request_identity", String.valueOf(activitySpec.serviceNotify.hashCode()));
        bundle.putInt("key_task_id", i);
        Bundle notifyService = notifyService(9, bundle);
        return notifyService != null && notifyService.getBoolean("check_finishing");
    }

    public boolean isActivityOpenEnterAnimExecuted(int i, String str) {
        ActivitySpec activitySpec = getActivitySpec(i, str);
        if (activitySpec != null) {
            return activitySpec.isOpenEnterAnimExecuted;
        }
        return false;
    }

    boolean isServiceAvailable() {
        return this.mIFloatingService != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void markActivityOpenEnterAnimExecutedInternal(int i, String str) {
        ActivitySpec activitySpec = getActivitySpec(i, str);
        if (activitySpec != null) {
            activitySpec.isOpenEnterAnimExecuted = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyPreviousActivitySlide(int i, String str) {
        final ActivitySpec activitySpec = getActivitySpec(i, str);
        if (activitySpec == null) {
            return;
        }
        Runnable runnable = new Runnable() { // from class: miuix.appcompat.app.floatingactivity.multiapp.MultiAppFloatingActivitySwitcher.2
            @Override // java.lang.Runnable
            public void run() {
                String valueOf = String.valueOf(activitySpec.serviceNotify.hashCode());
                Bundle bundle = new Bundle();
                bundle.putInt("key_task_id", activitySpec.taskId);
                bundle.putString("execute_slide", valueOf);
                MultiAppFloatingActivitySwitcher.this.notifyService(10, bundle);
            }
        };
        if (isServiceAvailable()) {
            runnable.run();
        } else {
            activitySpec.pendingTasks.add(runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void postEnterAnimationTask(int i, String str, Runnable runnable) {
        if (isActivityOpenEnterAnimExecuted(i, str)) {
            return;
        }
        if (getCurrentPageCount(i) > 1 || getServicePageCount(i) > 1) {
            markActivityOpenEnterAnimExecutedInternal(i, str);
        }
        if (isServiceAvailable()) {
            runnable.run();
            return;
        }
        ActivitySpec activitySpec = getActivitySpec(i, str);
        if (activitySpec != null) {
            activitySpec.pendingTasks.add(runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void remove(int i, String str) {
        ActivitySpec activitySpec = getActivitySpec(i, str);
        if (activitySpec == null || activitySpec.activity == null) {
            return;
        }
        unRegisterActivityFromService(i, str);
        ArrayList<ActivitySpec> arrayList = this.mActivityCache.get(i);
        if (arrayList != null) {
            arrayList.remove(activitySpec);
            if (arrayList.isEmpty()) {
                this.mActivityCache.remove(i);
            }
        }
        if (this.mActivityCache.size() == 0) {
            unbindService(activitySpec.activity);
            clear();
        }
    }

    void saveBitmap(Bitmap bitmap, int i, String str) throws Exception {
        ActivitySpec activitySpec;
        if (bitmap == null || (activitySpec = getActivitySpec(i, str)) == null) {
            return;
        }
        int byteCount = bitmap.getByteCount();
        ByteBuffer allocate = ByteBuffer.allocate(byteCount);
        bitmap.copyPixelsToBuffer(allocate);
        MemoryFileUtil.sendToFdServer(this.mIFloatingService, allocate.array(), byteCount, bitmap.getWidth(), bitmap.getHeight(), String.valueOf(activitySpec.serviceNotify.hashCode()), i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLastActivityPanel(View view) {
        this.mLastActivityPanel = new WeakReference<>(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateResumeState(int i, String str, boolean z) {
        ActivitySpec activitySpec = getActivitySpec(i, str);
        if (activitySpec != null) {
            activitySpec.resumed = z;
        }
    }
}
