package com.miui.maml.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableBinder;
import java.util.Iterator;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class BroadcastBinder extends VariableBinder {
    private String mAction;
    private IntentFilter mIntentFilter;
    private final BroadcastReceiver mIntentReceiver;
    private boolean mRegistered;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Variable extends VariableBinder.Variable {
        public String mExtraName;

        public Variable(Element element, Variables variables) {
            super(element, variables);
            this.mExtraName = element.getAttribute("extra");
        }
    }

    public BroadcastBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mIntentReceiver = new BroadcastReceiver() { // from class: com.miui.maml.data.BroadcastBinder.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.i("BroadcastBinder", "onNotify: " + BroadcastBinder.this.toString());
                BroadcastBinder.this.onNotify(context, intent, null);
            }
        };
        load(element);
    }

    private void load(Element element) {
        if (element == null) {
            Log.e("BroadcastBinder", "ContentProviderBinder node is null");
            throw new NullPointerException("node is null");
        }
        String attribute = element.getAttribute("action");
        this.mAction = attribute;
        if (TextUtils.isEmpty(attribute)) {
            Log.e("BroadcastBinder", "no action in broadcast binder");
            throw new IllegalArgumentException("no action in broadcast binder element");
        }
        this.mIntentFilter = new IntentFilter(this.mAction);
        loadVariables(element);
    }

    private void updateVariables(Intent intent) {
        String stringExtra;
        if (intent == null) {
            return;
        }
        Log.d("BroadcastBinder", "updateVariables: " + intent);
        Iterator<VariableBinder.Variable> it = this.mVariables.iterator();
        while (it.hasNext()) {
            Variable variable = (Variable) it.next();
            double d = 0.0d;
            int i = variable.mType;
            if (i != 2) {
                if (i == 3) {
                    d = intent.getIntExtra(variable.mExtraName, (int) variable.mDefNumberValue);
                } else if (i == 4) {
                    d = intent.getLongExtra(variable.mExtraName, (long) variable.mDefNumberValue);
                } else if (i == 5) {
                    d = intent.getFloatExtra(variable.mExtraName, (float) variable.mDefNumberValue);
                } else if (i != 6) {
                    Log.w("BroadcastBinder", "invalide type" + variable.mTypeStr);
                } else {
                    d = intent.getDoubleExtra(variable.mExtraName, variable.mDefNumberValue);
                }
                variable.set(d);
                stringExtra = String.format("%f", Double.valueOf(d));
            } else {
                stringExtra = intent.getStringExtra(variable.mExtraName);
                variable.set(stringExtra == null ? variable.mDefStringValue : stringExtra);
            }
            Log.d("BroadcastBinder", "updateVariables: " + String.format("name:%s type:%s value:%s", variable.mName, variable.mTypeStr, stringExtra));
        }
    }

    @Override // com.miui.maml.data.VariableBinder
    public void finish() {
        super.finish();
        unregister();
    }

    @Override // com.miui.maml.data.VariableBinder
    public void init() {
        super.init();
        register();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.data.VariableBinder
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getContext().mVariables);
    }

    protected void onNotify(Context context, Intent intent, Object obj) {
        updateVariables(intent);
        onUpdateComplete();
    }

    protected void onRegister() {
        updateVariables(getContext().mContext.registerReceiver(this.mIntentReceiver, this.mIntentFilter));
        onUpdateComplete();
    }

    protected void onUnregister() {
        getContext().mContext.unregisterReceiver(this.mIntentReceiver);
    }

    protected void register() {
        if (this.mRegistered) {
            return;
        }
        onRegister();
        this.mRegistered = true;
    }

    protected void unregister() {
        if (this.mRegistered) {
            try {
                onUnregister();
            } catch (IllegalArgumentException unused) {
            }
            this.mRegistered = false;
        }
    }
}
