package com.miui.maml.data;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.text.TextUtils;
import com.android.settings.network.telephony.ToggleSubscriptionDialogActivity;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.util.Utils;
import java.util.Iterator;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class SensorBinder extends VariableBinder {
    private boolean mEnable;
    private boolean mPaused;
    private int mRate;
    private SensorEventListener mSensorEventListener;
    private float mThreshold;
    private String mType;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Variable extends VariableBinder.Variable {
        public int mIndex;

        public Variable(Element element, Variables variables) {
            super(element, variables);
            this.mIndex = Utils.getAttrAsInt(element, "index", 0);
        }
    }

    public SensorBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mEnable = true;
        this.mType = element.getAttribute("type");
        this.mRate = Utils.getAttrAsInt(element, "rate", 3);
        this.mThreshold = Utils.getAttrAsFloat(element, "threshold", 0.1f);
        String attribute = element.getAttribute(ToggleSubscriptionDialogActivity.ARG_enable);
        if (!TextUtils.isEmpty(attribute)) {
            this.mEnable = Boolean.parseBoolean(attribute);
        }
        this.mSensorEventListener = new SensorEventListener() { // from class: com.miui.maml.data.SensorBinder.1
            @Override // android.hardware.SensorEventListener
            public void onAccuracyChanged(Sensor sensor, int i) {
            }

            @Override // android.hardware.SensorEventListener
            public void onSensorChanged(SensorEvent sensorEvent) {
                int length = sensorEvent.values.length;
                Iterator<VariableBinder.Variable> it = SensorBinder.this.mVariables.iterator();
                boolean z = false;
                while (it.hasNext()) {
                    Variable variable = (Variable) it.next();
                    int i = variable.mIndex;
                    if (i >= 0 && i < length && Math.abs(variable.getNumber() - sensorEvent.values[variable.mIndex]) > SensorBinder.this.mThreshold) {
                        variable.set(sensorEvent.values[variable.mIndex]);
                        z = true;
                    }
                }
                if (z) {
                    SensorBinder.this.onUpdateComplete();
                }
            }
        };
        loadVariables(element);
    }

    private void registerListener() {
        if (!this.mEnable || this.mPaused) {
            return;
        }
        MamlSensorManager.getInstance().registerListener(getContext().mContext, this.mType, this.mRate, this.mSensorEventListener);
    }

    private void unregisterListener() {
        MamlSensorManager.getInstance().unregisterListener(this.mType, this.mSensorEventListener);
    }

    @Override // com.miui.maml.data.VariableBinder
    public void finish() {
        unregisterListener();
        super.finish();
    }

    @Override // com.miui.maml.data.VariableBinder
    public void init() {
        super.init();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.data.VariableBinder
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getContext().mVariables);
    }

    @Override // com.miui.maml.data.VariableBinder
    public void pause() {
        super.pause();
        this.mPaused = true;
        unregisterListener();
    }

    @Override // com.miui.maml.data.VariableBinder
    public void resume() {
        super.resume();
        this.mPaused = false;
        registerListener();
    }

    public void turnOffSensorBinder() {
        this.mEnable = false;
        unregisterListener();
    }

    public void turnOnSensorBinder() {
        this.mEnable = true;
        registerListener();
    }
}
