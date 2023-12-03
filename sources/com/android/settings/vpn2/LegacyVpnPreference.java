package com.android.settings.vpn2;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import androidx.preference.Preference;
import com.android.internal.net.VpnProfile;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class LegacyVpnPreference extends ManageableRadioPreference {
    private View.OnClickListener mEditVpnListener;
    private VpnProfile mProfile;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LegacyVpnPreference(Context context) {
        super(context, null);
        setWidgetLayoutResource(R.layout.preference_widget_detail);
    }

    private void syncStartCheckAnim(CompoundButton compoundButton) {
        if (compoundButton != null) {
            Drawable buttonDrawable = compoundButton.getButtonDrawable();
            if (buttonDrawable instanceof StateListDrawable) {
                Drawable current = buttonDrawable.getCurrent();
                if (current instanceof AnimatedVectorDrawable) {
                    ((AnimatedVectorDrawable) current).stop();
                }
            }
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.Preference, java.lang.Comparable
    public int compareTo(Preference preference) {
        if (!(preference instanceof LegacyVpnPreference)) {
            if (preference instanceof AppPreference) {
                return (this.mState == 3 || ((AppPreference) preference).getState() != 3) ? -1 : 1;
            }
            return super.compareTo(preference);
        }
        LegacyVpnPreference legacyVpnPreference = (LegacyVpnPreference) preference;
        int i = legacyVpnPreference.mState - this.mState;
        if (i == 0) {
            int compareToIgnoreCase = this.mProfile.name.compareToIgnoreCase(legacyVpnPreference.mProfile.name);
            if (compareToIgnoreCase == 0) {
                VpnProfile vpnProfile = this.mProfile;
                int i2 = vpnProfile.type;
                VpnProfile vpnProfile2 = legacyVpnPreference.mProfile;
                int i3 = i2 - vpnProfile2.type;
                return i3 == 0 ? vpnProfile.key.compareTo(vpnProfile2.key) : i3;
            }
            return compareToIgnoreCase;
        }
        return i;
    }

    public VpnProfile getProfile() {
        return this.mProfile;
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        view.setTag(this);
        ImageView imageView = (ImageView) view.findViewById(R.id.detail_arrow);
        imageView.setTag(this.mProfile);
        imageView.setOnClickListener(this.mEditVpnListener);
        imageView.setContentDescription(((Object) getTitle()) + getContext().getString(R.string.accessibility_vpn_settings_info));
        syncStartCheckAnim((CompoundButton) view.findViewById(16908289));
    }

    public void setEditListener(View.OnClickListener onClickListener) {
        this.mEditVpnListener = onClickListener;
    }

    public void setProfile(VpnProfile vpnProfile) {
        VpnProfile vpnProfile2 = this.mProfile;
        String str = vpnProfile2 != null ? vpnProfile2.name : null;
        String str2 = vpnProfile != null ? vpnProfile.name : null;
        if (!TextUtils.equals(str, str2)) {
            setTitle(str2);
            notifyHierarchyChanged();
        }
        this.mProfile = vpnProfile;
    }
}
