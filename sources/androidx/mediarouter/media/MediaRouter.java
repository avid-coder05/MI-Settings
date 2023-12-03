package androidx.mediarouter.media;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import androidx.collection.ArrayMap;
import androidx.core.app.ActivityManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.display.DisplayManagerCompat;
import androidx.core.util.ObjectsCompat;
import androidx.core.util.Pair;
import androidx.mediarouter.media.MediaRoute2Provider;
import androidx.mediarouter.media.MediaRouteProvider;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.MediaRouter;
import androidx.mediarouter.media.RegisteredMediaRouteProviderWatcher;
import androidx.mediarouter.media.RemoteControlClientCompat;
import androidx.mediarouter.media.SystemMediaRouteProvider;
import com.google.common.util.concurrent.ListenableFuture;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
public final class MediaRouter {
    static final boolean DEBUG = Log.isLoggable("MediaRouter", 3);
    static GlobalMediaRouter sGlobal;
    final ArrayList<CallbackRecord> mCallbackRecords = new ArrayList<>();
    final Context mContext;

    /* loaded from: classes.dex */
    public static abstract class Callback {
        public void onProviderAdded(MediaRouter router, ProviderInfo provider) {
        }

        public void onProviderChanged(MediaRouter router, ProviderInfo provider) {
        }

        public void onProviderRemoved(MediaRouter router, ProviderInfo provider) {
        }

        public void onRouteAdded(MediaRouter router, RouteInfo route) {
        }

        public abstract void onRouteChanged(MediaRouter router, RouteInfo route);

        public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo route) {
        }

        public void onRouteRemoved(MediaRouter router, RouteInfo route) {
        }

        @Deprecated
        public void onRouteSelected(MediaRouter router, RouteInfo route) {
        }

        public void onRouteSelected(MediaRouter router, RouteInfo route, int reason) {
            onRouteSelected(router, route);
        }

        public void onRouteSelected(MediaRouter router, RouteInfo selectedRoute, int reason, RouteInfo requestedRoute) {
            onRouteSelected(router, selectedRoute, reason);
        }

        @Deprecated
        public void onRouteUnselected(MediaRouter router, RouteInfo route) {
        }

        public void onRouteUnselected(MediaRouter router, RouteInfo route, int reason) {
            onRouteUnselected(router, route);
        }

        public void onRouteVolumeChanged(MediaRouter router, RouteInfo route) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class CallbackRecord {
        public final Callback mCallback;
        public int mFlags;
        public final MediaRouter mRouter;
        public MediaRouteSelector mSelector = MediaRouteSelector.EMPTY;
        public long mTimestamp;

        public CallbackRecord(MediaRouter router, Callback callback) {
            this.mRouter = router;
            this.mCallback = callback;
        }

        public boolean filterRouteEvent(RouteInfo route, int what, RouteInfo optionalRoute, int reason) {
            if ((this.mFlags & 2) != 0 || route.matchesSelector(this.mSelector)) {
                return true;
            }
            if (MediaRouter.isTransferToLocalEnabled() && route.isDefaultOrBluetooth() && what == 262 && reason == 3 && optionalRoute != null) {
                return !optionalRoute.isDefaultOrBluetooth();
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ControlRequestCallback {
        public void onError(String error, Bundle data) {
        }

        public void onResult(Bundle data) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class GlobalMediaRouter implements SystemMediaRouteProvider.SyncCallback, RegisteredMediaRouteProviderWatcher.Callback {
        final Context mApplicationContext;
        private RouteInfo mBluetoothRoute;
        private int mCallbackCount;
        private MediaSessionCompat mCompatSession;
        private RouteInfo mDefaultRoute;
        private MediaRouteDiscoveryRequest mDiscoveryRequest;
        private MediaRouteDiscoveryRequest mDiscoveryRequestForMr2Provider;
        private final DisplayManagerCompat mDisplayManager;
        private final boolean mLowRam;
        final boolean mMediaTransferEnabled;
        final MediaRoute2Provider mMr2Provider;
        OnPrepareTransferListener mOnPrepareTransferListener;
        MediaSessionCompat mRccMediaSession;
        RegisteredMediaRouteProviderWatcher mRegisteredProviderWatcher;
        RouteInfo mRequestedRoute;
        MediaRouteProvider.RouteController mRequestedRouteController;
        private MediaRouterParams mRouterParams;
        RouteInfo mSelectedRoute;
        MediaRouteProvider.RouteController mSelectedRouteController;
        final SystemMediaRouteProvider mSystemProvider;
        PrepareTransferNotifier mTransferNotifier;
        final ArrayList<WeakReference<MediaRouter>> mRouters = new ArrayList<>();
        private final ArrayList<RouteInfo> mRoutes = new ArrayList<>();
        private final Map<Pair<String, String>, String> mUniqueIdMap = new HashMap();
        private final ArrayList<ProviderInfo> mProviders = new ArrayList<>();
        private final ArrayList<RemoteControlClientRecord> mRemoteControlClients = new ArrayList<>();
        final RemoteControlClientCompat.PlaybackInfo mPlaybackInfo = new RemoteControlClientCompat.PlaybackInfo();
        private final ProviderCallback mProviderCallback = new ProviderCallback();
        final CallbackHandler mCallbackHandler = new CallbackHandler();
        private MediaRouterActiveScanThrottlingHelper mActiveScanThrottlingHelper = new MediaRouterActiveScanThrottlingHelper(new Runnable() { // from class: androidx.mediarouter.media.MediaRouter.GlobalMediaRouter.1
            @Override // java.lang.Runnable
            public void run() {
                GlobalMediaRouter.this.updateDiscoveryRequest();
            }
        });
        final Map<String, MediaRouteProvider.RouteController> mRouteControllerMap = new HashMap();
        private MediaSessionCompat.OnActiveChangeListener mSessionActiveListener = new MediaSessionCompat.OnActiveChangeListener() { // from class: androidx.mediarouter.media.MediaRouter.GlobalMediaRouter.2
            @Override // android.support.v4.media.session.MediaSessionCompat.OnActiveChangeListener
            public void onActiveChanged() {
                MediaSessionCompat mediaSessionCompat = GlobalMediaRouter.this.mRccMediaSession;
                if (mediaSessionCompat != null) {
                    if (mediaSessionCompat.isActive()) {
                        GlobalMediaRouter globalMediaRouter = GlobalMediaRouter.this;
                        globalMediaRouter.addRemoteControlClient(globalMediaRouter.mRccMediaSession.getRemoteControlClient());
                        return;
                    }
                    GlobalMediaRouter globalMediaRouter2 = GlobalMediaRouter.this;
                    globalMediaRouter2.removeRemoteControlClient(globalMediaRouter2.mRccMediaSession.getRemoteControlClient());
                }
            }
        };
        MediaRouteProvider.DynamicGroupRouteController.OnDynamicRoutesChangedListener mDynamicRoutesListener = new MediaRouteProvider.DynamicGroupRouteController.OnDynamicRoutesChangedListener() { // from class: androidx.mediarouter.media.MediaRouter.GlobalMediaRouter.3
            @Override // androidx.mediarouter.media.MediaRouteProvider.DynamicGroupRouteController.OnDynamicRoutesChangedListener
            public void onRoutesChanged(MediaRouteProvider.DynamicGroupRouteController controller, MediaRouteDescriptor groupRouteDescriptor, Collection<MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor> routes) {
                GlobalMediaRouter globalMediaRouter = GlobalMediaRouter.this;
                if (controller != globalMediaRouter.mRequestedRouteController || groupRouteDescriptor == null) {
                    if (controller == globalMediaRouter.mSelectedRouteController) {
                        if (groupRouteDescriptor != null) {
                            globalMediaRouter.updateRouteDescriptorAndNotify(globalMediaRouter.mSelectedRoute, groupRouteDescriptor);
                        }
                        GlobalMediaRouter.this.mSelectedRoute.updateDynamicDescriptors(routes);
                        return;
                    }
                    return;
                }
                ProviderInfo provider = globalMediaRouter.mRequestedRoute.getProvider();
                String id = groupRouteDescriptor.getId();
                RouteInfo routeInfo = new RouteInfo(provider, id, GlobalMediaRouter.this.assignRouteUniqueId(provider, id));
                routeInfo.maybeUpdateDescriptor(groupRouteDescriptor);
                GlobalMediaRouter globalMediaRouter2 = GlobalMediaRouter.this;
                if (globalMediaRouter2.mSelectedRoute == routeInfo) {
                    return;
                }
                globalMediaRouter2.notifyTransfer(globalMediaRouter2, routeInfo, globalMediaRouter2.mRequestedRouteController, 3, globalMediaRouter2.mRequestedRoute, routes);
                GlobalMediaRouter globalMediaRouter3 = GlobalMediaRouter.this;
                globalMediaRouter3.mRequestedRoute = null;
                globalMediaRouter3.mRequestedRouteController = null;
            }
        };

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public final class CallbackHandler extends Handler {
            private final ArrayList<CallbackRecord> mTempCallbackRecords = new ArrayList<>();
            private final List<RouteInfo> mDynamicGroupRoutes = new ArrayList();

            CallbackHandler() {
            }

            private void invokeCallback(CallbackRecord record, int what, Object obj, int arg) {
                MediaRouter mediaRouter = record.mRouter;
                Callback callback = record.mCallback;
                int i = 65280 & what;
                if (i != 256) {
                    if (i != 512) {
                        return;
                    }
                    ProviderInfo providerInfo = (ProviderInfo) obj;
                    switch (what) {
                        case 513:
                            callback.onProviderAdded(mediaRouter, providerInfo);
                            return;
                        case 514:
                            callback.onProviderRemoved(mediaRouter, providerInfo);
                            return;
                        case 515:
                            callback.onProviderChanged(mediaRouter, providerInfo);
                            return;
                        default:
                            return;
                    }
                }
                RouteInfo routeInfo = (what == 264 || what == 262) ? (RouteInfo) ((Pair) obj).second : (RouteInfo) obj;
                RouteInfo routeInfo2 = (what == 264 || what == 262) ? (RouteInfo) ((Pair) obj).first : null;
                if (routeInfo == null || !record.filterRouteEvent(routeInfo, what, routeInfo2, arg)) {
                    return;
                }
                switch (what) {
                    case 257:
                        callback.onRouteAdded(mediaRouter, routeInfo);
                        return;
                    case 258:
                        callback.onRouteRemoved(mediaRouter, routeInfo);
                        return;
                    case 259:
                        callback.onRouteChanged(mediaRouter, routeInfo);
                        return;
                    case 260:
                        callback.onRouteVolumeChanged(mediaRouter, routeInfo);
                        return;
                    case 261:
                        callback.onRoutePresentationDisplayChanged(mediaRouter, routeInfo);
                        return;
                    case 262:
                        callback.onRouteSelected(mediaRouter, routeInfo, arg, routeInfo);
                        return;
                    case 263:
                        callback.onRouteUnselected(mediaRouter, routeInfo, arg);
                        return;
                    case 264:
                        callback.onRouteSelected(mediaRouter, routeInfo, arg, routeInfo2);
                        return;
                    default:
                        return;
                }
            }

            private void syncWithSystemProvider(int what, Object obj) {
                if (what == 262) {
                    RouteInfo routeInfo = (RouteInfo) ((Pair) obj).second;
                    GlobalMediaRouter.this.mSystemProvider.onSyncRouteSelected(routeInfo);
                    if (GlobalMediaRouter.this.mDefaultRoute == null || !routeInfo.isDefaultOrBluetooth()) {
                        return;
                    }
                    Iterator<RouteInfo> it = this.mDynamicGroupRoutes.iterator();
                    while (it.hasNext()) {
                        GlobalMediaRouter.this.mSystemProvider.onSyncRouteRemoved(it.next());
                    }
                    this.mDynamicGroupRoutes.clear();
                } else if (what == 264) {
                    RouteInfo routeInfo2 = (RouteInfo) ((Pair) obj).second;
                    this.mDynamicGroupRoutes.add(routeInfo2);
                    GlobalMediaRouter.this.mSystemProvider.onSyncRouteAdded(routeInfo2);
                    GlobalMediaRouter.this.mSystemProvider.onSyncRouteSelected(routeInfo2);
                } else {
                    switch (what) {
                        case 257:
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteAdded((RouteInfo) obj);
                            return;
                        case 258:
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteRemoved((RouteInfo) obj);
                            return;
                        case 259:
                            GlobalMediaRouter.this.mSystemProvider.onSyncRouteChanged((RouteInfo) obj);
                            return;
                        default:
                            return;
                    }
                }
            }

            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int i = msg.what;
                Object obj = msg.obj;
                int i2 = msg.arg1;
                if (i == 259 && GlobalMediaRouter.this.getSelectedRoute().getId().equals(((RouteInfo) obj).getId())) {
                    GlobalMediaRouter.this.updateSelectedRouteIfNeeded(true);
                }
                syncWithSystemProvider(i, obj);
                try {
                    int size = GlobalMediaRouter.this.mRouters.size();
                    while (true) {
                        size--;
                        if (size < 0) {
                            break;
                        }
                        MediaRouter mediaRouter = GlobalMediaRouter.this.mRouters.get(size).get();
                        if (mediaRouter == null) {
                            GlobalMediaRouter.this.mRouters.remove(size);
                        } else {
                            this.mTempCallbackRecords.addAll(mediaRouter.mCallbackRecords);
                        }
                    }
                    int size2 = this.mTempCallbackRecords.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        invokeCallback(this.mTempCallbackRecords.get(i3), i, obj, i2);
                    }
                } finally {
                    this.mTempCallbackRecords.clear();
                }
            }

            public void post(int msg, Object obj) {
                obtainMessage(msg, obj).sendToTarget();
            }

            public void post(int msg, Object obj, int arg) {
                Message obtainMessage = obtainMessage(msg, obj);
                obtainMessage.arg1 = arg;
                obtainMessage.sendToTarget();
            }
        }

        /* loaded from: classes.dex */
        private final class Mr2ProviderCallback extends MediaRoute2Provider.Callback {
            private Mr2ProviderCallback() {
            }

            @Override // androidx.mediarouter.media.MediaRoute2Provider.Callback
            public void onReleaseController(MediaRouteProvider.RouteController controller) {
                if (controller == GlobalMediaRouter.this.mSelectedRouteController) {
                    selectRouteToFallbackRoute(2);
                } else if (MediaRouter.DEBUG) {
                    Log.d("MediaRouter", "A RouteController unrelated to the selected route is released. controller=" + controller);
                }
            }

            @Override // androidx.mediarouter.media.MediaRoute2Provider.Callback
            public void onSelectFallbackRoute(int reason) {
                selectRouteToFallbackRoute(reason);
            }

            @Override // androidx.mediarouter.media.MediaRoute2Provider.Callback
            public void onSelectRoute(String routeDescriptorId, int reason) {
                RouteInfo routeInfo;
                Iterator<RouteInfo> it = GlobalMediaRouter.this.getRoutes().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        routeInfo = null;
                        break;
                    }
                    routeInfo = it.next();
                    if (routeInfo.getProviderInstance() == GlobalMediaRouter.this.mMr2Provider && TextUtils.equals(routeDescriptorId, routeInfo.getDescriptorId())) {
                        break;
                    }
                }
                if (routeInfo != null) {
                    GlobalMediaRouter.this.selectRouteInternal(routeInfo, reason);
                    return;
                }
                Log.w("MediaRouter", "onSelectRoute: The target RouteInfo is not found for descriptorId=" + routeDescriptorId);
            }

            void selectRouteToFallbackRoute(int reason) {
                RouteInfo chooseFallbackRoute = GlobalMediaRouter.this.chooseFallbackRoute();
                if (GlobalMediaRouter.this.getSelectedRoute() != chooseFallbackRoute) {
                    GlobalMediaRouter.this.selectRouteInternal(chooseFallbackRoute, reason);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public final class ProviderCallback extends MediaRouteProvider.Callback {
            ProviderCallback() {
            }

            @Override // androidx.mediarouter.media.MediaRouteProvider.Callback
            public void onDescriptorChanged(MediaRouteProvider provider, MediaRouteProviderDescriptor descriptor) {
                GlobalMediaRouter.this.updateProviderDescriptor(provider, descriptor);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public final class RemoteControlClientRecord implements RemoteControlClientCompat.VolumeCallback {
            private boolean mDisconnected;
            private final RemoteControlClientCompat mRccCompat;

            public RemoteControlClientRecord(Object rcc) {
                RemoteControlClientCompat obtain = RemoteControlClientCompat.obtain(GlobalMediaRouter.this.mApplicationContext, rcc);
                this.mRccCompat = obtain;
                obtain.setVolumeCallback(this);
                updatePlaybackInfo();
            }

            public void disconnect() {
                this.mDisconnected = true;
                this.mRccCompat.setVolumeCallback(null);
            }

            public Object getRemoteControlClient() {
                return this.mRccCompat.getRemoteControlClient();
            }

            @Override // androidx.mediarouter.media.RemoteControlClientCompat.VolumeCallback
            public void onVolumeSetRequest(int volume) {
                RouteInfo routeInfo;
                if (this.mDisconnected || (routeInfo = GlobalMediaRouter.this.mSelectedRoute) == null) {
                    return;
                }
                routeInfo.requestSetVolume(volume);
            }

            @Override // androidx.mediarouter.media.RemoteControlClientCompat.VolumeCallback
            public void onVolumeUpdateRequest(int direction) {
                RouteInfo routeInfo;
                if (this.mDisconnected || (routeInfo = GlobalMediaRouter.this.mSelectedRoute) == null) {
                    return;
                }
                routeInfo.requestUpdateVolume(direction);
            }

            public void updatePlaybackInfo() {
                this.mRccCompat.setPlaybackInfo(GlobalMediaRouter.this.mPlaybackInfo);
            }
        }

        @SuppressLint({"SyntheticAccessor", "NewApi"})
        GlobalMediaRouter(Context applicationContext) {
            this.mApplicationContext = applicationContext;
            this.mDisplayManager = DisplayManagerCompat.getInstance(applicationContext);
            this.mLowRam = ActivityManagerCompat.isLowRamDevice((ActivityManager) applicationContext.getSystemService("activity"));
            if (Build.VERSION.SDK_INT >= 30) {
                this.mMediaTransferEnabled = MediaTransferReceiver.isDeclared(applicationContext);
            } else {
                this.mMediaTransferEnabled = false;
            }
            if (this.mMediaTransferEnabled) {
                this.mMr2Provider = new MediaRoute2Provider(applicationContext, new Mr2ProviderCallback());
            } else {
                this.mMr2Provider = null;
            }
            this.mSystemProvider = SystemMediaRouteProvider.obtain(applicationContext, this);
        }

        private ProviderInfo findProviderInfo(MediaRouteProvider providerInstance) {
            int size = this.mProviders.size();
            for (int i = 0; i < size; i++) {
                if (this.mProviders.get(i).mProviderInstance == providerInstance) {
                    return this.mProviders.get(i);
                }
            }
            return null;
        }

        private int findRemoteControlClientRecord(Object rcc) {
            int size = this.mRemoteControlClients.size();
            for (int i = 0; i < size; i++) {
                if (this.mRemoteControlClients.get(i).getRemoteControlClient() == rcc) {
                    return i;
                }
            }
            return -1;
        }

        private int findRouteByUniqueId(String uniqueId) {
            int size = this.mRoutes.size();
            for (int i = 0; i < size; i++) {
                if (this.mRoutes.get(i).mUniqueId.equals(uniqueId)) {
                    return i;
                }
            }
            return -1;
        }

        private boolean isSystemDefaultRoute(RouteInfo route) {
            return route.getProviderInstance() == this.mSystemProvider && route.mDescriptorId.equals("DEFAULT_ROUTE");
        }

        private boolean isSystemLiveAudioOnlyRoute(RouteInfo route) {
            return route.getProviderInstance() == this.mSystemProvider && route.supportsControlCategory("android.media.intent.category.LIVE_AUDIO") && !route.supportsControlCategory("android.media.intent.category.LIVE_VIDEO");
        }

        private void updateMr2ProviderDiscoveryRequest(MediaRouteSelector selector, boolean activeScan) {
            if (isMediaTransferEnabled()) {
                MediaRouteDiscoveryRequest mediaRouteDiscoveryRequest = this.mDiscoveryRequestForMr2Provider;
                if (mediaRouteDiscoveryRequest != null && mediaRouteDiscoveryRequest.getSelector().equals(selector) && this.mDiscoveryRequestForMr2Provider.isActiveScan() == activeScan) {
                    return;
                }
                if (!selector.isEmpty() || activeScan) {
                    this.mDiscoveryRequestForMr2Provider = new MediaRouteDiscoveryRequest(selector, activeScan);
                } else if (this.mDiscoveryRequestForMr2Provider == null) {
                    return;
                } else {
                    this.mDiscoveryRequestForMr2Provider = null;
                }
                if (MediaRouter.DEBUG) {
                    Log.d("MediaRouter", "Updated MediaRoute2Provider's discovery request: " + this.mDiscoveryRequestForMr2Provider);
                }
                this.mMr2Provider.setDiscoveryRequest(this.mDiscoveryRequestForMr2Provider);
            }
        }

        private void updateProviderContents(ProviderInfo provider, MediaRouteProviderDescriptor providerDescriptor) {
            boolean z;
            if (provider.updateDescriptor(providerDescriptor)) {
                int i = 0;
                if (providerDescriptor == null || !(providerDescriptor.isValid() || providerDescriptor == this.mSystemProvider.getDescriptor())) {
                    Log.w("MediaRouter", "Ignoring invalid provider descriptor: " + providerDescriptor);
                    z = false;
                } else {
                    List<MediaRouteDescriptor> routes = providerDescriptor.getRoutes();
                    ArrayList<Pair> arrayList = new ArrayList();
                    ArrayList<Pair> arrayList2 = new ArrayList();
                    z = false;
                    for (MediaRouteDescriptor mediaRouteDescriptor : routes) {
                        if (mediaRouteDescriptor == null || !mediaRouteDescriptor.isValid()) {
                            Log.w("MediaRouter", "Ignoring invalid system route descriptor: " + mediaRouteDescriptor);
                        } else {
                            String id = mediaRouteDescriptor.getId();
                            int findRouteIndexByDescriptorId = provider.findRouteIndexByDescriptorId(id);
                            if (findRouteIndexByDescriptorId < 0) {
                                RouteInfo routeInfo = new RouteInfo(provider, id, assignRouteUniqueId(provider, id));
                                int i2 = i + 1;
                                provider.mRoutes.add(i, routeInfo);
                                this.mRoutes.add(routeInfo);
                                if (mediaRouteDescriptor.getGroupMemberIds().size() > 0) {
                                    arrayList.add(new Pair(routeInfo, mediaRouteDescriptor));
                                } else {
                                    routeInfo.maybeUpdateDescriptor(mediaRouteDescriptor);
                                    if (MediaRouter.DEBUG) {
                                        Log.d("MediaRouter", "Route added: " + routeInfo);
                                    }
                                    this.mCallbackHandler.post(257, routeInfo);
                                }
                                i = i2;
                            } else if (findRouteIndexByDescriptorId < i) {
                                Log.w("MediaRouter", "Ignoring route descriptor with duplicate id: " + mediaRouteDescriptor);
                            } else {
                                RouteInfo routeInfo2 = provider.mRoutes.get(findRouteIndexByDescriptorId);
                                int i3 = i + 1;
                                Collections.swap(provider.mRoutes, findRouteIndexByDescriptorId, i);
                                if (mediaRouteDescriptor.getGroupMemberIds().size() > 0) {
                                    arrayList2.add(new Pair(routeInfo2, mediaRouteDescriptor));
                                } else if (updateRouteDescriptorAndNotify(routeInfo2, mediaRouteDescriptor) != 0 && routeInfo2 == this.mSelectedRoute) {
                                    z = true;
                                }
                                i = i3;
                            }
                        }
                    }
                    for (Pair pair : arrayList) {
                        RouteInfo routeInfo3 = (RouteInfo) pair.first;
                        routeInfo3.maybeUpdateDescriptor((MediaRouteDescriptor) pair.second);
                        if (MediaRouter.DEBUG) {
                            Log.d("MediaRouter", "Route added: " + routeInfo3);
                        }
                        this.mCallbackHandler.post(257, routeInfo3);
                    }
                    for (Pair pair2 : arrayList2) {
                        RouteInfo routeInfo4 = (RouteInfo) pair2.first;
                        if (updateRouteDescriptorAndNotify(routeInfo4, (MediaRouteDescriptor) pair2.second) != 0 && routeInfo4 == this.mSelectedRoute) {
                            z = true;
                        }
                    }
                }
                for (int size = provider.mRoutes.size() - 1; size >= i; size--) {
                    RouteInfo routeInfo5 = provider.mRoutes.get(size);
                    routeInfo5.maybeUpdateDescriptor(null);
                    this.mRoutes.remove(routeInfo5);
                }
                updateSelectedRouteIfNeeded(z);
                for (int size2 = provider.mRoutes.size() - 1; size2 >= i; size2--) {
                    RouteInfo remove = provider.mRoutes.remove(size2);
                    if (MediaRouter.DEBUG) {
                        Log.d("MediaRouter", "Route removed: " + remove);
                    }
                    this.mCallbackHandler.post(258, remove);
                }
                if (MediaRouter.DEBUG) {
                    Log.d("MediaRouter", "Provider changed: " + provider);
                }
                this.mCallbackHandler.post(515, provider);
            }
        }

        void addMemberToDynamicGroup(RouteInfo route) {
            if (!(this.mSelectedRouteController instanceof MediaRouteProvider.DynamicGroupRouteController)) {
                throw new IllegalStateException("There is no currently selected dynamic group route.");
            }
            RouteInfo.DynamicGroupState dynamicGroupState = getDynamicGroupState(route);
            if (!this.mSelectedRoute.getMemberRoutes().contains(route) && dynamicGroupState != null && dynamicGroupState.isGroupable()) {
                ((MediaRouteProvider.DynamicGroupRouteController) this.mSelectedRouteController).onAddMemberRoute(route.getDescriptorId());
                return;
            }
            Log.w("MediaRouter", "Ignoring attempt to add a non-groupable route to dynamic group : " + route);
        }

        @Override // androidx.mediarouter.media.RegisteredMediaRouteProviderWatcher.Callback
        public void addProvider(MediaRouteProvider providerInstance) {
            if (findProviderInfo(providerInstance) == null) {
                ProviderInfo providerInfo = new ProviderInfo(providerInstance);
                this.mProviders.add(providerInfo);
                if (MediaRouter.DEBUG) {
                    Log.d("MediaRouter", "Provider added: " + providerInfo);
                }
                this.mCallbackHandler.post(513, providerInfo);
                updateProviderContents(providerInfo, providerInstance.getDescriptor());
                providerInstance.setCallback(this.mProviderCallback);
                providerInstance.setDiscoveryRequest(this.mDiscoveryRequest);
            }
        }

        public void addRemoteControlClient(Object rcc) {
            if (findRemoteControlClientRecord(rcc) < 0) {
                this.mRemoteControlClients.add(new RemoteControlClientRecord(rcc));
            }
        }

        String assignRouteUniqueId(ProviderInfo provider, String routeDescriptorId) {
            String flattenToShortString = provider.getComponentName().flattenToShortString();
            String str = flattenToShortString + ":" + routeDescriptorId;
            if (findRouteByUniqueId(str) < 0) {
                this.mUniqueIdMap.put(new Pair<>(flattenToShortString, routeDescriptorId), str);
                return str;
            }
            Log.w("MediaRouter", "Either " + routeDescriptorId + " isn't unique in " + flattenToShortString + " or we're trying to assign a unique ID for an already added route");
            int i = 2;
            while (true) {
                String format = String.format(Locale.US, "%s_%d", str, Integer.valueOf(i));
                if (findRouteByUniqueId(format) < 0) {
                    this.mUniqueIdMap.put(new Pair<>(flattenToShortString, routeDescriptorId), format);
                    return format;
                }
                i++;
            }
        }

        RouteInfo chooseFallbackRoute() {
            Iterator<RouteInfo> it = this.mRoutes.iterator();
            while (it.hasNext()) {
                RouteInfo next = it.next();
                if (next != this.mDefaultRoute && isSystemLiveAudioOnlyRoute(next) && next.isSelectable()) {
                    return next;
                }
            }
            return this.mDefaultRoute;
        }

        int getCallbackCount() {
            return this.mCallbackCount;
        }

        RouteInfo getDefaultRoute() {
            RouteInfo routeInfo = this.mDefaultRoute;
            if (routeInfo != null) {
                return routeInfo;
            }
            throw new IllegalStateException("There is no default route.  The media router has not yet been fully initialized.");
        }

        RouteInfo.DynamicGroupState getDynamicGroupState(RouteInfo route) {
            return this.mSelectedRoute.getDynamicGroupState(route);
        }

        public MediaSessionCompat.Token getMediaSessionToken() {
            MediaSessionCompat mediaSessionCompat = this.mCompatSession;
            if (mediaSessionCompat != null) {
                return mediaSessionCompat.getSessionToken();
            }
            return null;
        }

        public RouteInfo getRoute(String uniqueId) {
            Iterator<RouteInfo> it = this.mRoutes.iterator();
            while (it.hasNext()) {
                RouteInfo next = it.next();
                if (next.mUniqueId.equals(uniqueId)) {
                    return next;
                }
            }
            return null;
        }

        public MediaRouter getRouter(Context context) {
            int size = this.mRouters.size();
            while (true) {
                size--;
                if (size < 0) {
                    MediaRouter mediaRouter = new MediaRouter(context);
                    this.mRouters.add(new WeakReference<>(mediaRouter));
                    return mediaRouter;
                }
                MediaRouter mediaRouter2 = this.mRouters.get(size).get();
                if (mediaRouter2 == null) {
                    this.mRouters.remove(size);
                } else if (mediaRouter2.mContext == context) {
                    return mediaRouter2;
                }
            }
        }

        MediaRouterParams getRouterParams() {
            return this.mRouterParams;
        }

        public List<RouteInfo> getRoutes() {
            return this.mRoutes;
        }

        RouteInfo getSelectedRoute() {
            RouteInfo routeInfo = this.mSelectedRoute;
            if (routeInfo != null) {
                return routeInfo;
            }
            throw new IllegalStateException("There is no currently selected route.  The media router has not yet been fully initialized.");
        }

        String getUniqueId(ProviderInfo provider, String routeDescriptorId) {
            return this.mUniqueIdMap.get(new Pair(provider.getComponentName().flattenToShortString(), routeDescriptorId));
        }

        boolean isMediaTransferEnabled() {
            return this.mMediaTransferEnabled;
        }

        public boolean isRouteAvailable(MediaRouteSelector selector, int flags) {
            if (selector.isEmpty()) {
                return false;
            }
            if ((flags & 2) == 0 && this.mLowRam) {
                return true;
            }
            int size = this.mRoutes.size();
            for (int i = 0; i < size; i++) {
                RouteInfo routeInfo = this.mRoutes.get(i);
                if (((flags & 1) == 0 || !routeInfo.isDefaultOrBluetooth()) && routeInfo.matchesSelector(selector)) {
                    return true;
                }
            }
            return false;
        }

        boolean isTransferToLocalEnabled() {
            return false;
        }

        void maybeUpdateMemberRouteControllers() {
            if (this.mSelectedRoute.isGroup()) {
                List<RouteInfo> memberRoutes = this.mSelectedRoute.getMemberRoutes();
                HashSet hashSet = new HashSet();
                Iterator<RouteInfo> it = memberRoutes.iterator();
                while (it.hasNext()) {
                    hashSet.add(it.next().mUniqueId);
                }
                Iterator<Map.Entry<String, MediaRouteProvider.RouteController>> it2 = this.mRouteControllerMap.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<String, MediaRouteProvider.RouteController> next = it2.next();
                    if (!hashSet.contains(next.getKey())) {
                        MediaRouteProvider.RouteController value = next.getValue();
                        value.onUnselect(0);
                        value.onRelease();
                        it2.remove();
                    }
                }
                for (RouteInfo routeInfo : memberRoutes) {
                    if (!this.mRouteControllerMap.containsKey(routeInfo.mUniqueId)) {
                        MediaRouteProvider.RouteController onCreateRouteController = routeInfo.getProviderInstance().onCreateRouteController(routeInfo.mDescriptorId, this.mSelectedRoute.mDescriptorId);
                        onCreateRouteController.onSelect();
                        this.mRouteControllerMap.put(routeInfo.mUniqueId, onCreateRouteController);
                    }
                }
            }
        }

        void notifyTransfer(GlobalMediaRouter router, RouteInfo route, MediaRouteProvider.RouteController routeController, int reason, RouteInfo requestedRoute, Collection<MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor> memberRoutes) {
            OnPrepareTransferListener onPrepareTransferListener;
            PrepareTransferNotifier prepareTransferNotifier = this.mTransferNotifier;
            if (prepareTransferNotifier != null) {
                prepareTransferNotifier.cancel();
                this.mTransferNotifier = null;
            }
            PrepareTransferNotifier prepareTransferNotifier2 = new PrepareTransferNotifier(router, route, routeController, reason, requestedRoute, memberRoutes);
            this.mTransferNotifier = prepareTransferNotifier2;
            if (prepareTransferNotifier2.mReason != 3 || (onPrepareTransferListener = this.mOnPrepareTransferListener) == null) {
                prepareTransferNotifier2.lambda$new$0();
                return;
            }
            ListenableFuture<Void> onPrepareTransfer = onPrepareTransferListener.onPrepareTransfer(this.mSelectedRoute, prepareTransferNotifier2.mToRoute);
            if (onPrepareTransfer == null) {
                this.mTransferNotifier.lambda$new$0();
            } else {
                this.mTransferNotifier.setFuture(onPrepareTransfer);
            }
        }

        @Override // androidx.mediarouter.media.SystemMediaRouteProvider.SyncCallback
        public void onSystemRouteSelectedByDescriptorId(String id) {
            RouteInfo findRouteByDescriptorId;
            this.mCallbackHandler.removeMessages(262);
            ProviderInfo findProviderInfo = findProviderInfo(this.mSystemProvider);
            if (findProviderInfo == null || (findRouteByDescriptorId = findProviderInfo.findRouteByDescriptorId(id)) == null) {
                return;
            }
            findRouteByDescriptorId.select();
        }

        @Override // androidx.mediarouter.media.RegisteredMediaRouteProviderWatcher.Callback
        public void releaseProviderController(RegisteredMediaRouteProvider provider, MediaRouteProvider.RouteController controller) {
            if (this.mSelectedRouteController == controller) {
                selectRoute(chooseFallbackRoute(), 2);
            }
        }

        void removeMemberFromDynamicGroup(RouteInfo route) {
            if (!(this.mSelectedRouteController instanceof MediaRouteProvider.DynamicGroupRouteController)) {
                throw new IllegalStateException("There is no currently selected dynamic group route.");
            }
            RouteInfo.DynamicGroupState dynamicGroupState = getDynamicGroupState(route);
            if (this.mSelectedRoute.getMemberRoutes().contains(route) && dynamicGroupState != null && dynamicGroupState.isUnselectable()) {
                if (this.mSelectedRoute.getMemberRoutes().size() <= 1) {
                    Log.w("MediaRouter", "Ignoring attempt to remove the last member route.");
                    return;
                } else {
                    ((MediaRouteProvider.DynamicGroupRouteController) this.mSelectedRouteController).onRemoveMemberRoute(route.getDescriptorId());
                    return;
                }
            }
            Log.w("MediaRouter", "Ignoring attempt to remove a non-unselectable member route : " + route);
        }

        @Override // androidx.mediarouter.media.RegisteredMediaRouteProviderWatcher.Callback
        public void removeProvider(MediaRouteProvider providerInstance) {
            ProviderInfo findProviderInfo = findProviderInfo(providerInstance);
            if (findProviderInfo != null) {
                providerInstance.setCallback(null);
                providerInstance.setDiscoveryRequest(null);
                updateProviderContents(findProviderInfo, null);
                if (MediaRouter.DEBUG) {
                    Log.d("MediaRouter", "Provider removed: " + findProviderInfo);
                }
                this.mCallbackHandler.post(514, findProviderInfo);
                this.mProviders.remove(findProviderInfo);
            }
        }

        public void removeRemoteControlClient(Object rcc) {
            int findRemoteControlClientRecord = findRemoteControlClientRecord(rcc);
            if (findRemoteControlClientRecord >= 0) {
                this.mRemoteControlClients.remove(findRemoteControlClientRecord).disconnect();
            }
        }

        public void requestSetVolume(RouteInfo route, int volume) {
            MediaRouteProvider.RouteController routeController;
            MediaRouteProvider.RouteController routeController2;
            if (route == this.mSelectedRoute && (routeController2 = this.mSelectedRouteController) != null) {
                routeController2.onSetVolume(volume);
            } else if (this.mRouteControllerMap.isEmpty() || (routeController = this.mRouteControllerMap.get(route.mUniqueId)) == null) {
            } else {
                routeController.onSetVolume(volume);
            }
        }

        public void requestUpdateVolume(RouteInfo route, int delta) {
            MediaRouteProvider.RouteController routeController;
            MediaRouteProvider.RouteController routeController2;
            if (route == this.mSelectedRoute && (routeController2 = this.mSelectedRouteController) != null) {
                routeController2.onUpdateVolume(delta);
            } else if (this.mRouteControllerMap.isEmpty() || (routeController = this.mRouteControllerMap.get(route.mUniqueId)) == null) {
            } else {
                routeController.onUpdateVolume(delta);
            }
        }

        void selectRoute(RouteInfo route, int unselectReason) {
            if (!this.mRoutes.contains(route)) {
                Log.w("MediaRouter", "Ignoring attempt to select removed route: " + route);
            } else if (!route.mEnabled) {
                Log.w("MediaRouter", "Ignoring attempt to select disabled route: " + route);
            } else {
                if (Build.VERSION.SDK_INT >= 30) {
                    MediaRouteProvider providerInstance = route.getProviderInstance();
                    MediaRoute2Provider mediaRoute2Provider = this.mMr2Provider;
                    if (providerInstance == mediaRoute2Provider && this.mSelectedRoute != route) {
                        mediaRoute2Provider.transferTo(route.getDescriptorId());
                        return;
                    }
                }
                selectRouteInternal(route, unselectReason);
            }
        }

        void selectRouteInternal(RouteInfo route, int unselectReason) {
            if (MediaRouter.sGlobal == null || (this.mBluetoothRoute != null && route.isDefault())) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                StringBuilder sb = new StringBuilder();
                for (int i = 3; i < stackTrace.length; i++) {
                    StackTraceElement stackTraceElement = stackTrace[i];
                    sb.append(stackTraceElement.getClassName());
                    sb.append(".");
                    sb.append(stackTraceElement.getMethodName());
                    sb.append(":");
                    sb.append(stackTraceElement.getLineNumber());
                    sb.append("  ");
                }
                if (MediaRouter.sGlobal == null) {
                    Log.w("MediaRouter", "setSelectedRouteInternal is called while sGlobal is null: pkgName=" + this.mApplicationContext.getPackageName() + ", callers=" + sb.toString());
                } else {
                    Log.w("MediaRouter", "Default route is selected while a BT route is available: pkgName=" + this.mApplicationContext.getPackageName() + ", callers=" + sb.toString());
                }
            }
            if (this.mSelectedRoute == route) {
                return;
            }
            if (this.mRequestedRoute != null) {
                this.mRequestedRoute = null;
                MediaRouteProvider.RouteController routeController = this.mRequestedRouteController;
                if (routeController != null) {
                    routeController.onUnselect(3);
                    this.mRequestedRouteController.onRelease();
                    this.mRequestedRouteController = null;
                }
            }
            if (isMediaTransferEnabled() && route.getProvider().supportsDynamicGroup()) {
                MediaRouteProvider.DynamicGroupRouteController onCreateDynamicGroupRouteController = route.getProviderInstance().onCreateDynamicGroupRouteController(route.mDescriptorId);
                if (onCreateDynamicGroupRouteController != null) {
                    onCreateDynamicGroupRouteController.setOnDynamicRoutesChangedListener(ContextCompat.getMainExecutor(this.mApplicationContext), this.mDynamicRoutesListener);
                    this.mRequestedRoute = route;
                    this.mRequestedRouteController = onCreateDynamicGroupRouteController;
                    onCreateDynamicGroupRouteController.onSelect();
                    return;
                }
                Log.w("MediaRouter", "setSelectedRouteInternal: Failed to create dynamic group route controller. route=" + route);
            }
            MediaRouteProvider.RouteController onCreateRouteController = route.getProviderInstance().onCreateRouteController(route.mDescriptorId);
            if (onCreateRouteController != null) {
                onCreateRouteController.onSelect();
            }
            if (MediaRouter.DEBUG) {
                Log.d("MediaRouter", "Route selected: " + route);
            }
            if (this.mSelectedRoute != null) {
                notifyTransfer(this, route, onCreateRouteController, unselectReason, null, null);
                return;
            }
            this.mSelectedRoute = route;
            this.mSelectedRouteController = onCreateRouteController;
            this.mCallbackHandler.post(262, new Pair(null, route), unselectReason);
        }

        public void start() {
            addProvider(this.mSystemProvider);
            MediaRoute2Provider mediaRoute2Provider = this.mMr2Provider;
            if (mediaRoute2Provider != null) {
                addProvider(mediaRoute2Provider);
            }
            RegisteredMediaRouteProviderWatcher registeredMediaRouteProviderWatcher = new RegisteredMediaRouteProviderWatcher(this.mApplicationContext, this);
            this.mRegisteredProviderWatcher = registeredMediaRouteProviderWatcher;
            registeredMediaRouteProviderWatcher.start();
        }

        void transferToRoute(RouteInfo route) {
            if (!(this.mSelectedRouteController instanceof MediaRouteProvider.DynamicGroupRouteController)) {
                throw new IllegalStateException("There is no currently selected dynamic group route.");
            }
            RouteInfo.DynamicGroupState dynamicGroupState = getDynamicGroupState(route);
            if (dynamicGroupState == null || !dynamicGroupState.isTransferable()) {
                Log.w("MediaRouter", "Ignoring attempt to transfer to a non-transferable route.");
            } else {
                ((MediaRouteProvider.DynamicGroupRouteController) this.mSelectedRouteController).onUpdateMemberRoutes(Collections.singletonList(route.getDescriptorId()));
            }
        }

        public void updateDiscoveryRequest() {
            MediaRouteSelector.Builder builder = new MediaRouteSelector.Builder();
            this.mActiveScanThrottlingHelper.reset();
            int size = this.mRouters.size();
            int i = 0;
            boolean z = false;
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                MediaRouter mediaRouter = this.mRouters.get(size).get();
                if (mediaRouter == null) {
                    this.mRouters.remove(size);
                } else {
                    int size2 = mediaRouter.mCallbackRecords.size();
                    i += size2;
                    for (int i2 = 0; i2 < size2; i2++) {
                        CallbackRecord callbackRecord = mediaRouter.mCallbackRecords.get(i2);
                        builder.addSelector(callbackRecord.mSelector);
                        boolean z2 = (callbackRecord.mFlags & 1) != 0;
                        this.mActiveScanThrottlingHelper.requestActiveScan(z2, callbackRecord.mTimestamp);
                        if (z2) {
                            z = true;
                        }
                        int i3 = callbackRecord.mFlags;
                        if ((i3 & 4) != 0 && !this.mLowRam) {
                            z = true;
                        }
                        if ((i3 & 8) != 0) {
                            z = true;
                        }
                    }
                }
            }
            boolean finalizeActiveScanAndScheduleSuppressActiveScanRunnable = this.mActiveScanThrottlingHelper.finalizeActiveScanAndScheduleSuppressActiveScanRunnable();
            this.mCallbackCount = i;
            MediaRouteSelector build = z ? builder.build() : MediaRouteSelector.EMPTY;
            updateMr2ProviderDiscoveryRequest(builder.build(), finalizeActiveScanAndScheduleSuppressActiveScanRunnable);
            MediaRouteDiscoveryRequest mediaRouteDiscoveryRequest = this.mDiscoveryRequest;
            if (mediaRouteDiscoveryRequest != null && mediaRouteDiscoveryRequest.getSelector().equals(build) && this.mDiscoveryRequest.isActiveScan() == finalizeActiveScanAndScheduleSuppressActiveScanRunnable) {
                return;
            }
            if (!build.isEmpty() || finalizeActiveScanAndScheduleSuppressActiveScanRunnable) {
                this.mDiscoveryRequest = new MediaRouteDiscoveryRequest(build, finalizeActiveScanAndScheduleSuppressActiveScanRunnable);
            } else if (this.mDiscoveryRequest == null) {
                return;
            } else {
                this.mDiscoveryRequest = null;
            }
            if (MediaRouter.DEBUG) {
                Log.d("MediaRouter", "Updated discovery request: " + this.mDiscoveryRequest);
            }
            if (z && !finalizeActiveScanAndScheduleSuppressActiveScanRunnable && this.mLowRam) {
                Log.i("MediaRouter", "Forcing passive route discovery on a low-RAM device, system performance may be affected.  Please consider using CALLBACK_FLAG_REQUEST_DISCOVERY instead of CALLBACK_FLAG_FORCE_DISCOVERY.");
            }
            int size3 = this.mProviders.size();
            for (int i4 = 0; i4 < size3; i4++) {
                MediaRouteProvider mediaRouteProvider = this.mProviders.get(i4).mProviderInstance;
                if (mediaRouteProvider != this.mMr2Provider) {
                    mediaRouteProvider.setDiscoveryRequest(this.mDiscoveryRequest);
                }
            }
        }

        @SuppressLint({"NewApi"})
        void updatePlaybackInfoFromSelectedRoute() {
            RouteInfo routeInfo = this.mSelectedRoute;
            if (routeInfo != null) {
                this.mPlaybackInfo.volume = routeInfo.getVolume();
                this.mPlaybackInfo.volumeMax = this.mSelectedRoute.getVolumeMax();
                this.mPlaybackInfo.volumeHandling = this.mSelectedRoute.getVolumeHandling();
                this.mPlaybackInfo.playbackStream = this.mSelectedRoute.getPlaybackStream();
                this.mPlaybackInfo.playbackType = this.mSelectedRoute.getPlaybackType();
                if (this.mMediaTransferEnabled && this.mSelectedRoute.getProviderInstance() == this.mMr2Provider) {
                    this.mPlaybackInfo.volumeControlId = MediaRoute2Provider.getSessionIdForRouteController(this.mSelectedRouteController);
                } else {
                    this.mPlaybackInfo.volumeControlId = null;
                }
                int size = this.mRemoteControlClients.size();
                for (int i = 0; i < size; i++) {
                    this.mRemoteControlClients.get(i).updatePlaybackInfo();
                }
            }
        }

        void updateProviderDescriptor(MediaRouteProvider providerInstance, MediaRouteProviderDescriptor descriptor) {
            ProviderInfo findProviderInfo = findProviderInfo(providerInstance);
            if (findProviderInfo != null) {
                updateProviderContents(findProviderInfo, descriptor);
            }
        }

        int updateRouteDescriptorAndNotify(RouteInfo route, MediaRouteDescriptor routeDescriptor) {
            int maybeUpdateDescriptor = route.maybeUpdateDescriptor(routeDescriptor);
            if (maybeUpdateDescriptor != 0) {
                if ((maybeUpdateDescriptor & 1) != 0) {
                    if (MediaRouter.DEBUG) {
                        Log.d("MediaRouter", "Route changed: " + route);
                    }
                    this.mCallbackHandler.post(259, route);
                }
                if ((maybeUpdateDescriptor & 2) != 0) {
                    if (MediaRouter.DEBUG) {
                        Log.d("MediaRouter", "Route volume changed: " + route);
                    }
                    this.mCallbackHandler.post(260, route);
                }
                if ((maybeUpdateDescriptor & 4) != 0) {
                    if (MediaRouter.DEBUG) {
                        Log.d("MediaRouter", "Route presentation display changed: " + route);
                    }
                    this.mCallbackHandler.post(261, route);
                }
            }
            return maybeUpdateDescriptor;
        }

        void updateSelectedRouteIfNeeded(boolean selectedRouteDescriptorChanged) {
            RouteInfo routeInfo = this.mDefaultRoute;
            if (routeInfo != null && !routeInfo.isSelectable()) {
                Log.i("MediaRouter", "Clearing the default route because it is no longer selectable: " + this.mDefaultRoute);
                this.mDefaultRoute = null;
            }
            if (this.mDefaultRoute == null && !this.mRoutes.isEmpty()) {
                Iterator<RouteInfo> it = this.mRoutes.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    RouteInfo next = it.next();
                    if (isSystemDefaultRoute(next) && next.isSelectable()) {
                        this.mDefaultRoute = next;
                        Log.i("MediaRouter", "Found default route: " + this.mDefaultRoute);
                        break;
                    }
                }
            }
            RouteInfo routeInfo2 = this.mBluetoothRoute;
            if (routeInfo2 != null && !routeInfo2.isSelectable()) {
                Log.i("MediaRouter", "Clearing the bluetooth route because it is no longer selectable: " + this.mBluetoothRoute);
                this.mBluetoothRoute = null;
            }
            if (this.mBluetoothRoute == null && !this.mRoutes.isEmpty()) {
                Iterator<RouteInfo> it2 = this.mRoutes.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    RouteInfo next2 = it2.next();
                    if (isSystemLiveAudioOnlyRoute(next2) && next2.isSelectable()) {
                        this.mBluetoothRoute = next2;
                        Log.i("MediaRouter", "Found bluetooth route: " + this.mBluetoothRoute);
                        break;
                    }
                }
            }
            RouteInfo routeInfo3 = this.mSelectedRoute;
            if (routeInfo3 != null && routeInfo3.isEnabled()) {
                if (selectedRouteDescriptorChanged) {
                    maybeUpdateMemberRouteControllers();
                    updatePlaybackInfoFromSelectedRoute();
                    return;
                }
                return;
            }
            Log.i("MediaRouter", "Unselecting the current route because it is no longer selectable: " + this.mSelectedRoute);
            selectRouteInternal(chooseFallbackRoute(), 0);
        }
    }

    /* loaded from: classes.dex */
    public interface OnPrepareTransferListener {
        ListenableFuture<Void> onPrepareTransfer(RouteInfo fromRoute, RouteInfo toRoute);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class PrepareTransferNotifier {
        private final RouteInfo mFromRoute;
        final List<MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor> mMemberRoutes;
        final int mReason;
        private final RouteInfo mRequestedRoute;
        private final WeakReference<GlobalMediaRouter> mRouter;
        final RouteInfo mToRoute;
        final MediaRouteProvider.RouteController mToRouteController;
        private ListenableFuture<Void> mFuture = null;
        private boolean mFinished = false;
        private boolean mCanceled = false;

        PrepareTransferNotifier(GlobalMediaRouter router, RouteInfo route, MediaRouteProvider.RouteController routeController, int reason, RouteInfo requestedRoute, Collection<MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor> memberRoutes) {
            this.mRouter = new WeakReference<>(router);
            this.mToRoute = route;
            this.mToRouteController = routeController;
            this.mReason = reason;
            this.mFromRoute = router.mSelectedRoute;
            this.mRequestedRoute = requestedRoute;
            this.mMemberRoutes = memberRoutes != null ? new ArrayList(memberRoutes) : null;
            router.mCallbackHandler.postDelayed(new Runnable() { // from class: androidx.mediarouter.media.MediaRouter$PrepareTransferNotifier$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MediaRouter.PrepareTransferNotifier.this.lambda$new$0();
                }
            }, 15000L);
        }

        private void selectToRouteAndNotify() {
            GlobalMediaRouter globalMediaRouter = this.mRouter.get();
            if (globalMediaRouter == null) {
                return;
            }
            RouteInfo routeInfo = this.mToRoute;
            globalMediaRouter.mSelectedRoute = routeInfo;
            globalMediaRouter.mSelectedRouteController = this.mToRouteController;
            RouteInfo routeInfo2 = this.mRequestedRoute;
            if (routeInfo2 == null) {
                globalMediaRouter.mCallbackHandler.post(262, new Pair(this.mFromRoute, routeInfo), this.mReason);
            } else {
                globalMediaRouter.mCallbackHandler.post(264, new Pair(routeInfo2, routeInfo), this.mReason);
            }
            globalMediaRouter.mRouteControllerMap.clear();
            globalMediaRouter.maybeUpdateMemberRouteControllers();
            globalMediaRouter.updatePlaybackInfoFromSelectedRoute();
            List<MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor> list = this.mMemberRoutes;
            if (list != null) {
                globalMediaRouter.mSelectedRoute.updateDynamicDescriptors(list);
            }
        }

        private void unselectFromRouteAndNotify() {
            GlobalMediaRouter globalMediaRouter = this.mRouter.get();
            if (globalMediaRouter != null) {
                RouteInfo routeInfo = globalMediaRouter.mSelectedRoute;
                RouteInfo routeInfo2 = this.mFromRoute;
                if (routeInfo != routeInfo2) {
                    return;
                }
                globalMediaRouter.mCallbackHandler.post(263, routeInfo2, this.mReason);
                MediaRouteProvider.RouteController routeController = globalMediaRouter.mSelectedRouteController;
                if (routeController != null) {
                    routeController.onUnselect(this.mReason);
                    globalMediaRouter.mSelectedRouteController.onRelease();
                }
                if (!globalMediaRouter.mRouteControllerMap.isEmpty()) {
                    for (MediaRouteProvider.RouteController routeController2 : globalMediaRouter.mRouteControllerMap.values()) {
                        routeController2.onUnselect(this.mReason);
                        routeController2.onRelease();
                    }
                    globalMediaRouter.mRouteControllerMap.clear();
                }
                globalMediaRouter.mSelectedRouteController = null;
            }
        }

        void cancel() {
            if (this.mFinished || this.mCanceled) {
                return;
            }
            this.mCanceled = true;
            MediaRouteProvider.RouteController routeController = this.mToRouteController;
            if (routeController != null) {
                routeController.onUnselect(0);
                this.mToRouteController.onRelease();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: finishTransfer  reason: merged with bridge method [inline-methods] */
        public void lambda$new$0() {
            ListenableFuture<Void> listenableFuture;
            MediaRouter.checkCallingThread();
            if (this.mFinished || this.mCanceled) {
                return;
            }
            GlobalMediaRouter globalMediaRouter = this.mRouter.get();
            if (globalMediaRouter == null || globalMediaRouter.mTransferNotifier != this || ((listenableFuture = this.mFuture) != null && listenableFuture.isCancelled())) {
                cancel();
                return;
            }
            this.mFinished = true;
            globalMediaRouter.mTransferNotifier = null;
            unselectFromRouteAndNotify();
            selectToRouteAndNotify();
        }

        void setFuture(ListenableFuture<Void> future) {
            GlobalMediaRouter globalMediaRouter = this.mRouter.get();
            if (globalMediaRouter == null || globalMediaRouter.mTransferNotifier != this) {
                Log.w("MediaRouter", "Router is released. Cancel transfer");
                cancel();
            } else if (this.mFuture != null) {
                throw new IllegalStateException("future is already set");
            } else {
                this.mFuture = future;
                Runnable runnable = new Runnable() { // from class: androidx.mediarouter.media.MediaRouter$PrepareTransferNotifier$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaRouter.PrepareTransferNotifier.this.lambda$new$0();
                    }
                };
                final GlobalMediaRouter.CallbackHandler callbackHandler = globalMediaRouter.mCallbackHandler;
                Objects.requireNonNull(callbackHandler);
                future.addListener(runnable, new Executor() { // from class: androidx.mediarouter.media.MediaRouter$PrepareTransferNotifier$$ExternalSyntheticLambda2
                    @Override // java.util.concurrent.Executor
                    public final void execute(Runnable runnable2) {
                        MediaRouter.GlobalMediaRouter.CallbackHandler.this.post(runnable2);
                    }
                });
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderInfo {
        private MediaRouteProviderDescriptor mDescriptor;
        private final MediaRouteProvider.ProviderMetadata mMetadata;
        final MediaRouteProvider mProviderInstance;
        final List<RouteInfo> mRoutes = new ArrayList();

        ProviderInfo(MediaRouteProvider provider) {
            this.mProviderInstance = provider;
            this.mMetadata = provider.getMetadata();
        }

        RouteInfo findRouteByDescriptorId(String id) {
            int size = this.mRoutes.size();
            for (int i = 0; i < size; i++) {
                if (this.mRoutes.get(i).mDescriptorId.equals(id)) {
                    return this.mRoutes.get(i);
                }
            }
            return null;
        }

        int findRouteIndexByDescriptorId(String id) {
            int size = this.mRoutes.size();
            for (int i = 0; i < size; i++) {
                if (this.mRoutes.get(i).mDescriptorId.equals(id)) {
                    return i;
                }
            }
            return -1;
        }

        public ComponentName getComponentName() {
            return this.mMetadata.getComponentName();
        }

        public String getPackageName() {
            return this.mMetadata.getPackageName();
        }

        public MediaRouteProvider getProviderInstance() {
            MediaRouter.checkCallingThread();
            return this.mProviderInstance;
        }

        public List<RouteInfo> getRoutes() {
            MediaRouter.checkCallingThread();
            return Collections.unmodifiableList(this.mRoutes);
        }

        boolean supportsDynamicGroup() {
            MediaRouteProviderDescriptor mediaRouteProviderDescriptor = this.mDescriptor;
            return mediaRouteProviderDescriptor != null && mediaRouteProviderDescriptor.supportsDynamicGroupRoute();
        }

        public String toString() {
            return "MediaRouter.RouteProviderInfo{ packageName=" + getPackageName() + " }";
        }

        boolean updateDescriptor(MediaRouteProviderDescriptor descriptor) {
            if (this.mDescriptor != descriptor) {
                this.mDescriptor = descriptor;
                return true;
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static class RouteInfo {
        private boolean mCanDisconnect;
        private int mConnectionState;
        private String mDescription;
        MediaRouteDescriptor mDescriptor;
        final String mDescriptorId;
        private int mDeviceType;
        private Map<String, MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor> mDynamicGroupDescriptors;
        boolean mEnabled;
        private Bundle mExtras;
        private Uri mIconUri;
        private String mName;
        private int mPlaybackStream;
        private int mPlaybackType;
        private Display mPresentationDisplay;
        private final ProviderInfo mProvider;
        private IntentSender mSettingsIntent;
        final String mUniqueId;
        private int mVolume;
        private int mVolumeHandling;
        private int mVolumeMax;
        private final ArrayList<IntentFilter> mControlFilters = new ArrayList<>();
        private int mPresentationDisplayId = -1;
        private List<RouteInfo> mMemberRoutes = new ArrayList();

        /* loaded from: classes.dex */
        public static final class DynamicGroupState {
            final MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor mDynamicDescriptor;

            DynamicGroupState(MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor descriptor) {
                this.mDynamicDescriptor = descriptor;
            }

            public int getSelectionState() {
                MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor dynamicRouteDescriptor = this.mDynamicDescriptor;
                if (dynamicRouteDescriptor != null) {
                    return dynamicRouteDescriptor.getSelectionState();
                }
                return 1;
            }

            public boolean isGroupable() {
                MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor dynamicRouteDescriptor = this.mDynamicDescriptor;
                return dynamicRouteDescriptor != null && dynamicRouteDescriptor.isGroupable();
            }

            public boolean isTransferable() {
                MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor dynamicRouteDescriptor = this.mDynamicDescriptor;
                return dynamicRouteDescriptor != null && dynamicRouteDescriptor.isTransferable();
            }

            public boolean isUnselectable() {
                MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor dynamicRouteDescriptor = this.mDynamicDescriptor;
                return dynamicRouteDescriptor == null || dynamicRouteDescriptor.isUnselectable();
            }
        }

        RouteInfo(ProviderInfo provider, String descriptorId, String uniqueId) {
            this.mProvider = provider;
            this.mDescriptorId = descriptorId;
            this.mUniqueId = uniqueId;
        }

        private boolean isSameControlFilter(IntentFilter filter1, IntentFilter filter2) {
            int countActions;
            if (filter1 == filter2) {
                return true;
            }
            if (filter1 == null || filter2 == null || (countActions = filter1.countActions()) != filter2.countActions()) {
                return false;
            }
            for (int i = 0; i < countActions; i++) {
                if (!filter1.getAction(i).equals(filter2.getAction(i))) {
                    return false;
                }
            }
            int countCategories = filter1.countCategories();
            if (countCategories != filter2.countCategories()) {
                return false;
            }
            for (int i2 = 0; i2 < countCategories; i2++) {
                if (!filter1.getCategory(i2).equals(filter2.getCategory(i2))) {
                    return false;
                }
            }
            return true;
        }

        private boolean isSameControlFilters(List<IntentFilter> filters1, List<IntentFilter> filters2) {
            if (filters1 == filters2) {
                return true;
            }
            if (filters1 == null || filters2 == null) {
                return false;
            }
            ListIterator<IntentFilter> listIterator = filters1.listIterator();
            ListIterator<IntentFilter> listIterator2 = filters2.listIterator();
            while (listIterator.hasNext() && listIterator2.hasNext()) {
                if (!isSameControlFilter(listIterator.next(), listIterator2.next())) {
                    return false;
                }
            }
            return (listIterator.hasNext() || listIterator2.hasNext()) ? false : true;
        }

        private static boolean isSystemMediaRouteProvider(RouteInfo route) {
            return TextUtils.equals(route.getProviderInstance().getMetadata().getPackageName(), ThemeResources.FRAMEWORK_PACKAGE);
        }

        public boolean canDisconnect() {
            return this.mCanDisconnect;
        }

        RouteInfo findRouteByDynamicRouteDescriptor(MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor dynamicDescriptor) {
            return getProvider().findRouteByDescriptorId(dynamicDescriptor.getRouteDescriptor().getId());
        }

        public int getConnectionState() {
            return this.mConnectionState;
        }

        public String getDescription() {
            return this.mDescription;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getDescriptorId() {
            return this.mDescriptorId;
        }

        public int getDeviceType() {
            return this.mDeviceType;
        }

        public MediaRouteProvider.DynamicGroupRouteController getDynamicGroupController() {
            MediaRouteProvider.RouteController routeController = MediaRouter.sGlobal.mSelectedRouteController;
            if (routeController instanceof MediaRouteProvider.DynamicGroupRouteController) {
                return (MediaRouteProvider.DynamicGroupRouteController) routeController;
            }
            return null;
        }

        public DynamicGroupState getDynamicGroupState(RouteInfo route) {
            Objects.requireNonNull(route, "route must not be null");
            Map<String, MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor> map = this.mDynamicGroupDescriptors;
            if (map == null || !map.containsKey(route.mUniqueId)) {
                return null;
            }
            return new DynamicGroupState(this.mDynamicGroupDescriptors.get(route.mUniqueId));
        }

        public Uri getIconUri() {
            return this.mIconUri;
        }

        public String getId() {
            return this.mUniqueId;
        }

        public List<RouteInfo> getMemberRoutes() {
            return Collections.unmodifiableList(this.mMemberRoutes);
        }

        public String getName() {
            return this.mName;
        }

        public int getPlaybackStream() {
            return this.mPlaybackStream;
        }

        public int getPlaybackType() {
            return this.mPlaybackType;
        }

        public int getPresentationDisplayId() {
            return this.mPresentationDisplayId;
        }

        public ProviderInfo getProvider() {
            return this.mProvider;
        }

        public MediaRouteProvider getProviderInstance() {
            return this.mProvider.getProviderInstance();
        }

        public int getVolume() {
            return this.mVolume;
        }

        public int getVolumeHandling() {
            return this.mVolumeHandling;
        }

        public int getVolumeMax() {
            return this.mVolumeMax;
        }

        public boolean isDefault() {
            MediaRouter.checkCallingThread();
            return MediaRouter.sGlobal.getDefaultRoute() == this;
        }

        public boolean isDefaultOrBluetooth() {
            if (isDefault() || this.mDeviceType == 3) {
                return true;
            }
            return isSystemMediaRouteProvider(this) && supportsControlCategory("android.media.intent.category.LIVE_AUDIO") && !supportsControlCategory("android.media.intent.category.LIVE_VIDEO");
        }

        public boolean isEnabled() {
            return this.mEnabled;
        }

        public boolean isGroup() {
            return getMemberRoutes().size() >= 1;
        }

        boolean isSelectable() {
            return this.mDescriptor != null && this.mEnabled;
        }

        public boolean isSelected() {
            MediaRouter.checkCallingThread();
            return MediaRouter.sGlobal.getSelectedRoute() == this;
        }

        public boolean matchesSelector(MediaRouteSelector selector) {
            if (selector != null) {
                MediaRouter.checkCallingThread();
                return selector.matchesControlFilters(this.mControlFilters);
            }
            throw new IllegalArgumentException("selector must not be null");
        }

        int maybeUpdateDescriptor(MediaRouteDescriptor descriptor) {
            if (this.mDescriptor != descriptor) {
                return updateDescriptor(descriptor);
            }
            return 0;
        }

        public void requestSetVolume(int volume) {
            MediaRouter.checkCallingThread();
            MediaRouter.sGlobal.requestSetVolume(this, Math.min(this.mVolumeMax, Math.max(0, volume)));
        }

        public void requestUpdateVolume(int delta) {
            MediaRouter.checkCallingThread();
            if (delta != 0) {
                MediaRouter.sGlobal.requestUpdateVolume(this, delta);
            }
        }

        public void select() {
            MediaRouter.checkCallingThread();
            MediaRouter.sGlobal.selectRoute(this, 3);
        }

        public boolean supportsControlCategory(String category) {
            if (category != null) {
                MediaRouter.checkCallingThread();
                int size = this.mControlFilters.size();
                for (int i = 0; i < size; i++) {
                    if (this.mControlFilters.get(i).hasCategory(category)) {
                        return true;
                    }
                }
                return false;
            }
            throw new IllegalArgumentException("category must not be null");
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("MediaRouter.RouteInfo{ uniqueId=" + this.mUniqueId + ", name=" + this.mName + ", description=" + this.mDescription + ", iconUri=" + this.mIconUri + ", enabled=" + this.mEnabled + ", connectionState=" + this.mConnectionState + ", canDisconnect=" + this.mCanDisconnect + ", playbackType=" + this.mPlaybackType + ", playbackStream=" + this.mPlaybackStream + ", deviceType=" + this.mDeviceType + ", volumeHandling=" + this.mVolumeHandling + ", volume=" + this.mVolume + ", volumeMax=" + this.mVolumeMax + ", presentationDisplayId=" + this.mPresentationDisplayId + ", extras=" + this.mExtras + ", settingsIntent=" + this.mSettingsIntent + ", providerPackageName=" + this.mProvider.getPackageName());
            if (isGroup()) {
                sb.append(", members=[");
                int size = this.mMemberRoutes.size();
                for (int i = 0; i < size; i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    if (this.mMemberRoutes.get(i) != this) {
                        sb.append(this.mMemberRoutes.get(i).getId());
                    }
                }
                sb.append(']');
            }
            sb.append(" }");
            return sb.toString();
        }

        int updateDescriptor(MediaRouteDescriptor descriptor) {
            int i;
            this.mDescriptor = descriptor;
            if (descriptor != null) {
                if (ObjectsCompat.equals(this.mName, descriptor.getName())) {
                    i = 0;
                } else {
                    this.mName = descriptor.getName();
                    i = 1;
                }
                if (!ObjectsCompat.equals(this.mDescription, descriptor.getDescription())) {
                    this.mDescription = descriptor.getDescription();
                    i |= 1;
                }
                if (!ObjectsCompat.equals(this.mIconUri, descriptor.getIconUri())) {
                    this.mIconUri = descriptor.getIconUri();
                    i |= 1;
                }
                if (this.mEnabled != descriptor.isEnabled()) {
                    this.mEnabled = descriptor.isEnabled();
                    i |= 1;
                }
                if (this.mConnectionState != descriptor.getConnectionState()) {
                    this.mConnectionState = descriptor.getConnectionState();
                    i |= 1;
                }
                if (!isSameControlFilters(this.mControlFilters, descriptor.getControlFilters())) {
                    this.mControlFilters.clear();
                    this.mControlFilters.addAll(descriptor.getControlFilters());
                    i |= 1;
                }
                if (this.mPlaybackType != descriptor.getPlaybackType()) {
                    this.mPlaybackType = descriptor.getPlaybackType();
                    i |= 1;
                }
                if (this.mPlaybackStream != descriptor.getPlaybackStream()) {
                    this.mPlaybackStream = descriptor.getPlaybackStream();
                    i |= 1;
                }
                if (this.mDeviceType != descriptor.getDeviceType()) {
                    this.mDeviceType = descriptor.getDeviceType();
                    i |= 1;
                }
                if (this.mVolumeHandling != descriptor.getVolumeHandling()) {
                    this.mVolumeHandling = descriptor.getVolumeHandling();
                    i |= 3;
                }
                if (this.mVolume != descriptor.getVolume()) {
                    this.mVolume = descriptor.getVolume();
                    i |= 3;
                }
                if (this.mVolumeMax != descriptor.getVolumeMax()) {
                    this.mVolumeMax = descriptor.getVolumeMax();
                    i |= 3;
                }
                if (this.mPresentationDisplayId != descriptor.getPresentationDisplayId()) {
                    this.mPresentationDisplayId = descriptor.getPresentationDisplayId();
                    this.mPresentationDisplay = null;
                    i |= 5;
                }
                if (!ObjectsCompat.equals(this.mExtras, descriptor.getExtras())) {
                    this.mExtras = descriptor.getExtras();
                    i |= 1;
                }
                if (!ObjectsCompat.equals(this.mSettingsIntent, descriptor.getSettingsActivity())) {
                    this.mSettingsIntent = descriptor.getSettingsActivity();
                    i |= 1;
                }
                if (this.mCanDisconnect != descriptor.canDisconnectAndKeepPlaying()) {
                    this.mCanDisconnect = descriptor.canDisconnectAndKeepPlaying();
                    i |= 5;
                }
                List<String> groupMemberIds = descriptor.getGroupMemberIds();
                ArrayList arrayList = new ArrayList();
                boolean z = groupMemberIds.size() != this.mMemberRoutes.size();
                Iterator<String> it = groupMemberIds.iterator();
                while (it.hasNext()) {
                    RouteInfo route = MediaRouter.sGlobal.getRoute(MediaRouter.sGlobal.getUniqueId(getProvider(), it.next()));
                    if (route != null) {
                        arrayList.add(route);
                        if (!z && !this.mMemberRoutes.contains(route)) {
                            z = true;
                        }
                    }
                }
                if (z) {
                    this.mMemberRoutes = arrayList;
                    return i | 1;
                }
                return i;
            }
            return 0;
        }

        void updateDynamicDescriptors(Collection<MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor> dynamicDescriptors) {
            this.mMemberRoutes.clear();
            if (this.mDynamicGroupDescriptors == null) {
                this.mDynamicGroupDescriptors = new ArrayMap();
            }
            this.mDynamicGroupDescriptors.clear();
            for (MediaRouteProvider.DynamicGroupRouteController.DynamicRouteDescriptor dynamicRouteDescriptor : dynamicDescriptors) {
                RouteInfo findRouteByDynamicRouteDescriptor = findRouteByDynamicRouteDescriptor(dynamicRouteDescriptor);
                if (findRouteByDynamicRouteDescriptor != null) {
                    this.mDynamicGroupDescriptors.put(findRouteByDynamicRouteDescriptor.mUniqueId, dynamicRouteDescriptor);
                    if (dynamicRouteDescriptor.getSelectionState() == 2 || dynamicRouteDescriptor.getSelectionState() == 3) {
                        this.mMemberRoutes.add(findRouteByDynamicRouteDescriptor);
                    }
                }
            }
            MediaRouter.sGlobal.mCallbackHandler.post(259, this);
        }
    }

    MediaRouter(Context context) {
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkCallingThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("The media router service must only be accessed on the application's main thread.");
        }
    }

    private int findCallbackRecord(Callback callback) {
        int size = this.mCallbackRecords.size();
        for (int i = 0; i < size; i++) {
            if (this.mCallbackRecords.get(i).mCallback == callback) {
                return i;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getGlobalCallbackCount() {
        GlobalMediaRouter globalMediaRouter = sGlobal;
        if (globalMediaRouter == null) {
            return 0;
        }
        return globalMediaRouter.getCallbackCount();
    }

    public static MediaRouter getInstance(Context context) {
        if (context != null) {
            checkCallingThread();
            if (sGlobal == null) {
                GlobalMediaRouter globalMediaRouter = new GlobalMediaRouter(context.getApplicationContext());
                sGlobal = globalMediaRouter;
                globalMediaRouter.start();
            }
            return sGlobal.getRouter(context);
        }
        throw new IllegalArgumentException("context must not be null");
    }

    public static boolean isMediaTransferEnabled() {
        GlobalMediaRouter globalMediaRouter = sGlobal;
        if (globalMediaRouter == null) {
            return false;
        }
        return globalMediaRouter.isMediaTransferEnabled();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isTransferToLocalEnabled() {
        GlobalMediaRouter globalMediaRouter = sGlobal;
        if (globalMediaRouter == null) {
            return false;
        }
        return globalMediaRouter.isTransferToLocalEnabled();
    }

    public void addCallback(MediaRouteSelector selector, Callback callback) {
        addCallback(selector, callback, 0);
    }

    public void addCallback(MediaRouteSelector selector, Callback callback, int flags) {
        CallbackRecord callbackRecord;
        if (selector == null) {
            throw new IllegalArgumentException("selector must not be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        checkCallingThread();
        if (DEBUG) {
            Log.d("MediaRouter", "addCallback: selector=" + selector + ", callback=" + callback + ", flags=" + Integer.toHexString(flags));
        }
        int findCallbackRecord = findCallbackRecord(callback);
        if (findCallbackRecord < 0) {
            callbackRecord = new CallbackRecord(this, callback);
            this.mCallbackRecords.add(callbackRecord);
        } else {
            callbackRecord = this.mCallbackRecords.get(findCallbackRecord);
        }
        boolean z = false;
        boolean z2 = true;
        if (flags != callbackRecord.mFlags) {
            callbackRecord.mFlags = flags;
            z = true;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if ((flags & 1) != 0) {
            z = true;
        }
        callbackRecord.mTimestamp = elapsedRealtime;
        if (callbackRecord.mSelector.contains(selector)) {
            z2 = z;
        } else {
            callbackRecord.mSelector = new MediaRouteSelector.Builder(callbackRecord.mSelector).addSelector(selector).build();
        }
        if (z2) {
            sGlobal.updateDiscoveryRequest();
        }
    }

    public void addMemberToDynamicGroup(RouteInfo route) {
        Objects.requireNonNull(route, "route must not be null");
        checkCallingThread();
        sGlobal.addMemberToDynamicGroup(route);
    }

    public MediaSessionCompat.Token getMediaSessionToken() {
        GlobalMediaRouter globalMediaRouter = sGlobal;
        if (globalMediaRouter == null) {
            return null;
        }
        return globalMediaRouter.getMediaSessionToken();
    }

    public MediaRouterParams getRouterParams() {
        checkCallingThread();
        GlobalMediaRouter globalMediaRouter = sGlobal;
        if (globalMediaRouter == null) {
            return null;
        }
        return globalMediaRouter.getRouterParams();
    }

    public List<RouteInfo> getRoutes() {
        checkCallingThread();
        GlobalMediaRouter globalMediaRouter = sGlobal;
        return globalMediaRouter == null ? Collections.emptyList() : globalMediaRouter.getRoutes();
    }

    public RouteInfo getSelectedRoute() {
        checkCallingThread();
        return sGlobal.getSelectedRoute();
    }

    public boolean isRouteAvailable(MediaRouteSelector selector, int flags) {
        if (selector != null) {
            checkCallingThread();
            return sGlobal.isRouteAvailable(selector, flags);
        }
        throw new IllegalArgumentException("selector must not be null");
    }

    public void removeCallback(Callback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        checkCallingThread();
        if (DEBUG) {
            Log.d("MediaRouter", "removeCallback: callback=" + callback);
        }
        int findCallbackRecord = findCallbackRecord(callback);
        if (findCallbackRecord >= 0) {
            this.mCallbackRecords.remove(findCallbackRecord);
            sGlobal.updateDiscoveryRequest();
        }
    }

    public void removeMemberFromDynamicGroup(RouteInfo route) {
        Objects.requireNonNull(route, "route must not be null");
        checkCallingThread();
        sGlobal.removeMemberFromDynamicGroup(route);
    }

    public void transferToRoute(RouteInfo route) {
        Objects.requireNonNull(route, "route must not be null");
        checkCallingThread();
        sGlobal.transferToRoute(route);
    }

    public void unselect(int reason) {
        if (reason < 0 || reason > 3) {
            throw new IllegalArgumentException("Unsupported reason to unselect route");
        }
        checkCallingThread();
        RouteInfo chooseFallbackRoute = sGlobal.chooseFallbackRoute();
        if (sGlobal.getSelectedRoute() != chooseFallbackRoute) {
            sGlobal.selectRoute(chooseFallbackRoute, reason);
        }
    }
}
