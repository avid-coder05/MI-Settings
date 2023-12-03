package miui.content.res;

import android.util.Log;
import com.android.internal.util.XmlUtils;
import com.miui.internal.content.res.ThemeDefinition;
import com.miui.internal.content.res.ThemeToolUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import miui.util.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: classes3.dex */
public class ThemeCompatibilityLoader {
    private static final String COMPATIBILITY_FILE_NAME = "theme_compatibility.xml";
    public static final String DATA_THEME_COMPATIBILITY_PATH = "/data/system/theme_config/theme_compatibility.xml";
    public static final String SYSTEM_THEME_COMPATIBILITY_PATH = "/system/media/theme/theme_compatibility.xml";
    private static final String TAG = "ThemeCompatibility";
    private static final String TAG_ITEM = "item";
    private static final String TAG_PACKAGE = "package";
    private static final String TAG_PROPERTY1 = "property1";
    private static final String TAG_PROPERTY2 = "property2";
    private static final String TAG_PROPERTY3 = "property3";
    private static final String TAG_PROPERTYEXTRA = "propertyExtra";
    private static final String TAG_RESOURCE_TYPE = "resourceType";
    private static final String TAG_VERSION = "version";
    private static int sVersionInt = -1;

    private static Document getConfigDocumentTree() {
        BufferedInputStream bufferedInputStream;
        Throwable th;
        Exception e;
        int version = getVersion(DATA_THEME_COMPATIBILITY_PATH);
        int version2 = getVersion(SYSTEM_THEME_COMPATIBILITY_PATH);
        String[] strArr = new String[2];
        if (version > version2) {
            strArr[0] = DATA_THEME_COMPATIBILITY_PATH;
            strArr[1] = SYSTEM_THEME_COMPATIBILITY_PATH;
        } else {
            strArr[0] = SYSTEM_THEME_COMPATIBILITY_PATH;
        }
        Log.d(TAG, "getConfigDocumentTree(): " + version2 + " vs " + version);
        for (int i = 0; i < 2; i++) {
            String str = strArr[i];
            try {
                Log.d(TAG, "    parse file: " + str);
                DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                bufferedInputStream = new BufferedInputStream(new FileInputStream(str), 8192);
                try {
                    try {
                        Document parse = newDocumentBuilder.parse(bufferedInputStream);
                        IOUtils.closeQuietly(bufferedInputStream);
                        return parse;
                    } catch (Exception e2) {
                        e = e2;
                        Log.d(TAG, "    invalid file format: " + str + " -- " + e.toString());
                        e.printStackTrace();
                        IOUtils.closeQuietly(bufferedInputStream);
                    }
                } catch (Throwable th2) {
                    th = th2;
                    IOUtils.closeQuietly(bufferedInputStream);
                    throw th;
                }
            } catch (Exception e3) {
                bufferedInputStream = null;
                e = e3;
            } catch (Throwable th3) {
                bufferedInputStream = null;
                th = th3;
            }
        }
        return null;
    }

    public static int getVersion(String str) {
        BufferedReader bufferedReader;
        Exception e;
        BufferedReader bufferedReader2 = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(str));
            while (true) {
                try {
                    try {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        String trim = readLine.trim();
                        if (trim.startsWith("<version>") && trim.endsWith("</version>")) {
                            int parseInt = Integer.parseInt(trim.substring(9, trim.length() - 10));
                            IOUtils.closeQuietly(bufferedReader);
                            return parseInt;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        Log.d(TAG, "getVersion(): " + str + "  " + e.toString());
                        e.printStackTrace();
                        IOUtils.closeQuietly(bufferedReader);
                        return -1;
                    }
                } catch (Throwable th) {
                    th = th;
                    bufferedReader2 = bufferedReader;
                    IOUtils.closeQuietly(bufferedReader2);
                    throw th;
                }
            }
        } catch (Exception e3) {
            bufferedReader = null;
            e = e3;
        } catch (Throwable th2) {
            th = th2;
            IOUtils.closeQuietly(bufferedReader2);
            throw th;
        }
        IOUtils.closeQuietly(bufferedReader);
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<ThemeDefinition.CompatibilityInfo> loadConfig() {
        NodeList elementsByTagName;
        ArrayList arrayList = new ArrayList();
        Log.d(TAG, "START loading theme compatibility config.");
        try {
            Document configDocumentTree = getConfigDocumentTree();
            if (configDocumentTree != null) {
                NodeList childNodes = configDocumentTree.getDocumentElement().getChildNodes();
                for (int length = childNodes.getLength() - 1; length >= 0; length--) {
                    Node item = childNodes.item(length);
                    if (item.getNodeType() == 1) {
                        Element element = (Element) item;
                        String nodeName = element.getNodeName();
                        if ("version".equals(nodeName)) {
                            int convertValueToUnsignedInt = XmlUtils.convertValueToUnsignedInt(element.getTextContent(), -1);
                            sVersionInt = convertValueToUnsignedInt;
                            if (convertValueToUnsignedInt < 0) {
                                break;
                            }
                        } else {
                            ThemeDefinition.CompatibilityType type = ThemeDefinition.CompatibilityType.getType(nodeName);
                            if (type != ThemeDefinition.CompatibilityType.NONE && (elementsByTagName = element.getElementsByTagName("item")) != null) {
                                int length2 = elementsByTagName.getLength();
                                for (int i = 0; i < length2; i++) {
                                    ThemeDefinition.CompatibilityInfo parseCompatibilityInfo = parseCompatibilityInfo(type, elementsByTagName.item(i));
                                    if (parseCompatibilityInfo != null) {
                                        arrayList.add(parseCompatibilityInfo);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Invalid item format: " + e.toString());
            e.printStackTrace();
        }
        if (sVersionInt < 0) {
            arrayList.clear();
        }
        Log.d(TAG, "END loading: version=" + sVersionInt + " size=" + arrayList.size());
        return arrayList;
    }

    private static ThemeDefinition.CompatibilityInfo parseCompatibilityInfo(ThemeDefinition.CompatibilityType compatibilityType, Node node) {
        ThemeDefinition.FallbackInfo fallbackInfo;
        NamedNodeMap attributes = node.getAttributes();
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        for (int length = attributes.getLength() - 1; length >= 0; length--) {
            Node item = attributes.item(length);
            if ("package".equals(item.getNodeName())) {
                str = item.getNodeValue();
            } else if (TAG_RESOURCE_TYPE.equals(item.getNodeName())) {
                str2 = item.getNodeValue();
            } else if (TAG_PROPERTY1.equals(item.getNodeName())) {
                str3 = item.getNodeValue();
            } else if (TAG_PROPERTY2.equals(item.getNodeName())) {
                str4 = item.getNodeValue();
            } else if (TAG_PROPERTY3.equals(item.getNodeName())) {
                str5 = item.getNodeValue();
            } else if (TAG_PROPERTYEXTRA.equals(item.getNodeName())) {
                str6 = item.getNodeValue();
            }
        }
        if (compatibilityType == ThemeDefinition.CompatibilityType.FALLBACK) {
            fallbackInfo = new ThemeDefinition.FallbackInfo();
            fallbackInfo.mResPkgName = str;
            fallbackInfo.mResType = ThemeDefinition.ResourceType.getType(str2);
            fallbackInfo.mResOriginalName = str3;
            fallbackInfo.mResFallbackName = str4;
            if (ThemeToolUtils.isEmpty(str5) || str5.equals(str)) {
                str5 = null;
            }
            fallbackInfo.mResFallbackPkgName = str5;
            List<String> splitItemString = splitItemString(str6);
            if (!splitItemString.isEmpty()) {
                int min = Math.min(5, splitItemString.size());
                String[] strArr = new String[min];
                for (int i = 0; i < min; i++) {
                    strArr[i] = splitItemString.get(i);
                }
                fallbackInfo.mResPreferredConfigs = strArr;
            }
        } else if (compatibilityType == ThemeDefinition.CompatibilityType.NEW_DEF_VALUE) {
            fallbackInfo = new ThemeDefinition.NewDefaultValue();
            ((ThemeDefinition.NewDefaultValue) fallbackInfo).mResPkgName = str;
            ((ThemeDefinition.NewDefaultValue) fallbackInfo).mResType = ThemeDefinition.ResourceType.getType(str2);
            ((ThemeDefinition.NewDefaultValue) fallbackInfo).mResName = str3;
            ((ThemeDefinition.NewDefaultValue) fallbackInfo).mResValue = str4;
        } else {
            fallbackInfo = null;
        }
        if (fallbackInfo == null || fallbackInfo.isValid()) {
            return fallbackInfo;
        }
        Log.d(TAG, "Invalid compatibility info: " + fallbackInfo.toString());
        return null;
    }

    private static List<String> splitItemString(String str) {
        ArrayList arrayList = new ArrayList();
        String trim = str != null ? str.trim() : null;
        if (trim != null && !trim.startsWith("#")) {
            for (String str2 : trim.split(" |\t")) {
                if (str2 != null) {
                    String trim2 = str2.trim();
                    if (!trim2.isEmpty()) {
                        arrayList.add(trim2);
                    }
                }
            }
        }
        return arrayList;
    }
}
