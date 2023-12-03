package com.miui.maml;

import android.util.Log;
import com.miui.maml.data.Variables;
import com.miui.maml.util.Utils;
import java.io.InputStream;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public class LanguageHelper {
    public static boolean load(Locale locale, ResourceManager resourceManager, Variables variables) {
        String str;
        InputStream inputStream = null;
        String str2 = "strings/strings.xml";
        if (locale != null) {
            str = Utils.addFileNameSuffix("strings/strings.xml", locale.toString());
            if (!resourceManager.resourceExists(str)) {
                str = Utils.addFileNameSuffix("strings/strings.xml", locale.getLanguage());
            }
        } else {
            str = null;
        }
        if (resourceManager.resourceExists(str)) {
            str2 = str;
        } else if (!resourceManager.resourceExists("strings/strings.xml")) {
            Log.i("LanguageHelper", "no available string resources to load.");
            return false;
        }
        try {
            try {
                DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                inputStream = resourceManager.getInputStream(str2);
                Document parse = newDocumentBuilder.parse(inputStream);
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return setVariables(parse, variables);
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (Exception e3) {
            Log.e("LanguageHelper", e3.getMessage());
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
            return false;
        }
    }

    private static boolean setVariables(Document document, Variables variables) {
        boolean z;
        NodeList elementsByTagName = document.getElementsByTagName("resources");
        if (elementsByTagName.getLength() <= 0) {
            elementsByTagName = document.getElementsByTagName("strings");
            if (elementsByTagName.getLength() <= 0) {
                return false;
            }
            z = false;
        } else {
            z = true;
        }
        NodeList elementsByTagName2 = ((Element) elementsByTagName.item(0)).getElementsByTagName("string");
        for (int i = 0; i < elementsByTagName2.getLength(); i++) {
            Element element = (Element) elementsByTagName2.item(i);
            variables.put(element.getAttribute("name"), (z ? element.getTextContent() : element.getAttribute("value")).replaceAll("\\\\", ""));
        }
        return true;
    }
}
