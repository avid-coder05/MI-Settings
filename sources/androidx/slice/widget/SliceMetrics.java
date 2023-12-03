package androidx.slice.widget;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

/* loaded from: classes.dex */
class SliceMetrics {
    public static SliceMetrics getInstance(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= 28) {
            return new SliceMetricsWrapper(context, uri);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logHidden() {
        throw null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logTouch(int actionType, Uri subSlice) {
        throw null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logVisible() {
        throw null;
    }
}
