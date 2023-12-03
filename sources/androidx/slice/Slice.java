package androidx.slice;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Preconditions;
import androidx.slice.compat.SliceProviderCompat;
import androidx.versionedparcelable.CustomVersionedParcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public final class Slice extends CustomVersionedParcelable {
    static final String[] NO_HINTS = new String[0];
    static final SliceItem[] NO_ITEMS = new SliceItem[0];
    String[] mHints;
    SliceItem[] mItems;
    SliceSpec mSpec;
    String mUri;

    /* loaded from: classes.dex */
    public static class Builder {
        private int mChildId;
        private SliceSpec mSpec;
        private final Uri mUri;
        private ArrayList<SliceItem> mItems = new ArrayList<>();
        private ArrayList<String> mHints = new ArrayList<>();

        public Builder(Uri uri) {
            this.mUri = uri;
        }

        public Builder(Builder parent) {
            this.mUri = parent.getChildUri();
        }

        private Uri getChildUri() {
            Uri.Builder appendPath = this.mUri.buildUpon().appendPath("_gen");
            int i = this.mChildId;
            this.mChildId = i + 1;
            return appendPath.appendPath(String.valueOf(i)).build();
        }

        public Builder addAction(PendingIntent action, Slice s, String subType) {
            Preconditions.checkNotNull(action);
            Preconditions.checkNotNull(s);
            this.mItems.add(new SliceItem(action, s, "action", subType, s.getHintArray()));
            return this;
        }

        public Builder addHints(List<String> hints) {
            return addHints((String[]) hints.toArray(new String[hints.size()]));
        }

        public Builder addHints(String... hints) {
            this.mHints.addAll(Arrays.asList(hints));
            return this;
        }

        public Builder addIcon(IconCompat icon, String subType, List<String> hints) {
            Preconditions.checkNotNull(icon);
            return Slice.isValidIcon(icon) ? addIcon(icon, subType, (String[]) hints.toArray(new String[hints.size()])) : this;
        }

        public Builder addIcon(IconCompat icon, String subType, String... hints) {
            Preconditions.checkNotNull(icon);
            if (Slice.isValidIcon(icon)) {
                this.mItems.add(new SliceItem(icon, YellowPageContract.ImageLookup.DIRECTORY_IMAGE, subType, hints));
            }
            return this;
        }

        public Builder addInt(int value, String subType, List<String> hints) {
            return addInt(value, subType, (String[]) hints.toArray(new String[hints.size()]));
        }

        public Builder addInt(int value, String subType, String... hints) {
            this.mItems.add(new SliceItem(Integer.valueOf(value), "int", subType, hints));
            return this;
        }

        public Builder addItem(SliceItem item) {
            this.mItems.add(item);
            return this;
        }

        public Builder addLong(long time, String subType, List<String> hints) {
            return addLong(time, subType, (String[]) hints.toArray(new String[hints.size()]));
        }

        public Builder addLong(long time, String subType, String... hints) {
            this.mItems.add(new SliceItem(Long.valueOf(time), "long", subType, hints));
            return this;
        }

        public Builder addRemoteInput(RemoteInput remoteInput, String subType, List<String> hints) {
            Preconditions.checkNotNull(remoteInput);
            return addRemoteInput(remoteInput, subType, (String[]) hints.toArray(new String[hints.size()]));
        }

        public Builder addRemoteInput(RemoteInput remoteInput, String subType, String... hints) {
            Preconditions.checkNotNull(remoteInput);
            this.mItems.add(new SliceItem(remoteInput, "input", subType, hints));
            return this;
        }

        public Builder addSubSlice(Slice slice) {
            Preconditions.checkNotNull(slice);
            return addSubSlice(slice, null);
        }

        public Builder addSubSlice(Slice slice, String subType) {
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem(slice, "slice", subType, slice.getHintArray()));
            return this;
        }

        public Builder addText(CharSequence text, String subType, List<String> hints) {
            return addText(text, subType, (String[]) hints.toArray(new String[hints.size()]));
        }

        public Builder addText(CharSequence text, String subType, String... hints) {
            this.mItems.add(new SliceItem(text, "text", subType, hints));
            return this;
        }

        @Deprecated
        public Builder addTimestamp(long time, String subType, String... hints) {
            this.mItems.add(new SliceItem(Long.valueOf(time), "long", subType, hints));
            return this;
        }

        public Slice build() {
            ArrayList<SliceItem> arrayList = this.mItems;
            ArrayList<String> arrayList2 = this.mHints;
            return new Slice(arrayList, (String[]) arrayList2.toArray(new String[arrayList2.size()]), this.mUri, this.mSpec);
        }

        public Builder setSpec(SliceSpec spec) {
            this.mSpec = spec;
            return this;
        }
    }

    public Slice() {
        this.mSpec = null;
        this.mItems = NO_ITEMS;
        this.mHints = NO_HINTS;
        this.mUri = null;
    }

    public Slice(Bundle in) {
        this.mSpec = null;
        this.mItems = NO_ITEMS;
        this.mHints = NO_HINTS;
        this.mUri = null;
        this.mHints = in.getStringArray("hints");
        Parcelable[] parcelableArray = in.getParcelableArray("items");
        this.mItems = new SliceItem[parcelableArray.length];
        int i = 0;
        while (true) {
            SliceItem[] sliceItemArr = this.mItems;
            if (i >= sliceItemArr.length) {
                break;
            }
            if (parcelableArray[i] instanceof Bundle) {
                sliceItemArr[i] = new SliceItem((Bundle) parcelableArray[i]);
            }
            i++;
        }
        this.mUri = in.getParcelable("uri").toString();
        this.mSpec = in.containsKey("type") ? new SliceSpec(in.getString("type"), in.getInt("revision")) : null;
    }

    Slice(ArrayList<SliceItem> items, String[] hints, Uri uri, SliceSpec spec) {
        this.mSpec = null;
        this.mItems = NO_ITEMS;
        this.mHints = NO_HINTS;
        this.mUri = null;
        this.mHints = hints;
        this.mItems = (SliceItem[]) items.toArray(new SliceItem[items.size()]);
        this.mUri = uri.toString();
        this.mSpec = spec;
    }

    public static void appendHints(StringBuilder sb, String[] hints) {
        if (hints == null || hints.length == 0) {
            return;
        }
        sb.append('(');
        int length = hints.length - 1;
        for (int i = 0; i < length; i++) {
            sb.append(hints[i]);
            sb.append(", ");
        }
        sb.append(hints[length]);
        sb.append(")");
    }

    public static Slice bindSlice(Context context, Uri uri, Set<SliceSpec> supportedSpecs) {
        return Build.VERSION.SDK_INT >= 28 ? callBindSlice(context, uri, supportedSpecs) : SliceProviderCompat.bindSlice(context, uri, supportedSpecs);
    }

    private static Slice callBindSlice(Context context, Uri uri, Set<SliceSpec> supportedSpecs) {
        return SliceConvert.wrap(((android.app.slice.SliceManager) context.getSystemService(android.app.slice.SliceManager.class)).bindSlice(uri, SliceConvert.unwrap(supportedSpecs)), context);
    }

    static boolean isValidIcon(IconCompat icon) {
        if (icon == null) {
            return false;
        }
        if (icon.mType == 2 && icon.getResId() == 0) {
            throw new IllegalArgumentException("Failed to add icon, invalid resource id: " + icon.getResId());
        }
        return true;
    }

    public String[] getHintArray() {
        return this.mHints;
    }

    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }

    public SliceItem[] getItemArray() {
        return this.mItems;
    }

    public List<SliceItem> getItems() {
        return Arrays.asList(this.mItems);
    }

    public SliceSpec getSpec() {
        return this.mSpec;
    }

    public Uri getUri() {
        return Uri.parse(this.mUri);
    }

    public boolean hasHint(String hint) {
        return ArrayUtils.contains(this.mHints, hint);
    }

    public void onPostParceling() {
        for (int length = this.mItems.length - 1; length >= 0; length--) {
            SliceItem[] sliceItemArr = this.mItems;
            if (sliceItemArr[length].mObj == null) {
                SliceItem[] sliceItemArr2 = (SliceItem[]) ArrayUtils.removeElement(SliceItem.class, sliceItemArr, sliceItemArr[length]);
                this.mItems = sliceItemArr2;
                if (sliceItemArr2 == null) {
                    this.mItems = new SliceItem[0];
                }
            }
        }
    }

    public void onPreParceling(boolean isStream) {
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putStringArray("hints", this.mHints);
        Parcelable[] parcelableArr = new Parcelable[this.mItems.length];
        int i = 0;
        while (true) {
            SliceItem[] sliceItemArr = this.mItems;
            if (i >= sliceItemArr.length) {
                break;
            }
            parcelableArr[i] = sliceItemArr[i].toBundle();
            i++;
        }
        bundle.putParcelableArray("items", parcelableArr);
        bundle.putParcelable("uri", Uri.parse(this.mUri));
        SliceSpec sliceSpec = this.mSpec;
        if (sliceSpec != null) {
            bundle.putString("type", sliceSpec.getType());
            bundle.putInt("revision", this.mSpec.getRevision());
        }
        return bundle;
    }

    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent);
        sb.append("Slice ");
        String[] strArr = this.mHints;
        if (strArr.length > 0) {
            appendHints(sb, strArr);
            sb.append(' ');
        }
        sb.append('[');
        sb.append(this.mUri);
        sb.append("] {\n");
        String str = indent + "  ";
        int i = 0;
        while (true) {
            SliceItem[] sliceItemArr = this.mItems;
            if (i >= sliceItemArr.length) {
                sb.append(indent);
                sb.append('}');
                return sb.toString();
            }
            sb.append(sliceItemArr[i].toString(str));
            i++;
        }
    }
}
