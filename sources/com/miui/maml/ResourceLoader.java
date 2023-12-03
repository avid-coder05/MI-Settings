package com.miui.maml;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.MemoryFile;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.search.SearchUpdater;
import com.miui.maml.ResourceManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/* loaded from: classes2.dex */
public abstract class ResourceLoader {
    protected String mLanguageCountrySuffix;
    protected String mLanguageSuffix;
    protected Locale mLocale;
    protected String mManifestName = "manifest.xml";
    protected String mConfigName = "config.xml";

    private String getPathForLanguage(String str, String str2) {
        if (!TextUtils.isEmpty(this.mLanguageCountrySuffix)) {
            String str3 = str2 + "_" + this.mLanguageCountrySuffix + "/" + str;
            if (resourceExists(str3)) {
                return str3;
            }
        }
        if (!TextUtils.isEmpty(this.mLanguageSuffix)) {
            String str4 = str2 + "_" + this.mLanguageSuffix + "/" + str;
            if (resourceExists(str4)) {
                return str4;
            }
        }
        if (!TextUtils.isEmpty(str2)) {
            String str5 = str2 + "/" + str;
            if (resourceExists(str5)) {
                return str5;
            }
        }
        if (resourceExists(str)) {
            return str;
        }
        return null;
    }

    private Element getXmlRoot(String str) {
        InputStream inputStream = getInputStream(getPathForLanguage(str));
        try {
            if (inputStream == null) {
                Log.e("ResourceLoader", "getXmlRoot local inputStream is null");
                return null;
            }
            try {
                try {
                    try {
                        Element documentElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream).getDocumentElement();
                        try {
                            inputStream.close();
                        } catch (IOException unused) {
                        }
                        return documentElement;
                    } catch (IOException e) {
                        Log.e("ResourceLoader", e.toString());
                        try {
                            inputStream.close();
                        } catch (IOException unused2) {
                            return null;
                        }
                    }
                } catch (OutOfMemoryError e2) {
                    Log.e("ResourceLoader", e2.toString());
                    inputStream.close();
                } catch (ParserConfigurationException e3) {
                    Log.e("ResourceLoader", e3.toString());
                    inputStream.close();
                }
            } catch (SAXException e4) {
                Log.e("ResourceLoader", e4.toString());
                inputStream.close();
            } catch (Exception e5) {
                Log.e("ResourceLoader", e5.toString());
                inputStream.close();
            }
        } catch (Throwable th) {
            try {
                inputStream.close();
            } catch (IOException unused3) {
            }
            throw th;
        }
    }

    public void finish() {
    }

    public ResourceManager.BitmapInfo getBitmapInfo(String str, BitmapFactory.Options options) {
        Rect rect;
        Bitmap decodeStream;
        String pathForLanguage = getPathForLanguage(str, "images");
        if (pathForLanguage == null) {
            Log.d("ResourceLoader", "TRY AGAIN to get getPathForLanguage: " + str);
            pathForLanguage = getPathForLanguage(str, "images");
            if (pathForLanguage == null) {
                Log.e("ResourceLoader", "fail to get getPathForLanguage: " + str);
                return null;
            }
        }
        InputStream inputStream = getInputStream(pathForLanguage);
        if (inputStream == null) {
            Log.d("ResourceLoader", "TRY AGAIN to get InputStream: " + str);
            inputStream = getInputStream(pathForLanguage);
            if (inputStream == null) {
                Log.e("ResourceLoader", "fail to get InputStream: " + str);
                return null;
            }
        }
        try {
            try {
                rect = new Rect();
                decodeStream = BitmapFactory.decodeStream(inputStream, rect, options);
            } catch (OutOfMemoryError e) {
                Log.e("ResourceLoader", e.toString());
            }
            if (decodeStream != null) {
                ResourceManager.BitmapInfo bitmapInfo = new ResourceManager.BitmapInfo(decodeStream, rect);
                try {
                    inputStream.close();
                } catch (IOException unused) {
                }
                return bitmapInfo;
            }
            Log.d("ResourceLoader", "TRY AGAIN to decode bitmap: " + str);
            if (BitmapFactory.decodeStream(inputStream, rect, options) == null) {
                Log.e("ResourceLoader", "fail to decode bitmap: " + str);
                try {
                    inputStream.close();
                } catch (IOException unused2) {
                }
                return null;
            }
            try {
                inputStream.close();
            } catch (IOException unused3) {
                return null;
            }
        } catch (Throwable th) {
            try {
                inputStream.close();
            } catch (IOException unused4) {
            }
            throw th;
        }
    }

    public Element getConfigRoot() {
        return getXmlRoot(this.mConfigName);
    }

    public MemoryFile getFile(String str) {
        long[] jArr = new long[1];
        InputStream inputStream = getInputStream(str, jArr);
        if (inputStream == null) {
            return null;
        }
        try {
            try {
                byte[] bArr = new byte[SearchUpdater.GOOGLE];
                MemoryFile memoryFile = new MemoryFile(str, (int) jArr[0]);
                int i = 0;
                while (true) {
                    int read = inputStream.read(bArr, 0, SearchUpdater.GOOGLE);
                    if (read <= 0) {
                        break;
                    }
                    memoryFile.writeBytes(bArr, 0, i, read);
                    i += read;
                }
                if (memoryFile.length() > 0) {
                    try {
                        inputStream.close();
                    } catch (IOException unused) {
                    }
                    return memoryFile;
                }
            } catch (Throwable th) {
                try {
                    inputStream.close();
                } catch (IOException unused2) {
                }
                throw th;
            }
        } catch (IOException e) {
            Log.e("ResourceLoader", e.toString());
        } catch (OutOfMemoryError e2) {
            Log.e("ResourceLoader", e2.toString());
        }
        try {
            inputStream.close();
        } catch (IOException unused3) {
            return null;
        }
    }

    public abstract String getID();

    public final InputStream getInputStream(String str) {
        return getInputStream(str, null);
    }

    public abstract InputStream getInputStream(String str, long[] jArr);

    public Element getManifestRoot() {
        return getXmlRoot(this.mManifestName);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x002a, code lost:
    
        if (resourceExists(r0) == false) goto L14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0013, code lost:
    
        if (resourceExists(r0) == false) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String getPathForLanguage(java.lang.String r4) {
        /*
            r3 = this;
            java.lang.String r0 = r3.mLanguageCountrySuffix
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            r1 = 0
            if (r0 != 0) goto L15
            java.lang.String r0 = r3.mLanguageCountrySuffix
            java.lang.String r0 = com.miui.maml.util.Utils.addFileNameSuffix(r4, r0)
            boolean r2 = r3.resourceExists(r0)
            if (r2 != 0) goto L16
        L15:
            r0 = r1
        L16:
            if (r0 != 0) goto L2d
            java.lang.String r2 = r3.mLanguageSuffix
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L2d
            java.lang.String r0 = r3.mLanguageSuffix
            java.lang.String r0 = com.miui.maml.util.Utils.addFileNameSuffix(r4, r0)
            boolean r3 = r3.resourceExists(r0)
            if (r3 != 0) goto L2d
            goto L2e
        L2d:
            r1 = r0
        L2e:
            if (r1 == 0) goto L31
            r4 = r1
        L31:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ResourceLoader.getPathForLanguage(java.lang.String):java.lang.String");
    }

    public void init() {
    }

    public abstract boolean resourceExists(String str);

    public ResourceLoader setLocal(Locale locale) {
        if (locale != null) {
            this.mLanguageSuffix = locale.getLanguage();
            String locale2 = locale.toString();
            this.mLanguageCountrySuffix = locale2;
            if (TextUtils.equals(this.mLanguageSuffix, locale2)) {
                this.mLanguageSuffix = null;
            }
        }
        this.mLocale = locale;
        return this;
    }
}
