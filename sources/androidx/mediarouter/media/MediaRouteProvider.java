package androidx.mediarouter.media;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.util.ObjectsCompat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

/* loaded from: classes.dex */
public abstract class MediaRouteProvider {
    private Callback mCallback;
    private final Context mContext;
    private MediaRouteProviderDescriptor mDescriptor;
    private MediaRouteDiscoveryRequest mDiscoveryRequest;
    private final ProviderHandler mHandler;
    private final ProviderMetadata mMetadata;
    private boolean mPendingDescriptorChange;
    private boolean mPendingDiscoveryRequestChange;

    /* loaded from: classes.dex */
    public static abstract class Callback {
        public abstract void onDescriptorChanged(MediaRouteProvider provider, MediaRouteProviderDescriptor descriptor);
    }

    /* loaded from: classes.dex */
    public static abstract class DynamicGroupRouteController extends RouteController {
        Executor mExecutor;
        OnDynamicRoutesChangedListener mListener;
        private final Object mLock = new Object();
        MediaRouteDescriptor mPendingGroupRoute;
        Collection<DynamicRouteDescriptor> mPendingRoutes;

        /* loaded from: classes.dex */
        public static final class DynamicRouteDescriptor {
            final boolean mIsGroupable;
            final boolean mIsTransferable;
            final boolean mIsUnselectable;
            final MediaRouteDescriptor mMediaRouteDescriptor;
            final int mSelectionState;

            /* loaded from: classes.dex */
            public static final class Builder {
                private final MediaRouteDescriptor mRouteDescriptor;
                private int mSelectionState = 1;
                private boolean mIsUnselectable = false;
                private boolean mIsGroupable = false;
                private boolean mIsTransferable = false;

                public Builder(MediaRouteDescriptor descriptor) {
                    Objects.requireNonNull(descriptor, "descriptor must not be null");
                    this.mRouteDescriptor = descriptor;
                }

                public DynamicRouteDescriptor build() {
                    return new DynamicRouteDescriptor(this.mRouteDescriptor, this.mSelectionState, this.mIsUnselectable, this.mIsGroupable, this.mIsTransferable);
                }

                public Builder setIsGroupable(boolean value) {
                    this.mIsGroupable = value;
                    return this;
                }

                public Builder setIsTransferable(boolean value) {
                    this.mIsTransferable = value;
                    return this;
                }

                public Builder setIsUnselectable(boolean value) {
                    this.mIsUnselectable = value;
                    return this;
                }

                public Builder setSelectionState(int state) {
                    this.mSelectionState = state;
                    return this;
                }
            }

            DynamicRouteDescriptor(MediaRouteDescriptor mediaRouteDescriptor, int selectionState, boolean isUnselectable, boolean isGroupable, boolean isTransferable) {
                this.mMediaRouteDescriptor = mediaRouteDescriptor;
                this.mSelectionState = selectionState;
                this.mIsUnselectable = isUnselectable;
                this.mIsGroupable = isGroupable;
                this.mIsTransferable = isTransferable;
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            public static DynamicRouteDescriptor fromBundle(Bundle bundle) {
                if (bundle == null) {
                    return null;
                }
                return new DynamicRouteDescriptor(MediaRouteDescriptor.fromBundle(bundle.getBundle("mrDescriptor")), bundle.getInt("selectionState", 1), bundle.getBoolean("isUnselectable", false), bundle.getBoolean("isGroupable", false), bundle.getBoolean("isTransferable", false));
            }

            public MediaRouteDescriptor getRouteDescriptor() {
                return this.mMediaRouteDescriptor;
            }

            public int getSelectionState() {
                return this.mSelectionState;
            }

            public boolean isGroupable() {
                return this.mIsGroupable;
            }

            public boolean isTransferable() {
                return this.mIsTransferable;
            }

            public boolean isUnselectable() {
                return this.mIsUnselectable;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public interface OnDynamicRoutesChangedListener {
            void onRoutesChanged(DynamicGroupRouteController controller, MediaRouteDescriptor groupRoute, Collection<DynamicRouteDescriptor> routes);
        }

        public String getGroupableSelectionTitle() {
            return null;
        }

        public String getTransferableSectionTitle() {
            return null;
        }

        public final void notifyDynamicRoutesChanged(final MediaRouteDescriptor groupRoute, final Collection<DynamicRouteDescriptor> dynamicRoutes) {
            Objects.requireNonNull(groupRoute, "groupRoute must not be null");
            Objects.requireNonNull(dynamicRoutes, "dynamicRoutes must not be null");
            synchronized (this.mLock) {
                Executor executor = this.mExecutor;
                if (executor != null) {
                    final OnDynamicRoutesChangedListener onDynamicRoutesChangedListener = this.mListener;
                    executor.execute(new Runnable() { // from class: androidx.mediarouter.media.MediaRouteProvider.DynamicGroupRouteController.3
                        @Override // java.lang.Runnable
                        public void run() {
                            onDynamicRoutesChangedListener.onRoutesChanged(DynamicGroupRouteController.this, groupRoute, dynamicRoutes);
                        }
                    });
                } else {
                    this.mPendingGroupRoute = groupRoute;
                    this.mPendingRoutes = new ArrayList(dynamicRoutes);
                }
            }
        }

        public abstract void onAddMemberRoute(String routeId);

        public abstract void onRemoveMemberRoute(String routeId);

        public abstract void onUpdateMemberRoutes(List<String> routeIds);

        /* JADX INFO: Access modifiers changed from: package-private */
        public void setOnDynamicRoutesChangedListener(Executor executor, final OnDynamicRoutesChangedListener listener) {
            synchronized (this.mLock) {
                if (executor == null) {
                    throw new NullPointerException("Executor shouldn't be null");
                }
                if (listener == null) {
                    throw new NullPointerException("Listener shouldn't be null");
                }
                this.mExecutor = executor;
                this.mListener = listener;
                Collection<DynamicRouteDescriptor> collection = this.mPendingRoutes;
                if (collection != null && !collection.isEmpty()) {
                    final MediaRouteDescriptor mediaRouteDescriptor = this.mPendingGroupRoute;
                    final Collection<DynamicRouteDescriptor> collection2 = this.mPendingRoutes;
                    this.mPendingGroupRoute = null;
                    this.mPendingRoutes = null;
                    this.mExecutor.execute(new Runnable() { // from class: androidx.mediarouter.media.MediaRouteProvider.DynamicGroupRouteController.1
                        @Override // java.lang.Runnable
                        public void run() {
                            listener.onRoutesChanged(DynamicGroupRouteController.this, mediaRouteDescriptor, collection2);
                        }
                    });
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ProviderHandler extends Handler {
        ProviderHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                MediaRouteProvider.this.deliverDescriptorChanged();
            } else if (i != 2) {
            } else {
                MediaRouteProvider.this.deliverDiscoveryRequestChanged();
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderMetadata {
        private final ComponentName mComponentName;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ProviderMetadata(ComponentName componentName) {
            if (componentName == null) {
                throw new IllegalArgumentException("componentName must not be null");
            }
            this.mComponentName = componentName;
        }

        public ComponentName getComponentName() {
            return this.mComponentName;
        }

        public String getPackageName() {
            return this.mComponentName.getPackageName();
        }

        public String toString() {
            return "ProviderMetadata{ componentName=" + this.mComponentName.flattenToShortString() + " }";
        }
    }

    /* loaded from: classes.dex */
    public static abstract class RouteController {
        public void onRelease() {
        }

        public void onSelect() {
        }

        public void onSetVolume(int volume) {
        }

        @Deprecated
        public void onUnselect() {
        }

        public void onUnselect(int reason) {
            onUnselect();
        }

        public void onUpdateVolume(int delta) {
        }
    }

    public MediaRouteProvider(Context context) {
        this(context, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaRouteProvider(Context context, ProviderMetadata metadata) {
        this.mHandler = new ProviderHandler();
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        this.mContext = context;
        if (metadata == null) {
            this.mMetadata = new ProviderMetadata(new ComponentName(context, getClass()));
        } else {
            this.mMetadata = metadata;
        }
    }

    void deliverDescriptorChanged() {
        this.mPendingDescriptorChange = false;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onDescriptorChanged(this, this.mDescriptor);
        }
    }

    void deliverDiscoveryRequestChanged() {
        this.mPendingDiscoveryRequestChange = false;
        onDiscoveryRequestChanged(this.mDiscoveryRequest);
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final MediaRouteProviderDescriptor getDescriptor() {
        return this.mDescriptor;
    }

    public final MediaRouteDiscoveryRequest getDiscoveryRequest() {
        return this.mDiscoveryRequest;
    }

    public final Handler getHandler() {
        return this.mHandler;
    }

    public final ProviderMetadata getMetadata() {
        return this.mMetadata;
    }

    public DynamicGroupRouteController onCreateDynamicGroupRouteController(String initialMemberRouteId) {
        if (initialMemberRouteId != null) {
            return null;
        }
        throw new IllegalArgumentException("initialMemberRouteId cannot be null.");
    }

    public RouteController onCreateRouteController(String routeId) {
        if (routeId != null) {
            return null;
        }
        throw new IllegalArgumentException("routeId cannot be null");
    }

    public RouteController onCreateRouteController(String routeId, String routeGroupId) {
        if (routeId != null) {
            if (routeGroupId != null) {
                return onCreateRouteController(routeId);
            }
            throw new IllegalArgumentException("routeGroupId cannot be null");
        }
        throw new IllegalArgumentException("routeId cannot be null");
    }

    public void onDiscoveryRequestChanged(MediaRouteDiscoveryRequest request) {
    }

    public final void setCallback(Callback callback) {
        MediaRouter.checkCallingThread();
        this.mCallback = callback;
    }

    public final void setDescriptor(MediaRouteProviderDescriptor descriptor) {
        MediaRouter.checkCallingThread();
        if (this.mDescriptor != descriptor) {
            this.mDescriptor = descriptor;
            if (this.mPendingDescriptorChange) {
                return;
            }
            this.mPendingDescriptorChange = true;
            this.mHandler.sendEmptyMessage(1);
        }
    }

    public final void setDiscoveryRequest(MediaRouteDiscoveryRequest request) {
        MediaRouter.checkCallingThread();
        if (ObjectsCompat.equals(this.mDiscoveryRequest, request)) {
            return;
        }
        setDiscoveryRequestInternal(request);
    }

    final void setDiscoveryRequestInternal(MediaRouteDiscoveryRequest request) {
        this.mDiscoveryRequest = request;
        if (this.mPendingDiscoveryRequestChange) {
            return;
        }
        this.mPendingDiscoveryRequestChange = true;
        this.mHandler.sendEmptyMessage(2);
    }
}
