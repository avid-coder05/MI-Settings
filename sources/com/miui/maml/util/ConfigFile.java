package com.miui.maml.util;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.util.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import miui.content.res.ThemeNativeUtils;
import miui.provider.ExtraContacts;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public class ConfigFile {
    private boolean mDirty;
    private String mFilePath;
    private boolean mSaveViaProvider;
    private HashMap<String, Variable> mVariables = new HashMap<>();
    private HashMap<String, Task> mTasks = new HashMap<>();
    private ArrayList<Gadget> mGadgets = new ArrayList<>();

    /* loaded from: classes2.dex */
    public static class Gadget {
        public String path;
        public int x;
        public int y;

        public Gadget(String str, int i, int i2) {
            this.path = str;
            this.x = i;
            this.y = i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public interface OnLoadElementListener {
        void OnLoadElement(Element element);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class SaveAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;
        private String mData;

        public SaveAsyncTask(Context context, String str) {
            this.mContext = context;
            this.mData = str;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            try {
                Uri parse = Uri.parse("content://com.miui.maml.provider");
                Bundle bundle = new Bundle();
                bundle.putString("data", this.mData);
                this.mContext.getContentResolver().call(parse, "saveConfigFile", (String) null, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static class Variable {
        public String name;
        public String type;
        public String value;
    }

    private void createNewFile(String str) throws IOException {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        File file = new File(str);
        file.getParentFile().mkdirs();
        file.delete();
        file.createNewFile();
    }

    private void loadGadgets(Element element) {
        loadList(element, "Gadgets", "Gadget", new OnLoadElementListener() { // from class: com.miui.maml.util.ConfigFile.4
            @Override // com.miui.maml.util.ConfigFile.OnLoadElementListener
            public void OnLoadElement(Element element2) {
                if (element2 != null) {
                    ConfigFile.this.putGadget(new Gadget(element2.getAttribute("path"), Utils.getAttrAsInt(element2, "x", 0), Utils.getAttrAsInt(element2, "y", 0)));
                }
            }
        });
    }

    private void loadList(Element element, String str, String str2, OnLoadElementListener onLoadElementListener) {
        Element child = Utils.getChild(element, str);
        if (child == null) {
            return;
        }
        NodeList childNodes = child.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && item.getNodeName().equals(str2)) {
                onLoadElementListener.OnLoadElement((Element) item);
            }
        }
    }

    private void loadTasks(Element element) {
        loadList(element, "Tasks", "Intent", new OnLoadElementListener() { // from class: com.miui.maml.util.ConfigFile.3
            @Override // com.miui.maml.util.ConfigFile.OnLoadElementListener
            public void OnLoadElement(Element element2) {
                ConfigFile.this.putTask(Task.load(element2));
            }
        });
    }

    private void loadVariables(Element element) {
        loadList(element, "Variables", "Variable", new OnLoadElementListener() { // from class: com.miui.maml.util.ConfigFile.2
            @Override // com.miui.maml.util.ConfigFile.OnLoadElementListener
            public void OnLoadElement(Element element2) {
                ConfigFile.this.put(element2.getAttribute("name"), element2.getAttribute("value"), element2.getAttribute("type"));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void put(String str, String str2, String str3) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str3)) {
            return;
        }
        if ("string".equals(str3) || "number".equals(str3)) {
            Variable variable = this.mVariables.get(str);
            if (variable == null) {
                variable = new Variable();
                variable.name = str;
                this.mVariables.put(str, variable);
            }
            variable.type = str3;
            variable.value = str2;
        }
    }

    private void writeGadgets(StringBuilder sb) {
        if (this.mGadgets.size() == 0) {
            return;
        }
        writeTag(sb, "Gadgets", false);
        String[] strArr = {"path", "x", "y"};
        Iterator<Gadget> it = this.mGadgets.iterator();
        while (it.hasNext()) {
            Gadget next = it.next();
            writeTag(sb, "Gadget", strArr, new String[]{next.path, String.valueOf(next.x), String.valueOf(next.y)}, true);
        }
        writeTag(sb, "Gadgets", true);
    }

    private static void writeTag(StringBuilder sb, String str, boolean z) {
        sb.append("<");
        if (z) {
            sb.append("/");
        }
        sb.append(str);
        sb.append(">\n");
    }

    private static void writeTag(StringBuilder sb, String str, String[] strArr, String[] strArr2) {
        writeTag(sb, str, strArr, strArr2, true);
    }

    private static void writeTag(StringBuilder sb, String str, String[] strArr, String[] strArr2, boolean z) {
        sb.append("<");
        sb.append(str);
        for (int i = 0; i < strArr.length; i++) {
            if (!z || !TextUtils.isEmpty(strArr2[i])) {
                sb.append(" ");
                sb.append(strArr[i]);
                sb.append("=\"");
                sb.append(strArr2[i]);
                sb.append("\"");
            }
        }
        sb.append("/>\n");
    }

    private void writeTasks(StringBuilder sb) {
        if (this.mTasks.size() == 0) {
            return;
        }
        writeTag(sb, "Tasks", false);
        String[] strArr = {Task.TAG_ID, Task.TAG_ACTION, Task.TAG_TYPE, Task.TAG_CATEGORY, Task.TAG_PACKAGE, Task.TAG_CLASS, Task.TAG_NAME};
        for (Task task : this.mTasks.values()) {
            writeTag(sb, "Intent", strArr, new String[]{task.id, task.action, task.type, task.category, task.packageName, task.className, task.name}, true);
        }
        writeTag(sb, "Tasks", true);
    }

    private void writeVariables(StringBuilder sb) {
        if (this.mVariables.size() == 0) {
            return;
        }
        writeTag(sb, "Variables", false);
        String[] strArr = {"name", "type", "value"};
        for (Variable variable : this.mVariables.values()) {
            writeTag(sb, "Variable", strArr, new String[]{variable.name, variable.type, variable.value});
        }
        writeTag(sb, "Variables", true);
    }

    public Task getTask(String str) {
        return this.mTasks.get(str);
    }

    public Collection<Task> getTasks() {
        return this.mTasks.values();
    }

    public Collection<Variable> getVariables() {
        return this.mVariables.values();
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x005e, code lost:
    
        if (r2 == null) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0076, code lost:
    
        if (r2 == null) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x007f, code lost:
    
        if (r2 == null) goto L51;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean load(java.lang.String r5) {
        /*
            r4 = this;
            r4.mFilePath = r5
            java.util.HashMap<java.lang.String, com.miui.maml.util.ConfigFile$Variable> r0 = r4.mVariables
            r0.clear()
            java.util.HashMap<java.lang.String, com.miui.maml.util.Task> r0 = r4.mTasks
            r0.clear()
            javax.xml.parsers.DocumentBuilderFactory r0 = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            r1 = 0
            r2 = 0
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L58 java.lang.Exception -> L5a java.io.IOException -> L64 org.xml.sax.SAXException -> L6b javax.xml.parsers.ParserConfigurationException -> L72 java.io.FileNotFoundException -> L7f
            r3.<init>(r5)     // Catch: java.lang.Throwable -> L58 java.lang.Exception -> L5a java.io.IOException -> L64 org.xml.sax.SAXException -> L6b javax.xml.parsers.ParserConfigurationException -> L72 java.io.FileNotFoundException -> L7f
            javax.xml.parsers.DocumentBuilder r5 = r0.newDocumentBuilder()     // Catch: java.lang.Throwable -> L47 java.lang.Exception -> L4a java.io.IOException -> L4d org.xml.sax.SAXException -> L50 javax.xml.parsers.ParserConfigurationException -> L53 java.io.FileNotFoundException -> L56
            org.w3c.dom.Document r5 = r5.parse(r3)     // Catch: java.lang.Throwable -> L47 java.lang.Exception -> L4a java.io.IOException -> L4d org.xml.sax.SAXException -> L50 javax.xml.parsers.ParserConfigurationException -> L53 java.io.FileNotFoundException -> L56
            org.w3c.dom.Element r5 = r5.getDocumentElement()     // Catch: java.lang.Throwable -> L47 java.lang.Exception -> L4a java.io.IOException -> L4d org.xml.sax.SAXException -> L50 javax.xml.parsers.ParserConfigurationException -> L53 java.io.FileNotFoundException -> L56
            if (r5 != 0) goto L29
            r3.close()     // Catch: java.io.IOException -> L28
        L28:
            return r1
        L29:
            java.lang.String r0 = r5.getNodeName()     // Catch: java.lang.Throwable -> L47 java.lang.Exception -> L4a java.io.IOException -> L4d org.xml.sax.SAXException -> L50 javax.xml.parsers.ParserConfigurationException -> L53 java.io.FileNotFoundException -> L56
            java.lang.String r2 = "Config"
            boolean r0 = r0.equals(r2)     // Catch: java.lang.Throwable -> L47 java.lang.Exception -> L4a java.io.IOException -> L4d org.xml.sax.SAXException -> L50 javax.xml.parsers.ParserConfigurationException -> L53 java.io.FileNotFoundException -> L56
            if (r0 != 0) goto L39
            r3.close()     // Catch: java.io.IOException -> L38
        L38:
            return r1
        L39:
            r4.loadVariables(r5)     // Catch: java.lang.Throwable -> L47 java.lang.Exception -> L4a java.io.IOException -> L4d org.xml.sax.SAXException -> L50 javax.xml.parsers.ParserConfigurationException -> L53 java.io.FileNotFoundException -> L56
            r4.loadTasks(r5)     // Catch: java.lang.Throwable -> L47 java.lang.Exception -> L4a java.io.IOException -> L4d org.xml.sax.SAXException -> L50 javax.xml.parsers.ParserConfigurationException -> L53 java.io.FileNotFoundException -> L56
            r4.loadGadgets(r5)     // Catch: java.lang.Throwable -> L47 java.lang.Exception -> L4a java.io.IOException -> L4d org.xml.sax.SAXException -> L50 javax.xml.parsers.ParserConfigurationException -> L53 java.io.FileNotFoundException -> L56
            r4 = 1
            r3.close()     // Catch: java.io.IOException -> L46
        L46:
            return r4
        L47:
            r4 = move-exception
            r2 = r3
            goto L79
        L4a:
            r4 = move-exception
            r2 = r3
            goto L5b
        L4d:
            r4 = move-exception
            r2 = r3
            goto L65
        L50:
            r4 = move-exception
            r2 = r3
            goto L6c
        L53:
            r4 = move-exception
            r2 = r3
            goto L73
        L56:
            r2 = r3
            goto L7f
        L58:
            r4 = move-exception
            goto L79
        L5a:
            r4 = move-exception
        L5b:
            r4.printStackTrace()     // Catch: java.lang.Throwable -> L58
            if (r2 == 0) goto L82
        L60:
            r2.close()     // Catch: java.io.IOException -> L82
            goto L82
        L64:
            r4 = move-exception
        L65:
            r4.printStackTrace()     // Catch: java.lang.Throwable -> L58
            if (r2 == 0) goto L82
            goto L60
        L6b:
            r4 = move-exception
        L6c:
            r4.printStackTrace()     // Catch: java.lang.Throwable -> L58
            if (r2 == 0) goto L82
            goto L60
        L72:
            r4 = move-exception
        L73:
            r4.printStackTrace()     // Catch: java.lang.Throwable -> L58
            if (r2 == 0) goto L82
            goto L60
        L79:
            if (r2 == 0) goto L7e
            r2.close()     // Catch: java.io.IOException -> L7e
        L7e:
            throw r4
        L7f:
            if (r2 == 0) goto L82
            goto L60
        L82:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.ConfigFile.load(java.lang.String):boolean");
    }

    public void loadDefaultSettings(Element element) {
        if (element == null || !element.getNodeName().equals("Config")) {
            return;
        }
        Utils.traverseXmlElementChildren(element, "Group", new Utils.XmlTraverseListener() { // from class: com.miui.maml.util.ConfigFile.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                Utils.traverseXmlElementChildren(element2, null, new Utils.XmlTraverseListener() { // from class: com.miui.maml.util.ConfigFile.1.1
                    @Override // com.miui.maml.util.Utils.XmlTraverseListener
                    public void onChild(Element element3) {
                        String nodeName = element3.getNodeName();
                        String attribute = element3.getAttribute("id");
                        if ("StringInput".equals(nodeName)) {
                            ConfigFile.this.putString(attribute, element3.getAttribute(ExtraContacts.DefaultAccount.NAME));
                        } else if ("CheckBox".equals(nodeName)) {
                            ConfigFile.this.putNumber(attribute, element3.getAttribute(ExtraContacts.DefaultAccount.NAME).equals("1") ? "1" : "0");
                        } else if ("NumberInput".equals(nodeName)) {
                            ConfigFile.this.putNumber(attribute, Utils.doubleToString(Utils.getAttrAsFloat(element3, ExtraContacts.DefaultAccount.NAME, 0.0f)));
                        } else if ("StringChoice".equals(nodeName)) {
                            ConfigFile.this.putString(attribute, element3.getAttribute(ExtraContacts.DefaultAccount.NAME));
                        } else if ("NumberChoice".equals(nodeName)) {
                            ConfigFile.this.putNumber(attribute, Utils.doubleToString(Utils.getAttrAsFloat(element3, ExtraContacts.DefaultAccount.NAME, 0.0f)));
                        } else if ("AppPicker".equals(nodeName)) {
                            ConfigFile.this.putTask(Task.load(element3));
                        }
                    }
                });
            }
        });
    }

    public void putGadget(Gadget gadget) {
        if (gadget == null) {
            return;
        }
        this.mGadgets.add(gadget);
        this.mDirty = true;
    }

    public void putNumber(String str, double d) {
        putNumber(str, Utils.doubleToString(d));
    }

    public void putNumber(String str, String str2) {
        put(str, str2, "number");
        this.mDirty = true;
    }

    public void putString(String str, String str2) {
        put(str, str2, "string");
        this.mDirty = true;
    }

    public void putTask(Task task) {
        if (task == null || TextUtils.isEmpty(task.id)) {
            return;
        }
        this.mTasks.put(task.id, task);
        this.mDirty = true;
    }

    public boolean save(Context context) {
        boolean z = this.mDirty;
        this.mDirty = false;
        return !z || save(this.mFilePath, context);
    }

    public boolean save(String str, Context context) {
        String str2;
        StringBuilder sb = new StringBuilder();
        writeTag(sb, "Config", false);
        writeVariables(sb);
        writeTasks(sb);
        writeGadgets(sb);
        writeTag(sb, "Config", true);
        if (this.mSaveViaProvider) {
            new SaveAsyncTask(context, sb.toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            return true;
        }
        ThemeNativeUtils.remove(str);
        String str3 = null;
        try {
            createNewFile(str);
        } catch (IOException e) {
            try {
                File externalFilesDir = context.getExternalFilesDir(null);
                if (Build.VERSION.SDK_INT >= 23) {
                    if (externalFilesDir != null) {
                        str2 = externalFilesDir.getPath() + File.separator + "temp";
                    }
                    createNewFile(str3);
                } else {
                    str2 = context.getDir("temp", 0).getPath() + File.separator + "temp";
                }
                str3 = str2;
                createNewFile(str3);
            } catch (Exception unused) {
                Log.e("ConfigFile", "create target file failed" + e);
                return false;
            }
        } catch (Exception e2) {
            Log.e("ConfigFile", "create target file or temp file failed" + e2);
            return false;
        }
        try {
            if (new File(str).exists()) {
                ThemeNativeUtils.write(str, sb.toString());
            } else if (TextUtils.isEmpty(str3) || !new File(str3).exists()) {
                Log.w("ConfigFile", "target file and temp file are not exists");
                return false;
            } else {
                ThemeNativeUtils.write(str3, sb.toString());
                ThemeNativeUtils.copy(str3, str);
                ThemeNativeUtils.remove(str3);
            }
            ThemeNativeUtils.updateFilePermissionWithThemeContext(str);
            return true;
        } catch (Exception e3) {
            Log.e("ConfigFile", "write file error" + e3);
            return false;
        }
    }

    public void setSaveViaProvider(boolean z) {
        this.mSaveViaProvider = z;
    }
}
