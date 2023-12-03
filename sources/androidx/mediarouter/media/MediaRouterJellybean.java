package androidx.mediarouter.media;

import android.content.Context;
import android.media.MediaRouter;
import android.media.RemoteControlClient;
import android.os.Build;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
final class MediaRouterJellybean {

    /* loaded from: classes.dex */
    public interface Callback {
        void onRouteAdded(Object routeObj);

        void onRouteChanged(Object routeObj);

        void onRouteGrouped(Object routeObj, Object groupObj, int index);

        void onRouteRemoved(Object routeObj);

        void onRouteSelected(int type, Object routeObj);

        void onRouteUngrouped(Object routeObj, Object groupObj);

        void onRouteUnselected(int type, Object routeObj);

        void onRouteVolumeChanged(Object routeObj);
    }

    /* loaded from: classes.dex */
    static class CallbackProxy<T extends Callback> extends MediaRouter.Callback {
        protected final T mCallback;

        public CallbackProxy(T callback) {
            this.mCallback = callback;
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteAdded(android.media.MediaRouter router, MediaRouter.RouteInfo route) {
            this.mCallback.onRouteAdded(route);
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteChanged(android.media.MediaRouter router, MediaRouter.RouteInfo route) {
            this.mCallback.onRouteChanged(route);
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteGrouped(android.media.MediaRouter router, MediaRouter.RouteInfo route, MediaRouter.RouteGroup group, int index) {
            this.mCallback.onRouteGrouped(route, group, index);
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteRemoved(android.media.MediaRouter router, MediaRouter.RouteInfo route) {
            this.mCallback.onRouteRemoved(route);
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteSelected(android.media.MediaRouter router, int type, MediaRouter.RouteInfo route) {
            this.mCallback.onRouteSelected(type, route);
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteUngrouped(android.media.MediaRouter router, MediaRouter.RouteInfo route, MediaRouter.RouteGroup group) {
            this.mCallback.onRouteUngrouped(route, group);
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteUnselected(android.media.MediaRouter router, int type, MediaRouter.RouteInfo route) {
            this.mCallback.onRouteUnselected(type, route);
        }

        @Override // android.media.MediaRouter.Callback
        public void onRouteVolumeChanged(android.media.MediaRouter router, MediaRouter.RouteInfo route) {
            this.mCallback.onRouteVolumeChanged(route);
        }
    }

    /* loaded from: classes.dex */
    public static final class GetDefaultRouteWorkaround {
        private Method mGetSystemAudioRouteMethod;

        public GetDefaultRouteWorkaround() {
            int i = Build.VERSION.SDK_INT;
            if (i < 16 || i > 17) {
                throw new UnsupportedOperationException();
            }
            try {
                this.mGetSystemAudioRouteMethod = android.media.MediaRouter.class.getMethod("getSystemAudioRoute", new Class[0]);
            } catch (NoSuchMethodException unused) {
            }
        }

        public Object getDefaultRoute(Object routerObj) {
            android.media.MediaRouter mediaRouter = (android.media.MediaRouter) routerObj;
            Method method = this.mGetSystemAudioRouteMethod;
            if (method != null) {
                try {
                    return method.invoke(mediaRouter, new Object[0]);
                } catch (IllegalAccessException | InvocationTargetException unused) {
                }
            }
            return mediaRouter.getRouteAt(0);
        }
    }

    /* loaded from: classes.dex */
    public static final class RouteInfo {
        public static CharSequence getName(Object routeObj, Context context) {
            return ((MediaRouter.RouteInfo) routeObj).getName(context);
        }

        public static int getPlaybackStream(Object routeObj) {
            return ((MediaRouter.RouteInfo) routeObj).getPlaybackStream();
        }

        public static int getPlaybackType(Object routeObj) {
            return ((MediaRouter.RouteInfo) routeObj).getPlaybackType();
        }

        public static int getSupportedTypes(Object routeObj) {
            return ((MediaRouter.RouteInfo) routeObj).getSupportedTypes();
        }

        public static Object getTag(Object routeObj) {
            return ((MediaRouter.RouteInfo) routeObj).getTag();
        }

        public static int getVolume(Object routeObj) {
            return ((MediaRouter.RouteInfo) routeObj).getVolume();
        }

        public static int getVolumeHandling(Object routeObj) {
            return ((MediaRouter.RouteInfo) routeObj).getVolumeHandling();
        }

        public static int getVolumeMax(Object routeObj) {
            return ((MediaRouter.RouteInfo) routeObj).getVolumeMax();
        }

        public static void requestSetVolume(Object routeObj, int volume) {
            ((MediaRouter.RouteInfo) routeObj).requestSetVolume(volume);
        }

        public static void requestUpdateVolume(Object routeObj, int direction) {
            ((MediaRouter.RouteInfo) routeObj).requestUpdateVolume(direction);
        }

        public static void setTag(Object routeObj, Object tag) {
            ((MediaRouter.RouteInfo) routeObj).setTag(tag);
        }
    }

    /* loaded from: classes.dex */
    public static final class SelectRouteWorkaround {
        private Method mSelectRouteIntMethod;

        public SelectRouteWorkaround() {
            int i = Build.VERSION.SDK_INT;
            if (i < 16 || i > 17) {
                throw new UnsupportedOperationException();
            }
            try {
                this.mSelectRouteIntMethod = android.media.MediaRouter.class.getMethod("selectRouteInt", Integer.TYPE, MediaRouter.RouteInfo.class);
            } catch (NoSuchMethodException unused) {
            }
        }

        public void selectRoute(Object routerObj, int types, Object routeObj) {
            android.media.MediaRouter mediaRouter = (android.media.MediaRouter) routerObj;
            MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo) routeObj;
            if ((routeInfo.getSupportedTypes() & MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_NO_SCREENSHOT) == 0) {
                Method method = this.mSelectRouteIntMethod;
                if (method != null) {
                    try {
                        method.invoke(mediaRouter, Integer.valueOf(types), routeInfo);
                        return;
                    } catch (IllegalAccessException e) {
                        Log.w("MediaRouterJellybean", "Cannot programmatically select non-user route.  Media routing may not work.", e);
                    } catch (InvocationTargetException e2) {
                        Log.w("MediaRouterJellybean", "Cannot programmatically select non-user route.  Media routing may not work.", e2);
                    }
                } else {
                    Log.w("MediaRouterJellybean", "Cannot programmatically select non-user route because the platform is missing the selectRouteInt() method.  Media routing may not work.");
                }
            }
            mediaRouter.selectRoute(types, routeInfo);
        }
    }

    /* loaded from: classes.dex */
    public static final class UserRouteInfo {
        public static void setName(Object routeObj, CharSequence name) {
            ((MediaRouter.UserRouteInfo) routeObj).setName(name);
        }

        public static void setPlaybackStream(Object routeObj, int stream) {
            ((MediaRouter.UserRouteInfo) routeObj).setPlaybackStream(stream);
        }

        public static void setPlaybackType(Object routeObj, int type) {
            ((MediaRouter.UserRouteInfo) routeObj).setPlaybackType(type);
        }

        public static void setRemoteControlClient(Object routeObj, Object rccObj) {
            ((MediaRouter.UserRouteInfo) routeObj).setRemoteControlClient((RemoteControlClient) rccObj);
        }

        public static void setVolume(Object routeObj, int volume) {
            ((MediaRouter.UserRouteInfo) routeObj).setVolume(volume);
        }

        public static void setVolumeCallback(Object routeObj, Object volumeCallbackObj) {
            ((MediaRouter.UserRouteInfo) routeObj).setVolumeCallback((MediaRouter.VolumeCallback) volumeCallbackObj);
        }

        public static void setVolumeHandling(Object routeObj, int volumeHandling) {
            ((MediaRouter.UserRouteInfo) routeObj).setVolumeHandling(volumeHandling);
        }

        public static void setVolumeMax(Object routeObj, int volumeMax) {
            ((MediaRouter.UserRouteInfo) routeObj).setVolumeMax(volumeMax);
        }
    }

    /* loaded from: classes.dex */
    public interface VolumeCallback {
        void onVolumeSetRequest(Object routeObj, int volume);

        void onVolumeUpdateRequest(Object routeObj, int direction);
    }

    /* loaded from: classes.dex */
    static class VolumeCallbackProxy<T extends VolumeCallback> extends MediaRouter.VolumeCallback {
        protected final T mCallback;

        public VolumeCallbackProxy(T callback) {
            this.mCallback = callback;
        }

        @Override // android.media.MediaRouter.VolumeCallback
        public void onVolumeSetRequest(MediaRouter.RouteInfo route, int volume) {
            this.mCallback.onVolumeSetRequest(route, volume);
        }

        @Override // android.media.MediaRouter.VolumeCallback
        public void onVolumeUpdateRequest(MediaRouter.RouteInfo route, int direction) {
            this.mCallback.onVolumeUpdateRequest(route, direction);
        }
    }

    public static void addCallback(Object routerObj, int types, Object callbackObj) {
        ((android.media.MediaRouter) routerObj).addCallback(types, (MediaRouter.Callback) callbackObj);
    }

    public static void addUserRoute(Object routerObj, Object routeObj) {
        ((android.media.MediaRouter) routerObj).addUserRoute((MediaRouter.UserRouteInfo) routeObj);
    }

    public static Object createCallback(Callback callback) {
        return new CallbackProxy(callback);
    }

    public static Object createRouteCategory(Object routerObj, String name, boolean isGroupable) {
        return ((android.media.MediaRouter) routerObj).createRouteCategory(name, isGroupable);
    }

    public static Object createUserRoute(Object routerObj, Object categoryObj) {
        return ((android.media.MediaRouter) routerObj).createUserRoute((MediaRouter.RouteCategory) categoryObj);
    }

    public static Object createVolumeCallback(VolumeCallback callback) {
        return new VolumeCallbackProxy(callback);
    }

    public static Object getMediaRouter(Context context) {
        return context.getSystemService("media_router");
    }

    public static List getRoutes(Object routerObj) {
        android.media.MediaRouter mediaRouter = (android.media.MediaRouter) routerObj;
        int routeCount = mediaRouter.getRouteCount();
        ArrayList arrayList = new ArrayList(routeCount);
        for (int i = 0; i < routeCount; i++) {
            arrayList.add(mediaRouter.getRouteAt(i));
        }
        return arrayList;
    }

    public static Object getSelectedRoute(Object routerObj, int type) {
        return ((android.media.MediaRouter) routerObj).getSelectedRoute(type);
    }

    public static void removeCallback(Object routerObj, Object callbackObj) {
        ((android.media.MediaRouter) routerObj).removeCallback((MediaRouter.Callback) callbackObj);
    }

    public static void removeUserRoute(Object routerObj, Object routeObj) {
        ((android.media.MediaRouter) routerObj).removeUserRoute((MediaRouter.UserRouteInfo) routeObj);
    }

    public static void selectRoute(Object routerObj, int types, Object routeObj) {
        ((android.media.MediaRouter) routerObj).selectRoute(types, (MediaRouter.RouteInfo) routeObj);
    }
}
