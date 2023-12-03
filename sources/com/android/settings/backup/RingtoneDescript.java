package com.android.settings.backup;

import java.io.File;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public class RingtoneDescript {
    public String mAlarm;
    public String mNotification;
    public String mRingtone;
    public String mSmsDelivered;
    public String mSmsReceived;
    public File mXmlFile;

    /* JADX WARN: Code restructure failed: missing block: B:33:0x0055, code lost:
    
        if (r0 == null) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0057, code lost:
    
        r0.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x005b, code lost:
    
        if (r0 == null) goto L38;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x005e, code lost:
    
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean read(java.io.File r7) {
        /*
            r6 = this;
            boolean r0 = r7.exists()
            r1 = 0
            if (r0 != 0) goto L8
            return r1
        L8:
            r6.mXmlFile = r7
            r0 = 0
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L4e org.xmlpull.v1.XmlPullParserException -> L55 java.io.IOException -> L5b
            r2.<init>(r7)     // Catch: java.lang.Throwable -> L4e org.xmlpull.v1.XmlPullParserException -> L55 java.io.IOException -> L5b
            org.xmlpull.v1.XmlPullParserFactory r7 = org.xmlpull.v1.XmlPullParserFactory.newInstance()     // Catch: java.lang.Throwable -> L47 org.xmlpull.v1.XmlPullParserException -> L4a java.io.IOException -> L4c
            org.xmlpull.v1.XmlPullParser r7 = r7.newPullParser()     // Catch: java.lang.Throwable -> L47 org.xmlpull.v1.XmlPullParserException -> L4a java.io.IOException -> L4c
            java.lang.String r3 = "UTF-8"
            r7.setInput(r2, r3)     // Catch: java.lang.Throwable -> L47 org.xmlpull.v1.XmlPullParserException -> L4a java.io.IOException -> L4c
            r3 = r1
        L1e:
            r4 = 1
            if (r3 == r4) goto L29
            r5 = 2
            if (r3 == r5) goto L29
            int r3 = r7.next()     // Catch: java.lang.Throwable -> L47 org.xmlpull.v1.XmlPullParserException -> L4a java.io.IOException -> L4c
            goto L1e
        L29:
            int r3 = r7.next()     // Catch: java.lang.Throwable -> L47 org.xmlpull.v1.XmlPullParserException -> L4a java.io.IOException -> L4c
            if (r3 != r4) goto L33
            r2.close()     // Catch: java.io.IOException -> L32
        L32:
            return r4
        L33:
            r5 = 3
            if (r3 != r5) goto L37
            goto L29
        L37:
            r5 = 4
            if (r3 != r5) goto L42
            java.lang.String r3 = r7.getText()     // Catch: java.lang.Throwable -> L47 org.xmlpull.v1.XmlPullParserException -> L4a java.io.IOException -> L4c
            r6.readNode(r0, r3)     // Catch: java.lang.Throwable -> L47 org.xmlpull.v1.XmlPullParserException -> L4a java.io.IOException -> L4c
            goto L29
        L42:
            java.lang.String r0 = r7.getName()     // Catch: java.lang.Throwable -> L47 org.xmlpull.v1.XmlPullParserException -> L4a java.io.IOException -> L4c
            goto L29
        L47:
            r6 = move-exception
            r0 = r2
            goto L4f
        L4a:
            r0 = r2
            goto L55
        L4c:
            r0 = r2
            goto L5b
        L4e:
            r6 = move-exception
        L4f:
            if (r0 == 0) goto L54
            r0.close()     // Catch: java.io.IOException -> L54
        L54:
            throw r6
        L55:
            if (r0 == 0) goto L5e
        L57:
            r0.close()     // Catch: java.io.IOException -> L5e
            goto L5e
        L5b:
            if (r0 == 0) goto L5e
            goto L57
        L5e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.backup.RingtoneDescript.read(java.io.File):boolean");
    }

    protected void readNode(String str, String str2) {
        if (ThemeManagerConstants.COMPONENT_CODE_RINGTONE.equals(str)) {
            this.mRingtone = str2;
        } else if (ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION.equals(str)) {
            this.mNotification = str2;
        } else if (ThemeManagerConstants.COMPONENT_CODE_ALARM.equals(str)) {
            this.mAlarm = str2;
        } else if ("sms".equals(str)) {
            this.mSmsDelivered = str2;
        } else if ("sms_received".equals(str)) {
            this.mSmsReceived = str2;
        }
    }
}
