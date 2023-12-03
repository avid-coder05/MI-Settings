package com.android.settings.privacy;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;

/* loaded from: classes2.dex */
public class DataTaskLoader<D> extends AsyncTaskLoader<D> {
    private D mData;

    public DataTaskLoader(Context context) {
        super(context);
    }

    @Override // androidx.loader.content.Loader
    public void deliverResult(D d) {
        if (!isReset() && isStarted()) {
            super.deliverResult(d);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.loader.content.AsyncTaskLoader
    public D onLoadInBackground() {
        D d = (D) super.onLoadInBackground();
        this.mData = d;
        return d;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onStartLoading() {
        D d = this.mData;
        if (d != null) {
            deliverResult(d);
        }
        if (takeContentChanged() || this.mData == null) {
            forceLoad();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onStopLoading() {
        cancelLoad();
    }
}
