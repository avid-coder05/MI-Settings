package miui.cloud.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class XJSONProcessor implements XHttpClient.ISendDataProcessor, XHttpClient.IReceiveDataProcessor {
    public static final XAutoAdaptProcessor.DataProcessorCreator CREATOR = new XAutoAdaptProcessor.DataProcessorCreator() { // from class: miui.cloud.net.XJSONProcessor.1
        @Override // miui.cloud.net.XAutoAdaptProcessor.DataProcessorCreator
        public XHttpClient.IReceiveDataProcessor getInstanceIfAbleToProcessInData(String str, String str2) {
            if (str.equals(XJSONProcessor.MIME_TYPE)) {
                return new XJSONProcessor(str2);
            }
            return null;
        }

        @Override // miui.cloud.net.XAutoAdaptProcessor.DataProcessorCreator
        public XHttpClient.ISendDataProcessor getInstanceIfAbleToProcessOutData(Object obj, String str) {
            if ((obj instanceof JSONObject) || (obj instanceof JSONArray)) {
                return new XJSONProcessor(str);
            }
            return null;
        }
    };
    private static String MIME_TYPE = "application/json";
    private Object mBufferedData;
    private String mBufferedDataString;
    private XPlainTextProcessor mPlainTextProcessor;

    public XJSONProcessor(String str) {
        this.mPlainTextProcessor = new XPlainTextProcessor(str);
    }

    private void bufferData(Object obj) {
        if (this.mBufferedData == obj) {
            return;
        }
        this.mBufferedData = obj;
        this.mBufferedDataString = encode(obj);
    }

    private Object decode(String str) throws XHttpClient.DataConversionException {
        try {
            return new JSONObject(str);
        } catch (JSONException e) {
            throw new XHttpClient.DataConversionException("Bad JSON: " + str, e);
        }
    }

    private String encode(Object obj) {
        return obj.toString();
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public String getOutDataContentType(Object obj) {
        return MIME_TYPE;
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public int getOutDataLength(Object obj) throws XHttpClient.DataConversionException {
        bufferData(obj);
        return this.mPlainTextProcessor.getOutDataLength(this.mBufferedDataString);
    }

    @Override // miui.cloud.net.XHttpClient.IReceiveDataProcessor
    public Object processInData(Map<String, List<String>> map, InputStream inputStream) throws IOException, XHttpClient.DataConversionException {
        return decode((String) this.mPlainTextProcessor.processInData(map, inputStream));
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public void processOutData(Object obj, OutputStream outputStream) throws IOException, XHttpClient.DataConversionException {
        bufferData(obj);
        this.mPlainTextProcessor.processOutData(this.mBufferedDataString, outputStream);
    }
}
