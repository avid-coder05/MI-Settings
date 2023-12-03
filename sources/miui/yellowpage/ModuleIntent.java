package miui.yellowpage;

import android.content.Intent;
import java.io.Serializable;
import java.util.ArrayList;

/* loaded from: classes4.dex */
public class ModuleIntent implements Serializable {
    private static final long serialVersionUID = 757699801853589L;
    private int mHotId;
    private int mHotShowCount;
    private Intent mIntent;
    private int mModuleId;
    private boolean mSubItemsFlag;
    private ArrayList<ModuleIntent> mSubModuleIntent;
    private String mTitle;

    public ModuleIntent(String str, Intent intent, int i) {
        this.mTitle = str;
        this.mIntent = intent;
        this.mModuleId = i;
    }

    public ModuleIntent(String str, Intent intent, int i, boolean z) {
        this.mTitle = str;
        this.mIntent = intent;
        this.mModuleId = i;
        this.mSubItemsFlag = z;
    }

    public ModuleIntent(String str, Intent intent, int i, boolean z, int i2, int i3) {
        this(str, intent, i, z);
        this.mHotId = i2;
        this.mHotShowCount = i3;
    }

    public int getHotId() {
        return this.mHotId;
    }

    public int getHotShowCount() {
        return this.mHotShowCount;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public int getModuleId() {
        return this.mModuleId;
    }

    public boolean getSubItemsFlag() {
        return this.mSubItemsFlag;
    }

    public ArrayList<ModuleIntent> getSubModuleIntent() {
        return this.mSubModuleIntent;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setSubModuleIntent(ArrayList<ModuleIntent> arrayList) {
        this.mSubModuleIntent = arrayList;
    }
}
