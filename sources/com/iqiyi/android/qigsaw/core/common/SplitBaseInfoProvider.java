package com.iqiyi.android.qigsaw.core.common;

import java.lang.reflect.Field;

/* loaded from: classes2.dex */
public class SplitBaseInfoProvider {
    private static final String CLASS_QigsawConfig = ".QigsawConfig";
    private static final String DEFAULT_SPLIT_INFO_VERSION = "DEFAULT_SPLIT_INFO_VERSION";
    private static final String DEFAULT_SPLIT_INFO_VERSION_VALUE = "unknown_1.0";
    private static final String DEFAULT_VALUE = "unknown";
    private static final String DYNAMIC_FEATURES = "DYNAMIC_FEATURES";
    private static final String QIGSAW_ID = "QIGSAW_ID";
    private static final String QIGSAW_MODE = "QIGSAW_MODE";
    private static final String TAG = "SplitBaseInfoProvider";
    private static final String VERSION_NAME = "VERSION_NAME";
    private static String sPackageName;

    public static String getDefaultSplitInfoVersion() {
        try {
            Field field = getQigsawConfigClass().getField(DEFAULT_SPLIT_INFO_VERSION);
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException unused) {
            return DEFAULT_SPLIT_INFO_VERSION_VALUE;
        }
    }

    public static String[] getDynamicFeatures() {
        try {
            Field field = getQigsawConfigClass().getField(DYNAMIC_FEATURES);
            field.setAccessible(true);
            return (String[]) field.get(null);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException unused) {
            return null;
        }
    }

    private static Class<?> getQigsawConfigClass() throws ClassNotFoundException {
        try {
            ICompatBundle iCompatBundle = CompatBundle.instance;
            if (iCompatBundle != null) {
                return iCompatBundle.qigsawConfigClass();
            }
            return Class.forName(sPackageName + CLASS_QigsawConfig);
        } catch (ClassNotFoundException e) {
            SplitLog.w(TAG, "Qigsaw Warning: Can't find class " + sPackageName + ".QigsawConfig.class!", new Object[0]);
            throw e;
        }
    }

    public static String getQigsawId() {
        return "1.0";
    }

    public static String getVersionName() {
        try {
            Field field = getQigsawConfigClass().getField(VERSION_NAME);
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException unused) {
            return DEFAULT_VALUE;
        }
    }

    public static boolean isQigsawMode() {
        try {
            Field field = getQigsawConfigClass().getField(QIGSAW_MODE);
            field.setAccessible(true);
            return ((Boolean) field.get(null)).booleanValue();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException unused) {
            return false;
        }
    }

    public static void setPackageName(String str) {
        sPackageName = str;
    }
}
