package com.iqiyi.android.qigsaw.core.splitload;

/* loaded from: classes2.dex */
public class SplitLibraryLoaderHelper {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    /* JADX WARN: Code restructure failed: missing block: B:45:0x0023, code lost:
    
        continue;
     */
    @android.annotation.SuppressLint({"UnsafeDynamicallyLoadedCode"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean loadSplitLibrary(android.content.Context r9, java.lang.String r10) {
        /*
            boolean r0 = com.iqiyi.android.qigsaw.core.splitload.SplitLoadManagerService.hasInstance()
            r1 = 0
            if (r0 != 0) goto L8
            return r1
        L8:
            com.iqiyi.android.qigsaw.core.splitload.SplitLoadManager r0 = com.iqiyi.android.qigsaw.core.splitload.SplitLoadManagerService.getInstance()
            int r0 = r0.splitLoadMode()
            r2 = 1
            if (r0 == r2) goto L14
            return r1
        L14:
            com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager r0 = com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService.getInstance()
            java.util.Collection r0 = r0.getAllSplitInfo(r9)
            if (r0 != 0) goto L1f
            return r1
        L1f:
            java.util.Iterator r0 = r0.iterator()
        L23:
            boolean r3 = r0.hasNext()
            if (r3 == 0) goto La0
            java.lang.Object r3 = r0.next()
            com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo r3 = (com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo) r3
            com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo$LibData r4 = r3.getPrimaryLibData(r9)     // Catch: java.io.IOException -> L23
            if (r4 != 0) goto L36
            goto L23
        L36:
            java.util.List r5 = r4.getLibs()     // Catch: java.io.IOException -> L23
            java.util.Iterator r5 = r5.iterator()     // Catch: java.io.IOException -> L23
        L3e:
            boolean r6 = r5.hasNext()     // Catch: java.io.IOException -> L23
            if (r6 == 0) goto L23
            java.lang.Object r6 = r5.next()     // Catch: java.io.IOException -> L23
            com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo$LibData$Lib r6 = (com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo.LibData.Lib) r6     // Catch: java.io.IOException -> L23
            java.lang.String r7 = r6.getName()     // Catch: java.io.IOException -> L23
            java.lang.String r8 = java.lang.System.mapLibraryName(r10)     // Catch: java.io.IOException -> L23
            boolean r7 = r7.equals(r8)     // Catch: java.io.IOException -> L23
            if (r7 == 0) goto L3e
            boolean r5 = r9 instanceof android.app.Application     // Catch: java.io.IOException -> L23
            if (r5 == 0) goto L89
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.io.IOException -> L23
            r5.<init>()     // Catch: java.io.IOException -> L23
            com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager r7 = com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitPathManager.require()     // Catch: java.io.IOException -> L23
            java.lang.String r4 = r4.getAbi()     // Catch: java.io.IOException -> L23
            java.io.File r3 = r7.getSplitLibDir(r3, r4)     // Catch: java.io.IOException -> L23
            java.lang.String r3 = r3.getAbsolutePath()     // Catch: java.io.IOException -> L23
            r5.append(r3)     // Catch: java.io.IOException -> L23
            java.lang.String r3 = java.io.File.separator     // Catch: java.io.IOException -> L23
            r5.append(r3)     // Catch: java.io.IOException -> L23
            java.lang.String r3 = r6.getName()     // Catch: java.io.IOException -> L23
            r5.append(r3)     // Catch: java.io.IOException -> L23
            java.lang.String r3 = r5.toString()     // Catch: java.io.IOException -> L23
            java.lang.System.load(r3)     // Catch: java.io.IOException -> L23 java.lang.UnsatisfiedLinkError -> L88
            return r2
        L88:
            return r1
        L89:
            com.iqiyi.android.qigsaw.core.splitload.SplitApplicationLoaders r4 = com.iqiyi.android.qigsaw.core.splitload.SplitApplicationLoaders.getInstance()     // Catch: java.io.IOException -> L23
            java.lang.String r5 = r3.getSplitName()     // Catch: java.io.IOException -> L23
            com.iqiyi.android.qigsaw.core.splitload.SplitDexClassLoader r4 = r4.getValidClassLoader(r5)     // Catch: java.io.IOException -> L23
            if (r4 == 0) goto L23
            java.lang.String r3 = r3.getSplitName()     // Catch: java.io.IOException -> L23
            boolean r9 = loadSplitLibrary0(r4, r3, r10)     // Catch: java.io.IOException -> L23
            return r9
        La0:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.iqiyi.android.qigsaw.core.splitload.SplitLibraryLoaderHelper.loadSplitLibrary(android.content.Context, java.lang.String):boolean");
    }

    private static boolean loadSplitLibrary0(ClassLoader classLoader, String str, String str2) {
        try {
            Class<?> loadClass = classLoader.loadClass("com.iqiyi.android.qigsaw.core.splitlib." + str + "SplitLibraryLoader");
            HiddenApiReflection.findMethod(loadClass, "loadSplitLibrary", (Class<?>[]) new Class[]{String.class}).invoke(loadClass.newInstance(), str2);
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }
}
