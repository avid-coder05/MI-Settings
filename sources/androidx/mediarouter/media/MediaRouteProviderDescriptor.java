package androidx.mediarouter.media;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class MediaRouteProviderDescriptor {
    final List<MediaRouteDescriptor> mRoutes;
    final boolean mSupportsDynamicGroupRoute;

    /* loaded from: classes.dex */
    public static final class Builder {
        private List<MediaRouteDescriptor> mRoutes;
        private boolean mSupportsDynamicGroupRoute = false;

        public Builder addRoute(MediaRouteDescriptor route) {
            if (route != null) {
                List<MediaRouteDescriptor> list = this.mRoutes;
                if (list == null) {
                    this.mRoutes = new ArrayList();
                } else if (list.contains(route)) {
                    throw new IllegalArgumentException("route descriptor already added");
                }
                this.mRoutes.add(route);
                return this;
            }
            throw new IllegalArgumentException("route must not be null");
        }

        public Builder addRoutes(Collection<MediaRouteDescriptor> routes) {
            if (routes != null) {
                if (!routes.isEmpty()) {
                    Iterator<MediaRouteDescriptor> it = routes.iterator();
                    while (it.hasNext()) {
                        addRoute(it.next());
                    }
                }
                return this;
            }
            throw new IllegalArgumentException("routes must not be null");
        }

        public MediaRouteProviderDescriptor build() {
            return new MediaRouteProviderDescriptor(this.mRoutes, this.mSupportsDynamicGroupRoute);
        }

        public Builder setSupportsDynamicGroupRoute(boolean value) {
            this.mSupportsDynamicGroupRoute = value;
            return this;
        }
    }

    MediaRouteProviderDescriptor(List<MediaRouteDescriptor> routes, boolean supportsDynamicGroupRoute) {
        this.mRoutes = routes == null ? Collections.emptyList() : routes;
        this.mSupportsDynamicGroupRoute = supportsDynamicGroupRoute;
    }

    public static MediaRouteProviderDescriptor fromBundle(Bundle bundle) {
        ArrayList arrayList = null;
        if (bundle == null) {
            return null;
        }
        ArrayList parcelableArrayList = bundle.getParcelableArrayList("routes");
        if (parcelableArrayList != null && !parcelableArrayList.isEmpty()) {
            int size = parcelableArrayList.size();
            ArrayList arrayList2 = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                arrayList2.add(MediaRouteDescriptor.fromBundle((Bundle) parcelableArrayList.get(i)));
            }
            arrayList = arrayList2;
        }
        return new MediaRouteProviderDescriptor(arrayList, bundle.getBoolean("supportsDynamicGroupRoute", false));
    }

    public List<MediaRouteDescriptor> getRoutes() {
        return this.mRoutes;
    }

    public boolean isValid() {
        int size = getRoutes().size();
        for (int i = 0; i < size; i++) {
            MediaRouteDescriptor mediaRouteDescriptor = this.mRoutes.get(i);
            if (mediaRouteDescriptor == null || !mediaRouteDescriptor.isValid()) {
                return false;
            }
        }
        return true;
    }

    public boolean supportsDynamicGroupRoute() {
        return this.mSupportsDynamicGroupRoute;
    }

    public String toString() {
        return "MediaRouteProviderDescriptor{ routes=" + Arrays.toString(getRoutes().toArray()) + ", isValid=" + isValid() + " }";
    }
}
