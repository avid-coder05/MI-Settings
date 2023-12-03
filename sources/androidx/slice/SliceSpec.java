package androidx.slice;

import androidx.versionedparcelable.VersionedParcelable;

/* loaded from: classes.dex */
public final class SliceSpec implements VersionedParcelable {
    int mRevision;
    String mType;

    public SliceSpec() {
        this.mRevision = 1;
    }

    public SliceSpec(String type, int revision) {
        this.mRevision = 1;
        this.mType = type;
        this.mRevision = revision;
    }

    public boolean canRender(SliceSpec candidate) {
        return this.mType.equals(candidate.mType) && this.mRevision >= candidate.mRevision;
    }

    public boolean equals(Object obj) {
        if (obj instanceof SliceSpec) {
            SliceSpec sliceSpec = (SliceSpec) obj;
            return this.mType.equals(sliceSpec.mType) && this.mRevision == sliceSpec.mRevision;
        }
        return false;
    }

    public int getRevision() {
        return this.mRevision;
    }

    public String getType() {
        return this.mType;
    }

    public int hashCode() {
        return this.mType.hashCode() + this.mRevision;
    }

    public String toString() {
        return String.format("SliceSpec{%s,%d}", this.mType, Integer.valueOf(this.mRevision));
    }
}
