package com.android.settings.sound.coolsound;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import com.android.settings.R;
import com.android.settings.report.InternationalCompat;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.util.ToastUtil;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class RingtoneCardGridView extends RelativeLayout {
    private BaseAdapter mAdapter;
    private Context mContext;
    private ArrayList<RingtonePicker> mData;
    private boolean mIsThemeRingtoneAccess;
    private View.OnClickListener mListener;
    private RingtoneItem ringtoneAlarmView;
    private RingtoneItem ringtoneCallView;
    private RingtoneItemNotification ringtoneNotification;

    public RingtoneCardGridView(Context context) {
        super(context);
        this.mData = new ArrayList<>();
        this.mAdapter = null;
        this.mIsThemeRingtoneAccess = false;
        this.mListener = new View.OnClickListener() { // from class: com.android.settings.sound.coolsound.RingtoneCardGridView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent();
                int type = view instanceof RingtoneItem ? ((RingtoneItem) view).getType() : -1;
                if (view instanceof RingtoneItemNotification) {
                    type = ((RingtoneItemNotification) view).getType();
                }
                RingtonePicker picker = type != -1 ? RingtoneCardGridView.this.getPicker(type) : null;
                if (picker == null || picker.isDisable()) {
                    return;
                }
                if (RingtoneCardGridView.this.mIsThemeRingtoneAccess) {
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setData(Uri.parse("theme://zhuti.xiaomi.com/settingsringtone?type=" + picker.getResType() + "&miback=true&miref=" + RingtoneCardGridView.this.mContext.getPackageName()));
                    try {
                        RingtoneCardGridView.this.mContext.startActivity(intent);
                        return;
                    } catch (ActivityNotFoundException unused) {
                        ToastUtil.show(RingtoneCardGridView.this.mContext, R.string.thememanager_not_found, 0);
                        return;
                    }
                }
                if (picker.getResType() != 3) {
                    intent.setAction("miui.intent.action.COOL_SOUND_PHONE");
                    intent.putExtra("ringtone_type", picker.getResType());
                } else {
                    intent.setAction("miui.intent.action.ALARM_RINGTONE_PICKER");
                }
                RingtoneCardGridView.this.getContext().startActivity(intent);
                int resType = picker.getResType();
                if (resType == 0) {
                    InternationalCompat.trackReportEvent("sound_vibration_ringtone_click");
                } else if (resType == 3) {
                    InternationalCompat.trackReportEvent("sound_vibration_alarm_click");
                } else if (resType == 4) {
                    InternationalCompat.trackReportEvent("sound_vibration_calender_click");
                } else if (resType != 5) {
                } else {
                    InternationalCompat.trackReportEvent("sound_vibration_notifications_click");
                }
            }
        };
        this.mContext = context;
        init();
    }

    public RingtoneCardGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mData = new ArrayList<>();
        this.mAdapter = null;
        this.mIsThemeRingtoneAccess = false;
        this.mListener = new View.OnClickListener() { // from class: com.android.settings.sound.coolsound.RingtoneCardGridView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent();
                int type = view instanceof RingtoneItem ? ((RingtoneItem) view).getType() : -1;
                if (view instanceof RingtoneItemNotification) {
                    type = ((RingtoneItemNotification) view).getType();
                }
                RingtonePicker picker = type != -1 ? RingtoneCardGridView.this.getPicker(type) : null;
                if (picker == null || picker.isDisable()) {
                    return;
                }
                if (RingtoneCardGridView.this.mIsThemeRingtoneAccess) {
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setData(Uri.parse("theme://zhuti.xiaomi.com/settingsringtone?type=" + picker.getResType() + "&miback=true&miref=" + RingtoneCardGridView.this.mContext.getPackageName()));
                    try {
                        RingtoneCardGridView.this.mContext.startActivity(intent);
                        return;
                    } catch (ActivityNotFoundException unused) {
                        ToastUtil.show(RingtoneCardGridView.this.mContext, R.string.thememanager_not_found, 0);
                        return;
                    }
                }
                if (picker.getResType() != 3) {
                    intent.setAction("miui.intent.action.COOL_SOUND_PHONE");
                    intent.putExtra("ringtone_type", picker.getResType());
                } else {
                    intent.setAction("miui.intent.action.ALARM_RINGTONE_PICKER");
                }
                RingtoneCardGridView.this.getContext().startActivity(intent);
                int resType = picker.getResType();
                if (resType == 0) {
                    InternationalCompat.trackReportEvent("sound_vibration_ringtone_click");
                } else if (resType == 3) {
                    InternationalCompat.trackReportEvent("sound_vibration_alarm_click");
                } else if (resType == 4) {
                    InternationalCompat.trackReportEvent("sound_vibration_calender_click");
                } else if (resType != 5) {
                } else {
                    InternationalCompat.trackReportEvent("sound_vibration_notifications_click");
                }
            }
        };
        this.mContext = context;
        init();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RingtonePicker getPicker(int i) {
        Iterator<RingtonePicker> it = this.mData.iterator();
        while (it.hasNext()) {
            RingtonePicker next = it.next();
            if (next.getResType() == i) {
                return next;
            }
        }
        return null;
    }

    private void init() {
        LayoutInflater.from(this.mContext).inflate(R.layout.ringtone_settings_card_layout, (ViewGroup) this, true);
        ViewGroup.LayoutParams layoutParams = findViewById(R.id.ringtone_settings_card).getLayoutParams();
        boolean isHideRingtoneCall = SettingsFeatures.isHideRingtoneCall(this.mContext);
        if (SettingsFeatures.isSplitTabletDevice() && isHideRingtoneCall && getResources().getConfiguration().orientation == 2) {
            layoutParams.height = 208;
        } else {
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.ringtone_settings_card_height);
        }
        RingtoneItem ringtoneItem = (RingtoneItem) findViewById(R.id.ringtone_call);
        this.ringtoneCallView = ringtoneItem;
        if (isHideRingtoneCall) {
            ringtoneItem.setVisibility(8);
        }
        this.ringtoneAlarmView = (RingtoneItem) findViewById(R.id.ringtone_alarm);
        this.ringtoneNotification = (RingtoneItemNotification) findViewById(R.id.ringtone_notification);
    }

    public void setDisable(int i) {
        RingtonePicker picker = getPicker(i);
        if (picker != null) {
            picker.setDisable(true);
            setItemViewDisable(i);
        }
    }

    public void setItemViewDisable(int i) {
        RingtoneItem ringtoneItem = this.ringtoneCallView;
        if (ringtoneItem != null && ringtoneItem.getType() == i) {
            this.ringtoneCallView.setAlpha(0.3f);
            return;
        }
        RingtoneItem ringtoneItem2 = this.ringtoneAlarmView;
        if (ringtoneItem2 != null && ringtoneItem2.getType() == i) {
            this.ringtoneAlarmView.setAlpha(0.3f);
            return;
        }
        RingtoneItemNotification ringtoneItemNotification = this.ringtoneNotification;
        if (ringtoneItemNotification == null || ringtoneItemNotification.getType() != i) {
            return;
        }
        this.ringtoneNotification.setAlpha(0.3f);
    }
}
