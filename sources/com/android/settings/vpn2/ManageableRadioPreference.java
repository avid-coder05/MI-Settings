package com.android.settings.vpn2;

import android.content.Context;
import android.content.res.Resources;
import android.os.UserHandle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import com.android.settings.R;
import com.android.settings.stat.commonswitch.TalkbackSwitch;
import com.android.settingslib.Utils;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;

/* loaded from: classes2.dex */
public class ManageableRadioPreference extends RadioButtonPreference {
    public static int STATE_NONE = -1;
    boolean mIsAlwaysOn;
    boolean mIsInsecureVpn;
    int mState;
    int mUserId;

    public ManageableRadioPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsAlwaysOn = false;
        this.mIsInsecureVpn = false;
        this.mState = STATE_NONE;
        setPersistent(false);
        setOrder(0);
        setUserId(UserHandle.myUserId());
    }

    public void setAlwaysOn(boolean z) {
        if (this.mIsAlwaysOn != z) {
            this.mIsAlwaysOn = z;
            updateSummary();
        }
    }

    public void setInsecureVpn(boolean z) {
        if (this.mIsInsecureVpn != z) {
            this.mIsInsecureVpn = z;
            updateSummary();
        }
    }

    public void setState(int i) {
        if (!TalkbackSwitch.isTalkbackEnable(getContext())) {
            this.mState = i;
            updateSummary();
            notifyHierarchyChanged();
        } else if (this.mState != i) {
            this.mState = i;
            updateSummary();
            notifyHierarchyChanged();
        }
    }

    public void setUserId(int i) {
        this.mUserId = i;
    }

    protected void updateSummary() {
        Resources resources = getContext().getResources();
        String[] stringArray = resources.getStringArray(R.array.vpn_states);
        int i = this.mState;
        if (i >= stringArray.length) {
            return;
        }
        String str = i == STATE_NONE ? "" : stringArray[i];
        if (this.mIsAlwaysOn) {
            String string = resources.getString(R.string.vpn_always_on_summary_active);
            str = TextUtils.isEmpty(str) ? string : resources.getString(R.string.join_two_unrelated_items, str, string);
        }
        if (!this.mIsInsecureVpn) {
            setSummary(str);
            return;
        }
        String string2 = resources.getString(R.string.vpn_insecure_summary);
        if (!TextUtils.isEmpty(str)) {
            string2 = resources.getString(R.string.join_two_unrelated_items, str, string2);
        }
        SpannableString spannableString = new SpannableString(string2);
        spannableString.setSpan(new ForegroundColorSpan(Utils.getColorErrorDefaultColor(getContext())), 0, string2.length(), 34);
        setSummary(spannableString);
    }
}
