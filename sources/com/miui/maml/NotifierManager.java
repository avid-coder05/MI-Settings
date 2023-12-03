package com.miui.maml;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.util.HideSdkDependencyUtils;
import com.miui.maml.util.MobileDataUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* loaded from: classes2.dex */
public final class NotifierManager {
    private static boolean DBG = true;
    public static String TYPE_DARK_MODE = "DarkMode";
    public static String TYPE_MOBILE_DATA = "MobileData";
    public static String TYPE_TIME_CHANGED = "TimeChanged";
    public static String TYPE_WIFI_STATE = "WifiState";
    private static NotifierManager sInstance;
    private Context mContext;
    private HashMap<String, BaseNotifier> mNotifiers = new HashMap<>();

    /* loaded from: classes2.dex */
    public static abstract class BaseNotifier {
        private int mActiveCount;
        protected Context mContext;
        private ArrayList<Listener> mListeners = new ArrayList<>();
        private int mRefCount;
        private boolean mRegistered;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Listener {
            public Context context;
            public Intent intent;
            public Object obj;
            private boolean paused;
            private boolean pendingNotify;
            public WeakReference<OnNotifyListener> ref;

            public Listener(OnNotifyListener onNotifyListener) {
                this.ref = new WeakReference<>(onNotifyListener);
            }

            public void onNotify(Context context, Intent intent, Object obj) {
                if (this.paused) {
                    this.pendingNotify = true;
                    this.context = context;
                    this.intent = intent;
                    this.obj = obj;
                    return;
                }
                OnNotifyListener onNotifyListener = this.ref.get();
                if (onNotifyListener != null) {
                    onNotifyListener.onNotify(context, intent, obj);
                }
            }

            public void pause() {
                this.paused = true;
            }

            public void resume() {
                OnNotifyListener onNotifyListener;
                this.paused = false;
                if (!this.pendingNotify || (onNotifyListener = this.ref.get()) == null) {
                    return;
                }
                onNotifyListener.onNotify(this.context, this.intent, this.obj);
                this.pendingNotify = false;
                this.context = null;
                this.intent = null;
                this.obj = null;
            }
        }

        public BaseNotifier(Context context) {
            this.mContext = context;
        }

        private final void checkListeners() {
            synchronized (this.mListeners) {
                if (checkListenersLocked() == 0) {
                    pause();
                }
            }
        }

        private final int checkListenersLocked() {
            this.mActiveCount = 0;
            for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                Listener listener = this.mListeners.get(size);
                if (listener.ref.get() == null) {
                    this.mListeners.remove(size);
                } else if (!listener.paused) {
                    this.mActiveCount++;
                }
            }
            int size2 = this.mListeners.size();
            this.mRefCount = size2;
            return size2;
        }

        private final Listener findListenerLocked(OnNotifyListener onNotifyListener) {
            Iterator<Listener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                Listener next = it.next();
                if (next.ref.get() == onNotifyListener) {
                    return next;
                }
            }
            return null;
        }

        public final void addListener(OnNotifyListener onNotifyListener) {
            synchronized (this.mListeners) {
                if (findListenerLocked(onNotifyListener) == null) {
                    this.mListeners.add(new Listener(onNotifyListener));
                    checkListenersLocked();
                    onListenerAdded(onNotifyListener);
                }
            }
        }

        public void finish() {
            unregister();
        }

        public final int getRef() {
            checkListeners();
            return this.mRefCount;
        }

        public void init() {
            register();
        }

        protected void onListenerAdded(OnNotifyListener onNotifyListener) {
        }

        protected void onNotify(Context context, Intent intent, Object obj) {
            checkListeners();
            synchronized (this.mListeners) {
                Iterator<Listener> it = this.mListeners.iterator();
                while (it.hasNext()) {
                    it.next().onNotify(context, intent, obj);
                }
            }
        }

        protected abstract void onRegister();

        protected abstract void onUnregister();

        public void pause() {
            unregister();
        }

        public final int pauseListener(OnNotifyListener onNotifyListener) {
            synchronized (this.mListeners) {
                Listener findListenerLocked = findListenerLocked(onNotifyListener);
                if (findListenerLocked != null) {
                    findListenerLocked.pause();
                    return this.mRefCount;
                }
                checkListenersLocked();
                Log.w("NotifierManager", "pauseListener, listener not exist");
                return this.mRefCount;
            }
        }

        protected void register() {
            if (this.mRegistered) {
                return;
            }
            onRegister();
            this.mRegistered = true;
            if (NotifierManager.DBG) {
                Log.i("NotifierManager", "onRegister: " + toString());
            }
        }

        public final void removeListener(OnNotifyListener onNotifyListener) {
            synchronized (this.mListeners) {
                Listener findListenerLocked = findListenerLocked(onNotifyListener);
                if (findListenerLocked != null) {
                    this.mListeners.remove(findListenerLocked);
                    checkListenersLocked();
                }
            }
        }

        public void resume() {
            register();
        }

        public final int resumeListener(OnNotifyListener onNotifyListener) {
            synchronized (this.mListeners) {
                Listener findListenerLocked = findListenerLocked(onNotifyListener);
                if (findListenerLocked != null) {
                    findListenerLocked.resume();
                    return this.mRefCount;
                }
                checkListenersLocked();
                Log.w("NotifierManager", "resumeListener, listener not exist");
                return this.mRefCount;
            }
        }

        protected void unregister() {
            if (this.mRegistered) {
                try {
                    onUnregister();
                } catch (IllegalArgumentException e) {
                    Log.w("NotifierManager", e.toString());
                }
                this.mRegistered = false;
                if (NotifierManager.DBG) {
                    Log.i("NotifierManager", "onUnregister: " + toString());
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class BroadcastNotifier extends BaseNotifier {
        private String mAction;
        private IntentFilter mIntentFilter;
        private final BroadcastReceiver mIntentReceiver;

        public BroadcastNotifier(Context context) {
            super(context);
            this.mIntentReceiver = new BroadcastReceiver() { // from class: com.miui.maml.NotifierManager.BroadcastNotifier.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    if (NotifierManager.DBG) {
                        Log.i("NotifierManager", "onNotify: " + BroadcastNotifier.this.toString());
                    }
                    BroadcastNotifier.this.onNotify(context2, intent, null);
                }
            };
        }

        public BroadcastNotifier(Context context, String str) {
            super(context);
            this.mIntentReceiver = new BroadcastReceiver() { // from class: com.miui.maml.NotifierManager.BroadcastNotifier.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    if (NotifierManager.DBG) {
                        Log.i("NotifierManager", "onNotify: " + BroadcastNotifier.this.toString());
                    }
                    BroadcastNotifier.this.onNotify(context2, intent, null);
                }
            };
            this.mAction = str;
        }

        protected IntentFilter createIntentFilter() {
            String intentAction = getIntentAction();
            if (intentAction == null) {
                return null;
            }
            return new IntentFilter(intentAction);
        }

        protected String getIntentAction() {
            return this.mAction;
        }

        @Override // com.miui.maml.NotifierManager.BaseNotifier
        protected void onRegister() {
            if (this.mIntentFilter == null) {
                this.mIntentFilter = createIntentFilter();
            }
            IntentFilter intentFilter = this.mIntentFilter;
            if (intentFilter == null) {
                Log.e("NotifierManager", "onRegister: mIntentFilter is null");
                return;
            }
            Intent registerReceiver = this.mContext.registerReceiver(this.mIntentReceiver, intentFilter);
            if (registerReceiver != null) {
                onNotify(this.mContext, registerReceiver, null);
            }
        }

        @Override // com.miui.maml.NotifierManager.BaseNotifier
        protected void onUnregister() {
            try {
                this.mContext.unregisterReceiver(this.mIntentReceiver);
            } catch (IllegalArgumentException unused) {
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class ContentChangeNotifier extends BaseNotifier {
        protected final ContentObserver mObserver;
        private Uri mUri;

        public ContentChangeNotifier(Context context, Uri uri) {
            super(context);
            this.mObserver = new ContentObserver(null) { // from class: com.miui.maml.NotifierManager.ContentChangeNotifier.1
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    if (NotifierManager.DBG) {
                        Log.i("NotifierManager", "onNotify: " + ContentChangeNotifier.this.toString());
                    }
                    ContentChangeNotifier.this.onNotify(null, null, Boolean.valueOf(z));
                }
            };
            this.mUri = uri;
        }

        @Override // com.miui.maml.NotifierManager.BaseNotifier
        protected void onUnregister() {
            this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
        }
    }

    /* loaded from: classes2.dex */
    public static class DarkModeNotifier extends BaseNotifier {
        private ComponentCallbacks2 mComponentCallback;
        private String mDarkModeName;
        private ContentObserver mDarkModeObserver;
        private String mDarkWallpaperModeName;
        private ContentObserver mDarkWallpaperModeObserver;
        private boolean mIsDarkMode;
        private boolean mIsDarkWallpaperMode;
        private boolean mIsUIModeNight;
        private int mMamlDarkMode;

        public DarkModeNotifier(Context context) {
            super(context);
            Handler handler = null;
            this.mDarkModeObserver = new ContentObserver(handler) { // from class: com.miui.maml.NotifierManager.DarkModeNotifier.1
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    DarkModeNotifier.this.updateDarkMode();
                    DarkModeNotifier.this.checkIfNeedToNotify();
                }
            };
            this.mDarkWallpaperModeObserver = new ContentObserver(handler) { // from class: com.miui.maml.NotifierManager.DarkModeNotifier.2
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    DarkModeNotifier.this.updateDarkWallpaperMode();
                    DarkModeNotifier.this.checkIfNeedToNotify();
                }
            };
            this.mComponentCallback = new ComponentCallbacks2() { // from class: com.miui.maml.NotifierManager.DarkModeNotifier.3
                @Override // android.content.ComponentCallbacks
                public void onConfigurationChanged(Configuration configuration) {
                    DarkModeNotifier.this.updateUIModeNight(configuration);
                    DarkModeNotifier.this.checkIfNeedToNotify();
                }

                @Override // android.content.ComponentCallbacks
                public void onLowMemory() {
                }

                @Override // android.content.ComponentCallbacks2
                public void onTrimMemory(int i) {
                }
            };
            this.mDarkModeName = HideSdkDependencyUtils.SettingsSecure_UI_NIGHT_MODE();
            this.mDarkWallpaperModeName = HideSdkDependencyUtils.SystemSettingsSystem_DARKEN_WALLPAPER_UNDER_DARK_MODE();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void checkIfNeedToNotify() {
            int i = (this.mIsDarkMode || this.mIsUIModeNight) ? 1 : 0;
            if (this.mIsDarkWallpaperMode) {
                i |= 2;
            }
            if (i != this.mMamlDarkMode) {
                this.mMamlDarkMode = i;
                onNotify(null, null, Integer.valueOf(i));
                Log.d("NotifierManager", "maml dark mode " + i);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateDarkMode() {
            this.mIsDarkMode = Settings.Secure.getInt(this.mContext.getContentResolver(), this.mDarkModeName, 1) == 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateDarkWallpaperMode() {
            this.mIsDarkWallpaperMode = MiuiSettings.System.getBoolean(this.mContext.getContentResolver(), this.mDarkWallpaperModeName, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateUIModeNight(Configuration configuration) {
            this.mIsUIModeNight = (configuration.uiMode & 48) == 32;
        }

        @Override // com.miui.maml.NotifierManager.BaseNotifier
        protected void onListenerAdded(OnNotifyListener onNotifyListener) {
            onNotifyListener.onNotify(null, null, Integer.valueOf(this.mMamlDarkMode));
        }

        @Override // com.miui.maml.NotifierManager.BaseNotifier
        protected void onRegister() {
            if (TextUtils.isEmpty(this.mDarkModeName) || TextUtils.isEmpty(this.mDarkWallpaperModeName)) {
                return;
            }
            try {
                this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(this.mDarkModeName), false, this.mDarkModeObserver);
                this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(this.mDarkWallpaperModeName), false, this.mDarkWallpaperModeObserver);
                this.mContext.registerComponentCallbacks(this.mComponentCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateDarkMode();
            updateDarkWallpaperMode();
            updateUIModeNight(this.mContext.getResources().getConfiguration());
            checkIfNeedToNotify();
        }

        @Override // com.miui.maml.NotifierManager.BaseNotifier
        protected void onUnregister() {
            if (TextUtils.isEmpty(this.mDarkModeName) || TextUtils.isEmpty(this.mDarkWallpaperModeName)) {
                return;
            }
            try {
                this.mContext.getContentResolver().unregisterContentObserver(this.mDarkModeObserver);
                this.mContext.getContentResolver().unregisterContentObserver(this.mDarkWallpaperModeObserver);
                this.mContext.unregisterComponentCallbacks(this.mComponentCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class MobileDataNotifier extends ContentChangeNotifier {
        private MobileDataUtils mMobileDataUtils;

        public MobileDataNotifier(Context context) {
            super(context, null);
            this.mMobileDataUtils = MobileDataUtils.getInstance();
        }

        @Override // com.miui.maml.NotifierManager.BaseNotifier
        protected void onRegister() {
            this.mMobileDataUtils.registerContentObserver(this.mContext, this.mObserver);
            onNotify(null, null, Boolean.TRUE);
        }
    }

    /* loaded from: classes2.dex */
    public static class MultiBroadcastNotifier extends BroadcastNotifier {
        private String[] mIntents;

        public MultiBroadcastNotifier(Context context, String... strArr) {
            super(context);
            this.mIntents = strArr;
        }

        @Override // com.miui.maml.NotifierManager.BroadcastNotifier
        protected IntentFilter createIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            for (String str : this.mIntents) {
                intentFilter.addAction(str);
            }
            return intentFilter;
        }
    }

    /* loaded from: classes2.dex */
    public interface OnNotifyListener {
        void onNotify(Context context, Intent intent, Object obj);
    }

    private NotifierManager(Context context) {
        this.mContext = context;
    }

    private static BaseNotifier createNotifier(String str, Context context) {
        if (DBG) {
            Log.i("NotifierManager", "createNotifier:" + str);
        }
        return TYPE_MOBILE_DATA.equals(str) ? new MobileDataNotifier(context) : TYPE_WIFI_STATE.equals(str) ? new MultiBroadcastNotifier(context, "android.net.wifi.WIFI_STATE_CHANGED", "android.net.wifi.SCAN_RESULTS", "android.net.wifi.STATE_CHANGE") : TYPE_TIME_CHANGED.equals(str) ? new MultiBroadcastNotifier(context, "android.intent.action.TIMEZONE_CHANGED", "android.intent.action.TIME_SET") : TYPE_DARK_MODE.equals(str) ? new DarkModeNotifier(context) : new BroadcastNotifier(context, str);
    }

    public static synchronized NotifierManager getInstance(Context context) {
        NotifierManager notifierManager;
        synchronized (NotifierManager.class) {
            if (sInstance == null) {
                sInstance = new NotifierManager(context);
            }
            notifierManager = sInstance;
        }
        return notifierManager;
    }

    private BaseNotifier safeGet(String str) {
        BaseNotifier baseNotifier;
        synchronized (this.mNotifiers) {
            baseNotifier = this.mNotifiers.get(str);
        }
        return baseNotifier;
    }

    public void acquireNotifier(String str, OnNotifyListener onNotifyListener) {
        if (DBG) {
            Log.i("NotifierManager", "acquireNotifier:" + str + "  " + onNotifyListener.toString());
        }
        synchronized (this.mNotifiers) {
            BaseNotifier baseNotifier = this.mNotifiers.get(str);
            if (baseNotifier == null) {
                baseNotifier = createNotifier(str, this.mContext);
                if (baseNotifier == null) {
                    return;
                }
                baseNotifier.init();
                this.mNotifiers.put(str, baseNotifier);
            }
            baseNotifier.addListener(onNotifyListener);
        }
    }

    public void pause(String str, OnNotifyListener onNotifyListener) {
        BaseNotifier safeGet = safeGet(str);
        if (safeGet != null && safeGet.pauseListener(onNotifyListener) == 0) {
            safeGet.pause();
        }
    }

    public void releaseNotifier(String str, OnNotifyListener onNotifyListener) {
        if (DBG) {
            Log.i("NotifierManager", "releaseNotifier:" + str + "  " + onNotifyListener.toString());
        }
        synchronized (this.mNotifiers) {
            BaseNotifier baseNotifier = this.mNotifiers.get(str);
            if (baseNotifier == null) {
                return;
            }
            baseNotifier.removeListener(onNotifyListener);
            if (baseNotifier.getRef() == 0) {
                baseNotifier.finish();
                this.mNotifiers.remove(str);
            }
        }
    }

    public synchronized void resume(String str, OnNotifyListener onNotifyListener) {
        BaseNotifier safeGet = safeGet(str);
        if (safeGet == null) {
            return;
        }
        if (safeGet.resumeListener(onNotifyListener) == 1) {
            safeGet.resume();
        }
    }
}
