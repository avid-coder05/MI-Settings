package androidx.slice.builders;

import android.content.Context;
import android.net.Uri;
import androidx.slice.Clock;
import androidx.slice.Slice;
import androidx.slice.SliceManager;
import androidx.slice.SliceProvider;
import androidx.slice.SliceSpec;
import androidx.slice.SystemClock;
import androidx.slice.builders.impl.TemplateBuilderImpl;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class TemplateSliceBuilder {
    private final Slice.Builder mBuilder;
    private final Context mContext;
    private final TemplateBuilderImpl mImpl;
    private List<SliceSpec> mSpecs;

    public TemplateSliceBuilder(Context context, Uri uri) {
        this.mBuilder = new Slice.Builder(uri);
        this.mContext = context;
        this.mSpecs = getSpecs(uri);
        TemplateBuilderImpl selectImpl = selectImpl();
        this.mImpl = selectImpl;
        if (selectImpl == null) {
            throw new IllegalArgumentException("No valid specs found");
        }
        setImpl(selectImpl);
    }

    private List<SliceSpec> getSpecs(Uri uri) {
        return SliceProvider.getCurrentSpecs() != null ? new ArrayList(SliceProvider.getCurrentSpecs()) : new ArrayList(SliceManager.getInstance(this.mContext).getPinnedSpecs(uri));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean checkCompatible(SliceSpec candidate) {
        int size = this.mSpecs.size();
        for (int i = 0; i < size; i++) {
            if (this.mSpecs.get(i).canRender(candidate)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Slice.Builder getBuilder() {
        return this.mBuilder;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Clock getClock() {
        return SliceProvider.getClock() != null ? SliceProvider.getClock() : new SystemClock();
    }

    protected abstract TemplateBuilderImpl selectImpl();

    abstract void setImpl(TemplateBuilderImpl impl);
}
