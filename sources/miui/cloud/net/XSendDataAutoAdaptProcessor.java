package miui.cloud.net;

import java.io.IOException;
import java.io.OutputStream;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;

/* loaded from: classes3.dex */
public class XSendDataAutoAdaptProcessor extends XAutoAdaptProcessor implements XHttpClient.ISendDataProcessor {
    private Object mBufferedData = null;
    private XHttpClient.ISendDataProcessor mBufferedProcessor = null;
    private String mEncode;

    public XSendDataAutoAdaptProcessor(String str) {
        this.mEncode = null;
        this.mEncode = str;
    }

    private void bufferData(Object obj) {
        if (this.mBufferedData == obj) {
            return;
        }
        this.mBufferedData = obj;
        for (XAutoAdaptProcessor.DataProcessorCreator dataProcessorCreator : XAutoAdaptProcessor.AVALIABLE_PROCESSOR_CREATORS) {
            XHttpClient.ISendDataProcessor instanceIfAbleToProcessOutData = dataProcessorCreator.getInstanceIfAbleToProcessOutData(obj, this.mEncode);
            if (instanceIfAbleToProcessOutData != null) {
                this.mBufferedProcessor = instanceIfAbleToProcessOutData;
                return;
            }
        }
        this.mBufferedProcessor = new XPlainTextProcessor(this.mEncode);
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public String getOutDataContentType(Object obj) throws XHttpClient.DataConversionException {
        bufferData(obj);
        return this.mBufferedProcessor.getOutDataContentType(this.mBufferedData);
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public int getOutDataLength(Object obj) throws XHttpClient.DataConversionException {
        bufferData(obj);
        return this.mBufferedProcessor.getOutDataLength(this.mBufferedData);
    }

    @Override // miui.cloud.net.XHttpClient.ISendDataProcessor
    public void processOutData(Object obj, OutputStream outputStream) throws IOException, XHttpClient.DataConversionException {
        bufferData(obj);
        this.mBufferedProcessor.processOutData(this.mBufferedData, outputStream);
    }
}
