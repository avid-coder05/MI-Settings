package miui.cloud.net;

import miui.cloud.net.XHttpClient;

/* loaded from: classes3.dex */
public abstract class XAutoAdaptProcessor {
    protected static final DataProcessorCreator[] AVALIABLE_PROCESSOR_CREATORS = {XByteArrayProcessor.CREATOR, XPlainTextProcessor.CREATOR, XUrlencodedProcessor.CREATOR, XJSONProcessor.CREATOR};

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public interface DataProcessorCreator {
        XHttpClient.IReceiveDataProcessor getInstanceIfAbleToProcessInData(String str, String str2);

        XHttpClient.ISendDataProcessor getInstanceIfAbleToProcessOutData(Object obj, String str);
    }
}
