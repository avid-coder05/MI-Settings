package miuix.autodensity;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import miuix.core.util.screenutils.MultiWindowModeHelper;

/* loaded from: classes5.dex */
public class AutoDensityConfig {

    /* loaded from: classes5.dex */
    static class AutoDensityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        private static final Handler sUiHandler = new Handler(Looper.getMainLooper());
        private DisplayManager mDisplayManager = null;
        private HashMap<Integer, AutoDensityCallback> mModifier;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes5.dex */
        public class AutoDensityCallback implements DisplayManager.DisplayListener, ComponentCallbacks {
            private WeakReference<Activity> mRefs;

            AutoDensityCallback(Activity activity) {
                this.mRefs = null;
                this.mRefs = new WeakReference<>(activity);
            }

            private void removeCurrentConfig(Activity activity) {
                try {
                    Field declaredField = Activity.class.getDeclaredField("mCurrentConfig");
                    declaredField.setAccessible(true);
                    declaredField.set(activity, null);
                } catch (Exception e) {
                    Log.e("AutoDensity", "e: " + e);
                }
            }

            void clear() {
                WeakReference<Activity> weakReference = this.mRefs;
                if (weakReference != null) {
                    weakReference.clear();
                }
            }

            @Override // android.content.ComponentCallbacks
            public void onConfigurationChanged(Configuration configuration) {
                WeakReference<Activity> weakReference = this.mRefs;
                Activity activity = weakReference != null ? weakReference.get() : null;
                if (activity != null) {
                    DensityUtil.updateCustomDensity(activity);
                    int detectWindowMode = MultiWindowModeHelper.detectWindowMode(activity);
                    if (MultiWindowModeHelper.isInFreeModeWindow(detectWindowMode) || MultiWindowModeHelper.isInSplitModeWindow(detectWindowMode)) {
                        DebugUtil.printDensityLog("remove current config");
                        removeCurrentConfig(activity);
                    }
                }
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i) {
                WeakReference<Activity> weakReference = this.mRefs;
                Activity activity = weakReference == null ? null : weakReference.get();
                DebugUtil.printDensityLog("onDisplayChanged activity: " + activity);
                if (activity != null) {
                    DensityUtil.updateCustomDensity(activity);
                } else {
                    AutoDensityLifecycleCallbacks.this.unregisterDisplayListener(this);
                }
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i) {
            }

            @Override // android.content.ComponentCallbacks
            public void onLowMemory() {
            }
        }

        AutoDensityLifecycleCallbacks() {
        }

        private void addForOnConfigurationChange(Activity activity) {
            if (getConfigurationChangeFragment(activity) == null) {
                activity.getFragmentManager().beginTransaction().add(new ConfigurationChangeFragment(), "ConfigurationChangeFragment").commitAllowingStateLoss();
            } else {
                Log.d("AutoDensity", "ConfigurationChangeFragment has already added");
            }
        }

        private Fragment getConfigurationChangeFragment(Activity activity) {
            return activity.getFragmentManager().findFragmentByTag("ConfigurationChangeFragment");
        }

        private void registerCallback(Activity activity) {
            if (this.mDisplayManager == null) {
                this.mDisplayManager = (DisplayManager) activity.getApplication().getSystemService("display");
            }
            if (this.mModifier == null) {
                this.mModifier = new HashMap<>();
            }
            int hashCode = activity.hashCode();
            if (this.mModifier.get(Integer.valueOf(hashCode)) == null) {
                AutoDensityCallback autoDensityCallback = new AutoDensityCallback(activity);
                DebugUtil.printDensityLog("registerCallback obj: " + autoDensityCallback);
                this.mModifier.put(Integer.valueOf(hashCode), autoDensityCallback);
                this.mDisplayManager.registerDisplayListener(autoDensityCallback, sUiHandler);
                activity.registerComponentCallbacks(autoDensityCallback);
            }
        }

        private void unregisterCallback(Activity activity) {
            if (this.mModifier != null) {
                int hashCode = activity.hashCode();
                AutoDensityCallback autoDensityCallback = this.mModifier.get(Integer.valueOf(hashCode));
                DebugUtil.printDensityLog("unregisterCallback obj: " + autoDensityCallback);
                if (autoDensityCallback != null) {
                    unregisterDisplayListener(autoDensityCallback);
                    activity.unregisterComponentCallbacks(autoDensityCallback);
                    autoDensityCallback.clear();
                }
                this.mModifier.remove(Integer.valueOf(hashCode));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void unregisterDisplayListener(AutoDensityCallback autoDensityCallback) {
            DisplayManager displayManager = this.mDisplayManager;
            if (displayManager != null) {
                displayManager.unregisterDisplayListener(autoDensityCallback);
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle bundle) {
            if (!(activity instanceof IDensity ? ((IDensity) activity).shouldAdaptAutoDensity() : AutoDensityConfig.isShouldAdaptAutoDensity(activity.getApplication()))) {
                DensityUtil.setToDefaultDensity(activity);
                return;
            }
            DensityUtil.updateCustomDensity(activity);
            addForOnConfigurationChange(activity);
            registerCallback(activity);
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
            unregisterCallback(activity);
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityPaused(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityResumed(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStarted(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
        }
    }

    public static void init(final Application application) {
        DebugUtil.initAutoDensityDebugEnable();
        DensityConfigManager.getInstance().initDefaultConfig(new DensityConfig(application.getResources().getDisplayMetrics()));
        if (isShouldAdaptAutoDensity(application)) {
            DensityUtil.updateCustomDensity(application);
        } else {
            DensityUtil.setToDefaultDensity(application);
        }
        application.registerActivityLifecycleCallbacks(new AutoDensityLifecycleCallbacks());
        application.registerComponentCallbacks(new ComponentCallbacks() { // from class: miuix.autodensity.AutoDensityConfig.1
            @Override // android.content.ComponentCallbacks
            public void onConfigurationChanged(Configuration configuration) {
                if (AutoDensityConfig.isShouldAdaptAutoDensity(application)) {
                    DensityUtil.updateCustomDensity(application);
                    DensityConfig currentConfig = DensityConfigManager.getInstance().getCurrentConfig();
                    configuration.densityDpi = currentConfig.densityDpi;
                    configuration.fontScale = currentConfig.scaledDensity;
                }
            }

            @Override // android.content.ComponentCallbacks
            public void onLowMemory() {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isShouldAdaptAutoDensity(Application application) {
        if (application instanceof IDensity) {
            return ((IDensity) application).shouldAdaptAutoDensity();
        }
        return true;
    }
}
