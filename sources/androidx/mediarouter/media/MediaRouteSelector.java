package androidx.mediarouter.media;

import android.content.IntentFilter;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class MediaRouteSelector {
    public static final MediaRouteSelector EMPTY = new MediaRouteSelector(new Bundle(), null);
    private final Bundle mBundle;
    List<String> mControlCategories;

    /* loaded from: classes.dex */
    public static final class Builder {
        private ArrayList<String> mControlCategories;

        public Builder() {
        }

        public Builder(MediaRouteSelector selector) {
            if (selector == null) {
                throw new IllegalArgumentException("selector must not be null");
            }
            selector.ensureControlCategories();
            if (selector.mControlCategories.isEmpty()) {
                return;
            }
            this.mControlCategories = new ArrayList<>(selector.mControlCategories);
        }

        public Builder addControlCategories(Collection<String> categories) {
            if (categories != null) {
                if (!categories.isEmpty()) {
                    Iterator<String> it = categories.iterator();
                    while (it.hasNext()) {
                        addControlCategory(it.next());
                    }
                }
                return this;
            }
            throw new IllegalArgumentException("categories must not be null");
        }

        public Builder addControlCategory(String category) {
            if (category != null) {
                if (this.mControlCategories == null) {
                    this.mControlCategories = new ArrayList<>();
                }
                if (!this.mControlCategories.contains(category)) {
                    this.mControlCategories.add(category);
                }
                return this;
            }
            throw new IllegalArgumentException("category must not be null");
        }

        public Builder addSelector(MediaRouteSelector selector) {
            if (selector != null) {
                addControlCategories(selector.getControlCategories());
                return this;
            }
            throw new IllegalArgumentException("selector must not be null");
        }

        public MediaRouteSelector build() {
            if (this.mControlCategories == null) {
                return MediaRouteSelector.EMPTY;
            }
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("controlCategories", this.mControlCategories);
            return new MediaRouteSelector(bundle, this.mControlCategories);
        }
    }

    MediaRouteSelector(Bundle bundle, List<String> controlCategories) {
        this.mBundle = bundle;
        this.mControlCategories = controlCategories;
    }

    public static MediaRouteSelector fromBundle(Bundle bundle) {
        if (bundle != null) {
            return new MediaRouteSelector(bundle, null);
        }
        return null;
    }

    public Bundle asBundle() {
        return this.mBundle;
    }

    public boolean contains(MediaRouteSelector selector) {
        if (selector == null) {
            return false;
        }
        ensureControlCategories();
        selector.ensureControlCategories();
        return this.mControlCategories.containsAll(selector.mControlCategories);
    }

    void ensureControlCategories() {
        if (this.mControlCategories == null) {
            ArrayList<String> stringArrayList = this.mBundle.getStringArrayList("controlCategories");
            this.mControlCategories = stringArrayList;
            if (stringArrayList == null || stringArrayList.isEmpty()) {
                this.mControlCategories = Collections.emptyList();
            }
        }
    }

    public boolean equals(Object o) {
        if (o instanceof MediaRouteSelector) {
            MediaRouteSelector mediaRouteSelector = (MediaRouteSelector) o;
            ensureControlCategories();
            mediaRouteSelector.ensureControlCategories();
            return this.mControlCategories.equals(mediaRouteSelector.mControlCategories);
        }
        return false;
    }

    public List<String> getControlCategories() {
        ensureControlCategories();
        return this.mControlCategories;
    }

    public int hashCode() {
        ensureControlCategories();
        return this.mControlCategories.hashCode();
    }

    public boolean isEmpty() {
        ensureControlCategories();
        return this.mControlCategories.isEmpty();
    }

    public boolean isValid() {
        ensureControlCategories();
        return !this.mControlCategories.contains(null);
    }

    public boolean matchesControlFilters(List<IntentFilter> filters) {
        if (filters == null) {
            return false;
        }
        ensureControlCategories();
        if (this.mControlCategories.isEmpty()) {
            return false;
        }
        for (IntentFilter intentFilter : filters) {
            if (intentFilter != null) {
                Iterator<String> it = this.mControlCategories.iterator();
                while (it.hasNext()) {
                    if (intentFilter.hasCategory(it.next())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String toString() {
        return "MediaRouteSelector{ controlCategories=" + Arrays.toString(getControlCategories().toArray()) + " }";
    }
}
