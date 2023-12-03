package com.miui.maml.data;

import android.text.TextUtils;
import com.miui.maml.ScreenElementRoot;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import miui.cloud.sync.MiCloudStatusInfo;

/* loaded from: classes2.dex */
public class VariableUpdaterManager {
    private WeakReference<ScreenElementRoot> mRootRef;
    private ArrayList<VariableUpdater> mUpdaters = new ArrayList<>();

    public VariableUpdaterManager(ScreenElementRoot screenElementRoot) {
        this.mRootRef = new WeakReference<>(screenElementRoot);
    }

    public void add(VariableUpdater variableUpdater) {
        this.mUpdaters.add(variableUpdater);
    }

    public void addFromTag(String str) {
        if (TextUtils.isEmpty(str) || MiCloudStatusInfo.QuotaInfo.WARN_NONE.equalsIgnoreCase(str)) {
            return;
        }
        for (String str2 : str.split(",")) {
            String trim = str2.trim();
            String str3 = null;
            int indexOf = trim.indexOf(46);
            if (indexOf != -1) {
                String substring = trim.substring(0, indexOf);
                str3 = trim.substring(indexOf + 1);
                trim = substring;
            }
            if (trim.equals("DateTime")) {
                add(new DateTimeVariableUpdater(this, str3));
            } else if (trim.equals("Battery")) {
                add(new BatteryVariableUpdater(this));
            }
        }
    }

    public void finish() {
        Iterator<VariableUpdater> it = this.mUpdaters.iterator();
        while (it.hasNext()) {
            it.next().finish();
        }
    }

    public ScreenElementRoot getRoot() {
        WeakReference<ScreenElementRoot> weakReference = this.mRootRef;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public void init() {
        Iterator<VariableUpdater> it = this.mUpdaters.iterator();
        while (it.hasNext()) {
            it.next().init();
        }
    }

    public void pause() {
        Iterator<VariableUpdater> it = this.mUpdaters.iterator();
        while (it.hasNext()) {
            it.next().pause();
        }
    }

    public void resume() {
        Iterator<VariableUpdater> it = this.mUpdaters.iterator();
        while (it.hasNext()) {
            it.next().resume();
        }
    }

    public void tick(long j) {
        Iterator<VariableUpdater> it = this.mUpdaters.iterator();
        while (it.hasNext()) {
            it.next().tick(j);
        }
    }
}
