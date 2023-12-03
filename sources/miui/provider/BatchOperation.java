package miui.provider;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes3.dex */
public final class BatchOperation {
    public static final int BATCH_EXECUTE_SIZE = 100;
    private final String mAuthority;
    private final ArrayList<ContentProviderOperation> mOperations = new ArrayList<>();
    private final ContentResolver mResolver;

    public BatchOperation(ContentResolver contentResolver, String str) {
        this.mResolver = contentResolver;
        this.mAuthority = str;
    }

    public void add(ContentProviderOperation contentProviderOperation) {
        this.mOperations.add(contentProviderOperation);
    }

    public Uri execute() {
        Uri uri = null;
        if (this.mOperations.size() == 0) {
            return null;
        }
        try {
            ContentProviderResult[] applyBatch = this.mResolver.applyBatch(this.mAuthority, this.mOperations);
            if (applyBatch != null && applyBatch.length > 0) {
                uri = applyBatch[0].uri;
            }
        } catch (OperationApplicationException e) {
            Log.e("BatchOperation", "storing contact data failed", e);
        } catch (RemoteException e2) {
            Log.e("BatchOperation", "storing contact data failed", e2);
        }
        this.mOperations.clear();
        return uri;
    }

    public ContentResolver getContentResolver() {
        return this.mResolver;
    }

    public int size() {
        return this.mOperations.size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<ContentProviderOperation> it = this.mOperations.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString() + "\n");
        }
        return sb.toString();
    }
}
