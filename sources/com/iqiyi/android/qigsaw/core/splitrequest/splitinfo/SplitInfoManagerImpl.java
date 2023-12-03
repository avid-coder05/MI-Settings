package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import com.iqiyi.android.qigsaw.core.common.CompatBundle;
import com.iqiyi.android.qigsaw.core.common.FileUtil;
import com.iqiyi.android.qigsaw.core.common.ICompatBundle;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitInfoManagerImpl implements SplitInfoManager {
    private static final String TAG = "SplitInfoManagerImpl";
    private AtomicReference<SplitDetails> splitDetailsRef = new AtomicReference<>();
    private SplitInfoVersionManager versionManager;

    private static InputStream createInputStreamFromAssets(Context context, String str) {
        Resources resources = context.getResources();
        if (resources != null) {
            try {
                return resources.getAssets().open(str);
            } catch (IOException unused) {
            }
        }
        return null;
    }

    private SplitDetails createSplitDetailsForDefaultVersion(Context context, String str) {
        try {
            String str2 = "qigsaw/qigsaw_" + str + SplitConstants.DOT_JSON;
            SplitLog.i(TAG, "Default split file name: " + str2, new Object[0]);
            long currentTimeMillis = System.currentTimeMillis();
            SplitDetails parseSplitContentsForDefaultVersion = parseSplitContentsForDefaultVersion(context, str2);
            SplitLog.i(TAG, "Cost %d mil-second to parse default split info", Long.valueOf(System.currentTimeMillis() - currentTimeMillis));
            return parseSplitContentsForDefaultVersion;
        } catch (Throwable th) {
            SplitLog.printErrStackTrace(TAG, th, "Failed to create default split info!", new Object[0]);
            return null;
        }
    }

    private SplitDetails createSplitDetailsForNewVersion(File file) {
        try {
            SplitLog.i(TAG, "Updated split file path: " + file.getAbsolutePath(), new Object[0]);
            long currentTimeMillis = System.currentTimeMillis();
            SplitDetails parseSplitContentsForNewVersion = parseSplitContentsForNewVersion(file);
            SplitLog.i(TAG, "Cost %d mil-second to parse updated split info", Long.valueOf(System.currentTimeMillis() - currentTimeMillis));
            return parseSplitContentsForNewVersion;
        } catch (Throwable th) {
            SplitLog.printErrStackTrace(TAG, th, "Failed to create updated split info!", new Object[0]);
            return null;
        }
    }

    private synchronized SplitDetails getOrCreateSplitDetails(Context context) {
        SplitDetails createSplitDetailsForNewVersion;
        SplitInfoVersionManager splitInfoVersionManager = getSplitInfoVersionManager();
        SplitDetails splitDetails = getSplitDetails();
        if (splitDetails == null) {
            String currentVersion = splitInfoVersionManager.getCurrentVersion();
            String defaultVersion = splitInfoVersionManager.getDefaultVersion();
            if (currentVersion != null && currentVersion.length() > 0 && currentVersion.indexOf(95) > -1) {
                currentVersion = "1.0" + currentVersion.substring(currentVersion.indexOf(95));
            }
            if (defaultVersion != null && defaultVersion.length() > 0 && defaultVersion.indexOf(95) > -1) {
                defaultVersion = "1.0" + defaultVersion.substring(defaultVersion.indexOf(95));
            }
            SplitLog.i(TAG, "currentVersion : %s defaultVersion : %s", currentVersion, defaultVersion);
            if (defaultVersion.equals(currentVersion)) {
                createSplitDetailsForNewVersion = createSplitDetailsForDefaultVersion(context, defaultVersion);
            } else {
                createSplitDetailsForNewVersion = createSplitDetailsForNewVersion(new File(splitInfoVersionManager.getRootDir(), SplitConstants.QIGSAW_PREFIX + currentVersion + SplitConstants.DOT_JSON));
            }
            splitDetails = createSplitDetailsForNewVersion;
            if (splitDetails != null && TextUtils.isEmpty(splitDetails.getQigsawId())) {
                return null;
            }
            this.splitDetailsRef.compareAndSet(null, splitDetails);
        }
        return splitDetails;
    }

    private SplitDetails getSplitDetails() {
        return this.splitDetailsRef.get();
    }

    private SplitInfoVersionManager getSplitInfoVersionManager() {
        return this.versionManager;
    }

    private static SplitDetails parseSplitContentsForDefaultVersion(Context context, String str) throws IOException, JSONException {
        ICompatBundle iCompatBundle = CompatBundle.instance;
        return parseSplitsContent(iCompatBundle != null ? iCompatBundle.readDefaultSplitVersionContent(context, str) : readInputStreamContent(createInputStreamFromAssets(context, str)));
    }

    private SplitDetails parseSplitContentsForNewVersion(File file) throws IOException, JSONException {
        if (file == null || !file.exists()) {
            return null;
        }
        return parseSplitsContent(readInputStreamContent(new FileInputStream(file)));
    }

    private static SplitDetails parseSplitsContent(String str) throws JSONException {
        ArrayList arrayList;
        ArrayList arrayList2;
        ArrayList arrayList3;
        ArrayList arrayList4;
        String str2;
        int i;
        LinkedHashMap linkedHashMap;
        String str3;
        ArrayList arrayList5;
        int i2;
        int i3;
        ArrayList arrayList6;
        ArrayList arrayList7;
        ArrayList arrayList8;
        ArrayList arrayList9;
        int i4;
        int i5;
        ArrayList arrayList10;
        ArrayList arrayList11;
        ArrayList arrayList12 = null;
        if (str == null) {
            return null;
        }
        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        JSONObject jSONObject = new JSONObject(str);
        String optString = jSONObject.optString("qigsawId");
        String optString2 = jSONObject.optString("appVersionName");
        JSONArray optJSONArray = jSONObject.optJSONArray("updateSplits");
        if (optJSONArray == null || optJSONArray.length() <= 0) {
            arrayList = null;
        } else {
            ArrayList arrayList13 = new ArrayList(optJSONArray.length());
            for (int i6 = 0; i6 < optJSONArray.length(); i6++) {
                arrayList13.add(optJSONArray.getString(i6));
            }
            arrayList = arrayList13;
        }
        JSONArray optJSONArray2 = jSONObject.optJSONArray("splitEntryFragments");
        if (optJSONArray2 == null || optJSONArray2.length() <= 0) {
            arrayList2 = null;
        } else {
            ArrayList arrayList14 = new ArrayList(optJSONArray2.length());
            for (int i7 = 0; i7 < optJSONArray2.length(); i7++) {
                arrayList14.add(optJSONArray2.getString(i7));
            }
            arrayList2 = arrayList14;
        }
        JSONArray optJSONArray3 = jSONObject.optJSONArray("splits");
        if (optJSONArray3 != null) {
            int i8 = 0;
            while (i8 < optJSONArray3.length()) {
                JSONObject jSONObject2 = optJSONArray3.getJSONObject(i8);
                boolean optBoolean = jSONObject2.optBoolean("builtIn");
                String optString3 = jSONObject2.optString(SplitConstants.KET_NAME);
                String optString4 = jSONObject2.optString("version");
                int optInt = jSONObject2.optInt("minSdkVersion");
                int optInt2 = jSONObject2.optInt("dexNumber");
                JSONArray optJSONArray4 = jSONObject2.optJSONArray("workProcesses");
                if (optJSONArray4 == null || optJSONArray4.length() <= 0) {
                    arrayList3 = arrayList12;
                } else {
                    ArrayList arrayList15 = new ArrayList(optJSONArray4.length());
                    for (int i9 = 0; i9 < optJSONArray4.length(); i9++) {
                        arrayList15.add(optJSONArray4.optString(i9));
                    }
                    arrayList3 = arrayList15;
                }
                JSONArray optJSONArray5 = jSONObject2.optJSONArray("dependencies");
                if (optJSONArray5 == null || optJSONArray5.length() <= 0) {
                    arrayList4 = null;
                } else {
                    ArrayList arrayList16 = new ArrayList(optJSONArray5.length());
                    for (int i10 = 0; i10 < optJSONArray5.length(); i10++) {
                        arrayList16.add(optJSONArray5.optString(i10));
                    }
                    arrayList4 = arrayList16;
                }
                JSONArray optJSONArray6 = jSONObject2.optJSONArray("apkData");
                if (optJSONArray6 == null || optJSONArray6.length() == 0) {
                    throw new RuntimeException("No apkData found in split-details file!");
                }
                ArrayList arrayList17 = new ArrayList(optJSONArray6.length());
                JSONArray jSONArray = optJSONArray3;
                int i11 = 0;
                while (true) {
                    str2 = optString;
                    i = i8;
                    linkedHashMap = linkedHashMap2;
                    str3 = "abi";
                    if (i11 >= optJSONArray6.length()) {
                        break;
                    }
                    JSONObject optJSONObject = optJSONArray6.optJSONObject(i11);
                    arrayList17.add(new SplitInfo.ApkData(optJSONObject.optString("abi"), optJSONObject.optString("url"), optJSONObject.optString("md5"), optJSONObject.optLong("size")));
                    i11++;
                    optString = str2;
                    i8 = i;
                    linkedHashMap2 = linkedHashMap;
                }
                JSONArray optJSONArray7 = jSONObject2.optJSONArray("libData");
                if (optJSONArray7 == null || optJSONArray7.length() <= 0) {
                    arrayList5 = arrayList17;
                    i2 = optInt;
                    i3 = optInt2;
                    arrayList6 = arrayList3;
                    arrayList7 = arrayList4;
                    arrayList8 = null;
                } else {
                    ArrayList arrayList18 = new ArrayList(optJSONArray7.length());
                    int i12 = 0;
                    while (i12 < optJSONArray7.length()) {
                        JSONObject optJSONObject2 = optJSONArray7.optJSONObject(i12);
                        JSONArray jSONArray2 = optJSONArray7;
                        String optString5 = optJSONObject2.optString(str3);
                        String str4 = str3;
                        JSONArray optJSONArray8 = optJSONObject2.optJSONArray("jniLibs");
                        ArrayList arrayList19 = new ArrayList();
                        if (optJSONArray8 == null || optJSONArray8.length() <= 0) {
                            arrayList9 = arrayList17;
                            i4 = optInt;
                            i5 = optInt2;
                            arrayList10 = arrayList3;
                            arrayList11 = arrayList4;
                        } else {
                            arrayList9 = arrayList17;
                            arrayList11 = arrayList4;
                            int i13 = 0;
                            while (i13 < optJSONArray8.length()) {
                                JSONObject optJSONObject3 = optJSONArray8.optJSONObject(i13);
                                arrayList19.add(new SplitInfo.LibData.Lib(optJSONObject3.optString("name"), optJSONObject3.optString("md5"), optJSONObject3.optLong("size")));
                                i13++;
                                optJSONArray8 = optJSONArray8;
                                arrayList3 = arrayList3;
                                optInt = optInt;
                                optInt2 = optInt2;
                            }
                            i4 = optInt;
                            i5 = optInt2;
                            arrayList10 = arrayList3;
                        }
                        arrayList18.add(new SplitInfo.LibData(optString5, arrayList19));
                        i12++;
                        optJSONArray7 = jSONArray2;
                        str3 = str4;
                        arrayList17 = arrayList9;
                        arrayList4 = arrayList11;
                        arrayList3 = arrayList10;
                        optInt = i4;
                        optInt2 = i5;
                    }
                    arrayList5 = arrayList17;
                    i2 = optInt;
                    i3 = optInt2;
                    arrayList6 = arrayList3;
                    arrayList7 = arrayList4;
                    arrayList8 = arrayList18;
                }
                linkedHashMap2 = linkedHashMap;
                linkedHashMap2.put(optString3, new SplitInfo(optString3, optString2, optString4, optBoolean, i2, i3, arrayList6, arrayList7, arrayList5, arrayList8));
                i8 = i + 1;
                optJSONArray3 = jSONArray;
                optString = str2;
                arrayList12 = null;
            }
            return new SplitDetails(optString, optString2, arrayList, arrayList2, new SplitInfoListing(linkedHashMap2));
        }
        throw new RuntimeException("No splits found in split-details file!");
    }

    private static String readInputStreamContent(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                FileUtil.closeQuietly(inputStream);
                FileUtil.closeQuietly(bufferedReader);
                return sb.toString();
            }
            sb.append(readLine);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void attach(SplitInfoVersionManager splitInfoVersionManager) {
        this.versionManager = splitInfoVersionManager;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager
    public SplitDetails createSplitDetailsForJsonFile(String str) {
        File file = new File(str);
        if (file.exists()) {
            return createSplitDetailsForNewVersion(file);
        }
        return null;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager
    public Collection<SplitInfo> getAllSplitInfo(Context context) {
        SplitDetails orCreateSplitDetails = getOrCreateSplitDetails(context);
        if (orCreateSplitDetails != null) {
            return orCreateSplitDetails.getSplitInfoListing().getSplitInfoMap().values();
        }
        return null;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager
    public String getBaseAppVersionName(Context context) {
        SplitDetails orCreateSplitDetails = getOrCreateSplitDetails(context);
        if (orCreateSplitDetails != null) {
            return orCreateSplitDetails.getAppVersionName();
        }
        return null;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager
    public String getCurrentSplitInfoVersion() {
        return getSplitInfoVersionManager().getCurrentVersion();
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager
    public String getQigsawId(Context context) {
        SplitDetails orCreateSplitDetails = getOrCreateSplitDetails(context);
        if (orCreateSplitDetails != null) {
            return orCreateSplitDetails.getQigsawId();
        }
        return null;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager
    public List<String> getSplitEntryFragments(Context context) {
        SplitDetails orCreateSplitDetails = getOrCreateSplitDetails(context);
        if (orCreateSplitDetails != null) {
            return orCreateSplitDetails.getSplitEntryFragments();
        }
        return null;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager
    public SplitInfo getSplitInfo(Context context, String str) {
        SplitDetails orCreateSplitDetails = getOrCreateSplitDetails(context);
        if (orCreateSplitDetails != null) {
            for (SplitInfo splitInfo : orCreateSplitDetails.getSplitInfoListing().getSplitInfoMap().values()) {
                if (splitInfo.getSplitName().equals(str)) {
                    return splitInfo;
                }
            }
            return null;
        }
        return null;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager
    public List<SplitInfo> getSplitInfos(Context context, Collection<String> collection) {
        SplitDetails orCreateSplitDetails = getOrCreateSplitDetails(context);
        if (orCreateSplitDetails != null) {
            Collection<SplitInfo> values = orCreateSplitDetails.getSplitInfoListing().getSplitInfoMap().values();
            ArrayList arrayList = new ArrayList(collection.size());
            for (SplitInfo splitInfo : values) {
                if (collection.contains(splitInfo.getSplitName())) {
                    arrayList.add(splitInfo);
                }
            }
            return arrayList;
        }
        return null;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager
    public List<String> getUpdateSplits(Context context) {
        SplitDetails orCreateSplitDetails = getOrCreateSplitDetails(context);
        if (orCreateSplitDetails != null) {
            return orCreateSplitDetails.getUpdateSplits();
        }
        return null;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager
    public boolean updateSplitInfoVersion(Context context, String str, File file) {
        SplitInfoVersionManager splitInfoVersionManager = getSplitInfoVersionManager();
        if (str != null && str.length() > 0 && str.indexOf(95) > -1) {
            str = "1.0" + str.substring(str.indexOf(95));
        }
        return splitInfoVersionManager.updateVersion(context, str, file);
    }
}
