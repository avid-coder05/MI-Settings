package miui.content.res;

import android.app.MiuiThemeHelper;
import android.content.res.MiuiResources;
import android.content.res.Resources;
import android.util.Log;
import com.android.internal.util.XmlUtils;
import com.miui.internal.content.res.ThemeDefinition;
import com.miui.internal.content.res.ThemeToolUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import miui.util.IOUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: classes3.dex */
public class ThemeValues {
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PACKAGE = "package";
    private static final String DIVIDER = "|";
    private static final String TAG = "ThemeValues";
    private static final String TAG_ITEM = "item";
    private static final String TRUE = "true";
    public HashMap<Integer, Integer> mIntegers = new HashMap<>();
    public HashMap<Integer, String> mStrings = new HashMap<>();
    public HashMap<Integer, int[]> mIntegerArrays = new HashMap<>();
    public HashMap<Integer, String[]> mStringArrays = new HashMap<>();

    private static int getIdentifier(Resources resources, ThemeDefinition.ResourceType resourceType, String str, String str2) {
        String resourceType2 = (resourceType == ThemeDefinition.ResourceType.INTEGER_ARRAY || resourceType == ThemeDefinition.ResourceType.STRING_ARRAY) ? "array" : resourceType.toString();
        int identifier = resources.getIdentifier(str, resourceType2, str2);
        if (identifier == 0 && ThemeResources.MIUI_PACKAGE.equals(str2)) {
            int identifier2 = resources.getIdentifier(str, resourceType2, "miui.system");
            return identifier2 == 0 ? resources.getIdentifier(str, resourceType2, "android.miui") : identifier2;
        }
        return identifier;
    }

    private static List<Integer> getIdentifierWithFallback(Resources resources, ThemeDefinition.ResourceType resourceType, String str, String str2) {
        int identifier;
        ArrayList arrayList = new ArrayList();
        int identifier2 = getIdentifier(resources, resourceType, str, str2);
        if (identifier2 > 0) {
            arrayList.add(Integer.valueOf(identifier2));
        }
        List<ThemeDefinition.FallbackInfo> fallbackList = ThemeCompatibility.getFallbackList(str2);
        if (fallbackList != null) {
            for (ThemeDefinition.FallbackInfo fallbackInfo : fallbackList) {
                if (fallbackInfo.mResType == resourceType && fallbackInfo.mResFallbackPkgName == null && str.equals(fallbackInfo.mResFallbackName) && (identifier = getIdentifier(resources, resourceType, fallbackInfo.mResOriginalName, str2)) > 0) {
                    arrayList.add(Integer.valueOf(identifier));
                }
            }
        }
        return arrayList;
    }

    private static boolean ignoreResourceValue(String str, ThemeDefinition.ResourceType resourceType, String str2) {
        if (resourceType == ThemeDefinition.ResourceType.COLOR && ThemeResources.FRAMEWORK_PACKAGE.equals(str) && str2.startsWith("statusbar_content")) {
            return isOldVersionComponentTheme(str);
        }
        return false;
    }

    private static boolean isOldVersionComponentTheme(String str) {
        return new File(ThemeResources.THEME_VERSION_COMPATIBILITY_PATH + str).exists();
    }

    private static Object parseResourceArrayValue(ThemeDefinition.ResourceType resourceType, Element element) {
        int length;
        NodeList elementsByTagName = element.getElementsByTagName("item");
        if (elementsByTagName != null && (length = elementsByTagName.getLength()) != 0) {
            ArrayList arrayList = new ArrayList(length);
            for (int i = 0; i < length; i++) {
                arrayList.add(elementsByTagName.item(i).getTextContent());
            }
            if (resourceType == ThemeDefinition.ResourceType.INTEGER_ARRAY) {
                int[] iArr = new int[length];
                for (int i2 = 0; i2 < length; i2++) {
                    iArr[i2] = Integer.valueOf((String) arrayList.get(i2)).intValue();
                }
                return iArr;
            } else if (resourceType == ThemeDefinition.ResourceType.STRING_ARRAY) {
                return arrayList.toArray(new String[length]);
            }
        }
        return null;
    }

    private static Object parseResourceNonArrayValue(ThemeDefinition.ResourceType resourceType, String str) {
        if (ThemeToolUtils.isEmpty(str)) {
            return null;
        }
        String trim = str.trim();
        if (resourceType == ThemeDefinition.ResourceType.BOOLEAN) {
            return Integer.valueOf(TRUE.equals(trim) ? 1 : 0);
        }
        if (resourceType == ThemeDefinition.ResourceType.COLOR || resourceType == ThemeDefinition.ResourceType.INTEGER || resourceType == ThemeDefinition.ResourceType.DRAWABLE) {
            return Integer.valueOf(XmlUtils.convertValueToUnsignedInt(trim, 0));
        }
        if (resourceType == ThemeDefinition.ResourceType.DIMEN) {
            return MiuiThemeHelper.parseDimension(trim);
        }
        if (resourceType == ThemeDefinition.ResourceType.STRING) {
            return trim;
        }
        return null;
    }

    public static ThemeValues parseThemeValues(MiuiResources miuiResources, InputStream inputStream, String str) {
        ThemeDefinition.ResourceType type;
        Map map;
        Object parseResourceNonArrayValue;
        ThemeValues themeValues = new ThemeValues();
        try {
            try {
                NodeList childNodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new BufferedInputStream(inputStream, 8192)).getDocumentElement().getChildNodes();
                for (int length = childNodes.getLength() - 1; length >= 0; length--) {
                    Node item = childNodes.item(length);
                    if (item.getNodeType() == 1) {
                        Element element = (Element) item;
                        String attribute = element.getAttribute("name");
                        if (!ThemeToolUtils.isEmpty(attribute) && (type = ThemeDefinition.ResourceType.getType(element.getNodeName())) != ThemeDefinition.ResourceType.NONE && !ignoreResourceValue(str, type, attribute)) {
                            String attribute2 = element.getAttribute("package");
                            if (ThemeToolUtils.isEmpty(attribute2)) {
                                attribute2 = str;
                            }
                            List<Integer> identifierWithFallback = getIdentifierWithFallback(miuiResources, type, attribute, attribute2);
                            if (!identifierWithFallback.isEmpty()) {
                                if (type == ThemeDefinition.ResourceType.INTEGER_ARRAY) {
                                    map = themeValues.mIntegerArrays;
                                    parseResourceNonArrayValue = parseResourceArrayValue(type, element);
                                } else if (type == ThemeDefinition.ResourceType.STRING_ARRAY) {
                                    map = themeValues.mStringArrays;
                                    parseResourceNonArrayValue = parseResourceArrayValue(type, element);
                                } else if (type == ThemeDefinition.ResourceType.STRING) {
                                    map = themeValues.mStrings;
                                    parseResourceNonArrayValue = parseResourceNonArrayValue(type, element.getTextContent());
                                } else {
                                    map = themeValues.mIntegers;
                                    parseResourceNonArrayValue = parseResourceNonArrayValue(type, element.getTextContent());
                                }
                                saveIdentifierMap(map, identifierWithFallback, parseResourceNonArrayValue);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return themeValues;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static void saveIdentifierMap(Map map, List list, Object obj) {
        if (obj != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                map.put(it.next(), obj);
            }
        }
    }

    public boolean isEmpty() {
        return this.mIntegers.isEmpty() && this.mStrings.isEmpty() && this.mIntegerArrays.isEmpty() && this.mStringArrays.isEmpty();
    }

    public void mergeNewDefaultValueIfNeed(MiuiResources miuiResources, String str) {
        List<ThemeDefinition.NewDefaultValue> newDefaultValueList = ThemeCompatibility.getNewDefaultValueList(str);
        if (newDefaultValueList == null) {
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (ThemeDefinition.NewDefaultValue newDefaultValue : newDefaultValueList) {
                int identifier = getIdentifier(miuiResources, newDefaultValue.mResType, newDefaultValue.mResName, str);
                if (identifier <= 0) {
                    sb.append(DIVIDER);
                    sb.append(newDefaultValue.toString());
                } else {
                    if (!this.mStrings.containsKey(Integer.valueOf(identifier)) && !this.mIntegers.containsKey(Integer.valueOf(identifier))) {
                        Object parseResourceNonArrayValue = parseResourceNonArrayValue(newDefaultValue.mResType, newDefaultValue.mResValue);
                        if (parseResourceNonArrayValue != null) {
                            if (newDefaultValue.mResType == ThemeDefinition.ResourceType.STRING) {
                                this.mStrings.put(Integer.valueOf(identifier), (String) parseResourceNonArrayValue);
                            } else {
                                this.mIntegers.put(Integer.valueOf(identifier), (Integer) parseResourceNonArrayValue);
                            }
                        }
                    }
                    sb2.append(DIVIDER);
                    sb2.append(newDefaultValue.toString());
                }
            }
            if (sb.length() != 0) {
                Log.d(TAG, "can not find newDefValue: " + sb.toString());
            }
            if (sb2.length() != 0) {
                Log.d(TAG, "customized theme has contain this value: " + sb2.toString());
            }
        } catch (Exception unused) {
        }
    }

    public void putAll(ThemeValues themeValues) {
        this.mIntegers.putAll(themeValues.mIntegers);
        this.mStrings.putAll(themeValues.mStrings);
        this.mIntegerArrays.putAll(themeValues.mIntegerArrays);
        this.mStringArrays.putAll(themeValues.mStringArrays);
    }
}
