package com.android.settings.sound.coolsound;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.utils.SettingsFeatures;

/* loaded from: classes2.dex */
public class RingtoneItem extends LinearLayout {
    public ImageView imageView;
    public TextView summary;
    public TextView title;
    private int type;

    public RingtoneItem(Context context) {
        super(context);
        init(context);
    }

    public RingtoneItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private int getResourceId() {
        int i = R.layout.ringtone_settings_item;
        return (SettingsFeatures.isSplitTabletDevice() && SettingsFeatures.isHideRingtoneCall(((LinearLayout) this).mContext)) ? R.layout.ringtone_settings_item_tablet_no_call : i;
    }

    private void init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(getResourceId(), (ViewGroup) this, true);
        this.imageView = (ImageView) inflate.findViewById(R.id.ringtone_icon);
        this.title = (TextView) inflate.findViewById(R.id.ringtone_title);
        this.summary = (TextView) inflate.findViewById(R.id.ringtone_value);
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }
}
