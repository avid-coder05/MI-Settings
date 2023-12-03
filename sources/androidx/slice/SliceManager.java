package androidx.slice;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class SliceManager {
    public static SliceManager getInstance(Context context) {
        return Build.VERSION.SDK_INT >= 28 ? new SliceManagerWrapper(context) : new SliceManagerCompat(context);
    }

    public abstract List<Uri> getPinnedSlices();

    public abstract Set<SliceSpec> getPinnedSpecs(Uri uri);
}
