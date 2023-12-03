package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import com.android.settingslib.search.SettingsTree;
import java.util.LinkedList;
import miui.yellowpage.YellowPageStatistic;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class MiuiMemorySettingsTree extends SettingsTree {
    private String mCategory;

    protected MiuiMemorySettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected String getCategory(boolean z) {
        return (!z || TextUtils.isEmpty(this.mCategory)) ? super.getCategory(z) : this.mCategory;
    }

    public Intent getIntent() {
        String columnValue = getColumnValue("resource");
        if (!"cleaner".equals(columnValue)) {
            return ("sd_eject".equals(columnValue) || "usb_eject".equals(columnValue) || "sd_format".equals(columnValue) || "usb_format".equals(columnValue)) ? new Intent("com.miui.securitycenter.action.STORAGE_MANAGE") : super.getIntent();
        }
        Intent intent = new Intent("com.miui.cleanmaster.InstallAndLunchCleanMaster");
        intent.putExtra("cleanMasterAction", "miui.intent.action.GARBAGE_CLEANUP");
        return intent;
    }

    public LinkedList<SettingsTree> getSons() {
        if ("storage_settings".equals(getColumnValue("resource"))) {
            LinkedList sons = super.getSons();
            if (sons != null) {
                for (int size = sons.size() - 1; size >= 0; size--) {
                    SettingsTree settingsTree = (SettingsTree) sons.get(size);
                    if (Boolean.parseBoolean(settingsTree.getColumnValue("temporary"))) {
                        settingsTree.removeSelf();
                    }
                }
            }
            StorageManager storageManager = (StorageManager) ((SettingsTree) this).mContext.getSystemService("storage");
            for (VolumeInfo volumeInfo : storageManager.getVolumes()) {
                if (volumeInfo.getType() == 0) {
                    DiskInfo findDiskById = storageManager.findDiskById(volumeInfo.getDiskId());
                    JSONObject jSONObject = new JSONObject();
                    try {
                        jSONObject.put("temporary", true);
                        jSONObject.put("resource", findDiskById.isSd() ? "sd_eject" : "usb_eject");
                        MiuiMemorySettingsTree miuiMemorySettingsTree = (MiuiMemorySettingsTree) SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this);
                        miuiMemorySettingsTree.mCategory = findDiskById.getDescription();
                        addSon(miuiMemorySettingsTree);
                        jSONObject.put("resource", findDiskById.isSd() ? "sd_format" : "usb_format");
                        MiuiMemorySettingsTree miuiMemorySettingsTree2 = (MiuiMemorySettingsTree) SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this);
                        miuiMemorySettingsTree2.mCategory = findDiskById.getDescription();
                        addSon(miuiMemorySettingsTree2);
                    } catch (JSONException unused) {
                    }
                }
            }
        }
        return super.getSons();
    }

    public boolean initialize() {
        if ("storage_settings".equals(getColumnValue("resource"))) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("resource", "cleaner");
                jSONObject.put(YellowPageStatistic.Display.CATEGORY, "cleaner");
                addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
            } catch (JSONException unused) {
            }
        }
        return super.initialize();
    }
}
