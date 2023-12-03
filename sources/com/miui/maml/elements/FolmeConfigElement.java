package com.miui.maml.elements;

import androidx.collection.ArraySet;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.folme.ConfigValue;
import java.util.ArrayList;
import miuix.animation.utils.EaseManager;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public class FolmeConfigElement extends ConfigElement {
    private ArrayList<ConfigData> mConfigs;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class ConfigData {
        public Expression mDelayExp;
        public Expression[] mEaseExp;
        public Expression mFromSpeedExp;
        public Expression[] mOnBeginCallbackExp;
        public Expression[] mOnCompleteCallbackExp;
        public Expression[] mOnUpdateCallbackExp;
        public Expression[] mPropertyExp;

        private ConfigData() {
        }
    }

    public FolmeConfigElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        ArrayList<ConfigData> arrayList = new ArrayList<>();
        this.mConfigs = arrayList;
        arrayList.add(getConfigData(element));
        NodeList childNodes = element.getChildNodes();
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            if (childNodes.item(i).getNodeType() == 1) {
                Element element2 = (Element) childNodes.item(i);
                if ("Special".equals(element2.getTagName())) {
                    this.mConfigs.add(getConfigData(element2));
                }
            }
        }
    }

    private ConfigData getConfigData(Element element) {
        ConfigData configData = new ConfigData();
        configData.mEaseExp = Expression.buildMultiple(getVariables(), element.getAttribute("ease"));
        configData.mFromSpeedExp = Expression.build(getVariables(), element.getAttribute("fromSpeed"));
        configData.mDelayExp = Expression.build(getVariables(), element.getAttribute("delay"));
        configData.mOnBeginCallbackExp = Expression.buildMultiple(getVariables(), element.getAttribute("onBegin"));
        configData.mOnCompleteCallbackExp = Expression.buildMultiple(getVariables(), element.getAttribute("onComplete"));
        configData.mOnUpdateCallbackExp = Expression.buildMultiple(getVariables(), element.getAttribute("onUpdate"));
        configData.mPropertyExp = Expression.buildMultiple(getVariables(), element.getAttribute("property"));
        return configData;
    }

    private EaseManager.EaseStyle getEaseFromExpressions(Expression[] expressionArr) {
        if (expressionArr == null || expressionArr.length <= 0 || expressionArr[0] == null) {
            return null;
        }
        int evaluate = (int) expressionArr[0].evaluate();
        float[] fArr = new float[expressionArr.length - 1];
        int length = expressionArr.length;
        for (int i = 1; i < length; i++) {
            if (expressionArr[i] != null) {
                fArr[i - 1] = (float) expressionArr[i].evaluate();
            } else {
                fArr[i - 1] = 0.0f;
            }
        }
        try {
            return EaseManager.getStyle(evaluate, fArr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getStrFromExpToSet(Expression[] expressionArr, ArraySet<String> arraySet) {
        arraySet.clear();
        if (expressionArr == null || expressionArr.length <= 0) {
            return;
        }
        int length = expressionArr.length;
        for (int i = 0; i < length; i++) {
            if (expressionArr[i] != null) {
                arraySet.add(expressionArr[i].evaluateStr());
            }
        }
    }

    @Override // com.miui.maml.elements.ConfigElement
    protected void evaluateConfigValue() {
        this.mTempValueList.clear();
        int size = this.mConfigs.size();
        for (int i = 0; i < size; i++) {
            ConfigValue configValue = new ConfigValue();
            ConfigData configData = this.mConfigs.get(i);
            Expression expression = configData.mDelayExp;
            if (expression != null) {
                configValue.mDelay = (long) expression.evaluate();
            }
            if (configData.mFromSpeedExp != null) {
                configValue.mFromSpeed = (float) r4.evaluate();
                configValue.mHasFromSpeed = true;
            }
            configValue.mEase = getEaseFromExpressions(configData.mEaseExp);
            getStrFromExpToSet(configData.mPropertyExp, configValue.mRelatedProperty);
            getStrFromExpToSet(configData.mOnBeginCallbackExp, configValue.mOnBeginCallback);
            getStrFromExpToSet(configData.mOnCompleteCallbackExp, configValue.mOnCompleteCallback);
            getStrFromExpToSet(configData.mOnUpdateCallbackExp, configValue.mOnUpdateCallback);
            this.mTempValueList.add(configValue);
        }
    }
}
