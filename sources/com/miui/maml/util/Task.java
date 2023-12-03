package com.miui.maml.util;

import com.android.settings.search.FunctionColumns;
import miui.provider.ExtraTelephony;
import miui.yellowpage.YellowPageStatistic;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class Task {
    public static String TAG_ACTION = "action";
    public static String TAG_CATEGORY = "category";
    public static String TAG_CLASS = "class";
    public static String TAG_ID = "id";
    public static String TAG_NAME = "name";
    public static String TAG_PACKAGE = "package";
    public static String TAG_TYPE = "type";
    public String action;
    public String category;
    public String className;
    public String id;
    public String name;
    public String packageName;
    public String type;

    public static Task load(Element element) {
        if (element == null) {
            return null;
        }
        Task task = new Task();
        task.id = element.getAttribute("id");
        task.action = element.getAttribute("action");
        task.type = element.getAttribute("type");
        task.category = element.getAttribute(YellowPageStatistic.Display.CATEGORY);
        task.packageName = element.getAttribute(FunctionColumns.PACKAGE);
        task.className = element.getAttribute(ExtraTelephony.UnderstandInfo.CLASS);
        task.name = element.getAttribute("name");
        return task;
    }
}
