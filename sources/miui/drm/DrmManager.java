package miui.drm;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import miui.content.res.ThemeResources;
import miui.telephony.TelephonyManagerUtil;
import miui.util.HashUtils;
import miui.util.RSAUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* loaded from: classes3.dex */
public class DrmManager {
    private static final String ASSET_XPATH = "/o-ex:rights/o-ex:agreement/o-ex:asset/o-ex:context/o-dd:uid";
    private static final String DISPLAY_COUNT_XPATH = "/o-ex:rights/o-ex:agreement/o-ex:permission/o-dd:display/o-ex:constraint/o-dd:count";
    private static final String IMEI_EVERYONE = "-1";
    private static final String IMEI_PREFIX = "d";
    private static final String INDIVIDUAL_XPATH = "/o-ex:rights/o-ex:agreement/o-ex:permission/o-dd:execute/o-ex:constraint/oma-dd:individual/o-ex:context/o-dd:uid";
    private static final String ITEM_SEPARATOR = ",";
    private static final String O_EX_ID_CATEGORY = "o-ex:id";
    private static final String PAIR_SEPARATOR = ":";
    private static final String PUBLIC_KEY_E = "10001";
    private static final String PUBLIC_KEY_M = "a2ebd07cfae9a72345fc3c95d80cf5a21a55bf553fbab3025c82747ba4d53d1f9b02f46c20b5520585a910732698b165f0ecf7bd9ce5402e27c646cd0c5d34cff92b184d6a477e156a7d3503b756cc3e8531fb26c0da0ca051ab531c7f9f2a040a06642cadb698882c048630030b73edbbd62da73f7027065443c6e2558edfbd";
    private static final String SUPPORT_AD = "support_ad";
    public static final String TAG = "drm";
    private static final String TIME_END_XPATH = "/o-ex:rights/o-ex:agreement/o-ex:permission/o-dd:execute/o-ex:constraint/o-dd:datetime/o-dd:end";
    private static final String TIME_START_XPATH = "/o-ex:rights/o-ex:agreement/o-ex:permission/o-dd:execute/o-ex:constraint/o-dd:datetime/o-dd:start";
    private static final String USER_EVERYONE = "-1";
    private static final String USER_PREFIX = "m";
    private static Map<String, RightObjectCache> mRightsCache = new LinkedHashMap<String, RightObjectCache>(0, 0.75f, true) { // from class: miui.drm.DrmManager.1
        private static final long serialVersionUID = 1;

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<String, RightObjectCache> entry) {
            return size() > 50;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class DrmNSContext implements NamespaceContext {
        private DrmNSContext() {
        }

        @Override // javax.xml.namespace.NamespaceContext
        public String getNamespaceURI(String str) {
            if (str.equals("o-ex")) {
                return "http://odrl.net/1.1/ODRL-EX";
            }
            if (str.equals("o-dd")) {
                return "http://odrl.net/1.1/ODRL-DD";
            }
            if (str.equals("oma-dd")) {
                return "http://www.openmobilealliance.com/oma-dd";
            }
            return null;
        }

        @Override // javax.xml.namespace.NamespaceContext
        public String getPrefix(String str) {
            return null;
        }

        @Override // javax.xml.namespace.NamespaceContext
        public Iterator getPrefixes(String str) {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public enum DrmResult {
        DRM_SUCCESS,
        DRM_ERROR_IMEI_NOT_MATCH,
        DRM_ERROR_ASSET_NOT_MATCH,
        DRM_ERROR_TIME_NOT_MATCH,
        DRM_ERROR_RIGHT_OBJECT_IS_NULL,
        DRM_ERROR_RIGHT_FILE_NOT_EXISTS,
        DRM_ERROR_UNKNOWN
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class RightObject {
        public boolean adSupport;
        public List<String> assets;
        public long endTime;
        public List<String> imeis;
        public long startTime;
        public List<String> users;

        private RightObject() {
            this.assets = new ArrayList();
            this.imeis = new ArrayList();
            this.users = new ArrayList();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class RightObjectCache {
        public long lastModified;
        public RightObject ro;

        private RightObjectCache() {
        }
    }

    /* loaded from: classes3.dex */
    public static class TrialLimits {
        public long endTime;
        public long startTime;

        TrialLimits(long j, long j2) {
            this.startTime = j;
            this.endTime = j2;
        }
    }

    private static byte[] convertHexStringToBytes(String str) {
        byte[] bArr = new byte[str.length() / 2];
        int i = 0;
        while (i < str.length()) {
            int i2 = i + 2;
            bArr[i / 2] = (byte) Integer.parseInt(str.substring(i, i2), 16);
            i = i2;
        }
        return bArr;
    }

    public static void exportFatalLog(String str, String str2) {
        BufferedWriter bufferedWriter;
        Log.e(str, str2);
        BufferedWriter bufferedWriter2 = null;
        try {
            try {
                try {
                    File file = new File(ThemeResources.THEME_MAGIC_PATH + "drm.log");
                    if (file.length() > 102400) {
                        Log.i(str, "recreate log file " + file.getAbsolutePath());
                        file.delete();
                    }
                    if (!file.exists()) {
                        Log.i(str, "create log file " + file.getAbsolutePath());
                        file.createNewFile();
                    }
                    Log.i(str, "export error message into " + file.getAbsolutePath());
                    bufferedWriter = new BufferedWriter(new FileWriter(file, true));
                } catch (IOException e) {
                    e = e;
                }
            } catch (Throwable th) {
                th = th;
            }
            try {
                bufferedWriter.append((CharSequence) (getContextInfo() + " " + System.currentTimeMillis() + " " + str + " " + str2));
                bufferedWriter.newLine();
                bufferedWriter.close();
            } catch (IOException e2) {
                e = e2;
                bufferedWriter2 = bufferedWriter;
                e.printStackTrace();
                if (bufferedWriter2 != null) {
                    bufferedWriter2.close();
                }
            } catch (Throwable th2) {
                th = th2;
                bufferedWriter2 = bufferedWriter;
                if (bufferedWriter2 != null) {
                    try {
                        bufferedWriter2.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (IOException e4) {
            e4.printStackTrace();
        }
    }

    private static String getContextInfo() {
        return String.format("%s %s_%s %s", Build.DEVICE, Build.VERSION.RELEASE, Build.VERSION.INCREMENTAL, DateFormat.getDateTimeInstance().format(new Date()));
    }

    public static String getEncodedImei(Context context) {
        String originImei = getOriginImei(context);
        return TextUtils.isEmpty(originImei) ? "" : HashUtils.getMD5(originImei);
    }

    public static DrmResult getMorePreciseDrmResult(DrmResult drmResult, DrmResult drmResult2) {
        DrmResult drmResult3 = DrmResult.DRM_ERROR_TIME_NOT_MATCH;
        return ((drmResult != drmResult3 || drmResult2 == DrmResult.DRM_SUCCESS) && (drmResult == DrmResult.DRM_SUCCESS || drmResult2 != drmResult3)) ? drmResult.compareTo(drmResult2) < 0 ? drmResult : drmResult2 : drmResult3;
    }

    private static String getOriginImei(Context context) {
        String deviceId = TelephonyManagerUtil.getDeviceId();
        return TextUtils.isEmpty(deviceId) ? "" : deviceId;
    }

    private static long getTime(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public static TrialLimits getTrialLimits(File file) {
        RightObject parseRightsFile = parseRightsFile(file);
        if (parseRightsFile != null) {
            return new TrialLimits(parseRightsFile.startTime, parseRightsFile.endTime);
        }
        return null;
    }

    public static String getVAID(Context context) {
        if (Build.VERSION.SDK_INT <= 28) {
            return null;
        }
        try {
            return HashUtils.getMD5((String) Class.forName("com.android.id.IdentifierManager").getMethod("getVAID", Context.class).invoke(null, context));
        } catch (Exception e) {
            Log.e(TAG, "getVAID hanppens exception e = " + e);
            return null;
        }
    }

    public static DrmResult isLegal(Context context, File file, File file2) {
        return isLegal(context, HashUtils.getSHA1(file), file2);
    }

    public static DrmResult isLegal(Context context, String str, File file) {
        if (!file.exists() || (file.isDirectory() && file.listFiles() == null)) {
            return DrmResult.DRM_ERROR_RIGHT_FILE_NOT_EXISTS;
        }
        DrmResult drmResult = DrmResult.DRM_ERROR_UNKNOWN;
        if (!file.isDirectory()) {
            Log.d(TAG, "checking asset " + str + " with " + file.getAbsolutePath());
            return isLegal(context, str, parseRightsFile(file));
        }
        for (File file2 : file.listFiles()) {
            Log.d(TAG, "checking asset " + str + " with " + file2.getAbsolutePath());
            DrmResult isLegal = isLegal(context, str, parseRightsFile(file2));
            DrmResult drmResult2 = DrmResult.DRM_SUCCESS;
            if (isLegal == drmResult2) {
                return drmResult2;
            }
            drmResult = getMorePreciseDrmResult(drmResult, isLegal);
        }
        return drmResult;
    }

    private static DrmResult isLegal(Context context, String str, RightObject rightObject) {
        boolean z;
        boolean z2;
        if (rightObject == null) {
            return DrmResult.DRM_ERROR_RIGHT_OBJECT_IS_NULL;
        }
        Iterator<String> it = rightObject.assets.iterator();
        while (true) {
            z = true;
            if (!it.hasNext()) {
                z2 = false;
                break;
            } else if (it.next().equals(str)) {
                z2 = true;
                break;
            }
        }
        if (!z2) {
            exportFatalLog(TAG, "right object has no definition for asset " + str);
            return DrmResult.DRM_ERROR_ASSET_NOT_MATCH;
        }
        if (rightObject.imeis.size() == 0) {
            Log.d(TAG, "right object does not have any imeis");
        } else {
            String originImei = getOriginImei(context);
            String encodedImei = getEncodedImei(context);
            String vaid = getVAID(context);
            if (TextUtils.isEmpty(originImei)) {
                exportFatalLog(TAG, "the imei retrieved is empty");
            } else if (TextUtils.isEmpty(encodedImei)) {
                exportFatalLog(TAG, "the imei encoded is empty");
            }
            for (String str2 : rightObject.imeis) {
                if (str2.equals(originImei) || str2.equals(encodedImei) || str2.equals(vaid) || str2.equals("-1")) {
                    Log.d(TAG, "right object has matched imei");
                    break;
                }
            }
            z = false;
            if (!z) {
                exportFatalLog(TAG, "right object does not have matched imei");
                return DrmResult.DRM_ERROR_IMEI_NOT_MATCH;
            }
        }
        if (rightObject.startTime >= 0) {
            long j = rightObject.endTime;
            if (j >= 0) {
                if (j > 0) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis < rightObject.startTime || currentTimeMillis > rightObject.endTime) {
                        return DrmResult.DRM_ERROR_TIME_NOT_MATCH;
                    }
                }
                return DrmResult.DRM_SUCCESS;
            }
        }
        return DrmResult.DRM_ERROR_TIME_NOT_MATCH;
    }

    public static boolean isPermanentRights(File file) {
        return isPermanentRights(parseRightsFile(file));
    }

    private static boolean isPermanentRights(RightObject rightObject) {
        return rightObject != null && rightObject.startTime == 0 && rightObject.endTime == 0;
    }

    private static boolean isRightsFileLegal(File file) {
        try {
            Element documentElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getDocumentElement();
            String attribute = documentElement.getAttribute(O_EX_ID_CATEGORY);
            NodeList childNodes = documentElement.getChildNodes();
            String str = "";
            for (int i = 0; i < childNodes.getLength(); i++) {
                str = str + nodeToString(childNodes.item(i));
            }
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(attribute)) {
                byte[] bytes = str.getBytes();
                byte[] convertHexStringToBytes = convertHexStringToBytes(attribute);
                if (RSAUtils.verify(bytes, RSAUtils.getPublicKey(PUBLIC_KEY_M, PUBLIC_KEY_E), convertHexStringToBytes)) {
                    Log.i(TAG, "standard format rights file verify is legal");
                    return true;
                }
                boolean verify = RSAUtils.verify(str.replaceAll("/>", " />").getBytes(), RSAUtils.getPublicKey(PUBLIC_KEY_M, PUBLIC_KEY_E), convertHexStringToBytes);
                Log.i(TAG, "old format rights file verify result : " + verify);
                return verify;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
        } catch (ParserConfigurationException e3) {
            e3.printStackTrace();
        } catch (XPathExpressionException e4) {
            e4.printStackTrace();
        } catch (SAXException e5) {
            e5.printStackTrace();
        } catch (Exception e6) {
            e6.printStackTrace();
        }
        return false;
    }

    public static boolean isSupportAd(Context context) {
        return Settings.System.getInt(context.getContentResolver(), SUPPORT_AD, 0) > 0;
    }

    public static boolean isSupportAd(File file) {
        if (file.exists() && file.isFile()) {
            return parseRightsFile(file).adSupport;
        }
        return false;
    }

    private static String nodeToString(Node node) {
        StringWriter stringWriter = new StringWriter();
        try {
            Transformer newTransformer = TransformerFactory.newInstance().newTransformer();
            newTransformer.setOutputProperty("omit-xml-declaration", "yes");
            newTransformer.transform(new DOMSource(node), new StreamResult(stringWriter));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    private static RightObject parseRightsFile(File file) {
        String absolutePath = file.getAbsolutePath();
        RightObjectCache rightObjectCache = mRightsCache.get(absolutePath);
        if (rightObjectCache == null || file.lastModified() != rightObjectCache.lastModified) {
            if (isRightsFileLegal(file)) {
                RightObject rightObject = new RightObject();
                RightObjectCache rightObjectCache2 = new RightObjectCache();
                rightObjectCache2.ro = rightObject;
                rightObjectCache2.lastModified = file.lastModified();
                mRightsCache.put(absolutePath, rightObjectCache2);
                try {
                    DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
                    boolean z = true;
                    newInstance.setNamespaceAware(true);
                    Document parse = newInstance.newDocumentBuilder().parse(file);
                    XPath newXPath = XPathFactory.newInstance().newXPath();
                    newXPath.setNamespaceContext(new DrmNSContext());
                    NodeList nodeList = (NodeList) newXPath.evaluate(ASSET_XPATH, parse, XPathConstants.NODESET);
                    if (nodeList != null && nodeList.getLength() > 0) {
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            String[] split = ((Element) nodeList.item(i)).getTextContent().split(PAIR_SEPARATOR);
                            if (split.length == 1) {
                                rightObject.assets.addAll(Arrays.asList(split[0].split(ITEM_SEPARATOR)));
                            } else if (split.length == 2) {
                                rightObject.assets.add(split[0]);
                                rightObject.assets.addAll(Arrays.asList(split[1].split(ITEM_SEPARATOR)));
                            }
                        }
                    }
                    NodeList nodeList2 = (NodeList) newXPath.evaluate(INDIVIDUAL_XPATH, parse, XPathConstants.NODESET);
                    if (nodeList2 != null && nodeList2.getLength() > 0) {
                        for (int i2 = 0; i2 < nodeList2.getLength(); i2++) {
                            String textContent = ((Element) nodeList2.item(i2)).getTextContent();
                            if (textContent.startsWith("d")) {
                                rightObject.imeis.add(textContent.substring(1));
                            } else if (textContent.startsWith(USER_PREFIX)) {
                                rightObject.users.add(textContent.substring(1));
                            }
                        }
                    }
                    NodeList nodeList3 = (NodeList) newXPath.evaluate(TIME_START_XPATH, parse, XPathConstants.NODESET);
                    if (nodeList3 != null && nodeList3.getLength() > 0) {
                        rightObject.startTime = getTime(((Element) nodeList3.item(0)).getTextContent());
                    }
                    NodeList nodeList4 = (NodeList) newXPath.evaluate(TIME_END_XPATH, parse, XPathConstants.NODESET);
                    if (nodeList4 != null && nodeList4.getLength() > 0) {
                        rightObject.endTime = getTime(((Element) nodeList4.item(0)).getTextContent());
                    }
                    NodeList nodeList5 = (NodeList) newXPath.evaluate(DISPLAY_COUNT_XPATH, parse, XPathConstants.NODESET);
                    if (nodeList5 != null && nodeList5.getLength() > 0) {
                        if (Integer.valueOf(((Element) nodeList5.item(0)).getTextContent()).intValue() <= 0) {
                            z = false;
                        }
                        rightObject.adSupport = z;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e2) {
                    e2.printStackTrace();
                } catch (ParserConfigurationException e3) {
                    e3.printStackTrace();
                } catch (XPathExpressionException e4) {
                    e4.printStackTrace();
                } catch (SAXException e5) {
                    e5.printStackTrace();
                }
                return rightObject;
            }
            return null;
        }
        return rightObjectCache.ro;
    }

    public static void setSupportAd(Context context, boolean z) {
        Settings.System.putInt(context.getContentResolver(), SUPPORT_AD, z ? 1 : 0);
    }
}
