package com.miui.maml.util;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class IntentInfo {
    private Expression mClassNameExp;
    private ArrayList<Extra> mExtraList = new ArrayList<>();
    private Expression mPackageNameExp;
    private Task mTask;
    private String mUri;
    private Expression mUriExp;
    private Variables mVariables;

    /* renamed from: com.miui.maml.util.IntentInfo$2  reason: invalid class name */
    /* loaded from: classes2.dex */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$util$IntentInfo$Type;

        static {
            int[] iArr = new int[Type.values().length];
            $SwitchMap$com$miui$maml$util$IntentInfo$Type = iArr;
            try {
                iArr[Type.STRING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$util$IntentInfo$Type[Type.INT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$util$IntentInfo$Type[Type.LONG.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$maml$util$IntentInfo$Type[Type.FLOAT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$miui$maml$util$IntentInfo$Type[Type.DOUBLE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$miui$maml$util$IntentInfo$Type[Type.BOOLEAN.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    /* loaded from: classes2.dex */
    private class Extra {
        private Expression mCondition;
        private Expression mExpression;
        private String mName;
        protected Type mType = Type.DOUBLE;

        public Extra(Element element) {
            load(element);
        }

        private void load(Element element) {
            if (element == null) {
                Log.e("TaskVariable", "node is null");
                return;
            }
            this.mName = element.getAttribute("name");
            String attribute = element.getAttribute("type");
            if ("string".equalsIgnoreCase(attribute)) {
                this.mType = Type.STRING;
            } else if ("int".equalsIgnoreCase(attribute) || "integer".equalsIgnoreCase(attribute)) {
                this.mType = Type.INT;
            } else if ("long".equalsIgnoreCase(attribute)) {
                this.mType = Type.LONG;
            } else if ("float".equalsIgnoreCase(attribute)) {
                this.mType = Type.FLOAT;
            } else if ("double".equalsIgnoreCase(attribute)) {
                this.mType = Type.DOUBLE;
            } else if ("boolean".equalsIgnoreCase(attribute)) {
                this.mType = Type.BOOLEAN;
            }
            Expression build = Expression.build(IntentInfo.this.mVariables, element.getAttribute("expression"));
            this.mExpression = build;
            if (build == null) {
                Log.e("TaskVariable", "invalid expression in IntentCommand");
            }
            this.mCondition = Expression.build(IntentInfo.this.mVariables, element.getAttribute("condition"));
        }

        public double getDouble() {
            Expression expression = this.mExpression;
            if (expression == null) {
                return 0.0d;
            }
            return expression.evaluate();
        }

        public String getName() {
            return this.mName;
        }

        public String getString() {
            Expression expression = this.mExpression;
            if (expression == null) {
                return null;
            }
            return expression.evaluateStr();
        }

        public boolean isConditionTrue() {
            Expression expression = this.mCondition;
            return expression == null || expression.evaluate() > 0.0d;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum Type {
        STRING,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN
    }

    public IntentInfo(Element element, Variables variables) {
        if (element == null) {
            return;
        }
        this.mTask = Task.load(element);
        this.mVariables = variables;
        this.mPackageNameExp = Expression.build(variables, element.getAttribute("packageExp"));
        this.mClassNameExp = Expression.build(variables, element.getAttribute("classExp"));
        this.mUri = element.getAttribute("uri");
        this.mUriExp = Expression.build(variables, element.getAttribute("uriExp"));
        loadExtras(element);
    }

    private void loadExtras(Element element) {
        Utils.traverseXmlElementChildren(element, "Extra", new Utils.XmlTraverseListener() { // from class: com.miui.maml.util.IntentInfo.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                IntentInfo.this.mExtraList.add(new Extra(element2));
            }
        });
    }

    public String getAction() {
        Task task = this.mTask;
        if (task != null) {
            return task.action;
        }
        return null;
    }

    public String getId() {
        Task task = this.mTask;
        if (task != null) {
            return task.id;
        }
        return null;
    }

    public void set(Task task) {
        this.mTask = task;
    }

    public void update(Intent intent) {
        Task task = this.mTask;
        String str = task != null ? task.action : null;
        if (!TextUtils.isEmpty(str)) {
            intent.setAction(str);
        }
        Task task2 = this.mTask;
        String str2 = task2 != null ? task2.type : null;
        if (!TextUtils.isEmpty(str2)) {
            intent.setType(str2);
        }
        Task task3 = this.mTask;
        String str3 = task3 != null ? task3.category : null;
        if (!TextUtils.isEmpty(str3)) {
            intent.addCategory(str3);
        }
        Task task4 = this.mTask;
        String str4 = task4 != null ? task4.packageName : null;
        Expression expression = this.mPackageNameExp;
        if (expression != null) {
            str4 = expression.evaluateStr();
        }
        Task task5 = this.mTask;
        String str5 = task5 != null ? task5.className : null;
        Expression expression2 = this.mClassNameExp;
        if (expression2 != null) {
            str5 = expression2.evaluateStr();
        }
        if (!TextUtils.isEmpty(str4)) {
            if (TextUtils.isEmpty(str5)) {
                intent.setPackage(str4);
            } else {
                intent.setClassName(str4, str5);
            }
        }
        CustomUtils.replaceCameraIntentInfoOnF3M(str4, str5, intent);
        String str6 = this.mUri;
        Expression expression3 = this.mUriExp;
        if (expression3 != null) {
            str6 = expression3.evaluateStr();
        }
        if (!TextUtils.isEmpty(str6)) {
            intent.setData(Uri.parse(str6));
        }
        ArrayList<Extra> arrayList = this.mExtraList;
        if (arrayList != null) {
            Iterator<Extra> it = arrayList.iterator();
            while (it.hasNext()) {
                Extra next = it.next();
                if (next.isConditionTrue()) {
                    switch (AnonymousClass2.$SwitchMap$com$miui$maml$util$IntentInfo$Type[next.mType.ordinal()]) {
                        case 1:
                            intent.putExtra(next.getName(), next.getString());
                            continue;
                        case 2:
                            intent.putExtra(next.getName(), (int) next.getDouble());
                            continue;
                        case 3:
                            intent.putExtra(next.getName(), (long) next.getDouble());
                            continue;
                        case 4:
                            intent.putExtra(next.getName(), (float) next.getDouble());
                            continue;
                        case 5:
                            intent.putExtra(next.getName(), next.getDouble());
                            continue;
                        case 6:
                            intent.putExtra(next.getName(), next.getDouble() > 0.0d);
                            continue;
                    }
                } else {
                    intent.removeExtra(next.getName());
                }
            }
        }
    }
}
