package androidx.mediarouter.media;

import android.content.Context;
import android.os.Build;
import androidx.mediarouter.media.MediaRouterJellybean;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
abstract class RemoteControlClientCompat {
    protected final Context mContext;
    protected final Object mRcc;
    protected VolumeCallback mVolumeCallback;

    /* loaded from: classes.dex */
    static class JellybeanImpl extends RemoteControlClientCompat {
        private boolean mRegistered;
        private final Object mRouterObj;
        private final Object mUserRouteCategoryObj;
        private final Object mUserRouteObj;

        /* loaded from: classes.dex */
        private static final class VolumeCallbackWrapper implements MediaRouterJellybean.VolumeCallback {
            private final WeakReference<JellybeanImpl> mImplWeak;

            public VolumeCallbackWrapper(JellybeanImpl impl) {
                this.mImplWeak = new WeakReference<>(impl);
            }

            @Override // androidx.mediarouter.media.MediaRouterJellybean.VolumeCallback
            public void onVolumeSetRequest(Object routeObj, int volume) {
                VolumeCallback volumeCallback;
                JellybeanImpl jellybeanImpl = this.mImplWeak.get();
                if (jellybeanImpl == null || (volumeCallback = jellybeanImpl.mVolumeCallback) == null) {
                    return;
                }
                volumeCallback.onVolumeSetRequest(volume);
            }

            @Override // androidx.mediarouter.media.MediaRouterJellybean.VolumeCallback
            public void onVolumeUpdateRequest(Object routeObj, int direction) {
                VolumeCallback volumeCallback;
                JellybeanImpl jellybeanImpl = this.mImplWeak.get();
                if (jellybeanImpl == null || (volumeCallback = jellybeanImpl.mVolumeCallback) == null) {
                    return;
                }
                volumeCallback.onVolumeUpdateRequest(direction);
            }
        }

        public JellybeanImpl(Context context, Object rcc) {
            super(context, rcc);
            Object mediaRouter = MediaRouterJellybean.getMediaRouter(context);
            this.mRouterObj = mediaRouter;
            Object createRouteCategory = MediaRouterJellybean.createRouteCategory(mediaRouter, "", false);
            this.mUserRouteCategoryObj = createRouteCategory;
            this.mUserRouteObj = MediaRouterJellybean.createUserRoute(mediaRouter, createRouteCategory);
        }

        @Override // androidx.mediarouter.media.RemoteControlClientCompat
        public void setPlaybackInfo(PlaybackInfo info) {
            MediaRouterJellybean.UserRouteInfo.setVolume(this.mUserRouteObj, info.volume);
            MediaRouterJellybean.UserRouteInfo.setVolumeMax(this.mUserRouteObj, info.volumeMax);
            MediaRouterJellybean.UserRouteInfo.setVolumeHandling(this.mUserRouteObj, info.volumeHandling);
            MediaRouterJellybean.UserRouteInfo.setPlaybackStream(this.mUserRouteObj, info.playbackStream);
            MediaRouterJellybean.UserRouteInfo.setPlaybackType(this.mUserRouteObj, info.playbackType);
            if (this.mRegistered) {
                return;
            }
            this.mRegistered = true;
            MediaRouterJellybean.UserRouteInfo.setVolumeCallback(this.mUserRouteObj, MediaRouterJellybean.createVolumeCallback(new VolumeCallbackWrapper(this)));
            MediaRouterJellybean.UserRouteInfo.setRemoteControlClient(this.mUserRouteObj, this.mRcc);
        }
    }

    /* loaded from: classes.dex */
    static class LegacyImpl extends RemoteControlClientCompat {
        public LegacyImpl(Context context, Object rcc) {
            super(context, rcc);
        }
    }

    /* loaded from: classes.dex */
    public static final class PlaybackInfo {
        public int volume;
        public String volumeControlId;
        public int volumeMax;
        public int volumeHandling = 0;
        public int playbackStream = 3;
        public int playbackType = 1;
    }

    /* loaded from: classes.dex */
    public interface VolumeCallback {
        void onVolumeSetRequest(int volume);

        void onVolumeUpdateRequest(int direction);
    }

    protected RemoteControlClientCompat(Context context, Object rcc) {
        this.mContext = context;
        this.mRcc = rcc;
    }

    public static RemoteControlClientCompat obtain(Context context, Object rcc) {
        return Build.VERSION.SDK_INT >= 16 ? new JellybeanImpl(context, rcc) : new LegacyImpl(context, rcc);
    }

    public Object getRemoteControlClient() {
        return this.mRcc;
    }

    public void setPlaybackInfo(PlaybackInfo info) {
    }

    public void setVolumeCallback(VolumeCallback callback) {
        this.mVolumeCallback = callback;
    }
}
