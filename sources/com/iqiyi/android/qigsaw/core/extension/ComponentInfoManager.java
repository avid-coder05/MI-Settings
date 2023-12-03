package com.iqiyi.android.qigsaw.core.extension;

import com.iqiyi.android.qigsaw.core.common.CompatBundle;
import com.iqiyi.android.qigsaw.core.common.ICompatBundle;
import java.lang.reflect.Field;

/* loaded from: classes2.dex */
final class ComponentInfoManager {
    private static final String ACTIVITIES_SUFFIX = "_ACTIVITIES";
    private static final String APPLICATION_SUFFIX = "_APPLICATION";
    private static final String CLASS_ComponentInfo = "com.iqiyi.android.qigsaw.core.extension.ComponentInfo";
    private static final String RECEIVERS_SUFFIX = "_RECEIVERS";
    private static final String SERVICES_SUFFIX = "_SERVICES";

    ComponentInfoManager() {
    }

    private static Class<?> getComponentInfoClass() throws ClassNotFoundException, IllegalAccessException {
        ICompatBundle iCompatBundle = CompatBundle.instance;
        if (iCompatBundle == null || !iCompatBundle.disableComponentInfoManager()) {
            return ComponentInfo.class;
        }
        throw new IllegalAccessException("disabled ComponentInfoManager");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String[] getSplitActivities(String str) {
        try {
            Field field = getComponentInfoClass().getField(str + ACTIVITIES_SUFFIX);
            field.setAccessible(true);
            String str2 = (String) field.get(null);
            if (str2 != null) {
                return str2.split(",");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | NoSuchFieldException unused) {
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getSplitApplication(String str) {
        try {
            Field field = getComponentInfoClass().getField(str + APPLICATION_SUFFIX);
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException | NoSuchFieldException unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String[] getSplitReceivers(String str) {
        try {
            Field field = getComponentInfoClass().getField(str + RECEIVERS_SUFFIX);
            field.setAccessible(true);
            String str2 = (String) field.get(null);
            if (str2 != null) {
                return str2.split(",");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | NoSuchFieldException unused) {
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String[] getSplitServices(String str) {
        try {
            Field field = getComponentInfoClass().getField(str + SERVICES_SUFFIX);
            field.setAccessible(true);
            String str2 = (String) field.get(null);
            if (str2 != null) {
                return str2.split(",");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | NoSuchFieldException unused) {
        }
        return null;
    }
}
