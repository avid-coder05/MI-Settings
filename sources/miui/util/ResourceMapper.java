package miui.util;

import android.content.res.Resources;
import android.util.TypedValue;

/* loaded from: classes4.dex */
public class ResourceMapper {
    public static int resolveReference(Resources resources, int i) {
        TypedValue typedValue = new TypedValue();
        resources.getValue(i, typedValue, true);
        int i2 = typedValue.resourceId;
        return i2 == 0 ? i : i2;
    }
}
