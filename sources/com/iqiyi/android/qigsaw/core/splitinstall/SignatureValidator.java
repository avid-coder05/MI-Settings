package com.iqiyi.android.qigsaw.core.splitinstall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.Signature;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.split.signature.G;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
class SignatureValidator {
    private static final String TAG = "SignatureValidator";

    private SignatureValidator() {
    }

    private static boolean a(String str, List<X509Certificate> list) {
        boolean z;
        try {
            X509Certificate[][] a = G.a(str);
            if (a == null || a.length == 0 || a[0].length == 0) {
                SplitLog.e(TAG, "Downloaded split " + str + " is not signed.", new Object[0]);
                return false;
            } else if (list.isEmpty()) {
                SplitLog.e(TAG, "No certificates found for app.", new Object[0]);
                return false;
            } else {
                Iterator<X509Certificate> it = list.iterator();
                do {
                    z = true;
                    if (!it.hasNext()) {
                        return true;
                    }
                    X509Certificate next = it.next();
                    int length = a.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            z = false;
                            break;
                        } else if (a[i][0].equals(next)) {
                            break;
                        } else {
                            i++;
                        }
                    }
                } while (z);
                SplitLog.i(TAG, "There's an app certificate that doesn't sign the split.", new Object[0]);
                return false;
            }
        } catch (Exception e) {
            SplitLog.e(TAG, "Downloaded split " + str + " is not signed.", e);
            return false;
        }
    }

    private static X509Certificate decodeCertificate(Signature signature) {
        try {
            return (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(signature.toByteArray()));
        } catch (CertificateException e) {
            SplitLog.e(TAG, "Cannot decode certificate.", e);
            return null;
        }
    }

    @SuppressLint({"PackageManagerGetSignatures"})
    private static Signature[] getAppSignature(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 64).signatures;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean validateSplit(Context context, File file) {
        ArrayList arrayList;
        Signature[] appSignature = getAppSignature(context);
        if (appSignature == null) {
            arrayList = null;
        } else {
            ArrayList arrayList2 = new ArrayList();
            for (Signature signature : appSignature) {
                X509Certificate decodeCertificate = decodeCertificate(signature);
                if (decodeCertificate != null) {
                    arrayList2.add(decodeCertificate);
                }
            }
            arrayList = arrayList2;
        }
        if (arrayList == null || arrayList.isEmpty()) {
            return false;
        }
        return a(file.getAbsolutePath(), arrayList);
    }
}
