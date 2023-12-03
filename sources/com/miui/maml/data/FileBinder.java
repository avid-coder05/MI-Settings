package com.miui.maml.data;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.util.FilenameExtFilter;
import com.miui.maml.util.TextFormatter;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class FileBinder extends VariableBinder {
    private IndexedVariable mCountVar;
    protected TextFormatter mDirFormatter;
    private String[] mFiles;
    private String[] mFilters;
    private ArrayList<Variable> mVariables;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Variable extends VariableBinder.Variable {
        public Expression mIndex;

        public Variable(Element element, Variables variables) {
            super(element, variables);
            Expression build = Expression.build(variables, element.getAttribute("index"));
            this.mIndex = build;
            if (build == null) {
                Log.e("Variable", "fail to load file index expression");
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.miui.maml.data.VariableBinder.TypedValue
        public int parseType(String str) {
            return 2;
        }
    }

    public FileBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mVariables = new ArrayList<>();
        load(element);
    }

    private void load(Element element) {
        if (element == null) {
            Log.e("FileBinder", "FileBinder node is null");
            return;
        }
        String trim = element.getAttribute("filter").trim();
        this.mFilters = TextUtils.isEmpty(trim) ? null : trim.split(",");
        this.mDirFormatter = new TextFormatter(getVariables(), element.getAttribute("dir"), Expression.build(getVariables(), element.getAttribute("dirExp")));
        if (!TextUtils.isEmpty(this.mName)) {
            this.mCountVar = new IndexedVariable(this.mName + ".count", getContext().mVariables, true);
        }
        loadVariables(element);
    }

    private void updateVariables() {
        String[] strArr = this.mFiles;
        int length = strArr == null ? 0 : strArr.length;
        Iterator<Variable> it = this.mVariables.iterator();
        while (it.hasNext()) {
            Variable next = it.next();
            Expression expression = next.mIndex;
            if (expression != null) {
                next.set(length == 0 ? null : this.mFiles[((int) expression.evaluate()) % length]);
            }
        }
    }

    @Override // com.miui.maml.data.VariableBinder
    public void init() {
        super.init();
        refresh();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.data.VariableBinder
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getVariables());
    }

    @Override // com.miui.maml.data.VariableBinder
    public void refresh() {
        super.refresh();
        File file = new File(this.mDirFormatter.getText());
        String[] strArr = this.mFilters;
        String[] list = strArr == null ? file.list() : file.list(new FilenameExtFilter(strArr));
        this.mFiles = list;
        int length = list == null ? 0 : list.length;
        IndexedVariable indexedVariable = this.mCountVar;
        if (indexedVariable != null) {
            indexedVariable.set(length);
        }
        Log.i("FileBinder", "file count: " + length);
        updateVariables();
    }

    @Override // com.miui.maml.data.VariableBinder
    public void tick() {
        super.tick();
        updateVariables();
    }
}
