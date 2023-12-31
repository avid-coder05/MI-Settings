package com.android.settingslib.license;

import android.content.Context;
import android.util.Log;
import com.android.settingslib.R$string;
import com.android.settingslib.utils.AsyncLoaderCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class LicenseHtmlLoaderCompat extends AsyncLoaderCompat<File> {
    static final String[] DEFAULT_LICENSE_XML_PATHS = {"/system/etc/NOTICE.xml.gz", "/vendor/etc/NOTICE.xml.gz", "/odm/etc/NOTICE.xml.gz", "/oem/etc/NOTICE.xml.gz", "/product/etc/NOTICE.xml.gz", "/system_ext/etc/NOTICE.xml.gz", "/vendor_dlkm/etc/NOTICE.xml.gz", "/odm_dlkm/etc/NOTICE.xml.gz"};
    private final Context mContext;

    public LicenseHtmlLoaderCompat(Context context) {
        super(context);
        this.mContext = context;
    }

    private boolean generateHtmlFile(Context context, List<File> list, File file) {
        return LicenseHtmlGeneratorFromXml.generateHtml(list, file, context.getString(R$string.notice_header));
    }

    private File generateHtmlFromDefaultXmlFiles() {
        List<File> vaildXmlFiles = getVaildXmlFiles();
        if (vaildXmlFiles.isEmpty()) {
            Log.e("LicenseHtmlLoaderCompat", "No notice file exists.");
            return null;
        }
        File cachedHtmlFile = getCachedHtmlFile(this.mContext);
        if (!isCachedHtmlFileOutdated(vaildXmlFiles, cachedHtmlFile) || generateHtmlFile(this.mContext, vaildXmlFiles, cachedHtmlFile)) {
            return cachedHtmlFile;
        }
        return null;
    }

    private File getCachedHtmlFile(Context context) {
        return new File(context.getCacheDir(), "NOTICE.html");
    }

    private List<File> getVaildXmlFiles() {
        ArrayList arrayList = new ArrayList();
        for (String str : DEFAULT_LICENSE_XML_PATHS) {
            File file = new File(str);
            if (file.exists() && file.length() != 0) {
                arrayList.add(file);
            }
        }
        return arrayList;
    }

    private boolean isCachedHtmlFileOutdated(List<File> list, File file) {
        if (!file.exists() || file.length() == 0) {
            return true;
        }
        Iterator<File> it = list.iterator();
        while (it.hasNext()) {
            if (file.lastModified() < it.next().lastModified()) {
                return true;
            }
        }
        return false;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public File loadInBackground() {
        return generateHtmlFromDefaultXmlFiles();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoaderCompat
    public void onDiscardResult(File file) {
    }
}
