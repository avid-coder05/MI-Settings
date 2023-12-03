package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import miui.reflect.Method;

/* loaded from: classes.dex */
public class MiuiListView extends ListView {
    public MiuiListView(Context context) {
        super(context);
    }

    public MiuiListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MiuiListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private View superObtainView(int i, boolean[] zArr) {
        return (View) Method.of(ListView.class, "obtainView", View.class, new Class[]{Integer.TYPE, boolean[].class}).invokeObject(ListView.class, this, new Object[]{Integer.valueOf(i), zArr});
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View obtainView(int i, boolean[] zArr) {
        return superObtainView(i, zArr);
    }
}
