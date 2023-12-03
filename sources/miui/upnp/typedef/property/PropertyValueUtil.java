package miui.upnp.typedef.property;

import android.util.Log;

/* loaded from: classes4.dex */
public class PropertyValueUtil {
    private static final String TAG = "PropertyValueUtil";

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0017, code lost:
    
        r0 = r7;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static miui.upnp.typedef.property.PropertyValue createByType(java.lang.Class<?> r7) {
        /*
            r0 = 0
            java.lang.reflect.Constructor[] r1 = r7.getConstructors()     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            int r2 = r1.length     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            r3 = 0
            r4 = r3
        L8:
            if (r4 >= r2) goto L5d
            r5 = r1[r4]     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            java.lang.Class[] r6 = r5.getParameterTypes()     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            int r6 = r6.length     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            if (r6 != 0) goto L19
            java.lang.Object r7 = r7.newInstance()     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
        L17:
            r0 = r7
            goto L5d
        L19:
            java.lang.Class[] r5 = r5.getParameterTypes()     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            int r5 = r5.length     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            r6 = 1
            if (r5 != r6) goto L51
            java.lang.Class<java.lang.Boolean> r1 = java.lang.Boolean.class
            if (r7 != r1) goto L28
            java.lang.Boolean r7 = java.lang.Boolean.FALSE     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            goto L17
        L28:
            java.lang.Class<java.lang.Long> r1 = java.lang.Long.class
            if (r7 != r1) goto L33
            r1 = 0
            java.lang.Long r7 = java.lang.Long.valueOf(r1)     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            goto L17
        L33:
            java.lang.Class<java.lang.Integer> r1 = java.lang.Integer.class
            if (r7 != r1) goto L3c
            java.lang.Integer r7 = java.lang.Integer.valueOf(r3)     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            goto L17
        L3c:
            java.lang.Class<java.lang.Float> r1 = java.lang.Float.class
            if (r7 != r1) goto L46
            r7 = 0
            java.lang.Float r7 = java.lang.Float.valueOf(r7)     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            goto L17
        L46:
            java.lang.Class<java.lang.Double> r1 = java.lang.Double.class
            if (r7 != r1) goto L5d
            r1 = 0
            java.lang.Double r7 = java.lang.Double.valueOf(r1)     // Catch: java.lang.IllegalAccessException -> L54 java.lang.InstantiationException -> L59
            goto L17
        L51:
            int r4 = r4 + 1
            goto L8
        L54:
            r7 = move-exception
            r7.printStackTrace()
            goto L5d
        L59:
            r7 = move-exception
            r7.printStackTrace()
        L5d:
            miui.upnp.typedef.property.PropertyValue r7 = miui.upnp.typedef.property.PropertyValue.create(r0)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.upnp.typedef.property.PropertyValueUtil.createByType(java.lang.Class):miui.upnp.typedef.property.PropertyValue");
    }

    public static PropertyValue createByType(Class<?> cls, Object obj) {
        if (obj == null) {
            return createByType(cls);
        }
        if (cls.equals(obj.getClass())) {
            return PropertyValue.create(obj);
        }
        Log.e(TAG, String.format("invalid: type is %s, init value is %s (%s)", cls.getSimpleName(), obj.getClass().getSimpleName(), obj.toString()));
        return createByType(cls);
    }
}
