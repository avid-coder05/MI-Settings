package androidx.slice;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Spanned;
import androidx.core.text.HtmlCompat;
import androidx.core.util.Pair;
import androidx.versionedparcelable.VersionedParcelable;
import java.util.ArrayList;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class SliceItemHolder implements VersionedParcelable {
    public static HolderHandler sHandler;
    public static final Object sSerializeLock = new Object();
    Bundle mBundle;
    Object mCallback;
    int mInt;
    long mLong;
    Parcelable mParcelable;
    private SliceItemPool mPool;
    String mStr;
    public VersionedParcelable mVersionedParcelable;

    /* loaded from: classes.dex */
    public interface HolderHandler {
        void handle(SliceItemHolder holder, String format);
    }

    /* loaded from: classes.dex */
    public static class SliceItemPool {
        private final ArrayList<SliceItemHolder> mCached = new ArrayList<>();

        public SliceItemHolder get() {
            if (this.mCached.size() > 0) {
                return this.mCached.remove(r1.size() - 1);
            }
            return new SliceItemHolder(this);
        }

        public void release(SliceItemHolder sliceItemHolder) {
            sliceItemHolder.mParcelable = null;
            sliceItemHolder.mCallback = null;
            sliceItemHolder.mVersionedParcelable = null;
            sliceItemHolder.mInt = 0;
            sliceItemHolder.mLong = 0L;
            sliceItemHolder.mStr = null;
            this.mCached.add(sliceItemHolder);
        }
    }

    SliceItemHolder(SliceItemPool pool) {
        this.mVersionedParcelable = null;
        this.mParcelable = null;
        this.mStr = null;
        this.mInt = 0;
        this.mLong = 0L;
        this.mBundle = null;
        this.mPool = pool;
    }

    public SliceItemHolder(String format, Object mObj, boolean isStream) {
        this.mVersionedParcelable = null;
        this.mParcelable = null;
        this.mStr = null;
        this.mInt = 0;
        this.mLong = 0L;
        this.mBundle = null;
        format.hashCode();
        char c = 65535;
        switch (format.hashCode()) {
            case -1422950858:
                if (format.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case -1377881982:
                if (format.equals("bundle")) {
                    c = 1;
                    break;
                }
                break;
            case 104431:
                if (format.equals("int")) {
                    c = 2;
                    break;
                }
                break;
            case 3327612:
                if (format.equals("long")) {
                    c = 3;
                    break;
                }
                break;
            case 3556653:
                if (format.equals("text")) {
                    c = 4;
                    break;
                }
                break;
            case 100313435:
                if (format.equals(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                    c = 5;
                    break;
                }
                break;
            case 100358090:
                if (format.equals("input")) {
                    c = 6;
                    break;
                }
                break;
            case 109526418:
                if (format.equals("slice")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                Pair pair = (Pair) mObj;
                F f = pair.first;
                if (f instanceof PendingIntent) {
                    this.mParcelable = (Parcelable) f;
                } else if (!isStream) {
                    throw new IllegalArgumentException("Cannot write callback to parcel");
                }
                this.mVersionedParcelable = (VersionedParcelable) pair.second;
                break;
            case 1:
                this.mBundle = (Bundle) mObj;
                break;
            case 2:
                this.mInt = ((Integer) mObj).intValue();
                break;
            case 3:
                this.mLong = ((Long) mObj).longValue();
                break;
            case 4:
                this.mStr = mObj instanceof Spanned ? HtmlCompat.toHtml((Spanned) mObj, 0) : (String) mObj;
                break;
            case 5:
            case 7:
                this.mVersionedParcelable = (VersionedParcelable) mObj;
                break;
            case 6:
                this.mParcelable = (Parcelable) mObj;
                break;
        }
        HolderHandler holderHandler = sHandler;
        if (holderHandler != null) {
            holderHandler.handle(this, format);
        }
    }

    public Object getObj(String format) {
        HolderHandler holderHandler = sHandler;
        if (holderHandler != null) {
            holderHandler.handle(this, format);
        }
        format.hashCode();
        char c = 65535;
        switch (format.hashCode()) {
            case -1422950858:
                if (format.equals("action")) {
                    c = 0;
                    break;
                }
                break;
            case -1377881982:
                if (format.equals("bundle")) {
                    c = 1;
                    break;
                }
                break;
            case 104431:
                if (format.equals("int")) {
                    c = 2;
                    break;
                }
                break;
            case 3327612:
                if (format.equals("long")) {
                    c = 3;
                    break;
                }
                break;
            case 3556653:
                if (format.equals("text")) {
                    c = 4;
                    break;
                }
                break;
            case 100313435:
                if (format.equals(YellowPageContract.ImageLookup.DIRECTORY_IMAGE)) {
                    c = 5;
                    break;
                }
                break;
            case 100358090:
                if (format.equals("input")) {
                    c = 6;
                    break;
                }
                break;
            case 109526418:
                if (format.equals("slice")) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                Object obj = this.mParcelable;
                if (obj == null && this.mVersionedParcelable == null) {
                    return null;
                }
                if (obj == null) {
                    obj = this.mCallback;
                }
                return new Pair(obj, (Slice) this.mVersionedParcelable);
            case 1:
                return this.mBundle;
            case 2:
                return Integer.valueOf(this.mInt);
            case 3:
                return Long.valueOf(this.mLong);
            case 4:
                String str = this.mStr;
                return (str == null || str.length() == 0) ? "" : HtmlCompat.fromHtml(this.mStr, 0);
            case 5:
            case 7:
                return this.mVersionedParcelable;
            case 6:
                return this.mParcelable;
            default:
                throw new IllegalArgumentException("Unrecognized format " + format);
        }
    }

    public void release() {
        SliceItemPool sliceItemPool = this.mPool;
        if (sliceItemPool != null) {
            sliceItemPool.release(this);
        }
    }
}
