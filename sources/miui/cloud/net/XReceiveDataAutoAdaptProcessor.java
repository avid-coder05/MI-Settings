package miui.cloud.net;

import android.text.TextUtils;
import android.util.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import miui.cloud.net.XAutoAdaptProcessor;
import miui.cloud.net.XHttpClient;
import miui.provider.ExtraContacts;

/* loaded from: classes3.dex */
public class XReceiveDataAutoAdaptProcessor extends XAutoAdaptProcessor implements XHttpClient.IReceiveDataProcessor {
    private Pair<String, String> getContentTypeAndEncodeFromHeader(Map<String, List<String>> map) {
        String str;
        List<String> list = map.get("Content-Type");
        String str2 = "utf-8";
        if (list == null || list.isEmpty()) {
            str = "text/plain";
        } else {
            String[] split = TextUtils.split(list.get(0), ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION);
            str = split[0].trim();
            if (split.length > 1) {
                String[] split2 = TextUtils.split(split[1], "=");
                if (split2.length > 1) {
                    str2 = split2[1].trim();
                }
            }
        }
        return new Pair<>(str, str2);
    }

    @Override // miui.cloud.net.XHttpClient.IReceiveDataProcessor
    public Object processInData(Map<String, List<String>> map, InputStream inputStream) throws IOException, XHttpClient.DataConversionException {
        Pair<String, String> contentTypeAndEncodeFromHeader = getContentTypeAndEncodeFromHeader(map);
        for (XAutoAdaptProcessor.DataProcessorCreator dataProcessorCreator : XAutoAdaptProcessor.AVALIABLE_PROCESSOR_CREATORS) {
            XHttpClient.IReceiveDataProcessor instanceIfAbleToProcessInData = dataProcessorCreator.getInstanceIfAbleToProcessInData((String) contentTypeAndEncodeFromHeader.first, (String) contentTypeAndEncodeFromHeader.second);
            if (instanceIfAbleToProcessInData != null) {
                return instanceIfAbleToProcessInData.processInData(map, inputStream);
            }
        }
        return new XPlainTextProcessor((String) contentTypeAndEncodeFromHeader.second).processInData(map, inputStream);
    }
}
