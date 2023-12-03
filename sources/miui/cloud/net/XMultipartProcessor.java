package miui.cloud.net;

import android.view.MiuiWindowManager$LayoutParams;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;
import miui.cloud.net.XHttpClient;

/* loaded from: classes3.dex */
public class XMultipartProcessor implements XHttpClient.ISendDataProcessor {
    private static final String END = "\r\n";
    private static final String MIME_TYPE = "multipart/form-data";
    private final String mBoundary = UUID.randomUUID().toString();
    private final String mCharset;

    public XMultipartProcessor(String str) {
        this.mCharset = str;
    }

    private void writeBytes(OutputStream outputStream, File file) throws IOException {
        byte[] bArr = new byte[MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE];
        FileInputStream fileInputStream = null;
        try {
            FileInputStream fileInputStream2 = new FileInputStream(file);
            while (true) {
                try {
                    int read = fileInputStream2.read(bArr);
                    if (read == -1) {
                        try {
                            fileInputStream2.close();
                            return;
                        } catch (IOException unused) {
                            return;
                        }
                    }
                    outputStream.write(bArr, 0, read);
                } catch (Throwable th) {
                    th = th;
                    fileInputStream = fileInputStream2;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException unused2) {
                        }
                    }
                    throw th;
                }
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void writeBytes(OutputStream outputStream, String str) throws IOException {
        outputStream.write(str.getBytes(this.mCharset));
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public String getOutDataContentType(Object obj) {
        return "multipart/form-data;boundary=" + this.mBoundary;
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public int getOutDataLength(Object obj) {
        return -1;
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public void processOutData(Object obj, OutputStream outputStream) throws IOException {
        for (Map.Entry entry : ((Map) obj).entrySet()) {
            String str = (String) entry.getKey();
            Object value = entry.getValue();
            if (str == null || value == null) {
                throw new IllegalArgumentException("null key/value");
            }
            if (value instanceof File) {
                File file = (File) entry.getValue();
                writeBytes(outputStream, "--" + this.mBoundary + END);
                writeBytes(outputStream, "Content-Disposition: form-data; name=\"" + str + "\"; filename=\"" + str + "\"" + END);
                writeBytes(outputStream, "Content-Type: stream/octet\r\n");
                writeBytes(outputStream, "Content-Transfer-Encoding: binary\r\n");
                writeBytes(outputStream, END);
                writeBytes(outputStream, file);
                writeBytes(outputStream, END);
            } else if (!(value instanceof String)) {
                throw new IllegalArgumentException("bad entry type " + value.getClass().getSimpleName() + " of key " + str);
            } else {
                writeBytes(outputStream, "--" + this.mBoundary + END);
                writeBytes(outputStream, "Content-Disposition: form-data; name=\"" + str + "\"" + END);
                writeBytes(outputStream, END);
                writeBytes(outputStream, (String) value);
                writeBytes(outputStream, END);
            }
        }
        writeBytes(outputStream, "--" + this.mBoundary + "--");
    }
}
