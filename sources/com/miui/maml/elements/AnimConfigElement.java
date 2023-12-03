package com.miui.maml.elements;

import android.text.TextUtils;
import androidx.collection.ArraySet;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.folme.ConfigValue;
import java.util.concurrent.ConcurrentHashMap;
import miuix.animation.utils.EaseManager;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public class AnimConfigElement extends ConfigElement {
    private ConcurrentHashMap<String, ConfigData> mConfigs;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class ConfigData {
        public IndexedVariable mDelay;
        public IndexedVariable mEase;
        public IndexedVariable mEaseLen;
        public IndexedVariable mFromSpeed;
        public IndexedVariable mOnBeginCallback;
        public IndexedVariable mOnBeginCallbackLen;
        public IndexedVariable mOnCompleteCallback;
        public IndexedVariable mOnCompleteCallbackLen;
        public IndexedVariable mOnUpdateCallback;
        public IndexedVariable mOnUpdateCallbackLen;
        public IndexedVariable mProperty;
        public IndexedVariable mPropertyLen;

        private ConfigData() {
        }
    }

    public AnimConfigElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        ConcurrentHashMap<String, ConfigData> concurrentHashMap = new ConcurrentHashMap<>();
        this.mConfigs = concurrentHashMap;
        if (this.mHasName) {
            String str = this.mName;
            concurrentHashMap.put(str, getConfigData(element, str));
            NodeList childNodes = element.getChildNodes();
            int length = childNodes.getLength();
            for (int i = 0; i < length; i++) {
                if (childNodes.item(i).getNodeType() == 1) {
                    Element element2 = (Element) childNodes.item(i);
                    if ("Special".equals(element2.getTagName())) {
                        String attribute = element2.getAttribute("name");
                        if (!TextUtils.isEmpty(attribute)) {
                            String str2 = this.mName + "." + attribute;
                            this.mConfigs.put(str2, getConfigData(element2, str2));
                        }
                    }
                }
            }
        }
    }

    private ConfigData getConfigData(Element element, String str) {
        Variables variables = getVariables();
        ConfigData configData = new ConfigData();
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            String nodeName = attributes.item(i).getNodeName();
            Expression[] buildMultiple = Expression.buildMultiple(variables, element.getAttribute(nodeName));
            if (buildMultiple != null) {
                updateConfigIndexVariable(configData, str, nodeName, buildMultiple);
            }
        }
        return configData;
    }

    private void getConfigValueFromVarToSet(IndexedVariable indexedVariable, ArraySet<String> arraySet) {
        Object obj = indexedVariable != null ? indexedVariable.get() : null;
        if (obj == null || !(obj instanceof String[])) {
            return;
        }
        for (String str : (String[]) obj) {
            arraySet.add(str);
        }
    }

    private EaseManager.EaseStyle getEase(IndexedVariable indexedVariable) {
        Object obj = indexedVariable != null ? indexedVariable.get() : null;
        if (obj != null && (obj instanceof double[])) {
            double[] dArr = (double[]) obj;
            if (dArr.length > 0) {
                int i = (int) dArr[0];
                float[] fArr = new float[dArr.length - 1];
                int length = dArr.length;
                for (int i2 = 1; i2 < length; i2++) {
                    fArr[i2 - 1] = (float) dArr[i2];
                }
                try {
                    return EaseManager.getStyle(i, fArr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void removeConfigIndexVariable(ConfigData configData, String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2124458952:
                if (str.equals("onComplete")) {
                    c = 0;
                    break;
                }
                break;
            case -1353036278:
                if (str.equals("onBegin")) {
                    c = 1;
                    break;
                }
                break;
            case -993141291:
                if (str.equals("property")) {
                    c = 2;
                    break;
                }
                break;
            case 3105774:
                if (str.equals("ease")) {
                    c = 3;
                    break;
                }
                break;
            case 69481149:
                if (str.equals("fromSpeed")) {
                    c = 4;
                    break;
                }
                break;
            case 95467907:
                if (str.equals("delay")) {
                    c = 5;
                    break;
                }
                break;
            case 1559564168:
                if (str.equals("onUpdate")) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                IndexedVariable indexedVariable = configData.mOnCompleteCallback;
                if (indexedVariable != null) {
                    indexedVariable.set((Object) null);
                    configData.mOnCompleteCallback = null;
                }
                IndexedVariable indexedVariable2 = configData.mOnCompleteCallbackLen;
                if (indexedVariable2 != null) {
                    indexedVariable2.set(0.0d);
                    configData.mOnCompleteCallbackLen = null;
                    return;
                }
                return;
            case 1:
                IndexedVariable indexedVariable3 = configData.mOnBeginCallback;
                if (indexedVariable3 != null) {
                    indexedVariable3.set((Object) null);
                    configData.mOnBeginCallback = null;
                }
                IndexedVariable indexedVariable4 = configData.mOnBeginCallbackLen;
                if (indexedVariable4 != null) {
                    indexedVariable4.set(0.0d);
                    configData.mOnBeginCallbackLen = null;
                    return;
                }
                return;
            case 2:
                IndexedVariable indexedVariable5 = configData.mProperty;
                if (indexedVariable5 != null) {
                    indexedVariable5.set((Object) null);
                    configData.mProperty = null;
                }
                IndexedVariable indexedVariable6 = configData.mPropertyLen;
                if (indexedVariable6 != null) {
                    indexedVariable6.set(0.0d);
                    configData.mPropertyLen = null;
                    return;
                }
                return;
            case 3:
                IndexedVariable indexedVariable7 = configData.mEase;
                if (indexedVariable7 != null) {
                    indexedVariable7.set((Object) null);
                    configData.mEase = null;
                }
                IndexedVariable indexedVariable8 = configData.mEaseLen;
                if (indexedVariable8 != null) {
                    indexedVariable8.set(0.0d);
                    configData.mEaseLen = null;
                    return;
                }
                return;
            case 4:
                IndexedVariable indexedVariable9 = configData.mFromSpeed;
                if (indexedVariable9 != null) {
                    indexedVariable9.set(0.0d);
                    configData.mFromSpeed = null;
                    return;
                }
                return;
            case 5:
                IndexedVariable indexedVariable10 = configData.mDelay;
                if (indexedVariable10 != null) {
                    indexedVariable10.set(0.0d);
                    configData.mDelay = null;
                    return;
                }
                return;
            case 6:
                IndexedVariable indexedVariable11 = configData.mOnUpdateCallback;
                if (indexedVariable11 != null) {
                    indexedVariable11.set((Object) null);
                    configData.mOnUpdateCallback = null;
                }
                IndexedVariable indexedVariable12 = configData.mOnUpdateCallbackLen;
                if (indexedVariable12 != null) {
                    indexedVariable12.set(0.0d);
                    configData.mOnUpdateCallbackLen = null;
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v4, types: [java.lang.String[]] */
    /* JADX WARN: Type inference failed for: r5v5, types: [double[]] */
    /* JADX WARN: Type inference failed for: r5v8, types: [java.lang.Double] */
    private void updateConfigIndexVariable(ConfigData configData, String str, String str2, Expression[] expressionArr) {
        boolean z;
        IndexedVariable indexedVariable;
        IndexedVariable indexedVariable2;
        Object obj;
        IndexedVariable indexedVariable3;
        String str3 = str + "." + str2;
        Variables variables = getVariables();
        str2.hashCode();
        int i = 0;
        boolean z2 = true;
        char c = 65535;
        switch (str2.hashCode()) {
            case -2124458952:
                if (str2.equals("onComplete")) {
                    c = 0;
                    break;
                }
                break;
            case -1353036278:
                if (str2.equals("onBegin")) {
                    c = 1;
                    break;
                }
                break;
            case -993141291:
                if (str2.equals("property")) {
                    c = 2;
                    break;
                }
                break;
            case 3105774:
                if (str2.equals("ease")) {
                    c = 3;
                    break;
                }
                break;
            case 69481149:
                if (str2.equals("fromSpeed")) {
                    c = 4;
                    break;
                }
                break;
            case 95467907:
                if (str2.equals("delay")) {
                    c = 5;
                    break;
                }
                break;
            case 1559564168:
                if (str2.equals("onUpdate")) {
                    c = 6;
                    break;
                }
                break;
        }
        IndexedVariable indexedVariable4 = null;
        switch (c) {
            case 0:
                if (configData.mOnCompleteCallback == null) {
                    configData.mOnCompleteCallback = new IndexedVariable(str3, variables, false);
                }
                if (configData.mOnCompleteCallbackLen == null) {
                    configData.mOnCompleteCallbackLen = new IndexedVariable(str3 + ".length", variables, true);
                }
                IndexedVariable indexedVariable5 = configData.mOnCompleteCallback;
                indexedVariable4 = configData.mOnCompleteCallbackLen;
                indexedVariable3 = indexedVariable5;
                z = false;
                z2 = false;
                indexedVariable2 = indexedVariable3;
                break;
            case 1:
                if (configData.mOnBeginCallback == null) {
                    configData.mOnBeginCallback = new IndexedVariable(str3, variables, false);
                }
                if (configData.mOnBeginCallbackLen == null) {
                    configData.mOnBeginCallbackLen = new IndexedVariable(str3 + ".length", variables, true);
                }
                IndexedVariable indexedVariable6 = configData.mOnBeginCallback;
                indexedVariable4 = configData.mOnBeginCallbackLen;
                indexedVariable3 = indexedVariable6;
                z = false;
                z2 = false;
                indexedVariable2 = indexedVariable3;
                break;
            case 2:
                if (configData.mProperty == null) {
                    configData.mProperty = new IndexedVariable(str3, variables, false);
                }
                if (configData.mPropertyLen == null) {
                    configData.mPropertyLen = new IndexedVariable(str3 + ".length", variables, true);
                }
                IndexedVariable indexedVariable7 = configData.mProperty;
                indexedVariable4 = configData.mPropertyLen;
                indexedVariable3 = indexedVariable7;
                z = false;
                z2 = false;
                indexedVariable2 = indexedVariable3;
                break;
            case 3:
                if (configData.mEase == null) {
                    configData.mEase = new IndexedVariable(str3, variables, false);
                }
                if (configData.mEaseLen == null) {
                    configData.mEaseLen = new IndexedVariable(str3 + ".length", variables, true);
                }
                IndexedVariable indexedVariable8 = configData.mEase;
                indexedVariable4 = configData.mEaseLen;
                z = true;
                z2 = false;
                indexedVariable2 = indexedVariable8;
                break;
            case 4:
                if (configData.mFromSpeed == null) {
                    configData.mFromSpeed = new IndexedVariable(str3, variables, true);
                }
                indexedVariable = configData.mFromSpeed;
                z = false;
                indexedVariable2 = indexedVariable;
                break;
            case 5:
                if (configData.mDelay == null) {
                    configData.mDelay = new IndexedVariable(str3, variables, true);
                }
                indexedVariable = configData.mDelay;
                z = false;
                indexedVariable2 = indexedVariable;
                break;
            case 6:
                if (configData.mOnUpdateCallback == null) {
                    configData.mOnUpdateCallback = new IndexedVariable(str3, variables, false);
                }
                if (configData.mOnUpdateCallbackLen == null) {
                    configData.mOnUpdateCallbackLen = new IndexedVariable(str3 + ".length", variables, true);
                }
                IndexedVariable indexedVariable9 = configData.mOnUpdateCallback;
                indexedVariable4 = configData.mOnUpdateCallbackLen;
                indexedVariable3 = indexedVariable9;
                z = false;
                z2 = false;
                indexedVariable2 = indexedVariable3;
                break;
            default:
                return;
        }
        int length = expressionArr.length;
        if (length > 0) {
            if (z2) {
                obj = Double.valueOf(expressionArr[0].evaluate());
            } else if (z) {
                obj = new double[length];
                while (i < length) {
                    if (expressionArr[i] != null) {
                        obj[i] = expressionArr[i].evaluate();
                    }
                    i++;
                }
            } else {
                obj = new String[length];
                while (i < length) {
                    if (expressionArr[i] != null) {
                        obj[i] = expressionArr[i].evaluateStr();
                    }
                    i++;
                }
            }
            indexedVariable2.set(obj);
            if (indexedVariable4 != null) {
                indexedVariable4.set(length);
            }
        }
    }

    public void clearConfigData() {
        for (ConfigData configData : this.mConfigs.values()) {
            removeConfigIndexVariable(configData, "fromSpeed");
            removeConfigIndexVariable(configData, "delay");
            removeConfigIndexVariable(configData, "ease");
            removeConfigIndexVariable(configData, "property");
            removeConfigIndexVariable(configData, "onUpdate");
            removeConfigIndexVariable(configData, "onBegin");
            removeConfigIndexVariable(configData, "onComplete");
        }
        this.mConfigs.clear();
    }

    @Override // com.miui.maml.elements.ConfigElement
    protected void evaluateConfigValue() {
        this.mTempValueList.clear();
        for (ConfigData configData : this.mConfigs.values()) {
            ConfigValue configValue = new ConfigValue();
            IndexedVariable indexedVariable = configData.mDelay;
            if (indexedVariable != null) {
                configValue.mDelay = (long) indexedVariable.getDouble();
            }
            if (configData.mFromSpeed != null) {
                configValue.mFromSpeed = (float) r3.getDouble();
                configValue.mHasFromSpeed = true;
            }
            IndexedVariable indexedVariable2 = configData.mEase;
            if (indexedVariable2 != null) {
                configValue.mEase = getEase(indexedVariable2);
            }
            IndexedVariable indexedVariable3 = configData.mProperty;
            if (indexedVariable3 != null) {
                getConfigValueFromVarToSet(indexedVariable3, configValue.mRelatedProperty);
            }
            IndexedVariable indexedVariable4 = configData.mOnBeginCallback;
            if (indexedVariable4 != null) {
                getConfigValueFromVarToSet(indexedVariable4, configValue.mOnBeginCallback);
            }
            IndexedVariable indexedVariable5 = configData.mOnCompleteCallback;
            if (indexedVariable5 != null) {
                getConfigValueFromVarToSet(indexedVariable5, configValue.mOnCompleteCallback);
            }
            IndexedVariable indexedVariable6 = configData.mOnUpdateCallback;
            if (indexedVariable6 != null) {
                getConfigValueFromVarToSet(indexedVariable6, configValue.mOnUpdateCallback);
            }
            this.mTempValueList.add(configValue);
        }
    }

    public void removeConfigData(String str, String str2) {
        ConfigData configData = this.mConfigs.get(str);
        if (configData != null) {
            removeConfigIndexVariable(configData, str2);
        }
    }

    public void updateConfigData(String str, String str2, Expression[] expressionArr) {
        ConfigData configData = this.mConfigs.get(str);
        if (configData == null) {
            configData = new ConfigData();
            this.mConfigs.put(str, configData);
        }
        updateConfigIndexVariable(configData, str, str2, expressionArr);
    }
}
