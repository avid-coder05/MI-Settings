package androidx.mediarouter.media;

import android.media.MediaRouter;

/* loaded from: classes.dex */
final class MediaRouterJellybeanMr2 {

    /* loaded from: classes.dex */
    public static final class RouteInfo {
        public static CharSequence getDescription(Object routeObj) {
            return ((MediaRouter.RouteInfo) routeObj).getDescription();
        }

        public static boolean isConnecting(Object routeObj) {
            return ((MediaRouter.RouteInfo) routeObj).isConnecting();
        }
    }

    /* loaded from: classes.dex */
    public static final class UserRouteInfo {
        public static void setDescription(Object routeObj, CharSequence description) {
            ((MediaRouter.UserRouteInfo) routeObj).setDescription(description);
        }
    }

    public static void addCallback(Object routerObj, int types, Object callbackObj, int flags) {
        ((android.media.MediaRouter) routerObj).addCallback(types, (MediaRouter.Callback) callbackObj, flags);
    }

    public static Object getDefaultRoute(Object routerObj) {
        return ((android.media.MediaRouter) routerObj).getDefaultRoute();
    }
}
