package androidx.slice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import java.util.Collection;

/* loaded from: classes.dex */
public abstract class SliceViewManager {

    /* loaded from: classes.dex */
    public interface SliceCallback {
        void onSliceUpdated(Slice s);
    }

    public static SliceViewManager getInstance(Context context) {
        return Build.VERSION.SDK_INT >= 28 ? new SliceViewManagerWrapper(context) : new SliceViewManagerCompat(context);
    }

    public abstract Slice bindSlice(Intent intent);

    public abstract Slice bindSlice(Uri uri);

    public abstract Collection<Uri> getSliceDescendants(Uri uri);

    public abstract void pinSlice(Uri uri);

    public abstract void registerSliceCallback(Uri uri, SliceCallback callback);

    public abstract void unpinSlice(Uri uri);

    public abstract void unregisterSliceCallback(Uri uri, SliceCallback callback);
}
