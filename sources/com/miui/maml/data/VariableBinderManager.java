package com.miui.maml.data;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.ContentProviderBinder;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public class VariableBinderManager implements ContentProviderBinder.QueryCompleteListener {
    private ScreenElementRoot mRoot;
    private ArrayList<VariableBinder> mVariableBinders = new ArrayList<>();

    public VariableBinderManager(Element element, ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
        if (element != null) {
            load(element, screenElementRoot);
        }
    }

    private static VariableBinder createBinder(Element element, ScreenElementRoot screenElementRoot, VariableBinderManager variableBinderManager) {
        String tagName = element.getTagName();
        VariableBinder contentProviderBinder = tagName.equalsIgnoreCase("ContentProviderBinder") ? new ContentProviderBinder(element, screenElementRoot) : tagName.equalsIgnoreCase("SensorBinder") ? new SensorBinder(element, screenElementRoot) : tagName.equalsIgnoreCase("BroadcastBinder") ? new BroadcastBinder(element, screenElementRoot) : tagName.equalsIgnoreCase("FileBinder") ? new FileBinder(element, screenElementRoot) : tagName.equalsIgnoreCase("SettingsBinder") ? new SettingsBinder(element, screenElementRoot) : null;
        if (contentProviderBinder != null) {
            contentProviderBinder.setQueryCompleteListener(variableBinderManager);
        }
        return contentProviderBinder;
    }

    private void load(Element element, ScreenElementRoot screenElementRoot) {
        if (element != null) {
            loadBinders(element, screenElementRoot);
        } else {
            Log.e("VariableBinderManager", "node is null");
            throw new NullPointerException("node is null");
        }
    }

    private void loadBinders(Element element, ScreenElementRoot screenElementRoot) {
        VariableBinder createBinder;
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeType() == 1 && (createBinder = createBinder((Element) childNodes.item(i), screenElementRoot, this)) != null) {
                this.mVariableBinders.add(createBinder);
            }
        }
    }

    public VariableBinder findBinder(String str) {
        Iterator<VariableBinder> it = this.mVariableBinders.iterator();
        while (it.hasNext()) {
            VariableBinder next = it.next();
            if (TextUtils.equals(str, next.getName())) {
                return next;
            }
        }
        return null;
    }

    public void finish() {
        Iterator<VariableBinder> it = this.mVariableBinders.iterator();
        while (it.hasNext()) {
            it.next().finish();
        }
    }

    public void init() {
        Iterator<VariableBinder> it = this.mVariableBinders.iterator();
        while (it.hasNext()) {
            it.next().init();
        }
    }

    @Override // com.miui.maml.data.ContentProviderBinder.QueryCompleteListener
    public void onQueryCompleted(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        Iterator<VariableBinder> it = this.mVariableBinders.iterator();
        while (it.hasNext()) {
            VariableBinder next = it.next();
            String dependency = next.getDependency();
            if (!TextUtils.isEmpty(dependency) && dependency.equals(str)) {
                next.startQuery();
            }
        }
    }

    public void pause() {
        Iterator<VariableBinder> it = this.mVariableBinders.iterator();
        while (it.hasNext()) {
            it.next().pause();
        }
    }

    public void resume() {
        Iterator<VariableBinder> it = this.mVariableBinders.iterator();
        while (it.hasNext()) {
            it.next().resume();
        }
    }

    public void tick() {
        Iterator<VariableBinder> it = this.mVariableBinders.iterator();
        while (it.hasNext()) {
            it.next().tick();
        }
    }
}
