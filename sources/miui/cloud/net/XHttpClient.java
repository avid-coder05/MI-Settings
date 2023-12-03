package miui.cloud.net;

import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import miui.cloud.common.XBlockCallback;
import miui.cloud.common.XCallback;
import miui.cloud.common.XLogger;
import miui.cloud.common.XWrapper;

/* loaded from: classes3.dex */
public final class XHttpClient {
    private static final boolean DEBUG = true;
    private static final String DEFAULT_OUT_ENCODING = "utf-8";
    private static final int DEFAULT_RUNNING_TASKS = 5;
    private static final int HTTP_STATUS_OK_CODE = 200;
    private static final int REQUEST_TIME_OUT = 30000;
    private static final int TEST_RESPONSE_DELAY = 200;
    private static final int TEST_RESPONSE_STATUS_CODE = 1024;
    private static final String TEST_RESPONSE_STATUS_MSG = "TEST OK";
    private static final String TEST_URL = "[TEST]";
    private volatile DataProcessorFactor mDataProcessorFactor = new DataProcessorFactor();
    private volatile IUserAgentNameProvider mUserAgentNameProvider = null;
    private int mMaxRuningTaskCount = 5;
    private LinkedList<HttpRequest> mPendingTasks = new LinkedList<>();
    private int mRunningTaskCount = 0;

    /* loaded from: classes3.dex */
    public static class DataConversionException extends Exception {
        DataConversionException() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public DataConversionException(String str) {
            super(str);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public DataConversionException(String str, Throwable th) {
            super(str, th);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public DataConversionException(Throwable th) {
            super(th);
        }
    }

    /* loaded from: classes3.dex */
    public static class DataProcessorFactor {
        public IReceiveDataProcessor getReceiveDataProcessor(Map<String, List<String>> map, InputStream inputStream) {
            return new XReceiveDataAutoAdaptProcessor();
        }

        public ISendDataProcessor getSendDataProcessor(String str, Object obj) {
            return new XSendDataAutoAdaptProcessor(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class HttpRequest implements Runnable {
        private XCallback<IResponseHandler> mCallback;
        private Object mCtx;
        private Object mData;
        private Map<String, List<String>> mHeader;
        private String mMethod;
        private IReceiveDataProcessor mReceiveDataProcessor;
        private HttpResponse mResponse = new HttpResponse();
        private ISendDataProcessor mSendDataProcessor;
        private String mUrl;

        public HttpRequest(String str, String str2, Map<String, List<String>> map, Object obj, ISendDataProcessor iSendDataProcessor, IReceiveDataProcessor iReceiveDataProcessor, XCallback<IResponseHandler> xCallback, Object obj2) {
            this.mMethod = str;
            this.mUrl = str2;
            this.mHeader = map;
            this.mData = obj;
            this.mSendDataProcessor = iSendDataProcessor;
            this.mReceiveDataProcessor = iReceiveDataProcessor;
            this.mCallback = xCallback;
            this.mCtx = obj2;
        }

        private void prepareConn(URLConnection uRLConnection) {
            uRLConnection.setConnectTimeout(XHttpClient.REQUEST_TIME_OUT);
            uRLConnection.setReadTimeout(XHttpClient.REQUEST_TIME_OUT);
            if (XHttpClient.this.mUserAgentNameProvider != null) {
                String userAgent = XHttpClient.this.mUserAgentNameProvider.getUserAgent();
                if (TextUtils.isEmpty(userAgent)) {
                    return;
                }
                uRLConnection.setRequestProperty("User-Agent", userAgent);
            }
        }

        private void setRequestHeader(HttpURLConnection httpURLConnection, Map<String, List<String>> map) {
            if (map == null) {
                return;
            }
            for (String str : map.keySet()) {
                httpURLConnection.setRequestProperty(str, TextUtils.join(", ", map.get(str).toArray(new String[0])));
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:41:0x00fc, code lost:
        
            if (r0 != null) goto L73;
         */
        /* JADX WARN: Code restructure failed: missing block: B:58:0x012f, code lost:
        
            if (r0 == null) goto L74;
         */
        /* JADX WARN: Code restructure failed: missing block: B:65:0x014b, code lost:
        
            if (r0 == null) goto L74;
         */
        /* JADX WARN: Code restructure failed: missing block: B:72:0x0167, code lost:
        
            if (r0 == null) goto L74;
         */
        /* JADX WARN: Code restructure failed: missing block: B:73:0x0169, code lost:
        
            r0.asInterface().handleHttpResponse(r13.mResponse);
         */
        /* JADX WARN: Code restructure failed: missing block: B:74:0x0174, code lost:
        
            r4 = java.lang.System.currentTimeMillis() - r2;
         */
        /* JADX WARN: Code restructure failed: missing block: B:75:0x017e, code lost:
        
            if (r13.mResponse.error != null) goto L77;
         */
        /* JADX WARN: Code restructure failed: missing block: B:76:0x0180, code lost:
        
            r10 = com.xiaomi.micloudsdk.stat.MiCloudNetEventStatInjector.getInstance();
            r1 = r13.mUrl;
            r13 = r13.mResponse;
            r10.addNetSuccessEvent(new com.xiaomi.micloudsdk.stat.NetSuccessStatParam(r1, r2, r4, r13.contentLength, r13.stateCode, 0));
         */
        /* JADX WARN: Code restructure failed: missing block: B:77:0x0197, code lost:
        
            com.xiaomi.micloudsdk.stat.MiCloudNetEventStatInjector.getInstance().addNetFailedEvent(new com.xiaomi.micloudsdk.stat.NetFailedStatParam(r13.mUrl, r2, r4, r13.mResponse.error, 0));
         */
        /* JADX WARN: Code restructure failed: missing block: B:78:0x01ab, code lost:
        
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:94:?, code lost:
        
            return;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void doHttpRequest() {
            /*
                Method dump skipped, instructions count: 455
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: miui.cloud.net.XHttpClient.HttpRequest.doHttpRequest():void");
        }

        @Override // java.lang.Runnable
        public void run() {
            doHttpRequest();
            XHttpClient.this.finishTask(this);
        }
    }

    /* loaded from: classes3.dex */
    public static class HttpResponse {
        public Object content;
        public long contentLength;
        public Object ctx;
        public Date date;
        public Exception error;
        public Map<String, List<String>> headers;
        public int stateCode;
        public String stateMessage;

        public String toString() {
            if (this.error != null) {
                return "Error: \n" + this.error.toString() + "\n";
            }
            Object[] objArr = new Object[5];
            objArr[0] = Integer.valueOf(this.stateCode);
            objArr[1] = this.stateMessage;
            objArr[2] = this.headers;
            Object obj = this.content;
            objArr[3] = obj == null ? null : obj.getClass();
            objArr[4] = this.content;
            return String.format("%s %s \n%s \n%s:%s", objArr);
        }
    }

    /* loaded from: classes3.dex */
    public interface IReceiveDataProcessor {
        Object processInData(Map<String, List<String>> map, InputStream inputStream) throws IOException, DataConversionException;
    }

    /* loaded from: classes3.dex */
    public interface IResponseHandler {
        void handleHttpResponse(HttpResponse httpResponse);
    }

    /* loaded from: classes3.dex */
    public interface ISendDataProcessor {
        String getOutDataContentType(Object obj) throws DataConversionException;

        int getOutDataLength(Object obj) throws DataConversionException;

        void processOutData(Object obj, OutputStream outputStream) throws IOException, DataConversionException;
    }

    /* loaded from: classes3.dex */
    public interface IUserAgentNameProvider {
        String getUserAgent();
    }

    private synchronized void addTask(HttpRequest httpRequest) {
        this.mPendingTasks.add(httpRequest);
        scheduleTasksLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void finishTask(HttpRequest httpRequest) {
        int i = this.mRunningTaskCount - 1;
        this.mRunningTaskCount = i;
        XLogger.log("Task--", Integer.valueOf(i));
        scheduleTasksLocked();
    }

    private void scheduleTasksLocked() {
        if (this.mRunningTaskCount >= this.mMaxRuningTaskCount || this.mPendingTasks.isEmpty()) {
            return;
        }
        while (this.mRunningTaskCount < this.mMaxRuningTaskCount && !this.mPendingTasks.isEmpty()) {
            new Thread(this.mPendingTasks.getFirst()).start();
            this.mPendingTasks.removeFirst();
            this.mRunningTaskCount++;
        }
        XLogger.log("task++", Integer.valueOf(this.mRunningTaskCount));
    }

    public void asyncGet(String str, Map<String, List<String>> map, XCallback<IResponseHandler> xCallback, Object obj) {
        asyncSend("GET", str, map, null, null, null, xCallback, obj);
    }

    public void asyncGet(String str, XCallback<IResponseHandler> xCallback, Object obj) {
        asyncSend("GET", str, null, null, null, null, xCallback, obj);
    }

    public void asyncPost(String str, Object obj, String str2, XCallback<IResponseHandler> xCallback, Object obj2) {
        asyncSend("POST", str, null, obj, this.mDataProcessorFactor.getSendDataProcessor(str2, obj), null, xCallback, obj2);
    }

    public void asyncPost(String str, Object obj, XCallback<IResponseHandler> xCallback, Object obj2) {
        asyncSend("POST", str, null, obj, this.mDataProcessorFactor.getSendDataProcessor(DEFAULT_OUT_ENCODING, obj), null, xCallback, obj2);
    }

    public void asyncPost(String str, Map<String, List<String>> map, Object obj, String str2, XCallback<IResponseHandler> xCallback, Object obj2) {
        asyncSend("POST", str, map, obj, this.mDataProcessorFactor.getSendDataProcessor(str2, obj), null, xCallback, obj2);
    }

    public void asyncPost(String str, Map<String, List<String>> map, Object obj, XCallback<IResponseHandler> xCallback, Object obj2) {
        asyncSend("POST", str, map, obj, this.mDataProcessorFactor.getSendDataProcessor(DEFAULT_OUT_ENCODING, obj), null, xCallback, obj2);
    }

    public void asyncSend(String str, String str2, Map<String, List<String>> map, Object obj, ISendDataProcessor iSendDataProcessor, IReceiveDataProcessor iReceiveDataProcessor, XCallback<IResponseHandler> xCallback, Object obj2) {
        addTask(new HttpRequest(str, str2, map, obj, iSendDataProcessor, iReceiveDataProcessor, xCallback, obj2));
    }

    public void setDataProcessorFactor(DataProcessorFactor dataProcessorFactor) {
        Objects.requireNonNull(dataProcessorFactor);
        this.mDataProcessorFactor = dataProcessorFactor;
    }

    public synchronized void setMaxRunningTasks(int i) {
        this.mMaxRuningTaskCount = i;
        scheduleTasksLocked();
    }

    public void setUserAgentNameProvider(IUserAgentNameProvider iUserAgentNameProvider) {
        this.mUserAgentNameProvider = iUserAgentNameProvider;
    }

    public HttpResponse syncGet(String str) throws InterruptedException {
        return syncSend("GET", str, null, null, null, null);
    }

    public HttpResponse syncGet(String str, Map<String, List<String>> map) throws InterruptedException {
        return syncSend("GET", str, map, null, null, null);
    }

    public HttpResponse syncPost(String str, Object obj) throws InterruptedException {
        return syncSend("POST", str, null, obj, this.mDataProcessorFactor.getSendDataProcessor(DEFAULT_OUT_ENCODING, obj), null);
    }

    public HttpResponse syncPost(String str, Object obj, String str2) throws InterruptedException {
        return syncSend("POST", str, null, obj, this.mDataProcessorFactor.getSendDataProcessor(str2, obj), null);
    }

    public HttpResponse syncPost(String str, Map<String, List<String>> map, Object obj) throws InterruptedException {
        return syncSend("POST", str, map, obj, this.mDataProcessorFactor.getSendDataProcessor(DEFAULT_OUT_ENCODING, obj), null);
    }

    public HttpResponse syncPost(String str, Map<String, List<String>> map, Object obj, String str2) throws InterruptedException {
        return syncSend("POST", str, map, obj, this.mDataProcessorFactor.getSendDataProcessor(str2, obj), null);
    }

    public HttpResponse syncSend(String str, String str2, Map<String, List<String>> map, Object obj, ISendDataProcessor iSendDataProcessor, IReceiveDataProcessor iReceiveDataProcessor) throws InterruptedException {
        final XWrapper xWrapper = new XWrapper();
        XBlockCallback xBlockCallback = new XBlockCallback(IResponseHandler.class);
        asyncSend(str, str2, map, obj, iSendDataProcessor, iReceiveDataProcessor, xBlockCallback, null);
        xBlockCallback.waitForCallBack(new IResponseHandler() { // from class: miui.cloud.net.XHttpClient.1
            @Override // miui.cloud.net.XHttpClient.IResponseHandler
            public void handleHttpResponse(HttpResponse httpResponse) {
                xWrapper.set(httpResponse);
            }
        });
        return (HttpResponse) xWrapper.get();
    }
}
