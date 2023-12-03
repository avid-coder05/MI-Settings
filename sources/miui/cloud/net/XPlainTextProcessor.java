package miui.cloud.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;

/* loaded from: classes3.dex */
public class XPlainTextProcessor implements XHttpClient.ISendDataProcessor, XHttpClient.IReceiveDataProcessor {
    public static final XAutoAdaptProcessor.DataProcessorCreator CREATOR = new XAutoAdaptProcessor.DataProcessorCreator() { // from class: miui.cloud.net.XPlainTextProcessor.1
        @Override // miui.cloud.net.XAutoAdaptProcessor.DataProcessorCreator
        public XHttpClient.IReceiveDataProcessor getInstanceIfAbleToProcessInData(String str, String str2) {
            if (str.equals(XPlainTextProcessor.MIME_TYPE)) {
                return new XPlainTextProcessor(str2);
            }
            return null;
        }

        @Override // miui.cloud.net.XAutoAdaptProcessor.DataProcessorCreator
        public XHttpClient.ISendDataProcessor getInstanceIfAbleToProcessOutData(Object obj, String str) {
            if (obj instanceof String) {
                return new XPlainTextProcessor(str);
            }
            return null;
        }
    };
    private static String MIME_TYPE = "text/plain";
    private String mBufferedString;
    private byte[] mBufferedStringByte;
    private XByteArrayProcessor mByteArrayProcessor = new XByteArrayProcessor();
    private String mEncode;

    public XPlainTextProcessor(String str) {
        this.mEncode = str;
    }

    private void bufferString(String str) throws XHttpClient.DataConversionException {
        if (this.mBufferedString == str) {
            return;
        }
        this.mBufferedString = str;
        this.mBufferedStringByte = new byte[0];
        try {
            this.mBufferedStringByte = str.getBytes(this.mEncode);
        } catch (UnsupportedEncodingException e) {
            throw new XHttpClient.DataConversionException(e);
        }
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public String getOutDataContentType(Object obj) {
        return MIME_TYPE;
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public int getOutDataLength(Object obj) throws XHttpClient.DataConversionException {
        bufferString((String) obj);
        return this.mBufferedStringByte.length;
    }

    @Override // miui.cloud.net.XHttpClient.IReceiveDataProcessor
    public Object processInData(Map<String, List<String>> map, InputStream inputStream) throws IOException, XHttpClient.DataConversionException {
        try {
            return new String((byte[]) this.mByteArrayProcessor.processInData(map, inputStream), this.mEncode);
        } catch (UnsupportedEncodingException e) {
            throw new XHttpClient.DataConversionException(e);
        }
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public void processOutData(Object obj, OutputStream outputStream) throws IOException, XHttpClient.DataConversionException {
        bufferString(obj.toString());
        this.mByteArrayProcessor.processOutData(this.mBufferedStringByte, outputStream);
    }
}
