package androidx.media;

import android.media.AudioAttributes;
import android.os.Build;
import android.util.SparseIntArray;
import androidx.media.AudioAttributesImpl;
import androidx.media.AudioAttributesImplApi21;
import androidx.media.AudioAttributesImplApi26;
import androidx.media.AudioAttributesImplBase;
import androidx.versionedparcelable.VersionedParcelable;

/* loaded from: classes.dex */
public class AudioAttributesCompat implements VersionedParcelable {
    private static final int[] SDK_USAGES;
    private static final SparseIntArray SUPPRESSIBLE_USAGES;
    static boolean sForceLegacyBehavior;
    public AudioAttributesImpl mImpl;

    /* loaded from: classes.dex */
    public static class Builder {
        final AudioAttributesImpl.Builder mBuilderImpl;

        public Builder() {
            if (AudioAttributesCompat.sForceLegacyBehavior) {
                this.mBuilderImpl = new AudioAttributesImplBase.Builder();
                return;
            }
            int i = Build.VERSION.SDK_INT;
            if (i >= 26) {
                this.mBuilderImpl = new AudioAttributesImplApi26.Builder();
            } else if (i >= 21) {
                this.mBuilderImpl = new AudioAttributesImplApi21.Builder();
            } else {
                this.mBuilderImpl = new AudioAttributesImplBase.Builder();
            }
        }

        public AudioAttributesCompat build() {
            return new AudioAttributesCompat(this.mBuilderImpl.build());
        }

        public Builder setLegacyStreamType(int streamType) {
            this.mBuilderImpl.setLegacyStreamType(streamType);
            return this;
        }
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        SUPPRESSIBLE_USAGES = sparseIntArray;
        sparseIntArray.put(5, 1);
        sparseIntArray.put(6, 2);
        sparseIntArray.put(7, 2);
        sparseIntArray.put(8, 1);
        sparseIntArray.put(9, 1);
        sparseIntArray.put(10, 1);
        SDK_USAGES = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16};
    }

    public AudioAttributesCompat() {
    }

    AudioAttributesCompat(AudioAttributesImpl impl) {
        this.mImpl = impl;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int toVolumeStreamType(boolean fromGetVolumeControlStream, int flags, int usage) {
        if ((flags & 1) == 1) {
            return fromGetVolumeControlStream ? 1 : 7;
        } else if ((flags & 4) == 4) {
            return fromGetVolumeControlStream ? 0 : 6;
        } else {
            switch (usage) {
                case 0:
                case 1:
                case 12:
                case 14:
                case 16:
                    return 3;
                case 2:
                    return 0;
                case 3:
                    return fromGetVolumeControlStream ? 0 : 8;
                case 4:
                    return 4;
                case 5:
                case 7:
                case 8:
                case 9:
                case 10:
                    return 5;
                case 6:
                    return 2;
                case 11:
                    return 10;
                case 13:
                    return 1;
                case 15:
                default:
                    if (fromGetVolumeControlStream) {
                        throw new IllegalArgumentException("Unknown usage value " + usage + " in audio attributes");
                    }
                    return 3;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String usageToString(int usage) {
        switch (usage) {
            case 0:
                return "USAGE_UNKNOWN";
            case 1:
                return "USAGE_MEDIA";
            case 2:
                return "USAGE_VOICE_COMMUNICATION";
            case 3:
                return "USAGE_VOICE_COMMUNICATION_SIGNALLING";
            case 4:
                return "USAGE_ALARM";
            case 5:
                return "USAGE_NOTIFICATION";
            case 6:
                return "USAGE_NOTIFICATION_RINGTONE";
            case 7:
                return "USAGE_NOTIFICATION_COMMUNICATION_REQUEST";
            case 8:
                return "USAGE_NOTIFICATION_COMMUNICATION_INSTANT";
            case 9:
                return "USAGE_NOTIFICATION_COMMUNICATION_DELAYED";
            case 10:
                return "USAGE_NOTIFICATION_EVENT";
            case 11:
                return "USAGE_ASSISTANCE_ACCESSIBILITY";
            case 12:
                return "USAGE_ASSISTANCE_NAVIGATION_GUIDANCE";
            case 13:
                return "USAGE_ASSISTANCE_SONIFICATION";
            case 14:
                return "USAGE_GAME";
            case 15:
            default:
                return "unknown usage " + usage;
            case 16:
                return "USAGE_ASSISTANT";
        }
    }

    public static AudioAttributesCompat wrap(final Object aa) {
        if (sForceLegacyBehavior) {
            return null;
        }
        int i = Build.VERSION.SDK_INT;
        if (i >= 26) {
            return new AudioAttributesCompat(new AudioAttributesImplApi26((AudioAttributes) aa));
        }
        if (i >= 21) {
            return new AudioAttributesCompat(new AudioAttributesImplApi21((AudioAttributes) aa));
        }
        return null;
    }

    public boolean equals(Object o) {
        if (o instanceof AudioAttributesCompat) {
            AudioAttributesCompat audioAttributesCompat = (AudioAttributesCompat) o;
            AudioAttributesImpl audioAttributesImpl = this.mImpl;
            return audioAttributesImpl == null ? audioAttributesCompat.mImpl == null : audioAttributesImpl.equals(audioAttributesCompat.mImpl);
        }
        return false;
    }

    public int getLegacyStreamType() {
        return this.mImpl.getLegacyStreamType();
    }

    public int hashCode() {
        return this.mImpl.hashCode();
    }

    public String toString() {
        return this.mImpl.toString();
    }
}
