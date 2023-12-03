package com.android.settings;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;
import com.android.settingslib.util.ToastUtil;
import miui.telephony.MiuiHeDuoHaoUtil;

/* loaded from: classes.dex */
public class MiuiApnPreference extends RadioButtonPreference implements View.OnClickListener {
    private boolean mApnReadOnly;
    private String mApnType;
    private int mEdited;
    private int mSubId;

    public MiuiApnPreference(Context context) {
        this(context, null);
    }

    public MiuiApnPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mApnReadOnly = false;
        this.mSubId = -1;
        setWidgetLayoutResource(R.layout.preference_widget_detail);
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.detail_arrow);
        imageView.setOnClickListener(this);
        imageView.setContentDescription(((Object) getTitle()) + getContext().getResources().getString(R.string.accessibility_more_settings));
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Log.d("MiuiApnPreference", "jp_sb mApnType = " + this.mApnType + ",mEdited=" + this.mEdited);
        if ("jp_sb".equals(SystemProperties.get("ro.miui.customized.region")) && !"ims".equals(this.mApnType) && this.mEdited == 0) {
            ToastUtil.show(getContext(), R.string.softbank_apn_lock_toast, 0);
            return;
        }
        String simOperator = TelephonyManager.getDefault().getSimOperator(this.mSubId);
        if ("jp_kd".equals(SystemProperties.get("ro.miui.customized.region")) && this.mEdited == 0 && simOperator != null && "44051".equals(simOperator)) {
            ToastUtil.show(getContext(), R.string.softbank_apn_lock_toast, 0);
            return;
        }
        Intent intent = new Intent("android.intent.action.EDIT", ContentUris.withAppendedId(Telephony.Carriers.CONTENT_URI, Integer.parseInt(getKey())));
        intent.putExtra("DISABLE_EDITOR", this.mApnReadOnly);
        intent.putExtra(MiuiHeDuoHaoUtil.SUB_ID, this.mSubId);
        getContext().startActivity(intent);
    }

    public void setApnReadOnly(boolean z) {
        this.mApnReadOnly = z;
    }

    public void setApnType(String str) {
        this.mApnType = str;
    }

    public void setEdited(int i) {
        this.mEdited = i;
    }

    public void setSubId(int i) {
        this.mSubId = i;
    }
}
