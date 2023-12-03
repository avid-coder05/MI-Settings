package com.android.settings.ringtone;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.ExtraRingtoneManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;

/* loaded from: classes2.dex */
public class MultiSimRingtonePreference extends ValuePreference {
    private final int MSG_UPDATE_TITLE;
    private final int MSG_UPDATE_VALUE;
    private int mExtraRingtoneType;
    private Handler mHandler;
    private Runnable mUpdateUIRunnable;

    public MultiSimRingtonePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.MSG_UPDATE_TITLE = 0;
        this.MSG_UPDATE_VALUE = 1;
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.ringtone.MultiSimRingtonePreference.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 0) {
                    Pair pair = (Pair) message.obj;
                    MultiSimRingtonePreference.this.setTitle((CharSequence) pair.first);
                    MultiSimRingtonePreference.this.setSummary((CharSequence) pair.second);
                } else if (i == 1) {
                    MultiSimRingtonePreference.this.setValue((String) message.obj);
                }
            }
        };
        this.mUpdateUIRunnable = new Runnable() { // from class: com.android.settings.ringtone.MultiSimRingtonePreference.2
            @Override // java.lang.Runnable
            public void run() {
                MultiSimRingtonePreference.this.mHandler.sendMessage(MultiSimRingtonePreference.this.mHandler.obtainMessage(0, MultiSimRingtonePreference.this.getRingtoneTitle()));
                MultiSimRingtonePreference.this.mHandler.sendMessage(MultiSimRingtonePreference.this.mHandler.obtainMessage(1, MultiSimRingtonePreference.this.getRingtoneValue()));
            }
        };
        this.mExtraRingtoneType = 0;
        setShowRightArrow(true);
    }

    private int getDeviceSlotID() {
        if (isSlot1Position()) {
            return 0;
        }
        if (isSlot2Position()) {
            return 1;
        }
        return SubscriptionManager.INVALID_SLOT_ID;
    }

    private Drawable getRingtoneIcon() {
        if (isSlot1Position()) {
            return getContext().getResources().getDrawable(R.drawable.sim_slot_1_icon);
        }
        if (isSlot2Position()) {
            return getContext().getResources().getDrawable(R.drawable.sim_slot_2_icon);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Pair<String, String> getRingtoneTitle() {
        String str;
        String string;
        int i = this.mExtraRingtoneType;
        String str2 = null;
        if (i == 1) {
            string = getContext().getString(R.string.ringtone_title);
        } else if (i == 8) {
            string = getContext().getString(R.string.sms_delivered_sound_title);
        } else if (i != 16) {
            SubscriptionInfo subscriptionInfoForSlot = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(getDeviceSlotID());
            if (subscriptionInfoForSlot != null) {
                str2 = subscriptionInfoForSlot.getDisplayName().toString();
                str = subscriptionInfoForSlot.getDisplayNumber();
            } else {
                str = null;
            }
            return new Pair<>(str2, str);
        } else {
            string = getContext().getString(R.string.sms_received_sound_title);
        }
        str2 = string;
        str = null;
        return new Pair<>(str2, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getRingtoneValue() {
        return ExtraRingtoneManager.getDefaultSoundName(getContext(), this.mExtraRingtoneType);
    }

    private boolean isSlot1Position() {
        int i = this.mExtraRingtoneType;
        return i == 64 || i == 1024 || i == 256;
    }

    private boolean isSlot2Position() {
        int i = this.mExtraRingtoneType;
        return i == 128 || i == 2048 || i == 512;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0030, code lost:
    
        if (r1 != 2048) goto L23;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.content.Intent getRingtonePickerIntent() {
        /*
            r7 = this;
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "miui.intent.action.RINGTONE_PICKER"
            r0.<init>(r1)
            int r1 = r7.mExtraRingtoneType
            r2 = 0
            java.lang.String r3 = "android.intent.extra.ringtone.SHOW_DEFAULT"
            java.lang.String r4 = "android.intent.extra.ringtone.TYPE"
            r5 = 1
            if (r1 == r5) goto L48
            r6 = 8
            if (r1 == r6) goto L33
            r6 = 16
            if (r1 == r6) goto L33
            r6 = 64
            if (r1 == r6) goto L48
            r6 = 128(0x80, float:1.8E-43)
            if (r1 == r6) goto L48
            r6 = 256(0x100, float:3.59E-43)
            if (r1 == r6) goto L33
            r6 = 512(0x200, float:7.17E-43)
            if (r1 == r6) goto L33
            r6 = 1024(0x400, float:1.435E-42)
            if (r1 == r6) goto L33
            r6 = 2048(0x800, float:2.87E-42)
            if (r1 == r6) goto L33
            goto L4e
        L33:
            r0.putExtra(r4, r1)
            r0.putExtra(r3, r2)
            android.content.Context r1 = r7.getContext()
            com.android.settings.MiuiDefaultRingtonePreference.addMiuiNaturalSound(r1, r0)
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
            java.lang.String r2 = "android.intent.extra.ringtone.DEFAULT_URI"
            r0.putExtra(r2, r1)
            goto L4e
        L48:
            r0.putExtra(r4, r1)
            r0.putExtra(r3, r2)
        L4e:
            android.content.Context r1 = r7.getContext()
            int r7 = r7.mExtraRingtoneType
            android.net.Uri r7 = android.media.ExtraRingtoneManager.getDefaultSoundSettingUri(r1, r7)
            java.lang.String r1 = "android.intent.extra.ringtone.EXISTING_URI"
            r0.putExtra(r1, r7)
            java.lang.String r7 = "android.intent.extra.ringtone.SHOW_SILENT"
            r0.putExtra(r7, r5)
            java.lang.String r7 = "com.android.thememanager"
            java.lang.String r1 = "com.android.thememanager.activity.ThemeTabActivity"
            r0.setClassName(r7, r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.ringtone.MultiSimRingtonePreference.getRingtonePickerIntent():android.content.Intent");
    }

    @Override // com.android.settingslib.miuisettings.preference.ValuePreference, miuix.preference.TextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(R.id.value_right);
        textView.setSingleLine(true);
        textView.setGravity(3);
    }

    public void updateUI(int i) {
        if (this.mExtraRingtoneType != i) {
            this.mExtraRingtoneType = i;
            setIcon(getRingtoneIcon());
        }
        new Thread(this.mUpdateUIRunnable).start();
    }
}
