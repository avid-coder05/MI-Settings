package com.android.settings.ai;

/* loaded from: classes.dex */
public class AiSettingsItem {
    public int mIndex;
    public String name;
    public boolean selected;
    public int type;
    public int voiceAssistantMode;

    public AiSettingsItem() {
        this(false, -1, -1);
    }

    public AiSettingsItem(int i, int i2) {
        this(false, i, i2);
    }

    public AiSettingsItem(boolean z, int i, int i2) {
        this.voiceAssistantMode = 1;
        this.selected = z;
        this.type = i;
        this.mIndex = i2;
        this.name = "";
    }

    public boolean equals(Object obj) {
        return (obj instanceof AiSettingsItem) && this.type == ((AiSettingsItem) obj).type;
    }

    public String toString() {
        return "AiSettingsItem{,ai_settings_item_name index = '" + this.mIndex + "',ai_settings_item_type = '" + this.type + "'}";
    }
}
