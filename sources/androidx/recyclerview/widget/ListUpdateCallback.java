package androidx.recyclerview.widget;

/* loaded from: classes.dex */
public interface ListUpdateCallback {
    void onChanged(int position, int count, Object payload);

    void onInserted(int position, int count);

    void onMoved(int fromPosition, int toPosition);

    void onRemoved(int position, int count);
}
