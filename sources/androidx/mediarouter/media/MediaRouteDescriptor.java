package androidx.mediarouter.media;

import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/* loaded from: classes.dex */
public final class MediaRouteDescriptor {
    final Bundle mBundle;
    List<IntentFilter> mControlFilters;
    List<String> mGroupMemberIds;

    /* loaded from: classes.dex */
    public static final class Builder {
        private final Bundle mBundle;
        private ArrayList<IntentFilter> mControlFilters;
        private ArrayList<String> mGroupMemberIds;

        public Builder(MediaRouteDescriptor descriptor) {
            if (descriptor == null) {
                throw new IllegalArgumentException("descriptor must not be null");
            }
            this.mBundle = new Bundle(descriptor.mBundle);
            if (!descriptor.getGroupMemberIds().isEmpty()) {
                this.mGroupMemberIds = new ArrayList<>(descriptor.getGroupMemberIds());
            }
            if (descriptor.getControlFilters().isEmpty()) {
                return;
            }
            this.mControlFilters = new ArrayList<>(descriptor.mControlFilters);
        }

        public Builder(String id, String name) {
            this.mBundle = new Bundle();
            setId(id);
            setName(name);
        }

        public Builder addControlFilter(IntentFilter filter) {
            if (filter != null) {
                if (this.mControlFilters == null) {
                    this.mControlFilters = new ArrayList<>();
                }
                if (!this.mControlFilters.contains(filter)) {
                    this.mControlFilters.add(filter);
                }
                return this;
            }
            throw new IllegalArgumentException("filter must not be null");
        }

        public Builder addControlFilters(Collection<IntentFilter> filters) {
            if (filters != null) {
                if (!filters.isEmpty()) {
                    for (IntentFilter intentFilter : filters) {
                        if (intentFilter != null) {
                            addControlFilter(intentFilter);
                        }
                    }
                }
                return this;
            }
            throw new IllegalArgumentException("filters must not be null");
        }

        public Builder addGroupMemberId(String groupMemberId) {
            if (TextUtils.isEmpty(groupMemberId)) {
                throw new IllegalArgumentException("groupMemberId must not be empty");
            }
            if (this.mGroupMemberIds == null) {
                this.mGroupMemberIds = new ArrayList<>();
            }
            if (!this.mGroupMemberIds.contains(groupMemberId)) {
                this.mGroupMemberIds.add(groupMemberId);
            }
            return this;
        }

        public Builder addGroupMemberIds(Collection<String> groupMemberIds) {
            if (groupMemberIds != null) {
                if (!groupMemberIds.isEmpty()) {
                    Iterator<String> it = groupMemberIds.iterator();
                    while (it.hasNext()) {
                        addGroupMemberId(it.next());
                    }
                }
                return this;
            }
            throw new IllegalArgumentException("groupMemberIds must not be null");
        }

        public MediaRouteDescriptor build() {
            ArrayList<IntentFilter> arrayList = this.mControlFilters;
            if (arrayList != null) {
                this.mBundle.putParcelableArrayList("controlFilters", arrayList);
            }
            ArrayList<String> arrayList2 = this.mGroupMemberIds;
            if (arrayList2 != null) {
                this.mBundle.putStringArrayList("groupMemberIds", arrayList2);
            }
            return new MediaRouteDescriptor(this.mBundle);
        }

        public Builder setCanDisconnect(boolean canDisconnect) {
            this.mBundle.putBoolean("canDisconnect", canDisconnect);
            return this;
        }

        public Builder setConnectionState(int connectionState) {
            this.mBundle.putInt("connectionState", connectionState);
            return this;
        }

        public Builder setDescription(String description) {
            this.mBundle.putString("status", description);
            return this;
        }

        public Builder setDeviceType(int deviceType) {
            this.mBundle.putInt("deviceType", deviceType);
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.mBundle.putBoolean("enabled", enabled);
            return this;
        }

        public Builder setExtras(Bundle extras) {
            if (extras == null) {
                this.mBundle.putBundle("extras", null);
            } else {
                this.mBundle.putBundle("extras", new Bundle(extras));
            }
            return this;
        }

        public Builder setIconUri(Uri iconUri) {
            if (iconUri != null) {
                this.mBundle.putString("iconUri", iconUri.toString());
                return this;
            }
            throw new IllegalArgumentException("iconUri must not be null");
        }

        public Builder setId(String id) {
            Objects.requireNonNull(id, "id must not be null");
            this.mBundle.putString("id", id);
            return this;
        }

        public Builder setName(String name) {
            Objects.requireNonNull(name, "name must not be null");
            this.mBundle.putString("name", name);
            return this;
        }

        public Builder setPlaybackStream(int playbackStream) {
            this.mBundle.putInt("playbackStream", playbackStream);
            return this;
        }

        public Builder setPlaybackType(int playbackType) {
            this.mBundle.putInt("playbackType", playbackType);
            return this;
        }

        public Builder setPresentationDisplayId(int presentationDisplayId) {
            this.mBundle.putInt("presentationDisplayId", presentationDisplayId);
            return this;
        }

        public Builder setVolume(int volume) {
            this.mBundle.putInt("volume", volume);
            return this;
        }

        public Builder setVolumeHandling(int volumeHandling) {
            this.mBundle.putInt("volumeHandling", volumeHandling);
            return this;
        }

        public Builder setVolumeMax(int volumeMax) {
            this.mBundle.putInt("volumeMax", volumeMax);
            return this;
        }
    }

    MediaRouteDescriptor(Bundle bundle) {
        this.mBundle = bundle;
    }

    public static MediaRouteDescriptor fromBundle(Bundle bundle) {
        if (bundle != null) {
            return new MediaRouteDescriptor(bundle);
        }
        return null;
    }

    public boolean canDisconnectAndKeepPlaying() {
        return this.mBundle.getBoolean("canDisconnect", false);
    }

    void ensureControlFilters() {
        if (this.mControlFilters == null) {
            ArrayList parcelableArrayList = this.mBundle.getParcelableArrayList("controlFilters");
            this.mControlFilters = parcelableArrayList;
            if (parcelableArrayList == null) {
                this.mControlFilters = Collections.emptyList();
            }
        }
    }

    void ensureGroupMemberIds() {
        if (this.mGroupMemberIds == null) {
            ArrayList<String> stringArrayList = this.mBundle.getStringArrayList("groupMemberIds");
            this.mGroupMemberIds = stringArrayList;
            if (stringArrayList == null) {
                this.mGroupMemberIds = Collections.emptyList();
            }
        }
    }

    public int getConnectionState() {
        return this.mBundle.getInt("connectionState", 0);
    }

    public List<IntentFilter> getControlFilters() {
        ensureControlFilters();
        return this.mControlFilters;
    }

    public String getDescription() {
        return this.mBundle.getString("status");
    }

    public int getDeviceType() {
        return this.mBundle.getInt("deviceType");
    }

    public Bundle getExtras() {
        return this.mBundle.getBundle("extras");
    }

    public List<String> getGroupMemberIds() {
        ensureGroupMemberIds();
        return this.mGroupMemberIds;
    }

    public Uri getIconUri() {
        String string = this.mBundle.getString("iconUri");
        if (string == null) {
            return null;
        }
        return Uri.parse(string);
    }

    public String getId() {
        return this.mBundle.getString("id");
    }

    public int getMaxClientVersion() {
        return this.mBundle.getInt("maxClientVersion", Integer.MAX_VALUE);
    }

    public int getMinClientVersion() {
        return this.mBundle.getInt("minClientVersion", 1);
    }

    public String getName() {
        return this.mBundle.getString("name");
    }

    public int getPlaybackStream() {
        return this.mBundle.getInt("playbackStream", -1);
    }

    public int getPlaybackType() {
        return this.mBundle.getInt("playbackType", 1);
    }

    public int getPresentationDisplayId() {
        return this.mBundle.getInt("presentationDisplayId", -1);
    }

    public IntentSender getSettingsActivity() {
        return (IntentSender) this.mBundle.getParcelable("settingsIntent");
    }

    public int getVolume() {
        return this.mBundle.getInt("volume");
    }

    public int getVolumeHandling() {
        return this.mBundle.getInt("volumeHandling", 0);
    }

    public int getVolumeMax() {
        return this.mBundle.getInt("volumeMax");
    }

    public boolean isEnabled() {
        return this.mBundle.getBoolean("enabled", true);
    }

    public boolean isValid() {
        ensureControlFilters();
        return (TextUtils.isEmpty(getId()) || TextUtils.isEmpty(getName()) || this.mControlFilters.contains(null)) ? false : true;
    }

    public String toString() {
        return "MediaRouteDescriptor{ id=" + getId() + ", groupMemberIds=" + getGroupMemberIds() + ", name=" + getName() + ", description=" + getDescription() + ", iconUri=" + getIconUri() + ", isEnabled=" + isEnabled() + ", connectionState=" + getConnectionState() + ", controlFilters=" + Arrays.toString(getControlFilters().toArray()) + ", playbackType=" + getPlaybackType() + ", playbackStream=" + getPlaybackStream() + ", deviceType=" + getDeviceType() + ", volume=" + getVolume() + ", volumeMax=" + getVolumeMax() + ", volumeHandling=" + getVolumeHandling() + ", presentationDisplayId=" + getPresentationDisplayId() + ", extras=" + getExtras() + ", isValid=" + isValid() + ", minClientVersion=" + getMinClientVersion() + ", maxClientVersion=" + getMaxClientVersion() + " }";
    }
}
