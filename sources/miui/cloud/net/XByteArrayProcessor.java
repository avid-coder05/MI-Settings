package miui.cloud.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;

/* loaded from: classes3.dex */
public class XByteArrayProcessor implements XHttpClient.ISendDataProcessor, XHttpClient.IReceiveDataProcessor {
    public static final XAutoAdaptProcessor.DataProcessorCreator CREATOR = new XAutoAdaptProcessor.DataProcessorCreator() { // from class: miui.cloud.net.XByteArrayProcessor.1
        @Override // miui.cloud.net.XAutoAdaptProcessor.DataProcessorCreator
        public XHttpClient.IReceiveDataProcessor getInstanceIfAbleToProcessInData(String str, String str2) {
            if (str.equals(XByteArrayProcessor.MIME_TYPE)) {
                return new XByteArrayProcessor();
            }
            return null;
        }

        @Override // miui.cloud.net.XAutoAdaptProcessor.DataProcessorCreator
        public XHttpClient.ISendDataProcessor getInstanceIfAbleToProcessOutData(Object obj, String str) {
            if (obj instanceof byte[]) {
                return new XByteArrayProcessor();
            }
            return null;
        }
    };
    private static int IN_DATA_PROCESSING_BUFFER_LEN = 256;
    private static String MIME_TYPE = "application/octet-stream";

    protected static int getContentLengthFromHeader(Map<String, List<String>> map) {
        String str;
        List<String> list = map.get("Content-Encoding");
        if (list != null) {
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                if (!it.next().equals("identity")) {
                    return -1;
                }
            }
        }
        List<String> list2 = map.get("Content-Length");
        if (list2 == null || list2.isEmpty() || (str = list2.get(0)) == null) {
            return -1;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public String getOutDataContentType(Object obj) {
        return MIME_TYPE;
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public int getOutDataLength(Object obj) {
        return ((byte[]) obj).length;
    }

    @Override // miui.cloud.net.XHttpClient.IReceiveDataProcessor
    public Object processInData(Map<String, List<String>> map, InputStream inputStream) throws IOException {
        int contentLengthFromHeader = getContentLengthFromHeader(map);
        int i = 0;
        if (contentLengthFromHeader >= 0) {
            byte[] bArr = new byte[contentLengthFromHeader];
            do {
                int read = inputStream.read(bArr, i, contentLengthFromHeader - i);
                if (read <= 0) {
                    break;
                }
                i += read;
            } while (i != contentLengthFromHeader);
            return bArr;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr2 = new byte[IN_DATA_PROCESSING_BUFFER_LEN];
        while (true) {
            int read2 = inputStream.read(bArr2);
            if (read2 <= 0) {
                byteArrayOutputStream.close();
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr2, 0, read2);
        }
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public void processOutData(Object obj, OutputStream outputStream) throws IOException {
        outputStream.write((byte[]) obj);
    }
}
