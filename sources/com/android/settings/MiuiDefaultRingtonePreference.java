package com.android.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.ExtraRingtone;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.Toast;
import com.android.settings.report.InternationalCompat;
import com.android.settings.ringtone.MultiRingtoneSettingUtils;
import com.google.android.collect.Sets;
import java.util.ArrayList;
import java.util.Set;
import miui.app.constants.ThemeManagerConstants;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;
import miui.util.SimRingtoneUtils;

/* loaded from: classes.dex */
public class MiuiDefaultRingtonePreference extends DefaultRingtonePreference {
    private Set<Integer> mValidTypeSet;

    public MiuiDefaultRingtonePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mValidTypeSet = Sets.newArraySet(new Integer[]{1, 16, 4, 4096, 2});
    }

    public static void addMiuiNaturalSound(Context context, Intent intent) {
        ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
        for (String str : context.getResources().getStringArray(285409357)) {
            arrayList.add(new Uri.Builder().scheme(ThemeManagerConstants.COMPONENT_CODE_MASK).authority("ringtonePick").appendPath("extraRingtoneInfo").appendQueryParameter("title", ExtraRingtone.getRingtoneTitle(context, Uri.parse("file://" + str), true)).appendQueryParameter("path", str).build());
        }
        intent.putParcelableArrayListExtra("miui.intent.extra.ringtone.EXTRA_RINGTONE_URI_LIST", arrayList);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onClick() {
        int ringtoneType = getRingtoneType();
        boolean isVirtualSimEnabled = MiuiSettings.VirtualSim.isVirtualSimEnabled(getContext());
        if ((1 == ringtoneType || 8 == ringtoneType || 16 == ringtoneType) && TelephonyManager.getDefault().getPhoneCount() > 1 && SubscriptionManager.getDefault().getSubscriptionInfoCount() - (isVirtualSimEnabled ? 1 : 0) > 1) {
            getExtras().putInt("android.intent.extra.ringtone.TYPE", ringtoneType);
            setFragment(MultiRingtoneSettingUtils.getMultiRingtoneSettingFragmentName());
        } else {
            setFragment(null);
        }
        if (getFragment() == null) {
            try {
                Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                onPrepareRingtonePickerIntent(intent);
                setIntent(intent);
            } catch (ActivityNotFoundException unused) {
                Toast.makeText(getContext(), R.string.thememanager_not_found, 0).show();
            }
        }
        if (ringtoneType == 16) {
            InternationalCompat.trackReportEvent("message_click");
        }
    }

    @Override // com.android.settings.DefaultRingtonePreference, com.android.settings.RingtonePreference
    public void onPrepareRingtonePickerIntent(Intent intent) {
        super.onPrepareRingtonePickerIntent(intent);
        intent.putExtra("REQUEST_ENTRY_TYPE", getContext().getPackageName());
        int ringtoneType = getRingtoneType();
        if (4096 == ringtoneType || 8 == ringtoneType || 16 == ringtoneType) {
            intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", Settings.System.DEFAULT_NOTIFICATION_URI);
        }
        if (this.mValidTypeSet.contains(Integer.valueOf(ringtoneType))) {
            intent.putExtra("android.intent.extra.ringtone.TYPE", ringtoneType);
        }
        intent.putExtra(":miui:starting_window_label", getTitle());
        intent.setAction("miui.intent.action.RINGTONE_PICKER");
        if (2 == ringtoneType || 4096 == ringtoneType || 16 == ringtoneType) {
            addMiuiNaturalSound(getContext(), intent);
        }
    }

    @Override // com.android.settings.RingtonePreference
    public void setRingtoneType(int i) {
        if (SubscriptionManager.getDefault().getSubscriptionInfoCount() != 1 || SimRingtoneUtils.isDefaultSoundUniform(getContext(), i)) {
            super.setRingtoneType(i);
        } else {
            super.setRingtoneType(SimRingtoneUtils.getExtraRingtoneTypeBySlot(i, SubscriptionManager.getDefault().getDefaultSlotId()));
        }
    }

    @Override // androidx.preference.Preference
    public void setSummary(CharSequence charSequence) {
        if (SubscriptionManager.getDefault().getSubscriptionInfoCount() != 1 && !SimRingtoneUtils.isDefaultSoundUniform(getContext(), getRingtoneType())) {
            charSequence = "";
        }
        super.setSummary(charSequence);
    }
}
