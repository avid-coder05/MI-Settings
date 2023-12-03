package com.android.settings.sound;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.ExtraRingtoneManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.report.InternationalCompat;
import com.android.settings.sound.coolsound.CoolSoundUtils;
import com.android.settings.sound.coolsound.RingtoneItem;
import com.android.settings.sound.coolsound.RingtoneItemNotification;
import com.android.settings.sound.coolsound.RingtonePicker;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.util.ToastUtil;
import java.util.ArrayList;
import java.util.Iterator;
import miui.os.Build;
import miui.telephony.SubscriptionManager;
import miui.util.SimRingtoneUtils;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;

/* loaded from: classes2.dex */
public class RingtoneCardPreference extends Preference {
    private RingtonePicker alarmsPicker;
    private boolean isHideRingtoneCall;
    private Context mContext;
    private ArrayList<RingtonePicker> mData;
    private boolean mIsThemeRingtoneAccess;
    private final View.OnClickListener mListener;
    private RingtonePicker notificationPicker;
    private RingtoneItem ringtoneAlarmView;
    private RingtoneItem ringtoneCallView;
    private RingtoneItemNotification ringtoneNotification;
    private RingtonePicker telephonePicker;

    public RingtoneCardPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mData = new ArrayList<>();
        this.mIsThemeRingtoneAccess = false;
        this.mListener = new View.OnClickListener() { // from class: com.android.settings.sound.RingtoneCardPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent();
                int type = view instanceof RingtoneItem ? ((RingtoneItem) view).getType() : -1;
                if (view instanceof RingtoneItemNotification) {
                    type = ((RingtoneItemNotification) view).getType();
                }
                RingtonePicker picker = type != -1 ? RingtoneCardPreference.this.getPicker(type) : null;
                if (picker == null || picker.isDisable()) {
                    return;
                }
                if (RingtoneCardPreference.this.mIsThemeRingtoneAccess) {
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setData(Uri.parse("theme://zhuti.xiaomi.com/settingsringtone?type=" + picker.getResType() + "&miback=true&miref=" + RingtoneCardPreference.this.mContext.getPackageName()));
                    try {
                        RingtoneCardPreference.this.mContext.startActivity(intent);
                        return;
                    } catch (ActivityNotFoundException unused) {
                        ToastUtil.show(RingtoneCardPreference.this.mContext, R.string.thememanager_not_found, 0);
                        return;
                    }
                }
                if (picker.getResType() != 3) {
                    intent.setAction("miui.intent.action.COOL_SOUND_PHONE");
                    intent.putExtra("ringtone_type", picker.getResType());
                } else {
                    intent.setAction("miui.intent.action.ALARM_RINGTONE_PICKER");
                }
                RingtoneCardPreference.this.getContext().startActivity(intent);
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
        setLayoutResource(R.layout.ringtone_settings_card_layout);
        setSelectable(false);
        this.mContext = context;
        this.isHideRingtoneCall = SettingsFeatures.isHideRingtoneCall(context);
        init();
    }

    private boolean checkGlobalRingtoneAccess() {
        if (Build.IS_GLOBAL_BUILD) {
            try {
                Bundle call = this.mContext.getContentResolver().call(Uri.parse("content://com.android.thememanager.theme_provider"), "getRingtoneService", (String) null, (Bundle) null);
                if (call != null) {
                    return call.getBoolean("theme_ringtone_access");
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
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
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.sound.RingtoneCardPreference$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RingtoneCardPreference.this.lambda$init$0();
            }
        });
        String string = this.mContext.getString(R.string.ringtone_title);
        String string2 = this.mContext.getString(R.string.alarm_sound_title);
        String string3 = this.mContext.getString(R.string.notification_remind);
        this.telephonePicker = new RingtonePicker(0, R.drawable.ic_telephone_ring, string);
        this.alarmsPicker = new RingtonePicker(3, R.drawable.ic_alarms_ring, string2);
        this.notificationPicker = new RingtonePicker(5, R.drawable.ic_ringtone_notification, string3);
        this.mData.add(this.telephonePicker);
        if (CoolSoundUtils.isSupportCoolAlarm(this.mContext)) {
            this.mData.add(this.alarmsPicker);
        }
        this.mData.add(this.notificationPicker);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0() {
        this.mIsThemeRingtoneAccess = checkGlobalRingtoneAccess();
    }

    private void useFolme(View view) {
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.useAt(view).touch().handleTouchOf(view, new AnimConfig[0]);
        }
    }

    public Uri getUri(int i) {
        if (i == 0 && SubscriptionManager.getDefault().getSubscriptionInfoCount() > 1) {
            i = 1;
        }
        if (i == 3) {
            return RingtoneManager.getActualDefaultRingtoneUri(this.mContext, 4);
        }
        int transferToRingtoneType = CoolSoundUtils.transferToRingtoneType(i);
        if (SubscriptionManager.getDefault().getSubscriptionInfoCount() == 1 && !SimRingtoneUtils.isDefaultSoundUniform(getContext(), transferToRingtoneType)) {
            transferToRingtoneType = SimRingtoneUtils.getExtraRingtoneTypeBySlot(transferToRingtoneType, SubscriptionManager.getDefault().getDefaultSlotId());
        }
        return ExtraRingtoneManager.getDefaultSoundSettingUri(this.mContext, transferToRingtoneType);
    }

    public boolean isViewDisable(int i) {
        RingtonePicker picker = getPicker(i);
        if (picker != null) {
            return picker.isDisable();
        }
        return false;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        View view = preferenceViewHolder.itemView;
        view.setEnabled(false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (SettingsFeatures.isSplitTabletDevice() && this.isHideRingtoneCall && this.mContext.getResources().getConfiguration().orientation == 2) {
            layoutParams.height = 208;
        } else {
            layoutParams.height = this.mContext.getResources().getDimensionPixelSize(R.dimen.ringtone_settings_card_height);
        }
        this.ringtoneCallView = (RingtoneItem) view.findViewById(R.id.ringtone_call);
        if (SettingsFeatures.isSplitTabletDevice() && this.isHideRingtoneCall) {
            this.ringtoneCallView.setVisibility(8);
        }
        this.ringtoneAlarmView = (RingtoneItem) view.findViewById(R.id.ringtone_alarm);
        this.ringtoneNotification = (RingtoneItemNotification) view.findViewById(R.id.ringtone_notification);
        RingtoneItem ringtoneItem = this.ringtoneCallView;
        if (ringtoneItem != null) {
            ringtoneItem.setOnClickListener(this.mListener);
            this.ringtoneCallView.setType(0);
            useFolme(this.ringtoneCallView);
            if (this.telephonePicker != null) {
                r5.width -= 10;
                r5.height -= 10;
                this.ringtoneCallView.imageView.setLayoutParams((LinearLayout.LayoutParams) this.ringtoneCallView.imageView.getLayoutParams());
                this.ringtoneCallView.imageView.setBackgroundResource(this.telephonePicker.getDrawableId());
                this.ringtoneCallView.title.setText(this.telephonePicker.getRingtoneTitle());
                this.ringtoneCallView.summary.setText(this.telephonePicker.getRingtoneValue());
            }
        }
        RingtoneItem ringtoneItem2 = this.ringtoneAlarmView;
        if (ringtoneItem2 != null) {
            ringtoneItem2.setOnClickListener(this.mListener);
            this.ringtoneAlarmView.setType(3);
            useFolme(this.ringtoneAlarmView);
            RingtonePicker ringtonePicker = this.alarmsPicker;
            if (ringtonePicker != null) {
                this.ringtoneAlarmView.imageView.setBackgroundResource(ringtonePicker.getDrawableId());
                this.ringtoneAlarmView.title.setText(this.alarmsPicker.getRingtoneTitle());
                this.ringtoneAlarmView.summary.setText(this.alarmsPicker.getRingtoneValue());
            }
        }
        RingtoneItemNotification ringtoneItemNotification = this.ringtoneNotification;
        if (ringtoneItemNotification != null) {
            ringtoneItemNotification.setOnClickListener(this.mListener);
            this.ringtoneNotification.setType(5);
            useFolme(this.ringtoneNotification);
            RingtonePicker ringtonePicker2 = this.notificationPicker;
            if (ringtonePicker2 != null) {
                this.ringtoneNotification.imageView.setBackgroundResource(ringtonePicker2.getDrawableId());
                this.ringtoneNotification.title.setText(this.notificationPicker.getRingtoneTitle());
                this.ringtoneNotification.summary.setText(this.notificationPicker.getRingtoneValue());
            }
        }
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

    public void setValue(int i, CharSequence charSequence) {
        RingtonePicker picker = getPicker(i);
        if (picker != null) {
            picker.setRingtoneValue(charSequence.toString());
            updateItemView(i, charSequence);
        }
    }

    public void updateItemView(int i, CharSequence charSequence) {
        RingtoneItem ringtoneItem = this.ringtoneCallView;
        if (ringtoneItem != null && ringtoneItem.getType() == i) {
            this.ringtoneCallView.summary.setText(charSequence);
            return;
        }
        RingtoneItem ringtoneItem2 = this.ringtoneAlarmView;
        if (ringtoneItem2 != null && ringtoneItem2.getType() == i) {
            this.ringtoneAlarmView.summary.setText(charSequence);
            return;
        }
        RingtoneItemNotification ringtoneItemNotification = this.ringtoneNotification;
        if (ringtoneItemNotification == null || ringtoneItemNotification.getType() != i) {
            return;
        }
        this.ringtoneNotification.summary.setText(charSequence);
    }
}
