package com.android.settings.recommend;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import com.android.settings.recommend.bean.RecommendItem;
import com.android.settings.recommend.bean.RecommendPage;
import com.android.settings.recommend.bean.RecommendResult;
import com.android.settingslib.search.SearchUtils;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import miuix.core.util.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class RecommendManager {
    private static final String BUILD_UTC = "ro.build.date.utc";
    private static volatile RecommendManager INSTANCE = null;
    private static final String RECOMMEND_JSON_NAME = "recommend.json";
    public static final String TAG = "RecommendHelper";
    private Context mContext;
    private static AtomicBoolean mIsLoadComplete = new AtomicBoolean(false);
    private static Map<Integer, List<RecommendItem>> mCacheRecommendList = new HashMap();

    private RecommendManager(Context context) {
        this.mContext = context;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r9v0, types: [com.android.settings.recommend.RecommendManager] */
    /* JADX WARN: Type inference failed for: r9v1 */
    /* JADX WARN: Type inference failed for: r9v10, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r9v2 */
    /* JADX WARN: Type inference failed for: r9v3 */
    /* JADX WARN: Type inference failed for: r9v4, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r9v5 */
    /* JADX WARN: Type inference failed for: r9v6, types: [java.io.InputStream] */
    private void copyRecommendJson(File file) {
        FileInputStream fileInputStream;
        JSONObject readJSONObject;
        FileOutputStream fileOutputStream;
        Log.d(TAG, "copyRecommendJson");
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                this = this.mContext.getResources().getAssets().open(RECOMMEND_JSON_NAME);
                try {
                    FileUtils.copyToFile(this, file);
                    fileInputStream = new FileInputStream(file);
                    try {
                        readJSONObject = SearchUtils.readJSONObject(fileInputStream);
                        readJSONObject.put(BUILD_UTC, SystemProperties.get(BUILD_UTC));
                        fileOutputStream = new FileOutputStream(file);
                    } catch (IOException | JSONException unused) {
                    }
                    try {
                        fileOutputStream.write(readJSONObject.toString().getBytes());
                        fileOutputStream.flush();
                        try {
                            fileOutputStream.close();
                        } catch (IOException unused2) {
                            Log.e(TAG, "close os error!");
                        }
                        try {
                            fileInputStream.close();
                        } catch (IOException unused3) {
                            Log.e(TAG, "close desInputStream error!");
                        }
                        if (this == 0) {
                            return;
                        }
                    } catch (IOException | JSONException unused4) {
                        fileOutputStream2 = fileOutputStream;
                        Log.e(TAG, "operate file error!");
                        if (fileOutputStream2 != null) {
                            try {
                                fileOutputStream2.close();
                            } catch (IOException unused5) {
                                Log.e(TAG, "close os error!");
                            }
                        }
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (IOException unused6) {
                                Log.e(TAG, "close desInputStream error!");
                            }
                        }
                        if (this == 0) {
                            return;
                        }
                        this.close();
                    } catch (Throwable th) {
                        th = th;
                        fileOutputStream2 = fileOutputStream;
                        if (fileOutputStream2 != null) {
                            try {
                                fileOutputStream2.close();
                            } catch (IOException unused7) {
                                Log.e(TAG, "close os error!");
                            }
                        }
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (IOException unused8) {
                                Log.e(TAG, "close desInputStream error!");
                            }
                        }
                        if (this == 0) {
                            throw th;
                        }
                        try {
                            this.close();
                            throw th;
                        } catch (IOException unused9) {
                            Log.e(TAG, "close srcInputStream error!");
                            throw th;
                        }
                    }
                } catch (IOException | JSONException unused10) {
                    fileInputStream = null;
                } catch (Throwable th2) {
                    th = th2;
                    fileInputStream = null;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (IOException | JSONException unused11) {
            this = 0;
            fileInputStream = null;
        } catch (Throwable th4) {
            th = th4;
            this = 0;
            fileInputStream = null;
        }
        try {
            this.close();
        } catch (IOException unused12) {
            Log.e(TAG, "close srcInputStream error!");
        }
    }

    public static RecommendManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RecommendManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecommendManager(context);
                }
            }
        }
        return INSTANCE;
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x0094, code lost:
    
        if (r8 == null) goto L36;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.io.FileReader] */
    /* JADX WARN: Type inference failed for: r3v4, types: [java.io.FileReader] */
    /* JADX WARN: Type inference failed for: r3v5, types: [java.io.FileReader, java.io.Reader] */
    /* JADX WARN: Type inference failed for: r8v1, types: [java.io.File] */
    /* JADX WARN: Type inference failed for: r8v10 */
    /* JADX WARN: Type inference failed for: r8v3 */
    /* JADX WARN: Type inference failed for: r8v6, types: [java.io.BufferedReader] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String getRecommendFromData(java.lang.String r8) {
        /*
            r7 = this;
            java.lang.String r0 = "close file error!"
            java.lang.String r1 = "RecommendHelper"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            android.content.Context r7 = r7.mContext
            android.content.Context r7 = r7.getApplicationContext()
            java.io.File r7 = r7.getFilesDir()
            java.lang.String r7 = r7.getAbsolutePath()
            r3.append(r7)
            java.lang.String r7 = "/"
            r3.append(r7)
            r3.append(r8)
            java.lang.String r7 = r3.toString()
            java.io.File r8 = new java.io.File
            r8.<init>(r7)
            boolean r7 = r8.exists()
            if (r7 != 0) goto L39
            java.lang.String r7 = ""
            return r7
        L39:
            r7 = 0
            java.io.FileReader r3 = new java.io.FileReader     // Catch: java.lang.Throwable -> L69 java.io.IOException -> L6e
            r3.<init>(r8)     // Catch: java.lang.Throwable -> L69 java.io.IOException -> L6e
            java.io.BufferedReader r8 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L5f java.io.IOException -> L64
            r8.<init>(r3)     // Catch: java.lang.Throwable -> L5f java.io.IOException -> L64
        L44:
            java.lang.String r7 = r8.readLine()     // Catch: java.io.IOException -> L5d java.lang.Throwable -> L9c
            if (r7 == 0) goto L4e
            r2.append(r7)     // Catch: java.io.IOException -> L5d java.lang.Throwable -> L9c
            goto L44
        L4e:
            r3.close()     // Catch: java.io.IOException -> L52
            goto L55
        L52:
            android.util.Log.e(r1, r0)
        L55:
            r8.close()     // Catch: java.io.IOException -> L59
            goto L97
        L59:
            android.util.Log.e(r1, r0)
            goto L97
        L5d:
            r7 = move-exception
            goto L72
        L5f:
            r8 = move-exception
            r6 = r8
            r8 = r7
            r7 = r6
            goto L9d
        L64:
            r8 = move-exception
            r6 = r8
            r8 = r7
            r7 = r6
            goto L72
        L69:
            r8 = move-exception
            r3 = r7
            r7 = r8
            r8 = r3
            goto L9d
        L6e:
            r8 = move-exception
            r3 = r7
            r7 = r8
            r8 = r3
        L72:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L9c
            r4.<init>()     // Catch: java.lang.Throwable -> L9c
            java.lang.String r5 = "read file error:"
            r4.append(r5)     // Catch: java.lang.Throwable -> L9c
            java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> L9c
            r4.append(r7)     // Catch: java.lang.Throwable -> L9c
            java.lang.String r7 = r4.toString()     // Catch: java.lang.Throwable -> L9c
            android.util.Log.e(r1, r7)     // Catch: java.lang.Throwable -> L9c
            if (r3 == 0) goto L94
            r3.close()     // Catch: java.io.IOException -> L91
            goto L94
        L91:
            android.util.Log.e(r1, r0)
        L94:
            if (r8 == 0) goto L97
            goto L55
        L97:
            java.lang.String r7 = r2.toString()
            return r7
        L9c:
            r7 = move-exception
        L9d:
            if (r3 == 0) goto La6
            r3.close()     // Catch: java.io.IOException -> La3
            goto La6
        La3:
            android.util.Log.e(r1, r0)
        La6:
            if (r8 == 0) goto Laf
            r8.close()     // Catch: java.io.IOException -> Lac
            goto Laf
        Lac:
            android.util.Log.e(r1, r0)
        Laf:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.recommend.RecommendManager.getRecommendFromData(java.lang.String):java.lang.String");
    }

    private List<RecommendPage> getRecommendPageList() {
        RecommendResult recommendResult = getRecommendResult();
        if (recommendResult != null) {
            return recommendResult.getPages();
        }
        Log.e(TAG, "RecommendResult is null");
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x006d, code lost:
    
        if (r3 == null) goto L40;
     */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x0073 -> B:37:0x0076). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.android.settings.recommend.bean.RecommendResult getRecommendResult() {
        /*
            r8 = this;
            java.lang.String r0 = "ro.build.date.utc"
            java.lang.String r1 = "close desInputStream error!"
            java.lang.String r2 = "RecommendHelper"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            android.content.Context r4 = r8.mContext
            java.io.File r4 = r4.getFilesDir()
            java.lang.String r4 = r4.getAbsolutePath()
            r3.append(r4)
            java.lang.String r4 = "/"
            r3.append(r4)
            java.lang.String r4 = "recommend.json"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.io.File r5 = new java.io.File
            r5.<init>(r3)
            boolean r3 = r5.exists()
            if (r3 != 0) goto L37
            r8.copyRecommendJson(r5)
            goto L76
        L37:
            r3 = 0
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L5e org.json.JSONException -> L60 java.io.IOException -> L68
            r6.<init>(r5)     // Catch: java.lang.Throwable -> L5e org.json.JSONException -> L60 java.io.IOException -> L68
            org.json.JSONObject r3 = com.android.settingslib.search.SearchUtils.readJSONObject(r6)     // Catch: java.lang.Throwable -> L57 org.json.JSONException -> L5a java.io.IOException -> L5c
            r7 = 0
            int r3 = r3.optInt(r0, r7)     // Catch: java.lang.Throwable -> L57 org.json.JSONException -> L5a java.io.IOException -> L5c
            java.lang.String r0 = android.os.SystemProperties.get(r0)     // Catch: java.lang.Throwable -> L57 org.json.JSONException -> L5a java.io.IOException -> L5c
            int r0 = java.lang.Integer.parseInt(r0)     // Catch: java.lang.Throwable -> L57 org.json.JSONException -> L5a java.io.IOException -> L5c
            if (r3 == r0) goto L53
            r8.copyRecommendJson(r5)     // Catch: java.lang.Throwable -> L57 org.json.JSONException -> L5a java.io.IOException -> L5c
        L53:
            r6.close()     // Catch: java.io.IOException -> L73
            goto L76
        L57:
            r8 = move-exception
            r3 = r6
            goto L7f
        L5a:
            r3 = r6
            goto L60
        L5c:
            r3 = r6
            goto L68
        L5e:
            r8 = move-exception
            goto L7f
        L60:
            java.lang.String r0 = "JSON error"
            android.util.Log.e(r2, r0)     // Catch: java.lang.Throwable -> L5e
            if (r3 == 0) goto L76
            goto L6f
        L68:
            java.lang.String r0 = "IO error"
            android.util.Log.e(r2, r0)     // Catch: java.lang.Throwable -> L5e
            if (r3 == 0) goto L76
        L6f:
            r3.close()     // Catch: java.io.IOException -> L73
            goto L76
        L73:
            android.util.Log.e(r2, r1)
        L76:
            java.lang.String r0 = r8.getRecommendFromData(r4)
            com.android.settings.recommend.bean.RecommendResult r8 = r8.parseJson(r0)
            return r8
        L7f:
            if (r3 == 0) goto L88
            r3.close()     // Catch: java.io.IOException -> L85
            goto L88
        L85:
            android.util.Log.e(r2, r1)
        L88:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.recommend.RecommendManager.getRecommendResult():com.android.settings.recommend.bean.RecommendResult");
    }

    public static boolean isLoadComplete() {
        return mIsLoadComplete.get();
    }

    private RecommendResult parseJson(String str) {
        try {
            return (RecommendResult) new Gson().fromJson(str, RecommendResult.class);
        } catch (Exception e) {
            Log.e(TAG, "parseJson error:" + e.toString());
            return null;
        }
    }

    public List<RecommendItem> getRecommendItemList(int i) {
        Map<Integer, List<RecommendItem>> map = mCacheRecommendList;
        if (map == null || map.size() <= 0) {
            return null;
        }
        return mCacheRecommendList.get(Integer.valueOf(i));
    }

    public void loadRecommendList() {
        long currentTimeMillis = System.currentTimeMillis();
        mCacheRecommendList.clear();
        List<RecommendPage> recommendPageList = getRecommendPageList();
        if (recommendPageList != null && recommendPageList.size() > 0) {
            for (RecommendPage recommendPage : recommendPageList) {
                mCacheRecommendList.put(Integer.valueOf(recommendPage.getSourcePageIndex()), recommendPage.getItems());
            }
        }
        mIsLoadComplete.compareAndSet(false, true);
        Log.d(TAG, "Load end, total time" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
    }
}
