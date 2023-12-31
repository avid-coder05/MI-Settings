package androidx.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: classes.dex */
abstract class IndexBasedArrayIterator<T> implements Iterator<T> {
    private boolean mCanRemove;
    private int mIndex;
    private int mSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IndexBasedArrayIterator(int i) {
        this.mSize = i;
    }

    protected abstract T elementAt(int i);

    @Override // java.util.Iterator
    public final boolean hasNext() {
        return this.mIndex < this.mSize;
    }

    @Override // java.util.Iterator
    public T next() {
        if (hasNext()) {
            T elementAt = elementAt(this.mIndex);
            this.mIndex++;
            this.mCanRemove = true;
            return elementAt;
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.Iterator
    public void remove() {
        if (!this.mCanRemove) {
            throw new IllegalStateException();
        }
        int i = this.mIndex - 1;
        this.mIndex = i;
        removeAt(i);
        this.mSize--;
        this.mCanRemove = false;
    }

    protected abstract void removeAt(int i);
}
