package com.iqiyi.android.qigsaw.core.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* loaded from: classes2.dex */
public class AbiUtil {
    private static final String TAG = "Split:AbiUtil";
    private static final String armv5 = "armeabi";
    private static final String armv7 = "armeabi-v7a";
    private static final String armv8 = "arm64-v8a";
    private static AtomicReference<String> basePrimaryAbi = new AtomicReference<>();
    private static AtomicReference<String> currentInstructionSet = new AtomicReference<>();
    private static final String x86 = "x86";
    private static final String x86_64 = "x86_64";

    private static String findBasePrimaryAbi(Collection<String> collection) throws IOException {
        List<String> supportedAbis = getSupportedAbis();
        if (collection == null || collection.isEmpty()) {
            return supportedAbis.get(0);
        }
        for (String str : supportedAbis) {
            if (collection.contains(str)) {
                return str;
            }
        }
        throw new IOException(String.format("No supported abi for this device, supported abis: %s, sorted abis: %s", supportedAbis.toString(), collection.toString()));
    }

    private static String findPrimaryAbiFromBaseApk(Context context) throws IOException {
        ZipFile zipFile;
        String str = context.getApplicationInfo().sourceDir;
        HashSet hashSet = new HashSet();
        ZipFile zipFile2 = null;
        try {
            try {
                zipFile = new ZipFile(str);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.charAt(0) >= 'l' && name.charAt(0) <= 'l' && name.startsWith("lib/") && name.endsWith(SplitConstants.DOT_SO)) {
                    String[] split = name.split("/");
                    if (split.length == 3) {
                        hashSet.add(split[1]);
                    }
                }
            }
            FileUtil.closeQuietly(zipFile);
            Set<String> sortAbis = sortAbis(hashSet);
            SplitLog.i(TAG, "sorted abis: " + sortAbis, new Object[0]);
            return findBasePrimaryAbi(sortAbis);
        } catch (IOException e2) {
            e = e2;
            throw new IOException("Failed to open base apk " + str, e);
        } catch (Throwable th2) {
            th = th2;
            zipFile2 = zipFile;
            if (zipFile2 != null) {
                FileUtil.closeQuietly(zipFile2);
            }
            throw th;
        }
    }

    private static String findPrimaryAbiFromCurrentInstructionSet(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -806050265:
                if (str.equals(x86_64)) {
                    c = 0;
                    break;
                }
                break;
            case 117110:
                if (str.equals(x86)) {
                    c = 1;
                    break;
                }
                break;
            case 93084186:
                if (str.equals("arm64")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return x86_64;
            case 1:
                return x86;
            case 2:
                return armv8;
            default:
                return null;
        }
    }

    private static String findPrimaryAbiFromProperties(Context context) throws IOException {
        try {
            InputStream open = context.getAssets().open("base.app.cpu.abilist.properties");
            Properties properties = new Properties();
            properties.load(open);
            open.close();
            String property = properties.getProperty("abiList");
            if (TextUtils.isEmpty(property)) {
                return null;
            }
            String[] split = property.split(",");
            HashSet hashSet = new HashSet();
            Collections.addAll(hashSet, split);
            if (hashSet.isEmpty()) {
                return null;
            }
            Set<String> sortAbis = sortAbis(hashSet);
            SplitLog.i(TAG, "sorted abis: " + sortAbis, new Object[0]);
            return findBasePrimaryAbi(sortAbis);
        } catch (IOException e) {
            throw new IOException("Failed to read asset file 'assets/base.app.cpu.abilist.properties'!", e);
        }
    }

    public static String findSplitPrimaryAbi(String str, List<String> list) {
        if (list.contains(str)) {
            return str;
        }
        if (str.equals(armv8)) {
            if (list.contains(armv8)) {
                return armv8;
            }
            return null;
        } else if (str.equals(x86_64)) {
            if (list.contains(x86_64)) {
                return x86_64;
            }
            return null;
        } else {
            if (str.equals(x86)) {
                if (list.contains(x86)) {
                    return x86;
                }
                if (list.contains(armv5)) {
                    return armv5;
                }
            } else if (str.equals(armv7)) {
                if (list.contains(armv7)) {
                    return armv7;
                }
                if (list.contains(armv5)) {
                    return armv5;
                }
            } else if (str.equals(armv5)) {
                if (list.contains(armv5)) {
                    return armv5;
                }
                if (getSupportedAbis().contains(armv7) && list.contains(armv7)) {
                    return armv7;
                }
            }
            return null;
        }
    }

    public static String getBasePrimaryAbi(Context context) throws IOException {
        String str;
        if (TextUtils.isEmpty(basePrimaryAbi.get())) {
            synchronized (AbiUtil.class) {
                ApplicationInfo applicationInfo = context.getApplicationInfo();
                try {
                    Field field = ApplicationInfo.class.getField("primaryCpuAbi");
                    field.setAccessible(true);
                    basePrimaryAbi.compareAndSet(null, (String) field.get(applicationInfo));
                    SplitLog.i(TAG, "Succeed to get primaryCpuAbi %s from ApplicationInfo.", basePrimaryAbi);
                } catch (Throwable th) {
                    SplitLog.w(TAG, "Failed to get primaryCpuAbi from ApplicationInfo.", th);
                }
                if (TextUtils.isEmpty(basePrimaryAbi.get())) {
                    basePrimaryAbi.compareAndSet(null, findPrimaryAbiFromCurrentInstructionSet(getCurrentInstructionSet()));
                    if (TextUtils.isEmpty(basePrimaryAbi.get())) {
                        SplitLog.w(TAG, "Failed to get primaryCpuAbi from CurrentInstructionSet.", new Object[0]);
                        basePrimaryAbi.compareAndSet(null, findPrimaryAbiFromProperties(context));
                        if (TextUtils.isEmpty(basePrimaryAbi.get())) {
                            SplitLog.i(TAG, "Failed to get primaryCpuAbi from Properties.", new Object[0]);
                            basePrimaryAbi.compareAndSet(null, findPrimaryAbiFromBaseApk(context));
                            SplitLog.i(TAG, "Succeed to get primaryCpuAbi %s from BaseApk.", basePrimaryAbi);
                        } else {
                            SplitLog.i(TAG, "Succeed to get primaryCpuAbi %s from Properties.", basePrimaryAbi);
                        }
                    } else {
                        SplitLog.i(TAG, "Succeed to get primaryCpuAbi %s from CurrentInstructionSet.", basePrimaryAbi);
                    }
                }
                str = basePrimaryAbi.get();
            }
            return str;
        }
        return basePrimaryAbi.get();
    }

    @SuppressLint({"DiscouragedPrivateApi"})
    private static String getCurrentInstructionSet() {
        if (TextUtils.isEmpty(currentInstructionSet.get())) {
            try {
                Method declaredMethod = Class.forName("dalvik.system.VMRuntime").getDeclaredMethod("getCurrentInstructionSet", new Class[0]);
                declaredMethod.setAccessible(true);
                currentInstructionSet.compareAndSet(null, (String) declaredMethod.invoke(null, new Object[0]));
            } catch (Throwable unused) {
            }
            return currentInstructionSet.get();
        }
        return currentInstructionSet.get();
    }

    private static List<String> getSupportedAbis() {
        return Build.VERSION.SDK_INT >= 21 ? Arrays.asList(Build.SUPPORTED_ABIS) : Arrays.asList(Build.CPU_ABI, Build.CPU_ABI2);
    }

    public static boolean isArm64(Context context) {
        try {
            String basePrimaryAbi2 = getBasePrimaryAbi(context);
            return !TextUtils.isEmpty(basePrimaryAbi2) && basePrimaryAbi2.equals(armv8);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Set<String> sortAbis(Set<String> set) {
        if (set.isEmpty() || set.size() == 1) {
            return set;
        }
        HashSet hashSet = new HashSet(set.size());
        if (set.contains(armv8)) {
            hashSet.add(armv8);
        }
        if (set.contains(armv7)) {
            hashSet.add(armv7);
        }
        if (set.contains(armv5)) {
            hashSet.add(armv5);
        }
        if (set.contains(x86)) {
            hashSet.add(x86);
        }
        if (set.contains(x86_64)) {
            hashSet.add(x86_64);
        }
        return hashSet;
    }
}
